package com.mycompany.mars_colony.configuracion;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;

public class FabricaComponentes {
    public ComponenteCombate crear(ConfiguracionComponente cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula.");
        }

        cfg.validar();

        if (!cfg.isActivo()) {
            throw new IllegalArgumentException("No se puede crear un componente desde una configuración desactivada.");
        }

        return cfg.getTipo().crear(cfg);
    }
}
