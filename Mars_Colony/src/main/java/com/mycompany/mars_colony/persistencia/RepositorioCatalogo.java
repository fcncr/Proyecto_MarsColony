package com.mycompany.mars_colony.persistencia;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class RepositorioCatalogo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Path archivo;

    public RepositorioCatalogo(Path archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        this.archivo = archivo;
    }

    public void guardar(CatalogoComponentes c) {
        if (c == null) {
            throw new IllegalArgumentException("El catálogo no puede ser nulo.");
        }

        // Validar antes de abrir el archivo: un catálogo inválido no reemplaza al anterior.
        validarCatalogo(c);

        Path directorioPadre = archivo.getParent();

        try {
            if (directorioPadre != null) {
                Files.createDirectories(directorioPadre);
            }

            try (FileOutputStream archivoSalida = new FileOutputStream(archivo.toFile());
                    ObjectOutputStream salida = new ObjectOutputStream(archivoSalida)) {
                salida.writeObject(c);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el catálogo en: " + archivo, e);
        }
    }

    public CatalogoComponentes cargar() {
        if (Files.notExists(archivo)) {
            return new CatalogoComponentes();
        }

        try (FileInputStream archivoEntrada = new FileInputStream(archivo.toFile());
                ObjectInputStream entrada = new ObjectInputStream(archivoEntrada)) {
            Object objeto = entrada.readObject();

            if (!(objeto instanceof CatalogoComponentes catalogo)) {
                throw new IllegalStateException("El archivo no contiene un catálogo válido.");
            }

            validarCatalogo(catalogo);
            return catalogo;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar el catálogo desde: " + archivo, e);
        }
    }

    private void validarCatalogo(CatalogoComponentes catalogo) {
        try {
            for (ConfiguracionComponente configuracion : catalogo.listar()) {
                if (configuracion == null) {
                    throw new IllegalArgumentException("El catálogo contiene una configuración nula.");
                }
                configuracion.validar();
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "El catálogo contiene configuraciones inválidas y no puede utilizarse: "
                    + e.getMessage(), e
            );
        }
    }
}
