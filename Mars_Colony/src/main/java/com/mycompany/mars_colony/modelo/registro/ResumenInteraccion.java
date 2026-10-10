package com.mycompany.mars_colony.modelo.registro;

import java.io.Serializable;

public class ResumenInteraccion implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public synchronized String getIdUnidad() {
        return idUnidad;
    }

    public synchronized String getNombre() {
        return nombre;
    }

    public synchronized int getCantidadGolpes() {
        return cantidadGolpes;
    }

    public synchronized double getDanioTotal() {
        return danioTotal;
    }

    public synchronized void registrarGolpe(double danio) {
        if (danio <= 0) {
            return;
        }

        cantidadGolpes++;
        danioTotal += danio;
    }

    public synchronized ResumenInteraccion copiar() {
        ResumenInteraccion copia = new ResumenInteraccion(idUnidad, nombre);
        copia.cantidadGolpes = cantidadGolpes;
        copia.danioTotal = danioTotal;
        return copia;
    }
}