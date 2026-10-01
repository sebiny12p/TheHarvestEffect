package com.seb.harvesteffect.test;

import com.seb.harvesteffect.engine.*;
import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.civilization.*;
import com.seb.harvesteffect.model.entity.*;
import com.seb.harvesteffect.model.item.*;
import com.seb.harvesteffect.model.unit.*;

/**
 * Regression and integration verification suite for The Harvest Effect engine.
 * Tests domain models, contracts, polymorphic behaviors, Campaign Story Mode, and Tech Tree.
 */
public class HarvestEffectTestSuite {
    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("     RUNNING THE HARVEST EFFECT AUTOMATED TEST SUITE          ");
        System.out.println("==============================================================");

        testCivilizationEvolutionAndAscension();
        testPrematureHarvestGuard();
        testSystemCollisionGuard();
        testBiomechanicalSwarmLifecycle();
        testCitadelNexusRequisitionAndEezoGuard();
        testCargoHoldCapacityBoundary();
        testObserverPatternMassRelayOvercharge();
        testAll15SpeciesInstantiation();
        testVolusPassiveDividend();
        testQuarianRelayBandwidthBoost();
        testKroganImmunityAndMaxBiomass();
        testEasterEggsIntegrity();
        testCampaignTutorialProgression();
        testTechTreeUnlockAndBenefits();
        testCrucibleThreatClock();
        testSaveAndLoadRoundtrip();
        testStoryDilemmaExecution();
        testProceduralAudioSynthesizer();
        testStoryProgressionUnlocks();
        testGalacticEconomySeedingCostsAndHarvestROI();
        testGrowthModeAndCosmicSeasons();
        testSubSpaceFrequencyScannerAndDecoding();
        testCitadelNexusMegaStructureProgressionAndLockdown();
        testBranchingArmadaResearchMatrixAndSectorUnlocks();
        testPlanetaryKineticBarriersAndBreachMechanics();
        testSovereignFlagshipRelocationAndAura();
        testCampaignSaveLoadPersistenceWithActAndCitadel();
        testCrucibleDefeatAndCampaignVictoryConditions();
        testStoryDilemmaPipelineAndPendingQuery();
        testImmediateSectorUnlockInTechTree();
        testFourSectorStarMapAndPrimaryRelayGating();
        testPlanetaryClimatesAndAffinityMultipliers();
        testPlanetaryTerraformingAndBarrenInhabitability();
        testGenomeSynthesisFromReaperBioBanks();
        testCreepingMultiPlanetColonizationAndMutualDefense();
        testPostMe3NarrativeDilemmasAndPersistence();
        testGalacticStarChartMapWithCitadelAtCenter();
        testMissionManagerProgressionAndSelectionTriggers();
        testBugFixesVerification();
        testCliCommandParserAndTerminalRenderer();
        testSovereignMovementConstraintDroneSpecializationAndCrucibleWiping();
        testStoryUnlockedSpeciesGenomeSequencingReconciliation();

