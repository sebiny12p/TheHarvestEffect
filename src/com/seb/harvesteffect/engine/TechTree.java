package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.exception.*;
import java.io.Serializable;
import java.util.*;

/**
 * Armada Tech Tree: 21-Node Branching Research Matrix for the Reaper Armada.
 * Features 5 specialized branches: Extinction Armada, Indoctrination & Espionage,
 * Relay Engineering & Galactic Reach, Citadel Convergence & Economy, and Bio-Bank Genome Sequencing.
 */
public class TechTree implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Branch {
        EXTINCTION_ARMADA("Extinction Armada", "Harvest yield amplification, kinetic penetrators, and dreadnought larvae"),
        INDOCTRINATION("Indoctrination & Espionage", "Sub-space subliminal carriers, sleeper agents, and Crucible sabotage"),
        RELAY_EXPANSION("Relay Engineering & Reach", "Primary Mass Relay construction bridging into new galactic sectors"),
        CITADEL_CONVERGENCE("Citadel & Fleet Economy", "Dark space siphons, Mega-Structure security override, and catalyst convergence"),
        BIO_BANK_GENOMES("Bio-Bank Genome Sequencing", "Synthesize specialized alien species genomes (Volus, Vorcha, Elcor, Prothean, Yahg) into Reaper Bio-Banks");

        private final String title;
        private final String description;

        Branch(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    public static class Upgrade implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String name;
        private final String description;
        private final Branch branch;
        private final int eezoCost;
        private final int biomassCost;
        private final int requiredCargoPods;
        private final String requiredCargoDescription;
        private final int requiredMissionIndex;
        private final String requiredCycleName;
        private final String prerequisiteUpgradeId;
        private boolean unlocked;

        public Upgrade(String id, String name, String description, Branch branch,
                       int eezoCost, int biomassCost, int requiredMissionIndex,
                       String requiredCycleName, String prerequisiteUpgradeId) {
            this(id, name, description, branch, eezoCost, biomassCost, 0, null,
                    requiredMissionIndex, requiredCycleName, prerequisiteUpgradeId);
        }

        public Upgrade(String id, String name, String description, Branch branch,
                       int eezoCost, int biomassCost, int requiredCargoPods,
                       int requiredMissionIndex, String requiredCycleName,
                       String prerequisiteUpgradeId) {
            this(id, name, description, branch, eezoCost, biomassCost, requiredCargoPods,
                    requiredCargoPods > 0 ? "1x Harvested Biomass Matrix (Any Species)" : null,
                    requiredMissionIndex, requiredCycleName, prerequisiteUpgradeId);
        }

        public Upgrade(String id, String name, String description, Branch branch,
                       int eezoCost, int biomassCost, int requiredCargoPods,
                       String requiredCargoDescription,
                       int requiredMissionIndex, String requiredCycleName,
                       String prerequisiteUpgradeId) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.branch = branch;
            this.eezoCost = eezoCost;
            this.biomassCost = biomassCost;
            this.requiredCargoPods = requiredCargoPods;
            this.requiredCargoDescription = requiredCargoDescription;
            this.requiredMissionIndex = requiredMissionIndex;
            this.requiredCycleName = requiredCycleName;
            this.prerequisiteUpgradeId = prerequisiteUpgradeId;
            this.unlocked = false;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Branch getBranch() { return branch; }
        public int getEezoCost() { return eezoCost; }
        public int getBiomassCost() { return biomassCost; }
        public int getRequiredCargoPods() { return requiredCargoPods; }
        public String getRequiredCargoDescription() { return requiredCargoDescription; }
        public String getRequiredCycleName() { return requiredCycleName; }
        public String getPrerequisiteUpgradeId() { return prerequisiteUpgradeId; }
        public boolean isUnlocked() { return unlocked; }
        public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

        public boolean isAvailableInMission(int currentMissionIndex) {
            return currentMissionIndex >= requiredMissionIndex;
        }
    }

    private final Map<String, Upgrade> upgrades;

