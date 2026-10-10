package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelResultado extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionContinuarVictoria;
    private final Runnable accionRepetir;
    private final Runnable accionInforme;
    private final Runnable accionGuardarInicio;

    private final JLabel etiquetaResultado;
    private final JLabel imagenNucleo;
    private final JLabel etiquetaMensaje;
    private final JLabel etiquetaVida;
    private final JLabel etiquetaCapacidad;

    private final BotonMars botonPrincipal;
    private final BotonMars botonInforme;
    private final BotonMars botonSecundario;
    private final BotonMars botonGuardarInicio;

    private boolean victoria;

    public PanelResultado(ControladorJuego controlador, Runnable accionContinuarVictoria, Runnable accionRepetir, Runnable accionInforme, Runnable accionGuardarInicio) {
        super("RESULTADO DE LA MISIÓN", "", "");

        this.controlador = controlador;
        this.accionContinuarVictoria = accionContinuarVictoria;
        this.accionRepetir = accionRepetir;
        this.accionInforme = accionInforme;
        this.accionGuardarInicio = accionGuardarInicio;

        JPanel escenario = new JPanel(new GridBagLayout());
        escenario.setOpaque(false);

        PanelMars tarjeta = new PanelMars();
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setPreferredSize(new Dimension(760, 545));
        tarjeta.setMinimumSize(new Dimension(650, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        etiquetaResultado = new JLabel("¡VICTORIA!", SwingConstants.CENTER);
        etiquetaResultado.setFont(TemaMars.tituloGrande().deriveFont(36f));
        etiquetaResultado.setForeground(TemaMars.ACCION);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 4, 20);
        tarjeta.add(etiquetaResultado, gbc);

        imagenNucleo = new JLabel(
                GestorImagenes.cargar(
                        "assets/importados/Nucleo.png",
                        125,
                        125
                ),
                SwingConstants.CENTER
        );

        imagenNucleo.setPreferredSize(new Dimension(150, 130));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 3, 20);
        tarjeta.add(imagenNucleo, gbc);

        etiquetaMensaje = new JLabel(
                "La colonia resiste.",
                SwingConstants.CENTER
        );

        etiquetaMensaje.setFont(TemaMars.tituloPanel());
        etiquetaMensaje.setForeground(TemaMars.TEXTO);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 14, 20);
        tarjeta.add(etiquetaMensaje, gbc);

        JPanel datos = new JPanel(new GridLayout(1, 2, 16, 0));
        datos.setOpaque(false);
        datos.setPreferredSize(new Dimension(620, 82));

        etiquetaVida = new JLabel("-");
        etiquetaCapacidad = new JLabel("-");

        datos.add(crearDato("VIDA DEL NÚCLEO", etiquetaVida));
        datos.add(crearDato("CAPACIDAD", etiquetaCapacidad));

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 28, 10, 28);
        tarjeta.add(datos, gbc);

        botonPrincipal = BotonMars.primario("SIGUIENTE MISIÓN");
        configurarBotonAncho(botonPrincipal);

        botonPrincipal.addActionListener(e -> {
            if (victoria) {
                accionContinuarVictoria.run();
            } else {
                accionRepetir.run();
            }
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 28, 8, 28);
        tarjeta.add(botonPrincipal, gbc);

        JPanel accionesSecundarias = new JPanel(
                new GridLayout(1, 2, 16, 0)
        );

        accionesSecundarias.setOpaque(false);
        accionesSecundarias.setPreferredSize(
                new Dimension(620, TemaMars.ALTURA_BOTON)
        );

        botonInforme = BotonMars.secundario("Ver informe");
        botonInforme.addActionListener(e -> accionInforme.run());

        botonSecundario = BotonMars.secundario("Repetir misión");
        botonSecundario.addActionListener(e -> {
            if (victoria) {
                accionRepetir.run();
            }
        });

        accionesSecundarias.add(botonInforme);
        accionesSecundarias.add(botonSecundario);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 28, 8, 28);
        tarjeta.add(accionesSecundarias, gbc);

        botonGuardarInicio = BotonMars.secundario(
                "Guardar y volver al inicio"
        );

        configurarBotonAncho(botonGuardarInicio);

        botonGuardarInicio.addActionListener(
                e -> accionGuardarInicio.run()
        );

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 28, 16, 28);
        tarjeta.add(botonGuardarInicio, gbc);

        GridBagConstraints gbcEscenario = new GridBagConstraints();
        gbcEscenario.gridx = 0;
        gbcEscenario.gridy = 0;
        gbcEscenario.weightx = 1;
        gbcEscenario.weighty = 1;
        gbcEscenario.anchor = GridBagConstraints.CENTER;
        gbcEscenario.insets = new Insets(15, 15, 15, 15);

        escenario.add(tarjeta, gbcEscenario);

        setContenido(escenario);
    }

    private PanelMars crearDato(String titulo, JLabel valor) {
        PanelMars panel = new PanelMars(TemaMars.PANEL_SECUNDARIO);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(TemaMars.TEXTO_SECUNDARIO);

        gbc.gridy = 0;
        gbc.insets = new Insets(2, 0, 3, 0);
        panel.add(etiqueta, gbc);

        valor.setFont(TemaMars.tituloPanel());
        valor.setForeground(TemaMars.TEXTO);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(valor, gbc);

        return panel;
    }

    private void configurarBotonAncho(BotonMars boton) {
        boton.setPreferredSize(
                new Dimension(620, TemaMars.ALTURA_BOTON)
        );

        boton.setMinimumSize(
                new Dimension(300, TemaMars.ALTURA_BOTON)
        );

        boton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, TemaMars.ALTURA_BOTON)
        );
    }

    public void preparar(int capacidadAntes, int capacidadDespues) {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        Mision mision = partida.getMision();

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
        );

        victoria = mision.getEstado() == EstadoMision.VICTORIA;

        NucleoOxigeno nucleo = buscarNucleo(mision);

        if (victoria) {
            etiquetaResultado.setText("¡VICTORIA!");
            etiquetaResultado.setForeground(TemaMars.ACCION);

            etiquetaMensaje.setText("La colonia resiste.");

            if (controlador.puedeFinalizarCampania()) {
                botonPrincipal.setText("CONTINUAR");
            } else {
                botonPrincipal.setText("SIGUIENTE MISIÓN");
            }

            botonPrincipal.setEnabled(true);

            botonSecundario.setText("Repetir misión");
            botonSecundario.setEnabled(
                    controlador.puedeRepetirMision()
            );

            botonSecundario.setToolTipText(null);

            if (capacidadDespues > capacidadAntes) {
                etiquetaCapacidad.setText(
                        capacidadAntes
                        + " → "
                        + capacidadDespues
                );
            } else {
                etiquetaCapacidad.setText(
                        capacidadDespues
                        + " · sin nuevo premio"
                );
            }
        } else {
            etiquetaResultado.setText("NÚCLEO DESTRUIDO");
            etiquetaResultado.setForeground(TemaMars.ERROR);

            etiquetaMensaje.setText(
                    "La colonia te necesita."
            );

            botonPrincipal.setText("VOLVER A INTENTAR");
            botonPrincipal.setEnabled(
                    controlador.puedeRepetirMision()
            );

            botonSecundario.setText("Avanzar");
            botonSecundario.setEnabled(false);
            botonSecundario.setToolTipText(
                    "La misión debe superarse antes de avanzar."
            );

            etiquetaCapacidad.setText(
                    partida.getEscuadron().getCapacidadTotal()
                    + " · sin premio"
            );
        }

        if (nucleo == null) {
            etiquetaVida.setText("-");
        } else {
            etiquetaVida.setText(
                    formatear(nucleo.getVidaActual())
                    + " / "
                    + formatear(nucleo.getVidaMaxima())
            );
        }

        revalidate();
        repaint();
    }

    private NucleoOxigeno buscarNucleo(Mision mision) {
        for (ComponenteCombate componente : mision.getParticipantes()) {
            if (componente instanceof NucleoOxigeno nucleo) {
                return nucleo;
            }
        }

        return null;
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##").format(valor);
    }
}