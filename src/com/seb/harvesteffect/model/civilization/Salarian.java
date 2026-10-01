package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Salarian Union: Amphibious hyper-intellects with rapid metabolisms from Sur'Kesh.
 */
public class Salarian extends Civilization {

    public Salarian() {
        super("Salarian", "Sur'Kesh", "Hyperactive Cognition & Accelerated Research", 6);
    }

    @Override
    public void advanceEpoch(GalacticPhenomenon phenomenon) {
        super.advanceEpoch(phenomenon);
        // Accelerated cognitive metabolism yields additional research demographic jump
        this.populationBillions = (int) (this.populationBillions * 1.25);
    }

    @Override
    public int calculateBiomassScore() {
        return (int) (Math.min(130, populationBillions * 10) + (evolutionaryTier * 30));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"I am the very model of a scientist salarian, I've studied species turian, asari, and batarian...\"";
    }

    @Override
    public int getSeedingCost() {
        return 75; // Fast demographic growth species
    }
}
