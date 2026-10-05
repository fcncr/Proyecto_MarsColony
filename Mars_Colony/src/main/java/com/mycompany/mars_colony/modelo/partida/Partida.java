package com.mycompany.mars_colony.modelo.partida;

import com.mycompany.mars_colony.modelo.mapa.Tablero;
import java.util.Random;

public class Partida {

    private String versionFormato;
    private String nombreComandante;

    private int misionActual = 1;
    private int totalMisiones = 10;
    private int misionesSuperadas;

    private Escuadron escuadron;
    private Tablero tablero;
    private Mision mision;

    private Random azar;

    public Partida(String nombreComandante, Escuadron escuadron, Tablero tablero, Mision mision) {
        this.versionFormato = "1.0";
        this.nombreComandante = nombreComandante;
        this.misionActual = 1;
        this.totalMisiones = 10;
        this.misionesSuperadas = 0;
        this.escuadron = escuadron;
        this.tablero = tablero;
        this.mision = mision;
        this.azar = new Random();
    }

    public boolean puedeFinalizarCampania() {
        return misionesSuperadas >= totalMisiones;
    }

    public boolean validarEstado() {
        if (nombreComandante == null || nombreComandante.isBlank()) {
            return false;
        }

        if (misionActual < 1 || totalMisiones < 10 || misionesSuperadas < 0) {
            return false;
        }

        if (escuadron == null || tablero == null || mision == null) {
            return false;
        }

        return true;
    }

    public String getVersionFormato() {
        return versionFormato;
    }

    public String getNombreComandante() {
        return nombreComandante;
    }

    public int getMisionActual() {
        return misionActual;
    }

    public int getTotalMisiones() {
        return totalMisiones;
    }

    public int getMisionesSuperadas() {
        return misionesSuperadas;
    }

    public Escuadron getEscuadron() {
        return escuadron;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Mision getMision() {
        return mision;
    }

    public Random getAzar() {
        return azar;
    }

    public void setMisionActual(int misionActual) {
        this.misionActual = misionActual;
    }

    public void setMisionesSuperadas(int misionesSuperadas) {
        this.misionesSuperadas = misionesSuperadas;
    }

    public void setMision(Mision mision) {
        this.mision = mision;
    }
}
