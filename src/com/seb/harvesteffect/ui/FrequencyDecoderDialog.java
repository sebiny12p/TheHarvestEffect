package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.audio.SoundEffects;
import com.seb.harvesteffect.engine.GalacticState;
import com.seb.harvesteffect.engine.SubSpaceScanner;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Interactive sub-space frequency tuning and signal decryption minigame dialog.
 * Replaces the static easter egg cheat menu with an authentic sci-fi frequency dial.
 */
public class FrequencyDecoderDialog extends JDialog {
    private SubSpaceScanner.SignalTransmission transmission;
    private final SubSpaceScanner scanner;
    private final GalacticState state;
    private boolean isUpdatingFromSlider = false;
    private boolean isUpdatingFromText = false;

    private JLabel lblHeaderTitle;
    private JLabel lblSource;
    private JLabel lblClue;
    private JLabel lblFreqDisplay;
    private JTextField txtDirectFreq;
    private JSlider sldFreq;
    private JProgressBar barSignalStrength;
    private JLabel lblFeedback;
    private JTextArea txtDecryptedContent;
    private JButton btnLock;

    public FrequencyDecoderDialog(JFrame parent, SubSpaceScanner scanner, GalacticState state) {
        this(parent, scanner != null ? scanner.getActivePendingSignal() : null, scanner, state);
    }

