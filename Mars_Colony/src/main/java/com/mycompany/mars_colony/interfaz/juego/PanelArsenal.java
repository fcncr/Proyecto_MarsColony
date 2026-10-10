package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.juego.ControladorJuego;
import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.GestorImagenes;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class PanelArsenal extends PanelPantallaJuego {

    private final ControladorJuego controlador;
    private final Runnable accionVolver;
    private final Consumer<ConfiguracionComponente> accionElegirNueva;
    private final Consumer<Defensa> accionColocarExistente;
    private final Consumer<ConfiguracionComponente> accionFichaConfiguracion;
    private final Consumer<ComponenteCombate> accionFichaComponente;

    private final JLabel etiquetaCapacidad;
    private final JComboBox<String> filtro;
    private final JPanel panelNuevas;
    private final JPanel panelUnidades;

    public PanelArsenal(ControladorJuego controlador, Runnable accionVolver, Consumer<ConfiguracionComponente> accionElegirNueva, Consumer<Defensa> accionColocarExistente, Consumer<ConfiguracionComponente> accionFichaConfiguracion, Consumer<ComponenteCombate> accionFichaComponente) {
        super("ARSENAL", "Elige qué defensas llevarás al perímetro", "");

        this.controlador = controlador;
        this.accionVolver = accionVolver;
        this.accionElegirNueva = accionElegirNueva;
        this.accionColocarExistente = accionColocarExistente;
        this.accionFichaConfiguracion = accionFichaConfiguracion;
        this.accionFichaComponente = accionFichaComponente;

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setOpaque(false);

        JPanel superior = new JPanel(new BorderLayout(12, 0));
        superior.setOpaque(false);

        filtro = new JComboBox<>(new String[]{"Todas", "Contacto", "Alcance", "Dron", "Impacto", "Múltiple", "Barrera"});
        filtro.setFont(TemaMars.textoNormal());
        filtro.setPreferredSize(new Dimension(190, 44));
        filtro.addActionListener(e -> renderizar());

        etiquetaCapacidad = new JLabel("Capacidad libre: -", SwingConstants.CENTER);
        etiquetaCapacidad.setFont(TemaMars.textoDestacado());
        etiquetaCapacidad.setForeground(TemaMars.FONDO);
        etiquetaCapacidad.setOpaque(true);
        etiquetaCapacidad.setBackground(TemaMars.ENERGIA);
        etiquetaCapacidad.setPreferredSize(new Dimension(230, 44));

        superior.add(filtro, BorderLayout.WEST);
        superior.add(etiquetaCapacidad, BorderLayout.EAST);

        panelNuevas = new JPanel(new GridLayout(0, 3, 16, 16));
        panelNuevas.setOpaque(false);

        panelUnidades = new JPanel(new GridLayout(0, 3, 16, 16));
        panelUnidades.setOpaque(false);

        JScrollPane scrollNuevas = new JScrollPane(panelNuevas);
        scrollNuevas.setBorder(null);
        scrollNuevas.getViewport().setOpaque(false);
        scrollNuevas.setOpaque(false);
        scrollNuevas.getVerticalScrollBar().setUnitIncrement(18);

        JScrollPane scrollUnidades = new JScrollPane(panelUnidades);
        scrollUnidades.setBorder(null);
        scrollUnidades.getViewport().setOpaque(false);
        scrollUnidades.setOpaque(false);
        scrollUnidades.getVerticalScrollBar().setUnitIncrement(18);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TemaMars.textoDestacado());
        tabs.addTab("Nuevas defensas", scrollNuevas);
        tabs.addTab("Mis unidades", scrollUnidades);

        BotonMars volver = BotonMars.secundario("‹ Volver al perímetro");
        volver.setPreferredSize(new Dimension(220, TemaMars.ALTURA_BOTON));
        volver.addActionListener(e -> accionVolver.run());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pie.setOpaque(false);
        pie.add(volver);

        raiz.add(superior, BorderLayout.NORTH);
        raiz.add(tabs, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContenido(raiz);
    }

    public void actualizar() {
        Partida partida = controlador.consultarEstado();

        if (partida == null) {
            return;
        }

        setContexto("COMANDANTE " + partida.getNombreComandante().toUpperCase());

        etiquetaCapacidad.setText("Capacidad libre: " + partida.getEscuadron().capacidadRestante());

        renderizar();
    }

    private void renderizar() {
        renderizarNuevas();
        renderizarUnidades();
    }

    private void renderizarNuevas() {
        panelNuevas.removeAll();

        Partida partida = controlador.consultarEstado();

        if (partida == null || !partida.tieneCatalogoSnapshot()) {
            panelNuevas.revalidate();
            panelNuevas.repaint();
            return;
        }

        CatalogoComponentes catalogo = partida.getCatalogoSnapshot();

        for (ConfiguracionComponente configuracion : catalogo.listar()) {
            if (!configuracion.isActivo() || !esDefensa(configuracion.getTipo()) || !coincideFiltro(configuracion.getTipo())) {
                continue;
            }

            panelNuevas.add(crearTarjetaConfiguracion(configuracion));
        }

        panelNuevas.revalidate();
        panelNuevas.repaint();
    }

    private void renderizarUnidades() {
        panelUnidades.removeAll();

        Partida partida = controlador.consultarEstado();

        if (partida == null || partida.getEscuadron() == null) {
            return;
        }

        for (Defensa defensa : partida.getEscuadron().getDefensas()) {
            if (defensa == null || !coincideFiltro(obtenerTipo(defensa))) {
                continue;
            }

            panelUnidades.add(crearTarjetaDefensa(defensa));
        }

        panelUnidades.revalidate();
        panelUnidades.repaint();
    }

    private PanelMars crearTarjetaConfiguracion(ConfiguracionComponente configuracion) {
        PanelMars tarjeta = new PanelMars();
        tarjeta.setPreferredSize(new Dimension(280, 350));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel imagen = new JLabel(GestorImagenes.cargar(configuracion.getImagenes().getNormal(), 145, 145));
        imagen.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nombre = new JLabel(configuracion.getNombre());
        nombre.setFont(TemaMars.tituloPanel());
        nombre.setForeground(TemaMars.TEXTO);
        nombre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tipo = new JLabel(nombreTipo(configuracion.getTipo()));
        tipo.setFont(TemaMars.textoSecundario());
        tipo.setForeground(TemaMars.TEXTO_SECUNDARIO);
        tipo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel costo = new JLabel("Costo " + configuracion.getBase().getCostoCapacidad() + " puntos");
        costo.setFont(TemaMars.textoDestacado());
        costo.setForeground(TemaMars.TEXTO);
        costo.setAlignmentX(CENTER_ALIGNMENT);

        JPanel acciones = new JPanel(new GridLayout(1, 2, 8, 0));
        acciones.setOpaque(false);
        acciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        BotonMars ficha = BotonMars.secundario("Ver ficha");
        ficha.addActionListener(e -> accionFichaConfiguracion.accept(configuracion));

        int mision = controlador.consultarEstado().getMisionActual();
        int restante = controlador.consultarEstado().getEscuadron().capacidadRestante();
        int costoValor = configuracion.getBase().getCostoCapacidad();

        BotonMars elegir;

        if (configuracion.getMisionMinima() > mision) {
            elegir = BotonMars.bloqueado("Misión " + configuracion.getMisionMinima());
        } else if (costoValor > restante) {
            elegir = BotonMars.bloqueado("Faltan " + (costoValor - restante));
        } else {
            elegir = BotonMars.primario("Elegir");
            elegir.addActionListener(e -> accionElegirNueva.accept(configuracion));
        }

        acciones.add(ficha);
        acciones.add(elegir);

        tarjeta.add(imagen);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(nombre);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(tipo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(costo);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(acciones);

        return tarjeta;
    }

    private PanelMars crearTarjetaDefensa(Defensa defensa) {
        PanelMars tarjeta = new PanelMars();
        tarjeta.setPreferredSize(new Dimension(280, 350));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel imagen = new JLabel(GestorImagenes.cargar(defensa.getRutaImagenActual(), 145, 145));
        imagen.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nombre = new JLabel(defensa.getNombre());
        nombre.setFont(TemaMars.tituloPanel());
        nombre.setForeground(TemaMars.TEXTO);
        nombre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nivel = new JLabel("Nivel " + defensa.getNivel());
        nivel.setFont(TemaMars.textoSecundario());
        nivel.setForeground(TemaMars.TEXTO_SECUNDARIO);
        nivel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel estado = new JLabel(defensa.getPosicion() == null ? "Disponible" : "En mapa " + defensa.getPosicion());
        estado.setFont(TemaMars.textoDestacado());
        estado.setForeground(defensa.getPosicion() == null ? TemaMars.CORRECTO : TemaMars.ENERGIA);
        estado.setAlignmentX(CENTER_ALIGNMENT);

        JPanel acciones = new JPanel(new GridLayout(1, 2, 8, 0));
        acciones.setOpaque(false);
        acciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        BotonMars ficha = BotonMars.secundario("Ver ficha");
        ficha.addActionListener(e -> accionFichaComponente.accept(defensa));

        BotonMars accion;

        if (defensa.getPosicion() == null) {
            if (defensa.getCostoCapacidad() <= controlador.consultarEstado().getEscuadron().capacidadRestante()) {
                accion = BotonMars.primario("Colocar");
                accion.addActionListener(e -> accionColocarExistente.accept(defensa));
            } else {
                accion = BotonMars.bloqueado("Sin capacidad");
            }
        } else {
            accion = BotonMars.bloqueado("Ya colocada");
        }

        acciones.add(ficha);
        acciones.add(accion);

        tarjeta.add(imagen);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(nombre);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(nivel);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(estado);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(acciones);

        return tarjeta;
    }

    private TipoComponente obtenerTipo(Defensa defensa) {
        Partida partida = controlador.consultarEstado();

        if (partida == null || !partida.tieneCatalogoSnapshot()) {
            return null;
        }

        ConfiguracionComponente configuracion = partida.getCatalogoSnapshot().consultar(defensa.getIdConfiguracion());

        return configuracion == null ? null : configuracion.getTipo();
    }

    private boolean coincideFiltro(TipoComponente tipo) {
        String seleccionado = String.valueOf(filtro.getSelectedItem());

        if ("Todas".equals(seleccionado)) {
            return true;
        }

        if (tipo == null) {
            return false;
        }

        return switch (seleccionado) {
            case "Contacto" -> tipo == TipoComponente.DEFENSA_CONTACTO;
            case "Alcance" -> tipo == TipoComponente.DEFENSA_ALCANCE;
            case "Dron" -> tipo == TipoComponente.DRON;
            case "Impacto" -> tipo == TipoComponente.DEFENSA_IMPACTO;
            case "Múltiple" -> tipo == TipoComponente.DEFENSA_MULTIPLE;
            case "Barrera" -> tipo == TipoComponente.BARRERA;
            default -> true;
        };
    }

    private boolean esDefensa(TipoComponente tipo) {
        return tipo == TipoComponente.DEFENSA_CONTACTO
                || tipo == TipoComponente.DEFENSA_ALCANCE
                || tipo == TipoComponente.DRON
                || tipo == TipoComponente.DEFENSA_IMPACTO
                || tipo == TipoComponente.DEFENSA_MULTIPLE
                || tipo == TipoComponente.BARRERA;
    }

    private String nombreTipo(TipoComponente tipo) {
        if (tipo == null) {
            return "-";
        }

        return switch (tipo) {
            case DEFENSA_CONTACTO -> "Contacto";
            case DEFENSA_ALCANCE -> "Alcance medio";
            case DRON -> "Dron";
            case DEFENSA_IMPACTO -> "Impacto";
            case DEFENSA_MULTIPLE -> "Ataque múltiple";
            case BARRERA -> "Barrera";
            default -> tipo.name();
        };
    }
}