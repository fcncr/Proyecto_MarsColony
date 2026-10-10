package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class EncabezadoMars extends JPanel {

    private final JLabel etiquetaTitulo;
    private final JLabel etiquetaSubtitulo;
    private final EtiquetaEstadoMars etiquetaContexto;

    public EncabezadoMars(String titulo, String subtitulo, String contextoDerecha) {
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));
        setBorder(TemaMars.padding(10, 24, 12, 24));
        setPreferredSize(new Dimension(100, 82));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        etiquetaTitulo = new JLabel(titulo == null ? "" : titulo);
        etiquetaTitulo.setForeground(TemaMars.TEXTO_CLARO);
        etiquetaTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        etiquetaSubtitulo = new JLabel(subtitulo == null ? "" : subtitulo);
        etiquetaSubtitulo.setForeground(new Color(0xC9, 0xD6, 0xD9));
        etiquetaSubtitulo.setFont(TemaMars.textoSecundario());

        textos.add(Box.createVerticalGlue());
        textos.add(etiquetaTitulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(etiquetaSubtitulo);
        textos.add(Box.createVerticalGlue());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 13));
        derecha.setOpaque(false);

        etiquetaContexto = EtiquetaEstadoMars.energia(
                contextoDerecha == null ? "" : contextoDerecha
        );

        etiquetaContexto.setFont(
                TemaMars.textoSecundario().deriveFont(Font.BOLD, 15f)
        );

        etiquetaContexto.setPreferredSize(new Dimension(360, 36));
        etiquetaContexto.setMinimumSize(new Dimension(360, 36));
        etiquetaContexto.setMaximumSize(new Dimension(360, 36));

        etiquetaContexto.setHorizontalAlignment(SwingConstants.CENTER);

        etiquetaContexto.setVisible(
                contextoDerecha != null
                && !contextoDerecha.isBlank()
        );

        derecha.add(etiquetaContexto);

        add(textos, BorderLayout.CENTER);
        add(derecha, BorderLayout.EAST);
    }

    public void setTitulo(String titulo) {
        etiquetaTitulo.setText(
                titulo == null ? "" : titulo
        );
    }

    public void setSubtitulo(String subtitulo) {
        etiquetaSubtitulo.setText(
                subtitulo == null ? "" : subtitulo
        );
    }

    public void setContextoDerecha(String contexto) {
        etiquetaContexto.setText(
                contexto == null ? "" : contexto
        );

        etiquetaContexto.setVisible(
                contexto != null
                && !contexto.isBlank()
        );

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(TemaMars.FONDO);
        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        g2.setColor(TemaMars.ACCION);
        g2.fillRect(
                0,
                getHeight() - 3,
                getWidth(),
                3
        );

        g2.dispose();

        super.paintComponent(g);
    }
}