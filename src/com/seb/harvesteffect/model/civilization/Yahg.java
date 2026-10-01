package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Yahg: Apex hyper-predators possessing immense physical mass and savage intellect from Parnack.
 */
public class Yahg extends Civilization {

    public Yahg() {
        super("Yahg", "Parnack", "Apex Predatory Cunning & Shadow Hegemony", 5);
    }

    @Override
    public int calculateBiomassScore() {
        // Massive physical stature and predatory musculature (Shadow Broker species)
        return (int) (Math.min(280, populationBillions * 22) + (evolutionaryTier * 65));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"None stand above the Yahg. Not the Citadel Council, and not ancient machine leviathans.\"";
    }

    @Override
    public int getSeedingCost() {
        return 220; // Apex shadow predator species (highest biomass density)
    }
}
