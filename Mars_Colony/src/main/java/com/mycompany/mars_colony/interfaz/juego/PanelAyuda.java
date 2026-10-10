package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TarjetaMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelAyuda extends PanelPantallaJuego {

    private final Runnable accionVolver;

    public PanelAyuda(Runnable accionVolver) {
        super("CÓMO JUGAR", "Tres pasos para proteger la colonia", "");

        this.accionVolver = accionVolver;

        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setOpaque(false);

        JPanel pasos = new JPanel(new GridLayout(1, 3, 16, 0));
        pasos.setOpaque(false);

        pasos.add(crearTarjeta("1. Construye", "Elige una defensa en el Arsenal y colócala en una casilla libre.", "assets/DEFENSAS/Alcance medio/NT1.png"));
        pasos.add(crearTarjeta("2. Protege", "Cuida el núcleo de oxígeno y administra la capacidad disponible.", "assets/importados/Nucleo.png"));
        pasos.add(crearTarjeta("3. Observa", "Inicia la batalla. Las unidades se mueven y atacan automáticamente.", "assets/CRIATURAS/acechador/N1.png"));

        PanelMars controles = new PanelMars();
        controles.setLayout(new GridLayout(2, 3, 14, 8));

        JLabel titulo = new JLabel("CONTROLES");
        titulo.setFont(TemaMars.tituloPanel());
        titulo.setForeground(TemaMars.TEXTO);

        controles.add(titulo);
        controles.add(new JLabel(""));
        controles.add(new JLabel(""));
        controles.add(crearControl("Clic", "seleccionar / colocar"));
        controles.add(crearControl("Tab + Enter", "navegar por botones"));
        controles.add(crearControl("Esc", "cancelar / volver"));

        BotonMars volver = BotonMars.secundario("‹ Volver");
        volver.setPreferredSize(new Dimension(160, TemaMars.ALTURA_BOTON));
        volver.addActionListener(e -> this.accionVolver.run());

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(volver, BorderLayout.WEST);

        raiz.add(pasos, BorderLayout.CENTER);
        raiz.add(controles, BorderLayout.SOUTH);

        JPanel exterior = new JPanel(new BorderLayout(0, 12));
        exterior.setOpaque(false);
        exterior.add(raiz, BorderLayout.CENTER);
        exterior.add(pie, BorderLayout.SOUTH);

        setContenido(exterior);
    }

    private TarjetaMars crearTarjeta(String titulo, String descripcion, String imagen) {
        TarjetaMars tarjeta = new TarjetaMars();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel icono = new JLabel(GestorImagenes.cargar(imagen, 180, 180));
        icono.setAlignmentX(CENTER_ALIGNMENT);
        icono.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(TemaMars.tituloPanel());
        etiquetaTitulo.setForeground(TemaMars.TEXTO);
        etiquetaTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel texto = new JLabel("<html><div style='text-align:center;width:250px'>" + descripcion + "</div></html>");
        texto.setFont(TemaMars.textoNormal());
        texto.setForeground(TemaMars.TEXTO);
        texto.setAlignmentX(CENTER_ALIGNMENT);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(icono);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(etiquetaTitulo);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(texto);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel crearControl(String tecla, String accion) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiquetaTecla = new JLabel(tecla);
        etiquetaTecla.setFont(TemaMars.textoDestacado());
        etiquetaTecla.setForeground(TemaMars.TEXTO);

        JLabel etiquetaAccion = new JLabel(accion);
        etiquetaAccion.setFont(TemaMars.textoSecundario());
        etiquetaAccion.setForeground(TemaMars.TEXTO_SECUNDARIO);

        panel.add(etiquetaTecla);
        panel.add(Box.createVerticalStrut(3));
        panel.add(etiquetaAccion);

        return panel;
    }
}