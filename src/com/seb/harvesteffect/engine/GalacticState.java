package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.GalacticSector;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.item.CargoHold;
import com.seb.harvesteffect.model.item.FleetComponent;
import com.seb.harvesteffect.model.item.GenesisProbe;
import com.seb.harvesteffect.model.item.HarvestYield;
import com.seb.harvesteffect.model.item.Resource;

/**
 * State tracking for the Sovereign flagship, Eezo reserves, and galaxy grid.
 */
public class GalacticState {
    private final String flagshipName;
    private final String currentCycleName;
    private int cycleEpoch;
    private int eezoReserves;
    private int accumulatedBiomass;
    private GalacticPhenomenon currentPhenomenon;
    private final GalacticSector galaxyMap;
    private final CargoHold<Resource> cargoHold;
    private GrowthMode growthMode;
    private int realTimeTickSeconds;
    private int flagshipSector;
    private int flagshipCluster;
    private int activeViewSector;

    public GalacticState(String flagshipName, String currentCycleName) {
        this.flagshipName = (flagshipName == null || flagshipName.trim().isEmpty()) ? "Harbinger" : flagshipName;
        this.currentCycleName = (currentCycleName == null || currentCycleName.trim().isEmpty())
                ? "Prologue: The Fall of Earth (ME3 Climax)" : currentCycleName;
        this.cycleEpoch = 1;
        this.eezoReserves = 500;
        this.accumulatedBiomass = 0;
        this.currentPhenomenon = GalacticPhenomenon.STELLAR_CALM;
        this.galaxyMap = new GalacticSector();
        this.cargoHold = new CargoHold<Resource>(12);
        this.growthMode = GrowthMode.TURN_BASED;
        this.realTimeTickSeconds = 6;
        this.flagshipSector = 0;
        this.flagshipCluster = 0;
        this.activeViewSector = 0;
    }

    public int getFlagshipSector() {
        return flagshipSector;
    }

    public int getFlagshipCluster() {
        return flagshipCluster;
    }

    public int getActiveViewSector() {
        return activeViewSector;
    }

    public void setActiveViewSector(int sector) {
        if (sector >= 0 && sector < galaxyMap.getRowCount()) {
            this.activeViewSector = sector;
        }
    }

    private boolean flagshipJumpedThisEpoch = false;

    public boolean isFlagshipJumpedThisEpoch() {
        return flagshipJumpedThisEpoch;
    }

    public void setFlagshipJumpedThisEpoch(boolean jumped) {
        this.flagshipJumpedThisEpoch = jumped;
    }

    public void resetFlagshipJumps() {
        this.flagshipJumpedThisEpoch = false;
    }

    public void moveFlagship(int sector, int cluster) {
        this.flagshipSector = sector;
        this.flagshipCluster = cluster;
    }

    public int moveFlagship(int sector, int cluster, boolean chargeCost) throws InsufficientEezoException {
        StarSystem target = galaxyMap.getSystem(sector, cluster);
        int cost = 0;
        if (chargeCost && !target.isRelayBeamActive()) {
            cost = 25;
            if (this.eezoReserves < cost) {
                throw new InsufficientEezoException(cost, this.eezoReserves);
            }
            this.eezoReserves -= cost;
        }
        this.flagshipSector = sector;
        this.flagshipCluster = cluster;
        return cost;
    }

    public void addEezo(int amount) {
        if (amount > 0) {
            this.eezoReserves += amount;
        }
    }

    public void deductEezo(int amount) throws InsufficientEezoException {
        if (amount < 0) {
            throw new IllegalArgumentException("Deduction amount must be positive.");
        }
        if (this.eezoReserves < amount) {
            throw new InsufficientEezoException(amount, this.eezoReserves);
        }
        this.eezoReserves -= amount;
    }

    public void addBiomass(int amount) {
        if (amount > 0) {
            this.accumulatedBiomass += amount;
        }
    }

    public void deductBiomass(int amount) throws InsufficientBiomassException {
        if (amount < 0) {
            throw new IllegalArgumentException("Deduction amount must be positive.");
        }
        if (this.accumulatedBiomass < amount) {
            throw new InsufficientBiomassException(amount, this.accumulatedBiomass);
        }
        this.accumulatedBiomass -= amount;
    }

    public void setCycleEpoch(int cycleEpoch) {
        this.cycleEpoch = Math.max(1, cycleEpoch);
    }

    public void setEezoReserves(int eezoReserves) {
        this.eezoReserves = Math.max(0, eezoReserves);
    }

    public void setAccumulatedBiomass(int accumulatedBiomass) {
        this.accumulatedBiomass = Math.max(0, accumulatedBiomass);
    }

    public void setCurrentPhenomenon(GalacticPhenomenon currentPhenomenon) {
        if (currentPhenomenon != null) {
            this.currentPhenomenon = currentPhenomenon;
        }
    }

    public int calculatePassiveDividends() {
        int totalDividends = 0;
        for (int r = 0; r < galaxyMap.getRowCount(); r++) {
            for (int c = 0; c < galaxyMap.getColCount(); c++) {
                StarSystem sys = galaxyMap.getSystem(r, c);
                Civilization civ = sys.getCivilization();
                if (civ != null) {
                    int dividend = civ.getPassiveEezoDividend();
                    if (dividend > 0) {
                        totalDividends += dividend;
                    }
                }
            }
        }
        return totalDividends;
    }

