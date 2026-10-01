package com.seb.harvesteffect.model.item;

/**
 * Biological and energetic yield extracted from an ascended civilization.
 */
public class HarvestYield extends Resource {
    private final String originSpecies;
    private final int geneticBiomass;
    private final int darkEnergyYield;

    public HarvestYield(String originSpecies, int geneticBiomass, int darkEnergyYield) {
        super(originSpecies + " Biomass Matrix", geneticBiomass, darkEnergyYield);
        this.originSpecies = originSpecies;
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
        return String.format("Refined biotic genetic matrix of %s (%d Biomass, %d Dark Energy).",
                originSpecies, geneticBiomass, darkEnergyYield);
    }
}
