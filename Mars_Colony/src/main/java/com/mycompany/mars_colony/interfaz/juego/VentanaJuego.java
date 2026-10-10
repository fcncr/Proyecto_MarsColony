package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.combate.defensa.Barrera;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.nio.file.Path;
import com.mycompany.mars_colony.util.RutasAplicacion;

public class VentanaJuego extends JFrame {

    private static final int TAMANO_CASILLA = 24;
    private static final int SEPARACION_CASILLAS = 1;
    private static final int ANCHO_PANEL_LATERAL = 360;
    private static final int INTERVALO_REFRESCO_BATALLA = 100;

    private final ControladorJuego controlador;
    private final Map<String, ImageIcon> cacheIconos;

    private final JLabel etiquetaComandante;
    private final JLabel etiquetaMision;
    private final JLabel etiquetaEstadoMision;
    private final JLabel etiquetaCampania;
    private final JLabel etiquetaCoordenadaCasilla;
    private final JLabel etiquetaCapacidadTotal;
    private final JLabel etiquetaCapacidadUtilizada;
    private final JLabel etiquetaCapacidadRestante;

    private final DefaultComboBoxModel<ConfiguracionComponente> modeloDefensasDisponibles;
    private final JComboBox<ConfiguracionComponente> comboDefensasDisponibles;
    private final DefaultComboBoxModel<Defensa> modeloDefensasExistentes;
    private final JComboBox<Defensa> comboDefensasExistentes;
    private final JButton botonPrepararDefensa;
    private final JButton botonPrepararDefensaExistente;
    private final JButton botonRetirarDefensa;
    private final JButton botonCancelarColocacion;
    private final JLabel etiquetaModoColocacion;
    private ConfiguracionComponente configuracionPendiente;
    private Defensa defensaExistentePendiente;

    private final JPanel panelTablero;
    private final JPanel panelContenedorCuadricula;
    private final JPanel panelCuadricula;
    private JButton[][] botonesCasilla;

    private final JLabel etiquetaDetalleNombre;
    private final JLabel etiquetaDetalleTipo;
    private final JLabel etiquetaDetalleBando;
    private final JLabel etiquetaDetalleEstado;
    private final JLabel etiquetaDetalleVida;
    private final JLabel etiquetaDetalleDanio;
    private final JLabel etiquetaDetalleFrecuencia;
    private final JLabel etiquetaDetalleAlcance;
    private final JLabel etiquetaDetalleRadio;
    private final JLabel etiquetaDetalleCosto;
    private final JLabel etiquetaDetallePosicion;
    private final JLabel etiquetaGolpesRealizados;
    private final JLabel etiquetaGolpesRecibidos;
    private final JLabel etiquetaDanioCausado;
    private final JLabel etiquetaDanioRecibido;

    private final JButton botonNuevaPartida;
    private final JButton botonGuardarPartida;
    private final JButton botonCargarPartida;
    private final JButton botonIniciarBatalla;
    private final JButton botonActualizar;
    private final JButton botonCerrar;

    private final Timer temporizadorBatalla;

    private boolean resultadoBatallaMostrado;
    private String idUnidadSeleccionada;

    public VentanaJuego(Partida partida) {
        this(new ControladorJuego(partida));
    }

    public VentanaJuego(ControladorJuego controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador del juego no puede ser nulo.");
        }

        this.controlador = controlador;
        this.cacheIconos = new HashMap<>();
        this.etiquetaComandante = new JLabel("-");
        this.etiquetaMision = new JLabel("-");
        this.etiquetaEstadoMision = new JLabel("-");
        this.etiquetaCampania = new JLabel("-");
        this.etiquetaCoordenadaCasilla = new JLabel("Casilla: -");
        this.etiquetaCapacidadTotal = new JLabel("-");
        this.etiquetaCapacidadUtilizada = new JLabel("-");
        this.etiquetaCapacidadRestante = new JLabel("-");
        this.modeloDefensasDisponibles = new DefaultComboBoxModel<>();
        this.comboDefensasDisponibles = new JComboBox<>(modeloDefensasDisponibles);
        this.modeloDefensasExistentes = new DefaultComboBoxModel<>();
        this.comboDefensasExistentes = new JComboBox<>(modeloDefensasExistentes);
        this.botonPrepararDefensa = new JButton("Nueva");
        this.botonPrepararDefensaExistente = new JButton("Reutilizar");
        this.botonRetirarDefensa = new JButton("Retirar seleccionada");
        this.botonCancelarColocacion = new JButton("Cancelar");
        this.etiquetaModoColocacion = new JLabel("Sin defensa preparada");
        this.configuracionPendiente = null;
        this.defensaExistentePendiente = null;
        this.panelTablero = new JPanel(new BorderLayout());
        this.panelContenedorCuadricula = new JPanel(new GridBagLayout());
        this.panelCuadricula = new JPanel();
        this.botonesCasilla = null;
        this.etiquetaDetalleNombre = new JLabel("-");
        this.etiquetaDetalleTipo = new JLabel("-");
        this.etiquetaDetalleBando = new JLabel("-");
        this.etiquetaDetalleEstado = new JLabel("-");
        this.etiquetaDetalleVida = new JLabel("-");
        this.etiquetaDetalleDanio = new JLabel("-");
        this.etiquetaDetalleFrecuencia = new JLabel("-");
        this.etiquetaDetalleAlcance = new JLabel("-");
        this.etiquetaDetalleRadio = new JLabel("-");
        this.etiquetaDetalleCosto = new JLabel("-");
        this.etiquetaDetallePosicion = new JLabel("-");
        this.etiquetaGolpesRealizados = new JLabel("-");
        this.etiquetaGolpesRecibidos = new JLabel("-");
        this.etiquetaDanioCausado = new JLabel("-");
        this.etiquetaDanioRecibido = new JLabel("-");
        this.botonNuevaPartida = new JButton("Nueva partida");
        this.botonGuardarPartida = new JButton("Guardar partida");
        this.botonCargarPartida = new JButton("Cargar partida");
        this.botonIniciarBatalla = new JButton("Iniciar batalla");
        this.botonActualizar = new JButton("Actualizar estado");
        this.botonCerrar = new JButton("Cerrar");
        this.temporizadorBatalla = new Timer(INTERVALO_REFRESCO_BATALLA, e -> refrescarBatalla());
        this.temporizadorBatalla.setCoalesce(true);
        this.resultadoBatallaMostrado = false;
        this.idUnidadSeleccionada = null;

