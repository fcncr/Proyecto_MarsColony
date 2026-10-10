package com.mycompany.mars_colony.interfaz.admin;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelFondoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.util.RutasAplicacion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class VentanaConfiguracion extends JFrame {

    private static final String LOGIN = "LOGIN";
    private static final String CATALOGO = "CATALOGO";
    private static final String EDITOR = "EDITOR";

    private static final String PASO_DATOS = "DATOS";
    private static final String PASO_IMAGENES = "IMAGENES";

    private enum FiltroCatalogo {
        TODOS,
        DEFENSAS,
        CRIATURAS
    }

    private final ControladorAdmin controlador;

    private final CardLayout cardLayout;
    private final JPanel panelRaiz;

    private final JTextField campoUsuario;
    private final JPasswordField campoClave;
    private final JLabel etiquetaLoginError;

    private final JTextField campoBuscar;
    private final DefaultTableModel modeloTabla;
    private final JTable tablaCatalogo;
    private final List<ConfiguracionComponente> filasVisibles;

    private final BotonFiltro filtroTodos;
    private final BotonFiltro filtroDefensas;
    private final BotonFiltro filtroCriaturas;

    private final JLabel imagenDetalle;
    private final JLabel nombreDetalle;
    private final JLabel idDetalle;
    private final JLabel estadoDetalle;
    private final JLabel vidaDanioDetalle;
    private final JLabel frecuenciaAlcanceDetalle;
    private final JLabel costoMisionDetalle;

    private final BotonMars botonModificarDetalle;
    private final BotonMars botonDesactivarDetalle;

    private final EncabezadoAdmin encabezadoEditor;
    private final CardLayout cardPasosEditor;
    private final JPanel panelPasosEditor;
    private final BotonFiltro botonPasoDatos;
    private final BotonFiltro botonPasoImagenes;

    private final JTextField campoId;
    private final JTextField campoNombre;
    private final JComboBox<TipoComponente> comboTipo;

    private final JTextField campoVida;
    private final JTextField campoDanio;
    private final JTextField campoFrecuencia;
    private final JTextField campoAlcance;
    private final JTextField campoRadio;
    private final JTextField campoCosto;
    private final JTextField campoMisionMinima;
    private final JTextField campoCantidadAtaques;
    private final JTextField campoMaxObjetivos;
    private final JTextField campoIntervaloMovimiento;
    private final JCheckBox checkAtacaAereo;

    private final JTextField campoImagenNormal;
    private final JTextField campoImagenMovimiento;
    private final JTextField campoImagenAtaque;

    private final JLabel previewNormal;
    private final JLabel previewMovimiento;
    private final JLabel previewAtaque;

    private final JLabel archivoNormal;
    private final JLabel archivoMovimiento;
    private final JLabel archivoAtaque;

    private final JLabel estadoImagenes;

    private String idSeleccionado;
    private String idOriginalEdicion;

    private boolean modoEdicion;
    private boolean catalogoModificado;
    private boolean cargandoFormulario;

    private FiltroCatalogo filtroActual;

    public VentanaConfiguracion(ControladorAdmin controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException(
                    "El controlador administrativo no puede ser nulo."
            );
        }

        this.controlador = controlador;

        TemaMars.aplicarGlobalmente();

        cardLayout = new CardLayout();
        panelRaiz = new JPanel(cardLayout);

        campoUsuario = new JTextField();
        campoClave = new JPasswordField();
        etiquetaLoginError = new JLabel(" ");

        campoBuscar = new JTextField();

        filasVisibles = new ArrayList<>();

        modeloTabla = new DefaultTableModel(
                new String[]{
                    "Nombre",
                    "Tipo",
                    "Desde",
                    "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaCatalogo = new JTable(modeloTabla);

        filtroTodos = new BotonFiltro("Todos");
        filtroDefensas = new BotonFiltro("Defensas");
        filtroCriaturas = new BotonFiltro("Criaturas");

        imagenDetalle = new JLabel();
        nombreDetalle = new JLabel("Selecciona un componente");
        idDetalle = new JLabel("-");
        estadoDetalle = new JLabel("-");
        vidaDanioDetalle = new JLabel("-");
        frecuenciaAlcanceDetalle = new JLabel("-");
        costoMisionDetalle = new JLabel("-");

        botonModificarDetalle = BotonMars.primario("MODIFICAR");
        botonDesactivarDetalle = BotonMars.peligro("Desactivar componente");

        encabezadoEditor = new EncabezadoAdmin(
                "NUEVO COMPONENTE",
                "Define los datos y reglas de la unidad"
        );

        cardPasosEditor = new CardLayout();
        panelPasosEditor = new JPanel(cardPasosEditor);
        panelPasosEditor.setOpaque(false);

        botonPasoDatos = new BotonFiltro("1. Datos y reglas");
        botonPasoImagenes = new BotonFiltro("2. Imágenes");

        campoId = new JTextField();
        campoNombre = new JTextField();
        comboTipo = new JComboBox<>(TipoComponente.values());

        campoVida = new JTextField();
        campoDanio = new JTextField();
        campoFrecuencia = new JTextField();
        campoAlcance = new JTextField();
        campoRadio = new JTextField();
        campoCosto = new JTextField();
        campoMisionMinima = new JTextField();
        campoCantidadAtaques = new JTextField();
        campoMaxObjetivos = new JTextField();
        campoIntervaloMovimiento = new JTextField();

        checkAtacaAereo = new JCheckBox(
                "Puede atacar objetivos aéreos"
        );

        campoImagenNormal = new JTextField();
        campoImagenMovimiento = new JTextField();
        campoImagenAtaque = new JTextField();

        previewNormal = new JLabel();
        previewMovimiento = new JLabel();
        previewAtaque = new JLabel();

        archivoNormal = new JLabel("Sin imagen");
        archivoMovimiento = new JLabel("Sin imagen");
        archivoAtaque = new JLabel("Sin imagen");

        estadoImagenes = new JLabel(
                "Selecciona las tres imágenes."
        );

        idSeleccionado = null;
        idOriginalEdicion = null;

        modoEdicion = false;
        catalogoModificado = false;
        cargandoFormulario = false;

        filtroActual = FiltroCatalogo.TODOS;

        configurarVentana();
        configurarComponentes();
        construirInterfaz();
        conectarEventos();
    }

    private void configurarVentana() {
        setTitle(
                "Mars Colony - Administrador de configuraciones"
        );

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(1100, 700)
        );

        setSize(
                1440,
                840
        );

        setResizable(true);
        setLocationRelativeTo(null);

        setExtendedState(
                JFrame.MAXIMIZED_BOTH
        );

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        cerrarVentana();
                    }
                }
        );
    }

    private void configurarComponentes() {
        estilizarCampo(campoUsuario);
        estilizarCampo(campoClave);

        campoUsuario.setText("admin");

        etiquetaLoginError.setFont(
                TemaMars.textoSecundario()
        );

        etiquetaLoginError.setForeground(
                TemaMars.ERROR
        );

        estilizarCampo(campoBuscar);

        estilizarCampo(campoId);
        estilizarCampo(campoNombre);
        estilizarCampo(campoVida);
        estilizarCampo(campoDanio);
        estilizarCampo(campoFrecuencia);
        estilizarCampo(campoAlcance);
        estilizarCampo(campoRadio);
        estilizarCampo(campoCosto);
        estilizarCampo(campoMisionMinima);
        estilizarCampo(campoCantidadAtaques);
        estilizarCampo(campoMaxObjetivos);
        estilizarCampo(campoIntervaloMovimiento);

        comboTipo.setFont(
                TemaMars.textoNormal()
        );

        comboTipo.setBackground(Color.WHITE);
        comboTipo.setForeground(TemaMars.TEXTO);

        comboTipo.setPreferredSize(
                new Dimension(100, 48)
        );

        checkAtacaAereo.setOpaque(false);
        checkAtacaAereo.setFont(
                TemaMars.textoNormal()
        );

        checkAtacaAereo.setForeground(
                TemaMars.TEXTO
        );

        tablaCatalogo.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaCatalogo.setRowHeight(48);
        tablaCatalogo.setAutoCreateRowSorter(true);
        tablaCatalogo.setFillsViewportHeight(true);

        tablaCatalogo.setFont(
                TemaMars.textoNormal()
        );

        tablaCatalogo.setBackground(
                TemaMars.PANEL
        );

        tablaCatalogo.setForeground(
                TemaMars.TEXTO
        );

        tablaCatalogo.setSelectionBackground(
                TemaMars.SELECCION
        );

        tablaCatalogo.setSelectionForeground(
                TemaMars.TEXTO
        );

        tablaCatalogo.setGridColor(
                TemaMars.PANEL_SECUNDARIO
        );

        tablaCatalogo.getTableHeader().setFont(
                TemaMars.textoDestacado()
        );

        tablaCatalogo.getTableHeader().setBackground(
                TemaMars.FONDO
        );

        tablaCatalogo.getTableHeader().setForeground(
                TemaMars.TEXTO_CLARO
        );

        imagenDetalle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        nombreDetalle.setFont(
                TemaMars.tituloPantalla()
        );

        nombreDetalle.setForeground(
                TemaMars.TEXTO
        );

        nombreDetalle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        idDetalle.setFont(
                TemaMars.textoNormal()
        );

        idDetalle.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        idDetalle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        estadoDetalle.setFont(
                TemaMars.textoDestacado()
        );

        estadoDetalle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        estadoDetalle.setOpaque(true);

        botonModificarDetalle.setEnabled(false);
        botonDesactivarDetalle.setEnabled(false);

        configurarPreview(previewNormal);
        configurarPreview(previewMovimiento);
        configurarPreview(previewAtaque);

        configurarEtiquetaArchivo(archivoNormal);
        configurarEtiquetaArchivo(archivoMovimiento);
        configurarEtiquetaArchivo(archivoAtaque);

        estadoImagenes.setFont(
                TemaMars.textoDestacado()
        );

        estadoImagenes.setForeground(
                TemaMars.ERROR
        );
    }

    private void construirInterfaz() {
        panelRaiz.add(
                crearPantallaLogin(),
                LOGIN
        );

        panelRaiz.add(
                crearPantallaCatalogo(),
                CATALOGO
        );

        panelRaiz.add(
                crearPantallaEditor(),
                EDITOR
        );

        setContentPane(panelRaiz);

        cardLayout.show(
                panelRaiz,
                LOGIN
        );
    }

    private JPanel crearPantallaLogin() {
        EncabezadoAdmin encabezado = new EncabezadoAdmin(
                "MARS COLONY / ADMINISTRADOR",
                "Aplicación de configuración independiente"
        );

        JPanel escenario = new JPanel(
                new GridBagLayout()
        );

        escenario.setOpaque(false);

        PanelMars tarjeta = new PanelMars();

        tarjeta.setPreferredSize(
                new Dimension(610, 500)
        );

        tarjeta.setLayout(
                new GridBagLayout()
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel(
                "ACCESO AL CATÁLOGO",
                SwingConstants.CENTER
        );

        titulo.setFont(
                TemaMars.tituloPantalla()
        );

        titulo.setForeground(
                TemaMars.TEXTO
        );

        gbc.gridy = 0;
        gbc.insets = new Insets(
                5,
                25,
                10,
                25
        );

        tarjeta.add(
                titulo,
                gbc
        );

        JLabel mensaje = new JLabel(
                "<html><div style='text-align:center'>"
                + "Configura las unidades que aparecerán "
                + "en las expediciones."
                + "</div></html>",
                SwingConstants.CENTER
        );

        mensaje.setFont(
                TemaMars.textoNormal()
        );

        mensaje.setForeground(
                TemaMars.TEXTO
        );

        gbc.gridy = 1;
        gbc.insets = new Insets(
                0,
                25,
                18,
                25
        );

        tarjeta.add(
                mensaje,
                gbc
        );

        gbc.gridy = 2;
        gbc.insets = new Insets(
                0,
                25,
                12,
                25
        );

        tarjeta.add(
                crearBloqueCampo(
                        "Usuario",
                        campoUsuario
                ),
                gbc
        );

        gbc.gridy = 3;

        tarjeta.add(
                crearBloqueCampo(
                        "Contraseña",
                        campoClave
                ),
                gbc
        );

        gbc.gridy = 4;
        gbc.insets = new Insets(
                0,
                25,
                12,
                25
        );

        tarjeta.add(
                etiquetaLoginError,
                gbc
        );

        BotonMars ingresar =
                BotonMars.primario(
                        "ENTRAR"
                );

        ingresar.setPreferredSize(
                new Dimension(
                        100,
                        TemaMars.ALTURA_BOTON
                )
        );

        ingresar.addActionListener(
                e -> intentarLogin()
        );

        gbc.gridy = 5;
        gbc.insets = new Insets(
                5,
                25,
                12,
                25
        );

        tarjeta.add(
                ingresar,
                gbc
        );

        BotonMars salir =
                BotonMars.secundario(
                        "Salir"
                );

        salir.addActionListener(
                e -> cerrarVentana()
        );

        gbc.gridy = 6;
        gbc.insets = new Insets(
                0,
                25,
                5,
                25
        );

        tarjeta.add(
                salir,
                gbc
        );

        campoClave.addActionListener(
                e -> intentarLogin()
        );

        campoUsuario.addActionListener(
                e -> campoClave.requestFocusInWindow()
        );

        GridBagConstraints gbcEscenario =
                new GridBagConstraints();

        gbcEscenario.weightx = 1;
        gbcEscenario.weighty = 1;

        escenario.add(
                tarjeta,
                gbcEscenario
        );

        return envolverConFondo(
                encabezado,
                escenario
        );
    }

    private JPanel crearPantallaCatalogo() {
        EncabezadoAdmin encabezado = new EncabezadoAdmin(
                "CATÁLOGO DE COMPONENTES",
                ""
        );

        JPanel contenido = new JPanel(
                new BorderLayout(18, 14)
        );

        contenido.setOpaque(false);

        PanelMars izquierda =
                new PanelMars();

        izquierda.setLayout(
                new BorderLayout(0, 14)
        );

        JPanel buscador = new JPanel();

        buscador.setOpaque(false);

        buscador.setLayout(
                new BoxLayout(
                        buscador,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiquetaBuscar =
                new JLabel(
                        "Buscar componente"
                );

        etiquetaBuscar.setFont(
                TemaMars.textoDestacado()
        );

        etiquetaBuscar.setForeground(
                TemaMars.TEXTO
        );

        etiquetaBuscar.setAlignmentX(
                LEFT_ALIGNMENT
        );

        campoBuscar.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JPanel filtros = new JPanel(
                new GridLayout(
                        1,
                        3,
                        12,
                        0
                )
        );

        filtros.setOpaque(false);

        filtros.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        filtros.setAlignmentX(
                LEFT_ALIGNMENT
        );

        filtros.add(filtroTodos);
        filtros.add(filtroDefensas);
        filtros.add(filtroCriaturas);

        buscador.add(etiquetaBuscar);
        buscador.add(
                Box.createVerticalStrut(6)
        );
        buscador.add(campoBuscar);
        buscador.add(
                Box.createVerticalStrut(14)
        );
        buscador.add(filtros);

        JScrollPane scroll =
                new JScrollPane(
                        tablaCatalogo
                );

        scroll.setBorder(null);

        BotonMars nuevo =
                BotonMars.primario(
                        "+ NUEVO COMPONENTE"
                );

        nuevo.addActionListener(
                e -> abrirNuevoComponente()
        );

        izquierda.add(
                buscador,
                BorderLayout.NORTH
        );

        izquierda.add(
                scroll,
                BorderLayout.CENTER
        );

        izquierda.add(
                nuevo,
                BorderLayout.SOUTH
        );

        PanelMars derecha =
                crearPanelDetalle();

        derecha.setPreferredSize(
                new Dimension(410, 100)
        );

        contenido.add(
                izquierda,
                BorderLayout.CENTER
        );

        contenido.add(
                derecha,
                BorderLayout.EAST
        );

        JPanel pie = new JPanel(
                new BorderLayout()
        );

        pie.setOpaque(false);

        BotonMars cerrarSesion =
                BotonMars.secundario(
                        "‹ Cerrar sesión"
                );

        cerrarSesion.setPreferredSize(
                new Dimension(
                        180,
                        TemaMars.ALTURA_BOTON
                )
        );

        cerrarSesion.addActionListener(
                e -> cerrarSesion()
        );

        BotonMars guardar =
                BotonMars.secundario(
                        "Guardar catálogo"
                );

        guardar.setPreferredSize(
                new Dimension(
                        220,
                        TemaMars.ALTURA_BOTON
                )
        );

        guardar.addActionListener(
                e -> guardarCatalogo()
        );

        pie.add(
                cerrarSesion,
                BorderLayout.WEST
        );

        pie.add(
                guardar,
                BorderLayout.EAST
        );

        JPanel total = new JPanel(
                new BorderLayout(0, 14)
        );

        total.setOpaque(false);

        total.add(
                contenido,
                BorderLayout.CENTER
        );

        total.add(
                pie,
                BorderLayout.SOUTH
        );

        return envolverConFondo(
                encabezado,
                total
        );
    }

    private PanelMars crearPanelDetalle() {
        PanelMars panel = new PanelMars();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        nombreDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        idDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        estadoDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        imagenDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        imagenDetalle.setPreferredSize(
                new Dimension(250, 200)
        );

        imagenDetalle.setMaximumSize(
                new Dimension(250, 200)
        );

        JPanel datos = new JPanel(
                new GridLayout(
                        3,
                        2,
                        8,
                        14
                )
        );

        datos.setOpaque(false);

        datos.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        170
                )
        );

        datos.add(
                crearEtiquetaDetalle(
                        "Vida / Daño"
                )
        );

        datos.add(
                vidaDanioDetalle
        );

        datos.add(
                crearEtiquetaDetalle(
                        "Frecuencia / Alcance"
                )
        );

        datos.add(
                frecuenciaAlcanceDetalle
        );

        datos.add(
                crearEtiquetaDetalle(
                        "Costo / Misión mínima"
                )
        );

        datos.add(
                costoMisionDetalle
        );

        configurarValorDetalle(
                vidaDanioDetalle
        );

        configurarValorDetalle(
                frecuenciaAlcanceDetalle
        );

        configurarValorDetalle(
                costoMisionDetalle
        );

        botonModificarDetalle.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        TemaMars.ALTURA_BOTON
                )
        );

        botonModificarDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        botonModificarDetalle.addActionListener(
                e -> abrirModificarSeleccionado()
        );

        botonDesactivarDetalle.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        TemaMars.ALTURA_BOTON
                )
        );

        botonDesactivarDetalle.setAlignmentX(
                CENTER_ALIGNMENT
        );

        botonDesactivarDetalle.addActionListener(
            e -> cambiarEstadoSeleccionado()
    );

        JLabel tituloConsulta =
                new JLabel(
                        "Consultar"
                );

        tituloConsulta.setFont(
                TemaMars.tituloPanel()
        );

        tituloConsulta.setForeground(
                TemaMars.TEXTO
        );

        tituloConsulta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.add(nombreDetalle);
        panel.add(
                Box.createVerticalStrut(5)
        );
        panel.add(idDetalle);
        panel.add(
                Box.createVerticalStrut(8)
        );
        panel.add(estadoDetalle);
        panel.add(
                Box.createVerticalStrut(10)
        );
        panel.add(imagenDetalle);
        panel.add(
                Box.createVerticalStrut(12)
        );
        panel.add(tituloConsulta);
        panel.add(
                Box.createVerticalStrut(12)
        );
        panel.add(datos);
        panel.add(
                Box.createVerticalGlue()
        );
        panel.add(
                botonModificarDetalle
        );
        panel.add(
                Box.createVerticalStrut(10)
        );
        panel.add(
                botonDesactivarDetalle
        );

        mostrarDetalle(null);

        return panel;
    }

    private JPanel crearPantallaEditor() {
        JPanel contenido = new JPanel(
                new BorderLayout(0, 14)
        );

        contenido.setOpaque(false);

        PanelMars tarjeta = new PanelMars();

        tarjeta.setLayout(
                new BorderLayout(0, 16)
        );

        JPanel pasos = new JPanel(
                new GridLayout(
                        1,
                        2,
                        18,
                        0
                )
        );

        pasos.setOpaque(false);

        pasos.setPreferredSize(
                new Dimension(100, 48)
        );

        pasos.add(botonPasoDatos);
        pasos.add(botonPasoImagenes);

        panelPasosEditor.add(
                crearPasoDatos(),
                PASO_DATOS
        );

        panelPasosEditor.add(
                crearPasoImagenes(),
                PASO_IMAGENES
        );

        tarjeta.add(
                pasos,
                BorderLayout.NORTH
        );

        tarjeta.add(
                panelPasosEditor,
                BorderLayout.CENTER
        );

        BotonMars cancelar =
                BotonMars.secundario(
                        "‹ Cancelar"
                );

        cancelar.setPreferredSize(
                new Dimension(
                        170,
                        TemaMars.ALTURA_BOTON
                )
        );

        cancelar.addActionListener(
                e -> cancelarEditor()
        );

        JPanel pie = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        0,
                        0
                )
        );

        pie.setOpaque(false);
        pie.add(cancelar);

        contenido.add(
                tarjeta,
                BorderLayout.CENTER
        );

        contenido.add(
                pie,
                BorderLayout.SOUTH
        );

        return envolverConFondo(
                encabezadoEditor,
                contenido
        );
    }

    private JPanel crearPasoDatos() {
        JPanel raiz = new JPanel(
                new BorderLayout(0, 14)
        );

        raiz.setOpaque(false);

        JPanel formulario = new JPanel();

        formulario.setOpaque(false);

        formulario.setLayout(
                new BoxLayout(
                        formulario,
                        BoxLayout.Y_AXIS
                )
        );

        JPanel comunes = new JPanel(
                new GridLayout(
                        3,
                        3,
                        18,
                        14
                )
        );

        comunes.setOpaque(false);

        comunes.add(
                crearBloqueCampo(
                        "Nombre",
                        campoNombre
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Identificador único",
                        campoId
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Tipo concreto",
                        comboTipo
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Vida inicial",
                        campoVida
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Daño por golpe",
                        campoDanio
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Frecuencia (ataques/s)",
                        campoFrecuencia
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Alcance (casillas)",
                        campoAlcance
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Costo de capacidad",
                        campoCosto
                )
        );

        comunes.add(
                crearBloqueCampo(
                        "Misión mínima",
                        campoMisionMinima
                )
        );

        PanelMars opciones =
                new PanelMars(
                        TemaMars.PANEL_SECUNDARIO
                );

        opciones.setLayout(
                new BorderLayout(0, 10)
        );

        JLabel tituloOpciones =
                new JLabel(
                        "OPCIONES DEL TIPO"
                );

        tituloOpciones.setFont(
                TemaMars.textoDestacado()
        );

        tituloOpciones.setForeground(
                TemaMars.TEXTO
        );

        JPanel camposOpcionales =
                new JPanel(
                        new GridLayout(
                                1,
                                5,
                                12,
                                0
                        )
                );

        camposOpcionales.setOpaque(false);

        camposOpcionales.add(
                crearBloqueCampo(
                        "Radio de efecto",
                        campoRadio
                )
        );

        camposOpcionales.add(
                crearBloqueCampo(
                        "Ataques por ciclo",
                        campoCantidadAtaques
                )
        );

        camposOpcionales.add(
                crearBloqueCampo(
                        "Máx. objetivos",
                        campoMaxObjetivos
                )
        );

        camposOpcionales.add(
                crearBloqueCampo(
                        "Movimiento ms",
                        campoIntervaloMovimiento
                )
        );

        JPanel bloqueAereo = new JPanel();

        bloqueAereo.setOpaque(false);

        bloqueAereo.setLayout(
                new BoxLayout(
                        bloqueAereo,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiquetaAereo =
                new JLabel(
                        "Ataque aéreo"
                );

        etiquetaAereo.setFont(
                TemaMars.textoDestacado()
        );

        etiquetaAereo.setForeground(
                TemaMars.TEXTO
        );

        bloqueAereo.add(etiquetaAereo);
        bloqueAereo.add(
                Box.createVerticalStrut(8)
        );
        bloqueAereo.add(checkAtacaAereo);

        camposOpcionales.add(
                bloqueAereo
        );

        opciones.add(
                tituloOpciones,
                BorderLayout.NORTH
        );

        opciones.add(
                camposOpcionales,
                BorderLayout.CENTER
        );

        formulario.add(comunes);
        formulario.add(
                Box.createVerticalStrut(18)
        );
        formulario.add(opciones);

        JScrollPane scroll =
                new JScrollPane(
                        formulario
                );

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        BotonMars siguiente =
                BotonMars.primario(
                        "SIGUIENTE: IMÁGENES"
                );

        siguiente.setPreferredSize(
                new Dimension(
                        360,
                        TemaMars.ALTURA_BOTON
                )
        );

        siguiente.addActionListener(
                e -> irAImagenes()
        );

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        0
                )
        );

        acciones.setOpaque(false);
        acciones.add(siguiente);

        raiz.add(
                scroll,
                BorderLayout.CENTER
        );

        raiz.add(
                acciones,
                BorderLayout.SOUTH
        );

        return raiz;
    }

    private JPanel crearPasoImagenes() {
        JPanel raiz = new JPanel(
                new BorderLayout(0, 16)
        );

        raiz.setOpaque(false);

        JPanel tarjetas = new JPanel(
                new GridLayout(
                        1,
                        3,
                        18,
                        0
                )
        );

        tarjetas.setOpaque(false);

        tarjetas.add(
                crearTarjetaImagen(
                        "NORMAL",
                        campoImagenNormal,
                        previewNormal,
                        archivoNormal,
                        false
                )
        );

        tarjetas.add(
                crearTarjetaImagen(
                        "MOVIMIENTO",
                        campoImagenMovimiento,
                        previewMovimiento,
                        archivoMovimiento,
                        true
                )
        );

        tarjetas.add(
                crearTarjetaImagen(
                        "ATAQUE",
                        campoImagenAtaque,
                        previewAtaque,
                        archivoAtaque,
                        true
                )
        );

        estadoImagenes.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        JPanel inferior = new JPanel(
                new BorderLayout(18, 0)
        );

        inferior.setOpaque(false);

        JPanel izquierda = new JPanel();

        izquierda.setOpaque(false);

        izquierda.setLayout(
                new BoxLayout(
                        izquierda,
                        BoxLayout.Y_AXIS
                )
        );

        BotonMars volverDatos =
                BotonMars.secundario(
                        "Volver a datos"
                );

        volverDatos.setPreferredSize(
                new Dimension(
                        220,
                        TemaMars.ALTURA_BOTON
                )
        );

        volverDatos.addActionListener(
                e -> mostrarPasoEditor(
                        PASO_DATOS
                )
        );

        izquierda.add(estadoImagenes);
        izquierda.add(
                Box.createVerticalStrut(8)
        );
        izquierda.add(volverDatos);

        BotonMars guardar =
                BotonMars.primario(
                        "VALIDAR Y GUARDAR"
                );

        guardar.setPreferredSize(
                new Dimension(
                        320,
                        TemaMars.ALTURA_BOTON
                )
        );

        guardar.addActionListener(
                e -> aplicarCambiosEditor()
        );

        inferior.add(
                izquierda,
                BorderLayout.CENTER
        );

        inferior.add(
                guardar,
                BorderLayout.EAST
        );

        raiz.add(
                tarjetas,
                BorderLayout.CENTER
        );

        raiz.add(
                inferior,
                BorderLayout.SOUTH
        );

        return raiz;
    }

    private PanelMars crearTarjetaImagen(
            String titulo,
            JTextField campoRuta,
            JLabel preview,
            JLabel nombreArchivo,
            boolean permiteUsarNormal) {

        PanelMars panel =
                new PanelMars(
                        TemaMars.PANEL_SECUNDARIO
                );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiquetaTitulo =
                new JLabel(
                        titulo
                );

        etiquetaTitulo.setFont(
                TemaMars.tituloPanel()
        );

        etiquetaTitulo.setForeground(
                TemaMars.TEXTO
        );

        etiquetaTitulo.setAlignmentX(
                CENTER_ALIGNMENT
        );

        preview.setAlignmentX(
                CENTER_ALIGNMENT
        );

        nombreArchivo.setAlignmentX(
                CENTER_ALIGNMENT
        );

        BotonMars cambiar =
                BotonMars.secundario(
                        "Cambiar imagen"
                );

        cambiar.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        50
                )
        );

        cambiar.setAlignmentX(
                CENTER_ALIGNMENT
        );

        cambiar.addActionListener(e -> {
            seleccionarAsset(
                    campoRuta,
                    preview,
                    nombreArchivo
            );
        });

        panel.add(etiquetaTitulo);
        panel.add(
                Box.createVerticalStrut(12)
        );
        panel.add(preview);
        panel.add(
                Box.createVerticalStrut(10)
        );
        panel.add(nombreArchivo);
        panel.add(
                Box.createVerticalGlue()
        );
        panel.add(cambiar);

        if (permiteUsarNormal) {
            BotonMars usarNormal =
                    BotonMars.secundario(
                            "Usar imagen normal"
                    );

            usarNormal.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            46
                    )
            );

            usarNormal.setAlignmentX(
                    CENTER_ALIGNMENT
            );

            usarNormal.addActionListener(e -> {
                String ruta =
                        campoImagenNormal
                                .getText()
                                .trim();

                if (ruta.isBlank()) {
                    DialogoMars.informacion(
                            this,
                            "Imagen normal",
                            "Selecciona primero la imagen normal."
                    );

                    return;
                }

                establecerImagen(
                        campoRuta,
                        preview,
                        nombreArchivo,
                        ruta
                );

                actualizarEstadoImagenes();
            });

            panel.add(
                    Box.createVerticalStrut(8)
            );

            panel.add(usarNormal);
        }

        return panel;
    }

    private JPanel envolverConFondo(
            JComponent encabezado,
            JComponent contenido) {

        JPanel raiz = new JPanel(
                new BorderLayout()
        );

        raiz.setBackground(
                TemaMars.FONDO_PROFUNDO
        );

        PanelFondoMars fondo =
                new PanelFondoMars(
                        "assets/FondoMars.png"
                );

        fondo.setLayout(
                new BorderLayout()
        );

        fondo.setBorder(
                TemaMars.padding(24)
        );

        fondo.setOpacidadOscurecimiento(
                52
        );

        fondo.add(
                contenido,
                BorderLayout.CENTER
        );

        raiz.add(
                encabezado,
                BorderLayout.NORTH
        );

        raiz.add(
                fondo,
                BorderLayout.CENTER
        );

        return raiz;
    }

    private JPanel crearBloqueCampo(
            String titulo,
            JComponent componente) {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiqueta =
                new JLabel(
                        titulo
                );

        etiqueta.setFont(
                TemaMars.textoDestacado()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO
        );

        etiqueta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        componente.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.add(etiqueta);
        panel.add(
                Box.createVerticalStrut(6)
        );
        panel.add(componente);

        return panel;
    }

    private JLabel crearEtiquetaDetalle(
            String texto) {

        JLabel etiqueta =
                new JLabel(
                        texto
                );

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        return etiqueta;
    }

    private void configurarValorDetalle(
            JLabel etiqueta) {

        etiqueta.setFont(
                TemaMars.textoDestacado()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO
        );

        etiqueta.setHorizontalAlignment(
                SwingConstants.RIGHT
        );
    }

    private void configurarPreview(
            JLabel preview) {

        preview.setPreferredSize(
                new Dimension(230, 190)
        );

        preview.setMinimumSize(
                new Dimension(230, 190)
        );

        preview.setMaximumSize(
                new Dimension(230, 190)
        );

        preview.setHorizontalAlignment(
                SwingConstants.CENTER
        );
    }

    private void configurarEtiquetaArchivo(
            JLabel etiqueta) {

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        etiqueta.setHorizontalAlignment(
                SwingConstants.CENTER
        );
    }

    private void estilizarCampo(
            JTextField campo) {

        campo.setFont(
                TemaMars.textoNormal()
        );

        campo.setForeground(
                TemaMars.TEXTO
        );

        campo.setBackground(
                Color.WHITE
        );

        campo.setDisabledTextColor(
                TemaMars.TEXTO_SECUNDARIO
        );

        campo.setPreferredSize(
                new Dimension(100, 48)
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                TemaMars.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                12,
                                0,
                                12
                        )
                )
        );
    }

    private void conectarEventos() {
        filtroTodos.addActionListener(e -> {
            filtroActual =
                    FiltroCatalogo.TODOS;

            actualizarFiltros();
            mostrarCatalogo();
        });

        filtroDefensas.addActionListener(e -> {
            filtroActual =
                    FiltroCatalogo.DEFENSAS;

            actualizarFiltros();
            mostrarCatalogo();
        });

        filtroCriaturas.addActionListener(e -> {
            filtroActual =
                    FiltroCatalogo.CRIATURAS;

            actualizarFiltros();
            mostrarCatalogo();
        });

        campoBuscar.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        mostrarCatalogo();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        mostrarCatalogo();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        mostrarCatalogo();
                    }
                }
        );

        tablaCatalogo
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        seleccionarFilaCatalogo();
                    }
                });

        comboTipo.addActionListener(e -> {
            if (!cargandoFormulario) {
                aplicarReglasTipo(true);
            }
        });

        botonPasoDatos.addActionListener(
                e -> mostrarPasoEditor(
                        PASO_DATOS
                )
        );

        botonPasoImagenes.addActionListener(
                e -> irAImagenes()
        );
    }

    private void intentarLogin() {
        String usuario =
                campoUsuario.getText().trim();

        char[] clave =
                campoClave.getPassword();

        try {
            if (!controlador.iniciarSesion(
                    usuario,
                    clave
            )) {

                etiquetaLoginError.setText(
                        "Usuario o contraseña incorrectos."
                );

                campoClave.setText("");

                campoClave.requestFocusInWindow();

                return;
            }

            controlador.cargar();

            catalogoModificado = false;
            idSeleccionado = null;

            etiquetaLoginError.setText(" ");
            campoClave.setText("");

            campoBuscar.setText("");

            filtroActual =
                    FiltroCatalogo.TODOS;

            actualizarFiltros();
            mostrarCatalogo();

            cardLayout.show(
                    panelRaiz,
                    CATALOGO
            );

        } catch (RuntimeException e) {
            controlador.cerrarSesion();

            DialogoMars.error(
                    this,
                    "No se pudo abrir el catálogo",
                    obtenerMensaje(e)
            );
        } finally {
            Arrays.fill(
                    clave,
                    '\0'
            );
        }
    }

    public final void mostrarCatalogo() {
        if (!controlador.haySesionActiva()) {
            return;
        }

        try {
            String busqueda =
                    campoBuscar
                            .getText()
                            .trim()
                            .toLowerCase();

            filasVisibles.clear();
            modeloTabla.setRowCount(0);

            int indiceSeleccionado = -1;

            for (ConfiguracionComponente configuracion
                    : controlador.listar()) {

                if (!coincideFiltro(
                        configuracion
                )) {

                    continue;
                }

                if (!busqueda.isBlank()) {
                    String texto =
                            (
                            configuracion.getId()
                            + " "
                            + configuracion.getNombre()
                            )
                            .toLowerCase();

                    if (!texto.contains(busqueda)) {
                        continue;
                    }
                }

                filasVisibles.add(
                        configuracion
                );

                modeloTabla.addRow(
                        new Object[]{
                            configuracion.getNombre(),
                            nombreTipo(
                                    configuracion.getTipo()
                            ),
                            String.format(
                                    "M%02d",
                                    configuracion.getMisionMinima()
                            ),
                            configuracion.isActivo()
                                    ? "Activo"
                                    : "Inactivo"
                        }
                );

                if (idSeleccionado != null
                        && idSeleccionado.equals(
                                configuracion.getId()
                        )) {

                    indiceSeleccionado =
                            filasVisibles.size() - 1;
                }
            }

            if (indiceSeleccionado >= 0) {
                int vista =
                        tablaCatalogo
                                .convertRowIndexToView(
                                        indiceSeleccionado
                                );

                if (vista >= 0) {
                    tablaCatalogo
                            .setRowSelectionInterval(
                                    vista,
                                    vista
                            );
                }
            } else if (!filasVisibles.isEmpty()) {
                tablaCatalogo
                        .setRowSelectionInterval(
                                0,
                                0
                        );
            } else {
                idSeleccionado = null;
                mostrarDetalle(null);
            }

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo consultar",
                    obtenerMensaje(e)
            );
        }
    }

    private boolean coincideFiltro(
            ConfiguracionComponente configuracion) {

        return switch (filtroActual) {
            case TODOS -> true;
            case DEFENSAS ->
                esDefensa(
                        configuracion.getTipo()
                );
            case CRIATURAS ->
                !esDefensa(
                        configuracion.getTipo()
                );
        };
    }

    private void seleccionarFilaCatalogo() {
        int filaVista =
                tablaCatalogo.getSelectedRow();

        if (filaVista < 0) {
            mostrarDetalle(null);
            return;
        }

        int filaModelo =
                tablaCatalogo
                        .convertRowIndexToModel(
                                filaVista
                        );

        if (filaModelo < 0
                || filaModelo >= filasVisibles.size()) {

            mostrarDetalle(null);
            return;
        }

        ConfiguracionComponente configuracion =
                filasVisibles.get(
                        filaModelo
                );

        idSeleccionado =
                configuracion.getId();

        mostrarDetalle(
                configuracion
        );
    }

    private void mostrarDetalle(
            ConfiguracionComponente configuracion) {

        if (configuracion == null) {
            nombreDetalle.setText(
                    "Selecciona un componente"
            );

            idDetalle.setText("-");

            estadoDetalle.setText(
                    "SIN SELECCIÓN"
            );

            estadoDetalle.setBackground(
                    TemaMars.BLOQUEADO
            );

            estadoDetalle.setForeground(
                    TemaMars.TEXTO_CLARO
            );

            imagenDetalle.setIcon(null);

            vidaDanioDetalle.setText("-");
            frecuenciaAlcanceDetalle.setText("-");
            costoMisionDetalle.setText("-");

            botonModificarDetalle.setEnabled(
                    false
            );

            botonDesactivarDetalle.setEnabled(
                    false
            );

            return;
        }

        EstadisticasCombate stats =
                configuracion.getBase();

        nombreDetalle.setText(
                configuracion
                        .getNombre()
                        .toUpperCase()
        );

        idDetalle.setText(
                configuracion.getId()
        );

        estadoDetalle.setText(
                configuracion.isActivo()
                        ? "ACTIVO"
                        : "INACTIVO"
        );

        estadoDetalle.setBackground(
                configuracion.isActivo()
                        ? TemaMars.CORRECTO
                        : TemaMars.ERROR
        );

        estadoDetalle.setForeground(
                TemaMars.TEXTO
        );

        imagenDetalle.setIcon(
                GestorImagenes.cargar(
                        configuracion
                                .getImagenes()
                                .getNormal(),
                        220,
                        190
                )
        );

        vidaDanioDetalle.setText(
                formatear(
                        stats.getVidaMaxima()
                )
                + " / "
                + formatear(
                        stats.getDanioGolpe()
                )
        );

        frecuenciaAlcanceDetalle.setText(
                formatear(
                        stats.getFrecuenciaAtaque()
                )
                + " / "
                + stats.getAlcance()
        );

        costoMisionDetalle.setText(
                stats.getCostoCapacidad()
                + " pts / M"
                + String.format(
                        "%02d",
                        configuracion.getMisionMinima()
                )
        );

        botonModificarDetalle.setEnabled(true);
        botonDesactivarDetalle.setEnabled(true);

        if (configuracion.isActivo()) {
            botonDesactivarDetalle.setText(
                    "Desactivar componente"
            );

            botonDesactivarDetalle.setVariante(
                    BotonMars.Variante.PELIGRO
            );
        } else {
            botonDesactivarDetalle.setText(
                    "ACTIVAR COMPONENTE"
            );

            botonDesactivarDetalle.setVariante(
                    BotonMars.Variante.PRIMARIO
            );
        }
    }

    private void abrirNuevoComponente() {
        modoEdicion = false;
        idOriginalEdicion = null;

        encabezadoEditor.setTitulo(
                "NUEVO COMPONENTE"
        );

        encabezadoEditor.setSubtitulo(
                ""
        );

        limpiarEditor();

        mostrarPasoEditor(
                PASO_DATOS
        );

        cardLayout.show(
                panelRaiz,
                EDITOR
        );
    }

    private void abrirModificarSeleccionado() {
        if (idSeleccionado == null) {
            return;
        }

        try {
            ConfiguracionComponente configuracion =
                    controlador.consultar(
                            idSeleccionado
                    );

            if (configuracion == null) {
                throw new IllegalStateException(
                        "El componente seleccionado ya no existe."
                );
            }

            modoEdicion = true;
            idOriginalEdicion =
                    configuracion.getId();

            encabezadoEditor.setTitulo(
                    "EDITAR COMPONENTE"
            );

            encabezadoEditor.setSubtitulo(
                    configuracion.getNombre()
                    + " / "
                    + configuracion.getId()
            );

            cargarEditor(
                    configuracion
            );

            mostrarPasoEditor(
                    PASO_DATOS
            );

            cardLayout.show(
                    panelRaiz,
                    EDITOR
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo editar",
                    obtenerMensaje(e)
            );
        }
    }

    private void limpiarEditor() {
        cargandoFormulario = true;

        try {
            campoId.setText("");
            campoNombre.setText("");

            comboTipo.setSelectedItem(
                    TipoComponente.DEFENSA_CONTACTO
            );

            campoVida.setText("10");
            campoDanio.setText("1");
            campoFrecuencia.setText("1");
            campoAlcance.setText("1");
            campoRadio.setText("0");
            campoCosto.setText("1");
            campoMisionMinima.setText("1");
            campoCantidadAtaques.setText("1");
            campoMaxObjetivos.setText("1");
            campoIntervaloMovimiento.setText("0");

            checkAtacaAereo.setSelected(false);

            establecerImagen(
                    campoImagenNormal,
                    previewNormal,
                    archivoNormal,
                    ""
            );

            establecerImagen(
                    campoImagenMovimiento,
                    previewMovimiento,
                    archivoMovimiento,
                    ""
            );

            establecerImagen(
                    campoImagenAtaque,
                    previewAtaque,
                    archivoAtaque,
                    ""
            );

        } finally {
            cargandoFormulario = false;
        }

        aplicarReglasTipo(true);
        actualizarEstadoImagenes();
    }

    private void cargarEditor(
            ConfiguracionComponente configuracion) {

        cargandoFormulario = true;

        try {
            EstadisticasCombate stats =
                    configuracion.getBase();

            campoId.setText(
                    configuracion.getId()
            );

            campoNombre.setText(
                    configuracion.getNombre()
            );

            comboTipo.setSelectedItem(
                    configuracion.getTipo()
            );

            campoVida.setText(
                    String.valueOf(
                            stats.getVidaMaxima()
                    )
            );

            campoDanio.setText(
                    String.valueOf(
                            stats.getDanioGolpe()
                    )
            );

            campoFrecuencia.setText(
                    String.valueOf(
                            stats.getFrecuenciaAtaque()
                    )
            );

            campoAlcance.setText(
                    String.valueOf(
                            stats.getAlcance()
                    )
            );

            campoRadio.setText(
                    String.valueOf(
                            stats.getRadioEfecto()
                    )
            );

            campoCosto.setText(
                    String.valueOf(
                            stats.getCostoCapacidad()
                    )
            );

            campoMisionMinima.setText(
                    String.valueOf(
                            configuracion.getMisionMinima()
                    )
            );

            campoCantidadAtaques.setText(
                    String.valueOf(
                            stats.getCantidadAtaques()
                    )
            );

            campoMaxObjetivos.setText(
                    String.valueOf(
                            stats.getMaxObjetivos()
                    )
            );

            campoIntervaloMovimiento.setText(
                    String.valueOf(
                            stats.getIntervaloMovimientoMs()
                    )
            );

            checkAtacaAereo.setSelected(
                    stats.isAtacaAereo()
            );

            ImagenesEstado imagenes =
                    configuracion.getImagenes();

            establecerImagen(
                    campoImagenNormal,
                    previewNormal,
                    archivoNormal,
                    imagenes.getNormal()
            );

            establecerImagen(
                    campoImagenMovimiento,
                    previewMovimiento,
                    archivoMovimiento,
                    imagenes.getMovimiento()
            );

            establecerImagen(
                    campoImagenAtaque,
                    previewAtaque,
                    archivoAtaque,
                    imagenes.getAtaque()
            );

        } finally {
            cargandoFormulario = false;
        }

        aplicarReglasTipo(false);
        actualizarEstadoImagenes();
    }

    private void cancelarEditor() {
        cardLayout.show(
                panelRaiz,
                CATALOGO
        );
    }

    private void irAImagenes() {
        try {
            validarDatosEditor();

            mostrarPasoEditor(
                    PASO_IMAGENES
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "Revisa los datos",
                    obtenerMensaje(e)
            );
        }
    }

    private void mostrarPasoEditor(
            String paso) {

        cardPasosEditor.show(
                panelPasosEditor,
                paso
        );

        boolean datos =
                PASO_DATOS.equals(paso);

        botonPasoDatos.setActivo(
                datos
        );

        botonPasoImagenes.setActivo(
                !datos
        );
    }

    private void aplicarCambiosEditor() {
        try {
            ConfiguracionComponente configuracion =
                    construirConfiguracionFinal();

            if (modoEdicion) {
                ConfiguracionComponente anterior =
                        controlador.consultar(
                                idOriginalEdicion
                        );

                if (anterior == null) {
                    throw new IllegalStateException(
                            "La configuración original ya no existe."
                    );
                }

                if (!anterior.isActivo()) {
                    configuracion.desactivar();
                }

                controlador.modificar(
                        idOriginalEdicion,
                        configuracion
                );

            } else {
                controlador.crear(
                        configuracion
                );
            }

            idSeleccionado =
                    configuracion.getId();

            catalogoModificado = true;

            campoBuscar.setText("");

            filtroActual =
                    FiltroCatalogo.TODOS;

            actualizarFiltros();
            mostrarCatalogo();

            cardLayout.show(
                    panelRaiz,
                    CATALOGO
            );

            DialogoMars.exito(
                    this,
                    modoEdicion
                            ? "Componente modificado"
                            : "Componente creado",
                    "Los cambios están listos. "
                    + "Usa Guardar catálogo para escribirlos en el archivo."
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    obtenerMensaje(e)
            );
        }
    }

    private void validarDatosEditor() {
        construirConfiguracion(
                new ImagenesEstado(
                        "pendiente",
                        "pendiente",
                        "pendiente"
                )
        );
    }

    private ConfiguracionComponente construirConfiguracionFinal() {
        validarAssetsSeleccionados();

        ImagenesEstado imagenes =
                new ImagenesEstado(
                        campoImagenNormal
                                .getText()
                                .trim(),
                        campoImagenMovimiento
                                .getText()
                                .trim(),
                        campoImagenAtaque
                                .getText()
                                .trim()
                );

        return construirConfiguracion(
                imagenes
        );
    }

    private ConfiguracionComponente construirConfiguracion(
            ImagenesEstado imagenes) {

        String id =
                campoId.getText().trim();

        String nombre =
                campoNombre.getText().trim();

        TipoComponente tipo =
                (TipoComponente)
                comboTipo.getSelectedItem();

        double vida =
                leerDouble(
                        campoVida,
                        "Vida inicial"
                );

        double danio =
                leerDouble(
                        campoDanio,
                        "Daño por golpe"
                );

        double frecuencia =
                leerDouble(
                        campoFrecuencia,
                        "Frecuencia"
                );

        int alcance =
                leerInt(
                        campoAlcance,
                        "Alcance"
                );

        int radio =
                leerInt(
                        campoRadio,
                        "Radio de efecto"
                );

        int costo =
                leerInt(
                        campoCosto,
                        "Costo de capacidad"
                );

        int mision =
                leerInt(
                        campoMisionMinima,
                        "Misión mínima"
                );

        int cantidadAtaques =
                leerInt(
                        campoCantidadAtaques,
                        "Ataques por ciclo"
                );

        int maxObjetivos =
                leerInt(
                        campoMaxObjetivos,
                        "Máximo de objetivos"
                );

        long intervalo =
                leerLong(
                        campoIntervaloMovimiento,
                        "Intervalo de movimiento"
                );

        EstadisticasCombate stats =
                new EstadisticasCombate(
                        vida,
                        danio,
                        frecuencia,
                        alcance,
                        radio,
                        costo,
                        checkAtacaAereo.isSelected(),
                        cantidadAtaques,
                        maxObjetivos,
                        intervalo
                );

        return new ConfiguracionComponente(
                id,
                nombre,
                tipo,
                stats,
                imagenes,
                mision
        );
    }

    private void aplicarReglasTipo(
            boolean ajustarValores) {

        TipoComponente tipo =
                (TipoComponente)
                comboTipo.getSelectedItem();

        if (tipo == null) {
            return;
        }

        boolean barrera =
                tipo == TipoComponente.BARRERA;

        boolean contacto =
                tipo == TipoComponente.DEFENSA_CONTACTO
                || tipo == TipoComponente.ACECHADOR;

        boolean impacto =
                tipo == TipoComponente.DEFENSA_IMPACTO;

        boolean demoledor =
                tipo == TipoComponente.DEMOLEDOR;

        boolean radio =
                impacto || demoledor;

        boolean multiple =
                tipo == TipoComponente.DEFENSA_MULTIPLE
                || tipo == TipoComponente.ENJAMBRE;

        boolean movil =
                tipo == TipoComponente.DRON
                || tipo == TipoComponente.ACECHADOR
                || tipo == TipoComponente.ESCUPIDOR
                || tipo == TipoComponente.DEMOLEDOR
                || tipo == TipoComponente.VOLADOR
                || tipo == TipoComponente.ENJAMBRE;

        boolean alcanceEditable =
                !barrera
                && !impacto
                && !demoledor
                && !contacto;

        boolean puedeAereo =
                esDefensa(tipo)
                && !barrera;

        setCampoActivo(
                campoDanio,
                !barrera
        );

        setCampoActivo(
                campoFrecuencia,
                !barrera
        );

        setCampoActivo(
                campoAlcance,
                alcanceEditable
        );

        setCampoActivo(
                campoRadio,
                radio
        );

        setCampoActivo(
                campoCantidadAtaques,
                multiple
        );

        setCampoActivo(
                campoMaxObjetivos,
                multiple
        );

        setCampoActivo(
                campoIntervaloMovimiento,
                movil
        );

        checkAtacaAereo.setEnabled(
                puedeAereo
        );

        if (!puedeAereo) {
            checkAtacaAereo.setSelected(
                    false
            );
        }

        if (!ajustarValores) {
            return;
        }

        if (barrera) {
            campoDanio.setText("0");
            campoFrecuencia.setText("0");
            campoAlcance.setText("0");
            campoRadio.setText("0");
            campoCantidadAtaques.setText("0");
            campoMaxObjetivos.setText("0");
            campoIntervaloMovimiento.setText("0");
            return;
        }

        asegurarPositivo(
                campoDanio,
                "1"
        );

        asegurarPositivo(
                campoFrecuencia,
                "1"
        );

        if (contacto) {
            campoAlcance.setText("1");
        } else if (impacto) {
            campoAlcance.setText("0");
        } else if (demoledor) {
            campoAlcance.setText("1");
        } else {
            asegurarPositivo(
                    campoAlcance,
                    "2"
            );
        }

        if (radio) {
            asegurarPositivo(
                    campoRadio,
                    "1"
            );
        } else {
            campoRadio.setText("0");
        }

        if (multiple) {
            if (leerIntSeguro(
                    campoCantidadAtaques
            ) <= 1
                    && leerIntSeguro(
                            campoMaxObjetivos
                    ) <= 1) {

                campoCantidadAtaques.setText(
                        "2"
                );

                campoMaxObjetivos.setText(
                        "2"
                );
            }
        } else {
            campoCantidadAtaques.setText(
                    "1"
            );

            campoMaxObjetivos.setText(
                    "1"
            );
        }

        if (movil) {
            if (leerLongSeguro(
                    campoIntervaloMovimiento
            ) <= 0) {

                campoIntervaloMovimiento.setText(
                        "600"
                );
            }
        } else {
            campoIntervaloMovimiento.setText(
                    "0"
            );
        }
    }

    private void setCampoActivo(
            JTextField campo,
            boolean activo) {

        campo.setEnabled(activo);

        campo.setBackground(
                activo
                        ? Color.WHITE
                        : TemaMars.PANEL_SECUNDARIO
        );
    }

    private void asegurarPositivo(
            JTextField campo,
            String valor) {

        try {
            if (Double.parseDouble(
                    campo.getText().trim()
            ) <= 0) {

                campo.setText(valor);
            }
        } catch (NumberFormatException e) {
            campo.setText(valor);
        }
    }

    private int leerIntSeguro(
            JTextField campo) {

        try {
            return Integer.parseInt(
                    campo.getText().trim()
            );
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long leerLongSeguro(
            JTextField campo) {

        try {
            return Long.parseLong(
                    campo.getText().trim()
            );
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void seleccionarAsset(
            JTextField campoDestino,
            JLabel preview,
            JLabel nombreArchivo) {

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Seleccionar imagen"
        );

        selector.setFileSelectionMode(
                JFileChooser.FILES_ONLY
        );

        selector.setAcceptAllFileFilterUsed(
                false
        );

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "PNG, JPG, JPEG y WEBP",
                        "png",
                        "jpg",
                        "jpeg",
                        "webp"
                )
        );

        int resultado =
                selector.showOpenDialog(
                        this
                );

        if (resultado
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File seleccionado =
                selector.getSelectedFile();

        try {
            String ruta =
                    importarAsset(
                            seleccionado.toPath()
                    );

            establecerImagen(
                    campoDestino,
                    preview,
                    nombreArchivo,
                    ruta
            );

            actualizarEstadoImagenes();

        } catch (IOException e) {
            DialogoMars.error(
                    this,
                    "No se pudo importar",
                    e.getMessage()
            );
        }
    }

    private String importarAsset(
            Path origen) throws IOException {

        if (origen == null
                || !Files.isRegularFile(origen)) {

            throw new IOException(
                    "El archivo seleccionado no es válido."
            );
        }

        Path directorio =
                RutasAplicacion.resolver(
                        "assets/importados"
                );

        Files.createDirectories(
                directorio
        );

        String nombre =
                generarNombreAssetDisponible(
                        directorio,
                        origen.getFileName()
                                .toString()
                );

        Path destino =
                directorio.resolve(
                        nombre
                );

        Files.copy(
                origen,
                destino,
                StandardCopyOption.COPY_ATTRIBUTES
        );

        return RutasAplicacion.relativizar(
                destino
        );
    }

    private String generarNombreAssetDisponible(
            Path directorio,
            String original) {

        Path inicial =
                directorio.resolve(
                        original
                );

        if (!Files.exists(inicial)) {
            return original;
        }

        int punto =
                original.lastIndexOf('.');

        String base =
                punto > 0
                        ? original.substring(
                                0,
                                punto
                        )
                        : original;

        String extension =
                punto > 0
                        ? original.substring(
                                punto
                        )
                        : "";

        int numero = 2;

        while (Files.exists(
                directorio.resolve(
                        base
                        + "_"
                        + numero
                        + extension
                )
        )) {
            numero++;
        }

        return base
                + "_"
                + numero
                + extension;
    }

    private void establecerImagen(
            JTextField campo,
            JLabel preview,
            JLabel nombreArchivo,
            String ruta) {

        String valor =
                ruta == null
                        ? ""
                        : ruta.trim();

        campo.setText(valor);

        if (valor.isBlank()
                || !RutasAplicacion
                        .existeRecurso(valor)) {

            preview.setIcon(null);
            nombreArchivo.setText(
                    "Sin imagen"
            );

            return;
        }

        preview.setIcon(
                GestorImagenes.cargar(
                        valor,
                        210,
                        175
                )
        );

        nombreArchivo.setText(
                nombreArchivo(valor)
        );

        nombreArchivo.setToolTipText(
                valor
        );
    }

    private String nombreArchivo(
            String ruta) {

        int barra1 =
                ruta.lastIndexOf('/');

        int barra2 =
                ruta.lastIndexOf('\\');

        int posicion =
                Math.max(
                        barra1,
                        barra2
                );

        return posicion >= 0
                ? ruta.substring(
                        posicion + 1
                )
                : ruta;
    }

    private void actualizarEstadoImagenes() {
        boolean correctas =
                esAssetValido(
                        campoImagenNormal.getText()
                )
                && esAssetValido(
                        campoImagenMovimiento.getText()
                )
                && esAssetValido(
                        campoImagenAtaque.getText()
                );

        if (correctas) {
            estadoImagenes.setText(
                    "✓ Las tres imágenes están listas."
            );

            estadoImagenes.setForeground(
                    new Color(55, 130, 72)
            );
        } else {
            estadoImagenes.setText(
                    "Selecciona las tres imágenes."
            );

            estadoImagenes.setForeground(
                    TemaMars.ERROR
            );
        }
    }

    private boolean esAssetValido(
            String ruta) {

        return ruta != null
                && !ruta.isBlank()
                && RutasAplicacion
                        .existeRecurso(ruta);
    }

    private void validarAssetsSeleccionados() {
        validarAsset(
                campoImagenNormal.getText(),
                "imagen normal"
        );

        validarAsset(
                campoImagenMovimiento.getText(),
                "imagen de movimiento"
        );

        validarAsset(
                campoImagenAtaque.getText(),
                "imagen de ataque"
        );
    }

    private void validarAsset(
            String ruta,
            String nombre) {

        if (ruta == null
                || ruta.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar la "
                    + nombre
                    + "."
            );
        }

        if (!RutasAplicacion
                .existeRecurso(ruta)) {

            throw new IllegalArgumentException(
                    "No existe el archivo de la "
                    + nombre
                    + ": "
                    + ruta
            );
        }
    }

   private void cambiarEstadoSeleccionado() {
        if (idSeleccionado == null) {
            return;
        }

        try {
            ConfiguracionComponente configuracion =
                    controlador.consultar(
                            idSeleccionado
                    );

            if (configuracion == null) {
                throw new IllegalStateException(
                        "El componente seleccionado ya no existe."
                );
            }

            boolean estaActivo =
                    configuracion.isActivo();

            String titulo = estaActivo
                    ? "¿Desactivar " + configuracion.getNombre() + "?"
                    : "¿Activar " + configuracion.getNombre() + "?";

            String mensaje = estaActivo
                    ? "Dejará de estar disponible para nuevas selecciones."
                    : "Volverá a estar disponible en nuevas partidas y selecciones.";

            String textoBoton = estaActivo
                    ? "Desactivar"
                    : "Activar";

            boolean confirmar =
                    DialogoMars.confirmar(
                            this,
                            titulo,
                            mensaje,
                            textoBoton
                    );

            if (!confirmar) {
                return;
            }

            if (estaActivo) {
                controlador.desactivar(
                        idSeleccionado
                );
            } else {
                controlador.activar(
                        idSeleccionado
                );
            }

            catalogoModificado = true;

            mostrarCatalogo();

            DialogoMars.exito(
                    this,
                    estaActivo
                            ? "Componente desactivado"
                            : "Componente activado",
                    estaActivo
                            ? "La configuración quedó inactiva. Guarda el catálogo para persistir el cambio."
                            : "La configuración volvió a estar activa. Guarda el catálogo para persistir el cambio."
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo cambiar el estado",
                    obtenerMensaje(e)
            );
        }
    }

    private void guardarCatalogo() {
        try {
            controlador.guardar();

            catalogoModificado = false;

            DialogoMars.exito(
                    this,
                    "Catálogo guardado",
                    "Los cambios se guardaron correctamente."
            );

        } catch (RuntimeException e) {
            DialogoMars.error(
                    this,
                    "No se pudo guardar",
                    obtenerMensaje(e)
            );
        }
    }

    private void cerrarSesion() {
        if (catalogoModificado) {
            boolean cerrar =
                    DialogoMars.confirmar(
                            this,
                            "Cambios sin guardar",
                            "Hay cambios que todavía no se han guardado. "
                            + "¿Quieres cerrar la sesión y descartarlos?",
                            "Cerrar sesión"
                    );

            if (!cerrar) {
                return;
            }

            try {
                controlador.cargar();
            } catch (RuntimeException e) {
                DialogoMars.error(
                        this,
                        "No se pudo restaurar",
                        obtenerMensaje(e)
                );

                return;
            }
        }

        controlador.cerrarSesion();

        catalogoModificado = false;
        idSeleccionado = null;

        campoClave.setText("");
        etiquetaLoginError.setText(" ");

        cardLayout.show(
                panelRaiz,
                LOGIN
        );
    }

    private void cerrarVentana() {
        if (controlador.haySesionActiva()
                && catalogoModificado) {

            boolean cerrar =
                    DialogoMars.confirmar(
                            this,
                            "Cambios sin guardar",
                            "Hay cambios administrativos sin guardar. "
                            + "¿Quieres cerrar de todas formas?",
                            "Salir"
                    );

            if (!cerrar) {
                return;
            }
        }

        controlador.cerrarSesion();
        dispose();
    }

    private void actualizarFiltros() {
        filtroTodos.setActivo(
                filtroActual
                == FiltroCatalogo.TODOS
        );

        filtroDefensas.setActivo(
                filtroActual
                == FiltroCatalogo.DEFENSAS
        );

        filtroCriaturas.setActivo(
                filtroActual
                == FiltroCatalogo.CRIATURAS
        );
    }

    private boolean esDefensa(
            TipoComponente tipo) {

        return tipo
                == TipoComponente.DEFENSA_CONTACTO
                || tipo
                == TipoComponente.DEFENSA_ALCANCE
                || tipo
                == TipoComponente.DRON
                || tipo
                == TipoComponente.DEFENSA_IMPACTO
                || tipo
                == TipoComponente.DEFENSA_MULTIPLE
                || tipo
                == TipoComponente.BARRERA;
    }

    private String nombreTipo(
            TipoComponente tipo) {

        return switch (tipo) {
            case DEFENSA_CONTACTO ->
                "Contacto";

            case DEFENSA_ALCANCE ->
                "Alcance";

            case DRON ->
                "Dron";

            case DEFENSA_IMPACTO ->
                "Impacto";

            case DEFENSA_MULTIPLE ->
                "Múltiple";

            case BARRERA ->
                "Barrera";

            case ACECHADOR ->
                "Acechador";

            case ESCUPIDOR ->
                "Escupidor";

            case DEMOLEDOR ->
                "Demoledor";

            case VOLADOR ->
                "Volador";

            case ENJAMBRE ->
                "Enjambre";
        };
    }

    private double leerDouble(
            JTextField campo,
            String nombre) {

        try {
            return Double.parseDouble(
                    campo.getText().trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    nombre
                    + " debe contener un número válido."
            );
        }
    }

    private int leerInt(
            JTextField campo,
            String nombre) {

        try {
            return Integer.parseInt(
                    campo.getText().trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    nombre
                    + " debe contener un número entero válido."
            );
        }
    }

    private long leerLong(
            JTextField campo,
            String nombre) {

        try {
            return Long.parseLong(
                    campo.getText().trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    nombre
                    + " debe contener un número entero válido."
            );
        }
    }

    private String formatear(
            double valor) {

        if (valor == Math.rint(valor)) {
            return String.valueOf(
                    (long) valor
            );
        }

        return String.format(
                "%.2f",
                valor
        );
    }

    private String obtenerMensaje(
            Throwable error) {

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

    private static class EncabezadoAdmin extends JPanel {

        private final JLabel titulo;
        private final JLabel subtitulo;

        private EncabezadoAdmin(
                String titulo,
                String subtitulo) {

            setOpaque(false);

            setLayout(
                    new BorderLayout(18, 0)
            );

            setBorder(
                    TemaMars.padding(
                            10,
                            24,
                            12,
                            24
                    )
            );

            setPreferredSize(
                    new Dimension(100, 82)
            );

            JPanel textos =
                    new JPanel();

            textos.setOpaque(false);

            textos.setLayout(
                    new BoxLayout(
                            textos,
                            BoxLayout.Y_AXIS
                    )
            );

            this.titulo =
                    new JLabel(titulo);

            this.titulo.setFont(
                    new Font(
                            Font.SANS_SERIF,
                            Font.BOLD,
                            22
                    )
            );

            this.titulo.setForeground(
                    TemaMars.TEXTO_CLARO
            );

            this.subtitulo =
                    new JLabel(subtitulo);

            this.subtitulo.setFont(
                    TemaMars.textoSecundario()
            );

            this.subtitulo.setForeground(
                    new Color(
                            201,
                            214,
                            217
                    )
            );

            textos.add(
                    Box.createVerticalGlue()
            );

            textos.add(
                    this.titulo
            );

            textos.add(
                    Box.createVerticalStrut(3)
            );

            textos.add(
                    this.subtitulo
            );

            textos.add(
                    Box.createVerticalGlue()
            );

            JLabel administracion =
                    new JLabel(
                            "ADMINISTRACIÓN",
                            SwingConstants.CENTER
                    );

            administracion.setFont(
                    TemaMars.textoDestacado()
            );

            administracion.setForeground(
                    TemaMars.ACCION
            );

            administracion.setPreferredSize(
                    new Dimension(235, 36)
            );

            administracion.setBorder(
                    BorderFactory.createLineBorder(
                            TemaMars.ACCION
                    )
            );

            JPanel derecha =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT,
                                    0,
                                    13
                            )
                    );

            derecha.setOpaque(false);
            derecha.add(administracion);

            add(
                    textos,
                    BorderLayout.CENTER
            );

            add(
                    derecha,
                    BorderLayout.EAST
            );
        }

        private void setTitulo(
                String titulo) {

            this.titulo.setText(
                    titulo == null
                            ? ""
                            : titulo
            );
        }

        private void setSubtitulo(
                String subtitulo) {

            this.subtitulo.setText(
                    subtitulo == null
                            ? ""
                            : subtitulo
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setColor(
                    TemaMars.FONDO
            );

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            g2.setColor(
                    TemaMars.ACCION
            );

            g2.fillRect(
                    0,
                    getHeight() - 3,
                    getWidth(),
                    3
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class BotonFiltro extends JButton {

        private boolean activo;

        private BotonFiltro(String texto) {
            super(texto);

            activo = false;

            setFont(
                    TemaMars.textoDestacado()
            );

            setForeground(
                    TemaMars.TEXTO_CLARO
            );

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            setPreferredSize(
                    new Dimension(160, 46)
            );
        }

        private void setActivo(
                boolean activo) {

            this.activo = activo;

            setForeground(
                    activo
                            ? TemaMars.TEXTO
                            : TemaMars.TEXTO_CLARO
            );

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color color =
                    activo
                            ? TemaMars.ENERGIA
                            : TemaMars.FONDO;

            if (getModel().isRollover()) {
                color =
                        activo
                                ? TemaMars.ENERGIA.brighter()
                                : TemaMars.FONDO.brighter();
            }

            g2.setColor(color);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    14,
                    14
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}