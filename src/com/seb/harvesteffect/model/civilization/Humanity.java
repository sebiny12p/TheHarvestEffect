package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Humanity (Homo Sapiens): Adaptable, ambitious, and genetically diverse.
 */
public class Humanity extends Civilization {

    public Humanity() {
        super("Humanity", "Earth", "Genetic Plasticity & Rapid Adaptation", 8);
    }

    @Override
    public int calculateBiomassScore() {
        // High genetic variance yields higher base biomass multiplier
        return (int) (Math.min(150, populationBillions * 12) + (evolutionaryTier * 40));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"I'm Commander Shepard, and this is my favorite star system in the galaxy.\"";
    }

    @Override
    public int getSeedingCost() {
        return 50; // Versatile starter species
    }
}
