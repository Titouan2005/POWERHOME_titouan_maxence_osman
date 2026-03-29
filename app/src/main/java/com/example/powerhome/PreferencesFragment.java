package com.example.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * PreferencesFragment — Vue "Mes préférences".
 *
 * Permet au résident de :
 *   - Activer/désactiver les notifications push
 *   - Choisir la langue de l'interface
 *   - Accéder à la modification de son profil
 *   - Se déconnecter
 */
public class PreferencesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());

        // Afficher le nom de l'utilisateur
        TextView tvName = view.findViewById(R.id.tv_pref_username);
        tvName.setText(session.getFirstname() + " " + session.getLastname());

        // Bouton "Modifier mon profil"
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class)));

        // Bouton "Se déconnecter"
        view.findViewById(R.id.btn_logout).setOnClickListener(v ->
                showLogoutDialog(session));
    }

    private void showLogoutDialog(SessionManager session) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    session.clearSession();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
