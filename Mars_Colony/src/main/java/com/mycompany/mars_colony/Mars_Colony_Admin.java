package com.mycompany.mars_colony;

import com.mycompany.mars_colony.controlador.admin.AutenticadorAdmin;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import com.mycompany.mars_colony.interfaz.admin.VentanaConfiguracion;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import com.mycompany.mars_colony.util.RutasAplicacion;
import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Mars_Colony_Admin {

    private Mars_Colony_Admin() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mars_Colony_Admin::iniciarAdministrador);
    }

    private static void iniciarAdministrador() {
        try {
            TemaMars.aplicarGlobalmente();

            Path archivoCatalogo = RutasAplicacion.resolver(
                    "datos/catalogo.dat"
            );

            RepositorioCatalogo repositorio = new RepositorioCatalogo(
                    archivoCatalogo
            );

            char[] clave = "admin".toCharArray();

            AutenticadorAdmin autenticador;

            try {
                autenticador = new AutenticadorAdmin(
                        "admin",
                        clave
                );
            } finally {
                Arrays.fill(clave, '\0');
            }

            ControladorAdmin controlador = new ControladorAdmin(
                    repositorio,
                    autenticador
            );

            VentanaConfiguracion ventana = new VentanaConfiguracion(
                    controlador
            );

            ventana.setVisible(true);

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible iniciar el administrador.\n"
                    + obtenerMensaje(e),
                    "Mars Colony - Administración",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static String obtenerMensaje(Throwable error) {
        Throwable actual = error;

        while (actual.getCause() != null
                && actual.getCause() != actual) {

            actual = actual.getCause();
        }

        return actual.getMessage() == null
                || actual.getMessage().isBlank()
                ? "Ocurrió un error inesperado."
                : actual.getMessage();
    }
}