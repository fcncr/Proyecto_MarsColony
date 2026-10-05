package com.mycompany.mars_colony.servicio;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
import java.util.Collection;
import java.util.Random;

public class ServicioProgresion {

    private static final double MIN_INCREMENTO = 0.05;
    private static final double MAX_INCREMENTO = 0.20;

    public void aplicarCrecimiento(
            Collection<? extends ComponenteCombate> componentes,
            int numeroMision,
            Random azar) {

        if (componentes == null) {
            throw new IllegalArgumentException(
                    "La coleccion de componentes no puede ser null."
            );
        }

        if (numeroMision < 1) {
            throw new IllegalArgumentException(
                    "El numero de mision debe ser mayor o igual a 1."
            );
        }

        if (azar == null) {
            throw new IllegalArgumentException(
                    "El generador aleatorio no puede ser null."
            );
        }

        for (ComponenteCombate componente : componentes) {

            if (componente == null) {
                continue;
            }

            if (componente instanceof NucleoOxigeno) {
                continue;
            }

            aplicarCrecimientoPendiente(
                    componente,
                    numeroMision,
                    azar
            );
        }
    }

    private void aplicarCrecimientoPendiente(
            ComponenteCombate componente,
            int numeroMision,
            Random azar) {

        if (numeroMision
                <= componente.getMisionEscaladaHasta()) {

            return;
        }

        int siguienteMision =
                componente.getMisionEscaladaHasta() + 1;

        while (siguienteMision <= numeroMision) {

            double porcentajeVida =
                    generarPorcentaje(azar);

            double porcentajeDanio =
                    generarPorcentaje(azar);

            aplicarIncrementos(
                    componente,
                    siguienteMision,
                    porcentajeVida,
                    porcentajeDanio
            );

            siguienteMision++;
        }
    }

    private double generarPorcentaje(Random azar) {

        return MIN_INCREMENTO
                + azar.nextDouble()
                * (MAX_INCREMENTO - MIN_INCREMENTO);
    }

    private void aplicarIncrementos(
            ComponenteCombate componente,
            int numeroMision,
            double porcentajeVida,
            double porcentajeDanio) {

        double vidaAnterior =
                componente.getVidaMaxima();

        double danioAnterior =
                componente.getDanioGolpe();

        double incrementoVida =
                vidaAnterior * porcentajeVida;

        double incrementoDanio =
                danioAnterior * porcentajeDanio;

        double vidaNueva =
                vidaAnterior + incrementoVida;

        double danioNuevo =
                danioAnterior + incrementoDanio;

        componente.setVidaMaxima(
                vidaNueva
        );

        componente.setVidaActual(
                vidaNueva
        );

        componente.setDanio(
                danioNuevo
        );

        RegistroCrecimiento registro =
                new RegistroCrecimiento(
                        numeroMision,
                        porcentajeVida,
                        porcentajeDanio,
                        vidaAnterior,
                        vidaNueva,
                        danioAnterior,
                        danioNuevo
                );

        componente.agregarRegistroCrecimiento(
                registro
        );

        componente.setNivel(
                componente.getNivel() + 1
        );
    }
}