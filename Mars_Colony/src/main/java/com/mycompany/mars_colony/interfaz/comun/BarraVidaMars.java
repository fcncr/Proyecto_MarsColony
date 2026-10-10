package com.mycompany.mars_colony.interfaz.comun;

import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JProgressBar;

public class BarraVidaMars extends JProgressBar {

    private static final DecimalFormat FORMATO = new DecimalFormat("0.##");

    private double vidaActual;
    private double vidaMaxima;

    public BarraVidaMars() {
        super(0, 1000);
        setStringPainted(true);
        setFont(TemaMars.textoDestacado());
        setForeground(TemaMars.CORRECTO);
        setBackground(TemaMars.PANEL_SECUNDARIO);
        setBorderPainted(false);
        setPreferredSize(new Dimension(180, 24));
        setVida(0, 1);
    }

    public void setVida(double actual, double maxima) {
        vidaMaxima = Math.max(0, maxima);
        vidaActual = Math.max(0, Math.min(actual, vidaMaxima));

        double porcentaje = vidaMaxima <= 0 ? 0 : vidaActual / vidaMaxima;
        setValue((int) Math.round(porcentaje * 1000));

        if (porcentaje > 0.5) {
            setForeground(TemaMars.CORRECTO);
        } else if (porcentaje > 0.25) {
            setForeground(TemaMars.ACCION);
        } else {
            setForeground(TemaMars.ERROR);
        }

        setString(FORMATO.format(vidaActual) + " / " + FORMATO.format(vidaMaxima));
    }

    public double getVidaActual() {
        return vidaActual;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }
}