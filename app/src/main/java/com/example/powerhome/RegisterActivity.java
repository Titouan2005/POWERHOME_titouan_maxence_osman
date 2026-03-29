package com.example.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.Arrays;
import java.util.List;

/**
 * RegisterActivity — Inscription d'un nouveau résident.
 *
 * Collecte : prénom, nom, email, mot de passe, numéro de téléphone.
 * Envoie les données au serveur, récupère un token et redirige vers MainActivity.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText    etFirstname, etLastname, etEmail, etPassword, etPhone;
    private Spinner     spinnerCountryCode;
    private Button      btnCreate;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liaison des vues
        etFirstname       = findViewById(R.id.et_firstname);
        etLastname        = findViewById(R.id.et_lastname);
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etPhone           = findViewById(R.id.et_phone);
        spinnerCountryCode = findViewById(R.id.spinner_codes);
        btnCreate         = findViewById(R.id.btn_create);
        progressBar       = findViewById(R.id.progress_bar);

        // Bouton retour
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Remplir le Spinner des indicatifs téléphoniques
        setupCountrySpinner();

        btnCreate.setOnClickListener(v -> attemptRegister());
    }

    /** Configure le Spinner avec la liste des indicatifs. */
    private void setupCountrySpinner() {
        List<String> codes = Arrays.asList(
                "+33 (FR)", "+212 (MA)", "+213 (DZ)", "+216 (TN)",
                "+221 (SN)", "+225 (CI)", "+237 (CM)", "+242 (CG)",
                "+32 (BE)", "+41 (CH)", "+44 (UK)", "+49 (DE)",
                "+39 (IT)", "+34 (ES)", "+1 (US/CA)", "+971 (AE)"
        );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, codes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);
    }

    /** Valide les champs puis envoie la requête d'inscription. */
    private void attemptRegister() {
        String firstname = etFirstname.getText().toString().trim();
        String lastname  = etLastname.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String phone     = spinnerCountryCode.getSelectedItem().toString()
                + " " + etPhone.getText().toString().trim();

        // Validation locale
        boolean valid = true;
        if (firstname.isEmpty()) { etFirstname.setError("Obligatoire"); valid = false; }
        if (lastname.isEmpty())  { etLastname.setError("Obligatoire");  valid = false; }
        if (email.isEmpty())     { etEmail.setError("Obligatoire");     valid = false; }
        if (password.isEmpty())  { etPassword.setError("Obligatoire");  valid = false; }
        if (password.length() < 6 && !password.isEmpty()) {
            etPassword.setError("6 caractères minimum");
            valid = false;
        }
        if (!valid) return;

        setLoading(true);

        // --- Requête POST ---
        Ion.with(this)
                .load("POST", Constants.URL_REGISTER)
                .setBodyParameter("firstname", firstname)
                .setBodyParameter("lastname",  lastname)
                .setBodyParameter("email",     email)
                .setBodyParameter("password",  password)
                .setBodyParameter("phone",     phone)
                .asJsonObject()
                .setCallback((e, result) -> {
                    setLoading(false);

                    if (e != null || result == null) {
                        Toast.makeText(this, "Impossible de joindre le serveur.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean success = result.get("success").getAsBoolean();

                    if (!success) {
                        Toast.makeText(this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sauvegarder la session comme après un login
                    JsonObject data = result.getAsJsonObject("data");
                    String     token = data.get("token").getAsString();
                    JsonObject user  = data.getAsJsonObject("user");

                    new SessionManager(this).saveSession(
                            token,
                            user.get("id").getAsInt(),
                            user.get("firstname").getAsString(),
                            user.get("lastname").getAsString(),
                            user.get("email").getAsString(),
                            phone,
                            -1, // Pas encore d'habitat assigné
                            0
                    );

                    Toast.makeText(this, "Bienvenue !", Toast.LENGTH_SHORT).show();

                    // Aller à l'écran principal et vider la pile de retour
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!loading);
    }
}
