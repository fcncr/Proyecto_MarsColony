/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Movible;

public class Tablero {
    private static final int FILAS_POR_DEFECTO = 25;
    private static final int COLUMNAS_POR_DEFECTO = 25;

    private int filas;
    private int columnas;
    private Casilla[][] casillas;
    
    public Tablero(NucleoOxigeno nucleo) {
        this(FILAS_POR_DEFECTO, COLUMNAS_POR_DEFECTO, nucleo);
    }
   
    public Tablero(int filas, int columnas, NucleoOxigeno nucleo) {
        if (filas < 25 || columnas < 25) {
            throw new IllegalArgumentException("El tablero debe ser de al menos 25x25.");
        }

        if (nucleo == null) {
            throw new IllegalArgumentException("El tablero necesita un nucleo de oxigeno.");
        }

        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new Casilla[filas][columnas];

        inicializarCasillas();

        Posicion centro = new Posicion(filas / 2, columnas / 2);
        nucleo.setPosicion(centro);
        casillas[centro.getFila()][centro.getColumna()].colocar(nucleo);
    }
   
    private void inicializarCasillas() {
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                casillas[fila][columna] = new Casilla(new Posicion(fila, columna));
            }
        }
    }
   
    public boolean estaDentro(Posicion posicion) {
        if (posicion == null) {
            return false;
        }

        return posicion.getFila() >= 0 && posicion.getFila() < filas && posicion.getColumna() >= 0 && posicion.getColumna() < columnas;
    }
   
    public boolean estaLibre(Posicion posicion) {
        if (!estaDentro(posicion)) {
            return false;
        }

        return casillas[posicion.getFila()][posicion.getColumna()].estaLibre();
    }
    
    public OcupanteMapa obtener(Posicion posicion) {
        if (!estaDentro(posicion)) {
            return null;
        }

        return casillas[posicion.getFila()][posicion.getColumna()].getOcupante();
    }
    
    public boolean colocar(OcupanteMapa ocupante, Posicion posicion) {
        if (ocupante == null || !estaDentro(posicion) || !estaLibre(posicion)) {
            return false;
        }

        if (ocupante instanceof ComponenteCombate) {
            ComponenteCombate componente = (ComponenteCombate) ocupante;
            componente.setPosicion(posicion);
        } else if (!posicion.equals(ocupante.getPosicion())) {
            return false;
        }

        return casillas[posicion.getFila()][posicion.getColumna()].colocar(ocupante);
    }
    
    public OcupanteMapa retirar(Posicion posicion) {
        if (!estaDentro(posicion)) {
            return null;
        }

        OcupanteMapa ocupante = obtener(posicion);

        if (ocupante == null || ocupante instanceof NucleoOxigeno) {
            return null;
        }

        return casillas[posicion.getFila()][posicion.getColumna()].retirar();
    }
    
    public boolean mover(Posicion origen, Posicion destino) {
        if (!estaDentro(origen) || !estaDentro(destino) || !estaLibre(destino)) {
            return false;
        }

        OcupanteMapa ocupante = obtener(origen);

        if (!(ocupante instanceof Movible) || !(ocupante instanceof ComponenteCombate)) {
            return false;
        }

        ComponenteCombate componente = (ComponenteCombate) ocupante;

        casillas[origen.getFila()][origen.getColumna()].retirar();
        componente.setPosicion(destino);
        casillas[destino.getFila()][destino.getColumna()].colocar(ocupante);

        return true;
    }
    
    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
    
    public Casilla getCasilla(Posicion posicion) {
        if (!estaDentro(posicion)) {
            return null;
        }

        return casillas[posicion.getFila()][posicion.getColumna()];
    }
}