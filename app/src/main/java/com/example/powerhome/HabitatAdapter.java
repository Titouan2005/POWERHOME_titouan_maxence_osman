package com.example.powerhome;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * HabitatAdapter — Adapter personnalisé pour la ListView des habitats.
 *
 * Utilise le pattern ViewHolder pour optimiser le recyclage des vues
 * (évite les appels à findViewById() à chaque défilement).
 *
 * Un clic sur un item ouvre une boîte de dialogue de détail listant
 * chaque équipement avec son icône, son nom et sa consommation en watts.
 */
public class HabitatAdapter extends ArrayAdapter<Habitat> {

    private final LayoutInflater inflater;

    public HabitatAdapter(@NonNull Context context, @NonNull List<Habitat> data) {
        super(context, 0, data);
        inflater = LayoutInflater.from(context);
    }

    /**
     * ViewHolder : stocke les références aux vues d'un item pour éviter
     * de les rechercher à chaque appel à getView().
     */
    private static class ViewHolder {
        TextView    tvName;
        TextView    tvEquip;
        TextView    tvFloor;
        TextView    tvWattage;
        LinearLayout iconsContainer;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        // Réutiliser ou créer la vue
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_habitat, parent, false);
            holder = new ViewHolder();
            holder.tvName        = convertView.findViewById(R.id.tvName);
            holder.tvEquip       = convertView.findViewById(R.id.tvEquip);
            holder.tvFloor       = convertView.findViewById(R.id.tvFloorNumber);
            holder.tvWattage     = convertView.findViewById(R.id.tvWattage);
            holder.iconsContainer = convertView.findViewById(R.id.iconsContainer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Habitat habitat = getItem(position);
        if (habitat == null) return convertView;

        // Remplir les données
        holder.tvName.setText(habitat.getName());
        holder.tvFloor.setText(String.valueOf(habitat.getFloor()));

        int count = habitat.getEquipmentCount();
        holder.tvEquip.setText(count + " équipement" + (count > 1 ? "s" : ""));

        // Consommation totale avec couleur selon le niveau
        int watts = habitat.getTotalWattage();
        holder.tvWattage.setText(watts + " W");
        holder.tvWattage.setTextColor(getWattageColor(watts));

        // Icônes des équipements
        holder.iconsContainer.removeAllViews();
        for (Appliance app : habitat.getAppliances()) {
            if (app.getIconResId() == 0) continue;
            ImageView iv = new ImageView(getContext());
            int size = dp(20);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMarginEnd(dp(8));
            iv.setLayoutParams(lp);
            iv.setImageResource(app.getIconResId());
            iv.setAlpha(0.6f);
            holder.iconsContainer.addView(iv);
        }

        // Ouvrir le dialogue de détail au clic
        View finalConvertView = convertView;
        finalConvertView.setOnClickListener(v -> showDetailDialog(habitat));

        return convertView;
    }

    /**
     * Retourne une couleur selon la puissance totale de l'habitat :
     * vert (<= 1000 W), orange (<= 3000 W), rouge (> 3000 W).
     */
    private int getWattageColor(int watts) {
        if (watts <= 1000) return Color.parseColor("#2E7D32"); // vert
        if (watts <= 3000) return Color.parseColor("#E65100"); // orange
        return Color.parseColor("#B71C1C");                    // rouge
    }

    /**
     * Affiche la boîte de dialogue de détail d'un habitat.
     * Liste chaque équipement avec icône, nom et puissance.
     */
    @SuppressLint("SetTextI18n")
    private void showDetailDialog(Habitat habitat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_resident_detail, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Remplir les informations générales
        ((TextView) dialogView.findViewById(R.id.dialogTvName)).setText(habitat.getName());
        ((TextView) dialogView.findViewById(R.id.dialogTvFloor)).setText("Étage : " + habitat.getFloor());
        ((TextView) dialogView.findViewById(R.id.dialogTvArea)).setText("Surface : " + habitat.getArea() + " m²");

        // Remplir la liste des équipements dynamiquement
        LinearLayout container = dialogView.findViewById(R.id.dialogAppliancesContainer);
        container.removeAllViews();

        for (Appliance app : habitat.getAppliances()) {
            RelativeLayout row = new RelativeLayout(getContext());
            row.setPadding(0, dp(12), 0, dp(12));

            // Icône équipement
            ImageView iv = new ImageView(getContext());
            iv.setId(View.generateViewId());
            iv.setImageResource(app.getIconResId() != 0 ? app.getIconResId()
                    : android.R.drawable.ic_menu_manage);
            iv.setColorFilter(Color.parseColor("#555555"));
            RelativeLayout.LayoutParams lpIcon =
                    new RelativeLayout.LayoutParams(dp(24), dp(24));
            lpIcon.addRule(RelativeLayout.ALIGN_PARENT_START);
            lpIcon.addRule(RelativeLayout.CENTER_VERTICAL);
            iv.setLayoutParams(lpIcon);

            // Consommation (droite)
            TextView tvWatts = new TextView(getContext());
            tvWatts.setId(View.generateViewId());
            tvWatts.setText(app.getWattage() + " W");
            tvWatts.setTextSize(15f);
            tvWatts.setTextColor(getWattageColor(app.getWattage()));
            tvWatts.setTypeface(null, android.graphics.Typeface.BOLD);
            RelativeLayout.LayoutParams lpWatts =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            lpWatts.addRule(RelativeLayout.ALIGN_PARENT_END);
            lpWatts.addRule(RelativeLayout.CENTER_VERTICAL);
            tvWatts.setLayoutParams(lpWatts);

            // Nom équipement (centre)
            TextView tvName = new TextView(getContext());
            tvName.setText(app.getName());
            tvName.setTextSize(15f);
            tvName.setTextColor(Color.BLACK);
            tvName.setPadding(dp(10), 0, dp(10), 0);
            RelativeLayout.LayoutParams lpName =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            lpName.addRule(RelativeLayout.END_OF, iv.getId());
            lpName.addRule(RelativeLayout.START_OF, tvWatts.getId());
            lpName.addRule(RelativeLayout.CENTER_VERTICAL);
            tvName.setLayoutParams(lpName);

            row.addView(iv);
            row.addView(tvWatts);
            row.addView(tvName);
            container.addView(row);
        }

        // Total de la consommation de l'habitat en bas du dialog
        TextView tvTotal = dialogView.findViewById(R.id.dialogTvTotalWattage);
        if (tvTotal != null) {
            tvTotal.setText("Total : " + habitat.getTotalWattage() + " W");
            tvTotal.setTextColor(getWattageColor(habitat.getTotalWattage()));
        }

        ((Button) dialogView.findViewById(R.id.btnCloseDialog))
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /** Convertit des dp en pixels selon la densité de l'écran. */
    private int dp(int value) {
        return (int) (value * getContext().getResources().getDisplayMetrics().density);
    }
}
