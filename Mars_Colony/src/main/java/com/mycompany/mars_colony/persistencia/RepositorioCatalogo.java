package com.mycompany.mars_colony.persistencia;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
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

        Path directorioPadre = archivo.getParent();

        try {
            if (directorioPadre != null) {
                Files.createDirectories(directorioPadre);
            }

            try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo.toFile()))) {
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

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo.toFile()))) {
            Object objeto = entrada.readObject();

            if (!(objeto instanceof CatalogoComponentes)) {
                throw new IllegalStateException("El archivo no contiene un catálogo válido.");
            }

            return (CatalogoComponentes) objeto;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar el catálogo desde: " + archivo, e);
        }
    }
}