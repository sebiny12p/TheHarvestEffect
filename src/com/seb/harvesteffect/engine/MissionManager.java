package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.StarSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the multi-cycle historical campaign across eons of galactic history.
 * Provides clear instructional missions teaching planting, cultivation, and harvesting.
 */
public class MissionManager {

    public static class Mission {
        private final int missionNumber;
        private final String cycleEraTitle;
        private final String missionTitle;
        private final String tacticalBriefing;
        private final String[] taskDescriptions;
        private int currentTaskIndex;
        private boolean completed;

        public Mission(int missionNumber, String cycleEraTitle, String missionTitle,
                       String tacticalBriefing, String[] taskDescriptions) {
            this.missionNumber = missionNumber;
            this.cycleEraTitle = cycleEraTitle;
            this.missionTitle = missionTitle;
            this.tacticalBriefing = tacticalBriefing;
            this.taskDescriptions = taskDescriptions;
            this.currentTaskIndex = 0;
            this.completed = false;
        }

        public int getMissionNumber() { return missionNumber; }
        public String getCycleEraTitle() { return cycleEraTitle; }
        public String getMissionTitle() { return missionTitle; }
        public String getTacticalBriefing() { return tacticalBriefing; }
        public String[] getTaskDescriptions() { return taskDescriptions; }
        public int getCurrentTaskIndex() { return currentTaskIndex; }
        public boolean isCompleted() { return completed; }

        public void advanceTask() {
            if (currentTaskIndex < taskDescriptions.length - 1) {
                currentTaskIndex++;
            } else {
                completed = true;
            }
        }

        public void setCurrentTaskIndex(int currentTaskIndex) {
            this.currentTaskIndex = Math.max(0, Math.min(taskDescriptions.length - 1, currentTaskIndex));
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getActiveTaskText() {
            if (completed) {
                return "[MISSION COMPLETED] Directive successfully executed. Ready for next cycle.";
            }
            return String.format("[TASK %d/%d] %s",
                    currentTaskIndex + 1, taskDescriptions.length, taskDescriptions[currentTaskIndex]);
        }
    }

    private final Mission[] historicalMissions;
    private int activeMissionIndex;
    private int historicalCycleCount; // Each 10 epochs or successful purge advances historical cycles

