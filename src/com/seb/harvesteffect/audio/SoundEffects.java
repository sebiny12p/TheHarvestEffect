package com.seb.harvesteffect.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Procedural sci-fi sound synthesizer built using standard javax.sound.sampled.
 * Generates retro-futuristic Reaper sound effects programmatically without external audio files.
 */
public class SoundEffects {
    private static volatile boolean soundEnabled = true;
    private static final float SAMPLE_RATE = 44100f;
    private static volatile boolean ambientRunning = false;
    private static Thread ambientThread = null;
    private static SourceDataLine ambientLine = null;
    private static final Object ambientLock = new Object();

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (enabled) {
            startAmbience();
        } else {
            stopAmbience();
        }
    }

    public static synchronized void startAmbience() {
        if (!soundEnabled) return;
        synchronized (ambientLock) {
            if (ambientRunning) return;
            ambientRunning = true;
            ambientThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runAmbienceLoop();
                }
            }, "ReaperAmbienceThread");
            ambientThread.setDaemon(true);
            ambientThread.start();
        }
    }

    public static synchronized void stopAmbience() {
        synchronized (ambientLock) {
            ambientRunning = false;
            if (ambientLine != null) {
                try {
                    ambientLine.stop();
                    ambientLine.flush();
                    ambientLine.close();
                } catch (Exception ignored) {}
                ambientLine = null;
            }
            ambientThread = null;
        }
    }

    public static boolean isAmbienceRunning() {
        return ambientRunning;
    }

    public static void playReaperHorn() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Deep, terrifying low-frequency Reaper war horn (60Hz to 90Hz sawtooth blend)
                    int durationMs = 1200;
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];

                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        // Dual low-frequency wave with slow decay
                        double envelope = Math.max(0.0, 1.0 - (time / 1.2));
                        double wave1 = Math.sin(2 * Math.PI * 65.0 * time);
                        double wave2 = Math.sin(2 * Math.PI * 130.0 * time) * 0.5; // octave harmonic
                        double wave3 = (Math.sin(2 * Math.PI * 195.0 * time) > 0 ? 1 : -1) * 0.2; // sawtooth distortion
                        double sample = (wave1 + wave2 + wave3) * envelope * 100.0;
                        buffer[i] = (byte) Math.max(-128, Math.min(127, (int) sample));
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playRelayChime() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Resonant high-tech FTL transit chime (ascending triad)
                    int durationMs = 450;
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];

                    double[] freqs = { 587.33, 880.0, 1174.66 }; // D5, A5, D6
                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        int noteIdx = Math.min(2, (int) (time / 0.15));
                        double freq = freqs[noteIdx];
                        double envelope = Math.exp(-6.0 * (time % 0.15));
                        double sample = Math.sin(2 * Math.PI * freq * time) * envelope * 90.0;
                        buffer[i] = (byte) Math.max(-128, Math.min(127, (int) sample));
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playHarvestPulse() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Descending energetic biotic purge pulse
                    int durationMs = 600;
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];

                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        double freq = 450.0 - (350.0 * (time / 0.6));
                        double envelope = Math.sin(Math.PI * (time / 0.6));
                        double sample = Math.sin(2 * Math.PI * freq * time) * envelope * 110.0;
                        buffer[i] = (byte) Math.max(-128, Math.min(127, (int) sample));
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playCrucibleAlert() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Threat alarm warble
                    int durationMs = 500;
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];

                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        double freq = (i % 4000 < 2000) ? 650.0 : 850.0;
                        double sample = Math.sin(2 * Math.PI * freq * time) * 80.0;
                        buffer[i] = (byte) Math.max(-128, Math.min(127, (int) sample));
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playUiClick() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int numSamples = (int) (SAMPLE_RATE * 0.03); // 30ms chirp
                    byte[] buffer = new byte[numSamples];
                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        double sample = Math.sin(2 * Math.PI * 1200.0 * time) * (1.0 - time / 0.03) * 50.0;
                        buffer[i] = (byte) sample;
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playRadioStatic(final int durationMs) {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];
                    java.util.Random rnd = new java.util.Random();
                    for (int i = 0; i < numSamples; i++) {
                        buffer[i] = (byte) (rnd.nextInt(70) - 35);
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void playDecryptionChime() {
        if (!soundEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int durationMs = 600;
                    int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0));
                    byte[] buffer = new byte[numSamples];
                    double[] notes = { 523.25, 659.25, 783.99, 1046.50 }; // C5, E5, G5, C6
                    for (int i = 0; i < numSamples; i++) {
                        double time = i / SAMPLE_RATE;
                        int noteIdx = Math.min(3, (int) (time / 0.14));
                        double freq = notes[noteIdx];
                        double env = Math.exp(-7.0 * (time % 0.14));
                        double sample = Math.sin(2 * Math.PI * freq * time) * env * 95.0;
                        buffer[i] = (byte) Math.max(-128, Math.min(127, (int) sample));
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    private static void playBuffer(byte[] buffer) {
        SourceDataLine line = null;
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
        } catch (Exception ignored) {
        } finally {
            if (line != null) {
                try {
                    line.close();
                } catch (Exception ignored) {}
            }
        }
    }

    private static void runAmbienceLoop() {
        SourceDataLine line = null;
        try {
            float ambSampleRate = 44100f;
            AudioFormat format = new AudioFormat(ambSampleRate, 16, 1, true, false);
            line = AudioSystem.getSourceDataLine(format);
            line.open(format, 16384);
            line.start();
            synchronized (ambientLock) {
                ambientLine = line;
            }

            int numSamples = 2048;
            byte[] buf = new byte[numSamples * 2];
            long sampleIndex = 0;

            // Sci-Fi Analog Synth Chord Progression (Dm9 -> BbMaj7 -> F/C -> Gm9)
            // Lush, cinematic space chords reminiscent of classic sci-fi space exploration themes
            double[][] chordNotes = {
                { 73.42, 146.83, 174.61, 220.00, 261.63, 329.63 }, // Dm9 (Deep dark space)
                { 58.27, 146.83, 174.61, 220.00, 293.66, 349.23 }, // BbMaj7 (Mass Relay wonder)
                { 65.41, 130.81, 164.81, 196.00, 261.63, 329.63 }, // F/C (Galactic expanse)
                { 49.00, 146.83, 196.00, 233.08, 293.66, 329.63 }  // Gm9 (Ancient Reaper awakening)
            };

            // Ethereal arpeggiated space chime frequencies matching each chord
            double[][] arpeggioSets = {
                { 440.00, 523.25, 587.33, 659.25 }, // Dm: A4, C5, D5, E5
                { 466.16, 587.33, 698.46, 880.00 }, // Bb: Bb4, D5, F5, A5
                { 523.25, 659.25, 783.99, 659.25 }, // F: C5, E5, G5, E5
                { 392.00, 466.16, 587.33, 659.25 }  // Gm: G4, Bb4, D5, E5
            };

            double chordDuration = 8.0; // 8.0s per chord (32-second full evolving progression)
            double fadeTime = 2.0;       // 2.0s smooth equal-power crossfade
            double[] phasesPad1 = new double[6];
            double[] phasesPad2 = new double[6];
            double pluckPhase = 0;
            double currentPluckFreq = 440.0;
            int lastPluckStep = -1;

            while (ambientRunning && soundEnabled) {
                for (int i = 0; i < numSamples; i++) {
                    double t = sampleIndex / (double) ambSampleRate;
                    double loopT = t % (chordDuration * 4);
                    int chordIdx = (int) (loopT / chordDuration);
                    int nextChordIdx = (chordIdx + 1) % 4;
                    double chordProgress = (loopT - (chordIdx * chordDuration)) / chordDuration;

                    // Equal-power sinusoidal crossfade between chords
                    double crossfade = 0.0;
                    double fadeThreshold = 1.0 - (fadeTime / chordDuration);
                    if (chordProgress > fadeThreshold) {
                        double p = (chordProgress - fadeThreshold) / (fadeTime / chordDuration);
                        crossfade = 0.5 * (1.0 - Math.cos(p * Math.PI));
                    }

                    double padSample = 0;
                    double[] c1 = chordNotes[chordIdx];
                    double[] c2 = chordNotes[nextChordIdx];

                    for (int v = 0; v < 6; v++) {
                        phasesPad1[v] += 2.0 * Math.PI * c1[v] / ambSampleRate;
                        phasesPad2[v] += 2.0 * Math.PI * c2[v] / ambSampleRate;
                        if (phasesPad1[v] > 6283.185) phasesPad1[v] -= 6283.185;
                        if (phasesPad2[v] > 6283.185) phasesPad2[v] -= 6283.185;

                        // Analog synthesizer voice: fundamental + detuned chorus + gentle 2nd harmonic
                        double v1 = Math.sin(phasesPad1[v]) * 0.55
                                  + Math.sin(phasesPad1[v] * 1.0035) * 0.35
                                  + Math.sin(phasesPad1[v] * 2.0) * 0.10;

                        double v2 = Math.sin(phasesPad2[v]) * 0.55
                                  + Math.sin(phasesPad2[v] * 1.0035) * 0.35
                                  + Math.sin(phasesPad2[v] * 2.0) * 0.10;

                        double blend = v1 * (1.0 - crossfade) + v2 * crossfade;
                        double weight = (v == 0) ? 0.30 : 0.14;
                        padSample += blend * weight;
                    }

                    // Slow cosmic breathing LFO (0.08 Hz)
                    double lfo = 0.85 + 0.15 * Math.sin(2 * Math.PI * 0.08 * t);
                    padSample *= lfo;

                    // Ambient crystalline arpeggio note every 1.0 second (pure sci-fi melody)
                    double noteRate = 1.0;
                    int pluckStep = (int) (t / noteRate);
                    double noteT = (t - (pluckStep * noteRate));
                    if (pluckStep != lastPluckStep) {
                        lastPluckStep = pluckStep;
                        int noteIdx = pluckStep % 4;
                        currentPluckFreq = arpeggioSets[chordIdx][noteIdx];
                    }
                    pluckPhase += 2.0 * Math.PI * currentPluckFreq / ambSampleRate;
                    if (pluckPhase > 6283.185) pluckPhase -= 6283.185;
                    double pluckEnv = Math.exp(-3.2 * noteT) * (1.0 - Math.exp(-35.0 * noteT));
                    double pluckSample = (Math.sin(pluckPhase) + 0.15 * Math.sin(pluckPhase * 3.0)) * pluckEnv * 0.22;

                    // Composite soundtrack: warm analog pads + starry arpeggios (zero noise/static)
                    double total = (padSample * 0.78 + pluckSample * 0.22);
                    short sampleShort = (short) Math.max(-32767, Math.min(32767, (int) Math.round(total * 14000.0)));
                    buf[i * 2] = (byte) (sampleShort & 0xFF);
                    buf[i * 2 + 1] = (byte) ((sampleShort >> 8) & 0xFF);
                    sampleIndex++;
                }
                line.write(buf, 0, buf.length);
            }
        } catch (Exception ignored) {
        } finally {
            if (line != null) {
                try {
                    line.stop();
                    line.close();
                } catch (Exception ignored) {}
            }
            synchronized (ambientLock) {
                if (ambientLine == line) ambientLine = null;
                ambientRunning = false;
            }
        }
    }
}
