/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate;

import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.motor.MotorBatalla;
import java.util.Collections;
import java.util.List;



public abstract class DefensaActiva extends Defensa implements UnidadActiva {

    public DefensaActiva(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        super(idConfiguracion, nombre, estadisticas, imagenes, misionMinima, posicion);
    }
    @Override
    public boolean estaOperativa() {
        return estaOperativo();
    }

    @Override
    public void ejecutarCiclo(MotorBatalla motor, long dtMs) {
    }

    @Override
    public List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor) {
        return Collections.emptyList();
    }

    @Override
    public void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos) {
    }
    
}