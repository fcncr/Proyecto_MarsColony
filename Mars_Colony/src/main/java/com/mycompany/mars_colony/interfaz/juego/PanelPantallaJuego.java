package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.EncabezadoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelFondoMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PanelPantallaJuego extends JPanel {

    private final EncabezadoMars encabezado;
    private final PanelFondoMars fondo;

    public PanelPantallaJuego(String titulo, String subtitulo, String contexto) {
        setLayout(new BorderLayout());
        setBackground(TemaMars.FONDO_PROFUNDO);

        encabezado = new EncabezadoMars(titulo, subtitulo, contexto);

        fondo = new PanelFondoMars("assets/FondoMars.png");
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(TemaMars.padding(TemaMars.MARGEN_PANTALLA));

        add(encabezado, BorderLayout.NORTH);
        add(fondo, BorderLayout.CENTER);
    }

    public void setContenido(JComponent contenido) {
        fondo.removeAll();

        if (contenido != null) {
            fondo.add(contenido, BorderLayout.CENTER);
        }

        fondo.revalidate();
        fondo.repaint();
    }

    public void setContexto(String contexto) {
        encabezado.setContextoDerecha(contexto);
    }

    public EncabezadoMars getEncabezado() {
        return encabezado;
    }

    public PanelFondoMars getFondo() {
        return fondo;
    }
}