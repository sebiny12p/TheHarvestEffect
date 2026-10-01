package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.civilization.*;
import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.item.FleetComponent;
import com.seb.harvesteffect.model.item.GenesisProbe;
import com.seb.harvesteffect.model.item.HarvestYield;
import com.seb.harvesteffect.model.item.Resource;
import com.seb.harvesteffect.model.unit.CollectorDrone;
import com.seb.harvesteffect.model.unit.HuskSwarm;
import com.seb.harvesteffect.model.unit.ScionBehemoth;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core simulation controller executing player directives, managing the Campaign Story,
 * SubSpaceScanner anomalous transmissions, Citadel Mega-Structure, and multi-sector galaxy state.
 */
public class ReaperEngine implements Serializable {
    private static final long serialVersionUID = 1L;

    private final GalacticState state;
    private final CitadelNexus nexus;
    private final TechTree techTree;
    private final CampaignManager campaign;
    private final MissionManager missionManager;
    private final com.seb.harvesteffect.story.StoryDilemmaManager dilemmaManager;
    private final SubSpaceScanner scanner;
    private final Random random;
    private final java.util.Set<String> sequencedGenomes;
    private boolean enforceGenomeResearch;
    private String lastLockdownNotice;
    private String lastCrucibleNotice;

    public ReaperEngine(GalacticState state) {
        this.state = state;
        this.nexus = new CitadelNexus();
        if (this.state != null) {
            this.state.setCitadelNexus(this.nexus);
        }
        this.techTree = new TechTree();
        this.campaign = new CampaignManager();
        this.missionManager = new MissionManager();
        this.dilemmaManager = new com.seb.harvesteffect.story.StoryDilemmaManager();
        this.scanner = new SubSpaceScanner();
        this.random = new Random();
        this.sequencedGenomes = new java.util.HashSet<String>();
        this.sequencedGenomes.add("humanity"); // Humanity genome known from ME3 climax
        this.enforceGenomeResearch = false;
    }

    public boolean isGenomeSequenced(String speciesKey) {
        if (speciesKey == null) return false;
        String clean = speciesKey.toLowerCase().trim();
        if (sequencedGenomes.contains(clean)) {
            return true;
        }
        if (missionManager != null && missionManager.isSpeciesUnlocked(clean)) {
            return true;
        }
        return false;
    }

    public void sequenceGenome(String speciesKey) throws InsufficientEezoException, InsufficientBiomassException {
        if (speciesKey == null) {
            throw new IllegalArgumentException("Species key cannot be null.");
        }
        String clean = speciesKey.toLowerCase().trim();
        if (isGenomeSequenced(clean)) {
            return;
        }
        int eezoCost = getGenomeEezoCost(clean);
        int bioCost = getGenomeBiomassCost(clean);
        if (state.getEezoReserves() < eezoCost) {
            throw new InsufficientEezoException(eezoCost, state.getEezoReserves());
        }
        if (state.getAccumulatedBiomass() < bioCost) {
            throw new InsufficientBiomassException(bioCost, state.getAccumulatedBiomass());
        }
        state.deductEezo(eezoCost);
        state.deductBiomass(bioCost);
        sequencedGenomes.add(clean);
    }

    public int getGenomeEezoCost(String speciesKey) {
        if (speciesKey == null) return 40;
        switch (speciesKey.toLowerCase().trim()) {
            case "volus": return 100;
            case "vorcha": return 80;
            case "elcor": return 150;
            case "prothean": return 250;
            case "yahg": return 300;
            case "hanar": return 70;
            case "drell": return 60;
            case "rachni": return 120;
            default: return 40;
        }
    }

    public int getGenomeBiomassCost(String speciesKey) {
        if (speciesKey == null) return 30;
        switch (speciesKey.toLowerCase().trim()) {
            case "volus": return 50;
            case "vorcha": return 40;
            case "elcor": return 100;
            case "prothean": return 200;
            case "yahg": return 250;
            case "hanar": return 40;
            case "drell": return 45;
            case "rachni": return 90;
            default: return 30;
        }
    }

    public java.util.Set<String> getSequencedGenomes() {
        return new java.util.HashSet<String>(sequencedGenomes);
    }

