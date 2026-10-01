package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.audio.SoundEffects;
import com.seb.harvesteffect.engine.GalacticState;
import com.seb.harvesteffect.engine.MissionManager;
import com.seb.harvesteffect.engine.TechTree;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

/**
 * Interactive 21-Node Branching Tech Tree Dashboard.
 * Displays all 5 specialized research branches in tabbed categories with prerequisite tracking,
 * cost verification, and dynamic primary mass relay sector expansion.
 */
public class TechTreeDialog extends JDialog {
    private final TechTree techTree;
    private final GalacticState state;
    private final MissionManager missionManager;
    private final com.seb.harvesteffect.engine.ReaperEngine engine;
    private final Runnable onUpdateCallback;

    private JLabel lblReserves;
    private JTabbedPane tabbedPane;

    public TechTreeDialog(JFrame parent, com.seb.harvesteffect.engine.ReaperEngine engine, Runnable onUpdateCallback) {
        this(parent, engine.getTechTree(), engine.getState(), engine.getMissionManager(), engine, onUpdateCallback);
    }

    public TechTreeDialog(JFrame parent, TechTree techTree, GalacticState state,
                          MissionManager missionManager, Runnable onUpdateCallback) {
        this(parent, techTree, state, missionManager, null, onUpdateCallback);
    }

