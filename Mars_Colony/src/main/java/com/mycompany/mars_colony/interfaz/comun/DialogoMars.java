package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public final class DialogoMars {

    private DialogoMars() {
    }

    public static void informacion(Component propietario, String titulo, String mensaje) {
        mostrar(
                propietario,
                titulo,
                mensaje,
                Tipo.INFORMACION,
                false,
                null
        );
    }

    public static void exito(Component propietario, String titulo, String mensaje) {
        mostrar(
                propietario,
                titulo,
                mensaje,
                Tipo.EXITO,
                false,
                null
        );
    }

    public static void error(Component propietario, String titulo, String mensaje) {
        mostrar(
                propietario,
                titulo,
                mensaje,
                Tipo.ERROR,
                false,
                null
        );
    }

    public static boolean confirmar(Component propietario, String titulo, String mensaje, String textoConfirmar) {
        boolean[] resultado = {false};

        mostrar(
                propietario,
                titulo,
                mensaje,
                Tipo.CONFIRMACION,
                true,
                new Confirmacion(
                        resultado,
                        textoConfirmar
                )
        );

        return resultado[0];
    }

    private static void mostrar(Component propietario, String titulo, String mensaje, Tipo tipo, boolean confirmacion, Confirmacion estadoConfirmacion) {
        Window ventana;

        if (propietario instanceof Window window) {
            ventana = window;
        } else if (propietario == null) {
            ventana = null;
        } else {
            ventana = SwingUtilities.getWindowAncestor(propietario);
        }

        JDialog dialogo = new JDialog(
                ventana,
                titulo == null ? "" : titulo,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialogo.setResizable(false);

        JPanel raiz = new JPanel(
                new BorderLayout(0, 0)
        );

        raiz.setBackground(
                TemaMars.FONDO
        );

        raiz.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        12,
                        16,
                        12
                )
        );

        JPanel encabezado = new JPanel(
                new BorderLayout()
        );

        encabezado.setBackground(
                TemaMars.FONDO
        );

        encabezado.setBorder(
                TemaMars.padding(
                        12,
                        16,
                        10,
                        16
                )
        );

        JLabel etiquetaTitulo = new JLabel(
                titulo == null ? "" : titulo
        );

        etiquetaTitulo.setFont(
                TemaMars.tituloPanel()
        );

        etiquetaTitulo.setForeground(
                colorTipo(tipo)
        );

        encabezado.add(
                etiquetaTitulo,
                BorderLayout.WEST
        );

        PanelMars cuerpo = new PanelMars();

        cuerpo.setLayout(
                new BorderLayout(0, 20)
        );

        cuerpo.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        18,
                        18
                )
        );

        JTextArea etiquetaMensaje = new JTextArea(
                mensaje == null ? "" : mensaje
        );

        etiquetaMensaje.setFont(
                TemaMars.textoNormal()
        );

        etiquetaMensaje.setForeground(
                TemaMars.TEXTO
        );

        etiquetaMensaje.setEditable(false);
        etiquetaMensaje.setFocusable(false);

        etiquetaMensaje.setLineWrap(true);
        etiquetaMensaje.setWrapStyleWord(true);

        etiquetaMensaje.setOpaque(false);

        etiquetaMensaje.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        6,
                        0
                )
        );

        etiquetaMensaje.setPreferredSize(
                new Dimension(
                        400,
                        82
                )
        );

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        0
                )
        );

        acciones.setOpaque(false);

        acciones.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        6,
                        0
                )
        );

        if (confirmacion) {
            BotonMars cancelar = BotonMars.secundario(
                    "Cancelar"
            );

            BotonMars aceptar;

            if (tipo == Tipo.ERROR) {
                aceptar = BotonMars.peligro(
                        estadoConfirmacion.textoConfirmar
                );
            } else {
                aceptar = BotonMars.primario(
                        estadoConfirmacion.textoConfirmar
                );
            }

            configurarBoton(cancelar);
            configurarBoton(aceptar);

            cancelar.addActionListener(
                    e -> dialogo.dispose()
            );

            aceptar.addActionListener(e -> {
                estadoConfirmacion.resultado[0] = true;
                dialogo.dispose();
            });

            acciones.add(cancelar);
            acciones.add(aceptar);

            dialogo.getRootPane().setDefaultButton(
                    aceptar
            );
        } else {
            BotonMars aceptar = BotonMars.primario(
                    "Aceptar"
            );

            configurarBoton(aceptar);

            aceptar.addActionListener(
                    e -> dialogo.dispose()
            );

            acciones.add(aceptar);

            dialogo.getRootPane().setDefaultButton(
                    aceptar
            );
        }

        cuerpo.add(
                etiquetaMensaje,
                BorderLayout.CENTER
        );

        cuerpo.add(
                acciones,
                BorderLayout.SOUTH
        );

        raiz.add(
                encabezado,
                BorderLayout.NORTH
        );

        raiz.add(
                cuerpo,
                BorderLayout.CENTER
        );

        dialogo.setContentPane(
                raiz
        );

        dialogo.pack();

        int ancho = Math.max(
                520,
                dialogo.getWidth()
        );

        int alto = Math.max(
                320,
                dialogo.getHeight()
        );

        dialogo.setSize(
                ancho,
                alto
        );

        dialogo.setLocationRelativeTo(
                propietario
        );

        dialogo.setVisible(true);
    }

    private static void configurarBoton(BotonMars boton) {
        boton.setPreferredSize(
                new Dimension(
                        180,
                        TemaMars.ALTURA_BOTON
                )
        );

        boton.setMinimumSize(
                new Dimension(
                        180,
                        TemaMars.ALTURA_BOTON
                )
        );

        boton.setMaximumSize(
                new Dimension(
                        180,
                        TemaMars.ALTURA_BOTON
                )
        );
    }

    private static Color colorTipo(Tipo tipo) {
        return switch (tipo) {
            case EXITO -> TemaMars.CORRECTO;
            case ERROR -> TemaMars.ERROR;
            case CONFIRMACION -> TemaMars.ACCION;
            case INFORMACION -> TemaMars.ENERGIA;
        };
    }

    private enum Tipo {
        INFORMACION,
        EXITO,
        ERROR,
        CONFIRMACION
    }

    private static class Confirmacion {

        private final boolean[] resultado;
        private final String textoConfirmar;

        private Confirmacion(boolean[] resultado, String textoConfirmar) {
            this.resultado = resultado;

            this.textoConfirmar =
                    textoConfirmar == null
                    || textoConfirmar.isBlank()
                            ? "Confirmar"
                            : textoConfirmar;
        }
    }
}