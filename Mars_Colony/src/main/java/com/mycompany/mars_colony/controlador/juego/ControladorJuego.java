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
import com.mycompany.mars_colony.servicio.ServicioProgresion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ControladorJuego {

    private static final Set<TipoComponente> TIPOS_DEFENSA = EnumSet.of(TipoComponente.DEFENSA_CONTACTO, TipoComponente.DEFENSA_ALCANCE, TipoComponente.DRON, TipoComponente.DEFENSA_IMPACTO, TipoComponente.DEFENSA_MULTIPLE, TipoComponente.BARRERA);
    private static final String NOMBRE_PARTIDA_TEMPORAL = "Sin partida";
    
    private Partida partida;
    private final FabricaComponentes fabrica;
    private final RepositorioPartidas repositorioPartidas;
    private final CreadorPartida creadorPartida;
    private final ServicioCampania servicioCampania;
    private final ServicioProgresion servicioProgresion;
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
        this.servicioProgresion = new ServicioProgresion();
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
    
    public boolean hayPartidaReal() {
        if (partida == null) {
            return false;
        }

        String comandante = partida.getNombreComandante();

        return comandante != null
                && !comandante.isBlank()
                && !NOMBRE_PARTIDA_TEMPORAL.equalsIgnoreCase(comandante.trim());
    }

    public void guardarPartidaActual() {
        verificarRepositorio();

        if (!hayPartidaReal()) {
            throw new IllegalStateException(
                    "Debe crear o cargar una partida antes de guardar."
            );
        }

        try {
            repositorioPartidas.guardar(partida);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No fue posible guardar la partida de "
                    + partida.getNombreComandante()
                    + ".",
                    e
            );
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

        limpiarBatallaFinalizada();

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

        limpiarBatallaFinalizada();

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

    public List<Defensa> listarDefensasNoColocadas() {
        Escuadron escuadron = partida.getEscuadron();
        Tablero tablero = partida.getTablero();

        if (escuadron == null || tablero == null) {
            return Collections.emptyList();
        }

        List<Defensa> defensas = new ArrayList<>();

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && !escuadron.getSeleccionadas().contains(defensa.getId()) && buscarPosicionEnTablero(tablero, defensa) == null) {
                defensas.add(defensa);
            }
        }

        return Collections.unmodifiableList(defensas);
    }

    public Defensa colocarDefensa(String idConfiguracion, Posicion posicion) {
        if (idConfiguracion == null || idConfiguracion.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una configuración de defensa.");
        }

        validarColocacionDefensa(posicion);

        Escuadron escuadron = partida.getEscuadron();
        Tablero tablero = partida.getTablero();
        Mision mision = partida.getMision();
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

        aplicarProgresionDefensa(defensa);

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

        defensa.fijarPosicionInicial(posicion);
        mision.agregarParticipante(defensa);

        return defensa;
    }

    public Defensa colocarDefensaExistente(String idDefensa, Posicion posicion) {
        if (idDefensa == null || idDefensa.isBlank()) {
            throw new IllegalArgumentException("Debe indicar la defensa que desea colocar.");
        }

        validarColocacionDefensa(posicion);

        Escuadron escuadron = partida.getEscuadron();
        Tablero tablero = partida.getTablero();
        Mision mision = partida.getMision();
        Defensa defensa = buscarDefensaEscuadron(escuadron, idDefensa);

        if (defensa == null) {
            throw new IllegalStateException("La defensa indicada no pertenece al escuadrón.");
        }

        if (escuadron.getSeleccionadas().contains(defensa.getId())) {
            throw new IllegalStateException("La defensa indicada ya está seleccionada para la misión.");
        }

        if (buscarPosicionEnTablero(tablero, defensa) != null) {
            throw new IllegalStateException("La defensa indicada ya está colocada en el tablero.");
        }

        if (defensa.getCostoCapacidad() > escuadron.capacidadRestante()) {
            throw new IllegalStateException("Capacidad insuficiente. Costo: " + defensa.getCostoCapacidad() + ", restante: " + escuadron.capacidadRestante() + ".");
        }

        aplicarProgresionDefensa(defensa);
        defensa.setPosicion(null);

        if (!tablero.colocar(defensa, posicion)) {
            throw new IllegalStateException("El tablero rechazó la colocación de la defensa.");
        }

        if (!escuadron.seleccionar(defensa.getId())) {
            tablero.retirar(posicion);
            defensa.setPosicion(null);
            throw new IllegalStateException("No fue posible seleccionar la defensa dentro de la capacidad del escuadrón.");
        }

        defensa.fijarPosicionInicial(posicion);
        mision.agregarParticipante(defensa);

        return defensa;
    }

    public Defensa retirarDefensa(String idDefensa) {
        if (idDefensa == null || idDefensa.isBlank()) {
            throw new IllegalArgumentException("Debe indicar la defensa que desea retirar.");
        }

        Mision mision = partida.getMision();

        if (mision == null || mision.getEstado() != EstadoMision.PREPARACION) {
            throw new IllegalStateException("Las defensas solo pueden retirarse durante la preparación de la misión.");
        }

        Tablero tablero = partida.getTablero();
        Escuadron escuadron = partida.getEscuadron();

        if (tablero == null || escuadron == null) {
            throw new IllegalStateException("La partida no contiene un tablero o escuadrón válido.");
        }

        Defensa defensa = buscarDefensaEscuadron(escuadron, idDefensa);

        if (defensa == null) {
            throw new IllegalStateException("La defensa indicada no pertenece al escuadrón.");
        }

        if (!escuadron.getSeleccionadas().contains(defensa.getId())) {
            throw new IllegalStateException("La defensa indicada no está seleccionada para la misión.");
        }

        Posicion posicion = buscarPosicionEnTablero(tablero, defensa);

        if (posicion == null) {
            throw new IllegalStateException("La defensa indicada no está colocada en el tablero.");
        }

        if (tablero.retirar(posicion) != defensa) {
            throw new IllegalStateException("No fue posible retirar la defensa del tablero.");
        }

        if (!escuadron.deseleccionar(defensa.getId())) {
            tablero.colocar(defensa, posicion);
            throw new IllegalStateException("No fue posible liberar la capacidad ocupada por la defensa.");
        }

        mision.eliminarParticipante(defensa);
        defensa.setPosicion(null);
        sincronizarDefensasSeleccionadas();

        return defensa;
    }

    public void iniciarBatalla() {
        if (estaBatallaEnCurso()) {
            throw new IllegalStateException("Ya existe una batalla en ejecución.");
        }

        limpiarBatallaFinalizada();

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

        motorBatalla = nuevoMotor;
        hilosBatalla.clear();
        hilosBatalla.addAll(nuevosHilos);
        motorBatalla.iniciar();

        for (HiloUnidad hilo : hilosBatalla) {
            hilo.iniciar();
        }
    }

    public boolean estaBatallaEnCurso() {
        return motorBatalla != null && motorBatalla.estaEnEjecucion();
    }

    public void detenerBatallaYEsperar() {
        MotorBatalla motorActual = motorBatalla;

        if (motorActual != null) {
            motorActual.detener();
        }

        for (HiloUnidad hilo : hilosBatalla) {
            hilo.detener();
        }

        for (HiloUnidad hilo : hilosBatalla) {
            hilo.esperarFin();
        }

        hilosBatalla.clear();
        motorBatalla = null;
    }

    public void limpiarBatallaFinalizada() {
        if (motorBatalla == null || motorBatalla.estaEnEjecucion()) {
            return;
        }

        detenerBatallaYEsperar();
    }

    public void procesarResultadoMisionFinalizada() {
        Mision mision = partida.getMision();

        if (mision != null && mision.getEstado() == EstadoMision.VICTORIA) {
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

        if (mision == null || tablero == null) {
            throw new IllegalStateException("No existe una misión o tablero que repetir.");
        }

        retirarParticipantesDelTablero(mision, tablero);

        if (!servicioCampania.repetirActual(partida)) {
            throw new IllegalStateException("No fue posible preparar nuevamente la misión.");
        }

        restaurarParticipantesEnTablero(mision, tablero);
        validarConsistenciaParticipantes(mision, tablero);

        return true;
    }

    public boolean avanzarMisionDesdeResultado() {
        if (!servicioCampania.puedeAvanzar(partida)) {
            return false;
        }

        if (generadorMision == null) {
            throw new IllegalStateException("No se configuró el generador de misiones.");
        }

        Partida candidata = copiarPartida(partida);

        Mision misionAnterior = candidata.getMision();
        Tablero tablero = candidata.getTablero();
        Escuadron escuadron = candidata.getEscuadron();

        if (misionAnterior == null || tablero == null || escuadron == null) {
            throw new IllegalStateException(
                    "La partida no contiene los elementos necesarios para avanzar de misión."
            );
        }

        if (!servicioCampania.avanzar(candidata)) {
            return false;
        }

        limpiarParticipantesNoNucleo(misionAnterior, tablero);
        prepararDefensasParaNuevaMision(escuadron);
        generadorMision.generar(candidata);

        partida = candidata;

        return true;
    }

    public boolean finalizarCampaniaDesdeResultado() {
        return servicioCampania.finalizarCampania(partida);
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
        Escuadron escuadron = partida.getEscuadron();

        if (tablero == null || escuadron == null) {
            throw new IllegalStateException("La partida no tiene tablero o escuadrón.");
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

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && escuadron.getSeleccionadas().contains(defensa.getId()) && (defensa.getPosicion() == null || tablero.obtener(defensa.getPosicion()) != defensa)) {
                throw new IllegalStateException("Todas las defensas seleccionadas deben estar colocadas antes de iniciar la batalla.");
            }
        }
    }

    private void aplicarProgresionDefensa(Defensa defensa) {
        servicioProgresion.aplicarCrecimiento(defensa, partida.getMisionActual(), partida.getAzar());
    }

    private void validarColocacionDefensa(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("Debe indicar una posición.");
        }

        Mision mision = partida.getMision();

        if (mision == null || mision.getEstado() != EstadoMision.PREPARACION) {
            throw new IllegalStateException("Las defensas solo pueden colocarse durante la preparación de la misión.");
        }

        Tablero tablero = partida.getTablero();
        Escuadron escuadron = partida.getEscuadron();

        if (tablero == null || escuadron == null) {
            throw new IllegalStateException("La partida no contiene un tablero o escuadrón válido.");
        }

        if (!tablero.estaDentro(posicion)) {
            throw new IllegalArgumentException("La posición seleccionada está fuera del tablero.");
        }

        if (!tablero.estaLibre(posicion)) {
            throw new IllegalStateException("La casilla seleccionada ya está ocupada.");
        }
    }

    private void prepararDefensasParaNuevaMision(Escuadron escuadron) {
        if (escuadron == null) {
            throw new IllegalArgumentException("El escuadrón no puede ser nulo.");
        }

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null) {
                escuadron.deseleccionar(defensa.getId());
                defensa.prepararNuevaMision();
                defensa.setPosicion(null);
            }
        }
    }

    private void retirarParticipantesDelTablero(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            throw new IllegalStateException("No existe una misión o tablero que limpiar.");
        }

        limpiarParticipantesNoNucleo(mision, tablero);
    }

    private void restaurarParticipantesEnTablero(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            throw new IllegalStateException("No existe un tablero o misión que restaurar.");
        }

        try {
            Set<Posicion> posicionesReservadas = new HashSet<>();

            for (ComponenteCombate participante : mision.getParticipantes()) {
                if (participante == null) {
                    continue;
                }

                Posicion inicial = participante.getPosicionInicial();

                if (inicial == null) {
                    throw new IllegalStateException("El participante " + participante.getNombre() + " no tiene posición inicial.");
                }

                if (!tablero.estaDentro(inicial)) {
                    throw new IllegalStateException("La posición inicial de " + participante.getNombre() + " está fuera del tablero.");
                }

                if (participante instanceof NucleoOxigeno) {
                    if (tablero.obtener(inicial) != participante) {
                        throw new IllegalStateException("El núcleo no coincide con su posición inicial en el tablero.");
                    }

                    continue;
                }

                if (!posicionesReservadas.add(inicial)) {
                    throw new IllegalStateException("Existen dos participantes con la misma posición inicial: " + inicial);
                }

                if (!tablero.estaLibre(inicial)) {
                    throw new IllegalStateException("La posición inicial " + inicial + " de " + participante.getNombre() + " no está libre.");
                }
            }

            for (ComponenteCombate participante : mision.getParticipantes()) {
                if (participante == null || participante instanceof NucleoOxigeno) {
                    continue;
                }

                Posicion inicial = participante.getPosicionInicial();

                if (!tablero.colocar(participante, inicial)) {
                    throw new IllegalStateException("El tablero rechazó la restauración de " + participante.getNombre() + ".");
                }
            }
        } catch (RuntimeException e) {
            limpiarParticipantesNoNucleo(mision, tablero);
            throw e;
        }
    }

    private void limpiarParticipantesNoNucleo(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            return;
        }

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante == null || participante instanceof NucleoOxigeno) {
                continue;
            }

            for (int fila = 0; fila < tablero.getFilas(); fila++) {
                for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                    Posicion posicionTablero = new Posicion(fila, columna);

                    if (tablero.obtener(posicionTablero) == participante) {
                        tablero.retirar(posicionTablero);
                    }
                }
            }

            participante.setPosicion(null);
        }
    }

    private void validarConsistenciaParticipantes(Mision mision, Tablero tablero) {
        if (mision == null || tablero == null) {
            throw new IllegalStateException("No existe misión o tablero para validar.");
        }

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante == null) {
                continue;
            }

            Posicion posicion = participante.getPosicion();
            Posicion inicial = participante.getPosicionInicial();

            if (posicion == null) {
                throw new IllegalStateException("El participante " + participante.getNombre() + " quedó sin posición después de repetir la misión.");
            }

            if (!tablero.estaDentro(posicion)) {
                throw new IllegalStateException("El participante " + participante.getNombre() + " quedó fuera del tablero.");
            }

            if (tablero.obtener(posicion) != participante) {
                throw new IllegalStateException("Existe una inconsistencia entre el tablero y la posición de " + participante.getNombre() + ".");
            }

            if (inicial == null || !inicial.equals(posicion)) {
                throw new IllegalStateException("El participante " + participante.getNombre() + " no regresó a su posición inicial.");
            }
        }
    }

    private Defensa buscarDefensaEscuadron(Escuadron escuadron, String idDefensa) {
        if (escuadron == null || idDefensa == null) {
            return null;
        }

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && idDefensa.equals(defensa.getId())) {
                return defensa;
            }
        }

        return null;
    }

    private Posicion buscarPosicionEnTablero(Tablero tablero, ComponenteCombate componente) {
        if (tablero == null || componente == null) {
            return null;
        }

        Posicion posicionActual = componente.getPosicion();

        if (posicionActual != null && tablero.estaDentro(posicionActual) && tablero.obtener(posicionActual) == componente) {
            return posicionActual;
        }

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);

                if (tablero.obtener(posicion) == componente) {
                    return posicion;
                }
            }
        }

        return null;
    }

    private ConfiguracionComponente buscarDefensaDisponible(String id) {
        for (ConfiguracionComponente configuracion : listarDefensasDisponibles()) {
            if (configuracion.getId().equals(id)) {
                return configuracion;
            }
        }

        return null;
    }
    
    private Partida copiarPartida(Partida original) {
        if (original == null) {
            throw new IllegalArgumentException("La partida original no puede ser nula.");
        }

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            try (ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
                salida.writeObject(original);
            }

            try (ObjectInputStream entrada = new ObjectInputStream(
                    new ByteArrayInputStream(bytes.toByteArray()))) {

                Object copia = entrada.readObject();

                if (!(copia instanceof Partida partidaCopiada)) {
                    throw new IllegalStateException(
                            "No fue posible crear una copia válida de la partida."
                    );
                }

                return partidaCopiada;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No fue posible preparar el avance seguro de la misión.",
                    e
            );
        }
    }

    private void verificarRepositorio() {
        if (repositorioPartidas == null) {
            throw new IllegalStateException("No se configuró el repositorio de partidas.");
        }
    }

    private String normalizarNombre(String nombreComandante) {
        if (nombreComandante == null) {
            throw new IllegalArgumentException(
                    "El nombre del comandante es obligatorio."
            );
        }

        String nombre = nombreComandante.trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del comandante es obligatorio."
            );
        }

        if (NOMBRE_PARTIDA_TEMPORAL.equalsIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "El nombre 'Sin partida' está reservado por el sistema."
            );
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
        List<ComponenteCombate> participantesActuales = new ArrayList<>(mision.getParticipantes());

        for (ComponenteCombate participante : participantesActuales) {
            if (participante instanceof Defensa defensa && !escuadron.getSeleccionadas().contains(defensa.getId())) {
                mision.eliminarParticipante(defensa);
            }
        }

        for (Defensa defensa : escuadron.getDefensas()) {
            if (defensa != null && escuadron.getSeleccionadas().contains(defensa.getId())) {
                mision.agregarParticipante(defensa);
            }
        }
    }
}