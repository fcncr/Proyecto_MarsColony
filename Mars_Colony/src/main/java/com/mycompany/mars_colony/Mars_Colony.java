package com.mycompany.mars_colony;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.juego.VentanaJuego;
import com.mycompany.mars_colony.modelo.combate.criatura.Acechador;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaAlcance;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.generacion.GeneradorMision;
import java.util.List;
import javax.swing.SwingUtilities;

public class Mars_Colony {

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Partida partida = crearPartidaVictoria();

        ControladorJuego controlador = new ControladorJuego(partida);
        GeneradorMision generador = new GeneradorMision();
        controlador.configurarGeneradorMision(generador);

        VentanaJuego ventana = new VentanaJuego(controlador);
        ventana.setVisible(true);
    });
}

    private static Partida crearPartidaVictoria() {
        ImagenesEstado imagenes = new ImagenesEstado("normal.png", "movimiento.png", "ataque.png");

        NucleoOxigeno nucleo = new NucleoOxigeno("NUCLEO", "Núcleo de oxígeno", 100.0, imagenes, null);
        Tablero tablero = new Tablero(nucleo);

        EstadisticasCombate estadisticasCriatura = new EstadisticasCombate(100.0, 30.0, 4.0, 1, 0, 6, false, 1, 1, 150);
        Acechador criatura = new Acechador("CRI-TEST", "Acechador demoledor", estadisticasCriatura, imagenes, 1, new Posicion(12, 18));
        tablero.colocar(criatura, new Posicion(12, 20));

        Escuadron escuadron = new Escuadron();

        List<ComponenteCombate> participantes = List.of(nucleo, criatura);
        Mision mision = new Mision(1, escuadron.getCapacidadTotal(), List.of(criatura), participantes, true);

        return new Partida("Comandante batalla", escuadron, tablero, mision);
    }
}