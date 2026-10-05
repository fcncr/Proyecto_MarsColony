/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;
import java.io.Serializable;
/**
 *
 * @author fabic
 */
public class Casilla implements Serializable {

    private static final long serialVersionUID = 1L;
    private Posicion posicion;
    private OcupanteMapa ocupante;
    
    public Casilla(Posicion posicion) {
        this.posicion = posicion;
        this.ocupante = null;
    }
    
    public boolean estaLibre() {
        return ocupante == null;
    }
    
    public boolean colocar(OcupanteMapa nuevoOcupante) {
        if (!estaLibre() || nuevoOcupante == null) {
            return false;
        }

        ocupante = nuevoOcupante;
        return true;
    }
    
    public OcupanteMapa retirar() {
        OcupanteMapa retirado = ocupante;
        ocupante = null;
        return retirado;
    }
    
    public OcupanteMapa getOcupante() {
        return ocupante;
    }
    
    public Posicion getPosicion() {
        return posicion;
    }
}
