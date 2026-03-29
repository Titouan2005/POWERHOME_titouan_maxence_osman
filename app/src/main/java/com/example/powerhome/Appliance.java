package com.example.powerhome;

/**
 * Appliance — Modèle représentant un équipement électroménager.
 *
 * Correspond à une ligne de la table `appliances` en base de données.
 * La propriété iconResId est résolue localement côté Android
 * à partir de iconName reçu du serveur.
 */
public class Appliance {

    private int    id;
    private String name;
    private int    wattage;
    private String iconName;  // Nom de la ressource drawable (ex: "ic_laundry")
    private int    iconResId; // ID de ressource Android, résolu localement

    /** Constructeur pour données venant de l'API (sans iconResId). */
    public Appliance(int id, String name, int wattage, String iconName) {
        this.id       = id;
        this.name     = name;
        this.wattage  = wattage;
        this.iconName = iconName;
    }

    /** Constructeur pour données locales (hardcodées, tests). */
    public Appliance(String name, int iconResId, int wattage) {
        this.name      = name;
        this.iconResId = iconResId;
        this.wattage   = wattage;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWattage() {
        return wattage;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    /**
     * Retourne une description formatée de la consommation.
     * Ex : "Lave-linge — 2500 W"
     */
    @Override
    public String toString() {
        return name + " — " + wattage + " W";
    }
}
