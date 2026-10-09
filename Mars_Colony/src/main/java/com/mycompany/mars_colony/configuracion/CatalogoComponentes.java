package com.mycompany.mars_colony.configuracion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogoComponentes implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<ConfiguracionComponente> configuraciones;

    public CatalogoComponentes() {
        this.configuraciones = new ArrayList<>();
    }

    public void crear(ConfiguracionComponente c) {
        if (c == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula.");
        }

        c.validar();

        if (buscarIndicePorId(c.getId()) != -1) {
            throw new IllegalArgumentException("Ya existe una configuración con el id: " + c.getId());
        }

        configuraciones.add(c);
    }

    public void modificar(String id, ConfiguracionComponente c) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id a modificar no puede estar vacío.");
        }

        if (c == null) {
            throw new IllegalArgumentException("La nueva configuración no puede ser nula.");
        }

        c.validar();

        int indiceActual = buscarIndicePorId(id);

        if (indiceActual == -1) {
            throw new IllegalArgumentException("No existe una configuración con el id: " + id);
        }

        int indiceNuevoId = buscarIndicePorId(c.getId());

        if (indiceNuevoId != -1 && indiceNuevoId != indiceActual) {
            throw new IllegalArgumentException("Ya existe otra configuración con el id: " + c.getId());
        }

        configuraciones.set(indiceActual, c);
    }

    public ConfiguracionComponente consultar(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede estar vacío.");
        }

        int indice = buscarIndicePorId(id);

        if (indice == -1) {
            return null;
        }

        return configuraciones.get(indice);
    }

    public void desactivar(String id) {
        ConfiguracionComponente configuracion = consultar(id);

        if (configuracion == null) {
            throw new IllegalArgumentException("No existe una configuración con el id: " + id);
        }

        configuracion.desactivar();
    }

    public List<ConfiguracionComponente> disponibles(int mision) {
        if (mision < 1) {
            throw new IllegalArgumentException("La misión debe ser al menos 1.");
        }

        List<ConfiguracionComponente> resultado = new ArrayList<>();

        for (ConfiguracionComponente configuracion : configuraciones) {
            if (configuracion.isActivo() && configuracion.getMisionMinima() <= mision) {
                resultado.add(configuracion);
            }
        }

        return Collections.unmodifiableList(resultado);
    }

    private int buscarIndicePorId(String id) {
        for (int i = 0; i < configuraciones.size(); i++) {
            if (configuraciones.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }
    public List<ConfiguracionComponente> listar() {
        return Collections.unmodifiableList(new ArrayList<>(configuraciones));
    }
}