package com.mycompany.mars_colony.modelo.combate.defensa;

import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.DefensaActiva;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.util.ArrayList;
import java.util.List;

public class DefensaImpacto extends DefensaActiva {

    public DefensaImpacto(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public boolean puedeAtacar(ComponenteCombate objetivo) {
        if (objetivo == null || objetivo == this) {
            return false;
        }

        if (!estaOperativo() || !objetivo.estaOperativo()) {
            return false;
        }

        if (objetivo.getBando() == getBando()) {
            return false;
        }

        if (objetivo.esAereo() && !getEstadisticas().isAtacaAereo()) {
            return false;
        }

        if (getPosicion() == null || objetivo.getPosicion() == null) {
            return false;
        }

        double distancia = getPosicion().distanciaA(objetivo.getPosicion());

        return distancia <= getRadioEfecto();
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> objetivos = new ArrayList<>();

        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return objetivos;
        }

        List<ComponenteCombate> candidatos = motor.buscarObjetivos(this);

        for (ComponenteCombate candidato : candidatos) {

            if (candidato instanceof Criatura && puedeAtacar(candidato)) {
                objetivos.add(candidato);
            }
        }

        return objetivos;
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa()) {
            return;
        }

        boolean detono = false;

        for (ComponenteCombate objetivo : objetivos) {

            if (objetivo != null && puedeAtacar(objetivo)) {
                motor.aplicarAtaque(this, objetivo);
                detono = true;
            }
        }

        if (detono) {
            motor.destruirComponente(this);
        }
    }

    @Override
    public void ejecutarCiclo(MotorBatalla motor, long dtMs) {
        if (motor == null || !motor.estaEnEjecucion() || !estaOperativa()) {
            return;
        }

        List<ComponenteCombate> objetivos = seleccionarObjetivos(motor);

        if (!objetivos.isEmpty()) {
            atacar(motor, objetivos);
        }
    }
}