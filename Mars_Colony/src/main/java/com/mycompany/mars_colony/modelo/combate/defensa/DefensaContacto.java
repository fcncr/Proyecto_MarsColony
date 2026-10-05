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

public class DefensaContacto extends DefensaActiva {

    public DefensaContacto(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
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
                    && getPosicion().esAdyacente(candidato.getPosicion())) {

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

        if (!puedeEjecutarAtaque()) {
            return;
        }

        ComponenteCombate objetivo = objetivos.get(0);

        if (objetivo.getPosicion() != null
                && getPosicion() != null
                && getPosicion().esAdyacente(objetivo.getPosicion())) {

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

        if (!objetivos.isEmpty()) {
            atacar(motor, objetivos);
        }
    }
}