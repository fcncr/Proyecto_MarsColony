package com.mycompany.mars_colony.modelo.combate;

import com.mycompany.mars_colony.modelo.estado.Bando;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.EstadoVisual;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.registro.RegistroCombate;
import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ComponenteCombate implements OcupanteMapa, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String idConfiguracion;
    private String nombre;

    private EstadisticasCombate estadisticas;
    private ImagenesEstado imagenes;

    private double vidaActual;
    private int nivel;
    private int misionMinima;
    private int misionEscaladaHasta;

    private Posicion posicion;
    private Posicion posicionInicial;

    private EstadoVisual estadoVisual;

    private long restanteAtaqueMs;
    private long restanteMovimientoMs;

    private RegistroCombate registro;
    private List<RegistroCrecimiento> crecimientos;

    public ComponenteCombate(String idConfiguracion, String nombre, EstadisticasCombate estadisticas, ImagenesEstado imagenes, int misionMinima, Posicion posicion) {
        if (estadisticas == null) {
            throw new IllegalArgumentException("Las estadisticas no pueden ser null.");
        }

        this.id = UUID.randomUUID().toString();
        this.idConfiguracion = idConfiguracion;
        this.nombre = nombre;
        this.estadisticas = estadisticas.copiar();
        this.imagenes = imagenes;
        this.vidaActual = this.estadisticas.getVidaMaxima();
        this.nivel = 1;
        this.misionMinima = misionMinima;
        this.misionEscaladaHasta = misionMinima;
        this.posicion = posicion;
        this.posicionInicial = posicion;
        this.estadoVisual = EstadoVisual.NORMAL;
        this.restanteAtaqueMs = 0;
        this.restanteMovimientoMs = 0;
        this.registro = new RegistroCombate();
        this.crecimientos = new ArrayList<>();
    }

    public ComponenteCombate(String idConfiguracion, String nombre, double vidaInicial, int nivel, int misionAparicion, double danio, double frecuenciaAtaque, int alcance, int radioEfecto, int costoCapacidad, Posicion posicion) {
        this(idConfiguracion, nombre, new EstadisticasCombate(vidaInicial, danio, frecuenciaAtaque, alcance, radioEfecto, costoCapacidad, false, 1, 1, 1000), new ImagenesEstado(null, null, null), misionAparicion, posicion);
        this.nivel = nivel;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getIdConfiguracion() {
        return idConfiguracion;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadisticasCombate getEstadisticas() {
        return estadisticas;
    }

    public ImagenesEstado getImagenes() {
        return imagenes;
    }

    public double getVidaActual() {
        return vidaActual;
    }

    public double getVidaMaxima() {
        return estadisticas.getVidaMaxima();
    }

    public double getDanio() {
        return estadisticas.getDanioGolpe();
    }

    public double getDanioGolpe() {
        return estadisticas.getDanioGolpe();
    }

    public double getFrecuenciaAtaque() {
        return estadisticas.getFrecuenciaAtaque();
    }

    public int getAlcance() {
        return estadisticas.getAlcance();
    }

    public int getRadioEfecto() {
        return estadisticas.getRadioEfecto();
    }

    public int getCostoCapacidad() {
        return estadisticas.getCostoCapacidad();
    }

    public int getNivel() {
        return nivel;
    }

    public int getMisionMinima() {
        return misionMinima;
    }

    public int getMisionAparicion() {
        return misionMinima;
    }

    public int getMisionEscaladaHasta() {
        return misionEscaladaHasta;
    }

    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    public Posicion getPosicionInicial() {
        return posicionInicial;
    }

    public EstadoVisual getEstadoVisual() {
        return estadoVisual;
    }

    public long getRestanteAtaqueMs() {
        return restanteAtaqueMs;
    }

    public long getRestanteMovimientoMs() {
        return restanteMovimientoMs;
    }

    public RegistroCombate getRegistroCombate() {
        return registro;
    }

    public List<RegistroCrecimiento> getCrecimientos() {
        return crecimientos;
    }

    public List<RegistroCrecimiento> getHistorialCrecimiento() {
        return crecimientos;
    }

    public boolean estaOperativo() {
        return vidaActual > 0;
    }

    public boolean estaDestruido() {
        return !estaOperativo();
    }

    public double recibirDanio(double cantidad) {
        if (cantidad <= 0 || estaDestruido()) {
            return 0;
        }

        double danioEfectivo = Math.min(cantidad, vidaActual);
        vidaActual -= danioEfectivo;

        if (vidaActual <= 0) {
            vidaActual = 0;
            estadoVisual = EstadoVisual.DESTRUIDO;
        }

        return danioEfectivo;
    }

    public boolean puedeAtacar(ComponenteCombate objetivo) {
        if (objetivo == null || objetivo == this) {
            return false;
        }

        if (!estaOperativo() || !objetivo.estaOperativo()) {
            return false;
        }

        if (getBando() == objetivo.getBando()) {
            return false;
        }

        if (objetivo.esAereo() && !estadisticas.isAtacaAereo()) {
            return false;
        }

        if (estadisticas.getDanioGolpe() <= 0 || posicion == null || objetivo.getPosicion() == null) {
            return false;
        }

        int diferenciaFila = posicion.getFila() - objetivo.getPosicion().getFila();
        int diferenciaColumna = posicion.getColumna() - objetivo.getPosicion().getColumna();
        double distancia = Math.sqrt(diferenciaFila * diferenciaFila + diferenciaColumna * diferenciaColumna);

        return distancia <= estadisticas.getAlcance();
    }

    public abstract Bando getBando();

    @Override
    public boolean esAereo() {
        return false;
    }

    @Override
    public boolean bloqueaPasoTerrestre() {
        return false;
    }

    public void actualizarPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public void setPosicion(Posicion posicion) {
        actualizarPosicion(posicion);
    }

    public void setVidaActual(double vidaActual) {
        if (vidaActual < 0) {
            this.vidaActual = 0;
        } else if (vidaActual > estadisticas.getVidaMaxima()) {
            this.vidaActual = estadisticas.getVidaMaxima();
        } else {
            this.vidaActual = vidaActual;
        }

        if (this.vidaActual == 0) {
            estadoVisual = EstadoVisual.DESTRUIDO;
        }
    }

    public void setVidaMaxima(double vidaMaxima) {
        estadisticas.setVidaMaxima(vidaMaxima);

        if (vidaActual > vidaMaxima) {
            vidaActual = vidaMaxima;
        }
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setDanio(double danio) {
        estadisticas.setDanioGolpe(danio);
    }

    public void setEstadoVisual(EstadoVisual estadoVisual) {
        this.estadoVisual = estadoVisual;
    }

    public void agregarRegistroCrecimiento(RegistroCrecimiento registroCrecimiento) {
        if (registroCrecimiento != null) {
            crecimientos.add(registroCrecimiento);
            misionEscaladaHasta = registroCrecimiento.getNumeroMision();
        }
    }

    public void restablecerIntento() {
        vidaActual = estadisticas.getVidaMaxima();
        posicion = posicionInicial;
        estadoVisual = EstadoVisual.NORMAL;
        restanteAtaqueMs = 0;
        restanteMovimientoMs = 0;
        registro = new RegistroCombate();
    }

    protected void actualizarTiempos(long dtMs) {
        restanteAtaqueMs = Math.max(0, restanteAtaqueMs - dtMs);
        restanteMovimientoMs = Math.max(0, restanteMovimientoMs - dtMs);
    }
}