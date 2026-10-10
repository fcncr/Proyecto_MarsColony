package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.registro.RegistroCombate;
import com.mycompany.mars_colony.modelo.registro.ResumenInteraccion;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class VentanaResultado extends JDialog {

    private final Mision mision;
    private final AccionesResultado acciones;

    private final JLabel etiquetaMision;
    private final JLabel etiquetaResultado;
    private final DefaultTableModel modeloParticipantes;
    private final JTable tablaParticipantes;
    private final JLabel etiquetaUnidadSeleccionada;
    private final JLabel etiquetaDanio;
    private final JLabel etiquetaFrecuencia;
    private final JLabel etiquetaPosicionFinal;
    private final DefaultTableModel modeloObjetivos;
    private final JTable tablaObjetivos;
    private final DefaultTableModel modeloAtacantes;
    private final JTable tablaAtacantes;
    private final JButton botonRepetir;
    private final JButton botonAvanzar;
    private final JButton botonFinalizar;
    private final JButton botonCerrar;

    public interface AccionesResultado {

        boolean puedeRepetir();

        boolean puedeAvanzar();

        boolean puedeFinalizarCampania();

        boolean repetirMision();

        boolean avanzar();

        boolean finalizarCampania();
    }

    public VentanaResultado(Mision mision) {
        this((Window) null, mision, null);
    }

    public VentanaResultado(Window propietario, Mision mision) {
        this(propietario, mision, null);
    }

    public VentanaResultado(Mision mision, AccionesResultado acciones) {
        this((Window) null, mision, acciones);
    }

    public VentanaResultado(Window propietario, Mision mision, AccionesResultado acciones) {
        super(propietario, "Resultado de la misión", Dialog.ModalityType.APPLICATION_MODAL);

        this.mision = validarMisionFinalizada(mision);
        this.acciones = acciones;
        this.etiquetaMision = new JLabel("", SwingConstants.CENTER);
        this.etiquetaResultado = new JLabel("", SwingConstants.CENTER);

        String[] columnasParticipantes = {"Nombre", "Tipo", "Bando", "Vida inicial", "Vida final", "Estado"};

        this.modeloParticipantes = new DefaultTableModel(columnasParticipantes, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        this.tablaParticipantes = new JTable(modeloParticipantes);
        this.etiquetaUnidadSeleccionada = new JLabel("-");
        this.etiquetaDanio = new JLabel("-");
        this.etiquetaFrecuencia = new JLabel("-");
        this.etiquetaPosicionFinal = new JLabel("-");
        this.modeloObjetivos = crearModeloInteracciones();
        this.tablaObjetivos = new JTable(modeloObjetivos);
        this.modeloAtacantes = crearModeloInteracciones();
        this.tablaAtacantes = new JTable(modeloAtacantes);
        this.botonRepetir = new JButton("Repetir misión");
        this.botonAvanzar = new JButton("Avanzar");
        this.botonFinalizar = new JButton("Finalizar campaña");
        this.botonCerrar = new JButton("Cerrar");

        configurarVentana();
        construirInterfaz();
        mostrarResultado();
        conectarEventos();
        cargarParticipantes();
        actualizarAccionesDisponibles();
    }

    private static Mision validarMisionFinalizada(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }

        if (mision.getEstado() != EstadoMision.VICTORIA && mision.getEstado() != EstadoMision.DERROTA) {
            throw new IllegalArgumentException("La ventana de resultados requiere una misión finalizada.");
        }

        return mision;
    }

    private DefaultTableModel crearModeloInteracciones() {
        String[] columnas = {"Unidad", "Golpes", "Daño acumulado"};

        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1100, 720);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSiPermitido();
            }
        });
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelResultado = new JPanel(new GridLayout(2, 1, 5, 5));

        etiquetaMision.setFont(etiquetaMision.getFont().deriveFont(Font.BOLD, 18f));
        etiquetaResultado.setFont(etiquetaResultado.getFont().deriveFont(Font.BOLD, 30f));

        panelResultado.add(etiquetaMision);
        panelResultado.add(etiquetaResultado);

        tablaParticipantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaParticipantes.setAutoCreateRowSorter(true);
        tablaParticipantes.setFillsViewportHeight(true);

        JScrollPane scrollParticipantes = new JScrollPane(tablaParticipantes);
        scrollParticipantes.setBorder(BorderFactory.createTitledBorder("Participantes de la misión"));

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollParticipantes, crearPanelDetalle());
        division.setResizeWeight(0.45);
        division.setContinuousLayout(true);

        contenido.add(panelResultado, BorderLayout.NORTH);
        contenido.add(division, BorderLayout.CENTER);
        contenido.add(crearPanelAcciones(), BorderLayout.SOUTH);

        setContentPane(contenido);
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Detalle individual de combate"));

        JPanel datos = new JPanel(new GridLayout(2, 4, 8, 5));

        datos.add(new JLabel("Unidad:"));
        datos.add(etiquetaUnidadSeleccionada);
        datos.add(new JLabel("Daño:"));
        datos.add(etiquetaDanio);
        datos.add(new JLabel("Frecuencia:"));
        datos.add(etiquetaFrecuencia);
        datos.add(new JLabel("Posición final:"));
        datos.add(etiquetaPosicionFinal);

        tablaObjetivos.setFillsViewportHeight(true);
        tablaAtacantes.setFillsViewportHeight(true);

        JScrollPane scrollObjetivos = new JScrollPane(tablaObjetivos);
        scrollObjetivos.setBorder(BorderFactory.createTitledBorder("Objetivos atacados"));

        JScrollPane scrollAtacantes = new JScrollPane(tablaAtacantes);
        scrollAtacantes.setBorder(BorderFactory.createTitledBorder("Atacantes recibidos"));

        JPanel historiales = new JPanel(new GridLayout(1, 2, 10, 0));
        historiales.add(scrollObjetivos);
        historiales.add(scrollAtacantes);

        panel.add(datos, BorderLayout.NORTH);
        panel.add(historiales, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel();

        panel.add(botonRepetir);
        panel.add(botonAvanzar);
        panel.add(botonFinalizar);
        panel.add(botonCerrar);

        return panel;
    }

    private void conectarEventos() {
        botonCerrar.addActionListener(e -> cerrarSiPermitido());
        botonRepetir.addActionListener(e -> ejecutarRepeticion());
        botonAvanzar.addActionListener(e -> ejecutarAvance());
        botonFinalizar.addActionListener(e -> ejecutarFinalizacion());

        tablaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleSeleccionado();
            }
        });
    }

    private void mostrarResultado() {
        etiquetaMision.setText("Misión " + mision.getNumero());
        etiquetaResultado.setText(mision.getEstado() == EstadoMision.VICTORIA ? "VICTORIA" : "DERROTA");
    }

    private void cargarParticipantes() {
        modeloParticipantes.setRowCount(0);

        for (ComponenteCombate participante : mision.getParticipantes()) {
            RegistroCombate registro = participante.getRegistroCombate();
            String estado = participante.estaDestruido() ? "DESTRUIDO" : "VIVO";

            modeloParticipantes.addRow(new Object[]{
                participante.getNombre(),
                registro.getTipo(),
                participante.getBando(),
                registro.getVidaInicial(),
                registro.getVidaFinal(),
                estado
            });
        }

        if (modeloParticipantes.getRowCount() > 0) {
            tablaParticipantes.setRowSelectionInterval(0, 0);
        }
    }

    private void mostrarDetalleSeleccionado() {
        int filaVista = tablaParticipantes.getSelectedRow();

        if (filaVista < 0) {
            limpiarDetalle();
            return;
        }

        int filaModelo = tablaParticipantes.convertRowIndexToModel(filaVista);
        List<ComponenteCombate> participantes = mision.getParticipantes();

        if (filaModelo < 0 || filaModelo >= participantes.size()) {
            limpiarDetalle();
            return;
        }

        ComponenteCombate participante = participantes.get(filaModelo);
        RegistroCombate registro = participante.getRegistroCombate();
        Posicion posicionFinal = registro.getPosicionFinal();

        etiquetaUnidadSeleccionada.setText(participante.getNombre());
        etiquetaDanio.setText(String.valueOf(registro.getDanio()));
        etiquetaFrecuencia.setText(String.valueOf(registro.getFrecuencia()));
        etiquetaPosicionFinal.setText(posicionFinal == null ? "Sin posición" : posicionFinal.toString());

        cargarInteracciones(modeloObjetivos, registro.getObjetivosAtacados());
        cargarInteracciones(modeloAtacantes, registro.getAtacantesRecibidos());
    }

    private void cargarInteracciones(DefaultTableModel modelo, Map<String, ResumenInteraccion> interacciones) {
        modelo.setRowCount(0);

        for (ResumenInteraccion resumen : interacciones.values()) {
            modelo.addRow(new Object[]{
                resumen.getNombre(),
                resumen.getCantidadGolpes(),
                resumen.getDanioTotal()
            });
        }
    }

    private void limpiarDetalle() {
        etiquetaUnidadSeleccionada.setText("-");
        etiquetaDanio.setText("-");
        etiquetaFrecuencia.setText("-");
        etiquetaPosicionFinal.setText("-");
        modeloObjetivos.setRowCount(0);
        modeloAtacantes.setRowCount(0);
    }

    private void actualizarAccionesDisponibles() {
        if (acciones == null) {
            botonRepetir.setEnabled(false);
            botonAvanzar.setEnabled(false);
            botonFinalizar.setVisible(false);
            botonCerrar.setVisible(true);
            return;
        }

        boolean repetir = acciones.puedeRepetir();
        boolean avanzar = acciones.puedeAvanzar();
        boolean finalizar = acciones.puedeFinalizarCampania();

        botonRepetir.setEnabled(repetir);
        botonAvanzar.setEnabled(avanzar);
        botonFinalizar.setVisible(finalizar);
        botonFinalizar.setEnabled(finalizar);
        botonCerrar.setVisible(!repetir && !avanzar && !finalizar);
    }

    private void cerrarSiPermitido() {
        if (acciones == null) {
            dispose();
            return;
        }

        boolean hayAccion = acciones.puedeRepetir()
                || acciones.puedeAvanzar()
                || acciones.puedeFinalizarCampania();

        if (!hayAccion) {
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Seleccione Repetir misión, Avanzar o Finalizar campaña antes de cerrar el resultado.",
                "Resultado de la misión",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void ejecutarRepeticion() {
        if (acciones != null && acciones.repetirMision()) {
            dispose();
            return;
        }

        mostrarErrorAccion("No fue posible repetir la misión.");
        actualizarAccionesDisponibles();
    }

    private void ejecutarAvance() {
        if (acciones != null && acciones.avanzar()) {
            dispose();
            return;
        }

        mostrarErrorAccion("No fue posible avanzar a la siguiente misión.");
        actualizarAccionesDisponibles();
    }

    private void ejecutarFinalizacion() {
        if (acciones != null && acciones.finalizarCampania()) {
            dispose();
            return;
        }

        mostrarErrorAccion("No fue posible finalizar la campaña.");
        actualizarAccionesDisponibles();
    }

    private void mostrarErrorAccion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acción no disponible", JOptionPane.WARNING_MESSAGE);
    }

    public static void abrir(Mision mision) {
        abrir(null, mision, null);
    }

    public static void abrir(Window propietario, Mision mision) {
        abrir(propietario, mision, null);
    }

    public static void abrir(Mision mision, AccionesResultado acciones) {
        abrir(null, mision, acciones);
    }

    public static void abrir(Window propietario, Mision mision, AccionesResultado acciones) {
        validarMisionFinalizada(mision);

        ejecutarEnEdt(() -> {
            VentanaResultado ventana = new VentanaResultado(propietario, mision, acciones);
            ventana.setVisible(true);
        });
    }

    private static void ejecutarEnEdt(Runnable tarea) {
        if (SwingUtilities.isEventDispatchThread()) {
            tarea.run();
        } else {
            SwingUtilities.invokeLater(tarea);
        }
    }

    public void actualizarVista() {
        ejecutarEnEdt(this::actualizarVistaEnEdt);
    }

    private void actualizarVistaEnEdt() {
        mostrarResultado();
        cargarParticipantes();
        actualizarAccionesDisponibles();
        revalidate();
        repaint();
    }

    public Mision getMision() {
        return mision;
    }
}