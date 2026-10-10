package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class TarjetaMars extends PanelMars {

    private boolean seleccionada;

    public TarjetaMars() {
        super(TemaMars.PANEL);
        setBorder(TemaMars.padding(16));
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
        repaint();
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(seleccionada ? 3f : 1.5f));
        g2.setColor(seleccionada ? TemaMars.ENERGIA : TemaMars.BORDE);
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, getRadio(), getRadio());
        g2.dispose();
    }
}