package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelControlesZoomMapa extends JPanel {

    private final PanelMapaMars mapa;
    private final JLabel etiquetaZoom;

    public PanelControlesZoomMapa(PanelMapaMars mapa) {
        if (mapa == null) {
            throw new IllegalArgumentException(
                    "El mapa no puede ser nulo."
            );
        }

        this.mapa = mapa;

        setOpaque(false);

        setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        2
                )
        );

        JLabel ayuda = new JLabel(
                ""
        );

        ayuda.setFont(
                TemaMars.textoSecundario()
        );

        ayuda.setForeground(
                TemaMars.TEXTO_CLARO
        );

        JButton menos = crearBotonSimbolo(
                "-",
                48
        );

        JButton mas = crearBotonSimbolo(
                "+",
                48
        );

        etiquetaZoom = new JLabel(
                mapa.getZoomPorcentaje() + "%",
                JLabel.CENTER
        );

        etiquetaZoom.setFont(
                TemaMars.textoDestacado()
        );

        etiquetaZoom.setForeground(
                TemaMars.TEXTO_CLARO
        );

        etiquetaZoom.setPreferredSize(
                new Dimension(
                        68,
                        38
                )
        );

        JButton ajustar = crearBotonTexto(
                "Ajustar",
                96
        );

        menos.setToolTipText(
                "Alejar mapa"
        );

        mas.setToolTipText(
                "Acercar mapa"
        );

        ajustar.setToolTipText(
                "Mostrar la matriz completa"
        );

        menos.addActionListener(
                e -> mapa.disminuirZoom()
        );

        mas.addActionListener(
                e -> mapa.aumentarZoom()
        );

        ajustar.addActionListener(
                e -> mapa.ajustarMapa()
        );

        mapa.addPropertyChangeListener(
                "zoom",
                e -> actualizar()
        );

        add(ayuda);
        add(menos);
        add(etiquetaZoom);
        add(mas);
        add(ajustar);
    }

    private JButton crearBotonSimbolo(String simbolo, int ancho) {
        JButton boton = new JButton() {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color fondo = TemaMars.FONDO;

                if (getModel().isPressed()) {
                    fondo = new Color(
                            8,
                            26,
                            36
                    );
                } else if (getModel().isRollover()) {
                    fondo = new Color(
                            31,
                            65,
                            79
                    );
                }

                g2.setColor(fondo);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        12,
                        12
                );

                Font fuente = new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        25
                );

                g2.setFont(fuente);
                g2.setColor(TemaMars.TEXTO_CLARO);

                FontMetrics metricas = g2.getFontMetrics();

                int x = (
                        getWidth()
                        - metricas.stringWidth(simbolo)
                ) / 2;

                int y = (
                        getHeight()
                        - metricas.getHeight()
                ) / 2
                        + metricas.getAscent()
                        - 1;

                g2.drawString(
                        simbolo,
                        x,
                        y
                );

                g2.dispose();
            }
        };

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);

        boton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        ancho,
                        38
                )
        );

        boton.setMinimumSize(
                new Dimension(
                        ancho,
                        38
                )
        );

        boton.setMaximumSize(
                new Dimension(
                        ancho,
                        38
                )
        );

        return boton;
    }

    private JButton crearBotonTexto(String texto, int ancho) {
        JButton boton = new JButton(texto) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color fondo = TemaMars.FONDO;

                if (getModel().isPressed()) {
                    fondo = new Color(
                            8,
                            26,
                            36
                    );
                } else if (getModel().isRollover()) {
                    fondo = new Color(
                            31,
                            65,
                            79
                    );
                }

                g2.setColor(fondo);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        12,
                        12
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        boton.setFont(
                TemaMars.textoDestacado()
        );

        boton.setForeground(
                TemaMars.TEXTO_CLARO
        );

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);

        boton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        ancho,
                        38
                )
        );

        return boton;
    }

    private void actualizar() {
        etiquetaZoom.setText(
                mapa.getZoomPorcentaje()
                + "%"
        );
    }
}