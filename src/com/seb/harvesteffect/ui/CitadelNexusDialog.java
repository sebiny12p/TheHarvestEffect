package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.audio.SoundEffects;
import com.seb.harvesteffect.engine.CampaignManager;
import com.seb.harvesteffect.engine.CitadelNexus;
import com.seb.harvesteffect.engine.GalacticState;
import com.seb.harvesteffect.engine.TechTree;
import com.seb.harvesteffect.model.item.FleetComponent;
import com.seb.harvesteffect.model.item.GenesisProbe;
import com.seb.harvesteffect.model.item.Resource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Interactive Citadel Nexus Mega-Structure Dashboard.
 * Allows upgrading the Citadel across 5 tiers, triggering the Arms Lockdown defense grid,
 * requisitioning Genesis Probes and Armada components, and liquidating flagship cargo.
 */
public class CitadelNexusDialog extends JDialog {
    private final CitadelNexus nexus;
    private final GalacticState state;
    private final TechTree techTree;
    private final CampaignManager campaign;

    private JLabel lblTierTitle;
    private JLabel lblTierDesc;
    private JLabel lblDividends;
    private JButton btnUpgradeCitadel;
    private JButton btnArmsLockdown;
    private JComboBox<String> cmbCatalog;
    private JLabel lblItemCost;
    private JButton btnPurchase;

    // Cargo Manifest & Liquidation Bay Components
    private JComboBox<String> cmbCargo;
    private JLabel lblCargoStatus;
    private JButton btnLiquidate;
    private JButton btnEject;

    public CitadelNexusDialog(JFrame parent, CitadelNexus nexus, GalacticState state,
                              TechTree techTree, CampaignManager campaign) {
        super(parent, "Dark Space Citadel Nexus // Mega-Structure Control & Commerce Hub", true);
        this.nexus = nexus;
        this.state = state;
        this.techTree = techTree;
        this.campaign = campaign;

        buildUI();
        setSize(960, 740);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(10, 13, 20));
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // 1. Citadel Mega-Structure Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(18, 24, 38));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 240), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        lblTierTitle = new JLabel(nexus.getTierName(), SwingConstants.CENTER);
        lblTierTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTierTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblTierTitle.setForeground(new Color(0, 230, 255));

        lblTierDesc = new JLabel(nexus.getTierDescription(), SwingConstants.CENTER);
        lblTierDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTierDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTierDesc.setForeground(new Color(200, 230, 255));

        lblDividends = new JLabel(String.format("PASSIVE INCOME: +%d Eezo/epoch  |  TREASURY: %d Eezo, %d Biomass",
                nexus.getPassiveEezoDividend(), state.getEezoReserves(), state.getAccumulatedBiomass()), SwingConstants.CENTER);
        lblDividends.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDividends.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblDividends.setForeground(new Color(255, 215, 0));

