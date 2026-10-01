package com.seb.harvesteffect.model.contract;

import com.seb.harvesteffect.engine.GalacticPhenomenon;

/**
 * Observer contract receiving cosmic cycle notifications and environmental phenomena.
 */
public interface CycleObserver {

    /**
     * Triggered whenever the cosmic cycle advances an epoch (50,000 years).
     *
     * @param newCycle the incremented cycle epoch index
     * @param phenomenon the cosmic event occurring across the galactic sector
     */
    void onCycleAdvancement(int newCycle, GalacticPhenomenon phenomenon);
}
