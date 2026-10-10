package com.mycompany.mars_colony.modelo.estado;

import java.io.Serializable;

public final class ImagenesEstado implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private final String normal;
    private final String movimiento;
    private final String ataque;
    
    public ImagenesEstado(
            String normal,
            String movimiento,
            String ataque) {

        this.normal = normal;
        this.movimiento = movimiento;
        this.ataque = ataque;
    }

    public String getNormal() {
        return normal;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public String getAtaque() {
        return ataque;
    }
}