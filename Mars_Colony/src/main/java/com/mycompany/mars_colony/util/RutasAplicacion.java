package com.mycompany.mars_colony.util;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class RutasAplicacion {

    private static final Path DIRECTORIO_BASE = detectarDirectorioBase();

    private RutasAplicacion() {
    }

    public static Path directorioBase() {
        return DIRECTORIO_BASE;
    }

    public static Path resolver(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return DIRECTORIO_BASE;
        }

        Path path = Paths.get(ruta);

        if (path.isAbsolute()) {
            return path.normalize();
        }

        return DIRECTORIO_BASE
                .resolve(limpiarRuta(ruta))
                .normalize();
    }

    public static Path buscarArchivo(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        Path directa = resolver(ruta);

        if (Files.isRegularFile(directa)) {
            return directa;
        }

        Path recursoProyecto = DIRECTORIO_BASE
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve(limpiarRuta(ruta))
                .normalize();

        if (Files.isRegularFile(recursoProyecto)) {
            return recursoProyecto;
        }

        return null;
    }

    public static boolean existeRecurso(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return false;
        }

        String limpia = limpiarRuta(ruta);

        URL recurso = RutasAplicacion.class
                .getClassLoader()
                .getResource(limpia);

        if (recurso != null) {
            return true;
        }

        return buscarArchivo(limpia) != null;
    }

    public static String relativizar(Path archivo) {
        if (archivo == null) {
            return null;
        }

        Path normalizado = archivo
                .toAbsolutePath()
                .normalize();

        Path base = DIRECTORIO_BASE
                .toAbsolutePath()
                .normalize();

        if (normalizado.startsWith(base)) {
            return base
                    .relativize(normalizado)
                    .toString()
                    .replace("\\", "/");
        }

        return normalizado
                .toString()
                .replace("\\", "/");
    }

    public static String limpiarRuta(String ruta) {
        if (ruta == null) {
            return "";
        }

        String limpia = ruta
                .trim()
                .replace("\\", "/");

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia;
    }

    private static Path detectarDirectorioBase() {
        try {
            URI ubicacion = RutasAplicacion.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();

            Path ruta = Paths.get(ubicacion)
                    .toAbsolutePath()
                    .normalize();

            if (Files.isRegularFile(ruta)) {
                Path padre = ruta.getParent();

                if (padre != null) {
                    return padre;
                }
            }

            String texto = ruta
                    .toString()
                    .replace("\\", "/");

            if (texto.endsWith("/target/classes")) {
                Path target = ruta.getParent();

                if (target != null && target.getParent() != null) {
                    return target.getParent();
                }
            }

            return ruta;

        } catch (Exception e) {
            return Paths.get("")
                    .toAbsolutePath()
                    .normalize();
        }
    }
}