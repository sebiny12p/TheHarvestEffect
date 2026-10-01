package com.seb.harvesteffect.model.entity;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.model.contract.CycleObserver;
import com.seb.harvesteffect.model.contract.Maintainable;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Abstract base for cybernetic Reaper support constructs stationed in star systems.
 */
public abstract class BiomechanicalUnit implements Maintainable, CycleObserver {
    protected final String designation;
    protected final int powerCapacity;
    protected int currentEnergy;
    protected boolean operational;

    public BiomechanicalUnit(String designation, int powerCapacity) {
        if (designation == null || designation.trim().isEmpty()) {
            throw new IllegalArgumentException("Construct designation cannot be blank.");
        }
        if (powerCapacity <= 0) {
            throw new IllegalArgumentException("Power capacity must be positive.");
        }
        this.designation = designation;
        this.powerCapacity = powerCapacity;
        this.currentEnergy = 0; // Starts uncharged until player calibrates
        this.operational = false;
    }

    @Override
    public void recharge(int powerUnits) {
        if (powerUnits < 0) {
            throw new IllegalArgumentException("Power recharge units must be positive.");
        }
        this.currentEnergy = Math.min(this.powerCapacity, this.currentEnergy + powerUnits);
        this.operational = this.currentEnergy > 0;
    }

    @Override
    public boolean isDepleted() {
        return currentEnergy <= 0;
    }

    @Override
    public int getEnergyLevel() {
        return currentEnergy;
    }

    @Override
    public void onCycleAdvancement(int newCycle, GalacticPhenomenon phenomenon) {
        // Natural energy dissipation over a 50,000-year epoch
        int drain = 25;
        if (phenomenon == GalacticPhenomenon.DARK_ENERGY_STORM) {
            drain = 50; // Severe cosmic drain
        }
        this.currentEnergy = Math.max(0, this.currentEnergy - drain);
        this.operational = this.currentEnergy > 0;
    }

    /**
     * Executes operational duties (surveillance sweep, ground suppression, or bombardment).
     *
     * @return tactical sweep report
     */
    public abstract String operationalSweep();

    /**
     * Extracts synthesized research telemetry or biological samples.
     *
     * @return HarvestYield resource packet
     */
    public abstract HarvestYield extractTelemetry();

    public String getDesignation() {
        return designation;
    }

    public int getPowerCapacity() {
        return powerCapacity;
    }

    public boolean isOperational() {
        return operational;
    }

    /**
     * Element Zero (Eezo) manufacturing and stationing cost for this unit.
     */
    public int getDeploymentCost() {
        return 60; // Base default deployment cost
    }

    /**
     * Genetic Biomass construction cost for this unit.
     * Mechanical drones require 0, while cybernetic shock troops and heavy bio-tanks require harvested flesh.
     */
    public int getBiomassCost() {
        return 0; // Base default Biomass cost
    }

    @Override
    public String toString() {
        return String.format("[%s | Core: %d/%d EP | Status: %s]",
                designation, currentEnergy, powerCapacity, operational ? "ACTIVE" : "DEPLETED");
    }
}