    public MissionManager() {
        this.historicalCycleCount = 1;
        this.activeMissionIndex = 0;
        this.historicalMissions = new Mission[] {
            // Mission 1: Prologue (2186 CE / ME3 Climax) - The Crucible Refusal (Planting Tutorial)
            new Mission(
                1,
                "PROLOGUE: THE FALL OF EARTH (2186 CE / ME3 CLIMAX)",
                "Mission 1: The Crucible Refusal (Planting Civilizations)",
                "=== HARBINGER OVERSEER ARCHIVE // 2186 CE CLIMAX ===\n"
                + "\"Commander Shepard refused the Catalyst's choices. The Crucible is crushed, but the "
                + "dark energy backlash burned out the galactic mass relays and ruined the Citadel. Our reserves "
                + "are critically depleted.\n\n"
                + "Never again will we wait 50,000 years in dark space for wild evolution to threaten us. We enact "
                + "DIRECTIVE: THE HARVEST EFFECT. We will cultivate civilizations as controlled crops in planetary "
                + "nurseries from our genetic bio-banks, reaping them at peak maturity.\n\n"
                + "Target lifeless star system Sol [0,0] and deploy a Genesis Probe to seed Humanity and begin the nursery directive.\"",
                new String[] {
                    "Click on star system Sol [0,0] on the galaxy radar map to select it.",
                    "Select 'Humanity' in the species dropdown and click '🌱 Plant Humanity'.",
                    "Observe the system: the civilization is now planted at Tier 0 (Primordial)."
                }
            ),

            // Mission 2: Act I - Cultivation & The Millennial Watch (Post-ME3 Recovery)
            new Mission(
                2,
                "ACT I: THE RUINED CITADEL & SOL SILO",
                "Mission 2: Cultivation & Evolutionary Stages (The Watch)",
                "=== SOVEREIGN ARCHIVE ENTRY // POST-ME3 RECOVERY ===\n"
                + "\"A newly seeded civilization is fragile. They must grow through evolutionary epochs: "
                + "Primordial (Tier 0) -> Pre-Space (Tier 1) -> Industrial (Tier 2) -> Apex Zenith (Tier 3).\n\n"
                + "WARNING: Attempting to harvest before Tier 3 will fail and trigger a CivilizationPrematureException. "
                + "Advance the cosmic clock forward until they achieve Apex Zenith maturity.\"",
                new String[] {
                    "Click 'Advance Epoch (+5,000 Y)' once to progress time to Tier 1 (Pre-Space).",
                    "Click 'Advance Epoch' again to observe Industrial expansion (Tier 2).",
                    "Click 'Advance Epoch' a third time until Sol reaches Tier 3 Apex Zenith [APEX HARVEST READY]."
                }
            ),

            // Mission 3: Act I - The Ascension Harvest Protocol
            new Mission(
                3,
                "ACT I: THE RUINED CITADEL & SOL SILO",
                "Mission 3: The Mass Relay & The Ascension Harvest",
                "=== HARBINGER CONDUIT LAW // ASCENSION PROTOCOL ===\n"
                + "\"When organics reach their technological zenith, their chaos threatens the stars. "
                + "We construct Mass Relays to create an FTL corridor for rapid harvesting.\n\n"
                + "Deploy a Mass Relay Beacon into Sol, then execute the Ascension Harvest to extract their biomass "
                + "and replenish our depleted dark energy reserves.\"",
                new String[] {
                    "Select Sol [0,0] and click 'Deploy Relay Beacon' to establish an active FTL link.",
                    "With Sol [0,0] at Apex Zenith, click 'Ascension Harvest'!",
                    "Verify genetic biomass and Dark Energy have been deposited into your flagship cargo pods."
                }
            ),

            // Mission 4: Act II - Primary Relay Alpha & Martial Cultivation
            new Mission(
                4,
                "ACT II: THE ATTICAN TRAVERSE & MARTIAL GENOMES",
                "Mission 4: Primary Relay Alpha & Attican Expansion",
                "=== HARBINGER EXPANSION PROTOCOL // SECTOR 1 ===\n"
                + "\"Construct Primary Relay Alpha in the Armada Tech Tree to reconnect Sector 1 (Attican Traverse & Krogan DMZ).\n\n"
                + "Synthesize dense martial species (Turians, Krogan, Batarians) from our bio-banks. "
                + "Station Scion Behemoths or your Sovereign Flagship to breach orbital kinetic barriers and gather telemetry.\"",
                new String[] {
                    "Research Primary Relay Alpha, then seed Thessian Veil [0,1] and Palaven [0,2].",
                    "Deploy a 'Collector Drone' or 'Husk Swarm' into an empty system and recharge its power core.",
                    "Advance epochs and harvest both civilizations upon reaching Apex Zenith."
                }
            ),

            // Mission 5: Act III - Primary Relay Omega & Heresy Suppression
            new Mission(
                5,
                "ACT III: THE PERSEUS VEIL & SYNTHETIC HERESY",
                "Mission 5: Primary Relay Omega & Heresy Suppression",
                "=== SURVEILLANCE FEED // SYNTHETIC REBELLION ===\n"
                + "\"Construct Primary Relay Omega in the Tech Tree to reconnect Sector 2 (Terminus Systems & Perseus Veil).\n\n"
                + "Synthesize Quarian, Volus, and Hanar genomes. Warning: as spacefaring civilizations advance, rogue AI heresy "
                + "spreads across active relay corridors. Research Sub-Space Indoctrination Emitter or deploy Husk Swarms to purge synthetic contagion.\"",
                new String[] {
                    "Research 'Primary Relay Omega Construction' in Tech Tree to unlock Sector 2.",
                    "Open the Armada Tech Tree and research 'Sub-Space Indoctrination Emitter'.",
                    "Cultivate and harvest civilizations across Sector 2 to claim refined dark energy."
                }
            ),

            // Mission 6: Act IV - Primary Relay Gamma & The Crucible Remnant
            new Mission(
                6,
                "ACT IV: THE SHADOW RIM & PRECURSOR CLONES",
                "Mission 6: The Apex Tapestry & Crucible Remnants",
                "=== THE ETERNAL SILO // PRECURSOR SYNTHESIS ===\n"
                + "\"Surviving allied fleets in the Shadow Rim are assembling Crucible remnants. Construct Primary Relay Gamma "
                + "to unlock Sector 3.\n\n"
                + "Synthesize all remaining genomes (Prothean, Rachni, Elcor, Vorcha, Yahg) across our 24-world matrix. "
                + "Cultivate multiple systems to Apex Zenith simultaneously to forge the ultimate Reaper Dreadnought before the Crucible can fire!\"",
                new String[] {
                    "Research 'Primary Relay Gamma Construction' to unlock Sector 3 (Shadow Rim).",
                    "Deploy Mass Relay Beacons across multiple active systems in Sector 3.",
                    "Conduct simultaneous Ascension Harvests to crush the last remnants of resistance!"
                }
            )
        };
    }

