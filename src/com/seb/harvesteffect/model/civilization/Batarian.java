package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Batarian Hegemony: Four-eyed autocratic industrialists and miners from Khar'shan.
 */
public class Batarian extends Civilization {

    public Batarian() {
        super("Batarian", "Khar'shan", "Autocratic Mobilization & Heavy Mining", 6);
    }

    @Override
    public int calculateBiomassScore() {
        // High industrial mobilization yields compact dense biomass matrices
        return (int) (Math.min(170, populationBillions * 13) + (evolutionaryTier * 40));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"The Hegemony recognizes neither your council nor your cyclic gods.\"";
    }

    @Override
    public int getSeedingCost() {
        return 60; // Labor and industrialist caste species
    }
}
