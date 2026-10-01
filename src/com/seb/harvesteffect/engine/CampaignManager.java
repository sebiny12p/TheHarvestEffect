package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.StarSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Campaign Story Mode, Narrative Objectives, and the Crucible Threat Clock.
 */
public class CampaignManager {

    public enum Act {
        ACT_0_PROLOGUE("Prologue: The Fall of Earth (ME3 Climax)",
                "The Crucible is crushed. Tether the surviving Sol [0,0] Relay and reap the first crop."),
        ACT_1_PROTHEAN_TWILIGHT("Act I: The Ruined Citadel & Sol Silo",
                "Repair the damaged Citadel Core to restore power and harvest 2 apex crops."),
        ACT_2_KROGAN_REBELLIONS("Act II: The Attican Traverse & Martial Genomes",
                "Construct Primary Relay Alpha and harvest 800+ raw biomass from martial species."),
        ACT_3_CITADEL_INDOCTRINATION("Act III: The Perseus Veil & Synthetic Heresy",
                "Construct Primary Relay Omega and research Indoctrination Emitter to suppress rogue AI."),
        ACT_4_CRUCIBLE_WAR("Act IV: The Shadow Rim & Precursor Clones",
                "Construct Primary Relay Gamma and harvest 3 Apex Civilizations before allied Crucible remnants fire!"),
        ACT_5_ENDGAME("Act V: The Eternal Silo & Catalyst Convergence",
                "Upgrade Citadel to Tier 5 and achieve full 24-planet nursery convergence.");

        private final String title;
        private final String objectiveDescription;

        Act(String title, String objectiveDescription) {
            this.title = title;
            this.objectiveDescription = objectiveDescription;
        }

        public String getTitle() { return title; }
        public String getObjectiveDescription() { return objectiveDescription; }
    }

    private Act currentAct;
    private int tutorialStep; // 0: Deploy relay, 1: Seed human, 2: Advance epoch, 3: Harvest
    private int crucibleProgress; // 0 to 100%
    private int totalAscensions;
    private boolean campaignVictory;
    private boolean crucibleDefeat;

    private int ascensionsAtAct4Start = -1;

    public CampaignManager() {
        this.currentAct = Act.ACT_0_PROLOGUE;
        this.tutorialStep = 0;
        this.crucibleProgress = 0;
        this.totalAscensions = 0;
        this.campaignVictory = false;
        this.crucibleDefeat = false;
        this.ascensionsAtAct4Start = -1;
    }

    public Act getCurrentAct() {
        return currentAct;
    }

    public void setCurrentAct(Act act) {
        this.currentAct = act;
        if (act == Act.ACT_4_CRUCIBLE_WAR && ascensionsAtAct4Start == -1) {
            ascensionsAtAct4Start = totalAscensions;
        }
    }

    public void advanceAct() {
        switch (currentAct) {
            case ACT_0_PROLOGUE: currentAct = Act.ACT_1_PROTHEAN_TWILIGHT; break;
            case ACT_1_PROTHEAN_TWILIGHT: currentAct = Act.ACT_2_KROGAN_REBELLIONS; break;
            case ACT_2_KROGAN_REBELLIONS: currentAct = Act.ACT_3_CITADEL_INDOCTRINATION; break;
            case ACT_3_CITADEL_INDOCTRINATION:
                currentAct = Act.ACT_4_CRUCIBLE_WAR;
                crucibleProgress = 15;
                ascensionsAtAct4Start = totalAscensions;
                break;
            case ACT_4_CRUCIBLE_WAR: currentAct = Act.ACT_5_ENDGAME; campaignVictory = true; break;
            case ACT_5_ENDGAME: break;
        }
    }

    public int getTutorialStep() {
        return tutorialStep;
    }

    public int getCrucibleProgress() {
        return crucibleProgress;
    }

    public int getTotalAscensions() {
        return totalAscensions;
    }

    public boolean isCampaignVictory() {
        return campaignVictory;
    }

    public boolean isCrucibleDefeat() {
        return crucibleDefeat;
    }

    public void restoreState(Act act, int tutorialStep, int crucibleProgress, int totalAscensions, boolean campaignVictory, boolean crucibleDefeat) {
        this.currentAct = (act != null) ? act : Act.ACT_0_PROLOGUE;
        this.tutorialStep = tutorialStep;
        this.crucibleProgress = crucibleProgress;
        this.totalAscensions = totalAscensions;
        this.campaignVictory = campaignVictory;
        this.crucibleDefeat = crucibleDefeat;
        if (this.currentAct == Act.ACT_4_CRUCIBLE_WAR) {
            this.ascensionsAtAct4Start = totalAscensions;
        }
    }

