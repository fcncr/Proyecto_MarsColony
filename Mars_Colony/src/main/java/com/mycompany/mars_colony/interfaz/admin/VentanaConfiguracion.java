package com.mycompany.mars_colony.interfaz.admin;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class VentanaConfiguracion extends JFrame {

    private final ControladorAdmin controlador;
    private final CardLayout cardLayout;
    private final JPanel panelRaiz;

    private final JTextField campoUsuario;
    private final JPasswordField campoClave;
    private final JButton botonIngresar;

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
    private final JCheckBox checkAtacaAereo;
    private final JTextField campoCantidadAtaques;
    private final JTextField campoMaxObjetivos;
    private final JTextField campoIntervaloMovimiento;
    private final JTextField campoImagenNormal;
    private final JTextField campoImagenMovimiento;
    private final JTextField campoImagenAtaque;

    private final JButton botonCrear;
    private final JButton botonModificar;
    private final JButton botonConsultar;
    private final JButton botonDesactivar;
    private final JButton botonGuardar;
    private final JButton botonCargar;

    private final DefaultTableModel modeloTabla;
    private final JTable tablaCatalogo;

    public VentanaConfiguracion(ControladorAdmin controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador administrativo no puede ser nulo.");
        }

        this.controlador = controlador;
        this.cardLayout = new CardLayout();
        this.panelRaiz = new JPanel(cardLayout);

        this.campoUsuario = new JTextField(18);
        this.campoClave = new JPasswordField(18);
        this.botonIngresar = new JButton("Ingresar");

        this.campoId = new JTextField(15);
        this.campoNombre = new JTextField(15);
        this.comboTipo = new JComboBox<>(TipoComponente.values());
        this.campoVida = new JTextField(10);
        this.campoDanio = new JTextField(10);
        this.campoFrecuencia = new JTextField(10);
        this.campoAlcance = new JTextField(10);
        this.campoRadio = new JTextField(10);
        this.campoCosto = new JTextField(10);
        this.campoMisionMinima = new JTextField(10);
        this.checkAtacaAereo = new JCheckBox("Puede atacar objetivos aéreos");
        this.campoCantidadAtaques = new JTextField(10);
        this.campoMaxObjetivos = new JTextField(10);
        this.campoIntervaloMovimiento = new JTextField(10);
        this.campoImagenNormal = new JTextField(15);
        this.campoImagenMovimiento = new JTextField(15);
        this.campoImagenAtaque = new JTextField(15);

        this.botonCrear = new JButton("Crear");
        this.botonModificar = new JButton("Modificar");
        this.botonConsultar = new JButton("Consultar");
        this.botonDesactivar = new JButton("Desactivar");
        this.botonGuardar = new JButton("Guardar");
        this.botonCargar = new JButton("Cargar");

        String[] columnas = {"ID", "Nombre", "Tipo", "Vida", "Daño", "Frecuencia", "Alcance", "Radio", "Costo", "Misión", "Activo"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tablaCatalogo = new JTable(modeloTabla);

        configurarVentana();
        construirInterfaz();
        conectarLogin();
        configurarBotonesPendientes();
    }

    private void configurarVentana() {
        setTitle("Mars Colony - Administrador de configuraciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1250, 760);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        panelRaiz.add(crearPanelLogin(), "LOGIN");
        panelRaiz.add(crearPanelAdministrador(), "ADMIN");
        setContentPane(panelRaiz);
        cardLayout.show(panelRaiz, "LOGIN");
    }

    private JPanel crearPanelLogin() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        JPanel login = new JPanel(new GridBagLayout());
        login.setBorder(BorderFactory.createTitledBorder("Autenticación administrativa"));

        GridBagConstraints gbc = crearRestricciones();
        agregarCampo(login, gbc, 0, "Usuario:", campoUsuario);
        agregarCampo(login, gbc, 1, "Contraseña:", campoClave);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        login.add(botonIngresar, gbc);
        contenedor.add(login);

        return contenedor;
    }

    private JPanel crearPanelAdministrador() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = crearPanelFormulario();
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setPreferredSize(new Dimension(360, 600));

        tablaCatalogo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCatalogo.setAutoCreateRowSorter(true);

        JScrollPane scrollTabla = new JScrollPane(tablaCatalogo);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollFormulario, scrollTabla);
        division.setResizeWeight(0.30);

        panel.add(new JLabel("Catálogo administrativo de componentes"), BorderLayout.NORTH);
        panel.add(division, BorderLayout.CENTER);
        panel.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Configuración del componente"));

        GridBagConstraints gbc = crearRestricciones();
        int fila = 0;

        agregarCampo(panel, gbc, fila++, "ID:", campoId);
        agregarCampo(panel, gbc, fila++, "Nombre:", campoNombre);
        agregarCampo(panel, gbc, fila++, "Tipo:", comboTipo);
        agregarCampo(panel, gbc, fila++, "Vida máxima:", campoVida);
        agregarCampo(panel, gbc, fila++, "Daño por golpe:", campoDanio);
        agregarCampo(panel, gbc, fila++, "Frecuencia:", campoFrecuencia);
        agregarCampo(panel, gbc, fila++, "Alcance:", campoAlcance);
        agregarCampo(panel, gbc, fila++, "Radio de efecto:", campoRadio);
        agregarCampo(panel, gbc, fila++, "Costo capacidad:", campoCosto);
        agregarCampo(panel, gbc, fila++, "Misión mínima:", campoMisionMinima);
        agregarCampo(panel, gbc, fila++, "Cantidad ataques:", campoCantidadAtaques);
        agregarCampo(panel, gbc, fila++, "Máx. objetivos:", campoMaxObjetivos);
        agregarCampo(panel, gbc, fila++, "Intervalo mov. ms:", campoIntervaloMovimiento);
        agregarCampo(panel, gbc, fila++, "Imagen normal:", campoImagenNormal);
        agregarCampo(panel, gbc, fila++, "Imagen movimiento:", campoImagenMovimiento);
        agregarCampo(panel, gbc, fila++, "Imagen ataque:", campoImagenAtaque);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(checkAtacaAereo, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        panel.add(botonCrear);
        panel.add(botonModificar);
        panel.add(botonConsultar);
        panel.add(botonDesactivar);
        panel.add(botonGuardar);
        panel.add(botonCargar);

        return panel;
    }

    private GridBagConstraints crearRestricciones() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        return gbc;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, java.awt.Component componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(componente, gbc);
    }

    private void conectarLogin() {
        botonIngresar.addActionListener(e -> intentarLogin());
        campoClave.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        char[] clave = campoClave.getPassword();

        try {
            if (controlador.iniciarSesion(usuario, clave)) {
                campoClave.setText("");
                cardLayout.show(panelRaiz, "ADMIN");
                mostrarCatalogo();
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Acceso rechazado", JOptionPane.WARNING_MESSAGE);
                campoClave.setText("");
                campoClave.requestFocusInWindow();
            }
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    public void mostrarCatalogo() {
        try {
            List<ConfiguracionComponente> configuraciones = controlador.listar();
            modeloTabla.setRowCount(0);

            for (ConfiguracionComponente cfg : configuraciones) {
                Object[] fila = {cfg.getId(), cfg.getNombre(), cfg.getTipo(), cfg.getBase().getVidaMaxima(), cfg.getBase().getDanioGolpe(), cfg.getBase().getFrecuenciaAtaque(), cfg.getBase().getAlcance(), cfg.getBase().getRadioEfecto(), cfg.getBase().getCostoCapacidad(), cfg.getMisionMinima(), cfg.isActivo()};
                modeloTabla.addRow(fila);
            }
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void configurarBotonesPendientes() {
        botonCrear.setEnabled(false);
        botonModificar.setEnabled(false);
        botonConsultar.setEnabled(false);
        botonDesactivar.setEnabled(false);
        botonGuardar.setEnabled(false);
        botonCargar.setEnabled(false);

        botonCrear.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
        botonModificar.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
        botonConsultar.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
        botonDesactivar.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
        botonGuardar.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
        botonCargar.setToolTipText("Se conectará al controlador en la Tarea 10.1.");
    }
}