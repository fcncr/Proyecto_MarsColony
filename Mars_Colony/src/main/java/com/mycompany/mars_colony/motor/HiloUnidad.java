package com.mycompany.mars_colony.motor;

import com.mycompany.mars_colony.modelo.combate.UnidadActiva;

public class HiloUnidad implements Runnable {

    private static final double VELOCIDAD_BATALLA = 0.70;
    private static final long INTERVALO_ACTUALIZACION_MS = 100;

    private UnidadActiva unidad;
    private MotorBatalla motor;
    private Thread hilo;
    private volatile boolean cancelar;

    public HiloUnidad(UnidadActiva unidad, MotorBatalla motor) {
        if (unidad == null) {
            throw new IllegalArgumentException(
                    "La unidad no puede ser null."
            );
        }

        if (motor == null) {
            throw new IllegalArgumentException(
                    "El motor no puede ser null."
            );
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
        long tiempoAnterior =
                System.currentTimeMillis();

        while (!cancelar
                && motor.estaEnEjecucion()
                && unidad.estaOperativa()) {

            long tiempoActual =
                    System.currentTimeMillis();

            long dtReal =
                    tiempoActual - tiempoAnterior;

            tiempoAnterior =
                    tiempoActual;

            long dtSimulado =
                    Math.max(
                            1,
                            Math.round(
                                    dtReal
                                    * VELOCIDAD_BATALLA
                            )
                    );

            unidad.actualizarTiempos(
                    dtSimulado
            );

            unidad.ejecutarCiclo(
                    motor,
                    dtSimulado
            );

            try {
                Thread.sleep(
                        INTERVALO_ACTUALIZACION_MS
                );
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
        return hilo != null
                && hilo.isAlive();
    }
}