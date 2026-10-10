package com.mycompany.mars_colony.generacion;

import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneradorObstaculos {

    private static final int MIN_OBSTACULOS = 8;
    private static final int MAX_OBSTACULOS = 15;

    public void generar(Partida partida) {
        if (partida == null || partida.getTablero() == null || partida.getAzar() == null) {
            throw new IllegalArgumentException("La partida no contiene los datos necesarios para generar obstáculos.");
        }

        Tablero tablero = partida.getTablero();
        List<Posicion> candidatas = new ArrayList<>();

        for (int fila = 1; fila < tablero.getFilas() - 1; fila++) {
            for (int columna = 1; columna < tablero.getColumnas() - 1; columna++) {
                Posicion posicion = new Posicion(fila, columna);

                if (tablero.estaLibre(posicion)) {
                    candidatas.add(posicion);
                }
            }
        }

        if (candidatas.isEmpty()) {
            return;
        }

        Collections.shuffle(candidatas, partida.getAzar());

        int cantidad = MIN_OBSTACULOS + partida.getAzar().nextInt(MAX_OBSTACULOS - MIN_OBSTACULOS + 1);
        cantidad = Math.min(cantidad, candidatas.size());

        for (int i = 0; i < cantidad; i++) {
            Posicion posicion = candidatas.get(i);
            Obstaculo obstaculo = new Obstaculo("OBS-" + (i + 1), posicion);

            if (!tablero.colocar(obstaculo, posicion)) {
                throw new IllegalStateException("No fue posible colocar el obstáculo en " + posicion + ".");
            }
        }
    }
}