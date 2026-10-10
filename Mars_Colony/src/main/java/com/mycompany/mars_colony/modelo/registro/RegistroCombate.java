package com.mycompany.mars_colony.modelo.registro;

import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegistroCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, ResumenInteraccion> objetivosAtacados;
    private Map<String, ResumenInteraccion> atacantesRecibidos;
    private double vidaInicial;
    private double vidaFinal;
    private double danio;
    private double frecuencia;
    private String tipo;
    private Posicion posicionFinal;
    private boolean cerrado;

    public RegistroCombate() {
        objetivosAtacados = new HashMap<>();
        atacantesRecibidos = new HashMap<>();
        cerrado = false;
    }

    public synchronized void registrarAtaqueRealizado(String idObjetivo, String nombreObjetivo, double danio) {
        if (idObjetivo == null || danio <= 0 || cerrado) {
            return;
        }

        ResumenInteraccion resumen = objetivosAtacados.get(idObjetivo);

        if (resumen == null) {
            resumen = new ResumenInteraccion(idObjetivo, nombreObjetivo);
            objetivosAtacados.put(idObjetivo, resumen);
        }

        resumen.registrarGolpe(danio);
    }

    public synchronized void registrarAtaqueRecibido(String idAtacante, String nombreAtacante, double danio) {
        if (idAtacante == null || danio <= 0 || cerrado) {
            return;
        }

        ResumenInteraccion resumen = atacantesRecibidos.get(idAtacante);

        if (resumen == null) {
            resumen = new ResumenInteraccion(idAtacante, nombreAtacante);
            atacantesRecibidos.put(idAtacante, resumen);
        }

        resumen.registrarGolpe(danio);
    }

    public synchronized void cerrar(double vidaInicial, double vidaFinal, double danio, double frecuencia, String tipo, Posicion posicionFinal) {
        if (cerrado) {
            return;
        }

        this.vidaInicial = vidaInicial;
        this.vidaFinal = vidaFinal;
        this.danio = danio;
        this.frecuencia = frecuencia;
        this.tipo = tipo;
        this.posicionFinal = posicionFinal == null ? null : new Posicion(posicionFinal.getFila(), posicionFinal.getColumna());
        this.cerrado = true;
    }

    public synchronized Map<String, ResumenInteraccion> getObjetivosAtacados() {
        return copiarMapa(objetivosAtacados);
    }

    public synchronized Map<String, ResumenInteraccion> getAtacantesRecibidos() {
        return copiarMapa(atacantesRecibidos);
    }

    public synchronized int getCantidadGolpesRealizados() {
        return contarGolpes(objetivosAtacados);
    }

    public synchronized int getCantidadGolpesRecibidos() {
        return contarGolpes(atacantesRecibidos);
    }

    public synchronized double getDanioCausadoTotal() {
        return sumarDanio(objetivosAtacados);
    }

    public synchronized double getDanioRecibidoTotal() {
        return sumarDanio(atacantesRecibidos);
    }

    public synchronized double getVidaInicial() {
        return vidaInicial;
    }

    public synchronized double getVidaFinal() {
        return vidaFinal;
    }

    public synchronized double getDanio() {
        return danio;
    }

    public synchronized double getFrecuencia() {
        return frecuencia;
    }

    public synchronized String getTipo() {
        return tipo;
    }

    public synchronized Posicion getPosicionFinal() {
        return posicionFinal == null ? null : new Posicion(posicionFinal.getFila(), posicionFinal.getColumna());
    }

    public synchronized boolean isCerrado() {
        return cerrado;
    }

    private Map<String, ResumenInteraccion> copiarMapa(Map<String, ResumenInteraccion> origen) {
        Map<String, ResumenInteraccion> copia = new HashMap<>();

        for (Map.Entry<String, ResumenInteraccion> entrada : origen.entrySet()) {
            if (entrada.getValue() != null) {
                copia.put(entrada.getKey(), entrada.getValue().copiar());
            }
        }

        return Collections.unmodifiableMap(copia);
    }

    private int contarGolpes(Map<String, ResumenInteraccion> interacciones) {
        int total = 0;

        for (ResumenInteraccion resumen : interacciones.values()) {
            if (resumen != null) {
                total += resumen.getCantidadGolpes();
            }
        }

        return total;
    }

    private double sumarDanio(Map<String, ResumenInteraccion> interacciones) {
        double total = 0;

        for (ResumenInteraccion resumen : interacciones.values()) {
            if (resumen != null) {
                total += resumen.getDanioTotal();
            }
        }

        return total;
    }
}