package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.modelo.registro.RegistroCombate;
import com.mycompany.mars_colony.modelo.registro.ResumenInteraccion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PanelInformeCombate extends PanelPantallaJuego {

    private enum Filtro {
        TODOS,
        DEFENSAS,
        CRIATURAS
    }

    private final ControladorJuego controlador;
    private final Runnable accionResultado;

    private final DefaultTableModel modeloParticipantes;
    private final JTable tablaParticipantes;
    private final List<ComponenteCombate> participantesFiltrados;

    private final JLabel imagen;
    private final JLabel nombre;
    private final JLabel identidad;
    private final JLabel estadisticas;
    private final JLabel resumenVida;

    private final DefaultTableModel modeloObjetivos;
    private final DefaultTableModel modeloAtacantes;

    private Filtro filtroActual;

    public PanelInformeCombate(ControladorJuego controlador, Runnable accionResultado) {
        super("INFORME DE COMBATE", "", "");

        this.controlador = controlador;
        this.accionResultado = accionResultado;
        this.participantesFiltrados = new ArrayList<>();
        this.filtroActual = Filtro.TODOS;

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setOpaque(false);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);

        PanelMars izquierda = new PanelMars();
        izquierda.setLayout(new BorderLayout(0, 12));

        JLabel tituloParticipantes = new JLabel("PARTICIPANTES");
        tituloParticipantes.setFont(TemaMars.tituloPanel());
        tituloParticipantes.setForeground(TemaMars.TEXTO);

        JPanel filtros = new JPanel(new GridLayout(1, 3, 10, 0));
        filtros.setOpaque(false);

        JButton todos = crearFiltro("Todos");
        JButton defensas = crearFiltro("Defensas");
        JButton criaturas = crearFiltro("Criaturas");

        todos.addActionListener(e -> {
            filtroActual = Filtro.TODOS;
            cargarParticipantes();
        });

        defensas.addActionListener(e -> {
            filtroActual = Filtro.DEFENSAS;
            cargarParticipantes();
        });

        criaturas.addActionListener(e -> {
            filtroActual = Filtro.CRIATURAS;
            cargarParticipantes();
        });

        filtros.add(todos);
        filtros.add(defensas);
        filtros.add(criaturas);

        JPanel cabeceraIzquierda = new JPanel();
        cabeceraIzquierda.setOpaque(false);
        cabeceraIzquierda.setLayout(
                new BoxLayout(cabeceraIzquierda, BoxLayout.Y_AXIS)
        );

        cabeceraIzquierda.add(tituloParticipantes);
        cabeceraIzquierda.add(Box.createVerticalStrut(12));
        cabeceraIzquierda.add(filtros);

        modeloParticipantes = new DefaultTableModel(
                new String[]{
                    "Participante",
                    "Vida inicial",
                    "Vida final",
                    "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaParticipantes = new JTable(modeloParticipantes);
        tablaParticipantes.setRowHeight(42);
        tablaParticipantes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tablaParticipantes.setBackground(TemaMars.PANEL);
        tablaParticipantes.setForeground(TemaMars.TEXTO);
        tablaParticipantes.getTableHeader().setBackground(TemaMars.FONDO);
        tablaParticipantes.getTableHeader().setForeground(
                TemaMars.TEXTO_CLARO
        );

        tablaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleSeleccionado();
            }
        });

        JScrollPane scrollParticipantes = new JScrollPane(tablaParticipantes);
        scrollParticipantes.setBorder(null);

        JLabel pista = new JLabel(
                "Selecciona una fila para abrir su detalle."
        );

        pista.setFont(TemaMars.textoSecundario());
        pista.setForeground(TemaMars.TEXTO_SECUNDARIO);

        izquierda.add(cabeceraIzquierda, BorderLayout.NORTH);
        izquierda.add(scrollParticipantes, BorderLayout.CENTER);
        izquierda.add(pista, BorderLayout.SOUTH);

        PanelMars derecha = new PanelMars();
        derecha.setLayout(new BorderLayout(0, 14));

        JPanel identidadPanel = new JPanel(new BorderLayout(14, 0));
        identidadPanel.setOpaque(false);

        imagen = new JLabel();
        imagen.setPreferredSize(new Dimension(100, 100));
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        nombre = new JLabel("Selecciona una unidad");
        nombre.setFont(TemaMars.tituloPanel());
        nombre.setForeground(TemaMars.TEXTO);

        identidad = new JLabel("-");
        identidad.setFont(TemaMars.textoSecundario());
        identidad.setForeground(TemaMars.TEXTO_SECUNDARIO);

        estadisticas = new JLabel("-");
        estadisticas.setFont(TemaMars.textoNormal());
        estadisticas.setForeground(TemaMars.TEXTO);

        textos.add(nombre);
        textos.add(Box.createVerticalStrut(4));
        textos.add(identidad);
        textos.add(Box.createVerticalStrut(12));
        textos.add(estadisticas);

        identidadPanel.add(imagen, BorderLayout.WEST);
        identidadPanel.add(textos, BorderLayout.CENTER);

        modeloObjetivos = crearModeloInteracciones();
        modeloAtacantes = crearModeloInteracciones();

        JTable tablaObjetivos = crearTabla(modeloObjetivos);
        JTable tablaAtacantes = crearTabla(modeloAtacantes);

        JTabbedPane historial = new JTabbedPane();
        historial.setFont(TemaMars.textoDestacado());
        historial.addTab(
                "Objetivos atacados",
                new JScrollPane(tablaObjetivos)
        );
        historial.addTab(
                "Atacantes recibidos",
                new JScrollPane(tablaAtacantes)
        );

        resumenVida = new JLabel("-");
        resumenVida.setFont(TemaMars.textoNormal());
        resumenVida.setForeground(TemaMars.TEXTO);

        derecha.add(identidadPanel, BorderLayout.NORTH);
        derecha.add(historial, BorderLayout.CENTER);
        derecha.add(resumenVida, BorderLayout.SOUTH);

        centro.add(izquierda);
        centro.add(derecha);

        BotonMars resultado = BotonMars.secundario("‹ Resultado");
        resultado.setPreferredSize(
                new Dimension(180, TemaMars.ALTURA_BOTON)
        );
        resultado.addActionListener(e -> accionResultado.run());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pie.setOpaque(false);
        pie.add(resultado);

        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);
    }

    private JButton crearFiltro(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(TemaMars.textoDestacado());
        boton.setForeground(TemaMars.TEXTO_CLARO);
        boton.setBackground(TemaMars.FONDO);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(120, 44));
        return boton;
    }

    private DefaultTableModel crearModeloInteracciones() {
        return new DefaultTableModel(
                new String[]{"Nombre / ID", "Golpes", "Daño"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(38);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);
        return tabla;
    }

    public void preparar() {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
        );

        filtroActual = Filtro.TODOS;
        cargarParticipantes();
    }

    private void cargarParticipantes() {
        modeloParticipantes.setRowCount(0);
        participantesFiltrados.clear();

        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        Mision mision = partida.getMision();

        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (!aceptaFiltro(participante)) {
                continue;
            }

            participantesFiltrados.add(participante);

            RegistroCombate registro = participante.getRegistroCombate();

            modeloParticipantes.addRow(new Object[]{
                participante.getNombre()
                + " "
                + abreviarId(participante.getId()),
                formatear(registro.getVidaInicial()),
                formatear(registro.getVidaFinal()),
                participante.estaDestruido()
                ? "Destruido"
                : "Vivo"
            });
        }

        if (!participantesFiltrados.isEmpty()) {
            tablaParticipantes.setRowSelectionInterval(0, 0);
        } else {
            limpiarDetalle();
        }
    }

    private boolean aceptaFiltro(ComponenteCombate componente) {
        return switch (filtroActual) {
            case TODOS -> true;
            case DEFENSAS -> componente instanceof Defensa;
            case CRIATURAS -> componente instanceof Criatura;
        };
    }

    private void mostrarDetalleSeleccionado() {
        int fila = tablaParticipantes.getSelectedRow();

        if (fila < 0 || fila >= participantesFiltrados.size()) {
            limpiarDetalle();
            return;
        }

        ComponenteCombate participante =
                participantesFiltrados.get(fila);

        RegistroCombate registro =
                participante.getRegistroCombate();

        String ruta = participante.getRutaImagenActual();

        if (ruta == null || ruta.isBlank()) {
            imagen.setIcon(null);
        } else {
            imagen.setIcon(
                    GestorImagenes.cargar(ruta, 95, 95)
            );
        }

        nombre.setText(
                participante.getNombre()
                + " "
                + abreviarId(participante.getId())
        );

        Posicion posicion = registro.getPosicionFinal();

        identidad.setText(
                participante.getClass().getSimpleName()
                + " · "
                + (posicion == null
                ? "Sin posición final"
                : posicion.toString())
        );

        estadisticas.setText(
                "Daño: "
                + formatear(registro.getDanio())
                + " · Frecuencia: "
                + formatear(registro.getFrecuencia())
                + " ataques/s"
        );

        cargarInteracciones(
                modeloObjetivos,
                registro.getObjetivosAtacados()
        );

        cargarInteracciones(
                modeloAtacantes,
                registro.getAtacantesRecibidos()
        );

        resumenVida.setText(
                "<html>Vida inicial "
                + formatear(registro.getVidaInicial())
                + " → final "
                + formatear(registro.getVidaFinal())
                + "<br>Daño recibido: "
                + formatear(registro.getDanioRecibidoTotal())
                + "</html>"
        );
    }

    private void cargarInteracciones(DefaultTableModel modelo, Map<String, ResumenInteraccion> mapa) {
        modelo.setRowCount(0);

        int golpes = 0;
        double danio = 0;

        for (ResumenInteraccion resumen : mapa.values()) {
            modelo.addRow(new Object[]{
                resumen.getNombre()
                + " "
                + abreviarId(resumen.getIdUnidad()),
                resumen.getCantidadGolpes(),
                formatear(resumen.getDanioTotal())
            });

            golpes += resumen.getCantidadGolpes();
            danio += resumen.getDanioTotal();
        }

        modelo.addRow(new Object[]{
            "TOTAL",
            golpes,
            formatear(danio)
        });
    }

    private void limpiarDetalle() {
        imagen.setIcon(null);
        nombre.setText("Selecciona una unidad");
        identidad.setText("-");
        estadisticas.setText("-");
        resumenVida.setText("-");
        modeloObjetivos.setRowCount(0);
        modeloAtacantes.setRowCount(0);
    }

    private String abreviarId(String id) {
        if (id == null) {
            return "";
        }

        return "#"
                + id.substring(
                        0,
                        Math.min(4, id.length())
                ).toUpperCase();
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##").format(valor);
    }
}