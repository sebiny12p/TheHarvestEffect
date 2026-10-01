package com.seb.harvesteffect.model.entity;

import java.io.Serializable;

/**
 * Multi-Sector Galactic Grid representing the Milky Way star cluster network.
 * Features 4 interstellar galactic sectors (24 star systems total, 6 per sector)
 * connected radially to the Citadel Relay Nexus at the galactic core.
 * Each sector requires an active Primary Mass Relay to manage.
 */
public class GalacticSector implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int ROWS = 4;
    public static final int COLS = 6;

    public static final String[] SECTOR_NAMES = {
        "Sector 0: The Core Systems (Local Cluster & Serpent Nebula)",
        "Sector 1: The Attican Traverse & Krogan DMZ",
        "Sector 2: The Terminus Systems & Perseus Veil",
        "Sector 3: The Shadow Rim & Precursor Verge"
    };

    public static final String[] SECTOR_SHORT_NAMES = {
        "The Core Systems",
        "The Attican Traverse",
        "The Terminus Systems",
        "The Shadow Rim"
    };

    private static final String[][] SYSTEM_NAMES = {
        { "Sol", "Thessian Veil", "Palaven Expanse", "Sur'Kesh Node", "Dekuuna Spire", "Elysium Void" },
        { "Tuchanka Core", "Khar'shan Reach", "Rakhana Verge", "Kahje Deep", "Korlus Basin", "Feros Ridge" },
        { "Rannoch Expanse", "Irune Basin", "Heshtok Ridge", "Omega Void", "Noveria Shelf", "Horizon Verge" },
        { "Suen Void", "Eden Prime", "Parnack Shelf", "Sanctum Deep", "Ilos Crypt", "Virmire Reach" }
    };

    private static final ClimateType[][] DEFAULT_CLIMATES = {
        { ClimateType.ARID, ClimateType.GARDEN, ClimateType.ARID, ClimateType.GARDEN, ClimateType.METHANE, ClimateType.BARREN },
        { ClimateType.ARID, ClimateType.VOLCANIC, ClimateType.ARID, ClimateType.GARDEN, ClimateType.BARREN, ClimateType.ARID },
        { ClimateType.ARID, ClimateType.METHANE, ClimateType.VOLCANIC, ClimateType.VOLCANIC, ClimateType.BARREN, ClimateType.GARDEN },
        { ClimateType.METHANE, ClimateType.GARDEN, ClimateType.VOLCANIC, ClimateType.BARREN, ClimateType.GARDEN, ClimateType.GARDEN }
    };

    private final StarSystem[][] grid;
    private final boolean[] sectorUnlocked;

    public GalacticSector() {
        this.grid = new StarSystem[ROWS][COLS];
        // Sector 0 active at start; Sectors 1, 2, 3 require Primary Relay construction
        this.sectorUnlocked = new boolean[] { true, false, false, false };
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new StarSystem(SYSTEM_NAMES[r][c], r, c, DEFAULT_CLIMATES[r][c]);
            }
        }
    }

    public boolean isSectorUnlocked(int sector) {
        if (sector < 0 || sector >= ROWS) return false;
        return sectorUnlocked[sector];
    }

    public boolean isSectorRelayActive(int sector) {
        return isSectorUnlocked(sector);
    }

    public void setSectorUnlocked(int sector, boolean unlocked) {
        if (sector >= 0 && sector < ROWS) {
            sectorUnlocked[sector] = unlocked;
        }
    }

    public void buildSectorRelay(int sector) {
        setSectorUnlocked(sector, true);
    }

    public void unlockAllSectors() {
        for (int i = 0; i < ROWS; i++) {
            sectorUnlocked[i] = true;
        }
    }

    public String getSectorName(int sector) {
        if (sector >= 0 && sector < ROWS) return SECTOR_NAMES[sector];
        return "Unknown Sector " + sector;
    }

    public String getSectorShortName(int sector) {
        if (sector >= 0 && sector < ROWS) return SECTOR_SHORT_NAMES[sector];
        return "Sector " + sector;
    }

    public StarSystem getSystem(int sector, int cluster) {
        if (sector < 0 || sector >= ROWS || cluster < 0 || cluster >= COLS) {
            throw new IndexOutOfBoundsException(
                    String.format("Coordinates [%d, %d] outside galactic radar grid.", sector, cluster));
        }
        return grid[sector][cluster];
    }

    public int getRowCount() {
        return ROWS;
    }

    public int getColCount() {
        return COLS;
    }

    public int getTotalWorldCount() {
        return ROWS * COLS;
    }

    public java.util.List<StarSystem> getAllSystems() {
        java.util.List<StarSystem> list = new java.util.ArrayList<StarSystem>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                list.add(grid[r][c]);
            }
        }
        return list;
    }
}
