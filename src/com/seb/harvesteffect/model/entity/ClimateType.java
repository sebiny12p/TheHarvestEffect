package com.seb.harvesteffect.model.entity;

import java.io.Serializable;

/**
 * Planetary Climate Biomes across the Milky Way Galaxy.
 * Dictates organic species growth compatibility (+50% ideal vs -50% hostile)
 * and harvest yields (+25% ideal vs -25% hostile).
 */
public enum ClimateType implements Serializable {
    GARDEN(
            "Garden / Oceanic",
            "Azure oceans and emerald continents. Ideal for Humanity, Asari, Salarian, Prothean, and Hanar.",
            new String[] { "humanity", "human", "asari", "salarian", "prothean", "hanar" },
            new String[] { "vorcha", "krogan", "drell", "batarian", "yahg", "volus", "elcor" }
    ),
    ARID(
            "Arid / Scorched",
            "Rust dunes, radioactive craters and canyon plains. Ideal for Turian, Krogan, Quarian, Vorcha, and Drell.",
            new String[] { "turian", "krogan", "quarian", "vorcha", "drell" },
            new String[] { "hanar", "salarian" }
    ),
    METHANE(
            "Dense / Methane",
            "Golden methane clouds and high-pressure rings. Ideal for Volus and Elcor.",
            new String[] { "volus", "elcor" },
            new String[] { "humanity", "human", "asari", "turian", "rachni" }
    ),
    VOLCANIC(
            "Volcanic / Basalt",
            "Obsidian crust and glowing magma fissures. Ideal for Batarians, Yahg, and Rachni.",
            new String[] { "batarian", "yahg", "rachni" },
            new String[] { "salarian", "quarian", "prothean", "hanar" }
    ),
    BARREN(
            "Barren / Frozen",
            "Glacial ice sheets and cratered dead rock. Uninhabitable until terraformed.",
            new String[] {},
            new String[] { "humanity", "human", "asari", "turian", "salarian", "krogan", "quarian",
                           "batarian", "volus", "hanar", "drell", "elcor", "vorcha", "rachni", "prothean", "yahg" }
    );

    private final String displayName;
    private final String description;
    private final String[] idealSpecies;
    private final String[] hostileSpecies;

    ClimateType(String displayName, String description, String[] idealSpecies, String[] hostileSpecies) {
        this.displayName = displayName;
        this.description = description;
        this.idealSpecies = idealSpecies;
        this.hostileSpecies = hostileSpecies;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIdealFor(String speciesName) {
        if (speciesName == null) return false;
        String s = speciesName.toLowerCase().trim();
        for (String ideal : idealSpecies) {
            if (s.contains(ideal)) return true;
        }
        return false;
    }

    public boolean isHostileFor(String speciesName) {
        if (speciesName == null) return false;
        String s = speciesName.toLowerCase().trim();
        for (String hostile : hostileSpecies) {
            if (s.contains(hostile)) return true;
        }
        return false;
    }

    public double getGrowthMultiplier(String speciesName) {
        if (this == BARREN) return 0.25;
        if (isIdealFor(speciesName)) return 1.50;
        if (isHostileFor(speciesName)) return 0.50;
        return 1.00;
    }

    public double getHarvestYieldMultiplier(String speciesName) {
        if (this == BARREN) return 0.50;
        if (isIdealFor(speciesName)) return 1.25;
        if (isHostileFor(speciesName)) return 0.75;
        return 1.00;
    }

    public String getAffinityLabel(String speciesName) {
        if (this == BARREN) return "❄️ BARREN (Uninhabitable: -75% Growth)";
        if (isIdealFor(speciesName)) return "✨ IDEAL CLIMATE (+50% Growth, +25% Yield)";
        if (isHostileFor(speciesName)) return "⚠️ HOSTILE CLIMATE (-50% Growth, -25% Yield)";
        return "⚖️ MODERATE CLIMATE (Standard Growth)";
    }

    public String getAffinityDescription(String speciesName) {
        return getAffinityLabel(speciesName);
    }

    public String getFavorableSpeciesNames() {
        if (idealSpecies.length == 0) return "None";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idealSpecies.length; i++) {
            String s = idealSpecies[i];
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
            if (i < idealSpecies.length - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
