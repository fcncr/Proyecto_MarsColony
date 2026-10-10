package com.mycompany.mars_colony;

import com.mycompany.mars_colony.controlador.admin.AutenticadorAdmin;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import com.mycompany.mars_colony.interfaz.admin.VentanaConfiguracion;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import com.mycompany.mars_colony.util.RutasAplicacion;
import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class MarsColonyAdmin {

    private static final String USUARIO_ADMIN = "admin";

    private MarsColonyAdmin() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarsColonyAdmin::iniciarAdministrador);
    }

    private static void iniciarAdministrador() {
        char[] claveAdmin = "marte123".toCharArray();

        try {
            Path archivoCatalogo = RutasAplicacion.resolver("datos/catalogo.dat");
            RepositorioCatalogo repositorioCatalogo = new RepositorioCatalogo(archivoCatalogo);
            AutenticadorAdmin autenticador = new AutenticadorAdmin(USUARIO_ADMIN, claveAdmin);
            ControladorAdmin controlador = new ControladorAdmin(repositorioCatalogo, autenticador);
            VentanaConfiguracion ventana = new VentanaConfiguracion(controlador);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "No fue posible iniciar el administrador.\n" + e.getMessage(), "Mars Colony - Administrador", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(claveAdmin, '\0');
        }
    }
}