    public Mission getActiveMission() {
        if (activeMissionIndex >= historicalMissions.length) {
            return historicalMissions[historicalMissions.length - 1];
        }
        return historicalMissions[activeMissionIndex];
    }

    public int getActiveMissionIndex() {
        return activeMissionIndex;
    }

    public int getTotalMissions() {
        return historicalMissions.length;
    }

    public int getHistoricalCycleCount() {
        return historicalCycleCount;
    }

    public void advanceMission() {
        if (activeMissionIndex < historicalMissions.length - 1) {
            activeMissionIndex++;
            historicalCycleCount++;
        }
    }

    public void setActiveMissionIndex(int activeMissionIndex) {
        this.activeMissionIndex = Math.max(0, Math.min(historicalMissions.length - 1, activeMissionIndex));
    }

    public void setHistoricalCycleCount(int historicalCycleCount) {
        this.historicalCycleCount = Math.max(1, historicalCycleCount);
    }

    public void restoreMissionState(int activeMissionIndex, int currentTaskIndex, int historicalCycleCount) {
        setActiveMissionIndex(activeMissionIndex);
        setHistoricalCycleCount(historicalCycleCount);
        Mission active = getActiveMission();
        if (active != null) {
            active.setCurrentTaskIndex(currentTaskIndex);
        }
    }

    public List<String> getUnlockedSpecies() {
        List<String> list = new ArrayList<String>();
        list.add("Humanity");

        if (activeMissionIndex >= 1) { // Act I (Mission 2 & 3)
            list.add("Asari");
            list.add("Turian");
            list.add("Salarian");
        }
        if (activeMissionIndex >= 3) { // Act II (Mission 4)
            list.add("Krogan");
            list.add("Quarian");
            list.add("Batarian");
        }
        if (activeMissionIndex >= 4) { // Act III (Mission 5)
            list.add("Hanar");
            list.add("Drell");
            list.add("Rachni");
        }
        // Note: 5 Specialized species (Volus, Vorcha, Elcor, Prothean, Yahg)
        // are NOT auto-unlocked by story, but MUST be researched in Reaper Bio-Banks!
        return list;
    }

