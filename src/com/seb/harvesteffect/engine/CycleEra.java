package com.seb.harvesteffect.engine;

/**
 * Historical epoch eras in the 50,000-year Reaper extinction cycle.
 */
public enum CycleEra {
    ERA_DORMANCY(1, 10, "Inception Era", "Dormant star systems. Organics discover fire and basic tools."),
    ERA_EXPANSION(11, 25, "Awakening Era", "Organics master planetary spaceflight and discover Mass Relays."),
    ERA_CONVERGENCE(26, 40, "Ascendance Era", "Galactic community forms; cross-species trade and biotics flourish."),
    ERA_EXTINCTION(41, 50, "Extinction Zenith", "Apex maturity reached across the galaxy. The Harvest Effect begins.");

    private final int minEpoch;
    private final int maxEpoch;
    private final String displayName;
    private final String narrativeDescription;

    CycleEra(int minEpoch, int maxEpoch, String displayName, String narrativeDescription) {
        this.minEpoch = minEpoch;
        this.maxEpoch = maxEpoch;
        this.displayName = displayName;
        this.narrativeDescription = narrativeDescription;
    }

    public static CycleEra fromEpoch(int epoch) {
        for (CycleEra era : values()) {
            if (epoch >= era.minEpoch && epoch <= era.maxEpoch) {
                return era;
            }
        }
        return ERA_EXTINCTION;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNarrativeDescription() {
        return narrativeDescription;
    }
}
