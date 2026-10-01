package com.seb.harvesteffect.engine;

/**
 * Defines whether civilization crop growth is advanced manually turn-by-turn
 * or continuously in real time.
 */
public enum GrowthMode {
    TURN_BASED("Turn-Based", "Manual Epoch Advancement (Strategic Turn Pace)"),
    REAL_TIME("Real-Time", "Continuous Real-Time Clock (Autonomous Galactic Evolution)");

    private final String displayName;
    private final String description;

    GrowthMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
