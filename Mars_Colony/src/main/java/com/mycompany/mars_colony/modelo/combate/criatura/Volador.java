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

        ComponenteCombate masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (ComponenteCombate candidato : motor.buscarObjetivos(this)) {
            if (candidato == null || candidato.getPosicion() == null) {
                continue;
            }

            double distancia = getPosicion().distanciaA(candidato.getPosicion());

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
        if (motor == null || !estaOperativa()) {
            return false;
        }

        List<ComponenteCombate> objetivos = seleccionarObjetivos(motor);

        if (objetivos.isEmpty()) {
            return false;
        }

        return motor.moverAereoHaciaObjetivo(this, objetivos.get(0));
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa() || !puedeEjecutarAtaque()) {
            return;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (getPosicion() != null && objetivo.getPosicion() != null && getPosicion().distanciaA(objetivo.getPosicion()) <= getAlcance()) {
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

        if (getPosicion() != null && objetivo.getPosicion() != null && getPosicion().distanciaA(objetivo.getPosicion()) <= getAlcance()) {
            atacar(motor, objetivos);
        } else {
            motor.moverAereoHaciaObjetivo(this, objetivo);
        }
    }
}