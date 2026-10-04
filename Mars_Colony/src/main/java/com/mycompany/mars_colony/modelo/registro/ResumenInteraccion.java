/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.registro;

/**
 *
 * @author fabic
 */
public class ResumenInteraccion {
    private String idUnidad;
    private String nombre;
    private int cantidadGolpes;
    private double danioTotal;

    
    public ResumenInteraccion(String idUnidad, String nombre) {
        this.idUnidad = idUnidad;
        this.nombre = nombre;
        this.cantidadGolpes = 0;
        this.danioTotal = 0;
    }
    
    public String getIdUnidad() {
        return idUnidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadGolpes() {
        return cantidadGolpes;
    }

    public double getDanioTotal() {
        return danioTotal;
    }
    
    public void registrarGolpe(double danio) {
        cantidadGolpes++;
        danioTotal += danio;
    }


}
