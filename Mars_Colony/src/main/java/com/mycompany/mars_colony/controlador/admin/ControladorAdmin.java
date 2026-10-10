package com.mycompany.mars_colony.controlador.admin;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import java.util.List;

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

    public boolean iniciarSesion(String usuario, char[] clave) {
        sesionActiva = auth.autenticar(usuario, clave);
        return sesionActiva;
    }

    public void cerrarSesion() {
        sesionActiva = false;
    }

    public boolean haySesionActiva() {
        return sesionActiva;
    }

    public void crear(ConfiguracionComponente configuracion) {
        exigirSesionActiva();
        catalogo.crear(configuracion);
    }

    public void modificar(String id, ConfiguracionComponente configuracion) {
        exigirSesionActiva();
        catalogo.modificar(id, configuracion);
    }

    public void desactivar(String id) {
        exigirSesionActiva();
        catalogo.desactivar(id);
    }
    public void activar(String id) {
        exigirSesionActiva();
        catalogo.activar(id);
    }

    public ConfiguracionComponente consultar(String id) {
        exigirSesionActiva();
        return catalogo.consultar(id);
    }

    public List<ConfiguracionComponente> listar() {
        exigirSesionActiva();
        return catalogo.listar();
    }

    public void guardar() {
        exigirSesionActiva();
        repo.guardar(catalogo);
    }

    public void cargar() {
        exigirSesionActiva();
        catalogo = repo.cargar();
    }

    private void exigirSesionActiva() {
        if (!sesionActiva) {
            throw new IllegalStateException(
                    "Debe iniciar sesión como administrador para realizar esta operación."
            );
        }
    }
}