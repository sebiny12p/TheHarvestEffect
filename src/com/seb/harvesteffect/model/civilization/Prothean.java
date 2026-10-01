package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Prothean: Extinct apex imperial civilization of the previous 50,000-year cycle.
 */
public class Prothean extends Civilization {

    public Prothean() {
        super("Prothean", "Eden Prime", "Precursor Memory Beacon Awakening & Biotic Supremacy", 3);
        this.darkEnergyYield = 350;
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(250, populationBillions * 20) + (evolutionaryTier * 60));
    }

    @Override
    public int calculateDarkEnergyYield() {
        // Ancient precursor technology amplifies Dark Energy harvest yield by x3.0
        return (int) (super.calculateDarkEnergyYield() * 3.0);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"Stand amongst the ashes of a trillion dead souls, and ask the ghosts if honor matters. The silence is your answer.\"";
    }

    @Override
    public int getSeedingCost() {
        return 200; // Legendary precursor empire (x3.0 dark energy harvest return)
    }
}
