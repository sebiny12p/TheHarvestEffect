package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.engine.CampaignManager;
import com.seb.harvesteffect.engine.GalacticState;
import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.GalacticSector;
import com.seb.harvesteffect.model.entity.StarSystem;

/**
 * Terminal UI renderer producing high-density ASCII tactical radar displays and narrative mission HUDs.
 */
public class TerminalRenderer {

    public static void renderHUD(GalacticState state, CampaignManager campaign) {
        System.out.println("==========================================================================================");
        System.out.printf("  THE HARVEST EFFECT // FLAGSHIP: %-12s | SEASON: %s | EPOCH: %-2d%n",
                state.getFlagshipName(), state.getCosmicSeason(), state.getCycleEpoch());
        System.out.printf("  ELEMENT ZERO (EEZO): %-5d                 | HARVESTED BIOMASS: %-5d UNITS%n",
                state.getEezoReserves(), state.getAccumulatedBiomass());
        System.out.printf("  COSMIC PHENOMENON: %-22s | STATUS: %s%n",
                state.getCurrentPhenomenon().getTitle(), state.getCurrentPhenomenon().getDescription());

        if (campaign != null) {
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf("  CAMPAIGN: %-30s | DIRECTIVE:%n  %s%n",
                    campaign.getCurrentAct().getTitle(), campaign.getCurrentObjective());
            if (campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
                System.out.printf("  [CRITICAL THREAT] Allied Fleet Crucible Construction: %d%%%n",
                        campaign.getCrucibleProgress());
            }
        }
        System.out.println("==========================================================================================");
    }

    public static void renderHUD(GalacticState state) {
        renderHUD(state, null);
    }

    public static void renderGalaxyMap(GalacticSector galaxy) {
        System.out.println("\n==========================================================================================");
        System.out.println("            MILKY WAY GALAXY // 4-SECTOR CITADEL RELAY MATRIX (24 WORLDS)                 ");
        System.out.println("==========================================================================================");
        System.out.println("  Legend: [.] Fallow | [*] T1 Seed | [#] T2 Spacefaring | [@] T3 RIPE | (R) Relay | (B) Barrier");
        System.out.println("  Biomes: [G] Garden  [A] Arid  [M] Methane  [V] Volcanic  [X] Barren (Needs Terraforming)\n");

        for (int r = 0; r < galaxy.getRowCount(); r++) {
            boolean unlocked = galaxy.isSectorUnlocked(r);
            String status = unlocked ? "[⚡ ONLINE - PRIMARY RELAY ACTIVE]" : "[🔒 LOCKED - REQUIRES PRIMARY RELAY]";
            System.out.printf("--- [SECTOR %d: %s] %s ---------------------------------------------------%n",
                    r, galaxy.getSectorName(r), status);
            for (int c = 0; c < galaxy.getColCount(); c++) {
                StarSystem sys = galaxy.getSystem(r, c);
                String tag = formatSystemCell(sys);
                System.out.printf("  Cluster %d: %-15s %-20s ", c, sys.getSystemName(), tag);
                if (c == 2 || c == 5) {
                    System.out.println();
                }
            }
            System.out.println();
        }
        System.out.println("==========================================================================================");
    }

    private static String formatSystemCell(StarSystem sys) {
        String relayTag = sys.isRelayBeamActive() ? "(R)" : "   ";
        Civilization civ = sys.getCivilization();
        BiomechanicalUnit unit = sys.getBiomechanicalUnit();
        char climChar = (sys.getClimateType() == com.seb.harvesteffect.model.entity.ClimateType.BARREN)
                ? 'X' : sys.getClimateType().name().charAt(0);

        if (civ != null) {
            char icon = civ.isHarvestReady() ? '@' : (civ.getEvolutionaryTier() >= 2 ? '#' : '*');
            String barrierTag = civ.hasKineticBarrier() ? "(B)" : "   ";
            return String.format("[%c|%c] %-8s %s%s", icon, climChar, civ.getSpeciesName(), relayTag, barrierTag);
        } else if (unit != null) {
            return String.format("[!|%c] %-8s %s", climChar, "Swarm", relayTag);
        } else {
            String label = (sys.getClimateType() == com.seb.harvesteffect.model.entity.ClimateType.BARREN) ? "Barren" : "Fallow";
            return String.format("[.|%c] %-8s %s", climChar, label, relayTag);
        }
    }
}
