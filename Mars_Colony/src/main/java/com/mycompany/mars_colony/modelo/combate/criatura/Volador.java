package com.mycompany.mars_colony.modelo.combate.criatura;

import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.util.ArrayList;
import java.util.List;

public class Volador extends Criatura {

    public Volador(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public boolean esAereo() {
        return true;
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> seleccionados = new ArrayList<>();

        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return seleccionados;
        }

        List<ComponenteCombate> candidatos = motor.buscarObjetivos(this);

        ComponenteCombate masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (ComponenteCombate candidato : candidatos) {

            if (candidato == null || candidato.getPosicion() == null) {
                continue;
            }

            double distancia = getPosicion().distanciaA(
                    candidato.getPosicion()
            );

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = candidato;
            }
        }

        if (masCercano != null) {
            seleccionados.add(masCercano);
        }

        return seleccionados;
    }

    @Override
    public boolean mover(MotorBatalla motor) {
        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return false;
        }

        List<ComponenteCombate> objetivos = seleccionarObjetivos(motor);

        if (objetivos.isEmpty()) {
            return false;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (objetivo.getPosicion() == null) {
            return false;
        }

        Posicion actual = getPosicion();
        Posicion destino = objetivo.getPosicion();

        double distancia = actual.distanciaA(destino);

        if (distancia <= getAlcance()) {
            return false;
        }

        int cambioFila = Integer.compare(
                destino.getFila(),
                actual.getFila()
        );

        int cambioColumna = Integer.compare(
                destino.getColumna(),
                actual.getColumna()
        );

        Posicion siguiente = new Posicion(
                actual.getFila() + cambioFila,
                actual.getColumna() + cambioColumna
        );

        return motor.moverAereo(this, siguiente);
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa()) {
            return;
        }

        if (!puedeEjecutarAtaque()) {
            return;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (getPosicion() == null || objetivo.getPosicion() == null) {
            return;
        }

        double distancia = getPosicion().distanciaA(
                objetivo.getPosicion()
        );

        if (distancia <= getAlcance()) {

            double danioEfectivo = motor.aplicarAtaque(this, objetivo);

            if (danioEfectivo > 0) {
                consumirAtaque();
            }
        }
    }
    @Override
    public void ejecutarCiclo(MotorBatalla motor, long dtMs) {
        if (motor == null || !motor.estaEnEjecucion() || !estaOperativa()) {
            return;
        }

        List<ComponenteCombate> objetivos = seleccionarObjetivos(motor);

        if (objetivos.isEmpty()) {
            return;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (objetivo.getPosicion() == null || getPosicion() == null) {
            return;
        }

        double distancia = getPosicion().distanciaA(
                objetivo.getPosicion()
        );

        if (distancia <= getAlcance()) {
            atacar(motor, objetivos);
        } else {
            mover(motor);
        }
    }
}