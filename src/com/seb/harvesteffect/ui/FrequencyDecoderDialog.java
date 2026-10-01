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
    private final SubSpaceScanner.SignalTransmission transmission;
    private final SubSpaceScanner scanner;
    private final GalacticState state;

    private JLabel lblFreqDisplay;
    private JSlider sldFreq;
    private JProgressBar barSignalStrength;
    private JLabel lblFeedback;
    private JTextArea txtDecryptedContent;
    private JButton btnLock;

    public FrequencyDecoderDialog(JFrame parent, SubSpaceScanner.SignalTransmission transmission,
                                  SubSpaceScanner scanner, GalacticState state) {
        super(parent, "Sub-Space Signal Frequency Decoder", true);
        this.transmission = transmission;
        this.scanner = scanner;
        this.state = state;

        buildUI();
        setSize(700, 620);
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

        JLabel lblTitle = new JLabel("⚡ [ENCRYPTED SUB-SPACE TRANSMISSION DETECTED]", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitle.setForeground(new Color(255, 180, 40));

        JLabel lblSource = new JLabel("SOURCE: " + transmission.getSource(), SwingConstants.CENTER);
        lblSource.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSource.setForeground(new Color(180, 220, 255));

        JLabel lblClue = new JLabel("CLUE: " + transmission.getFrequencyClue(), SwingConstants.CENTER);
        lblClue.setFont(new Font("Monospaced", Font.ITALIC, 11));
        lblClue.setForeground(new Color(140, 240, 200));

        headerPanel.add(lblTitle);
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

        // Big Frequency Readout
        lblFreqDisplay = new JLabel("450.0 MHz", SwingConstants.CENTER);
        lblFreqDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFreqDisplay.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblFreqDisplay.setForeground(new Color(0, 240, 255));

        // Tuning Slider
        sldFreq = new JSlider(1000, 9990, 4500); // 100.0 to 999.0 MHz in 0.1 increments
        sldFreq.setBackground(new Color(14, 18, 28));
        sldFreq.setForeground(new Color(150, 200, 230));
        sldFreq.setMajorTickSpacing(1500);
        sldFreq.setMinorTickSpacing(250);
        sldFreq.setPaintTicks(true);
        sldFreq.setPaintLabels(false);

        sldFreq.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double freq = sldFreq.getValue() / 10.0;
                lblFreqDisplay.setText(String.format("%.1f MHz", freq));
                updateSignalStrengthPreview(freq);
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

        lblFeedback = new JLabel("Adjust tuning slider or use fine-tune buttons to locate carrier wave.", SwingConstants.CENTER);
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

        JButton btnClose = new JButton("Close Scanner");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        footer.add(btnLock);
        footer.add(btnClose);
        content.add(footer, BorderLayout.SOUTH);

        add(content);
        updateSignalStrengthPreview(450.0);
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
        double delta = Math.abs(freq - transmission.getTargetFrequencyMHz());
        int strength = (int) Math.max(5, Math.min(100, (1.0 - (delta / 80.0)) * 100));
        barSignalStrength.setValue(strength);

        if (delta <= 1.5) {
            barSignalStrength.setString(String.format("SIGNAL STRENGTH: 100%% [PHASE LOCK READY: %.1f MHz]", freq));
            barSignalStrength.setForeground(new Color(0, 240, 100));
            lblFeedback.setText("✦ CARRIER DETECTED! Press 'ATTEMPT DECRYPTION' to lock and decrypt!");
            lblFeedback.setForeground(new Color(0, 255, 120));
        } else if (delta < 15.0) {
            barSignalStrength.setString(String.format("SIGNAL STRENGTH: %d%% [CRITICAL HARMONIC RESONANCE]", strength));
            barSignalStrength.setForeground(new Color(255, 200, 50));
            lblFeedback.setText("Very close! Fine-tune by ±0.1 or ±1.0 MHz.");
            lblFeedback.setForeground(new Color(255, 220, 80));
        } else if (delta < 50.0) {
            barSignalStrength.setString(String.format("SIGNAL STRENGTH: %d%% [WEAK SUB-SPACE LEAKAGE]", strength));
            barSignalStrength.setForeground(new Color(255, 130, 40));
            String hint = freq < transmission.getTargetFrequencyMHz() ? "Tune higher." : "Tune lower.";
            lblFeedback.setText("Sub-space carrier detected nearby. " + hint);
            lblFeedback.setForeground(new Color(255, 160, 80));
        } else {
            barSignalStrength.setString(String.format("SIGNAL STRENGTH: %d%% [STATIC NOISE]", strength));
            barSignalStrength.setForeground(new Color(180, 50, 50));
            lblFeedback.setText("Pure static on this frequency band. Consult the signal clue above.");
            lblFeedback.setForeground(new Color(180, 120, 130));
        }
    }

    private void executeDecodeAttempt() {
        double currentFreq = sldFreq.getValue() / 10.0;
        SubSpaceScanner.DecodeResult res = scanner.attemptDecode(transmission.getId(), currentFreq, state);

        lblFeedback.setText(res.getFeedback());
        if (res.isLocked()) {
            txtDecryptedContent.setText(transmission.getRawContent());
            txtDecryptedContent.setForeground(new Color(0, 255, 150));
            btnLock.setEnabled(false);
            btnLock.setText("✓ DECRYPTED & LOGGED IN ARCHIVE");
            btnLock.setBackground(new Color(30, 100, 50));
            JOptionPane.showMessageDialog(this,
                    "CARRIER FREQUENCY LOCKED (" + String.format("%.1f", transmission.getTargetFrequencyMHz()) + " MHz)!\n\n"
                    + "Transmission decrypted successfully!\n"
                    + String.format("Rewards: +%d Eezo, +%d Biomass deposited into fleet reserves.",
                            transmission.getEezoReward(), transmission.getBiomassReward()),
                    "Decryption Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
