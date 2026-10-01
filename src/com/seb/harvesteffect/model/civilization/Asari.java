package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Asari: Mono-gendered biotic masters with millennial lifespans from Thessia.
 */
public class Asari extends Civilization {

    public Asari() {
        super("Asari", "Thessia", "Biotic Dark Energy Mastery", 5);
        this.darkEnergyYield = 250; // Starts with superior natural biotic resonance
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(180, populationBillions * 15) + (evolutionaryTier * 45));
    }

    @Override
    public int calculateDarkEnergyYield() {
        // Asari biotic resonance boosts Dark Energy harvest yield by +150%
        int base = super.calculateDarkEnergyYield();
        return (int) (base * 2.5);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"By the Goddess, the stars themselves tremble before the ancient ones.\"";
    }

    @Override
    public int getSeedingCost() {
        return 150; // High tier biotic species (+150% Eezo harvest yield)
    }
}
