package com.mycompany.mars_colony.servicio;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ValidarPresupuestoCampania {

    private static final int INCREMENTO_CAPACIDAD = 5;

    private static final Set<TipoComponente> TIPOS_CRIATURA = EnumSet.of(
            TipoComponente.ACECHADOR,
            TipoComponente.ESCUPIDOR,
            TipoComponente.DEMOLEDOR,
            TipoComponente.VOLADOR,
            TipoComponente.ENJAMBRE
    );

    public void validar(
            CatalogoComponentes catalogo,
            int capacidadInicial,
            int misionHasta,
            int maximoCriaturas) {

        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "El catálogo no puede ser nulo."
            );
        }

        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad inicial debe ser mayor que cero."
            );
        }

        if (misionHasta < 1) {
            throw new IllegalArgumentException(
                    "La misión máxima a validar debe ser al menos 1."
            );
        }

        if (maximoCriaturas <= 0) {
            throw new IllegalArgumentException(
                    "Debe existir al menos una posición disponible para criaturas."
            );
        }

        for (int mision = 1; mision <= misionHasta; mision++) {
            int capacidad = capacidadInicial
                    + ((mision - 1) * INCREMENTO_CAPACIDAD);

            List<Integer> costos = obtenerCostosCriaturas(
                    catalogo,
                    mision
            );

            if (costos.isEmpty()) {
                throw new IllegalStateException(
                        "No existen criaturas activas disponibles para la misión "
                        + mision + "."
                );
            }

            if (!puedeFormarPresupuestoExacto(
                    capacidad,
                    maximoCriaturas,
                    costos)) {

                throw new IllegalStateException(
                        "El catálogo no puede formar exactamente "
                        + capacidad
                        + " puntos de capacidad enemiga en la misión "
                        + mision
                        + ". Costos disponibles: "
                        + costos
                        + "."
                );
            }
        }
    }

    private List<Integer> obtenerCostosCriaturas(
            CatalogoComponentes catalogo,
            int mision) {

        Set<Integer> costos = new TreeSet<>();

        for (ConfiguracionComponente configuracion
                : catalogo.disponibles(mision)) {

            if (configuracion == null) {
                continue;
            }

            if (!TIPOS_CRIATURA.contains(
                    configuracion.getTipo())) {
                continue;
            }

            int costo = configuracion
                    .getBase()
                    .getCostoCapacidad();

            if (costo > 0) {
                costos.add(costo);
            }
        }

        return new ArrayList<>(costos);
    }

    private boolean puedeFormarPresupuestoExacto(
            int capacidad,
            int maximoCriaturas,
            List<Integer> costos) {

        boolean[][] alcanzable
                = new boolean[maximoCriaturas + 1][capacidad + 1];

        alcanzable[0][0] = true;

        for (int cantidad = 0;
                cantidad < maximoCriaturas;
                cantidad++) {

            for (int presupuesto = 0;
                    presupuesto <= capacidad;
                    presupuesto++) {

                if (!alcanzable[cantidad][presupuesto]) {
                    continue;
                }

                for (int costo : costos) {
                    int nuevoPresupuesto = presupuesto + costo;

                    if (nuevoPresupuesto > capacidad) {
                        continue;
                    }

                    alcanzable[cantidad + 1][nuevoPresupuesto] = true;
                }
            }
        }

        for (int cantidad = 1;
                cantidad <= maximoCriaturas;
                cantidad++) {

            if (alcanzable[cantidad][capacidad]) {
                return true;
            }
        }

        return false;
    }
}