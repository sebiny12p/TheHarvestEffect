package com.seb.harvesteffect.engine;

/**
 * Cosmic events dispatched to all star system CycleObservers when an epoch advances.
 */
public enum GalacticPhenomenon {
    STELLAR_CALM("Stellar Calm", "Equilibrium across the stars. Organics evolve along predetermined paths.", 1.0),
    SOLAR_FLARE_SURGE("Solar Flare Surge", "Coronal mass ejections mutate organic DNA, accelerating evolution.", 1.4),
    DARK_ENERGY_STORM("Dark Energy Storm", "Element Zero spatial disruptions drain construct power arrays.", 0.8),
    MASS_RELAY_OVERCHARGE("Mass Relay Overcharge", "Gravitational resonance automatically activates dormant beacons.", 1.2),
    ORGANIC_REBELLION("Organic Insurrection", "Armed resistance movements challenge planetary biomechanical units.", 0.7);

    private final String title;
    private final String description;
    private final double growthMultiplier;

    GalacticPhenomenon(String title, String description, double growthMultiplier) {
        this.title = title;
        this.description = description;
        this.growthMultiplier = growthMultiplier;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getGrowthMultiplier() {
        return growthMultiplier;
    }
}
