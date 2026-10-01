package com.seb.harvesteffect.exception;

/**
 * Base checked exception for the Harvest Effect simulation domain.
 */
public class ReaperException extends Exception {

    public ReaperException(String message) {
        super(message);
    }

    public ReaperException(String message, Throwable cause) {
        super(message, cause);
    }
}
