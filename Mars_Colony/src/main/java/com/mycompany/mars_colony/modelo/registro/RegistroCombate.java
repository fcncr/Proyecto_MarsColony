package com.mycompany.mars_colony.modelo.registro;

import com.mycompany.mars_colony.modelo.mapa.Posicion;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class RegistroCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, ResumenInteraccion> objetivosAtacados;
    private Map<String, ResumenInteraccion> atacantesRecibidos;

    // Datos finales del participante
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

    public void registrarAtaqueRealizado(
            String idObjetivo,
            String nombreObjetivo,
            double danio) {

        ResumenInteraccion resumen =
                objetivosAtacados.get(idObjetivo);

        if (resumen == null) {

            resumen = new ResumenInteraccion(
                    idObjetivo,
                    nombreObjetivo
            );

            objetivosAtacados.put(
                    idObjetivo,
                    resumen
            );
        }

        resumen.registrarGolpe(danio);
    }

    public void registrarAtaqueRecibido(
            String idAtacante,
            String nombreAtacante,
            double danio) {

        ResumenInteraccion resumen =
                atacantesRecibidos.get(idAtacante);

        if (resumen == null) {

            resumen = new ResumenInteraccion(
                    idAtacante,
                    nombreAtacante
            );

            atacantesRecibidos.put(
                    idAtacante,
                    resumen
            );
        }

        resumen.registrarGolpe(danio);
    }

    public void cerrar(
            double vidaInicial,
            double vidaFinal,
            double danio,
            double frecuencia,
            String tipo,
            Posicion posicionFinal) {

        if (cerrado) {
            return;
        }

        this.vidaInicial = vidaInicial;
        this.vidaFinal = vidaFinal;
        this.danio = danio;
        this.frecuencia = frecuencia;
        this.tipo = tipo;

        if (posicionFinal != null) {

            this.posicionFinal = new Posicion(
                    posicionFinal.getFila(),
                    posicionFinal.getColumna()
            );

        } else {

            this.posicionFinal = null;
        }

        this.cerrado = true;
    }

    public Map<String, ResumenInteraccion> getObjetivosAtacados() {
        return objetivosAtacados;
    }

    public Map<String, ResumenInteraccion> getAtacantesRecibidos() {
        return atacantesRecibidos;
    }

    public double getVidaInicial() {
        return vidaInicial;
    }

    public double getVidaFinal() {
        return vidaFinal;
    }

    public double getDanio() {
        return danio;
    }

    public double getFrecuencia() {
        return frecuencia;
    }

    public String getTipo() {
        return tipo;
    }

    public Posicion getPosicionFinal() {
        return posicionFinal;
    }

    public boolean isCerrado() {
        return cerrado;
    }
}