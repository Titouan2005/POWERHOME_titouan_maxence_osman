package com.example.powerhome;

/**
 * Constants — Constantes globales de l'application PowerHome.
 *
 * Centraliser l'URL de base ici permet de changer l'environnement
 * (dev / prod) en modifiant un seul endroit.
 *
 * 10.0.2.2 est l'alias d'Android Studio pour "localhost" de la machine hôte.
 * Remplacer par votre IP locale (ex: 192.168.1.42) pour un vrai appareil.
 */
public class Constants {

    // URL de base du serveur PHP
    // Émulateur Android : 10.0.2.2 = localhost de la machine hôte
    public static final String BASE_URL = "http://10.0.2.2/powerhome/api";

    // Endpoints
    public static final String URL_LOGIN           = BASE_URL + "/login.php";
    public static final String URL_REGISTER        = BASE_URL + "/register.php";
    public static final String URL_FORGOT_PASSWORD = BASE_URL + "/forgot_password.php";
    public static final String URL_EDIT_PROFILE    = BASE_URL + "/edit_profile.php";
    public static final String URL_HABITATS        = BASE_URL + "/habitats.php";
    public static final String URL_MY_HABITAT      = BASE_URL + "/my_habitat.php";

    // Clés SharedPreferences (SessionManager)
    public static final String PREF_NAME       = "PowerHomePrefs";
    public static final String KEY_TOKEN       = "token";
    public static final String KEY_USER_ID     = "user_id";
    public static final String KEY_FIRSTNAME   = "firstname";
    public static final String KEY_LASTNAME    = "lastname";
    public static final String KEY_EMAIL       = "email";
    public static final String KEY_PHONE       = "phone";
    public static final String KEY_HABITAT_ID  = "habitat_id";
    public static final String KEY_ECO_COINS   = "eco_coins";
}
