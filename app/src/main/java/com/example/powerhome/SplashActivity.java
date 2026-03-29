package com.example.powerhome;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity — Écran de démarrage animé.
 *
 * Affiché pendant 2 secondes avec une animation de fondu et d'agrandissement.
 * Vérifie ensuite si un token de session est présent :
 * - Si oui → redirige directement vers MainActivity (l'utilisateur reste connecté)
 * - Si non → redirige vers LoginActivity
 *
 * Cet écran ne doit JAMAIS rester dans la pile de retour arrière.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Récupérer les vues pour l'animation
        ImageView logo     = findViewById(R.id.splash_logo);
        TextView  appName  = findViewById(R.id.splash_app_name);
        TextView  tagline  = findViewById(R.id.splash_tagline);

        // --- Animation : logo (scale + fade in) ---
        ObjectAnimator scaleX   = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.4f, 1f);
        ObjectAnimator scaleY   = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.4f, 1f);
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(logo, View.ALPHA,   0f,   1f);

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(scaleX, scaleY, logoFade);
        logoAnim.setDuration(700);

        // --- Animation : textes (fade in décalé) ---
        ObjectAnimator textFade1 = ObjectAnimator.ofFloat(appName, View.ALPHA, 0f, 1f);
        textFade1.setStartDelay(400);
        textFade1.setDuration(600);

        ObjectAnimator textFade2 = ObjectAnimator.ofFloat(tagline, View.ALPHA, 0f, 1f);
        textFade2.setStartDelay(700);
        textFade2.setDuration(600);

        AnimatorSet fullAnim = new AnimatorSet();
        fullAnim.playTogether(logoAnim, textFade1, textFade2);
        fullAnim.start();

        // --- Redirection après SPLASH_DURATION_MS ---
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DURATION_MS);
    }

    /**
     * Détermine vers quelle activité rediriger selon la présence d'une session.
     */
    private void navigateNext() {
        SessionManager session = new SessionManager(this);

        Intent intent;
        if (session.isLoggedIn()) {
            // Session active → on va directement à l'écran principal
            intent = new Intent(this, MainActivity.class);
        } else {
            // Pas de session → écran de connexion
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        // finish() empêche de revenir sur le splash avec le bouton retour
        finish();
    }
}
