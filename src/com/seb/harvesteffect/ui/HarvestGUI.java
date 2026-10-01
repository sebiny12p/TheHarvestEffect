package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.audio.SoundEffects;
import com.seb.harvesteffect.engine.*;
import com.seb.harvesteffect.exception.*;
import com.seb.harvesteffect.model.entity.*;
import com.seb.harvesteffect.model.item.*;
import com.seb.harvesteffect.model.unit.*;
import com.seb.harvesteffect.story.StoryDilemma;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Atmospheric Sci-Fi Java Swing GUI for The Harvest Effect.
 * Features a cinematic Main Menu, multi-cycle historical campaign, instructional missions,
 * procedural audio, and save/load persistence.
 */
public class HarvestGUI extends JFrame {
    private GalacticState state;
    private ReaperEngine engine;
    private CampaignManager campaign;
    private TechTree techTree;
    private MissionManager missionManager;

    // CardLayout Container
    private CardLayout cardLayout;
    private JPanel containerPanel;
    private MainMenuPanel mainMenuPanel;
    private JPanel gamePanel;

    // Game HUD Components
    private JLabel lblHeader;
    private JLabel lblSpeaker;
    private JLabel lblCycleTitle;
    private JLabel lblMissionTitle;
    private JLabel lblTaskDirective;
    private JProgressBar barCrucible;
    private JLabel lblStats;
    private JLabel lblPhenomenon;
    private PlanetTilePanel[][] planetTiles;
    private GalacticMapPanel galacticMapPanel;
    private CardLayout galaxyCardLayout;
    private JPanel galaxyCenterCards;
    private boolean isMapViewActive = true;
    private JButton btnToggleMapView;
    private JLabel lblTargetStatus;
    private JLabel lblPlantCostInfo;
    private JButton btnSeed;
    private JButton btnAdvance;
    private JButton btnHarvest;
    private JButton btnBatchHarvest;
    private JButton btnRelay;
    private JButton btnDeployUnit;
    private JButton btnTechTree;
    private JButton btnNexus;
    private JButton btnEconomy;
    private JButton btnAlmanac;
    private JButton btnBioBanks;
    private JButton btnCargoHold;
    private boolean victoryDialogShown = false;
    private JButton btnTimeMode;
    private JButton btnMissionBriefing;
    private JButton btnCodex;
    private JButton btnAudioToggle;
    private JButton btnScanner;
    private JButton btnJumpFlagship;
    private Timer realTimeTimer;
    private boolean realTimePaused = false;
    private JTextArea txtLog;
    private JComboBox<String> cmbSpecies;
    private JComboBox<String> cmbUnits;
    private JButton btnTerraform;
    private JLabel lblCitadelHubStats;
    private JLabel lblConduitCorridors;
    private int selectedSector = 0;
    private int selectedCluster = 0;

    private static final String[] ALL_SPECIES = {
        "Humanity", "Asari", "Turian", "Salarian", "Krogan",
        "Quarian", "Batarian", "Volus", "Hanar", "Drell",
        "Elcor", "Vorcha", "Rachni", "Prothean", "Yahg"
    };

    private static final String[] ALL_UNITS = {
        "Collector Drone", "Husk Swarm", "Scion Behemoth"
    };

    public HarvestGUI(GalacticState initialState, ReaperEngine initialEngine) {
        super("The Harvest Effect");
        this.state = initialState;
        this.engine = initialEngine;
        this.campaign = engine.getCampaign();
        this.techTree = engine.getTechTree();
        this.missionManager = engine.getMissionManager();

        initLookAndFeel();
        buildCardContainer();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 920);
        setLocationRelativeTo(null);
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void buildCardContainer() {
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Card 1: Main Menu
        mainMenuPanel = new MainMenuPanel(new MainMenuPanel.MenuListener() {
            @Override
            public void onContinueGame() {
                continueGameAction();
            }

            @Override
            public void onNewGame() {
                startNewCampaignAction();
            }

            @Override
            public void onLoadGame() {
                loadGameAction();
            }

            @Override
            public void onOpenCodex() {
                showHistoricalCodexDialog();
            }

            @Override
            public void onOpenCredits() {
                showCreditsDialog();
            }

            @Override
            public void onExitGame() {
                System.exit(0);
            }
        });

        // Card 2: Game Screen
        gamePanel = buildGameScreenPanel();

        containerPanel.add(mainMenuPanel, "MAIN_MENU");
        containerPanel.add(gamePanel, "GAME_SCREEN");

        add(containerPanel);
        cardLayout.show(containerPanel, "MAIN_MENU");
    }

