package com.example.powerhome;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager — Gestion de la session utilisateur via SharedPreferences.
 *
 * Stocke le token et les données de l'utilisateur connecté localement,
 * de façon persistante entre les lancements de l'application.
 *
 * Utilisation :
 *   SessionManager session = new SessionManager(context);
 *   session.saveSession(token, user);   // après login
 *   session.getToken();                 // récupérer le token
 *   session.clearSession();             // après logout
 */
public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Sauvegarde le token et les infos utilisateur après connexion réussie.
     */
    public void saveSession(String token, int userId, String firstname, String lastname,
                            String email, String phone, int habitatId, int ecoCoins) {
        editor.putString(Constants.KEY_TOKEN,      token);
        editor.putInt   (Constants.KEY_USER_ID,    userId);
        editor.putString(Constants.KEY_FIRSTNAME,  firstname);
        editor.putString(Constants.KEY_LASTNAME,   lastname);
        editor.putString(Constants.KEY_EMAIL,      email);
        editor.putString(Constants.KEY_PHONE,      phone != null ? phone : "");
        editor.putInt   (Constants.KEY_HABITAT_ID, habitatId);
        editor.putInt   (Constants.KEY_ECO_COINS,  ecoCoins);
        editor.apply();
    }

    /** Supprime toutes les données de session (logout). */
    public void clearSession() {
        editor.clear().apply();
    }

    /** Indique si un utilisateur est connecté (token présent). */
    public boolean isLoggedIn() {
        return prefs.getString(Constants.KEY_TOKEN, null) != null;
    }

    // --- Getters ---

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public int getUserId() {
        return prefs.getInt(Constants.KEY_USER_ID, -1);
    }

    public String getFirstname() {
        return prefs.getString(Constants.KEY_FIRSTNAME, "");
    }

    public String getLastname() {
        return prefs.getString(Constants.KEY_LASTNAME, "");
    }

    public String getEmail() {
        return prefs.getString(Constants.KEY_EMAIL, "");
    }

    public String getPhone() {
        return prefs.getString(Constants.KEY_PHONE, "");
    }

    public int getHabitatId() {
        return prefs.getInt(Constants.KEY_HABITAT_ID, -1);
    }

    public int getEcoCoins() {
        return prefs.getInt(Constants.KEY_ECO_COINS, 0);
    }

    /** Met à jour les données de profil localement après un edit_profile. */
    public void updateProfile(String firstname, String lastname, String phone) {
        editor.putString(Constants.KEY_FIRSTNAME, firstname);
        editor.putString(Constants.KEY_LASTNAME,  lastname);
        editor.putString(Constants.KEY_PHONE,     phone != null ? phone : "");
        editor.apply();
    }
}