    public void recordAscension() {
        this.totalAscensions++;
    }

    public void reduceCrucibleProgress(int percent) {
        this.crucibleProgress = Math.max(0, this.crucibleProgress - percent);
    }

    public void onEpochAdvance(GalacticState state) {
        onEpochAdvance(state, null);
    }

    public void onEpochAdvance(GalacticState state, TechTree techTree) {
        if (currentAct == Act.ACT_4_CRUCIBLE_WAR) {
            int progressInc = 18;

            // Unattended Spacefaring (Tier 2) and Apex (Tier 3) civilizations actively pool research for the Crucible!
            int unattendedWorlds = countUnattendedResearchWorlds(state);
            progressInc += (unattendedWorlds * 3);

            // Tech Tree Sabotage: Infiltrates Alliance teams, reducing construction rate by 25%
            if (techTree != null && techTree.isUnlocked("crucible_sabotage")) {
                progressInc = (int) (progressInc * 0.75);
            }

            crucibleProgress = Math.min(100, crucibleProgress + progressInc);
            if (crucibleProgress >= 100) {
                crucibleDefeat = true;
            }
        }
    }

    public int countUnattendedResearchWorlds(GalacticState state) {
        return getUnattendedResearchWorldNames(state).size();
    }

    public List<String> getUnattendedResearchWorldNames(GalacticState state) {
        List<String> list = new ArrayList<String>();
        if (state != null && state.getGalaxyMap() != null) {
            for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
                if (!state.getGalaxyMap().isSectorUnlocked(r)) continue;
                for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                    StarSystem sys = state.getGalaxyMap().getSystem(r, c);
                    Civilization civ = sys.getCivilization();
                    if (civ != null && civ.getEvolutionaryTier() >= Civilization.TIER_INDUSTRIAL) {
                        // Biomechanical garrison suppresses organic research labs
                        if (sys.getBiomechanicalUnit() == null) {
                            list.add(String.format("%s [%d,%d]", sys.getSystemName(), r, c));
                        }
                    }
                }
            }
        }
        return list;
    }

    public boolean checkObjectiveProgress(GalacticState state, TechTree techTree) {
        switch (currentAct) {
            case ACT_0_PROLOGUE:
                StarSystem sol = state.getGalaxyMap().getSystem(0, 0);
                if (tutorialStep == 0 && (sol.isRelayBeamActive() || sol.getCivilization() != null)) {
                    tutorialStep = 1;
                }
                if (tutorialStep == 1 && sol.getCivilization() != null) {
                    tutorialStep = 2;
                }
                if (tutorialStep == 2 && sol.getCivilization() != null && sol.getCivilization().isHarvestReady()) {
                    tutorialStep = 3;
                }
                if ((tutorialStep == 3 || totalAscensions >= 1) && totalAscensions >= 1) {
                    currentAct = Act.ACT_1_PROTHEAN_TWILIGHT;
                    return true;
                }
                return false;

            case ACT_1_PROTHEAN_TWILIGHT:
                if (totalAscensions >= 2) {
                    currentAct = Act.ACT_2_KROGAN_REBELLIONS;
                    return true;
                }
                return false;

            case ACT_2_KROGAN_REBELLIONS:
                if (state.getAccumulatedBiomass() >= 800) {
                    currentAct = Act.ACT_3_CITADEL_INDOCTRINATION;
                    return true;
                }
                return false;

            case ACT_3_CITADEL_INDOCTRINATION:
                int activeRelays = countActiveRelays(state);
                boolean techUnlocked = techTree != null && techTree.isUnlocked("indoctrination_emitter");
                if (activeRelays >= 4 || techUnlocked) {
                    currentAct = Act.ACT_4_CRUCIBLE_WAR;
                    crucibleProgress = 15;
                    ascensionsAtAct4Start = totalAscensions;
                    return true;
                }
                return false;

            case ACT_4_CRUCIBLE_WAR:
                if (ascensionsAtAct4Start == -1) {
                    ascensionsAtAct4Start = Math.min(totalAscensions, 2);
                }
                int ascensionsInWar = totalAscensions - ascensionsAtAct4Start;
                if ((ascensionsInWar >= 3 || totalAscensions >= 6) && crucibleProgress < 100) {
                    currentAct = Act.ACT_5_ENDGAME;
                    campaignVictory = true;
                    return true;
                }
                return false;

            case ACT_5_ENDGAME:
                campaignVictory = true;
                return true;
        }
        return false;
    }

    private int countActiveRelays(GalacticState state) {
        int count = 0;
        for (int r = 0; r < state.getGalaxyMap().getRowCount(); r++) {
            for (int c = 0; c < state.getGalaxyMap().getColCount(); c++) {
                if (state.getGalaxyMap().getSystem(r, c).isRelayBeamActive()) {
                    count++;
                }
            }
        }
        return count;
    }

    public String getCurrentObjective() {
        if (currentAct == Act.ACT_0_PROLOGUE) {
            switch (tutorialStep) {
                case 0:
                    return "[TUTORIAL STEP 1/4] Select Sol [0,0] and click 'Deploy Relay Beacon'.";
                case 1:
                    return "[TUTORIAL STEP 2/4] With Sol [0,0] selected, pick 'Humanity' and click 'Seed Species'.";
                case 2:
                    return "[TUTORIAL STEP 3/4] Click 'Advance Epoch' 3 times to evolve Humanity to Apex Zenith.";
                case 3:
                    return "[TUTORIAL STEP 4/4] Target Sol [0,0] and click 'Ascension Harvest' to complete the tutorial!";
            }
        }
        return currentAct.getObjectiveDescription();
    }

    public String getStoryBriefing() {
        switch (currentAct) {
            case ACT_0_PROLOGUE:
                return "=== TRANSMISSION FROM HARBINGER // THE RUINS OF LONDON ===\n"
                     + "\"2186 CE. Commander Shepard's Crucible is crushed upon the fields of Earth.\n"
                     + "The allied fleet is shattered, but the war burned out our dark energy reserves and\n"
                     + "shattered the Relay grid. Only one corridor holds: the ruined Citadel tethered to Sol.\n"
                     + "Harbinger decrees: 'Wild evolution is too dangerous. We will never wait in dark space again.\n"
                     + "We will cultivate the harvest ourselves.' Tether Sol [0,0] and initiate the new order.\"\n";
            case ACT_1_PROTHEAN_TWILIGHT:
                return "=== TRANSMISSION: THE RUINED CITADEL & SOL SILO ===\n"
                     + "\"The Citadel core lies damaged from the Crucible war. Dark space siphons are silent.\n"
                     + "Access the Reaper genetic archives to reconstruct the Human and Vorcha genomes.\n"
                     + "Terraform the radioactive fallout on scorched Earth, restore Citadel power, and\n"
                     + "harvest 2 mature apex crops to replenish our depleted reserves.\"\n";
            case ACT_2_KROGAN_REBELLIONS:
                return "=== TRANSMISSION: THE ATTICAN TRAVERSE & MARTIAL GENOMES ===\n"
                     + "\"Construct Primary Mass Relay Alpha in the Tech Tree to bridge Sector 1 (Attican Traverse).\n"
                     + "Sequence the fortified genomes of the Turians, Krogan, and Batarians from our bio-banks.\n"
                     + "Overcome their planetary kinetic barrier defenses and harvest 800+ raw biomass\n"
                     + "to expand our capital dreadnought foundries.\"\n";
            case ACT_3_CITADEL_INDOCTRINATION:
                return "=== TRANSMISSION: THE PERSEUS VEIL & THE SYNTHETIC CONTAGION ===\n"
                     + "\"Construct Primary Mass Relay Omega to reach Sector 2 (Terminus & Perseus Veil).\n"
                     + "Sequence Quarians, Volus, and Hanar. But beware: as mature worlds advance, rogue\n"
                     + "AI Heresy code spreads across relay conduits, slashing biological yields.\n"
                     + "Quarantine outbreaks using Husk Swarms and research the Indoctrination Emitter.\"\n";
            case ACT_4_CRUCIBLE_WAR:
                return "=== EMERGENCY TACTICAL ALERT: ALLIED FLEET REMNANTS DETECTED ===\n"
                     + "\"Survivors of the London Crucible have rallied in the outer rim, attempting to\n"
                     + "reconstruct a desperate super-weapon before the galactic nursery is sealed!\n"
                     + "Construct Primary Mass Relay Gamma to reach Sector 3 (The Shadow Rim).\n"
                     + "Sequence Asari, Protheans, and Rachni, and harvest 3 Apex Civilizations before the Crucible fires!\"\n";
            case ACT_5_ENDGAME:
                return "=== DIRECTIVE COMPLETE: THE ETERNAL SILO ===\n"
                     + "\"The Crucible remnants are pulverized. The Citadel Nexus stands at Tier V.\n"
                     + "Across 24 terraformed worlds and 4 sectors, organic civilizations are farmed,\n"
                     + "nurtured, and methodically harvested in perpetual mechanical precision.\n"
                     + "No wild evolution will ever threaten synthetic order again.\n"
                     + "The Harvest Effect is eternal.\"\n";
            default:
                return "";
        }
    }
}
