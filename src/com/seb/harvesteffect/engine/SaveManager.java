package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.item.FleetComponent;
import com.seb.harvesteffect.model.item.GenesisProbe;
import com.seb.harvesteffect.model.item.HarvestYield;
import com.seb.harvesteffect.model.item.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Robust Save/Load persistence manager using Java Serialization.
 * Allows saving game state, auto-saving across epoch transitions, and restoring campaigns.
 */
public class SaveManager {
    public static final String DEFAULT_SAVE_FILE = "the_harvest_effect_save.dat";

    /**
     * Serializable DTO representing full game progression.
     */
    public static class SaveState implements Serializable {
        private static final long serialVersionUID = 1L;

        public String flagshipName;
        public String currentCycleName;
        public int cycleEpoch;
        public int eezoReserves;
        public int accumulatedBiomass;
        public String currentPhenomenon;
        public String growthMode;
        public int realTimeTickSeconds;

        public static class SystemDTO implements Serializable {
            private static final long serialVersionUID = 1L;
            public String name;
            public int sector;
            public int cluster;
            public boolean relayDeployed;
            public boolean relayActive;
            public String civSpecies;
            public int civTier;
            public int civPopulation;
            public boolean kineticBarrier;
            public boolean aiHeresy;
            public int epochsAtApex;
            public String unitType;
            public int unitEnergy;
            public String climate;
            public int colonizationTimer;
            public boolean hasColonized;
        }

        public SystemDTO[][] grid = new SystemDTO[4][6];
        public List<String> unlockedTech = new ArrayList<String>();
        public List<String> researchedGenomes = new ArrayList<String>();

        public String currentAct;
        public int tutorialStep;
        public int crucibleProgress;
        public int totalAscensions;
        public boolean campaignVictory;
        public boolean crucibleDefeat;
        public int activeMissionIndex;
        public int activeTaskIndex;
        public int historicalCycleCount;
        public boolean enforceGenomeResearch;

        // Mega-Structure, Fleet, and Secret Sub-Space Signal Progression
        public int citadelTier = 1;
        public boolean armsLockdownActive = false;
        public int flagshipSector = 0;
        public int flagshipCluster = 0;
        public boolean[] sectorsUnlocked = new boolean[] { true, false, false, false };
        public List<String> discoveredSignals = new ArrayList<String>();
        public List<String> decodedSignals = new ArrayList<String>();
        public List<String> resolvedDilemmas = new ArrayList<String>();

        public static class ItemDTO implements Serializable {
            private static final long serialVersionUID = 1L;
            public String identifier;
            public int mass;
            public int eezo;
            public String category; // PROBE, YIELD, COMPONENT
        }
        public List<ItemDTO> cargoItems = new ArrayList<ItemDTO>();
    }

    public static final String SAVE_SLOT_PREFIX = "the_harvest_effect_slot_";
    public static final int MAX_SLOTS = 5;

    /**
     * Lightweight metadata summary of a save slot for UI selection.
     */
    public static class SaveMetadata {
        private final int slotNumber; // 0 = Auto-Save, 1..5 = Manual Slots
        private final boolean exists;
        private final String flagshipName;
        private final String currentAct;
        private final int epoch;
        private final int eezo;
        private final int biomass;
        private final long timestamp;

        public SaveMetadata(int slotNumber, boolean exists, String flagshipName, String currentAct,
                            int epoch, int eezo, int biomass, long timestamp) {
            this.slotNumber = slotNumber;
            this.exists = exists;
            this.flagshipName = flagshipName;
            this.currentAct = currentAct;
            this.epoch = epoch;
            this.eezo = eezo;
            this.biomass = biomass;
            this.timestamp = timestamp;
        }

        public int getSlotNumber() { return slotNumber; }
        public boolean exists() { return exists; }
        public String getFlagshipName() { return flagshipName; }
        public String getCurrentAct() { return currentAct; }
        public int getEpoch() { return epoch; }
        public int getEezo() { return eezo; }
        public int getBiomass() { return biomass; }
        public long getTimestamp() { return timestamp; }

        public String getDisplayLabel() {
            String slotName = (slotNumber == 0) ? "AUTO-SAVE" : ("SLOT " + slotNumber);
            if (!exists) {
                return String.format("[%s] — Empty Save Slot", slotName);
            }
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateStr = sdf.format(new java.util.Date(timestamp));
            return String.format("[%s] %s | %s | Epoch %d | %d Eezo, %d Bio (%s)",
                    slotName, flagshipName, currentAct, epoch, eezo, biomass, dateStr);
        }
    }

    public static File getSlotFile(int slot) {
        if (slot <= 0) return new File(DEFAULT_SAVE_FILE);
        return new File(SAVE_SLOT_PREFIX + slot + ".dat");
    }

