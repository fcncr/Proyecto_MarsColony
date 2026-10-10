package com.mycompany.mars_colony.modelo.combate.criatura;

import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.util.ArrayList;
import java.util.List;

public class Demoledor extends Criatura {

    public Demoledor(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public boolean puedeAtacar(ComponenteCombate objetivo) {
        if (objetivo == null || objetivo == this || !estaOperativo() || !objetivo.estaOperativo() || objetivo.getBando() == getBando() || objetivo.esAereo() && !getEstadisticas().isAtacaAereo() || getPosicion() == null || objetivo.getPosicion() == null) {
            return false;
        }

        return getPosicion().distanciaA(objetivo.getPosicion()) <= getRadioEfecto();
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> seleccionados = new ArrayList<>();

        if (motor == null || !estaOperativa()) {
            return seleccionados;
        }

        ComponenteCombate objetivo = motor.buscarObjetivoTerrestreAlcanzable(this);

        if (objetivo != null) {
            seleccionados.add(objetivo);
        }

        return seleccionados;
    }

    @Override
    public boolean mover(MotorBatalla motor) {
        if (motor == null || !estaOperativa()) {
            return false;
        }

        ComponenteCombate objetivo = motor.buscarObjetivoTerrestreAlcanzable(this);
        return objetivo != null && motor.moverHaciaObjetivoTerrestre(this, objetivo);
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || !estaOperativa() || getPosicion() == null || !puedeEjecutarAtaque()) {
            return;
        }

        boolean exploto = false;

        for (ComponenteCombate candidato : motor.buscarObjetivos(this)) {
            if (candidato != null && candidato.getPosicion() != null && getPosicion().distanciaA(candidato.getPosicion()) <= getRadioEfecto()) {
                double danioEfectivo = motor.aplicarAtaque(this, candidato);

                if (danioEfectivo > 0) {
                    exploto = true;
                }
            }
        }

        if (exploto) {
            consumirAtaque();
            motor.destruirComponente(this);
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

        if (getPosicion() != null && objetivo.getPosicion() != null && getPosicion().esAdyacente(objetivo.getPosicion())) {
            atacar(motor, objetivos);
        } else {
            motor.moverHaciaObjetivoTerrestre(this, objetivo);
        }
    }
}