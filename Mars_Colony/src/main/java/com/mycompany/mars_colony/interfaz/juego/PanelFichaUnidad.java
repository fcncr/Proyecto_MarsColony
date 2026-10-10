package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.interfaz.comun.BarraVidaMars;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PanelFichaUnidad extends PanelPantallaJuego {

    public enum Origen {
        PREPARACION,
        ARSENAL
    }

    private final Runnable accionPreparacion;
    private final Runnable accionArsenal;
    private final Consumer<Defensa> accionMover;

    private final JLabel imagen;
    private final JLabel nombre;
    private final JLabel identidad;
    private final JLabel posicion;
    private final JLabel misionMinima;
    private final BarraVidaMars vida;

    private final JLabel valorVida;
    private final JLabel valorDanio;
    private final JLabel valorFrecuencia;
    private final JLabel valorAlcance;
    private final JLabel valorRadio;
    private final JLabel valorCosto;

    private final DefaultTableModel modeloCrecimiento;
    private final BotonMars botonMover;

    private Defensa defensaActual;

    public PanelFichaUnidad(Runnable accionPreparacion, Runnable accionArsenal, Consumer<Defensa> accionMover) {
        super("FICHA DE UNIDAD", "Identidad, estadísticas y crecimiento", "");

        this.accionPreparacion = accionPreparacion;
        this.accionArsenal = accionArsenal;
        this.accionMover = accionMover;

        JPanel raiz = new JPanel(new BorderLayout(18, 14));
        raiz.setOpaque(false);

        PanelMars identidadPanel = new PanelMars(TemaMars.FONDO);
        identidadPanel.setPreferredSize(new Dimension(330, 100));
        identidadPanel.setLayout(new BoxLayout(identidadPanel, BoxLayout.Y_AXIS));

        imagen = new JLabel();
        imagen.setAlignmentX(CENTER_ALIGNMENT);
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagen.setMaximumSize(new Dimension(210, 210));

        nombre = new JLabel("-");
        nombre.setFont(TemaMars.tituloPantalla());
        nombre.setForeground(TemaMars.TEXTO_CLARO);
        nombre.setAlignmentX(CENTER_ALIGNMENT);

        identidad = new JLabel("-");
        identidad.setFont(TemaMars.textoNormal());
        identidad.setForeground(TemaMars.ENERGIA);
        identidad.setAlignmentX(CENTER_ALIGNMENT);

        posicion = new JLabel("-");
        posicion.setFont(TemaMars.textoSecundario());
        posicion.setForeground(TemaMars.TEXTO_CLARO);
        posicion.setAlignmentX(CENTER_ALIGNMENT);

        misionMinima = new JLabel("-");
        misionMinima.setFont(TemaMars.textoSecundario());
        misionMinima.setForeground(TemaMars.TEXTO_CLARO);
        misionMinima.setAlignmentX(CENTER_ALIGNMENT);

        vida = new BarraVidaMars();
        vida.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        identidadPanel.add(Box.createVerticalGlue());
        identidadPanel.add(imagen);
        identidadPanel.add(Box.createVerticalStrut(16));
        identidadPanel.add(nombre);
        identidadPanel.add(Box.createVerticalStrut(6));
        identidadPanel.add(identidad);
        identidadPanel.add(Box.createVerticalStrut(12));
        identidadPanel.add(posicion);
        identidadPanel.add(Box.createVerticalStrut(5));
        identidadPanel.add(misionMinima);
        identidadPanel.add(Box.createVerticalStrut(18));
        identidadPanel.add(vida);
        identidadPanel.add(Box.createVerticalGlue());

        PanelMars detalle = new PanelMars();
        detalle.setLayout(new BorderLayout(0, 15));

        JPanel estadisticas = new JPanel(new GridLayout(2, 3, 12, 12));
        estadisticas.setOpaque(false);
        estadisticas.setPreferredSize(new Dimension(100, 190));

        valorVida = new JLabel("-");
        valorDanio = new JLabel("-");
        valorFrecuencia = new JLabel("-");
        valorAlcance = new JLabel("-");
        valorRadio = new JLabel("-");
        valorCosto = new JLabel("-");

        estadisticas.add(crearEstadistica("VIDA", valorVida));
        estadisticas.add(crearEstadistica("DAÑO", valorDanio));
        estadisticas.add(crearEstadistica("FRECUENCIA", valorFrecuencia));
        estadisticas.add(crearEstadistica("ALCANCE", valorAlcance));
        estadisticas.add(crearEstadistica("RADIO", valorRadio));
        estadisticas.add(crearEstadistica("COSTO", valorCosto));

        modeloCrecimiento = new DefaultTableModel(new String[]{"Misión", "Vida", "Daño"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloCrecimiento);
        tabla.setRowHeight(36);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);

        detalle.add(estadisticas, BorderLayout.NORTH);
        detalle.add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new GridLayout(1, 3, 12, 0));
        pie.setOpaque(false);

        BotonMars preparacion = BotonMars.secundario("Volver al perímetro");
        preparacion.addActionListener(e -> accionPreparacion.run());

        BotonMars arsenal = BotonMars.secundario("Volver al arsenal");
        arsenal.addActionListener(e -> accionArsenal.run());

        botonMover = BotonMars.primario("MOVER UNIDAD");
        botonMover.addActionListener(e -> {
            if (defensaActual != null) {
                accionMover.accept(defensaActual);
            }
        });

        pie.add(preparacion);
        pie.add(arsenal);
        pie.add(botonMover);

        raiz.add(identidadPanel, BorderLayout.WEST);
        raiz.add(detalle, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);
    }

    private PanelMars crearEstadistica(String titulo, JLabel valor) {
        PanelMars panel = new PanelMars(TemaMars.PANEL_SECUNDARIO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(TemaMars.TEXTO_SECUNDARIO);

        valor.setFont(TemaMars.tituloPanel());
        valor.setForeground(TemaMars.TEXTO);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(10));
        panel.add(valor);

        return panel;
    }

    public void mostrar(ComponenteCombate componente, Origen origen) {
        if (componente == null) {
            return;
        }

        defensaActual = componente instanceof Defensa defensa ? defensa : null;

        imagen.setIcon(GestorImagenes.cargar(componente.getRutaImagenActual(), 205, 205));
        nombre.setText(componente.getNombre());
        identidad.setText(componente.getClass().getSimpleName() + " · Nivel " + componente.getNivel());
        posicion.setText("Posición: " + (componente.getPosicion() == null ? "Sin colocar" : componente.getPosicion()));
        misionMinima.setText("Misión mínima: " + componente.getMisionMinima());

        vida.setVida(componente.getVidaActual(), componente.getVidaMaxima());

        valorVida.setText(formatear(componente.getVidaMaxima()));
        valorDanio.setText(formatear(componente.getDanioGolpe()));
        valorFrecuencia.setText(formatear(componente.getFrecuenciaAtaque()));
        valorAlcance.setText(String.valueOf(componente.getAlcance()));
        valorRadio.setText(String.valueOf(componente.getRadioEfecto()));
        valorCosto.setText(String.valueOf(componente.getCostoCapacidad()));

        modeloCrecimiento.setRowCount(0);

        for (RegistroCrecimiento crecimiento : componente.getCrecimientos()) {
            modeloCrecimiento.addRow(new Object[]{
                crecimiento.getNumeroMision(),
                formatear(crecimiento.getVidaAnterior()) + " → " + formatear(crecimiento.getVidaNueva()),
                formatear(crecimiento.getDanioAnterior()) + " → " + formatear(crecimiento.getDanioNuevo())
            });
        }

        botonMover.setVisible(defensaActual != null && defensaActual.getPosicion() != null);

        setContexto("");
    }

    public void mostrar(ConfiguracionComponente configuracion) {
        if (configuracion == null) {
            return;
        }

        defensaActual = null;

        imagen.setIcon(GestorImagenes.cargar(configuracion.getImagenes().getNormal(), 205, 205));
        nombre.setText(configuracion.getNombre());
        identidad.setText(configuracion.getTipo().name());
        posicion.setText("Configuración base");
        misionMinima.setText("Misión mínima: " + configuracion.getMisionMinima());

        vida.setVida(configuracion.getBase().getVidaMaxima(), configuracion.getBase().getVidaMaxima());

        valorVida.setText(formatear(configuracion.getBase().getVidaMaxima()));
        valorDanio.setText(formatear(configuracion.getBase().getDanioGolpe()));
        valorFrecuencia.setText(formatear(configuracion.getBase().getFrecuenciaAtaque()));
        valorAlcance.setText(String.valueOf(configuracion.getBase().getAlcance()));
        valorRadio.setText(String.valueOf(configuracion.getBase().getRadioEfecto()));
        valorCosto.setText(String.valueOf(configuracion.getBase().getCostoCapacidad()));

        modeloCrecimiento.setRowCount(0);
        modeloCrecimiento.addRow(new Object[]{"Base", "-", "-"});

        botonMover.setVisible(false);
        setContexto("");
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##").format(valor);
    }
}