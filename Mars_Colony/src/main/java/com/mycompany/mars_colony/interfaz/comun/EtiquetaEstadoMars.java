package com.mycompany.mars_colony.interfaz.comun;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class EtiquetaEstadoMars extends JLabel {

    private Color colorFondo;
    private Color colorTexto;

    public EtiquetaEstadoMars(String texto, Color colorFondo, Color colorTexto) {
        super(texto);
        this.colorFondo = colorFondo == null ? TemaMars.FONDO : colorFondo;
        this.colorTexto = colorTexto == null ? TemaMars.TEXTO_CLARO : colorTexto;
        configurar();
    }

    public static EtiquetaEstadoMars correcto(String texto) {
        return new EtiquetaEstadoMars(texto, TemaMars.CORRECTO, TemaMars.TEXTO);
    }

    public static EtiquetaEstadoMars error(String texto) {
        return new EtiquetaEstadoMars(texto, TemaMars.ERROR, TemaMars.TEXTO);
    }

    public static EtiquetaEstadoMars energia(String texto) {
        return new EtiquetaEstadoMars(texto, TemaMars.FONDO, TemaMars.ENERGIA);
    }

    public static EtiquetaEstadoMars accion(String texto) {
        return new EtiquetaEstadoMars(texto, TemaMars.ACCION, TemaMars.TEXTO);
    }

    public static EtiquetaEstadoMars neutro(String texto) {
        return new EtiquetaEstadoMars(texto, TemaMars.BLOQUEADO, TemaMars.TEXTO_CLARO);
    }

    private void configurar() {
        setOpaque(false);
        setForeground(colorTexto);
        setFont(TemaMars.textoDestacado());
        setBorder(TemaMars.padding(6, 12, 6, 12));
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}