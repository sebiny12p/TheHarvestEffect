package com.seb.harvesteffect.ui;

import com.seb.harvesteffect.model.entity.Civilization;
import com.seb.harvesteffect.model.entity.ClimateType;
import com.seb.harvesteffect.model.entity.StarSystem;
import com.seb.harvesteffect.model.entity.BiomechanicalUnit;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Interactive procedural planet tile component for The Harvest Effect.
 * Renders authentic celestial bodies with radial sphere lighting, atmospheric glow,
 * orbital rings, evolutionary growth arcs, planetary biomes/climates, Mass Relay FTL beacons,
 * and tactical targeting brackets.
 */
public class PlanetTilePanel extends JPanel {

    public interface TileClickListener {
        void onTileClicked(int sector, int cluster);
    }

    private StarSystem system;
    private final TileClickListener listener;
    private boolean isSelected;
    private boolean isHovered;
    private boolean isFlagshipStationed;
    private boolean isSectorLocked;

    public PlanetTilePanel(StarSystem system, TileClickListener listener) {
        this.system = system;
        this.listener = listener;
        this.isSelected = false;
        this.isHovered = false;

        setPreferredSize(new Dimension(175, 135));
        setMinimumSize(new Dimension(150, 120));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onTileClicked(PlanetTilePanel.this.system.getSector(), PlanetTilePanel.this.system.getCluster());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                updateTooltip();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
        updateTooltip();
    }

    public void updateTooltip() {
        if (system == null) {
            setToolTipText(null);
            return;
        }
        StringBuilder sb = new StringBuilder("<html><body style='background-color:#0d121d; color:#cce; padding:6px; font-family:monospace;'>");
        sb.append("<b style='color:#00e5ff; font-size:12px;'>").append(system.getSystemName())
          .append(" [").append(system.getSector()).append(",").append(system.getCluster()).append("]</b><br>");
        sb.append("Climate: <b style='color:#ffe082;'>").append(system.getClimateType().getDisplayName()).append("</b><br>");
        if (isSectorLocked) {
            sb.append("<span style='color:#ff5252;'>🔒 Sector Locked (Requires Primary Relay)</span>");
        } else if (system.getCivilization() != null) {
            Civilization civ = system.getCivilization();
            sb.append("Civilization: <b style='color:#ffffff;'>").append(civ.getSpeciesName()).append("</b> (Tier ").append(civ.getEvolutionaryTier()).append(")<br>");
            sb.append("Population: ").append(civ.getPopulationBillions()).append("B organics<br>");
            sb.append("Climate Affinity: <span style='color:#81d4fa;'>").append(system.getClimateType().getAffinityLabel(civ.getSpeciesName())).append("</span><br>");
            sb.append("Status: ").append(civ.isHarvestReady() ? "<b style='color:#ffd700;'>🌾 RIPE FOR HARVEST!</b>" : "<span style='color:#aaa;'>Incubating (Stage " + (civ.getEvolutionaryTier() + 1) + "/3)</span>").append("<br>");
            if (civ.hasKineticBarrier()) sb.append("<span style='color:#4fc3f7;'>🛡️ Kinetic Barrier Active (Requires Scion/Husk or Sovereign)</span><br>");
        } else {
            sb.append("Status: <span style='color:#81c784;'>Lifeless World (Ready for Seeding)</span><br>");
            sb.append("Favorable Species: ").append(system.getClimateType().getFavorableSpeciesNames()).append("<br>");
        }
        if (system.getBiomechanicalUnit() != null) {
            sb.append("Garrison: <b style='color:#ffb74d;'>").append(system.getBiomechanicalUnit().getDesignation()).append("</b><br>");
        }
        if (isFlagshipStationed) {
            sb.append("<b style='color:#ff5252;'>👑 Sovereign Flagship Stationed</b><br>");
        }
        if (system.isRelayBeamActive()) {
            sb.append("<span style='color:#00e5ff;'>✦ Relay Beam Active (+50% Eezo)</span><br>");
        }
        sb.append("<span style='color:#ffb300; font-size:9px;'>[Click to Target]</span>");
        sb.append("</body></html>");
        setToolTipText(sb.toString());
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateTooltip();
        repaint();
    }

