package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PanelMejoras extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionPreparar;

    private final JLabel etiquetaMision;
    private final JLabel etiquetaCapacidad;
    private final DefaultTableModel modelo;
    private final JLabel etiquetaDesbloqueo;
    private final BotonMars botonPreparar;

    public PanelMejoras(ControladorJuego controlador, Runnable accionPreparar) {
        super("TU COLONIA MEJORA", "Al avanzar se muestran los cambios de cada unidad", "");

        this.controlador = controlador;
        this.accionPreparar = accionPreparar;

        JPanel escenario = new JPanel(new BorderLayout());
        escenario.setOpaque(false);

        PanelMars tarjeta = new PanelMars();
        tarjeta.setLayout(new BorderLayout(0, 16));

        JPanel cabecera = new JPanel(new BorderLayout(20, 0));
        cabecera.setBackground(TemaMars.FONDO);

        JLabel nucleo = new JLabel(
                GestorImagenes.cargar(
                        "assets/importados/Nucleo.png",
                        105,
                        105
                )
        );

        nucleo.setPreferredSize(new Dimension(125, 115));
        nucleo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        etiquetaMision = new JLabel("Misión superada");
        etiquetaMision.setFont(TemaMars.tituloPantalla());
        etiquetaMision.setForeground(TemaMars.TEXTO_CLARO);

        JLabel capacidadTitulo = new JLabel(
                "CAPACIDAD DEL ESCUADRÓN"
        );

        capacidadTitulo.setFont(TemaMars.textoSecundario());
        capacidadTitulo.setForeground(
                new java.awt.Color(190, 205, 209)
        );

        textos.add(Box.createVerticalGlue());
        textos.add(etiquetaMision);
        textos.add(Box.createVerticalStrut(10));
        textos.add(capacidadTitulo);
        textos.add(Box.createVerticalGlue());

        etiquetaCapacidad = new JLabel("-");
        etiquetaCapacidad.setFont(
                TemaMars.tituloGrande().deriveFont(40f)
        );
        etiquetaCapacidad.setForeground(TemaMars.ACCION);
        etiquetaCapacidad.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        etiquetaCapacidad.setPreferredSize(
                new Dimension(290, 100)
        );

        cabecera.add(nucleo, BorderLayout.WEST);
        cabecera.add(textos, BorderLayout.CENTER);
        cabecera.add(etiquetaCapacidad, BorderLayout.EAST);

        modelo = new DefaultTableModel(
                new String[]{
                    "Unidad",
                    "Vida máxima",
                    "Daño por golpe",
                    "Incrementos"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(42);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);

        JPanel inferior = new JPanel(new BorderLayout(20, 0));
        inferior.setOpaque(false);

        etiquetaDesbloqueo = new JLabel("-");
        etiquetaDesbloqueo.setFont(TemaMars.textoNormal());
        etiquetaDesbloqueo.setForeground(TemaMars.TEXTO);

        botonPreparar = BotonMars.primario("PREPARAR MISIÓN");
        botonPreparar.setPreferredSize(
                new Dimension(320, TemaMars.ALTURA_BOTON)
        );
        botonPreparar.addActionListener(e -> accionPreparar.run());

        inferior.add(etiquetaDesbloqueo, BorderLayout.CENTER);
        inferior.add(botonPreparar, BorderLayout.EAST);

        tarjeta.add(cabecera, BorderLayout.NORTH);
        tarjeta.add(scroll, BorderLayout.CENTER);
        tarjeta.add(inferior, BorderLayout.SOUTH);

        escenario.add(tarjeta, BorderLayout.CENTER);

        setContenido(escenario);
    }

    public void preparar(int misionSuperada, int capacidadAntes, int capacidadDespues) {
        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getMision() == null) {
            return;
        }

        int nuevaMision = partida.getMisionActual();

        setContexto(
                "COMANDANTE "
                + partida.getNombreComandante().toUpperCase()
        );

        etiquetaMision.setText(
                "Misión "
                + String.format("%02d", misionSuperada)
                + " superada"
        );

        etiquetaCapacidad.setText(
                capacidadAntes
                + "  →  "
                + capacidadDespues
        );

        botonPreparar.setText(
                "PREPARAR MISIÓN "
                + String.format("%02d", nuevaMision)
        );

        cargarCrecimientos(nuevaMision);
        cargarDesbloqueos(nuevaMision);
    }

    private void cargarCrecimientos(int nuevaMision) {
        modelo.setRowCount(0);

        Partida partida = controlador.consultarEstado();
        List<ComponenteCombate> componentes = new ArrayList<>();

        for (Defensa defensa : partida.getEscuadron().getDefensas()) {
            if (defensa != null) {
                componentes.add(defensa);
            }
        }

        for (Criatura criatura : partida.getMision().getCriaturas()) {
            if (criatura != null) {
                componentes.add(criatura);
            }
        }

        for (ComponenteCombate componente : componentes) {
            RegistroCrecimiento crecimiento =
                    buscarCrecimiento(
                            componente,
                            nuevaMision
                    );

            if (crecimiento == null) {
                continue;
            }

            modelo.addRow(new Object[]{
                componente.getNombre()
                + " "
                + abreviarId(componente.getId()),

                formatear(crecimiento.getVidaAnterior())
                + " → "
                + formatear(crecimiento.getVidaNueva()),

                formatear(crecimiento.getDanioAnterior())
                + " → "
                + formatear(crecimiento.getDanioNuevo()),

                "Vida +"
                + porcentaje(crecimiento.getPorcentajeVida())
                + " · Daño +"
                + porcentaje(crecimiento.getPorcentajeDanio())
            });
        }
    }

    private RegistroCrecimiento buscarCrecimiento(ComponenteCombate componente, int mision) {
        for (RegistroCrecimiento crecimiento : componente.getCrecimientos()) {
            if (crecimiento.getNumeroMision() == mision) {
                return crecimiento;
            }
        }

        return null;
    }

    private void cargarDesbloqueos(int mision) {
        Partida partida = controlador.consultarEstado();

        if (!partida.tieneCatalogoSnapshot()) {
            etiquetaDesbloqueo.setText(
                    "Sin nuevos desbloqueos."
            );
            return;
        }

        List<String> desbloqueos = new ArrayList<>();

        for (ConfiguracionComponente configuracion
                : partida.getCatalogoSnapshot().listar()) {

            if (configuracion != null
                    && configuracion.isActivo()
                    && configuracion.getMisionMinima() == mision
                    && esDefensa(configuracion.getTipo())) {

                desbloqueos.add(configuracion.getNombre());
            }
        }

        if (desbloqueos.isEmpty()) {
            etiquetaDesbloqueo.setText(
                    "Sin nuevos desbloqueos en esta misión."
            );
        } else {
            etiquetaDesbloqueo.setText(
                    "<html><b>Nuevo desbloqueo:</b> "
                    + String.join(", ", desbloqueos)
                    + "<br>Disponible desde la misión "
                    + String.format("%02d", mision)
                    + ".</html>"
            );
        }
    }

    private boolean esDefensa(TipoComponente tipo) {
        return tipo == TipoComponente.DEFENSA_CONTACTO
                || tipo == TipoComponente.DEFENSA_ALCANCE
                || tipo == TipoComponente.DRON
                || tipo == TipoComponente.DEFENSA_IMPACTO
                || tipo == TipoComponente.DEFENSA_MULTIPLE
                || tipo == TipoComponente.BARRERA;
    }

    private String abreviarId(String id) {
        if (id == null) {
            return "";
        }

        return "#"
                + id.substring(
                        0,
                        Math.min(4, id.length())
                ).toUpperCase();
    }

    private String porcentaje(double valor) {
        return new DecimalFormat("0.#").format(valor * 100)
                + "%";
    }

    private String formatear(double valor) {
        return new DecimalFormat("0.##").format(valor);
    }
}