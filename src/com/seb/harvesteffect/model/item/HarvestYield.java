package com.seb.harvesteffect.model.item;

/**
 * Biological and energetic yield extracted from an ascended civilization.
 */
public class HarvestYield extends Resource {
    public static final int TRIVIAL_SALVAGE_EEZO = 15;
    private final String originSpecies;
    private final int geneticBiomass;
    private final int darkEnergyYield;

    public HarvestYield(String originSpecies, int geneticBiomass, int darkEnergyYield) {
        super(originSpecies.contains("Biomass Matrix") ? originSpecies : originSpecies + " Biomass Matrix",
                geneticBiomass, TRIVIAL_SALVAGE_EEZO);
        this.originSpecies = originSpecies.replace(" Biomass Matrix", "");
        this.geneticBiomass = geneticBiomass;
        this.darkEnergyYield = darkEnergyYield;
    }

    public String getOriginSpecies() {
        return originSpecies;
    }

    public int getGeneticBiomass() {
        return geneticBiomass;
    }

    public int getDarkEnergyYield() {
        return darkEnergyYield;
    }

    @Override
    public String getDescription() {
        return String.format("Encapsulated genetic specimen of %s (%d Biomass | 15 Eezo salvage scrap | Preserved for Apex Research).",
                originSpecies, geneticBiomass);
    }
}
