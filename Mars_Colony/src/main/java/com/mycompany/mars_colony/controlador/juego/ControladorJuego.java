package com.mycompany.mars_colony.controlador.juego;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.FabricaComponentes;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.generacion.GeneradorMision;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.combate.UnidadActiva;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.motor.HiloUnidad;
import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.persistencia.RepositorioPartidas;
import com.mycompany.mars_colony.servicio.ServicioCampania;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ControladorJuego {

    private static final Set<TipoComponente> TIPOS_DEFENSA = EnumSet.of(TipoComponente.DEFENSA_CONTACTO, TipoComponente.DEFENSA_ALCANCE, TipoComponente.DRON, TipoComponente.DEFENSA_IMPACTO, TipoComponente.DEFENSA_MULTIPLE, TipoComponente.BARRERA);

    private Partida partida;

    private final FabricaComponentes fabrica;
    private final RepositorioPartidas repositorioPartidas;
    private final CreadorPartida creadorPartida;
    private final ServicioCampania servicioCampania;

    private GeneradorMision generadorMision;

    private MotorBatalla motorBatalla;
    private final List<HiloUnidad> hilosBatalla;

    @FunctionalInterface
    public interface CreadorPartida {

        Partida crear(String nombreComandante);
    }

    public ControladorJuego(Partida partida) {
        this(partida, new FabricaComponentes(), null, null);
    }

    public ControladorJuego(Partida partida, RepositorioPartidas repositorioPartidas) {
        this(partida, new FabricaComponentes(), repositorioPartidas, null);
    }

    public ControladorJuego(Partida partida, RepositorioPartidas repositorioPartidas, CreadorPartida creadorPartida) {
        this(partida, new FabricaComponentes(), repositorioPartidas, creadorPartida);
    }

    public ControladorJuego(Partida partida, FabricaComponentes fabrica, RepositorioPartidas repositorioPartidas, CreadorPartida creadorPartida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (fabrica == null) {
            throw new IllegalArgumentException("La fábrica de componentes no puede ser nula.");
        }

        this.partida = partida;
        this.fabrica = fabrica;
        this.repositorioPartidas = repositorioPartidas;
        this.creadorPartida = creadorPartida;
        this.servicioCampania = new ServicioCampania();
        this.generadorMision = null;
        this.motorBatalla = null;
        this.hilosBatalla = new ArrayList<>();
    }

    public Partida consultarEstado() {
        return partida;
    }

    public void configurarGeneradorMision(GeneradorMision generadorMision) {
        if (generadorMision == null) {
            throw new IllegalArgumentException("El generador de misión no puede ser nulo.");
        }

        this.generadorMision = generadorMision;
    }

    public boolean persistenciaDisponible() {
        return repositorioPartidas != null;
    }

    public boolean creacionDisponible() {
        return repositorioPartidas != null && creadorPartida != null;
    }

    public List<String> listarPartidasGuardadas() {
        verificarRepositorio();

        try {
            List<String> nombres = new ArrayList<>(repositorioPartidas.listar());
            Collections.sort(nombres);
            return Collections.unmodifiableList(nombres);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible listar las partidas guardadas.", e);
        }
    }

    public void guardarPartidaActual() {
        verificarRepositorio();

        try {
            repositorioPartidas.guardar(partida);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible guardar la partida de " + partida.getNombreComandante() + ".", e);
        }
    }

    public Partida cargarPartida(String nombreComandante) {
        verificarRepositorio();

        if (estaBatallaEnCurso()) {
            throw new IllegalStateException("No se puede cargar una partida durante una batalla.");
        }

        if (nombreComandante == null || nombreComandante.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una partida.");
        }

        try {
            Partida cargada = repositorioPartidas.cargar(nombreComandante);

            if (cargada == null) {
                throw new IllegalStateException("No se encontró la partida solicitada.");
            }

            this.partida = cargada;
            return cargada;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible cargar la partida de " + nombreComandante + ".", e);
        }
    }

    public Partida crearNuevaPartida(String nombreComandante) {
        verificarRepositorio();

        if (estaBatallaEnCurso()) {
            throw new IllegalStateException("No se puede crear una partida durante una batalla.");
        }

        if (creadorPartida == null) {
            throw new IllegalStateException("No se configuró la creación de nuevas partidas.");
        }

        String nombre = normalizarNombre(nombreComandante);

        try {
            if (repositorioPartidas.existeComandante(nombre)) {
                throw new IllegalStateException("Ya existe una partida para el comandante " + nombre + ".");
            }

            Partida nueva = creadorPartida.crear(nombre);

            if (nueva == null) {
                throw new IllegalStateException("No fue posible crear la nueva partida.");
            }

            repositorioPartidas.guardar(nueva);
            this.partida = nueva;

            return nueva;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible crear la partida.", e);
        }
    }

    public List<ConfiguracionComponente> listarDefensasDisponibles() {
        if (!partida.tieneCatalogoSnapshot()) {
            return Collections.emptyList();
        }

        CatalogoComponentes catalogo = partida.getCatalogoSnapshot();

        if (catalogo == null) {
            return Collections.emptyList();
        }

        List<ConfiguracionComponente> disponibles = catalogo.disponibles(partida.getMisionActual());
        List<ConfiguracionComponente> defensas = new ArrayList<>();

        for (ConfiguracionComponente configuracion : disponibles) {
            if (TIPOS_DEFENSA.contains(configuracion.getTipo())) {
                defensas.add(configuracion);
            }
        }

        return Collections.unmodifiableList(defensas);
    }

    public Defensa colocarDefensa(String idConfiguracion, Posicion posicion) {
        if (idConfiguracion == null || idConfiguracion.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una configuración de defensa.");
        }

        if (posicion == null) {
            throw new IllegalArgumentException("Debe indicar una posición.");
        }

        Mision mision = partida.getMision();

        if (mision == null || mision.getEstado() != EstadoMision.PREPARACION) {
            throw new IllegalStateException("Las defensas solo pueden colocarse durante la preparación de la misión.");
        }

        Tablero tablero = partida.getTablero();

        if (tablero == null) {
            throw new IllegalStateException("La partida no tiene un tablero.");
        }

        if (!tablero.estaDentro(posicion)) {
            throw new IllegalArgumentException("La posición seleccionada está fuera del tablero.");
        }

        if (!tablero.estaLibre(posicion)) {
            throw new IllegalStateException("La casilla seleccionada ya está ocupada.");
        }

        Escuadron escuadron = partida.getEscuadron();

        if (escuadron == null) {
            throw new IllegalStateException("La partida no tiene un escuadrón.");
        }

        ConfiguracionComponente configuracion = buscarDefensaDisponible(idConfiguracion);

        if (configuracion == null) {
            throw new IllegalStateException("La defensa seleccionada no está disponible para la misión actual.");
        }

        int costo = configuracion.getBase().getCostoCapacidad();

        if (costo > escuadron.capacidadRestante()) {
            throw new IllegalStateException("Capacidad insuficiente. Costo: " + costo + ", restante: " + escuadron.capacidadRestante() + ".");
        }

        ComponenteCombate componente = fabrica.crear(configuracion);

        if (!(componente instanceof Defensa defensa)) {
            throw new IllegalStateException("La configuración seleccionada no produjo una defensa.");
        }

        if (!tablero.colocar(defensa, posicion)) {
            throw new IllegalStateException("El tablero rechazó la colocación de la defensa.");
        }

        if (!escuadron.agregarDefensa(defensa)) {
            tablero.retirar(posicion);
            defensa.setPosicion(null);
            throw new IllegalStateException("No fue posible agregar la defensa al escuadrón.");
        }

        if (!escuadron.seleccionar(defensa.getId())) {
            tablero.retirar(posicion);
            defensa.setPosicion(null);
            throw new IllegalStateException("No fue posible seleccionar la defensa dentro de la capacidad del escuadrón.");
        }

        Mision misionActual = partida.getMision();

        if (misionActual != null) {
            misionActual.agregarParticipante(defensa);
        }

        return defensa;
    }

    public void iniciarBatalla() {
        if (estaBatallaEnCurso()) {
            throw new IllegalStateException("Ya existe una batalla en ejecución.");
        }

        Mision mision = partida.getMision();
        
        sincronizarDefensasSeleccionadas();
        validarMisionParaBatalla(mision);

        MotorBatalla nuevoMotor = new MotorBatalla(partida);
        List<HiloUnidad> nuevosHilos = new ArrayList<>();

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante instanceof UnidadActiva unidad && unidad.estaOperativa()) {
                nuevosHilos.add(new HiloUnidad(unidad, nuevoMotor));
            }
        }

        if (nuevosHilos.isEmpty()) {
            throw new IllegalStateException("La misión no contiene unidades activas para ejecutar la batalla.");
        }

        this.motorBatalla = nuevoMotor;
        this.hilosBatalla.clear();
        this.hilosBatalla.addAll(nuevosHilos);

        motorBatalla.iniciar();

        for (HiloUnidad hilo : hilosBatalla) {
            hilo.iniciar();
        }
    }

    private void validarMisionParaBatalla(Mision mision) {
        if (mision == null) {
            throw new IllegalStateException("No existe una misión preparada.");
        }

        if (!mision.isGenerada()) {
            throw new IllegalStateException("La misión todavía no ha sido generada.");
        }

        if (mision.getEstado() != EstadoMision.PREPARACION) {
            throw new IllegalStateException("La misión no se encuentra en estado de preparación.");
        }

        if (mision.getCriaturas().isEmpty()) {
            throw new IllegalStateException("La misión no contiene criaturas.");
        }

        Tablero tablero = partida.getTablero();

        if (tablero == null) {
            throw new IllegalStateException("La partida no tiene tablero.");
        }

        NucleoOxigeno nucleo = null;

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante instanceof NucleoOxigeno encontrado) {
                nucleo = encontrado;
                break;
            }
        }

        if (nucleo == null || nucleo.getPosicion() == null || tablero.obtener(nucleo.getPosicion()) != nucleo) {
            throw new IllegalStateException("El núcleo de oxígeno no está correctamente colocado en el tablero.");
        }

        for (ComponenteCombate criatura : mision.getCriaturas()) {
            if (criatura == null || criatura.getPosicion() == null || tablero.obtener(criatura.getPosicion()) != criatura) {
                throw new IllegalStateException("Todas las criaturas deben estar colocadas antes de iniciar la batalla.");
            }
        }
    }

    public boolean estaBatallaEnCurso() {
        return motorBatalla != null && motorBatalla.estaEnEjecucion();
    }

    public void limpiarBatallaFinalizada() {
        if (motorBatalla == null || motorBatalla.estaEnEjecucion()) {
            return;
        }

        for (HiloUnidad hilo : hilosBatalla) {
            if (hilo.estaActivo()) {
                hilo.detener();
            }
        }

        hilosBatalla.clear();
        motorBatalla = null;
    }

    public void procesarResultadoMisionFinalizada() {
        Mision mision = partida.getMision();

        if (mision == null) {
            return;
        }

        if (mision.getEstado() == EstadoMision.VICTORIA) {
            servicioCampania.registrarVictoria(partida);
        }
    }

    public boolean puedeRepetirMision() {
        return servicioCampania.puedeRepetir(partida);
    }

    public boolean puedeAvanzarMision() {
        return servicioCampania.puedeAvanzar(partida);
    }

    public boolean puedeFinalizarCampania() {
        return servicioCampania.puedeFinalizar(partida);
    }

    public boolean repetirMisionDesdeResultado() {
        if (!servicioCampania.puedeRepetir(partida)) {
            return false;
        }

        Mision mision = partida.getMision();
        Tablero tablero = partida.getTablero();

        retirarParticipantesMoviblesDelTablero(mision, tablero);

        boolean repetida = servicioCampania.repetirActual(partida);

        if (!repetida) {
            return false;
        }

        restaurarParticipantesEnTablero(mision, tablero);

        return true;
    }

    public boolean avanzarMisionDesdeResultado() {
        if (!servicioCampania.puedeAvanzar(partida)) {
            return false;
        }

        if (generadorMision == null) {
            throw new IllegalStateException("No se configuró el generador de misiones.");
        }

        int numeroAnterior = partida.getMisionActual();

        boolean avanzo = servicioCampania.avanzar(partida);

        if (!avanzo) {
            return false;
        }

        try {
            generadorMision.generar(partida);
            return true;
        } catch (RuntimeException e) {
            partida.setMisionActual(numeroAnterior);
            throw e;
        }
    }

    public boolean finalizarCampaniaDesdeResultado() {
        return servicioCampania.finalizarCampania(partida);
    }

    private void retirarParticipantesMoviblesDelTablero(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            return;
        }

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante == null || participante instanceof NucleoOxigeno) {
                continue;
            }

            Posicion posicion = participante.getPosicion();

            if (posicion != null && tablero.obtener(posicion) == participante) {
                tablero.retirar(posicion);
            }
        }
    }

    private void restaurarParticipantesEnTablero(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            throw new IllegalStateException("No existe un tablero o misión que restaurar.");
        }

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante == null || participante instanceof NucleoOxigeno) {
                continue;
            }

            Posicion posicion = participante.getPosicion();

            if (posicion == null) {
                continue;
            }

            if (!tablero.estaLibre(posicion)) {
                throw new IllegalStateException("No fue posible restaurar la posición inicial de " + participante.getNombre() + ".");
            }

            if (!tablero.colocar(participante, posicion)) {
                throw new IllegalStateException("El tablero rechazó la restauración de " + participante.getNombre() + ".");
            }
        }
    }

    private ConfiguracionComponente buscarDefensaDisponible(String id) {
        for (ConfiguracionComponente configuracion : listarDefensasDisponibles()) {
            if (configuracion.getId().equals(id)) {
                return configuracion;
            }
        }

        return null;
    }

    private void verificarRepositorio() {
        if (repositorioPartidas == null) {
            throw new IllegalStateException("No se configuró el repositorio de partidas.");
        }
    }

    private String normalizarNombre(String nombreComandante) {
        if (nombreComandante == null) {
            throw new IllegalArgumentException("El nombre del comandante es obligatorio.");
        }

        String nombre = nombreComandante.trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del comandante es obligatorio.");
        }

        return nombre;
    }
    
    private void sincronizarDefensasSeleccionadas() {
        Mision mision = partida.getMision();
        Escuadron escuadron = partida.getEscuadron();

        if (mision == null || escuadron == null) {
            return;
        }

        mision.registrarParticipantes();

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && escuadron.getSeleccionadas().contains(defensa.getId())) {
                mision.agregarParticipante(defensa);
            }
        }
    }
}