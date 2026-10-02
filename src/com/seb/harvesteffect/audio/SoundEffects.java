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
            float ambSampleRate = 22050f;
            AudioFormat format = new AudioFormat(ambSampleRate, 8, 1, true, true);
            line = AudioSystem.getSourceDataLine(format);
            line.open(format, 4096);
            line.start();
            synchronized (ambientLock) {
                ambientLine = line;
            }

            int bufSize = 2048;
            byte[] buf = new byte[bufSize];
            long sampleIndex = 0;
            java.util.Random rnd = new java.util.Random();

            while (ambientRunning && soundEnabled) {
                for (int i = 0; i < bufSize; i++) {
                    double t = sampleIndex / (double) ambSampleRate;
                    // Dark sub-bass binaural drone (44.0 Hz + 46.5 Hz beating at 2.5 Hz)
                    double wave1 = Math.sin(2 * Math.PI * 44.0 * t);
                    double wave2 = Math.sin(2 * Math.PI * 46.5 * t) * 0.75;
                    // Deep dark harmonic resonance at 88 Hz & 132 Hz
                    double wave3 = Math.sin(2 * Math.PI * 88.0 * t) * 0.25;
                    double wave4 = Math.sin(2 * Math.PI * 132.0 * t) * 0.12;
                    // Slow 7-second cosmic pulse (LFO)
                    double lfo = 0.65 + 0.35 * Math.sin(2 * Math.PI * 0.14 * t);
                    // Extremely gentle pink-noise cosmic whisper
                    double noise = (rnd.nextDouble() - 0.5) * 0.12;
                    double sample = (wave1 + wave2 + wave3 + wave4 + noise) * lfo * 14.0;
                    buf[i] = (byte) Math.max(-128, Math.min(127, (int) Math.round(sample)));
                    sampleIndex++;
                }
                line.write(buf, 0, bufSize);
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
