package com.seb.harvesteffect.engine;

import java.util.Random;

/**
 * Manages narrative Mass Effect easter eggs and anomalous cosmic transmissions.
 */
public class EasterEggManager {
    private static final Random RNG = new Random();

    public static String getShepardEncounter() {
        return "\n==============================================================\n"
             + "  [PRIORITY RED TACTICAL ALERT: THE SHEPARD ANOMALY DETECTED] \n"
             + "==============================================================\n"
             + "Intercepted Comm-Link: SSV Normandy SR-2 // Alliance Stealth Frigate\n"
             + "Commander Shepard: \"I'm Commander Shepard, and this is my favorite star\n"
             + "system in the galaxy. Tell your Sovereign and Harbinger that humanity\n"
             + "will not kneel. We will find a way to end your cycle forever.\"\n"
             + "Harbinger: *\"THIS INSECT PEST DISRUPTS THE PARADIGM. ANOMALY LOGGED.\"*\n"
             + "==============================================================\n";
    }

    public static String getMordinSong() {
        return "\n==============================================================\n"
             + "  [SUR'KESH STG BIOLAB SURVEILLANCE INTERCEPT: DR. MORDIN SOLUS] \n"
             + "==============================================================\n"
             + "(Salarian humming briskly to Gilbert & Sullivan)\n"
             + "\"I am the very model of a scientist salarian,\n"
             + " I've studied species turian, asari, and batarian.\n"
             + " I'm quite good at genetics (as a subset of biology),\n"
             + " because I am an expert which I know is a tautology!\"\n"
             + "Collector Recon Drone: Audio stored in genetic archives. High cultural value.\n"
             + "==============================================================\n";
    }

    public static String getGarrusCalibrations() {
        return "\n==============================================================\n"
             + "  [PALAVEN DEXTRO-BATTERY TELEMETRY: GARRUS VAKARIAN] \n"
             + "==============================================================\n"
             + "Automated Target Lock Intercepted...\n"
             + "Battery Officer: \"Commander, we have incoming dreadnought signatures!\"\n"
             + "Garrus: \"Can it wait for a bit? I'm in the middle of some calibrations.\n"
             + "Wait... never mind, main gun is primed. Let's see how they like Thanix rounds.\"\n"
             + "Reaper Kinetic Shielding recalibrated by +10% in respect.\n"
             + "==============================================================\n";
    }

    public static String getBlastoTheSpectre() {
        return "\n==============================================================\n"
             + "  [CITADEL NEWS NETWORK INTERCEPT: BLASTO THE HANAR SPECTRE] \n"
             + "==============================================================\n"
             + "\"Blasto 6: Enkindle This! The Galaxy's Greatest Action Film.\"\n"
             + "Blasto: \"This one has forgotten whether its particle beam has expended\n"
             + "all thermal clips, or if it retains one lethal discharge.\n"
             + "You must interrogate yourself: does this criminal feel fortuitous?\"\n"
             + "Harbinger: *\"IRRATIONAL MOLLUSK DETECTED. PURGE FROM HISTORICAL RECORD.\"*\n"
             + "==============================================================\n";
    }

    public static String getSpaceHamster() {
        return "\n==============================================================\n"
             + "  [FLAGSHIP CARGO POD 04 DISCOVERY: MINIATURE GIANT SPACE HAMSTER] \n"
             + "==============================================================\n"
             + "Opening sealed environmental container...\n"
             + "Inside sits a fluffy rodent with brown and white fur.\n"
             + "Hamster: *SQUEAK!*\n"
             + "The tiny creature gazes into the ancient synthetic mind of Harbinger.\n"
             + "Harbinger: *\"AN UNKNOWN COSMIC INTELLIGENCE. WE WILL PERMIT ITS EXISTENCE.\"*\n"
             + "==============================================================\n";
    }

    public static String getMarauderShields() {
        return "\n==============================================================\n"
             + "  [TACTICAL MONUMENT: THE LEGEND OF MARAUDER SHIELDS] \n"
             + "==============================================================\n"
             + "Location: London Conduit Mass Accelerator Beam.\n"
             + "A lone Turian Marauder stands before the final ascension beam,\n"
             + "firing his phaeston rifle until his barriers shatter.\n"
             + "\"He died trying to save you from the ending.\"\n"
             + "Never forgotten in galactic lore.\n"
             + "==============================================================\n";
    }

    public static String getConradVerner() {
        return "\n==============================================================\n"
             + "  [UNIDENTIFIED BOARDING CRAFT: CONRAD VERNER] \n"
             + "==============================================================\n"
             + "A civilian shuttle has docked haphazardly at Sovereign's sensor spire.\n"
             + "Conrad Verner: \"Oh wow! Are you guys the Reapers?! I'm Commander Shepard's\n"
             + "biggest fan! Can I get an autograph on my thermal clip?\"\n"
             + "Harbinger: *\"TACTICAL INCOMPREHENSION. TRACTOR-BEAMING CRAFT TO DEEP SPACE.\"*\n"
             + "==============================================================\n";
    }

    public static String checkRandomEasterEgg() {
        int roll = RNG.nextInt(100);
        if (roll < 10) return getShepardEncounter();
        if (roll < 20) return getMordinSong();
        if (roll < 30) return getGarrusCalibrations();
        if (roll < 40) return getBlastoTheSpectre();
        if (roll < 50) return getSpaceHamster();
        return null;
    }
}
