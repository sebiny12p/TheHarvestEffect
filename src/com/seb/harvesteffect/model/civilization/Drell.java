package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Drell: Reptilian humanoids possessing eidetic photographic memories from Rakhana.
 */
public class Drell extends Civilization {

    public Drell() {
        super("Drell", "Rakhana", "Eidetic Synaptic Memory & Biotic Assassin Discipline", 2);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(150, populationBillions * 12) + (evolutionaryTier * 35));
    }

    @Override
    public int calculateDarkEnergyYield() {
        // Eidetic neural paths store dense synaptic energy
        return (int) (super.calculateDarkEnergyYield() * 1.5);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"Amonkira, Lord of Hunters, grant that my hands be steady, my aim true, and my feet swift.\"";
    }

    @Override
    public int getSeedingCost() {
        return 110; // Eidetic memory biotic species (+50% dark energy)
    }
}
