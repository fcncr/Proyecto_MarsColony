package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DialogoMenuJuego extends JDialog {

    public enum Contexto {
        PREPARACION,
        BATALLA
    }

    private DialogoMenuJuego(Window propietario, Contexto contexto, boolean persistenciaDisponible, Runnable accionGuardar, Runnable accionInicio, Runnable accionSalir) {
        super(propietario, "Menú", Dialog.ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 535);
        setMinimumSize(new Dimension(480, 500));
        setResizable(false);
        setLocationRelativeTo(propietario);

        JPanel fondo = new JPanel();
        fondo.setBackground(TemaMars.FONDO);
        fondo.setLayout(new BoxLayout(fondo, BoxLayout.Y_AXIS));
        fondo.setBorder(TemaMars.padding(22));

        PanelMars tarjeta = new PanelMars();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("MENÚ DE MISIÓN");
        titulo.setFont(TemaMars.tituloPantalla());
        titulo.setForeground(TemaMars.TEXTO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                contexto == Contexto.BATALLA
                        ? "La batalla continúa mientras este menú está abierto."
                        : "Gestiona tu expedición."
        );

        subtitulo.setFont(TemaMars.textoSecundario());
        subtitulo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        BotonMars continuar = BotonMars.primario("CONTINUAR");
        configurarBoton(continuar);
        continuar.addActionListener(e -> dispose());

        BotonMars guardar;

        if (contexto == Contexto.PREPARACION && persistenciaDisponible) {
            guardar = BotonMars.secundario("Guardar partida");
            guardar.addActionListener(e -> {
                if (accionGuardar != null) {
                    accionGuardar.run();
                }
            });
        } else {
            guardar = BotonMars.bloqueado("Guardar partida");

            if (contexto == Contexto.BATALLA) {
                guardar.setToolTipText(
                        "No se puede guardar una batalla en curso."
                );
            } else {
                guardar.setToolTipText(
                        "La persistencia no está disponible."
                );
            }
        }

        configurarBoton(guardar);

        BotonMars inicio;

        if (contexto == Contexto.PREPARACION) {
            inicio = BotonMars.secundario("Volver al inicio");

            inicio.addActionListener(e -> {
                dispose();

                if (accionInicio != null) {
                    accionInicio.run();
                }
            });
        } else {
            inicio = BotonMars.bloqueado("Volver al inicio");
            inicio.setToolTipText(
                    "Termina la batalla antes de volver al inicio."
            );
        }

        configurarBoton(inicio);

        JButton salir = crearBotonSalir();

        salir.addActionListener(e -> {
            dispose();

            if (accionSalir != null) {
                accionSalir.run();
            }
        });

        JLabel aviso = new JLabel();

        if (contexto == Contexto.BATALLA) {
            aviso.setText(
                    "<html><div style='text-align:center;width:390px'>"
                    + "Guardar y volver al inicio estarán disponibles cuando termine la batalla."
                    + "</div></html>"
            );

            aviso.setForeground(TemaMars.ERROR);
        } else if (!persistenciaDisponible) {
            aviso.setText(
                    "El sistema de guardado no está disponible."
            );

            aviso.setForeground(TemaMars.ERROR);
        } else {
            aviso.setText(
                    ""
            );

            aviso.setForeground(TemaMars.TEXTO_SECUNDARIO);
        }

        aviso.setFont(TemaMars.textoSecundario());
        aviso.setAlignmentX(CENTER_ALIGNMENT);
        aviso.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(continuar);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(guardar);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(inicio);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(salir);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(aviso);

        fondo.add(tarjeta);

        setContentPane(fondo);

        getRootPane().setDefaultButton(continuar);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void configurarBoton(JButton boton) {
        boton.setAlignmentX(CENTER_ALIGNMENT);
        boton.setPreferredSize(
                new Dimension(410, TemaMars.ALTURA_BOTON)
        );
        boton.setMinimumSize(
                new Dimension(410, TemaMars.ALTURA_BOTON)
        );
        boton.setMaximumSize(
                new Dimension(410, TemaMars.ALTURA_BOTON)
        );
    }

    private JButton crearBotonSalir() {
        JButton salir = new JButton("Salir del juego") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color color = new Color(185, 49, 49);

                if (getModel().isPressed()) {
                    color = new Color(135, 31, 31);
                } else if (getModel().isRollover()) {
                    color = new Color(205, 58, 58);
                }

                g2.setColor(color);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        14,
                        14
                );

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
        salir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        configurarBoton(salir);

        return salir;
    }

    public static void mostrar(Component padre, Contexto contexto, boolean persistenciaDisponible, Runnable accionGuardar, Runnable accionInicio, Runnable accionSalir) {
        Window ventana = padre == null
                ? null
                : SwingUtilities.getWindowAncestor(padre);

        DialogoMenuJuego dialogo = new DialogoMenuJuego(
                ventana,
                contexto,
                persistenciaDisponible,
                accionGuardar,
                accionInicio,
                accionSalir
        );

        dialogo.setVisible(true);
    }
}