    public void setFlagshipStationed(boolean stationed) {
        this.isFlagshipStationed = stationed;
        updateTooltip();
        repaint();
    }

    public void setSectorLocked(boolean locked) {
        this.isSectorLocked = locked;
        updateTooltip();
        repaint();
    }

    public void setSystem(StarSystem system) {
        this.system = system;
        updateTooltip();
        repaint();
    }

    public StarSystem getSystem() {
        return system;
    }

    private Color getPrimaryPlanetColor() {
        ClimateType c = system.getClimateType();
        switch (c) {
            case GARDEN: return new Color(20, 90, 180);
            case ARID: return new Color(180, 100, 30);
            case METHANE: return new Color(210, 165, 40);
            case VOLCANIC: return new Color(45, 50, 60);
            case BARREN:
            default: return new Color(55, 65, 75);
        }
    }

    private Color getSecondaryPlanetColor() {
        ClimateType c = system.getClimateType();
        switch (c) {
            case GARDEN: return new Color(34, 139, 34);
            case ARID: return new Color(220, 150, 50);
            case METHANE: return new Color(175, 120, 20);
            case VOLCANIC: return new Color(190, 30, 20);
            case BARREN:
            default: return new Color(120, 140, 160);
        }
    }

    private Color getAtmosphereColor() {
        ClimateType c = system.getClimateType();
        switch (c) {
            case GARDEN: return new Color(100, 200, 255, 140);
            case ARID: return new Color(255, 180, 80, 130);
            case METHANE: return new Color(255, 225, 110, 150);
            case VOLCANIC: return new Color(220, 70, 40, 120);
            case BARREN:
            default: return new Color(160, 200, 220, 80);
        }
    }

