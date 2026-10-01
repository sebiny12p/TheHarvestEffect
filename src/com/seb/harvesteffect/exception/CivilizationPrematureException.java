package com.seb.harvesteffect.exception;

/**
 * Thrown when an ascension harvest is commanded on a civilization that has not reached apex maturity.
 */
public class CivilizationPrematureException extends ReaperException {
    private final String speciesName;
    private final int currentTier;

    public CivilizationPrematureException(String speciesName, int currentTier) {
        super(String.format("Premature Harvest Aborted: Species '%s' is at Tier %d. Must reach Tier 3 Apex Zenith.",
                speciesName, currentTier));
        this.speciesName = speciesName;
        this.currentTier = currentTier;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public int getCurrentTier() {
        return currentTier;
    }
}
