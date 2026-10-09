package com.mycompany.mars_colony.generacion;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.FabricaComponentes;
import com.mycompany.mars_colony.modelo.mapa.Casilla;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.servicio.ServicioProgresion;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

public class GeneradorMision {
    private static final Set<TipoComponente> TIPOS_CRIATURA = EnumSet.of(TipoComponente.ACECHADOR, TipoComponente.ESCUPIDOR, TipoComponente.DEMOLEDOR, TipoComponente.VOLADOR, TipoComponente.ENJAMBRE);
    private final FabricaComponentes fabrica;
    private final CatalogoComponentes catalogo;
    private final ServicioProgresion progresion;

    public GeneradorMision(FabricaComponentes fabrica, CatalogoComponentes catalogo) {
        this(fabrica, catalogo, new ServicioProgresion());
    }

    public GeneradorMision(FabricaComponentes fabrica, CatalogoComponentes catalogo, ServicioProgresion progresion) {
        if (fabrica == null) {
            throw new IllegalArgumentException("La fábrica de componentes no puede ser nula.");
        }

        if (catalogo == null) {
            throw new IllegalArgumentException("El catálogo de componentes no puede ser nulo.");
        }

        if (progresion == null) {
            throw new IllegalArgumentException("El servicio de progresión no puede ser nulo.");
        }

        this.fabrica = fabrica;
        this.catalogo = catalogo;
        this.progresion = progresion;
    }
    
