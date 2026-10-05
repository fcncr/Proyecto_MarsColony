/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mars_colony.modelo.partida;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 *
 * @author fabic
 */
public class Escuadron {
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
            if (existente.getId().equals(defensa.getId())) {
                return false;
            }
        }

        defensas.add(defensa);
        return true;
    }
    
    private Defensa buscarDefensa(String id) {
        for (Defensa defensa : defensas) {
            if (defensa.getId().equals(id)) {
                return defensa;
            }
        }

        return null;
    }
    
    public int capacidadUtilizada() {
        int total = 0;

        for (Defensa defensa : defensas) {
            if (seleccionadas.contains(defensa.getId())) {
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
            throw new IllegalArgumentException(
                    "La cantidad a aumentar debe ser mayor que cero."
            );
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
        return seleccionadas.remove(id);
    }
    
    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public List<Defensa> getDefensas() {
        return defensas;
    }

    public Set<String> getSeleccionadas() {
        return seleccionadas;
    }
}