        configurarRenderDefensas();
        configurarVentana();
        construirInterfaz();
        conectarEventos();
        actualizarVista();
        actualizarModoColocacion();
    }

    private void configurarVentana() {
        setTitle("Mars Colony - Juego");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 760));
        setSize(1250, 820);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarVentana();
            }
        });
    }

    private void configurarRenderDefensas() {
        comboDefensasDisponibles.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice, boolean seleccionado, boolean tieneFoco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(lista, valor, indice, seleccionado, tieneFoco);

                if (valor instanceof ConfiguracionComponente configuracion) {
                    etiqueta.setText(configuracion.getNombre() + " | " + configuracion.getTipo() + " | costo " + configuracion.getBase().getCostoCapacidad());
                }

                return etiqueta;
            }
        });

        comboDefensasExistentes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice, boolean seleccionado, boolean tieneFoco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(lista, valor, indice, seleccionado, tieneFoco);

                if (valor instanceof Defensa defensa) {
                    etiqueta.setText(defensa.getNombre() + " | nivel " + defensa.getNivel() + " | costo " + defensa.getCostoCapacidad());
                }

                return etiqueta;
            }
        });
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel(new BorderLayout(6, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        contenido.add(crearPanelEstadoPartida(), BorderLayout.NORTH);
        contenido.add(crearPanelCentral(), BorderLayout.CENTER);
        contenido.add(crearPanelControles(), BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    private JPanel crearPanelEstadoPartida() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Estado de la partida"));
        panel.add(new JLabel("Comandante:"));
        panel.add(new JLabel("Misión actual:"));
        panel.add(new JLabel("Estado misión:"));
        panel.add(new JLabel("Campaña:"));
        panel.add(etiquetaComandante);
        panel.add(etiquetaMision);
        panel.add(etiquetaEstadoMision);
        panel.add(etiquetaCampania);
        return panel;
    }

    private JPanel crearPanelCentral() {
        prepararContenedorTablero();
        JPanel panelCentral = new JPanel(new BorderLayout(8, 0));
        panelCentral.add(panelTablero, BorderLayout.CENTER);
        panelCentral.add(crearPanelLateral(), BorderLayout.EAST);
        return panelCentral;
    }

    private void prepararContenedorTablero() {
        panelTablero.removeAll();
        panelTablero.setBorder(BorderFactory.createTitledBorder("Tablero"));
        panelContenedorCuadricula.removeAll();
        panelContenedorCuadricula.add(panelCuadricula);
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelInferior.add(etiquetaCoordenadaCasilla);
        panelTablero.add(panelContenedorCuadricula, BorderLayout.CENTER);
        panelTablero.add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setPreferredSize(new Dimension(ANCHO_PANEL_LATERAL, 650));
        panel.setMinimumSize(new Dimension(ANCHO_PANEL_LATERAL, 450));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 6, 6));
        panelSuperior.add(crearPanelCapacidad());
        panelSuperior.add(crearPanelPreparacionDefensas());
        panelSuperior.add(crearLeyendaTablero());

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(crearPanelDetalleUnidad(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCapacidad() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 6, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Capacidad del escuadrón"));
        panel.add(new JLabel("Total:"));
        panel.add(etiquetaCapacidadTotal);
        panel.add(new JLabel("Utilizada:"));
        panel.add(etiquetaCapacidadUtilizada);
        panel.add(new JLabel("Restante:"));
        panel.add(etiquetaCapacidadRestante);
        return panel;
    }

    private JPanel crearPanelPreparacionDefensas() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Preparación de defensas"));

        JPanel nueva = new JPanel(new BorderLayout(4, 0));
        nueva.add(comboDefensasDisponibles, BorderLayout.CENTER);
        nueva.add(botonPrepararDefensa, BorderLayout.EAST);

        JPanel existente = new JPanel(new BorderLayout(4, 0));
        existente.add(comboDefensasExistentes, BorderLayout.CENTER);
        existente.add(botonPrepararDefensaExistente, BorderLayout.EAST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        acciones.add(botonRetirarDefensa);
        acciones.add(botonCancelarColocacion);

        panel.add(new JLabel("Crear nueva:"));
        panel.add(nueva);
        panel.add(new JLabel("Reutilizar existente:"));
        panel.add(existente);
        panel.add(acciones);

        return panel;
    }

    private JPanel crearLeyendaTablero() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 4, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Leyenda"));
        panel.add(new JLabel("· Libre"));
        panel.add(new JLabel("N Núcleo"));
        panel.add(new JLabel("O Obstáculo"));
        panel.add(new JLabel("D Defensa"));
        panel.add(new JLabel("B Barrera"));
        panel.add(new JLabel("C Criatura"));
        return panel;
    }

    private JPanel crearPanelDetalleUnidad() {
        JPanel panel = new JPanel(new GridLayout(15, 2, 5, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Unidad seleccionada"));
        panel.add(new JLabel("Nombre:"));
        panel.add(etiquetaDetalleNombre);
        panel.add(new JLabel("Tipo:"));
        panel.add(etiquetaDetalleTipo);
        panel.add(new JLabel("Bando:"));
        panel.add(etiquetaDetalleBando);
        panel.add(new JLabel("Estado:"));
        panel.add(etiquetaDetalleEstado);
        panel.add(new JLabel("Vida:"));
        panel.add(etiquetaDetalleVida);
        panel.add(new JLabel("Daño:"));
        panel.add(etiquetaDetalleDanio);
        panel.add(new JLabel("Frecuencia:"));
        panel.add(etiquetaDetalleFrecuencia);
        panel.add(new JLabel("Alcance:"));
        panel.add(etiquetaDetalleAlcance);
        panel.add(new JLabel("Radio:"));
        panel.add(etiquetaDetalleRadio);
        panel.add(new JLabel("Costo:"));
        panel.add(etiquetaDetalleCosto);
        panel.add(new JLabel("Posición:"));
        panel.add(etiquetaDetallePosicion);
        panel.add(new JLabel("Golpes realizados:"));
        panel.add(etiquetaGolpesRealizados);
        panel.add(new JLabel("Golpes recibidos:"));
        panel.add(etiquetaGolpesRecibidos);
        panel.add(new JLabel("Daño causado:"));
        panel.add(etiquetaDanioCausado);
        panel.add(new JLabel("Daño recibido:"));
        panel.add(etiquetaDanioRecibido);
        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Controles de partida"));
        panel.add(botonNuevaPartida);
        panel.add(botonGuardarPartida);
        panel.add(botonCargarPartida);
        panel.add(botonIniciarBatalla);
        panel.add(botonActualizar);
        panel.add(botonCerrar);
        return panel;
    }

    private void conectarEventos() {
        botonNuevaPartida.addActionListener(e -> crearNuevaPartidaDesdeInterfaz());
        botonGuardarPartida.addActionListener(e -> guardarPartidaDesdeInterfaz());
        botonCargarPartida.addActionListener(e -> cargarPartidaDesdeInterfaz());
        botonIniciarBatalla.addActionListener(e -> iniciarBatallaDesdeInterfaz());
        botonActualizar.addActionListener(e -> actualizarVista());
        botonCerrar.addActionListener(e -> cerrarVentana());
        botonPrepararDefensa.addActionListener(e -> prepararDefensaNuevaSeleccionada());
        botonPrepararDefensaExistente.addActionListener(e -> prepararDefensaExistenteSeleccionada());
        botonRetirarDefensa.addActionListener(e -> retirarDefensaSeleccionada());
        botonCancelarColocacion.addActionListener(e -> cancelarColocacion());
    }

    public void actualizarVista() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::actualizarVista);
            return;
        }

        Partida partida = controlador.consultarEstado();
        pintarEstado(partida);
        actualizarPreparacionDefensas();
        refrescarDetalleSeleccionado();
        actualizarControlesBatalla();
    }

    public void pintarEstado(Partida partida) {
        if (partida == null) {
            mostrarEstadoVacio();
            return;
        }

        etiquetaComandante.setText(partida.getNombreComandante());
        etiquetaMision.setText(String.valueOf(partida.getMisionActual()));
        etiquetaCampania.setText(partida.isCampaniaFinalizada() ? "FINALIZADA" : "ACTIVA");

        Mision mision = partida.getMision();
        etiquetaEstadoMision.setText(mision == null || mision.getEstado() == null ? "-" : mision.getEstado().name());

        Escuadron escuadron = partida.getEscuadron();

        if (escuadron == null) {
            etiquetaCapacidadTotal.setText("-");
            etiquetaCapacidadUtilizada.setText("-");
            etiquetaCapacidadRestante.setText("-");
        } else {
            etiquetaCapacidadTotal.setText(String.valueOf(escuadron.getCapacidadTotal()));
            etiquetaCapacidadUtilizada.setText(String.valueOf(escuadron.capacidadUtilizada()));
            etiquetaCapacidadRestante.setText(String.valueOf(escuadron.capacidadRestante()));
        }

        actualizarTablero(partida.getTablero());
    }

    private void actualizarPreparacionDefensas() {
        actualizarDefensasNuevas();
        actualizarDefensasExistentes();
    }

    private void actualizarDefensasNuevas() {
        String idSeleccionado = null;
        Object seleccionActual = comboDefensasDisponibles.getSelectedItem();

        if (seleccionActual instanceof ConfiguracionComponente configuracion) {
            idSeleccionado = configuracion.getId();
        }

        modeloDefensasDisponibles.removeAllElements();

        for (ConfiguracionComponente configuracion : controlador.listarDefensasDisponibles()) {
            modeloDefensasDisponibles.addElement(configuracion);
        }

        if (idSeleccionado != null) {
            for (int i = 0; i < modeloDefensasDisponibles.getSize(); i++) {
                ConfiguracionComponente configuracion = modeloDefensasDisponibles.getElementAt(i);

                if (idSeleccionado.equals(configuracion.getId())) {
                    comboDefensasDisponibles.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void actualizarDefensasExistentes() {
        String idSeleccionado = null;
        Object seleccionActual = comboDefensasExistentes.getSelectedItem();

        if (seleccionActual instanceof Defensa defensa) {
            idSeleccionado = defensa.getId();
        }

        modeloDefensasExistentes.removeAllElements();

        for (Defensa defensa : controlador.listarDefensasNoColocadas()) {
            modeloDefensasExistentes.addElement(defensa);
        }

        if (idSeleccionado != null) {
            for (int i = 0; i < modeloDefensasExistentes.getSize(); i++) {
                Defensa defensa = modeloDefensasExistentes.getElementAt(i);

                if (idSeleccionado.equals(defensa.getId())) {
                    comboDefensasExistentes.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void prepararDefensaNuevaSeleccionada() {
        Object seleccion = comboDefensasDisponibles.getSelectedItem();

        if (!(seleccion instanceof ConfiguracionComponente configuracion)) {
            JOptionPane.showMessageDialog(this, "No hay una defensa nueva seleccionada.", "Preparación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        configuracionPendiente = configuracion;
        defensaExistentePendiente = null;
        actualizarModoColocacion();
        actualizarControlesBatalla();
    }

    private void prepararDefensaExistenteSeleccionada() {
        Object seleccion = comboDefensasExistentes.getSelectedItem();

        if (!(seleccion instanceof Defensa defensa)) {
            JOptionPane.showMessageDialog(this, "No hay una defensa existente seleccionada.", "Preparación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        defensaExistentePendiente = defensa;
        configuracionPendiente = null;
        actualizarModoColocacion();
        actualizarControlesBatalla();
    }

    private void retirarDefensaSeleccionada() {
        if (idUnidadSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una defensa colocada.", "Preparación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ComponenteCombate componente = buscarComponente(idUnidadSeleccionada);

        if (!(componente instanceof Defensa defensa) || defensa.getPosicion() == null) {
            JOptionPane.showMessageDialog(this, "La unidad seleccionada no es una defensa colocada.", "Preparación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controlador.retirarDefensa(defensa.getId());
            cancelarColocacion();
            idUnidadSeleccionada = defensa.getId();
            actualizarVista();
            mostrarDetalleParticipante(defensa);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo retirar la defensa", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelarColocacion() {
        configuracionPendiente = null;
        defensaExistentePendiente = null;
        actualizarModoColocacion();
    }

    private void actualizarModoColocacion() {
        if (configuracionPendiente != null) {
            etiquetaModoColocacion.setText("Nueva: " + configuracionPendiente.getNombre() + " | costo " + configuracionPendiente.getBase().getCostoCapacidad());
            botonCancelarColocacion.setEnabled(true);
            return;
        }

        if (defensaExistentePendiente != null) {
            etiquetaModoColocacion.setText("Reubicar: " + defensaExistentePendiente.getNombre() + " | nivel " + defensaExistentePendiente.getNivel());
            botonCancelarColocacion.setEnabled(true);
            return;
        }

        etiquetaModoColocacion.setText("Sin defensa preparada");
        botonCancelarColocacion.setEnabled(false);
    }

    private void actualizarTablero(Tablero tablero) {
        if (tablero == null) {
            mostrarTableroVacio();
            return;
        }

        asegurarCuadricula(tablero);

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                actualizarBotonCasilla(botonesCasilla[fila][columna], posicion, tablero.obtener(posicion));
            }
        }
    }

    private void asegurarCuadricula(Tablero tablero) {
        if (botonesCasilla != null && botonesCasilla.length == tablero.getFilas() && botonesCasilla[0].length == tablero.getColumnas()) {
            return;
        }

        panelCuadricula.removeAll();
        panelCuadricula.setLayout(new GridLayout(tablero.getFilas() + 1, tablero.getColumnas() + 1, SEPARACION_CASILLAS, SEPARACION_CASILLAS));
        botonesCasilla = new JButton[tablero.getFilas()][tablero.getColumnas()];

        panelCuadricula.add(crearEtiquetaCoordenada(""));

        for (int columna = 0; columna < tablero.getColumnas(); columna++) {
            panelCuadricula.add(crearEtiquetaCoordenada(String.valueOf(columna)));
        }

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            panelCuadricula.add(crearEtiquetaCoordenada(String.valueOf(fila)));

            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                JButton boton = crearBotonCasilla(posicion);
                botonesCasilla[fila][columna] = boton;
                panelCuadricula.add(boton);
            }
        }

        int ancho = ((tablero.getColumnas() + 1) * TAMANO_CASILLA) + (tablero.getColumnas() * SEPARACION_CASILLAS);
        int alto = ((tablero.getFilas() + 1) * TAMANO_CASILLA) + (tablero.getFilas() * SEPARACION_CASILLAS);
        Dimension tamanoCuadricula = new Dimension(ancho, alto);

        panelCuadricula.setPreferredSize(tamanoCuadricula);
        panelCuadricula.setMinimumSize(tamanoCuadricula);
        panelCuadricula.setMaximumSize(tamanoCuadricula);
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }

    private JLabel crearEtiquetaCoordenada(String texto) {
        JLabel etiqueta = new JLabel(texto, JLabel.CENTER);
        etiqueta.setPreferredSize(new Dimension(TAMANO_CASILLA, TAMANO_CASILLA));
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 9f));
        return etiqueta;
    }

    private JButton crearBotonCasilla(Posicion posicion) {
        JButton boton = new JButton("·");
        Dimension tamano = new Dimension(TAMANO_CASILLA, TAMANO_CASILLA);
        boton.setPreferredSize(tamano);
        boton.setMinimumSize(tamano);
        boton.setMaximumSize(tamano);
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setFont(boton.getFont().deriveFont(Font.BOLD, 10f));
        boton.setFocusable(false);
        boton.setIconTextGap(0);
        boton.addActionListener(e -> seleccionarCasilla(posicion));
        return boton;
    }

    private void actualizarBotonCasilla(JButton boton, Posicion posicion, OcupanteMapa ocupante) {
        boton.setIcon(null);
        boton.setText("");
        boton.setOpaque(true);
        boton.setForeground(Color.BLACK);

        if (ocupante == null) {
            boton.setText("·");
            boton.setBackground(UIManager.getColor("Button.background"));
            boton.setToolTipText("Casilla " + posicion + " - terreno libre");
            return;
        }

        if (ocupante instanceof NucleoOxigeno nucleo) {
            if (!aplicarImagenComponente(boton, nucleo)) {
                boton.setText("N");
            }

            boton.setBackground(new Color(255, 193, 7));
            boton.setToolTipText("Casilla " + posicion + " - Núcleo: " + nucleo.getNombre());
            return;
        }

        if (ocupante instanceof Obstaculo obstaculo) {
            ImageIcon icono = cargarIcono(obstaculo.getRutaImagen());

            if (icono != null) {
                boton.setIcon(icono);
                boton.setText("");
            } else {
                boton.setText("O");
            }

            boton.setBackground(new Color(110, 110, 110));
            boton.setForeground(Color.WHITE);
            boton.setToolTipText("Casilla " + posicion + " - Obstáculo");
            return;
        }

        if (ocupante instanceof Barrera barrera) {
            if (!aplicarImagenComponente(boton, barrera)) {
                boton.setText("B");
            }

            boton.setBackground(new Color(70, 130, 180));
            boton.setForeground(Color.WHITE);
            boton.setToolTipText("Casilla " + posicion + " - Barrera: " + barrera.getNombre());
            return;
        }

        if (ocupante instanceof Defensa defensa) {
            if (!aplicarImagenComponente(boton, defensa)) {
                boton.setText("D");
            }

            boton.setBackground(new Color(46, 125, 50));
            boton.setForeground(Color.WHITE);
            boton.setToolTipText("Casilla " + posicion + " - Defensa: " + defensa.getNombre());
            return;
        }

        if (ocupante instanceof Criatura criatura) {
            if (!aplicarImagenComponente(boton, criatura)) {
                boton.setText("C");
            }

            boton.setBackground(new Color(198, 40, 40));
            boton.setForeground(Color.WHITE);
            boton.setToolTipText("Casilla " + posicion + " - Criatura: " + criatura.getNombre());
            return;
        }

        boton.setText("?");
        boton.setBackground(new Color(158, 158, 158));
        boton.setToolTipText("Casilla " + posicion + " - " + ocupante.getClass().getSimpleName());
    }

    private boolean aplicarImagenComponente(JButton boton, ComponenteCombate componente) {
        if (boton == null || componente == null) {
            return false;
        }

        ImageIcon icono = cargarIcono(componente.getRutaImagenActual());

        if (icono == null) {
            return false;
        }

        boton.setIcon(icono);
        boton.setText("");
        return true;
    }

    private ImageIcon cargarIcono(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        String rutaLimpia = RutasAplicacion.limpiarRuta(ruta);
        ImageIcon almacenado = cacheIconos.get(rutaLimpia);

        if (almacenado != null) {
            return almacenado;
        }

        URL recurso = getClass().getClassLoader().getResource(rutaLimpia);

        if (recurso != null) {
            ImageIcon icono = escalarIcono(new ImageIcon(recurso));

            if (icono != null) {
                cacheIconos.put(rutaLimpia, icono);
            }

            return icono;
        }

        Path archivo = RutasAplicacion.buscarArchivo(rutaLimpia);

        if (archivo == null) {
            return null;
        }

        ImageIcon icono = escalarIcono(new ImageIcon(archivo.toAbsolutePath().toString()));

        if (icono != null) {
            cacheIconos.put(rutaLimpia, icono);
        }

        return icono;
    }

    private ImageIcon escalarIcono(ImageIcon original) {
        if (original == null || original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            return null;
        }

        int tamano = TAMANO_CASILLA - 3;
        Image imagen = original.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }

    private void seleccionarCasilla(Posicion posicion) {
        etiquetaCoordenadaCasilla.setText("Casilla: " + posicion);

        if (configuracionPendiente != null || defensaExistentePendiente != null) {
            intentarColocarDefensa(posicion);
            return;
        }

        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getTablero() == null) {
            limpiarDetalle();
            return;
        }

        OcupanteMapa ocupante = partida.getTablero().obtener(posicion);

        if (ocupante instanceof ComponenteCombate componente) {
            idUnidadSeleccionada = componente.getId();
            mostrarDetalleParticipante(componente);
        } else {
            limpiarDetalle();
        }

        actualizarControlesBatalla();
    }

    private void intentarColocarDefensa(Posicion posicion) {
        try {
            Defensa defensa;

            if (defensaExistentePendiente != null) {
                defensa = controlador.colocarDefensaExistente(defensaExistentePendiente.getId(), posicion);
            } else if (configuracionPendiente != null) {
                defensa = controlador.colocarDefensa(configuracionPendiente.getId(), posicion);
            } else {
                return;
            }

            cancelarColocacion();
            idUnidadSeleccionada = defensa.getId();
            actualizarVista();
            mostrarDetalleParticipante(defensa);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo colocar la defensa", JOptionPane.WARNING_MESSAGE);
            actualizarVista();
        }
    }

    public void mostrarDetalle(String id) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> mostrarDetalle(id));
            return;
        }

        if (id == null || id.isBlank()) {
            limpiarDetalle();
            return;
        }

        ComponenteCombate participante = buscarComponente(id);

        if (participante == null) {
            limpiarDetalle();
            return;
        }

        idUnidadSeleccionada = id;
        mostrarDetalleParticipante(participante);
    }

    private ComponenteCombate buscarComponente(String id) {
        if (id == null) {
            return null;
        }

        Partida partida = controlador.consultarEstado();

        if (partida == null) {
            return null;
        }

        ComponenteCombate componente = buscarComponenteEnTablero(partida.getTablero(), id);

        if (componente != null) {
            return componente;
        }

        if (partida.getMision() != null) {
            for (ComponenteCombate participante : partida.getMision().getParticipantes()) {
                if (participante != null && id.equals(participante.getId())) {
                    return participante;
                }
            }
        }

        if (partida.getEscuadron() != null) {
            for (Defensa defensa : partida.getEscuadron().getDefensas()) {
                if (defensa != null && id.equals(defensa.getId())) {
                    return defensa;
                }
            }
        }

        return null;
    }

    private ComponenteCombate buscarComponenteEnTablero(Tablero tablero, String id) {
        if (tablero == null || id == null) {
            return null;
        }

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                OcupanteMapa ocupante = tablero.obtener(new Posicion(fila, columna));

                if (ocupante instanceof ComponenteCombate componente && id.equals(componente.getId())) {
                    return componente;
                }
            }
        }

        return null;
    }

    private void refrescarDetalleSeleccionado() {
        if (idUnidadSeleccionada == null) {
            return;
        }

        ComponenteCombate participante = buscarComponente(idUnidadSeleccionada);

        if (participante == null) {
            limpiarDetalle();
            return;
        }

        mostrarDetalleParticipante(participante);
    }

    private void mostrarDetalleParticipante(ComponenteCombate participante) {
        etiquetaDetalleNombre.setText(participante.getNombre());
        etiquetaDetalleTipo.setText(participante.getClass().getSimpleName());
        etiquetaDetalleBando.setText(String.valueOf(participante.getBando()));
        etiquetaDetalleEstado.setText(String.valueOf(participante.getEstadoVisual()));
        etiquetaDetalleVida.setText(formatear(participante.getVidaActual()) + " / " + formatear(participante.getVidaMaxima()));
        etiquetaDetalleDanio.setText(formatear(participante.getDanioGolpe()));
        etiquetaDetalleFrecuencia.setText(formatear(participante.getFrecuenciaAtaque()));
        etiquetaDetalleAlcance.setText(String.valueOf(participante.getAlcance()));
        etiquetaDetalleRadio.setText(String.valueOf(participante.getRadioEfecto()));
        etiquetaDetalleCosto.setText(String.valueOf(participante.getCostoCapacidad()));
        etiquetaDetallePosicion.setText(participante.getPosicion() == null ? "Sin posición" : participante.getPosicion().toString());
        etiquetaGolpesRealizados.setText(String.valueOf(participante.getRegistroCombate().getCantidadGolpesRealizados()));
        etiquetaGolpesRecibidos.setText(String.valueOf(participante.getRegistroCombate().getCantidadGolpesRecibidos()));
        etiquetaDanioCausado.setText(formatear(participante.getRegistroCombate().getDanioCausadoTotal()));
        etiquetaDanioRecibido.setText(formatear(participante.getRegistroCombate().getDanioRecibidoTotal()));
    }

    private String formatear(double valor) {
        return String.format("%.2f", valor);
    }

    private void limpiarDetalle() {
        idUnidadSeleccionada = null;
        etiquetaDetalleNombre.setText("-");
        etiquetaDetalleTipo.setText("-");
        etiquetaDetalleBando.setText("-");
        etiquetaDetalleEstado.setText("-");
        etiquetaDetalleVida.setText("-");
        etiquetaDetalleDanio.setText("-");
        etiquetaDetalleFrecuencia.setText("-");
        etiquetaDetalleAlcance.setText("-");
        etiquetaDetalleRadio.setText("-");
        etiquetaDetalleCosto.setText("-");
        etiquetaDetallePosicion.setText("-");
        etiquetaGolpesRealizados.setText("-");
        etiquetaGolpesRecibidos.setText("-");
        etiquetaDanioCausado.setText("-");
        etiquetaDanioRecibido.setText("-");
    }

    private void iniciarBatallaDesdeInterfaz() {
        try {
            cancelarColocacion();
            controlador.iniciarBatalla();
            resultadoBatallaMostrado = false;
            temporizadorBatalla.start();
            actualizarControlesBatalla();
            refrescarBatalla();
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo iniciar la batalla", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refrescarBatalla() {
        Partida partida = controlador.consultarEstado();

        if (partida == null) {
            temporizadorBatalla.stop();
            mostrarEstadoVacio();
            return;
        }

        pintarEstado(partida);
        refrescarDetalleSeleccionado();
        actualizarControlesBatalla();

        Mision mision = partida.getMision();

        if (mision == null) {
            return;
        }

        boolean finalizada = mision.getEstado() == EstadoMision.VICTORIA || mision.getEstado() == EstadoMision.DERROTA;

        if (finalizada && !controlador.estaBatallaEnCurso()) {
            temporizadorBatalla.stop();
            controlador.limpiarBatallaFinalizada();
            actualizarControlesBatalla();

            if (!resultadoBatallaMostrado) {
                resultadoBatallaMostrado = true;
                controlador.procesarResultadoMisionFinalizada();
                actualizarVista();
                VentanaResultado.abrir(this, mision, crearAccionesResultado());
            }
        }
    }

    private VentanaResultado.AccionesResultado crearAccionesResultado() {
        return new VentanaResultado.AccionesResultado() {

            @Override
            public boolean puedeRepetir() {
                return controlador.puedeRepetirMision();
            }

            @Override
            public boolean puedeAvanzar() {
                return controlador.puedeAvanzarMision();
            }

            @Override
            public boolean puedeFinalizarCampania() {
                return controlador.puedeFinalizarCampania();
            }

            @Override
            public boolean repetirMision() {
                try {
                    boolean resultado = controlador.repetirMisionDesdeResultado();

                    if (resultado) {
                        prepararInterfazParaNuevaMision();
                    }

                    return resultado;
                } catch (IllegalArgumentException | IllegalStateException e) {
                    mostrarErrorResultado(e.getMessage());
                    return false;
                }
            }

            @Override
            public boolean avanzar() {
                try {
                    boolean resultado = controlador.avanzarMisionDesdeResultado();

                    if (resultado) {
                        prepararInterfazParaNuevaMision();
                    }

                    return resultado;
                } catch (IllegalArgumentException | IllegalStateException e) {
                    mostrarErrorResultado(e.getMessage());
                    return false;
                }
            }

            @Override
            public boolean finalizarCampania() {
                try {
                    boolean resultado = controlador.finalizarCampaniaDesdeResultado();

                    if (resultado) {
                        resultadoBatallaMostrado = true;
                        cancelarColocacion();
                        limpiarDetalle();
                        actualizarVista();
                    }

                    return resultado;
                } catch (IllegalArgumentException | IllegalStateException e) {
                    mostrarErrorResultado(e.getMessage());
                    return false;
                }
            }
        };
    }

    private void prepararInterfazParaNuevaMision() {
        resultadoBatallaMostrado = false;
        cacheIconos.clear();
        cancelarColocacion();
        limpiarDetalle();
        actualizarVista();
    }

    private void mostrarErrorResultado(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acción de resultado", JOptionPane.WARNING_MESSAGE);
    }

    private void actualizarControlesBatalla() {
        boolean batalla = controlador.estaBatallaEnCurso();
        Partida partida = controlador.consultarEstado();
        Mision mision = partida == null ? null : partida.getMision();
        boolean preparacion = mision != null && mision.getEstado() == EstadoMision.PREPARACION;
        boolean misionLista = preparacion && mision.isGenerada();
        boolean puedePreparar = !batalla && preparacion;
        boolean hayNuevas = modeloDefensasDisponibles.getSize() > 0;
        boolean hayExistentes = modeloDefensasExistentes.getSize() > 0;
        boolean hayPendiente = configuracionPendiente != null || defensaExistentePendiente != null;

        botonIniciarBatalla.setEnabled(!batalla && misionLista);
        comboDefensasDisponibles.setEnabled(puedePreparar && hayNuevas);
        botonPrepararDefensa.setEnabled(puedePreparar && hayNuevas);
        comboDefensasExistentes.setEnabled(puedePreparar && hayExistentes);
        botonPrepararDefensaExistente.setEnabled(puedePreparar && hayExistentes);
        botonCancelarColocacion.setEnabled(puedePreparar && hayPendiente);
        botonRetirarDefensa.setEnabled(puedePreparar && defensaSeleccionadaColocada());

        actualizarControlesPersistencia();
    }

    private boolean defensaSeleccionadaColocada() {
        if (idUnidadSeleccionada == null) {
            return false;
        }

        ComponenteCombate componente = buscarComponente(idUnidadSeleccionada);

        if (!(componente instanceof Defensa defensa) || defensa.getPosicion() == null) {
            return false;
        }

        Partida partida = controlador.consultarEstado();

        return partida != null && partida.getTablero() != null && partida.getTablero().obtener(defensa.getPosicion()) == defensa;
    }

    private void actualizarControlesPersistencia() {
        boolean persistencia = controlador.persistenciaDisponible();
        boolean batalla = controlador.estaBatallaEnCurso();

        botonGuardarPartida.setEnabled(persistencia && !batalla);
        botonCargarPartida.setEnabled(persistencia && !batalla);
        botonNuevaPartida.setEnabled(controlador.creacionDisponible() && !batalla);

        if (!persistencia) {
            botonGuardarPartida.setToolTipText("No hay repositorio de partidas configurado.");
            botonCargarPartida.setToolTipText("No hay repositorio de partidas configurado.");
        } else {
            botonGuardarPartida.setToolTipText(null);
            botonCargarPartida.setToolTipText(null);
        }

        botonNuevaPartida.setToolTipText(controlador.creacionDisponible() ? null : "No hay creador de partidas configurado.");
    }

    private void crearNuevaPartidaDesdeInterfaz() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del comandante:", "Nueva partida", JOptionPane.QUESTION_MESSAGE);

        if (nombre == null) {
            return;
        }

        try {
            controlador.crearNuevaPartida(nombre);
            cacheIconos.clear();
            cancelarColocacion();
            limpiarDetalle();
            resultadoBatallaMostrado = false;
            etiquetaCoordenadaCasilla.setText("Casilla: -");
            actualizarVista();
            JOptionPane.showMessageDialog(this, "Partida creada correctamente.", "Nueva partida", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErrorPersistencia(e.getMessage());
        }
    }

    private void guardarPartidaDesdeInterfaz() {
        try {
            controlador.guardarPartidaActual();
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente.", "Guardar partida", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            mostrarErrorPersistencia(e.getMessage());
        }
    }

    private void cargarPartidaDesdeInterfaz() {
        try {
            List<String> partidas = controlador.listarPartidasGuardadas();

            if (partidas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay partidas guardadas.", "Cargar partida", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Object seleccion = JOptionPane.showInputDialog(
                    this,
                    "Seleccione una partida:",
                    "Cargar partida",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    partidas.toArray(),
                    partidas.get(0)
            );

            if (seleccion == null) {
                return;
            }

            controlador.cargarPartida(seleccion.toString());

            cacheIconos.clear();
            cancelarColocacion();
            limpiarDetalle();
            resultadoBatallaMostrado = false;
            etiquetaCoordenadaCasilla.setText("Casilla: -");
            botonesCasilla = null;

            actualizarVista();

            JOptionPane.showMessageDialog(
                    this,
                    "Partida cargada correctamente.",
                    "Cargar partida",
                    JOptionPane.INFORMATION_MESSAGE
            );

            abrirResultadoCargadoSiCorresponde();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErrorPersistencia(e.getMessage());
        }
    }
    private void abrirResultadoCargadoSiCorresponde() {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        Mision mision = partida.getMision();

        if (mision.getEstado() != EstadoMision.VICTORIA
                && mision.getEstado() != EstadoMision.DERROTA) {
            return;
        }

        controlador.procesarResultadoMisionFinalizada();

        resultadoBatallaMostrado = true;

        actualizarVista();

        VentanaResultado.abrir(
                this,
                mision,
                crearAccionesResultado()
        );
    }

    private void mostrarErrorPersistencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de partida", JOptionPane.ERROR_MESSAGE);
    }

    private void cerrarVentana() {
        if (controlador.estaBatallaEnCurso()) {
            int opcion = JOptionPane.showConfirmDialog(this, "Hay una batalla en curso. ¿Desea detenerla y cerrar?", "Cerrar Mars Colony", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        temporizadorBatalla.stop();
        controlador.detenerBatallaYEsperar();
        dispose();
    }

    private void mostrarTableroVacio() {
        panelCuadricula.removeAll();
        panelCuadricula.setLayout(new BorderLayout());
        JLabel etiqueta = new JLabel("Sin tablero", JLabel.CENTER);
        panelCuadricula.add(etiqueta, BorderLayout.CENTER);
        Dimension tamano = new Dimension(500, 500);
        panelCuadricula.setPreferredSize(tamano);
        panelCuadricula.setMinimumSize(tamano);
        panelCuadricula.setMaximumSize(tamano);
        botonesCasilla = null;
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }

    private void mostrarEstadoVacio() {
        etiquetaComandante.setText("-");
        etiquetaMision.setText("-");
        etiquetaEstadoMision.setText("-");
        etiquetaCampania.setText("-");
        etiquetaCapacidadTotal.setText("-");
        etiquetaCapacidadUtilizada.setText("-");
        etiquetaCapacidadRestante.setText("-");
        etiquetaCoordenadaCasilla.setText("Casilla: -");
        mostrarTableroVacio();
        limpiarDetalle();
    }

    public ControladorJuego getControlador() {
        return controlador;
    }
}