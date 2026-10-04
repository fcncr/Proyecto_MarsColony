/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.registro.RegistroCombate;
import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author fabic
 */
public abstract class ComponenteCombate implements OcupanteMapa, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String nombre;

    private double vidaInicial;
    private double vidaMaxima;
    private double vidaActual;

    private int nivel;
    private int misionAparicion;

    private double danio;
    private double frecuenciaAtaque;
    private int alcance;
    private int radioEfecto;
    private int costoCapacidad;

    private Posicion posicion;

    private RegistroCombate registroCombate;
    private List<RegistroCrecimiento> historialCrecimiento;
    
    public ComponenteCombate(String id, String nombre, double vidaInicial, int nivel, int misionAparicion, double danio, double frecuenciaAtaque, int alcance, int radioEfecto, int costoCapacidad, Posicion posicion) {
        this.id = id;
        this.nombre = nombre;
        this.vidaInicial = vidaInicial;
        this.vidaMaxima = vidaInicial;
        this.vidaActual = vidaInicial;
        this.nivel = nivel;
        this.misionAparicion = misionAparicion;
        this.danio = danio;
        this.frecuenciaAtaque = frecuenciaAtaque;
        this.alcance = alcance;
        this.radioEfecto = radioEfecto;
        this.costoCapacidad = costoCapacidad;
        this.posicion = posicion;
        this.registroCombate = new RegistroCombate();
        this.historialCrecimiento = new ArrayList<>();
    }
    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getVidaInicial() {
        return vidaInicial;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public double getVidaActual() {
        return vidaActual;
    }

    public int getNivel() {
        return nivel;
    }

    public int getMisionAparicion() {
        return misionAparicion;
    }

    public double getDanio() {
        return danio;
    }

    public double getFrecuenciaAtaque() {
        return frecuenciaAtaque;
    }

    public int getAlcance() {
        return alcance;
    }

    public int getRadioEfecto() {
        return radioEfecto;
    }

    public int getCostoCapacidad() {
        return costoCapacidad;
    }

    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    public RegistroCombate getRegistroCombate() {
        return registroCombate;
    }

    public List<RegistroCrecimiento> getHistorialCrecimiento() {
        return historialCrecimiento;
    }
    
    public void setVidaActual(double vidaActual) {
        if (vidaActual < 0) {
            this.vidaActual = 0;
        } else if (vidaActual > vidaMaxima) {
            this.vidaActual = vidaMaxima;
        } else {
            this.vidaActual = vidaActual;
        }
    }

    public void setVidaMaxima(double vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setDanio(double danio) {
        this.danio = danio;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }
    
    public void agregarRegistroCrecimiento(RegistroCrecimiento registro) {
        historialCrecimiento.add(registro);
    }
    
    @Override
    public abstract boolean bloqueaPasoTerrestre();

    @Override
    public abstract boolean esAereo();
}
