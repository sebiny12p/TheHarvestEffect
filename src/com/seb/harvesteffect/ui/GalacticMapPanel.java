package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.engine.CitadelNexus;
import com.seb.harvesteffect.engine.GalacticState;
import com.seb.harvesteffect.engine.MissionManager;
import com.seb.harvesteffect.engine.ReaperEngine;
import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.ClimateType;
import com.seb.harvesteffect.model.entity.GalacticSector;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.unit.CollectorDrone;
import com.seb.harvesteffect.model.unit.HuskSwarm;
import com.seb.harvesteffect.model.unit.ScionBehemoth;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.MultipleGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Random;

/**
 * Tactical Galactic Star Chart for The Harvest Effect.
 * Replaces the rigid rectangular grid with an authentic Mass Effect galaxy map
 * centered on the iconic Citadel Station in the Serpent Nebula.
 *
 * Features:
 * - The Citadel Nexus at the exact galactic core (Presidium ring + 5 radiating Ward arms)
 * - 4 Galactic Sectors arranged as organic quadrants / spiral arms
 * - 4 Primary Mass Relays with pulsing FTL Conduit Beams connecting to the Citadel
 * - 24 Planetary Star Systems with realistic 3D sphere rendering, biomes, and orbital rings
 * - Interactive hover tooltips, targeting reticles, and real-time animation pulses
 */
public class GalacticMapPanel extends JPanel implements Serializable {

    public interface MapInteractionListener {
        void onSystemSelected(int sector, int cluster);
        void onCitadelClicked();
        void onPrimaryRelayClicked(int sector);
    }

    private GalacticState state;
    private ReaperEngine engine;
    private MissionManager missionManager;
    private MapInteractionListener listener;

    private int selectedSector = 0;
    private int selectedCluster = 0;

    private int hoveredSector = -1;
    private int hoveredCluster = -1;
    private boolean isCitadelHovered = false;
    private int hoveredRelaySector = -1;
    private Point mousePos = null;

    private float pulsePhase = 0.0f;
    private transient Timer animTimer;

    // Background starfield coordinates (deterministic)
    private static final int STAR_COUNT = 160;
    private final float[] starNormX = new float[STAR_COUNT];
    private final float[] starNormY = new float[STAR_COUNT];
    private final float[] starSizes = new float[STAR_COUNT];
    private final int[] starBrightness = new int[STAR_COUNT];

    // Primary Relay Normalized Positions [Sector 0..3]
    private static final double[][] RELAY_NORM_COORDS = {
        { -0.30, -0.24 }, // Sector 0 (Core Systems, NW)
        {  0.30, -0.24 }, // Sector 1 (Attican Traverse, NE)
        { -0.30,  0.24 }, // Sector 2 (Terminus Systems, SW)
        {  0.30,  0.24 }  // Sector 3 (Shadow Rim, SE)
    };

    // 24 Planet Normalized Galaxy Coordinates [Sector 0..3][Cluster 0..5]
    private static final double[][][] PLANET_NORM_COORDS = {
        // Sector 0: The Core Systems (NW Quadrant)
        {
            { -0.62, -0.62 }, // 0: Sol
            { -0.32, -0.74 }, // 1: Thessian Veil
            { -0.84, -0.46 }, // 2: Palaven Expanse
            { -0.54, -0.32 }, // 3: Sur'Kesh Node
            { -0.18, -0.54 }, // 4: Dekuuna Spire
            { -0.78, -0.12 }  // 5: Elysium Void
        },
        // Sector 1: The Attican Traverse & Krogan DMZ (NE Quadrant)
        {
            {  0.32, -0.74 }, // 0: Tuchanka Core
            {  0.62, -0.62 }, // 1: Khar'shan Reach
            {  0.84, -0.46 }, // 2: Rakhana Verge
            {  0.54, -0.32 }, // 3: Kahje Deep
            {  0.18, -0.54 }, // 4: Korlus Basin
            {  0.78, -0.12 }  // 5: Feros Ridge
        },
        // Sector 2: The Terminus Systems & Perseus Veil (SW Quadrant)
        {
            { -0.78,  0.12 }, // 0: Rannoch Expanse
            { -0.54,  0.32 }, // 1: Irune Basin
            { -0.84,  0.46 }, // 2: Heshtok Ridge
            { -0.62,  0.62 }, // 3: Omega Void
            { -0.32,  0.74 }, // 4: Noveria Shelf
            { -0.18,  0.54 }  // 5: Horizon Verge
        },
        // Sector 3: The Shadow Rim & Precursor Verge (SE Quadrant)
        {
            {  0.78,  0.12 }, // 0: Suen Void
            {  0.54,  0.32 }, // 1: Eden Prime
            {  0.84,  0.46 }, // 2: Parnack Shelf
            {  0.62,  0.62 }, // 3: Sanctum Deep
            {  0.32,  0.74 }, // 4: Ilos Crypt
            {  0.18,  0.54 }  // 5: Virmire Reach
        }
    };

    public GalacticMapPanel(GalacticState state, ReaperEngine engine, MissionManager missionManager) {
        this.state = state;
        this.engine = engine;
        this.missionManager = missionManager;

        setBackground(new Color(6, 8, 14));
        setDoubleBuffered(true);

        // Generate deterministic starfield
        Random rand = new Random(2186); // Mass Effect 3 lore seed
        for (int i = 0; i < STAR_COUNT; i++) {
            starNormX[i] = (rand.nextFloat() * 2.0f) - 1.0f;
            starNormY[i] = (rand.nextFloat() * 2.0f) - 1.0f;
            starSizes[i] = 1.0f + (rand.nextFloat() * 2.2f);
            starBrightness[i] = 60 + rand.nextInt(180);
        }

        initMouseListeners();
        startAnimationTimer();
    }

    private void startAnimationTimer() {
        animTimer = new Timer(40, e -> {
            pulsePhase = (pulsePhase + 0.02f) % 1.0f;
            repaint();
        });
        animTimer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (animTimer != null && !animTimer.isRunning()) {
            animTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }
    }

