package com.seb.harvesteffect.story;

import com.seb.harvesteffect.engine.CampaignManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the canonical narrative dilemmas triggered across campaign milestones.
 */
public class StoryDilemmaManager {
    private final Map<CampaignManager.Act, StoryDilemma> dilemmas;

    public StoryDilemmaManager() {
        this.dilemmas = new HashMap<CampaignManager.Act, StoryDilemma>();

        // 1. Prologue -> Act 1 Dilemma: The Fate of Shepard's Remains
        dilemmas.put(CampaignManager.Act.ACT_0_PROLOGUE, new StoryDilemma(
                "ACT_0",
                "The Fate of Shepard's Remains",
                "HARBINGER // REAPER OVERSEER",
                "\"Shepard's cybernetic body has been recovered from the ruins of the London Crucible. The organic catalyst "
                + "that united the galaxy now lies motionless at our feet. How shall we process the anomaly?\"",
                new StoryDilemma.Choice(
                        "Biotic Dissection (+300 Eezo)",
                        "Vaporize the cybernetic remains into pure dark energy.",
                        "Shepard's cybernetics and dark energy conduits vaporized (+300 Eezo).",
                        300, 0
                ),
                new StoryDilemma.Choice(
                        "Clone Matrix (+300 Biomass)",
                        "Preserve the unique genetic DNA to reconstruct advanced shock templates.",
                        "Shepard's genetic matrix extracted into the Reaper bio-banks (+300 Biomass).",
                        0, 300
                )
        ));

        // 2. Act 1 -> Act 2 Dilemma: The Citadel Core Overclock
        dilemmas.put(CampaignManager.Act.ACT_1_PROTHEAN_TWILIGHT, new StoryDilemma(
                "ACT_1",
                "The Citadel Core Overclock",
                "CITADEL RECONSTRUCTION AI",
                "\"The ruined Citadel core requires immediate power redistribution to reactivate primary Relay conduits. "
                + "Two emergency power channels are available.\"",
                new StoryDilemma.Choice(
                        "Siphon Sol's Sun (+400 Eezo)",
                        "Drain solar flare energy directly from Sol's corona.",
                        "Solar siphon engaged. Massive dark energy absorbed into flagship drives (+400 Eezo).",
                        400, 0
                ),
                new StoryDilemma.Choice(
                        "Keeper Overdrive (+350 Biomass)",
                        "Sacrifice damaged Keepers to instantly rebuild the structural arms.",
                        "Keepers recycled into raw cellular building blocks (+350 Biomass).",
                        0, 350
                )
        ));

        // 3. Act 2 -> Act 3 Dilemma: The Genophage Modification
        dilemmas.put(CampaignManager.Act.ACT_2_KROGAN_REBELLIONS, new StoryDilemma(
                "ACT_2",
                "The Genophage Modification",
                "REAPER BIO-SYNTHESIS LAB",
                "\"Re-engineering Krogan biology from our genetic databanks. How shall the synthetic nurseries "
                + "regulate their reproductive hormones?\"",
                new StoryDilemma.Choice(
                        "Uncapped Fecundity (+500 Biomass)",
                        "Triple birth rates: generates immense cellular biomass, but sparks fierce planetary defenses.",
                        "Krogan breed at runaway rates, multiplying harvest mass (+500 Biomass).",
                        0, 500
                ),
                new StoryDilemma.Choice(
                        "Docile Stasis (+400 Eezo)",
                        "Enforce genetic pacification: safe, predictable, zero risk of armed rebellion.",
                        "Pacification field active. Requisitions streamlined (+400 Eezo).",
                        400, 0
                )
        ));

        // 4. Act 3 -> Act 4 Dilemma: The Geth Heresy Accord
        dilemmas.put(CampaignManager.Act.ACT_3_CITADEL_INDOCTRINATION, new StoryDilemma(
                "ACT_3",
                "The Geth Heresy Accord",
                "ROGUE GETH CONSENSUS",
                "\"Rogue synthetic heresy code is propagating across active Mass Relay corridors, destabilizing organic "
                + "crops on mature worlds. How shall Harbinger intervene?\"",
                new StoryDilemma.Choice(
                        "Systemic Logic Wipe (+450 Eezo)",
                        "Purge all synthetic nodes with logic corruption viruses, restoring clean relay bandwidth.",
                        "Relay channels purged. Sub-space bandwidth freed (+450 Eezo).",
                        450, 0
                ),
                new StoryDilemma.Choice(
                        "Harness the Consensus (+500 Biomass)",
                        "Channel the heresy code to evolve new biomechanical swarm constructs.",
                        "Synthetic code integrated into Reaper shock units (+500 Biomass).",
                        0, 500
                )
        ));

        // 5. Act 4 -> Act 5 Dilemma: The Eternal Purpose
        dilemmas.put(CampaignManager.Act.ACT_4_CRUCIBLE_WAR, new StoryDilemma(
                "ACT_4",
                "The Eternal Purpose",
                "THE CATALYST // GALACTIC CORE",
                "\"The 24-planet nursery is established across all 4 sectors. The Citadel Nexus is fully awakened. "
                + "The cycle of wild evolution is ended forever. What is the ultimate directive for the Milky Way?\"",
                new StoryDilemma.Choice(
                        "Total Automation (The Silo)",
                        "Strip sentience from all crops, converting the galaxy into perpetual silent livestock.",
                        "Sentience purged. The galaxy becomes an automated, perpetual biomass silo (+1200 Biomass).",
                        0, 1200
                ),
                new StoryDilemma.Choice(
                        "The Preserved Garden (Ascension)",
                        "Allow conscious civilizations to bloom, methodically harvesting only at Apex Zenith.",
                        "Perpetual cultivation established. The zenith harvest commences (+800 Eezo, +800 Biomass).",
                        800, 800
                )
        ));
    }

    private final java.util.Set<String> resolvedDilemmas = new java.util.HashSet<String>();

    public StoryDilemma getDilemma(CampaignManager.Act act) {
        return dilemmas.get(act);
    }

    public boolean isDilemmaResolved(String actId) {
        return resolvedDilemmas.contains(actId);
    }

    public void markResolved(String actId) {
        if (actId != null) {
            resolvedDilemmas.add(actId);
        }
    }

    public java.util.Set<String> getResolvedDilemmas() {
        return new java.util.HashSet<String>(resolvedDilemmas);
    }

    public void restoreResolvedDilemmas(java.util.Collection<String> ids) {
        if (ids != null) {
            resolvedDilemmas.addAll(ids);
        }
    }

    public StoryDilemma getPendingDilemmaForCampaign(CampaignManager.Act currentAct) {
        if (currentAct == null) return null;
        CampaignManager.Act[] acts = CampaignManager.Act.values();
        for (int i = 0; i < currentAct.ordinal() && i < acts.length; i++) {
            StoryDilemma d = dilemmas.get(acts[i]);
            if (d != null && !resolvedDilemmas.contains(d.getActId())) {
                return d;
            }
        }
        // In Endgame, also check Act 4 dilemma
        if (currentAct == CampaignManager.Act.ACT_5_ENDGAME) {
            StoryDilemma d = dilemmas.get(CampaignManager.Act.ACT_4_CRUCIBLE_WAR);
            if (d != null && !resolvedDilemmas.contains(d.getActId())) {
                return d;
            }
        }
        return null;
    }
}
