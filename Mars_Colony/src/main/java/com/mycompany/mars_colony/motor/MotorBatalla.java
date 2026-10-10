package com.mycompany.mars_colony.motor;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MotorBatalla {

    private Partida partida;
    private boolean enEjecucion;
    private final ReentrantLock lock;
    private final Condition condicion;

    public MotorBatalla(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser null.");
        }

        this.partida = partida;
        this.enEjecucion = false;
        this.lock = new ReentrantLock();
        this.condicion = lock.newCondition();
    }

    public Partida getPartida() {
        return partida;
    }

    public boolean estaEnEjecucion() {
        lock.lock();

        try {
            return enEjecucion;
        } finally {
            lock.unlock();
        }
    }

    public void iniciar() {
        lock.lock();

        try {
            enEjecucion = true;

            if (partida.getMision() != null) {
                partida.getMision().setEstado(EstadoMision.EN_CURSO);
            }

            condicion.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void detener() {
        lock.lock();

        try {
            enEjecucion = false;
            condicion.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean mover(ComponenteCombate componente, Posicion destino) {
        lock.lock();

        try {
            if (componente == null || destino == null || !enEjecucion || !componente.puedeMoverseAhora()) {
                return false;
            }

            Posicion origen = componente.getPosicion();

            if (origen == null) {
                return false;
            }

            boolean movio = partida.getTablero().mover(origen, destino);

            if (movio) {
                componente.consumirMovimiento();
            }

            return movio;
        } finally {
            lock.unlock();
        }
    }

    public boolean moverHaciaObjetivoTerrestre(ComponenteCombate componente, ComponenteCombate objetivo) {
        lock.lock();

        try {
            if (componente == null || objetivo == null || !enEjecucion || componente.getPosicion() == null || objetivo.getPosicion() == null) {
                return false;
            }

            List<Posicion> ruta = NavegadorBatalla.calcularRutaTerrestre(partida.getTablero(), componente, objetivo);

            if (ruta.size() < 2) {
                return false;
            }

            return mover(componente, ruta.get(1));
        } finally {
            lock.unlock();
        }
    }

    public boolean moverAereoHaciaObjetivo(ComponenteCombate componente, ComponenteCombate objetivo) {
        lock.lock();

        try {
            if (componente == null || objetivo == null || !componente.esAereo() || !enEjecucion) {
                return false;
            }

            Posicion siguiente = NavegadorBatalla.buscarSiguientePasoAereo(partida.getTablero(), componente, objetivo);

            if (siguiente == null) {
                return false;
            }

            return moverAereo(componente, siguiente);
        } finally {
            lock.unlock();
        }
    }

    public ComponenteCombate buscarObjetivoTerrestreAlcanzable(ComponenteCombate atacante) {
        lock.lock();

        try {
            if (atacante == null || !atacante.estaOperativo() || atacante.getPosicion() == null) {
                return null;
            }

            return NavegadorBatalla.buscarObjetivoTerrestreAlcanzable(partida.getTablero(), atacante, buscarObjetivos(atacante));
        } finally {
            lock.unlock();
        }
    }

    public double aplicarAtaque(ComponenteCombate atacante, ComponenteCombate objetivo) {
        lock.lock();

        try {
            if (!enEjecucion || atacante == null || objetivo == null || !atacante.estaOperativo() || !objetivo.estaOperativo() || !atacante.puedeAtacar(objetivo)) {
                return 0;
            }

            double danioSolicitado = atacante.getDanioGolpe();
            double danioEfectivo = objetivo.recibirDanio(danioSolicitado);

            if (danioEfectivo <= 0) {
                return 0;
            }

            atacante.getRegistroCombate().registrarAtaqueRealizado(objetivo.getId(), objetivo.getNombre(), danioEfectivo);
            objetivo.getRegistroCombate().registrarAtaqueRecibido(atacante.getId(), atacante.getNombre(), danioEfectivo);

            if (objetivo.estaDestruido()) {
                partida.getTablero().retirar(objetivo.getPosicion());
            }

            evaluarFin();
            return danioEfectivo;
        } finally {
            lock.unlock();
        }
    }

    public List<ComponenteCombate> buscarObjetivos(ComponenteCombate atacante) {
        lock.lock();

        try {
            List<ComponenteCombate> objetivos = new ArrayList<>();

            if (atacante == null || !atacante.estaOperativo() || partida.getMision() == null) {
                return objetivos;
            }

            for (ComponenteCombate candidato : partida.getMision().getParticipantes()) {
                if (candidato == null || candidato == atacante || !candidato.estaOperativo() || candidato.getBando() == atacante.getBando()) {
                    continue;
                }

                if (candidato.esAereo() && !atacante.getEstadisticas().isAtacaAereo()) {
                    continue;
                }

                if (atacante instanceof Defensa && candidato instanceof Criatura) {
                    objetivos.add(candidato);
                } else if (atacante instanceof Criatura && (candidato instanceof Defensa || candidato instanceof NucleoOxigeno)) {
                    objetivos.add(candidato);
                }
            }

            return objetivos;
        } finally {
            lock.unlock();
        }
    }

    public void evaluarFin() {
        lock.lock();

        try {
            if (partida.getMision() == null) {
                return;
            }

            if (partida.getMision().todasCriaturasEliminadas()) {
                partida.getMision().setEstado(EstadoMision.VICTORIA);
                partida.getMision().cerrarRegistros();
                enEjecucion = false;
                condicion.signalAll();
                return;
            }

            for (ComponenteCombate participante : partida.getMision().getParticipantes()) {
                if (participante instanceof NucleoOxigeno && participante.estaDestruido()) {
                    partida.getMision().setEstado(EstadoMision.DERROTA);
                    partida.getMision().cerrarRegistros();
                    enEjecucion = false;
                    condicion.signalAll();
                    return;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void destruirComponente(ComponenteCombate componente) {
        lock.lock();

        try {
            if (componente == null || componente.estaDestruido()) {
                return;
            }

            Posicion posicion = componente.getPosicion();
            componente.recibirDanio(componente.getVidaActual());

            if (posicion != null) {
                partida.getTablero().retirar(posicion);
            }

            evaluarFin();
        } finally {
            lock.unlock();
        }
    }

    public boolean moverAereo(ComponenteCombate componente, Posicion destino) {
        lock.lock();

        try {
            if (componente == null || destino == null || !enEjecucion || !componente.esAereo() || !componente.puedeMoverseAhora() || !partida.getTablero().estaDentro(destino) || !partida.getTablero().estaLibre(destino)) {
                return false;
            }

            Posicion origen = componente.getPosicion();

            if (origen == null) {
                return false;
            }

            partida.getTablero().retirar(origen);

            if (!partida.getTablero().colocar(componente, destino)) {
                partida.getTablero().colocar(componente, origen);
                return false;
            }

            componente.consumirMovimiento();
            return true;
        } finally {
            lock.unlock();
        }
    }
}