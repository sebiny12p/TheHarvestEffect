package com.seb.harvesteffect.engine;

import com.seb.harvesteffect.audio.SoundEffects;
import java.io.Serializable;
import java.util.*;

/**
 * SubSpaceScanner manages anomalous cosmic signals, encrypted transmissions,
 * and the frequency tuning mini-game for secret Mass Effect easter eggs.
 */
public class SubSpaceScanner implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class SignalTransmission implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String title;
        private final String source;
        private final double targetFrequencyMHz;
        private final String frequencyClue;
        private final String rawContent;
        private final int eezoReward;
        private final int biomassReward;
        private boolean discovered;
        private boolean decoded;

        public SignalTransmission(String id, String title, String source, double targetFrequencyMHz,
                                  String frequencyClue, String rawContent, int eezoReward, int biomassReward) {
            this.id = id;
            this.title = title;
            this.source = source;
            this.targetFrequencyMHz = targetFrequencyMHz;
            this.frequencyClue = frequencyClue;
            this.rawContent = rawContent;
            this.eezoReward = eezoReward;
            this.biomassReward = biomassReward;
            this.discovered = false;
            this.decoded = false;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getSource() { return source; }
        public double getTargetFrequencyMHz() { return targetFrequencyMHz; }
        public String getFrequencyClue() { return frequencyClue; }
        public String getRawContent() { return rawContent; }
        public int getEezoReward() { return eezoReward; }
        public int getBiomassReward() { return biomassReward; }
        public boolean isDiscovered() { return discovered; }
        public void setDiscovered(boolean discovered) { this.discovered = discovered; }
        public boolean isDecoded() { return decoded; }
        public void setDecoded(boolean decoded) { this.decoded = decoded; }
    }

    public static class DecodeResult {
        private final boolean locked;
        private final double signalStrength; // 0.0 to 1.0
        private final String feedback;
        private final SignalTransmission transmission;

        public DecodeResult(boolean locked, double signalStrength, String feedback, SignalTransmission transmission) {
            this.locked = locked;
            this.signalStrength = signalStrength;
            this.feedback = feedback;
            this.transmission = transmission;
        }

        public boolean isLocked() { return locked; }
        public double getSignalStrength() { return signalStrength; }
        public String getFeedback() { return feedback; }
        public SignalTransmission getTransmission() { return transmission; }
    }

    private final Map<String, SignalTransmission> signals;
    private String lastInterceptedId = null;

    public SubSpaceScanner() {
        signals = new LinkedHashMap<String, SignalTransmission>();

        // 1. Shepard Anomaly (Normandy decloak)
        signals.put("shepard", new SignalTransmission(
                "shepard",
                "The Shepard Anomaly",
                "SSV Normandy SR-2 // Stealth Comm Array",
                119.4,
                "Carrier wave detected in lower military VHF band (110 - 130 MHz)...",
                EasterEggManager.getShepardEncounter(),
                200, 100
        ));

        // 2. Mordin Solus (Sur'Kesh STG lab)
        signals.put("mordin", new SignalTransmission(
                "mordin",
                "Scientist Salarian (Dr. Mordin Solus)",
                "Sur'Kesh STG Bio-Lab Surveillance Channel",
                248.6,
                "Micro-wave genetics telemetry detected between 240 and 260 MHz...",
                EasterEggManager.getMordinSong(),
                150, 150
        ));

        // 3. Garrus Vakarian (Palaven calibrations)
        signals.put("garrus", new SignalTransmission(
                "garrus",
                "Garrus Vakarian's Calibrations",
                "Palaven Orbital Battery Defense Telemetry",
                384.2,
                "Dextro-frequency radar resonance detected between 370 and 395 MHz...",
                EasterEggManager.getGarrusCalibrations(),
                250, 50
        ));

        // 4. Miniature Giant Space Hamster
        signals.put("hamster", new SignalTransmission(
                "hamster",
                "Miniature Giant Space Hamster",
                "Flagship Cargo Pod 04 Environmental Ping",
                888.8,
                "High-frequency bio-acoustic squeak echoing in upper band (880 - 900 MHz)...",
                EasterEggManager.getSpaceHamster(),
                100, 300
        ));

        // 5. Blasto the Hanar Spectre
        signals.put("blasto", new SignalTransmission(
                "blasto",
                "Blasto the Hanar Spectre",
                "Citadel News Network Entertainment Holo-Feed",
                555.5,
                "Wide-spectrum mid-band commercial broadcast located between 540 and 570 MHz...",
                EasterEggManager.getBlastoTheSpectre(),
                180, 80
        ));

        // 6. Marauder Shields
        signals.put("marauder", new SignalTransmission(
                "marauder",
                "The Legend of Marauder Shields",
                "London Conduit Defense Relay Feed",
                714.0,
                "Emergency distress transponder pinging between 700 and 730 MHz...",
                EasterEggManager.getMarauderShields(),
                300, 200
        ));

        // 7. Conrad Verner
        signals.put("conrad", new SignalTransmission(
                "conrad",
                "Conrad Verner",
                "Civilian Shuttle Automated Beacon",
                104.2,
                "Low-frequency civilian FM communication link near 100 - 110 MHz...",
                EasterEggManager.getConradVerner(),
                80, 50
        ));
    }

    public boolean triggerSignal(String id) {
        SignalTransmission sig = signals.get(id);
        if (sig != null && !sig.isDiscovered()) {
            sig.setDiscovered(true);
            lastInterceptedId = id;
            SoundEffects.playRadioStatic(800);
            return true;
        }
        return false;
    }

    public boolean hasPendingUndecodedSignal() {
        for (SignalTransmission s : signals.values()) {
            if (s.isDiscovered() && !s.isDecoded()) {
                return true;
            }
        }
        return false;
    }

    public SignalTransmission getActivePendingSignal() {
        if (lastInterceptedId != null) {
            SignalTransmission last = signals.get(lastInterceptedId);
            if (last != null && last.isDiscovered() && !last.isDecoded()) {
                return last;
            }
        }
        for (SignalTransmission s : signals.values()) {
            if (s.isDiscovered() && !s.isDecoded()) {
                return s;
            }
        }
        return null;
    }

    public List<SignalTransmission> getAllDiscoveredSignals() {
        List<SignalTransmission> list = new ArrayList<SignalTransmission>();
        for (SignalTransmission s : signals.values()) {
            if (s.isDiscovered()) list.add(s);
        }
        return list;
    }

    public List<SignalTransmission> getDecodedArchive() {
        List<SignalTransmission> list = new ArrayList<SignalTransmission>();
        for (SignalTransmission s : signals.values()) {
            if (s.isDecoded()) list.add(s);
        }
        return list;
    }

    public DecodeResult attemptDecode(String id, double frequencyMHz, GalacticState state) {
        SignalTransmission sig = signals.get(id);
        if (sig == null) {
            return new DecodeResult(false, 0.0, "Signal identifier not recognized.", null);
        }
        if (sig.isDecoded()) {
            return new DecodeResult(true, 1.0, "Signal already decrypted in archives.", sig);
        }

        double delta = Math.abs(frequencyMHz - sig.getTargetFrequencyMHz());
        double strength = Math.max(0.05, 1.0 - (delta / 80.0));

        if (delta <= 1.5) { // Frequency lock achieved!
            sig.setDecoded(true);
            SoundEffects.playDecryptionChime();
            if (state != null) {
                state.addEezo(sig.getEezoReward());
                state.addBiomass(sig.getBiomassReward());
            }
            return new DecodeResult(true, 1.0,
                    String.format("[CARRIER LOCKED: %.1f MHz] Decryption Successful! Rewards: +%d Eezo, +%d Biomass.",
                            sig.getTargetFrequencyMHz(), sig.getEezoReward(), sig.getBiomassReward()),
                    sig);
        }

        SoundEffects.playRadioStatic(300);
        String dirHint = frequencyMHz < sig.getTargetFrequencyMHz()
                ? "Tuning is TOO LOW. Increase frequency toward higher bands."
                : "Tuning is TOO HIGH. Decrease frequency toward lower bands.";

        String strengthStr;
        if (delta < 10.0) {
            strengthStr = "CRITICAL RESONANCE (Very close - fine tune dial!)";
        } else if (delta < 30.0) {
            strengthStr = "STRONG HARMONIC (Approaching carrier wave)";
        } else if (delta < 80.0) {
            strengthStr = "WEAK SUB-SPACE LEAKAGE (Detecting faint fluctuations)";
        } else {
            strengthStr = "PURE STATIC (No signal detected on this band)";
        }

        return new DecodeResult(false, strength,
                String.format("[%s] Delta: %.1f MHz off target. %s", strengthStr, delta, dirHint),
                sig);
    }

    public SignalTransmission getSignal(String id) {
        return signals.get(id);
    }

    public SignalTransmission findNearestSignal(double frequencyMHz) {
        SignalTransmission nearest = null;
        double minDelta = Double.MAX_VALUE;
        for (SignalTransmission s : signals.values()) {
            double delta = Math.abs(frequencyMHz - s.getTargetFrequencyMHz());
            if (delta < minDelta) {
                minDelta = delta;
                nearest = s;
            }
        }
        return nearest;
    }

    public SignalTransmission findNearestUndecodedSignal(double frequencyMHz) {
        SignalTransmission nearest = null;
        double minDelta = Double.MAX_VALUE;
        for (SignalTransmission s : signals.values()) {
            if (s.isDecoded()) continue;
            double delta = Math.abs(frequencyMHz - s.getTargetFrequencyMHz());
            if (delta < minDelta) {
                minDelta = delta;
                nearest = s;
            }
        }
        return nearest;
    }

    public DecodeResult attemptDecodeAny(double frequencyMHz, GalacticState state) {
        // 1. Check if any undecoded signal is in lock range (<= 1.5 MHz)
        for (SignalTransmission s : signals.values()) {
            if (!s.isDecoded() && Math.abs(frequencyMHz - s.getTargetFrequencyMHz()) <= 1.5) {
                s.setDiscovered(true);
                return attemptDecode(s.getId(), frequencyMHz, state);
            }
        }
        // 2. Check if any already decoded signal is in lock range
        for (SignalTransmission s : signals.values()) {
            if (Math.abs(frequencyMHz - s.getTargetFrequencyMHz()) <= 1.5) {
                return attemptDecode(s.getId(), frequencyMHz, state);
            }
        }
        // 3. Proximity feedback to nearest undecoded signal (or nearest signal)
        SignalTransmission nearest = findNearestUndecodedSignal(frequencyMHz);
        if (nearest == null) nearest = findNearestSignal(frequencyMHz);
        if (nearest == null) {
            return new DecodeResult(false, 0.05, "No sub-space signals detected.", null);
        }
        double delta = Math.abs(frequencyMHz - nearest.getTargetFrequencyMHz());
        double strength = Math.max(0.05, 1.0 - (delta / 80.0));
        SoundEffects.playRadioStatic(250);
        String dirHint = frequencyMHz < nearest.getTargetFrequencyMHz()
                ? "Tuning is TOO LOW. Increase frequency toward higher bands."
                : "Tuning is TOO HIGH. Decrease frequency toward lower bands.";
        String strengthStr;
        if (delta < 10.0) {
            strengthStr = "CRITICAL RESONANCE (Very close - fine tune dial!)";
        } else if (delta < 30.0) {
            strengthStr = "STRONG HARMONIC (Approaching carrier wave)";
        } else if (delta < 80.0) {
            strengthStr = "WEAK SUB-SPACE LEAKAGE (Detecting faint fluctuations)";
        } else {
            strengthStr = "PURE STATIC (No signal detected on this band)";
        }
        return new DecodeResult(false, strength,
                String.format("[%s] Delta: %.1f MHz off target. %s", strengthStr, delta, dirHint),
                nearest);
    }

    public Collection<SignalTransmission> getAllSignals() {
        return Collections.unmodifiableCollection(signals.values());
    }
}
