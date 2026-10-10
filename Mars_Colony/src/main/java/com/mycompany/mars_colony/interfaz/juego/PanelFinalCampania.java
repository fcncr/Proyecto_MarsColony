package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelFinalCampania extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionFinalizar;
    private final Runnable accionGenerarExtra;

    private final JLabel etiquetaProgreso;
    private final BotonMars botonFinalizar;
    private final BotonMars botonExtra;

    public PanelFinalCampania(ControladorJuego controlador, Runnable accionFinalizar, Runnable accionGenerarExtra) {
        super("CAMPAÑA COMPLETADA", "El final de una expedición puede ser el inicio de otra", "");

        this.controlador = controlador;
        this.accionFinalizar = accionFinalizar;
        this.accionGenerarExtra = accionGenerarExtra;

        JPanel escenario = new JPanel(new GridBagLayout());
        escenario.setOpaque(false);

        PanelMars tarjeta = new PanelMars();
        tarjeta.setPreferredSize(new Dimension(880, 560));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        etiquetaProgreso = new JLabel(
                "10 / 10 MISIONES SUPERADAS",
                SwingConstants.CENTER
        );

        etiquetaProgreso.setOpaque(true);
        etiquetaProgreso.setBackground(TemaMars.FONDO);
        etiquetaProgreso.setForeground(TemaMars.ACCION);
        etiquetaProgreso.setFont(TemaMars.textoDestacado());
        etiquetaProgreso.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        etiquetaProgreso.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("La colonia está a salvo.");
        titulo.setFont(TemaMars.tituloGrande().deriveFont(38f));
        titulo.setForeground(TemaMars.TEXTO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nucleo = new JLabel(
                GestorImagenes.cargar(
                        "assets/importados/Nucleo.png",
                        235,
                        235
                )
        );

        nucleo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel mensaje = new JLabel(
                "Puedes finalizar aquí o seguir explorando."
        );

        mensaje.setFont(TemaMars.textoNormal());
        mensaje.setForeground(TemaMars.TEXTO);
        mensaje.setAlignmentX(CENTER_ALIGNMENT);

        JPanel acciones = new JPanel(new GridLayout(1, 2, 18, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(LEFT_ALIGNMENT);
        acciones.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, TemaMars.ALTURA_BOTON)
        );

        botonFinalizar = BotonMars.secundario(
                "FINALIZAR CAMPAÑA"
        );

        botonFinalizar.addActionListener(e -> accionFinalizar.run());

        botonExtra = BotonMars.primario("GENERAR MISIÓN 11");
        botonExtra.addActionListener(e -> accionGenerarExtra.run());

        acciones.add(botonFinalizar);
        acciones.add(botonExtra);

        tarjeta.add(etiquetaProgreso);
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(nucleo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(mensaje);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(acciones);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;

        escenario.add(tarjeta, gbc);

        setContenido(escenario);
    }

    public void preparar() {
        Partida partida = controlador.consultarEstado();

        if (partida == null) {
            return;
        }

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
        );

        etiquetaProgreso.setText(
                partida.getTotalMisiones()
                + " / "
                + partida.getTotalMisiones()
                + " MISIONES SUPERADAS"
        );

        botonExtra.setText(
                "GENERAR MISIÓN "
                + String.format(
                        "%02d",
                        partida.getMisionActual() + 1
                )
        );

        botonFinalizar.setEnabled(
                controlador.puedeFinalizarCampania()
        );

        botonExtra.setEnabled(
                controlador.puedeAvanzarMision()
        );
    }
}