    public int collectPassiveDividends() {
        int totalDividends = calculatePassiveDividends();
        if (totalDividends > 0) {
            addEezo(totalDividends);
        }
        return totalDividends;
    }

    public void setCargoCapacity(int capacity) {
        if (cargoHold != null) {
            cargoHold.setCapacity(capacity);
        }
    }

    public int countCargoSpecimens() {
        if (cargoHold == null) return 0;
        int count = 0;
        for (Resource r : cargoHold.getManifest()) {
            if (r instanceof HarvestYield) {
                count++;
            }
        }
        return count;
    }

    public boolean consumeCargoSpecimens(int count) {
        if (cargoHold == null || count <= 0) return true;
        if (countCargoSpecimens() < count) return false;
        int consumed = 0;
        for (int i = cargoHold.getOccupiedCount() - 1; i >= 0 && consumed < count; i--) {
            Resource r = cargoHold.getManifest().get(i);
            if (r instanceof HarvestYield) {
                cargoHold.retrieve(i);
                consumed++;
            }
        }
        return consumed == count;
    }

    public void advanceEpoch(GalacticPhenomenon phenomenon) {
        this.cycleEpoch++;
        this.currentPhenomenon = phenomenon;
        // Notify all star system observers across the galaxy
        for (int r = 0; r < galaxyMap.getRowCount(); r++) {
            for (int c = 0; c < galaxyMap.getColCount(); c++) {
                galaxyMap.getSystem(r, c).onCycleAdvancement(this.cycleEpoch, phenomenon);
            }
        }
    }

    public String getFlagshipName() {
        return flagshipName;
    }

    public String getCurrentCycleName() {
        return currentCycleName;
    }

    public int getCycleEpoch() {
        return cycleEpoch;
    }

    public int getEezoReserves() {
        return eezoReserves;
    }

    public int getAccumulatedBiomass() {
        return accumulatedBiomass;
    }

    public GalacticPhenomenon getCurrentPhenomenon() {
        return currentPhenomenon;
    }

    public GalacticSector getGalaxyMap() {
        return galaxyMap;
    }

    public CargoHold<Resource> getCargoHold() {
        return cargoHold;
    }

    public boolean hasGenesisProbe(String speciesKey) {
        if (cargoHold == null || speciesKey == null) return false;
        String clean = speciesKey.toLowerCase().trim();
        for (Resource r : cargoHold.getManifest()) {
            if (r instanceof GenesisProbe) {
                GenesisProbe gp = (GenesisProbe) r;
                if (gp.getTargetSpecies().equalsIgnoreCase(clean)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getGenesisProbeCount(String speciesKey) {
        if (cargoHold == null || speciesKey == null) return 0;
        String clean = speciesKey.toLowerCase().trim();
        int count = 0;
        for (Resource r : cargoHold.getManifest()) {
            if (r instanceof GenesisProbe) {
                GenesisProbe gp = (GenesisProbe) r;
                if (gp.getTargetSpecies().equalsIgnoreCase(clean)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean hasFleetComponent(String componentKeyword) {
        if (cargoHold == null || componentKeyword == null) return false;
        String clean = componentKeyword.toLowerCase().trim();
        for (Resource r : cargoHold.getManifest()) {
            if (r instanceof FleetComponent) {
                FleetComponent fc = (FleetComponent) r;
                if (fc.getSubsystemName().toLowerCase().contains(clean)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getInstalledFleetArmorIntegrity() {
        int totalArmor = 100; // Base Sovereign capital armor
        if (cargoHold == null) return totalArmor;
        for (Resource r : cargoHold.getManifest()) {
            if (r instanceof FleetComponent) {
                totalArmor += ((FleetComponent) r).getArmorBuff();
            }
        }
        return totalArmor;
    }

    public CycleEra getCurrentEra() {
        return CycleEra.fromEpoch(cycleEpoch);
    }

    public GrowthMode getGrowthMode() {
        return growthMode;
    }

    public void setGrowthMode(GrowthMode growthMode) {
        this.growthMode = (growthMode != null) ? growthMode : GrowthMode.TURN_BASED;
    }

    public int getRealTimeTickSeconds() {
        return realTimeTickSeconds;
    }

    public void setRealTimeTickSeconds(int realTimeTickSeconds) {
        this.realTimeTickSeconds = Math.max(1, realTimeTickSeconds);
    }

    public String getCosmicSeason() {
        int seasonIndex = Math.floorMod(cycleEpoch - 1, 4);
        switch (seasonIndex) {
            case 0:
                return "Cosmic Spring (Planting & Irrigation)";
            case 1:
                return "Cosmic Summer (Solar Maturation)";
            case 2:
                return "Cosmic Autumn (The Great Reaping)";
            case 3:
            default:
                return "Cosmic Winter (Threat Convergence)";
        }
    }

    private CitadelNexus citadelNexus;

    public CitadelNexus getCitadelNexus() {
        return citadelNexus;
    }

    public void setCitadelNexus(CitadelNexus citadelNexus) {
        this.citadelNexus = citadelNexus;
    }
}
