package com.mycompany.mars_colony.motor;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.criatura.Demoledor;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class NavegadorBatalla {

    private static final int[][] DIRECCIONES = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    private NavegadorBatalla() {
    }

    public static ComponenteCombate buscarObjetivoTerrestreAlcanzable(Tablero tablero, ComponenteCombate atacante, List<ComponenteCombate> candidatos) {
        if (tablero == null || atacante == null || candidatos == null || atacante.getPosicion() == null) {
            return null;
        }

        ComponenteCombate mejorObjetivo = null;
        int mejorDistanciaRuta = Integer.MAX_VALUE;
        double mejorDistanciaDirecta = Double.MAX_VALUE;

        for (ComponenteCombate candidato : candidatos) {
            if (candidato == null || candidato.getPosicion() == null || !candidato.estaOperativo()) {
                continue;
            }

            List<Posicion> ruta = calcularRutaTerrestre(tablero, atacante, candidato);

            if (ruta.isEmpty()) {
                continue;
            }

            int distanciaRuta = ruta.size() - 1;
            double distanciaDirecta = atacante.getPosicion().distanciaA(candidato.getPosicion());

            if (distanciaRuta < mejorDistanciaRuta || distanciaRuta == mejorDistanciaRuta && distanciaDirecta < mejorDistanciaDirecta) {
                mejorObjetivo = candidato;
                mejorDistanciaRuta = distanciaRuta;
                mejorDistanciaDirecta = distanciaDirecta;
            }
        }

        return mejorObjetivo;
    }

    public static List<Posicion> calcularRutaTerrestre(Tablero tablero, ComponenteCombate atacante, ComponenteCombate objetivo) {
        if (tablero == null || atacante == null || objetivo == null || atacante.getPosicion() == null || objetivo.getPosicion() == null) {
            return Collections.emptyList();
        }

        Posicion inicio = atacante.getPosicion();

        if (puedeAtacarDesde(atacante, inicio, objetivo)) {
            return List.of(inicio);
        }

        Queue<Posicion> pendientes = new ArrayDeque<>();
        Set<Posicion> visitadas = new HashSet<>();
        Map<Posicion, Posicion> anterior = new HashMap<>();

        pendientes.add(inicio);
        visitadas.add(inicio);

        while (!pendientes.isEmpty()) {
            Posicion actual = pendientes.remove();

            for (Posicion vecino : obtenerVecinos(tablero, actual)) {
                if (visitadas.contains(vecino) || !tablero.estaLibre(vecino)) {
                    continue;
                }

                visitadas.add(vecino);
                anterior.put(vecino, actual);

                if (puedeAtacarDesde(atacante, vecino, objetivo)) {
                    return reconstruirRuta(anterior, inicio, vecino);
                }

                pendientes.add(vecino);
            }
        }

        return Collections.emptyList();
    }

    public static Posicion buscarSiguientePasoAereo(Tablero tablero, ComponenteCombate atacante, ComponenteCombate objetivo) {
        if (tablero == null || atacante == null || objetivo == null || atacante.getPosicion() == null || objetivo.getPosicion() == null) {
            return null;
        }

        Posicion actual = atacante.getPosicion();
        Posicion destino = objetivo.getPosicion();
        double distanciaActual = actual.distanciaA(destino);
        Posicion mejor = null;
        double mejorDistancia = distanciaActual;

        for (Posicion vecino : obtenerVecinos(tablero, actual)) {
            if (!tablero.estaLibre(vecino)) {
                continue;
            }

            double distancia = vecino.distanciaA(destino);

            if (distancia < mejorDistancia) {
                mejor = vecino;
                mejorDistancia = distancia;
            }
        }

        return mejor;
    }

    private static boolean puedeAtacarDesde(ComponenteCombate atacante, Posicion desde, ComponenteCombate objetivo) {
        if (atacante == null || desde == null || objetivo == null || objetivo.getPosicion() == null) {
            return false;
        }

        if (atacante instanceof Demoledor) {
            return desde.esAdyacente(objetivo.getPosicion());
        }

        if (atacante.getAlcance() == 1) {
            return desde.esAdyacente(objetivo.getPosicion());
        }

        return desde.distanciaA(objetivo.getPosicion()) <= atacante.getAlcance();
    }

    private static List<Posicion> obtenerVecinos(Tablero tablero, Posicion posicion) {
        List<Posicion> vecinos = new ArrayList<>(8);

        for (int[] direccion : DIRECCIONES) {
            Posicion vecino = new Posicion(posicion.getFila() + direccion[0], posicion.getColumna() + direccion[1]);

            if (tablero.estaDentro(vecino)) {
                vecinos.add(vecino);
            }
        }

        return vecinos;
    }

    private static List<Posicion> reconstruirRuta(Map<Posicion, Posicion> anterior, Posicion inicio, Posicion destino) {
        List<Posicion> ruta = new ArrayList<>();
        Posicion actual = destino;

        while (actual != null) {
            ruta.add(actual);

            if (actual.equals(inicio)) {
                break;
            }

            actual = anterior.get(actual);
        }

        if (ruta.isEmpty() || !ruta.get(ruta.size() - 1).equals(inicio)) {
            return Collections.emptyList();
        }

        Collections.reverse(ruta);
        return ruta;
    }
}