    public FrequencyDecoderDialog(JFrame parent, SubSpaceScanner.SignalTransmission transmission,
                                  SubSpaceScanner scanner, GalacticState state) {
        super(parent, "Sub-Space Signal Frequency Decoder & Scanner", true);
        this.transmission = transmission;
        this.scanner = scanner;
        this.state = state;

        buildUI();
        setSize(860, 700);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(10, 14, 22));
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // 1. Header & Source Banner
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.setBackground(new Color(16, 22, 34));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 170, 30), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        lblHeaderTitle = new JLabel(
                (transmission != null)
                        ? "⚡ [ENCRYPTED SUB-SPACE TRANSMISSION INTERCEPTED]"
                        : "⚡ [SUB-SPACE FREQUENCY RECEIVER & SCANNER ACTIVE]",
                SwingConstants.CENTER);
        lblHeaderTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblHeaderTitle.setForeground(new Color(255, 180, 40));

        lblSource = new JLabel(
                (transmission != null)
                        ? "SOURCE: " + transmission.getSource()
                        : "SOURCE: WIDE-SPECTRUM REAPER INTERCEPT ARRAY (100.0 — 999.0 MHz)",
                SwingConstants.CENTER);
        lblSource.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSource.setForeground(new Color(180, 220, 255));

        lblClue = new JLabel(
                (transmission != null)
                        ? "CLUE: " + transmission.getFrequencyClue()
                        : "CLUE: Enter target frequency or tune the dial to locate carrier waves across the galaxy.",
                SwingConstants.CENTER);
        lblClue.setFont(new Font("Monospaced", Font.ITALIC, 11));
        lblClue.setForeground(new Color(140, 240, 200));

        headerPanel.add(lblHeaderTitle);
        headerPanel.add(lblSource);
        headerPanel.add(lblClue);
        content.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Tuning Deck
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(14, 18, 28));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 180, 230)),
                        "HARBINGER SUB-SPACE FREQUENCY TUNER (100.0 — 999.0 MHz)",
                        0, 0, new Font("Monospaced", Font.BOLD, 12), new Color(0, 200, 255)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Determine initial frequency
        double initFreq = 450.0;
        if (transmission != null && !transmission.isDecoded()) {
            initFreq = transmission.getTargetFrequencyMHz() + 3.0;
            if (initFreq > 999.0) initFreq = transmission.getTargetFrequencyMHz() - 3.0;
        }
        int initSliderVal = (int) Math.round(initFreq * 10.0);

        // Big Frequency Readout
        lblFreqDisplay = new JLabel(String.format(java.util.Locale.US, "%.1f MHz", initFreq), SwingConstants.CENTER);
        lblFreqDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFreqDisplay.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblFreqDisplay.setForeground(new Color(0, 240, 255));

        // Direct Frequency Input Deck
        JPanel directInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        directInputPanel.setOpaque(false);

        JLabel lblDirectPrompt = new JLabel("Direct Frequency Entry:");
        lblDirectPrompt.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblDirectPrompt.setForeground(new Color(180, 220, 255));

        txtDirectFreq = new JTextField(String.format(java.util.Locale.US, "%.1f", initFreq), 6);
        txtDirectFreq.setFont(new Font("Monospaced", Font.BOLD, 15));
        txtDirectFreq.setHorizontalAlignment(JTextField.CENTER);
        txtDirectFreq.setBackground(new Color(25, 35, 50));
        txtDirectFreq.setForeground(new Color(0, 255, 200));
        txtDirectFreq.setCaretColor(Color.CYAN);
        txtDirectFreq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 230), 1),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));

        // Live typing synchronization
        txtDirectFreq.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { syncTextToSlider(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { syncTextToSlider(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { syncTextToSlider(); }

            private void syncTextToSlider() {
                if (isUpdatingFromSlider) return;
                String text = txtDirectFreq.getText().trim().replace(',', '.');
                try {
                    double freq = Double.parseDouble(text);
                    if (freq >= 100.0 && freq <= 999.0) {
                        isUpdatingFromText = true;
                        try {
                            int sliderVal = (int) Math.round(freq * 10.0);
                            sldFreq.setValue(sliderVal);
                            lblFreqDisplay.setText(String.format(java.util.Locale.US, "%.1f MHz", freq));
                            updateSignalStrengthPreview(freq);
                        } finally {
                            isUpdatingFromText = false;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        JLabel lblMHz = new JLabel("MHz");
        lblMHz.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblMHz.setForeground(new Color(180, 220, 255));

        JButton btnDirectTune = new JButton("📻 Tune Receiver");
        btnDirectTune.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnDirectTune.setBackground(new Color(0, 130, 190));
        btnDirectTune.setForeground(Color.WHITE);
        btnDirectTune.setFocusPainted(false);

        ActionListener tuneAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyDirectFrequencyInput();
            }
        };
        txtDirectFreq.addActionListener(tuneAction);
        btnDirectTune.addActionListener(tuneAction);

        directInputPanel.add(lblDirectPrompt);
        directInputPanel.add(txtDirectFreq);
        directInputPanel.add(lblMHz);
        directInputPanel.add(btnDirectTune);

        // Tuning Slider
        sldFreq = new JSlider(1000, 9990, initSliderVal); // 100.0 to 999.0 MHz in 0.1 increments
        sldFreq.setBackground(new Color(14, 18, 28));
        sldFreq.setForeground(new Color(150, 200, 230));
        sldFreq.setMajorTickSpacing(1500);
        sldFreq.setMinorTickSpacing(250);
        sldFreq.setPaintTicks(true);
        sldFreq.setPaintLabels(false);

        sldFreq.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (isUpdatingFromText) return;
                isUpdatingFromSlider = true;
                try {
                    double freq = sldFreq.getValue() / 10.0;
                    lblFreqDisplay.setText(String.format(java.util.Locale.US, "%.1f MHz", freq));
                    if (!txtDirectFreq.hasFocus()) {
                        txtDirectFreq.setText(String.format(java.util.Locale.US, "%.1f", freq));
                    }
                    updateSignalStrengthPreview(freq);
                } finally {
                    isUpdatingFromSlider = false;
                }
            }
        });

        // Fine Tuning Buttons
        JPanel fineTunePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        fineTunePanel.setOpaque(false);
        JButton btnMinus10 = createTuneButton("-10.0 MHz", -100);
        JButton btnMinus1 = createTuneButton("-1.0 MHz", -10);
        JButton btnMinusPoint1 = createTuneButton("-0.1 MHz", -1);
        JButton btnPlusPoint1 = createTuneButton("+0.1 MHz", 1);
        JButton btnPlus1 = createTuneButton("+1.0 MHz", 10);
        JButton btnPlus10 = createTuneButton("+10.0 MHz", 100);
        fineTunePanel.add(btnMinus10);
        fineTunePanel.add(btnMinus1);
        fineTunePanel.add(btnMinusPoint1);
        fineTunePanel.add(btnPlusPoint1);
        fineTunePanel.add(btnPlus1);
        fineTunePanel.add(btnPlus10);

        // Signal Strength Bar
        barSignalStrength = new JProgressBar(0, 100);
        barSignalStrength.setValue(10);
        barSignalStrength.setStringPainted(true);
        barSignalStrength.setString("SIGNAL STRENGTH: 10% [PURE STATIC]");
        barSignalStrength.setForeground(new Color(220, 50, 50));
        barSignalStrength.setBackground(new Color(30, 20, 25));

        lblFeedback = new JLabel("Enter target frequency above or adjust slider/fine-tune buttons to locate carrier wave.", SwingConstants.CENTER);
        lblFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFeedback.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblFeedback.setForeground(new Color(180, 210, 230));

        // Decrypted Text Area
        txtDecryptedContent = new JTextArea(8, 50);
        txtDecryptedContent.setEditable(false);
        txtDecryptedContent.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDecryptedContent.setBackground(new Color(8, 12, 18));
        txtDecryptedContent.setForeground(new Color(150, 180, 200));
        txtDecryptedContent.setText("=== ENCRYPTED SIGNAL BUFFER LOCKED ===\nCarrier wave unaligned. Match target broadcast frequency to decrypt audio transcript.");
        JScrollPane scroll = new JScrollPane(txtDecryptedContent);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 65)),
                "Decrypted Transcript Feed", 0, 0, new Font("Monospaced", Font.PLAIN, 10), new Color(130, 160, 190)));

        centerPanel.add(lblFreqDisplay);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(directInputPanel);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(sldFreq);
        centerPanel.add(fineTunePanel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(barSignalStrength);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(lblFeedback);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(scroll);
        content.add(centerPanel, BorderLayout.CENTER);

        // 3. Action Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        footer.setBackground(new Color(12, 16, 24));

        btnLock = new JButton("📡 ATTEMPT DECRYPTION & PHASE LOCK");
        btnLock.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLock.setBackground(new Color(0, 140, 210));
        btnLock.setForeground(Color.WHITE);
        btnLock.setFocusPainted(false);
        btnLock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeDecodeAttempt();
            }
        });

        JButton btnArchives = new JButton("📖 Frequency Log & Clues");
        btnArchives.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnArchives.setBackground(new Color(40, 65, 95));
        btnArchives.setForeground(Color.WHITE);
        btnArchives.setFocusPainted(false);
        btnArchives.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFrequencyLogAndCluesDialog();
            }
        });

        JButton btnClose = new JButton("Close Scanner");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        footer.add(btnArchives);
        footer.add(btnLock);
        footer.add(btnClose);
        content.add(footer, BorderLayout.SOUTH);

        add(content);
        updateSignalStrengthPreview(initFreq);
    }

    private void applyDirectFrequencyInput() {
        if (txtDirectFreq == null) return;
        String text = txtDirectFreq.getText().trim().replace(',', '.');
        try {
            double freq = Double.parseDouble(text);
            if (freq < 100.0 || freq > 999.0) {
                JOptionPane.showMessageDialog(this,
                        "Frequency out of range!\nPlease enter a frequency between 100.0 and 999.0 MHz.",
                        "Out of Range", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int sliderVal = (int) Math.round(freq * 10.0);
            isUpdatingFromText = true;
            try {
                sldFreq.setValue(sliderVal);
                double clamped = sliderVal / 10.0;
                lblFreqDisplay.setText(String.format(java.util.Locale.US, "%.1f MHz", clamped));
                txtDirectFreq.setText(String.format(java.util.Locale.US, "%.1f", clamped));
                updateSignalStrengthPreview(clamped);
            } finally {
                isUpdatingFromText = false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid frequency format: '" + text + "'.\nPlease enter a valid number (e.g. 119.4).",
                    "Invalid Format", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createTuneButton(String label, final int delta) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 10));
        btn.setBackground(new Color(25, 35, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int next = Math.max(1000, Math.min(9990, sldFreq.getValue() + delta));
                sldFreq.setValue(next);
            }
        });
        return btn;
    }

    private void updateSignalStrengthPreview(double freq) {
        SubSpaceScanner.SignalTransmission nearest = scanner.findNearestSignal(freq);
        SubSpaceScanner.SignalTransmission nearestUndecoded = scanner.findNearestUndecodedSignal(freq);

        // 1. If within carrier lock range (1.5 MHz) of ANY known transmission
        if (nearest != null && Math.abs(freq - nearest.getTargetFrequencyMHz()) <= 1.5) {
            if (nearest.isDecoded()) {
                barSignalStrength.setValue(100);
                barSignalStrength.setString(String.format(java.util.Locale.US, "SIGNAL STRENGTH: 100%% [ARCHIVED CARRIER: %.1f MHz]", freq));
                barSignalStrength.setForeground(new Color(0, 220, 255));
                lblFeedback.setText("✓ " + nearest.getTitle() + " - Already decrypted in archives.");
                lblFeedback.setForeground(new Color(0, 240, 255));
                txtDecryptedContent.setText(nearest.getRawContent());
                txtDecryptedContent.setForeground(new Color(0, 255, 150));
                txtDecryptedContent.setCaretPosition(0);
                btnLock.setEnabled(false);
                btnLock.setText("✓ ALREADY DECRYPTED");
                btnLock.setBackground(new Color(30, 80, 50));
                return;
            } else {
                barSignalStrength.setValue(100);
                barSignalStrength.setString(String.format(java.util.Locale.US, "SIGNAL STRENGTH: 100%% [PHASE LOCK READY: %.1f MHz]", freq));
                barSignalStrength.setForeground(new Color(0, 255, 100));
                lblFeedback.setText("✦ CARRIER DETECTED: " + nearest.getTitle() + "! Press 'ATTEMPT DECRYPTION' to lock and decrypt!");
                lblFeedback.setForeground(new Color(0, 255, 120));
                btnLock.setEnabled(true);
                btnLock.setText("📡 ATTEMPT DECRYPTION & PHASE LOCK");
                btnLock.setBackground(new Color(0, 140, 210));
                return;
            }
        }

        btnLock.setEnabled(true);
        btnLock.setText("📡 ATTEMPT DECRYPTION & PHASE LOCK");
        btnLock.setBackground(new Color(0, 140, 210));

        // 2. Proximity feedback guided toward nearest undecoded signal (or nearest signal)
        SubSpaceScanner.SignalTransmission target = (nearestUndecoded != null) ? nearestUndecoded : nearest;
        double delta = (target != null) ? Math.abs(freq - target.getTargetFrequencyMHz()) : 999.0;
        int strength = (int) Math.max(5, Math.min(100, (1.0 - (delta / 80.0)) * 100));
        barSignalStrength.setValue(strength);

        if (delta < 15.0) {
            barSignalStrength.setString(String.format(java.util.Locale.US, "SIGNAL STRENGTH: %d%% [CRITICAL HARMONIC RESONANCE]", strength));
            barSignalStrength.setForeground(new Color(255, 200, 50));
            String hint = (target != null && freq < target.getTargetFrequencyMHz()) ? "Tune higher (+)." : "Tune lower (-).";
            lblFeedback.setText("Critical resonance! " + hint + " Fine-tune by ±0.1 or ±1.0 MHz.");
            lblFeedback.setForeground(new Color(255, 220, 80));
        } else if (delta < 50.0) {
            barSignalStrength.setString(String.format(java.util.Locale.US, "SIGNAL STRENGTH: %d%% [WEAK SUB-SPACE LEAKAGE]", strength));
            barSignalStrength.setForeground(new Color(255, 130, 40));
            String hint = (target != null && freq < target.getTargetFrequencyMHz()) ? "Tune higher (+)." : "Tune lower (-).";
            lblFeedback.setText("Sub-space carrier detected nearby. " + hint);
            lblFeedback.setForeground(new Color(255, 160, 80));
        } else {
            barSignalStrength.setString(String.format(java.util.Locale.US, "SIGNAL STRENGTH: %d%% [STATIC NOISE]", strength));
            barSignalStrength.setForeground(new Color(180, 50, 50));
            String dir = (target != null && freq < target.getTargetFrequencyMHz()) ? "higher" : "lower";
            lblFeedback.setText("Static on " + String.format(java.util.Locale.US, "%.1f", freq) + " MHz. Enter known freq (e.g. 119.4, 714.0) or tune " + dir + ".");
            lblFeedback.setForeground(new Color(180, 120, 130));
        }
    }

    private void executeDecodeAttempt() {
        applyDirectFrequencyInput();
        double currentFreq = sldFreq.getValue() / 10.0;
        SubSpaceScanner.DecodeResult res = scanner.attemptDecodeAny(currentFreq, state);

        lblFeedback.setText(res.getFeedback());
        if (res.isLocked()) {
            this.transmission = res.getTransmission();
            lblHeaderTitle.setText("⚡ [CARRIER FREQUENCY LOCKED & DECRYPTED]");
            lblSource.setText("SOURCE: " + transmission.getSource());
            lblClue.setText("CLUE: " + transmission.getFrequencyClue());
            txtDecryptedContent.setText(transmission.getRawContent());
            txtDecryptedContent.setForeground(new Color(0, 255, 150));
            txtDecryptedContent.setCaretPosition(0);
            btnLock.setEnabled(false);
            btnLock.setText("✓ DECRYPTED & LOGGED IN ARCHIVE");
            btnLock.setBackground(new Color(30, 100, 50));
            updateSignalStrengthPreview(currentFreq);
            JOptionPane.showMessageDialog(this,
                    "CARRIER FREQUENCY LOCKED (" + String.format(java.util.Locale.US, "%.1f", transmission.getTargetFrequencyMHz()) + " MHz)!\n\n"
                    + "Transmission decrypted: " + transmission.getTitle() + "\n\n"
                    + String.format("Rewards: +%d Eezo, +%d Biomass deposited into fleet reserves.",
                            transmission.getEezoReward(), transmission.getBiomassReward()),
                    "Decryption Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            updateSignalStrengthPreview(currentFreq);
        }
    }

    private void showFrequencyLogAndCluesDialog() {
        StringBuilder sb = new StringBuilder("=== SUB-SPACE FREQUENCY TELEMETRY ARCHIVE ===\n\n");
        sb.append("Tuning Band: 100.0 MHz to 999.0 MHz\n\n");
        for (SubSpaceScanner.SignalTransmission sig : scanner.getAllSignals()) {
            if (sig.isDecoded()) {
                sb.append(String.format("✓ [DECRYPTED - %.1f MHz] %s\n", sig.getTargetFrequencyMHz(), sig.getTitle()));
                sb.append(String.format("   Source: %s\n\n", sig.getSource()));
            } else if (sig.isDiscovered()) {
                sb.append(String.format("⚡ [INTERCEPTED - PENDING] %s\n", sig.getTitle()));
                sb.append(String.format("   Source: %s\n", sig.getSource()));
                sb.append(String.format("   Clue: %s\n\n", sig.getFrequencyClue()));
            } else {
                sb.append(String.format("? [ENCRYPTED SIGNAL] %s\n", sig.getTitle()));
                sb.append(String.format("   Clue: %s\n\n", sig.getFrequencyClue()));
            }
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append("HINT: Enter any target frequency in 'Direct Frequency Entry' and\n");
        sb.append("click '📻 Tune Receiver' (or press Enter) to jump straight to that frequency!");

        JTextArea area = new JTextArea(sb.toString(), 22, 60);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(12, 16, 25));
        area.setForeground(new Color(210, 235, 255));
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Sub-Space Frequency Archives & Lore Clues", JOptionPane.PLAIN_MESSAGE);
    }
}
