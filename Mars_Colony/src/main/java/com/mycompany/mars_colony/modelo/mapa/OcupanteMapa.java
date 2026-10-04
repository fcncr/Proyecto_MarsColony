/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.mars_colony.modelo.mapa;

/**
 *
 * @author fabic
 */
public interface OcupanteMapa {
    String getId();

    Posicion getPosicion();

    boolean bloqueaPasoTerrestre();

    boolean esAereo();
}
