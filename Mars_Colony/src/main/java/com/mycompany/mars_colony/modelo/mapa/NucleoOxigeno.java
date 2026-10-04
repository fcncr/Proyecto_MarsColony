/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;

public class NucleoOxigeno extends ComponenteCombate {

    public NucleoOxigeno(String id, String nombre, double vidaInicial, Posicion posicion) {
        super(id, nombre, vidaInicial, 1, 1, 0, 0, 0, 0, 0, posicion);
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
