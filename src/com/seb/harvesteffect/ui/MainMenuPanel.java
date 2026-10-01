package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.audio.SoundEffects;
import com.seb.harvesteffect.engine.SaveManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Cinematic Main Menu screen for The Harvest Effect.
 * Provides Continue, New Game, Load, Codex, Credits, and Exit.
 */
public class MainMenuPanel extends JPanel {

    public interface MenuListener {
        void onContinueGame();
        void onNewGame();
        void onLoadGame();
        void onOpenCodex();
        void onOpenCredits();
        void onExitGame();
    }

    private JButton btnContinue;

    public MainMenuPanel(final MenuListener listener) {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(8, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // 1. Header / Title Banner
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("THE HARVEST EFFECT", SwingConstants.CENTER);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 38));
        lblTitle.setForeground(new Color(0, 230, 255));

        JLabel lblQuote = new JLabel("\"You exist because we allow it, and you will end because we demand it.\"", SwingConstants.CENTER);
        lblQuote.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuote.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblQuote.setForeground(new Color(170, 190, 220));

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(lblQuote);
        add(titlePanel, BorderLayout.NORTH);

        // 2. Center Button Column
        JPanel buttonCol = new JPanel();
        buttonCol.setLayout(new GridLayout(6, 1, 14, 14));
        buttonCol.setOpaque(false);
        buttonCol.setMaximumSize(new Dimension(450, 360));

        boolean hasSave = SaveManager.hasSaveFile();

        // Continue Button
        btnContinue = createMenuButton("CONTINUE CAMPAIGN", new Color(0, 160, 220), hasSave);
        btnContinue.setToolTipText(hasSave ? "Resume existing campaign from disk" : "No saved campaign detected");
        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playRelayChime();
                listener.onContinueGame();
            }
        });

        // New Game Button
        JButton btnNewGame = createMenuButton("START NEW CAMPAIGN", new Color(200, 130, 20), true);
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playReaperHorn();
                listener.onNewGame();
            }
        });

        // Load Game Button
        JButton btnLoad = createMenuButton("LOAD SAVED CAMPAIGN", new Color(30, 130, 180), true);
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                listener.onLoadGame();
            }
        });

        // Codex Button
        JButton btnCodex = createMenuButton("REAPER CODEX & CAMPAIGN ARCHIVE", new Color(110, 60, 170), true);
        btnCodex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                listener.onOpenCodex();
            }
        });

        // Credits Button
        JButton btnCredits = createMenuButton("TRANSMISSION CREDITS", new Color(60, 90, 130), true);
        btnCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                listener.onOpenCredits();
            }
        });

        // Exit Button
        JButton btnExit = createMenuButton("EXIT TO DARK SPACE", new Color(180, 40, 40), true);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onExitGame();
            }
        });

        buttonCol.add(btnContinue);
        buttonCol.add(btnNewGame);
        buttonCol.add(btnLoad);
        buttonCol.add(btnCodex);
        buttonCol.add(btnCredits);
        buttonCol.add(btnExit);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(buttonCol);
        add(centerWrapper, BorderLayout.CENTER);

        // 3. Footer
        JLabel lblFooter = new JLabel("Object-Oriented Programming in Java | Swing & CLI", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(100, 120, 150));
        add(lblFooter, BorderLayout.SOUTH);
    }

    public void refreshMenuState() {
        boolean hasSave = SaveManager.hasSaveFile();
        if (btnContinue != null) {
            btnContinue.setEnabled(hasSave);
            if (hasSave) {
                btnContinue.setBackground(new Color(20, 26, 38));
                btnContinue.setForeground(Color.WHITE);
                btnContinue.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 160, 220), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
                btnContinue.setToolTipText("Resume existing campaign from disk");
            } else {
                btnContinue.setBackground(new Color(15, 18, 24));
                btnContinue.setForeground(new Color(100, 110, 130));
                btnContinue.setBorder(BorderFactory.createLineBorder(new Color(40, 45, 60), 1));
                btnContinue.setToolTipText("No saved campaign detected");
            }
        }
    }

    private JButton createMenuButton(String text, Color accentColor, boolean enabled) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setEnabled(enabled);

        if (enabled) {
            btn.setBackground(new Color(20, 26, 38));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
        } else {
            btn.setBackground(new Color(15, 18, 24));
            btn.setForeground(new Color(100, 110, 130));
            btn.setBorder(BorderFactory.createLineBorder(new Color(40, 45, 60), 1));
        }
        return btn;
    }
}
