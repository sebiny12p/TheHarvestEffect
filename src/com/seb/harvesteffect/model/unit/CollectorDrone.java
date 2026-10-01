package com.seb.harvesteffect.model.unit;

import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Collector Drone: Airborne insectoid surveyor extracting preliminary genetic samples.
 */
public class CollectorDrone extends BiomechanicalUnit {
    private boolean sampleReady;

    public CollectorDrone() {
        this("Standard");
    }

    public CollectorDrone(String unitId) {
        super("Collector Drone [" + unitId + "]", 100);
        this.sampleReady = false;
    }

    @Override
    public String operationalSweep() {
        if (!operational) {
            return designation + " is offline. Recharge required.";
        }
        sampleReady = true;
        return designation + " completed planetary bio-scan. Telemetry packet ready for retrieval.";
    }

    @Override
    public HarvestYield extractTelemetry() {
        if (!sampleReady) {
            return null;
        }
        sampleReady = false;
        return new HarvestYield("Telemetry Matrix", 40, 80);
    }

    @Override
    public int getDeploymentCost() {
        return 75; // Growth catalyst & bio-surveyor deployment cost
    }

    @Override
    public int getBiomassCost() {
        return 0; // Mechanical scout: 0 Biomass required (deployable before first harvest)
    }
}
