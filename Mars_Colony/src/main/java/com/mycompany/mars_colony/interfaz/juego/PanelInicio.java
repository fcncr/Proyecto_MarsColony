package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.EtiquetaEstadoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelFondoMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelInicio extends PanelFondoMars {

    private final ControladorJuego controlador;
    private final Runnable accionContinuar;
    private final BotonMars botonContinuar;
    private final JLabel etiquetaPartida;

    public PanelInicio(ControladorJuego controlador, Runnable accionContinuar, Runnable accionNueva, Runnable accionCargar, Runnable accionAyuda, Runnable accionSalir) {
        super("assets/FondoMars.png");

        this.controlador = controlador;
        this.accionContinuar = accionContinuar;

        setLayout(new GridBagLayout());
        setBorder(TemaMars.padding(24));
        setOpacidadOscurecimiento(72);

        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        EtiquetaEstadoMars modo = EtiquetaEstadoMars.energia("ESTRATEGIA  /  UN JUGADOR");
        modo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tituloMars = new JLabel("MARS");
        tituloMars.setFont(TemaMars.tituloGrande().deriveFont(50f));
        tituloMars.setForeground(TemaMars.TEXTO_CLARO);
        tituloMars.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tituloColony = new JLabel("COLONY");
        tituloColony.setFont(TemaMars.tituloGrande().deriveFont(50f));
        tituloColony.setForeground(TemaMars.ACCION);
        tituloColony.setAlignmentX(LEFT_ALIGNMENT);

        botonContinuar = BotonMars.primario("CONTINUAR");
        botonContinuar.setPreferredSize(new Dimension(420, TemaMars.ALTURA_BOTON));
        botonContinuar.setMaximumSize(new Dimension(420, TemaMars.ALTURA_BOTON));
        botonContinuar.setAlignmentX(LEFT_ALIGNMENT);
        botonContinuar.addActionListener(e -> this.accionContinuar.run());

        etiquetaPartida = new JLabel("");
        etiquetaPartida.setForeground(TemaMars.TEXTO_CLARO);
        etiquetaPartida.setFont(TemaMars.textoSecundario());
        etiquetaPartida.setAlignmentX(LEFT_ALIGNMENT);

        BotonMars nueva = BotonMars.secundario("Nueva expedición");
        nueva.setPreferredSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        nueva.setMaximumSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        nueva.setAlignmentX(LEFT_ALIGNMENT);
        nueva.addActionListener(e -> accionNueva.run());

        BotonMars cargar = BotonMars.secundario("Cargar partida");
        cargar.setPreferredSize(new Dimension(195, TemaMars.ALTURA_BOTON));
        cargar.setMaximumSize(new Dimension(195, TemaMars.ALTURA_BOTON));
        cargar.addActionListener(e -> accionCargar.run());

        BotonMars ayuda = BotonMars.secundario("Ayuda");
        ayuda.setPreferredSize(new Dimension(115, TemaMars.ALTURA_BOTON));
        ayuda.setMaximumSize(new Dimension(115, TemaMars.ALTURA_BOTON));
        ayuda.addActionListener(e -> accionAyuda.run());

        JPanel filaSecundaria = new JPanel();
        filaSecundaria.setOpaque(false);
        filaSecundaria.setLayout(new BoxLayout(filaSecundaria, BoxLayout.X_AXIS));
        filaSecundaria.setAlignmentX(LEFT_ALIGNMENT);
        filaSecundaria.setPreferredSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        filaSecundaria.setMaximumSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        filaSecundaria.add(cargar);
        filaSecundaria.add(Box.createHorizontalStrut(10));
        filaSecundaria.add(ayuda);

        JButton salir = new JButton("Salir del juego") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = new Color(185, 49, 49);

                if (getModel().isPressed()) {
                    color = new Color(135, 31, 31);
                } else if (getModel().isRollover()) {
                    color = new Color(205, 58, 58);
                }

                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        salir.setFont(TemaMars.textoNormal());
        salir.setForeground(Color.WHITE);
        salir.setOpaque(false);
        salir.setContentAreaFilled(false);
        salir.setFocusPainted(false);
        salir.setBorderPainted(false);
        salir.setPreferredSize(new Dimension(195, TemaMars.ALTURA_BOTON));
        salir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        salir.addActionListener(e -> accionSalir.run());

        JPanel filaSalir = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaSalir.setOpaque(false);
        filaSalir.setAlignmentX(LEFT_ALIGNMENT);
        filaSalir.setPreferredSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        filaSalir.setMaximumSize(new Dimension(320, TemaMars.ALTURA_BOTON));
        filaSalir.add(salir);

        bloque.add(modo);
        bloque.add(Box.createVerticalStrut(22));
        bloque.add(tituloMars);
        bloque.add(tituloColony);
        bloque.add(Box.createVerticalStrut(24));
        bloque.add(botonContinuar);
        bloque.add(Box.createVerticalStrut(5));
        bloque.add(etiquetaPartida);
        bloque.add(Box.createVerticalStrut(15));
        bloque.add(nueva);
        bloque.add(Box.createVerticalStrut(10));
        bloque.add(filaSecundaria);
        bloque.add(Box.createVerticalStrut(14));
        bloque.add(filaSalir);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 36, 10, 10);

        add(bloque, gbc);

        actualizar();
    }

    public final void actualizar() {
        boolean real = controlador.hayPartidaReal();

        botonContinuar.setVisible(real);
        etiquetaPartida.setVisible(real);

        if (real) {
            Partida partida = controlador.consultarEstado();
            etiquetaPartida.setText(partida.getNombreComandante() + " · Misión " + String.format("%02d", partida.getMisionActual()));
        } else {
            etiquetaPartida.setText("");
        }

        revalidate();
        repaint();
    }
}