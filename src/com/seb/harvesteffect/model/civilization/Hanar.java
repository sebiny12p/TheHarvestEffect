package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Hanar: Invertebrate aquatic beings devoted to the Enkindlers (Protheans) from Kahje.
 */
public class Hanar extends Civilization {

    public Hanar() {
        super("Hanar", "Kahje", "Enkindler Devotion & Beacon Resonance", 3);
        // Spiritual attunement to Prothean relays
        this.relayLinked = true;
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(120, populationBillions * 10) + (evolutionaryTier * 30));
    }

    @Override
    public int calculateDarkEnergyYield() {
        // Enkindler reverent shrines produce purified dark energy currents
        return (int) (super.calculateDarkEnergyYield() * 1.4);
    }

    @Override
    public String getSpecialDialogue() {
        return "\"This one inquires whether its particle discharge array is expended, or if it retains another burst.\"";
    }

    @Override
    public int getSeedingCost() {
        return 100; // Auto-relay tethering aquatic species
    }
}
