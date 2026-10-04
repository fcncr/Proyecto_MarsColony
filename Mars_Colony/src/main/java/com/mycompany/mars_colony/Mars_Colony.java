/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mars_colony;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Casilla;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.combate.defensa.Barrera;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.combate.criatura.Acechador;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaContacto;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;

import com.mycompany.mars_colony.controlador.admin.AutenticadorAdmin;
/**
 *
 * @author fabic
 */
public class Mars_Colony {

    public static void main(String[] args) {
        char[] claveOriginal = "marte123".toCharArray();
        AutenticadorAdmin autenticador = new AutenticadorAdmin("admin", claveOriginal);

        boolean correcta = autenticador.autenticar("admin", "marte123".toCharArray());
        boolean claveIncorrecta = autenticador.autenticar("admin", "incorrecta".toCharArray());
        boolean usuarioIncorrecto = autenticador.autenticar("otro", "marte123".toCharArray());
        boolean usuarioNulo = autenticador.autenticar(null, "marte123".toCharArray());
        boolean claveNula = autenticador.autenticar("admin", null);

        System.out.println("Credenciales correctas: " + correcta);
        System.out.println("Contraseña incorrecta rechazada: " + !claveIncorrecta);
        System.out.println("Usuario incorrecto rechazado: " + !usuarioIncorrecto);
        System.out.println("Usuario nulo rechazado: " + !usuarioNulo);
        System.out.println("Contraseña nula rechazada: " + !claveNula);
    }
}
