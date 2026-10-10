package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelNuevaExpedicion extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionCancelar;
    private final Runnable accionCreada;
    private final JTextField campoNombre;
    private final JLabel etiquetaValidacion;
    private final BotonMars botonCrear;

    public PanelNuevaExpedicion(ControladorJuego controlador, Runnable accionCancelar, Runnable accionCreada) {
        super("NUEVA EXPEDICIÓN", "Tu colonia comienza con un comandante", "");

        this.controlador = controlador;
        this.accionCancelar = accionCancelar;
        this.accionCreada = accionCreada;

        JPanel escenario = new JPanel(new GridBagLayout());
        escenario.setOpaque(false);

        PanelMars tarjeta = new PanelMars();
        tarjeta.setPreferredSize(new Dimension(760, 520));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel pregunta = new JLabel("¿Cómo te llamas, comandante?");
        pregunta.setFont(TemaMars.tituloPantalla());
        pregunta.setForeground(TemaMars.TEXTO);
        pregunta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nombre = new JLabel("Nombre único");
        nombre.setFont(TemaMars.textoSecundario());
        nombre.setForeground(TemaMars.TEXTO_SECUNDARIO);
        nombre.setAlignmentX(LEFT_ALIGNMENT);

        campoNombre = new JTextField();
        campoNombre.setFont(TemaMars.textoNormal());
        campoNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        campoNombre.setPreferredSize(new Dimension(100, 48));
        campoNombre.setAlignmentX(LEFT_ALIGNMENT);

        etiquetaValidacion = new JLabel(" ");
        etiquetaValidacion.setFont(TemaMars.textoSecundario());
        etiquetaValidacion.setAlignmentX(LEFT_ALIGNMENT);

        JPanel datos = new JPanel(new GridLayout(1, 2, 16, 0));
        datos.setOpaque(false);
        datos.setAlignmentX(LEFT_ALIGNMENT);
        datos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        datos.add(crearDato("CAPACIDAD INICIAL", "20 puntos"));
        datos.add(crearDato("PRIMERA CAMPAÑA", "10 misiones"));

        JLabel explicacion = new JLabel("<html>Cada misión superada por primera vez suma <b>5 puntos</b> a tu capacidad.</html>");
        explicacion.setFont(TemaMars.textoNormal());
        explicacion.setForeground(TemaMars.TEXTO);
        explicacion.setAlignmentX(LEFT_ALIGNMENT);

        JPanel acciones = new JPanel(new GridLayout(1, 2, 16, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(LEFT_ALIGNMENT);
        acciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, TemaMars.ALTURA_BOTON));

        BotonMars cancelar = BotonMars.secundario("Cancelar");
        cancelar.addActionListener(e -> accionCancelar.run());

        botonCrear = BotonMars.primario("CREAR EXPEDICIÓN");
        botonCrear.addActionListener(e -> crearExpedicion());

        acciones.add(cancelar);
        acciones.add(botonCrear);

        tarjeta.add(pregunta);
        tarjeta.add(Box.createVerticalStrut(30));
        tarjeta.add(nombre);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(campoNombre);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(etiquetaValidacion);
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(datos);
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(explicacion);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(acciones);

        campoNombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarNombre();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarNombre();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarNombre();
            }
        });

        campoNombre.addActionListener(e -> {
            if (botonCrear.isEnabled()) {
                crearExpedicion();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(24, 24, 24, 24);

        escenario.add(tarjeta, gbc);

        setContenido(escenario);
        validarNombre();
    }

    private PanelMars crearDato(String titulo, String valor) {
        PanelMars panel = new PanelMars(TemaMars.PANEL_SECUNDARIO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(TemaMars.textoSecundario());
        etiquetaTitulo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        etiquetaTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel etiquetaValor = new JLabel(valor);
        etiquetaValor.setFont(TemaMars.tituloPanel());
        etiquetaValor.setForeground(TemaMars.TEXTO);
        etiquetaValor.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(etiquetaTitulo);
        panel.add(Box.createVerticalStrut(8));
        panel.add(etiquetaValor);

        return panel;
    }

    public void preparar() {
        campoNombre.setText("");
        setContexto("");
        validarNombre();
        campoNombre.requestFocusInWindow();
    }

    private void validarNombre() {
        String texto = campoNombre.getText() == null ? "" : campoNombre.getText().trim();

        if (texto.isEmpty()) {
            etiquetaValidacion.setText("Escribe un nombre para continuar.");
            etiquetaValidacion.setForeground(TemaMars.ERROR);
            botonCrear.setEnabled(false);
            return;
        }

        if ("Sin partida".equalsIgnoreCase(texto)) {
            etiquetaValidacion.setText("Ese nombre está reservado por el sistema.");
            etiquetaValidacion.setForeground(TemaMars.ERROR);
            botonCrear.setEnabled(false);
            return;
        }

        try {
            List<String> existentes = controlador.listarPartidasGuardadas();

            for (String existente : existentes) {
                if (existente.equalsIgnoreCase(texto)) {
                    etiquetaValidacion.setText("Ese comandante ya tiene una expedición.");
                    etiquetaValidacion.setForeground(TemaMars.ERROR);
                    botonCrear.setEnabled(false);
                    return;
                }
            }
        } catch (RuntimeException e) {
            etiquetaValidacion.setText("No se pudo comprobar el nombre.");
            etiquetaValidacion.setForeground(TemaMars.ERROR);
            botonCrear.setEnabled(false);
            return;
        }

        etiquetaValidacion.setText("✓ Nombre disponible");
        etiquetaValidacion.setForeground(TemaMars.CORRECTO);
        botonCrear.setEnabled(true);
    }

    private void crearExpedicion() {
        if (!botonCrear.isEnabled()) {
            return;
        }

        try {
            controlador.crearNuevaPartida(campoNombre.getText().trim());
            accionCreada.run();
        } catch (RuntimeException e) {
            DialogoMars.error(this, "No se pudo crear", obtenerMensaje(e));
            validarNombre();
        }
    }

    private String obtenerMensaje(Throwable error) {
        Throwable actual = error;

        while (actual.getCause() != null && actual.getCause() != actual) {
            actual = actual.getCause();
        }

        return actual.getMessage() == null || actual.getMessage().isBlank() ? "No fue posible crear la expedición." : actual.getMessage();
    }
}