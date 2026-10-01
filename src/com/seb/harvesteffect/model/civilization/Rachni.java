package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Rachni: Insectoid hive-mind species communicating via quantum acoustic songs from Suen.
 */
public class Rachni extends Civilization {

    public Rachni() {
        super("Rachni", "Suen", "Quantum Hive-Mind Song & Sub-Space Resonance", 12);
        this.relayLinked = true; // Natural FTL resonance functions as intrinsic relay link
    }

    @Override
    public int calculateBiomassScore() {
        // Chitinous exoskeletons and massive drone broods yield immense biomass
        return (int) (Math.min(210, populationBillions * 17) + (evolutionaryTier * 50));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"We hear the sour yellow note of the oily shadows. The queens sing the ancient memory of the cleansing.\"";
    }

    @Override
    public int getSeedingCost() {
        return 130; // Insectoid hive-mind species with intrinsic relay link
    }
}
