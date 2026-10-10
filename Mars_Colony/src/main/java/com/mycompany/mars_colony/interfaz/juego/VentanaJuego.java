package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class VentanaJuego extends JFrame {

    private static final String INICIO = "inicio";
    private static final String NUEVA = "nueva";
    private static final String CARGAR = "cargar";
    private static final String CAMPANIA = "campania";
    private static final String PREPARACION = "preparacion";
    private static final String ARSENAL = "arsenal";
    private static final String FICHA = "ficha";
    private static final String BATALLA = "batalla";
    private static final String HISTORIAL = "historial";
    private static final String RESULTADO = "resultado";
    private static final String INFORME = "informe";
    private static final String MEJORAS = "mejoras";
    private static final String FINAL_CAMPANIA = "finalCampania";
    private static final String AYUDA = "ayuda";

    private final ControladorJuego controlador;
    private final CardLayout tarjetas;
    private final JPanel contenedor;

    private final PanelInicio panelInicio;
    private final PanelNuevaExpedicion panelNueva;
    private final PanelCargarPartida panelCargar;
    private final PanelCampania panelCampania;
    private final PanelPreparacion panelPreparacion;
    private final PanelArsenal panelArsenal;
    private final PanelFichaUnidad panelFicha;
    private final PanelBatalla panelBatalla;
    private final PanelHistorialEnVivo panelHistorial;
    private final PanelResultado panelResultado;
    private final PanelInformeCombate panelInforme;
    private final PanelMejoras panelMejoras;
    private final PanelFinalCampania panelFinalCampania;
    private final PanelAyuda panelAyuda;

    private Timer temporizadorBatalla;
    private boolean procesandoFinBatalla;

    private int capacidadAntesResultado;
    private int capacidadDespuesResultado;
    private int misionResultadoProcesada;

    private String pantallaActual;
    private String pantallaAnterior;

    public VentanaJuego(Partida partida) {
        this(new ControladorJuego(partida));
    }

    public VentanaJuego(ControladorJuego controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException(
                    "El controlador del juego no puede ser nulo."
            );
        }

        this.controlador = controlador;

        capacidadAntesResultado = -1;
        capacidadDespuesResultado = -1;
        misionResultadoProcesada = -1;

        TemaMars.aplicarGlobalmente();

        tarjetas = new CardLayout();
        contenedor = new JPanel(tarjetas);

        panelInicio = new PanelInicio(
                controlador,
                this::continuarPartida,
                this::mostrarNuevaExpedicion,
                this::mostrarCargarPartida,
                this::mostrarAyuda,
                this::salirAplicacion
        );

        panelNueva = new PanelNuevaExpedicion(
                controlador,
                this::mostrarInicio,
                this::expedicionCreada
        );

        panelCargar = new PanelCargarPartida(
                controlador,
                this::mostrarInicio,
                this::partidaCargada
        );

        panelCampania = new PanelCampania(
                controlador,
                this::mostrarInicio,
                this::mostrarPreparacion
        );

        panelPreparacion = new PanelPreparacion(
                controlador,
                this::mostrarCampania,
                this::mostrarArsenal,
                this::mostrarFichaDesdePreparacion,
                this::abrirMenuPreparacion,
                this::mostrarBatalla
        );

        panelArsenal = new PanelArsenal(
                controlador,
                this::mostrarPreparacion,
                this::elegirDefensaNueva,
                this::elegirDefensaExistente,
                this::mostrarFichaConfiguracion,
                this::mostrarFichaDesdeArsenal
        );

        panelFicha = new PanelFichaUnidad(
                this::mostrarPreparacion,
                this::mostrarArsenal,
                this::moverDefensaDesdeFicha
        );

        panelBatalla = new PanelBatalla(
                controlador,
                this::mostrarHistorialDe,
                this::abrirMenuBatalla
        );

        panelHistorial = new PanelHistorialEnVivo(
                controlador,
                this::mostrarBatalla
        );

        panelResultado = new PanelResultado(
                controlador,
                this::continuarDesdeResultado,
                this::repetirDesdeResultado,
                this::mostrarInforme,
                this::guardarYVolverInicio
        );

        panelInforme = new PanelInformeCombate(
                controlador,
                this::mostrarResultado
        );

        panelMejoras = new PanelMejoras(
                controlador,
                this::mostrarPreparacion
        );

        panelFinalCampania = new PanelFinalCampania(
                controlador,
                this::finalizarCampania,
                this::generarMisionExtra
        );

        panelAyuda = new PanelAyuda(
                this::volver
        );

        construirPantallas();
        configurarVentana();
        mostrarInicio();
    }

    private void configurarVentana() {
        setTitle("Mars Colony");
        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(1100, 680)
        );

        setSize(1440, 820);
        setLocationRelativeTo(null);
        setResizable(true);

        setExtendedState(
                JFrame.MAXIMIZED_BOTH
        );

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        salirAplicacion();
                    }
                }
        );
    }

    private void construirPantallas() {
        contenedor.add(panelInicio, INICIO);
        contenedor.add(panelNueva, NUEVA);
        contenedor.add(panelCargar, CARGAR);
        contenedor.add(panelCampania, CAMPANIA);
        contenedor.add(panelPreparacion, PREPARACION);
        contenedor.add(panelArsenal, ARSENAL);
        contenedor.add(panelFicha, FICHA);
        contenedor.add(panelBatalla, BATALLA);
        contenedor.add(panelHistorial, HISTORIAL);
        contenedor.add(panelResultado, RESULTADO);
        contenedor.add(panelInforme, INFORME);
        contenedor.add(panelMejoras, MEJORAS);
        contenedor.add(
                panelFinalCampania,
                FINAL_CAMPANIA
        );
        contenedor.add(panelAyuda, AYUDA);

        setContentPane(contenedor);
    }

    public void mostrarInicio() {
        panelInicio.actualizar();
        mostrar(INICIO);
    }

    public void mostrarNuevaExpedicion() {
        panelNueva.preparar();
        mostrar(NUEVA);
    }

    public void mostrarCargarPartida() {
        panelCargar.actualizar();
        mostrar(CARGAR);
    }

    public void mostrarCampania() {
        if (!controlador.hayPartidaReal()) {
            DialogoMars.informacion(
                    this,
                    "Sin expedición",
                    "Crea o carga una expedición antes de consultar la campaña."
            );

            mostrarInicio();
            return;
        }

        panelCampania.actualizar();
        mostrar(CAMPANIA);
    }

    public void mostrarPreparacion() {
        if (!controlador.hayPartidaReal()) {
            mostrarInicio();
            return;
        }

        Partida partida =
                controlador.consultarEstado();

        if (partida.getMision() != null
                && (partida.getMision().getEstado()
                == EstadoMision.VICTORIA
                || partida.getMision().getEstado()
                == EstadoMision.DERROTA)) {

            mostrarResultado();
            return;
        }

        panelPreparacion.preparar();
        mostrar(PREPARACION);
    }

    public void mostrarArsenal() {
        panelArsenal.actualizar();
        mostrar(ARSENAL);
    }

    private void elegirDefensaNueva(ConfiguracionComponente configuracion) {
        panelPreparacion.iniciarNueva(
                configuracion
        );

        mostrar(PREPARACION);
    }

    private void elegirDefensaExistente(Defensa defensa) {
        panelPreparacion.iniciarExistente(
                defensa
        );

        mostrar(PREPARACION);
    }

    private void mostrarFichaDesdePreparacion(ComponenteCombate componente) {
        panelFicha.mostrar(
                componente,
                PanelFichaUnidad.Origen.PREPARACION
        );

        mostrar(FICHA);
    }

    private void mostrarFichaDesdeArsenal(ComponenteCombate componente) {
        panelFicha.mostrar(
                componente,
                PanelFichaUnidad.Origen.ARSENAL
        );

        mostrar(FICHA);
    }

    private void mostrarFichaConfiguracion(ConfiguracionComponente configuracion) {
        panelFicha.mostrar(configuracion);
        mostrar(FICHA);
    }

    private void moverDefensaDesdeFicha(Defensa defensa) {
        panelPreparacion.actualizar();
        panelPreparacion.iniciarMovimiento(
                defensa
        );

        mostrar(PREPARACION);
    }

    public void mostrarBatalla() {
        panelBatalla.preparar();
        mostrar(BATALLA);

        iniciarSeguimientoBatalla();
    }

    private void mostrarHistorialDe(ComponenteCombate componente) {
        panelHistorial.mostrar(componente);
        mostrar(HISTORIAL);

        iniciarSeguimientoBatalla();
    }

    public void mostrarResultado() {
        detenerTemporizadorBatalla();

        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            return;
        }

        EstadoMision estado =
                partida.getMision().getEstado();

        if (estado != EstadoMision.VICTORIA
                && estado != EstadoMision.DERROTA) {

            return;
        }

        asegurarResultadoProcesado();

        panelResultado.preparar(
                capacidadAntesResultado,
                capacidadDespuesResultado
        );

        mostrar(RESULTADO);
    }

    public void mostrarInforme() {
        panelInforme.preparar();
        mostrar(INFORME);
    }

    public void mostrarFinalCampania() {
        detenerTemporizadorBatalla();

        panelFinalCampania.preparar();

        mostrar(FINAL_CAMPANIA);
    }

    public void mostrarAyuda() {
        if (controlador.hayPartidaReal()) {
            panelAyuda.setContexto(
                    "COMANDANTE "
                    + controlador
                            .consultarEstado()
                            .getNombreComandante()
                            .toUpperCase()
            );
        } else {
            panelAyuda.setContexto("");
        }

        mostrar(AYUDA);
    }

    private void abrirMenuPreparacion() {
        DialogoMenuJuego.mostrar(
                this,
                DialogoMenuJuego.Contexto.PREPARACION,
                controlador.persistenciaDisponible(),
                this::guardarDesdeMenu,
                this::mostrarInicio,
                this::salirAplicacion
        );
    }

    private void abrirMenuBatalla() {
        DialogoMenuJuego.mostrar(
                this,
                DialogoMenuJuego.Contexto.BATALLA,
                false,
                null,
                null,
                this::salirAplicacion
        );
    }

    private void guardarDesdeMenu() {
        if (!controlador.hayPartidaReal()) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    "No existe una expedición activa."
            );

            return;
        }

        if (controlador.estaBatallaEnCurso()) {
            DialogoMars.error(
                    this,
                    "Guardado no disponible",
                    "No se puede guardar mientras una batalla está en curso."
            );

            return;
        }

        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || !partida.puedePersistirse()) {

            DialogoMars.error(
                    this,
                    "Guardado no disponible",
                    "El estado actual de la partida no puede guardarse."
            );

            return;
        }

        try {
            controlador.guardarPartidaActual();

            DialogoMars.exito(
                    this,
                    "Partida guardada",
                    "La expedición se guardó correctamente."
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    obtenerMensaje(e)
            );
        }
    }

    public void volver() {
        if (pantallaAnterior == null
                || pantallaAnterior.equals(
                        pantallaActual
                )) {

            mostrarInicio();
            return;
        }

        mostrarSinHistorial(
                pantallaAnterior
        );
    }

    private void mostrar(String nombre) {
        if (pantallaActual != null
                && !pantallaActual.equals(nombre)) {

            pantallaAnterior =
                    pantallaActual;
        }

        pantallaActual = nombre;

        tarjetas.show(
                contenedor,
                nombre
        );

        contenedor.revalidate();
        contenedor.repaint();
    }

    private void mostrarSinHistorial(String nombre) {
        pantallaActual = nombre;

        tarjetas.show(
                contenedor,
                nombre
        );

        contenedor.revalidate();
        contenedor.repaint();
    }

    private void expedicionCreada() {
        reiniciarEstadoResultado();

        panelInicio.actualizar();

        mostrarCampania();
    }

    private void partidaCargada() {
        reiniciarEstadoResultado();

        panelInicio.actualizar();

        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            mostrarCampania();
            return;
        }

        Mision mision =
                partida.getMision();

        if (mision.getEstado()
                == EstadoMision.VICTORIA
                || mision.getEstado()
                == EstadoMision.DERROTA) {

            mostrarResultado();
            return;
        }

        mostrarPreparacion();
    }

    private void continuarPartida() {
        if (!controlador.hayPartidaReal()) {
            mostrarNuevaExpedicion();
            return;
        }

        Partida partida =
                controlador.consultarEstado();

        if (partida.isCampaniaFinalizada()) {
            mostrarFinalCampania();
            return;
        }

        Mision mision =
                partida.getMision();

        if (mision == null) {
            mostrarCampania();
            return;
        }

        if (mision.getEstado()
                == EstadoMision.VICTORIA
                || mision.getEstado()
                == EstadoMision.DERROTA) {

            mostrarResultado();
            return;
        }

        if (mision.getEstado()
                == EstadoMision.EN_CURSO) {

            mostrarBatalla();
            return;
        }

        mostrarPreparacion();
    }

    private void asegurarResultadoProcesado() {
        Partida partida =
                controlador.consultarEstado();

        Mision mision =
                partida.getMision();

        if (misionResultadoProcesada
                == mision.getNumero()) {

            return;
        }

        capacidadAntesResultado =
                partida.getEscuadron()
                        .getCapacidadTotal();

        controlador
                .procesarResultadoMisionFinalizada();

        capacidadDespuesResultado =
                partida.getEscuadron()
                        .getCapacidadTotal();

        misionResultadoProcesada =
                mision.getNumero();

        panelInicio.actualizar();
    }

    private void continuarDesdeResultado() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            return;
        }

        if (partida.getMision().getEstado()
                != EstadoMision.VICTORIA) {

            return;
        }

        if (controlador.puedeFinalizarCampania()) {
            mostrarFinalCampania();
            return;
        }

        avanzarYMostrarMejoras();
    }

    private void repetirDesdeResultado() {
        try {
            boolean resultado =
                    controlador
                            .repetirMisionDesdeResultado();

            if (!resultado) {
                DialogoMars.error(
                        this,
                        "No se pudo repetir",
                        "La misión no puede repetirse en este momento."
                );

                return;
            }

            reiniciarEstadoResultado();

            mostrarPreparacion();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo repetir",
                    obtenerMensaje(e)
            );
        }
    }

    private void avanzarYMostrarMejoras() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null) {
            return;
        }

        int misionSuperada =
                partida.getMisionActual();

        try {
            boolean resultado =
                    controlador
                            .avanzarMisionDesdeResultado();

            if (!resultado) {
                DialogoMars.error(
                        this,
                        "No se pudo avanzar",
                        "La siguiente misión no está disponible."
                );

                return;
            }

            panelMejoras.preparar(
                    misionSuperada,
                    capacidadAntesResultado,
                    capacidadDespuesResultado
            );

            misionResultadoProcesada = -1;

            mostrar(MEJORAS);

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo avanzar",
                    obtenerMensaje(e)
            );
        }
    }

    private void generarMisionExtra() {
        avanzarYMostrarMejoras();
    }

    private void finalizarCampania() {
        try {
            if (!controlador
                    .finalizarCampaniaDesdeResultado()) {

                DialogoMars.error(
                        this,
                        "No se pudo finalizar",
                        "La campaña todavía no cumple los requisitos."
                );

                return;
            }

            if (controlador
                    .persistenciaDisponible()) {

                controlador
                        .guardarPartidaActual();
            }

            DialogoMars.exito(
                    this,
                    "Campaña completada",
                    "La expedición quedó marcada como finalizada."
            );

            panelInicio.actualizar();

            mostrarInicio();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo finalizar",
                    obtenerMensaje(e)
            );
        }
    }

    private void guardarYVolverInicio() {
        try {
            controlador.guardarPartidaActual();

            DialogoMars.exito(
                    this,
                    "Partida guardada",
                    "La expedición se guardó correctamente."
            );

            panelInicio.actualizar();

            mostrarInicio();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    obtenerMensaje(e)
            );
        }
    }

    private void reiniciarEstadoResultado() {
        capacidadAntesResultado = -1;
        capacidadDespuesResultado = -1;
        misionResultadoProcesada = -1;
    }

    private void iniciarSeguimientoBatalla() {
        detenerTemporizadorBatalla();

        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            return;
        }

        temporizadorBatalla =
                new Timer(
                        150,
                        e -> actualizarSeguimientoBatalla()
                );

        temporizadorBatalla.setCoalesce(true);
        temporizadorBatalla.start();

        actualizarSeguimientoBatalla();
    }

    private void actualizarSeguimientoBatalla() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            detenerTemporizadorBatalla();
            return;
        }

        panelBatalla.actualizarEnVivo();
        panelHistorial.actualizarEnVivo();

        EstadoMision estado =
                partida.getMision()
                        .getEstado();

        if (estado == EstadoMision.VICTORIA
                || estado == EstadoMision.DERROTA) {

            finalizarSeguimientoBatalla();
        }
    }

    private void finalizarSeguimientoBatalla() {
        if (procesandoFinBatalla) {
            return;
        }

        procesandoFinBatalla = true;

        try {
            detenerTemporizadorBatalla();

            controlador
                    .limpiarBatallaFinalizada();

            mostrarResultado();

        } finally {
            procesandoFinBatalla = false;
        }
    }

    private void detenerTemporizadorBatalla() {
        if (temporizadorBatalla != null) {
            temporizadorBatalla.stop();
            temporizadorBatalla = null;
        }
    }

    private String obtenerMensaje(Throwable error) {
        Throwable actual = error;

        while (actual.getCause() != null
                && actual.getCause() != actual) {

            actual = actual.getCause();
        }

        return actual.getMessage() == null
                || actual.getMessage().isBlank()
                ? "Ocurrió un error inesperado."
                : actual.getMessage();
    }

    private void salirAplicacion() {
        if (controlador.estaBatallaEnCurso()) {
            boolean salir = DialogoMars.confirmar(
                    this,
                    "Batalla en curso",
                    "La batalla sigue activa y no puede guardarse. ¿Quieres detenerla y cerrar el juego?",
                    "Salir"
            );

            if (!salir) {
                return;
            }

            detenerTemporizadorBatalla();

            controlador
                    .detenerBatallaYEsperar();

            dispose();
            return;
        }

        boolean salir = DialogoMars.confirmar(
            this,
            "Salir del juego",
            "¿Quieres cerrar Mars Colony? Los cambios sin guardar se perderán.",
            "Salir"
        );

        if (!salir) {
            return;
        }

        detenerTemporizadorBatalla();
        dispose();
    }
}