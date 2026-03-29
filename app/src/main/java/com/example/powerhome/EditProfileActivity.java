package com.example.powerhome;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

/**
 * EditProfileActivity — Modification du profil du résident connecté.
 *
 * Pré-remplit les champs avec les données de session.
 * Envoie le token en en-tête Authorization pour authentifier la requête.
 * Met à jour les données locales (SessionManager) après succès.
 */
public class EditProfileActivity extends AppCompatActivity {

    private EditText    etFirstname, etLastname, etPhone, etCurrentPassword, etNewPassword;
    private Button      btnSave;
    private ProgressBar progressBar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        session = new SessionManager(this);

        // Liaison des vues
        etFirstname      = findViewById(R.id.et_firstname);
        etLastname       = findViewById(R.id.et_lastname);
        etPhone          = findViewById(R.id.et_phone);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword    = findViewById(R.id.et_new_password);
        btnSave          = findViewById(R.id.btn_save);
        progressBar      = findViewById(R.id.progress_bar);

        // Bouton retour
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Pré-remplir avec les données de session existantes
        etFirstname.setText(session.getFirstname());
        etLastname.setText(session.getLastname());
        etPhone.setText(session.getPhone());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstname       = etFirstname.getText().toString().trim();
        String lastname        = etLastname.getText().toString().trim();
        String phone           = etPhone.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword     = etNewPassword.getText().toString().trim();

        if (firstname.isEmpty()) { etFirstname.setError("Obligatoire"); return; }
        if (lastname.isEmpty())  { etLastname.setError("Obligatoire");  return; }

        if (!newPassword.isEmpty() && newPassword.length() < 6) {
            etNewPassword.setError("6 caractères minimum");
            return;
        }

        setLoading(true);

        // Construire la requête — le token est transmis dans l'en-tête Authorization
        Ion.with(this)
                .load("POST", Constants.URL_EDIT_PROFILE)
                .addHeader("Authorization", "Bearer " + session.getToken())
                .setBodyParameter("firstname",        firstname)
                .setBodyParameter("lastname",         lastname)
                .setBodyParameter("phone",            phone)
                .setBodyParameter("current_password", currentPassword)
                .setBodyParameter("new_password",     newPassword)
                .asJsonObject()
                .setCallback((e, result) -> {
                    setLoading(false);

                    if (e != null || result == null) {
                        Toast.makeText(this, "Impossible de joindre le serveur.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean success = result.get("success").getAsBoolean();
                    String  message = result.get("message").getAsString();

                    if (!success) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Mettre à jour les données en session locale
                    session.updateProfile(firstname, lastname, phone);

                    Toast.makeText(this, "Profil mis à jour !", Toast.LENGTH_SHORT).show();
                    finish(); // Retour au fragment profil ou MainActivity
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}
