package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelCampania extends PanelPantallaJuego {

    private static final int[] ORDEN_RUTA = {1, 2, 3, 4, 5, 10, 9, 8, 7, 6};

    private final ControladorJuego controlador;
    private final Runnable accionInicio;
    private final Runnable accionPreparar;
    private final PanelRutaCampania ruta;
    private final JLabel etiquetaNumero;
    private final JLabel etiquetaCapacidad;
    private final JLabel etiquetaEnemigo;

    public PanelCampania(ControladorJuego controlador, Runnable accionInicio, Runnable accionPreparar) {
        super("MAPA DE CAMPAÑA", "Tu siguiente objetivo siempre a la vista", "");

        this.controlador = controlador;
        this.accionInicio = accionInicio;
        this.accionPreparar = accionPreparar;

        JPanel principal = new JPanel(new BorderLayout(18, 0));
        principal.setOpaque(false);

        PanelMars panelRuta = new PanelMars(TemaMars.FONDO);
        panelRuta.setLayout(new BorderLayout(0, 12));

        JLabel titulo = new JLabel("RUTA DE LA COLONIA");
        titulo.setFont(TemaMars.tituloPanel());
        titulo.setForeground(TemaMars.TEXTO_CLARO);

        ruta = new PanelRutaCampania();

        panelRuta.add(titulo, BorderLayout.NORTH);
        panelRuta.add(ruta, BorderLayout.CENTER);

        PanelMars lateral = new PanelMars();
        lateral.setPreferredSize(new Dimension(380, 100));
        lateral.setMinimumSize(new Dimension(350, 100));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));

        JLabel siguiente = new JLabel("SIGUIENTE MISIÓN");
        siguiente.setFont(TemaMars.textoDestacado());
        siguiente.setForeground(TemaMars.TEXTO);
        siguiente.setAlignmentX(CENTER_ALIGNMENT);

        etiquetaNumero = new JLabel("01", SwingConstants.CENTER);
        etiquetaNumero.setFont(TemaMars.tituloGrande().deriveFont(58f));
        etiquetaNumero.setForeground(TemaMars.TEXTO);
        etiquetaNumero.setAlignmentX(CENTER_ALIGNMENT);

        JLabel mensaje = new JLabel("<html><div style='text-align:center;width:290px'>La colonia avanza.<br>Prepara el perímetro antes del ataque.</div></html>");
        mensaje.setFont(TemaMars.textoNormal());
        mensaje.setForeground(TemaMars.TEXTO);
        mensaje.setAlignmentX(CENTER_ALIGNMENT);

        PanelMars datos = new PanelMars(TemaMars.PANEL_SECUNDARIO);
        datos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

        JLabel capacidadTitulo = new JLabel("TU CAPACIDAD");
        capacidadTitulo.setFont(TemaMars.textoSecundario());
        capacidadTitulo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        capacidadTitulo.setAlignmentX(LEFT_ALIGNMENT);

        etiquetaCapacidad = new JLabel("-");
        etiquetaCapacidad.setFont(TemaMars.tituloPanel());
        etiquetaCapacidad.setForeground(TemaMars.TEXTO);
        etiquetaCapacidad.setAlignmentX(LEFT_ALIGNMENT);

        etiquetaEnemigo = new JLabel("-");
        etiquetaEnemigo.setFont(TemaMars.textoSecundario());
        etiquetaEnemigo.setForeground(TemaMars.TEXTO);
        etiquetaEnemigo.setAlignmentX(LEFT_ALIGNMENT);

        datos.add(capacidadTitulo);
        datos.add(Box.createVerticalStrut(5));
        datos.add(etiquetaCapacidad);
        datos.add(Box.createVerticalStrut(12));
        datos.add(etiquetaEnemigo);

        BotonMars preparar = BotonMars.primario("PREPARAR MISIÓN");
        preparar.setAlignmentX(CENTER_ALIGNMENT);
        preparar.setMaximumSize(new Dimension(Integer.MAX_VALUE, TemaMars.ALTURA_BOTON));
        preparar.addActionListener(e -> accionPreparar.run());

        lateral.add(siguiente);
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(etiquetaNumero);
        lateral.add(Box.createVerticalStrut(6));
        lateral.add(mensaje);
        lateral.add(Box.createVerticalStrut(14));
        lateral.add(datos);
        lateral.add(Box.createVerticalGlue());
        lateral.add(preparar);

        principal.add(panelRuta, BorderLayout.CENTER);
        principal.add(lateral, BorderLayout.EAST);

        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setOpaque(false);
        raiz.add(principal, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        BotonMars inicio = BotonMars.secundario("‹ Inicio");
        inicio.setPreferredSize(new Dimension(150, TemaMars.ALTURA_BOTON));
        inicio.addActionListener(e -> accionInicio.run());

        BotonMars guardar = BotonMars.secundario("Guardar");
        guardar.setPreferredSize(new Dimension(150, TemaMars.ALTURA_BOTON));
        guardar.addActionListener(e -> guardar());

        pie.add(inicio, BorderLayout.WEST);
        pie.add(guardar, BorderLayout.EAST);

        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);
    }

    public void actualizar() {
        Partida partida = controlador.consultarEstado();

        if (partida == null || !controlador.hayPartidaReal()) {
            return;
        }

        setContexto("COMANDANTE " + partida.getNombreComandante().toUpperCase());

        ruta.removeAll();

        for (int numero : ORDEN_RUTA) {
            EstadoNodo estado;

            if (partida.fueMisionSuperada(numero)) {
                estado = EstadoNodo.SUPERADA;
            } else if (numero == partida.getMisionActual()) {
                estado = EstadoNodo.ACTUAL;
            } else {
                estado = EstadoNodo.PENDIENTE;
            }

            ruta.add(new NodoMision(numero, estado));
        }

        int actual = partida.getMisionActual();
        int capacidad = partida.getEscuadron().getCapacidadTotal();
        int enemiga = partida.getMision() == null ? capacidad : partida.getMision().getCapacidadEnemiga();

        etiquetaNumero.setText(String.format("%02d", actual));
        etiquetaCapacidad.setText(capacidad + " puntos");
        etiquetaEnemigo.setText("<html>Ejército enemigo: <b>" + enemiga + " puntos</b></html>");

        ruta.revalidate();
        ruta.repaint();
    }

    private void guardar() {
        try {
            controlador.guardarPartidaActual();
            DialogoMars.exito(this, "Partida guardada", "La expedición se guardó correctamente.");
        } catch (RuntimeException e) {
            DialogoMars.error(this, "No se pudo guardar", obtenerMensaje(e));
        }
    }

    private String obtenerMensaje(Throwable error) {
        Throwable actual = error;

        while (actual.getCause() != null && actual.getCause() != actual) {
            actual = actual.getCause();
        }

        return actual.getMessage() == null || actual.getMessage().isBlank() ? "No fue posible guardar la expedición." : actual.getMessage();
    }

    private enum EstadoNodo {
        SUPERADA,
        ACTUAL,
        PENDIENTE
    }

    private static class PanelRutaCampania extends JPanel {

        private PanelRutaCampania() {
            setOpaque(false);
            setLayout(new GridLayout(2, 5, 14, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(90, 119, 130));
            g2.setStroke(new BasicStroke(4f));

            int anchoCelda = getWidth() / 5;
            int altoCelda = getHeight() / 2;

            int ySuperior = altoCelda / 2;
            int yInferior = altoCelda + altoCelda / 2;

            int xPrimero = anchoCelda / 2;
            int xUltimo = anchoCelda * 4 + anchoCelda / 2;

            g2.drawLine(xPrimero, ySuperior, xUltimo, ySuperior);
            g2.drawLine(xUltimo, ySuperior, xUltimo, yInferior);
            g2.drawLine(xPrimero, yInferior, xUltimo, yInferior);

            g2.dispose();
        }
    }

    private static class NodoMision extends JPanel {

        private final int numero;
        private final EstadoNodo estado;

        private NodoMision(int numero, EstadoNodo estado) {
            this.numero = numero;
            this.estado = estado;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro = Math.min(74, Math.min(getWidth() - 12, getHeight() - 42));
            int x = (getWidth() - diametro) / 2;
            int y = Math.max(4, (getHeight() - diametro - 28) / 2);

            Color fondo = switch (estado) {
                case SUPERADA -> TemaMars.CORRECTO;
                case ACTUAL -> TemaMars.ACCION;
                case PENDIENTE -> new Color(43, 72, 86);
            };

            g2.setColor(fondo);
            g2.fillOval(x, y, diametro, diametro);

            g2.setColor(estado == EstadoNodo.PENDIENTE ? new Color(92, 123, 136) : fondo.darker());
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, diametro, diametro);

            String numeroTexto = estado == EstadoNodo.SUPERADA ? "✓" : String.format("%02d", numero);

            g2.setFont(TemaMars.tituloPanel());
            g2.setColor(estado == EstadoNodo.PENDIENTE ? TemaMars.TEXTO_CLARO : TemaMars.TEXTO);

            int tx = (getWidth() - g2.getFontMetrics().stringWidth(numeroTexto)) / 2;
            int ty = y + (diametro - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();

            g2.drawString(numeroTexto, tx, ty);

            String descripcion = switch (estado) {
                case SUPERADA -> "Superada";
                case ACTUAL -> "Actual";
                case PENDIENTE -> "Pendiente";
            };

            g2.setFont(TemaMars.textoSecundario());
            g2.setColor(estado == EstadoNodo.ACTUAL ? TemaMars.ACCION : TemaMars.TEXTO_CLARO);

            int dx = (getWidth() - g2.getFontMetrics().stringWidth(descripcion)) / 2;
            g2.drawString(descripcion, dx, y + diametro + 20);

            g2.dispose();
        }
    }
}