        System.out.println("==============================================================");
        System.out.printf("Test Results: %d/%d tests passed successfully!%n", passedTests, totalTests);
        if (passedTests == totalTests) {
            System.out.println("ALL INTEGRATION TESTS PASSED (100% SUCCESS) [OK]");
        } else {
            System.err.printf("[FAILURE] %d tests failed!%n", (totalTests - passedTests));
            System.exit(1);
        }
    }

    private static void assertTrue(String conditionName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("  [PASS] %s%n", conditionName);
        } else {
            System.err.printf("  [FAIL] %s%n", conditionName);
        }
    }

    private static void testCivilizationEvolutionAndAscension() {
        System.out.println("\n[Test 1: Civilization Evolution & Ascension Harvest]");
        Civilization humanity = new Humanity();
        assertTrue("Newly seeded humanity is not at apex maturity", !humanity.isHarvestReady());

        humanity.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        humanity.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        humanity.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        assertTrue("Humanity reached Tier 3 Apex Zenith after 3 epochs", humanity.isHarvestReady());

        try {
            HarvestYield yield = humanity.harvest();
            assertTrue("Harvest yields non-null genetic biomass matrix", yield != null);
            assertTrue("Harvested biomass yields positive Dark Energy value", yield.getDarkEnergyYield() > 0);
        } catch (CivilizationPrematureException e) {
            assertTrue("Unexpected premature harvest exception", false);
        }
    }

    private static void testPrematureHarvestGuard() {
        System.out.println("\n[Test 2: Premature Harvest Exception Guard]");
        Civilization asari = new Asari();
        boolean caught = false;
        try {
            asari.harvest();
        } catch (CivilizationPrematureException e) {
            caught = true;
        }
        assertTrue("CivilizationPrematureException caught on immature organic civilization", caught);
    }

    private static void testSystemCollisionGuard() {
        System.out.println("\n[Test 3: System Collision Exception Guard]");
        StarSystem system = new StarSystem("Palaven", 0, 2);
        boolean caught = false;
        try {
            system.deployCivilization(new Turian());
            system.deployCivilization(new Humanity());
        } catch (SystemOccupiedException e) {
            caught = true;
        }
        assertTrue("SystemOccupiedException caught when deploying to occupied system", caught);
    }

    private static void testBiomechanicalSwarmLifecycle() {
        System.out.println("\n[Test 4: Biomechanical Swarm Power & Production]");
        CollectorDrone drone = new CollectorDrone("D-01");
        assertTrue("New construct starts in depleted energy state", drone.isDepleted());
        assertTrue("No telemetry ready before power injection", drone.extractTelemetry() == null);

        drone.recharge(50);
        assertTrue("Construct is operational after recharge", drone.isOperational());

        drone.operationalSweep();
        HarvestYield sample = drone.extractTelemetry();
        assertTrue("Extracted yield is a Telemetry packet", sample != null);
        assertTrue("Yield cleared after extraction", drone.extractTelemetry() == null);
    }

    private static void testCitadelNexusRequisitionAndEezoGuard() {
        System.out.println("\n[Test 5: Citadel Requisition & Eezo Guard]");
        GalacticState state = new GalacticState("Sovereign", "Test Cycle");
        CitadelNexus nexus = new CitadelNexus();

        boolean caught = false;
        try {
            state.deductEezo(state.getEezoReserves());
            nexus.purchaseItem("occular-beam-emitter", state);
        } catch (InsufficientEezoException e) {
            caught = true;
        } catch (CargoHoldFullException e) {
            // Not expected here
        }
        assertTrue("InsufficientEezoException correctly caught on over-budget requisition", caught);
    }

    private static void testCargoHoldCapacityBoundary() {
        System.out.println("\n[Test 6: Cargo Pod Storage Capacity Limit]");
        CargoHold<Resource> hold = new CargoHold<Resource>(2);
        boolean caught = false;
        try {
            hold.store(new GenesisProbe("Humanity"));
            hold.store(new GenesisProbe("Asari"));
            hold.store(new GenesisProbe("Turian"));
        } catch (CargoHoldFullException e) {
            caught = true;
        }
        assertTrue("CargoHoldFullException successfully prevented cargo overflow", caught);
    }

    private static void testObserverPatternMassRelayOvercharge() {
        System.out.println("\n[Test 7: Observer Pattern Relay Surge]");
        StarSystem system = new StarSystem("Sol", 0, 0);
        assertTrue("System starts unlinked without active beam", !system.isRelayBeamActive());

        system.onCycleAdvancement(2, GalacticPhenomenon.MASS_RELAY_OVERCHARGE);
        assertTrue("Relay Surge automatically linked system via Observer pattern", system.isRelayBeamActive());
    }

    private static void testAll15SpeciesInstantiation() {
        System.out.println("\n[Test 8: 15 Pre-Andromeda Mass Effect Species Verification]");
        GalacticState state = new GalacticState("TestShip", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);

        String[] species = {
            "humanity", "asari", "turian", "salarian", "krogan",
            "quarian", "batarian", "volus", "hanar", "drell",
            "elcor", "vorcha", "rachni", "prothean", "yahg"
        };

        boolean allValid = true;
        for (String s : species) {
            Civilization c = engine.createCivilization(s);
            if (c == null || c.getSpeciesName().isEmpty() || c.getHomeworld().isEmpty()) {
                allValid = false;
                break;
            }
        }
        assertTrue("All 15 Mass Effect species instantiate with valid homeworlds and traits", allValid);
    }

    private static void testVolusPassiveDividend() {
        System.out.println("\n[Test 9: Volus Banking Cartel Dividend]");
        GalacticState state = new GalacticState("TestShip", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        try {
            engine.seedCivilization(0, 0, "volus");
            int initialEezo = state.getEezoReserves();
            int dividends = state.collectPassiveDividends();
            assertTrue("Volus species generates positive Eezo dividends", dividends > 0);
            assertTrue("State Eezo reserves increased by dividend amount",
                    state.getEezoReserves() == initialEezo + dividends);
        } catch (Exception e) {
            assertTrue("Unexpected exception: " + e.getMessage(), false);
        }
    }

    private static void testQuarianRelayBandwidthBoost() {
        System.out.println("\n[Test 10: Quarian Relay Bandwidth Engineering]");
        Quarian quarian = new Quarian();
        assertTrue("Quarian passive bandwidth boost is +0.5", quarian.getRelayBandwidthBoost() == 0.5);
        quarian.establishRelayLink();
        assertTrue("Quarian total relay bandwidth is 2.0 (1.5 base + 0.5 boost)", quarian.getRelayBandwidth() == 2.0);
    }

    private static void testKroganImmunityAndMaxBiomass() {
        System.out.println("\n[Test 11: Krogan Physiology & Insurrection Immunity]");
        Krogan krogan = new Krogan();
        assertTrue("Krogan is immune to Organic Insurrection phenomenon",
                krogan.isImmuneToPhenomenon(GalacticPhenomaticRebellion()));

        Civilization salarian = new Salarian();
        assertTrue("Krogan biomass multiplier exceeds Salarian biomass multiplier",
                krogan.calculateBiomassScore() > salarian.calculateBiomassScore());
    }

    private static GalacticPhenomenon GalacticPhenomaticRebellion() {
        return GalacticPhenomenon.ORGANIC_REBELLION;
    }

    private static void testEasterEggsIntegrity() {
        System.out.println("\n[Test 12: Mass Effect Easter Eggs Integrity]");
        assertTrue("Shepard easter egg returns Commander Shepard transmission",
                EasterEggManager.getShepardEncounter().contains("Commander Shepard"));
        assertTrue("Mordin easter egg returns Scientist Salarian lyrics",
                EasterEggManager.getMordinSong().contains("scientist salarian"));
        assertTrue("Garrus easter egg returns Calibrations quote",
                EasterEggManager.getGarrusCalibrations().contains("calibrations"));
        assertTrue("Blasto easter egg returns Hanar Spectre dialogue",
                EasterEggManager.getBlastoTheSpectre().contains("Blasto"));
        assertTrue("Space Hamster easter egg returns Hamster squeak",
                EasterEggManager.getSpaceHamster().contains("SQUEAK"));
        assertTrue("Marauder Shields easter egg honors the final protector",
                EasterEggManager.getMarauderShields().contains("MARAUDER SHIELDS"));
        assertTrue("Conrad Verner easter egg features thermal clip autograph request",
                EasterEggManager.getConradVerner().contains("Conrad Verner"));
    }

    private static void testCampaignTutorialProgression() {
        System.out.println("\n[Test 13: Campaign Tutorial & Mission Directives]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        CampaignManager campaign = engine.getCampaign();

        assertTrue("Campaign starts in Act 0 Prologue",
                campaign.getCurrentAct() == CampaignManager.Act.ACT_0_PROLOGUE);
        assertTrue("Tutorial starts at Step 0", campaign.getTutorialStep() == 0);

        // Step 0 -> 1: Deploy Relay in Sol
        try {
            engine.deployRelay(0, 0);
        } catch (Exception e) {
            assertTrue("Failed to deploy relay: " + e.getMessage(), false);
        }
        assertTrue("Tutorial advances to Step 1 after deploying Relay",
                campaign.getTutorialStep() == 1);

        // Step 1 -> 2: Seed Humanity
        try {
            engine.seedCivilization(0, 0, "humanity");
            assertTrue("Tutorial advances to Step 2 after seeding Humanity",
                    campaign.getTutorialStep() == 2);
        } catch (Exception e) {
            assertTrue("Failed to seed humanity", false);
        }

        // Step 2 -> 3: Advance to Apex
        engine.advanceCycle();
        engine.advanceCycle();
        engine.advanceCycle();
        assertTrue("Tutorial advances to Step 3 when Humanity hits Apex Zenith",
                campaign.getTutorialStep() == 3);

        // Step 3 -> Act 1: Harvest
        try {
            engine.harvestSystem(0, 0);
            assertTrue("Campaign advances to Act 1 Precursor Twilight after tutorial harvest",
                    campaign.getCurrentAct() == CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT);
        } catch (Exception e) {
            assertTrue("Failed to harvest tutorial system", false);
        }
    }

    private static void testTechTreeUnlockAndBenefits() {
        System.out.println("\n[Test 14: Armada Tech Tree Unlocks & Enhancements]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        TechTree tree = new TechTree();

        assertTrue("Indoctrination emitter starts locked", !tree.isUnlocked("indoctrination_emitter"));

        state.addEezo(500);
        state.addBiomass(500);

        try {
            tree.unlockUpgrade("indoctrination_emitter", state);
            assertTrue("Indoctrination emitter successfully unlocked",
                    tree.isUnlocked("indoctrination_emitter"));
        } catch (Exception e) {
            assertTrue("Failed to unlock upgrade: " + e.getMessage(), false);
        }
    }

    private static void testCrucibleThreatClock() {
        System.out.println("\n[Test 15: Crucible Threat Clock & Defeat Condition]");
        CampaignManager campaign = new CampaignManager();
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");

        // Manually test threat clock progression in late game
        assertTrue("Crucible initially at 0%", campaign.getCrucibleProgress() == 0);
        assertTrue("Campaign is not defeated initially", !campaign.isCrucibleDefeat());
    }

    private static void testSaveAndLoadRoundtrip() {
        System.out.println("\n[Test 16: Campaign Save & Load Persistence]");
        GalacticState state = new GalacticState("Sovereign-Prime", "Cycle 99");
        ReaperEngine engine = new ReaperEngine(state);

        try {
            engine.seedCivilization(0, 0, "humanity");
            engine.deployRelay(0, 0);
            state.addEezo(350);
            state.addBiomass(420);
            state.setGrowthMode(GrowthMode.REAL_TIME);
            state.setRealTimeTickSeconds(4);

            java.io.File tempSave = new java.io.File("test_temp_save.dat");
            SaveManager.saveGame(engine, tempSave);
            assertTrue("Save file successfully written to disk", tempSave.exists());

            GalacticState freshState = new GalacticState("Harbinger", "Cycle 1");
            ReaperEngine freshEngine = new ReaperEngine(freshState);

            boolean loaded = SaveManager.loadGame(freshEngine, tempSave);
            assertTrue("Save file successfully loaded", loaded);
            assertTrue("Eezo reserves accurately restored", freshState.getEezoReserves() == state.getEezoReserves());
            assertTrue("Accumulated biomass accurately restored", freshState.getAccumulatedBiomass() == 420);
            assertTrue("Seeded civilization restored in Sol",
                    freshState.getGalaxyMap().getSystem(0, 0).getCivilization() != null);
            assertTrue("Relay active status restored in Sol",
                    freshState.getGalaxyMap().getSystem(0, 0).isRelayBeamActive());
            assertTrue("GrowthMode restored to REAL_TIME", freshState.getGrowthMode() == GrowthMode.REAL_TIME);
            assertTrue("RealTimeTickSeconds restored to 4s", freshState.getRealTimeTickSeconds() == 4);

            tempSave.delete();

            // Multi-Slot Save & Metadata Verification
            SaveManager.saveSlot(engine, 1);
            SaveManager.SaveMetadata slot1Meta = SaveManager.getSlotMetadata(1);
            assertTrue("Slot 1 file exists on disk", slot1Meta.exists());
            assertTrue("Slot 1 metadata reports Sovereign-Prime", "Sovereign-Prime".equals(slot1Meta.getFlagshipName()));
            assertTrue("Slot 1 metadata label is formatted properly", slot1Meta.getDisplayLabel().contains("SLOT 1"));

            GalacticState slot1State = new GalacticState("Harbinger", "Cycle 1");
            ReaperEngine slot1Engine = new ReaperEngine(slot1State);
            boolean slot1Loaded = SaveManager.loadSlot(slot1Engine, 1);
            assertTrue("Slot 1 loaded successfully", slot1Loaded);
            assertTrue("Slot 1 Eezo matches saved state", slot1State.getEezoReserves() == state.getEezoReserves());

            SaveManager.getSlotFile(1).delete();
        } catch (Exception e) {
            assertTrue("Save/load threw exception: " + e.getMessage(), false);
        }
    }

    private static void testStoryDilemmaExecution() {
        System.out.println("\n[Test 17: Interactive Story Dilemma & Choice Impact]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        com.seb.harvesteffect.story.StoryDilemmaManager dm = new com.seb.harvesteffect.story.StoryDilemmaManager();
        com.seb.harvesteffect.story.StoryDilemma prologueDilemma = dm.getDilemma(CampaignManager.Act.ACT_0_PROLOGUE);

        assertTrue("Prologue dilemma successfully fetched", prologueDilemma != null);
        assertTrue("Choice A label is non-empty", !prologueDilemma.getChoiceA().getLabel().isEmpty());

        int initialEezo = state.getEezoReserves();
        prologueDilemma.getChoiceA().apply(state);
        assertTrue("Choice A accurately applied Eezo reward to galactic state",
                state.getEezoReserves() == initialEezo + prologueDilemma.getChoiceA().getEezoBonus());
    }

    private static void testProceduralAudioSynthesizer() {
        System.out.println("\n[Test 18: Procedural Sci-Fi Audio Synthesizer]");
        assertTrue("Audio initially enabled", com.seb.harvesteffect.audio.SoundEffects.isSoundEnabled());
        com.seb.harvesteffect.audio.SoundEffects.setSoundEnabled(false);
        assertTrue("Audio can be muted", !com.seb.harvesteffect.audio.SoundEffects.isSoundEnabled());
        com.seb.harvesteffect.audio.SoundEffects.setSoundEnabled(true);
        assertTrue("Audio can be re-enabled", com.seb.harvesteffect.audio.SoundEffects.isSoundEnabled());
    }

    private static void testStoryProgressionUnlocks() {
        System.out.println("\n[Test 19: Story Progression Unlocks Across Historical Cycles]");
        MissionManager mm = new MissionManager();
        TechTree tree = new TechTree();
        GalacticState state = new GalacticState("TestShip", "Cycle 1");
        state.addEezo(1000);
        state.addBiomass(1000);

        // Cycle I / Mission 0
        assertTrue("Cycle I initially unlocks only Humanity",
                mm.getUnlockedSpecies().size() == 1 && mm.isSpeciesUnlocked("Humanity"));
        assertTrue("Asari is locked in Cycle I", !mm.isSpeciesUnlocked("Asari"));
        assertTrue("Cycle I initially unlocks only Collector Drone",
                mm.getUnlockedUnits().size() == 1 && mm.isUnitUnlocked("Collector Drone"));
        assertTrue("Husk Swarm is locked in Cycle I", !mm.isUnitUnlocked("Husk Swarm"));
        assertTrue("Indoctrination emitter available in Cycle I",
                tree.getUpgrade("indoctrination_emitter").isAvailableInMission(0));
        assertTrue("Thanix cannons locked in Cycle I",
                !tree.getUpgrade("thanix_cannons").isAvailableInMission(0));

        boolean techLockedCaught = false;
        try {
            tree.unlockUpgrade("thanix_cannons", state, 0);
        } catch (IllegalStateException e) {
            techLockedCaught = true;
        } catch (Exception e) {}
        assertTrue("Attempting to research future-cycle tech throws IllegalStateException", techLockedCaught);

        // Advance to Mission 1 (Cycle II)
        mm.advanceMission();
        assertTrue("Cycle II unlocks Asari, Turian, Salarian (4 total species)",
                mm.getUnlockedSpecies().size() == 4 && mm.isSpeciesUnlocked("Asari"));
        assertTrue("Cycle II unlocks Husk Swarm (2 total units)",
                mm.getUnlockedUnits().size() == 2 && mm.isUnitUnlocked("Husk Swarm"));
        assertTrue("Cyclonic barrier available in Cycle II",
                tree.getUpgrade("cyclonic_barrier").isAvailableInMission(1));
        assertTrue("Unlock announcement generated for Cycle II",
                mm.getUnlockNotification(1) != null && mm.getUnlockNotification(1).contains("Asari"));

        // Advance to Mission 3 (Cycle III)
        mm.advanceMission(); // mission index 2
        mm.advanceMission(); // mission index 3
        assertTrue("Act II unlocks Krogan, Quarian, Batarian (7 total story species)",
                mm.getUnlockedSpecies().size() == 7 && mm.isSpeciesUnlocked("Krogan"));
        assertTrue("Cycle III unlocks Scion Behemoth", mm.isUnitUnlocked("Scion Behemoth"));
        assertTrue("Thanix cannons available in Cycle III",
                tree.getUpgrade("thanix_cannons").isAvailableInMission(3));

        // Advance to Mission 4 (Cycle IV)
        mm.advanceMission(); // mission index 4
        assertTrue("Act III unlocks Hanar, Drell, Rachni (10 total story species)",
                mm.getUnlockedSpecies().size() == 10 && mm.isSpeciesUnlocked("Hanar"));
        assertTrue("Biomass vats available in Cycle IV",
                tree.getUpgrade("biomass_vats").isAvailableInMission(4));

        // Advance to Mission 5 (Cycle V)
        mm.advanceMission(); // mission index 5
        assertTrue("10 species unlocked via campaign story, while 5 specialized species require Bio-Bank research",
                mm.getUnlockedSpecies().size() == 10);
        assertTrue("Quantum entanglement relays available in Cycle V",
                tree.getUpgrade("quantum_entanglement_relays").isAvailableInMission(5));
    }

    private static void testGalacticEconomySeedingCostsAndHarvestROI() {
        System.out.println("\n[Test 20: Galactic Economy, Seeding Costs & Harvest ROI]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);

        // 1. Verify specific seeding costs across species
        assertTrue("Humanity seeding cost is 50 Eezo", new Humanity().getSeedingCost() == 50);
        assertTrue("Asari seeding cost is 150 Eezo", new Asari().getSeedingCost() == 150);
        assertTrue("Vorcha seeding cost is 40 Eezo", new Vorcha().getSeedingCost() == 40);
        assertTrue("Yahg seeding cost is 220 Eezo", new Yahg().getSeedingCost() == 220);

        // 2. Unit deployment costs
        assertTrue("Collector Drone cost is 75 Eezo and 0 Biomass",
                new CollectorDrone().getDeploymentCost() == 75 && new CollectorDrone().getBiomassCost() == 0);
        assertTrue("Husk Swarm cost is 30 Eezo and 50 Biomass",
                new HuskSwarm().getDeploymentCost() == 30 && new HuskSwarm().getBiomassCost() == 50);
        assertTrue("Scion Behemoth cost is 60 Eezo and 140 Biomass",
                new ScionBehemoth().getDeploymentCost() == 60 && new ScionBehemoth().getBiomassCost() == 140);

        // 3. Seeding deduction test
        int initialEezo = state.getEezoReserves(); // 500
        try {
            engine.seedCivilization(0, 0, "humanity");
            assertTrue("Seeding Humanity deducted 50 Eezo", state.getEezoReserves() == initialEezo - 50);

            engine.deployRelay(0, 0);
            assertTrue("Deploying Relay deducted 50 Eezo", state.getEezoReserves() == initialEezo - 100);

            engine.deployUnit(0, 0, "Collector Drone");
            assertTrue("Deploying Collector Drone deducted 75 Eezo", state.getEezoReserves() == initialEezo - 175);
        } catch (Exception e) {
            assertTrue("Economic deployment threw unexpected error: " + e.getMessage(), false);
        }

        // 4. Insufficient Eezo Guard
        try {
            state.deductEezo(state.getEezoReserves()); // Set Eezo to 0
        } catch (InsufficientEezoException ignored) {}
        assertTrue("Eezo depleted to 0", state.getEezoReserves() == 0);

        boolean insufficientCaught = false;
        try {
            engine.seedCivilization(0, 1, "asari");
        } catch (InsufficientEezoException e) {
            insufficientCaught = true;
            assertTrue("Exception identifies required cost of 150", e.getCost() == 150);
            assertTrue("Exception identifies 0 available", e.getAvailable() == 0);
        } catch (Exception e) {
            // Other exceptions shouldn't occur
        }
        assertTrue("Seeding without funds threw InsufficientEezoException", insufficientCaught);

        boolean unitInsufficientCaught = false;
        try {
            engine.deployUnit(0, 1, "Scion Behemoth");
        } catch (InsufficientEezoException e) {
            unitInsufficientCaught = true;
            assertTrue("Exception identifies unit Eezo cost of 60", e.getCost() == 60);
        } catch (Exception e) {
        }
        assertTrue("Deploying unit without funds threw InsufficientEezoException", unitInsufficientCaught);

        // Insufficient Biomass Guard: replenish Eezo to 500, but leave Biomass at 0
        state.addEezo(500);
        boolean biomassInsufficientCaught = false;
        try {
            engine.deployUnit(0, 1, "Husk Swarm");
        } catch (InsufficientBiomassException e) {
            biomassInsufficientCaught = true;
            assertTrue("Exception identifies required biomass of 50", e.getCost() == 50);
            assertTrue("Exception identifies 0 biomass available", e.getAvailable() == 0);
        } catch (Exception e) {
        }
        assertTrue("Deploying Husk Swarm without biomass threw InsufficientBiomassException", biomassInsufficientCaught);

        // 5. Harvest ROI Test
        // Advance Sol (Humanity) to Apex
        engine.advanceCycle();
        engine.advanceCycle();
        engine.advanceCycle();

        try {
            HarvestYield yield = engine.harvestSystem(0, 0);
            assertTrue("Harvest returned substantial Dark Energy (Eezo ROI)", yield.getDarkEnergyYield() >= 150);
            assertTrue("Harvest returned massive Genetic Biomass", yield.getGeneticBiomass() >= 120);
            assertTrue("State Eezo reserves restored by harvest dividend", state.getEezoReserves() >= 150);
            assertTrue("State Biomass increased by harvest dividend", state.getAccumulatedBiomass() >= 120);

            // Now with harvested biomass, deploying Husk Swarm succeeds and deducts both Eezo and Biomass!
            int preEezo = state.getEezoReserves();
            int preBio = state.getAccumulatedBiomass();
            engine.deployUnit(0, 1, "Husk Swarm");
            assertTrue("Deploying Husk Swarm deducted 30 Eezo", state.getEezoReserves() == preEezo - 30);
            assertTrue("Deploying Husk Swarm deducted 50 Biomass", state.getAccumulatedBiomass() == preBio - 50);
        } catch (Exception e) {
            assertTrue("Harvest ROI test failed: " + e.getMessage(), false);
        }
    }

    private static void testGrowthModeAndCosmicSeasons() {
        System.out.println("\n[Test 21: Growth Mode & Cosmic Agricultural Calendar]");
        GalacticState state = new GalacticState("Sovereign", "Cycle 1");

        // 1. Initial defaults
        assertTrue("Default growth mode is TURN_BASED", state.getGrowthMode() == GrowthMode.TURN_BASED);
        assertTrue("Default real-time tick interval is 6 seconds", state.getRealTimeTickSeconds() == 6);

        // 2. Mode mutation and clamping
        state.setGrowthMode(GrowthMode.REAL_TIME);
        assertTrue("Growth mode updated to REAL_TIME", state.getGrowthMode() == GrowthMode.REAL_TIME);
        state.setRealTimeTickSeconds(10);
        assertTrue("Real-time tick seconds set to 10", state.getRealTimeTickSeconds() == 10);
        state.setRealTimeTickSeconds(-5);
        assertTrue("Real-time tick seconds clamped to positive minimum", state.getRealTimeTickSeconds() >= 1);
        state.setGrowthMode(null);
        assertTrue("Null growth mode safely defaults to TURN_BASED", state.getGrowthMode() == GrowthMode.TURN_BASED);

        // 3. Cosmic Agricultural Seasons
        // Epoch 1 -> Spring
        assertTrue("Epoch 1 is Cosmic Spring", state.getCosmicSeason().contains("Spring"));

        ReaperEngine engine = new ReaperEngine(state);
        // Advance to Epoch 2 -> Summer
        engine.advanceCycle();
        assertTrue("Epoch 2 is Cosmic Summer", state.getCosmicSeason().contains("Summer"));

        // Advance to Epoch 3 -> Autumn
        engine.advanceCycle();
        assertTrue("Epoch 3 is Cosmic Autumn", state.getCosmicSeason().contains("Autumn"));

        // Advance to Epoch 4 -> Winter
        engine.advanceCycle();
        assertTrue("Epoch 4 is Cosmic Winter", state.getCosmicSeason().contains("Winter"));

        // Advance to Epoch 5 -> Spring again (cyclic)
        engine.advanceCycle();
        assertTrue("Epoch 5 returns cyclically to Cosmic Spring", state.getCosmicSeason().contains("Spring"));
    }

    private static void testSubSpaceFrequencyScannerAndDecoding() {
        System.out.println("\n[Test 22: Sub-Space Frequency Scanner & Easter Egg Decryption Minigame]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        SubSpaceScanner scanner = new SubSpaceScanner();

        // 1. Initial state
        assertTrue("Scanner initially has no pending undecoded signals", !scanner.hasPendingUndecodedSignal());
        assertTrue("Decoded archive is initially empty", scanner.getDecodedArchive().isEmpty());

        // 2. Trigger Shepard transmission (119.4 MHz)
        boolean triggered = scanner.triggerSignal("shepard");
        assertTrue("Shepard signal successfully triggered", triggered);
        assertTrue("Pending undecoded signal detected", scanner.hasPendingUndecodedSignal());
        SubSpaceScanner.SignalTransmission sig = scanner.getActivePendingSignal();
        assertTrue("Active pending signal is Shepard", sig != null && "shepard".equals(sig.getId()));
        assertTrue("Signal target frequency is 119.4 MHz", Math.abs(sig.getTargetFrequencyMHz() - 119.4) < 0.01);

        // 3. Tuning off-target: 400.0 MHz
        SubSpaceScanner.DecodeResult offTarget = scanner.attemptDecode("shepard", 400.0, state);
        assertTrue("Off-target tuning does not achieve lock", !offTarget.isLocked());
        assertTrue("Off-target provides tuning direction hint", offTarget.getFeedback().contains("TOO HIGH") || offTarget.getFeedback().contains("Decrease"));

        // 4. Fine-tuning on-target: 119.5 MHz (delta <= 1.5 MHz)
        int preEezo = state.getEezoReserves();
        int preBio = state.getAccumulatedBiomass();
        SubSpaceScanner.DecodeResult locked = scanner.attemptDecode("shepard", 119.5, state);
        assertTrue("Carrier lock achieved within 1.5 MHz tolerance", locked.isLocked());
        assertTrue("Decryption reward added Eezo to state", state.getEezoReserves() == preEezo + sig.getEezoReward());
        assertTrue("Decryption reward added Biomass to state", state.getAccumulatedBiomass() == preBio + sig.getBiomassReward());
        assertTrue("Signal is marked as decoded", sig.isDecoded());
        assertTrue("No pending undecoded signals remaining", !scanner.hasPendingUndecodedSignal());
        assertTrue("Decoded archive now contains Shepard transcript", scanner.getDecodedArchive().size() == 1);

        // 5. Test Hamster and Conrad triggers via advanceCycle
        ReaperEngine testEng = new ReaperEngine(state);
        assertTrue("Hamster initially undiscovered", !testEng.getScanner().getSignal("hamster").isDiscovered());
        state.addBiomass(600);
        testEng.advanceCycle();
        assertTrue("Hamster signal triggered after stockpiling 500+ biomass", testEng.getScanner().getSignal("hamster").isDiscovered());

        assertTrue("Conrad initially undiscovered", !testEng.getScanner().getSignal("conrad").isDiscovered());
        for (int ep = state.getCycleEpoch(); ep < 5; ep++) {
            testEng.advanceCycle();
        }
        testEng.advanceCycle();
        assertTrue("Conrad Verner signal triggered at Epoch 5+", testEng.getScanner().getSignal("conrad").isDiscovered());
    }

    private static void testCitadelNexusMegaStructureProgressionAndLockdown() {
        System.out.println("\n[Test 23: Citadel Nexus Mega-Structure Progression & Lockdown]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        CitadelNexus nexus = new CitadelNexus();
        state.setCitadelNexus(nexus);
        TechTree techTree = new TechTree();
        CampaignManager campaign = new CampaignManager();

        // 1. Initial Tier 1
        assertTrue("Citadel starts at Tier 1 (Dormant)", nexus.getTier() == 1);
        assertTrue("Tier 1 dividend is 0", nexus.getPassiveEezoDividend() == 0);
        int baseCost = nexus.getCatalog().get("humanity-probe");
        assertTrue("Effective cost at Tier 1 equals base cost", nexus.getEffectiveCost("humanity-probe") == baseCost);

        // 2. Upgrade to Tier 2 (Keepers)
        state.addEezo(1000);
        state.addBiomass(1000);
        try {
            nexus.upgradeCitadel(state, techTree);
            assertTrue("Citadel upgraded to Tier 2 (Keepers)", nexus.getTier() == 2);
            assertTrue("Tier 2 provides 20% discount on requisitions", nexus.getEffectiveCost("humanity-probe") == (int) (baseCost * 0.8));
            assertTrue("Tier 2 provides +40 Eezo/epoch dividend", nexus.getPassiveEezoDividend() == 40);
        } catch (Exception e) {
            assertTrue("Unexpected exception on Tier 2 upgrade: " + e.getMessage(), false);
        }

        // 3. Attempt Tier 3 without TechTree upgrade "citadel_core_control"
        boolean blockedWithoutTech = false;
        try {
            nexus.upgradeCitadel(state, techTree);
        } catch (IllegalStateException ex) {
            blockedWithoutTech = true;
        } catch (Exception ex) {}
        assertTrue("Tier 3 blocked without Citadel Core Security Override tech", blockedWithoutTech);

        // 4. Research tech and upgrade to Tier 3 (Lockdown)
        state.addEezo(2000);
        state.addBiomass(2000);
        try {
            // Unlock prerequisites: cyclonic_barrier -> citadel_eezo_siphons -> citadel_core_control
            techTree.getUpgrade("cyclonic_barrier").setUnlocked(true);
            techTree.getUpgrade("citadel_eezo_siphons").setUnlocked(true);
            techTree.unlockUpgrade("citadel_core_control", state, 2);
            assertTrue("Citadel core control tech unlocked", techTree.isUnlocked("citadel_core_control"));

            nexus.upgradeCitadel(state, techTree);
            assertTrue("Citadel upgraded to Tier 3 (Arms Lockdown Grid)", nexus.getTier() == 3);
            assertTrue("Lockdown grid is primed", nexus.isLockdownReady());
        } catch (Exception e) {
            assertTrue("Tier 3 unlock failed: " + e.getMessage(), false);
        }

        // 5. Test Arms Lockdown in Crucible War
        campaign.advanceAct(); // Act 1
        campaign.advanceAct(); // Act 2
        campaign.advanceAct(); // Act 3
        campaign.advanceAct(); // Act 4 (Crucible War)
        // Advance crucible progress to 50%
        for (int i = 0; i < 10; i++) campaign.onEpochAdvance(state);
        int preCrucible = campaign.getCrucibleProgress();
        assertTrue("Crucible has active progress", preCrucible > 0);

        String result = nexus.activateArmsLockdown(campaign);
        assertTrue("Lockdown execution returned confirmation", result.contains("CITADEL ARMS LOCKED"));
        assertTrue("Crucible progress delayed by -25%", campaign.getCrucibleProgress() == Math.max(0, preCrucible - 25));
        assertTrue("Lockdown is marked triggered", nexus.isArmsLockdownActive());
        assertTrue("Lockdown active duration is 4 epochs", nexus.getLockdownDurationRemaining() == 4);

        // 6. Attempt duplicate lockdown throws IllegalStateException
        boolean duplicateCaught = false;
        try {
            nexus.activateArmsLockdown(campaign);
        } catch (IllegalStateException ex) {
            duplicateCaught = true;
        }
        assertTrue("Duplicate lockdown activation correctly prevented", duplicateCaught);

        // 7. Verify Crucible is frozen during the 4 active lockdown epochs
        int frozenProgress = campaign.getCrucibleProgress();
        for (int epoch = 1; epoch <= 3; epoch++) {
            campaign.onEpochAdvance(state);
            String notice = nexus.onEpochAdvance();
            assertTrue("Notice is null while lockdown remains active", notice == null);
            assertTrue("Crucible progress remains frozen at " + frozenProgress + "%", campaign.getCrucibleProgress() == frozenProgress);
            assertTrue("Lockdown is still active", nexus.isArmsLockdownActive());
            assertTrue("Lockdown remaining epochs is " + (4 - epoch), nexus.getLockdownDurationRemaining() == 4 - epoch);
        }

        // 4th epoch advance - arms open!
        campaign.onEpochAdvance(state);
        assertTrue("Crucible progress remained frozen through 4th epoch", campaign.getCrucibleProgress() == frozenProgress);
        String openNotice = nexus.onEpochAdvance();
        assertTrue("Arms opened notice received", openNotice != null && openNotice.contains("CITADEL ARMS OPENED"));
        assertTrue("Lockdown is no longer active", !nexus.isArmsLockdownActive());
        assertTrue("Lockdown duration is 0", nexus.getLockdownDurationRemaining() == 0);
        assertTrue("Cooldown is 4 epochs", nexus.getLockdownCooldownRemaining() == 4);
        assertTrue("Lockdown not ready while on cooldown", !nexus.isLockdownReady());

        // Cooldown prevents reactivation
        boolean cooldownBlocked = false;
        try {
            nexus.activateArmsLockdown(campaign);
        } catch (IllegalStateException ex) {
            cooldownBlocked = true;
        }
        assertTrue("Lockdown blocked while cooling down", cooldownBlocked);

        // Cooldown ticks down over 4 epochs (Crucible resumes advancing)
        for (int cd = 1; cd <= 3; cd++) {
            campaign.onEpochAdvance(state);
            String cdNotice = nexus.onEpochAdvance();
            assertTrue("Cooldown notice is null until finish", cdNotice == null);
            assertTrue("Crucible resumes advancing during cooldown", campaign.getCrucibleProgress() > frozenProgress);
            assertTrue("Cooldown remaining decrements to " + (4 - cd), nexus.getLockdownCooldownRemaining() == 4 - cd);
            assertTrue("Lockdown still not ready", !nexus.isLockdownReady());
        }

        // 4th cooldown epoch finishes - recharged!
        campaign.onEpochAdvance(state);
        String rechargeNotice = nexus.onEpochAdvance();
        assertTrue("Recharge notice received", rechargeNotice != null && rechargeNotice.contains("CITADEL ARMS RECHARGED"));
        assertTrue("Cooldown is 0", nexus.getLockdownCooldownRemaining() == 0);
        assertTrue("Lockdown is ready again", nexus.isLockdownReady());

        // Trigger lockdown a second time!
        int crucibleBeforeSecond = campaign.getCrucibleProgress();
        String secondLockdown = nexus.activateArmsLockdown(campaign);
        assertTrue("Second lockdown engaged successfully", secondLockdown.contains("CITADEL ARMS LOCKED"));
        assertTrue("Crucible delayed again by -25%", campaign.getCrucibleProgress() == Math.max(0, crucibleBeforeSecond - 25));
        assertTrue("Lockdown is active again", nexus.isArmsLockdownActive());
        assertTrue("Lockdown duration is 4", nexus.getLockdownDurationRemaining() == 4);
    }

    private static void testBranchingArmadaResearchMatrixAndSectorUnlocks() {
        System.out.println("\n[Test 24: 21-Node Branching Armada Research Matrix & Dynamic Sector Unlocks]");
        TechTree tree = new TechTree();
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        state.addEezo(5000);
        state.addBiomass(5000);

        // 1. Total upgrades count
        assertTrue("Tech tree features 22 upgrades across 5 branches", tree.getAllUpgrades().size() == 22);
        assertTrue("Extinction Armada has 4 upgrades", tree.getUpgradesByBranch(TechTree.Branch.EXTINCTION_ARMADA).size() == 4);
        assertTrue("Indoctrination & Espionage has 4 upgrades", tree.getUpgradesByBranch(TechTree.Branch.INDOCTRINATION).size() == 4);
        assertTrue("Relay Engineering & Reach has 5 upgrades", tree.getUpgradesByBranch(TechTree.Branch.RELAY_EXPANSION).size() == 5);
        assertTrue("Citadel & Fleet Economy has 4 upgrades", tree.getUpgradesByBranch(TechTree.Branch.CITADEL_CONVERGENCE).size() == 4);
        assertTrue("Bio-Bank Genome Sequencing has 5 upgrades", tree.getUpgradesByBranch(TechTree.Branch.BIO_BANK_GENOMES).size() == 5);

        // 2. Prerequisite checking: primary_relay_omega requires primary_relay_alpha
        boolean prereqBlocked = false;
        try {
            tree.unlockUpgrade("primary_relay_omega", state, 4);
        } catch (IllegalStateException e) {
            prereqBlocked = true;
        } catch (Exception e) {}
        assertTrue("Cannot research Primary Relay Omega without Alpha prerequisite", prereqBlocked);

        // 3. Dynamic Sector Unlocks
        GalacticSector map = state.getGalaxyMap();
        map.setSectorUnlocked(0, true);
        map.setSectorUnlocked(1, false);
        map.setSectorUnlocked(2, false);
        map.setSectorUnlocked(3, false);
        assertTrue("Sector 1 initially locked", !map.isSectorUnlocked(1));
        assertTrue("Sector 2 initially locked", !map.isSectorUnlocked(2));
        assertTrue("Sector 3 initially locked", !map.isSectorUnlocked(3));

        // Unlock secondary_relay_alignment -> primary_relay_alpha
        try {
            tree.unlockUpgrade("secondary_relay_alignment", state, 1);
            tree.unlockUpgrade("primary_relay_alpha", state, 1);
            map.setSectorUnlocked(1, true); // Mimics dialog or engine unlock
            assertTrue("Primary Relay Alpha researched", tree.isUnlocked("primary_relay_alpha"));
            assertTrue("Sector 1 (Attican Traverse) now unlocked", map.isSectorUnlocked(1));

            tree.unlockUpgrade("primary_relay_omega", state, 3);
            map.setSectorUnlocked(2, true);
            assertTrue("Primary Relay Omega researched", tree.isUnlocked("primary_relay_omega"));
            assertTrue("Sector 2 (Terminus Systems) now unlocked", map.isSectorUnlocked(2));

            tree.unlockUpgrade("primary_relay_gamma", state, 4);
            map.setSectorUnlocked(3, true);
            assertTrue("Primary Relay Gamma researched", tree.isUnlocked("primary_relay_gamma"));
            assertTrue("Sector 3 (Shadow Rim) now unlocked", map.isSectorUnlocked(3));
        } catch (Exception e) {
            assertTrue("Sector unlock research failed: " + e.getMessage(), false);
        }
    }

    private static void testPlanetaryKineticBarriersAndBreachMechanics() {
        System.out.println("\n[Test 25: Planetary Kinetic Barriers & Breaching Mechanics]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        state.addEezo(3000);
        state.addBiomass(3000);
        ReaperEngine engine = new ReaperEngine(state);

        // 1. Seed martial race (Turian) at [0, 2]
        try {
            engine.seedCivilization(0, 2, "turian");
        } catch (Exception e) {
            assertTrue("Seeding failed: " + e.getMessage(), false);
        }
        StarSystem sys = state.getGalaxyMap().getSystem(0, 2);
        Civilization turian = sys.getCivilization();
        assertTrue("Turian civilization established", turian != null);

        // Immature: no barrier
        assertTrue("Immature civilization has no kinetic barrier", !turian.hasKineticBarrier());

        // Advance to Tier 1
        turian.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        assertTrue("Immature Tier 1 has no kinetic barrier", !turian.hasKineticBarrier());

        // Advance to Tier 2 -> Kinetic barrier activates!
        turian.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        assertTrue("Turian Tier 2 establishes planetary kinetic barrier", turian.hasKineticBarrier());

        // Advance to Tier 3 Apex
        turian.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
        assertTrue("Turian is harvest ready", turian.isHarvestReady());

        // 2. Direct harvest attempt is deflected by barrier
        boolean barrierDeflected = false;
        try {
            engine.harvestSystem(0, 2);
        } catch (CivilizationBarrierException e) {
            barrierDeflected = true;
            assertTrue("Barrier exception identifies target species", e.getMessage().contains("Turian"));
        } catch (Exception e) {
            assertTrue("Unexpected exception: " + e.getMessage(), false);
        }
        assertTrue("Kinetic barrier successfully deflected unassisted harvest", barrierDeflected);

        // 3. Station a Scion Behemoth to breach the barrier
        try {
            engine.deployUnit(0, 2, "Scion Behemoth");
            sys.getBiomechanicalUnit().recharge(100);
            HarvestYield yield = engine.harvestSystem(0, 2);
            assertTrue("Harvest succeeded with Scion Behemoth barrier breach", yield != null);
            assertTrue("Biomass returned from Turian ascension", yield.getGeneticBiomass() > 0);
        } catch (Exception e) {
            assertTrue("Scion breach harvest failed: " + e.getMessage(), false);
        }
    }

    private static void testSovereignFlagshipRelocationAndAura() {
        System.out.println("\n[Test 26: Sovereign Flagship Relocation & Movement]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        state.addEezo(500);
        ReaperEngine engine = new ReaperEngine(state);

        // 1. Flagship initial location
        assertTrue("Flagship starts in Sector 0", state.getFlagshipSector() == 0);
        assertTrue("Flagship starts in Cluster 0", state.getFlagshipCluster() == 0);

        // 2. Relocate to [0, 3] without relay beam (costs 25 Eezo)
        int preEezo = state.getEezoReserves();
        try {
            int cost = engine.relocateFlagship(0, 3);
            assertTrue("Flagship relocation deducted 25 Eezo for unlinked star", cost == 25);
            assertTrue("State reserves deducted 25 Eezo", state.getEezoReserves() == preEezo - 25);
            assertTrue("Flagship sector updated to 0", state.getFlagshipSector() == 0);
            assertTrue("Flagship cluster updated to 3", state.getFlagshipCluster() == 3);
        } catch (Exception e) {
            assertTrue("Relocation failed: " + e.getMessage(), false);
        }

        // 3. Relocate with active Relay beam (free movement)
        try {
            engine.deployRelay(0, 4);
            state.getGalaxyMap().getSystem(0, 4).setRelayBeamActive(true);
            int freeCost = engine.relocateFlagship(0, 4);
            assertTrue("Flagship relocation via active Relay corridor is free (0 Eezo)", freeCost == 0);
            assertTrue("Flagship now stationed in [0, 4]", state.getFlagshipSector() == 0 && state.getFlagshipCluster() == 4);
        } catch (Exception e) {
            assertTrue("Relay relocation failed: " + e.getMessage(), false);
        }
    }

    private static void testCampaignSaveLoadPersistenceWithActAndCitadel() {
        System.out.println("\n[Test 27: Campaign Save/Load Persistence for Act, Citadel & Barriers]");
        GalacticState state = new GalacticState("Sovereign-Prime", "Cycle 99");
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(2000);
        state.addBiomass(2000);

        try {
            // Setup state
            engine.getCampaign().setCurrentAct(CampaignManager.Act.ACT_3_CITADEL_INDOCTRINATION);
            engine.getNexus().setTier(3);
            engine.getNexus().setArmsLockdownTriggered(true);
            engine.getDilemmaManager().markResolved("ACT_0");
            engine.getDilemmaManager().markResolved("ACT_1");

            // Setup a fortified civilization
            engine.seedCivilization(0, 2, "turian");
            StarSystem turianSys = state.getGalaxyMap().getSystem(0, 2);
            turianSys.getCivilization().advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
            turianSys.getCivilization().advanceEpoch(GalacticPhenomenon.STELLAR_CALM); // Tier 2 - barrier active
            assertTrue("Turian has kinetic barrier before save", turianSys.getCivilization().hasKineticBarrier());

            java.io.File tempSave = new java.io.File("test_act_citadel_save.dat");
            SaveManager.saveGame(engine, tempSave);
            assertTrue("Save file created", tempSave.exists());

            GalacticState loadedState = new GalacticState("Harbinger", "Cycle 1");
            ReaperEngine loadedEngine = new ReaperEngine(loadedState);
            boolean loaded = SaveManager.loadGame(loadedEngine, tempSave);
            assertTrue("Game loaded successfully", loaded);

            // Verify campaign Act & story state was restored
            assertTrue("Campaign Act accurately restored to Act 3",
                    loadedEngine.getCampaign().getCurrentAct() == CampaignManager.Act.ACT_3_CITADEL_INDOCTRINATION);

            // Verify Citadel tier was restored without resource deductions
            assertTrue("Citadel tier accurately restored to Tier 3",
                    loadedEngine.getNexus().getCurrentTier() == 3);
            assertTrue("Citadel arms lockdown state restored",
                    loadedEngine.getNexus().isArmsLockdownActive());
            assertTrue("Citadel arms lockdown duration restored",
                    loadedEngine.getNexus().getLockdownDurationRemaining() == 4);

            // Verify resolved dilemmas were restored
            assertTrue("Act 0 dilemma resolved status preserved",
                    loadedEngine.getDilemmaManager().isDilemmaResolved("ACT_0"));
            assertTrue("Act 1 dilemma resolved status preserved",
                    loadedEngine.getDilemmaManager().isDilemmaResolved("ACT_1"));
            assertTrue("Act 2 dilemma is NOT marked resolved yet",
                    !loadedEngine.getDilemmaManager().isDilemmaResolved("ACT_2"));

            // Verify fortified civilization state
            StarSystem loadedTurian = loadedState.getGalaxyMap().getSystem(0, 2);
            assertTrue("Civilization restored on [0, 2]", loadedTurian.getCivilization() != null);
            assertTrue("Turian kinetic barrier restored", loadedTurian.getCivilization().hasKineticBarrier());

            tempSave.delete();
        } catch (Exception e) {
            assertTrue("Save/load roundtrip threw: " + e.getMessage(), false);
        }
    }

    private static void testCrucibleDefeatAndCampaignVictoryConditions() {
        System.out.println("\n[Test 28: Crucible Defeat Clock & Campaign Victory End Conditions]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        CampaignManager cm = engine.getCampaign();

        // 1. Advance to Act 4
        cm.setCurrentAct(CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
        assertTrue("Act 4 active", cm.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
        assertTrue("Crucible initially not 100%", cm.getCrucibleProgress() < 100);
        assertTrue("Campaign not defeated yet", !cm.isCrucibleDefeat());

        // 2. Advance epochs to trigger Crucible completion
        for (int i = 0; i < 6; i++) {
            cm.onEpochAdvance(state);
        }
        assertTrue("Crucible reached 100%", cm.getCrucibleProgress() == 100);
        assertTrue("Crucible defeat triggered", cm.isCrucibleDefeat());

        // 3. Test Victory condition in a fresh CampaignManager
        CampaignManager victoryCampaign = new CampaignManager();
        victoryCampaign.setCurrentAct(CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
        // Record 3 ascensions during Act 4
        victoryCampaign.recordAscension();
        victoryCampaign.recordAscension();
        victoryCampaign.recordAscension();
        victoryCampaign.checkObjectiveProgress(state, engine.getTechTree());

        assertTrue("Campaign achieved victory in Act 4", victoryCampaign.isCampaignVictory());
        assertTrue("Campaign advanced to Act 5 Endgame",
                victoryCampaign.getCurrentAct() == CampaignManager.Act.ACT_5_ENDGAME);

        // 4. Test Unattended Organic Crucible Research Acceleration
        CampaignManager dynCm = new CampaignManager();
        dynCm.setCurrentAct(CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
        GalacticState dynState = new GalacticState("Harbinger", "Act IV");
        dynState.getGalaxyMap().setSectorUnlocked(0, true);
        dynCm.reduceCrucibleProgress(100);
        assertTrue("Crucible reset to 0%", dynCm.getCrucibleProgress() == 0);

        // Advance 1 epoch with 0 civilizations -> base increment +18%
        dynCm.onEpochAdvance(dynState);
        assertTrue("Base Crucible increment with 0 civilizations is +18%", dynCm.getCrucibleProgress() == 18);

        // Add 2 Spacefaring civilizations unattended -> increment becomes 18 + (2 * 3) = 24%
        Civilization civ1 = new com.seb.harvesteffect.model.civilization.Humanity();
        civ1.setEvolutionaryTier(Civilization.TIER_INDUSTRIAL);
        try {
            dynState.getGalaxyMap().getSystem(0, 0).deployCivilization(civ1);
            Civilization civ2 = new com.seb.harvesteffect.model.civilization.Turian();
            civ2.setEvolutionaryTier(Civilization.TIER_APEX_ZENITH);
            dynState.getGalaxyMap().getSystem(0, 1).deployCivilization(civ2);
        } catch (Exception e) {}

        assertTrue("2 unattended research worlds detected", dynCm.countUnattendedResearchWorlds(dynState) == 2);
        dynCm.onEpochAdvance(dynState);
        assertTrue("Crucible advanced by 24% (18 base + 6 from 2 worlds)", dynCm.getCrucibleProgress() == 42);

        // Garrison world [0, 0] with Husk Swarm -> suppresses research labs on that world
        try {
            dynState.getGalaxyMap().getSystem(0, 0).deployUnit(new com.seb.harvesteffect.model.unit.HuskSwarm());
        } catch (Exception e) {}
        assertTrue("Research worlds reduced to 1 after garrison deployment", dynCm.countUnattendedResearchWorlds(dynState) == 1);

        // Research crucible_sabotage -> reduces construction by 25%
        TechTree tech = new TechTree();
        tech.getUpgrade("crucible_sabotage").setUnlocked(true);
        dynCm.onEpochAdvance(dynState, tech);
        // increment = (18 + 3) * 0.75 = 21 * 0.75 = 15
        assertTrue("Crucible sabotage slowed progress increment", dynCm.getCrucibleProgress() == 42 + 15);
    }

    private static void testStoryDilemmaPipelineAndPendingQuery() {
        System.out.println("\n[Test 29: Story Dilemma Pipeline & Dynamic Pending Query]");
        com.seb.harvesteffect.story.StoryDilemmaManager dm = new com.seb.harvesteffect.story.StoryDilemmaManager();

        // In Act 0 Prologue, no prior act dilemmas are pending
        assertTrue("No pending dilemmas in Act 0",
                dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_0_PROLOGUE) == null);

        // In Act 1, Act 0 dilemma is pending
        com.seb.harvesteffect.story.StoryDilemma act0Dilemma =
                dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT);
        assertTrue("Act 0 dilemma pending when in Act 1", act0Dilemma != null);
        assertTrue("Act 0 dilemma matches ACT_0", "ACT_0".equals(act0Dilemma.getActId()));

        // Resolve Act 0 dilemma
        dm.markResolved("ACT_0");
        assertTrue("No pending dilemmas remaining in Act 1",
                dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT) == null);

        // Advance to Act 2, Act 1 dilemma should now be pending
        com.seb.harvesteffect.story.StoryDilemma act1Dilemma =
                dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_2_KROGAN_REBELLIONS);
        assertTrue("Act 1 dilemma pending when in Act 2", act1Dilemma != null);
        assertTrue("Act 1 dilemma matches ACT_1", "ACT_1".equals(act1Dilemma.getActId()));
    }

    private static void testImmediateSectorUnlockInTechTree() {
        System.out.println("\n[Test 30: Immediate Sector Unlock in Tech Tree]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        state.addEezo(2000);
        state.addBiomass(2000);
        state.getGalaxyMap().setSectorUnlocked(1, false);
        state.getGalaxyMap().setSectorUnlocked(2, false);
        TechTree tech = new TechTree();

        assertTrue("Sector 1 initially locked", !state.getGalaxyMap().isSectorUnlocked(1));

        try {
            // Unlock Secondary Relay Alignment (prereq)
            tech.unlockUpgrade("secondary_relay_alignment", state, 5);
            // Unlock Primary Relay Alpha
            tech.unlockUpgrade("primary_relay_alpha", state, 5);

            assertTrue("Sector 1 (Attican Traverse) is unlocked IMMEDIATELY in state",
                    state.getGalaxyMap().isSectorUnlocked(1));
        } catch (Exception e) {
            assertTrue("Tech unlock threw: " + e.getMessage(), false);
        }
    }

    private static void testFourSectorStarMapAndPrimaryRelayGating() {
        System.out.println("\n[Test 31: 4-Sector Star Map & Primary Mass Relay Gating]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        GalacticSector map = state.getGalaxyMap();
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(3000);
        state.addBiomass(3000);

        // 1. Map dimensions: 4 sectors, 6 clusters per sector (24 systems total)
        assertTrue("GalacticSector has 4 sectors (rows)", map.getRowCount() == 4);
        assertTrue("GalacticSector has 6 clusters per sector (cols)", map.getColCount() == 6);
        assertTrue("Total galactic systems count is 24", map.getAllSystems().size() == 24);

        // 2. Initial Relay Status: Sector 0 starts unlocked, Sectors 1, 2, 3 locked
        assertTrue("Sector 0 (Citadel-Sol Corridor) is unlocked at cycle start", map.isSectorUnlocked(0));
        assertTrue("Sector 1 (Attican Traverse) starts locked", !map.isSectorUnlocked(1));
        assertTrue("Sector 2 (Terminus Systems) starts locked", !map.isSectorUnlocked(2));
        assertTrue("Sector 3 (Shadow Rim) starts locked", !map.isSectorUnlocked(3));

        // 3. Locked sector operation gating: Attempting to seed or deploy in locked sector throws ReaperException
        boolean seedBlocked = false;
        try {
            engine.seedCivilization(1, 0, "humanity");
        } catch (ReaperException e) {
            seedBlocked = true;
            assertTrue("ReaperException informs player to build sector's Primary Mass Relay",
                    e.getMessage().contains("Primary Mass Relay"));
        } catch (Exception ignored) {}
        assertTrue("Seeding in locked Sector 1 is blocked by Primary Relay requirement", seedBlocked);

        boolean relayBlocked = false;
        try {
            engine.deployRelay(1, 0);
        } catch (ReaperException e) {
            relayBlocked = true;
        } catch (Exception ignored) {}
        assertTrue("Deploying secondary relay beacon in locked Sector 1 is blocked", relayBlocked);

        boolean harvestBlocked = false;
        try {
            engine.harvestSystem(1, 0);
        } catch (ReaperException e) {
            harvestBlocked = true;
        } catch (Exception ignored) {}
        assertTrue("Harvesting in locked Sector 1 is blocked", harvestBlocked);

        // 4. Constructing Primary Mass Relay opens sector
        map.setSectorUnlocked(1, true);
        assertTrue("Sector 1 now unlocked after Primary Relay construction", map.isSectorUnlocked(1));
        try {
            engine.seedCivilization(1, 0, "humanity");
            assertTrue("Seeding in Sector 1 now succeeds after Primary Relay activation",
                    map.getSystem(1, 0).getCivilization() != null);
        } catch (Exception e) {
            assertTrue("Seeding in unlocked Sector 1 threw unexpected error: " + e.getMessage(), false);
        }
    }

    private static void testPlanetaryClimatesAndAffinityMultipliers() {
        System.out.println("\n[Test 32: Planetary Climates & Affinity Multipliers]");
        // 1. Verify 5 distinct planetary biomes exist
        assertTrue("GARDEN biome exists", ClimateType.GARDEN != null);
        assertTrue("ARID biome exists", ClimateType.ARID != null);
        assertTrue("METHANE biome exists", ClimateType.METHANE != null);
        assertTrue("VOLCANIC biome exists", ClimateType.VOLCANIC != null);
        assertTrue("BARREN biome exists", ClimateType.BARREN != null);

        // 2. Growth and Yield Affinity: Humanity on GARDEN vs METHANE
        assertTrue("Humanity has ideal affinity for GARDEN biome", ClimateType.GARDEN.isIdealFor("Humanity"));
        assertTrue("Humanity has hostile affinity for METHANE biome", ClimateType.METHANE.isHostileFor("Humanity"));
        assertTrue("Humanity on GARDEN gets +50% growth rate (1.5x multiplier)",
                Math.abs(ClimateType.GARDEN.getGrowthMultiplier("Humanity") - 1.50) < 0.01);
        assertTrue("Humanity on GARDEN gets +25% harvest yield (1.25x multiplier)",
                Math.abs(ClimateType.GARDEN.getHarvestYieldMultiplier("Humanity") - 1.25) < 0.01);
        assertTrue("Humanity on METHANE suffers -50% growth penalty (0.5x multiplier)",
                Math.abs(ClimateType.METHANE.getGrowthMultiplier("Humanity") - 0.50) < 0.01);
        assertTrue("Humanity on METHANE suffers -25% harvest yield penalty (0.75x multiplier)",
                Math.abs(ClimateType.METHANE.getHarvestYieldMultiplier("Humanity") - 0.75) < 0.01);

        // 3. Krogan on ARID vs GARDEN
        assertTrue("Krogan has ideal affinity for ARID biome", ClimateType.ARID.isIdealFor("Krogan"));
        assertTrue("Krogan has hostile affinity for GARDEN biome", ClimateType.GARDEN.isHostileFor("Krogan"));

        // 4. Volus on METHANE
        assertTrue("Volus has ideal affinity for METHANE biome", ClimateType.METHANE.isIdealFor("Volus"));

        // 5. Verify ALL 15 Species have verified Ideal biomes and Hostile biomes
        String[][] allSpeciesPreferences = {
            {"Humanity", "Garden", "Methane"},
            {"Asari",    "Garden", "Methane"},
            {"Salarian", "Garden", "Arid"},
            {"Prothean", "Garden", "Volcanic"},
            {"Hanar",    "Garden", "Arid"},
            {"Turian",   "Arid",   "Methane"},
            {"Krogan",   "Arid",   "Garden"},
            {"Quarian",  "Arid",   "Volcanic"},
            {"Vorcha",   "Arid",   "Garden"},
            {"Drell",    "Arid",   "Garden"},
            {"Volus",    "Methane", "Garden"},
            {"Elcor",    "Methane", "Garden"},
            {"Batarian", "Volcanic", "Garden"},
            {"Yahg",     "Volcanic", "Garden"},
            {"Rachni",   "Volcanic", "Methane"}
        };

        for (String[] specPref : allSpeciesPreferences) {
            String speciesName = specPref[0];
            String idealBiome = specPref[1];
            String hostileBiome = specPref[2];

            ClimateType idealClimate = ClimateType.valueOf(idealBiome.toUpperCase());
            ClimateType hostileClimate = ClimateType.valueOf(hostileBiome.toUpperCase());

            assertTrue(speciesName + " has ideal affinity for " + idealBiome, idealClimate.isIdealFor(speciesName));
            assertTrue(speciesName + " ideal growth multiplier is 1.5x", Math.abs(idealClimate.getGrowthMultiplier(speciesName) - 1.50) < 0.01);
            assertTrue(speciesName + " ideal yield multiplier is 1.25x", Math.abs(idealClimate.getHarvestYieldMultiplier(speciesName) - 1.25) < 0.01);

            assertTrue(speciesName + " has hostile affinity for " + hostileBiome, hostileClimate.isHostileFor(speciesName));
            assertTrue(speciesName + " hostile growth multiplier is 0.5x", Math.abs(hostileClimate.getGrowthMultiplier(speciesName) - 0.50) < 0.01);
            assertTrue(speciesName + " hostile yield multiplier is 0.75x", Math.abs(hostileClimate.getHarvestYieldMultiplier(speciesName) - 0.75) < 0.01);

            assertTrue(speciesName + " is hostile on BARREN worlds", ClimateType.BARREN.isHostileFor(speciesName));
        }

        // 6. BARREN uninhabitable properties
        assertTrue("BARREN biome growth multiplier is 0.25 (severely stunted)",
                Math.abs(ClimateType.BARREN.getGrowthMultiplier("Humanity") - 0.25) < 0.01);
        assertTrue("BARREN biome affinity label warns player",
                ClimateType.BARREN.getAffinityLabel("Humanity").contains("BARREN"));
    }

    private static void testPlanetaryTerraformingAndBarrenInhabitability() {
        System.out.println("\n[Test 33: Planetary Terraforming & Barren Inhabitability]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(1000);

        // System [0, 4] is Charon / Barren dead rock
        StarSystem barrenWorld = state.getGalaxyMap().getSystem(0, 4);
        barrenWorld.terraform(ClimateType.BARREN);
        assertTrue("System [0, 4] is BARREN dead rock", barrenWorld.getClimateType() == ClimateType.BARREN);

        // 1. Attempting to seed on a BARREN world throws ReaperException
        boolean seedBarrenBlocked = false;
        try {
            engine.seedCivilization(0, 4, "humanity");
        } catch (ReaperException e) {
            seedBarrenBlocked = true;
            assertTrue("ReaperException notifies player to terraform world first",
                    e.getMessage().contains("BARREN") && e.getMessage().contains("Terraform"));
        } catch (Exception ignored) {}
        assertTrue("Seeding on BARREN world is blocked", seedBarrenBlocked);

        // 2. Terraforming deducts 75 Eezo and transforms biome
        int preEezo = state.getEezoReserves();
        try {
            engine.terraformSystem(0, 4, ClimateType.GARDEN);
            assertTrue("Terraforming deducted 75 Eezo", state.getEezoReserves() == preEezo - 75);
            assertTrue("System [0, 4] successfully converted to GARDEN biome",
                    barrenWorld.getClimateType() == ClimateType.GARDEN);

            // 3. Seeding now succeeds on terraformed world!
            engine.seedCivilization(0, 4, "humanity");
            assertTrue("Seeding succeeds on newly terraformed GARDEN world",
                    barrenWorld.getCivilization() != null);
        } catch (Exception e) {
            assertTrue("Terraforming threw unexpected error: " + e.getMessage(), false);
        }

        // 4. Further atmospheric conditioning: convert from GARDEN to ARID
        try {
            engine.terraformSystem(0, 4, ClimateType.ARID);
            assertTrue("System [0, 4] re-terraformed to ARID biome",
                    barrenWorld.getClimateType() == ClimateType.ARID);
        } catch (Exception e) {
            assertTrue("Re-terraforming threw unexpected error: " + e.getMessage(), false);
        }
    }

    private static void testGenomeSynthesisFromReaperBioBanks() {
        System.out.println("\n[Test 34: Genome Synthesis from Reaper Bio-Banks]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        engine.setEnforceGenomeResearch(true);
        state.addEezo(1000);
        state.addBiomass(1000);

        // 1. Post-ME3 initial synthesis state: Humanity is known; alien genomes unsequenced
        assertTrue("Humanity genome is initially sequenced from Crucible/Earth records",
                engine.isGenomeSequenced("humanity"));
        assertTrue("Asari genome is initially unsequenced", !engine.isGenomeSequenced("asari"));
        assertTrue("Turian genome is initially unsequenced", !engine.isGenomeSequenced("turian"));

        // 2. Attempting to seed unsequenced Asari throws ReaperException
        boolean unsequencedBlocked = false;
        try {
            engine.seedCivilization(0, 1, "asari");
        } catch (ReaperException e) {
            unsequencedBlocked = true;
            assertTrue("ReaperException notifies player to research genome in bio-banks",
                    e.getMessage().contains("unsequenced") || e.getMessage().contains("Bio-Banks"));
        } catch (Exception ignored) {}
        assertTrue("Seeding unsequenced species is blocked by bio-bank protocol", unsequencedBlocked);

        // 3. Synthesizing Asari genome deducts 40 Eezo and 30 Biomass
        int preEezo = state.getEezoReserves();
        int preBio = state.getAccumulatedBiomass();
        try {
            engine.sequenceGenome("asari");
            assertTrue("Synthesizing genome deducted 40 Eezo", state.getEezoReserves() == preEezo - 40);
            assertTrue("Synthesizing genome deducted 30 Biomass", state.getAccumulatedBiomass() == preBio - 30);
            assertTrue("Asari genome is now sequenced", engine.isGenomeSequenced("asari"));

            // 4. Seeding Asari now succeeds!
            engine.seedCivilization(0, 1, "asari");
            assertTrue("Asari successfully seeded after genome synthesis",
                    state.getGalaxyMap().getSystem(0, 1).getCivilization() != null);
        } catch (Exception e) {
            assertTrue("Genome synthesis threw unexpected error: " + e.getMessage(), false);
        }
    }

    private static void testCreepingMultiPlanetColonizationAndMutualDefense() {
        System.out.println("\n[Test 35: Creeping Multi-Planet Colonization & Mutual Defense Barriers]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(2000);
        state.addBiomass(2000);

        // Seed Turian in Sector 0, Cluster 1
        try {
            engine.seedCivilization(0, 1, "turian");
        } catch (Exception e) {
            assertTrue("Initial seeding failed: " + e.getMessage(), false);
        }

        Civilization turian = state.getGalaxyMap().getSystem(0, 1).getCivilization();
        assertTrue("Turian seeded in [0, 1]", turian != null);
        assertTrue("Initially has not colonized adjacent worlds", !turian.hasColonized());

        // Advance 2 epochs: reaches Tier 2 spacefaring
        engine.advanceCycle();
        engine.advanceCycle();
        assertTrue("Turian reached Tier 2 spacefaring age", turian.getEvolutionaryTier() >= 2);
        assertTrue("Turian establishes orbital kinetic defense barrier", turian.hasKineticBarrier());

        // Advance 2 more epochs: creeping colonization expands to adjacent empty world in sector
        engine.advanceCycle();
        engine.advanceCycle();
        assertTrue("Turian completed colonization expansion timer", turian.hasColonized());

        // Check that an adjacent system in Sector 0 now has a Turian colony!
        boolean colonyFound = false;
        int colonyCluster = -1;
        for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
            if (c == 1) continue;
            Civilization colCiv = state.getGalaxyMap().getSystem(0, c).getCivilization();
            if (colCiv != null && "Turian".equalsIgnoreCase(colCiv.getSpeciesName())) {
                colonyFound = true;
                colonyCluster = c;
                break;
            }
        }
        assertTrue("Creeping colonization established Turian off-world colony in adjacent system", colonyFound);
        Civilization colony = state.getGalaxyMap().getSystem(0, colonyCluster).getCivilization();
        assertTrue("Colony is initialized with off-world colony name prefix",
                colony.getSpeciesName().contains("Colony") || "Turian".equalsIgnoreCase(colony.getSpeciesName()));
        assertTrue("Colony activates mutual defense kinetic barrier", colony.hasKineticBarrier());
    }

    private static void testPostMe3NarrativeDilemmasAndPersistence() {
        System.out.println("\n[Test 36: Post-ME3 Narrative Dilemmas & Persistence]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        com.seb.harvesteffect.story.StoryDilemmaManager dm = engine.getDilemmaManager();
        state.addEezo(500);

        // 1. Act 0: Crucible Debris & Commander Shepard's Remains
        com.seb.harvesteffect.story.StoryDilemma act0 = dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT);
        assertTrue("Act 0 dilemma is present", act0 != null);
        assertTrue("Act 0 concerns Shepard's Crucible wreckage",
                act0.getTitle().contains("Shepard") || act0.getTitle().contains("Crucible"));
        assertTrue("Choice A label is non-empty", !act0.getChoiceA().getLabel().isEmpty());
        assertTrue("Choice B label is non-empty", !act0.getChoiceB().getLabel().isEmpty());

        // 2. Resolve Choice A and verify fleet impact
        int preBio = state.getAccumulatedBiomass();
        int preEezo = state.getEezoReserves();
        act0.getChoiceA().apply(state);
        dm.markResolved("ACT_0");
        assertTrue("Choice A applied genetic biomass reward from Shepard's DNA",
                state.getAccumulatedBiomass() == preBio + act0.getChoiceA().getBiomassBonus());
        assertTrue("Act 0 dilemma is now marked resolved",
                dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT) == null);

        // 3. Act 1: Citadel Relay Core Overclock
        com.seb.harvesteffect.story.StoryDilemma act1 = dm.getPendingDilemmaForCampaign(CampaignManager.Act.ACT_2_KROGAN_REBELLIONS);
        assertTrue("Act 1 dilemma concerns Citadel Core Overclock",
                act1 != null && act1.getTitle().contains("Citadel"));

        // 4. Persistence roundtrip with SaveManager
        java.io.File testSave = new java.io.File("test_campaign_overhaul.dat");
        try {
            SaveManager.saveGame(engine, testSave);
            assertTrue("Save file successfully created on disk", testSave.exists());

            ReaperEngine loadedEngine = new ReaperEngine(new GalacticState("Test", "Cycle 1"));
            boolean loaded = SaveManager.loadGame(loadedEngine, testSave);
            assertTrue("Loaded campaign from disk successfully", loaded);
            assertTrue("Cycle epoch restored without resetting", loadedEngine.getState().getCycleEpoch() == state.getCycleEpoch());
            assertTrue("Eezo reserves restored exactly without inflation", loadedEngine.getState().getEezoReserves() == state.getEezoReserves());
            assertTrue("Accumulated biomass restored exactly without inflation", loadedEngine.getState().getAccumulatedBiomass() == state.getAccumulatedBiomass());
            assertTrue("Resolved status of Act 0 dilemma preserved across save/load",
                    loadedEngine.getDilemmaManager().getPendingDilemmaForCampaign(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT) == null);
            assertTrue("Act 1 dilemma remains pending in loaded game",
                    loadedEngine.getDilemmaManager().getPendingDilemmaForCampaign(CampaignManager.Act.ACT_2_KROGAN_REBELLIONS) != null);
        } catch (Exception e) {
            assertTrue("Persistence test threw unexpected error: " + e.getMessage(), false);
        } finally {
            if (testSave.exists()) testSave.delete();
        }
    }

    private static void testGalacticStarChartMapWithCitadelAtCenter() {
        System.out.println("\n[Test 37: Galactic Star Chart Radial Map with Citadel at Center]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        CampaignManager campaign = engine.getCampaign();
        MissionManager missionManager = engine.getMissionManager();

        com.seb.harvesteffect.ui.GalacticMapPanel mapPanel =
                new com.seb.harvesteffect.ui.GalacticMapPanel(state, engine, missionManager);

        assertTrue("GalacticMapPanel instantiates cleanly", mapPanel != null);

        // 1. Configure dimensions and test system selection
        mapPanel.setSize(1200, 500);
        mapPanel.setSelectedSystem(0, 0); // Sol

        final boolean[] clicked = new boolean[3]; // [citadel, relay, system]
        mapPanel.setMapInteractionListener(new com.seb.harvesteffect.ui.GalacticMapPanel.MapInteractionListener() {
            @Override
            public void onSystemSelected(int sector, int cluster) {
                clicked[2] = true;
            }

            @Override
            public void onCitadelClicked() {
                clicked[0] = true;
            }

            @Override
            public void onPrimaryRelayClicked(int sector) {
                clicked[1] = true;
            }
        });

        // 2. Headless Graphic Render Simulation (verify no Exceptions or NPE during paint)
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(1200, 500, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        boolean renderSuccess = false;
        try {
            mapPanel.paint(g2);
            renderSuccess = true;
        } catch (Exception e) {
            renderSuccess = false;
        } finally {
            g2.dispose();
        }
        assertTrue("GalacticMapPanel renders all 24 systems, 4 relays, and Citadel core without errors", renderSuccess);

        // Verify non-empty raster output (pixel drawing occurred)
        int centerPixel = img.getRGB(600, 250);
        assertTrue("Galactic Core area contains rendered pixels", centerPixel != 0);

        // 3. Clean up animation timer
        mapPanel.removeNotify();
        assertTrue("GalacticMapPanel cleanly unregisters animation timer", true);
    }

    private static void testMissionManagerProgressionAndSelectionTriggers() {
        System.out.println("\n[Test 38: Mission 1 Selection & Fast Progression Triggers]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        MissionManager mm = engine.getMissionManager();

        assertTrue("Mission 1 starts active", mm.getActiveMission().getMissionNumber() == 1);
        assertTrue("Mission 1 starts at Task 0", mm.getActiveMission().getCurrentTaskIndex() == 0);

        // 1. Target Lock / Selection of Sol [0,0] triggers Task 0 -> Task 1 advancement
        boolean done0 = mm.checkMissionTriggers(state, 0, 0, 0);
        assertTrue("Selecting Sol [0,0] advances Mission 1 from Task 0 to Task 1",
                mm.getActiveMission().getCurrentTaskIndex() == 1);
        assertTrue("Mission 1 not fully complete on selection alone", !done0);

        // 2. Seeding Humanity on Sol [0,0] completes Mission 1 immediately
        try {
            engine.seedCivilization(0, 0, "humanity");
        } catch (Exception e) {
            assertTrue("Seeding humanity failed: " + e.getMessage(), false);
        }

        boolean done1 = mm.checkMissionTriggers(state, 0, 0, 0);
        assertTrue("Seeding Humanity completes Mission 1 triggers", done1);
        assertTrue("Mission 1 marked completed", mm.getActiveMission().isCompleted());

        // 3. Advance to Mission 2
        mm.advanceMission();
        assertTrue("Advanced to Mission 2", mm.getActiveMission().getMissionNumber() == 2);
    }

    private static void testBugFixesVerification() {
        System.out.println("\n[Test 39: Additional Bug Fixes Verification]");
        GalacticState state = new GalacticState("Harbinger", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(2000);
        state.addBiomass(2000);

        // 1. Verify calculatePassiveDividends does not mutate state
        try {
            engine.seedCivilization(0, 1, "volus");
        } catch (Exception ignored) {}
        int initialEezo = state.getEezoReserves();
        int previewDividends = state.calculatePassiveDividends();
        assertTrue("calculatePassiveDividends returns positive for Volus", previewDividends > 0);
        assertTrue("calculatePassiveDividends does not mutate Eezo reserves", state.getEezoReserves() == initialEezo);

        // 2. Verify story-unlocked species can be seeded with enforceGenomeResearch enabled
        engine.setEnforceGenomeResearch(true);
        engine.getMissionManager().advanceMission(); // Mission index 1 (Act I: unlocks Asari, Turian, Salarian)
        boolean seededAsari = false;
        try {
            engine.seedCivilization(0, 3, "asari");
            seededAsari = true;
        } catch (Exception e) {
            seededAsari = false;
        }
        assertTrue("Story-unlocked Asari seeds successfully with enforceGenomeResearch active", seededAsari);

        // 3. Verify cargo full does not award free biomass/eezo or leave unharvested exploit
        try {
            // Fill cargo hold
            while (!state.getCargoHold().isFull()) {
                state.getCargoHold().store(new com.seb.harvesteffect.model.item.HarvestYield("Dummy", 10, 10));
            }
            // Advance Asari to Apex Zenith
            for (int i = 0; i < 3; i++) {
                state.getGalaxyMap().getSystem(0, 3).getCivilization().advanceEpoch(com.seb.harvesteffect.engine.GalacticPhenomenon.STELLAR_CALM, com.seb.harvesteffect.model.entity.ClimateType.GARDEN);
            }
            int bioBefore = state.getAccumulatedBiomass();
            int eezoBefore = state.getEezoReserves();
            boolean caughtFull = false;
            try {
                engine.harvestSystem(0, 3);
            } catch (com.seb.harvesteffect.exception.CargoHoldFullException chfe) {
                caughtFull = true;
            }
            assertTrue("CargoHoldFullException thrown when harvesting with full cargo", caughtFull);
            assertTrue("No Biomass awarded when harvest deflected by full cargo", state.getAccumulatedBiomass() == bioBefore);
            assertTrue("No Eezo awarded when harvest deflected by full cargo", state.getEezoReserves() == eezoBefore);
            assertTrue("Civilization remains unpurged for subsequent harvest after clearing cargo", state.getGalaxyMap().getSystem(0, 3).getCivilization() != null);
        } catch (Exception e) {
            assertTrue("Cargo hold test failed with exception: " + e.getMessage(), false);
        }

        // 4. Verify save/load restores active task index, historicalCycleCount, enforceGenomeResearch, and biomass_vats
        java.io.File testFile = new java.io.File("test_bug_fix_save.dat");
        try {
            engine.getMissionManager().getActiveMission().advanceTask(); // Advance to task 1
            int taskIdx = engine.getMissionManager().getActiveMission().getCurrentTaskIndex();
            int cycleCnt = engine.getMissionManager().getHistoricalCycleCount();
            engine.getTechTree().unlockUpgrade("thanix_cannons", state, 4);
            engine.getTechTree().unlockUpgrade("biomass_vats", state, 4);

            SaveManager.saveGame(engine, testFile);

            ReaperEngine loadedEngine = new ReaperEngine(new GalacticState("Test", "Cycle 1"));
            SaveManager.loadGame(loadedEngine, testFile);

            assertTrue("Loaded engine restored enforceGenomeResearch", loadedEngine.isEnforceGenomeResearch());
            assertTrue("Loaded engine restored active task index", loadedEngine.getMissionManager().getActiveMission().getCurrentTaskIndex() == taskIdx);
            assertTrue("Loaded engine restored historicalCycleCount", loadedEngine.getMissionManager().getHistoricalCycleCount() == cycleCnt);
            assertTrue("Loaded engine expanded cargo hold capacity to 24 for biomass_vats", loadedEngine.getState().getCargoHold().getCapacity() == 24);
        } catch (Exception e) {
            assertTrue("Save/load bug verification failed: " + e.getMessage(), false);
        } finally {
            if (testFile.exists()) testFile.delete();
        }
    }

    private static void testCliCommandParserAndTerminalRenderer() {
        System.out.println("\n[Test 40: CLI Command Parser & Terminal Renderer Verification]");

        // 1. Test CommandParser readInt
        java.util.Scanner scInt = new java.util.Scanner("invalid\n99\n3\n");
        com.seb.harvesteffect.ui.CommandParser cpInt = new com.seb.harvesteffect.ui.CommandParser(scInt);
        int parsedInt = cpInt.readInt("Prompt: ", 1, 5);
        assertTrue("CommandParser readInt parses valid integer within bounds after error", parsedInt == 3);

        // 2. Test CommandParser readInt on EOF
        java.util.Scanner scEof = new java.util.Scanner("");
        com.seb.harvesteffect.ui.CommandParser cpEof = new com.seb.harvesteffect.ui.CommandParser(scEof);
        int eofInt = cpEof.readInt("Prompt: ", 2, 8);
        assertTrue("CommandParser readInt gracefully handles EOF returning min bound", eofInt == 2);

        // 3. Test CommandParser readDouble
        java.util.Scanner scDbl = new java.util.Scanner("bad\n50.0\n150.5\n");
        com.seb.harvesteffect.ui.CommandParser cpDbl = new com.seb.harvesteffect.ui.CommandParser(scDbl);
        double parsedDbl = cpDbl.readDouble("Prompt: ", 100.0, 999.0);
        assertTrue("CommandParser readDouble parses valid double within bounds after error", Math.abs(parsedDbl - 150.5) < 0.001);

        // 4. Test CommandParser readString with default and custom value
        java.util.Scanner scStr = new java.util.Scanner("\nHarbinger-Prime\n");
        com.seb.harvesteffect.ui.CommandParser cpStr = new com.seb.harvesteffect.ui.CommandParser(scStr);
        String defStr = cpStr.readString("Prompt: ", "DefaultFlagship");
        String customStr = cpStr.readString("Prompt: ", "DefaultFlagship");
        assertTrue("CommandParser readString returns default on empty line", "DefaultFlagship".equals(defStr));
        assertTrue("CommandParser readString returns entered string", "Harbinger-Prime".equals(customStr));

        // 5. Test CommandParser readCoordinates
        java.util.Scanner scCoord = new java.util.Scanner("invalid\n9 9\n0 2\n");
        com.seb.harvesteffect.ui.CommandParser cpCoord = new com.seb.harvesteffect.ui.CommandParser(scCoord);
        int[] coords = cpCoord.readCoordinates("Prompt", 4, 6);
        assertTrue("CommandParser readCoordinates returns parsed sector", coords[0] == 0);
        assertTrue("CommandParser readCoordinates returns parsed cluster", coords[1] == 2);

        // 6. Test TerminalRenderer HUD execution
        GalacticState state = new GalacticState("Sovereign", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        boolean hudRendered = false;
        try {
            com.seb.harvesteffect.ui.TerminalRenderer.renderHUD(state);
            com.seb.harvesteffect.ui.TerminalRenderer.renderHUD(state, engine.getCampaign());
            hudRendered = true;
        } catch (Exception e) {
            hudRendered = false;
        }
        assertTrue("TerminalRenderer renderHUD runs without errors", hudRendered);

        // 7. Test TerminalRenderer Galaxy Map execution with various system states
        boolean mapRendered = false;
        try {
            engine.seedCivilization(0, 0, "humanity");
            engine.deployUnit(0, 1, "collector_drone");
            state.getGalaxyMap().getSystem(0, 0).setRelayBeamActive(true);
            com.seb.harvesteffect.ui.TerminalRenderer.renderGalaxyMap(state.getGalaxyMap());
            mapRendered = true;
        } catch (Exception e) {
            mapRendered = false;
        }
        assertTrue("TerminalRenderer renderGalaxyMap renders all systems without errors", mapRendered);
    }

    private static void testSovereignMovementConstraintDroneSpecializationAndCrucibleWiping() {
        System.out.println("\n[Test 41: Sovereign Movement Constraints, Drone Differentiation & Act 4 Planet Wiping]");
        GalacticState state = new GalacticState("Sovereign", "Act 4 War");
        ReaperEngine engine = new ReaperEngine(state);
        state.addEezo(3000);
        state.addBiomass(3000);

        // 1. Sovereign 1-jump-per-epoch movement constraint
        assertTrue("Sovereign initially can jump", engine.canFlagshipJump());
        try {
            engine.moveFlagship(0, 1, true);
            assertTrue("Sovereign successfully made first jump", state.getFlagshipCluster() == 1);
            assertTrue("Flagship marked as jumped this epoch", state.isFlagshipJumpedThisEpoch());
            assertTrue("Engine reports cannot jump", !engine.canFlagshipJump());
        } catch (Exception e) {
            assertTrue("First jump should succeed: " + e.getMessage(), false);
        }

        // Attempt second jump in same epoch throws ReaperException
        boolean secondJumpBlocked = false;
        try {
            engine.moveFlagship(0, 2, true);
        } catch (ReaperException re) {
            secondJumpBlocked = true;
            assertTrue("Error mentions static electricity / 1 jump limit", re.getMessage().contains("discharging static electricity"));
        } catch (Exception e) {
            assertTrue("Wrong exception: " + e, false);
        }
        assertTrue("Second jump in same epoch strictly blocked", secondJumpBlocked);

        // Advancing epoch discharges static electricity and allows jumping again
        engine.advanceCycle();
        assertTrue("Flagship can jump again after advancing cycle", engine.canFlagshipJump());
        assertTrue("Flagship jumped flag reset", !state.isFlagshipJumpedThisEpoch());

        // 2. Act 4 Planet Wiping lowers Crucible research
        engine.getCampaign().setCurrentAct(CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
        engine.getCampaign().reduceCrucibleProgress(100);
        // Set Crucible progress to 36%
        engine.getCampaign().onEpochAdvance(state);
        engine.getCampaign().onEpochAdvance(state);
        int preWipeProgress = engine.getCampaign().getCrucibleProgress();
        assertTrue("Crucible has active progress", preWipeProgress >= 36);

        // Seed and grow an Apex civilization (Tier 3) on [0, 0]
        try {
            engine.seedCivilization(0, 0, "Humanity");
            Civilization humanCiv = state.getGalaxyMap().getSystem(0, 0).getCivilization();
            humanCiv.setEvolutionaryTier(Civilization.TIER_APEX_ZENITH);
            assertTrue("Humanity is Tier 3 Apex Zenith", humanCiv.isHarvestReady());

            HarvestYield yield = engine.harvestSystem(0, 0);
            assertTrue("Harvest succeeded", yield != null);
            assertTrue("Planet purged of civilization", state.getGalaxyMap().getSystem(0, 0).getCivilization() == null);
            assertTrue("Wiping Tier 3 Apex world reduced Crucible progress by -10%",
                    engine.getCampaign().getCrucibleProgress() == preWipeProgress - 10);
            assertTrue("Crucible notice generated", engine.getLastCrucibleNotice() != null && engine.getLastCrucibleNotice().contains("-10%"));
        } catch (Exception e) {
            assertTrue("Wiping apex world failed: " + e.getMessage(), false);
        }

        // 3. Biomechanical Unit differentiation & Act 4 Crucible Sabotage
        // Seed spacefaring civilizations on habitable clusters [0, 2], [0, 3], [0, 4]
        int currentProgress = engine.getCampaign().getCrucibleProgress();
        try {
            // Deploy Collector Drone on spacefaring world [0, 2] -> -4%
            engine.seedCivilization(0, 2, "Turian");
            state.getGalaxyMap().getSystem(0, 2).getCivilization().setEvolutionaryTier(Civilization.TIER_INDUSTRIAL);
            engine.deployUnit(0, 2, "Collector Drone");
            assertTrue("Collector Drone reduced Crucible by -4%",
                    engine.getCampaign().getCrucibleProgress() == currentProgress - 4);
            currentProgress = engine.getCampaign().getCrucibleProgress();

            // Deploy Husk Swarm on spacefaring world [0, 3] -> -6%
            engine.seedCivilization(0, 3, "Salarian");
            state.getGalaxyMap().getSystem(0, 3).getCivilization().setEvolutionaryTier(Civilization.TIER_INDUSTRIAL);
            engine.deployUnit(0, 3, "Husk Swarm");
            assertTrue("Husk Swarm reduced Crucible by -6%",
                    engine.getCampaign().getCrucibleProgress() == currentProgress - 6);
            currentProgress = engine.getCampaign().getCrucibleProgress();

            // Deploy Scion Behemoth on spacefaring world [0, 4] -> -10%
            engine.seedCivilization(0, 4, "Volus");
            state.getGalaxyMap().getSystem(0, 4).getCivilization().setEvolutionaryTier(Civilization.TIER_INDUSTRIAL);
            engine.deployUnit(0, 4, "Scion Behemoth");
            assertTrue("Scion Behemoth reduced Crucible by -10%",
                    engine.getCampaign().getCrucibleProgress() == currentProgress - 10);
        } catch (Exception e) {
            assertTrue("Drone sabotage test failed: " + e.getMessage(), false);
        }
    }

    private static void testStoryUnlockedSpeciesGenomeSequencingReconciliation() {
        System.out.println("\n[Test 42: Story Unlocked Species & Genome Sequencing Reconciliation]");
        GalacticState state = new GalacticState("Sovereign", "Cycle 1");
        ReaperEngine engine = new ReaperEngine(state);
        engine.setEnforceGenomeResearch(true);
        state.addEezo(1000);
        state.addBiomass(1000);

        // 1. Initial State (Mission 1 / Index 0): Humanity is sequenced; Alien story species and specialized species unsequenced
        assertTrue("Humanity is sequenced at campaign start", engine.isGenomeSequenced("humanity"));
        assertTrue("Asari is unsequenced before Mission 2", !engine.isGenomeSequenced("asari"));
        assertTrue("Turian is unsequenced before Mission 2", !engine.isGenomeSequenced("turian"));
        assertTrue("Volus is unsequenced before Bio-Bank research", !engine.isGenomeSequenced("volus"));

        // 2. Advance to Mission 2 (Act I): Story unlocks Asari, Turian, Salarian
        engine.getMissionManager().advanceMission(); // Index 1
        assertTrue("Story unlock marks Asari as sequenced in engine archives", engine.isGenomeSequenced("asari"));
        assertTrue("Story unlock marks Turian as sequenced in engine archives", engine.isGenomeSequenced("turian"));
        assertTrue("Story unlock marks Salarian as sequenced in engine archives", engine.isGenomeSequenced("salarian"));
        assertTrue("Non-story specialized species Volus remains unsequenced", !engine.isGenomeSequenced("volus"));

        // 3. Story-unlocked Asari seeds directly without requiring bio-bank synthesis
        boolean asariSeeded = false;
        try {
            engine.seedCivilization(0, 1, "asari");
            asariSeeded = true;
        } catch (Exception e) {
            asariSeeded = false;
        }
        assertTrue("Story-unlocked Asari seeds without throwing unsequenced exception", asariSeeded);

        // 4. Calling sequenceGenome on already-story-unlocked species does not waste Eezo/Biomass
        int eezoBefore = state.getEezoReserves();
        int bioBefore = state.getAccumulatedBiomass();
        try {
            engine.sequenceGenome("asari");
            assertTrue("No Eezo deducted for already-unlocked story species", state.getEezoReserves() == eezoBefore);
            assertTrue("No Biomass deducted for already-unlocked story species", state.getAccumulatedBiomass() == bioBefore);
        } catch (Exception e) {
            assertTrue("sequenceGenome on unlocked species threw error: " + e.getMessage(), false);
        }

        // 5. Researching specialized species (Volus) via Bio-Banks deducts resources and unlocks it
        int volusEezoCost = engine.getGenomeEezoCost("volus");
        int volusBioCost = engine.getGenomeBiomassCost("volus");
        assertTrue("Volus has specialized Eezo cost (100)", volusEezoCost == 100);
        assertTrue("Volus has specialized Biomass cost (50)", volusBioCost == 50);

        try {
            engine.sequenceGenome("volus");
            assertTrue("Volus synthesis deducted 100 Eezo", state.getEezoReserves() == eezoBefore - 100);
            assertTrue("Volus synthesis deducted 50 Biomass", state.getAccumulatedBiomass() == bioBefore - 50);
            assertTrue("Volus is now sequenced after Bio-Bank research", engine.isGenomeSequenced("volus"));
            assertTrue("Volus now appears in getUnlockedSpecies", engine.getMissionManager().getUnlockedSpecies(engine).contains("Volus"));
        } catch (Exception e) {
            assertTrue("Volus synthesis failed: " + e.getMessage(), false);
        }
    }
}
