/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.partida;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Mision implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numero;
    private EstadoMision estado;
    private int capacidadEnemiga;

    private List<Criatura> criaturas;
    private List<ComponenteCombate> participantes;

    private boolean generada;

    public Mision(int numero, int capacidadEnemiga, List<Criatura> criaturas, List<ComponenteCombate> participantes, boolean generada) {
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
        this.estado = estado;
    }

    public int getCapacidadEnemiga() {
        return capacidadEnemiga;
    }

    public List<Criatura> getCriaturas() {
        return criaturas;
    }

    public List<ComponenteCombate> getParticipantes() {
        return participantes;
    }

    public boolean isGenerada() {
        return generada;
    }
    
    public void agregarParticipante(ComponenteCombate participante) {
        if (participante != null && !participantes.contains(participante)) {
            participantes.add(participante);
        }
    }
}