/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.estado.Bando;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;

public class NucleoOxigeno extends ComponenteCombate {

    public NucleoOxigeno(String idConfiguracion, String nombre, double vidaMaxima, ImagenesEstado imagenes, Posicion posicion) {
        super(idConfiguracion, nombre, new EstadisticasCombate(vidaMaxima, 0, 0, 0, 0, 0, false, 0, 0, 0), imagenes, 1, posicion);
    }

    @Override
    public Bando getBando() {
        return Bando.ALIADO;
    }

    @Override
    public boolean bloqueaPasoTerrestre() {
        return true;
    }
}
