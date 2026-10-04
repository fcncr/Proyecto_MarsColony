package com.mycompany.mars_colony.controlador.admin;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;

public class ControladorAdmin {

    private CatalogoComponentes catalogo;
    private final RepositorioCatalogo repo;
    private final AutenticadorAdmin auth;
    private boolean sesionActiva;

    public ControladorAdmin(RepositorioCatalogo repo, AutenticadorAdmin auth) {
        if (repo == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }

        if (auth == null) {
            throw new IllegalArgumentException("El autenticador no puede ser nulo.");
        }

        this.repo = repo;
        this.auth = auth;
        this.catalogo = repo.cargar();
        this.sesionActiva = false;
    }

    public boolean iniciarSesion(String u, char[] clave) {
        sesionActiva = auth.autenticar(u, clave);
        return sesionActiva;
    }

    public void crear(ConfiguracionComponente c) {
        exigirSesionActiva();
        catalogo.crear(c);
    }

    public void modificar(String id, ConfiguracionComponente c) {
        exigirSesionActiva();
        catalogo.modificar(id, c);
    }

    public void desactivar(String id) {
        exigirSesionActiva();
        catalogo.desactivar(id);
    }

    public void guardar() {
        exigirSesionActiva();
        repo.guardar(catalogo);
    }

    private void exigirSesionActiva() {
        if (!sesionActiva) {
            throw new IllegalStateException("Debe iniciar sesión como administrador para realizar esta operación.");
        }
    }
}