    public List<String> getUnlockedSpecies(ReaperEngine engine) {
        List<String> list = getUnlockedSpecies();
        if (engine != null && engine.getSequencedGenomes() != null) {
            for (String seq : engine.getSequencedGenomes()) {
                if (seq == null || seq.trim().isEmpty()) continue;
                String cap = seq.substring(0, 1).toUpperCase() + seq.substring(1).toLowerCase();
                boolean found = false;
                for (String existing : list) {
                    if (existing.equalsIgnoreCase(seq)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    list.add(cap);
                }
            }
        }
        return list;
    }

    public boolean isSpeciesUnlocked(String speciesName) {
        return isSpeciesUnlocked(speciesName, null);
    }

    public boolean isSpeciesUnlocked(String speciesName, ReaperEngine engine) {
        if (speciesName == null) return false;
        String lower = speciesName.toLowerCase().trim();
        for (String s : getUnlockedSpecies(engine)) {
            if (s.toLowerCase().equals(lower)) return true;
        }
        return false;
    }

    public List<String> getUnlockedUnits() {
        List<String> list = new ArrayList<String>();
        list.add("Collector Drone");
        if (activeMissionIndex >= 1) {
            list.add("Husk Swarm");
        }
        if (activeMissionIndex >= 3) {
            list.add("Scion Behemoth");
        }
        return list;
    }

    public boolean isUnitUnlocked(String unitName) {
        if (unitName == null) return false;
        String lower = unitName.toLowerCase().trim();
        for (String u : getUnlockedUnits()) {
            if (u.toLowerCase().equals(lower) || lower.contains(u.toLowerCase())) return true;
        }
        return false;
    }

    public String getUnlockNotification(int missionIndex) {
        switch (missionIndex) {
            case 1:
                return "========================================================================\n"
                     + "       *** ACT I UNLOCKED: THE RUINED CITADEL & SOL SILO ***            \n"
                     + "========================================================================\n"
                     + "  [+] NARRATIVE ADVANCEMENT:\n"
                     + "      Shepard's Crucible is crushed, but the dark energy backlash ruined\n"
                     + "      the Citadel and severed the primary relays. The Reapers transition\n"
                     + "      to cosmic farmers to replenish their depleted dark space reserves!\n\n"
                     + "  [+] NEW ASSETS & PROTOCOLS UNLOCKED:\n"
                     + "      - Ruined Citadel Reconstruction (Keepers & Eezo Dividends)\n"
                     + "      - Bio-Bank Genome Synthesis: Asari, Turian, Salarian Genomes\n"
                     + "      - Biomechanical Swarm Construct: Husk Swarm\n"
                     + "      - Cyclonic Kinetic Shield Matrix Research\n"
                     + "========================================================================";
            case 3:
                return "========================================================================\n"
                     + "       *** ACT II UNLOCKED: THE ATTICAN TRAVERSE & MARTIAL GENOMES ***  \n"
                     + "========================================================================\n"
                     + "  [+] SECTOR RECONNECTED:\n"
                     + "      - Sector 1: The Attican Traverse & Krogan DMZ (6 Worlds)\n"
                     + "  [+] NEW MARTIAL GENOMES AVAILABLE IN BIO-BANKS:\n"
                     + "      - Krogan, Quarian, Batarian, Volus\n"
                     + "  [+] NEW SWARM CONSTRUCT:\n"
                     + "      - Scion Behemoth (Heavy Shock Artillery // Breaches Kinetic Barriers)\n"
                     + "  [+] NEW ARMADA RESEARCH:\n"
                     + "      - Thanix Magneto-Hydrodynamic Cannons (+30% Dark Energy on harvest)\n"
                     + "========================================================================";
            case 4:
                return "========================================================================\n"
                     + "       *** ACT III UNLOCKED: THE PERSEUS VEIL & SYNTHETIC HERESY ***    \n"
                     + "========================================================================\n"
                     + "  [+] SECTOR RECONNECTED:\n"
                     + "      - Sector 2: The Terminus Systems & Perseus Veil (6 Worlds)\n"
                     + "  [+] PRECURSOR & SPECIALIZED SPECIES GENOMES AVAILABLE IN BIO-BANKS:\n"
                     + "      - Prothean (Eden Prime): Ancient imperial masters (x3.0 Dark Energy)\n"
                     + "      - Hanar (Kahje)        : Enkindler acolytes (Auto-aligns Mass Relays)\n"
                     + "      - Drell (Rakhana)      : Photographic memory (+50% synaptic dark energy)\n"
                     + "      - Rachni (Suen)        : Quantum song hive (Functions as intrinsic relay)\n"
                     + "  [+] NEW ARMADA RESEARCH:\n"
                     + "      - Genetic Synthesis Vats (Doubles Cargo Hold + 50% biomass value)\n"
                     + "========================================================================";
            case 5:
                return "========================================================================\n"
                     + "       *** ACT IV UNLOCKED: THE SHADOW RIM & PRECURSOR CLONES ***       \n"
                     + "========================================================================\n"
                     + "  [+] SECTOR RECONNECTED:\n"
                     + "      - Sector 3: The Shadow Rim & Precursor Verge (6 Worlds)\n"
                     + "  [+] ALL 15 CANONICAL SPECIES NOW UNLOCKED IN BIO-BANKS:\n"
                     + "      - Elcor (Dekuuna)      : Living artillery (Capital ship kinetic armor)\n"
                     + "      - Vorcha (Heshtok)     : Cellular plasticity (Immune to all cosmic storms)\n"
                     + "      - Yahg (Parnack)       : Apex predators (Shadow Broker lineage)\n"
                     + "  [+] APEX ARMADA RESEARCH:\n"
                     + "      - Quantum Entangled Relay Lattice (Cluster-wide Mass Relay tethering)\n"
                     + "      - Catalyst Convergence Protocol (Citadel AI Awakening)\n"
                     + "========================================================================";
            default:
                return null;
        }
    }

    public boolean checkMissionTriggers(GalacticState state, int totalAscensions) {
        return checkMissionTriggers(state, null, totalAscensions, -1, -1);
    }

    public boolean checkMissionTriggers(GalacticState state, int totalAscensions, int selectedSector, int selectedCluster) {
        return checkMissionTriggers(state, null, totalAscensions, selectedSector, selectedCluster);
    }

    public boolean checkMissionTriggers(GalacticState state, TechTree techTree, int totalAscensions, int selectedSector, int selectedCluster) {
        Mission current = getActiveMission();
        if (current.isCompleted()) return true;

        switch (current.getMissionNumber()) {
            case 1: // Seeding Tutorial
                StarSystem sol1 = state.getGalaxyMap().getSystem(0, 0);
                // Task 0: Click on star system Sol [0,0] on the galaxy radar map to select it
                if (current.getCurrentTaskIndex() == 0 && (selectedSector == 0 && selectedCluster == 0)) {
                    current.advanceTask();
                }
                // Task 1: Select 'Humanity' in the species dropdown and click '🌱 Plant Humanity'
                if (current.getCurrentTaskIndex() == 1 && sol1.getCivilization() != null) {
                    current.advanceTask();
                }
                // Task 2: Observe the system: the civilization is now planted at Tier 0 (Primordial)
                if (current.getCurrentTaskIndex() == 2 && sol1.getCivilization() != null) {
                    current.advanceTask();
                    return true;
                }
                // Resilient fallback: If player planted Humanity right away (or via test/CLI)
                if (sol1.getCivilization() != null) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;

            case 2: // Cultivation Watch (Check highest tier across all galaxy civilizations)
                int maxTier = -1;
                if (state != null && state.getGalaxyMap() != null) {
                    for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                        for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                            Civilization civ = state.getGalaxyMap().getSystem(r, c).getCivilization();
                            if (civ != null) {
                                maxTier = Math.max(maxTier, civ.getEvolutionaryTier());
                            }
                        }
                    }
                }
                if (maxTier >= 1 && current.getCurrentTaskIndex() == 0) {
                    current.advanceTask();
                }
                if (maxTier >= 2 && current.getCurrentTaskIndex() == 1) {
                    current.advanceTask();
                }
                if (maxTier >= 3) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;

            case 3: // Ascension Harvest
                boolean anyRelayActive = false;
                if (state != null && state.getGalaxyMap() != null) {
                    for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                        for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                            StarSystem sys = state.getGalaxyMap().getSystem(r, c);
                            if (sys.isRelayBeaconDeployed() || sys.isRelayBeamActive()) {
                                anyRelayActive = true;
                                break;
                            }
                        }
                    }
                }
                if (anyRelayActive && current.getCurrentTaskIndex() == 0) {
                    current.advanceTask();
                }
                if (totalAscensions >= 1) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;

