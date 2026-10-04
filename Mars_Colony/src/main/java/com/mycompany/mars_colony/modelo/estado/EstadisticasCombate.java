package com.mycompany.mars_colony.modelo.estado;

import java.io.Serializable;

public class EstadisticasCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private double vidaMaxima;
    private double danioGolpe;
    private double frecuenciaAtaque;
    private int alcance;
    private int radioEfecto;
    private int costoCapacidad;
    private boolean atacaAereo;
    private int cantidadAtaques;
    private int maxObjetivos;
    private long intervaloMovimientoMs;

    public EstadisticasCombate(
            double vidaMaxima,
            double danioGolpe,
            double frecuenciaAtaque,
            int alcance,
            int radioEfecto,
            int costoCapacidad,
            boolean atacaAereo,
            int cantidadAtaques,
            int maxObjetivos,
            long intervaloMovimientoMs) {

        this.vidaMaxima = vidaMaxima;
        this.danioGolpe = danioGolpe;
        this.frecuenciaAtaque = frecuenciaAtaque;
        this.alcance = alcance;
        this.radioEfecto = radioEfecto;
        this.costoCapacidad = costoCapacidad;
        this.atacaAereo = atacaAereo;
        this.cantidadAtaques = cantidadAtaques;
        this.maxObjetivos = maxObjetivos;
        this.intervaloMovimientoMs = intervaloMovimientoMs;
    }

    public EstadisticasCombate copiar() {
        return new EstadisticasCombate(
                vidaMaxima,
                danioGolpe,
                frecuenciaAtaque,
                alcance,
                radioEfecto,
                costoCapacidad,
                atacaAereo,
                cantidadAtaques,
                maxObjetivos,
                intervaloMovimientoMs
        );
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(double vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public double getDanioGolpe() {
        return danioGolpe;
    }

    public void setDanioGolpe(double danioGolpe) {
        this.danioGolpe = danioGolpe;
    }

    public double getFrecuenciaAtaque() {
        return frecuenciaAtaque;
    }

    public void setFrecuenciaAtaque(double frecuenciaAtaque) {
        this.frecuenciaAtaque = frecuenciaAtaque;
    }

    public int getAlcance() {
        return alcance;
    }

    public void setAlcance(int alcance) {
        this.alcance = alcance;
    }

    public int getRadioEfecto() {
        return radioEfecto;
    }

    public void setRadioEfecto(int radioEfecto) {
        this.radioEfecto = radioEfecto;
    }

    public int getCostoCapacidad() {
        return costoCapacidad;
    }

    public void setCostoCapacidad(int costoCapacidad) {
        this.costoCapacidad = costoCapacidad;
    }

    public boolean isAtacaAereo() {
        return atacaAereo;
    }

    public void setAtacaAereo(boolean atacaAereo) {
        this.atacaAereo = atacaAereo;
    }

    public int getCantidadAtaques() {
        return cantidadAtaques;
    }

    public void setCantidadAtaques(int cantidadAtaques) {
        this.cantidadAtaques = cantidadAtaques;
    }

    public int getMaxObjetivos() {
        return maxObjetivos;
    }

    public void setMaxObjetivos(int maxObjetivos) {
        this.maxObjetivos = maxObjetivos;
    }

    public long getIntervaloMovimientoMs() {
        return intervaloMovimientoMs;
    }

    public void setIntervaloMovimientoMs(long intervaloMovimientoMs) {
        this.intervaloMovimientoMs = intervaloMovimientoMs;
    }
}