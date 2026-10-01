package com.seb.harvesteffect.model.unit;

import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Husk Swarm: Cybernetically converted organic shock troops suppressing resistance.
 */
public class HuskSwarm extends BiomechanicalUnit {
    private int processedTroops;

    public HuskSwarm() {
        this("Standard");
    }

    public HuskSwarm(String unitId) {
        super("Husk Swarm [" + unitId + "]", 80);
        this.processedTroops = 100;
    }

    @Override
    public String operationalSweep() {
        if (!operational) {
            return designation + " lacks power to maintain cyber-synapse leash.";
        }
        processedTroops += 50;
        return designation + " pacified planetary insurgencies. Assimilated biomass ready.";
    }

    @Override
    public HarvestYield extractTelemetry() {
        if (processedTroops <= 0) {
            return null;
        }
        int yield = processedTroops;
        processedTroops = 0;
        return new HarvestYield("Husk Ground Biomass", yield * 2, 20);
    }

    @Override
    public int getDeploymentCost() {
        return 30; // Refined cybernetic control implants
    }

    @Override
    public int getBiomassCost() {
        return 50; // Assimilated organic bodies converted into cybernetic shock troops
    }
}
