package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BarraVidaMars;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.EtiquetaEstadoMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PanelBatalla extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Consumer<ComponenteCombate> accionHistorial;
    private final Runnable accionMenu;

    private final PanelMapaMars mapa;

    private final JLabel etiquetaNucleo;
    private final JLabel etiquetaCriaturas;
    private final JLabel etiquetaEstado;

    private final JLabel imagen;
    private final JLabel nombre;
    private final JLabel identidad;
    private final JLabel estadoUnidad;
    private final BarraVidaMars barraVida;
    private final JLabel textoVida;
    private final JLabel danio;
    private final JLabel alcance;
    private final JLabel costo;

    private final BotonMars botonHistorial;

    private ComponenteCombate seleccionado;

    public PanelBatalla(ControladorJuego controlador, Consumer<ComponenteCombate> accionHistorial, Runnable accionMenu) {
        super("BATALLA AUTOMÁTICA", "", "");

        this.controlador = controlador;
        this.accionHistorial = accionHistorial;
        this.accionMenu = accionMenu;

        JPanel raiz = new JPanel(
                new BorderLayout(0, 12)
        );

        raiz.setOpaque(false);

        JPanel resumen = new JPanel(
                new GridLayout(1, 3, 12, 0)
        );

        resumen.setOpaque(false);

        resumen.setPreferredSize(
                new Dimension(100, 76)
        );

        etiquetaNucleo = new JLabel("-");
        etiquetaCriaturas = new JLabel("-");
        etiquetaEstado = new JLabel(
                "COMBATE AUTOMÁTICO"
        );

        resumen.add(
                crearResumen(
                        "NÚCLEO",
                        etiquetaNucleo
                )
        );

        resumen.add(
                crearResumen(
                        "CRIATURAS ACTIVAS",
                        etiquetaCriaturas
                )
        );

        resumen.add(
                crearResumen(
                        "ESTADO",
                        etiquetaEstado
                )
        );

        JPanel centro = new JPanel(
                new BorderLayout(14, 0)
        );

        centro.setOpaque(false);

        mapa = new PanelMapaMars();

        mapa.setAccionCasilla(
                this::seleccionarCasilla
        );

        JScrollPane scrollMapa =
                new JScrollPane(mapa);

        scrollMapa.setBorder(null);

        scrollMapa.getViewport().setBackground(
                TemaMars.FONDO_PROFUNDO
        );

        PanelMars inspector =
                new PanelMars();

        inspector.setPreferredSize(
                new Dimension(340, 100)
        );

        inspector.setMinimumSize(
                new Dimension(320, 100)
        );

        inspector.setLayout(
                new BoxLayout(
                        inspector,
                        BoxLayout.Y_AXIS
                )
        );

        nombre = new JLabel(
                "Selecciona una unidad"
        );

        nombre.setFont(
                TemaMars.tituloPanel()
        );

        nombre.setForeground(
                TemaMars.TEXTO
        );

        nombre.setAlignmentX(
                CENTER_ALIGNMENT
        );

        identidad = new JLabel("-");

        identidad.setFont(
                TemaMars.textoSecundario()
        );

        identidad.setForeground(
                TemaMars.ENERGIA
        );

        identidad.setAlignmentX(
                CENTER_ALIGNMENT
        );

        estadoUnidad = new JLabel("-");

        estadoUnidad.setFont(
                TemaMars.textoDestacado()
        );

        estadoUnidad.setForeground(
                TemaMars.CORRECTO
        );

        estadoUnidad.setAlignmentX(
                CENTER_ALIGNMENT
        );

        imagen = new JLabel();

        imagen.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        imagen.setAlignmentX(
                CENTER_ALIGNMENT
        );

        imagen.setMaximumSize(
                new Dimension(175, 175)
        );

        textoVida = new JLabel("-");

        textoVida.setFont(
                TemaMars.tituloPanel()
        );

        textoVida.setForeground(
                TemaMars.TEXTO
        );

        textoVida.setAlignmentX(
                CENTER_ALIGNMENT
        );

        barraVida = new BarraVidaMars();

        barraVida.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        28
                )
        );

        danio = crearValor("-");
        alcance = crearValor("-");
        costo = crearValor("-");

        JPanel datos = new JPanel(
                new GridLayout(3, 2, 8, 7)
        );

        datos.setOpaque(false);

        datos.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        115
                )
        );

        datos.add(
                crearEtiqueta("Daño por golpe")
        );

        datos.add(danio);

        datos.add(
                crearEtiqueta("Alcance")
        );

        datos.add(alcance);

        datos.add(
                crearEtiqueta("Costo")
        );

        datos.add(costo);

        botonHistorial =
                BotonMars.primario(
                        "VER HISTORIAL"
                );

        botonHistorial.setFont(
                TemaMars.textoDestacado()
                        .deriveFont(16f)
        );

        botonHistorial.setPreferredSize(
                new Dimension(
                        250,
                        TemaMars.ALTURA_BOTON
                )
        );

        botonHistorial.setMinimumSize(
                new Dimension(
                        250,
                        TemaMars.ALTURA_BOTON
                )
        );

        botonHistorial.setMaximumSize(
                new Dimension(
                        250,
                        TemaMars.ALTURA_BOTON
                )
        );

        botonHistorial.setAlignmentX(
                CENTER_ALIGNMENT
        );

        botonHistorial.setEnabled(false);

        botonHistorial.addActionListener(e -> {
            if (seleccionado != null) {
                accionHistorial.accept(
                        seleccionado
                );
            }
        });

        JLabel pista = new JLabel(
                "Selecciona una unidad para consultar."
        );

        pista.setFont(
                TemaMars.textoSecundario()
        );

        pista.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        pista.setAlignmentX(
                CENTER_ALIGNMENT
        );

        inspector.add(nombre);
        inspector.add(Box.createVerticalStrut(5));
        inspector.add(identidad);
        inspector.add(Box.createVerticalStrut(5));
        inspector.add(estadoUnidad);
        inspector.add(Box.createVerticalStrut(10));
        inspector.add(imagen);
        inspector.add(Box.createVerticalStrut(8));
        inspector.add(textoVida);
        inspector.add(Box.createVerticalStrut(4));
        inspector.add(barraVida);
        inspector.add(Box.createVerticalStrut(15));
        inspector.add(datos);
        inspector.add(Box.createVerticalGlue());
        inspector.add(botonHistorial);
        inspector.add(Box.createVerticalStrut(8));
        inspector.add(pista);

        centro.add(
                scrollMapa,
                BorderLayout.CENTER
        );

        centro.add(
                inspector,
                BorderLayout.EAST
        );

        JPanel pie = new JPanel(
                new BorderLayout(12, 0)
        );

        pie.setOpaque(false);

        JPanel observando = new JPanel();
        observando.setOpaque(false);

        observando.setLayout(
                new BoxLayout(
                        observando,
                        BoxLayout.X_AXIS
                )
        );

        EtiquetaEstadoMars etiquetaObservando =
                EtiquetaEstadoMars.energia(
                        "OBSERVANDO LA BATALLA"
                );

        observando.add(
                etiquetaObservando
        );

        JPanel acciones = new JPanel(
                new GridLayout(1, 2, 12, 0)
        );

        acciones.setOpaque(false);

        acciones.setPreferredSize(
                new Dimension(
                        440,
                        TemaMars.ALTURA_BOTON
                )
        );

        BotonMars participantes =
                BotonMars.secundario(
                        "Participantes"
                );

        participantes.addActionListener(
                e -> abrirParticipantes()
        );

        BotonMars menu =
                BotonMars.secundario("Menú");

        menu.addActionListener(
                e -> accionMenu.run()
        );

        acciones.add(participantes);
        acciones.add(menu);

        pie.add(
                observando,
                BorderLayout.WEST
        );

        pie.add(
                acciones,
                BorderLayout.EAST
        );

        raiz.add(
                resumen,
                BorderLayout.NORTH
        );

        raiz.add(
                centro,
                BorderLayout.CENTER
        );

        raiz.add(
                pie,
                BorderLayout.SOUTH
        );

        setContenido(raiz);

        limpiarInspector();
    }

    private PanelMars crearResumen(String titulo, JLabel valor) {
        PanelMars panel =
                new PanelMars();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiqueta =
                new JLabel(titulo);

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        valor.setFont(
                TemaMars.textoDestacado()
        );

        valor.setForeground(
                TemaMars.TEXTO
        );

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valor);

        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta =
                new JLabel(texto);

        etiqueta.setFont(
                TemaMars.textoSecundario()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        return etiqueta;
    }

    private JLabel crearValor(String texto) {
        JLabel etiqueta =
                new JLabel(
                        texto,
                        SwingConstants.RIGHT
                );

        etiqueta.setFont(
                TemaMars.textoDestacado()
        );

        etiqueta.setForeground(
                TemaMars.TEXTO
        );

        return etiqueta;
    }

    public void preparar() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null) {
            return;
        }

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante()
                        .toUpperCase()
                + " · MISIÓN "
                + String.format(
                        "%02d",
                        partida.getMisionActual()
                )
        );

        mapa.actualizar(partida);

        if (seleccionado == null) {
            limpiarInspector();
        } else {
            actualizarInspector();
        }

        actualizarEnVivo();
    }

    public void actualizarEnVivo() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            return;
        }

        mapa.actualizar(partida);

        NucleoOxigeno nucleo =
                buscarNucleo(
                        partida.getMision()
                );

        if (nucleo == null) {
            etiquetaNucleo.setText(
                    "NO DISPONIBLE"
            );
        } else {
            etiquetaNucleo.setText(
                    formatear(
                            nucleo.getVidaActual()
                    )
                    + " / "
                    + formatear(
                            nucleo.getVidaMaxima()
                    )
            );
        }

        int activas = 0;

        for (Criatura criatura
                : partida.getMision()
                        .getCriaturas()) {

            if (criatura != null
                    && criatura.estaOperativo()) {

                activas++;
            }
        }

        etiquetaCriaturas.setText(
                activas + " ACTIVAS"
        );

        switch (partida.getMision().getEstado()) {
            case EN_CURSO ->
                etiquetaEstado.setText(
                        "COMBATE AUTOMÁTICO"
                );

            case VICTORIA ->
                etiquetaEstado.setText(
                        "VICTORIA"
                );

            case DERROTA ->
                etiquetaEstado.setText(
                        "DERROTA"
                );

            default ->
                etiquetaEstado.setText(
                        partida.getMision()
                                .getEstado()
                                .name()
                );
        }

        if (seleccionado != null) {
            actualizarInspector();
        }

        repaint();
    }

    public ComponenteCombate getSeleccionado() {
        return seleccionado;
    }

    public void seleccionar(ComponenteCombate componente) {
        seleccionado = componente;

        if (componente == null) {
            mapa.setSeleccionada(null);
            limpiarInspector();
            return;
        }

        mapa.setSeleccionada(
                componente.getPosicion()
        );

        actualizarInspector();
    }

    private void seleccionarCasilla(Posicion posicion) {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getTablero() == null) {

            return;
        }

        OcupanteMapa ocupante =
                partida.getTablero()
                        .obtener(posicion);

        if (ocupante
                instanceof ComponenteCombate componente) {

            seleccionar(componente);
        } else {
            seleccionado = null;
            mapa.setSeleccionada(posicion);
            limpiarInspector();
        }
    }

    private void actualizarInspector() {
        if (seleccionado == null) {
            limpiarInspector();
            return;
        }

        String ruta =
                seleccionado.getRutaImagenActual();

        if (ruta == null || ruta.isBlank()) {
            imagen.setIcon(null);
        } else {
            imagen.setIcon(
                    GestorImagenes.cargar(
                            ruta,
                            170,
                            170
                    )
            );
        }

        nombre.setText(
                seleccionado.getNombre()
        );

        identidad.setText(
                "NIVEL "
                + seleccionado.getNivel()
                + " · "
                + abreviarId(
                        seleccionado.getId()
                )
        );

        if (seleccionado.estaDestruido()) {
            estadoUnidad.setText(
                    "DESTRUIDA"
            );

            estadoUnidad.setForeground(
                    TemaMars.ERROR
            );
        } else {
            estadoUnidad.setText(
                    seleccionado
                            .getEstadoVisual()
                            .name()
                            .replace('_', ' ')
            );

            estadoUnidad.setForeground(
                    TemaMars.CORRECTO
            );
        }

        textoVida.setText(
                formatear(
                        seleccionado.getVidaActual()
                )
                + " / "
                + formatear(
                        seleccionado.getVidaMaxima()
                )
        );

        barraVida.setVida(
                seleccionado.getVidaActual(),
                seleccionado.getVidaMaxima()
        );

        danio.setText(
                formatear(
                        seleccionado.getDanioGolpe()
                )
        );

        alcance.setText(
                seleccionado.getAlcance()
                + " casillas"
        );

        costo.setText(
                seleccionado.getCostoCapacidad()
                + " puntos"
        );

        botonHistorial.setEnabled(true);
    }

    private void limpiarInspector() {
        imagen.setIcon(null);

        nombre.setText(
                "Selecciona una unidad"
        );

        identidad.setText("-");
        estadoUnidad.setText("-");

        estadoUnidad.setForeground(
                TemaMars.TEXTO_SECUNDARIO
        );

        textoVida.setText("-");

        barraVida.setVida(0, 1);

        danio.setText("-");
        alcance.setText("-");
        costo.setText("-");

        botonHistorial.setEnabled(false);
    }

    private void abrirParticipantes() {
        Partida partida =
                controlador.consultarEstado();

        if (partida == null
                || partida.getMision() == null) {

            return;
        }

        List<ComponenteCombate> participantes =
                partida.getMision()
                        .getParticipantes();

        DialogoParticipantesBatalla.mostrar(
                this,
                participantes,
                seleccionado,
                this::seleccionar
        );
    }

    private NucleoOxigeno buscarNucleo(Mision mision) {
        for (ComponenteCombate componente
                : mision.getParticipantes()) {

            if (componente
                    instanceof NucleoOxigeno nucleo) {

                return nucleo;
            }
        }

        return null;
    }

    private String abreviarId(String id) {
        if (id == null) {
            return "-";
        }

        if (id.length() <= 8) {
            return id;
        }

        return id.substring(0, 8)
                .toUpperCase();
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##")
                .format(valor);
    }
}