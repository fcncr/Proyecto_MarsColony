/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.registro;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author fabic
 */
public class RegistroCombate {
    private Map<String, ResumenInteraccion> objetivosAtacados;
    private Map<String, ResumenInteraccion> atacantesRecibidos;
    
    
    public RegistroCombate() {
        objetivosAtacados = new HashMap<>();
        atacantesRecibidos = new HashMap<>();
    }
    
    
    public void registrarAtaqueRealizado(
        String idObjetivo,
        String nombreObjetivo,
        double danio) {

        ResumenInteraccion resumen = objetivosAtacados.get(idObjetivo);

        if (resumen == null) {
            resumen = new ResumenInteraccion(idObjetivo,nombreObjetivo);
            objetivosAtacados.put(idObjetivo, resumen);
        }
        resumen.registrarGolpe(danio);
    }
}
