package com.seb.harvesteffect.model.civilization;

import com.seb.harvesteffect.model.entity.Civilization;

/**
 * Elcor: Massive high-gravity quadrupeds with deliberate vocal qualifiers from Dekuuna.
 */
public class Elcor extends Civilization {

    public Elcor() {
        super("Elcor", "Dekuuna", "High-Gravity Skeletal Density & Living Artillery", 4);
    }

    @Override
    public int calculateBiomassScore() {
        // Massive physical stature and dense skeletal tissue
        return (int) (Math.min(200, populationBillions * 16) + (evolutionaryTier * 45));
    }

    @Override
    public String getSpecialDialogue() {
        return "\"[With deep, somber defiance] We stand unbroken upon high-gravity soil. Bring forth your harvesters.\"";
    }

    @Override
    public int getSeedingCost() {
        return 115; // High gravity heavy skeletal species
    }
}
