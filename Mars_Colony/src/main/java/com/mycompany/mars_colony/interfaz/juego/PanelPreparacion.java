package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BarraVidaMars;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PanelPreparacion extends PanelPantallaJuego {

    private enum Modo {
        NINGUNO,
        NUEVA,
        EXISTENTE,
        MOVER
    }

    private final ControladorJuego controlador;
    private final Runnable accionCampania;
    private final Runnable accionArsenal;
    private final Consumer<ComponenteCombate> accionFicha;
    private final Runnable accionMenu;
    private final Runnable accionBatalla;

    private final PanelMapaMars mapa;
    private final JLabel etiquetaNucleo;
    private final JLabel etiquetaCapacidad;
    private final JLabel etiquetaTablero;
    private final JLabel etiquetaModo;

    private final JLabel imagenSeleccionada;
    private final JLabel etiquetaNombre;
    private final JLabel etiquetaTipo;
    private final JLabel etiquetaId;
    private final JLabel etiquetaNivel;
    private final JLabel etiquetaPosicion;
    private final JLabel etiquetaDanio;
    private final JLabel etiquetaFrecuencia;
    private final JLabel etiquetaAlcance;
    private final JLabel etiquetaRadio;
    private final JLabel etiquetaCosto;
    private final BarraVidaMars barraVida;

    private final BotonMars botonFicha;
    private final BotonMars botonMover;
    private final BotonMars botonRetirar;

    private Modo modo;
    private ConfiguracionComponente configuracionPendiente;
    private Defensa defensaPendiente;
    private Posicion posicionOriginal;
    private ComponenteCombate seleccionado;

    public PanelPreparacion(ControladorJuego controlador, Runnable accionCampania, Runnable accionArsenal, Consumer<ComponenteCombate> accionFicha, Runnable accionMenu, Runnable accionBatalla) {
        super(
                "PREPARACIÓN DE DEFENSAS",
                "Construye el perímetro antes de iniciar el ataque",
                ""
        );

        this.controlador = controlador;
        this.accionCampania = accionCampania;
        this.accionArsenal = accionArsenal;
        this.accionFicha = accionFicha;
        this.accionMenu = accionMenu;
        this.accionBatalla = accionBatalla;

        modo = Modo.NINGUNO;

        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setOpaque(false);

        JPanel resumen = new JPanel(
                new GridLayout(1, 3, 12, 0)
        );

        resumen.setOpaque(false);
        resumen.setPreferredSize(
                new Dimension(100, 74)
        );

        etiquetaNucleo = new JLabel("-");
        etiquetaCapacidad = new JLabel("-");
        etiquetaTablero = new JLabel("25 × 25");

        resumen.add(
                crearResumen("NÚCLEO", etiquetaNucleo)
        );

        resumen.add(
                crearResumen("CAPACIDAD", etiquetaCapacidad)
        );

        resumen.add(
                crearResumen("MAPA", etiquetaTablero)
        );

        JPanel centro = new JPanel(
                new BorderLayout(14, 0)
        );

        centro.setOpaque(false);

        mapa = new PanelMapaMars();
        mapa.setAccionCasilla(this::procesarCasilla);

        JScrollPane scrollMapa = new JScrollPane(mapa);
        scrollMapa.setBorder(null);

        scrollMapa.getViewport().setBackground(
                TemaMars.FONDO_PROFUNDO
        );

        scrollMapa.getVerticalScrollBar().setUnitIncrement(20);
        scrollMapa.getHorizontalScrollBar().setUnitIncrement(20);

        PanelMars inspector = new PanelMars();

        inspector.setPreferredSize(
                new Dimension(310, 100)
        );

        inspector.setMinimumSize(
                new Dimension(290, 100)
        );

        inspector.setLayout(
                new BoxLayout(inspector, BoxLayout.Y_AXIS)
        );

        etiquetaModo = new JLabel(
                "Selecciona una unidad del mapa"
        );

        etiquetaModo.setFont(
                TemaMars.textoSecundario()
        );

        etiquetaModo.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        etiquetaModo.setAlignmentX(LEFT_ALIGNMENT);

        imagenSeleccionada = new JLabel();
        imagenSeleccionada.setPreferredSize(
                new Dimension(150, 150)
        );

        imagenSeleccionada.setMaximumSize(
                new Dimension(150, 150)
        );

        imagenSeleccionada.setAlignmentX(CENTER_ALIGNMENT);

        imagenSeleccionada.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        etiquetaNombre = crearValorGrande("-");
        etiquetaTipo = crearValor("-");
        etiquetaId = crearValor("-");
        etiquetaNivel = crearValor("-");
        etiquetaPosicion = crearValor("-");

        barraVida = new BarraVidaMars();

        barraVida.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 30)
        );

        JPanel identidad = new JPanel(
                new GridLayout(4, 2, 6, 4)
        );

        identidad.setOpaque(false);

        identidad.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 125)
        );

        identidad.add(crearEtiqueta("Tipo"));
        identidad.add(etiquetaTipo);

        identidad.add(crearEtiqueta("ID"));
        identidad.add(etiquetaId);

        identidad.add(crearEtiqueta("Nivel"));
        identidad.add(etiquetaNivel);

        identidad.add(crearEtiqueta("Posición"));
        identidad.add(etiquetaPosicion);

        etiquetaDanio = crearValor("-");
        etiquetaFrecuencia = crearValor("-");
        etiquetaAlcance = crearValor("-");
        etiquetaRadio = crearValor("-");
        etiquetaCosto = crearValor("-");

        JPanel estadisticas = new JPanel(
                new GridLayout(5, 2, 6, 4)
        );

        estadisticas.setOpaque(false);

        estadisticas.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 150)
        );

        estadisticas.add(crearEtiqueta("Daño"));
        estadisticas.add(etiquetaDanio);

        estadisticas.add(crearEtiqueta("Frecuencia"));
        estadisticas.add(etiquetaFrecuencia);

        estadisticas.add(crearEtiqueta("Alcance"));
        estadisticas.add(etiquetaAlcance);

        estadisticas.add(crearEtiqueta("Radio"));
        estadisticas.add(etiquetaRadio);

        estadisticas.add(crearEtiqueta("Costo"));
        estadisticas.add(etiquetaCosto);

        botonFicha = BotonMars.secundario("Ficha");

        botonFicha.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );

        botonFicha.addActionListener(e -> {
            if (seleccionado != null) {
                accionFicha.accept(seleccionado);
            }
        });

        botonMover = BotonMars.secundario("Mover");

        botonMover.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );

        botonMover.addActionListener(e -> {
            if (seleccionado instanceof Defensa defensa) {
                iniciarMovimiento(defensa);
            }
        });

        botonRetirar = BotonMars.peligro("Retirar");

        botonRetirar.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );

        botonRetirar.addActionListener(
                e -> retirarSeleccionada()
        );

        JPanel accionesInspector = new JPanel(
                new GridLayout(1, 3, 8, 0)
        );

        accionesInspector.setOpaque(false);

        accionesInspector.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );

        accionesInspector.add(botonFicha);
        accionesInspector.add(botonMover);
        accionesInspector.add(botonRetirar);

        inspector.add(etiquetaModo);
        inspector.add(Box.createVerticalStrut(12));
        inspector.add(imagenSeleccionada);
        inspector.add(Box.createVerticalStrut(10));
        inspector.add(etiquetaNombre);
        inspector.add(Box.createVerticalStrut(8));
        inspector.add(barraVida);
        inspector.add(Box.createVerticalStrut(12));
        inspector.add(identidad);
        inspector.add(Box.createVerticalStrut(12));
        inspector.add(estadisticas);
        inspector.add(Box.createVerticalGlue());
        inspector.add(accionesInspector);

        centro.add(scrollMapa, BorderLayout.CENTER);
        centro.add(inspector, BorderLayout.EAST);

        JPanel pie = new JPanel(
                new BorderLayout(12, 0)
        );

        pie.setOpaque(false);

        JPanel izquierda = new JPanel(
                new GridLayout(1, 4, 8, 0)
        );

        izquierda.setOpaque(false);

        BotonMars campania = BotonMars.secundario(
                "Campaña"
        );

        campania.addActionListener(
                e -> accionCampania.run()
        );

        BotonMars arsenal = BotonMars.secundario(
                "Arsenal"
        );

        arsenal.addActionListener(
                e -> accionArsenal.run()
        );

        BotonMars guardar = BotonMars.secundario(
                "Guardar"
        );

        guardar.addActionListener(e -> guardar());

        BotonMars menu = BotonMars.secundario(
                "Menú"
        );

        menu.addActionListener(
                e -> accionMenu.run()
        );

        izquierda.add(campania);
        izquierda.add(arsenal);
        izquierda.add(guardar);
        izquierda.add(menu);

        BotonMars iniciar = BotonMars.primario(
                "INICIAR BATALLA"
        );

        iniciar.setPreferredSize(
                new Dimension(230, TemaMars.ALTURA_BOTON)
        );

        iniciar.addActionListener(
                e -> validarEIniciar()
        );

        pie.add(izquierda, BorderLayout.CENTER);
        pie.add(iniciar, BorderLayout.EAST);

        raiz.add(resumen, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);

        limpiarInspector();
    }

    private PanelMars crearResumen(String titulo, JLabel valor) {
        PanelMars panel = new PanelMars();
        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        valor.setFont(TemaMars.textoDestacado());
        valor.setForeground(TemaMars.TEXTO);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valor);

        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        return etiqueta;
    }

    private JLabel crearValor(String texto) {
        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO
        );

        return etiqueta;
    }

    private JLabel crearValorGrande(String texto) {
        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(
                TemaMars.tituloPanel()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO
        );

        etiqueta.setAlignmentX(CENTER_ALIGNMENT);

        return etiqueta;
    }

    public void preparar() {
        cancelarColocacion();

        seleccionado = null;

        mapa.setSeleccionada(null);

        actualizar();
    }

    public void actualizar() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null) {
            return;
        }

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
        );

        Escuadron escuadron =
                partida.getEscuadron();

        Tablero tablero =
                partida.getTablero();

        NucleoOxigeno nucleo =
                buscarNucleo(partida);

        if (nucleo != null) {
            etiquetaNucleo.setText(
                    formatear(nucleo.getVidaActual())
                    + " / "
                    + formatear(nucleo.getVidaMaxima())
            );
        } else {
            etiquetaNucleo.setText(
                    "No disponible"
            );
        }

        if (escuadron != null) {
            etiquetaCapacidad.setText(
                    escuadron.getCapacidadTotal()
                    + " total · "
                    + escuadron.capacidadUtilizada()
                    + " usada · "
                    + escuadron.capacidadRestante()
                    + " libre"
            );
        }

        if (tablero != null) {
            etiquetaTablero.setText(
                    tablero.getFilas()
                    + " × "
                    + tablero.getColumnas()
            );
        }

        mapa.actualizar(partida);
        actualizarInspector();
    }

    public void iniciarNueva(ConfiguracionComponente configuracion) {
        if (configuracion == null) {
            return;
        }

        actualizar();

        modo = Modo.NUEVA;
        configuracionPendiente = configuracion;
        defensaPendiente = null;
        posicionOriginal = null;

        etiquetaModo.setText(
                "Coloca "
                + configuracion.getNombre()
                + " · costo "
                + configuracion.getBase().getCostoCapacidad()
        );

        etiquetaModo.setForeground(
                TemaMars.CORRECTO
        );

        mapa.setModoColocacion(true);
    }

    public void iniciarExistente(Defensa defensa) {
        if (defensa == null) {
            return;
        }

        actualizar();

        modo = Modo.EXISTENTE;
        configuracionPendiente = null;
        defensaPendiente = defensa;
        posicionOriginal = null;

        etiquetaModo.setText(
                "Coloca "
                + defensa.getNombre()
                + " sin crear una nueva unidad"
        );

        etiquetaModo.setForeground(
                TemaMars.CORRECTO
        );

        mapa.setModoColocacion(true);
    }

    public void iniciarMovimiento(Defensa defensa) {
        if (defensa == null
                || defensa.getPosicion() == null) {

            return;
        }

        Partida partida =
                controlador.consultarEstado();

        if (partida.getMision() == null
                || partida.getMision().getEstado()
                != EstadoMision.PREPARACION) {

            DialogoMars.error(
                    this,
                    "No se puede mover",
                    "Las defensas solo pueden moverse durante la preparación."
            );

            return;
        }

        modo = Modo.MOVER;
        configuracionPendiente = null;
        defensaPendiente = defensa;

        posicionOriginal = new Posicion(
                defensa.getPosicion().getFila(),
                defensa.getPosicion().getColumna()
        );

        etiquetaModo.setText(
                "Mueve "
                + defensa.getNombre()
                + " · no consume capacidad adicional"
        );

        etiquetaModo.setForeground(
                TemaMars.ENERGIA
        );

        mapa.setModoColocacion(true);
    }

    private void cancelarColocacion() {
        modo = Modo.NINGUNO;
        configuracionPendiente = null;
        defensaPendiente = null;
        posicionOriginal = null;

        if (mapa != null) {
            mapa.setModoColocacion(false);
        }

        if (etiquetaModo != null) {
            etiquetaModo.setText(
                    "Selecciona una unidad del mapa"
            );

            etiquetaModo.setForeground(
                    TemaMars.TEXTO_SECUNDARIO
            );
        }
    }

    private void procesarCasilla(Posicion posicion) {
        if (posicion == null) {
            return;
        }

        if (modo != Modo.NINGUNO) {
            procesarColocacion(posicion);
            return;
        }

        Partida partida =
                controlador.consultarEstado();

        OcupanteMapa ocupante =
                partida.getTablero().obtener(posicion);

        mapa.setSeleccionada(posicion);

        if (ocupante instanceof ComponenteCombate componente) {
            seleccionado = componente;
        } else {
            seleccionado = null;
        }

        actualizarInspector();
    }

    private void procesarColocacion(Posicion posicion) {
        Partida partida =
                controlador.consultarEstado();

        Tablero tablero =
                partida.getTablero();

        try {
            Defensa colocada;

            if (modo == Modo.NUEVA) {
                colocada = controlador.colocarDefensa(
                        configuracionPendiente.getId(),
                        posicion
                );
            } else if (modo == Modo.EXISTENTE) {
                colocada =
                        controlador.colocarDefensaExistente(
                                defensaPendiente.getId(),
                                posicion
                        );
            } else {
                if (posicionOriginal != null
                        && posicionOriginal.equals(posicion)) {

                    cancelarColocacion();
                    actualizar();
                    return;
                }

                moverDefensa(
                        defensaPendiente,
                        posicionOriginal,
                        posicion,
                        tablero
                );

                colocada = defensaPendiente;
            }

            seleccionado = colocada;

            mapa.setSeleccionada(posicion);

            cancelarColocacion();
            actualizar();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "Posición no válida",
                    obtenerMensaje(e)
            );
        }
    }

    private void moverDefensa(Defensa defensa, Posicion origen, Posicion destino, Tablero tablero) {
        if (defensa == null
                || origen == null
                || destino == null) {

            throw new IllegalArgumentException(
                    "No existe una defensa válida para mover."
            );
        }

        if (!tablero.estaDentro(destino)) {
            throw new IllegalArgumentException(
                    "La posición está fuera del tablero."
            );
        }

        if (!tablero.estaLibre(destino)) {
            throw new IllegalStateException(
                    "La casilla seleccionada está ocupada."
            );
        }

        if (tablero.obtener(origen) != defensa) {
            throw new IllegalStateException(
                    "La defensa ya no se encuentra en su posición original."
            );
        }

        OcupanteMapa retirado =
                tablero.retirar(origen);

        if (retirado != defensa) {
            throw new IllegalStateException(
                    "No fue posible retirar temporalmente la defensa."
            );
        }

        if (!tablero.colocar(defensa, destino)) {
            tablero.colocar(defensa, origen);

            defensa.fijarPosicionInicial(origen);

            throw new IllegalStateException(
                    "No fue posible mover la defensa."
            );
        }

        defensa.fijarPosicionInicial(destino);
    }

    private void retirarSeleccionada() {
        if (!(seleccionado instanceof Defensa defensa)) {
            return;
        }

        boolean confirmar = DialogoMars.confirmar(
                this,
                "Retirar defensa",
                "La defensa volverá a tus unidades disponibles y liberará su capacidad para esta misión.",
                "Retirar"
        );

        if (!confirmar) {
            return;
        }

        try {
            controlador.retirarDefensa(
                    defensa.getId()
            );

            seleccionado = null;

            mapa.setSeleccionada(null);

            cancelarColocacion();
            actualizar();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo retirar",
                    obtenerMensaje(e)
            );
        }
    }

    private void actualizarInspector() {
        if (seleccionado == null) {
            limpiarInspector();
            return;
        }

        imagenSeleccionada.setIcon(
                GestorImagenes.cargar(
                        seleccionado.getRutaImagenActual(),
                        145,
                        145
                )
        );

        etiquetaNombre.setText(
                seleccionado.getNombre()
        );

        etiquetaTipo.setText(
                nombreTipo(seleccionado)
        );

        etiquetaId.setText(
                abreviarId(seleccionado.getId())
        );

        etiquetaNivel.setText(
                String.valueOf(seleccionado.getNivel())
        );

        etiquetaPosicion.setText(
                seleccionado.getPosicion() == null
                        ? "Sin colocar"
                        : seleccionado.getPosicion().toString()
        );

        barraVida.setVida(
                seleccionado.getVidaActual(),
                seleccionado.getVidaMaxima()
        );

        etiquetaDanio.setText(
                formatear(seleccionado.getDanioGolpe())
        );

        etiquetaFrecuencia.setText(
                formatear(seleccionado.getFrecuenciaAtaque())
        );

        etiquetaAlcance.setText(
                String.valueOf(seleccionado.getAlcance())
        );

        etiquetaRadio.setText(
                String.valueOf(seleccionado.getRadioEfecto())
        );

        etiquetaCosto.setText(
                String.valueOf(seleccionado.getCostoCapacidad())
        );

        botonFicha.setVisible(true);

        boolean defensa =
                seleccionado instanceof Defensa;

        botonMover.setVisible(defensa);
        botonRetirar.setVisible(defensa);

        botonMover.setEnabled(
                defensa
                && seleccionado.getPosicion() != null
        );

        botonRetirar.setEnabled(
                defensa
                && seleccionado.getPosicion() != null
        );
    }

    private void limpiarInspector() {
        imagenSeleccionada.setIcon(null);

        etiquetaNombre.setText(
                "Selecciona una unidad"
        );

        etiquetaTipo.setText("-");
        etiquetaId.setText("-");
        etiquetaNivel.setText("-");
        etiquetaPosicion.setText("-");

        barraVida.setVida(0, 1);

        etiquetaDanio.setText("-");
        etiquetaFrecuencia.setText("-");
        etiquetaAlcance.setText("-");
        etiquetaRadio.setText("-");
        etiquetaCosto.setText("-");

        botonFicha.setVisible(false);
        botonMover.setVisible(false);
        botonRetirar.setVisible(false);
    }

    private void guardar() {
        try {
            controlador.guardarPartidaActual();

            DialogoMars.exito(
                    this,
                    "Partida guardada",
                    "La preparación quedó guardada correctamente."
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    obtenerMensaje(e)
            );
        }
    }

    private void validarEIniciar() {
        cancelarColocacion();

        try {
            validarPreparacionInterna();

            controlador.iniciarBatalla();

            accionBatalla.run();

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo iniciar la batalla",
                    obtenerMensaje(e)
            );

            actualizar();
        }
    }

    private void validarPreparacionInterna() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null) {
            throw new IllegalStateException(
                    "No existe una partida activa."
            );
        }

        Mision mision =
                partida.getMision();

        Escuadron escuadron =
                partida.getEscuadron();

        Tablero tablero =
                partida.getTablero();

        if (mision == null) {
            throw new IllegalStateException(
                    "No existe una misión preparada."
            );
        }

        if (!mision.isGenerada()) {
            throw new IllegalStateException(
                    "La misión todavía no ha sido generada correctamente."
            );
        }

        if (mision.getEstado()
                != EstadoMision.PREPARACION) {

            throw new IllegalStateException(
                    "La misión no se encuentra en preparación."
            );
        }

        if (escuadron == null) {
            throw new IllegalStateException(
                    "La partida no contiene un escuadrón válido."
            );
        }

        if (tablero == null) {
            throw new IllegalStateException(
                    "La partida no contiene un tablero válido."
            );
        }

        if (!validarNucleo(partida)) {
            throw new IllegalStateException(
                    "El núcleo de oxígeno no está correctamente colocado."
            );
        }

        if (escuadron.capacidadUtilizada()
                > escuadron.getCapacidadTotal()
                || escuadron.capacidadRestante() < 0) {

            throw new IllegalStateException(
                    "La capacidad utilizada por el escuadrón no es válida."
            );
        }

        if (!validarPosiciones(
                mision,
                escuadron,
                tablero
        )) {

            throw new IllegalStateException(
                    "Uno o más participantes no están correctamente colocados."
            );
        }
    }

    private boolean validarNucleo(Partida partida) {
        NucleoOxigeno nucleo =
                buscarNucleo(partida);

        if (nucleo == null
                || nucleo.getPosicion() == null
                || partida.getTablero() == null) {

            return false;
        }

        return partida.getTablero()
                .obtener(nucleo.getPosicion())
                == nucleo;
    }

    private boolean validarPosiciones(Mision mision, Escuadron escuadron, Tablero tablero) {
        if (mision == null
                || escuadron == null
                || tablero == null
                || !mision.isGenerada()
                || mision.getEstado()
                != EstadoMision.PREPARACION
                || mision.getCriaturas().isEmpty()) {

            return false;
        }

        for (ComponenteCombate criatura
                : mision.getCriaturas()) {

            if (criatura == null
                    || criatura.getPosicion() == null
                    || tablero.obtener(
                            criatura.getPosicion()
                    ) != criatura) {

                return false;
            }
        }

        for (Defensa defensa
                : escuadron.getDefensas()) {

            if (defensa != null
                    && escuadron.getSeleccionadas()
                            .contains(defensa.getId())) {

                if (defensa.getPosicion() == null
                        || tablero.obtener(
                                defensa.getPosicion()
                        ) != defensa) {

                    return false;
                }
            }
        }

        return true;
    }

    private NucleoOxigeno buscarNucleo(Partida partida) {
        if (partida == null
                || partida.getTablero() == null) {

            return null;
        }

        Tablero tablero =
                partida.getTablero();

        for (int fila = 0;
                fila < tablero.getFilas();
                fila++) {

            for (int columna = 0;
                    columna < tablero.getColumnas();
                    columna++) {

                OcupanteMapa ocupante =
                        tablero.obtener(
                                new Posicion(
                                        fila,
                                        columna
                                )
                        );

                if (ocupante
                        instanceof NucleoOxigeno nucleo) {

                    return nucleo;
                }
            }
        }

        return null;
    }

    private String nombreTipo(ComponenteCombate componente) {
        if (componente instanceof NucleoOxigeno) {
            return "Núcleo de oxígeno";
        }

        return componente
                .getClass()
                .getSimpleName();
    }

    private String abreviarId(String id) {
        if (id == null
                || id.length() <= 12) {

            return id == null ? "-" : id;
        }

        return id.substring(0, 12) + "…";
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##")
                .format(valor);
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
}