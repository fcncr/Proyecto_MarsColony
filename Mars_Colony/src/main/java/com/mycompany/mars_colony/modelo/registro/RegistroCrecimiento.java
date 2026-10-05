/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.registro;
import java.io.Serializable;
/**
 *
 * @author fabic
 */
public class RegistroCrecimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    private int numeroMision;
    
    private double porcentajeVida;
    private double porcentajeDanio;

    private double vidaAnterior;
    private double vidaNueva;

    private double danioAnterior;
    private double danioNuevo;
    
    public RegistroCrecimiento(int numeroMision, double porcentajeVida, double porcentajeDanio, double vidaAnterior, double vidaNueva, double danioAnterior, double danioNuevo) {
        this.numeroMision = numeroMision;
        this.porcentajeVida = porcentajeVida;
        this.porcentajeDanio = porcentajeDanio;
        this.vidaAnterior = vidaAnterior;
        this.vidaNueva = vidaNueva;
        this.danioAnterior = danioAnterior;
        this.danioNuevo = danioNuevo;
    }
    
    public int getNumeroMision() {
        return numeroMision;
    }

    public double getPorcentajeVida() {
        return porcentajeVida;
    }

    public double getPorcentajeDanio() {
        return porcentajeDanio;
    }

    public double getVidaAnterior() {
        return vidaAnterior;
    }

    public double getVidaNueva() {
        return vidaNueva;
    }

    public double getDanioAnterior() {
        return danioAnterior;
    }

    public double getDanioNuevo() {
        return danioNuevo;
    }
}
