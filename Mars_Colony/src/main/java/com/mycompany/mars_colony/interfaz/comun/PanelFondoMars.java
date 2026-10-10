package com.mycompany.mars_colony.interfaz.comun;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelFondoMars extends JPanel {

    private String rutaFondo;
    private Color colorBase;
    private int opacidadOscurecimiento;

    public PanelFondoMars() {
        this(null);
    }

    public PanelFondoMars(String rutaFondo) {
        this.rutaFondo = rutaFondo;
        this.colorBase = TemaMars.FONDO_PROFUNDO;
        this.opacidadOscurecimiento = 70;
        setOpaque(true);
    }

    public void setRutaFondo(String rutaFondo) {
        this.rutaFondo = rutaFondo;
        repaint();
    }

    public void setColorBase(Color colorBase) {
        this.colorBase = colorBase == null ? TemaMars.FONDO_PROFUNDO : colorBase;
        repaint();
    }

    public void setOpacidadOscurecimiento(int opacidadOscurecimiento) {
        this.opacidadOscurecimiento = Math.max(0, Math.min(255, opacidadOscurecimiento));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(colorBase);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (rutaFondo != null && !rutaFondo.isBlank() && GestorImagenes.existe(rutaFondo)) {
            ImageIcon icono = GestorImagenes.cargarCubriendo(rutaFondo, Math.max(1, getWidth()), Math.max(1, getHeight()));
            Image imagen = icono.getImage();
            g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);
            g2.setColor(new Color(4, 17, 27, opacidadOscurecimiento));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }
}