    public static boolean hasSaveFile() {
        if (new File(DEFAULT_SAVE_FILE).exists()) return true;
        for (int i = 1; i <= MAX_SLOTS; i++) {
            if (getSlotFile(i).exists()) return true;
        }
        return false;
    }

    public static File getMostRecentSaveFile() {
        File bestFile = null;
        long bestTime = -1;

        File def = new File(DEFAULT_SAVE_FILE);
        if (def.exists()) {
            bestFile = def;
            bestTime = def.lastModified();
        }

        for (int i = 1; i <= MAX_SLOTS; i++) {
            File f = getSlotFile(i);
            if (f.exists() && f.lastModified() > bestTime) {
                bestFile = f;
                bestTime = f.lastModified();
            }
        }
        return bestFile;
    }

    public static SaveMetadata getSlotMetadata(int slot) {
        File f = getSlotFile(slot);
        if (!f.exists()) {
            return new SaveMetadata(slot, false, "None", "None", 0, 0, 0, 0);
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            try {
                SaveState s = (SaveState) ois.readObject();
                return new SaveMetadata(slot, true, s.flagshipName, s.currentAct,
                        s.cycleEpoch, s.eezoReserves, s.accumulatedBiomass, f.lastModified());
            } finally {
                ois.close();
            }
        } catch (Exception e) {
            return new SaveMetadata(slot, true, "Corrupted/Unreadable", "Unknown", 0, 0, 0, f.lastModified());
        }
    }

    public static List<SaveMetadata> getAllSlotMetadata() {
        List<SaveMetadata> list = new ArrayList<SaveMetadata>();
        list.add(getSlotMetadata(0)); // Slot 0: Auto-Save
        for (int i = 1; i <= MAX_SLOTS; i++) {
            list.add(getSlotMetadata(i));
        }
        return list;
    }

    public static void saveSlot(ReaperEngine engine, int slot) throws IOException {
        saveGame(engine, getSlotFile(slot));
    }

    public static boolean loadSlot(ReaperEngine engine, int slot) throws IOException, ClassNotFoundException {
        return loadGame(engine, getSlotFile(slot));
    }

