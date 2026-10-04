/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate.criatura;

import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.mapa.Posicion;

public class Escupidor extends Criatura {

    public Escupidor(String id, String nombre, double vidaInicial, int nivel, int misionAparicion, double danio, double frecuenciaAtaque, int alcance, int radioEfecto, int costoCapacidad, Posicion posicion) {
        super(id, nombre, vidaInicial, nivel, misionAparicion, danio, frecuenciaAtaque, alcance, radioEfecto, costoCapacidad, posicion);
    }

    @Override
    public boolean esAereo() {
        return false;
    }
}