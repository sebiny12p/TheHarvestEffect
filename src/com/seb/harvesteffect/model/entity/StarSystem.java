package com.seb.harvesteffect.model.entity;

import com.seb.harvesteffect.engine.GalacticPhenomenon;
import com.seb.harvesteffect.exception.SystemOccupiedException;
import com.seb.harvesteffect.model.civilization.Hanar;
import com.seb.harvesteffect.model.contract.CycleObserver;

/**
 * Star system coordinate representing a celestial sector in the Milky Way.
 * Acts as an Observer listening to cyclic galactic phenomena.
 */
public class StarSystem implements CycleObserver {
    private final String systemName;
    private final int sector;
    private final int cluster;
    private boolean relayBeaconDeployed;
    private boolean relayBeamActive;
    private Civilization civilization;
    private BiomechanicalUnit biomechanicalUnit;
    private ClimateType climateType;

    public StarSystem(String systemName, int sector, int cluster) {
        this(systemName, sector, cluster, ClimateType.GARDEN);
    }

    public StarSystem(String systemName, int sector, int cluster, ClimateType climateType) {
        this.systemName = systemName;
        this.sector = sector;
        this.cluster = cluster;
        this.relayBeaconDeployed = false;
        this.relayBeamActive = false;
        this.climateType = (climateType != null) ? climateType : ClimateType.GARDEN;
    }

    public ClimateType getClimateType() {
        return climateType != null ? climateType : ClimateType.GARDEN;
    }

    public void setClimateType(ClimateType climateType) {
        this.climateType = (climateType != null) ? climateType : ClimateType.GARDEN;
    }

    public void terraform(ClimateType targetClimate) {
        if (targetClimate == null) {
            throw new IllegalArgumentException("Target climate cannot be null.");
        }
        this.climateType = targetClimate;
    }

    public void deployCivilization(Civilization civ) throws SystemOccupiedException {
        if (this.civilization != null) {
            throw new SystemOccupiedException(systemName, "Civilization: " + civilization.getSpeciesName());
        }
        this.civilization = civ;
        if (relayBeamActive) {
            civ.establishRelayLink();
        }
    }

    public void deployUnit(BiomechanicalUnit unit) throws SystemOccupiedException {
        if (this.biomechanicalUnit != null) {
            throw new SystemOccupiedException(systemName, "Swarm Construct: " + biomechanicalUnit.getDesignation());
        }
        this.biomechanicalUnit = unit;
    }

    public void deployRelayBeacon() {
        this.relayBeaconDeployed = true;
        this.relayBeamActive = true;
        if (civilization != null) {
            civilization.establishRelayLink();
        }
    }

    public void purgeSystem() {
        this.civilization = null;
    }

    public void removeUnit() {
        this.biomechanicalUnit = null;
    }

    @Override
    public void onCycleAdvancement(int newCycle, GalacticPhenomenon phenomenon) {
        if (phenomenon == GalacticPhenomenon.MASS_RELAY_OVERCHARGE) {
            this.relayBeaconDeployed = true;
            this.relayBeamActive = true;
        }

        if (civilization != null) {
            // Hanar spiritual attunement automatically establishes relay tether
            if (civilization instanceof Hanar) {
                this.relayBeaconDeployed = true;
                this.relayBeamActive = true;
            }
            if (relayBeamActive) {
                civilization.establishRelayLink();
            }
            civilization.advanceEpoch(phenomenon, climateType);
        }

        if (biomechanicalUnit != null) {
            biomechanicalUnit.onCycleAdvancement(newCycle, phenomenon);
        }
    }

    public String getSystemName() {
        return systemName;
    }

    public int getSector() {
        return sector;
    }

    public int getCluster() {
        return cluster;
    }

    public boolean isRelayBeaconDeployed() {
        return relayBeaconDeployed;
    }

    public boolean isRelayBeamActive() {
        return relayBeamActive;
    }

    public void setRelayBeamActive(boolean relayBeamActive) {
        this.relayBeamActive = relayBeamActive;
        if (civilization != null) {
            if (relayBeamActive) {
                civilization.establishRelayLink();
            } else {
                civilization.severRelayLink();
            }
        }
    }

    public Civilization getCivilization() {
        return civilization;
    }

    public BiomechanicalUnit getBiomechanicalUnit() {
        return biomechanicalUnit;
    }

    @Override
    public String toString() {
        String civStr = (civilization != null) ? civilization.getSpeciesName() : "Lifeless";
        String unitStr = (biomechanicalUnit != null) ? biomechanicalUnit.getDesignation() : "Unmonitored";
        String climateStr = (climateType != null) ? climateType.getDisplayName() : "Unknown";
        return String.format("[%s (Sec %d, Clust %d) | Climate: %s | Civ: %s | Unit: %s | Relay: %s]",
                systemName, sector, cluster, climateStr, civStr, unitStr, relayBeamActive ? "ACTIVE" : "OFFLINE");
    }
}
