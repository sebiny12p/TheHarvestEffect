package com.seb.harvesteffect.model.contract;

import com.seb.harvesteffect.exception.CivilizationPrematureException;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Contract for entities subject to the eternal galactic cycle harvest.
 */
public interface Harvestable {

    /**
     * Determines whether the entity has reached apex maturity for ascension.
     *
     * @return true if ripe for harvest, false otherwise
     */
    boolean isHarvestReady();

    /**
     * Initiates the ascension harvest, processing organic genetic material.
     *
     * @return HarvestYield containing biotic biomass and dark energy output
     * @throws CivilizationPrematureException if harvested prior to apex readiness
     */
    HarvestYield harvest() throws CivilizationPrematureException;

    /**
     * Current developmental maturity label (e.g., Primordial, Industrial, Apex Zenith).
     *
     * @return descriptive maturity stage
     */
    String getMaturityStage();
}
