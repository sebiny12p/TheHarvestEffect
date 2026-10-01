package com.seb.harvesteffect.exception;

/**
 * Thrown when an action requires more refined Element Zero (Eezo) than currently stored.
 */
public class InsufficientEezoException extends ReaperException {
    private final int required;
    private final int available;

    public InsufficientEezoException(int required, int available) {
        super(String.format("Deficit Incurred: Operation demands %d Eezo, but only %d is available in fleet reserves.",
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