    private JPanel buildGameScreenPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(10, 13, 20));

        // Top HUD & Holo-Comm Screen
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(14, 18, 28));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 220), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Navigation Bar inside game screen
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setOpaque(false);

        JButton btnBackToMenu = new JButton("<< Main Menu");
        btnBackToMenu.setFont(new Font("Monospaced", Font.BOLD, 12));
        btnBackToMenu.setBackground(new Color(30, 40, 60));
        btnBackToMenu.setForeground(Color.WHITE);
        btnBackToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (realTimeTimer != null) realTimeTimer.stop();
                SaveManager.autoSave(engine);
                if (mainMenuPanel != null) mainMenuPanel.refreshMenuState();
                cardLayout.show(containerPanel, "MAIN_MENU");
            }
        });
        navBar.add(btnBackToMenu, BorderLayout.WEST);

        lblHeader = new JLabel("THE HARVEST EFFECT // FLAGSHIP: " + state.getFlagshipName(), SwingConstants.CENTER);
        lblHeader.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblHeader.setForeground(new Color(0, 230, 255));
        navBar.add(lblHeader, BorderLayout.CENTER);

        JButton btnQuickSave = new JButton("Save Game");
        btnQuickSave.setFont(new Font("Monospaced", Font.BOLD, 12));
        btnQuickSave.setBackground(new Color(20, 100, 80));
        btnQuickSave.setForeground(Color.WHITE);
        btnQuickSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGameAction();
            }
        });
        navBar.add(btnQuickSave, BorderLayout.EAST);
        topPanel.add(navBar);

        // Holo-Comm Mission Directive Panel
        JPanel holoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        holoPanel.setBackground(new Color(22, 26, 40));
        holoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 175, 30), 2),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        lblCycleTitle = new JLabel("", SwingConstants.CENTER);
        lblCycleTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblCycleTitle.setForeground(new Color(255, 90, 70));

        lblMissionTitle = new JLabel("", SwingConstants.CENTER);
        lblMissionTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMissionTitle.setForeground(new Color(255, 215, 0));

        lblTaskDirective = new JLabel("", SwingConstants.CENTER);
        lblTaskDirective.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTaskDirective.setForeground(new Color(230, 240, 255));

        holoPanel.add(lblCycleTitle);
        holoPanel.add(lblMissionTitle);
        holoPanel.add(lblTaskDirective);

        barCrucible = new JProgressBar(0, 100);
        barCrucible.setValue(0);
        barCrucible.setStringPainted(true);
        barCrucible.setString("CRITICAL THREAT: Crucible Completion: 0%");
        barCrucible.setForeground(new Color(230, 40, 40));
        barCrucible.setBackground(new Color(50, 15, 15));
        barCrucible.setVisible(false);

        lblStats = new JLabel("", SwingConstants.CENTER);
        lblStats.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStats.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblStats.setForeground(new Color(180, 230, 255));

        lblPhenomenon = new JLabel("", SwingConstants.CENTER);
        lblPhenomenon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPhenomenon.setFont(new Font("Monospaced", Font.ITALIC, 12));
        lblPhenomenon.setForeground(new Color(255, 160, 60));

        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(holoPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(barCrucible);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(lblStats);
        topPanel.add(lblPhenomenon);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center Galaxy Matrix: Citadel Nexus Galactic Core + 4 Quadrant Sectors (24 Planets)
        JPanel galaxyContainer = new JPanel(new BorderLayout(6, 6));
        galaxyContainer.setBackground(new Color(9, 12, 18));

        // Citadel Core Nexus Hub
        JPanel citadelHub = buildCitadelNexusHub();
        galaxyContainer.add(citadelHub, BorderLayout.NORTH);

        // 2x2 Quadrant Grid for the 4 Sectors
        JPanel quadrantsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        quadrantsPanel.setBackground(new Color(9, 12, 18));

        planetTiles = new PlanetTilePanel[4][6];
        GalacticSector galaxy = state.getGalaxyMap();

        for (int r = 0; r < 4; r++) {
            final int sec = r;
            JPanel sectorCard = new JPanel(new BorderLayout(4, 4));
            sectorCard.setBackground(new Color(12, 16, 24));
            Color sectorBorderColor = (r == 0) ? new Color(0, 200, 255) : (r == 1 ? new Color(255, 170, 40) : (r == 2 ? new Color(180, 80, 255) : new Color(255, 70, 90)));
            sectorCard.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(sectorBorderColor, 1),
                    galaxy.getSectorName(r),
                    0, 0, new Font("Monospaced", Font.BOLD, 11), sectorBorderColor));

            JPanel grid2x3 = new JPanel(new GridLayout(2, 3, 4, 4));
            grid2x3.setOpaque(false);

            for (int c = 0; c < 6; c++) {
                final int clu = c;
                StarSystem sys = galaxy.getSystem(r, c);
                PlanetTilePanel tile = new PlanetTilePanel(sys, new PlanetTilePanel.TileClickListener() {
                    @Override
                    public void onTileClicked(int s, int cl) {
                        SoundEffects.playUiClick();
                        selectSystem(s, cl);
                    }
                });

                if (r == selectedSector && c == selectedCluster) {
                    tile.setSelected(true);
                }

                planetTiles[r][c] = tile;
                grid2x3.add(tile);
            }
            sectorCard.add(grid2x3, BorderLayout.CENTER);
            quadrantsPanel.add(sectorCard);
        }

        // Galactic Star Chart (Radial Map with Citadel at Galactic Core)
        galacticMapPanel = new GalacticMapPanel(state, engine, missionManager);
        galacticMapPanel.setMapInteractionListener(new GalacticMapPanel.MapInteractionListener() {
            @Override
            public void onSystemSelected(int s, int cl) {
                SoundEffects.playUiClick();
                selectSystem(s, cl);
            }

            @Override
            public void onCitadelClicked() {
                SoundEffects.playRelayChime();
                showNexusDialog();
            }

            @Override
            public void onPrimaryRelayClicked(int s) {
                SoundEffects.playUiClick();
                handlePrimaryRelayInteraction(s);
            }
        });

        galaxyCardLayout = new CardLayout();
        galaxyCenterCards = new JPanel(galaxyCardLayout);
        galaxyCenterCards.setBackground(new Color(9, 12, 18));
        galaxyCenterCards.add(galacticMapPanel, "MAP");
        galaxyCenterCards.add(quadrantsPanel, "GRID");
        galaxyCardLayout.show(galaxyCenterCards, "MAP"); // STAR CHART IS DEFAULT VIEW!

        galaxyContainer.add(galaxyCenterCards, BorderLayout.CENTER);
        panel.add(galaxyContainer, BorderLayout.CENTER);

        // Bottom Tactical Command Console
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(14, 18, 28));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));

        // 1. Target Planet Status Banner
        JPanel targetBanner = new JPanel(new BorderLayout());
        targetBanner.setBackground(new Color(18, 24, 38));
        targetBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 220), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        lblTargetStatus = new JLabel("TARGET: Sol [0,0]  |  STATUS: UNINHABITED (Lifeless Space — Ready for Planting)");
        lblTargetStatus.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblTargetStatus.setForeground(new Color(0, 230, 255));
        targetBanner.add(lblTargetStatus, BorderLayout.CENTER);
        bottomPanel.add(targetBanner);
        bottomPanel.add(Box.createVerticalStrut(6));

        // 2. Core Farming Action Pods (3 Distinct Step Pods)
        JPanel coreActionsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        coreActionsPanel.setOpaque(false);

        // POD 1: PLANTING
        JPanel podPlant = createActionPod("STEP 1: PLANT & TERRAFORM", new Color(0, 170, 230));
        JPanel plantControls = new JPanel(new GridLayout(4, 1, 3, 3));
        plantControls.setOpaque(false);

        cmbSpecies = new JComboBox<String>();
        cmbSpecies.setFont(new Font("SansSerif", Font.BOLD, 12));
        cmbSpecies.setBackground(new Color(24, 30, 44));
        cmbSpecies.setForeground(Color.WHITE);
        cmbSpecies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSeedingIntel();
            }
        });

        lblPlantCostInfo = new JLabel("COST: 50 Eezo | SEEDING FORECAST", SwingConstants.CENTER);
        lblPlantCostInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblPlantCostInfo.setForeground(new Color(130, 200, 240));

        btnSeed = createActionButton("🌱 Plant Civilization", new Color(0, 140, 210));
        btnSeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                seedSelectedCivilization();
            }
        });

        btnTerraform = createActionButton("🌍 Terraform World", new Color(30, 130, 80));
        btnTerraform.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnTerraform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showTerraformDialog();
            }
        });

        plantControls.add(cmbSpecies);
        plantControls.add(lblPlantCostInfo);
        plantControls.add(btnSeed);
        plantControls.add(btnTerraform);
        podPlant.add(plantControls, BorderLayout.CENTER);

        // POD 2: TIME & GROW
        JPanel podGrow = createActionPod("STEP 2: GROW & EVOLVE", new Color(0, 200, 110));
        JPanel growControls = new JPanel(new BorderLayout(4, 4));
        growControls.setOpaque(false);

        btnAdvance = createActionButton("⏩ ADVANCE TIME (+5,000 Y)", new Color(0, 150, 80));
        btnAdvance.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAdvance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advanceCycleAction();
            }
        });
        JLabel lblGrowHint = new JLabel("Grows planets: Tier 0 ➔ Tier 3 Apex", SwingConstants.CENTER);
        lblGrowHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblGrowHint.setForeground(new Color(160, 220, 190));
        growControls.add(btnAdvance, BorderLayout.CENTER);
        growControls.add(lblGrowHint, BorderLayout.SOUTH);
        podGrow.add(growControls, BorderLayout.CENTER);

        // POD 3: HARVEST & MASS RELAYS
        JPanel podHarvest = createActionPod("STEP 3: HARVEST & RELAYS", new Color(230, 50, 50));
        JPanel harvestControls = new JPanel(new GridLayout(3, 1, 4, 3));
        harvestControls.setOpaque(false);

        btnHarvest = createActionButton("⚡ ASCENSION HARVEST", new Color(200, 30, 30));
        btnHarvest.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnHarvest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playHarvestPulse();
                harvestSelectedSystem();
            }
        });

        btnBatchHarvest = createActionButton("🛸 Batch Reap (0 Ripe)", new Color(220, 50, 50));
        btnBatchHarvest.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnBatchHarvest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                batchHarvestAllRipeWorlds();
            }
        });

        btnRelay = createActionButton("✦ Deploy Relay Beacon", new Color(0, 110, 180));
        btnRelay.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRelay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    engine.deployRelay(selectedSector, selectedCluster);
                    SoundEffects.playRelayChime();
                    log(String.format("Mass Relay corridor activated in [%d, %d] (-50 Eezo).", selectedSector, selectedCluster));
                    checkMissionProgress();
                } catch (InsufficientEezoException ex) {
                    JOptionPane.showMessageDialog(HarvestGUI.this, ex.getMessage(), "Insufficient Eezo", JOptionPane.WARNING_MESSAGE);
                } catch (ReaperException ex) {
                    JOptionPane.showMessageDialog(HarvestGUI.this, ex.getMessage(), "Deployment Aborted", JOptionPane.WARNING_MESSAGE);
                }
                updateDisplay();
            }
        });

        harvestControls.add(btnHarvest);
        harvestControls.add(btnBatchHarvest);
        harvestControls.add(btnRelay);
        podHarvest.add(harvestControls, BorderLayout.CENTER);

        coreActionsPanel.add(podPlant);
        coreActionsPanel.add(podGrow);
        coreActionsPanel.add(podHarvest);
        bottomPanel.add(coreActionsPanel);
        bottomPanel.add(Box.createVerticalStrut(6));

        // 3. Secondary Dock: Swarm & Auxiliaries
        JPanel secondaryDock = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        secondaryDock.setBackground(new Color(16, 20, 30));
        secondaryDock.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 70)));

        JLabel lblSwarm = new JLabel("SWARM GARRISON:");
        lblSwarm.setFont(new Font("Monospaced", Font.BOLD, 11));
        lblSwarm.setForeground(new Color(0, 200, 255));
        secondaryDock.add(lblSwarm);

        cmbUnits = new JComboBox<String>();
        cmbUnits.setFont(new Font("SansSerif", Font.PLAIN, 11));
        secondaryDock.add(cmbUnits);

        btnDeployUnit = new JButton("Station Swarm");
        btnDeployUnit.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnDeployUnit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                deploySelectedUnit();
            }
        });
        secondaryDock.add(btnDeployUnit);

        btnJumpFlagship = new JButton("👑 Jump Sovereign");
        btnJumpFlagship.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnJumpFlagship.setBackground(new Color(180, 130, 20));
        btnJumpFlagship.setForeground(Color.WHITE);
        btnJumpFlagship.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!state.getGalaxyMap().isSectorUnlocked(selectedSector)) {
                    JOptionPane.showMessageDialog(HarvestGUI.this,
                            "Cannot jump Sovereign Flagship to a locked sector!\nConstruct a Primary Mass Relay in the Tech Tree first.",
                            "FTL Reach Exceeded", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    int cost = engine.moveFlagship(selectedSector, selectedCluster);
                    SoundEffects.playReaperHorn();
                    String sysName = state.getGalaxyMap().getSystem(selectedSector, selectedCluster).getSystemName();
                    log(String.format("SOVEREIGN RELOCATED: Stationed in orbit around %s [%d, %d] (-%d Eezo).",
                            sysName, selectedSector, selectedCluster, cost));
                    updateDisplay();
                } catch (InsufficientEezoException ex) {
                    JOptionPane.showMessageDialog(HarvestGUI.this, ex.getMessage(), "Insufficient Eezo Reserves", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        secondaryDock.add(btnJumpFlagship);
        secondaryDock.add(Box.createHorizontalStrut(6));

        btnTechTree = new JButton("🔬 Research");
        btnTechTree.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnTechTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showTechTreeDialog();
            }
        });
        secondaryDock.add(btnTechTree);

        btnNexus = new JButton("🏛️ Citadel Nexus");
        btnNexus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnNexus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showNexusDialog();
            }
        });
        secondaryDock.add(btnNexus);
 
        btnEconomy = new JButton("📊 Economy");
        btnEconomy.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnEconomy.setBackground(new Color(25, 90, 140));
        btnEconomy.setForeground(Color.WHITE);
        btnEconomy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showGalacticEconomyDialog();
            }
        });
        secondaryDock.add(btnEconomy);

        btnAlmanac = new JButton("🌾 Crop Almanac");
        btnAlmanac.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAlmanac.setBackground(new Color(35, 110, 60));
        btnAlmanac.setForeground(Color.WHITE);
        btnAlmanac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showCropAlmanacDialog();
            }
        });
        secondaryDock.add(btnAlmanac);

        btnBioBanks = new JButton("🧬 Bio-Banks");
        btnBioBanks.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnBioBanks.setBackground(new Color(110, 40, 140));
        btnBioBanks.setForeground(Color.WHITE);
        btnBioBanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showBioBankGenomeDialog();
            }
        });
        secondaryDock.add(btnBioBanks);

        btnCargoHold = new JButton("📦 Cargo Hold");
        btnCargoHold.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCargoHold.setBackground(new Color(25, 90, 110));
        btnCargoHold.setForeground(Color.WHITE);
        btnCargoHold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showCargoHoldDialog();
            }
        });
        secondaryDock.add(btnCargoHold);

        btnTimeMode = new JButton("⏱ Mode: Turn-Based");
        btnTimeMode.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnTimeMode.setBackground(new Color(80, 50, 110));
        btnTimeMode.setForeground(Color.WHITE);
        btnTimeMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                toggleTimeModeAction();
            }
        });
        secondaryDock.add(btnTimeMode);

        btnMissionBriefing = new JButton("📜 Mission Directives");
        btnMissionBriefing.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMissionBriefing.setBackground(new Color(180, 110, 20));
        btnMissionBriefing.setForeground(Color.WHITE);
        btnMissionBriefing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showMissionBriefingDialog();
            }
        });
        secondaryDock.add(btnMissionBriefing);

        btnCodex = new JButton("📖 Codex");
        btnCodex.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnCodex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistoricalCodexDialog();
            }
        });
        secondaryDock.add(btnCodex);

        btnScanner = new JButton("📻 Sub-Space Scanner");
        btnScanner.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnScanner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showSubSpaceScannerDialog();
            }
        });
        secondaryDock.add(btnScanner);

        btnAudioToggle = new JButton("Audio: " + (SoundEffects.isSoundEnabled() ? "ON" : "OFF"));
        btnAudioToggle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnAudioToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.setSoundEnabled(!SoundEffects.isSoundEnabled());
                btnAudioToggle.setText("Audio: " + (SoundEffects.isSoundEnabled() ? "ON" : "OFF"));
                log("Audio Synthesis: " + (SoundEffects.isSoundEnabled() ? "ENABLED" : "MUTED"));
            }
        });
        secondaryDock.add(btnAudioToggle);

        bottomPanel.add(secondaryDock);
        bottomPanel.add(Box.createVerticalStrut(4));

        txtLog = new JTextArea(3, 50);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(new Color(8, 10, 15));
        txtLog.setForeground(new Color(180, 220, 240));
        txtLog.setText("=== Sovereign Telemetry Log Online ===\nFollow the active instructional directive in the top banner.\n");
        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 65)),
                "Tactical Comm-Link", 0, 0, new Font("Monospaced", Font.PLAIN, 10), new Color(130, 160, 190)));
        bottomPanel.add(scroll);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        refreshDropdowns();
        return panel;
    }

    private JPanel buildCitadelNexusHub() {
        JPanel hubPanel = new JPanel(new BorderLayout(8, 4));
        hubPanel.setBackground(new Color(16, 20, 32));
        hubPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoPanel.setOpaque(false);

        lblCitadelHubStats = new JLabel("🏛️ CITADEL NEXUS // RELAY CORE: Tier I (Dormant Hub)  |  PASSIVE DIVIDENDS: +10 Eezo/Epoch", SwingConstants.LEFT);
        lblCitadelHubStats.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblCitadelHubStats.setForeground(new Color(0, 230, 255));

        lblConduitCorridors = new JLabel("CONDUIT STATUS: Sector 0 [ONLINE] | Sector 1 [LOCKED] | Sector 2 [LOCKED] | Sector 3 [LOCKED]", SwingConstants.LEFT);
        lblConduitCorridors.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblConduitCorridors.setForeground(new Color(170, 210, 240));

        infoPanel.add(lblCitadelHubStats);
        infoPanel.add(lblConduitCorridors);
        hubPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel hubButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hubButtons.setOpaque(false);

        JButton btnManageCitadel = new JButton("🏛️ Manage Nexus");
        btnManageCitadel.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnManageCitadel.setBackground(new Color(30, 90, 140));
        btnManageCitadel.setForeground(Color.WHITE);
        btnManageCitadel.setFocusPainted(false);
        btnManageCitadel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                showNexusDialog();
            }
        });

        JButton btnCitadelLockdown = new JButton("🔒 Arms Lockdown");
        btnCitadelLockdown.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCitadelLockdown.setBackground(new Color(140, 40, 50));
        btnCitadelLockdown.setForeground(Color.WHITE);
        btnCitadelLockdown.setFocusPainted(false);
        btnCitadelLockdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CitadelNexus nexus = engine.getNexus();
                if (nexus.getTier() < CitadelNexus.TIER_3_LOCKDOWN) {
                    JOptionPane.showMessageDialog(HarvestGUI.this,
                            "Citadel Arms Lockdown requires Citadel Nexus Tier III or higher.\nUpgrade the Citadel Mega-Structure first!",
                            "Lockdown Unavailable", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!nexus.isLockdownReady()) {
                    JOptionPane.showMessageDialog(HarvestGUI.this,
                            "Citadel Arms Lockdown has already been triggered for this cycle.",
                            "Lockdown Exhausted", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                SoundEffects.playReaperHorn();
                String msg = nexus.triggerCitadelLockdown(campaign);
                JOptionPane.showMessageDialog(HarvestGUI.this, msg, "Arms Lockdown Engaged", JOptionPane.INFORMATION_MESSAGE);
                log("CITADEL ARMS LOCKDOWN ENGAGED: Crucible construction severely disrupted (-25% progress)!");
                updateDisplay();
            }
        });

        btnToggleMapView = new JButton("⊞ Switch to Grid Matrix");
        btnToggleMapView.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnToggleMapView.setBackground(new Color(25, 45, 75));
        btnToggleMapView.setForeground(new Color(0, 230, 255));
        btnToggleMapView.setFocusPainted(false);
        btnToggleMapView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffects.playUiClick();
                isMapViewActive = !isMapViewActive;
                if (isMapViewActive) {
                    galaxyCardLayout.show(galaxyCenterCards, "MAP");
                    btnToggleMapView.setText("⊞ Switch to Grid Matrix");
                    btnToggleMapView.setBackground(new Color(25, 45, 75));
                } else {
                    galaxyCardLayout.show(galaxyCenterCards, "GRID");
                    btnToggleMapView.setText("🌌 Switch to Star Chart");
                    btnToggleMapView.setBackground(new Color(50, 30, 80));
                }
            }
        });

        hubButtons.add(btnManageCitadel);
        hubButtons.add(btnCitadelLockdown);
        hubButtons.add(btnToggleMapView);
        hubPanel.add(hubButtons, BorderLayout.EAST);

        return hubPanel;
    }

    private void refreshDropdowns() {
        if (cmbSpecies == null || cmbUnits == null) return;
        cmbSpecies.removeAllItems();
        for (String sp : missionManager.getUnlockedSpecies(engine)) {
            Civilization tempCiv = engine.createCivilization(sp);
            boolean sequenced = engine.isGenomeSequenced(sp);
            cmbSpecies.addItem(String.format("%s (%d Eezo)%s", sp, tempCiv.getSeedingCost(), sequenced ? "" : " [Unsequenced]"));
        }

        cmbUnits.removeAllItems();
        for (String u : missionManager.getUnlockedUnits()) {
            int eCost = u.toLowerCase().contains("scion") ? 60 : (u.toLowerCase().contains("drone") ? 75 : 30);
            int bCost = u.toLowerCase().contains("scion") ? 140 : (u.toLowerCase().contains("drone") ? 0 : 50);
            if (bCost > 0) {
                cmbUnits.addItem(String.format("%s (%d Eezo, %d Bio)", u, eCost, bCost));
            } else {
                cmbUnits.addItem(String.format("%s (%d Eezo)", u, eCost));
            }
        }
        updateSeedingIntel();
    }

    private String getSelectedSpeciesRaw() {
        if (cmbSpecies == null || cmbSpecies.getSelectedItem() == null) return "Humanity";
        String item = (String) cmbSpecies.getSelectedItem();
        if (item.contains(" (")) {
            return item.substring(0, item.indexOf(" (")).trim();
        }
        return item.trim();
    }

    private String getSelectedUnitRaw() {
        if (cmbUnits == null || cmbUnits.getSelectedItem() == null) return "Collector Drone";
        String item = (String) cmbUnits.getSelectedItem();
        if (item.contains(" (")) {
            return item.substring(0, item.indexOf(" (")).trim();
        }
        return item.trim();
    }

    private void updateSeedingIntel() {
        if (cmbSpecies == null || lblPlantCostInfo == null || engine == null || state == null) return;
        String sp = getSelectedSpeciesRaw();
        try {
            Civilization c = engine.createCivilization(sp);
            int cost = c.getSeedingCost();
            StarSystem selectedSys = state.getGalaxyMap().getSystem(selectedSector, selectedCluster);
            boolean sectorLocked = !state.getGalaxyMap().isSectorUnlocked(selectedSector);

            if (sectorLocked) {
                lblPlantCostInfo.setText("SECTOR BEYOND FTL REACH — Construct Primary Mass Relay in Tech Tree");
                btnSeed.setEnabled(false);
                btnSeed.setText("🔒 Sector Locked");
                btnSeed.setBackground(new Color(40, 30, 35));
                return;
            }

            String affinityTag = selectedSys.getClimateType().getAffinityDescription(sp);
            lblPlantCostInfo.setText(String.format("COST: %d Eezo  |  BIOME: %s  |  %s",
                    cost, selectedSys.getClimateType().getDisplayName(), affinityTag));

            if (selectedSys.getCivilization() == null) {
                boolean hasProbe = (engine != null && engine.hasGenesisProbeInCargo(sp));
                if (selectedSys.getClimateType() == ClimateType.BARREN) {
                    btnSeed.setEnabled(false);
                    btnSeed.setText("❄️ Planet Barren (Terraform First)");
                    btnSeed.setBackground(new Color(60, 40, 45));
                } else if (hasProbe) {
                    btnSeed.setEnabled(true);
                    btnSeed.setText(String.format("🌱 Deploy %s Probe (In Cargo - 0 Eezo)", sp));
                    btnSeed.setBackground(new Color(0, 180, 120));
                } else if (engine.isEnforceGenomeResearch() && !engine.isGenomeSequenced(sp)) {
                    btnSeed.setEnabled(true);
                    btnSeed.setText(String.format("🧬 Sequence %s Genome (40 E, 30 B)", sp));
                    btnSeed.setBackground(new Color(130, 50, 170));
                } else {
                    if (state.getEezoReserves() >= cost) {
                        btnSeed.setText(String.format("🌱 Plant %s (-%d Eezo)", sp, cost));
                        btnSeed.setEnabled(true);
                        btnSeed.setBackground(new Color(0, 140, 210));
                    } else {
                        btnSeed.setText(String.format("🌱 Insufficient Eezo (Need %d)", cost));
                        btnSeed.setEnabled(false);
                        btnSeed.setBackground(new Color(60, 40, 45));
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void continueGameAction() {
        try {
            File latest = SaveManager.getMostRecentSaveFile();
            if (latest != null && latest.exists()) {
                boolean loaded = SaveManager.loadGame(engine, latest);
                if (loaded) {
                    this.state = engine.getState();
                    this.campaign = engine.getCampaign();
                    this.techTree = engine.getTechTree();
                    this.missionManager = engine.getMissionManager();
                    this.victoryDialogShown = (campaign != null && campaign.isVictoryAcknowledged());
                    log("CAMPAIGN PROGRESSION RESTORED FROM: " + latest.getName());
                    if (galacticMapPanel != null) {
                        galacticMapPanel.setGameState(this.state, this.engine, this.missionManager);
                    }
                    refreshDropdowns();
                    setupRealTimeTimer();
                    cardLayout.show(containerPanel, "GAME_SCREEN");
                    updateDisplay();
                    SoundEffects.playRelayChime();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "No saved campaign found on disk. Please start a New Campaign.", "Load Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load save file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startNewCampaignAction() {
        String[] timeModes = {
            "Turn-Based Mode (Classic strategic pace — advance epochs manually)",
            "Real-Time Mode (Living Galaxy — civilizations grow every 6s in real time)"
        };
        int choice = JOptionPane.showOptionDialog(this,
                "Select Galaxy Time Flow Mode:\n(Note: You can freely switch between Real-Time and Turn-Based at any time during gameplay!)",
                "Choose Simulation Time Flow",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                timeModes,
                timeModes[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        this.victoryDialogShown = false;
        this.selectedSector = 0;
        this.selectedCluster = 0;
        this.state = new GalacticState("Harbinger", "Prologue: The Fall of Earth (ME3 Climax)");
        this.state.getGalaxyMap().setSectorUnlocked(0, true);
        this.state.getGalaxyMap().setSectorUnlocked(1, false);
        this.state.getGalaxyMap().setSectorUnlocked(2, false);
        this.state.getGalaxyMap().setSectorUnlocked(3, false);
        if (choice == 1) {
            this.state.setGrowthMode(GrowthMode.REAL_TIME);
        } else {
            this.state.setGrowthMode(GrowthMode.TURN_BASED);
        }
        this.engine = new ReaperEngine(state);
        this.engine.setEnforceGenomeResearch(true);
        this.campaign = engine.getCampaign();
        this.techTree = engine.getTechTree();
        this.missionManager = engine.getMissionManager();

        if (galacticMapPanel != null) {
            galacticMapPanel.setGameState(this.state, this.engine, this.missionManager);
        }

        refreshDropdowns();
        setupRealTimeTimer();
        cardLayout.show(containerPanel, "GAME_SCREEN");
        updateDisplay();
        showMissionBriefingDialog();
    }

    private void loadGameAction() {
        List<SaveManager.SaveMetadata> all = SaveManager.getAllSlotMetadata();
        List<String> validLabels = new ArrayList<String>();
        final List<Integer> validSlots = new ArrayList<Integer>();

        for (SaveManager.SaveMetadata meta : all) {
            if (meta.exists()) {
                validLabels.add(meta.getDisplayLabel());
                validSlots.add(meta.getSlotNumber());
            }
        }

        if (validLabels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved campaigns found on disk. Please start a New Campaign.", "No Saves Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        validLabels.add("Cancel");
        String[] options = validLabels.toArray(new String[0]);

        int choice = JOptionPane.showOptionDialog(this,
                "Select a saved campaign slot to load and restore:",
                "Load Saved Campaign // Select Slot",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice >= 0 && choice < validSlots.size()) {
            int slot = validSlots.get(choice);
            try {
                boolean loaded = SaveManager.loadGame(engine, SaveManager.getSlotFile(slot));
                if (loaded) {
                    this.state = engine.getState();
                    this.campaign = engine.getCampaign();
                    this.techTree = engine.getTechTree();
                    this.missionManager = engine.getMissionManager();
                    this.victoryDialogShown = (campaign != null && campaign.isVictoryAcknowledged());
                    if (galacticMapPanel != null) {
                        galacticMapPanel.setGameState(this.state, this.engine, this.missionManager);
                    }
                    refreshDropdowns();
                    setupRealTimeTimer();
                    cardLayout.show(containerPanel, "GAME_SCREEN");
                    updateDisplay();
                    SoundEffects.playRelayChime();
                    String slotDesc = (slot == 0) ? "Auto-Save" : ("Slot " + slot);
                    JOptionPane.showMessageDialog(this, "Campaign restored from " + slotDesc + "!", "Save Loaded", JOptionPane.INFORMATION_MESSAGE);
                    log("CAMPAIGN RESTORED FROM " + slotDesc.toUpperCase() + ".");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load slot: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveGameAction() {
        String[] slotOptions = new String[SaveManager.MAX_SLOTS + 1];
        for (int i = 1; i <= SaveManager.MAX_SLOTS; i++) {
            SaveManager.SaveMetadata meta = SaveManager.getSlotMetadata(i);
            slotOptions[i - 1] = meta.getDisplayLabel();
        }
        slotOptions[SaveManager.MAX_SLOTS] = "Cancel";

        int choice = JOptionPane.showOptionDialog(this,
                "Select a Save Slot to record your current campaign state:\n(Choose any slot to overwrite and maintain multiple historical checkpoints)",
                "Save Campaign // Select Slot",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, slotOptions, slotOptions[0]);

        if (choice >= 0 && choice < SaveManager.MAX_SLOTS) {
            int selectedSlot = choice + 1;
            try {
                SaveManager.saveSlot(engine, selectedSlot);
                SaveManager.autoSave(engine);
                SoundEffects.playUiClick();
                JOptionPane.showMessageDialog(this,
                        String.format("Campaign state successfully recorded to Slot %d!\n\n%s",
                                selectedSlot, SaveManager.getSlotMetadata(selectedSlot).getDisplayLabel()),
                        "Campaign Saved", JOptionPane.INFORMATION_MESSAGE);
                log("CAMPAIGN SAVED TO SLOT " + selectedSlot + ".");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save to Slot " + selectedSlot + ": " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectSystem(int sector, int cluster) {
        GalacticSector galaxy = state.getGalaxyMap();
        if (planetTiles != null && selectedSector >= 0 && selectedSector < galaxy.getRowCount() && selectedCluster >= 0 && selectedCluster < galaxy.getColCount()) {
            planetTiles[selectedSector][selectedCluster].setSelected(false);
        }
        this.selectedSector = sector;
        this.selectedCluster = cluster;
        if (planetTiles != null && selectedSector >= 0 && selectedSector < galaxy.getRowCount() && selectedCluster >= 0 && selectedCluster < galaxy.getColCount()) {
            planetTiles[selectedSector][selectedCluster].setSelected(true);
        }
        if (galacticMapPanel != null) {
            galacticMapPanel.setSelectedSystem(sector, cluster);
        }
        StarSystem sys = galaxy.getSystem(sector, cluster);
        log(String.format("Target Lock: %s [%d, %d] | Biome: %s | Occupant: %s",
                sys.getSystemName(), sector, cluster, sys.getClimateType().getDisplayName(),
                sys.getCivilization() != null ? sys.getCivilization().toString() : "Uninhabited"));
        checkMissionProgress();
        updateDisplay();
    }

    private void seedSelectedCivilization() {
        StarSystem sys = state.getGalaxyMap().getSystem(selectedSector, selectedCluster);
        if (sys.getClimateType() == ClimateType.BARREN) {
            int tfChoice = JOptionPane.showConfirmDialog(this,
                    String.format("Planet %s is BARREN and cannot support organic life!\n\n"
                            + "Would you like to terraform this world now (Cost: 75 Eezo)?", sys.getSystemName()),
                    "Barren World", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (tfChoice == JOptionPane.YES_OPTION) {
                showTerraformDialog();
            }
            return;
        }

        String species = getSelectedSpeciesRaw();
        boolean hasProbeInCargo = (engine != null && engine.hasGenesisProbeInCargo(species));
        boolean storyUnlocked = (missionManager != null && missionManager.isSpeciesUnlocked(species, engine));
        if (!hasProbeInCargo && engine.isEnforceGenomeResearch() && !engine.isGenomeSequenced(species) && !storyUnlocked) {
            int eCost = engine.getGenomeEezoCost(species);
            int bCost = engine.getGenomeBiomassCost(species);
            int synthChoice = JOptionPane.showConfirmDialog(this,
                    String.format("The genome for %s is unsequenced in Reaper Bio-Banks.\n\n"
                            + "Synthesizing costs %d Eezo and %d Genetic Biomass.\n"
                            + "Would you like to synthesize this genome now?", species, eCost, bCost),
                    "Genome Synthesis Required", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (synthChoice == JOptionPane.YES_OPTION) {
                try {
                    engine.sequenceGenome(species);
                    SoundEffects.playUiClick();
                    log(String.format("GENOME SYNTHESIZED: %s genetic archive unlocked from dark space bio-banks (-%d Eezo, -%d Bio).",
                            species, eCost, bCost));
                    refreshDropdowns();
                    updateDisplay();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Synthesis Failed", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }

        try {
            Civilization targetCiv = engine.createCivilization(species);
            boolean hadProbe = engine.hasGenesisProbeInCargo(species);
            engine.seedCivilization(selectedSector, selectedCluster, species);
            if (hadProbe) {
                log(String.format("GENESIS PROBE DEPLOYED: Deployed %s Genesis Probe from flagship cargo! Seeding cost waived (+25%% Incubation).",
                        species));
            } else {
                log(String.format("Genesis Probe deployed: Incubating %s on [%d, %d] (-%d Eezo).",
                        species, selectedSector, selectedCluster, targetCiv.getSeedingCost()));
            }
            checkMissionProgress();
        } catch (InsufficientEezoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Eezo Reserves", JOptionPane.WARNING_MESSAGE);
        } catch (SystemOccupiedException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Deployment Collision", JOptionPane.WARNING_MESSAGE);
        } catch (ReaperException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Deployment Aborted", JOptionPane.WARNING_MESSAGE);
        }
        updateDisplay();
    }

    private void showTerraformDialog() {
        if (!state.getGalaxyMap().isSectorUnlocked(selectedSector)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot terraform worlds in a locked sector!\nConstruct this sector's Primary Mass Relay in the Tech Tree first.",
                    "Sector Inaccessible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StarSystem sys = state.getGalaxyMap().getSystem(selectedSector, selectedCluster);
        ClimateType currentClimate = sys.getClimateType();

        String[] options = {
            "Garden Biome (Earth/Sur'Kesh Archetype — Favors Humanity, Salarian, Prothean, Asari)",
            "Arid Biome (Palaven Archetype — Favors Turian, Drell, Batarian)",
            "Methane Biome (Irune Archetype — Favors Volus, Vorcha)",
            "Volcanic Biome (Tuchanka Archetype — Favors Krogan, Yahg, Rachni)",
            "Cancel"
        };

        String prompt = String.format(
                "TERRAFORMING ATMOSPHERIC CONVERTER\n\n"
                + "Target World: %s [%d, %d]\n"
                + "Current Climate: %s\n"
                + "Fleet Eezo Reserves: %d Eezo\n"
                + "Terraforming Cost: 75 Eezo\n\n"
                + "Affinities: Matching biomes grant +50%% Evolution Growth & +25%% Ascension Yield.\n"
                + "Hostile biomes suffer -50%% Growth & -25%% Yield.\n"
                + "Barren dead rocks must be terraformed before seeding.\n\n"
                + "Select target atmospheric biome:",
                sys.getSystemName(), selectedSector, selectedCluster,
                currentClimate.getDisplayName(), state.getEezoReserves());

        int choice = JOptionPane.showOptionDialog(
                this,
                prompt,
                "Terraform Planetary Climate // " + sys.getSystemName(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice < 0 || choice == 4) return;

        ClimateType target;
        switch (choice) {
            case 0: target = ClimateType.GARDEN; break;
            case 1: target = ClimateType.ARID; break;
            case 2: target = ClimateType.METHANE; break;
            case 3: target = ClimateType.VOLCANIC; break;
            default: return;
        }

        if (target == currentClimate) {
            JOptionPane.showMessageDialog(this,
                    String.format("Planet %s is already configured as %s biome.", sys.getSystemName(), target.getDisplayName()),
                    "Terraform Redundant", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            engine.terraformSystem(selectedSector, selectedCluster, target);
            SoundEffects.playRelayChime();
            log(String.format("TERRAFORMING COMPLETE: %s [%d, %d] converted to %s biome (-75 Eezo).",
                    sys.getSystemName(), selectedSector, selectedCluster, target.getDisplayName()));
            JOptionPane.showMessageDialog(this,
                    String.format("Atmospheric Conditioning Complete!\n\n%s [%d, %d] is now a %s.\nFavorable Species: %s",
                            sys.getSystemName(), selectedSector, selectedCluster, target.getDisplayName(), target.getFavorableSpeciesNames()),
                    "Terraforming Successful", JOptionPane.INFORMATION_MESSAGE);
            checkMissionProgress();
            updateDisplay();
        } catch (InsufficientEezoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Eezo Reserves", JOptionPane.WARNING_MESSAGE);
        } catch (ReaperException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Terraforming Aborted", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deploySelectedUnit() {
        String unit = getSelectedUnitRaw();
        try {
            engine.deployUnit(selectedSector, selectedCluster, unit);
            log(String.format("Biomechanical Swarm stationed: %s at [%d, %d].",
                    unit, selectedSector, selectedCluster));
            checkMissionProgress();
        } catch (InsufficientEezoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Eezo Reserves", JOptionPane.WARNING_MESSAGE);
        } catch (InsufficientBiomassException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Genetic Biomass", JOptionPane.WARNING_MESSAGE);
        } catch (SystemOccupiedException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Deployment Error", JOptionPane.WARNING_MESSAGE);
        } catch (ReaperException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Deployment Aborted", JOptionPane.WARNING_MESSAGE);
        }
        updateDisplay();
    }

    private void harvestSelectedSystem() {
        try {
            HarvestYield yield = engine.harvestSystem(selectedSector, selectedCluster);
            String msg = String.format("ASCENSION COMPLETE: Extracted %d Biomass and %d Dark Energy from %s.",
                    yield.getGeneticBiomass(), yield.getDarkEnergyYield(), yield.getOriginSpecies());
            log(msg);
            checkMissionProgress();
            JOptionPane.showMessageDialog(this, msg, "Ascension Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (CivilizationPrematureException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Premature Harvest Aborted", JOptionPane.ERROR_MESSAGE);
        } catch (CivilizationBarrierException e) {
            JOptionPane.showMessageDialog(this,
                    "ASCENSION DEFLECTED BY PLANETARY KINETIC BARRIER!\n\n"
                    + e.getMessage() + "\n\n"
                    + "Tactical Breach Solutions:\n"
                    + "  • Station a Scion Behemoth or Husk Swarm garrison on this planet.\n"
                    + "  • Research 'Cyclonic Kinetic Shield Matrix' in the Tech Tree.\n"
                    + "  • Jump the Sovereign Flagship directly to this system.",
                    "Orbital Defense Deflection", JOptionPane.WARNING_MESSAGE);
        } catch (CargoHoldFullException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Cargo Hold Full", JOptionPane.WARNING_MESSAGE);
        } catch (ReaperException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ascension Aborted", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Target Empty", JOptionPane.INFORMATION_MESSAGE);
        }
        updateDisplay();
    }

    private void checkMissionProgress() {
        if (missionManager == null || campaign == null) return;

        MissionManager.Mission active = missionManager.getActiveMission();
        if (active.isCompleted()) {
            missionManager.advanceMission();
            active = missionManager.getActiveMission();
        }

        int oldTaskIndex = active.getCurrentTaskIndex();

        boolean justCompleted = missionManager.checkMissionTriggers(
                state, techTree, campaign.getTotalAscensions(), selectedSector, selectedCluster);

        int newTaskIndex = active.getCurrentTaskIndex();

        if (justCompleted || active.isCompleted()) {
            SoundEffects.playRelayChime();
            String completedTitle = active.getMissionTitle();

            missionManager.advanceMission();

            String unlockMsg = missionManager.getUnlockNotification(missionManager.getActiveMissionIndex());
            if (unlockMsg != null && !unlockMsg.isEmpty()) {
                SoundEffects.playReaperHorn();
                JOptionPane.showMessageDialog(this,
                        "MISSION DIRECTIVE ACCOMPLISHED!\n\n"
                        + unlockMsg,
                        "Assets Unlocked // Story Progression", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "MISSION DIRECTIVE ACCOMPLISHED!\n\n"
                        + completedTitle + " complete.\n"
                        + "Advancing to the next campaign act!",
                        "Mission Complete", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshDropdowns();
            showMissionBriefingDialog();
            SaveManager.autoSave(engine);
        } else if (newTaskIndex > oldTaskIndex) {
            SoundEffects.playRelayChime();
            log(String.format("[MISSION OBJECTIVE UPDATED] %s", active.getActiveTaskText()));
        }

        updateDisplay();

        // Check for pending Story Dilemmas
        checkPendingStoryDilemma();

        // Check Campaign Victory or Crucible Defeat
        checkCampaignEndConditions();
    }

    private void checkPendingStoryDilemma() {
        if (engine == null || engine.getDilemmaManager() == null || campaign == null) return;
        com.seb.harvesteffect.story.StoryDilemma dilemma =
                engine.getDilemmaManager().getPendingDilemmaForCampaign(campaign.getCurrentAct());
        if (dilemma == null) return;

        SoundEffects.playRadioStatic(400);

        String[] options = {
            "<html><b style='color: #000000;'>" + dilemma.getChoiceA().getLabel() + "</b><br><small style='color: #222222;'>" + dilemma.getChoiceA().getDescription() + "</small></html>",
            "<html><b style='color: #000000;'>" + dilemma.getChoiceB().getLabel() + "</b><br><small style='color: #222222;'>" + dilemma.getChoiceB().getDescription() + "</small></html>"
        };

        String message = String.format("<html><body style='width: 480px; font-family: sans-serif; color: #000000;'>"
                + "<h3 style='color: #004D73; margin-top: 0; margin-bottom: 4px;'>⚡ %s</h3>"
                + "<p style='margin-top: 0; margin-bottom: 6px; color: #111111;'><b>COMM-LINK:</b> <span style='color: #A05500;'><b>%s</b></span></p>"
                + "<p style='font-style: italic; color: #000000; font-size: 12px; line-height: 1.4; margin-top: 6px; margin-bottom: 8px;'>\"%s\"</p>"
                + "<hr>"
                + "<p style='color: #000000; margin-top: 6px;'><b>Select Sovereign's Strategic Directive:</b></p></body></html>",
                dilemma.getTitle(), dilemma.getSpeaker(), dilemma.getTransmissionText());

        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Narrative Dilemma // " + dilemma.getTitle(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        com.seb.harvesteffect.story.StoryDilemma.Choice chosen = (choice == 1) ? dilemma.getChoiceB() : dilemma.getChoiceA();
        chosen.apply(state);
        engine.getDilemmaManager().markResolved(dilemma.getActId());
        SoundEffects.playRelayChime();

        String outcomeMsg = String.format("<html><body style='width: 440px; font-family: sans-serif; color: #000000;'>"
                + "<h4 style='color: #006622; margin-top: 0; margin-bottom: 6px;'>CONSEQUENCE OF DIRECTIVE:</h4>"
                + "<p style='color: #000000; font-size: 12px; line-height: 1.4;'>%s</p>"
                + "<p style='color: #7A5200; font-size: 12px;'><b>Fleet Impact: +%d Eezo, +%d Genetic Biomass</b></p></body></html>",
                chosen.getOutcomeNarrative(), chosen.getEezoBonus(), chosen.getBiomassBonus());

        JOptionPane.showMessageDialog(this, outcomeMsg, "Directive Outcome", JOptionPane.INFORMATION_MESSAGE);
        log(String.format("[DILEMMA RESOLVED] %s -> %s (+%d Eezo, +%d Bio).",
                dilemma.getTitle(), chosen.getLabel(), chosen.getEezoBonus(), chosen.getBiomassBonus()));
        SaveManager.autoSave(engine);
    }

    private void checkCampaignEndConditions() {
        if (campaign == null) return;

        if (campaign.isCampaignVictory()) {
            if (victoryDialogShown || campaign.isVictoryAcknowledged()) {
                return;
            }
            victoryDialogShown = true;
            campaign.setVictoryAcknowledged(true);
            SaveManager.autoSave(engine);

            if (realTimeTimer != null) realTimeTimer.stop();
            SoundEffects.playReaperHorn();
            String victoryMsg = "<html><body style='width: 540px; font-family: sans-serif; color: #000000;'>"
                    + "<h2 style='color: #006622; margin-top: 0;'>✦ VICTORY: THE HARVEST EFFECT PREVAILS ✦</h2>"
                    + "<p style='font-size: 13px; line-height: 1.4; color: #000000;'><b>\"The Crucible remnants are pulverized. The Citadel Nexus stands at Tier V.<br>"
                    + "Across 24 terraformed worlds and 4 sectors, organic civilizations are farmed,<br>"
                    + "nurtured, and methodically harvested in perpetual mechanical precision.<br>"
                    + "No wild evolution will ever threaten synthetic order again.<br>"
                    + "The Harvest Effect is eternal.\"</b></p>"
                    + "<hr>"
                    + String.format("<p style='font-size: 13px; color: #000000;'><b>Total Ascensions:</b> %d<br>"
                            + "<b>Final Biomass:</b> %d Units<br>"
                            + "<b>Element Zero Reserves:</b> %d</p>"
                    + "<p style='color: #004D73;'><b>You may continue in unrestricted Sandbox Galaxy Mode or return to the Main Menu.</b></p>"
                    + "</body></html>",
                    campaign.getTotalAscensions(), state.getAccumulatedBiomass(), state.getEezoReserves());

            String[] endOptions = { "Continue Sandbox Mode", "Return to Main Menu" };
            int res = JOptionPane.showOptionDialog(this, victoryMsg, "Campaign Victory",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, endOptions, endOptions[0]);
            if (res == 1) {
                cardLayout.show(containerPanel, "MAIN_MENU");
            } else {
                log("✦ CAMPAIGN VICTORY ARCHIVED: Entering Sandbox Galaxy Mode. Enjoy unrestricted harvesting!");
            }
            return;
        }

        if (campaign.isCrucibleDefeat()) {
            if (realTimeTimer != null) realTimeTimer.stop();
            SoundEffects.playCrucibleAlert();
            String defeatMsg = "<html><body style='width: 520px; font-family: sans-serif; color: #000000;'>"
                    + "<h2 style='color: #990000; margin-top: 0;'>⚠️ CRITICAL FAILURE: THE CRUCIBLE HAS FIRED! ⚠️</h2>"
                    + "<p style='font-size: 13px; line-height: 1.4; color: #000000;'><b>The Allied Fleets succeeded in defending the Crucible remnants construction site.<br>"
                    + "The surviving resistance triggered the catalyst beam.<br>"
                    + "A cataclysmic energy wave tears through all mass relays.<br>"
                    + "Sovereign and the Reaper armada have been annihilated.</b></p>"
                    + "<hr>"
                    + "<p style='color: #880000;'><b>The organics have broken the Harvest Effect.</b></p>"
                    + "</body></html>";

            String[] options = { "Load Last Save", "New Campaign", "Main Menu" };
            int res = JOptionPane.showOptionDialog(this, defeatMsg, "Campaign Defeated",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if (res == 0) {
                continueGameAction();
            } else if (res == 1) {
                startNewCampaignAction();
            } else {
                cardLayout.show(containerPanel, "MAIN_MENU");
            }
        }
    }

    private void showLargeStyledDialog(String title, String bannerTitle, String contentText, int width, int height) {
        final JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(width, height);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(11, 14, 23));
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        if (bannerTitle != null && !bannerTitle.isEmpty()) {
            JPanel banner = new JPanel(new BorderLayout());
            banner.setBackground(new Color(16, 22, 34));
            banner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 200, 240), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            JLabel lblBanner = new JLabel(bannerTitle, SwingConstants.LEFT);
            lblBanner.setFont(new Font("Monospaced", Font.BOLD, 14));
            lblBanner.setForeground(new Color(0, 230, 255));
            banner.add(lblBanner, BorderLayout.CENTER);
            root.add(banner, BorderLayout.NORTH);
        }

        JTextArea area = new JTextArea(contentText);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(15, 20, 30));
        area.setForeground(new Color(225, 238, 255));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 45, 65)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        footer.setBackground(new Color(11, 14, 23));
        JButton btnClose = new JButton("ACKNOWLEDGE DIRECTIVE // CLOSE");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setBackground(new Color(0, 140, 200));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        footer.add(btnClose);
        root.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private void showMissionBriefingDialog() {
        MissionManager.Mission m = missionManager.getActiveMission();
        StringBuilder sb = new StringBuilder();
        sb.append(m.getCycleEraTitle()).append("\n");
        sb.append(m.getMissionTitle()).append("\n\n");
        sb.append(m.getTacticalBriefing()).append("\n\n");
        sb.append("TACTICAL TASKS:\n");
        for (int i = 0; i < m.getTaskDescriptions().length; i++) {
            String check = (i < m.getCurrentTaskIndex() || m.isCompleted()) ? "[X]" : "[ ]";
            sb.append(String.format("  %s %s\n", check, m.getTaskDescriptions()[i]));
        }

        showLargeStyledDialog(m.getMissionTitle(), "⚡ MISSION DIRECTIVE: " + m.getCycleEraTitle(), sb.toString(), 920, 640);
    }

    private void showHistoricalCodexDialog() {
        StringBuilder sb = new StringBuilder("=== THE REAPER CAMPAIGN & NURSERY CODEX ===\n\n");
        sb.append("PROLOGUE: THE FALL OF EARTH (2186 CE / ME3 CLIMAX)\n");
        sb.append("   Shepard refuses the Catalyst's choices. The Crucible is crushed, but the dark energy\n");
        sb.append("   backlash shatters the galactic mass relays and damages the Citadel. Harbinger establishes\n");
        sb.append("   Directive: The Harvest Effect — controlled planetary nurseries seeded from bio-banks.\n\n");
        sb.append("ACT I: THE RUINED CITADEL & SOL SILO\n");
        sb.append("   The damaged Citadel core is reconstructed, awakening Keepers. Organics are synthesized\n");
        sb.append("   from genetic databanks, and scorched worlds in Sector 0 are terraformed.\n\n");
        sb.append("ACT II: THE ATTICAN TRAVERSE & MARTIAL GENOMES\n");
        sb.append("   Primary Relay Alpha is constructed to reconnect Sector 1. Dense martial species\n");
        sb.append("   (Turians, Krogan, Batarians) erect orbital kinetic barriers requiring heavy breaching.\n\n");
        sb.append("ACT III: THE PERSEUS VEIL & SYNTHETIC HERESY\n");
        sb.append("   Primary Relay Omega reaches Sector 2. Rogue Geth/AI heresy threatens to infect Relay\n");
        sb.append("   channels, suppressed by Sub-Space Indoctrination Emitters and Husk Swarms.\n\n");
        sb.append("ACT IV: THE SHADOW RIM & PRECURSOR CLONES\n");
        sb.append("   Primary Relay Gamma unlocks Sector 3. Allied Crucible remnants attempt a desperate\n");
        sb.append("   last-stand weapon while spacefaring species launch creeping off-world colonies.\n\n");
        sb.append("ACT V: THE ETERNAL SILO & CATALYST CONVERGENCE\n");
        sb.append("   The Citadel awakens to Tier 5 Catalyst Convergence. All 24 worlds across 4 sectors\n");
        sb.append("   are cultivated in perpetual, automated mechanical perfection.\n");

        showLargeStyledDialog("Reaper Campaign Codex", "📖 THE REAPER CAMPAIGN & PLANETARY NURSERY CODEX", sb.toString(), 980, 720);
    }

    private void showCreditsDialog() {
        String cr = "THE HARVEST EFFECT\n\n"
                + "Developer: Sebastian\n"
                + "Project: Object-Oriented Programming in Java\n"
                + "Engine Architecture: Clean OOP / Observer Pattern / MVC\n"
                + "Framework: Java Swing (javax.swing / java.awt)\n"
                + "Audio: Procedural Sound Synthesizer (javax.sound.sampled)\n\n"
                + "Tribute to the Mass Effect trilogy (BioWare / Electronic Arts).\n"
                + "Built with pure Java 8+ and zero third-party dependencies.";
        showLargeStyledDialog("Transmission Credits", "✨ THE HARVEST EFFECT // ARCHIVE CREDITS", cr, 760, 520);
    }

    private void showCargoHoldDialog() {
        final JDialog dlg = new JDialog(this, "Flagship Cargo Hold // Subsystems & Stored Manifest", true);
        dlg.setSize(940, 660);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(11, 14, 23));
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBackground(new Color(18, 24, 38));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 240), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JLabel lblTitle = new JLabel("📦 SOVEREIGN FLAGSHIP CARGO MANIFEST & MODULES", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0, 230, 255));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblArmor = new JLabel(String.format("FLAGSHIP HULL ARMOR: %d Integrity  |  CAPACITY: %d/%d Pods",
                state.getInstalledFleetArmorIntegrity(),
                state.getCargoHold().getOccupiedCount(), state.getCargoHold().getCapacity()), SwingConstants.RIGHT);
        lblArmor.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblArmor.setForeground(new Color(255, 215, 0));
        header.add(lblArmor, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        final DefaultListModel<String> listModel = new DefaultListModel<String>();
        final List<com.seb.harvesteffect.model.item.Resource> manifest = state.getCargoHold().getManifest();
        for (int i = 0; i < manifest.size(); i++) {
            com.seb.harvesteffect.model.item.Resource r = manifest.get(i);
            String type = "";
            if (r instanceof com.seb.harvesteffect.model.item.GenesisProbe) {
                type = " [GENESIS PROBE: Plant on World — Cost Waived & +25% Incubation]";
            } else if (r instanceof com.seb.harvesteffect.model.item.FleetComponent) {
                type = String.format(" [FLEET SUBSYSTEM: +%d Armor, Breaches Barriers, Yield Boost]",
                        ((com.seb.harvesteffect.model.item.FleetComponent) r).getArmorBuff());
            } else {
                type = String.format(" [ASCENSION YIELD: %d Biomass / %d Eezo Value]", r.getMassUnits(), r.getEezoValue());
            }
            listModel.addElement(String.format("Pod %02d: %-28s | %s", i + 1, r.getItemName(), type));
        }

        final JList<String> list = new JList<String>(listModel);
        list.setFont(new Font("Monospaced", Font.PLAIN, 12));
        list.setBackground(new Color(15, 20, 30));
        list.setForeground(new Color(225, 238, 255));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 60, 85)));
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setOpaque(false);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setOpaque(false);

        JButton btnLiquidate = new JButton("⚡ Liquidate Selected Asset (+Eezo)");
        btnLiquidate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLiquidate.setBackground(new Color(0, 150, 100));
        btnLiquidate.setForeground(Color.WHITE);
        btnLiquidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = list.getSelectedIndex();
                if (idx < 0 || idx >= state.getCargoHold().getOccupiedCount()) {
                    JOptionPane.showMessageDialog(dlg, "Select a cargo pod to liquidate.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int earned = engine.getNexus().liquidateAsset(idx, state);
                SoundEffects.playUiClick();
                JOptionPane.showMessageDialog(dlg,
                        String.format("Pod %d converted into %d Eezo.\nUpdated Reserves: %d Eezo",
                                idx + 1, earned, state.getEezoReserves()),
                        "Asset Liquidated", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                updateDisplay();
                showCargoHoldDialog();
            }
        });

        JButton btnEject = new JButton("🗑️ Eject Pod");
        btnEject.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnEject.setBackground(new Color(60, 40, 45));
        btnEject.setForeground(Color.WHITE);
        btnEject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = list.getSelectedIndex();
                if (idx < 0 || idx >= state.getCargoHold().getOccupiedCount()) {
                    JOptionPane.showMessageDialog(dlg, "Select a cargo pod to eject.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                com.seb.harvesteffect.model.item.Resource removed = state.getCargoHold().retrieve(idx);
                SoundEffects.playUiClick();
                JOptionPane.showMessageDialog(dlg,
                        String.format("Jettisoned '%s' into dark space. Pod %d is now free.", removed.getItemName(), idx + 1),
                        "Pod Ejected", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                updateDisplay();
                showCargoHoldDialog();
            }
        });

        btnRow.add(btnLiquidate);
        btnRow.add(btnEject);
        footer.add(btnRow, BorderLayout.WEST);

        JButton btnClose = new JButton("Close Cargo Bay");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        footer.add(btnClose, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private void showTechTreeDialog() {
        TechTreeDialog dialog = new TechTreeDialog(this, engine, new Runnable() {
            @Override
            public void run() {
                checkMissionProgress();
                updateDisplay();
                refreshDropdowns();
            }
        });
        dialog.setVisible(true);
        checkMissionProgress();
        updateDisplay();
        refreshDropdowns();
    }

    private void showNexusDialog() {
        CitadelNexusDialog dialog = new CitadelNexusDialog(this, engine.getNexus(), state, techTree, campaign);
        dialog.setVisible(true);
        updateDisplay();
    }

    private void handlePrimaryRelayInteraction(int sector) {
        GalacticSector galaxy = state.getGalaxyMap();
        if (galaxy.isSectorUnlocked(sector)) {
            SoundEffects.playRelayChime();
            JOptionPane.showMessageDialog(this,
                    "MASS RELAY NETWORK STATUS: ONLINE\n\n"
                            + "Primary Relay conduit is fully operational.\n"
                            + "Element Zero FTL corridor connects Citadel Nexus to " + galaxy.getSectorName(sector) + ".\n"
                            + "All connected star systems receive +50% dark energy transit bonus.",
                    galaxy.getSectorName(sector) + " // Conduit Operational",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String relayName = (sector == 1) ? "Primary Relay Alpha" : (sector == 2 ? "Primary Relay Omega" : "Primary Relay Gamma");
        int eezoCost = (sector == 1) ? 250 : (sector == 2 ? 400 : 450);
        int bioCost = (sector == 1) ? 250 : (sector == 2 ? 450 : 500);

        int choice = JOptionPane.showConfirmDialog(this,
                "PRIMARY MASS RELAY OFFLINE: " + relayName + "\n\n"
                        + "This primary relay links the Citadel Conduit to " + galaxy.getSectorName(sector) + ".\n"
                        + "Construction Requirements: " + eezoCost + " Eezo, " + bioCost + " Biomass.\n"
                        + "Current Reserves: " + state.getEezoReserves() + " Eezo, " + state.getAccumulatedBiomass() + " Biomass.\n\n"
                        + "Would you like to open the Tech Tree to construct this relay?",
                "Construct " + relayName,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            showTechTreeDialog();
        }
    }

    private void showGalacticEconomyDialog() {
        StringBuilder sb = new StringBuilder("=== GALACTIC ASCENSION ECONOMY & INVESTMENT LEDGER ===\n\n");
        sb.append(String.format("FLEET TREASURY: %d Eezo  |  %d Accumulated Biomass\n",
                state.getEezoReserves(), state.getAccumulatedBiomass()));
        sb.append(String.format("PASSIVE INCOME: +%d Eezo / Epoch (Volus Dividends & Telemetry)\n\n",
                state.calculatePassiveDividends()));

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-12s | %-10s | %-12s | %-12s | %-12s | %s\n",
                "SPECIES", "COST", "GROWTH", "EST. BIOMASS", "EST. EEZO", "ECONOMIC TRAIT / ROI"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (String sp : missionManager.getUnlockedSpecies(engine)) {
            Civilization c = engine.createCivilization(sp);
            int cost = c.getSeedingCost();
            int baseBio = c.calculateBiomassScore();
            int baseEezo = c.calculateDarkEnergyYield();
            String trait = c.getRacialTrait();
            if (trait.length() > 28) trait = trait.substring(0, 25) + "...";

            sb.append(String.format("%-12s | %4d Eezo   | 3 Epochs     | ~%-9d   | ~%-9d   | %s\n",
                    c.getSpeciesName(), cost, baseBio * 3, baseEezo * 2, trait));
        }

        sb.append("----------------------------------------------------------------------------------------\n\n");
        sb.append("BIOMECHANICAL SWARM CONSTRUCTS & MACHINERY:\n");
        sb.append("  • Collector Drone [75 Eezo,   0 Bio]: Mechanical scout & Growth Catalyst (2x evolution) + telemetry.\n");
        sb.append("  • Husk Swarm      [30 Eezo,  50 Bio]: Assimilated organic garrison (shields against civil insurrections).\n");
        sb.append("  • Scion Behemoth  [60 Eezo, 140 Bio]: Heavy bio-tank (+50% bonus Biomass & Dark Energy on Ascension).\n");
        sb.append("  • Mass Relay      [50 Eezo,   0 Bio]: FTL corridor connection (+50% dark energy transit bonus).\n\n");

        sb.append("ECONOMIC PRO-TIPS:\n");
        sb.append("  1. Plant Volus early to compound passive Eezo dividends into your fleet treasury every cycle.\n");
        sb.append("  2. Station Collector Drones to cut incubation time in half and accelerate time-to-harvest.\n");
        sb.append("  3. Deploy Scion Behemoths on high-value worlds (Asari, Krogan, Protheans) for massive profit.\n");

        showLargeStyledDialog("Galactic Economy Ledger", "📊 GALACTIC DARK ENERGY & BIOMASS LEDGER", sb.toString(), 920, 660);
    }

    private void showSubSpaceScannerDialog() {
        SubSpaceScanner scanner = engine.getScanner();
        if (scanner.hasPendingUndecodedSignal()) {
            SubSpaceScanner.SignalTransmission pending = scanner.getActivePendingSignal();
            FrequencyDecoderDialog dlg = new FrequencyDecoderDialog(this, pending, scanner, state);
            dlg.setVisible(true);
            updateDisplay();
            return;
        }

        List<SubSpaceScanner.SignalTransmission> decoded = scanner.getDecodedArchive();
        List<SubSpaceScanner.SignalTransmission> discovered = scanner.getAllDiscoveredSignals();

        StringBuilder sb = new StringBuilder("=== SUB-SPACE TELEMETRY & TRANSMISSION SCANNER ===\n\n");
        sb.append(String.format("Scanner Status: ACTIVE  |  Discovered Signals: %d/7  |  Decrypted Archives: %d/7\n\n",
                discovered.size(), decoded.size()));

        if (decoded.isEmpty()) {
            sb.append("No decrypted transmissions in archives yet.\n\n");
            sb.append("LORE SIGNAL RADAR HINTS:\n");
            sb.append("  • Shepard Anomaly: Inspect or interact with Sol [0, 0].\n");
            sb.append("  • Mordin Solus: Deploy STG research on Sur'Kesh [0, 3].\n");
            sb.append("  • Garrus Calibrations: Palaven [0, 2] during Solar Flare Surge.\n");
            sb.append("  • Blasto Spectre: Upgrade Citadel Nexus or Kahje [1, 3] in Dark Storm.\n");
            sb.append("  • Space Hamster: Stockpile biomass reserves.\n");
            sb.append("  • Marauder Shields: Advance into the Crucible War (Act 4).\n");
            sb.append("  • Conrad Verner: Long-term epoch progression & civilian flights.\n\n");
            sb.append("When an anomalous carrier wave is detected, tune the frequency dial (100.0 - 999.0 MHz) to decode it!");
            JOptionPane.showMessageDialog(this, sb.toString(), "Sub-Space Telemetry Scanner", JOptionPane.INFORMATION_MESSAGE);
        } else {
            sb.append("DECRYPTED SIGNAL ARCHIVES:\n");
            for (int i = 0; i < decoded.size(); i++) {
                SubSpaceScanner.SignalTransmission sig = decoded.get(i);
                sb.append(String.format("[%d] %s (%.1f MHz)\n    Source: %s\n\n",
                        i + 1, sig.getTitle(), sig.getTargetFrequencyMHz(), sig.getSource()));
            }
            sb.append("Enter signal number to re-read archived transcript (or Cancel):");

            String input = JOptionPane.showInputDialog(this, sb.toString(),
                    "Decrypted Sub-Space Archives", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int idx = Integer.parseInt(input.trim()) - 1;
                    if (idx >= 0 && idx < decoded.size()) {
                        SubSpaceScanner.SignalTransmission selected = decoded.get(idx);
                        JTextArea area = new JTextArea(selected.getRawContent(), 16, 52);
                        area.setEditable(false);
                        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        area.setBackground(new Color(15, 20, 30));
                        area.setForeground(new Color(220, 240, 255));
                        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                                selected.getTitle() + " [" + selected.getTargetFrequencyMHz() + " MHz]",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private void log(String msg) {
        txtLog.append(msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public void updateDisplay() {
        MissionManager.Mission m = missionManager.getActiveMission();
        lblCycleTitle.setText(m.getCycleEraTitle());
        lblMissionTitle.setText(m.getMissionTitle());
        lblTaskDirective.setText(m.getActiveTaskText());

        if (campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR) {
            barCrucible.setVisible(true);
            barCrucible.setValue(campaign.getCrucibleProgress());
            List<String> unattended = campaign.getUnattendedResearchWorldNames(state);
            String extra;
            if (!unattended.isEmpty()) {
                String worldList = String.join(", ", unattended);
                if (worldList.length() > 40) worldList = worldList.substring(0, 37) + "...";
                extra = String.format(" (Accelerated +%d%%: %s — Deploy Swarm Garrison to block labs!)",
                        unattended.size() * 3, worldList);
            } else {
                extra = " (All spacefaring labs suppressed by Swarm Garrisons)";
            }
            barCrucible.setString("CRITICAL THREAT: Crucible Completion: " + campaign.getCrucibleProgress() + "%" + extra);
        } else {
            barCrucible.setVisible(false);
        }

        lblStats.setText(String.format("SEASON: %s  |  EPOCH: %d  |  EEZO: %d  |  BIOMASS: %d  |  REAPED: %d",
                state.getCosmicSeason(), state.getCycleEpoch(),
                state.getEezoReserves(), state.getAccumulatedBiomass(),
                campaign.getTotalAscensions()));

        lblPhenomenon.setText("Cosmic State: " + state.getCurrentPhenomenon().getTitle()
                + " — " + state.getCurrentPhenomenon().getDescription());

        GalacticSector galaxy = state.getGalaxyMap();
        if (planetTiles != null) {
            for (int r = 0; r < galaxy.getRowCount(); r++) {
                for (int c = 0; c < galaxy.getColCount(); c++) {
                    planetTiles[r][c].setSystem(galaxy.getSystem(r, c));
                    planetTiles[r][c].setSelected(r == selectedSector && c == selectedCluster);
                    planetTiles[r][c].setFlagshipStationed(state.getFlagshipSector() == r && state.getFlagshipCluster() == c);
                    planetTiles[r][c].setSectorLocked(!galaxy.isSectorUnlocked(r));
                    planetTiles[r][c].repaint();
                }
            }
        }

        if (galacticMapPanel != null) {
            galacticMapPanel.setSelectedSystem(selectedSector, selectedCluster);
            galacticMapPanel.updateMap();
        }

        if (lblCitadelHubStats != null && engine.getNexus() != null) {
            CitadelNexus n = engine.getNexus();
            lblCitadelHubStats.setText(String.format("🏛️ CITADEL NEXUS // %s  |  PASSIVE DIVIDENDS: +%d Eezo/Epoch",
                    n.getTierName(), n.getPassiveEezoDividend()));
        }
        if (lblConduitCorridors != null) {
            StringBuilder csb = new StringBuilder("CONDUIT STATUS: ");
            for (int i = 0; i < galaxy.getRowCount(); i++) {
                boolean u = galaxy.isSectorUnlocked(i);
                csb.append(String.format("Sector %d (%s) [%s]%s",
                        i, galaxy.getSectorShortName(i), u ? "ONLINE" : "OFFLINE", i < galaxy.getRowCount() - 1 ? " | " : ""));
            }
            lblConduitCorridors.setText(csb.toString());
        }

        lblHeader.setText(String.format("THE HARVEST EFFECT // FLAGSHIP: %s [Sector %d, Cluster %d]",
                state.getFlagshipName(), state.getFlagshipSector(), state.getFlagshipCluster()));

        StarSystem selectedSys = galaxy.getSystem(selectedSector, selectedCluster);
        Civilization civ = selectedSys.getCivilization();
        BiomechanicalUnit unit = selectedSys.getBiomechanicalUnit();
        boolean sectorLocked = !galaxy.isSectorUnlocked(selectedSector);

        if (btnTerraform != null) {
            if (sectorLocked) {
                btnTerraform.setEnabled(false);
                btnTerraform.setText("🔒 Sector Locked");
                btnTerraform.setBackground(new Color(40, 30, 35));
            } else {
                int tfCost = 75;
                if (state.getEezoReserves() >= tfCost) {
                    btnTerraform.setEnabled(true);
                    btnTerraform.setText(String.format("🌍 Terraform World (-%d Eezo)", tfCost));
                    btnTerraform.setBackground(new Color(30, 130, 80));
                } else {
                    btnTerraform.setEnabled(false);
                    btnTerraform.setText(String.format("🌍 Terraform (Need %d Eezo)", tfCost));
                    btnTerraform.setBackground(new Color(60, 40, 45));
                }
            }
        }

        if (sectorLocked) {
            lblTargetStatus.setText(String.format("TARGET: Sector %d [%s]  |  STATUS: 🔒 BEYOND FTL REACH (Construct Primary Mass Relay in Tech Tree)",
                    selectedSector, galaxy.getSectorShortName(selectedSector)));
            lblTargetStatus.setForeground(new Color(255, 90, 80));

            btnSeed.setEnabled(false);
            btnSeed.setText("🔒 Sector Beyond FTL Reach");
            btnSeed.setBackground(new Color(40, 30, 35));

            btnHarvest.setEnabled(false);
            btnHarvest.setText("🔒 Sector Locked");
            btnHarvest.setBackground(new Color(40, 30, 35));

            btnRelay.setEnabled(false);
            btnRelay.setText("🔒 Primary Relay Required");
            btnRelay.setBackground(new Color(40, 30, 35));

            if (btnDeployUnit != null) {
                btnDeployUnit.setEnabled(false);
                btnDeployUnit.setText("🔒 Sector Inaccessible");
            }
            if (btnJumpFlagship != null) {
                btnJumpFlagship.setEnabled(false);
                btnJumpFlagship.setText("🔒 Sector Locked");
                btnJumpFlagship.setBackground(new Color(50, 40, 45));
            }
        } else {
            String unattendedTag = (campaign != null && campaign.getCurrentAct() == CampaignManager.Act.ACT_4_CRUCIBLE_WAR
                    && civ != null && civ.getEvolutionaryTier() >= Civilization.TIER_INDUSTRIAL && unit == null)
                    ? "  |  ⚠️ UNATTENDED LABS (+3% Crucible/Epoch - Station Garrison!)" : "";

            String garrisonTag = (unit != null)
                    ? String.format("  |  GARRISON: %s (%d EP - %s)",
                            unit.getDesignation(), unit.getEnergyLevel(),
                            unit instanceof CollectorDrone ? "+Growth Boost" : (unit instanceof ScionBehemoth ? "+50% Harvest Yield" : "Security"))
                    : unattendedTag;

            String barrierTag = (civ != null && civ.hasKineticBarrier()) ? "  |  🛡️ BARRIER ACTIVE" : "";
            String heresyTag = (civ != null && civ.isAiHeresyActive()) ? "  |  ⚠️ AI HERESY (-50% Yield)" : "";

            if (civ != null) {
                if (civ.isHarvestReady()) {
                    int estBiomass = civ.calculateBiomassScore();
                    int estEezo = civ.calculateDarkEnergyYield();
                    if (techTree.isUnlocked("biomass_vats")) estBiomass = (int) (estBiomass * 1.5);
                    if (techTree.isUnlocked("thanix_cannons")) estEezo = (int) (estEezo * 1.3);
                    if (selectedSys.getBiomechanicalUnit() instanceof ScionBehemoth) {
                        estBiomass = (int) (estBiomass * 1.5);
                        estEezo = (int) (estEezo * 1.5);
                    }

                    lblTargetStatus.setText(String.format("TARGET: %s [%d,%d]  |  SPECIES: %s (TIER 3 APEX ZENITH)%s%s%s  |  EST. ASCENSION REWARD: +%d Eezo, +%d Bio",
                            selectedSys.getSystemName(), selectedSector, selectedCluster,
                            civ.getSpeciesName().toUpperCase(), garrisonTag, barrierTag, heresyTag, estEezo, estBiomass));
                    lblTargetStatus.setForeground(new Color(0, 255, 150));

                    btnSeed.setEnabled(false);
                    btnSeed.setText("🌱 Populated (" + civ.getSpeciesName() + ")");
                    btnSeed.setBackground(new Color(40, 50, 65));

                    btnHarvest.setEnabled(true);
                    btnHarvest.setText(String.format("⚡ ASCENSION HARVEST (+%d Eezo, +%d Bio)", estEezo, estBiomass));
                    btnHarvest.setBackground(new Color(230, 40, 40));
                } else {
                    lblTargetStatus.setText(String.format("TARGET: %s [%d,%d]  |  SPECIES: %s (TIER %d/3)%s%s%s  |  POP: %dB  |  STATUS: INCUBATING (Advance Time to Grow)",
                            selectedSys.getSystemName(), selectedSector, selectedCluster,
                            civ.getSpeciesName(), civ.getEvolutionaryTier(), garrisonTag, barrierTag, heresyTag, civ.getPopulationBillions()));
                    lblTargetStatus.setForeground(new Color(255, 200, 60));

                    btnSeed.setEnabled(false);
                    btnSeed.setText("🌱 Growing (" + civ.getSpeciesName() + ")");
                    btnSeed.setBackground(new Color(40, 50, 65));

                    btnHarvest.setEnabled(false);
                    btnHarvest.setText(String.format("⏳ Growing (Tier %d/3 - Immature)", civ.getEvolutionaryTier()));
                    btnHarvest.setBackground(new Color(70, 30, 30));
                }
            } else if (unit != null) {
                lblTargetStatus.setText(String.format("TARGET: %s [%d,%d]%s  |  STATUS: Drone Standing By (Lifeless World — Ready for Planting)",
                        selectedSys.getSystemName(), selectedSector, selectedCluster, garrisonTag));
                lblTargetStatus.setForeground(new Color(100, 220, 255));

                btnHarvest.setEnabled(false);
                btnHarvest.setText("⚡ No Civilization on Target");
                btnHarvest.setBackground(new Color(50, 30, 30));
            } else if (selectedSys.getClimateType() == ClimateType.BARREN) {
                lblTargetStatus.setText(String.format("TARGET: %s [%d,%d]  |  STATUS: ❄️ BARREN DEAD ROCK (Terraform World to Seed Organics)",
                        selectedSys.getSystemName(), selectedSector, selectedCluster));
                lblTargetStatus.setForeground(new Color(180, 190, 205));

                btnHarvest.setEnabled(false);
                btnHarvest.setText("⚡ No Civilization on Target");
                btnHarvest.setBackground(new Color(50, 30, 30));
            } else {
                lblTargetStatus.setText(String.format("TARGET: %s [%d,%d]  |  STATUS: UNINHABITED (Lifeless World — Ready for Planting)",
                        selectedSys.getSystemName(), selectedSector, selectedCluster));
                lblTargetStatus.setForeground(new Color(0, 230, 255));

                btnHarvest.setEnabled(false);
                btnHarvest.setText("⚡ No Civilization on Target");
                btnHarvest.setBackground(new Color(50, 30, 30));
            }

            if (civ == null) {
                if (selectedSys.getClimateType() == ClimateType.BARREN) {
                    btnSeed.setEnabled(false);
                    btnSeed.setText("❄️ Planet Barren (Terraform First)");
                    btnSeed.setBackground(new Color(60, 40, 45));
                } else {
                    String selSp = getSelectedSpeciesRaw();
                    boolean hasProbe = (engine != null && engine.hasGenesisProbeInCargo(selSp));
                    if (hasProbe) {
                        btnSeed.setEnabled(true);
                        btnSeed.setText(String.format("🌱 Deploy %s Probe (In Cargo - 0 Eezo)", selSp));
                        btnSeed.setBackground(new Color(0, 180, 120));
                    } else if (engine.isEnforceGenomeResearch() && !engine.isGenomeSequenced(selSp)) {
                        btnSeed.setEnabled(true);
                        btnSeed.setText(String.format("🧬 Sequence %s (40 E, 30 B)", selSp));
                        btnSeed.setBackground(new Color(130, 50, 170));
                    } else {
                        int cost = 50;
                        try {
                            cost = engine.createCivilization(selSp).getSeedingCost();
                        } catch (Exception ignored) {}

                        if (state.getEezoReserves() >= cost) {
                            btnSeed.setEnabled(true);
                            btnSeed.setText(String.format("🌱 Plant %s (-%d Eezo)", selSp, cost));
                            btnSeed.setBackground(new Color(0, 140, 210));
                        } else {
                            btnSeed.setEnabled(false);
                            btnSeed.setText(String.format("🌱 Insufficient Eezo (Need %d)", cost));
                            btnSeed.setBackground(new Color(60, 40, 45));
                        }
                    }
                }
            }

            if (btnDeployUnit != null) {
                if (unit != null) {
                    btnDeployUnit.setText("Swarm Stationed");
                    btnDeployUnit.setEnabled(false);
                } else {
                    String selU = getSelectedUnitRaw();
                    int uEezo = selU.toLowerCase().contains("scion") ? 60 : (selU.toLowerCase().contains("drone") ? 75 : 30);
                    int uBio = selU.toLowerCase().contains("scion") ? 140 : (selU.toLowerCase().contains("drone") ? 0 : 50);

                    boolean hasEezo = state.getEezoReserves() >= uEezo;
                    boolean hasBio = state.getAccumulatedBiomass() >= uBio;

                    if (hasEezo && hasBio) {
                        if (uBio > 0) {
                            btnDeployUnit.setText(String.format("Station %s (-%d Eezo, -%d Bio)", selU, uEezo, uBio));
                        } else {
                            btnDeployUnit.setText(String.format("Station %s (-%d Eezo)", selU, uEezo));
                        }
                        btnDeployUnit.setEnabled(true);
                    } else if (!hasEezo) {
                        btnDeployUnit.setText(String.format("Station (Need %d Eezo)", uEezo));
                        btnDeployUnit.setEnabled(false);
                    } else {
                        btnDeployUnit.setText(String.format("Station (Need %d Bio)", uBio));
                        btnDeployUnit.setEnabled(false);
                    }
                }
            }

            if (selectedSys.isRelayBeamActive()) {
                btnRelay.setText("✦ Relay Active (Boosted)");
                btnRelay.setEnabled(false);
                btnRelay.setBackground(new Color(40, 70, 90));
            } else {
                if (state.getEezoReserves() >= 50) {
                    btnRelay.setText("✦ Deploy Relay Beacon (-50 Eezo)");
                    btnRelay.setEnabled(true);
                    btnRelay.setBackground(new Color(0, 110, 180));
                } else {
                    btnRelay.setText("✦ Relay (Need 50 Eezo)");
                    btnRelay.setEnabled(false);
                    btnRelay.setBackground(new Color(60, 40, 45));
                }
            }

            if (btnJumpFlagship != null) {
                boolean isFlagshipHere = (state.getFlagshipSector() == selectedSector && state.getFlagshipCluster() == selectedCluster);
                if (isFlagshipHere) {
                    btnJumpFlagship.setText("👑 Sovereign Stationed Here");
                    btnJumpFlagship.setEnabled(false);
                    btnJumpFlagship.setBackground(new Color(50, 50, 60));
                } else {
                    int jumpCost = selectedSys.isRelayBeamActive() ? 0 : 25;
                    if (state.getEezoReserves() >= jumpCost) {
                        btnJumpFlagship.setText(jumpCost > 0 ? String.format("👑 Jump Sovereign (-%d Eezo)", jumpCost) : "👑 Jump Sovereign (Relay Free)");
                        btnJumpFlagship.setEnabled(true);
                        btnJumpFlagship.setBackground(new Color(180, 130, 20));
                    } else {
                        btnJumpFlagship.setText(String.format("👑 Jump Sovereign (Need %d Eezo)", jumpCost));
                        btnJumpFlagship.setEnabled(false);
                        btnJumpFlagship.setBackground(new Color(60, 40, 45));
                    }
                }
            }
        }

        // Update Batch Harvest Button
        int ripeCount = 0;
        if (galaxy != null) {
            for (int r = 0; r < galaxy.getRowCount(); r++) {
                if (!galaxy.isSectorUnlocked(r)) continue;
                for (int c = 0; c < galaxy.getColCount(); c++) {
                    Civilization cObj = galaxy.getSystem(r, c).getCivilization();
                    if (cObj != null && cObj.isHarvestReady()) {
                        ripeCount++;
                    }
                }
            }
        }
        if (btnBatchHarvest != null) {
            btnBatchHarvest.setText(ripeCount > 0 ? String.format("🛸 Batch Reap (%d Ripe)", ripeCount) : "🛸 Batch Reap (0 Ripe)");
            btnBatchHarvest.setEnabled(ripeCount > 0);
            if (ripeCount > 0) {
                btnBatchHarvest.setBackground(new Color(220, 50, 50));
            } else {
                btnBatchHarvest.setBackground(new Color(60, 40, 45));
            }
        }

        // Update Sub-Space Scanner Button Alert State
        if (btnScanner != null) {
            if (engine.getScanner().hasPendingUndecodedSignal()) {
                btnScanner.setText("⚡ 📻 SIGNAL INTERCEPTED!");
                btnScanner.setBackground(new Color(255, 140, 0));
                btnScanner.setForeground(Color.BLACK);
            } else {
                btnScanner.setText("📻 Sub-Space Scanner");
                btnScanner.setBackground(new Color(40, 50, 65));
                btnScanner.setForeground(Color.WHITE);
            }
        }

        // Update Time Mode Button
        if (btnTimeMode != null) {
            if (state.getGrowthMode() == GrowthMode.REAL_TIME) {
                btnTimeMode.setText(realTimePaused ? "⏱ Real-Time [⏸ PAUSED]" : String.format("⏱ Real-Time [▶ %ds]", state.getRealTimeTickSeconds()));
                btnTimeMode.setBackground(realTimePaused ? new Color(130, 75, 20) : new Color(20, 120, 80));
            } else {
                btnTimeMode.setText("⏱ Mode: Turn-Based");
                btnTimeMode.setBackground(new Color(80, 50, 110));
            }
        }

        // Update Advance Button
        if (btnAdvance != null) {
            if (state.getGrowthMode() == GrowthMode.REAL_TIME) {
                btnAdvance.setText(realTimePaused ? "▶ RESUME TIME" : "⏩ Fast-Forward (+1)");
            } else {
                btnAdvance.setText("⏳ ADVANCE EPOCH (+5,000 Y)");
            }
        }

        // Update Cargo Hold Button occupancy
        if (btnCargoHold != null && state != null && state.getCargoHold() != null) {
            btnCargoHold.setText(String.format("📦 Cargo (%d/%d)",
                    state.getCargoHold().getOccupiedCount(),
                    state.getCargoHold().getCapacity()));
        }

        updateSeedingIntel();
    }

    private JPanel createActionPod(String title, Color borderColor) {
        JPanel pod = new JPanel(new BorderLayout(4, 4));
        pod.setBackground(new Color(18, 22, 34));
        pod.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTitle.setForeground(borderColor);
        pod.add(lblTitle, BorderLayout.NORTH);
        return pod;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void advanceCycleAction() {
        SoundEffects.playReaperHorn();
        GalacticPhenomenon phen = engine.advanceCycle();
        log(String.format("[EPOCH ADVANCED] Cycle Epoch %d (%s). Cosmic Phenomenon: %s.",
                state.getCycleEpoch(), state.getCosmicSeason(), phen.getTitle()));
        checkMissionProgress();
        updateDisplay();
    }

    private void setupRealTimeTimer() {
        if (realTimeTimer != null) {
            realTimeTimer.stop();
            realTimeTimer = null;
        }
        if (state != null && state.getGrowthMode() == GrowthMode.REAL_TIME) {
            int delayMs = Math.max(1, state.getRealTimeTickSeconds()) * 1000;
            realTimeTimer = new Timer(delayMs, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (state != null && state.getGrowthMode() == GrowthMode.REAL_TIME && !realTimePaused) {
                        advanceCycleAction();
                    }
                }
            });
            realTimeTimer.start();
        }
    }

    private void toggleTimeModeAction() {
        String[] options = {
            "1. Turn-Based Mode (Advance epochs manually with button)",
            "2. Real-Time Mode (Fast: 3 seconds per epoch)",
            "3. Real-Time Mode (Normal: 6 seconds per epoch)",
            "4. Real-Time Mode (Relaxed: 10 seconds per epoch)",
            realTimePaused ? "5. Resume Real-Time Simulation [▶]" : "5. Pause Real-Time Simulation [⏸]"
        };

        String currentStatus = state.getGrowthMode().getDisplayName() +
                (state.getGrowthMode() == GrowthMode.REAL_TIME ? (realTimePaused ? " [PAUSED]" : " [RUNNING (" + state.getRealTimeTickSeconds() + "s)]") : "");

        String choice = (String) JOptionPane.showInputDialog(this,
                "Current Simulation Pace: " + currentStatus + "\n\nSelect Time Flow & Growth Mode Configuration:",
                "Time Flow & Growth Controls",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice != null) {
            if (choice.startsWith("1.")) {
                state.setGrowthMode(GrowthMode.TURN_BASED);
                realTimePaused = false;
                log("TIME FLOW: Switched to Turn-Based Mode (manual epoch control).");
            } else if (choice.startsWith("2.")) {
                state.setGrowthMode(GrowthMode.REAL_TIME);
                state.setRealTimeTickSeconds(3);
                realTimePaused = false;
                log("TIME FLOW: Real-Time Mode configured to FAST (3s per epoch).");
            } else if (choice.startsWith("3.")) {
                state.setGrowthMode(GrowthMode.REAL_TIME);
                state.setRealTimeTickSeconds(6);
                realTimePaused = false;
                log("TIME FLOW: Real-Time Mode configured to NORMAL (6s per epoch).");
            } else if (choice.startsWith("4.")) {
                state.setGrowthMode(GrowthMode.REAL_TIME);
                state.setRealTimeTickSeconds(10);
                realTimePaused = false;
                log("TIME FLOW: Real-Time Mode configured to RELAXED (10s per epoch).");
            } else if (choice.startsWith("5.")) {
                realTimePaused = !realTimePaused;
                log("TIME FLOW: Real-Time simulation " + (realTimePaused ? "PAUSED." : "RESUMED."));
            }
            setupRealTimeTimer();
            updateDisplay();
        }
    }

    private void batchHarvestAllRipeWorlds() {
        GalacticSector map = state.getGalaxyMap();
        List<StarSystem> ripeSystems = new ArrayList<StarSystem>();
        for (int r = 0; r < map.getRowCount(); r++) {
            if (!map.isSectorUnlocked(r)) continue;
            for (int c = 0; c < map.getColCount(); c++) {
                StarSystem sys = map.getSystem(r, c);
                if (sys.getCivilization() != null && sys.getCivilization().isHarvestReady()) {
                    ripeSystems.add(sys);
                }
            }
        }

        if (ripeSystems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No ripe civilizations found across the galactic sector.\nAllow crops to reach Tier 3 Apex Zenith (Ripe) before harvesting.",
                    "No Ripe Worlds", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int totalBio = 0;
        int totalEezo = 0;
        int barrierDeflectedCount = 0;
        int cargoFullCount = 0;
        StringBuilder sb = new StringBuilder("=== THE GREAT BATCH ASCENSION HARVEST ===\n\n");
        sb.append(String.format("Reaping %d ripe worlds simultaneously across the sector:\n\n", ripeSystems.size()));

        for (StarSystem sys : ripeSystems) {
            try {
                HarvestYield y = engine.harvestSystem(sys.getSector(), sys.getCluster());
                totalBio += y.getGeneticBiomass();
                totalEezo += y.getDarkEnergyYield();
                sb.append(String.format("  • %-12s [%d,%d]: +%d Eezo, +%d Biomass\n",
                        y.getOriginSpecies(), sys.getSector(), sys.getCluster(),
                        y.getDarkEnergyYield(), y.getGeneticBiomass()));
            } catch (CivilizationBarrierException cbe) {
                barrierDeflectedCount++;
                sb.append(String.format("  [!] %-12s [%d,%d]: DEFLECTED by Planetary Kinetic Barrier!\n",
                        sys.getCivilization().getSpeciesName(), sys.getSector(), sys.getCluster()));
            } catch (CargoHoldFullException chfe) {
                cargoFullCount++;
                sb.append(String.format("  [!] %-12s [%d,%d]: DEFLECTED - Flagship Cargo Hold Full!\n",
                        sys.getCivilization().getSpeciesName(), sys.getSector(), sys.getCluster()));
            } catch (Exception ex) {
                // Safeguarded by isHarvestReady()
            }
        }

        if (barrierDeflectedCount > 0) {
            sb.append(String.format("\n[DEFENSE NOTICE] %d world(s) deflected ascension via planetary kinetic barriers.\nStation Scions/Husks, jump Sovereign, or research Cyclonic Shields to breach.\n", barrierDeflectedCount));
        }
        if (cargoFullCount > 0) {
            sb.append(String.format("\n[CARGO NOTICE] %d world(s) could not be harvested because all %d flagship cargo pods are full.\nResearch Genetic Synthesis Vats in Tech Tree or purge pods to free space.\n",
                    cargoFullCount, state.getCargoHold().getCapacity()));
        }

        SoundEffects.playHarvestPulse();
        sb.append("\n----------------------------------------------------\n");
        sb.append(String.format("TOTAL ASCENSION RETURN: +%d Eezo, +%d Biomass\n", totalEezo, totalBio));
        sb.append("Civilization genetic material successfully secured in cargo pods.");

        int successfulReaped = ripeSystems.size() - barrierDeflectedCount - cargoFullCount;
        log(String.format("BATCH HARVEST: Reaped %d worlds (+%d Eezo, +%d Bio).",
                successfulReaped, totalEezo, totalBio));
        checkMissionProgress();
        updateDisplay();

        JOptionPane.showMessageDialog(this, sb.toString(),
                "Batch Ascension (" + successfulReaped + " Reaped, " + barrierDeflectedCount + " Deflected, " + cargoFullCount + " Cargo Full)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCropAlmanacDialog() {
        StringBuilder sb = new StringBuilder("=== THE GALACTIC AGRONOMY & CROP ALMANAC ===\n");
        sb.append("\"The cycle of existence is organic cultivation: seed the soil, nurture the harvest, reap the ascension.\"\n\n");

        sb.append("1. THE 15 ORGANIC CROP SPECIES (BOTANICAL & AGRONOMIC CLASSIFICATION):\n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s | %-17s | %-8s | %-10s | %-10s | %-14s | %s\n",
                "SPECIES", "AGRONOMY CLASS", "SEED COST", "IDEAL BIOME", "MATURATION", "BASE HARVEST", "SPECIAL BOTANICAL TRAIT"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        String[][] cropData = {
            {"Humanity",    "Adaptive Hybrid",      "50 Eezo",  "Garden",   "3 Epochs", "120 Bio / 150 Eezo",  "Unpredictable genetic diversity"},
            {"Vorcha",      "Hardy Wildgrass",      "40 Eezo",  "Arid",     "3 Epochs", "100 Bio / 120 Eezo",  "Immune to solar flares & cosmic storms"},
            {"Salarian",    "Fast-Growth Sprout",   "75 Eezo",  "Garden",   "2-3 Epochs","110 Bio / 180 Eezo", "Hyper-accelerated population expansion"},
            {"Volus",       "Perennial Cash Crop",  "80 Eezo",  "Methane",  "3 Epochs", "115 Bio / 190 Eezo",  "Generates passive Eezo dividends every cycle"},
            {"Turian",      "Fortified Grain",      "90 Eezo",  "Arid",     "3 Epochs", "140 Bio / 210 Eezo",  "Metallic carapace; builds planetary barriers"},
            {"Quarian",     "Hydroponic Vine",      "85 Eezo",  "Arid",     "3 Epochs", "125 Bio / 200 Eezo",  "Passive +0.5 Mass Relay irrigation boost"},
            {"Batarian",    "Mineral-Rich Root",    "60 Eezo",  "Volcanic", "3 Epochs", "130 Bio / 160 Eezo",  "Deep soil tilling & heavy mineral extraction"},
            {"Hanar",       "Aquatic Kelp",         "100 Eezo", "Garden",   "3 Epochs", "150 Bio / 230 Eezo",  "Relic attunement auto-links Mass Relays"},
            {"Drell",       "Arid Desert Succulent","110 Eezo", "Arid",     "3 Epochs", "160 Bio / 250 Eezo",  "Dense synaptic memory dark energy yield"},
            {"Elcor",       "Ancient Ironbark",     "115 Eezo", "Methane",  "3 Epochs", "175 Bio / 260 Eezo",  "Ultra-dense mass yields capital hull armor"},
            {"Krogan",      "Colossus Tuber",       "120 Eezo", "Arid",     "3 Epochs", "220 Bio / 270 Eezo",  "Immune to pestilence & organic rebellions"},
            {"Rachni",      "Quantum Spore Colony", "130 Eezo", "Volcanic", "3 Epochs", "200 Bio / 290 Eezo",  "Sub-space song acts as permanent FTL link"},
            {"Asari",       "Biotic Regal Orchid",  "150 Eezo", "Garden",   "3 Epochs", "250 Bio / 350 Eezo",  "Highest Dark Energy (Eezo) ascension yield"},
            {"Prothean",    "Precursor Heirloom",   "200 Eezo", "Garden",   "3 Epochs", "300 Bio / 450 Eezo",  "Triple dark energy yield upon Ascension"},
            {"Yahg",        "Apex Predator Vine",   "220 Eezo", "Volcanic", "3 Epochs", "340 Bio / 500 Eezo",  "Massive genetic biomass bulk return"}
        };

        for (String[] c : cropData) {
            boolean unlocked = missionManager.isSpeciesUnlocked(c[0], engine);
            String name = unlocked ? c[0] : "[Locked]";
            String cls = unlocked ? c[1] : "Ancient Unresearched";
            String cost = unlocked ? c[2] : "??? Eezo";
            String biome = unlocked ? c[3] : "???";
            String mat = unlocked ? c[4] : "???";
            String yld = unlocked ? c[5] : "???";
            String trait = unlocked ? c[6] : "Advance campaign missions to discover";

            sb.append(String.format("%-10s | %-17s | %-8s | %-10s | %-10s | %-14s | %s\n",
                    name, cls, cost, biome, mat, yld, trait));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n\n");
        sb.append("2. PLANETARY BIOMES & CLIMATE AFFINITY MATRIX:\n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append("Each planetary world has an atmospheric biome that dictates species growth and harvest yields:\n");
        sb.append("  • Matching (Ideal) Biome: +50% Evolutionary Growth Speed & +25% Ascension Biomass/Eezo Yield.\n");
        sb.append("  • Hostile Biome:          -50% Growth Speed & -25% Harvest Yield.\n");
        sb.append("  • Moderate Biome:         Standard (1.0x) Evolutionary Growth & Yield.\n");
        sb.append("  • Barren / Frozen Biome:  Uninhabitable (-75% Growth). Must be terraformed with an Atmospheric\n");
        sb.append("                            Converter (Cost: 75 Eezo) before seeding organic life.\n\n");
        sb.append("BIOME ARCHETYPES & SPECIES COMPATIBILITY (ALL 15 SPECIES COVERED):\n");
        sb.append("  ✦ Garden / Oceanic  : Ideal for Humanity, Asari, Salarian, Prothean, Hanar. Hostile to Vorcha, Krogan, Drell, Batarian, Yahg, Volus, Elcor.\n");
        sb.append("  ✦ Arid / Scorched   : Ideal for Turian, Krogan, Quarian, Vorcha, Drell. Hostile to Hanar, Salarian.\n");
        sb.append("  ✦ Dense / Methane   : Ideal for Volus, Elcor. Hostile to Humanity, Asari, Turian, Rachni.\n");
        sb.append("  ✦ Volcanic / Basalt : Ideal for Batarians, Yahg, Rachni. Hostile to Salarians, Quarians, Prothean, Hanar.\n");
        sb.append("  ✦ Barren / Frozen   : Dead rock (-75% growth). Hostile to ALL 15 species until terraformed!\n\n");
        sb.append("3. AGRICULTURAL TOOLS & AUTOMATION MACHINERY:\n");
        sb.append("  • Mass Relay (Irrigation Grid):\n");
        sb.append("    Connects planetary soil to the FTL corridor, ensuring +50% dark energy transit bonus.\n\n");
        sb.append("  • Collector Drone (Automated Sprinkler / Growth Fertilizer):\n");
        sb.append("    Halves incubation time (2x evolution speed) and extracts early genetic telemetry samples.\n\n");
        sb.append("  • Husk Swarm (Pest Defense / Scarecrow):\n");
        sb.append("    Cybernetic ground troops pacifying invasive planetary rebellions and crop blights.\n\n");
        sb.append("  • Scion Behemoth (Harvester Combine):\n");
        sb.append("    Stationed heavy biotic bio-tank amplifying planetary harvest yield by +50% on Ascension.\n\n");
        sb.append("4. PLANETARY THREATS & COUNTER-MEASURES:\n");
        sb.append("  • Planetary Kinetic Defense Barriers:\n");
        sb.append("    Erected by martial species (Turian, Batarian) at Tier 2+, or formed through Creeping Colonization\n");
        sb.append("    mutual defense pacts. Deflects unassisted harvests. Breached by stationing Scion Behemoths,\n");
        sb.append("    parking Sovereign Flagship in orbit, or researching Cyclonic Kinetic Shields.\n\n");
        sb.append("  • Creeping Multi-Planet Colonization:\n");
        sb.append("    Spacefaring civilizations (Tier 2+) left unharvested for 2 epochs launch colony expeditions to\n");
        sb.append("    neighboring worlds, expanding their defensive perimeter.\n\n");
        sb.append("  • Rogue Synthetic Heresy (AI Rebellions):\n");
        sb.append("    Apex civilizations (Tier 3) left unharvested for 3+ epochs invent rogue AI, triggering civil war\n");
        sb.append("    and halving their population. Suppressed with Husk Swarms or Indoctrination Emitters.\n\n");
        sb.append("  • The Crucible Threat Clock (Act IV Defeat Clock):\n");
        sb.append("    Surviving Allied resistance pools research from unattended spacefaring worlds (+3% extra per world).\n");
        sb.append("    Reaching 100% results in Critical Campaign Defeat. Delayed via Citadel Arms Lockdown (-25%)\n");
        sb.append("    or Crucible Sabotage Protocols (-25% construction speed).\n\n");
        sb.append("5. THE 4 COSMIC SEASONS:\n");
        sb.append("  • Cosmic Spring (Epochs 1-2): Seeding & Irrigation phase under calm stellar conditions.\n");
        sb.append("  • Cosmic Summer (Epochs 3-4): Radiation & flare surges accelerating cellular expansion.\n");
        sb.append("  • Cosmic Autumn (Epochs 5-6): The Great Reaping. Prime season for batch Ascension harvests.\n");
        sb.append("  • Cosmic Winter (Epochs 7+): Threat phase. Organic resistance races toward the Crucible.\n\n");
        sb.append("6. TIME FLOW & MULTI-SLOT CHECKPOINTS:\n");
        sb.append("  • Switch between Turn-Based and Real-Time at any time using [⏱ Mode].\n");
        sb.append("  • Save your campaign across 5 distinct Save Slots in addition to the automatic checkpoint slot,\n");
        sb.append("    allowing you to load back to any point if you make a mistake or face unexpected resistance!\n");

        showLargeStyledDialog("Galactic Agronomy Almanac", "🌱 GALACTIC AGRONOMY & ORGANIC CROP ALMANAC", sb.toString(), 1020, 750);
    }

    private void showBioBankGenomeDialog() {
        final JDialog dlg = new JDialog(this, "Reaper Dark Space Bio-Banks // 15-Genome Archive", true);
        dlg.setSize(980, 760);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBackground(new Color(12, 16, 26));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout(6, 6));
        header.setBackground(new Color(18, 24, 38));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitle = new JLabel("🧬 REAPER DARK SPACE BIO-BANKS & GENOME SYNTHESIS", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0, 230, 255));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblTreasury = new JLabel(String.format("Fleet Treasury: %d Eezo  |  %d Biomass",
                state.getEezoReserves(), state.getAccumulatedBiomass()), SwingConstants.RIGHT);
        lblTreasury.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTreasury.setForeground(new Color(255, 215, 0));
        header.add(lblTreasury, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 8, 8));
        gridPanel.setOpaque(false);

        for (final String sp : ALL_SPECIES) {
            final Civilization c = engine.createCivilization(sp);
            final boolean sequenced = engine.isGenomeSequenced(sp);

            JPanel card = new JPanel(new BorderLayout(8, 4));
            card.setBackground(sequenced ? new Color(18, 32, 28) : new Color(18, 22, 34));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(sequenced ? new Color(0, 200, 100) : new Color(50, 60, 80), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);

            JLabel nameLbl = new JLabel(String.format("%s (%d Eezo)", sp, c.getSeedingCost()));
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            nameLbl.setForeground(sequenced ? new Color(0, 255, 140) : Color.WHITE);

            JLabel traitLbl = new JLabel("<html><body style='width: 175px;'>Trait: " + c.getRacialTrait() + "</body></html>");
            traitLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            traitLbl.setForeground(new Color(170, 190, 210));

            info.add(nameLbl);
            info.add(Box.createVerticalStrut(2));
            info.add(traitLbl);

            card.add(info, BorderLayout.CENTER);

            JButton btnAction = new JButton();
            btnAction.setFont(new Font("SansSerif", Font.BOLD, 10));

            boolean isStorySpecies = missionManager.isSpeciesUnlocked(sp);
            int reqE = engine.getGenomeEezoCost(sp);
            int reqB = engine.getGenomeBiomassCost(sp);

            if (sequenced || isStorySpecies) {
                btnAction.setText(isStorySpecies ? "✓ STORY UNLOCKED" : "✓ RESEARCHED");
                btnAction.setEnabled(false);
                btnAction.setBackground(new Color(20, 80, 45));
                btnAction.setForeground(new Color(180, 255, 200));
            } else {
                boolean afford = state.getEezoReserves() >= reqE && state.getAccumulatedBiomass() >= reqB;
                btnAction.setText(String.format("RESEARCH (-%d E, -%d B)", reqE, reqB));
                btnAction.setEnabled(afford);
                btnAction.setBackground(afford ? new Color(130, 50, 170) : new Color(50, 35, 45));
                btnAction.setForeground(Color.WHITE);
                btnAction.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            engine.sequenceGenome(sp);
                            SoundEffects.playRelayChime();
                            log(String.format("GENOME SYNTHESIZED: %s genetic archive unlocked from dark space bio-banks (-%d Eezo, -%d Bio).", sp, engine.getGenomeEezoCost(sp), engine.getGenomeBiomassCost(sp)));
                            refreshDropdowns();
                            updateDisplay();
                            dlg.dispose();
                            showBioBankGenomeDialog();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Synthesis Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
            }

            card.add(btnAction, BorderLayout.EAST);
            gridPanel.add(card);
        }

        content.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton btnClose = new JButton("Close Bio-Banks Vault");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnClose.setBackground(new Color(40, 50, 70));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        bottom.add(btnClose);
        content.add(bottom, BorderLayout.SOUTH);

        dlg.setContentPane(content);
        dlg.setVisible(true);
    }
}
