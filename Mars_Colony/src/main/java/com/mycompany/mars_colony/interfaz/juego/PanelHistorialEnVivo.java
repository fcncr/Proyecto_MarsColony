package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.DialogoMars;
import com.mycompany.mars_colony.interfaz.comun.EtiquetaEstadoMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.modelo.registro.RegistroCombate;
import com.mycompany.mars_colony.modelo.registro.ResumenInteraccion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialEnVivo extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionVolverBatalla;

    private final PanelMapaMars mapa;

    private final JLabel etiquetaNucleo;
    private final JLabel etiquetaCriaturas;

    private final JLabel nombre;
    private final JLabel estado;
    private final JLabel vida;

    private final JLabel estadisticaVida;
    private final JLabel estadisticaDanio;
    private final JLabel estadisticaFrecuencia;
    private final JLabel estadisticaAlcance;
    private final JLabel estadisticaRadio;
    private final JLabel estadisticaPosicion;

    private final DefaultTableModel modeloObjetivos;
    private final DefaultTableModel modeloAtacantes;

    private ComponenteCombate seleccionado;

    public PanelHistorialEnVivo(ControladorJuego controlador, Runnable accionVolverBatalla) {
        super("HISTORIAL EN VIVO", "Cada golpe tiene un origen y un destino", "");

        this.controlador = controlador;
        this.accionVolverBatalla = accionVolverBatalla;

        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setOpaque(false);

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setOpaque(false);

        JPanel zonaMapa = new JPanel(new BorderLayout(0, 10));
        zonaMapa.setOpaque(false);

        JPanel resumen = new JPanel(new GridLayout(1, 2, 12, 0));
        resumen.setOpaque(false);
        resumen.setPreferredSize(new Dimension(100, 72));

        etiquetaNucleo = new JLabel("-");
        etiquetaCriaturas = new JLabel("-");

        resumen.add(crearResumen("NÚCLEO", etiquetaNucleo));
        resumen.add(crearResumen("CRIATURAS ACTIVAS", etiquetaCriaturas));

        mapa = new PanelMapaMars();
        mapa.setAccionCasilla(this::seleccionarCasilla);

        JScrollPane scrollMapa = new JScrollPane(mapa);
        scrollMapa.setBorder(null);
        scrollMapa.getViewport().setBackground(TemaMars.FONDO_PROFUNDO);

        zonaMapa.add(resumen, BorderLayout.NORTH);
        zonaMapa.add(scrollMapa, BorderLayout.CENTER);

        PanelMars detalle = new PanelMars();
        detalle.setPreferredSize(new Dimension(520, 100));
        detalle.setMinimumSize(new Dimension(450, 100));
        detalle.setLayout(new BorderLayout(0, 14));

        JPanel cabeceraDetalle = new JPanel();
        cabeceraDetalle.setOpaque(false);
        cabeceraDetalle.setLayout(new BoxLayout(cabeceraDetalle, BoxLayout.Y_AXIS));

        nombre = new JLabel("SELECCIONA UNA UNIDAD");
        nombre.setFont(TemaMars.tituloPantalla());
        nombre.setForeground(TemaMars.TEXTO);
        nombre.setAlignmentX(LEFT_ALIGNMENT);

        JPanel estadoVida = new JPanel(new BorderLayout(12, 0));
        estadoVida.setOpaque(false);
        estadoVida.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        estadoVida.setAlignmentX(LEFT_ALIGNMENT);

        estado = new JLabel("EN VIVO", SwingConstants.CENTER);
        estado.setFont(TemaMars.textoDestacado());
        estado.setOpaque(true);
        estado.setBackground(TemaMars.FONDO);
        estado.setForeground(TemaMars.CORRECTO);
        estado.setPreferredSize(new Dimension(135, 38));

        vida = new JLabel("-");
        vida.setFont(TemaMars.tituloPanel());
        vida.setForeground(TemaMars.TEXTO);

        estadoVida.add(estado, BorderLayout.WEST);
        estadoVida.add(vida, BorderLayout.CENTER);

        cabeceraDetalle.add(nombre);
        cabeceraDetalle.add(Box.createVerticalStrut(8));
        cabeceraDetalle.add(estadoVida);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(TemaMars.textoDestacado());

        JPanel panelEstadisticas = new JPanel(new GridLayout(6, 2, 8, 9));
        panelEstadisticas.setBackground(TemaMars.PANEL);

        estadisticaVida = crearValor("-");
        estadisticaDanio = crearValor("-");
        estadisticaFrecuencia = crearValor("-");
        estadisticaAlcance = crearValor("-");
        estadisticaRadio = crearValor("-");
        estadisticaPosicion = crearValor("-");

        panelEstadisticas.add(crearEtiqueta("Vida"));
        panelEstadisticas.add(estadisticaVida);
        panelEstadisticas.add(crearEtiqueta("Daño"));
        panelEstadisticas.add(estadisticaDanio);
        panelEstadisticas.add(crearEtiqueta("Frecuencia"));
        panelEstadisticas.add(estadisticaFrecuencia);
        panelEstadisticas.add(crearEtiqueta("Alcance"));
        panelEstadisticas.add(estadisticaAlcance);
        panelEstadisticas.add(crearEtiqueta("Radio"));
        panelEstadisticas.add(estadisticaRadio);
        panelEstadisticas.add(crearEtiqueta("Posición"));
        panelEstadisticas.add(estadisticaPosicion);

        modeloObjetivos = crearModelo();
        modeloAtacantes = crearModelo();

        JTable tablaObjetivos = crearTabla(modeloObjetivos);
        JTable tablaAtacantes = crearTabla(modeloAtacantes);

        pestañas.addTab("Estadísticas", panelEstadisticas);
        pestañas.addTab("Objetivos", new JScrollPane(tablaObjetivos));
        pestañas.addTab("Atacantes", new JScrollPane(tablaAtacantes));

        BotonMars participantes = BotonMars.secundario("TODOS LOS PARTICIPANTES");
        participantes.setFont(TemaMars.textoDestacado().deriveFont(15f));
        participantes.setPreferredSize(new Dimension(300, TemaMars.ALTURA_BOTON));
        participantes.setMinimumSize(new Dimension(300, TemaMars.ALTURA_BOTON));
        participantes.setMaximumSize(new Dimension(300, TemaMars.ALTURA_BOTON));
        participantes.addActionListener(e -> abrirParticipantes());
        participantes.addActionListener(e -> abrirParticipantes());

        detalle.add(cabeceraDetalle, BorderLayout.NORTH);
        detalle.add(pestañas, BorderLayout.CENTER);
        detalle.add(participantes, BorderLayout.SOUTH);

        centro.add(zonaMapa, BorderLayout.CENTER);
        centro.add(detalle, BorderLayout.EAST);

        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);

        JPanel observando = new JPanel();
        observando.setOpaque(false);
        observando.setLayout(new BoxLayout(observando, BoxLayout.X_AXIS));

        EtiquetaEstadoMars etiquetaObservando = EtiquetaEstadoMars.energia("OBSERVANDO LA BATALLA");

        JLabel mensaje = new JLabel("Las unidades actúan solas.");
        mensaje.setFont(TemaMars.textoNormal());
        mensaje.setForeground(TemaMars.TEXTO_CLARO);

        observando.add(etiquetaObservando);
        observando.add(Box.createHorizontalStrut(18));
        observando.add(mensaje);

        JPanel acciones = new JPanel(new GridLayout(1, 2, 12, 0));
        acciones.setOpaque(false);
        acciones.setPreferredSize(new Dimension(440, TemaMars.ALTURA_BOTON));

        BotonMars volver = BotonMars.secundario("Volver a batalla");
        volver.addActionListener(e -> accionVolverBatalla.run());

        BotonMars menu = BotonMars.secundario("Menú");
        menu.addActionListener(e -> DialogoMars.informacion(
                this,
                "Batalla en curso",
                "Consultar el historial no pausa el combate."
        ));

        acciones.add(volver);
        acciones.add(menu);

        pie.add(observando, BorderLayout.WEST);
        pie.add(acciones, BorderLayout.EAST);

        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);
    }

    private PanelMars crearResumen(String titulo, JLabel valor) {
        PanelMars panel = new PanelMars();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(TemaMars.TEXTO_SECUNDARIO);

        valor.setFont(TemaMars.textoDestacado());
        valor.setForeground(TemaMars.TEXTO);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(4));
        panel.add(valor);

        return panel;
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(new String[]{"Unidad", "Golpes", "Daño"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);
        return tabla;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TemaMars.textoSecundario());
        etiqueta.setForeground(TemaMars.TEXTO_SECUNDARIO);
        return etiqueta;
    }

    private JLabel crearValor(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TemaMars.textoDestacado());
        etiqueta.setForeground(TemaMars.TEXTO);
        return etiqueta;
    }

    public void mostrar(ComponenteCombate componente) {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
                + " · MISIÓN "
                + String.format("%02d", partida.getMisionActual())
        );

        if (componente == null) {
            List<ComponenteCombate> participantes = partida.getMision().getParticipantes();

            if (!participantes.isEmpty()) {
                componente = participantes.get(0);
            }
        }

        seleccionar(componente);
        actualizarEnVivo();
    }

    public void actualizarEnVivo() {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        mapa.actualizar(partida);

        NucleoOxigeno nucleo = buscarNucleo(partida.getMision());

        if (nucleo == null) {
            etiquetaNucleo.setText("-");
        } else {
            etiquetaNucleo.setText(
                    formatear(nucleo.getVidaActual())
                    + " / "
                    + formatear(nucleo.getVidaMaxima())
            );
        }

        int activas = 0;

        for (Criatura criatura : partida.getMision().getCriaturas()) {
            if (criatura != null && criatura.estaOperativo()) {
                activas++;
            }
        }

        etiquetaCriaturas.setText(activas + " ACTIVAS");

        if (seleccionado != null) {
            actualizarDetalle();
        }
    }

    private void seleccionarCasilla(Posicion posicion) {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getTablero() == null) {
            return;
        }

        OcupanteMapa ocupante = partida.getTablero().obtener(posicion);

        if (ocupante instanceof ComponenteCombate componente) {
            seleccionar(componente);
        }
    }

    private void seleccionar(ComponenteCombate componente) {
        seleccionado = componente;

        if (componente == null) {
            mapa.setSeleccionada(null);
            limpiarDetalle();
            return;
        }

        mapa.setSeleccionada(componente.getPosicion());
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        if (seleccionado == null) {
            limpiarDetalle();
            return;
        }

        nombre.setText(
                seleccionado.getNombre().toUpperCase()
                + " · "
                + abreviarId(seleccionado.getId())
        );

        if (seleccionado.estaOperativo()) {
            estado.setText("EN VIVO");
            estado.setForeground(TemaMars.CORRECTO);
        } else {
            estado.setText("DESTRUIDA");
            estado.setForeground(TemaMars.ERROR);
        }

        vida.setText(
                "Vida "
                + formatear(seleccionado.getVidaActual())
                + " / "
                + formatear(seleccionado.getVidaMaxima())
        );

        estadisticaVida.setText(
                formatear(seleccionado.getVidaActual())
                + " / "
                + formatear(seleccionado.getVidaMaxima())
        );

        estadisticaDanio.setText(formatear(seleccionado.getDanioGolpe()));
        estadisticaFrecuencia.setText(formatear(seleccionado.getFrecuenciaAtaque()));
        estadisticaAlcance.setText(String.valueOf(seleccionado.getAlcance()));
        estadisticaRadio.setText(String.valueOf(seleccionado.getRadioEfecto()));
        estadisticaPosicion.setText(
                seleccionado.getPosicion() == null
                ? "-"
                : seleccionado.getPosicion().toString()
        );

        actualizarInteracciones(
                modeloObjetivos,
                seleccionado.getRegistroCombate().getObjetivosAtacados()
        );

        actualizarInteracciones(
                modeloAtacantes,
                seleccionado.getRegistroCombate().getAtacantesRecibidos()
        );
    }

    private void actualizarInteracciones(DefaultTableModel modelo, Map<String, ResumenInteraccion> interacciones) {
        modelo.setRowCount(0);

        List<ResumenInteraccion> filas = new ArrayList<>(interacciones.values());

        filas.sort(
                Comparator.comparing(
                        ResumenInteraccion::getNombre,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        int golpesTotales = 0;
        double danioTotal = 0;

        for (ResumenInteraccion resumen : filas) {
            modelo.addRow(new Object[]{
                resumen.getNombre() + " · " + abreviarId(resumen.getIdUnidad()),
                resumen.getCantidadGolpes(),
                formatear(resumen.getDanioTotal())
            });

            golpesTotales += resumen.getCantidadGolpes();
            danioTotal += resumen.getDanioTotal();
        }

        modelo.addRow(new Object[]{
            "TOTAL",
            golpesTotales,
            formatear(danioTotal)
        });
    }

    private void limpiarDetalle() {
        nombre.setText("SELECCIONA UNA UNIDAD");
        estado.setText("-");
        estado.setForeground(TemaMars.TEXTO_SECUNDARIO);
        vida.setText("-");
        estadisticaVida.setText("-");
        estadisticaDanio.setText("-");
        estadisticaFrecuencia.setText("-");
        estadisticaAlcance.setText("-");
        estadisticaRadio.setText("-");
        estadisticaPosicion.setText("-");
        modeloObjetivos.setRowCount(0);
        modeloAtacantes.setRowCount(0);
    }

    private void abrirParticipantes() {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        DialogoParticipantesBatalla.mostrar(
                this,
                partida.getMision().getParticipantes(),
                seleccionado,
                this::seleccionar
        );
    }

    private NucleoOxigeno buscarNucleo(Mision mision) {
        for (ComponenteCombate participante : mision.getParticipantes()) {
            if (participante instanceof NucleoOxigeno nucleo) {
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

        return id.substring(0, 8).toUpperCase();
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##").format(valor);
    }
}