package com.seb.harvesteffect.exception;

/**
 * Thrown when attempting to seed or deploy into an already occupied star system.
 */
public class SystemOccupiedException extends ReaperException {
    public SystemOccupiedException(String systemName, String occupant) {
        super(String.format("Deployment Collision: Star system '%s' is already occupied by '%s'.",
                systemName, occupant));
    }
}
