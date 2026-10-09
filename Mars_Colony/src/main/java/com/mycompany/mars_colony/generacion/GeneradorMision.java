package com.mycompany.mars_colony.generacion;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.FabricaComponentes;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

public class GeneradorMision {
    private static final Set<TipoComponente> TIPOS_CRIATURA = EnumSet.of(TipoComponente.ACECHADOR, TipoComponente.ESCUPIDOR, TipoComponente.DEMOLEDOR, TipoComponente.VOLADOR, TipoComponente.ENJAMBRE);
    private final FabricaComponentes fabrica;
    private final CatalogoComponentes catalogo;

    public GeneradorMision(FabricaComponentes fabrica, CatalogoComponentes catalogo) {
        if (fabrica == null) {
            throw new IllegalArgumentException("La fábrica de componentes no puede ser nula.");
        }

        if (catalogo == null) {
            throw new IllegalArgumentException("El catálogo de componentes no puede ser nulo.");
        }

        this.fabrica = fabrica;
        this.catalogo = catalogo;
    }
    
    public List<ConfiguracionComponente> obtenerCriaturasDisponibles(int numeroMision) {
        List<ConfiguracionComponente> disponibles = catalogo.disponibles(numeroMision);
        List<ConfiguracionComponente> criaturas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : disponibles) {
            if (TIPOS_CRIATURA.contains(configuracion.getTipo())) {
                criaturas.add(configuracion);
            }
        }

        return Collections.unmodifiableList(criaturas);
    }
    
    public List<ConfiguracionComponente> seleccionarConfiguracionesEnemigas(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);

        int presupuesto = partida.getEscuadron().getCapacidadTotal();
        List<ConfiguracionComponente> candidatas = obtenerCriaturasDisponibles(numeroMision);
        List<ConfiguracionComponente> combinacion = buscarCombinacionExacta(candidatas, presupuesto);

        if (combinacion == null) {
            throw new IllegalStateException("No existe una combinación de criaturas que complete exactamente la capacidad enemiga de " + presupuesto + ".");
        }

        return Collections.unmodifiableList(combinacion);
    }
    
    private List<ConfiguracionComponente> buscarCombinacionExacta(List<ConfiguracionComponente> candidatas, int presupuesto) {
        boolean[] alcanzable = new boolean[presupuesto + 1];
        ConfiguracionComponente[] usada = new ConfiguracionComponente[presupuesto + 1];
        int[] anterior = new int[presupuesto + 1];

        alcanzable[0] = true;

        for (int acumulado = 0; acumulado <= presupuesto; acumulado++) {
            if (!alcanzable[acumulado]) {
                continue;
            }

            for (ConfiguracionComponente candidata : candidatas) {
                int costo = candidata.getBase().getCostoCapacidad();

                if (costo <= 0) {
                    throw new IllegalStateException("Una criatura disponible tiene un costo de capacidad inválido.");
                }

                int siguiente = acumulado + costo;

                if (siguiente <= presupuesto && !alcanzable[siguiente]) {
                    alcanzable[siguiente] = true;
                    usada[siguiente] = candidata;
                    anterior[siguiente] = acumulado;
                }
            }
        }

        if (!alcanzable[presupuesto]) {
            return null;
        }

        List<ConfiguracionComponente> resultado = new ArrayList<>();
        int actual = presupuesto;

        while (actual > 0) {
            ConfiguracionComponente configuracion = usada[actual];

            if (configuracion == null) {
                throw new IllegalStateException("No se pudo reconstruir la combinación de criaturas.");
            }

            resultado.add(configuracion);
            actual = anterior[actual];
        }

        Collections.reverse(resultado);
        return resultado;
    }
    
    private void validarContexto(Partida partida, int numeroMision) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (numeroMision < 1) {
            throw new IllegalArgumentException("El número de misión debe ser mayor o igual a 1.");
        }

        if (numeroMision != partida.getMisionActual()) {
            throw new IllegalArgumentException("El número indicado debe coincidir con la misión actual de la partida.");
        }

        if (partida.getEscuadron() == null) {
            throw new IllegalStateException("La partida no tiene un escuadrón disponible.");
        }

        if (partida.getTablero() == null) {
            throw new IllegalStateException("La partida no tiene un tablero disponible.");
        }
    }

    public Mision generar(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);

        seleccionarConfiguracionesEnemigas(partida, numeroMision);

        int capacidadEnemiga = partida.getEscuadron().getCapacidadTotal();

        return new Mision(numeroMision, capacidadEnemiga, Collections.emptyList(), Collections.emptyList(), false);
    }
}