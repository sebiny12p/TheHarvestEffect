package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Volus: Ammonia-breathing financial savants and masters of the galactic Eezo economy from Irune.
 */
public class Volus extends Civilization {

    public Volus() {
        super("Volus", "Irune", "Galactic Banking Cartel & Compound Eezo Interest", 4);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(110, populationBillions * 9) + (evolutionaryTier * 25));
    }

    @Override
    public int getPassiveEezoDividend() {
        // Generates passive Element Zero dividend every epoch based on economic maturity tier
        return 50 * (evolutionaryTier + 1);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"[Exhales pressure] *Ksshh* We are the architects of the galactic credit exchange. All must balance!\"";
    }

    @Override
    public int getSeedingCost() {
        return 80; // High passive ROI dividend banking species
    }
}
