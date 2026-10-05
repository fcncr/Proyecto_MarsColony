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

public class DefensaAlcance extends DefensaActiva {

    public DefensaAlcance(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> objetivos = new ArrayList<>();

        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return objetivos;
        }

        List<ComponenteCombate> candidatos = motor.buscarObjetivos(this);

        for (ComponenteCombate candidato : candidatos) {

            if (candidato instanceof Criatura
                    && candidato.getPosicion() != null
                    && getPosicion().distanciaA(candidato.getPosicion()) <= getAlcance()) {

                objetivos.add(candidato);
                break;
            }
        }

        return objetivos;
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa()) {
            return;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (getPosicion() != null
                && objetivo.getPosicion() != null
                && getPosicion().distanciaA(objetivo.getPosicion()) <= getAlcance()) {

            motor.aplicarAtaque(this, objetivo);
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