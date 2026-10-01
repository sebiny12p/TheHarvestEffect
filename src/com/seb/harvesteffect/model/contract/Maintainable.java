package com.seb.harvesteffect.model.contract;

/**
 * Contract for biomechanical constructs requiring periodic energy calibration.
 */
public interface Maintainable {

    /**
     * Replenishes the unit's core with dark-matter energy.
     *
     * @param powerUnits quantity of power to restore
     */
    void recharge(int powerUnits);

    /**
     * Checks if the unit's energy reserve has hit critical exhaustion.
     *
     * @return true if depleted and inert
     */
    boolean isDepleted();

    /**
     * Remaining operational power percentage (0-100).
     *
     * @return energy level
     */
    int getEnergyLevel();
}
