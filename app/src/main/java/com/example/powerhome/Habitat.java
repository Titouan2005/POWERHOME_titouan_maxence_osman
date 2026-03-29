package com.example.powerhome;

import java.util.ArrayList;
import java.util.List;

/**
 * Habitat — Modèle représentant un logement dans la résidence.
 *
 * Chaque habitat appartient à un résident et contient une liste
 * d'équipements. La consommation totale est calculée à partir
 * de la somme des puissances des équipements.
 */
public class Habitat {

    private int              id;
    private String           name;
    private int              floor;
    private double           area;
    private List<Appliance>  appliances;
    private int              totalWattage; // Reçu du serveur ou calculé localement

    /** Constructeur complet (données issues de l'API). */
    public Habitat(int id, String name, int floor, double area,
                   List<Appliance> appliances, int totalWattage) {
        this.id           = id;
        this.name         = name;
        this.floor        = floor;
        this.area         = area;
        this.appliances   = appliances;
        this.totalWattage = totalWattage;
    }

    /** Constructeur pour données locales (rétrocompatibilité). */
    public Habitat(String name, int floor, double area, List<Appliance> appliances) {
        this.name       = name;
        this.floor      = floor;
        this.area       = area;
        this.appliances = appliances != null ? appliances : new ArrayList<>();
        // Calculer le total localement
        this.totalWattage = calculateTotalWattage();
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFloor() {
        return floor;
    }

    public double getArea() {
        return area;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public int getTotalWattage() {
        return totalWattage;
    }

    /** Nombre d'équipements dans l'habitat. */
    public int getEquipmentCount() {
        return appliances != null ? appliances.size() : 0;
    }

    /**
     * Calcule la consommation totale en additionnant les watts de chaque équipement.
     * Utilisé quand les données viennent localement (sans champ total_wattage du serveur).
     */
    private int calculateTotalWattage() {
        if (appliances == null) return 0;
        int total = 0;
        for (Appliance a : appliances) {
            total += a.getWattage();
        }
        return total;
    }
}