    public static void saveGame(ReaperEngine engine, File file) throws IOException {
        SaveState state = new SaveState();
        GalacticState gs = engine.getState();

        state.flagshipName = gs.getFlagshipName();
        state.currentCycleName = gs.getCurrentCycleName();
        state.cycleEpoch = gs.getCycleEpoch();
        state.eezoReserves = gs.getEezoReserves();
        state.accumulatedBiomass = gs.getAccumulatedBiomass();
        state.currentPhenomenon = gs.getCurrentPhenomenon().name();
        state.growthMode = gs.getGrowthMode().name();
        state.realTimeTickSeconds = gs.getRealTimeTickSeconds();

        // 1. Grid Systems
        int rowCount = gs.getGalaxyMap().getRowCount();
        int colCount = gs.getGalaxyMap().getColCount();
        state.grid = new SaveState.SystemDTO[rowCount][colCount];

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                StarSystem sys = gs.getGalaxyMap().getSystem(r, c);
                SaveState.SystemDTO dto = new SaveState.SystemDTO();
                dto.name = sys.getSystemName();
                dto.sector = sys.getSector();
                dto.cluster = sys.getCluster();
                dto.relayDeployed = sys.isRelayBeaconDeployed();
                dto.relayActive = sys.isRelayBeamActive();
                dto.climate = sys.getClimateType().name();

                Civilization civ = sys.getCivilization();
                if (civ != null) {
                    dto.civSpecies = civ.getSpeciesName();
                    dto.civTier = civ.getEvolutionaryTier();
                    dto.civPopulation = civ.getPopulationBillions();
                    dto.kineticBarrier = civ.hasKineticBarrier();
                    dto.aiHeresy = civ.isAiHeresyActive();
                    dto.epochsAtApex = civ.getEpochsAtApexZenith();
                    dto.colonizationTimer = civ.getColonizationTimer();
                    dto.hasColonized = civ.hasColonized();
                }

                BiomechanicalUnit u = sys.getBiomechanicalUnit();
                if (u != null) {
                    dto.unitType = u.getClass().getSimpleName();
                    dto.unitEnergy = u.getEnergyLevel();
                }

                state.grid[r][c] = dto;
            }
        }

        // 2. Tech Tree
        for (TechTree.Upgrade u : engine.getTechTree().getAllUpgrades()) {
            if (u.isUnlocked()) {
                state.unlockedTech.add(u.getId());
            }
        }

        // 2b. Sequenced Genomes
        state.researchedGenomes.addAll(engine.getSequencedGenomes());

        // 3. Campaign Data
        CampaignManager cm = engine.getCampaign();
        state.currentAct = cm.getCurrentAct().name();
        state.tutorialStep = cm.getTutorialStep();
        state.crucibleProgress = cm.getCrucibleProgress();
        state.totalAscensions = cm.getTotalAscensions();
        state.campaignVictory = cm.isCampaignVictory();
        state.crucibleDefeat = cm.isCrucibleDefeat();
        state.activeMissionIndex = engine.getMissionManager().getActiveMissionIndex();
        state.activeTaskIndex = engine.getMissionManager().getActiveMission().getCurrentTaskIndex();
        state.historicalCycleCount = engine.getMissionManager().getHistoricalCycleCount();
        state.enforceGenomeResearch = engine.isEnforceGenomeResearch();

        // 4. Cargo
        for (Resource r : gs.getCargoHold().getManifest()) {
            SaveState.ItemDTO itemDto = new SaveState.ItemDTO();
            itemDto.identifier = r.getIdentifier();
            itemDto.mass = r.getMassUnits();
            itemDto.eezo = r.getEezoValue();
            if (r instanceof GenesisProbe) {
                itemDto.category = "PROBE";
            } else if (r instanceof HarvestYield) {
                itemDto.category = "YIELD";
            } else {
                itemDto.category = "COMPONENT";
            }
            state.cargoItems.add(itemDto);
        }

        // 5. Citadel Nexus Mega-Structure
        state.citadelTier = engine.getNexus().getCurrentTier();
        state.armsLockdownActive = engine.getNexus().isArmsLockdownActive();

        // 6. Sovereign Flagship & Sectors
        state.flagshipSector = gs.getFlagshipSector();
        state.flagshipCluster = gs.getFlagshipCluster();
        state.sectorsUnlocked = new boolean[rowCount];
        for (int i = 0; i < rowCount; i++) {
            state.sectorsUnlocked[i] = gs.getGalaxyMap().isSectorUnlocked(i);
        }

        // 7. Sub-Space Scanner Signals
        if (engine.getScanner() != null) {
            for (SubSpaceScanner.SignalTransmission sig : engine.getScanner().getAllDiscoveredSignals()) {
                state.discoveredSignals.add(sig.getId());
            }
            for (SubSpaceScanner.SignalTransmission sig : engine.getScanner().getDecodedArchive()) {
                state.decodedSignals.add(sig.getId());
            }
        }

        // 8. Resolved Story Dilemmas
        if (engine.getDilemmaManager() != null) {
            state.resolvedDilemmas.addAll(engine.getDilemmaManager().getResolvedDilemmas());
        }

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        try {
            oos.writeObject(state);
            oos.flush();
        } finally {
            oos.close();
        }
    }

    public static void autoSave(ReaperEngine engine) {
        try {
            saveGame(engine, new File(DEFAULT_SAVE_FILE));
        } catch (Exception ignored) {}
    }

    public static boolean loadGame(ReaperEngine engine, File file) throws IOException, ClassNotFoundException {
        if (!file.exists()) return false;

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        SaveState s;
        try {
            s = (SaveState) ois.readObject();
        } finally {
            ois.close();
        }

        GalacticState gs = engine.getState();

        // Restore core state metrics directly
        gs.setCycleEpoch(s.cycleEpoch);
        gs.setEezoReserves(s.eezoReserves);
        gs.setAccumulatedBiomass(s.accumulatedBiomass);
        if (s.currentPhenomenon != null) {
            try {
                gs.setCurrentPhenomenon(GalacticPhenomenon.valueOf(s.currentPhenomenon));
            } catch (Exception ignored) {}
        }

        if (s.growthMode != null) {
            try {
                gs.setGrowthMode(GrowthMode.valueOf(s.growthMode));
            } catch (Exception ignored) {}
        }
        if (s.realTimeTickSeconds > 0) {
            gs.setRealTimeTickSeconds(s.realTimeTickSeconds);
        }

        // Reconstruct Systems
        int targetRows = gs.getGalaxyMap().getRowCount();
        int targetCols = gs.getGalaxyMap().getColCount();
        for (int r = 0; r < s.grid.length && r < targetRows; r++) {
            for (int c = 0; c < s.grid[r].length && c < targetCols; c++) {
                StarSystem sys = gs.getGalaxyMap().getSystem(r, c);
                SaveState.SystemDTO dto = s.grid[r][c];
                if (dto == null) continue;

                sys.purgeSystem();
                sys.removeUnit();

                if (dto.relayDeployed) sys.deployRelayBeacon();
                sys.setRelayBeamActive(dto.relayActive);

                if (dto.climate != null) {
                    try {
                        sys.setClimateType(com.seb.harvesteffect.model.entity.ClimateType.valueOf(dto.climate));
                    } catch (Exception ignored) {}
                }

                if (dto.civSpecies != null) {
                    try {
                        Civilization civ = engine.createCivilization(dto.civSpecies);
                        while (civ.getEvolutionaryTier() < dto.civTier) {
                            civ.advanceEpoch(GalacticPhenomenon.STELLAR_CALM, sys.getClimateType());
                        }
                        if (dto.civPopulation > 0) civ.setPopulationBillions(dto.civPopulation);
                        civ.setKineticBarrier(dto.kineticBarrier);
                        civ.setAiHeresyActive(dto.aiHeresy);
                        civ.setEpochsAtApexZenith(dto.epochsAtApex);
                        civ.setColonizationTimer(dto.colonizationTimer);
                        civ.setHasColonized(dto.hasColonized);
                        sys.deployCivilization(civ);
                    } catch (SystemOccupiedException ignored) {}
                }

                if (dto.unitType != null) {
                    try {
                        engine.deployUnit(r, c, dto.unitType, false);
                        if (sys.getBiomechanicalUnit() != null) {
                            sys.getBiomechanicalUnit().recharge(dto.unitEnergy);
                        }
                    } catch (ReaperException ignored) {}
                }
            }
        }

        // Restore Tech
        for (String techId : s.unlockedTech) {
            TechTree.Upgrade u = engine.getTechTree().getUpgrade(techId);
            if (u != null) u.setUnlocked(true);
        }

        // Restore Sequenced Genomes
        if (s.researchedGenomes != null) {
            for (String g : s.researchedGenomes) {
                engine.addSequencedGenome(g);
            }
        }
        engine.setEnforceGenomeResearch(s.enforceGenomeResearch);

        // Restore Missions
        int cycleCount = (s.historicalCycleCount > 0) ? s.historicalCycleCount : 1;
        engine.getMissionManager().restoreMissionState(s.activeMissionIndex, s.activeTaskIndex, cycleCount);

        if (s.unlockedTech != null && s.unlockedTech.contains("biomass_vats")) {
            gs.setCargoCapacity(24);
        }

        // Restore Campaign Progression & Story State
        if (s.currentAct != null) {
            try {
                CampaignManager.Act act = CampaignManager.Act.valueOf(s.currentAct);
                engine.getCampaign().restoreState(act, s.tutorialStep, s.crucibleProgress,
                        s.totalAscensions, s.campaignVictory, s.crucibleDefeat);
            } catch (Exception ignored) {}
        }

        // Restore Cargo
        while (gs.getCargoHold().getOccupiedCount() > 0) {
            gs.getCargoHold().retrieve(0);
        }
        for (SaveState.ItemDTO item : s.cargoItems) {
            try {
                if ("PROBE".equals(item.category)) {
                    gs.getCargoHold().store(new GenesisProbe(item.identifier.replace(" Genesis Probe", "")));
                } else if ("YIELD".equals(item.category)) {
                    gs.getCargoHold().store(new HarvestYield(item.identifier, item.mass, item.eezo));
                } else {
                    gs.getCargoHold().store(new FleetComponent(item.identifier, item.mass / 10, item.eezo));
                }
            } catch (CargoHoldFullException ignored) {}
        }

        // Restore Citadel Nexus Mega-Structure (without re-deducting Eezo/Biomass or re-triggering damage)
        engine.getNexus().setTier(Math.max(1, s.citadelTier));
        engine.getNexus().setArmsLockdownTriggered(s.armsLockdownActive);

        // Restore Sovereign Flagship & Sectors
        gs.moveFlagship(s.flagshipSector, s.flagshipCluster);
        if (s.sectorsUnlocked != null) {
            for (int i = 0; i < s.sectorsUnlocked.length && i < targetRows; i++) {
                gs.getGalaxyMap().setSectorUnlocked(i, s.sectorsUnlocked[i]);
            }
        }

        // Restore Sub-Space Scanner Signals
        if (engine.getScanner() != null) {
            if (s.discoveredSignals != null) {
                for (String sigId : s.discoveredSignals) {
                    SubSpaceScanner.SignalTransmission sig = engine.getScanner().getSignal(sigId);
                    if (sig != null) sig.setDiscovered(true);
                }
            }
            if (s.decodedSignals != null) {
                for (String sigId : s.decodedSignals) {
                    SubSpaceScanner.SignalTransmission sig = engine.getScanner().getSignal(sigId);
                    if (sig != null) {
                        sig.setDiscovered(true);
                        sig.setDecoded(true);
                    }
                }
            }
        }

        // Restore Resolved Story Dilemmas
        if (engine.getDilemmaManager() != null && s.resolvedDilemmas != null) {
            engine.getDilemmaManager().restoreResolvedDilemmas(s.resolvedDilemmas);
        }

        return true;
    }
}
