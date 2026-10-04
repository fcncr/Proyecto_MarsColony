package com.mycompany.mars_colony.configuracion;

import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import java.io.Serializable;

public class ConfiguracionComponente implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String nombre;
    private final TipoComponente tipo;
    private final EstadisticasCombate base;
    private final ImagenesEstado imagenes;
    private final int misionMinima;
    private boolean activo;

    public ConfiguracionComponente(String id, String nombre, TipoComponente tipo, EstadisticasCombate base, ImagenesEstado imagenes, int misionMinima) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.base = base == null ? null : base.copiar();
        this.imagenes = imagenes;
        this.misionMinima = misionMinima;
        this.activo = true;
        validar();
    }

    public void validar() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede estar vacío.");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de componente es obligatorio.");
        }

        if (base == null) {
            throw new IllegalArgumentException("Las estadísticas base son obligatorias.");
        }

        if (imagenes == null) {
            throw new IllegalArgumentException("Las imágenes son obligatorias.");
        }

        if (misionMinima < 1) {
            throw new IllegalArgumentException("La misión mínima debe ser al menos 1.");
        }

        if (base.getVidaMaxima() <= 0) {
            throw new IllegalArgumentException("La vida máxima debe ser mayor que 0.");
        }

        if (base.getDanioGolpe() < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }

        if (base.getFrecuenciaAtaque() <= 0) {
            throw new IllegalArgumentException("La frecuencia de ataque debe ser mayor que 0.");
        }

        if (base.getAlcance() < 0) {
            throw new IllegalArgumentException("El alcance no puede ser negativo.");
        }

        if (base.getRadioEfecto() < 0) {
            throw new IllegalArgumentException("El radio de efecto no puede ser negativo.");
        }

        if (base.getCostoCapacidad() <= 0) {
            throw new IllegalArgumentException("El costo de capacidad debe ser mayor que 0.");
        }

        if (tipo == TipoComponente.BARRERA && base.getDanioGolpe() != 0) {
            throw new IllegalArgumentException("Una barrera debe tener daño igual a 0.");
        }
    }

    public void desactivar() {
        activo = false;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoComponente getTipo() {
        return tipo;
    }

    public EstadisticasCombate getBase() {
        return base.copiar();
    }

    public ImagenesEstado getImagenes() {
        return imagenes;
    }

    public int getMisionMinima() {
        return misionMinima;
    }

    public boolean isActivo() {
        return activo;
    }
}