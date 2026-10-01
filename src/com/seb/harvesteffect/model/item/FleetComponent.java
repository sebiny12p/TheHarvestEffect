package com.seb.harvesteffect.model.item;

/**
 * Capital ship hardware component acquired from the Citadel Nexus or advanced harvests.
 */
public class FleetComponent extends Resource {
    private final String subsystemName;
    private final int armorBuff;

    public FleetComponent(String subsystemName, int armorBuff, int cost) {
        super(subsystemName, armorBuff * 10, cost);
        this.subsystemName = subsystemName;
        this.armorBuff = armorBuff;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public int getArmorBuff() {
        return armorBuff;
    }

    @Override
    public String getDescription() {
        return String.format("Advanced Sovereign-class armor module (+%d Armor Integrity).", armorBuff);
    }
}