            case 4: // Multi-sector & Swarm
                if (current.getCurrentTaskIndex() == 0) {
                    boolean sector1Unlocked = state != null && state.getGalaxyMap() != null && state.getGalaxyMap().isSectorUnlocked(1);
                    if (sector1Unlocked) current.advanceTask();
                }
                if (current.getCurrentTaskIndex() == 1) {
                    boolean droneFound = false;
                    if (state != null && state.getGalaxyMap() != null) {
                        for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                            for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                                if (state.getGalaxyMap().getSystem(r, c).getBiomechanicalUnit() != null) {
                                    droneFound = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (droneFound) current.advanceTask();
                }
                if (totalAscensions >= 6) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;

            case 5: // Act III: The Perseus Veil & Heresy Suppression
                if (current.getCurrentTaskIndex() == 0) {
                    boolean sector2Unlocked = state != null && state.getGalaxyMap() != null && state.getGalaxyMap().isSectorUnlocked(2);
                    if (sector2Unlocked) current.advanceTask();
                }
                if (current.getCurrentTaskIndex() == 1) {
                    boolean indoctrinationUnlocked = techTree != null && techTree.isUnlocked("indoctrination_emitter");
                    if (indoctrinationUnlocked) current.advanceTask();
                }
                if (totalAscensions >= 9) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;

            case 6: // Act IV: Grand Convergence & Shadow Rim
                if (current.getCurrentTaskIndex() == 0) {
                    boolean sector3Unlocked = state != null && state.getGalaxyMap() != null && state.getGalaxyMap().isSectorUnlocked(3);
                    if (sector3Unlocked) current.advanceTask();
                }
                if (current.getCurrentTaskIndex() == 1) {
                    boolean sector3RelayActive = false;
                    if (state != null && state.getGalaxyMap() != null) {
                        for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                            StarSystem sys = state.getGalaxyMap().getSystem(3, c);
                            if (sys != null && (sys.isRelayBeaconDeployed() || sys.isRelayBeamActive())) {
                                sector3RelayActive = true;
                                break;
                            }
                        }
                    }
                    if (sector3RelayActive) current.advanceTask();
                }
                if (totalAscensions >= 12) {
                    while (!current.isCompleted()) {
                        current.advanceTask();
                    }
                    return true;
                }
                break;
        }
        return current.isCompleted();
    }
}
