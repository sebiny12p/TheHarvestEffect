package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Quarian: Envirosuit-clad cybernetic engineers and master starship pilots from Rannoch.
 */
public class Quarian extends Civilization {

    public Quarian() {
        super("Quarian", "Rannoch", "Cybernetic Integration & Mass Relay Tuning", 4);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(140, populationBillions * 11) + (evolutionaryTier * 35));
    }

    @Override
    public double getRelayBandwidthBoost() {
        // Quarian engineering mastery optimizes Mass Relay transit conduits
        return 0.5;
    }

    @Override
    public int calculateDarkEnergyYield() {
        // Advanced cybernetic suit implants channel additional dark energy
        return (int) (super.calculateDarkEnergyYield() * 1.3);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"Keelah se'lai. Even against gods of dark space, the Migrant Fleet endures.\"";
    }

    @Override
    public int getSeedingCost() {
        return 85; // Cybernetic relay engineering species
    }
}