    public TechTree() {
        Map<String, Upgrade> map = new LinkedHashMap<String, Upgrade>();

        // ==========================================
        // BRANCH 1: EXTINCTION ARMADA
        // ==========================================
        map.put("scion_amplifier", new Upgrade(
                "scion_amplifier",
                "Scion Heavy Biotic Amplifiers",
                "Garrisoned Scion Behemoths amplify planetary harvest yields by +75% (up from +50%).",
                Branch.EXTINCTION_ARMADA,
                150, 100, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("thanix_cannons", new Upgrade(
                "thanix_cannons",
                "Thanix Magneto-Hydrodynamic Arrays",
                "Liquid-metal kinetic penetrators: increases Ascension Harvest Dark Energy yield by +30%.",
                Branch.EXTINCTION_ARMADA,
                250, 200, 3, "Act II: The Attican Traverse", null
        ));

        map.put("biomass_vats", new Upgrade(
                "biomass_vats",
                "Genetic Synthesis Vats",
                "Doubles flagship Cargo Hold capacity (to 24) and boosts biomass value by +50%. Requires 1x Harvested Biomass Matrix (reap any mature civilization) to calibrate synthesis vats.",
                Branch.EXTINCTION_ARMADA,
                350, 350, 1, "1x Harvested Biomass Matrix (Any Species)", 4, "Act III: The Perseus Veil", "thanix_cannons"
        ));

        map.put("reaper_larva_core", new Upgrade(
                "reaper_larva_core",
                "Human-Reaper Larva Matrix",
                "Apex synthetic dreadnought: doubles harvest yields and unlocks final victory protocol. Requires 2x Harvested Biomass Matrix pods (reaped civilization slurry) to construct larva chassis.",
                Branch.EXTINCTION_ARMADA,
                600, 900, 2, "2x Harvested Biomass Matrix (Humanity / Any Species)", 4, "Act IV: The Shadow Rim", "biomass_vats"
        ));

        // ==========================================
        // BRANCH 2: INDOCTRINATION & ESPIONAGE
        // ==========================================
        map.put("indoctrination_emitter", new Upgrade(
                "indoctrination_emitter",
                "Sub-Space Indoctrination Emitter",
                "Subliminal carrier waves: organic civilizations advance through evolutionary tiers 40% faster.",
                Branch.INDOCTRINATION,
                150, 100, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("sleeper_agents", new Upgrade(
                "sleeper_agents",
                "Sleeper Agent Infiltration",
                "Subtle neuro-chemical conditioning prevents AI Heresy / Rogue Geth rebellions on mature worlds.",
                Branch.INDOCTRINATION,
                220, 200, 1, "Act I: The Ruined Citadel", "indoctrination_emitter"
        ));

        map.put("shadow_broker_tap", new Upgrade(
                "shadow_broker_tap",
                "Shadow Broker Sub-Space Tap",
                "Intercepts galactic communications: reveals future cosmic phenomena in advance & +30 Eezo/epoch.",
                Branch.INDOCTRINATION,
                300, 250, 2, "Act I: The Ruined Citadel", "sleeper_agents"
        ));

        map.put("crucible_sabotage", new Upgrade(
                "crucible_sabotage",
                "Crucible Sabotage Protocols",
                "Infiltrates Alliance engineering teams, reducing Crucible construction speed by 25%.",
                Branch.INDOCTRINATION,
                450, 400, 4, "Act III: The Perseus Veil", "shadow_broker_tap"
        ));

        // ==========================================
        // BRANCH 3: RELAY ENGINEERING & GALACTIC EXPANSION
        // ==========================================
        map.put("secondary_relay_alignment", new Upgrade(
                "secondary_relay_alignment",
                "Secondary Relay Alignment",
                "Boosts Mass Relay bandwidth by +25% and reduces Sovereign jump costs between unlinked stars.",
                Branch.RELAY_EXPANSION,
                120, 80, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("primary_relay_alpha", new Upgrade(
                "primary_relay_alpha",
                "Primary Relay Alpha Construction",
                "Constructs interstellar gateway unlocking SECTOR 1: THE ATTICAN TRAVERSE & KROGAN DMZ (6 new worlds)!",
                Branch.RELAY_EXPANSION,
                250, 250, 1, "Act I: The Ruined Citadel", "secondary_relay_alignment"
        ));

        map.put("primary_relay_omega", new Upgrade(
                "primary_relay_omega",
                "Primary Relay Omega Construction",
                "Constructs deep-space gateway unlocking SECTOR 2: THE TERMINUS SYSTEMS & PERSEUS VEIL (6 new worlds)!",
                Branch.RELAY_EXPANSION,
                400, 450, 3, "Act II: The Attican Traverse", "primary_relay_alpha"
        ));

        map.put("primary_relay_gamma", new Upgrade(
                "primary_relay_gamma",
                "Primary Relay Gamma Construction",
                "Constructs deep-space gateway unlocking SECTOR 3: THE SHADOW RIM & PRECURSOR VERGE (6 new worlds)!",
                Branch.RELAY_EXPANSION,
                450, 500, 4, "Act III: The Perseus Veil", "primary_relay_omega"
        ));

        map.put("quantum_entanglement_relays", new Upgrade(
                "quantum_entanglement_relays",
                "Quantum Entangled Relay Lattice",
                "Deploying a Mass Relay automatically tethers all adjacent star systems in the cluster.",
                Branch.RELAY_EXPANSION,
                550, 600, 4, "Act IV: The Shadow Rim", "primary_relay_gamma"
        ));

        // ==========================================
        // BRANCH 4: CITADEL CONVERGENCE & FLEET ECONOMY
        // ==========================================
        map.put("cyclonic_barrier", new Upgrade(
                "cyclonic_barrier",
                "Cyclonic Kinetic Shield Matrix",
                "Advanced Reaper defensive shielding: protects fleet assets from cosmic turbulence and unlocks Citadel Eezo Siphons.",
                Branch.CITADEL_CONVERGENCE,
                120, 100, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("citadel_eezo_siphons", new Upgrade(
                "citadel_eezo_siphons",
                "Citadel Eezo Siphons",
                "Taps the Citadel core directly, siphoning +75 Element Zero into fleet reserves every epoch.",
                Branch.CITADEL_CONVERGENCE,
                200, 150, 1, "Act I: The Ruined Citadel", "cyclonic_barrier"
        ));

        map.put("citadel_core_control", new Upgrade(
                "citadel_core_control",
                "Citadel Core Security Override",
                "Overrides keeper protocols: unlocks Citadel Nexus Mega-Structure Tiers III (Lockdown) and IV (Conduit).",
                Branch.CITADEL_CONVERGENCE,
                350, 300, 2, "Act I: The Ruined Citadel", "citadel_eezo_siphons"
        ));

        map.put("catalyst_convergence_protocol", new Upgrade(
                "catalyst_convergence_protocol",
                "Catalyst Convergence Protocol",
                "Awakens the Citadel AI: doubles all Citadel passive dividends and unlocks Citadel Tier V.",
                Branch.CITADEL_CONVERGENCE,
                500, 500, 4, "Act IV: The Shadow Rim", "citadel_core_control"
        ));

        // ==========================================
        // BRANCH 5: BIO-BANK GENOME SEQUENCING
        // ==========================================
        map.put("genome_volus", new Upgrade(
                "genome_volus",
                "Volus Genome Sequencing",
                "Synthesizes Volus perennial cash crop genome into bio-banks. Generates passive Eezo dividends every cycle.",
                Branch.BIO_BANK_GENOMES,
                100, 50, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("genome_vorcha", new Upgrade(
                "genome_vorcha",
                "Vorcha Genome Sequencing",
                "Synthesizes Vorcha hardy wildgrass genome into bio-banks. Immune to solar flares and cosmic storms.",
                Branch.BIO_BANK_GENOMES,
                80, 40, 0, "Prologue: The Fall of Earth", null
        ));

        map.put("genome_elcor", new Upgrade(
                "genome_elcor",
                "Elcor Genome Sequencing",
                "Synthesizes Elcor ironbark genome into bio-banks. Ultra-dense mass yields capital hull armor.",
                Branch.BIO_BANK_GENOMES,
                150, 100, 1, "Act I: The Ruined Citadel", null
        ));

        map.put("genome_prothean", new Upgrade(
                "genome_prothean",
                "Prothean Genome Sequencing",
                "Synthesizes Prothean precursor heirloom genome into bio-banks. Returns triple (3.0x) Dark Energy upon Ascension. Requires 1x Harvested Biomass Matrix (DNA sequencing template).",
                Branch.BIO_BANK_GENOMES,
                250, 200, 1, "1x Harvested Biomass Matrix (DNA Template)", 3, "Act II: The Attican Traverse", null
        ));

        map.put("genome_yahg", new Upgrade(
                "genome_yahg",
                "Yahg Genome Sequencing",
                "Synthesizes Yahg apex predator genome into bio-banks. Returns massive genetic biomass bulk upon Ascension. Requires 1x Harvested Biomass Matrix (muscle tissue template).",
                Branch.BIO_BANK_GENOMES,
                300, 250, 1, "1x Harvested Biomass Matrix (Apex Tissue)", 4, "Act III: The Perseus Veil", null
        ));

        this.upgrades = Collections.unmodifiableMap(map);
    }

    public List<Upgrade> getAllUpgrades() {
        return new ArrayList<Upgrade>(upgrades.values());
    }

    public List<Upgrade> getUpgradesByBranch(Branch branch) {
        List<Upgrade> list = new ArrayList<Upgrade>();
        for (Upgrade u : upgrades.values()) {
            if (u.getBranch() == branch) list.add(u);
        }
        return list;
    }

    public Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    public boolean isUnlocked(String id) {
        Upgrade u = upgrades.get(id);
        return u != null && u.isUnlocked();
    }

    public void unlockUpgrade(String id, GalacticState state) throws InsufficientEezoException, InsufficientBiomassException {
        unlockUpgrade(id, state, 999);
    }

    public void unlockUpgrade(String id, GalacticState state, int currentMissionIndex)
            throws InsufficientEezoException, InsufficientBiomassException {
        Upgrade u = upgrades.get(id);
        if (u == null) {
            throw new IllegalArgumentException("Unknown tech upgrade ID: " + id);
        }
        if (u.isUnlocked()) {
            throw new IllegalStateException("Upgrade already researched: " + u.getName());
        }
        if (!u.isAvailableInMission(currentMissionIndex)) {
            throw new IllegalStateException(String.format(
                    "Research Locked: %s requires advancing story to %s.",
                    u.getName(), u.getRequiredCycleName()));
        }
        if (u.getPrerequisiteUpgradeId() != null && !isUnlocked(u.getPrerequisiteUpgradeId())) {
            Upgrade prereq = getUpgrade(u.getPrerequisiteUpgradeId());
            String pName = prereq != null ? prereq.getName() : u.getPrerequisiteUpgradeId();
            throw new IllegalStateException(String.format(
                    "Prerequisite Missing: %s requires '%s' to be researched first.",
                    u.getName(), pName));
        }
        if (state.getEezoReserves() < u.getEezoCost()) {
            throw new InsufficientEezoException(u.getEezoCost(), state.getEezoReserves());
        }
        if (state.getAccumulatedBiomass() < u.getBiomassCost()) {
            throw new InsufficientBiomassException(u.getBiomassCost(), state.getAccumulatedBiomass());
        }
        if (u.getRequiredCargoPods() > 0 && state.countCargoSpecimens() < u.getRequiredCargoPods()) {
            String cargoDesc = u.getRequiredCargoDescription() != null
                    ? u.getRequiredCargoDescription()
                    : String.format("%dx Harvested Biomass Matrix (Any Species)", u.getRequiredCargoPods());
            throw new IllegalStateException(String.format(
                    "Missing Required Cargo: '%s' requires %s in Flagship Cargo Hold (Currently in hold: %d/%d).\n"
                    + "How to obtain: Reap any mature organic civilization world across the galaxy to secure its genetic Biomass Matrix.",
                    u.getName(), cargoDesc, state.countCargoSpecimens(), u.getRequiredCargoPods()));
        }

        state.deductEezo(u.getEezoCost());
        state.deductBiomass(u.getBiomassCost());
        if (u.getRequiredCargoPods() > 0) {
            state.consumeCargoSpecimens(u.getRequiredCargoPods());
        }
        u.setUnlocked(true);

        if (state.getGalaxyMap() != null) {
            if ("primary_relay_alpha".equals(id)) {
                state.getGalaxyMap().setSectorUnlocked(1, true);
            } else if ("primary_relay_omega".equals(id)) {
                state.getGalaxyMap().setSectorUnlocked(2, true);
            } else if ("primary_relay_gamma".equals(id)) {
                state.getGalaxyMap().setSectorUnlocked(3, true);
            }
        }
        if ("biomass_vats".equals(id) && state.getCargoHold() != null && state.getCargoHold().getCapacity() < 24) {
            state.setCargoCapacity(24);
        }
    }
}
