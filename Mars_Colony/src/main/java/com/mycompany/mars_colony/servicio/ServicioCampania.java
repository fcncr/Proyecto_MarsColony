package com.mycompany.mars_colony.servicio;

import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;

public class ServicioCampania {

    private static final int BONIFICACION_CAPACIDAD = 5;

    public boolean registrarVictoria(Partida partida) {

        validarPartida(partida);

        if (partida.isCampaniaFinalizada()) {
            return false;
        }

        Mision mision = partida.getMision();

        if (mision == null) {
            return false;
        }

        if (mision.getEstado() != EstadoMision.VICTORIA) {
            return false;
        }

        if (mision.getNumero() != partida.getMisionActual()) {
            return false;
        }

        boolean victoriaNueva =
                partida.registrarMisionSuperada(
                        mision.getNumero()
                );

        if (victoriaNueva) {

            partida.getEscuadron().aumentarCapacidad(
                    BONIFICACION_CAPACIDAD
            );
        }

        return victoriaNueva;
    }

    public boolean avanzar(Partida partida) {
        validarPartida(partida);

        if (!puedeAvanzar(partida)) {
            return false;
        }

        int actual = partida.getMisionActual();
        int siguiente;

        if (actual < partida.getTotalMisiones()) {
            siguiente = actual + 1;
        } else {
            siguiente = generarNumeroMisionExtra(partida);
        }

        partida.setMisionActual(siguiente);

        return true;
    }

    public boolean repetirActual(Partida partida) {
        validarPartida(partida);

        if (!puedeRepetir(partida)) {
            return false;
        }

        partida.getMision().prepararRepeticion();

        return true;
    }

    public boolean puedeFinalizar(Partida partida) {

        validarPartida(partida);

        if (partida.isCampaniaFinalizada()) {
            return false;
        }

        return partida.puedeFinalizarCampania();
    }

    public boolean finalizarCampania(Partida partida) {

        validarPartida(partida);

        if (!puedeFinalizar(partida)) {
            return false;
        }

        return partida.finalizarCampania();
    }

    public int generarNumeroMisionExtra(Partida partida) {

        validarPartida(partida);

        if (partida.isCampaniaFinalizada()) {
            throw new IllegalStateException(
                    "La campania ya fue finalizada."
            );
        }

        if (!partida.puedeFinalizarCampania()) {
            throw new IllegalStateException(
                    "Primero deben completarse las 10 misiones iniciales."
            );
        }

        int primeraExtra =
                partida.getTotalMisiones() + 1;

        int siguiente =
                partida.getMisionActual() + 1;

        return Math.max(
                primeraExtra,
                siguiente
        );
    }

    private void validarPartida(Partida partida) {

        if (partida == null) {
            throw new IllegalArgumentException(
                    "La partida no puede ser null."
            );
        }
    }
    
    public boolean puedeRepetir(Partida partida) {
        validarPartida(partida);

        if (partida.isCampaniaFinalizada()) {
            return false;
        }

        Mision mision = partida.getMision();

        if (mision == null) {
            return false;
        }

        if (mision.getNumero() != partida.getMisionActual()) {
            return false;
        }

        return mision.getEstado() == EstadoMision.VICTORIA || mision.getEstado() == EstadoMision.DERROTA;
    }

    public boolean puedeAvanzar(Partida partida) {
        validarPartida(partida);

        if (partida.isCampaniaFinalizada()) {
            return false;
        }

        int actual = partida.getMisionActual();

        if (!partida.fueMisionSuperada(actual)) {
            return false;
        }

        if (actual < partida.getTotalMisiones()) {
            return true;
        }

        return partida.puedeFinalizarCampania();
    }
}