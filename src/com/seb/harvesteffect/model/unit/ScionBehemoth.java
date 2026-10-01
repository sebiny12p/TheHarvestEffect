package com.seb.harvesteffect.model.unit;

import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Scion Behemoth: Massive composite bio-tank firing high-yield biotic shockwaves.
 */
public class ScionBehemoth extends BiomechanicalUnit {
    private int shockwaveEnergy;

    public ScionBehemoth() {
        this("Standard");
    }

    public ScionBehemoth(String unitId) {
        super("Scion Behemoth [" + unitId + "]", 150);
        this.shockwaveEnergy = 0;
    }

    @Override
    public String operationalSweep() {
        if (!operational) {
            return designation + " core inert. Heavy kinetic barrier offline.";
        }
        shockwaveEnergy += 40;
        return designation + " fortified planetary perimeter with high-frequency biotic shockwaves.";
    }

    @Override
    public HarvestYield extractTelemetry() {
        if (shockwaveEnergy <= 0) {
            return null;
        }
        int power = shockwaveEnergy;
        shockwaveEnergy = 0;
        return new HarvestYield("Biotic Shock Core", power * 3, power * 2);
    }

    @Override
    public int getDeploymentCost() {
        return 60; // Biotic shock amplifier components
    }

    @Override
    public int getBiomassCost() {
        return 140; // Massive composite bio-tank fused from harvested organisms
    }
}
