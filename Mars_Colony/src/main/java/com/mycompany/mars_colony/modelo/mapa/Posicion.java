/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;

/**
 *
 * @author fabic
 */
public class Posicion {
    private int fila;
    private int columna;
    
    
    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }
    
    public int getFila() {
        return fila;
    }
    
    public int getColumna() {
        return columna;
    }
    
    @Override
    public boolean equals(Object obj) {

    if (this == obj) {
        return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }

    Posicion otra = (Posicion) obj;

    return fila == otra.fila && columna == otra.columna;
    }
    
    @Override
    public int hashCode() {
        int resultado = 17;
        resultado = 31 * resultado + fila;
        resultado = 31 * resultado + columna;
        return resultado;
    }
    
    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
    
    public double distanciaA(Posicion otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La posicion no puede ser null.");
        }

        int diferenciaFila = fila - otra.fila;
        int diferenciaColumna = columna - otra.columna;

        return Math.sqrt(diferenciaFila * diferenciaFila + diferenciaColumna * diferenciaColumna);
    }
    
    public boolean esAdyacente(Posicion otra) {
        if (otra == null || equals(otra)) {
            return false;
        }

        int diferenciaFila = Math.abs(fila - otra.fila);
        int diferenciaColumna = Math.abs(columna - otra.columna);

        return diferenciaFila <= 1 && diferenciaColumna <= 1;
    }
    
    
}