    public void addSequencedGenome(String speciesKey) {
        if (speciesKey != null) {
            sequencedGenomes.add(speciesKey.toLowerCase().trim());
        }
    }

    public boolean isEnforceGenomeResearch() {
        return enforceGenomeResearch;
    }

    public void setEnforceGenomeResearch(boolean enforce) {
        this.enforceGenomeResearch = enforce;
    }

    public void seedCivilization(int sector, int cluster, String speciesKey)
            throws SystemOccupiedException, InsufficientEezoException, ReaperException {
        seedCivilization(sector, cluster, speciesKey, true);
    }

    public void seedCivilization(int sector, int cluster, String speciesKey, boolean chargeCost)
            throws SystemOccupiedException, InsufficientEezoException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format(
                    "Sector %d (%s) is beyond dark space! Construct its Primary Mass Relay in the Tech Tree to connect this sector.",
                    sector, state.getGalaxyMap().getSectorShortName(sector)));
        }

        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        if (system.getClimateType() == com.seb.harvesteffect.model.entity.ClimateType.BARREN) {
            throw new ReaperException(String.format(
                    "Planet %s is BARREN and uninhabitable! Terraform this world before seeding.",
                    system.getSystemName()));
        }

        String clean = speciesKey.toLowerCase().trim();
        boolean unlockedByStory = (missionManager != null && missionManager.isSpeciesUnlocked(clean, this));

        // Check if player has a matching GenesisProbe loaded in flagship cargo hold
        int matchingProbeSlot = -1;
        if (state.getCargoHold() != null) {
            List<Resource> manifest = state.getCargoHold().getManifest();
            for (int i = 0; i < manifest.size(); i++) {
                Resource r = manifest.get(i);
                if (r instanceof GenesisProbe) {
                    GenesisProbe gp = (GenesisProbe) r;
                    if (gp.getTargetSpecies().equalsIgnoreCase(clean)) {
                        matchingProbeSlot = i;
                        break;
                    }
                }
            }
        }

        boolean usedProbeFromCargo = false;
        if (matchingProbeSlot != -1) {
            // Deploy and consume probe directly from flagship cargo
            state.getCargoHold().retrieve(matchingProbeSlot);
            usedProbeFromCargo = true;
            // Physical Genesis Probe zygotes unlock & sequence the genome
            if (!isGenomeSequenced(clean)) {
                addSequencedGenome(clean);
            }
        } else {
            if (enforceGenomeResearch && !isGenomeSequenced(clean) && !unlockedByStory) {
                throw new ReaperException(String.format(
                        "Genome for %s is unsequenced in Reaper archives! Requisition a Genesis Probe or research its genome in Bio-Banks.",
                        speciesKey));
            }
        }

        Civilization civ = createCivilization(speciesKey);
        if (chargeCost && !usedProbeFromCargo) {
            int cost = civ.getSeedingCost();
            if (state.getEezoReserves() < cost) {
                throw new InsufficientEezoException(cost, state.getEezoReserves());
            }
            state.deductEezo(cost);
        } else if (usedProbeFromCargo) {
            // Genesis Probe Incubation Boost: accelerates initial cellular colonization
            civ.grow(1.25);
        }
        system.deployCivilization(civ);
        campaign.checkObjectiveProgress(state, techTree);
    }

    public void deployUnit(int sector, int cluster, String unitType)
            throws SystemOccupiedException, InsufficientEezoException, InsufficientBiomassException, ReaperException {
        deployUnit(sector, cluster, unitType, true);
    }

    public void deployUnit(int sector, int cluster, String unitType, boolean chargeCost)
            throws SystemOccupiedException, InsufficientEezoException, InsufficientBiomassException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format(
                    "Sector %d (%s) is inaccessible. Build a Primary Mass Relay to reach this sector.",
                    sector, state.getGalaxyMap().getSectorShortName(sector)));
        }

        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        String unitId = String.format("%d%d", sector, cluster);
        BiomechanicalUnit unit;

        String key = unitType.toLowerCase().trim();
        if (key.contains("drone") || key.contains("collector")) {
            unit = new CollectorDrone(unitId);
        } else if (key.contains("husk")) {
            unit = new HuskSwarm(unitId);
        } else if (key.contains("scion") || key.contains("behemoth")) {
            unit = new ScionBehemoth(unitId);
        } else {
            throw new IllegalArgumentException("Unknown biomechanical unit classification: " + unitType);
        }

        if (chargeCost) {
            int eezoCost = unit.getDeploymentCost();
            int bioCost = unit.getBiomassCost();
            if (state.getEezoReserves() < eezoCost) {
                throw new InsufficientEezoException(eezoCost, state.getEezoReserves());
            }
            if (state.getAccumulatedBiomass() < bioCost) {
                throw new InsufficientBiomassException(bioCost, state.getAccumulatedBiomass());
            }
            state.deductEezo(eezoCost);
            state.deductBiomass(bioCost);
        }

        unit.recharge(unit.getPowerCapacity());
        system.deployUnit(unit);

        // In Act 4 (Crucible War): Specialized biomechanical units raid/sabotage Crucible research on spacefaring worlds!
        if (campaign != null && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            Civilization targetCiv = system.getCivilization();
            if (targetCiv != null && targetCiv.getEvolutionaryTier() >= Civilization.TIER_INDUSTRIAL) {
                int reduction;
                String desc;
                if (unit instanceof ScionBehemoth) {
                    reduction = 10;
                    desc = "Biotic Shockwave Demolition pulverized Alliance Crucible assembly yards";
                } else if (unit instanceof HuskSwarm) {
                    reduction = 6;
                    desc = "Cybernetic Ground Swarm assaulted and captured planetary Crucible research facilities";
                } else {
                    reduction = 4;
                    desc = "Airborne Collector Drones infiltrated communications networks and corrupted Crucible telemetry";
                }
                campaign.reduceCrucibleProgress(reduction);
                lastCrucibleNotice = String.format("[CRUCIBLE SABOTAGED] %s: %s on %s [%d,%d]! Crucible progress reduced by -%d%%! (Current: %d%%)",
                        unit.getDesignation(), desc, system.getSystemName(), sector, cluster, reduction, campaign.getCrucibleProgress());
            } else {
                lastCrucibleNotice = null;
            }
        } else {
            lastCrucibleNotice = null;
        }

        // Easter Egg Trigger: Collector Drone deployed on Sur'Kesh while Salarians at Tier 2
        Civilization civ = system.getCivilization();
        if (unit instanceof CollectorDrone && sector == 0 && cluster == 3 && civ != null && civ.getEvolutionaryTier() >= 2) {
            scanner.triggerSignal("mordin");
        }
    }

    public int[] dismantleUnit(int sector, int cluster) throws ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format("Sector %d is inaccessible.", sector));
        }
        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        BiomechanicalUnit unit = system.getBiomechanicalUnit();
        if (unit == null) {
            throw new ReaperException(String.format("No biomechanical unit stationed at %s [%d,%d] to dismantle.",
                    system.getSystemName(), sector, cluster));
        }

        int refundEezo = unit.getDeploymentCost() / 2;
        int refundBio = unit.getBiomassCost() / 2;
        state.addEezo(refundEezo);
        state.addBiomass(refundBio);
        system.removeUnit();

        return new int[] { refundEezo, refundBio };
    }

    public void moveUnit(int fromSector, int fromCluster, int toSector, int toCluster)
            throws SystemOccupiedException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(fromSector)) {
            throw new ReaperException(String.format("Origin Sector %d is inaccessible.", fromSector));
        }
        if (!state.getGalaxyMap().isSectorUnlocked(toSector)) {
            throw new ReaperException(String.format(
                    "Destination Sector %d (%s) is inaccessible. Build a Primary Mass Relay to reach this sector.",
                    toSector, state.getGalaxyMap().getSectorShortName(toSector)));
        }

        StarSystem fromSystem = state.getGalaxyMap().getSystem(fromSector, fromCluster);
        BiomechanicalUnit unit = fromSystem.getBiomechanicalUnit();
        if (unit == null) {
            throw new ReaperException(String.format("No biomechanical unit stationed at %s [%d,%d] to relocate.",
                    fromSystem.getSystemName(), fromSector, fromCluster));
        }

        StarSystem toSystem = state.getGalaxyMap().getSystem(toSector, toCluster);
        if (toSystem.getBiomechanicalUnit() != null) {
            throw new SystemOccupiedException(toSystem.getSystemName(),
                    "Swarm Construct: " + toSystem.getBiomechanicalUnit().getDesignation());
        }

        fromSystem.removeUnit();
        toSystem.deployUnit(unit);

        // In Act 4 (Crucible War): Relocating specialized biomechanical units onto spacefaring worlds can raid/sabotage Crucible research!
        if (campaign != null && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            Civilization targetCiv = toSystem.getCivilization();
            if (targetCiv != null && targetCiv.getEvolutionaryTier() >= Civilization.TIER_INDUSTRIAL) {
                int reduction;
                String desc;
                if (unit instanceof ScionBehemoth) {
                    reduction = 10;
                    desc = "Biotic Shockwave Demolition pulverized Alliance Crucible assembly yards";
                } else if (unit instanceof HuskSwarm) {
                    reduction = 6;
                    desc = "Cybernetic Ground Swarm assaulted and captured planetary Crucible research facilities";
                } else {
                    reduction = 4;
                    desc = "Airborne Collector Drones infiltrated communications networks and corrupted Crucible telemetry";
                }
                campaign.reduceCrucibleProgress(reduction);
                lastCrucibleNotice = String.format("[CRUCIBLE SABOTAGED] %s: %s on %s [%d,%d]! Crucible progress reduced by -%d%%! (Current: %d%%)",
                        unit.getDesignation(), desc, toSystem.getSystemName(), toSector, toCluster, reduction, campaign.getCrucibleProgress());
            }
        }

        // Easter Egg Trigger: Collector Drone relocated to Sur'Kesh while Salarians at Tier 2+
        Civilization civ = toSystem.getCivilization();
        if (unit instanceof CollectorDrone && toSector == 0 && toCluster == 3 && civ != null && civ.getEvolutionaryTier() >= 2) {
            scanner.triggerSignal("mordin");
        }
    }

    public HarvestYield harvestSystem(int sector, int cluster)
            throws CivilizationPrematureException, CargoHoldFullException, CivilizationBarrierException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format(
                    "Sector %d (%s) Primary Relay is OFFLINE! Cannot transmit ascension yields.",
                    sector, state.getGalaxyMap().getSectorShortName(sector)));
        }

        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        Civilization civ = system.getCivilization();
        if (civ == null) {
            throw new IllegalStateException("System contains no organic civilization to harvest.");
        }

        // Kinetic Orbital Barrier Defense Check
        if (civ.hasKineticBarrier()) {
            boolean barrierBreached = false;
            // Sovereign Flagship in local orbit breaches planetary barrier
            if (state.getFlagshipSector() == sector && state.getFlagshipCluster() == cluster) {
                barrierBreached = true;
                civ.disableKineticBarrier();
            }
            // Scion Behemoth or Husk Swarm garrison breaches barrier
            if (system.getBiomechanicalUnit() instanceof ScionBehemoth || system.getBiomechanicalUnit() instanceof HuskSwarm) {
                barrierBreached = true;
                civ.disableKineticBarrier();
            }
            // Tech upgrade: Cyclonic Kinetic Barrier penetrates defenses
            if (techTree.isUnlocked("cyclonic_barrier") || techTree.isUnlocked("cyclonic_barriers")) {
                barrierBreached = true;
                civ.disableKineticBarrier();
            }
            // Fleet Components in cargo: Cyclonic Kinetic Barrier or Thanix Cannon breaches defenses
            if (state.hasFleetComponent("barrier") || state.hasFleetComponent("thanix")) {
                barrierBreached = true;
                civ.disableKineticBarrier();
            }
            if (!barrierBreached) {
                throw new CivilizationBarrierException(civ.getSpeciesName());
            }
        }

        // Easter Egg Trigger: Harvesting Sol [0,0] (Earth) at Apex Zenith triggers Shepard Anomaly
        if (sector == 0 && cluster == 0 && "Humanity".equalsIgnoreCase(civ.getSpeciesName()) && civ.isHarvestReady()) {
            scanner.triggerSignal("shepard");
        }

        // Easter Egg Trigger: Harvesting Turians during Crucible War with Crucible > 70%
        if ("Turian".equalsIgnoreCase(civ.getSpeciesName())
                && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR
                && campaign.getCrucibleProgress() >= 70) {
            scanner.triggerSignal("marauder");
        }

        HarvestYield baseYield = civ.harvest();
        int biomass = baseYield.getGeneticBiomass();
        int darkEnergy = baseYield.getDarkEnergyYield();

        // Climate Affinity Yield Modifier (+25% ideal vs -25% hostile)
        double climateYieldMult = system.getClimateType().getHarvestYieldMultiplier(civ.getSpeciesName());
        biomass = (int) (biomass * climateYieldMult);
        darkEnergy = (int) (darkEnergy * climateYieldMult);

        // Apply Tech Tree Upgrades
        if (techTree.isUnlocked("biomass_vats")) {
            biomass = (int) (biomass * 1.5);
        }
        if (techTree.isUnlocked("thanix_cannons")) {
            darkEnergy = (int) (darkEnergy * 1.3);
        }

        // Scion Behemoth Yield Amplification
        if (system.getBiomechanicalUnit() instanceof ScionBehemoth) {
            double scionMultiplier = techTree.isUnlocked("scion_amplifier") ? 1.75 : 1.5;
            darkEnergy = (int) (darkEnergy * scionMultiplier);
            biomass = (int) (biomass * scionMultiplier);
        }

        // Human-Reaper Larva Core Endgame Multiplier
        if (techTree.isUnlocked("reaper_larva_core")) {
            darkEnergy = (int) (darkEnergy * 2.0);
            biomass = (int) (biomass * 2.0);
        }

        // Citadel Conduit Lattice Multiplier (Tier 4)
        if (system.isRelayBeamActive()) {
            darkEnergy = (int) (darkEnergy * nexus.getHarvestRelayBonusMultiplier());
        }

        // Sovereign Flagship Local Orbit Presence Bonus (+20%)
        if (state.getFlagshipSector() == sector && state.getFlagshipCluster() == cluster) {
            darkEnergy = (int) (darkEnergy * 1.20);
            biomass = (int) (biomass * 1.20);
        }

        // Fleet Components in Cargo Yield Amplification
        if (state.hasFleetComponent("thanix")) {
            darkEnergy = (int) (darkEnergy * 1.30);
        }
        if (state.hasFleetComponent("occular") || state.hasFleetComponent("laser")) {
            biomass = (int) (biomass * 1.25);
        }

        HarvestYield yield = new HarvestYield(baseYield.getOriginSpecies(), biomass, darkEnergy);
        if (state.getCargoHold().isFull()) {
            throw new CargoHoldFullException(state.getCargoHold().getCapacity());
        }
        state.getCargoHold().store(yield);
        state.addBiomass(yield.getGeneticBiomass());
        state.addEezo(yield.getDarkEnergyYield());

        int civTierBeforeHarvest = civ.getEvolutionaryTier();
        String speciesBeforeHarvest = civ.getSpeciesName();
        system.purgeSystem();

        // In Act 4 (The Crucible War): Wiping an organic civilization strikes a devastating blow to Crucible construction!
        if (campaign != null && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            int reduction = 0;
            if (civTierBeforeHarvest >= Civilization.TIER_APEX_ZENITH) { // Tier 3 Apex
                reduction = 10;
            } else if (civTierBeforeHarvest >= Civilization.TIER_INDUSTRIAL) { // Tier 2 Spacefaring
                reduction = 5;
            }
            if (reduction > 0) {
                campaign.reduceCrucibleProgress(reduction);
                lastCrucibleNotice = String.format("[CRUCIBLE HIT] The fall and purge of %s destroyed Allied Crucible engineering shipyards! Crucible progress reduced by -%d%%! (Current: %d%%)",
                        speciesBeforeHarvest, reduction, campaign.getCrucibleProgress());
            } else {
                lastCrucibleNotice = null;
            }
        } else {
            lastCrucibleNotice = null;
        }

        campaign.recordAscension();
        campaign.checkObjectiveProgress(state, techTree);
        return yield;
    }

    public void deployRelay(int sector, int cluster) throws InsufficientEezoException, ReaperException {
        deployRelay(sector, cluster, true);
    }

    public void deployRelay(int sector, int cluster, boolean chargeCost) throws InsufficientEezoException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format(
                    "Sector %d (%s) Primary Relay is OFFLINE! Cannot deploy secondary relay nodes.",
                    sector, state.getGalaxyMap().getSectorShortName(sector)));
        }

        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        if (chargeCost && !system.isRelayBeaconDeployed()) {
            int cost = 50;
            if (state.getEezoReserves() < cost) {
                throw new InsufficientEezoException(cost, state.getEezoReserves());
            }
            state.deductEezo(cost);
        }
        system.deployRelayBeacon();

        // Quantum entanglement upgrade auto-tethers sector
        if (techTree.isUnlocked("quantum_entanglement_relays")) {
            for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                state.getGalaxyMap().getSystem(sector, c).deployRelayBeacon();
            }
        }
        campaign.checkObjectiveProgress(state, techTree);
    }

    public void terraformSystem(int sector, int cluster, com.seb.harvesteffect.model.entity.ClimateType targetClimate)
            throws InsufficientEezoException, ReaperException {
        terraformSystem(sector, cluster, targetClimate, true);
    }

    public void terraformSystem(int sector, int cluster, com.seb.harvesteffect.model.entity.ClimateType targetClimate, boolean chargeCost)
            throws InsufficientEezoException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(sector)) {
            throw new ReaperException(String.format(
                    "Sector %d (%s) is inaccessible. Build the sector's Primary Mass Relay to reach this world.",
                    sector, state.getGalaxyMap().getSectorShortName(sector)));
        }
        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        if (targetClimate == null) {
            throw new IllegalArgumentException("Target climate cannot be null.");
        }
        int cost = 75;
        if (chargeCost) {
            if (state.getEezoReserves() < cost) {
                throw new InsufficientEezoException(cost, state.getEezoReserves());
            }
            state.deductEezo(cost);
        }
        system.terraform(targetClimate);
    }

    public void rechargeUnit(int sector, int cluster, int powerUnits) throws InsufficientEezoException {
        StarSystem system = state.getGalaxyMap().getSystem(sector, cluster);
        BiomechanicalUnit unit = system.getBiomechanicalUnit();
        if (unit == null) {
            throw new IllegalStateException("No biomechanical construct stationed in target system.");
        }
        int eezoCost = powerUnits / 2;
        state.deductEezo(eezoCost);
        unit.recharge(powerUnits);
    }

    public int moveFlagship(int targetSector, int targetCluster) throws InsufficientEezoException {
        try {
            return moveFlagship(targetSector, targetCluster, false);
        } catch (ReaperException e) {
            throw new RuntimeException(e);
        }
    }

    public int moveFlagship(int targetSector, int targetCluster, boolean enforceSingleJumpLimit)
            throws InsufficientEezoException, ReaperException {
        if (!state.getGalaxyMap().isSectorUnlocked(targetSector)) {
            throw new ReaperException(String.format("Sector %d (%s) is inaccessible. Build a Primary Mass Relay first.",
                    targetSector, state.getGalaxyMap().getSectorShortName(targetSector)));
        }
        if (enforceSingleJumpLimit && state.isFlagshipJumpedThisEpoch()) {
            throw new ReaperException(
                    "Sovereign FTL Drive Core is discharging static electricity! (Limit: 1 Jump per Epoch).\n"
                    + "Advance the epoch to dissipate static charge, or deploy Biomechanical Swarms to reach this system.");
        }
        boolean relayDiscount = techTree.isUnlocked("secondary_relay_alignment");
        StarSystem sys = state.getGalaxyMap().getSystem(targetSector, targetCluster);
        int cost = 0;
        if (!sys.isRelayBeamActive()) {
            cost = relayDiscount ? 15 : 25;
            // Fleet Components in cargo: Occular laser guidance and Sovereign armor optimize sub-light jump thrusters
            if (state.hasFleetComponent("occular") || state.getInstalledFleetArmorIntegrity() > 100) {
                cost = Math.max(5, cost / 2);
            }
            if (state.getEezoReserves() < cost) {
                throw new InsufficientEezoException(cost, state.getEezoReserves());
            }
            state.deductEezo(cost);
        }
        state.moveFlagship(targetSector, targetCluster, false);
        state.setFlagshipJumpedThisEpoch(true);
        return cost;
    }

    public boolean canFlagshipJump() {
        return !state.isFlagshipJumpedThisEpoch();
    }

    public boolean hasGenesisProbeInCargo(String speciesKey) {
        return state.hasGenesisProbe(speciesKey);
    }

    public int relocateFlagship(int targetSector, int targetCluster) throws InsufficientEezoException {
        return moveFlagship(targetSector, targetCluster);
    }

    public GalacticPhenomenon advanceCycle() {
        GalacticPhenomenon[] events = GalacticPhenomenon.values();
        GalacticPhenomenon chosen = events[random.nextInt(events.length)];
        state.advanceEpoch(chosen);
        state.collectPassiveDividends();

        // Citadel Mega-Structure Passive Income
        int citDividends = nexus.getPassiveEezoDividend();
        if (techTree.isUnlocked("citadel_eezo_siphons")) citDividends += 75;
        if (techTree.isUnlocked("shadow_broker_tap")) citDividends += 30;
        if (techTree.isUnlocked("catalyst_convergence_protocol")) citDividends *= 2;
        if (citDividends > 0) state.addEezo(citDividends);

        // Tech Tree Dynamic Sector Unlocks
        if (techTree.isUnlocked("primary_relay_alpha")) {
            state.getGalaxyMap().setSectorUnlocked(1, true);
        }
        if (techTree.isUnlocked("primary_relay_omega")) {
            state.getGalaxyMap().setSectorUnlocked(2, true);
        }
        if (techTree.isUnlocked("primary_relay_gamma")) {
            state.getGalaxyMap().setSectorUnlocked(3, true);
        }
        if (techTree.isUnlocked("biomass_vats") && state.getCargoHold() != null && state.getCargoHold().getCapacity() < 24) {
            state.setCargoCapacity(24);
        }

        // Creeping Colonization: Tier 2+ spacefaring species expand to adjacent empty terraformed worlds
        List<StarSystem> colonizationCandidates = new ArrayList<StarSystem>();
        for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
            if (!state.getGalaxyMap().isSectorUnlocked(r)) continue;
            for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                StarSystem sys = state.getGalaxyMap().getSystem(r, c);
                Civilization civ = sys.getCivilization();
                if (civ != null && civ.getEvolutionaryTier() >= Civilization.TIER_INDUSTRIAL && !civ.hasColonized()) {
                    colonizationCandidates.add(sys);
                }
            }
        }

        for (StarSystem sys : colonizationCandidates) {
            Civilization civ = sys.getCivilization();
            if (civ == null || civ.hasColonized()) continue;
            civ.incrementColonizationTimer();
            if (civ.getColonizationTimer() >= 2) {
                int r = sys.getSector();
                int c = sys.getCluster();
                int[] neighbors = { c + 1, c - 1 };
                for (int nextC : neighbors) {
                    if (nextC >= 0 && nextC < state.getGalaxyMap().getColCount()) {
                        StarSystem adjSys = state.getGalaxyMap().getSystem(r, nextC);
                        if (adjSys.getCivilization() == null && adjSys.getClimateType() != com.seb.harvesteffect.model.entity.ClimateType.BARREN) {
                            try {
                                Civilization colony = createCivilization(civ.getSpeciesName());
                                colony.advanceEpoch(GalacticPhenomenon.STELLAR_CALM, adjSys.getClimateType());
                                colony.setPopulationBillions(2);
                                // Mutual Defense Pact: activates planetary kinetic barriers on both worlds
                                colony.setKineticBarrier(true);
                                civ.setKineticBarrier(true);
                                civ.setHasColonized(true);
                                colony.setHasColonized(true);
                                adjSys.deployCivilization(colony);
                                break;
                            } catch (SystemOccupiedException ignored) {}
                        }
                    }
                }
            }
        }

        // Sub-Space Indoctrination Emitter Bonus
        if (techTree.isUnlocked("indoctrination_emitter")) {
            for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                    Civilization cObj = state.getGalaxyMap().getSystem(r, c).getCivilization();
                    if (cObj != null && cObj.getEvolutionaryTier() < Civilization.TIER_APEX_ZENITH) {
                        cObj.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
                    }
                }
            }
        }

        // Sleeper Agents & Citadel Tier V: Suppress AI Heresy Rebellions
        if (techTree.isUnlocked("sleeper_agents") || nexus.isImmuneToSyntheticHeresy()) {
            for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                    Civilization cObj = state.getGalaxyMap().getSystem(r, c).getCivilization();
                    if (cObj != null) {
                        cObj.suppressAiHeresy();
                    }
                }
            }
        }

        // Biomechanical Swarm Construct Automation & Growth Catalyst
        for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
            for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                StarSystem sys = state.getGalaxyMap().getSystem(r, c);
                BiomechanicalUnit u = sys.getBiomechanicalUnit();
                if (u != null && u.isOperational()) {
                    u.operationalSweep();
                    HarvestYield tel = u.extractTelemetry();
                    if (tel != null) {
                        state.addBiomass(tel.getGeneticBiomass());
                        state.addEezo(tel.getDarkEnergyYield());
                    }
                    Civilization civ = sys.getCivilization();
                    if (civ != null && u instanceof CollectorDrone && civ.getEvolutionaryTier() < Civilization.TIER_APEX_ZENITH) {
                        // Collector Drone acts as an Automated Growth Catalyst (accelerates organic evolution)
                        civ.advanceEpoch(GalacticPhenomenon.STELLAR_CALM);
                    }
                }
            }
        }

        // Easter Egg Trigger: Garrus Calibrations on Palaven [0,2] during Solar Flare Surge
        if (chosen == GalacticPhenomenon.SOLAR_FLARE_SURGE) {
            StarSystem pSys = state.getGalaxyMap().getSystem(0, 2);
            Civilization palavenCiv = pSys.getCivilization();
            if (palavenCiv instanceof Turian || (palavenCiv != null && pSys.getSystemName().toLowerCase().contains("palaven"))) {
                scanner.triggerSignal("garrus");
            }
        }

        // Easter Egg Trigger: Blasto on Kahje during Dark Energy Storm
        if (chosen == GalacticPhenomenon.DARK_ENERGY_STORM) {
            StarSystem kSys = state.getGalaxyMap().getSystem(1, 3);
            Civilization kahjeCiv = kSys.getCivilization();
            if (kahjeCiv instanceof Hanar || (kahjeCiv != null && kSys.getSystemName().toLowerCase().contains("kahje"))) {
                scanner.triggerSignal("blasto");
            }
        }

        // Easter Egg Trigger: Space Hamster in cargo hold when biomass reserves reach 500+
        if (state.getAccumulatedBiomass() >= 500) {
            scanner.triggerSignal("hamster");
        }

        // Easter Egg Trigger: Conrad Verner civilian flight arrives at Epoch 5+
        if (state.getCycleEpoch() >= 5) {
            scanner.triggerSignal("conrad");
        }

        state.resetFlagshipJumps();
        campaign.onEpochAdvance(state, techTree);
        this.lastLockdownNotice = (nexus != null) ? nexus.onEpochAdvance() : null;
        campaign.checkObjectiveProgress(state, techTree);
        return chosen;
    }

    public Civilization createCivilization(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Species key cannot be null.");
        }
        String clean = key.toLowerCase().trim();
        switch (clean) {
            case "humanity":
            case "human":
                return new Humanity();
            case "asari":
                return new Asari();
            case "turian":
                return new Turian();
            case "salarian":
                return new Salarian();
            case "krogan":
                return new Krogan();
            case "quarian":
                return new Quarian();
            case "batarian":
                return new Batarian();
            case "volus":
                return new Volus();
            case "hanar":
                return new Hanar();
            case "drell":
                return new Drell();
            case "elcor":
                return new Elcor();
            case "vorcha":
                return new Vorcha();
            case "rachni":
                return new Rachni();
            case "prothean":
                return new Prothean();
            case "yahg":
                return new Yahg();
            default:
                throw new IllegalArgumentException("Unknown Mass Effect species designation: " + key);
        }
    }

    public GalacticState getState() {
        return state;
    }

    public CitadelNexus getNexus() {
        return nexus;
    }

    public TechTree getTechTree() {
        return techTree;
    }

    public CampaignManager getCampaign() {
        return campaign;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public com.seb.harvesteffect.story.StoryDilemmaManager getDilemmaManager() {
        return dilemmaManager;
    }

    public SubSpaceScanner getScanner() {
        return scanner;
    }

    public String getLastLockdownNotice() {
        return lastLockdownNotice;
    }

    public String getLastCrucibleNotice() {
        return lastCrucibleNotice;
    }
}
