package com.seb.harvesteffect.exception;

/**
 * Thrown when attempting to store resources or components in a saturated CargoHold.
 */
public class CargoHoldFullException extends ReaperException {
    public CargoHoldFullException(int capacity) {
        super(String.format("Storage Saturated: Cargo hold has reached its maximum capacity limit of %d pods.", capacity));
    }
}
