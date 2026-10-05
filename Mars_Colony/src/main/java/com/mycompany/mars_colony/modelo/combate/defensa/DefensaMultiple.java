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

public class DefensaMultiple extends DefensaActiva {

    public DefensaMultiple(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        List<ComponenteCombate> seleccionados = new ArrayList<>();

        if (motor == null || !estaOperativa() || getPosicion() == null) {
            return seleccionados;
        }

        List<ComponenteCombate> candidatos = motor.buscarObjetivos(this);

        int maxObjetivos = Math.max(
                1,
                getEstadisticas().getMaxObjetivos()
        );

        for (ComponenteCombate candidato : candidatos) {

            if (!(candidato instanceof Criatura)) {
                continue;
            }

            if (candidato.getPosicion() == null) {
                continue;
            }

            double distancia = getPosicion().distanciaA(
                    candidato.getPosicion()
            );

            if (distancia <= getAlcance()) {
                seleccionados.add(candidato);
            }

            if (seleccionados.size() >= maxObjetivos) {
                break;
            }
        }

        return seleccionados;
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
        if (motor == null || objetivos == null || objetivos.isEmpty() || !estaOperativa()) {
            return;
        }

        if (!puedeEjecutarAtaque()) {
            return;
        }

        int cantidadAtaques = Math.max(
                1,
                getEstadisticas().getCantidadAtaques()
        );

        int indiceObjetivo = 0;
        boolean realizoAtaque = false;

        for (int ataque = 0; ataque < cantidadAtaques; ataque++) {

            if (!estaOperativa()) {
                break;
            }

            ComponenteCombate objetivo = obtenerSiguienteObjetivoOperativo(
                    objetivos,
                    indiceObjetivo
            );

            if (objetivo == null) {
                break;
            }

            indiceObjetivo = objetivos.indexOf(objetivo) + 1;

            if (indiceObjetivo >= objetivos.size()) {
                indiceObjetivo = 0;
            }

            if (objetivo.getPosicion() == null || getPosicion() == null) {
                continue;
            }

            double distancia = getPosicion().distanciaA(
                    objetivo.getPosicion()
            );

            if (distancia <= getAlcance()) {

                double danioEfectivo = motor.aplicarAtaque(
                        this,
                        objetivo
                );

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

            int indice = (inicio + i) % objetivos.size();

            ComponenteCombate objetivo = objetivos.get(indice);

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
        }
    }
}