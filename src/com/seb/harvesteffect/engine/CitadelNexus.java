package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.exception.CargoHoldFullException;
import com.seb.harvesteffect.exception.InsufficientBiomassException;
import com.seb.harvesteffect.exception.InsufficientEezoException;
import com.seb.harvesteffect.model.item.FleetComponent;
import com.seb.harvesteffect.model.item.GenesisProbe;
import com.seb.harvesteffect.model.item.Resource;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Requisition and commerce exchange located at the Dark Space Citadel Relay Nexus.
 * Features an expandable 5-Tier Mega-Structure upgrade tree granting passive Eezo dividends,
 * trade discounts, Relay harvest amplification, and Citadel Arms Lockdown against the Crucible.
 */
public class CitadelNexus implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TIER_1_DORMANT = 1;
    public static final int TIER_2_KEEPERS = 2;
    public static final int TIER_3_LOCKDOWN = 3;
    public static final int TIER_4_CONDUIT = 4;
    public static final int TIER_5_CATALYST = 5;

    public static final int LOCKDOWN_ACTIVE_DURATION = 4;
    public static final int LOCKDOWN_COOLDOWN_DURATION = 4;

    private int tier;
    private boolean lockdownActive;
    private int lockdownDurationRemaining;
    private int lockdownCooldownRemaining;
    private final Map<String, Integer> requisitionCatalog;

    public CitadelNexus() {
        this.tier = TIER_1_DORMANT;
        this.lockdownActive = false;
        this.lockdownDurationRemaining = 0;
        this.lockdownCooldownRemaining = 0;

        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        // 15 Species Genesis Probes
        map.put("humanity-probe", 80);
        map.put("asari-probe", 120);
        map.put("turian-probe", 100);
        map.put("salarian-probe", 90);
        map.put("krogan-probe", 110);
        map.put("quarian-probe", 85);
        map.put("batarian-probe", 75);
        map.put("volus-probe", 130);
        map.put("hanar-probe", 95);
        map.put("drell-probe", 105);
        map.put("elcor-probe", 115);
        map.put("vorcha-probe", 60);
        map.put("rachni-probe", 140);
        map.put("prothean-probe", 200);
        map.put("yahg-probe", 160);

        // Biomechanical Swarm Constructs
        map.put("collector-drone", 150);
        map.put("husk-swarm", 120);
        map.put("scion-behemoth", 250);

        // Fleet Capital Ship Upgrades
        map.put("thanix-cannon-core", 300);
        map.put("cyclonic-barrier-tech", 220);
        map.put("occular-beam-emitter", 400);

        this.requisitionCatalog = Collections.unmodifiableMap(map);
    }

    public int getTier() {
        return tier;
    }

    public int getCurrentTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = Math.max(1, Math.min(5, tier));
    }

    public boolean isArmsLockdownActive() {
        return lockdownActive;
    }

    public void setArmsLockdownTriggered(boolean active) {
        this.lockdownActive = active;
        this.lockdownDurationRemaining = active ? LOCKDOWN_ACTIVE_DURATION : 0;
        if (!active) {
            this.lockdownCooldownRemaining = 0;
        }
    }

    public int getLockdownDurationRemaining() {
        return lockdownDurationRemaining;
    }

    public int getLockdownCooldownRemaining() {
        return lockdownCooldownRemaining;
    }

    public void restoreLockdownState(boolean active, int durationRemaining, int cooldownRemaining) {
        this.lockdownActive = active;
        this.lockdownDurationRemaining = Math.max(0, durationRemaining);
        this.lockdownCooldownRemaining = Math.max(0, cooldownRemaining);
    }

    public void upgradeTier(GalacticState state) throws InsufficientEezoException, InsufficientBiomassException {
        upgradeCitadel(state, null);
    }

    public String activateArmsLockdown(CampaignManager campaign) {
        return triggerCitadelLockdown(campaign);
    }

    public String getTierName() {
        switch (tier) {
            case TIER_1_DORMANT: return "Tier I: Dormant Dark Space Hub";
            case TIER_2_KEEPERS: return "Tier II: Keeper Indoctrination Pylon";
            case TIER_3_LOCKDOWN: return "Tier III: Citadel Arms Lockdown Grid";
            case TIER_4_CONDUIT: return "Tier IV: Dark Energy Conduit Lattice";
            case TIER_5_CATALYST: return "Tier V: Catalyst Convergence Core";
            default: return "Unknown Tier";
        }
    }

    public String getTierDescription() {
        switch (tier) {
            case TIER_1_DORMANT:
                return "Base station: Standard Citadel requisition exchange for seed probes and swarm units.";
            case TIER_2_KEEPERS:
                return "Keepers secretly maintain Citadel infrastructure (-20% requisition discount, +40 Eezo/epoch).";
            case TIER_3_LOCKDOWN:
                return "Citadel Arms defense array primed: can close the arms to delay Crucible progress by 25%.";
            case TIER_4_CONDUIT:
                return "Amplifies galactic relay grid: +25% bonus Dark Energy on all Relay Ascension harvests (+75 Eezo/epoch).";
            case TIER_5_CATALYST:
                return "Full synthetic convergence: All systems immune to AI Heresy / Synthetic Rebellion (+150 Eezo/epoch).";
            default:
                return "";
        }
    }

    public int getNextTierEezoCost() {
        switch (tier) {
            case 1: return 300;
            case 2: return 600;
            case 3: return 1000;
            case 4: return 1500;
            default: return 0;
        }
    }

    public int getNextTierBiomassCost() {
        switch (tier) {
            case 1: return 200;
            case 2: return 450;
            case 3: return 800;
            case 4: return 1200;
            default: return 0;
        }
    }

    public boolean canUpgrade(GalacticState state, TechTree techTree) {
        if (tier >= TIER_5_CATALYST) return false;
        int eezo = getNextTierEezoCost();
        int bio = getNextTierBiomassCost();
        if (state.getEezoReserves() < eezo || state.getAccumulatedBiomass() < bio) return false;
        if (tier >= 2 && techTree != null && !techTree.isUnlocked("citadel_core_control")) {
            return false;
        }
        return true;
    }

    public void upgradeCitadel(GalacticState state, TechTree techTree)
            throws InsufficientEezoException, InsufficientBiomassException {
        if (tier >= TIER_5_CATALYST) {
            throw new IllegalStateException("Citadel Nexus is already at maximum Catalyst Convergence (Tier V).");
        }
        int eezoCost = getNextTierEezoCost();
        int bioCost = getNextTierBiomassCost();

        if (tier >= 2 && techTree != null && !techTree.isUnlocked("citadel_core_control")) {
            throw new IllegalStateException("Citadel Tiers III+ require 'Citadel Core Security Override' in the Tech Tree.");
        }
        if (state.getEezoReserves() < eezoCost) {
            throw new InsufficientEezoException(eezoCost, state.getEezoReserves());
        }
        if (state.getAccumulatedBiomass() < bioCost) {
            throw new InsufficientBiomassException(bioCost, state.getAccumulatedBiomass());
        }

        state.deductEezo(eezoCost);
        state.deductBiomass(bioCost);
        tier++;
    }

    public int getPassiveEezoDividend() {
        switch (tier) {
            case TIER_2_KEEPERS: return 40;
            case TIER_3_LOCKDOWN: return 50;
            case TIER_4_CONDUIT: return 75;
            case TIER_5_CATALYST: return 150;
            default: return 0;
        }
    }

    public double getHarvestRelayBonusMultiplier() {
        return (tier >= TIER_4_CONDUIT) ? 1.25 : 1.0;
    }

    public boolean isImmuneToSyntheticHeresy() {
        return tier >= TIER_5_CATALYST;
    }

    public boolean isLockdownReady() {
        return tier >= TIER_3_LOCKDOWN && !lockdownActive && lockdownCooldownRemaining <= 0;
    }

    public String onEpochAdvance() {
        if (lockdownActive) {
            lockdownDurationRemaining--;
            if (lockdownDurationRemaining <= 0) {
                lockdownActive = false;
                lockdownDurationRemaining = 0;
                lockdownCooldownRemaining = LOCKDOWN_COOLDOWN_DURATION;
                return "[CITADEL ARMS OPENED] The Citadel defense systems have overheated and the arms open again! Arms Lockdown is on cooldown for 4 epochs.";
            }
        } else if (lockdownCooldownRemaining > 0) {
            lockdownCooldownRemaining--;
            if (lockdownCooldownRemaining == 0) {
                return "[CITADEL ARMS RECHARGED] Citadel defense conduits are fully recharged! Arms Lockdown is ready to deploy again.";
            }
        }
        return null;
    }

    public String triggerCitadelLockdown(CampaignManager campaign) {
        if (tier < TIER_3_LOCKDOWN) {
            throw new IllegalStateException("Citadel Arms Lockdown requires Citadel Tier III.");
        }
        if (lockdownActive) {
            throw new IllegalStateException(String.format(
                    "Citadel Arms are already sealed shut! (%d epochs remaining).", lockdownDurationRemaining));
        }
        if (lockdownCooldownRemaining > 0) {
            throw new IllegalStateException(String.format(
                    "Citadel defense conduits are recharging! (%d epochs cooldown remaining).", lockdownCooldownRemaining));
        }
        lockdownActive = true;
        lockdownDurationRemaining = LOCKDOWN_ACTIVE_DURATION;
        lockdownCooldownRemaining = 0;
        if (campaign != null && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            campaign.reduceCrucibleProgress(25);
            return "[CITADEL ARMS LOCKED] Sovereign closes the Citadel arms! Alliance fleets locked out. Crucible progress delayed by -25%! Arms will remain sealed for 4 epochs, freezing Crucible construction while you find solutions to lower research.";
        }
        return "[CITADEL DEFENSE ENGAGED] Citadel perimeter locked down in dark space for 4 epochs.";
    }

    public Map<String, Integer> getCatalog() {
        return requisitionCatalog;
    }

    public int getEffectiveCost(String itemKey) {
        if (!requisitionCatalog.containsKey(itemKey.toLowerCase())) return 0;
        int base = requisitionCatalog.get(itemKey.toLowerCase());
        if (tier >= TIER_2_KEEPERS) {
            return (int) (base * 0.80); // 20% discount
        }
        return base;
    }

    public Resource purchaseItem(String itemKey, GalacticState state)
            throws InsufficientEezoException, CargoHoldFullException {
        if (!requisitionCatalog.containsKey(itemKey.toLowerCase())) {
            throw new IllegalArgumentException("Unknown requisition item code: " + itemKey);
        }

        int cost = getEffectiveCost(itemKey);
        if (state.getEezoReserves() < cost) {
            throw new InsufficientEezoException(cost, state.getEezoReserves());
        }

        Resource item;
        String key = itemKey.toLowerCase();
        if (key.endsWith("-probe")) {
            String species = key.replace("-probe", "");
            species = species.substring(0, 1).toUpperCase() + species.substring(1);
            item = new GenesisProbe(species);
        } else if (key.equals("thanix-cannon-core")) {
            item = new FleetComponent("Thanix Magnetic Cannon", 35, cost);
        } else if (key.equals("cyclonic-barrier-tech")) {
            item = new FleetComponent("Cyclonic Kinetic Barrier", 25, cost);
        } else if (key.equals("occular-beam-emitter")) {
            item = new FleetComponent("Occular Laser Array", 50, cost);
        } else {
            item = new FleetComponent("Fleet Enhancement [" + key + "]", 15, cost);
        }

        state.deductEezo(cost);
        state.getCargoHold().store(item);
        return item;
    }

    public int liquidateAsset(int cargoSlotIndex, GalacticState state) {
        Resource removed = state.getCargoHold().retrieve(cargoSlotIndex);
        int value = removed.getEezoValue();
        state.addEezo(value);
        return value;
    }
}
