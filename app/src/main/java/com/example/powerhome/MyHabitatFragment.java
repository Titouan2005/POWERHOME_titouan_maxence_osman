package com.example.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

/**
 * MyHabitatFragment — Vue "Mon habitat" du résident connecté.
 *
 * Affiche :
 *   - Nom, étage, surface de l'habitat
 *   - Liste des équipements avec leur consommation
 *   - Puissance totale consommée
 *   - Solde éco-coins du résident
 *
 * Données issues de GET /api/my_habitat.php (authentifié par token).
 */
public class MyHabitatFragment extends Fragment {

    private TextView    tvHabitatName, tvFloor, tvArea, tvTotalWattage, tvEcoCoins;
    private LinearLayout  appliancesContainer;
    private ProgressBar  progressBar;
    private View         cardHabitat, tvNoHabitat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_habitat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHabitatName      = view.findViewById(R.id.tv_habitat_name);
        tvFloor            = view.findViewById(R.id.tv_floor);
        tvArea             = view.findViewById(R.id.tv_area);
        tvTotalWattage     = view.findViewById(R.id.tv_total_wattage);
        tvEcoCoins         = view.findViewById(R.id.tv_eco_coins);
        appliancesContainer = view.findViewById(R.id.appliances_container);
        progressBar        = view.findViewById(R.id.progress_bar);
        cardHabitat        = view.findViewById(R.id.card_habitat);
        tvNoHabitat        = view.findViewById(R.id.tv_no_habitat);

        // Bouton "Modifier mon profil"
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class)));

        loadMyHabitat();
    }

    /** Recharger quand on revient sur ce fragment (après EditProfile par exemple). */
    @Override
    public void onResume() {
        super.onResume();
        // Mettre à jour le nom affiché si le profil a été modifié
        SessionManager session = new SessionManager(requireContext());
        TextView tvWelcome = requireView().findViewById(R.id.tv_welcome);
        if (tvWelcome != null) {
            tvWelcome.setText("Bonjour, " + session.getFirstname() + " !");
        }
    }

    /**
     * Appelle GET /api/my_habitat.php pour récupérer l'habitat du résident.
     */
    private void loadMyHabitat() {
        progressBar.setVisibility(View.VISIBLE);
        cardHabitat.setVisibility(View.GONE);
        tvNoHabitat.setVisibility(View.GONE);

        SessionManager session = new SessionManager(requireContext());

        Ion.with(this)
                .load("GET", Constants.URL_MY_HABITAT)
                .addHeader("Authorization", "Bearer " + session.getToken())
                .asJsonObject()
                .setCallback((e, result) -> {
                    progressBar.setVisibility(View.GONE);

                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur réseau.", Toast.LENGTH_SHORT).show();
                        showFallbackData(session);
                        return;
                    }

                    boolean success = result.get("success").getAsBoolean();
                    if (!success) {
                        Toast.makeText(getContext(),
                                result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // data peut être null si pas d'habitat assigné
                    if (result.get("data").isJsonNull()) {
                        tvNoHabitat.setVisibility(View.VISIBLE);
                        return;
                    }

                    displayHabitat(result.getAsJsonObject("data"), session.getEcoCoins());
                });
    }

    /** Affiche les données de l'habitat dans les vues. */
    private void displayHabitat(JsonObject data, int ecoCoins) {
        cardHabitat.setVisibility(View.VISIBLE);

        tvHabitatName.setText(data.get("name").getAsString());
        tvFloor.setText("Étage " + data.get("floor").getAsInt());
        tvArea.setText(data.get("area").getAsDouble() + " m²");
        tvTotalWattage.setText(data.get("total_wattage").getAsInt() + " W");

        // Les éco-coins viennent soit du JSON, soit de la session locale
        int coins = data.has("eco_coins") ? data.get("eco_coins").getAsInt() : ecoCoins;
        tvEcoCoins.setText(coins + " éco-coins 🌿");

        // Remplir dynamiquement la liste des équipements
        appliancesContainer.removeAllViews();
        JsonArray appliances = data.getAsJsonArray("appliances");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (JsonElement ae : appliances) {
            JsonObject a = ae.getAsJsonObject();
            View item = inflater.inflate(R.layout.item_appliance, appliancesContainer, false);

            ((TextView) item.findViewById(R.id.tv_appliance_name))
                    .setText(a.get("name").getAsString());
            ((TextView) item.findViewById(R.id.tv_appliance_wattage))
                    .setText(a.get("wattage").getAsInt() + " W");

            appliancesContainer.addView(item);
        }
    }

    /** Données de démo si le réseau est indisponible. */
    private void showFallbackData(SessionManager session) {
        cardHabitat.setVisibility(View.VISIBLE);
        tvHabitatName.setText("Appartement A101 (démo)");
        tvFloor.setText("Étage 1");
        tvArea.setText("45.5 m²");
        tvTotalWattage.setText("4700 W");
        tvEcoCoins.setText(session.getEcoCoins() + " éco-coins 🌿");
    }
}
