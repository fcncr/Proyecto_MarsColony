package com.mycompany.mars_colony;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.generacion.GeneradorMision;
import com.mycompany.mars_colony.interfaz.juego.VentanaJuego;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import com.mycompany.mars_colony.persistencia.RepositorioPartidas;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Mars_Colony {

    private static final Path ARCHIVO_CATALOGO = Path.of("datos", "catalogo.dat");
    private static final Path DIRECTORIO_PARTIDAS = Path.of("partidas");

    private Mars_Colony() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> iniciarJuego());
    }

    private static void iniciarJuego() {
        try {
            RepositorioCatalogo repositorioCatalogo = new RepositorioCatalogo(ARCHIVO_CATALOGO);
            RepositorioPartidas repositorioPartidas = new RepositorioPartidas(DIRECTORIO_PARTIDAS);
            GeneradorMision generadorMision = new GeneradorMision();

            Partida partidaInicial = crearPartidaInicial();

            ControladorJuego.CreadorPartida creadorPartida = nombreComandante -> crearNuevaPartida(nombreComandante, repositorioCatalogo, generadorMision);
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

    private static Partida crearNuevaPartida(String nombreComandante, RepositorioCatalogo repositorioCatalogo, GeneradorMision generadorMision) {
        CatalogoComponentes catalogo = repositorioCatalogo.cargar();

        if (catalogo.listar().isEmpty()) {
            throw new IllegalStateException("El catálogo está vacío. Abra primero el administrador, cree configuraciones y guarde el catálogo.");
        }

        Escuadron escuadron = new Escuadron();
        NucleoOxigeno nucleo = crearNucleo();
        Tablero tablero = new Tablero(nucleo);
        Mision misionInicial = new Mision(1, escuadron.getCapacidadTotal(), Collections.emptyList(), List.of(nucleo), false);
        Partida partida = new Partida(nombreComandante, escuadron, tablero, misionInicial, catalogo);

        generadorMision.generar(partida);

        return partida;
    }

    private static NucleoOxigeno crearNucleo() {
        ImagenesEstado imagenesNucleo = new ImagenesEstado("assets/importados/Nucleo.png", "assets/importados/Nucleo.png", "assets/importados/Nucleo.png");

        return new NucleoOxigeno("NUCLEO", "Núcleo de oxígeno", 100.0, imagenesNucleo, null);
    }
}