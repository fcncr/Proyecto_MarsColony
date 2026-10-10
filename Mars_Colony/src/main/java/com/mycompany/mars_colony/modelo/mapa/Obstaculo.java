package com.mycompany.mars_colony.modelo.mapa;

import java.io.Serializable;

public class Obstaculo implements OcupanteMapa, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String IMAGEN_POR_DEFECTO = "assets/importados/OBS2.png";

    private String id;
    private Posicion posicion;
    private String rutaImagen;

    public Obstaculo(String id, Posicion posicion) {
        this(id, posicion, IMAGEN_POR_DEFECTO);
    }

    public Obstaculo(String id, Posicion posicion, String rutaImagen) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del obstáculo es obligatorio.");
        }

        if (posicion == null) {
            throw new IllegalArgumentException("La posición del obstáculo es obligatoria.");
        }

        this.id = id;
        this.posicion = posicion;
        this.rutaImagen = rutaImagen == null || rutaImagen.isBlank() ? IMAGEN_POR_DEFECTO : rutaImagen;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    @Override
    public boolean bloqueaPasoTerrestre() {
        return true;
    }

    @Override
    public boolean esAereo() {
        return false;
    }
}