package com.mycompany.mars_colony.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RutasAplicacion {

    private static final Path DIRECTORIO_BASE = detectarDirectorioBase();

    private RutasAplicacion() {
    }

    public static Path directorioBase() {
        return DIRECTORIO_BASE;
    }

    public static Path resolver(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            throw new IllegalArgumentException("La ruta no puede estar vacía.");
        }

        Path path = Path.of(ruta.replace("\\", "/"));

        if (path.isAbsolute()) {
            return path.normalize();
        }

        return DIRECTORIO_BASE.resolve(path).normalize();
    }

    public static Path buscarArchivo(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        try {
            Path archivo = resolver(ruta);

            if (Files.isRegularFile(archivo)) {
                return archivo;
            }

            String limpia = limpiarRuta(ruta);
            Path recursoDesarrollo = DIRECTORIO_BASE.resolve("src").resolve("main").resolve("resources").resolve(limpia).normalize();

            if (Files.isRegularFile(recursoDesarrollo)) {
                return recursoDesarrollo;
            }

            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static boolean existeRecurso(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return false;
        }

        String limpia = limpiarRuta(ruta);
        URL recurso = RutasAplicacion.class.getClassLoader().getResource(limpia);

        if (recurso != null) {
            return true;
        }

        return buscarArchivo(ruta) != null;
    }

    public static String relativizar(Path archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }

        Path absoluto = archivo.toAbsolutePath().normalize();

        if (!absoluto.startsWith(DIRECTORIO_BASE)) {
            return absoluto.toString().replace('\\', '/');
        }

        return DIRECTORIO_BASE.relativize(absoluto).toString().replace('\\', '/');
    }

    public static String limpiarRuta(String ruta) {
        if (ruta == null) {
            return "";
        }

        String limpia = ruta.replace("\\", "/");

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia;
    }

    private static Path detectarDirectorioBase() {
        try {
            URL ubicacionUrl = RutasAplicacion.class.getProtectionDomain().getCodeSource().getLocation();

            if (ubicacionUrl == null) {
                return Path.of("").toAbsolutePath().normalize();
            }

            Path ubicacion = Path.of(ubicacionUrl.toURI()).toAbsolutePath().normalize();

            if (Files.isRegularFile(ubicacion)) {
                return ubicacion.getParent();
            }

            Path nombre = ubicacion.getFileName();
            Path padre = ubicacion.getParent();

            if (nombre != null && padre != null && "classes".equals(nombre.toString()) && padre.getFileName() != null && "target".equals(padre.getFileName().toString()) && padre.getParent() != null) {
                return padre.getParent().normalize();
            }

            return ubicacion;
        } catch (Exception e) {
            return Path.of("").toAbsolutePath().normalize();
        }
    }
}