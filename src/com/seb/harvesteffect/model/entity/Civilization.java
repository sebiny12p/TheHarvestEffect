package com.seb.harvesteffect.model.entity;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.exception.CivilizationPrematureException;
import com.seb.harvesteffect.model.contract.Harvestable;
import com.seb.harvesteffect.model.contract.RelayLinked;
import com.seb.harvesteffect.model.item.HarvestYield;

/**
 * Abstract foundation for all organic civilizations across the Milky Way Galaxy.
 * Encapsulates demographic growth, cultural evolution, and ascension logic.
 */
public abstract class Civilization implements Harvestable, RelayLinked {
    public static final int TIER_PRIMORDIAL = 0;
    public static final int TIER_PRE_SPACE = 1;
    public static final int TIER_INDUSTRIAL = 2;
    public static final int TIER_APEX_ZENITH = 3;

    protected final String speciesName;
    protected final String homeworld;
    protected final String racialTrait;
    protected int evolutionaryTier;
    protected int populationBillions;
    protected boolean relayLinked;
    protected int darkEnergyYield;
    protected int epochsAtApexZenith;
    protected boolean aiHeresyActive;
    protected boolean kineticBarrierActive;

    public Civilization(String speciesName, String homeworld, String racialTrait, int initialPopulation) {
        if (speciesName == null || speciesName.trim().isEmpty()) {
            throw new IllegalArgumentException("Species name must not be blank.");
        }
        this.speciesName = speciesName;
        this.homeworld = homeworld;
        this.racialTrait = racialTrait;
        this.evolutionaryTier = TIER_PRIMORDIAL;
        this.populationBillions = Math.max(1, initialPopulation);
        this.relayLinked = false;
        this.darkEnergyYield = 100;
        this.epochsAtApexZenith = 0;
        this.aiHeresyActive = false;
        this.kineticBarrierActive = false;
    }

    protected int colonizationTimer = 0;
    protected boolean hasColonized = false;

    /**
     * Advances the civilization along its evolutionary trajectory using neutral climate.
     */
    public void advanceEpoch(GalacticPhenomenon phenomenon) {
        advanceEpoch(phenomenon, null);
    }

    /**
     * Advances the civilization along its evolutionary trajectory taking planetary climate into account.
     */
    public void advanceEpoch(GalacticPhenomenon phenomenon, ClimateType climate) {
        double multiplier = phenomenon != null ? phenomenon.getGrowthMultiplier() : 1.0;
        if (phenomenon != null && isImmuneToPhenomenon(phenomenon)) {
            multiplier = Math.max(1.0, multiplier);
        }
        if (climate != null) {
            multiplier *= climate.getGrowthMultiplier(this.speciesName);
        }

        if (evolutionaryTier < TIER_APEX_ZENITH) {
            evolutionaryTier++;
            if (isMartialSpecies() && evolutionaryTier >= TIER_INDUSTRIAL) {
                kineticBarrierActive = true;
            }
        } else {
            epochsAtApexZenith++;
            if (epochsAtApexZenith >= 3 && !aiHeresyActive) {
                aiHeresyActive = true;
                populationBillions = Math.max(1, populationBillions / 2);
            }
        }

        // Demographic expansion scaled by technological tier and planetary climate
        double growthFactor = (1.12 + (evolutionaryTier * 0.12)) * multiplier;
        populationBillions = (int) Math.min(20, Math.max(1, populationBillions * growthFactor));
        darkEnergyYield = Math.min(350, (int) (darkEnergyYield * (1.10 + (evolutionaryTier * 0.05))));
    }

    public int getColonizationTimer() {
        return colonizationTimer;
    }

    public void incrementColonizationTimer() {
        this.colonizationTimer++;
    }

    public void resetColonizationTimer() {
        this.colonizationTimer = 0;
    }

    public void setColonizationTimer(int colonizationTimer) {
        this.colonizationTimer = Math.max(0, colonizationTimer);
    }

    public boolean hasColonized() {
        return hasColonized;
    }

    public void setHasColonized(boolean hasColonized) {
        this.hasColonized = hasColonized;
    }

    public void setEvolutionaryTier(int tier) {
        this.evolutionaryTier = Math.max(0, Math.min(TIER_APEX_ZENITH, tier));
    }

    /**
     * Polymorphic calculation of total refined biomass score.
     */
    public abstract int calculateBiomassScore();

    /**
     * Passive Element Zero dividend generated per epoch (e.g. Volus banking).
     */
    public int getPassiveEezoDividend() {
        return 0;
    }

