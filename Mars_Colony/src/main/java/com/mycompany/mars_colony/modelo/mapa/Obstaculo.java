/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;
import java.io.Serializable;

public class Obstaculo implements OcupanteMapa, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Posicion posicion;

    public Obstaculo(String id, Posicion posicion) {
        this.id = id;
        this.posicion = posicion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    @Override
    public boolean bloqueaPasoTerrestre() {
        return true;
    }

    @Override
    public boolean esAereo() {
        return false;
    }
}