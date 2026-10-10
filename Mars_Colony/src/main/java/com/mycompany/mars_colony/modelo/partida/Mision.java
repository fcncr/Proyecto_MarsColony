package com.mycompany.mars_colony.modelo.partida;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mision implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numero;
    private EstadoMision estado;
    private int capacidadEnemiga;
    private List<Criatura> criaturas;
    private List<ComponenteCombate> participantes;
    private boolean generada;

    public Mision(int numero, int capacidadEnemiga, List<Criatura> criaturas, List<ComponenteCombate> participantes, boolean generada) {
        if (numero < 1) {
            throw new IllegalArgumentException("El número de misión debe ser mayor o igual a 1.");
        }

        if (capacidadEnemiga < 0) {
            throw new IllegalArgumentException("La capacidad enemiga no puede ser negativa.");
        }

        this.numero = numero;
        this.estado = EstadoMision.PREPARACION;
        this.capacidadEnemiga = capacidadEnemiga;
        this.criaturas = criaturas == null ? new ArrayList<>() : new ArrayList<>(criaturas);
        this.participantes = participantes == null ? new ArrayList<>() : new ArrayList<>(participantes);
        this.generada = generada;

        registrarParticipantes();
    }

    public void registrarParticipantes() {
        for (Criatura criatura : criaturas) {
            if (criatura != null && !participantes.contains(criatura)) {
                participantes.add(criatura);
            }
        }
    }

    public void agregarParticipante(ComponenteCombate participante) {
        if (participante != null && !participantes.contains(participante)) {
            participantes.add(participante);
        }
    }

    public boolean eliminarParticipante(ComponenteCombate participante) {
        if (participante == null) {
            return false;
        }

        return participantes.remove(participante);
    }

    public boolean todasCriaturasEliminadas() {
        for (Criatura criatura : criaturas) {
            if (criatura != null && criatura.estaOperativo()) {
                return false;
            }
        }

        return true;
    }

    public void cerrarRegistros() {
        for (ComponenteCombate participante : participantes) {
            if (participante == null) {
                continue;
            }

            participante.getRegistroCombate().cerrar(
                    participante.getVidaMaxima(),
                    participante.getVidaActual(),
                    participante.getDanioGolpe(),
                    participante.getFrecuenciaAtaque(),
                    participante.getClass().getSimpleName(),
                    participante.getPosicion()
            );
        }
    }

    public void prepararRepeticion() {
        for (ComponenteCombate participante : participantes) {
            if (participante != null) {
                participante.restablecerIntento();
            }
        }

        estado = EstadoMision.PREPARACION;
    }

    public int getNumero() {
        return numero;
    }

    public EstadoMision getEstado() {
        return estado;
    }

    public void setEstado(EstadoMision estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la misión no puede ser nulo.");
        }

        this.estado = estado;
    }

    public int getCapacidadEnemiga() {
        return capacidadEnemiga;
    }

    public List<Criatura> getCriaturas() {
        return Collections.unmodifiableList(new ArrayList<>(criaturas));
    }

    public List<ComponenteCombate> getParticipantes() {
        return Collections.unmodifiableList(new ArrayList<>(participantes));
    }

    public boolean isGenerada() {
        return generada;
    }
}