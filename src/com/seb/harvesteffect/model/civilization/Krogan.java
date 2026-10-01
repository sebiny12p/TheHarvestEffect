package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Krogan: Hulking reptilian warriors with redundant organ systems from Tuchanka.
 */
public class Krogan extends Civilization {

    public Krogan() {
        super("Krogan", "Tuchanka", "Redundant Organ Physiology & Cellular Regeneration", 10);
    }

    @Override
    public int calculateBiomassScore() {
        // Redundant organs, massive bone plates, and thick hides yield high biomass
        return (int) (Math.min(220, populationBillions * 18) + (evolutionaryTier * 50));
    }

    @Override
    public boolean isImmuneToPhenomenon(GalacticPhenomenon phenomenon) {
        // Unyielding stamina resists insurrection collapse and severe storms
        return phenomenon == GalacticPhenomenon.ORGANIC_REBELLION;
    }

    @Override
    public String getSpecialDialogue() {
        return "\"I AM URDNOT WREX, AND THIS IS MY PLANET! COME GET SOME!\"";
    }

    @Override
    public int getSeedingCost() {
        return 120; // Heavy biomass titan species
    }
}