    public TechTreeDialog(JFrame parent, TechTree techTree, GalacticState state,
                          MissionManager missionManager, com.seb.harvesteffect.engine.ReaperEngine engine,
                          Runnable onUpdateCallback) {
        super(parent, "Reaper Armada Research Matrix", true);
        this.techTree = techTree;
        this.state = state;
        this.missionManager = missionManager;
        this.engine = engine;
        this.onUpdateCallback = onUpdateCallback;

        buildUI();
        setSize(980, 750);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(10, 13, 20));
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.setBackground(new Color(18, 24, 38));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 230), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel("⚡ REAPER ARMADA RESEARCH MATRIX", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 15));
        lblTitle.setForeground(new Color(0, 230, 255));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        lblReserves = new JLabel("", SwingConstants.RIGHT);
        lblReserves.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblReserves.setForeground(new Color(255, 215, 0));
        updateReservesLabel();
        headerPanel.add(lblReserves, BorderLayout.EAST);

        content.add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Branches
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));
        tabbedPane.setBackground(new Color(16, 20, 30));
        tabbedPane.setForeground(new Color(200, 230, 255));

        for (TechTree.Branch branch : TechTree.Branch.values()) {
            tabbedPane.addTab(branch.getTitle(), createBranchPanel(branch));
        }

        content.add(tabbedPane, BorderLayout.CENTER);

        // 3. Bottom Close Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton btnClose = new JButton("Close Research Matrix");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setBackground(new Color(40, 50, 70));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottomPanel.add(btnClose);
        content.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private void updateReservesLabel() {
        lblReserves.setText(String.format("FLEET TREASURY: %d Eezo  |  %d Biomass  |  %d Biomass Matrix Pod(s) in Hold",
                state.getEezoReserves(), state.getAccumulatedBiomass(), state.countCargoSpecimens()));
    }

    private JPanel createBranchPanel(final TechTree.Branch branch) {
        JPanel branchPanel = new JPanel(new BorderLayout(8, 8));
        branchPanel.setBackground(new Color(12, 16, 26));
        branchPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblDesc = new JLabel("BRANCH DIRECTIVE: " + branch.getDescription());
        lblDesc.setFont(new Font("Monospaced", Font.ITALIC, 12));
        lblDesc.setForeground(new Color(140, 180, 220));
        lblDesc.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 6));
        branchPanel.add(lblDesc, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        listPanel.setOpaque(false);

        List<TechTree.Upgrade> upgrades = techTree.getUpgradesByBranch(branch);
        int activeMission = missionManager.getActiveMissionIndex();

        for (final TechTree.Upgrade u : upgrades) {
            JPanel card = new JPanel(new BorderLayout(10, 6));
            card.setBackground(u.isUnlocked() ? new Color(18, 32, 28) : new Color(18, 22, 34));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(u.isUnlocked() ? new Color(0, 200, 100) : new Color(40, 50, 70), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            // Info Column
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);

            JLabel lblName = new JLabel(u.getName());
            lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblName.setForeground(u.isUnlocked() ? new Color(0, 255, 140) : Color.WHITE);

            JLabel lblDescription = new JLabel("<html><body style='width: 480px;'>" + u.getDescription() + "</body></html>");
            lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblDescription.setForeground(new Color(180, 200, 220));

            StringBuilder reqSb = new StringBuilder();
            reqSb.append(String.format("Cost: %d Eezo, %d Biomass", u.getEezoCost(), u.getBiomassCost()));
            if (u.getRequiredCargoPods() > 0) {
                int held = state.countCargoSpecimens();
                String cargoDesc = u.getRequiredCargoDescription() != null
                        ? u.getRequiredCargoDescription()
                        : String.format("%dx Harvested Biomass Matrix (Any Species)", u.getRequiredCargoPods());
                String status = held >= u.getRequiredCargoPods()
                        ? String.format("[In Hold: %d/%d Ready]", held, u.getRequiredCargoPods())
                        : String.format("[In Hold: %d/%d — Reap Mature World]", held, u.getRequiredCargoPods());
                reqSb.append(String.format("  |  Cargo: %s %s", cargoDesc, status));
            }
            reqSb.append(String.format("  |  Era: %s", u.getRequiredCycleName()));
            if (u.getPrerequisiteUpgradeId() != null) {
                TechTree.Upgrade prereq = techTree.getUpgrade(u.getPrerequisiteUpgradeId());
                String pName = (prereq != null) ? prereq.getName() : u.getPrerequisiteUpgradeId();
                reqSb.append("  |  Prereq: ").append(pName);
            }

            JLabel lblRequirements = new JLabel(reqSb.toString());
            lblRequirements.setFont(new Font("Monospaced", Font.PLAIN, 10));
            lblRequirements.setForeground(new Color(255, 180, 60));

            infoPanel.add(lblName);
            infoPanel.add(Box.createVerticalStrut(2));
            infoPanel.add(lblDescription);
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(lblRequirements);

            card.add(infoPanel, BorderLayout.CENTER);

            // Action Column
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
            actionPanel.setOpaque(false);

            boolean eraOk = u.isAvailableInMission(activeMission);
            boolean prereqOk = (u.getPrerequisiteUpgradeId() == null || techTree.isUnlocked(u.getPrerequisiteUpgradeId()));
            boolean affordOk = state.getEezoReserves() >= u.getEezoCost() && state.getAccumulatedBiomass() >= u.getBiomassCost();
            boolean cargoOk = (u.getRequiredCargoPods() == 0 || state.countCargoSpecimens() >= u.getRequiredCargoPods());

            JButton btnAction = new JButton();
            btnAction.setFont(new Font("SansSerif", Font.BOLD, 11));

            if (u.isUnlocked()) {
                btnAction.setText("✓ RESEARCHED");
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(20, 80, 45));
                btnAction.setForeground(new Color(180, 255, 200));
            } else if (!eraOk) {
                btnAction.setText("LOCKED (Era)");
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(40, 40, 50));
                btnAction.setForeground(new Color(130, 130, 140));
            } else if (!prereqOk) {
                btnAction.setText("LOCKED (Prereq)");
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(50, 35, 40));
                btnAction.setForeground(new Color(160, 120, 120));
            } else if (!affordOk) {
                btnAction.setText("INSUFFICIENT FUNDS");
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(60, 30, 35));
                btnAction.setForeground(new Color(200, 140, 140));
            } else if (!cargoOk) {
                btnAction.setText(String.format("NEED %dx BIOMASS MATRIX", u.getRequiredCargoPods()));
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(65, 35, 25));
                btnAction.setForeground(new Color(255, 170, 120));
                String cargoTip = u.getRequiredCargoDescription() != null
                        ? u.getRequiredCargoDescription()
                        : "Harvested Biomass Matrix";
                btnAction.setToolTipText(String.format("Requires %s in Flagship Cargo Hold. Harvest any mature civilization to extract a Biomass Matrix pod.", cargoTip));
            } else {
                if (u.getRequiredCargoPods() > 0) {
                    btnAction.setText(String.format("RESEARCH (-%d E, -%d B, -%d Pod)",
                            u.getEezoCost(), u.getBiomassCost(), u.getRequiredCargoPods()));
                } else {
                    btnAction.setText(String.format("RESEARCH (-%d Eezo, -%d Bio)", u.getEezoCost(), u.getBiomassCost()));
                }
                btnAction.setEnabled(true);
                btnAction.setBackground(new Color(0, 140, 220));
                btnAction.setForeground(Color.WHITE);
                btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnAction.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            techTree.unlockUpgrade(u.getId(), state, missionManager.getActiveMissionIndex());
                            SoundEffects.playRelayChime();

                            // Unlock sectors dynamically if primary relays were researched
                            if ("primary_relay_alpha".equals(u.getId())) {
                                state.getGalaxyMap().setSectorUnlocked(1, true);
                            } else if ("primary_relay_omega".equals(u.getId())) {
                                state.getGalaxyMap().setSectorUnlocked(2, true);
                            } else if ("primary_relay_gamma".equals(u.getId())) {
                                state.getGalaxyMap().setSectorUnlocked(3, true);
                            }

                            if (u.getId().startsWith("genome_")) {
                                String speciesKey = u.getId().substring("genome_".length());
                                if (engine != null) {
                                    engine.addSequencedGenome(speciesKey);
                                }
                            }

                            JOptionPane.showMessageDialog(TechTreeDialog.this,
                                    "ARMADA RESEARCH COMPLETE:\n\n" + u.getName() + "\n" + u.getDescription(),
                                    "Upgrade Researched", JOptionPane.INFORMATION_MESSAGE);

                            refreshAllTabs();
                            if (onUpdateCallback != null) onUpdateCallback.run();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(TechTreeDialog.this, ex.getMessage(),
                                    "Research Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
            }

            actionPanel.add(btnAction);
            card.add(actionPanel, BorderLayout.EAST);
            listPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        branchPanel.add(scrollPane, BorderLayout.CENTER);
        return branchPanel;
    }

    private void refreshAllTabs() {
        updateReservesLabel();
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.removeAll();
        for (TechTree.Branch branch : TechTree.Branch.values()) {
            tabbedPane.addTab(branch.getTitle(), createBranchPanel(branch));
        }
        if (selectedIndex >= 0 && selectedIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(selectedIndex);
        }
    }
}