    /**
     * Passive bonus to Mass Relay bandwidth (e.g. Quarian engineering).
     */
    public double getRelayBandwidthBoost() {
        return 0.0;
    }

    /**
     * Checks if species possesses evolutionary immunity to a cosmic event (e.g. Vorcha, Turian).
     */
    public boolean isImmuneToPhenomenon(GalacticPhenomenon phenomenon) {
        return false;
    }

    /**
     * Element Zero (Eezo) manufacturing and probe deployment cost to seed this species.
     */
    public int getSeedingCost() {
        return 50; // Default base seeding cost
    }

    /**
     * Thematic quote or cultural transmission.
     */
    public abstract String getSpecialDialogue();

    public boolean isMartialSpecies() {
        String n = speciesName.toLowerCase();
        return n.contains("turian") || n.contains("krogan") || n.contains("batarian") || n.contains("yahg");
    }

    public boolean hasKineticBarrier() {
        return kineticBarrierActive;
    }

    public void setKineticBarrier(boolean active) {
        this.kineticBarrierActive = active;
    }

    public void disableKineticBarrier() {
        this.kineticBarrierActive = false;
    }

    public boolean isAiHeresyActive() {
        return aiHeresyActive;
    }

    public void suppressAiHeresy() {
        this.aiHeresyActive = false;
        this.epochsAtApexZenith = 0;
    }

    public void setAiHeresyActive(boolean active) {
        this.aiHeresyActive = active;
    }

    public int getEpochsAtApexZenith() {
        return epochsAtApexZenith;
    }

    public void setEpochsAtApexZenith(int epochs) {
        this.epochsAtApexZenith = epochs;
    }

    public void setPopulationBillions(int populationBillions) {
        this.populationBillions = Math.max(1, populationBillions);
    }

    @Override
    public boolean isHarvestReady() {
        return evolutionaryTier >= TIER_APEX_ZENITH;
    }

    @Override
    public HarvestYield harvest() throws CivilizationPrematureException {
        if (!isHarvestReady()) {
            throw new CivilizationPrematureException(speciesName, evolutionaryTier);
        }
        int biomass = calculateBiomassScore();
        int darkEnergy = calculateDarkEnergyYield();
        if (aiHeresyActive) {
            biomass = (int) (biomass * 0.5); // Rogue synthetic civil war damaged biological yields
            darkEnergy = (int) (darkEnergy * 0.6);
        }
        return new HarvestYield(speciesName, biomass, darkEnergy);
    }

    public int calculateDarkEnergyYield() {
        double linkBonus = relayLinked ? 1.5 : 1.0;
        return (int) (darkEnergyYield * linkBonus);
    }

    @Override
    public String getMaturityStage() {
        if (aiHeresyActive) {
            return "AI HERESY REBELLION (Rogue Geth War: Yield Halved!)";
        }
        switch (evolutionaryTier) {
            case TIER_PRIMORDIAL:
                return "Primordial (Tier 0)";
            case TIER_PRE_SPACE:
                return "Pre-Spaceflight (Tier 1)";
            case TIER_INDUSTRIAL:
                return kineticBarrierActive ? "Industrial Expansion [Orbital Shielded] (Tier 2)" : "Industrial Expansion (Tier 2)";
            case TIER_APEX_ZENITH:
                return kineticBarrierActive ? "APEX ZENITH [Orbital Shielded] - HARVEST READY" : "APEX ZENITH - HARVEST READY (Tier 3)";
            default:
                return "Unknown Evolution Stage";
        }
    }

    @Override
    public boolean isLinked() {
        return relayLinked;
    }

    @Override
    public void establishRelayLink() {
        this.relayLinked = true;
    }

    @Override
    public void severRelayLink() {
        this.relayLinked = false;
    }

    @Override
    public double getRelayBandwidth() {
        return (relayLinked ? 1.5 : 1.0) + getRelayBandwidthBoost();
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public String getHomeworld() {
        return homeworld;
    }

    public String getRacialTrait() {
        return racialTrait;
    }

    public int getEvolutionaryTier() {
        return evolutionaryTier;
    }

    public int getPopulationBillions() {
        return populationBillions;
    }

    @Override
    public String toString() {
        return String.format("[%s on %s | Pop: %dB | %s | Relay: %s]",
                speciesName, homeworld, populationBillions, getMaturityStage(), relayLinked ? "LINKED" : "OFFLINE");
    }
}
