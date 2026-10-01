package com.seb.harvesteffect.exception;

/**
 * Thrown when an operation demands more genetic biomass than currently stored in fleet reserves.
 */
public class InsufficientBiomassException extends ReaperException {
    private final int required;
    private final int available;

    public InsufficientBiomassException(int required, int available) {
        super(String.format("Biomass Deficit: Operation demands %d Genetic Biomass, but only %d is stored in fleet vats.",
                required, available));
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public int getCost() {
        return required;
    }

    public int getAvailable() {
        return available;
    }
}
