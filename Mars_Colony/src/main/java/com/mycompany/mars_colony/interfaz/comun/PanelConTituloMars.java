package com.mycompany.mars_colony.interfaz.comun;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelConTituloMars extends PanelMars {

    private final JLabel etiquetaTitulo;
    private final JLabel etiquetaSubtitulo;
    private final JPanel contenido;

    public PanelConTituloMars(String titulo) {
        this(titulo, null);
    }

    public PanelConTituloMars(String titulo, String subtitulo) {
        super();
        setLayout(new BorderLayout(0, 16));

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));

        etiquetaTitulo = new JLabel(titulo == null ? "" : titulo);
        etiquetaTitulo.setFont(TemaMars.tituloPanel());
        etiquetaTitulo.setForeground(TemaMars.TEXTO);

        etiquetaSubtitulo = new JLabel(subtitulo == null ? "" : subtitulo);
        etiquetaSubtitulo.setFont(TemaMars.textoSecundario());
        etiquetaSubtitulo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        etiquetaSubtitulo.setVisible(subtitulo != null && !subtitulo.isBlank());

        cabecera.add(etiquetaTitulo);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(etiquetaSubtitulo);

        contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);

        add(cabecera, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    public JPanel getContenido() {
        return contenido;
    }

    public void setTitulo(String titulo) {
        etiquetaTitulo.setText(titulo == null ? "" : titulo);
    }

    public void setSubtitulo(String subtitulo) {
        etiquetaSubtitulo.setText(subtitulo == null ? "" : subtitulo);
        etiquetaSubtitulo.setVisible(subtitulo != null && !subtitulo.isBlank());
    }

    public void setColorTitulo(Color color) {
        etiquetaTitulo.setForeground(color == null ? TemaMars.TEXTO : color);
    }
}