package com.mycompany.mars_colony.interfaz.admin;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.controlador.admin.AutenticadorAdmin;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
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
    private String idSeleccionado;

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
        this.idSeleccionado = null;

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
        conectarAccionesAdministrativas();
        habilitarBotonesAdministrativos(false);
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
                habilitarBotonesAdministrativos(true);
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

    private void conectarAccionesAdministrativas() {
        botonCrear.addActionListener(e -> crearConfiguracion());
        botonModificar.addActionListener(e -> modificarConfiguracion());
        botonConsultar.addActionListener(e -> consultarConfiguracion());
        botonDesactivar.addActionListener(e -> desactivarConfiguracion());
        botonGuardar.addActionListener(e -> guardarCatalogo());
        botonCargar.addActionListener(e -> cargarCatalogo());

        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionTabla();
            }
        });
    }

    private ConfiguracionComponente leerConfiguracionFormulario() {
        String id = campoId.getText().trim();
        String nombre = campoNombre.getText().trim();
        TipoComponente tipo = (TipoComponente) comboTipo.getSelectedItem();
        double vida = leerDouble(campoVida, "Vida máxima");
        double danio = leerDouble(campoDanio, "Daño por golpe");
        double frecuencia = leerDouble(campoFrecuencia, "Frecuencia de ataque");
        int alcance = leerInt(campoAlcance, "Alcance");
        int radio = leerInt(campoRadio, "Radio de efecto");
        int costo = leerInt(campoCosto, "Costo de capacidad");
        int mision = leerInt(campoMisionMinima, "Misión mínima");
        int cantidadAtaques = leerInt(campoCantidadAtaques, "Cantidad de ataques");
        int maxObjetivos = leerInt(campoMaxObjetivos, "Máximo de objetivos");
        long intervalo = leerLong(campoIntervaloMovimiento, "Intervalo de movimiento");
        boolean atacaAereo = checkAtacaAereo.isSelected();
        String imagenNormal = campoImagenNormal.getText().trim();
        String imagenMovimiento = campoImagenMovimiento.getText().trim();
        String imagenAtaque = campoImagenAtaque.getText().trim();

        EstadisticasCombate stats = new EstadisticasCombate(vida, danio, frecuencia, alcance, radio, costo, atacaAereo, cantidadAtaques, maxObjetivos, intervalo);
        ImagenesEstado imagenes = new ImagenesEstado(imagenNormal, imagenMovimiento, imagenAtaque);

        return new ConfiguracionComponente(id, nombre, tipo, stats, imagenes, mision);
    }

    private double leerDouble(JTextField campo, String nombreCampo) {
        try {
            return Double.parseDouble(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe contener un número válido.");
        }
    }

    private int leerInt(JTextField campo, String nombreCampo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe contener un número entero válido.");
        }
    }

    private long leerLong(JTextField campo, String nombreCampo) {
        try {
            return Long.parseLong(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe contener un número entero válido.");
        }
    }

    private void crearConfiguracion() {
        try {
            ConfiguracionComponente configuracion = leerConfiguracionFormulario();
            controlador.crear(configuracion);
            idSeleccionado = configuracion.getId();
            mostrarCatalogo();
            JOptionPane.showMessageDialog(this, "Configuración creada correctamente.", "Crear", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void consultarConfiguracion() {
        try {
            String id = campoId.getText().trim();

            if (id.isBlank()) {
                throw new IllegalArgumentException("Debe indicar el ID que desea consultar.");
            }

            ConfiguracionComponente configuracion = controlador.consultar(id);

            if (configuracion == null) {
                throw new IllegalArgumentException("No existe una configuración con el ID: " + id);
            }

            idSeleccionado = configuracion.getId();
            cargarFormulario(configuracion);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarFormulario(ConfiguracionComponente cfg) {
        EstadisticasCombate stats = cfg.getBase();
        ImagenesEstado imagenes = cfg.getImagenes();

        campoId.setText(cfg.getId());
        campoNombre.setText(cfg.getNombre());
        comboTipo.setSelectedItem(cfg.getTipo());
        campoVida.setText(String.valueOf(stats.getVidaMaxima()));
        campoDanio.setText(String.valueOf(stats.getDanioGolpe()));
        campoFrecuencia.setText(String.valueOf(stats.getFrecuenciaAtaque()));
        campoAlcance.setText(String.valueOf(stats.getAlcance()));
        campoRadio.setText(String.valueOf(stats.getRadioEfecto()));
        campoCosto.setText(String.valueOf(stats.getCostoCapacidad()));
        campoMisionMinima.setText(String.valueOf(cfg.getMisionMinima()));
        checkAtacaAereo.setSelected(stats.isAtacaAereo());
        campoCantidadAtaques.setText(String.valueOf(stats.getCantidadAtaques()));
        campoMaxObjetivos.setText(String.valueOf(stats.getMaxObjetivos()));
        campoIntervaloMovimiento.setText(String.valueOf(stats.getIntervaloMovimientoMs()));
        campoImagenNormal.setText(imagenes.getNormal());
        campoImagenMovimiento.setText(imagenes.getMovimiento());
        campoImagenAtaque.setText(imagenes.getAtaque());
    }

    private void cargarSeleccionTabla() {
        int filaVista = tablaCatalogo.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        try {
            int filaModelo = tablaCatalogo.convertRowIndexToModel(filaVista);
            String id = String.valueOf(modeloTabla.getValueAt(filaModelo, 0));
            ConfiguracionComponente configuracion = controlador.consultar(id);

            if (configuracion != null) {
                idSeleccionado = configuracion.getId();
                cargarFormulario(configuracion);
            }
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void modificarConfiguracion() {
        try {
            String idOriginal = idSeleccionado;

            if (idOriginal == null || idOriginal.isBlank()) {
                idOriginal = campoId.getText().trim();
            }

            if (idOriginal.isBlank()) {
                throw new IllegalArgumentException("Debe seleccionar o consultar una configuración antes de modificar.");
            }

            ConfiguracionComponente anterior = controlador.consultar(idOriginal);

            if (anterior == null) {
                throw new IllegalArgumentException("No existe la configuración que desea modificar.");
            }

            ConfiguracionComponente nueva = leerConfiguracionFormulario();

            if (!anterior.isActivo()) {
                nueva.desactivar();
            }

            controlador.modificar(idOriginal, nueva);
            idSeleccionado = nueva.getId();
            mostrarCatalogo();
            JOptionPane.showMessageDialog(this, "Configuración modificada correctamente.", "Modificar", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void desactivarConfiguracion() {
        try {
            String id = idSeleccionado;

            if (id == null || id.isBlank()) {
                id = campoId.getText().trim();
            }

            if (id.isBlank()) {
                throw new IllegalArgumentException("Debe seleccionar o indicar una configuración para desactivar.");
            }

            ConfiguracionComponente configuracion = controlador.consultar(id);

            if (configuracion == null) {
                throw new IllegalArgumentException("No existe una configuración con el ID: " + id);
            }

            if (!configuracion.isActivo()) {
                throw new IllegalArgumentException("La configuración ya está desactivada.");
            }

            int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea desactivar " + configuracion.getNombre() + "?", "Confirmar desactivación", JOptionPane.YES_NO_OPTION);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            controlador.desactivar(id);
            mostrarCatalogo();

            ConfiguracionComponente actualizada = controlador.consultar(id);

            if (actualizada != null) {
                cargarFormulario(actualizada);
            }

            JOptionPane.showMessageDialog(this, "Configuración desactivada correctamente.", "Desactivar", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void guardarCatalogo() {
        try {
            controlador.guardar();
            JOptionPane.showMessageDialog(this, "Catálogo guardado correctamente.", "Guardar", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarCatalogo() {
        try {
            controlador.cargar();
            idSeleccionado = null;
            limpiarFormulario();
            mostrarCatalogo();
            JOptionPane.showMessageDialog(this, "Catálogo cargado correctamente.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarFormulario() {
        campoId.setText("");
        campoNombre.setText("");
        comboTipo.setSelectedIndex(0);
        campoVida.setText("");
        campoDanio.setText("");
        campoFrecuencia.setText("");
        campoAlcance.setText("");
        campoRadio.setText("");
        campoCosto.setText("");
        campoMisionMinima.setText("");
        checkAtacaAereo.setSelected(false);
        campoCantidadAtaques.setText("");
        campoMaxObjetivos.setText("");
        campoIntervaloMovimiento.setText("");
        campoImagenNormal.setText("");
        campoImagenMovimiento.setText("");
        campoImagenAtaque.setText("");
    }

    public void mostrarCatalogo() {
        try {
            List<ConfiguracionComponente> configuraciones = controlador.listar();
            modeloTabla.setRowCount(0);

            for (ConfiguracionComponente cfg : configuraciones) {
                EstadisticasCombate stats = cfg.getBase();
                Object[] fila = {cfg.getId(), cfg.getNombre(), cfg.getTipo(), stats.getVidaMaxima(), stats.getDanioGolpe(), stats.getFrecuenciaAtaque(), stats.getAlcance(), stats.getRadioEfecto(), stats.getCostoCapacidad(), cfg.getMisionMinima(), cfg.isActivo()};
                modeloTabla.addRow(fila);
            }
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void habilitarBotonesAdministrativos(boolean habilitados) {
        botonCrear.setEnabled(habilitados);
        botonModificar.setEnabled(habilitados);
        botonConsultar.setEnabled(habilitados);
        botonDesactivar.setEnabled(habilitados);
        botonGuardar.setEnabled(habilitados);
        botonCargar.setEnabled(habilitados);
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            char[] claveAdmin = "marte123".toCharArray();

            try {
                Path archivo = Path.of("datos", "catalogo.dat");
                RepositorioCatalogo repo = new RepositorioCatalogo(archivo);
                AutenticadorAdmin auth = new AutenticadorAdmin("admin", claveAdmin);
                ControladorAdmin controlador = new ControladorAdmin(repo, auth);
                VentanaConfiguracion ventana = new VentanaConfiguracion(controlador);
                ventana.setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null, "No se pudo iniciar el administrador: " + e.getMessage(), "Error de inicio", JOptionPane.ERROR_MESSAGE);
            } finally {
                Arrays.fill(claveAdmin, '\0');
            }
        });
    }
       
}