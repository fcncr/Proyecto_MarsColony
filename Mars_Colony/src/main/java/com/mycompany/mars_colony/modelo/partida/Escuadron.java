package com.mycompany.mars_colony.modelo.partida;

import com.mycompany.mars_colony.modelo.combate.Defensa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Escuadron implements Serializable {

    private static final long serialVersionUID = 1L;

    private int capacidadTotal = 20;
    private List<Defensa> defensas;
    private Set<String> seleccionadas;

    public Escuadron() {
        defensas = new ArrayList<>();
        seleccionadas = new HashSet<>();
    }

    public boolean agregarDefensa(Defensa defensa) {
        if (defensa == null) {
            return false;
        }

        for (Defensa existente : defensas) {
            if (existente != null && existente.getId().equals(defensa.getId())) {
                return false;
            }
        }

        defensas.add(defensa);
        return true;
    }

    private Defensa buscarDefensa(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        for (Defensa defensa : defensas) {
            if (defensa != null && id.equals(defensa.getId())) {
                return defensa;
            }
        }

        return null;
    }

    public int capacidadUtilizada() {
        int total = 0;

        for (Defensa defensa : defensas) {
            if (defensa != null && seleccionadas.contains(defensa.getId())) {
                total += defensa.getCostoCapacidad();
            }
        }

        return total;
    }

    public int capacidadRestante() {
        return capacidadTotal - capacidadUtilizada();
    }

    public void aumentarCapacidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor que cero.");
        }

        capacidadTotal += cantidad;
    }

    public boolean seleccionar(String id) {
        Defensa defensa = buscarDefensa(id);

        if (defensa == null || seleccionadas.contains(id)) {
            return false;
        }

        if (capacidadUtilizada() + defensa.getCostoCapacidad() > capacidadTotal) {
            return false;
        }

        seleccionadas.add(id);
        return true;
    }

    public boolean deseleccionar(String id) {
        if (id == null) {
            return false;
        }

        return seleccionadas.remove(id);
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public List<Defensa> getDefensas() {
        return Collections.unmodifiableList(new ArrayList<>(defensas));
    }

    public Set<String> getSeleccionadas() {
        return Collections.unmodifiableSet(new HashSet<>(seleccionadas));
    }
}