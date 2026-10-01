package com.seb.harvesteffect.exception;

/**
 * Thrown when an Ascension Harvest is deflected by a planetary kinetic orbital barrier.
 */
public class CivilizationBarrierException extends ReaperException {
    private final String speciesName;

    public CivilizationBarrierException(String speciesName) {
        super(String.format(
                "Ascension Harvest deflected by %s planetary kinetic defense grid! "
                + "Station a Scion Behemoth or Husk Swarm to breach defenses, or research Cyclonic Kinetic Shields.",
                speciesName));
        this.speciesName = speciesName;
    }

    public String getSpeciesName() {
        return speciesName;
    }
}
