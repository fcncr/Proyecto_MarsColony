package com.mycompany.mars_colony.configuracion;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.criatura.Acechador;
import com.mycompany.mars_colony.modelo.combate.criatura.Demoledor;
import com.mycompany.mars_colony.modelo.combate.criatura.Enjambre;
import com.mycompany.mars_colony.modelo.combate.criatura.Escupidor;
import com.mycompany.mars_colony.modelo.combate.criatura.Volador;
import com.mycompany.mars_colony.modelo.combate.defensa.Barrera;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaAlcance;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaContacto;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaImpacto;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaMultiple;
import com.mycompany.mars_colony.modelo.combate.defensa.Dron;
public enum TipoComponente {
    
    DEFENSA_CONTACTO {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new DefensaContacto(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    DEFENSA_ALCANCE {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new DefensaAlcance(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    DRON {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Dron(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    DEFENSA_IMPACTO {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new DefensaImpacto(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    DEFENSA_MULTIPLE {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new DefensaMultiple(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    BARRERA {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Barrera(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    ACECHADOR {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Acechador(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    ESCUPIDOR {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Escupidor(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    DEMOLEDOR {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Demoledor(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    VOLADOR {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Volador(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    },

    ENJAMBRE {
        @Override
        public ComponenteCombate crear(ConfiguracionComponente cfg) {
            return new Enjambre(cfg.getId(), cfg.getNombre(), cfg.getBase(), cfg.getImagenes(), cfg.getMisionMinima(), null);
        }
    };

    public abstract ComponenteCombate crear(ConfiguracionComponente cfg);
}