    public void setMapInteractionListener(MapInteractionListener listener) {
        this.listener = listener;
    }

    public void setGameState(GalacticState state, ReaperEngine engine, MissionManager missionManager) {
        this.state = state;
        this.engine = engine;
        this.missionManager = missionManager;
        repaint();
    }

    public void setSelectedSystem(int sector, int cluster) {
        this.selectedSector = sector;
        this.selectedCluster = cluster;
        repaint();
    }

    public void updateMap() {
        repaint();
    }

    private void initMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                Point p = e.getPoint();

                // 1. Check Citadel Click
                if (p.distance(cx, cy) <= 34) {
                    if (listener != null) {
                        listener.onCitadelClicked();
                    }
                    return;
                }

                // 2. Check Primary Relay Clicks
                double scaleX = (getWidth() / 2.0) * 0.90;
                double scaleY = (getHeight() / 2.0) * 0.85;

                for (int s = 0; s < 4; s++) {
                    int rx = cx + (int) (RELAY_NORM_COORDS[s][0] * scaleX);
                    int ry = cy + (int) (RELAY_NORM_COORDS[s][1] * scaleY);
                    if (p.distance(rx, ry) <= 22) {
                        if (listener != null) {
                            listener.onPrimaryRelayClicked(s);
                        }
                        return;
                    }
                }

                // 3. Check Planet Clicks
                for (int s = 0; s < 4; s++) {
                    for (int c = 0; c < 6; c++) {
                        int px = cx + (int) (PLANET_NORM_COORDS[s][c][0] * scaleX);
                        int py = cy + (int) (PLANET_NORM_COORDS[s][c][1] * scaleY);
                        if (p.distance(px, py) <= 26) {
                            selectedSector = s;
                            selectedCluster = c;
                            if (listener != null) {
                                listener.onSystemSelected(s, c);
                            }
                            repaint();
                            return;
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredSector = -1;
                hoveredCluster = -1;
                isCitadelHovered = false;
                hoveredRelaySector = -1;
                mousePos = null;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                int oldHoverSec = hoveredSector;
                int oldHoverClu = hoveredCluster;
                boolean oldCitadel = isCitadelHovered;
                int oldRelay = hoveredRelaySector;

                hoveredSector = -1;
                hoveredCluster = -1;
                isCitadelHovered = false;
                hoveredRelaySector = -1;

                boolean foundInteractive = false;

                // Citadel Check
                if (mousePos.distance(cx, cy) <= 34) {
                    isCitadelHovered = true;
                    foundInteractive = true;
                } else {
                    double scaleX = (getWidth() / 2.0) * 0.90;
                    double scaleY = (getHeight() / 2.0) * 0.85;

                    // Primary Relays Check
                    for (int s = 0; s < 4; s++) {
                        int rx = cx + (int) (RELAY_NORM_COORDS[s][0] * scaleX);
                        int ry = cy + (int) (RELAY_NORM_COORDS[s][1] * scaleY);
                        if (mousePos.distance(rx, ry) <= 22) {
                            hoveredRelaySector = s;
                            foundInteractive = true;
                            break;
                        }
                    }

                    // Planets Check
                    if (!foundInteractive) {
                        for (int s = 0; s < 4; s++) {
                            for (int c = 0; c < 6; c++) {
                                int px = cx + (int) (PLANET_NORM_COORDS[s][c][0] * scaleX);
                                int py = cy + (int) (PLANET_NORM_COORDS[s][c][1] * scaleY);
                                if (mousePos.distance(px, py) <= 26) {
                                    hoveredSector = s;
                                    hoveredCluster = c;
                                    foundInteractive = true;
                                    break;
                                }
                            }
                            if (foundInteractive) break;
                        }
                    }
                }

                setCursor(foundInteractive ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

                if (oldHoverSec != hoveredSector || oldHoverClu != hoveredCluster
                        || oldCitadel != isCitadelHovered || oldRelay != hoveredRelaySector) {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        double scaleX = (w / 2.0) * 0.90;
        double scaleY = (h / 2.0) * 0.85;

        GalacticSector galaxy = state.getGalaxyMap();

        // 1. Draw Deep Space Background & Starfield
        drawDeepSpaceAndStars(g2, w, h);

        // 2. Draw Galactic Core Glow & Sector Spiral Arms
        drawGalacticCoreAndSpiralArms(g2, cx, cy, w, h);

        // 3. Draw Tactical Range Rings & Sector Quadrants
        drawTacticalGridAndBoundaries(g2, cx, cy, w, h);

        // 4. Draw Mass Relay Conduit Beams (Citadel -> Primary Relays)
        drawPrimaryConduitBeams(g2, cx, cy, scaleX, scaleY, galaxy);

        // 5. Draw Sub-Relay Corridors (Primary Relays -> Star Systems)
        drawSubRelayCorridors(g2, cx, cy, scaleX, scaleY, galaxy);

        // 6. Draw 4 Primary Mass Relays
        drawPrimaryMassRelays(g2, cx, cy, scaleX, scaleY, galaxy);

        // 7. Draw 24 Planetary Star Systems
        drawPlanetaryStarSystems(g2, cx, cy, scaleX, scaleY, galaxy);

        // 8. Draw The Citadel Station at Galactic Core
        drawCitadelStation(g2, cx, cy);

        // 9. Draw Floating Tactical HUD Overlay on Hover
        drawTacticalHUD(g2, w, h, galaxy);

        // 10. Draw Map Legend in Corner
        drawMapLegend(g2, w, h);

        g2.dispose();
    }

    // =========================================================================
    // LAYER 1: Deep Space & Stars
    // =========================================================================
    private void drawDeepSpaceAndStars(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(6, 8, 14));
        g2.fillRect(0, 0, w, h);

        int halfW = w / 2;
        int halfH = h / 2;

        for (int i = 0; i < STAR_COUNT; i++) {
            int sx = halfW + (int) (starNormX[i] * halfW);
            int sy = halfH + (int) (starNormY[i] * halfH);
            float sz = starSizes[i];
            int alpha = starBrightness[i];

            // Subtle twinkling
            if (i % 5 == 0) {
                alpha = Math.max(40, Math.min(255, (int) (alpha * (0.8 + 0.4 * Math.sin(pulsePhase * 2 * Math.PI + i)))));
            }

            g2.setColor(new Color(190, 220, 255, alpha));
            g2.fillOval(sx, sy, (int) sz, (int) sz);
        }
    }

    // =========================================================================
    // LAYER 2: Galactic Core & Spiral Arms
    // =========================================================================
    private void drawGalacticCoreAndSpiralArms(Graphics2D g2, int cx, int cy, int w, int h) {
        int coreRadius = (int) (Math.min(w, h) * 0.45);

        // Core nebula gradient
        RadialGradientPaint corePaint = new RadialGradientPaint(
                cx, cy, coreRadius,
                new float[]{ 0.0f, 0.4f, 0.75f, 1.0f },
                new Color[]{
                        new Color(80, 50, 130, 90),
                        new Color(30, 45, 90, 60),
                        new Color(15, 25, 45, 30),
                        new Color(6, 8, 14, 0)
                }
        );
        g2.setPaint(corePaint);
        g2.fillOval(cx - coreRadius, cy - coreRadius, coreRadius * 2, coreRadius * 2);

        // Quadrant Sector Subtle Glows
        int qw = w / 2;
        int qh = h / 2;

        // Sector 0: Core Systems (NW - Cyan)
        g2.setColor(new Color(0, 229, 255, 12));
        g2.fillArc(0, 0, w, h, 90, 90);

        // Sector 1: Attican Traverse (NE - Amber)
        g2.setColor(new Color(255, 167, 38, 12));
        g2.fillArc(0, 0, w, h, 0, 90);

        // Sector 2: Terminus Systems (SW - Purple)
        g2.setColor(new Color(186, 104, 200, 12));
        g2.fillArc(0, 0, w, h, 180, 90);

        // Sector 3: Shadow Rim (SE - Crimson)
        g2.setColor(new Color(255, 82, 82, 12));
        g2.fillArc(0, 0, w, h, 270, 90);
    }

    // =========================================================================
    // LAYER 3: Tactical Range Rings & Sector Quadrants
    // =========================================================================
    private void drawTacticalGridAndBoundaries(Graphics2D g2, int cx, int cy, int w, int h) {
        g2.setColor(new Color(40, 60, 90, 45));
        float[] dash = { 4.0f, 6.0f };
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

        int[] radii = { 80, 180, 290, 420 };
        for (int r : radii) {
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        }

        // Quadrant divider axes
        g2.setColor(new Color(30, 45, 70, 70));
        g2.drawLine(cx, 10, cx, h - 10);
        g2.drawLine(10, cy, w - 10, cy);

        // Sector Watermark Labels in Corners
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));

        // NW: Sector 0
        g2.setColor(new Color(0, 200, 255, 160));
        g2.drawString("SECTOR 0: THE CORE SYSTEMS", 14, 20);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(120, 180, 220, 110));
        g2.drawString("Local Cluster & Serpent Nebula (Conduit Online)", 14, 32);

        // NE: Sector 1
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(new Color(255, 170, 40, 160));
        String s1 = "SECTOR 1: THE ATTICAN TRAVERSE";
        int s1w = g2.getFontMetrics().stringWidth(s1);
        g2.drawString(s1, w - s1w - 14, 20);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(230, 180, 120, 110));
        String s1Sub = "Krogan DMZ & Traverse Reach (Relay Alpha)";
        g2.drawString(s1Sub, w - g2.getFontMetrics().stringWidth(s1Sub) - 14, 32);