        headerPanel.add(lblTierTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTierDesc);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblDividends);
        content.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Panel: Mega-Structure Upgrades & Arms Lockdown
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(14, 18, 28));

        // Citadel Upgrade Pod
        JPanel upgradePod = new JPanel(new BorderLayout(8, 8));
        upgradePod.setBackground(new Color(20, 26, 40));
        upgradePod.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 180, 40)),
                "CITADEL MEGA-STRUCTURE AWAKENING", 0, 0,
                new Font("Monospaced", Font.BOLD, 12), new Color(255, 200, 60)));

        JLabel lblRoadmap = new JLabel(
                "<html><center><div style='padding: 3px; font-family: monospace;'>"
                + "<b>5-TIER MEGA-STRUCTURE PATH:</b> "
                + "<span style='color: #00E6FF;'>[I: Dormant]</span> ➔ "
                + "<span style='color: #FFD700;'>[II: Keepers]</span> ➔ "
                + "<span style='color: #FF5555;'>[III: Lockdown Array]</span> ➔ "
                + "<span style='color: #B070FF;'>[IV: Conduit]</span> ➔ "
                + "<span style='color: #00FF90;'>[V: Catalyst]</span>"
                + "</div></center></html>",
                SwingConstants.CENTER);
        upgradePod.add(lblRoadmap, BorderLayout.NORTH);

        btnUpgradeCitadel = new JButton("UPGRADE CITADEL");
        btnUpgradeCitadel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnUpgradeCitadel.setBackground(new Color(200, 120, 20));
        btnUpgradeCitadel.setForeground(Color.WHITE);
        btnUpgradeCitadel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upgradeCitadelAction();
            }
        });

        btnArmsLockdown = new JButton("INITIATE CITADEL ARMS LOCKDOWN (-25% CRUCIBLE)");
        btnArmsLockdown.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnArmsLockdown.setBackground(new Color(180, 40, 40));
        btnArmsLockdown.setForeground(Color.WHITE);
        btnArmsLockdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triggerLockdownAction();
            }
        });

        JPanel upgradeBtnGrid = new JPanel(new GridLayout(2, 1, 6, 6));
        upgradeBtnGrid.setOpaque(false);
        upgradeBtnGrid.add(btnUpgradeCitadel);
        upgradeBtnGrid.add(btnArmsLockdown);
        upgradePod.add(upgradeBtnGrid, BorderLayout.CENTER);
        centerPanel.add(upgradePod);
        centerPanel.add(Box.createVerticalStrut(10));

        // Citadel Requisitions Catalog Pod
        JPanel catalogPod = new JPanel(new BorderLayout(8, 8));
        catalogPod.setBackground(new Color(18, 22, 34));
        catalogPod.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 220)),
                "CITADEL REQUISITION & COMMERCE CATALOG", 0, 0,
                new Font("Monospaced", Font.BOLD, 12), new Color(0, 200, 255)));

        cmbCatalog = new JComboBox<String>();
        cmbCatalog.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cmbCatalog.setBackground(new Color(25, 30, 45));
        cmbCatalog.setForeground(Color.WHITE);

        List<String> items = new ArrayList<String>(nexus.getCatalog().keySet());
        for (String k : items) {
            int eff = nexus.getEffectiveCost(k);
            cmbCatalog.addItem(String.format("%-25s : %d Eezo", k, eff));
        }

        lblItemCost = new JLabel("Select an item to requisition into flagship cargo.", SwingConstants.CENTER);
        lblItemCost.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblItemCost.setForeground(new Color(160, 200, 230));

        btnPurchase = new JButton("Requisition Selected Item");
        btnPurchase.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPurchase.setBackground(new Color(0, 130, 200));
        btnPurchase.setForeground(Color.WHITE);
        btnPurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                purchaseItemAction();
            }
        });

        JPanel reqGrid = new JPanel(new GridLayout(3, 1, 4, 4));
        reqGrid.setOpaque(false);
        reqGrid.add(cmbCatalog);
        reqGrid.add(lblItemCost);
        reqGrid.add(btnPurchase);
        catalogPod.add(reqGrid, BorderLayout.CENTER);
        centerPanel.add(catalogPod);

        centerPanel.add(Box.createVerticalStrut(10));

        // Flagship Cargo Manifest & Commerce Liquidation Pod
        JPanel cargoPod = new JPanel(new BorderLayout(8, 8));
        cargoPod.setBackground(new Color(18, 24, 32));
        cargoPod.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 230, 150)),
                "FLAGSHIP CARGO PODS & COMMERCE LIQUIDATION BAY", 0, 0,
                new Font("Monospaced", Font.BOLD, 12), new Color(0, 255, 170)));

        lblCargoStatus = new JLabel("CARGO PODS: Loading...", SwingConstants.CENTER);
        lblCargoStatus.setFont(new Font("Monospaced", Font.BOLD, 11));
        lblCargoStatus.setForeground(new Color(180, 240, 210));

        cmbCargo = new JComboBox<String>();
        cmbCargo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cmbCargo.setBackground(new Color(20, 30, 35));
        cmbCargo.setForeground(Color.WHITE);

        btnLiquidate = new JButton("⚡ Liquidate Selected Asset (+Eezo)");
        btnLiquidate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLiquidate.setBackground(new Color(0, 150, 100));
        btnLiquidate.setForeground(Color.WHITE);
        btnLiquidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                liquidateItemAction();
            }
        });

        btnEject = new JButton("🗑️ Eject Pod (Discard)");
        btnEject.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnEject.setBackground(new Color(60, 40, 45));
        btnEject.setForeground(Color.WHITE);
        btnEject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejectItemAction();
            }
        });

        JPanel cargoBtnGrid = new JPanel(new GridLayout(1, 2, 6, 6));
        cargoBtnGrid.setOpaque(false);
        cargoBtnGrid.add(btnLiquidate);
        cargoBtnGrid.add(btnEject);

        JPanel cargoGrid = new JPanel(new GridLayout(3, 1, 4, 4));
        cargoGrid.setOpaque(false);
        cargoGrid.add(lblCargoStatus);
        cargoGrid.add(cmbCargo);
        cargoGrid.add(cargoBtnGrid);
        cargoPod.add(cargoGrid, BorderLayout.CENTER);
        centerPanel.add(cargoPod);

        content.add(centerPanel, BorderLayout.CENTER);

        // 3. Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        footer.setBackground(new Color(12, 16, 24));
        JButton btnClose = new JButton("Close Nexus");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        footer.add(btnClose);
        content.add(footer, BorderLayout.SOUTH);

        add(content);
        updateUIState();
    }

    private void updateUIState() {
        lblTierTitle.setText(nexus.getTierName());
        lblTierDesc.setText(nexus.getTierDescription());
        lblDividends.setText(String.format("PASSIVE INCOME: +%d Eezo/epoch  |  TREASURY: %d Eezo, %d Biomass",
                nexus.getPassiveEezoDividend(), state.getEezoReserves(), state.getAccumulatedBiomass()));

        if (nexus.getTier() >= CitadelNexus.TIER_5_CATALYST) {
            btnUpgradeCitadel.setText("CITADEL AT MAXIMUM CONVERGENCE (Tier V)");
            btnUpgradeCitadel.setEnabled(false);
            btnUpgradeCitadel.setBackground(new Color(50, 70, 60));
        } else {
            int eCost = nexus.getNextTierEezoCost();
            int bCost = nexus.getNextTierBiomassCost();
            boolean canAfford = state.getEezoReserves() >= eCost && state.getAccumulatedBiomass() >= bCost;
            boolean techOk = (nexus.getTier() < 2) || (techTree != null && techTree.isUnlocked("citadel_core_control"));

            if (!techOk) {
                btnUpgradeCitadel.setText("UPGRADE TO TIER III LOCKED: Research 'Citadel Core Control' in Tech Tree (Branch 4)");
                btnUpgradeCitadel.setToolTipText("Research Citadel Core Security Override under Citadel & Fleet Economy in the Tech Tree to unlock Tiers III and IV.");
                btnUpgradeCitadel.setEnabled(false);
                btnUpgradeCitadel.setBackground(new Color(60, 40, 45));
            } else if (!canAfford) {
                btnUpgradeCitadel.setText(String.format("UPGRADE TIER %d (Cost: %d Eezo, %d Biomass) [Insufficient Funds]",
                        nexus.getTier() + 1, eCost, bCost));
                btnUpgradeCitadel.setEnabled(false);
                btnUpgradeCitadel.setBackground(new Color(60, 40, 45));
            } else {
                btnUpgradeCitadel.setText(String.format("UPGRADE TO TIER %d (Cost: %d Eezo, %d Biomass)",
                        nexus.getTier() + 1, eCost, bCost));
                btnUpgradeCitadel.setEnabled(true);
                btnUpgradeCitadel.setBackground(new Color(200, 120, 20));
            }
        }

        if (nexus.getTier() < CitadelNexus.TIER_3_LOCKDOWN) {
            btnArmsLockdown.setText("CITADEL ARMS LOCKDOWN (Locked — Requires Citadel Tier III)");
            btnArmsLockdown.setEnabled(false);
            btnArmsLockdown.setBackground(new Color(60, 40, 45));
        } else if (nexus.isArmsLockdownActive()) {
            btnArmsLockdown.setText(String.format("🔒 CITADEL ARMS SEALED SHUT (%d Epochs Left — Crucible Frozen)",
                    nexus.getLockdownDurationRemaining()));
            btnArmsLockdown.setEnabled(false);
            btnArmsLockdown.setBackground(new Color(20, 70, 90));
        } else if (!nexus.isLockdownReady()) {
            btnArmsLockdown.setText(String.format("⏳ ARMS CAPACITORS RECHARGING (%d Epochs Cooldown Remaining)",
                    nexus.getLockdownCooldownRemaining()));
            btnArmsLockdown.setEnabled(false);
            btnArmsLockdown.setBackground(new Color(80, 50, 20));
        } else {
            btnArmsLockdown.setText("⚡ CLOSE CITADEL ARMS (-25% & FREEZE CRUCIBLE FOR 4 EPOCHS)");
            btnArmsLockdown.setEnabled(true);
            btnArmsLockdown.setBackground(new Color(200, 30, 30));
        }

        // Refresh Cargo Bay Manifest & Liquidation Pod
        if (cmbCargo != null) {
            cmbCargo.removeAllItems();
            List<Resource> manifest = state.getCargoHold().getManifest();
            for (int i = 0; i < manifest.size(); i++) {
                Resource r = manifest.get(i);
                String role = "";
                if (r instanceof GenesisProbe) {
                    role = " [Deploy on world: Cost Waived & +25% Incubation]";
                } else if (r instanceof FleetComponent) {
                    role = String.format(" [Flagship Subsystem: +%d Armor]", ((FleetComponent) r).getArmorBuff());
                } else {
                    role = " [Ascension Bio-Extract: Liquidate for Eezo]";
                }
                cmbCargo.addItem(String.format("Pod %02d: %s (Value: %d Eezo)%s", i + 1, r.getItemName(), r.getEezoValue(), role));
            }
            boolean hasCargo = !manifest.isEmpty();
            btnLiquidate.setEnabled(hasCargo);
            btnEject.setEnabled(hasCargo);
            lblCargoStatus.setText(String.format("CARGO BAYS: %d/%d Pods Occupied  |  SOVEREIGN HULL ARMOR: %d Integrity",
                    state.getCargoHold().getOccupiedCount(), state.getCargoHold().getCapacity(),
                    state.getInstalledFleetArmorIntegrity()));
        }
    }

    private void liquidateItemAction() {
        if (cmbCargo == null || cmbCargo.getSelectedIndex() < 0) return;
        int slot = cmbCargo.getSelectedIndex();
        try {
            int eezoEarned = nexus.liquidateAsset(slot, state);
            SoundEffects.playUiClick();
            JOptionPane.showMessageDialog(this,
                    String.format("ASSET LIQUIDATED!\n\nPod %d successfully converted into %d Eezo.\nUpdated Reserves: %d Eezo",
                            slot + 1, eezoEarned, state.getEezoReserves()),
                    "Liquidation Complete", JOptionPane.INFORMATION_MESSAGE);
            updateUIState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Liquidation Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void ejectItemAction() {
        if (cmbCargo == null || cmbCargo.getSelectedIndex() < 0) return;
        int slot = cmbCargo.getSelectedIndex();
        try {
            Resource r = state.getCargoHold().retrieve(slot);
            SoundEffects.playUiClick();
            JOptionPane.showMessageDialog(this,
                    String.format("CARGO EJECTED!\n\nJettisoned '%s' into dark space.\nPod %d is now empty.",
                            r.getItemName(), slot + 1),
                    "Cargo Pod Ejected", JOptionPane.INFORMATION_MESSAGE);
            updateUIState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ejection Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void upgradeCitadelAction() {
        try {
            nexus.upgradeCitadel(state, techTree);
            SoundEffects.playRelayChime();
            JOptionPane.showMessageDialog(this,
                    "CITADEL MEGA-STRUCTURE ADVANCED!\n\n"
                    + nexus.getTierName() + "\n"
                    + nexus.getTierDescription(),
                    "Citadel Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
            updateUIState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Upgrade Aborted", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void triggerLockdownAction() {
        try {
            String msg = nexus.triggerCitadelLockdown(campaign);
            SoundEffects.playReaperHorn();
            JOptionPane.showMessageDialog(this,
                    msg + "\n\nCRUCIBLE RESEARCH IS FROZEN FOR 4 EPOCHS.\n"
                    + "During these 4 epochs, find solutions to lower crucible research before the arms reopen:\n"
                    + "  • Deploy Swarm Garrisons on spacefaring worlds to block Allied research.\n"
                    + "  • Research Crucible Sabotage Protocols in the Tech Tree.\n"
                    + "  • Requisition Sovereign Armor to endure the conflict.",
                    "Citadel Arms Closed", JOptionPane.INFORMATION_MESSAGE);
            updateUIState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lockdown Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void purchaseItemAction() {
        if (cmbCatalog.getSelectedItem() == null) return;
        String line = (String) cmbCatalog.getSelectedItem();
        String itemKey = line.substring(0, line.indexOf(":")).trim();
        try {
            Resource r = nexus.purchaseItem(itemKey, state);
            SoundEffects.playUiClick();
            JOptionPane.showMessageDialog(this,
                    "Requisitioned: " + r.getItemName() + " stored in Flagship Cargo Hold.\nRemaining Eezo: " + state.getEezoReserves(),
                    "Requisition Successful", JOptionPane.INFORMATION_MESSAGE);
            updateUIState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Requisition Failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
