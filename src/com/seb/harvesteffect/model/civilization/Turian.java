package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Turian Hierarchy: Avian meritocracy with metallic carapaces from Palaven.
 */
public class Turian extends Civilization {

    public Turian() {
        super("Turian", "Palaven", "Meritocratic Fortification & Dextro-Biology", 7);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(160, populationBillions * 14) + (evolutionaryTier * 40));
    }

    @Override
    public boolean isImmuneToPhenomenon(GalacticPhenomenon phenomenon) {
        // Metallic carapace and military grid shielding shields against Solar Flares
        return phenomenon == GalacticPhenomenon.SOLAR_FLARE_SURGE;
    }

    @Override
    public String getSpecialDialogue() {
        return "\"Palaven defense array is currently in the middle of some calibrations.\"";
    }

    @Override
    public int getSeedingCost() {
        return 90; // Militarized carapace species
    }
}
