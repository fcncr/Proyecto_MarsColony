/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate.defensa;
import com.mycompany.mars_colony.modelo.combate.DefensaActiva;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
/**
 *
 * @author fabic
 */
public class DefensaContacto extends DefensaActiva {

    public DefensaContacto(String id, String nombre, double vidaInicial, int nivel, int misionAparicion, double danio, double frecuenciaAtaque, int alcance, int radioEfecto, int costoCapacidad, Posicion posicion) {
        super(id, nombre, vidaInicial, nivel, misionAparicion, danio, frecuenciaAtaque, alcance, radioEfecto, costoCapacidad, posicion);
    }
}
