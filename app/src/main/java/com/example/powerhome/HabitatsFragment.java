package com.example.powerhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * HabitatsFragment — Liste de tous les habitats de la résidence.
 *
 * Groupe 2 de l'énoncé : appel d'un service Web, manipulation JSON,
 * mise à jour des données relatives au bâtiment.
 *
 * Ce fragment récupère la liste via GET /api/habitats.php (avec token)
 * et l'affiche dans une ListView avec un HabitatAdapter.
 * Un clic sur un item ouvre une boîte de dialogue de détail.
 */
public class HabitatsFragment extends Fragment {

    private ListView     listView;
    private ProgressBar  progressBar;
    private TextView     tvEmpty;
    private HabitatAdapter adapter;
    private final List<Habitat> habitats = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habitats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView    = view.findViewById(R.id.lv_habitats);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty     = view.findViewById(R.id.tv_empty);

        // Initialiser l'adapter avec la liste vide
        adapter = new HabitatAdapter(requireContext(), habitats);
        listView.setAdapter(adapter);

        // Charger les données depuis l'API
        loadHabitats();
    }

    /**
     * Appelle GET /api/habitats.php pour récupérer la liste des habitats.
     * Le token est envoyé dans l'en-tête Authorization.
     */
    private void loadHabitats() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        SessionManager session = new SessionManager(requireContext());
        String token = session.getToken();

        // Si pas de token, afficher les données de démo directement
        if (token == null) {
            progressBar.setVisibility(View.GONE);
            showFallbackData();
            return;
        }

        Ion.with(this)
                .load("GET", Constants.URL_HABITATS)
                .addHeader("Authorization", "Bearer " + token)
                .asJsonObject()
                .setCallback((e, result) -> {
                    progressBar.setVisibility(View.GONE);

                    if (e != null || result == null) {
                        showFallbackData();
                        return;
                    }

                    boolean success = result.get("success").getAsBoolean();
                    if (!success) {
                        Toast.makeText(getContext(),
                                result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        showFallbackData();
                        return;
                    }

                    parseHabitats(result.getAsJsonArray("data"));
                });
    }

    /**
     * Convertit le tableau JSON en liste d'objets Habitat.
     *
     * Chaque objet JSON a la forme :
     * { "id":1, "name":"...", "floor":1, "area":45.5,
     *   "total_wattage":4700, "appliances":[...] }
     */
    private void parseHabitats(JsonArray jsonArray) {
        habitats.clear();

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            // Analyser les équipements imbriqués
            List<Appliance> appliances = new ArrayList<>();
            JsonArray appliancesJson = obj.getAsJsonArray("appliances");
            for (JsonElement ae : appliancesJson) {
                JsonObject a = ae.getAsJsonObject();
                Appliance appliance = new Appliance(
                        a.get("id").getAsInt(),
                        a.get("name").getAsString(),
                        a.get("wattage").getAsInt(),
                        a.get("icon_name").getAsString()
                );
                // Résoudre l'icône localement depuis le nom de la ressource
                appliance.setIconResId(resolveIconResId(a.get("icon_name").getAsString()));
                appliances.add(appliance);
            }

            Habitat habitat = new Habitat(
                    obj.get("id").getAsInt(),
                    obj.get("name").getAsString(),
                    obj.get("floor").getAsInt(),
                    obj.get("area").getAsDouble(),
                    appliances,
                    obj.get("total_wattage").getAsInt()
            );
            habitats.add(habitat);
        }

        adapter.notifyDataSetChanged();

        tvEmpty.setVisibility(habitats.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Résout l'ID de ressource Android à partir du nom de l'icône.
     * Si le nom n'est pas trouvé, retourne une icône par défaut.
     */
    private int resolveIconResId(String iconName) {
        int resId = getResources().getIdentifier(
                iconName, "drawable", requireContext().getPackageName());
        return resId != 0 ? resId : android.R.drawable.ic_menu_manage;
    }

    /**
     * Données de secours (fallback) quand le réseau est indisponible.
     * Permet de tester l'interface sans serveur.
     */
    private void showFallbackData() {
        habitats.clear();
        Appliance lm = new Appliance("Lave-linge",  R.drawable.ic_laundry, 2500);
        Appliance fr = new Appliance("Réfrigérateur", R.drawable.ic_fridge, 300);
        Appliance ro = new Appliance("Robinet élec", R.drawable.ic_tap,    100);
        Appliance fe = new Appliance("Fer à repasser", R.drawable.ic_iron, 1800);

        habitats.add(new Habitat("Appartement A101", 1, 45.5, List.of(lm, fr, ro, fe)));
        habitats.add(new Habitat("Appartement A102", 1, 32.0, List.of(lm)));
        habitats.add(new Habitat("Appartement B201", 2, 50.0, List.of(fe, ro)));
        habitats.add(new Habitat("Appartement B301", 3, 65.0, List.of(lm, fe, ro)));
        habitats.add(new Habitat("Appartement C101", 0, 28.5, List.of(ro)));

        adapter.notifyDataSetChanged();
    }
}
