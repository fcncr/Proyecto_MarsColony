/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate.defensa;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
/**
 *
 * @author fabic
 */
public class Barrera extends Defensa {

    public Barrera(String id, String nombre, double vidaInicial, int nivel, int misionAparicion, int costoCapacidad, Posicion posicion) {
        super(id, nombre, vidaInicial, nivel, misionAparicion, 0, 0, 0, 0, costoCapacidad, posicion);
    }
}