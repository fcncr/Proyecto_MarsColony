package com.mycompany.mars_colony.modelo.combate.criatura;

import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.util.ArrayList;
import java.util.List;

public class Enjambre extends Criatura {

    public Enjambre(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> seleccionados = new ArrayList<>();

        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return seleccionados;
        }

        int maxObjetivos = Math.max(1, getEstadisticas().getMaxObjetivos());

        for (ComponenteCombate candidato : motor.buscarObjetivos(this)) {
            if (candidato != null && candidato.getPosicion() != null && getPosicion().distanciaA(candidato.getPosicion()) <= getAlcance()) {
                seleccionados.add(candidato);
            }

            if (seleccionados.size() >= maxObjetivos) {
                break;
            }
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
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa() || !puedeEjecutarAtaque()) {
            return;
        }

        int cantidadAtaques = Math.max(1, getEstadisticas().getCantidadAtaques());
        int indiceObjetivo = 0;
        boolean realizoAtaque = false;

        for (int ataque = 0; ataque < cantidadAtaques; ataque++) {
            if (!motor.estaEnEjecucion() || !estaOperativa()) {
                break;
            }

            ComponenteCombate objetivo = obtenerSiguienteObjetivoOperativo(objetivos, indiceObjetivo);

            if (objetivo == null) {
                break;
            }

            indiceObjetivo = objetivos.indexOf(objetivo) + 1;

            if (indiceObjetivo >= objetivos.size()) {
                indiceObjetivo = 0;
            }

            if (objetivo.getPosicion() != null && getPosicion() != null && getPosicion().distanciaA(objetivo.getPosicion()) <= getAlcance()) {
                double danioEfectivo = motor.aplicarAtaque(this, objetivo);

                if (danioEfectivo > 0) {
                    realizoAtaque = true;
                }
            }
        }

        if (realizoAtaque) {
            consumirAtaque();
        }
    }

    private ComponenteCombate obtenerSiguienteObjetivoOperativo(List<ComponenteCombate> objetivos, int inicio) {
        if (objetivos == null || objetivos.isEmpty()) {
            return null;
        }

        for (int i = 0; i < objetivos.size(); i++) {
            ComponenteCombate objetivo = objetivos.get((inicio + i) % objetivos.size());

            if (objetivo != null && objetivo.estaOperativo()) {
                return objetivo;
            }
        }

        return null;
    }

    @Override
    public void ejecutarCiclo(MotorBatalla motor, long dtMs) {
        if (motor == null || !motor.estaEnEjecucion() || !estaOperativa()) {
            return;
        }

        List<ComponenteCombate> objetivos = seleccionarObjetivos(motor);

        if (!objetivos.isEmpty()) {
            atacar(motor, objetivos);
        } else {
            mover(motor);
        }
    }
}