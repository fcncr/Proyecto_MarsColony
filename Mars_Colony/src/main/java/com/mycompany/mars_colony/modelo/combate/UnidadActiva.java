/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.mars_colony.modelo.combate;

import com.mycompany.mars_colony.motor.MotorBatalla;
import java.util.List;

public interface UnidadActiva {

    boolean estaOperativa();

    void ejecutarCiclo(MotorBatalla motor, long dtMs);

    List<ComponenteCombate> seleccionarObjetivos(MotorBatalla motor);

    void atacar(MotorBatalla motor, List<ComponenteCombate> objetivos);
    
    void actualizarTiempos(long dtMs);
}