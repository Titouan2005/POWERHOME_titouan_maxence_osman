package com.example.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail     = findViewById(R.id.et_email);
        etPassword  = findViewById(R.id.et_mdp);
        btnLogin    = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        Ion.with(this)
                .load("POST", Constants.URL_LOGIN)
                .setBodyParameter("email",    email)
                .setBodyParameter("password", password)
                .asJsonObject()
                .setCallback((e, result) -> {
                    btnLogin.setEnabled(true);

                    if (e != null || result == null) {
                        Toast.makeText(this,
                                "Serveur inaccessible. Vérifiez XAMPP.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean success = result.get("success").getAsBoolean();
                    if (!success) {
                        Toast.makeText(this,
                                result.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sauvegarder le token
                    JsonObject data = result.getAsJsonObject("data");
                    String token    = data.get("token").getAsString();
                    JsonObject user = data.getAsJsonObject("user");

                    String phone = (user.has("phone") && !user.get("phone").isJsonNull())
                            ? user.get("phone").getAsString() : "";
                    int habitatId = (user.has("habitat_id") && !user.get("habitat_id").isJsonNull())
                            ? user.get("habitat_id").getAsInt() : -1;
                    int ecoCoins  = user.has("eco_coins")
                            ? user.get("eco_coins").getAsInt() : 0;

                    new SessionManager(this).saveSession(
                            token,
                            user.get("id").getAsInt(),
                            user.get("firstname").getAsString(),
                            user.get("lastname").getAsString(),
                            user.get("email").getAsString(),
                            phone, habitatId, ecoCoins
                    );

                    // Aller à l'écran principal
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
}