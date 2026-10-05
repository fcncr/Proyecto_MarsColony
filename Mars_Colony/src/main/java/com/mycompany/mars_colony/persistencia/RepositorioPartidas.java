package com.mycompany.mars_colony.persistencia;

import com.mycompany.mars_colony.modelo.partida.Partida;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RepositorioPartidas {

    private static final String EXTENSION = ".dat";

    private Path directorio;

    public RepositorioPartidas(Path directorio)
            throws IOException {

        if (directorio == null) {
            throw new IllegalArgumentException(
                    "El directorio no puede ser null."
            );
        }

        this.directorio = directorio.toAbsolutePath().normalize();

        Files.createDirectories(
                this.directorio
        );
    }

    public void guardar(Partida partida)
        throws IOException {

        if (partida == null) {
            throw new IllegalArgumentException(
                    "La partida no puede ser null."
            );
        }

        String comandante =
                partida.getNombreComandante();

        validarComandante(comandante);

        Path archivoFinal =
                obtenerArchivo(comandante);

        Path archivoTemporal =
                obtenerArchivoTemporal(comandante);

        boolean escrituraCompleta = false;

        try {

          
            Files.deleteIfExists(
                    archivoTemporal
            );

            try (ObjectOutputStream salida =
                    new ObjectOutputStream(
                            Files.newOutputStream(
                                    archivoTemporal
                            )
                    )) {

                salida.writeObject(partida);

                salida.flush();
            }

            escrituraCompleta = true;

            moverTemporalAFinal(
                    archivoTemporal,
                    archivoFinal
            );

        } finally {

            if (!escrituraCompleta
                    || Files.exists(archivoTemporal)) {

                eliminarTemporalSilenciosamente(
                        archivoTemporal
                );
            }
        }
    }

    public Partida cargar(String comandante)
            throws IOException,
            ClassNotFoundException {

        validarComandante(comandante);

        Path archivo =
                obtenerArchivo(comandante);

        if (!Files.isRegularFile(archivo)) {
            throw new IOException(
                    "No existe una partida para el comandante: "
                    + comandante
            );
        }

        try (ObjectInputStream entrada =
                new ObjectInputStream(
                        Files.newInputStream(archivo)
                )) {

            Object objeto =
                    entrada.readObject();

            if (!(objeto instanceof Partida)) {
                throw new IOException(
                        "El archivo no contiene una partida valida."
                );
            }

            return (Partida) objeto;
        }
    }

    public List<String> listar()
            throws IOException {

        List<String> comandantes =
                new ArrayList<>();

        try (Stream<Path> archivos =
                Files.list(directorio)) {

            archivos
                    .filter(Files::isRegularFile)
                    .map(path ->
                            path.getFileName()
                                    .toString())
                    .filter(nombre ->
                            nombre.endsWith(EXTENSION))
                    .map(nombre ->
                            nombre.substring(
                                    0,
                                    nombre.length()
                                    - EXTENSION.length()
                            ))
                    .forEach(comandantes::add);
        }

        Collections.sort(comandantes);

        return comandantes;
    }

    public boolean existeComandante(
            String comandante) {

        validarComandante(comandante);

        Path archivo =
                obtenerArchivo(comandante);

        return Files.isRegularFile(archivo);
    }

    private Path obtenerArchivo(String comandante) {

        String nombreSeguro =
                sanitizarComandante(comandante);

        Path directorioNormalizado =
                directorio
                        .toAbsolutePath()
                        .normalize();

        Path archivo =
                directorioNormalizado
                        .resolve(
                                nombreSeguro
                                + EXTENSION
                        )
                        .normalize();

        if (!archivo.startsWith(
                directorioNormalizado)) {

            throw new IllegalArgumentException(
                    "El nombre del comandante produce una ruta no permitida."
            );
        }

        return archivo;
    }
    
    private Path obtenerArchivoTemporal(
        String comandante) {

        String nombreSeguro =
                sanitizarComandante(comandante);

        Path directorioNormalizado =
                directorio
                        .toAbsolutePath()
                        .normalize();

        Path archivo =
                directorioNormalizado
                        .resolve(
                                nombreSeguro + ".tmp"
                        )
                        .normalize();

        if (!archivo.startsWith(
                directorioNormalizado)) {

            throw new IllegalArgumentException(
                    "El nombre del comandante produce una ruta temporal no permitida."
            );
        }

        return archivo;
    }

    private void validarComandante(
            String comandante) {

        if (comandante == null
                || comandante.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del comandante no puede estar vacio."
            );
        }
    }

    public Path getDirectorio() {
        return directorio;
    }
    
    private String sanitizarComandante(String comandante) {

        validarComandante(comandante);

        String limpio = comandante.trim();

        limpio = limpio.replaceAll(
                "[^a-zA-Z0-9_-]",
                "_"
        );

        limpio = limpio.replaceAll(
                "_+",
                "_"
        );

        if (limpio.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del comandante no genera un nombre de archivo valido."
            );
        }

        return limpio;
    }
    
    private void moverTemporalAFinal(
        Path archivoTemporal,
        Path archivoFinal)
        throws IOException {

        try {

            Files.move(
                    archivoTemporal,
                    archivoFinal,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );

        } catch (AtomicMoveNotSupportedException ex) {

            Files.move(
                    archivoTemporal,
                    archivoFinal,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
    
    private void eliminarTemporalSilenciosamente(
        Path archivoTemporal) {

        try {

            Files.deleteIfExists(
                    archivoTemporal
            );

        } catch (IOException ex) {

        }
    }
}