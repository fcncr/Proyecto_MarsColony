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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class PanelMapaMars extends JPanel implements Scrollable {

    private static final int MARGEN_IZQUIERDO = 38;
    private static final int MARGEN_SUPERIOR = 34;
    private static final int MARGEN_DERECHO = 20;
    private static final int MARGEN_INFERIOR = 20;

    private static final double ZOOM_MINIMO = 0.60;
    private static final double ZOOM_MAXIMO = 3.50;
    private static final double PASO_ZOOM = 0.10;

    private static final int DURACION_ZOOM_MS = 105;
    private static final int INTERVALO_ZOOM_MS = 15;
    private static final int TAMANO_ICONO_CACHE = 256;

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

    private double zoomActual;
    private double zoomObjetivo;
    private double zoomVisual;
    private double zoomVisualInicio;
    private double zoomBaseCaptura;

    private boolean animandoZoom;
    private long inicioAnimacionNano;
    private final Timer temporizadorZoom;
    private BufferedImage capturaZoom;

    private double anclaColumna;
    private double anclaFila;
    private double anclaBaseX;
    private double anclaBaseY;
    private int anclaViewportX;
    private int anclaViewportY;
    private boolean anclaValida;

    private double acumuladorRueda;

    private boolean desplazando;
    private Point inicioArrastrePantalla;
    private Point inicioArrastreViewport;

    public PanelMapaMars() {
        rutaFondo = resolverFondoPrincipal();

        zoomActual = 1.0;
        zoomObjetivo = 1.0;
        zoomVisual = 1.0;
        zoomVisualInicio = 1.0;
        zoomBaseCaptura = 1.0;

        animandoZoom = false;
        anclaValida = false;
        desplazando = false;
        acumuladorRueda = 0;

        setOpaque(true);
        setBackground(TemaMars.FONDO_PROFUNDO);
        setMinimumSize(new Dimension(450, 360));
        setToolTipText("");

        temporizadorZoom = new Timer(
                INTERVALO_ZOOM_MS,
                e -> actualizarAnimacionZoom()
        );

        temporizadorZoom.setCoalesce(true);

        configurarMouse();
        actualizarCursor();
    }

    private void configurarMouse() {
        MouseAdapter mouse = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                Posicion posicion = obtenerPosicion(e);

                if (posicion != null && accionCasilla != null) {
                    accionCasilla.accept(posicion);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                JViewport viewport = obtenerViewport();

                if (viewport == null) {
                    return;
                }

                inicioArrastrePantalla = e.getLocationOnScreen();
                inicioArrastreViewport = viewport.getViewPosition();

                desplazando = true;

                setCursor(
                        Cursor.getPredefinedCursor(
                                Cursor.MOVE_CURSOR
                        )
                );
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!desplazando) {
                    return;
                }

                if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == 0) {
                    return;
                }

                JViewport viewport = obtenerViewport();

                if (viewport == null
                        || inicioArrastrePantalla == null
                        || inicioArrastreViewport == null) {

                    return;
                }

                Point actual = e.getLocationOnScreen();

                int deltaX = actual.x - inicioArrastrePantalla.x;
                int deltaY = actual.y - inicioArrastrePantalla.y;

                int nuevoX = inicioArrastreViewport.x - deltaX;
                int nuevoY = inicioArrastreViewport.y - deltaY;

                viewport.setViewPosition(
                        limitarPosicionViewport(
                                viewport,
                                nuevoX,
                                nuevoY
                        )
                );
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                desplazando = false;
                inicioArrastrePantalla = null;
                inicioArrastreViewport = null;

                actualizarCursor();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (desplazando) {
                    return;
                }

                Posicion nueva = obtenerPosicion(e);

                if ((posicionHover == null && nueva != null)
                        || (posicionHover != null && !posicionHover.equals(nueva))) {

                    posicionHover = nueva;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!desplazando) {
                    posicionHover = null;
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!e.isControlDown()) {
                    return;
                }

                e.consume();

                double rotacion = e.getPreciseWheelRotation();

                if (rotacion == 0) {
                    return;
                }

                if (acumuladorRueda != 0
                        && Math.signum(acumuladorRueda) != Math.signum(rotacion)) {

                    acumuladorRueda = 0;
                }

                acumuladorRueda += rotacion;

                while (Math.abs(acumuladorRueda) >= 1.0) {
                    if (acumuladorRueda < 0) {
                        cambiarNivelZoom(
                                1,
                                e.getPoint()
                        );

                        acumuladorRueda += 1.0;
                    } else {
                        cambiarNivelZoom(
                                -1,
                                e.getPoint()
                        );

                        acumuladorRueda -= 1.0;
                    }
                }
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
    }

    public void actualizar(Partida partida) {
        this.partida = partida;

        revalidate();
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

        actualizarCursor();
        repaint();
    }

    public void aumentarZoom() {
        cambiarNivelZoom(
                1,
                obtenerCentroVisible()
        );
    }

    public void disminuirZoom() {
        cambiarNivelZoom(
                -1,
                obtenerCentroVisible()
        );
    }

    public void ajustarMapa() {
        detenerAnimacion();

        double anterior = zoomObjetivo;

        zoomActual = 1.0;
        zoomObjetivo = 1.0;
        zoomVisual = 1.0;
        zoomVisualInicio = 1.0;
        zoomBaseCaptura = 1.0;

        capturaZoom = null;
        anclaValida = false;
        acumuladorRueda = 0;

        firePropertyChange(
                "zoom",
                anterior,
                zoomObjetivo
        );

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            JViewport viewport = obtenerViewport();

            if (viewport != null) {
                viewport.setViewPosition(
                        new Point(0, 0)
                );
            }

            revalidate();
            repaint();
        });
    }

    public void restablecerZoom() {
        ajustarMapa();
    }

    public int getZoomPorcentaje() {
        return (int) Math.round(
                zoomObjetivo * 100
        );
    }

    public double getZoom() {
        return zoomObjetivo;
    }
    
    public void establecerZoomSinAnimacion(double nuevoZoom) {
        detenerAnimacion();

        double anterior = zoomObjetivo;

        double limitado = Math.max(
                ZOOM_MINIMO,
                Math.min(
                        ZOOM_MAXIMO,
                        nuevoZoom
                )
        );

        limitado = Math.round(
                limitado * 10.0
        ) / 10.0;

        zoomActual = limitado;
        zoomObjetivo = limitado;
        zoomVisual = limitado;
        zoomVisualInicio = limitado;
        zoomBaseCaptura = limitado;

        capturaZoom = null;
        anclaValida = false;
        acumuladorRueda = 0;

        firePropertyChange(
                "zoom",
                anterior,
                zoomObjetivo
        );

        revalidate();
        repaint();
    }

    private void cambiarNivelZoom(int direccion, Point puntoAncla) {
        double base = zoomObjetivo;

        double nuevoZoom = redondearNivel(
                base + direccion * PASO_ZOOM
        );

        nuevoZoom = Math.max(
                ZOOM_MINIMO,
                Math.min(
                        ZOOM_MAXIMO,
                        nuevoZoom
                )
        );

        if (Math.abs(
                nuevoZoom - zoomObjetivo
        ) < 0.0001) {

            return;
        }

        iniciarTransicionZoom(
                nuevoZoom,
                puntoAncla
        );
    }

    private double redondearNivel(double valor) {
        return Math.round(
                valor * 10.0
        ) / 10.0;
    }

    private void iniciarTransicionZoom(double nuevoZoom, Point puntoAncla) {
        double objetivoAnterior = zoomObjetivo;

        if (!animandoZoom) {
            zoomBaseCaptura = zoomActual;
            zoomVisual = zoomActual;

            prepararAnclaInicial(
                    puntoAncla
            );

            capturaZoom = crearCapturaMapa();
        } else if (puntoAncla != null) {
            actualizarAnclaDuranteAnimacion(
                    puntoAncla
            );
        }

        zoomVisualInicio = zoomVisual;
        zoomObjetivo = nuevoZoom;

        inicioAnimacionNano = System.nanoTime();
        animandoZoom = true;

        firePropertyChange(
                "zoom",
                objetivoAnterior,
                zoomObjetivo
        );

        revalidate();

        temporizadorZoom.restart();

        repaint();
    }

    private void actualizarAnimacionZoom() {
        if (!animandoZoom) {
            temporizadorZoom.stop();
            return;
        }

        double transcurridoMs =
                (
                System.nanoTime()
                - inicioAnimacionNano
                ) / 1_000_000.0;

        double progreso =
                transcurridoMs
                / DURACION_ZOOM_MS;

        if (progreso >= 1.0) {
            finalizarAnimacion();
            return;
        }

        progreso = Math.max(
                0,
                Math.min(
                        1,
                        progreso
                )
        );

        double suavizado = easeOutCubic(
                progreso
        );

        zoomVisual =
                zoomVisualInicio
                + (
                zoomObjetivo
                - zoomVisualInicio
                ) * suavizado;

        repaint();
    }

    private double easeOutCubic(double t) {
        double inverso = 1.0 - t;

        return 1.0
                - inverso
                * inverso
                * inverso;
    }

    private void finalizarAnimacion() {
        temporizadorZoom.stop();

        zoomActual = zoomObjetivo;
        zoomVisual = zoomActual;
        zoomVisualInicio = zoomActual;

        animandoZoom = false;

        capturaZoom = null;

        revalidate();

        SwingUtilities.invokeLater(() -> {
            ajustarViewportAlAncla();

            anclaValida = false;

            revalidate();
            repaint();
        });
    }

    private void detenerAnimacion() {
        temporizadorZoom.stop();

        animandoZoom = false;
        capturaZoom = null;
    }

    private void prepararAnclaInicial(Point puntoAncla) {
        if (partida == null
                || partida.getTablero() == null) {

            anclaValida = false;
            return;
        }

        JViewport viewport = obtenerViewport();

        if (viewport == null) {
            anclaValida = false;
            return;
        }

        if (puntoAncla == null) {
            puntoAncla = obtenerCentroVisible();
        }

        if (puntoAncla == null) {
            anclaValida = false;
            return;
        }

        Tablero tablero = partida.getTablero();

        MetricaMapa metrica = calcularMetrica(
                tablero,
                zoomActual,
                getWidth(),
                getHeight()
        );

        Point vista = viewport.getViewPosition();

        anclaViewportX =
                puntoAncla.x
                - vista.x;

        anclaViewportY =
                puntoAncla.y
                - vista.y;

        anclaColumna =
                (
                puntoAncla.x
                - metrica.origenX
                ) / metrica.tamanoCasilla;

        anclaFila =
                (
                puntoAncla.y
                - metrica.origenY
                ) / metrica.tamanoCasilla;

        anclaBaseX =
                metrica.origenX
                + anclaColumna
                * metrica.tamanoCasilla;

        anclaBaseY =
                metrica.origenY
                + anclaFila
                * metrica.tamanoCasilla;

        anclaValida = true;
    }

    private void actualizarAnclaDuranteAnimacion(Point punto) {
        if (!anclaValida
                || capturaZoom == null
                || partida == null
                || partida.getTablero() == null) {

            return;
        }

        JViewport viewport = obtenerViewport();

        if (viewport == null) {
            return;
        }

        double escala =
                zoomVisual
                / zoomBaseCaptura;

        if (escala <= 0) {
            return;
        }

        Point vista = viewport.getViewPosition();

        double destinoAnteriorX =
                vista.x
                + anclaViewportX;

        double destinoAnteriorY =
                vista.y
                + anclaViewportY;

        double traslacionX =
                destinoAnteriorX
                - anclaBaseX
                * escala;

        double traslacionY =
                destinoAnteriorY
                - anclaBaseY
                * escala;

        double fuenteX =
                (
                punto.x
                - traslacionX
                ) / escala;

        double fuenteY =
                (
                punto.y
                - traslacionY
                ) / escala;

        MetricaMapa base = calcularMetrica(
                partida.getTablero(),
                zoomBaseCaptura,
                capturaZoom.getWidth(),
                capturaZoom.getHeight()
        );

        anclaColumna =
                (
                fuenteX
                - base.origenX
                ) / base.tamanoCasilla;

        anclaFila =
                (
                fuenteY
                - base.origenY
                ) / base.tamanoCasilla;

        anclaBaseX =
                base.origenX
                + anclaColumna
                * base.tamanoCasilla;

        anclaBaseY =
                base.origenY
                + anclaFila
                * base.tamanoCasilla;

        anclaViewportX =
                punto.x
                - vista.x;

        anclaViewportY =
                punto.y
                - vista.y;
    }

    private void ajustarViewportAlAncla() {
        if (!anclaValida
                || partida == null
                || partida.getTablero() == null) {

            return;
        }

        JViewport viewport = obtenerViewport();

        if (viewport == null) {
            return;
        }

        MetricaMapa finalMapa = calcularMetrica(
                partida.getTablero(),
                zoomActual,
                getWidth(),
                getHeight()
        );

        int contenidoX =
                (int) Math.round(
                        finalMapa.origenX
                        + anclaColumna
                        * finalMapa.tamanoCasilla
                );

        int contenidoY =
                (int) Math.round(
                        finalMapa.origenY
                        + anclaFila
                        * finalMapa.tamanoCasilla
                );

        viewport.setViewPosition(
                limitarPosicionViewport(
                        viewport,
                        contenidoX - anclaViewportX,
                        contenidoY - anclaViewportY
                )
        );
    }

    private BufferedImage crearCapturaMapa() {
        if (partida == null
                || partida.getTablero() == null
                || getWidth() <= 0
                || getHeight() <= 0) {

            return null;
        }

        BufferedImage imagen = new BufferedImage(
                getWidth(),
                getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = imagen.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        pintarEscena(
                g2,
                zoomActual,
                getWidth(),
                getHeight()
        );

        g2.dispose();

        return imagen;
    }

    private double obtenerZoomEstructural() {
        if (!animandoZoom) {
            return zoomActual;
        }

        return Math.max(
                zoomBaseCaptura,
                zoomObjetivo
        );
    }

    private Dimension calcularDimensionContenido(double zoom) {
        Dimension viewport = obtenerDimensionViewport();

        if (partida == null
                || partida.getTablero() == null) {

            return new Dimension(
                    Math.max(
                            450,
                            viewport.width
                    ),
                    Math.max(
                            360,
                            viewport.height
                    )
            );
        }

        Tablero tablero = partida.getTablero();

        double tamanoCasilla =
                calcularTamanoBase(
                        tablero,
                        viewport
                ) * zoom;

        int ancho =
                (int) Math.ceil(
                        MARGEN_IZQUIERDO
                        + tablero.getColumnas()
                        * tamanoCasilla
                        + MARGEN_DERECHO
                );

        int alto =
                (int) Math.ceil(
                        MARGEN_SUPERIOR
                        + tablero.getFilas()
                        * tamanoCasilla
                        + MARGEN_INFERIOR
                );

        return new Dimension(
                Math.max(
                        viewport.width,
                        ancho
                ),
                Math.max(
                        viewport.height,
                        alto
                )
        );
    }

    private JViewport obtenerViewport() {
        if (getParent() instanceof JViewport viewport) {
            return viewport;
        }

        return null;
    }

    private Dimension obtenerDimensionViewport() {
        JViewport viewport = obtenerViewport();

        if (viewport != null) {
            Dimension dimension =
                    viewport.getExtentSize();

            if (dimension.width > 0
                    && dimension.height > 0) {

                return dimension;
            }
        }

        return new Dimension(
                900,
                680
        );
    }

    private Point obtenerCentroVisible() {
        JViewport viewport = obtenerViewport();

        if (viewport == null) {
            return null;
        }

        Point posicion =
                viewport.getViewPosition();

        Dimension visible =
                viewport.getExtentSize();

        return new Point(
                posicion.x + visible.width / 2,
                posicion.y + visible.height / 2
        );
    }

    private double calcularTamanoBase(
            Tablero tablero,
            Dimension viewport) {

        double disponibleX =
                Math.max(
                        1,
                        viewport.width
                        - MARGEN_IZQUIERDO
                        - MARGEN_DERECHO
                );

        double disponibleY =
                Math.max(
                        1,
                        viewport.height
                        - MARGEN_SUPERIOR
                        - MARGEN_INFERIOR
                );

        return Math.max(
                1,
                Math.min(
                        disponibleX
                        / tablero.getColumnas(),
                        disponibleY
                        / tablero.getFilas()
                )
        );
    }

    private Point limitarPosicionViewport(
            JViewport viewport,
            int x,
            int y) {

        Dimension contenido =
                calcularDimensionContenido(
                        obtenerZoomEstructural()
                );

        Dimension visible =
                viewport.getExtentSize();

        int maxX =
                Math.max(
                        0,
                        contenido.width
                        - visible.width
                );

        int maxY =
                Math.max(
                        0,
                        contenido.height
                        - visible.height
                );

        return new Point(
                Math.max(
                        0,
                        Math.min(
                                x,
                                maxX
                        )
                ),
                Math.max(
                        0,
                        Math.min(
                                y,
                                maxY
                        )
                )
        );
    }

    private void actualizarCursor() {
        if (desplazando) {
            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.MOVE_CURSOR
                    )
            );

            return;
        }

        if (modoColocacion) {
            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.CROSSHAIR_CURSOR
                    )
            );

            return;
        }

        setCursor(
                Cursor.getDefaultCursor()
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return calcularDimensionContenido(
                obtenerZoomEstructural()
        );
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (desplazando) {
            return null;
        }

        Posicion posicion =
                obtenerPosicion(e);

        if (posicion == null
                || partida == null
                || partida.getTablero() == null) {

            return null;
        }

        OcupanteMapa ocupante =
                partida.getTablero()
                        .obtener(posicion);

        if (ocupante instanceof ComponenteCombate componente) {
            return posicion
                    + " · "
                    + componente.getNombre();
        }

        if (ocupante instanceof Obstaculo) {
            return posicion
                    + " · Obstáculo";
        }

        return posicion
                + " · Libre";
    }

    private Posicion obtenerPosicion(MouseEvent e) {
        if (partida == null
                || partida.getTablero() == null) {

            return null;
        }

        if (animandoZoom
                && capturaZoom != null
                && anclaValida) {

            return obtenerPosicionDuranteZoom(
                    e.getPoint()
            );
        }

        Tablero tablero = partida.getTablero();

        MetricaMapa metrica = calcularMetrica(
                tablero,
                zoomActual,
                getWidth(),
                getHeight()
        );

        return posicionDesdeMetrica(
                tablero,
                metrica,
                e.getX(),
                e.getY()
        );
    }

    private Posicion obtenerPosicionDuranteZoom(Point punto) {
        Tablero tablero =
                partida.getTablero();

        JViewport viewport =
                obtenerViewport();

        if (viewport == null) {
            return null;
        }

        double escala =
                zoomVisual
                / zoomBaseCaptura;

        Point vista =
                viewport.getViewPosition();

        double destinoX =
                vista.x
                + anclaViewportX;

        double destinoY =
                vista.y
                + anclaViewportY;

        double trasladoX =
                destinoX
                - anclaBaseX
                * escala;

        double trasladoY =
                destinoY
                - anclaBaseY
                * escala;

        double fuenteX =
                (
                punto.x
                - trasladoX
                ) / escala;

        double fuenteY =
                (
                punto.y
                - trasladoY
                ) / escala;

        MetricaMapa base = calcularMetrica(
                tablero,
                zoomBaseCaptura,
                capturaZoom.getWidth(),
                capturaZoom.getHeight()
        );

        return posicionDesdeMetrica(
                tablero,
                base,
                fuenteX,
                fuenteY
        );
    }

    private Posicion posicionDesdeMetrica(
            Tablero tablero,
            MetricaMapa metrica,
            double x,
            double y) {

        double relativoX =
                x - metrica.origenX;

        double relativoY =
                y - metrica.origenY;

        if (relativoX < 0
                || relativoY < 0
                || relativoX >= metrica.anchoMatriz
                || relativoY >= metrica.altoMatriz) {

            return null;
        }

        int columna =
                (int) Math.floor(
                        relativoX
                        / metrica.tamanoCasilla
                );

        int fila =
                (int) Math.floor(
                        relativoY
                        / metrica.tamanoCasilla
                );

        Posicion posicion =
                new Posicion(
                        fila,
                        columna
                );

        return tablero.estaDentro(posicion)
                ? posicion
                : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        if (animandoZoom
                && capturaZoom != null
                && anclaValida) {

            pintarAnimacionZoom(g2);

            g2.dispose();
            return;
        }

        pintarEscena(
                g2,
                zoomActual,
                getWidth(),
                getHeight()
        );

        g2.dispose();
    }

    private void pintarAnimacionZoom(Graphics2D g2) {
        g2.setColor(
                TemaMars.FONDO_PROFUNDO
        );

        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        JViewport viewport =
                obtenerViewport();

        double escala =
                zoomVisual
                / zoomBaseCaptura;

        double destinoX =
                anclaBaseX;

        double destinoY =
                anclaBaseY;

        if (viewport != null) {
            Point vista =
                    viewport.getViewPosition();

            destinoX =
                    vista.x
                    + anclaViewportX;

            destinoY =
                    vista.y
                    + anclaViewportY;
        }

        double trasladoX =
                destinoX
                - anclaBaseX
                * escala;

        double trasladoY =
                destinoY
                - anclaBaseY
                * escala;

        g2.translate(
                trasladoX,
                trasladoY
        );

        g2.scale(
                escala,
                escala
        );

        g2.drawImage(
                capturaZoom,
                0,
                0,
                null
        );
    }

    private void pintarEscena(
            Graphics2D g2,
            double zoom,
            int ancho,
            int alto) {

        pintarFondo(
                g2,
                ancho,
                alto
        );

        if (partida == null
                || partida.getTablero() == null) {

            return;
        }

        Tablero tablero =
                partida.getTablero();

        MetricaMapa metrica =
                calcularMetrica(
                        tablero,
                        zoom,
                        ancho,
                        alto
                );

        pintarMarcoMapa(
                g2,
                metrica
        );

        pintarCoordenadas(
                g2,
                tablero,
                metrica
        );

        pintarMatriz(
                g2,
                tablero,
                metrica
        );
    }

    private void pintarFondo(
            Graphics2D g2,
            int ancho,
            int alto) {

        g2.setColor(
                TemaMars.FONDO_PROFUNDO
        );

        g2.fillRect(
                0,
                0,
                ancho,
                alto
        );

        if (rutaFondo != null
                && GestorImagenes.existe(
                        rutaFondo
                )) {

            ImageIcon fondo =
                    GestorImagenes.cargarCubriendo(
                            rutaFondo,
                            Math.max(
                                    1,
                                    ancho
                            ),
                            Math.max(
                                    1,
                                    alto
                            )
                    );

            g2.drawImage(
                    fondo.getImage(),
                    0,
                    0,
                    ancho,
                    alto,
                    null
            );
        }

        g2.setColor(
                VELO_FONDO
        );

        g2.fillRect(
                0,
                0,
                ancho,
                alto
        );
    }

    private MetricaMapa calcularMetrica(
            Tablero tablero,
            double zoom,
            int ancho,
            int alto) {

        Dimension viewport =
                obtenerDimensionViewport();

        double tamanoCasilla =
                calcularTamanoBase(
                        tablero,
                        viewport
                ) * zoom;

        double anchoMatriz =
                tamanoCasilla
                * tablero.getColumnas();

        double altoMatriz =
                tamanoCasilla
                * tablero.getFilas();

        double espacioX =
                Math.max(
                        1,
                        ancho
                        - MARGEN_IZQUIERDO
                        - MARGEN_DERECHO
                );

        double espacioY =
                Math.max(
                        1,
                        alto
                        - MARGEN_SUPERIOR
                        - MARGEN_INFERIOR
                );

        double origenX =
                MARGEN_IZQUIERDO
                + Math.max(
                        0,
                        (
                        espacioX
                        - anchoMatriz
                        ) / 2.0
                );

        double origenY =
                MARGEN_SUPERIOR
                + Math.max(
                        0,
                        (
                        espacioY
                        - altoMatriz
                        ) / 2.0
                );

        return new MetricaMapa(
                origenX,
                origenY,
                tamanoCasilla,
                anchoMatriz,
                altoMatriz
        );
    }

    private void pintarMarcoMapa(
            Graphics2D g2,
            MetricaMapa metrica) {

        double margen = 5;

        Rectangle2D.Double marco =
                new Rectangle2D.Double(
                        metrica.origenX - margen,
                        metrica.origenY - margen,
                        metrica.anchoMatriz + margen * 2,
                        metrica.altoMatriz + margen * 2
                );

        g2.setColor(
                new Color(
                        3,
                        15,
                        23,
                        65
                )
        );

        g2.fill(marco);

        g2.setColor(
                new Color(
                        111,
                        229,
                        235,
                        80
                )
        );

        g2.setStroke(
                new BasicStroke(1.5f)
        );

        g2.draw(marco);
    }

    private void pintarCoordenadas(
            Graphics2D g2,
            Tablero tablero,
            MetricaMapa metrica) {

        if (metrica.tamanoCasilla < 14) {
            return;
        }

        g2.setFont(
                TemaMars.textoPequeno()
        );

        g2.setColor(
                new Color(
                        247,
                        242,
                        233,
                        185
                )
        );

        for (int columna = 0;
                columna < tablero.getColumnas();
                columna++) {

            if (columna % 5 == 0
                    || columna
                    == tablero.getColumnas() - 1) {

                String texto =
                        String.valueOf(
                                columna
                        );

                int anchoTexto =
                        g2.getFontMetrics()
                                .stringWidth(
                                        texto
                                );

                double x =
                        metrica.origenX
                        + columna
                        * metrica.tamanoCasilla
                        + (
                        metrica.tamanoCasilla
                        - anchoTexto
                        ) / 2.0;

                g2.drawString(
                        texto,
                        (float) x,
                        (float) (
                                metrica.origenY - 9
                        )
                );
            }
        }

        for (int fila = 0;
                fila < tablero.getFilas();
                fila++) {

            if (fila % 5 == 0
                    || fila
                    == tablero.getFilas() - 1) {

                String texto =
                        String.valueOf(
                                fila
                        );

                int anchoTexto =
                        g2.getFontMetrics()
                                .stringWidth(
                                        texto
                                );

                double x =
                        metrica.origenX
                        - anchoTexto
                        - 9;

                double y =
                        metrica.origenY
                        + fila
                        * metrica.tamanoCasilla
                        + metrica.tamanoCasilla / 2.0
                        + g2.getFontMetrics()
                                .getAscent() / 2.0
                        - 2;

                g2.drawString(
                        texto,
                        (float) x,
                        (float) y
                );
            }
        }
    }

    private void pintarMatriz(
            Graphics2D g2,
            Tablero tablero,
            MetricaMapa metrica) {

        for (int fila = 0;
                fila < tablero.getFilas();
                fila++) {

            for (int columna = 0;
                    columna < tablero.getColumnas();
                    columna++) {

                Posicion posicion =
                        new Posicion(
                                fila,
                                columna
                        );

                pintarCasilla(
                        g2,
                        tablero,
                        posicion,
                        metrica
                );
            }
        }
    }

    private void pintarCasilla(
            Graphics2D g2,
            Tablero tablero,
            Posicion posicion,
            MetricaMapa metrica) {

        double x =
                metrica.origenX
                + posicion.getColumna()
                * metrica.tamanoCasilla;

        double y =
                metrica.origenY
                + posicion.getFila()
                * metrica.tamanoCasilla;

        OcupanteMapa ocupante =
                tablero.obtener(
                        posicion
                );

        Rectangle2D.Double rectangulo =
                new Rectangle2D.Double(
                        x,
                        y,
                        metrica.tamanoCasilla,
                        metrica.tamanoCasilla
                );

        g2.setColor(
                ocupante == null
                        ? CASILLA_LIBRE
                        : CASILLA_OCUPADA
        );

        g2.fill(rectangulo);

        boolean lineaPrincipal =
                posicion.getFila() % 5 == 0
                || posicion.getColumna() % 5 == 0;

        g2.setColor(
                lineaPrincipal
                        ? BORDE_PRINCIPAL
                        : BORDE_CASILLA
        );

        g2.setStroke(
                new BasicStroke(
                        lineaPrincipal
                                ? 1.25f
                                : 0.7f
                )
        );

        g2.draw(rectangulo);

        if (ocupante != null) {
            pintarOcupante(
                    g2,
                    ocupante,
                    x,
                    y,
                    metrica.tamanoCasilla
            );
        }

        if (modoColocacion
                && posicion.equals(
                        posicionHover
                )) {

            g2.setColor(
                    tablero.estaLibre(posicion)
                            ? HOVER_LIBRE
                            : HOVER_OCUPADO
            );

            g2.fill(
                    new Rectangle2D.Double(
                            x + 1,
                            y + 1,
                            Math.max(
                                    1,
                                    metrica.tamanoCasilla - 2
                            ),
                            Math.max(
                                    1,
                                    metrica.tamanoCasilla - 2
                            )
                    )
            );
        }

        if (seleccionada != null
                && seleccionada.equals(
                        posicion
                )) {

            g2.setColor(SELECCION);

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    2f,
                                    (float) (
                                            metrica.tamanoCasilla
                                            / 9.0
                                    )
                            )
                    )
            );

            g2.draw(
                    new Rectangle2D.Double(
                            x + 1,
                            y + 1,
                            Math.max(
                                    1,
                                    metrica.tamanoCasilla - 2
                            ),
                            Math.max(
                                    1,
                                    metrica.tamanoCasilla - 2
                            )
                    )
            );
        }
    }

    private void pintarOcupante(
            Graphics2D g2,
            OcupanteMapa ocupante,
            double x,
            double y,
            double tamano) {

        String ruta = null;

        if (ocupante instanceof Obstaculo obstaculo) {
            ruta =
                    obstaculo.getRutaImagen();

        } else if (ocupante instanceof ComponenteCombate componente) {
            ruta =
                    componente.getRutaImagenActual();
        }

        double margen =
                Math.max(
                        1,
                        tamano / 10.0
                );

        int destino =
                Math.max(
                        2,
                        (int) Math.round(
                                tamano
                                - margen * 2
                        )
                );

        int destinoX =
                (int) Math.round(
                        x + margen
                );

        int destinoY =
                (int) Math.round(
                        y + margen
                );

        if (ruta != null
                && !ruta.isBlank()) {

            ImageIcon icono =
                    GestorImagenes.cargar(
                            ruta,
                            TAMANO_ICONO_CACHE,
                            TAMANO_ICONO_CACHE
                    );

            g2.drawImage(
                    icono.getImage(),
                    destinoX,
                    destinoY,
                    destino,
                    destino,
                    null
            );

        } else if (ocupante instanceof NucleoOxigeno) {
            g2.setColor(
                    TemaMars.ACCION
            );

            int diametro =
                    Math.max(
                            2,
                            (int) Math.round(
                                    tamano
                                    - margen * 4
                            )
                    );

            g2.fillOval(
                    (int) Math.round(
                            x + margen * 2
                    ),
                    (int) Math.round(
                            y + margen * 2
                    ),
                    diametro,
                    diametro
            );

        } else {
            g2.setColor(
                    TemaMars.ENERGIA
            );

            int diametro =
                    Math.max(
                            2,
                            (int) Math.round(
                                    tamano
                                    - margen * 4
                            )
                    );

            g2.fillOval(
                    (int) Math.round(
                            x + margen * 2
                    ),
                    (int) Math.round(
                            y + margen * 2
                    ),
                    diametro,
                    diametro
            );
        }

        if (ocupante instanceof ComponenteCombate componente
                && tamano >= 13) {

            pintarVida(
                    g2,
                    componente,
                    x,
                    y,
                    tamano
            );
        }
    }

    private void pintarVida(
            Graphics2D g2,
            ComponenteCombate componente,
            double x,
            double y,
            double tamano) {

        double maximo =
                componente.getVidaMaxima();

        if (maximo <= 0) {
            return;
        }

        double porcentaje =
                componente.getVidaActual()
                / maximo;

        porcentaje =
                Math.max(
                        0,
                        Math.min(
                                1,
                                porcentaje
                        )
                );

        double margen =
                Math.max(
                        2,
                        tamano / 10
                );

        double anchoDisponible =
                tamano
                - margen * 2;

        double anchoVida =
                anchoDisponible
                * porcentaje;

        double alto =
                Math.max(
                        2,
                        tamano / 12
                );

        double yBarra =
                y
                + tamano
                - alto
                - 2;

        g2.setColor(
                new Color(
                        4,
                        13,
                        19,
                        190
                )
        );

        g2.fill(
                new Rectangle2D.Double(
                        x + margen,
                        yBarra,
                        anchoDisponible,
                        alto
                )
        );

        Color colorVida;

        if (porcentaje > 0.5) {
            colorVida =
                    TemaMars.CORRECTO;
        } else if (porcentaje > 0.25) {
            colorVida =
                    TemaMars.ACCION;
        } else {
            colorVida =
                    TemaMars.ERROR;
        }

        g2.setColor(
                colorVida
        );

        g2.fill(
                new Rectangle2D.Double(
                        x + margen,
                        yBarra,
                        anchoVida,
                        alto
                )
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
        return new Dimension(
                900,
                680
        );
    }

    @Override
    public int getScrollableUnitIncrement(
            Rectangle visibleRect,
            int orientation,
            int direction) {

        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(
            Rectangle visibleRect,
            int orientation,
            int direction) {

        if (orientation == SwingConstants.VERTICAL) {
            return Math.max(
                    50,
                    visibleRect.height - 50
            );
        }

        return Math.max(
                50,
                visibleRect.width - 50
        );
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        JViewport viewport =
                obtenerViewport();

        if (viewport == null) {
            return zoomActual <= 1.001;
        }

        return calcularDimensionContenido(
                obtenerZoomEstructural()
        ).width <= viewport.getExtentSize().width;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        JViewport viewport =
                obtenerViewport();

        if (viewport == null) {
            return zoomActual <= 1.001;
        }

        return calcularDimensionContenido(
                obtenerZoomEstructural()
        ).height <= viewport.getExtentSize().height;
    }

    private static class MetricaMapa {

        private final double origenX;
        private final double origenY;
        private final double tamanoCasilla;
        private final double anchoMatriz;
        private final double altoMatriz;

        private MetricaMapa(
                double origenX,
                double origenY,
                double tamanoCasilla,
                double anchoMatriz,
                double altoMatriz) {

            this.origenX = origenX;
            this.origenY = origenY;
            this.tamanoCasilla = tamanoCasilla;
            this.anchoMatriz = anchoMatriz;
            this.altoMatriz = altoMatriz;
        }
    }
}