    public List<ConfiguracionComponente> obtenerCriaturasDisponibles(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);
        CatalogoComponentes snapshot = obtenerCatalogoSnapshot(partida);
        return filtrarCriaturas(snapshot, numeroMision);
    }

    private List<ConfiguracionComponente> filtrarCriaturas(CatalogoComponentes fuente, int numeroMision) {
        List<ConfiguracionComponente> disponibles = fuente.disponibles(numeroMision);
        List<ConfiguracionComponente> criaturas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : disponibles) {
            if (TIPOS_CRIATURA.contains(configuracion.getTipo())) {
                criaturas.add(configuracion);
            }
        }

        return Collections.unmodifiableList(criaturas);
    }
    
    private CatalogoComponentes obtenerCatalogoSnapshot(Partida partida) {
        if (!partida.tieneCatalogoSnapshot()) {
            partida.establecerCatalogoSnapshot(catalogo);
        }

        CatalogoComponentes snapshot = partida.getCatalogoSnapshot();

        if (snapshot == null) {
            throw new IllegalStateException("No se pudo obtener el catálogo snapshot de la partida.");
        }

        return snapshot;
    }
    
    public List<ConfiguracionComponente> seleccionarConfiguracionesEnemigas(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);

        int presupuesto = partida.getEscuadron().getCapacidadTotal();
        List<ConfiguracionComponente> candidatas = obtenerCriaturasDisponibles(partida, numeroMision);
        List<ConfiguracionComponente> combinacion = buscarCombinacionExacta(candidatas, presupuesto);

        if (combinacion == null) {
            throw new IllegalStateException("No existe una combinación de criaturas que complete exactamente la capacidad enemiga de " + presupuesto + ".");
        }

        return Collections.unmodifiableList(combinacion);
    }
    
    public List<Criatura> instanciarCriaturasEnemigas(Partida partida, int numeroMision) {
        List<ConfiguracionComponente> seleccion = seleccionarConfiguracionesEnemigas(partida, numeroMision);
        List<Criatura> criaturas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : seleccion) {
            ComponenteCombate componente = fabrica.crear(configuracion);

            if (!(componente instanceof Criatura criatura)) {
                throw new IllegalStateException("La configuración " + configuracion.getId() + " no produjo una criatura.");
            }

            criaturas.add(criatura);
        }
        
        progresion.aplicarCrecimiento(partida.getEscuadron().getDefensas(), numeroMision, partida.getAzar());
        progresion.aplicarCrecimiento(criaturas, numeroMision, partida.getAzar());
        return Collections.unmodifiableList(criaturas);
    }
    
    public List<Criatura> distribuirCriaturasEnPosicionesValidas(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);

        if (partida.getAzar() == null) {
            throw new IllegalStateException("La partida no tiene un generador aleatorio disponible.");
        }

        List<Criatura> criaturas = instanciarCriaturasEnemigas(partida, numeroMision);
        Tablero tablero = partida.getTablero();
        List<Posicion> posiciones = obtenerEntradasExterioresLibres(tablero);

        if (posiciones.size() < criaturas.size()) {
            throw new IllegalStateException("No hay suficientes posiciones exteriores libres para colocar todas las criaturas.");
        }

        Collections.shuffle(posiciones, partida.getAzar());

        List<Criatura> colocadas = new ArrayList<>();

        for (int indice = 0; indice < criaturas.size(); indice++) {
            Criatura criatura = criaturas.get(indice);
            Posicion posicion = posiciones.get(indice);

            if (!tablero.colocar(criatura, posicion)) {
                revertirColocaciones(tablero, colocadas);
                throw new IllegalStateException("No se pudo colocar una criatura en la posición exterior " + posicion + ".");
            }

            colocadas.add(criatura);
        }

        return Collections.unmodifiableList(colocadas);
    }
    
    private List<Posicion> obtenerEntradasExterioresLibres(Tablero tablero) {
        List<Posicion> libres = new ArrayList<>();

        for (Posicion posicion : tablero.posicionesExteriores()) {
            Casilla casilla = tablero.getCasilla(posicion);

            if (casilla != null && casilla.estaLibre()) {
                libres.add(posicion);
            }
        }

        return libres;
    }
    
    private void revertirColocaciones(Tablero tablero, List<Criatura> colocadas) {
        for (Criatura criatura : colocadas) {
            Posicion posicion = criatura.getPosicion();

            if (posicion != null && tablero.obtener(posicion) == criatura) {
                tablero.retirar(posicion);
            }

            criatura.setPosicion(null);
        }
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
        if (partida.getAzar() == null) {
            throw new IllegalStateException("La partida no tiene un generador aleatorio disponible.");
        }
    }

    public Mision generar(Partida partida, int numeroMision) {
        validarContexto(partida, numeroMision);

        List<Criatura> criaturas = distribuirCriaturasEnPosicionesValidas(partida, numeroMision);

        try {
            int capacidadEnemiga = partida.getEscuadron().getCapacidadTotal();
            List<ComponenteCombate> participantes = construirParticipantes(partida, criaturas);
            Mision misionPreparada = new Mision(numeroMision, capacidadEnemiga, criaturas, participantes, true);

            partida.setMision(misionPreparada);

            return misionPreparada;
        } catch (RuntimeException e) {
            revertirColocaciones(partida.getTablero(), criaturas);
            throw e;
        }
    }
    
    private NucleoOxigeno obtenerNucleo(Tablero tablero) {
        Posicion centro = new Posicion(tablero.getFilas() / 2, tablero.getColumnas() / 2);

        if (!(tablero.obtener(centro) instanceof NucleoOxigeno nucleo)) {
            throw new IllegalStateException("El tablero no contiene el núcleo de oxígeno en su posición central.");
        }

        return nucleo;
    }
    
    private List<ComponenteCombate> construirParticipantes(Partida partida, List<Criatura> criaturas) {
        List<ComponenteCombate> participantes = new ArrayList<>();

        NucleoOxigeno nucleo = obtenerNucleo(partida.getTablero());
        participantes.add(nucleo);

        for (Defensa defensa : partida.getEscuadron().getDefensas()) {
            if (defensa != null && partida.getEscuadron().getSeleccionadas().contains(defensa.getId())) {
                participantes.add(defensa);
            }
        }

        participantes.addAll(criaturas);

        return participantes;
    }
}