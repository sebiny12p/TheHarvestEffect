package com.seb.harvesteffect.model.item;

/**
 * Seeding device deployed into dormant star systems to incubate organic species.
 */
public class GenesisProbe extends Resource {
    private final String targetSpecies;

    public GenesisProbe(String targetSpecies) {
        super(targetSpecies + " Genesis Probe", 50, 100);
        this.targetSpecies = targetSpecies;
    }

    public String getTargetSpecies() {
        return targetSpecies;
    }

    @Override
    public String getDescription() {
        return "Planetary seeding apparatus carrying dormant genetic zygotes of " + targetSpecies + ".";
    }
}
