/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mars_colony;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
/**
 *
 * @author fabic
 */
public class Mars_Colony {

    public static void main(String[] args) {
         Posicion p1 = new Posicion(5, 10);
        Posicion p2 = new Posicion(5, 10);
        Posicion p3 = new Posicion(5, 11);

        System.out.println("Posicion 1: " + p1);

        System.out.println("Fila: " + p1.getFila());
        System.out.println("Columna: " + p1.getColumna());

        System.out.println("p1 es igual a p2: " + p1.equals(p2));
        System.out.println("p1 es igual a p3: " + p1.equals(p3));
    }
}
