package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Vorcha: Hyper-adaptive bipeds with non-differentiated cellular plasticity from Heshtok.
 */
public class Vorcha extends Civilization {

    public Vorcha() {
        super("Vorcha", "Heshtok", "Extreme Cellular Plasticity & Rapid Adaptation", 9);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(130, populationBillions * 11) + (evolutionaryTier * 30));
    }

    @Override
    public boolean isImmuneToPhenomenon(GalacticPhenomenon phenomenon) {
        // Extreme cellular adaptation shrugs off solar flares and dark energy storms
        return phenomenon == GalacticPhenomenon.SOLAR_FLARE_SURGE
                || phenomenon == GalacticPhenomenon.DARK_ENERGY_STORM;
    }

    @Override
    public String getSpecialDialogue() {
        return "\"Vorcha adapt! Vorcha survive! You burn us, we grow back tougher! Gaaah!\"";
    }

    @Override
    public int getSeedingCost() {
        return 40; // Cheap, resilient, hyper-adaptive species
    }
}
