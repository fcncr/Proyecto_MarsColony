package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TarjetaUnidadMars extends TarjetaMars {

    private final JLabel etiquetaImagen;
    private final JLabel etiquetaNombre;
    private final JLabel etiquetaTipo;
    private final JLabel etiquetaDetalle;
    private final JPanel panelAcciones;

    public TarjetaUnidadMars() {
        setLayout(new BorderLayout(10, 12));
        setPreferredSize(new Dimension(300, 330));

        etiquetaImagen = new JLabel();
        etiquetaImagen.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaImagen.setPreferredSize(new Dimension(150, 150));

        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(new BoxLayout(informacion, BoxLayout.Y_AXIS));

        etiquetaNombre = new JLabel("UNIDAD");
        etiquetaNombre.setFont(TemaMars.tituloPanel());
        etiquetaNombre.setForeground(TemaMars.TEXTO);
        etiquetaNombre.setAlignmentX(LEFT_ALIGNMENT);

        etiquetaTipo = new JLabel("-");
        etiquetaTipo.setFont(TemaMars.textoNormal());
        etiquetaTipo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        etiquetaTipo.setAlignmentX(LEFT_ALIGNMENT);

        etiquetaDetalle = new JLabel("-");
        etiquetaDetalle.setFont(TemaMars.textoDestacado());
        etiquetaDetalle.setForeground(TemaMars.TEXTO);
        etiquetaDetalle.setAlignmentX(LEFT_ALIGNMENT);

        informacion.add(etiquetaNombre);
        informacion.add(Box.createVerticalStrut(4));
        informacion.add(etiquetaTipo);
        informacion.add(Box.createVerticalStrut(10));
        informacion.add(etiquetaDetalle);

        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelAcciones.setOpaque(false);

        add(etiquetaImagen, BorderLayout.NORTH);
        add(informacion, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void setImagen(ImageIcon imagen) {
        etiquetaImagen.setIcon(imagen);
    }

    public void setRutaImagen(String ruta) {
        etiquetaImagen.setIcon(GestorImagenes.cargar(ruta, 145, 145));
    }

    public void setNombre(String nombre) {
        etiquetaNombre.setText(nombre == null ? "" : nombre);
    }

    public void setTipo(String tipo) {
        etiquetaTipo.setText(tipo == null ? "" : tipo);
    }

    public void setDetalle(String detalle) {
        etiquetaDetalle.setText(detalle == null ? "" : detalle);
    }

    public void limpiarAcciones() {
        panelAcciones.removeAll();
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }

    public void agregarAccion(AbstractButton boton) {
        if (boton != null) {
            panelAcciones.add(boton);
            panelAcciones.revalidate();
            panelAcciones.repaint();
        }
    }
}