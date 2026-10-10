package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class BotonMars extends JButton {

    public enum Variante {
        PRIMARIO,
        SECUNDARIO,
        PELIGRO,
        BLOQUEADO
    }

    private Variante variante;

    public BotonMars(String texto, Variante variante) {
        super(texto);
        this.variante = variante == null ? Variante.SECUNDARIO : variante;
        configurar();
    }

    public static BotonMars primario(String texto) {
        return new BotonMars(texto, Variante.PRIMARIO);
    }

    public static BotonMars secundario(String texto) {
        return new BotonMars(texto, Variante.SECUNDARIO);
    }

    public static BotonMars peligro(String texto) {
        return new BotonMars(texto, Variante.PELIGRO);
    }

    public static BotonMars bloqueado(String texto) {
        BotonMars boton = new BotonMars(texto, Variante.BLOQUEADO);
        boton.setEnabled(false);
        return boton;
    }

    private void configurar() {
        setFont(TemaMars.textoBoton());
        setForeground(colorTexto());
        setPreferredSize(new Dimension(180, TemaMars.ALTURA_BOTON));
        setMinimumSize(new Dimension(120, TemaMars.ALTURA_BOTON));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(true);
    }

    public void setVariante(Variante variante) {
        this.variante = variante == null ? Variante.SECUNDARIO : variante;
        setForeground(colorTexto());
        repaint();
    }

    public Variante getVariante() {
        return variante;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fondo = colorFondo();

        if (!isEnabled()) {
            fondo = TemaMars.BLOQUEADO;
        } else if (getModel().isPressed()) {
            fondo = oscurecer(fondo, 0.14f);
        } else if (getModel().isRollover()) {
            fondo = aclarar(fondo, 0.08f);
        }

        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), TemaMars.RADIO_BOTON, TemaMars.RADIO_BOTON);

        if (hasFocus() && isEnabled()) {
            g2.setColor(TemaMars.ENERGIA);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, TemaMars.RADIO_BOTON, TemaMars.RADIO_BOTON);
        }

        g2.dispose();

        setForeground(colorTexto());
        super.paintComponent(g);
    }

    private Color colorFondo() {
        return switch (variante) {
            case PRIMARIO -> TemaMars.ACCION;
            case SECUNDARIO -> TemaMars.FONDO;
            case PELIGRO -> TemaMars.ERROR;
            case BLOQUEADO -> TemaMars.BLOQUEADO;
        };
    }

    private Color colorTexto() {
        if (!isEnabled() || variante == Variante.BLOQUEADO) {
            return TemaMars.TEXTO_CLARO;
        }

        return variante == Variante.PRIMARIO ? TemaMars.TEXTO : TemaMars.TEXTO_CLARO;
    }

    private Color aclarar(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    private Color oscurecer(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
}