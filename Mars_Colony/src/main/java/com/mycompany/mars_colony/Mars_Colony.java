package com.mycompany.mars_colony;

import com.mycompany.mars_colony.configuracion.CatalogoComponentes;
import com.mycompany.mars_colony.configuracion.ConfiguracionComponente;
import com.mycompany.mars_colony.configuracion.TipoComponente;
import com.mycompany.mars_colony.controlador.admin.AutenticadorAdmin;
import com.mycompany.mars_colony.controlador.admin.ControladorAdmin;
import com.mycompany.mars_colony.interfaz.admin.VentanaConfiguracion;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.persistencia.RepositorioCatalogo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.SwingUtilities;

public class Mars_Colony {

    public static void main(String[] args) {
        Path archivo = Path.of("datos", "catalogo_gui_prueba.dat");

        borrarArchivo(archivo);

        RepositorioCatalogo repo = new RepositorioCatalogo(archivo);
        CatalogoComponentes catalogo = new CatalogoComponentes();

        ImagenesEstado imagenesTorre = new ImagenesEstado("torre_normal.png", "torre_movimiento.png", "torre_ataque.png");
        EstadisticasCombate statsTorre = new EstadisticasCombate(12.0, 3.0, 1.0, 6, 0, 2, true, 1, 1, 0);
        ConfiguracionComponente torre = new ConfiguracionComponente("DEF001", "Torre láser", TipoComponente.DEFENSA_ALCANCE, statsTorre, imagenesTorre, 1);

        ImagenesEstado imagenesBarrera = new ImagenesEstado("muro_normal.png", "muro_movimiento.png", "muro_ataque.png");
        EstadisticasCombate statsBarrera = new EstadisticasCombate(30.0, 0.0, 0.0, 0, 0, 1, false, 0, 0, 0);
        ConfiguracionComponente barrera = new ConfiguracionComponente("DEF002", "Muro de titanio", TipoComponente.BARRERA, statsBarrera, imagenesBarrera, 1);

        catalogo.crear(torre);
        catalogo.crear(barrera);
        catalogo.desactivar("DEF002");

        repo.guardar(catalogo);

        char[] claveAdmin = "marte123".toCharArray();
        AutenticadorAdmin auth = new AutenticadorAdmin("admin", claveAdmin);
        ControladorAdmin controlador = new ControladorAdmin(repo, auth);

        SwingUtilities.invokeLater(() -> {
            VentanaConfiguracion ventana = new VentanaConfiguracion(controlador);

            ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    borrarArchivo(archivo);
                }
            });

            ventana.setVisible(true);
        });
    }

    private static void borrarArchivo(Path archivo) {
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            System.out.println("[AVISO] No se pudo eliminar el archivo temporal de GUI: " + e.getMessage());
        }
    }
}