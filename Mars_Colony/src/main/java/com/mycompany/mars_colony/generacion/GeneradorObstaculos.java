package com.mycompany.mars_colony.generacion;

import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GeneradorObstaculos {

    private static final int MIN_OBSTACULOS = 8;
    private static final int MAX_OBSTACULOS = 15;

    private static final int[][] DIRECCIONES = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    public void generar(Partida partida) {
        if (partida == null
                || partida.getTablero() == null
                || partida.getAzar() == null) {
            throw new IllegalArgumentException(
                    "La partida no contiene los datos necesarios para generar obstáculos."
            );
        }

        Tablero tablero = partida.getTablero();
        Posicion posicionNucleo = buscarPosicionNucleo(tablero);

        if (posicionNucleo == null) {
            throw new IllegalStateException(
                    "No se encontró el núcleo de oxígeno en el tablero."
            );
        }

        List<Posicion> candidatas = obtenerCandidatas(
                tablero,
                posicionNucleo
        );

        if (candidatas.isEmpty()) {
            throw new IllegalStateException(
                    "No existen posiciones válidas para generar obstáculos."
            );
        }

        Collections.shuffle(candidatas, partida.getAzar());

        int cantidadObjetivo = MIN_OBSTACULOS
                + partida.getAzar().nextInt(
                        MAX_OBSTACULOS - MIN_OBSTACULOS + 1
                );

        cantidadObjetivo = Math.min(
                cantidadObjetivo,
                candidatas.size()
        );

        int colocados = 0;

        for (Posicion posicion : candidatas) {
            if (colocados >= cantidadObjetivo) {
                break;
            }

            Obstaculo obstaculo = new Obstaculo(
                    "OBS-" + (colocados + 1),
                    posicion
            );

            if (!tablero.colocar(obstaculo, posicion)) {
                continue;
            }

            if (!existeRutaHaciaNucleo(
                    tablero,
                    posicionNucleo)) {

                tablero.retirar(posicion);
                continue;
            }

            colocados++;
        }

        if (colocados < MIN_OBSTACULOS) {
            throw new IllegalStateException(
                    "No fue posible generar al menos "
                    + MIN_OBSTACULOS
                    + " obstáculos sin bloquear el acceso terrestre al núcleo."
            );
        }
    }

    private List<Posicion> obtenerCandidatas(
            Tablero tablero,
            Posicion posicionNucleo) {

        List<Posicion> candidatas = new ArrayList<>();

        for (int fila = 1;
                fila < tablero.getFilas() - 1;
                fila++) {

            for (int columna = 1;
                    columna < tablero.getColumnas() - 1;
                    columna++) {

                Posicion posicion = new Posicion(
                        fila,
                        columna
                );

                if (!tablero.estaLibre(posicion)) {
                    continue;
                }

                if (estaJuntoAlNucleo(
                        posicion,
                        posicionNucleo)) {
                    continue;
                }

                candidatas.add(posicion);
            }
        }

        return candidatas;
    }

    private boolean estaJuntoAlNucleo(
            Posicion posicion,
            Posicion nucleo) {

        int diferenciaFila = Math.abs(
                posicion.getFila() - nucleo.getFila()
        );

        int diferenciaColumna = Math.abs(
                posicion.getColumna() - nucleo.getColumna()
        );

        return diferenciaFila <= 1
                && diferenciaColumna <= 1;
    }

    private Posicion buscarPosicionNucleo(
            Tablero tablero) {

        for (int fila = 0;
                fila < tablero.getFilas();
                fila++) {

            for (int columna = 0;
                    columna < tablero.getColumnas();
                    columna++) {

                Posicion posicion = new Posicion(
                        fila,
                        columna
                );

                OcupanteMapa ocupante
                        = tablero.obtener(posicion);

                if (ocupante instanceof NucleoOxigeno) {
                    return posicion;
                }
            }
        }

        return null;
    }

    private boolean existeRutaHaciaNucleo(
            Tablero tablero,
            Posicion nucleo) {

        Set<Posicion> destinos
                = obtenerCasillasAdyacentesLibres(
                        tablero,
                        nucleo
                );

        if (destinos.isEmpty()) {
            return false;
        }

        Queue<Posicion> pendientes
                = new ArrayDeque<>();

        Set<Posicion> visitadas
                = new HashSet<>();

        agregarEntradasPerimetro(
                tablero,
                pendientes,
                visitadas
        );

        while (!pendientes.isEmpty()) {
            Posicion actual = pendientes.remove();

            if (destinos.contains(actual)) {
                return true;
            }

            for (int[] direccion : DIRECCIONES) {
                Posicion siguiente = new Posicion(
                        actual.getFila() + direccion[0],
                        actual.getColumna() + direccion[1]
                );

                if (!tablero.estaDentro(siguiente)) {
                    continue;
                }

                if (visitadas.contains(siguiente)) {
                    continue;
                }

                if (!tablero.estaLibre(siguiente)) {
                    continue;
                }

                visitadas.add(siguiente);
                pendientes.add(siguiente);
            }
        }

        return false;
    }

    private Set<Posicion> obtenerCasillasAdyacentesLibres(
            Tablero tablero,
            Posicion nucleo) {

        Set<Posicion> resultado = new HashSet<>();

        for (int fila = -1; fila <= 1; fila++) {
            for (int columna = -1;
                    columna <= 1;
                    columna++) {

                if (fila == 0 && columna == 0) {
                    continue;
                }

                Posicion posicion = new Posicion(
                        nucleo.getFila() + fila,
                        nucleo.getColumna() + columna
                );

                if (tablero.estaDentro(posicion)
                        && tablero.estaLibre(posicion)) {
                    resultado.add(posicion);
                }
            }
        }

        return resultado;
    }

    private void agregarEntradasPerimetro(
            Tablero tablero,
            Queue<Posicion> pendientes,
            Set<Posicion> visitadas) {

        for (Posicion posicion
                : tablero.posicionesExteriores()) {

            if (!tablero.estaLibre(posicion)) {
                continue;
            }

            if (visitadas.add(posicion)) {
                pendientes.add(posicion);
            }
        }
    }
}