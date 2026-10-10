package com.mycompany.mars_colony.generacion;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.FabricaComponentes;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.servicio.ServicioProgresion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GeneradorMision {

    private static final Set<TipoComponente> TIPOS_CRIATURA = EnumSet.of(TipoComponente.ACECHADOR, TipoComponente.ESCUPIDOR, TipoComponente.DEMOLEDOR, TipoComponente.VOLADOR, TipoComponente.ENJAMBRE);

    private final FabricaComponentes fabrica;
    private final ServicioProgresion progresion;

    public GeneradorMision() {
        this(new FabricaComponentes(), new ServicioProgresion());
    }

    public GeneradorMision(FabricaComponentes fabrica, ServicioProgresion progresion) {
        if (fabrica == null) {
            throw new IllegalArgumentException("La fábrica de componentes no puede ser nula.");
        }

        if (progresion == null) {
            throw new IllegalArgumentException("El servicio de progresión no puede ser nulo.");
        }

        this.fabrica = fabrica;
        this.progresion = progresion;
    }

    public Mision generar(Partida partida) {
        validarPartida(partida);

        int numeroMision = partida.getMisionActual();
        Escuadron escuadron = partida.getEscuadron();
        Tablero tablero = partida.getTablero();
        CatalogoComponentes catalogo = partida.getCatalogoSnapshot();

        if (catalogo == null) {
            throw new IllegalStateException("La partida no contiene un catálogo de componentes.");
        }

        NucleoOxigeno nucleo = buscarNucleo(tablero);

        if (nucleo == null) {
            throw new IllegalStateException("No se encontró el núcleo de oxígeno en el tablero.");
        }

        List<ConfiguracionComponente> disponibles = catalogo.disponibles(numeroMision);
        CatalogoComponentes catalogoCriaturas = crearCatalogoCriaturas(disponibles);
        List<Posicion> exterioresLibres = obtenerPosicionesExterioresLibres(tablero);

        if (exterioresLibres.isEmpty()) {
            throw new IllegalStateException("No existen casillas exteriores libres para generar criaturas.");
        }

        int capacidadEnemiga = escuadron.getCapacidadTotal();
        List<Criatura> criaturas = completarPresupuesto(capacidadEnemiga, exterioresLibres.size(), catalogoCriaturas);

        if (criaturas.isEmpty()) {
            throw new IllegalStateException("No fue posible generar el ejército enemigo.");
        }

        List<ComponenteCombate> participantes = crearParticipantes(nucleo, escuadron, criaturas);

        prepararParticipantesNuevaMision(participantes);
        progresion.aplicarCrecimiento(participantes, numeroMision, partida.getAzar());
        colocarCriaturas(partida, tablero, criaturas, exterioresLibres);

        Mision mision = new Mision(numeroMision, capacidadEnemiga, criaturas, participantes, true);
        partida.setMision(mision);

        return mision;
    }

    public List<Criatura> completarPresupuesto(int capacidad, int maxCasillas, CatalogoComponentes catalogo) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad enemiga debe ser mayor que cero.");
        }

        if (maxCasillas <= 0) {
            throw new IllegalArgumentException("La cantidad máxima de casillas debe ser mayor que cero.");
        }

        if (catalogo == null) {
            throw new IllegalArgumentException("El catálogo no puede ser nulo.");
        }

        List<ConfiguracionComponente> candidatas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : catalogo.listar()) {
            if (configuracion != null && configuracion.isActivo() && esTipoCriatura(configuracion.getTipo())) {
                candidatas.add(configuracion);
            }
        }

        if (candidatas.isEmpty()) {
            throw new IllegalStateException("No existen configuraciones de criaturas disponibles.");
        }

        boolean[][] alcanzable = new boolean[maxCasillas + 1][capacidad + 1];
        int[][] presupuestoAnterior = new int[maxCasillas + 1][capacidad + 1];
        ConfiguracionComponente[][] configuracionUsada = new ConfiguracionComponente[maxCasillas + 1][capacidad + 1];

        alcanzable[0][0] = true;

        for (int cantidad = 0; cantidad < maxCasillas; cantidad++) {
            for (int presupuesto = 0; presupuesto <= capacidad; presupuesto++) {
                if (!alcanzable[cantidad][presupuesto]) {
                    continue;
                }

                for (ConfiguracionComponente configuracion : candidatas) {
                    int costo = configuracion.getBase().getCostoCapacidad();
                    int nuevoPresupuesto = presupuesto + costo;

                    if (costo <= 0 || nuevoPresupuesto > capacidad) {
                        continue;
                    }

                    if (!alcanzable[cantidad + 1][nuevoPresupuesto]) {
                        alcanzable[cantidad + 1][nuevoPresupuesto] = true;
                        presupuestoAnterior[cantidad + 1][nuevoPresupuesto] = presupuesto;
                        configuracionUsada[cantidad + 1][nuevoPresupuesto] = configuracion;
                    }
                }
            }
        }

        int cantidadElegida = -1;

        for (int cantidad = 1; cantidad <= maxCasillas; cantidad++) {
            if (alcanzable[cantidad][capacidad]) {
                cantidadElegida = cantidad;
                break;
            }
        }

        if (cantidadElegida < 0) {
            throw new IllegalStateException("No existe una combinación exacta de criaturas para la capacidad enemiga " + capacidad + ".");
        }

        List<ConfiguracionComponente> seleccionadas = reconstruirConfiguraciones(cantidadElegida, capacidad, presupuestoAnterior, configuracionUsada);
        List<Criatura> criaturas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : seleccionadas) {
            ComponenteCombate componente = fabrica.crear(configuracion);

            if (!(componente instanceof Criatura criatura)) {
                throw new IllegalStateException("La configuración " + configuracion.getId() + " no creó una criatura.");
            }

            criaturas.add(criatura);
        }

        return criaturas;
    }

    public void reajustarEjercito(Partida partida) {
        validarPartida(partida);

        Mision mision = partida.getMision();

        if (mision == null) {
            throw new IllegalStateException("La partida no tiene una misión que reajustar.");
        }

        progresion.aplicarCrecimiento(mision.getParticipantes(), partida.getMisionActual(), partida.getAzar());
    }

    private void prepararParticipantesNuevaMision(List<ComponenteCombate> participantes) {
        if (participantes == null) {
            throw new IllegalArgumentException("La lista de participantes no puede ser nula.");
        }

        for (ComponenteCombate participante : participantes) {
            if (participante != null) {
                participante.prepararNuevaMision();
            }
        }
    }

    private CatalogoComponentes crearCatalogoCriaturas(List<ConfiguracionComponente> configuraciones) {
        CatalogoComponentes resultado = new CatalogoComponentes();

        for (ConfiguracionComponente configuracion : configuraciones) {
            if (configuracion != null && configuracion.isActivo() && esTipoCriatura(configuracion.getTipo())) {
                resultado.crear(configuracion.copiar());
            }
        }

        if (resultado.listar().isEmpty()) {
            throw new IllegalStateException("No existen criaturas disponibles para la misión actual.");
        }

        return resultado;
    }

    private boolean esTipoCriatura(TipoComponente tipo) {
        return tipo != null && TIPOS_CRIATURA.contains(tipo);
    }

    private List<ConfiguracionComponente> reconstruirConfiguraciones(int cantidad, int capacidad, int[][] presupuestoAnterior, ConfiguracionComponente[][] configuracionUsada) {
        List<ConfiguracionComponente> resultado = new ArrayList<>();
        int cantidadActual = cantidad;
        int presupuestoActual = capacidad;

        while (cantidadActual > 0) {
            ConfiguracionComponente configuracion = configuracionUsada[cantidadActual][presupuestoActual];

            if (configuracion == null) {
                throw new IllegalStateException("No fue posible reconstruir el ejército enemigo.");
            }

            resultado.add(configuracion);
            presupuestoActual = presupuestoAnterior[cantidadActual][presupuestoActual];
            cantidadActual--;
        }

        Collections.reverse(resultado);
        return resultado;
    }

    private List<Posicion> obtenerPosicionesExterioresLibres(Tablero tablero) {
        List<Posicion> resultado = new ArrayList<>();

        for (Posicion posicion : tablero.posicionesExteriores()) {
            if (tablero.estaLibre(posicion)) {
                resultado.add(posicion);
            }
        }

        return resultado;
    }

    private void colocarCriaturas(Partida partida, Tablero tablero, List<Criatura> criaturas, List<Posicion> posicionesDisponibles) {
        List<Posicion> posiciones = new ArrayList<>(posicionesDisponibles);
        List<Posicion> colocadas = new ArrayList<>();

        Collections.shuffle(posiciones, partida.getAzar());

        if (criaturas.size() > posiciones.size()) {
            throw new IllegalStateException("No existen suficientes casillas exteriores libres para colocar todas las criaturas.");
        }

        try {
            for (int i = 0; i < criaturas.size(); i++) {
                Criatura criatura = criaturas.get(i);
                Posicion posicion = posiciones.get(i);

                if (!tablero.colocar(criatura, posicion)) {
                    throw new IllegalStateException("No fue posible colocar la criatura " + criatura.getNombre() + " en " + posicion + ".");
                }

                criatura.fijarPosicionInicial(posicion);
                colocadas.add(posicion);
            }
        } catch (RuntimeException e) {
            for (Posicion posicion : colocadas) {
                tablero.retirar(posicion);
            }

            for (Criatura criatura : criaturas) {
                criatura.setPosicion(null);
            }

            throw e;
        }
    }

    private List<ComponenteCombate> crearParticipantes(NucleoOxigeno nucleo, Escuadron escuadron, List<Criatura> criaturas) {
        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);

        Set<String> seleccionadas = escuadron.getSeleccionadas();

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && seleccionadas.contains(defensa.getId())) {
                participantes.add(defensa);
            }
        }

        participantes.addAll(criaturas);
        return participantes;
    }

    private NucleoOxigeno buscarNucleo(Tablero tablero) {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);

                if (tablero.obtener(posicion) instanceof NucleoOxigeno nucleo) {
                    return nucleo;
                }
            }
        }

        return null;
    }

    private void validarPartida(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (partida.getEscuadron() == null) {
            throw new IllegalStateException("La partida no tiene un escuadrón.");
        }

        if (partida.getTablero() == null) {
            throw new IllegalStateException("La partida no tiene un tablero.");
        }

        if (!partida.tieneCatalogoSnapshot()) {
            throw new IllegalStateException("La partida no contiene un catálogo snapshot.");
        }

        if (partida.getMisionActual() < 1) {
            throw new IllegalStateException("El número de misión no es válido.");
        }

        if (partida.getAzar() == null) {
            throw new IllegalStateException("La partida no contiene un generador aleatorio.");
        }
    }
}