    private boolean hasPlanetaryRings() {
        String name = system.getSystemName().toLowerCase();
        return system.getClimateType() == ClimateType.METHANE || name.contains("thessia") || name.contains("suen") || name.contains("irune");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Sector Locked Shroud Check
        if (isSectorLocked) {
            Color bgStart = new Color(8, 10, 16);
            Color bgEnd = new Color(4, 6, 10);
            g2.setPaint(new GradientPaint(0, 0, bgStart, 0, h, bgEnd));
            g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);
            g2.setColor(new Color(30, 38, 50));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 10, 10);

            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2.setColor(new Color(255, 90, 80));
            drawCenteredString(g2, "🔒 BEYOND FTL REACH", w / 2, h / 2 - 8);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(140, 160, 180));
            drawCenteredString(g2, "[Build Primary Relay]", w / 2, h / 2 + 10);
            g2.dispose();
            return;
        }

        // 2. Tile Base Background
        Color bgStart = isSelected ? new Color(18, 30, 52) : (isHovered ? new Color(16, 24, 38) : new Color(10, 14, 22));
        Color bgEnd = isSelected ? new Color(10, 18, 32) : new Color(6, 9, 15);
        GradientPaint bgGrad = new GradientPaint(0, 0, bgStart, 0, h, bgEnd);
        g2.setPaint(bgGrad);
        g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);

        // 3. Tile Border & Selection Holographic Brackets
        if (isSelected) {
            g2.setColor(new Color(0, 230, 255));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 10, 10);

            // Tactical Corner Brackets
            g2.setColor(new Color(255, 220, 80));
            g2.setStroke(new BasicStroke(2.5f));
            int bLen = 10;
            // Top-Left
            g2.drawLine(2, 2 + bLen, 2, 2);
            g2.drawLine(2, 2, 2 + bLen, 2);
            // Top-Right
            g2.drawLine(w - 3 - bLen, 2, w - 3, 2);
            g2.drawLine(w - 3, 2, w - 3, 2 + bLen);
            // Bottom-Left
            g2.drawLine(2, h - 3 - bLen, 2, h - 3);
            g2.drawLine(2, h - 3, 2 + bLen, h - 3);
            // Bottom-Right
            g2.drawLine(w - 3 - bLen, h - 3, w - 3, h - 3);
            g2.drawLine(w - 3, h - 3 - bLen, w - 3, h - 3);
        } else if (system.isRelayBeamActive()) {
            g2.setColor(new Color(0, 140, 200, 160));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 10, 10);
        } else {
            g2.setColor(isHovered ? new Color(60, 80, 110) : new Color(30, 38, 52));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 10, 10);
        }

        // 4. Header: System Name & Badges
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(isSelected ? new Color(0, 240, 255) : Color.WHITE);
        String title = system.getSystemName();
        g2.drawString(title, 8, 15);

        // Climate Biome Badge on Top Right
        ClimateType climate = system.getClimateType();
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        Color biomeBadgeColor;
        switch (climate) {
            case GARDEN: biomeBadgeColor = new Color(0, 210, 120); break;
            case ARID: biomeBadgeColor = new Color(255, 160, 40); break;
            case METHANE: biomeBadgeColor = new Color(240, 210, 40); break;
            case VOLCANIC: biomeBadgeColor = new Color(255, 80, 60); break;
            case BARREN:
            default: biomeBadgeColor = new Color(130, 150, 170); break;
        }
        g2.setColor(biomeBadgeColor);
        String badgeText = climate.name();
        FontMetrics fm = g2.getFontMetrics();
        int bw = fm.stringWidth(badgeText);
        g2.drawString(badgeText, w - bw - 8, 15);

        // Relay / Unit / Flagship Badges (sub-header)
        int subBadgeX = 8;
        if (isFlagshipStationed) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("👑 SOVEREIGN", subBadgeX, 27);
            subBadgeX += 72;
        }
        if (system.isRelayBeamActive()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(new Color(0, 220, 255));
            g2.drawString("✦ RELAY", subBadgeX, 27);
            subBadgeX += 50;
        }
        if (system.getBiomechanicalUnit() != null) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(new Color(130, 220, 255));
            g2.drawString("⬡ SWARM", subBadgeX, 27);
        }

        // 5. Center Coordinates for Celestial Planet
        int planetX = w / 2;
        int planetY = h / 2 - 2;
        int planetR = 22;

        // 6. Orbit & Growth Progress Arc
        Civilization civ = system.getCivilization();
        int orbitR = planetR + 8;

        if (civ == null) {
            g2.setColor(new Color(60, 75, 95, 90));
            float[] dash = { 3.0f, 3.0f };
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2.drawOval(planetX - orbitR, planetY - orbitR, orbitR * 2, orbitR * 2);
        } else {
            int tier = civ.getEvolutionaryTier();
            if (civ.isHarvestReady()) {
                // APEX ZENITH - Golden Radiant Aura
                g2.setColor(new Color(255, 215, 0, 200));
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawOval(planetX - orbitR, planetY - orbitR, orbitR * 2, orbitR * 2);

                g2.setColor(new Color(255, 140, 0, 130));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(planetX - orbitR - 3, planetY - orbitR - 3, (orbitR + 3) * 2, (orbitR + 3) * 2);
            } else {
                g2.setColor(new Color(45, 55, 70));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(planetX - orbitR, planetY - orbitR, orbitR * 2, orbitR * 2);

                Color arcColor = (tier == 0) ? new Color(0, 230, 115) : (tier == 1 ? new Color(0, 200, 255) : new Color(255, 180, 30));
                int sweepAngle = (tier == 0) ? 90 : (tier == 1 ? 180 : 270);
                g2.setColor(arcColor);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(planetX - orbitR, planetY - orbitR, orbitR * 2, orbitR * 2, 90, -sweepAngle);
            }
        }

        // 7. Planetary Rings
        Color atmoColor = getAtmosphereColor();
        if (hasPlanetaryRings()) {
            g2.setColor(new Color(atmoColor.getRed(), atmoColor.getGreen(), atmoColor.getBlue(), 120));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(planetX - planetR - 10, planetY - 4, (planetR + 10) * 2, 8);
        }

        // 8. Render Procedural 3D Planet Sphere
        Point focus = new Point(planetX - 5, planetY - 5);
        float[] dist = { 0.0f, 0.65f, 1.0f };
        Color pColor = getPrimaryPlanetColor();
        Color sColor = getSecondaryPlanetColor();

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
        Color[] colors = { highlight, pColor, shadow };
        RadialGradientPaint planetPaint = new RadialGradientPaint(
                planetX, planetY, planetR, focus.x, focus.y, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );
        g2.setPaint(planetPaint);
        g2.fillOval(planetX - planetR, planetY - planetR, planetR * 2, planetR * 2);

        // Planet Atmospheric Halo Ring
        g2.setColor(atmoColor);
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(planetX - planetR, planetY - planetR, planetR * 2, planetR * 2);

        // Planetary Kinetic Barrier Ring
        if (civ != null && civ.hasKineticBarrier()) {
            g2.setColor(new Color(0, 240, 255, 180));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawOval(planetX - planetR - 4, planetY - planetR - 4, (planetR + 4) * 2, (planetR + 4) * 2);
        }

        // Front half of rings
        if (hasPlanetaryRings()) {
            g2.setColor(new Color(atmoColor.getRed(), atmoColor.getGreen(), atmoColor.getBlue(), 170));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawArc(planetX - planetR - 10, planetY - 4, (planetR + 10) * 2, 8, 180, 180);
        }

        // 9. Footer Status Pill & Labels
        if (civ != null) {
            if (civ.isAiHeresyActive()) {
                g2.setColor(new Color(180, 25, 25));
                g2.fillRoundRect(6, h - 33, w - 12, 15, 6, 6);
                g2.setColor(new Color(255, 90, 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(6, h - 33, w - 12, 15, 6, 6);

                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.setColor(Color.WHITE);
                drawCenteredString(g2, "⚠️ AI HERESY REBELLION", w / 2, h - 22);
            } else if (civ.isHarvestReady()) {
                g2.setColor(new Color(210, 140, 10));
                g2.fillRoundRect(6, h - 33, w - 12, 15, 6, 6);
                g2.setColor(new Color(255, 230, 110));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(6, h - 33, w - 12, 15, 6, 6);

                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.setColor(Color.WHITE);
                String ripeLabel = civ.hasKineticBarrier() ? "🌾 RIPE [SHIELDED]" : "🌾 RIPE: ASCEND NOW";
                drawCenteredString(g2, ripeLabel, w / 2, h - 22);
            } else {
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                int tier = civ.getEvolutionaryTier();
                if (tier <= 1) {
                    g2.setColor(new Color(100, 230, 130));
                    drawCenteredString(g2, "🌱 Seedling (Stage 1/3)", w / 2, h - 22);
                } else {
                    g2.setColor(new Color(255, 200, 60));
                    String matLabel = civ.hasKineticBarrier() ? "🌿 Maturing [Shielded]" : "🌿 Maturing (Stage 2/3)";
                    drawCenteredString(g2, matLabel, w / 2, h - 22);
                }
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(new Color(190, 220, 245));
            String sub = civ.getSpeciesName() + " | " + civ.getPopulationBillions() + "B";
            drawCenteredString(g2, sub, w / 2, h - 9);
        } else {
            BiomechanicalUnit unit = system.getBiomechanicalUnit();
            if (unit != null) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.setColor(new Color(0, 210, 255));
                drawCenteredString(g2, "[ " + unit.getDesignation() + " ]", w / 2, h - 22);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(new Color(160, 190, 220));
                drawCenteredString(g2, "Automated (" + unit.getEnergyLevel() + " EP)", w / 2, h - 9);
            } else if (climate == ClimateType.BARREN) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
                g2.setColor(new Color(140, 160, 180));
                drawCenteredString(g2, "❄️ Dead Rock", w / 2, h - 22);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2.setColor(new Color(100, 130, 160));
                drawCenteredString(g2, "[Terraform to Farm]", w / 2, h - 9);
            } else {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
                g2.setColor(new Color(120, 140, 165));
                drawCenteredString(g2, "⛏ Fallow Soil", w / 2, h - 22);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2.setColor(new Color(90, 110, 130));
                drawCenteredString(g2, "[" + climate.getDisplayName() + "]", w / 2, h - 9);
            }
        }

        g2.dispose();
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int strW = fm.stringWidth(text);
        g2.drawString(text, x - strW / 2, y);
    }
}
