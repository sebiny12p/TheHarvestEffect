package com.seb.harvesteffect;

import com.seb.harvesteffect.engine.*;
import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.GalacticSector;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.item.HarvestYield;
import com.seb.harvesteffect.model.item.Resource;
import com.seb.harvesteffect.ui.CommandParser;
import com.seb.harvesteffect.ui.HarvestGUI;
import com.seb.harvesteffect.ui.TerminalRenderer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Master entry point for The Harvest Effect.
 * Features Main Menu (Continue, New Game, Load, Codex, Credits, Exit)
 * and clear instructional missions across galactic sectors.
 */
public class Main {

    public static void main(String[] args) {
        boolean launchGui = false;
        for (String arg : args) {
            if ("--gui".equalsIgnoreCase(arg) || "-g".equalsIgnoreCase(arg)) {
                launchGui = true;
                break;
            }
        }

        if (launchGui) {
            launchGuiMode();
        } else {
            launchCliMode();
        }
    }

    private static void launchGuiMode() {
        GalacticState state = new GalacticState("Harbinger", "Post-ME3 Refusal Era");
        ReaperEngine engine = new ReaperEngine(state);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HarvestGUI gui = new HarvestGUI(state, engine);
                gui.setVisible(true);
            }
        });
    }

    private static void launchCliMode() {
        CommandParser parser = new CommandParser();

        boolean inMainMenu = true;
        while (inMainMenu) {
            System.out.println("==========================================================================");
            System.out.println("                            THE HARVEST EFFECT                            ");
            System.out.println("==========================================================================");
            System.out.println("\"You exist because we allow it, and you will end because we demand it.\"");
            System.out.println("                             - Sovereign, Vanguard of Destruction\n");

            boolean hasSave = SaveManager.hasSaveFile();
            System.out.println("[MAIN MENU]");
            System.out.println("  1. Continue Campaign " + (hasSave ? "(Saved cycle found on disk)" : "[No save found]"));
            System.out.println("  2. New Campaign (Post-ME3 Refusal: Nursery Directive)");
            System.out.println("  3. Load Saved Campaign");
            System.out.println("  4. View Galactic Codex (Species, Biomes & Nursery Lore)");
            System.out.println("  5. Transmission Credits");
            System.out.println("  0. Exit to Desktop");

            int menuChoice = parser.readInt("Select menu directive (0-5): ", 0, 5);
            System.out.println();

            switch (menuChoice) {
                case 0:
                    System.out.println("Withdrawing to dark space. Process terminated.");
                    inMainMenu = false;
                    break;
                case 1:
                    // Continue most recent campaign
                    File mostRecent = SaveManager.getMostRecentSaveFile();
                    if (mostRecent != null && mostRecent.exists()) {
                        GalacticState contState = new GalacticState("Harbinger", "Prologue: The Fall of Earth (ME3 Climax)");
                        ReaperEngine contEngine = new ReaperEngine(contState);
                        try {
                            if (SaveManager.loadGame(contEngine, mostRecent)) {
                                System.out.printf("[SUCCESS] Restored most recent campaign from %s!%n%n", mostRecent.getName());
                                runGameLoop(contEngine, parser);
                            } else {
                                System.out.println("[ERROR] Could not read save file.\n");
                            }
                        } catch (Exception e) {
                            System.out.println("[ERROR] Failed to continue save: " + e.getMessage() + "\n");
                        }
                    } else {
                        System.out.println("[ERROR] No saved campaign found on disk. Please select 'New Campaign'.\n");
                    }
                    break;
                case 3:
                    handleLoadGame(parser);
                    break;
                case 2:
                    String flagship = parser.readString("Designate Sovereign Flagship [Default: Harbinger]: ", "Harbinger");
                    GalacticState newState = new GalacticState(flagship, "Prologue: The Fall of Earth (ME3 Climax)");
                    ReaperEngine newEngine = new ReaperEngine(newState);
                    System.out.printf("%n[SYSTEM] %s connected to the Dark Space Citadel Network. Systems primed.%n", flagship);
                    runGameLoop(newEngine, parser);
                    break;
                case 4:
                    showCliCodex();
                    break;
                case 5:
                    showCliCredits();
                    break;
            }
        }
    }

    private static void runGameLoop(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        MissionManager missionManager = engine.getMissionManager();

        System.out.println(missionManager.getActiveMission().getTacticalBriefing());

        boolean running = true;
        while (running) {
            MissionManager.Mission activeMission = missionManager.getActiveMission();

            System.out.println("\n==========================================================================================");
            System.out.printf("  CAMPAIGN ERA:     %-40s%n", activeMission.getCycleEraTitle());
            System.out.printf("  ACTIVE MISSION:   %-40s%n", activeMission.getMissionTitle());
            System.out.printf("  INSTRUCTION:      %s%n", activeMission.getActiveTaskText());
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf("  FLAGSHIP: %-12s | EPOCH: %-2d | EEZO: %-5d | BIOMASS: %-5d UNITS%n",
                    state.getFlagshipName(), state.getCycleEpoch(), state.getEezoReserves(), state.getAccumulatedBiomass());
            System.out.println("==========================================================================================");

            TerminalRenderer.renderGalaxyMap(state.getGalaxyMap());

            System.out.println("\n[TACTICAL COMMAND CONSOLE]");
            System.out.println("  1. Deploy Mass Relay Beacon in Star System");
            System.out.println("  2. Seed Organic Civilization from Genesis Probe");
            System.out.println("  3. Broadcast Relay Guidance Signal");
            System.out.println("  4. Initiate The Extinction Harvest (Harvest Protocol)");
            System.out.println("  5. Command Biomechanical Swarm (Recharge, Calibrate, Extract)");
            System.out.println("  6. Access Dark Space Citadel Nexus (Upgrades, Lockdown, Trade)");
            System.out.println("  7. Inspect Flagship Cargo Pods");
            System.out.println("  8. Advance Galactic Epoch (+5,000 Years / Evolutionary Growth)");
            System.out.println("  9. Access Armada Research Matrix");
            System.out.println(" 10. Relocate Sovereign Flagship Local Orbit");
            System.out.println(" 11. View Mission Tactical Briefing & Tasks");
            String alertTag = engine.getScanner().hasPendingUndecodedSignal() ? " [🚨 ENCRYPTED SIGNAL DETECTED!]" : "";
            System.out.println(" 12. Sub-Space Frequency Scanner & Decoder Minigame" + alertTag);
            System.out.println(" 13. Save Campaign Progression to Disk");
            System.out.println(" 14. Terraform Planetary Climate (Biome Shift)");
            System.out.println(" 15. Sequence Species Genome from Bio-Banks");
            System.out.println("  0. Return to Main Menu");

            int action = parser.readInt("Enter tactical directive (0-15): ", 0, 15);
            System.out.println();

            switch (action) {
                case 0:
                    SaveManager.autoSave(engine);
                    System.out.println("Campaign auto-saved. Returning to Main Menu...");
                    running = false;
                    break;
                case 1:
                    handleDeployRelay(engine, parser);
                    break;
                case 2:
                    handleSeedCivilization(engine, parser);
                    break;
                case 3:
                    handleBroadcastSignal(engine, parser);
                    break;
                case 4:
                    handleHarvest(engine, parser);
                    break;
                case 5:
                    handleSwarm(engine, parser);
                    break;
                case 6:
                    handleCitadelNexus(engine, state, parser);
                    break;
                case 7:
                    handleInspectCargo(state);
                    break;
                case 8:
                    handleAdvanceEpoch(engine, state);
                    break;
                case 9:
                    handleTechTree(engine, parser);
                    break;
                case 10:
                    handleRelocateFlagship(engine, parser);
                    break;
                case 11:
                    System.out.println(activeMission.getTacticalBriefing());
                    System.out.println("ACTIVE TASK: " + activeMission.getActiveTaskText());
                    break;
                case 12:
                    handleSubSpaceScanner(engine, parser);
                    break;
                case 13:
                    handleSaveGame(engine, parser);
                    break;
                case 14:
                    handleTerraform(engine, parser);
                    break;
                case 15:
                    handleSequenceGenome(engine, parser);
                    break;
            }

            // Check if mission completed
            boolean missionDone = missionManager.getActiveMission().isCompleted()
                    || missionManager.checkMissionTriggers(state, engine.getCampaign().getTotalAscensions());
            if (missionDone) {
                System.out.println("\n**************************************************************************");
                System.out.println("             *** MISSION DIRECTIVE ACCOMPLISHED! ***                      ");
                System.out.println("**************************************************************************");
                System.out.println("Directive Completed: " + activeMission.getMissionTitle());
                missionManager.advanceMission();
                String unlockAnnouncement = missionManager.getUnlockNotification(missionManager.getActiveMissionIndex());
                if (unlockAnnouncement != null && !unlockAnnouncement.isEmpty()) {
                    System.out.println("\n" + unlockAnnouncement + "\n");
                }
                System.out.println(missionManager.getActiveMission().getTacticalBriefing());
                SaveManager.autoSave(engine);
            }

            // Check for pending Story Dilemma
            com.seb.harvesteffect.story.StoryDilemma pendingDilemma =
                    engine.getDilemmaManager().getPendingDilemmaForCampaign(engine.getCampaign().getCurrentAct());
            if (pendingDilemma != null) {
                handleStoryDilemma(pendingDilemma, state, engine.getDilemmaManager(), parser);
            }

            // Check Campaign Victory
            if (engine.getCampaign().isCampaignVictory()) {
                System.out.println("\n**************************************************************************");
                System.out.println("            *** VICTORY: THE HARVEST EFFECT PREVAILS! ***                 ");
                System.out.println("**************************************************************************");
                System.out.println(engine.getCampaign().getStoryBriefing());
                System.out.println("**************************************************************************");
                System.out.printf("Total Ascensions Sealed: %d | Final Biomass: %d | Final Eezo: %d%n%n",
                        engine.getCampaign().getTotalAscensions(), state.getAccumulatedBiomass(), state.getEezoReserves());
                SaveManager.autoSave(engine);
                parser.readString("Press Enter to return to Main Menu...", "");
                running = false;
                break;
            }

            // Check Campaign Defeat (Crucible Reached 100%)
            if (engine.getCampaign().isCrucibleDefeat()) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("             *** CRITICAL DEFEAT: THE CRUCIBLE HAS FIRED! ***            ");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("The Allied Fleets united in the outer rim and triggered the Crucible remnants super-weapon.");
                System.out.println("Dark energy shockwaves cascade through all Mass Relays. The Reaper armada is shattered.");
                System.out.println("The Harvest Effect has failed. The organics have survived.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                parser.readString("Press Enter to return to Main Menu...", "");
                running = false;
                break;
            }
            System.out.println();
        }
    }

    private static void handleStoryDilemma(com.seb.harvesteffect.story.StoryDilemma dilemma,
                                           GalacticState state,
                                           com.seb.harvesteffect.story.StoryDilemmaManager dilemmaMgr,
                                           CommandParser parser) {
        System.out.println("\n==========================================================================");
        System.out.println("             *** INCOMING HIGH-PRIORITY NARRATIVE TRANSMISSION ***        ");
        System.out.println("==========================================================================");
        System.out.printf("DILEMMA: %s%n", dilemma.getTitle());
        System.out.printf("SPEAKER: %s%n%n", dilemma.getSpeaker());
        System.out.println(dilemma.getTransmissionText());
        System.out.println("\nTACTICAL DIRECTIVES (Choose Sovereign's Action):");
        System.out.printf("  1. %s%n     %s%n", dilemma.getChoiceA().getLabel(), dilemma.getChoiceA().getDescription());
        System.out.printf("  2. %s%n     %s%n", dilemma.getChoiceB().getLabel(), dilemma.getChoiceB().getDescription());

        int pick = parser.readInt("Select directive (1 or 2): ", 1, 2);
        com.seb.harvesteffect.story.StoryDilemma.Choice chosen = (pick == 1) ? dilemma.getChoiceA() : dilemma.getChoiceB();
        chosen.apply(state);
        dilemmaMgr.markResolved(dilemma.getActId());

        System.out.println("\n--- [CONSEQUENCE] --------------------------------------------------------");
        System.out.println(chosen.getOutcomeNarrative());
        if (chosen.getEezoBonus() > 0 || chosen.getBiomassBonus() > 0) {
            System.out.printf("Treasury Impact: +%d Eezo, +%d Genetic Biomass.%n",
                    chosen.getEezoBonus(), chosen.getBiomassBonus());
        }
        System.out.println("--------------------------------------------------------------------------\n");
    }

    private static void handleDeployRelay(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        int[] coords = parser.readCoordinates("Target star system for Mass Relay deployment",
                state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
        try {
            engine.deployRelay(coords[0], coords[1]);
            System.out.printf("[SUCCESS] Mass Relay Beacon operational in Sector %d, Cluster %d (-50 Eezo).%n",
                    coords[0], coords[1]);
        } catch (InsufficientEezoException e) {
            System.out.printf("[ABORTED] %s%n", e.getMessage());
        } catch (ReaperException e) {
            System.out.printf("[ABORTED] %s%n", e.getMessage());
        }
    }

    private static void handleSeedCivilization(ReaperEngine engine, CommandParser parser) {
        MissionManager mm = engine.getMissionManager();
        GalacticState state = engine.getState();
        List<String> unlocked = mm.getUnlockedSpecies(engine);

        System.out.println("Available Milky Way Species to Seed (Story Progression & Economics):");
        for (int i = 0; i < unlocked.size(); i++) {
            Civilization tempCiv = engine.createCivilization(unlocked.get(i));
            boolean sequenced = engine.isGenomeSequenced(unlocked.get(i));
            System.out.printf("  [%2d] %-10s (%3d Eezo) [%s] ", i + 1, unlocked.get(i), tempCiv.getSeedingCost(),
                    sequenced ? "Ready" : "Unsequenced");
            if ((i + 1) % 2 == 0 || i == unlocked.size() - 1) {
                System.out.println();
            }
        }
        if (unlocked.size() < 15) {
            System.out.printf("  >> Notice: %d species still classified. Advance campaign missions to discover them!%n",
                    15 - unlocked.size());
        }

        int choice = parser.readInt("Select species number (1-" + unlocked.size() + "): ", 1, unlocked.size());
        String selectedSpecies = unlocked.get(choice - 1);
        Civilization targetCiv = engine.createCivilization(selectedSpecies);

        int[] coords = parser.readCoordinates(String.format("Target star system to deploy %s Genesis Probe (Cost: %d Eezo)",
                selectedSpecies, targetCiv.getSeedingCost()), state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
        try {
            engine.seedCivilization(coords[0], coords[1], selectedSpecies);
            System.out.printf("[SUCCESS] %s successfully seeded in [%d, %d] (-%d Eezo). Incubation initialized.%n",
                    selectedSpecies.toUpperCase(), coords[0], coords[1], targetCiv.getSeedingCost());
        } catch (ReaperException e) {
            System.out.printf("[ABORTED] %s%n", e.getMessage());
        }
    }

    private static void handleBroadcastSignal(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        int[] coords = parser.readCoordinates("Target system for Mass Relay alignment",
                state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
        StarSystem sys = engine.getState().getGalaxyMap().getSystem(coords[0], coords[1]);
        if (!sys.isRelayBeaconDeployed()) {
            System.out.println("[ERROR] No Mass Relay Beacon deployed in target star system.");
            return;
        }
        sys.setRelayBeamActive(true);
        System.out.printf("[SUCCESS] Primary Mass Relay corridor focused on %s [%d, %d].%n",
                sys.getSystemName(), coords[0], coords[1]);
    }

    private static void handleHarvest(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        System.out.println("Extinction Harvest Directives:");
        System.out.println("  1. Harvest Targeted Star System");
        System.out.println("  2. Batch Harvest ALL Ripe Worlds Across Galaxy");
        int subChoice = parser.readInt("Select protocol (1-2): ", 1, 2);

        if (subChoice == 2) {
            handleBatchHarvest(engine);
            return;
        }

        int[] coords = parser.readCoordinates("Designate star system for Ascension Harvest",
                state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
        try {
            HarvestYield yield = engine.harvestSystem(coords[0], coords[1]);
            System.out.println("==============================================================");
            System.out.println("           ASCENSION COMPLETE // BIOLOGICAL PURGE             ");
            System.out.println("==============================================================");
            System.out.printf("  Origin Species:     %s%n", yield.getOriginSpecies());
            System.out.printf("  Genetic Biomass:    +%d Units%n", yield.getGeneticBiomass());
            System.out.printf("  Dark Energy (Eezo): +%d Units%n", yield.getDarkEnergyYield());
            System.out.println("  Biomass matrix secured in Flagship Cargo Pods.");
            System.out.println("==============================================================");

            if (engine.getScanner().hasPendingUndecodedSignal()) {
                System.out.println("\n  🚨 [SUB-SPACE ENCRYPTED SIGNAL INTERCEPTED!]");
                System.out.println("  Anomalous carrier wave detected! Access directive [12] to tune and decrypt.");
            }
        } catch (CivilizationPrematureException e) {
            System.out.printf("[HARVEST BLOCKED] %s%n", e.getMessage());
        } catch (CivilizationBarrierException e) {
            System.out.printf("[HARVEST DEFLECTED BY KINETIC BARRIER] %s%n", e.getMessage());
        } catch (CargoHoldFullException e) {
            System.out.printf("[HARVEST BLOCKED] %s%n", e.getMessage());
        } catch (ReaperException e) {
            System.out.printf("[HARVEST BLOCKED] %s%n", e.getMessage());
        } catch (IllegalStateException e) {
            System.out.printf("[ERROR] %s%n", e.getMessage());
        }
    }

    private static void handleBatchHarvest(ReaperEngine engine) {
        GalacticState state = engine.getState();
        GalacticSector map = state.getGalaxyMap();
        List<StarSystem> ripe = new ArrayList<StarSystem>();
        for (int r = 0; r < map.getRowCount(); r++) {
            for (int c = 0; c < map.getColCount(); c++) {
                StarSystem sys = map.getSystem(r, c);
                if (sys.getCivilization() != null && sys.getCivilization().isHarvestReady()) {
                    ripe.add(sys);
                }
            }
        }
        if (ripe.isEmpty()) {
            System.out.println("[NOTICE] No civilizations currently at Tier 3 Apex Zenith (Ripe) for harvest.");
            return;
        }
        int totalBio = 0;
        int totalEezo = 0;
        System.out.println("==============================================================");
        System.out.printf("   THE GREAT ASCENSION // BATCH HARVEST (%d WORLDS)%n", ripe.size());
        System.out.println("==============================================================");
        for (StarSystem sys : ripe) {
            try {
                HarvestYield y = engine.harvestSystem(sys.getSector(), sys.getCluster());
                totalBio += y.getGeneticBiomass();
                totalEezo += y.getDarkEnergyYield();
                System.out.printf("  • %-12s [%d,%d]: +%d Eezo, +%d Biomass%n",
                        y.getOriginSpecies(), sys.getSector(), sys.getCluster(),
                        y.getDarkEnergyYield(), y.getGeneticBiomass());
            } catch (Exception ignored) {}
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("  TOTAL ASCENSION REAPED: +%d Eezo, +%d Genetic Biomass%n", totalEezo, totalBio);
        System.out.println("==============================================================");
    }

    private static void handleSwarm(ReaperEngine engine, CommandParser parser) {
        MissionManager mm = engine.getMissionManager();
        List<String> unlockedUnits = mm.getUnlockedUnits();

        System.out.println("Biomechanical Swarm Operations (Story Progression):");
        for (int i = 0; i < unlockedUnits.size(); i++) {
            String uName = unlockedUnits.get(i);
            int eCost = uName.toLowerCase().contains("scion") ? 60 : (uName.toLowerCase().contains("drone") ? 75 : 30);
            int bCost = uName.toLowerCase().contains("scion") ? 140 : (uName.toLowerCase().contains("drone") ? 0 : 50);
            if (bCost > 0) {
                System.out.printf("  %d. Station %s (Cost: %d Eezo, %d Biomass)%n", i + 1, uName, eCost, bCost);
            } else {
                System.out.printf("  %d. Station %s (Cost: %d Eezo)%n", i + 1, uName, eCost);
            }
        }
        int rechargeChoice = unlockedUnits.size() + 1;
        int extractChoice = unlockedUnits.size() + 2;
        int moveChoice = unlockedUnits.size() + 3;
        int dismantleChoice = unlockedUnits.size() + 4;
        System.out.printf("  %d. Recharge Biomechanical Energy Core%n", rechargeChoice);
        System.out.printf("  %d. Extract Telemetry / Biotic Yield%n", extractChoice);
        System.out.printf("  %d. Relocate / Move Stationed Swarm%n", moveChoice);
        System.out.printf("  %d. Dismantle Stationed Swarm (Recover 50%% Salvage)%n", dismantleChoice);

        GalacticState state = engine.getState();
        int choice = parser.readInt(String.format("Directive (1-%d): ", dismantleChoice), 1, dismantleChoice);
        if (choice >= 1 && choice <= unlockedUnits.size()) {
            String selectedUnit = unlockedUnits.get(choice - 1);
            int[] coords = parser.readCoordinates("Target star system for swarm deployment",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            try {
                engine.deployUnit(coords[0], coords[1], selectedUnit);
                System.out.printf("[SUCCESS] %s construct deployed in [%d, %d].%n",
                        selectedUnit, coords[0], coords[1]);
            } catch (ReaperException e) {
                System.out.printf("[ABORTED] %s%n", e.getMessage());
            }
        } else if (choice == rechargeChoice) {
            int[] coords = parser.readCoordinates("Target system to recharge",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            int power = parser.readInt("Energy units to inject (cost: 1 Eezo per 2 EP): ", 10, 200);
            try {
                engine.rechargeUnit(coords[0], coords[1], power);
                System.out.println("[SUCCESS] Core calibrated and power replenished.");
            } catch (Exception e) {
                System.out.printf("[ERROR] %s%n", e.getMessage());
            }
        } else if (choice == extractChoice) {
            int[] coords = parser.readCoordinates("Target system to extract telemetry",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            StarSystem sys = engine.getState().getGalaxyMap().getSystem(coords[0], coords[1]);
            if (sys.getBiomechanicalUnit() != null) {
                HarvestYield y = sys.getBiomechanicalUnit().extractTelemetry();
                if (y != null) {
                    try {
                        engine.getState().getCargoHold().store(y);
                        engine.getState().addBiomass(y.getGeneticBiomass());
                        System.out.printf("[SUCCESS] Extracted: %s%n", y.getDescription());
                    } catch (CargoHoldFullException e) {
                        System.out.printf("[ABORTED] %s%n", e.getMessage());
                    }
                } else {
                    System.out.println("[NOTICE] No telemetry or samples ready for retrieval.");
                }
            } else {
                System.out.println("[ERROR] No unit stationed in target system.");
            }
        } else if (choice == moveChoice) {
            int[] from = parser.readCoordinates("Source star system with stationed swarm",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            int[] to = parser.readCoordinates("Destination star system to relocate swarm",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            try {
                engine.moveUnit(from[0], from[1], to[0], to[1]);
                System.out.printf("[SUCCESS] Swarm construct successfully transferred from [%d, %d] to [%d, %d].%n",
                        from[0], from[1], to[0], to[1]);
            } catch (Exception e) {
                System.out.printf("[RELOCATION FAILED] %s%n", e.getMessage());
            }
        } else if (choice == dismantleChoice) {
            int[] coords = parser.readCoordinates("Target system with stationed swarm to dismantle",
                    state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
            try {
                int[] refund = engine.dismantleUnit(coords[0], coords[1]);
                System.out.printf("[SUCCESS] Swarm construct decommissioned at [%d, %d]. Recovered +%d Eezo, +%d Biomass.%n",
                        coords[0], coords[1], refund[0], refund[1]);
            } catch (Exception e) {
                System.out.printf("[DISMANTLE FAILED] %s%n", e.getMessage());
            }
        }
    }

    private static void handleCitadelNexus(ReaperEngine engine, GalacticState state, CommandParser parser) {
        CitadelNexus nexus = engine.getNexus();
        System.out.println("==============================================================");
        System.out.println("      DARK SPACE CITADEL RELAY NEXUS & MEGA-STRUCTURE         ");
        System.out.println("==============================================================");
        System.out.printf("  Current Status: %s%n", nexus.getTierName());
        System.out.printf("  Perks:          %s%n", nexus.getTierDescription());
        System.out.printf("  Passive Income: +%d Eezo/epoch | Fleet Reserves: %d Eezo, %d Biomass%n%n",
                nexus.getPassiveEezoDividend(), state.getEezoReserves(), state.getAccumulatedBiomass());

        System.out.println("Citadel Mega-Structure Directives:");
        System.out.println("  1. Upgrade Citadel Mega-Structure (" +
                (nexus.getTier() < 5 ? String.format("Next: Tier %d — Cost: %d Eezo, %d Bio",
                        nexus.getTier() + 1, nexus.getNextTierEezoCost(), nexus.getNextTierBiomassCost()) : "MAX CONVERGENCE") + ")");
        System.out.println("  2. Initiate Citadel Arms Lockdown (" +
                (nexus.getTier() >= CitadelNexus.TIER_3_LOCKDOWN ? (nexus.isLockdownReady() ? "READY: -25% Crucible Progress" : "ALREADY USED") : "LOCKED — Requires Tier III") + ")");
        System.out.println("  3. Requisition Catalog Item (Probes, Units, Armor with Citadel Discount)");
        System.out.println("  0. Return to Command Console");

        int choice = parser.readInt("Enter directive (0-3): ", 0, 3);
        if (choice == 1) {
            try {
                nexus.upgradeCitadel(state, engine.getTechTree());
                System.out.println("[SUCCESS] Citadel Mega-Structure advanced to: " + nexus.getTierName());
            } catch (Exception e) {
                System.out.println("[UPGRADE FAILED] " + e.getMessage());
            }
        } else if (choice == 2) {
            try {
                String msg = nexus.triggerCitadelLockdown(engine.getCampaign());
                System.out.println(msg);
            } catch (Exception e) {
                System.out.println("[LOCKDOWN FAILED] " + e.getMessage());
            }
        } else if (choice == 3) {
            List<String> items = new ArrayList<String>(nexus.getCatalog().keySet());
            System.out.println("\nAvailable Requisitions (Includes Citadel Discount):");
            for (int i = 0; i < items.size(); i++) {
                String key = items.get(i);
                int cost = nexus.getEffectiveCost(key);
                System.out.printf("  [%2d] %-25s : %4d Eezo%n", i + 1, key, cost);
            }
            int itemChoice = parser.readInt("Enter catalog item number to requisition (0 to cancel): ", 0, items.size());
            if (itemChoice > 0) {
                String itemKey = items.get(itemChoice - 1);
                try {
                    Resource purchased = nexus.purchaseItem(itemKey, state);
                    System.out.printf("[ACQUIRED] %s stored in Cargo Hold. Remaining Eezo: %d%n",
                            purchased.getItemName(), state.getEezoReserves());
                } catch (Exception e) {
                    System.out.printf("[TRANSACTION FAILED] %s%n", e.getMessage());
                }
            }
        }
    }

    private static void handleInspectCargo(GalacticState state) {
        System.out.println("==============================================================");
        System.out.printf("      FLAGSHIP CARGO POD MANIFEST (%d/%d SLOTS OCCUPIED)       %n",
                state.getCargoHold().getOccupiedCount(), state.getCargoHold().getCapacity());
        System.out.println("==============================================================");
        List<Resource> items = state.getCargoHold().getManifest();
        if (items.isEmpty()) {
            System.out.println("  Cargo pods empty. Harvest civilizations or trade at Citadel.");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Resource r = items.get(i);
                System.out.printf("  Pod [%2d] %-30s | %4d Mass | Salvage: %d Eezo (Needed for Apex Tech)%n",
                        i, r.getItemName(), r.getMassUnits(), r.getEezoValue());
            }
        }
        System.out.println("==============================================================");
    }

    private static void handleAdvanceEpoch(ReaperEngine engine, GalacticState state) {
        GalacticPhenomenon phen = engine.advanceCycle();
        System.out.println("==============================================================");
        System.out.printf("  EPOCH ADVANCED: +5,000 YEARS (Now Cycle Epoch %d: %s)%n",
                state.getCycleEpoch(), state.getCurrentEra().getDisplayName());
        System.out.printf("  Cosmic Phenomenon: %s%n", phen.getTitle());
        System.out.printf("  Impact: %s%n", phen.getDescription());

        int dividends = state.calculatePassiveDividends();
        if (dividends > 0) {
            System.out.printf("  [COMMERCE] Planetary Volus dividends deposited +%d Eezo!%n", dividends);
        }

        if (engine.getCampaign().getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            int researchWorlds = engine.getCampaign().countUnattendedResearchWorlds(state);
            System.out.printf("  [CRITICAL THREAT] Crucible construction at %d%%", engine.getCampaign().getCrucibleProgress());
            if (researchWorlds > 0) {
                System.out.printf(" (Accelerated by +%d%% from %d unattended organic research worlds!)%n",
                        researchWorlds * 3, researchWorlds);
            } else {
                System.out.println();
            }
        }

        if (engine.getScanner().hasPendingUndecodedSignal()) {
            System.out.println("\n  🚨 [ALERT: ENCRYPTED SUB-SPACE SIGNAL INTERCEPTED!]");
            System.out.println("  Access directive [12] Sub-Space Frequency Scanner to tune and decrypt!");
        }
        System.out.println("==============================================================");
    }

    private static void handleRelocateFlagship(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        int[] coords = parser.readCoordinates("Target star system for Sovereign Flagship relocation",
                state.getGalaxyMap().getRowCount(), state.getGalaxyMap().getColCount());
        try {
            int cost = engine.moveFlagship(coords[0], coords[1]);
            StarSystem sys = engine.getState().getGalaxyMap().getSystem(coords[0], coords[1]);
            System.out.printf("[SUCCESS] Sovereign Flagship now stationed in orbit around %s [%d, %d] (-%d Eezo).%n",
                    sys.getSystemName(), coords[0], coords[1], cost);
            System.out.println("Local Orbit Bonus Active: +20% Harvest Yield & +1 Growth Acceleration!");
        } catch (InsufficientEezoException e) {
            System.out.printf("[RELOCATION FAILED] %s%n", e.getMessage());
        }
    }

    private static void handleTerraform(ReaperEngine engine, CommandParser parser) {
        GalacticState state = engine.getState();
        GalacticSector map = state.getGalaxyMap();
        int[] coords = parser.readCoordinates("Target star system for planetary terraforming (Cost: 75 Eezo)",
                map.getRowCount(), map.getColCount());
        StarSystem sys = map.getSystem(coords[0], coords[1]);

        System.out.printf("Target: %s [%d, %d] | Current Biome: %s%n",
                sys.getSystemName(), coords[0], coords[1], sys.getClimateType().getDisplayName());
        System.out.println("Available Biome Configurations:");
        System.out.println("  1. Garden Biome   (Earth/Sur'Kesh archetype — Favors Humanity, Asari, Salarian, Prothean, Hanar)");
        System.out.println("  2. Arid Biome     (Palaven/Rannoch archetype — Favors Turian, Krogan, Quarian, Vorcha, Drell)");
        System.out.println("  3. Methane Biome  (Irune/Dekuuna archetype — Favors Volus, Elcor)");
        System.out.println("  4. Volcanic Biome (Khar'shan/Parnack archetype — Favors Batarian, Yahg, Rachni)");

        int choice = parser.readInt("Select target biome (1-4, 0 to cancel): ", 0, 4);
        if (choice == 0) return;

        com.seb.harvesteffect.model.entity.ClimateType targetClimate;
        switch (choice) {
            case 1: targetClimate = com.seb.harvesteffect.model.entity.ClimateType.GARDEN; break;
            case 2: targetClimate = com.seb.harvesteffect.model.entity.ClimateType.ARID; break;
            case 3: targetClimate = com.seb.harvesteffect.model.entity.ClimateType.METHANE; break;
            default: targetClimate = com.seb.harvesteffect.model.entity.ClimateType.VOLCANIC; break;
        }

        try {
            engine.terraformSystem(coords[0], coords[1], targetClimate);
            System.out.printf("[SUCCESS] Atmospheric conditioning complete: %s [%d, %d] converted to %s (-75 Eezo).%n",
                    sys.getSystemName(), coords[0], coords[1], targetClimate.getDisplayName());
        } catch (Exception e) {
            System.out.printf("[TERRAFORM FAILED] %s%n", e.getMessage());
        }
    }

    private static void handleSequenceGenome(ReaperEngine engine, CommandParser parser) {
        MissionManager mm = engine.getMissionManager();
        String[] allSpecies = {
            "Humanity", "Asari", "Turian", "Salarian", "Krogan",
            "Quarian", "Batarian", "Volus", "Hanar", "Drell",
            "Elcor", "Vorcha", "Rachni", "Prothean", "Yahg"
        };

        System.out.println("==============================================================");
        System.out.println("    REAPER DARK SPACE BIO-BANKS & GENOME RESEARCH            ");
        System.out.println("==============================================================");
        System.out.printf("  Fleet Treasury: %d Eezo, %d Biomass%n%n",
                engine.getState().getEezoReserves(), engine.getState().getAccumulatedBiomass());

        for (int i = 0; i < allSpecies.length; i++) {
            String sp = allSpecies[i];
            boolean storyUnlocked = mm.isSpeciesUnlocked(sp);
            boolean sequenced = engine.isGenomeSequenced(sp);
            int eCost = engine.getGenomeEezoCost(sp);
            int bCost = engine.getGenomeBiomassCost(sp);

            String statusStr;
            if (sequenced || storyUnlocked) {
                statusStr = storyUnlocked ? "✓ STORY UNLOCKED" : "✓ RESEARCHED";
            } else {
                statusStr = String.format("RESEARCHABLE (Cost: %d Eezo, %d Bio)", eCost, bCost);
            }
            System.out.printf("  [%2d] %-12s | %s%n", i + 1, sp, statusStr);
        }

        int choice = parser.readInt("Select species to research (1-15, 0 to cancel): ", 0, 15);
        if (choice == 0) return;

        String sp = allSpecies[choice - 1];
        if (engine.isGenomeSequenced(sp) || mm.isSpeciesUnlocked(sp)) {
            System.out.printf("[INFO] Genome for %s is already unlocked and ready for seeding.%n", sp);
            return;
        }

        try {
            int eCost = engine.getGenomeEezoCost(sp);
            int bCost = engine.getGenomeBiomassCost(sp);
            engine.sequenceGenome(sp);
            System.out.printf("[SUCCESS] Genome for %s successfully sequenced from dark space archives (-%d Eezo, -%d Bio).%n", sp, eCost, bCost);
        } catch (Exception e) {
            System.out.printf("[SYNTHESIS FAILED] %s%n", e.getMessage());
        }
    }

    private static void handleTechTree(ReaperEngine engine, CommandParser parser) {
        TechTree tree = engine.getTechTree();
        GalacticState state = engine.getState();
        int activeMission = engine.getMissionManager().getActiveMissionIndex();
        List<TechTree.Upgrade> upgrades = tree.getAllUpgrades();

        System.out.println("==============================================================");
        System.out.println("             REAPER ARMADA RESEARCH MATRIX            ");
        System.out.println("==============================================================");
        System.out.printf("  Fleet Treasury: %d Eezo  |  %d Biomass%n%n",
                state.getEezoReserves(), state.getAccumulatedBiomass());

        TechTree.Branch currentBranch = null;
        for (int i = 0; i < upgrades.size(); i++) {
            TechTree.Upgrade u = upgrades.get(i);
            if (u.getBranch() != currentBranch) {
                currentBranch = u.getBranch();
                System.out.printf("--- [BRANCH: %s] --------------------%n", currentBranch.getTitle().toUpperCase());
            }

            String status;
            if (u.isUnlocked()) {
                status = "RESEARCHED";
            } else if (!u.isAvailableInMission(activeMission)) {
                status = "LOCKED — REQUIRES " + u.getRequiredCycleName().toUpperCase();
            } else if (u.getPrerequisiteUpgradeId() != null && !tree.isUnlocked(u.getPrerequisiteUpgradeId())) {
                status = "PREREQUISITE LOCKED";
            } else {
                status = "AVAILABLE TO RESEARCH";
            }

            System.out.printf("  [%2d] %-35s [%s]%n       Cost: %d Eezo, %d Biomass%n       %s%n",
                    i + 1, u.getName(), status,
                    u.getEezoCost(), u.getBiomassCost(), u.getDescription());
        }
        System.out.println("\n  [ 0] Return to Tactical Console");

        int choice = parser.readInt("Select upgrade to research (0-" + upgrades.size() + "): ", 0, upgrades.size());
        if (choice > 0) {
            TechTree.Upgrade target = upgrades.get(choice - 1);
            try {
                tree.unlockUpgrade(target.getId(), state, activeMission);
                if (target.getId().startsWith("genome_")) {
                    String speciesKey = target.getId().substring("genome_".length());
                    engine.addSequencedGenome(speciesKey);
                }
                System.out.printf("[UPGRADE COMPLETE] %s is now active!%n", target.getName());
            } catch (Exception e) {
                System.out.printf("[RESEARCH FAILED] %s%n", e.getMessage());
            }
        }
    }

    private static void handleSubSpaceScanner(ReaperEngine engine, CommandParser parser) {
        SubSpaceScanner scanner = engine.getScanner();
        GalacticState state = engine.getState();

        System.out.println("==============================================================");
        System.out.println("       SUB-SPACE SIGNAL SCANNER & FREQUENCY TUNING DECK       ");
        System.out.println("==============================================================");

        if (scanner.hasPendingUndecodedSignal()) {
            SubSpaceScanner.SignalTransmission sig = scanner.getActivePendingSignal();
            System.out.println("  🚨 [PRIORITY INTERCEPT: ENCRYPTED CARRIER DETECTED]");
            System.out.printf("  Source: %s%n", sig.getSource());
            System.out.printf("  Clue:   %s%n%n", sig.getFrequencyClue());
            System.out.println("Tune receiver frequency to match carrier wave and lock signal.");
            System.out.println("Target range: 100.0 to 999.0 MHz (Enter 0 to abort tuning).");

            while (true) {
                double tuned = parser.readDouble("Enter tuning frequency in MHz (e.g. 119.4) or 0 to exit: ", 0.0, 999.0);
                if (tuned == 0.0) break;

                SubSpaceScanner.DecodeResult res = scanner.attemptDecode(sig.getId(), tuned, state);
                System.out.println(res.getFeedback());
                if (res.isLocked()) {
                    System.out.println("\n--- [DECRYPTED AUDIO TRANSCRIPT] ----------------------------");
                    System.out.println(sig.getRawContent());
                    System.out.println("-------------------------------------------------------------");
                    break;
                }
            }
        } else {
            List<SubSpaceScanner.SignalTransmission> archive = scanner.getDecodedArchive();
            if (archive.isEmpty()) {
                System.out.println("  Receiver scanning sub-space channels... (All bands quiet).");
                System.out.println("  Anomalies trigger through gameplay events (harvesting Earth,");
                System.out.println("  Collector drone scans, solar flare surges, or cargo expansion).");
            } else {
                System.out.printf("  Decrypted Signal Archives (%d Unlocked Transmissions):%n", archive.size());
                for (int i = 0; i < archive.size(); i++) {
                    SubSpaceScanner.SignalTransmission sig = archive.get(i);
                    System.out.printf("  [%d] %-30s (Carrier: %.1f MHz)%n", i + 1, sig.getTitle(), sig.getTargetFrequencyMHz());
                }
                int viewChoice = parser.readInt("Select transmission to replay (0 to exit): ", 0, archive.size());
                if (viewChoice > 0) {
                    SubSpaceScanner.SignalTransmission target = archive.get(viewChoice - 1);
                    System.out.println("\n--- [" + target.getTitle().toUpperCase() + "] ---");
                    System.out.println("Source: " + target.getSource());
                    System.out.println(target.getRawContent());
                    System.out.println("-------------------------------------------------------------");
                }
            }
        }
        System.out.println("==============================================================");
    }

    private static void handleSaveGame(ReaperEngine engine, CommandParser parser) {
        System.out.println("==============================================================");
        System.out.println("            SAVE CAMPAIGN // SELECT SAVE SLOT                 ");
        System.out.println("==============================================================");
        for (int i = 1; i <= SaveManager.MAX_SLOTS; i++) {
            SaveManager.SaveMetadata meta = SaveManager.getSlotMetadata(i);
            System.out.printf("  [%d] %s%n", i, meta.getDisplayLabel());
        }
        System.out.println("  [0] Abort Saving (Cancel)");

        int choice = parser.readInt("Select target slot (1-5, 0 to cancel): ", 0, SaveManager.MAX_SLOTS);
        if (choice == 0) return;

        try {
            SaveManager.saveSlot(engine, choice);
            SaveManager.autoSave(engine);
            System.out.printf("[SUCCESS] Campaign state recorded to Slot %d!%n", choice);
        } catch (Exception e) {
            System.out.printf("[ERROR] Failed to save: %s%n", e.getMessage());
        }
    }

    private static void handleLoadGame(CommandParser parser) {
        List<SaveManager.SaveMetadata> all = SaveManager.getAllSlotMetadata();
        System.out.println("==============================================================");
        System.out.println("            LOAD CAMPAIGN // SELECT SAVE SLOT                 ");
        System.out.println("==============================================================");
        List<Integer> validSlots = new ArrayList<Integer>();
        for (SaveManager.SaveMetadata meta : all) {
            if (meta.exists()) {
                validSlots.add(meta.getSlotNumber());
                System.out.printf("  [%d] %s%n", meta.getSlotNumber(), meta.getDisplayLabel());
            }
        }
        if (validSlots.isEmpty()) {
            System.out.println("  No saved campaigns detected on disk.");
            return;
        }
        System.out.println("  [99] Cancel");

        int choice = parser.readInt("Select slot number to load (99 to cancel): ", 0, 99);
        if (choice == 99) return;
        if (!validSlots.contains(choice)) {
            System.out.println("[ERROR] Slot " + choice + " is empty or invalid.");
            return;
        }

        GalacticState loadState = new GalacticState("Harbinger", "Prologue: The Fall of Earth (ME3 Climax)");
        ReaperEngine loadEngine = new ReaperEngine(loadState);
        try {
            boolean loaded = SaveManager.loadGame(loadEngine, SaveManager.getSlotFile(choice));
            if (loaded) {
                String slotDesc = (choice == 0) ? "Auto-Save" : ("Slot " + choice);
                System.out.printf("[SUCCESS] Campaign restored from %s!%n%n", slotDesc);
                runGameLoop(loadEngine, parser);
            } else {
                System.out.println("[ERROR] Failed to restore save.");
            }
        } catch (Exception e) {
            System.out.printf("[ERROR] Failed to load save: %s%n", e.getMessage());
        }
    }

    private static void showCliCodex() {
        System.out.println("==========================================================================");
        System.out.println("              THE HARVEST EFFECT // MASTER GALACTIC CODEX                 ");
        System.out.println("==========================================================================");
        System.out.println("1. CAMPAIGN DIRECTIVES & STORY ACTS (POST-ME3 REFUSAL):");
        System.out.println("   Prologue: The Fall of Earth (2186 CE / ME3 Refusal Climax)");
        System.out.println("   Act I:    The Ruined Citadel & Sol Silo (Keepers & Bio-Banks)");
        System.out.println("   Act II:   The Attican Traverse & Martial Genomes (Relay Alpha)");
        System.out.println("   Act III:  The Perseus Veil & Synthetic Heresy (Relay Omega)");
        System.out.println("   Act IV:   The Shadow Rim & The Crucible Remnant (Relay Gamma)");
        System.out.println("   Act V:    The Eternal Silo & Catalyst Convergence (Full 24 Worlds)\n");

        System.out.println("2. PLANETARY BIOMES & CLIMATE COMPATIBILITY MATRIX:");
        System.out.println("   Each planet has an atmospheric biome. Seeding matching crops yields +50% growth");
        System.out.println("   and +25% harvest yield. Hostile biomes suffer -50% growth and -25% harvest yield.");
        System.out.println("   • Garden / Oceanic  : Favors Humanity, Asari, Salarian, Prothean, Hanar. Hostile to Vorcha, Krogan, Drell, Batarian, Yahg, Volus, Elcor.");
        System.out.println("   • Arid / Scorched   : Favors Turian, Krogan, Quarian, Vorcha, Drell. Hostile to Hanar, Salarian.");
        System.out.println("   • Dense / Methane   : Favors Volus, Elcor. Hostile to Humanity, Asari, Turian, Rachni.");
        System.out.println("   • Volcanic / Basalt : Favors Batarian, Yahg, Rachni. Hostile to Salarian, Quarian, Prothean, Hanar.");
        System.out.println("   • Barren / Frozen   : Dead celestial rock (-75% growth). Uninhabitable to ALL 15 species until terraformed");
        System.out.println("                         using an Atmospheric Converter (Cost: 75 Eezo).\n");

        System.out.println("3. PLANETARY THREATS & COUNTER-MEASURES:");
        System.out.println("   • Kinetic Defense Barriers: Martial species (Turians, Batarians) build orbital shields");
        System.out.println("     at Tier 2+. Deflects unassisted harvests. Breached by Scion Behemoths or Flagship orbit.");
        System.out.println("   • Creeping Colonization: Spacefaring species (Tier 2+) left unharvested for 2 epochs");
        System.out.println("     colonize adjacent worlds and form mutual defense pacts.");
        System.out.println("   • Rogue Synthetic Heresy: Apex species (Tier 3) left unharvested for 3+ epochs invent");
        System.out.println("     rogue AI, triggering rebellions that cut population in half. Suppressed with Husk Swarms.");
        System.out.println("   • The Crucible Threat Clock (Act IV): Allied resistance pools research from unattended");
        System.out.println("     spacefaring worlds (+3% extra per world). Reaching 100% results in Critical Defeat.");
        System.out.println("     Countered by Citadel Arms Lockdown (-25%) or Crucible Sabotage tech (-25% speed).\n");

        System.out.println("4. AGRICULTURAL MACHINERY & CONSTRUCTS:");
        System.out.println("   • Mass Relay      : Irrigation conduit providing +50% dark energy bonus upon harvest.");
        System.out.println("   • Collector Drone : Automated sprinkler doubling evolutionary growth speed.");
        System.out.println("   • Husk Swarm      : Ground defense pacifying AI rebellions and planetary blights.");
        System.out.println("   • Scion Behemoth  : Biotic siege combine providing +50% harvest yield & breaching barriers.\n");

        System.out.println("5. TIME FLOW & MULTI-SLOT CHECKPOINTS:");
        System.out.println("   • Save across 5 distinct slots (plus automatic checkpoints) to revert mistakes.");
        System.out.println("==========================================================================");
    }

    private static void showCliCredits() {
        System.out.println("==============================================================");
        System.out.println("                THE HARVEST EFFECT CREDITS                    ");
        System.out.println("==============================================================");
        System.out.println("  Developer: Sebastian");
        System.out.println("  Project: Object-Oriented Programming in Java");
        System.out.println("  Architecture: Clean OOP / Observer Pattern / MVC");
        System.out.println("  Framework: Java Swing (javax.swing) + Procedural Audio");
        System.out.println("  Tribute: Mass Effect Trilogy (BioWare / Electronic Arts)");
        System.out.println("==============================================================");
    }
}
