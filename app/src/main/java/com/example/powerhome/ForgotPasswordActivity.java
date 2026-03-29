package com.example.powerhome;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.ion.Ion;

/**
 * ForgotPasswordActivity — Demande de réinitialisation du mot de passe.
 *
 * L'utilisateur saisit son email. Le serveur simule l'envoi d'un email
 * de reset. La réponse est toujours "succès" côté serveur pour ne pas
 * révéler quels emails sont enregistrés.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText    etEmail;
    private Button      btnSend;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail     = findViewById(R.id.et_email);
        btnSend     = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendResetRequest());
    }

    private void sendResetRequest() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Veuillez saisir votre email");
            return;
        }

        setLoading(true);

        Ion.with(this)
                .load("POST", Constants.URL_FORGOT_PASSWORD)
                .setBodyParameter("email", email)
                .asJsonObject()
                .setCallback((e, result) -> {
                    setLoading(false);

                    if (e != null || result == null) {
                        Toast.makeText(this, "Impossible de joindre le serveur.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Le serveur répond toujours succès (sécurité)
                    String message = result.get("message").getAsString();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    // Retour à l'écran de login après l'envoi
                    finish();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!loading);
    }
}
