package com.mycompany.mars_colony.motor;

import com.mycompany.mars_colony.modelo.combate.UnidadActiva;

public class HiloUnidad implements Runnable {

    private UnidadActiva unidad;
    private MotorBatalla motor;
    private Thread hilo;
    private volatile boolean cancelar;

    public HiloUnidad(UnidadActiva unidad, MotorBatalla motor) {
        if (unidad == null) {
            throw new IllegalArgumentException("La unidad no puede ser null.");
        }

        if (motor == null) {
            throw new IllegalArgumentException("El motor no puede ser null.");
        }

        this.unidad = unidad;
        this.motor = motor;
        this.cancelar = false;
    }

    public void iniciar() {
        if (hilo != null && hilo.isAlive()) {
            return;
        }

        cancelar = false;

        hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {

        long tiempoAnterior = System.currentTimeMillis();

        while (!cancelar && motor.estaEnEjecucion() && unidad.estaOperativa()) {

            long tiempoActual = System.currentTimeMillis();
            long dtMs = tiempoActual - tiempoAnterior;
            tiempoAnterior = tiempoActual;

            unidad.actualizarTiempos(dtMs);

            unidad.ejecutarCiclo(motor, dtMs);

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

                if (cancelar) {
                    break;
                }

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detener() {
        cancelar = true;

        if (hilo != null) {
            hilo.interrupt();
        }
    }

    public void esperarFin() {
        if (hilo == null) {
            return;
        }

        try {
            hilo.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean estaActivo() {
        return hilo != null && hilo.isAlive();
    }
}