package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class PanelMapaMars extends JPanel implements Scrollable {

    private static final int MARGEN_IZQUIERDO = 38;
    private static final int MARGEN_SUPERIOR = 34;
    private static final int MARGEN_DERECHO = 20;
    private static final int MARGEN_INFERIOR = 20;

    private static final Color VELO_FONDO = new Color(5, 18, 27, 18);
    private static final Color CASILLA_LIBRE = new Color(12, 39, 50, 10);
    private static final Color CASILLA_OCUPADA = new Color(12, 39, 50, 24);
    private static final Color BORDE_CASILLA = new Color(224, 239, 240, 42);
    private static final Color BORDE_PRINCIPAL = new Color(111, 229, 235, 70);
    private static final Color HOVER_LIBRE = new Color(163, 221, 120, 105);
    private static final Color HOVER_OCUPADO = new Color(233, 120, 101, 110);
    private static final Color SELECCION = new Color(111, 229, 235, 215);

    private Partida partida;
    private Posicion seleccionada;
    private Posicion posicionHover;
    private Consumer<Posicion> accionCasilla;
    private boolean modoColocacion;
    private final String rutaFondo;

    public PanelMapaMars() {
        rutaFondo = resolverFondoPrincipal();

        setOpaque(true);
        setBackground(TemaMars.FONDO_PROFUNDO);
        setPreferredSize(new Dimension(900, 700));
        setMinimumSize(new Dimension(450, 360));
        setToolTipText("");

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Posicion posicion = obtenerPosicion(e);

                if (posicion != null && accionCasilla != null) {
                    accionCasilla.accept(posicion);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Posicion nueva = obtenerPosicion(e);

                if ((posicionHover == null && nueva != null)
                        || (posicionHover != null && !posicionHover.equals(nueva))) {
                    posicionHover = nueva;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                posicionHover = null;
                repaint();
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void actualizar(Partida partida) {
        this.partida = partida;
        repaint();
    }

    public void setAccionCasilla(Consumer<Posicion> accionCasilla) {
        this.accionCasilla = accionCasilla;
    }

    public void setSeleccionada(Posicion seleccionada) {
        this.seleccionada = seleccionada;
        repaint();
    }

    public void setModoColocacion(boolean modoColocacion) {
        this.modoColocacion = modoColocacion;
        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Posicion posicion = obtenerPosicion(e);

        if (posicion == null || partida == null || partida.getTablero() == null) {
            return null;
        }

        OcupanteMapa ocupante = partida.getTablero().obtener(posicion);

        if (ocupante instanceof ComponenteCombate componente) {
            return posicion + " · " + componente.getNombre();
        }

        if (ocupante instanceof Obstaculo) {
            return posicion + " · Obstáculo";
        }

        return posicion + " · Libre";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        pintarFondo(g2);

        if (partida == null || partida.getTablero() == null) {
            g2.dispose();
            return;
        }

        Tablero tablero = partida.getTablero();
        MetricaMapa metrica = calcularMetrica(tablero);

        pintarMarcoMapa(g2, metrica);
        pintarCoordenadas(g2, tablero, metrica);
        pintarMatriz(g2, tablero, metrica);

        g2.dispose();
    }

    private void pintarFondo(Graphics2D g2) {
        g2.setColor(TemaMars.FONDO_PROFUNDO);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (rutaFondo != null && GestorImagenes.existe(rutaFondo)) {
            ImageIcon fondo = GestorImagenes.cargarCubriendo(rutaFondo, Math.max(1, getWidth()), Math.max(1, getHeight()));
            g2.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), null);
        }

        g2.setColor(VELO_FONDO);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void pintarMarcoMapa(Graphics2D g2, MetricaMapa metrica) {
        int margen = 5;

        g2.setColor(new Color(3, 15, 23, 65));
        g2.fillRoundRect(
                metrica.origenX - margen,
                metrica.origenY - margen,
                metrica.anchoMatriz + margen * 2,
                metrica.altoMatriz + margen * 2,
                14,
                14
        );

        g2.setColor(new Color(111, 229, 235, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(
                metrica.origenX - margen,
                metrica.origenY - margen,
                metrica.anchoMatriz + margen * 2,
                metrica.altoMatriz + margen * 2,
                14,
                14
        );
    }

    private void pintarCoordenadas(Graphics2D g2, Tablero tablero, MetricaMapa metrica) {
        if (metrica.tamanoCasilla < 14) {
            return;
        }

        g2.setFont(TemaMars.textoPequeno());
        g2.setColor(new Color(247, 242, 233, 185));

        for (int columna = 0; columna < tablero.getColumnas(); columna++) {
            if (columna % 5 == 0 || columna == tablero.getColumnas() - 1) {
                String texto = String.valueOf(columna);
                int anchoTexto = g2.getFontMetrics().stringWidth(texto);

                int x = metrica.origenX
                        + columna * metrica.tamanoCasilla
                        + (metrica.tamanoCasilla - anchoTexto) / 2;

                g2.drawString(texto, x, metrica.origenY - 9);
            }
        }

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            if (fila % 5 == 0 || fila == tablero.getFilas() - 1) {
                String texto = String.valueOf(fila);
                int anchoTexto = g2.getFontMetrics().stringWidth(texto);

                int x = metrica.origenX - anchoTexto - 9;
                int y = metrica.origenY
                        + fila * metrica.tamanoCasilla
                        + metrica.tamanoCasilla / 2
                        + g2.getFontMetrics().getAscent() / 2
                        - 2;

                g2.drawString(texto, x, y);
            }
        }
    }

    private void pintarMatriz(Graphics2D g2, Tablero tablero, MetricaMapa metrica) {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                pintarCasilla(g2, tablero, posicion, metrica);
            }
        }
    }

    private void pintarCasilla(Graphics2D g2, Tablero tablero, Posicion posicion, MetricaMapa metrica) {
        int x = metrica.origenX + posicion.getColumna() * metrica.tamanoCasilla;
        int y = metrica.origenY + posicion.getFila() * metrica.tamanoCasilla;

        OcupanteMapa ocupante = tablero.obtener(posicion);

        g2.setColor(ocupante == null ? CASILLA_LIBRE : CASILLA_OCUPADA);
        g2.fillRect(x, y, metrica.tamanoCasilla, metrica.tamanoCasilla);

        boolean lineaPrincipal = posicion.getFila() % 5 == 0 || posicion.getColumna() % 5 == 0;

        g2.setColor(lineaPrincipal ? BORDE_PRINCIPAL : BORDE_CASILLA);
        g2.setStroke(new BasicStroke(lineaPrincipal ? 1.25f : 0.7f));
        g2.drawRect(x, y, metrica.tamanoCasilla, metrica.tamanoCasilla);

        if (ocupante != null) {
            pintarOcupante(g2, ocupante, x, y, metrica.tamanoCasilla);
        }

        if (modoColocacion && posicion.equals(posicionHover)) {
            boolean libre = tablero.estaLibre(posicion);

            g2.setColor(libre ? HOVER_LIBRE : HOVER_OCUPADO);
            g2.fillRect(x + 1, y + 1, metrica.tamanoCasilla - 1, metrica.tamanoCasilla - 1);

            g2.setColor(libre ? TemaMars.CORRECTO : TemaMars.ERROR);
            g2.setStroke(new BasicStroke(Math.max(2f, metrica.tamanoCasilla / 10f)));
            g2.drawRect(x + 1, y + 1, metrica.tamanoCasilla - 2, metrica.tamanoCasilla - 2);
        }

        if (seleccionada != null && seleccionada.equals(posicion)) {
            g2.setColor(SELECCION);
            g2.setStroke(new BasicStroke(Math.max(2f, metrica.tamanoCasilla / 9f)));
            g2.drawRect(x + 1, y + 1, metrica.tamanoCasilla - 2, metrica.tamanoCasilla - 2);
        }
    }

    private void pintarOcupante(Graphics2D g2, OcupanteMapa ocupante, int x, int y, int tamano) {
        String ruta = null;

        if (ocupante instanceof Obstaculo obstaculo) {
            ruta = obstaculo.getRutaImagen();
        } else if (ocupante instanceof ComponenteCombate componente) {
            ruta = componente.getRutaImagenActual();
        }

        int margen = Math.max(1, tamano / 10);
        int tamanoImagen = Math.max(2, tamano - margen * 2);

        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = GestorImagenes.cargar(ruta, tamanoImagen, tamanoImagen);

            g2.drawImage(
                    icono.getImage(),
                    x + margen,
                    y + margen,
                    tamanoImagen,
                    tamanoImagen,
                    null
            );
        } else if (ocupante instanceof NucleoOxigeno) {
            g2.setColor(TemaMars.ACCION);
            g2.fillOval(
                    x + margen * 2,
                    y + margen * 2,
                    Math.max(2, tamano - margen * 4),
                    Math.max(2, tamano - margen * 4)
            );
        } else {
            g2.setColor(TemaMars.ENERGIA);
            g2.fillOval(
                    x + margen * 2,
                    y + margen * 2,
                    Math.max(2, tamano - margen * 4),
                    Math.max(2, tamano - margen * 4)
            );
        }

        if (ocupante instanceof ComponenteCombate componente && tamano >= 13) {
            pintarVida(g2, componente, x, y, tamano);
        }
    }

    private void pintarVida(Graphics2D g2, ComponenteCombate componente, int x, int y, int tamano) {
        double maximo = componente.getVidaMaxima();

        if (maximo <= 0) {
            return;
        }

        double porcentaje = componente.getVidaActual() / maximo;
        porcentaje = Math.max(0, Math.min(1, porcentaje));

        int margen = Math.max(2, tamano / 10);
        int anchoDisponible = tamano - margen * 2;
        int anchoVida = (int) Math.round(anchoDisponible * porcentaje);
        int alto = Math.max(2, tamano / 12);
        int yBarra = y + tamano - alto - 2;

        g2.setColor(new Color(4, 13, 19, 190));
        g2.fillRoundRect(x + margen, yBarra, anchoDisponible, alto, alto, alto);

        Color colorVida;

        if (porcentaje > 0.5) {
            colorVida = TemaMars.CORRECTO;
        } else if (porcentaje > 0.25) {
            colorVida = TemaMars.ACCION;
        } else {
            colorVida = TemaMars.ERROR;
        }

        g2.setColor(colorVida);
        g2.fillRoundRect(x + margen, yBarra, anchoVida, alto, alto, alto);
    }

    private Posicion obtenerPosicion(MouseEvent e) {
        if (partida == null || partida.getTablero() == null) {
            return null;
        }

        Tablero tablero = partida.getTablero();
        MetricaMapa metrica = calcularMetrica(tablero);

        int relativoX = e.getX() - metrica.origenX;
        int relativoY = e.getY() - metrica.origenY;

        if (relativoX < 0 || relativoY < 0
                || relativoX >= metrica.anchoMatriz
                || relativoY >= metrica.altoMatriz) {
            return null;
        }

        int columna = relativoX / metrica.tamanoCasilla;
        int fila = relativoY / metrica.tamanoCasilla;

        Posicion posicion = new Posicion(fila, columna);

        return tablero.estaDentro(posicion) ? posicion : null;
    }

    private MetricaMapa calcularMetrica(Tablero tablero) {
        int espacioHorizontal = Math.max(
                1,
                getWidth() - MARGEN_IZQUIERDO - MARGEN_DERECHO
        );

        int espacioVertical = Math.max(
                1,
                getHeight() - MARGEN_SUPERIOR - MARGEN_INFERIOR
        );

        int porAncho = Math.max(1, espacioHorizontal / tablero.getColumnas());
        int porAlto = Math.max(1, espacioVertical / tablero.getFilas());

        int tamanoCasilla = Math.max(1, Math.min(porAncho, porAlto));

        int anchoMatriz = tamanoCasilla * tablero.getColumnas();
        int altoMatriz = tamanoCasilla * tablero.getFilas();

        int areaInicioX = MARGEN_IZQUIERDO;
        int areaInicioY = MARGEN_SUPERIOR;

        int origenX = areaInicioX + Math.max(0, (espacioHorizontal - anchoMatriz) / 2);
        int origenY = areaInicioY + Math.max(0, (espacioVertical - altoMatriz) / 2);

        return new MetricaMapa(
                origenX,
                origenY,
                tamanoCasilla,
                anchoMatriz,
                altoMatriz
        );
    }

    private String resolverFondoPrincipal() {
        String[] candidatos = {
            "assets/PRINCIPAL.png",
            "assets/PRINCIPAL.PNG",
            "assets/PRINCIPAL.jpg",
            "assets/PRINCIPAL.JPG",
            "assets/PRINCIPAL.jpeg",
            "assets/PRINCIPAL.JPEG",
            "assets/PRINCIPAL"
        };

        for (String candidato : candidatos) {
            if (GestorImagenes.existe(candidato)) {
                return candidato;
            }
        }

        return null;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(900, 680);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL) {
            return Math.max(50, visibleRect.height - 50);
        }

        return Math.max(50, visibleRect.width - 50);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    private static class MetricaMapa {

        private final int origenX;
        private final int origenY;
        private final int tamanoCasilla;
        private final int anchoMatriz;
        private final int altoMatriz;

        private MetricaMapa(int origenX, int origenY, int tamanoCasilla, int anchoMatriz, int altoMatriz) {
            this.origenX = origenX;
            this.origenY = origenY;
            this.tamanoCasilla = tamanoCasilla;
            this.anchoMatriz = anchoMatriz;
            this.altoMatriz = altoMatriz;
        }
    }
}