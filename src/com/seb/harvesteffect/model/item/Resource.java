package com.seb.harvesteffect.model.item;

import com.seb.harvesteffect.model.contract.Tradable;

/**
 * Abstract root for all physical resources, items, and biological matrices.
 */
public abstract class Resource implements Tradable {
    protected final String identifier;
    protected final int massUnits;
    protected final int eezoBaseValue;

    public Resource(String identifier, int massUnits, int eezoBaseValue) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource identifier cannot be null or empty.");
        }
        if (massUnits < 0 || eezoBaseValue < 0) {
            throw new IllegalArgumentException("Mass and Eezo value must be non-negative.");
        }
        this.identifier = identifier;
        this.massUnits = massUnits;
        this.eezoBaseValue = eezoBaseValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getMassUnits() {
        return massUnits;
    }

    @Override
    public int getEezoValue() {
        return eezoBaseValue;
    }

    @Override
    public String getItemName() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("[%s: %d Mass | %d Eezo]", identifier, massUnits, eezoBaseValue);
    }
}
