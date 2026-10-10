package com.mycompany.mars_colony;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.generacion.GeneradorMision;
import com.mycompany.mars_colony.generacion.GeneradorObstaculos;
import com.mycompany.mars_colony.interfaz.juego.VentanaJuego;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import com.mycompany.mars_colony.persistencia.RepositorioPartidas;
import com.mycompany.mars_colony.util.RutasAplicacion;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Mars_Colony {

    private Mars_Colony() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mars_Colony::iniciarJuego);
    }

    private static void iniciarJuego() {
        try {
            Path archivoCatalogo = RutasAplicacion.resolver("datos/catalogo.dat");
            Path directorioPartidas = RutasAplicacion.resolver("partidas");
            RepositorioCatalogo repositorioCatalogo = new RepositorioCatalogo(archivoCatalogo);
            RepositorioPartidas repositorioPartidas = new RepositorioPartidas(directorioPartidas);
            GeneradorMision generadorMision = new GeneradorMision();
            GeneradorObstaculos generadorObstaculos = new GeneradorObstaculos();
            Partida partidaInicial = crearPartidaInicial();
            ControladorJuego.CreadorPartida creadorPartida = nombreComandante -> crearNuevaPartida(nombreComandante, repositorioCatalogo, generadorMision, generadorObstaculos);
            ControladorJuego controlador = new ControladorJuego(partidaInicial, repositorioPartidas, creadorPartida);

            controlador.configurarGeneradorMision(generadorMision);

            VentanaJuego ventana = new VentanaJuego(controlador);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No fue posible iniciar Mars Colony.\n" + e.getMessage(), "Mars Colony", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Partida crearPartidaInicial() {
        CatalogoComponentes catalogoVacio = new CatalogoComponentes();
        Escuadron escuadron = new Escuadron();
        NucleoOxigeno nucleo = crearNucleo();
        Tablero tablero = new Tablero(nucleo);
        Mision mision = new Mision(1, escuadron.getCapacidadTotal(), Collections.emptyList(), List.of(nucleo), false);

        return new Partida("Sin partida", escuadron, tablero, mision, catalogoVacio);
    }

    private static Partida crearNuevaPartida(String nombreComandante, RepositorioCatalogo repositorioCatalogo, GeneradorMision generadorMision, GeneradorObstaculos generadorObstaculos) {
        CatalogoComponentes catalogo = repositorioCatalogo.cargar();

        if (catalogo.listar().isEmpty()) {
            throw new IllegalStateException("El catálogo está vacío. Abra primero el administrador, cree configuraciones y guarde el catálogo.");
        }

        Escuadron escuadron = new Escuadron();
        NucleoOxigeno nucleo = crearNucleo();
        Tablero tablero = new Tablero(nucleo);
        Mision misionInicial = new Mision(1, escuadron.getCapacidadTotal(), Collections.emptyList(), List.of(nucleo), false);
        Partida partida = new Partida(nombreComandante, escuadron, tablero, misionInicial, catalogo);

        generadorObstaculos.generar(partida);
        generadorMision.generar(partida);

        return partida;
    }

    private static NucleoOxigeno crearNucleo() {
        ImagenesEstado imagenes = new ImagenesEstado("assets/importados/Nucleo.png", "assets/importados/Nucleo.png", "assets/importados/Nucleo.png");
        return new NucleoOxigeno("NUCLEO", "Núcleo de oxígeno", 100.0, imagenes, null);
    }
}