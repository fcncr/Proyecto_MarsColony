package com.mycompany.mars_colony.modelo.partida;

import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.io.Serializable;

public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    private String versionFormato;
    private String nombreComandante;

    private int misionActual = 1;
    private int totalMisiones = 10;

    private Set<Integer> misionesSuperadas;
    private boolean campaniaFinalizada;

    private Escuadron escuadron;
    private Tablero tablero;
    private Mision mision;
    
    private CatalogoComponentes catalogoSnapshot;
    private boolean catalogoSnapshotInicializado;

    private Random azar;
    
    

    public Partida(String nombreComandante, Escuadron escuadron, Tablero tablero, Mision mision) {
        this(nombreComandante, escuadron, tablero, mision, null);
    }

    public Partida(String nombreComandante, Escuadron escuadron, Tablero tablero, Mision mision, CatalogoComponentes catalogoSnapshot) {
        this.versionFormato = "1.0";
        this.nombreComandante = nombreComandante;
        this.misionActual = 1;
        this.totalMisiones = 10;
        this.misionesSuperadas = new HashSet<>();
        this.campaniaFinalizada = false;
        this.escuadron = escuadron;
        this.tablero = tablero;
        this.mision = mision;

        if (catalogoSnapshot == null) {
            this.catalogoSnapshot = null;
            this.catalogoSnapshotInicializado = false;
        } else {
            this.catalogoSnapshot = catalogoSnapshot.copiar();
            this.catalogoSnapshotInicializado = true;
        }

        this.azar = new Random();
    }

   
    public boolean registrarMisionSuperada(int numero) {

        if (numero < 1) {
            throw new IllegalArgumentException(
                    "El numero de mision debe ser mayor o igual a 1."
            );
        }

        return misionesSuperadas.add(numero);
    }

    public boolean fueMisionSuperada(int numero) {

        if (numero < 1) {
            return false;
        }

        return misionesSuperadas.contains(numero);
    }

    public boolean puedeFinalizarCampania() {

        for (int numero = 1; numero <= totalMisiones; numero++) {

            if (!misionesSuperadas.contains(numero)) {
                return false;
            }
        }

        return true;
    }

    public boolean validarEstado() {

        if (nombreComandante == null
                || nombreComandante.isBlank()) {

            return false;
        }

        if (misionActual < 1 || totalMisiones < 10) {
            return false;
        }

        if (misionesSuperadas == null) {
            return false;
        }

        for (Integer numero : misionesSuperadas) {

            if (numero == null || numero < 1) {
                return false;
            }
        }

        if (campaniaFinalizada && !puedeFinalizarCampania()) {
            return false;
        }

        if (escuadron == null
                || tablero == null
                || mision == null) {

            return false;
        }
        
        if (catalogoSnapshotInicializado && catalogoSnapshot == null) {
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

    public Set<Integer> getMisionesSuperadas() {
        return Collections.unmodifiableSet(
                misionesSuperadas
        );
    }

    public int getCantidadMisionesSuperadas() {
        return misionesSuperadas.size();
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
    
    public boolean tieneCatalogoSnapshot() {
        return catalogoSnapshotInicializado && catalogoSnapshot != null;
    }

    public CatalogoComponentes getCatalogoSnapshot() {
        if (!tieneCatalogoSnapshot()) {
            return null;
        }

        return catalogoSnapshot.copiar();
    }

    public void establecerCatalogoSnapshot(CatalogoComponentes catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("El catálogo no puede ser null.");
        }

        if (tieneCatalogoSnapshot()) {
            throw new IllegalStateException("La partida ya tiene un catálogo snapshot.");
        }

        this.catalogoSnapshot = catalogo.copiar();
        this.catalogoSnapshotInicializado = true;
    }

    public void setMisionActual(int misionActual) {

        if (misionActual < 1) {
            throw new IllegalArgumentException(
                    "La mision actual debe ser mayor o igual a 1."
            );
        }

        this.misionActual = misionActual;
    }

    public void setMision(Mision mision) {

        if (mision == null) {
            throw new IllegalArgumentException(
                    "La mision no puede ser null."
            );
        }

        this.mision = mision;
    }
    
    public boolean isCampaniaFinalizada() {
    return campaniaFinalizada;
    }

    public boolean finalizarCampania() {

        if (!puedeFinalizarCampania()) {
            return false;
        }

        campaniaFinalizada = true;
        return true;
    }
}