        // SW: Sector 2
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(new Color(190, 110, 255, 160));
        g2.drawString("SECTOR 2: THE TERMINUS SYSTEMS", 14, h - 28);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(180, 150, 220, 110));
        g2.drawString("Perseus Veil & Terminus Rim (Relay Omega)", 14, h - 16);

        // SE: Sector 3
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(new Color(255, 80, 90, 160));
        String s3 = "SECTOR 3: THE SHADOW RIM";
        int s3w = g2.getFontMetrics().stringWidth(s3);
        g2.drawString(s3, w - s3w - 14, h - 28);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(220, 140, 150, 110));
        String s3Sub = "Precursor Verge & Beyond (Relay Gamma)";
        g2.drawString(s3Sub, w - g2.getFontMetrics().stringWidth(s3Sub) - 14, h - 16);
    }

    // =========================================================================
    // LAYER 4: Mass Relay FTL Conduit Beams (Citadel -> Primary Relays)
    // =========================================================================
    private void drawPrimaryConduitBeams(Graphics2D g2, int cx, int cy, double scaleX, double scaleY, GalacticSector galaxy) {
        for (int s = 0; s < 4; s++) {
            int rx = cx + (int) (RELAY_NORM_COORDS[s][0] * scaleX);
            int ry = cy + (int) (RELAY_NORM_COORDS[s][1] * scaleY);
            boolean unlocked = galaxy.isSectorUnlocked(s);

            if (unlocked) {
                // Outer glowing beam
                g2.setColor(new Color(0, 180, 255, 70));
                g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx, cy, rx, ry);

                // Core sharp beam
                g2.setColor(new Color(0, 240, 255, 200));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawLine(cx, cy, rx, ry);

                // Animated light pulse packet
                double pulsePos = (pulsePhase + (s * 0.25)) % 1.0;
                int pulseX = (int) (cx + (rx - cx) * pulsePos);
                int pulseY = (int) (cy + (ry - cy) * pulsePos);

                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillOval(pulseX - 3, pulseY - 3, 7, 7);
                g2.setColor(new Color(0, 230, 255, 140));
                g2.fillOval(pulseX - 6, pulseY - 6, 13, 13);
            } else {
                // Severed / Offline Conduit Line
                g2.setColor(new Color(160, 40, 50, 75));
                float[] dash = { 4.0f, 6.0f };
                g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.drawLine(cx, cy, rx, ry);
            }
        }
    }

    // =========================================================================
    // LAYER 5: Sub-Relay Corridors (Primary Relays -> Star Systems)
    // =========================================================================
    private void drawSubRelayCorridors(Graphics2D g2, int cx, int cy, double scaleX, double scaleY, GalacticSector galaxy) {
        for (int s = 0; s < 4; s++) {
            int rx = cx + (int) (RELAY_NORM_COORDS[s][0] * scaleX);
            int ry = cy + (int) (RELAY_NORM_COORDS[s][1] * scaleY);
            boolean sectorUnlocked = galaxy.isSectorUnlocked(s);

            for (int c = 0; c < 6; c++) {
                int px = cx + (int) (PLANET_NORM_COORDS[s][c][0] * scaleX);
                int py = cy + (int) (PLANET_NORM_COORDS[s][c][1] * scaleY);
                StarSystem sys = galaxy.getSystem(s, c);

                if (sectorUnlocked && sys.isRelayBeamActive()) {
                    // Active Mass Relay Beam connecting Star System to Primary Relay
                    g2.setColor(new Color(0, 200, 255, 80));
                    g2.setStroke(new BasicStroke(3.0f));
                    g2.drawLine(rx, ry, px, py);

                    g2.setColor(new Color(140, 240, 255, 210));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawLine(rx, ry, px, py);

                    // Tiny animated data packet
                    double pPos = (pulsePhase + (c * 0.15)) % 1.0;
                    int packetX = (int) (rx + (px - rx) * pPos);
                    int packetY = (int) (ry + (py - ry) * pPos);
                    g2.setColor(new Color(0, 240, 255, 180));
                    g2.fillOval(packetX - 2, packetY - 2, 4, 4);
                } else if (sectorUnlocked) {
                    // Faint sublight transit lane
                    g2.setColor(new Color(40, 60, 90, 40));
                    float[] dash = { 2.0f, 4.0f };
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g2.drawLine(rx, ry, px, py);
                }
            }
        }
    }

    // =========================================================================
    // LAYER 6: Primary Mass Relays
    // =========================================================================
    private void drawPrimaryMassRelays(Graphics2D g2, int cx, int cy, double scaleX, double scaleY, GalacticSector galaxy) {
        String[] relayNames = {
                "Conduit Gateway",
                "Primary Relay Alpha",
                "Primary Relay Omega",
                "Primary Relay Gamma"
        };

        for (int s = 0; s < 4; s++) {
            int rx = cx + (int) (RELAY_NORM_COORDS[s][0] * scaleX);
            int ry = cy + (int) (RELAY_NORM_COORDS[s][1] * scaleY);
            boolean unlocked = galaxy.isSectorUnlocked(s);
            boolean hovered = (hoveredRelaySector == s);

            // Relay Gyroscope Arms (Iconic curved fork)
            g2.setStroke(new BasicStroke(hovered ? 2.5f : 1.8f));
            g2.setColor(unlocked ? new Color(130, 160, 190) : new Color(80, 50, 50));

            // Draw curved metal prongs
            g2.drawArc(rx - 12, ry - 14, 24, 28, 45, 90);
            g2.drawArc(rx - 12, ry - 14, 24, 28, 225, 90);

            // Element Zero Core
            int coreR = 6;
            if (unlocked) {
                // Pulsing glowing blue Eezo core
                Color coreGlow = new Color(0, 220, 255, (int) (140 + 70 * Math.sin(pulsePhase * 2 * Math.PI + s)));
                g2.setColor(coreGlow);
                g2.fillOval(rx - coreR - 3, ry - coreR - 3, (coreR + 3) * 2, (coreR + 3) * 2);

                g2.setColor(new Color(220, 250, 255));
                g2.fillOval(rx - coreR, ry - coreR, coreR * 2, coreR * 2);

                // Label
                g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                g2.setColor(hovered ? new Color(0, 255, 255) : new Color(140, 200, 240));
                drawCenteredString(g2, relayNames[s], rx, ry + 16);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2.setColor(new Color(0, 220, 140));
                drawCenteredString(g2, "[ONLINE]", rx, ry + 25);
            } else {
                // Offline locked core
                g2.setColor(new Color(150, 30, 40, 160));
                g2.fillOval(rx - coreR - 2, ry - coreR - 2, (coreR + 2) * 2, (coreR + 2) * 2);
                g2.setColor(new Color(255, 70, 70));
                g2.fillOval(rx - coreR, ry - coreR, coreR * 2, coreR * 2);

                g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                g2.setColor(hovered ? new Color(255, 120, 120) : new Color(200, 70, 70));
                drawCenteredString(g2, relayNames[s], rx, ry + 16);
                g2.setFont(new Font("SansSerif", Font.BOLD, 8));
                g2.setColor(new Color(255, 90, 80));
                drawCenteredString(g2, "🔒 [OFFLINE - Click]", rx, ry + 25);
            }

            if (hovered) {
                g2.setColor(new Color(0, 240, 255, 180));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(rx - 16, ry - 16, 32, 32);
            }
        }
    }

    // =========================================================================
    // LAYER 7: Planetary Star Systems
    // =========================================================================
    private void drawPlanetaryStarSystems(Graphics2D g2, int cx, int cy, double scaleX, double scaleY, GalacticSector galaxy) {
        int pr = 16; // Planet radius

        for (int s = 0; s < 4; s++) {
            boolean sectorUnlocked = galaxy.isSectorUnlocked(s);

            for (int c = 0; c < 6; c++) {
                int px = cx + (int) (PLANET_NORM_COORDS[s][c][0] * scaleX);
                int py = cy + (int) (PLANET_NORM_COORDS[s][c][1] * scaleY);

                StarSystem sys = galaxy.getSystem(s, c);
                boolean selected = (s == selectedSector && c == selectedCluster);
                boolean hovered = (s == hoveredSector && c == hoveredCluster);
                boolean flagship = (state.getFlagshipSector() == s && state.getFlagshipCluster() == c);

                if (!sectorUnlocked) {
                    // Shrouded planet beyond FTL reach
                    drawLockedPlanet(g2, px, py, pr, sys, hovered);
                } else {
                    drawActivePlanet(g2, px, py, pr, sys, s, c, selected, hovered, flagship);
                }
            }
        }
    }

    private void drawLockedPlanet(Graphics2D g2, int px, int py, int pr, StarSystem sys, boolean hovered) {
        // Dark silhouette
        g2.setColor(new Color(20, 25, 35, 140));
        g2.fillOval(px - pr, py - pr, pr * 2, pr * 2);
        g2.setColor(new Color(60, 40, 50, 120));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawOval(px - pr, py - pr, pr * 2, pr * 2);

        // Lock glyph
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(255, 80, 80, 160));
        drawCenteredString(g2, "🔒", px, py + 4);

        // System Name in subdued font
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g2.setColor(new Color(130, 90, 100, 160));
        drawCenteredString(g2, sys.getSystemName(), px, py + pr + 11);

        if (hovered) {
            g2.setColor(new Color(255, 80, 80, 120));
            g2.drawOval(px - pr - 4, py - pr - 4, (pr + 4) * 2, (pr + 4) * 2);
        }
    }

    private void drawActivePlanet(Graphics2D g2, int px, int py, int pr, StarSystem sys,
                                  int sec, int clu, boolean selected, boolean hovered, boolean flagship) {
        Civilization civ = sys.getCivilization();
        ClimateType climate = sys.getClimateType();

        // 1. Orbital Ring & Evolutionary Arc
        int orbitR = pr + 7;
        if (civ == null) {
            g2.setColor(new Color(50, 65, 85, 80));
            float[] dash = { 3.0f, 3.0f };
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2.drawOval(px - orbitR, py - orbitR, orbitR * 2, orbitR * 2);
        } else {
            int tier = civ.getEvolutionaryTier();
            if (civ.isHarvestReady()) {
                // Ripe Golden Radiant Halo with Pulse
                float pulseAura = (float) (120 + 80 * Math.sin(pulsePhase * 2 * Math.PI));
                g2.setColor(new Color(255, 215, 0, (int) pulseAura));
                g2.setStroke(new BasicStroke(2.8f));
                g2.drawOval(px - orbitR, py - orbitR, orbitR * 2, orbitR * 2);

                g2.setColor(new Color(255, 160, 20, (int) (pulseAura * 0.6)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(px - orbitR - 3, py - orbitR - 3, (orbitR + 3) * 2, (orbitR + 3) * 2);
            } else {
                g2.setColor(new Color(35, 45, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(px - orbitR, py - orbitR, orbitR * 2, orbitR * 2);

                Color arcColor = (tier == 0) ? new Color(0, 230, 115) : (tier == 1 ? new Color(0, 200, 255) : new Color(255, 180, 30));
                int sweepAngle = (tier == 0) ? 90 : (tier == 1 ? 180 : 270);
                g2.setColor(arcColor);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(px - orbitR, py - orbitR, orbitR * 2, orbitR * 2, 90, -sweepAngle);
            }
        }

        // 2. 3D Procedural Planetary Sphere
        Color pColor = getPrimaryPlanetColor(climate);
        Color sColor = getSecondaryPlanetColor(climate);
        Color highlight = new Color(
                Math.min(255, sColor.getRed() + 50),
                Math.min(255, sColor.getGreen() + 50),
                Math.min(255, sColor.getBlue() + 50)
        );
        Color shadow = new Color(
                Math.max(0, pColor.getRed() - 40),
                Math.max(0, pColor.getGreen() - 40),
                Math.max(0, pColor.getBlue() - 40)
        );

        RadialGradientPaint planetPaint = new RadialGradientPaint(
                (float) px, (float) py, (float) pr, (float) (px - 4), (float) (py - 4),
                new float[]{ 0.0f, 0.65f, 1.0f },
                new Color[]{ highlight, pColor, shadow },
                MultipleGradientPaint.CycleMethod.NO_CYCLE
        );
        g2.setPaint(planetPaint);
        g2.fillOval(px - pr, py - pr, pr * 2, pr * 2);

        // Planetary Atmospheric Haze Halo
        Color atmoColor = getAtmosphereColor(climate);
        g2.setColor(atmoColor);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawOval(px - pr, py - pr, pr * 2, pr * 2);

        // Methane Planetary Rings
        if (climate == ClimateType.METHANE || sys.getSystemName().toLowerCase().contains("thessia")) {
            g2.setColor(new Color(atmoColor.getRed(), atmoColor.getGreen(), atmoColor.getBlue(), 130));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawOval(px - pr - 7, py - 3, (pr + 7) * 2, 6);
        }

        // Kinetic Defense Barrier
        if (civ != null && civ.hasKineticBarrier()) {
            g2.setColor(new Color(0, 240, 255, 175));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(px - pr - 3, py - pr - 3, (pr + 3) * 2, (pr + 3) * 2);
        }

        // 3. Sovereign Flagship Presence Badge
        if (flagship) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 8));
            g2.setColor(new Color(255, 215, 0));
            drawCenteredString(g2, "👑 SOVEREIGN", px, py - pr - 10);
        }

        // 4. Garrison Unit Indicator
        if (sys.getBiomechanicalUnit() != null) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 8));
            g2.setColor(new Color(120, 220, 255));
            String unitGlyph = "⬡";
            if (sys.getBiomechanicalUnit() instanceof CollectorDrone) unitGlyph = "🤖";
            else if (sys.getBiomechanicalUnit() instanceof HuskSwarm) unitGlyph = "💀";
            else if (sys.getBiomechanicalUnit() instanceof ScionBehemoth) unitGlyph = "⚔️";
            drawCenteredString(g2, unitGlyph, px, py - pr - (flagship ? 19 : 10));
        }

        // 5. System Labels (Name & Occupant)
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(selected ? new Color(0, 245, 255) : Color.WHITE);
        drawCenteredString(g2, sys.getSystemName(), px, py + pr + 11);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        if (civ != null) {
            if (civ.isHarvestReady()) {
                g2.setColor(new Color(255, 220, 80));
                drawCenteredString(g2, "🌾 RIPE (T3)", px, py + pr + 21);
            } else if (civ.isAiHeresyActive()) {
                g2.setColor(new Color(255, 80, 80));
                drawCenteredString(g2, "⚠️ AI REBELLION", px, py + pr + 21);
            } else {
                g2.setColor(new Color(130, 200, 240));
                drawCenteredString(g2, civ.getSpeciesName() + " [T" + civ.getEvolutionaryTier() + "]", px, py + pr + 21);
            }
        } else {
            if (climate == ClimateType.BARREN) {
                g2.setColor(new Color(140, 160, 180));
                drawCenteredString(g2, "[Dead Rock]", px, py + pr + 21);
            } else {
                g2.setColor(new Color(90, 120, 150));
                drawCenteredString(g2, "Uninhabited", px, py + pr + 21);
            }
        }

        // 6. Selection Targeting Reticle
        if (selected) {
            drawSelectionReticle(g2, px, py, pr);
        } else if (hovered) {
            g2.setColor(new Color(0, 240, 255, 140));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(px - pr - 4, py - pr - 4, (pr + 4) * 2, (pr + 4) * 2);
        }
    }

    private void drawSelectionReticle(Graphics2D g2, int px, int py, int pr) {
        g2.setColor(new Color(0, 240, 255));
        g2.setStroke(new BasicStroke(1.8f));

        int bSize = pr + 9;
        int cornerLen = 7;

        // Top-Left Bracket
        g2.drawLine(px - bSize, py - bSize + cornerLen, px - bSize, py - bSize);
        g2.drawLine(px - bSize, py - bSize, px - bSize + cornerLen, py - bSize);

        // Top-Right Bracket
        g2.drawLine(px + bSize - cornerLen, py - bSize, px + bSize, py - bSize);
        g2.drawLine(px + bSize, py - bSize, px + bSize, py - bSize + cornerLen);

        // Bottom-Left Bracket
        g2.drawLine(px - bSize, py + bSize - cornerLen, px - bSize, py + bSize);
        g2.drawLine(px - bSize, py + bSize, px - bSize + cornerLen, py + bSize);

        // Bottom-Right Bracket
        g2.drawLine(px + bSize - cornerLen, py + bSize, px + bSize, py + bSize);
        g2.drawLine(px + bSize, py + bSize - cornerLen, px + bSize, py + bSize);

        // Pulsing reticle ring
        float ringAura = (float) (140 + 70 * Math.sin(pulsePhase * 2 * Math.PI));
        g2.setColor(new Color(255, 220, 80, (int) ringAura));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(px - bSize - 2, py - bSize - 2, (bSize + 2) * 2, (bSize + 2) * 2);
    }

    // =========================================================================
    // LAYER 8: The Citadel Station at Galactic Core
    // =========================================================================
    private void drawCitadelStation(Graphics2D g2, int cx, int cy) {
        boolean hovered = isCitadelHovered;

        // Outer Dark Space Pulsing Halo
        float corePulse = (float) (110 + 70 * Math.sin(pulsePhase * 2 * Math.PI));
        g2.setColor(new Color(124, 77, 255, (int) corePulse));
        g2.fillOval(cx - 36, cy - 36, 72, 72);

        // 5 Radiating Ward Arms (Iconic Citadel Silhouette)
        double armLength = 36.0;
        double armInnerR = 14.0;
        int numArms = 5;
        double baseAngle = -Math.PI / 2.0; // Point north

        g2.setColor(new Color(40, 50, 70));
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < numArms; i++) {
            double angle = baseAngle + (i * 2.0 * Math.PI / numArms);
            int x1 = (int) (cx + Math.cos(angle) * armInnerR);
            int y1 = (int) (cy + Math.sin(angle) * armInnerR);
            int x2 = (int) (cx + Math.cos(angle) * armLength);
            int y2 = (int) (cy + Math.sin(angle) * armLength);

            // Outer Hull of Ward Arm
            g2.setColor(new Color(55, 70, 95));
            g2.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);

            // Glowing presidium interior light slit
            g2.setColor(new Color(0, 240, 255, 200));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawLine(x1, y1, x2, y2);
        }

        // Central Presidium Ring
        int ringR = 16;
        g2.setColor(new Color(25, 35, 50));
        g2.fillOval(cx - ringR, cy - ringR, ringR * 2, ringR * 2);

        // Presidium Lake & Gardens Green Ring
        g2.setColor(new Color(0, 220, 130, 200));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx - ringR + 2, cy - ringR + 2, (ringR - 2) * 2, (ringR - 2) * 2);

        // Dark Energy Singularity / Presidium Core
        int coreR = 8;
        RadialGradientPaint coreGrad = new RadialGradientPaint(
                cx, cy, coreR,
                new float[]{ 0.0f, 0.7f, 1.0f },
                new Color[]{
                        new Color(255, 255, 255),
                        new Color(0, 229, 255),
                        new Color(124, 77, 255)
                }
        );
        g2.setPaint(coreGrad);
        g2.fillOval(cx - coreR, cy - coreR, coreR * 2, coreR * 2);

        // Citadel Station Title & Subtext
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(hovered ? new Color(0, 255, 255) : new Color(0, 220, 255));
        drawCenteredString(g2, "CITADEL NEXUS", cx, cy + 44);

        g2.setFont(new Font("SansSerif", Font.BOLD, 8));
        g2.setColor(new Color(255, 200, 50));
        drawCenteredString(g2, "[GALACTIC CORE // CLICK TO MANAGE]", cx, cy + 54);

        // Selection / Hover Halo
        if (hovered) {
            g2.setColor(new Color(0, 255, 255, 200));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - 40, cy - 40, 80, 80);
        }
    }

    // =========================================================================
    // LAYER 9: Floating Tactical HUD Overlay on Hover
    // =========================================================================
    private void drawTacticalHUD(Graphics2D g2, int w, int h, GalacticSector galaxy) {
        if (mousePos == null) return;

        // 1. Planet Hover HUD
        if (hoveredSector >= 0 && hoveredCluster >= 0) {
            StarSystem sys = galaxy.getSystem(hoveredSector, hoveredCluster);
            boolean unlocked = galaxy.isSectorUnlocked(hoveredSector);
            drawPlanetHUDCard(g2, w, h, sys, hoveredSector, hoveredCluster, unlocked);
            return;
        }

        // 2. Citadel Hover HUD
        if (isCitadelHovered) {
            drawCitadelHUDCard(g2, w, h);
            return;
        }

        // 3. Primary Relay Hover HUD
        if (hoveredRelaySector >= 0) {
            drawRelayHUDCard(g2, w, h, hoveredRelaySector, galaxy.isSectorUnlocked(hoveredRelaySector));
        }
    }

    private void drawPlanetHUDCard(Graphics2D g2, int w, int h, StarSystem sys, int sec, int clu, boolean unlocked) {
        Civilization civ = sys.getCivilization();
        boolean hasUnit = (sys.getBiomechanicalUnit() != null);
        boolean hasFlagship = (sec == state.getFlagshipSector() && clu == state.getFlagshipCluster());
        boolean hasColony = (civ != null && civ.getEvolutionaryTier() >= 2);
        int extraLines = (hasUnit ? 1 : 0) + (hasFlagship ? 1 : 0) + (hasColony ? 1 : 0);

        int cardW = 275;
        int cardH = unlocked ? (160 + extraLines * 16) : 85;
        int cardX = Math.min(w - cardW - 15, Math.max(15, mousePos.x + 15));
        int cardY = Math.min(h - cardH - 15, Math.max(15, mousePos.y - 40));

        // Background
        g2.setColor(new Color(10, 14, 24, 240));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 8, 8);
        g2.setColor(new Color(0, 200, 255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 8, 8);

        // Header
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(new Color(0, 240, 255));
        g2.drawString(sys.getSystemName() + " [" + sec + "," + clu + "]", cardX + 10, cardY + 18);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(160, 185, 210));
        g2.drawString(sys.getClimateType().getDisplayName(), cardX + 10, cardY + 32);

        if (!unlocked) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(255, 90, 80));
            g2.drawString("🔒 BEYOND FTL REACH", cardX + 10, cardY + 54);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(new Color(200, 160, 160));
            g2.drawString("Construct Primary Relay in Tech Tree first.", cardX + 10, cardY + 68);
            return;
        }

        int y = cardY + 48;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        if (civ != null) {
            g2.setColor(Color.WHITE);
            g2.drawString("Civilization: " + civ.getSpeciesName() + " (Tier " + civ.getEvolutionaryTier() + ")", cardX + 10, y);
            y += 15;
            g2.setColor(new Color(140, 210, 240));
            g2.drawString("Population: " + civ.getPopulationBillions() + " Billion Organics", cardX + 10, y);
            y += 15;

            // Affinity label
            String affLabel = sys.getClimateType().getAffinityLabel(civ.getSpeciesName());
            g2.setColor(sys.getClimateType().isIdealFor(civ.getSpeciesName()) ? new Color(0, 230, 120) :
                    (sys.getClimateType().isHostileFor(civ.getSpeciesName()) ? new Color(255, 100, 90) : new Color(200, 200, 200)));
            g2.drawString("Climate: " + affLabel, cardX + 10, y);
            y += 15;

            // Harvest readiness
            if (civ.isHarvestReady()) {
                g2.setColor(new Color(255, 215, 0));
                g2.drawString("🌾 Status: RIPE FOR ASCENSION REAPING!", cardX + 10, y);
            } else {
                g2.setColor(new Color(180, 180, 180));
                g2.drawString("Growth: Incubating (Stage " + (civ.getEvolutionaryTier() + 1) + "/3)", cardX + 10, y);
            }
            y += 15;

            // Defenses & Relays
            String defText = (civ.hasKineticBarrier() ? "🛡️ Barrier Active" : "No Barrier")
                    + (sys.isRelayBeamActive() ? " | ✦ Relay Linked" : "");
            g2.setColor(new Color(130, 200, 255));
            g2.drawString(defText, cardX + 10, y);
            y += 15;
        } else {
            g2.setColor(new Color(160, 180, 200));
            g2.drawString("Status: Lifeless World (Ready for Seeding)", cardX + 10, y);
            y += 16;
            g2.drawString("Favorable Crops: " + sys.getClimateType().getFavorableSpeciesNames(), cardX + 10, y);
            y += 16;
            g2.drawString("Relay Conduit: " + (sys.isRelayBeamActive() ? "✦ Active (+50% Eezo)" : "Offline"), cardX + 10, y);
            y += 16;
        }

        // Garrison Drone Info
        if (hasUnit) {
            g2.setColor(new Color(255, 180, 50));
            g2.drawString("🤖 Garrison: " + sys.getBiomechanicalUnit().getDesignation() + " (" + sys.getBiomechanicalUnit().getEnergyLevel() + "% Power)", cardX + 10, y);
            y += 15;
        }

        // Sovereign Presence
        if (hasFlagship) {
            g2.setColor(new Color(255, 80, 90));
            g2.drawString("👑 Sovereign Flagship Stationed", cardX + 10, y);
            y += 15;
        }

        // Colony Outpost
        if (hasColony) {
            g2.setColor(new Color(100, 255, 180));
            g2.drawString("🚀 Autonomous Colony Outpost Active", cardX + 10, y);
            y += 15;
        }

        // Action prompt footer
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(255, 200, 50));
        g2.drawString("[Click to Target Star System]", cardX + 10, cardH + cardY - 8);
    }

    private void drawCitadelHUDCard(Graphics2D g2, int w, int h) {
        int cardW = 270;
        int cardH = 125;
        int cardX = Math.min(w - cardW - 15, Math.max(15, mousePos.x + 15));
        int cardY = Math.min(h - cardH - 15, Math.max(15, mousePos.y - 40));

        g2.setColor(new Color(12, 16, 28, 245));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 8, 8);
        g2.setColor(new Color(124, 77, 255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 8, 8);

        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(new Color(0, 240, 255));
        g2.drawString("CITADEL NEXUS // GALACTIC CORE", cardX + 10, cardY + 18);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        CitadelNexus nexus = (engine != null) ? engine.getNexus() : null;
        String statusText = (nexus != null) ? nexus.getTierName() : "Tier I: Dormant Dark Space Hub";
        g2.drawString("Status: " + statusText, cardX + 10, cardY + 34);

        g2.setColor(new Color(255, 215, 0));
        g2.drawString("Passive Eezo Dividends: +" + state.calculatePassiveDividends() + " Eezo / Epoch", cardX + 10, cardY + 50);

        g2.setColor(new Color(140, 220, 255));
        String conduitStatus = "Conduits: Sec 0 [ON] | Sec 1 [" + (state.getGalaxyMap().isSectorUnlocked(1) ? "ON" : "LOCKED") + "] | Sec 2 [" + (state.getGalaxyMap().isSectorUnlocked(2) ? "ON" : "LOCKED") + "]";
        g2.drawString(conduitStatus, cardX + 10, cardY + 66);

        boolean lockdown = (nexus != null) && nexus.isArmsLockdownActive();
        g2.setColor(lockdown ? new Color(255, 80, 80) : new Color(0, 230, 140));
        g2.drawString("Crucible Arms Lockdown: " + (lockdown ? "ENGAGED (-25% Threat)" : "STANDBY"), cardX + 10, cardY + 82);

        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(255, 200, 50));
        g2.drawString("[Click to Open Citadel Nexus Manager]", cardX + 10, cardY + 110);
    }

    private void drawRelayHUDCard(Graphics2D g2, int w, int h, int sec, boolean unlocked) {
        int cardW = 250;
        int cardH = 95;
        int cardX = Math.min(w - cardW - 15, Math.max(15, mousePos.x + 15));
        int cardY = Math.min(h - cardH - 15, Math.max(15, mousePos.y - 40));

        g2.setColor(new Color(12, 16, 28, 245));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 8, 8);
        g2.setColor(unlocked ? new Color(0, 200, 255) : new Color(255, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 8, 8);

        String[] names = { "Citadel Conduit Hub", "Primary Relay Alpha", "Primary Relay Omega", "Primary Relay Gamma" };

        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(unlocked ? new Color(0, 240, 255) : new Color(255, 100, 100));
        g2.drawString(names[sec], cardX + 10, cardY + 18);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(Color.WHITE);
        g2.drawString("Connects: Citadel Core ➔ Sector " + sec, cardX + 10, cardY + 34);

        g2.setColor(unlocked ? new Color(0, 230, 140) : new Color(255, 90, 80));
        g2.drawString("Conduit Status: " + (unlocked ? "ACTIVE [FTL Corridor Online]" : "SEVERED [Offline]"), cardX + 10, cardY + 50);

        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(255, 200, 50));
        if (unlocked) {
            g2.drawString("[Relay Operational // Sector Unlocked]", cardX + 10, cardY + 75);
        } else {
            g2.drawString("[Click to Construct Primary Relay in Tech Tree]", cardX + 10, cardY + 75);
        }
    }

    // =========================================================================
    // LAYER 10: Map Legend
    // =========================================================================
    private void drawMapLegend(Graphics2D g2, int w, int h) {
        int legW = 340;
        int legH = 22;
        int legX = (w - legW) / 2;
        int legY = h - legH - 6;

        g2.setColor(new Color(10, 14, 22, 190));
        g2.fillRoundRect(legX, legY, legW, legH, 6, 6);
        g2.setColor(new Color(30, 45, 65));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(legX, legY, legW, legH, 6, 6);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        int lx = legX + 10;
        g2.setColor(new Color(0, 230, 120));
        g2.drawString("✨ Ideal (+50%)", lx, legY + 15);
        lx += 75;
        g2.setColor(new Color(255, 100, 90));
        g2.drawString("⚠️ Hostile (-50%)", lx, legY + 15);
        lx += 85;
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("🌾 Ripe", lx, legY + 15);
        lx += 45;
        g2.setColor(new Color(0, 220, 255));
        g2.drawString("🛡️ Barrier", lx, legY + 15);
        lx += 55;
        g2.setColor(new Color(0, 240, 255));
        g2.drawString("✦ Relay", lx, legY + 15);
    }

    // =========================================================================
    // HELPER METHODS: Colors & Utilities
    // =========================================================================
    private Color getPrimaryPlanetColor(ClimateType c) {
        if (c == null) return new Color(55, 65, 75);
        switch (c) {
            case GARDEN: return new Color(20, 90, 180);
            case ARID: return new Color(180, 100, 30);
            case METHANE: return new Color(210, 165, 40);
            case VOLCANIC: return new Color(45, 50, 60);
            case BARREN:
            default: return new Color(55, 65, 75);
        }
    }

    private Color getSecondaryPlanetColor(ClimateType c) {
        if (c == null) return new Color(120, 140, 160);
        switch (c) {
            case GARDEN: return new Color(34, 139, 34);
            case ARID: return new Color(220, 150, 50);
            case METHANE: return new Color(175, 120, 20);
            case VOLCANIC: return new Color(190, 30, 20);
            case BARREN:
            default: return new Color(120, 140, 160);
        }
    }

    private Color getAtmosphereColor(ClimateType c) {
        if (c == null) return new Color(160, 200, 220, 80);
        switch (c) {
            case GARDEN: return new Color(100, 200, 255, 140);
            case ARID: return new Color(255, 180, 80, 130);
            case METHANE: return new Color(255, 225, 110, 150);
            case VOLCANIC: return new Color(220, 70, 40, 120);
            case BARREN:
            default: return new Color(160, 200, 220, 80);
        }
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int y) {
        if (text == null) return;
        FontMetrics fm = g2.getFontMetrics();
        int sx = x - (fm.stringWidth(text) / 2);
        g2.drawString(text, sx, y);
    }
}
