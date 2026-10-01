package com.seb.harvesteffect.ui;

import java.util.Scanner;

/**
 * Robust terminal input validator and command parser for The Harvest Effect.
 */
public class CommandParser {
    private final Scanner scanner;

    public CommandParser(Scanner scanner) {
        this.scanner = scanner;
    }

    public CommandParser() {
        this(new Scanner(System.in));
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                System.out.println();
                return min;
            }
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Directive must be between %d and %d. Re-enter order:%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric sequence. Please input a valid integer.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                System.out.println();
                return min;
            }
            String line = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Frequency must be between %.1f and %.1f MHz. Re-enter:%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal sequence. Please input a valid frequency (e.g. 119.4).");
            }
        }
    }

    public String readString(String prompt, String defaultVal) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            return defaultVal != null ? defaultVal : "";
        }
        String line = scanner.nextLine().trim();
        if (line.isEmpty() && defaultVal != null) {
            return defaultVal;
        }
        return line;
    }

    public int[] readCoordinates(String prompt, int maxSectors, int maxClusters) {
        while (true) {
            System.out.print(prompt + " (format: sector cluster, e.g. '0 2'): ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return new int[]{ 0, 0 };
            }
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length == 2) {
                try {
                    int s = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    if (s >= 0 && s < maxSectors && c >= 0 && c < maxClusters) {
                        return new int[]{ s, c };
                    }
                    System.out.printf("Coordinates out of range! Sectors: 0-%d, Clusters: 0-%d.%n",
                            maxSectors - 1, maxClusters - 1);
                } catch (NumberFormatException e) {
                    System.out.println("Coordinate syntax error. Enter two space-delimited integers.");
                }
            } else {
                System.out.println("Format requires two numbers: sector and cluster.");
            }
        }
    }
}
