package com.mycompany.mars_colony.interfaz.comun;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelMars extends JPanel {

    private Color colorFondo;
    private int radio;

    public PanelMars() {
        this(TemaMars.PANEL, TemaMars.RADIO_PANEL);
    }

    public PanelMars(Color colorFondo) {
        this(colorFondo, TemaMars.RADIO_PANEL);
    }

    public PanelMars(Color colorFondo, int radio) {
        this.colorFondo = colorFondo == null ? TemaMars.PANEL : colorFondo;
        this.radio = Math.max(0, radio);
        setOpaque(false);
        setBorder(TemaMars.padding(TemaMars.PADDING_PANEL));
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo == null ? TemaMars.PANEL : colorFondo;
        repaint();
    }

    protected Color getColorFondo() {
        return colorFondo;
    }

    protected int getRadio() {
        return radio;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        g2.dispose();
        super.paintComponent(g);
    }
}