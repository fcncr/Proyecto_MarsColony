package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.persistencia.RepositorioPartidas;
import com.mycompany.mars_colony.util.RutasAplicacion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PanelCargarPartida extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionInicio;
    private final Runnable accionCargada;
    private final JTextField campoBuscar;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final TableRowSorter<DefaultTableModel> filtro;
    private final JLabel etiquetaNombre;
    private final JLabel etiquetaMision;
    private final JLabel etiquetaCapacidad;
    private final JLabel etiquetaEstado;
    private final BotonMars botonCargar;
    private final List<ResumenGuardado> resumenes;

    public PanelCargarPartida(ControladorJuego controlador, Runnable accionInicio, Runnable accionCargada) {
        super("CARGAR PARTIDA", "Elige el comandante con el que quieres continuar", "");

        this.controlador = controlador;
        this.accionInicio = accionInicio;
        this.accionCargada = accionCargada;
        this.resumenes = new ArrayList<>();

        JPanel principal = new JPanel(new GridLayout(1, 2, 18, 0));
        principal.setOpaque(false);

        PanelMars izquierda = new PanelMars();
        izquierda.setLayout(new BorderLayout(0, 14));

        JPanel buscador = new JPanel();
        buscador.setOpaque(false);
        buscador.setLayout(new BoxLayout(buscador, BoxLayout.Y_AXIS));

        JLabel etiquetaBuscar = new JLabel("Buscar comandante");
        etiquetaBuscar.setFont(TemaMars.textoDestacado());
        etiquetaBuscar.setForeground(TemaMars.TEXTO);
        etiquetaBuscar.setAlignmentX(LEFT_ALIGNMENT);

        campoBuscar = new JTextField();
        campoBuscar.setFont(TemaMars.textoNormal());
        campoBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        campoBuscar.setAlignmentX(LEFT_ALIGNMENT);

        buscador.add(etiquetaBuscar);
        buscador.add(Box.createVerticalStrut(6));
        buscador.add(campoBuscar);

        modelo = new DefaultTableModel(new String[]{"Comandante", "Misión", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(44);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);

        filtro = new TableRowSorter<>(modelo);
        tabla.setRowSorter(filtro);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);

        izquierda.add(buscador, BorderLayout.NORTH);
        izquierda.add(scroll, BorderLayout.CENTER);

        PanelMars derecha = new PanelMars();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));

        etiquetaNombre = new JLabel("SELECCIONA UNA EXPEDICIÓN");
        etiquetaNombre.setFont(TemaMars.tituloPantalla());
        etiquetaNombre.setForeground(TemaMars.TEXTO);
        etiquetaNombre.setAlignmentX(CENTER_ALIGNMENT);

        JPanel datos = new JPanel(new GridLayout(1, 2, 12, 0));
        datos.setOpaque(false);
        datos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        etiquetaMision = new JLabel("--");
        etiquetaCapacidad = new JLabel("--");

        datos.add(crearDato("MISIÓN", etiquetaMision));
        datos.add(crearDato("CAPACIDAD", etiquetaCapacidad));

        etiquetaEstado = new JLabel("Selecciona una expedición.");
        etiquetaEstado.setFont(TemaMars.textoNormal());
        etiquetaEstado.setForeground(TemaMars.TEXTO_SECUNDARIO);
        etiquetaEstado.setAlignmentX(CENTER_ALIGNMENT);

        botonCargar = BotonMars.primario("CARGAR EXPEDICIÓN");
        botonCargar.setAlignmentX(CENTER_ALIGNMENT);
        botonCargar.setMaximumSize(new Dimension(Integer.MAX_VALUE, TemaMars.ALTURA_BOTON));
        botonCargar.setEnabled(false);
        botonCargar.addActionListener(e -> cargarSeleccionada());

        derecha.add(Box.createVerticalGlue());
        derecha.add(etiquetaNombre);
        derecha.add(Box.createVerticalStrut(35));
        derecha.add(datos);
        derecha.add(Box.createVerticalStrut(24));
        derecha.add(etiquetaEstado);
        derecha.add(Box.createVerticalGlue());
        derecha.add(botonCargar);

        principal.add(izquierda);
        principal.add(derecha);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setOpaque(false);
        raiz.add(principal, BorderLayout.CENTER);

        BotonMars inicio = BotonMars.secundario("‹ Inicio");
        inicio.setPreferredSize(new Dimension(150, TemaMars.ALTURA_BOTON));
        inicio.addActionListener(e -> accionInicio.run());

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(inicio, BorderLayout.WEST);

        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);

        campoBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarPreview();
            }
        });
    }

    private PanelMars crearDato(String titulo, JLabel valor) {
        PanelMars panel = new PanelMars(TemaMars.PANEL_SECUNDARIO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(TemaMars.TEXTO_SECUNDARIO);

        valor.setFont(TemaMars.tituloPanel());
        valor.setForeground(TemaMars.TEXTO);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(8));
        panel.add(valor);

        return panel;
    }

    public void actualizar() {
        resumenes.clear();
        modelo.setRowCount(0);
        botonCargar.setEnabled(false);

        etiquetaNombre.setText("SELECCIONA UNA EXPEDICIÓN");
        etiquetaMision.setText("--");
        etiquetaCapacidad.setText("--");
        etiquetaEstado.setText("Selecciona una expedición.");

        campoBuscar.setText("");

        try {
            RepositorioPartidas repositorio = new RepositorioPartidas(RutasAplicacion.resolver("partidas"));

            for (String nombre : controlador.listarPartidasGuardadas()) {
                try {
                    Partida partida = repositorio.cargar(nombre);

                    String estado = partida.getMision() == null || partida.getMision().getEstado() == null ? "-" : formatearEstado(partida.getMision().getEstado().name());

                    ResumenGuardado resumen = new ResumenGuardado(nombre, partida.getMisionActual(), partida.getEscuadron().getCapacidadTotal(), estado, true);

                    resumenes.add(resumen);
                    modelo.addRow(new Object[]{nombre, String.format("%02d", resumen.mision), estado});
                } catch (Exception e) {
                    ResumenGuardado resumen = new ResumenGuardado(nombre, 0, 0, "No disponible", false);

                    resumenes.add(resumen);
                    modelo.addRow(new Object[]{nombre, "--", "No disponible"});
                }
            }
        } catch (Exception e) {
            DialogoMars.error(this, "No se pudo cargar", "No fue posible consultar las expediciones guardadas.");
        }
    }

    private void filtrar() {
        String texto = campoBuscar.getText() == null ? "" : campoBuscar.getText().trim();

        if (texto.isEmpty()) {
            filtro.setRowFilter(null);
        } else {
            filtro.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0));
        }
    }

    private void actualizarPreview() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            botonCargar.setEnabled(false);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        if (filaModelo < 0 || filaModelo >= resumenes.size()) {
            botonCargar.setEnabled(false);
            return;
        }

        ResumenGuardado resumen = resumenes.get(filaModelo);

        etiquetaNombre.setText(resumen.nombre.toUpperCase());
        etiquetaMision.setText(resumen.mision <= 0 ? "--" : String.format("%02d", resumen.mision));
        etiquetaCapacidad.setText(resumen.capacidad <= 0 ? "--" : resumen.capacidad + " puntos");

        if (resumen.valido) {
            etiquetaEstado.setText("Estado: " + resumen.estado);
        } else {
            etiquetaEstado.setText("Este guardado no puede abrirse.");
        }

        botonCargar.setEnabled(resumen.valido);
    }

    private void cargarSeleccionada() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        if (filaModelo < 0 || filaModelo >= resumenes.size()) {
            return;
        }

        ResumenGuardado resumen = resumenes.get(filaModelo);

        if (!resumen.valido) {
            DialogoMars.error(this, "No se pudo cargar", "Este archivo no es válido.");
            return;
        }

        try {
            controlador.cargarPartida(resumen.nombre);
            accionCargada.run();
        } catch (RuntimeException e) {
            DialogoMars.error(this, "No se pudo cargar", obtenerMensaje(e));
        }
    }

    private String formatearEstado(String estado) {
        String texto = estado.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private String obtenerMensaje(Throwable error) {
        Throwable actual = error;

        while (actual.getCause() != null && actual.getCause() != actual) {
            actual = actual.getCause();
        }

        return actual.getMessage() == null || actual.getMessage().isBlank() ? "No fue posible cargar la expedición." : actual.getMessage();
    }

    private static class ResumenGuardado {

        private final String nombre;
        private final int mision;
        private final int capacidad;
        private final String estado;
        private final boolean valido;

        private ResumenGuardado(String nombre, int mision, int capacidad, String estado, boolean valido) {
            this.nombre = nombre;
            this.mision = mision;
            this.capacidad = capacidad;
            this.estado = estado;
            this.valido = valido;
        }
    }
}