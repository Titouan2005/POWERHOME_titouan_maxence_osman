package com.example.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

/**
 * MainActivity — Activité principale avec Navigation Drawer.
 *
 * Groupe 3 de l'énoncé : organisation des vues en Drawer.
 * Contient 4 fragments accessibles via le menu latéral :
 *   1. Liste des habitats   (HabitatsFragment)
 *   2. Mon habitat          (MyHabitatFragment)
 *   3. Mes notifications    (NotificationsFragment)
 *   4. Mes préférences      (PreferencesFragment)
 *
 * La boîte de dialogue "À propos" est accessible depuis le menu.
 * Le header du Drawer affiche le nom et l'email du résident connecté.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout   drawerLayout;
    private NavigationView navigationView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session        = new SessionManager(this);
        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Remplir le header du Drawer avec les infos de session
        setupDrawerHeader();

        navigationView.setNavigationItemSelectedListener(this);

        // Ouvrir/fermer le Drawer depuis le bouton burger
        findViewById(R.id.btn_burger).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        // Charger le fragment par défaut (liste des habitats) au premier démarrage
        if (savedInstanceState == null) {
            loadFragment(new HabitatsFragment(), false);
            navigationView.setCheckedItem(R.id.nav_habitats);
            setToolbarTitle(getString(R.string.nav_habitats));
        }
    }

    /**
     * Affiche le prénom, nom et email dans l'en-tête du menu latéral.
     */
    private void setupDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName  = headerView.findViewById(R.id.header_name);
        TextView tvEmail = headerView.findViewById(R.id.header_email);

        String fullName = session.getFirstname() + " " + session.getLastname();
        tvName.setText(fullName.trim().isEmpty() ? "Résident" : fullName);
        tvEmail.setText(session.getEmail());
    }

    /**
     * Gestion des clics sur les items du menu latéral.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_habitats) {
            loadFragment(new HabitatsFragment(), false);
            setToolbarTitle(getString(R.string.nav_habitats));

        } else if (id == R.id.nav_my_habitat) {
            loadFragment(new MyHabitatFragment(), false);
            setToolbarTitle(getString(R.string.nav_my_habitat));

        } else if (id == R.id.nav_notifications) {
            loadFragment(new NotificationsFragment(), false);
            setToolbarTitle(getString(R.string.nav_notifications));

        } else if (id == R.id.nav_preferences) {
            loadFragment(new PreferencesFragment(), false);
            setToolbarTitle(getString(R.string.nav_preferences));

        } else if (id == R.id.nav_about) {
            // Boîte de dialogue "À propos" (pas un fragment, mais un dialog)
            showAboutDialog();

        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Remplace le fragment actuel dans le conteneur principal.
     *
     * @param fragment  Le fragment à afficher
     * @param addToBack true pour ajouter à la pile de retour (navigation imbriquée)
     */
    public void loadFragment(Fragment fragment, boolean addToBack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment);

        if (addToBack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /** Met à jour le titre dans la barre d'outils. */
    public void setToolbarTitle(String title) {
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        if (tvTitle != null) tvTitle.setText(title);
    }

    /**
     * Affiche la boîte de dialogue "À propos" de l'application.
     * Demandé explicitement dans l'énoncé (Groupe 3).
     */
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton("Fermer", null)
                .show();
    }

    /**
     * Déconnecte l'utilisateur : supprime la session et revient au Login.
     */
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    session.clearSession();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Non", null)
                .show();
    }

    /**
     * Bouton retour physique : ferme le Drawer s'il est ouvert,
     * sinon comportement normal (retour fragment ou quitte l'app).
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
