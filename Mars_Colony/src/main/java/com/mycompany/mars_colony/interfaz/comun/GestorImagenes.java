package com.mycompany.mars_colony.interfaz.comun;

import com.mycompany.mars_colony.util.RutasAplicacion;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class GestorImagenes {

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    private GestorImagenes() {
    }

    public static ImageIcon cargar(String ruta, int ancho, int alto) {
        int anchoSeguro = Math.max(1, ancho);
        int altoSeguro = Math.max(1, alto);
        String rutaLimpia = RutasAplicacion.limpiarRuta(ruta);
        String clave = "CONTAIN|" + rutaLimpia + "|" + anchoSeguro + "|" + altoSeguro;
        return CACHE.computeIfAbsent(clave, k -> crearIcono(rutaLimpia, anchoSeguro, altoSeguro));
    }

    public static ImageIcon cargarCubriendo(String ruta, int ancho, int alto) {
        int anchoSeguro = Math.max(1, ancho);
        int altoSeguro = Math.max(1, alto);
        String rutaLimpia = RutasAplicacion.limpiarRuta(ruta);
        String clave = "COVER|" + rutaLimpia + "|" + anchoSeguro + "|" + altoSeguro;
        return CACHE.computeIfAbsent(clave, k -> crearIconoCubriendo(rutaLimpia, anchoSeguro, altoSeguro));
    }

    public static boolean existe(String ruta) {
        return RutasAplicacion.existeRecurso(ruta);
    }

    public static void limpiarCache() {
        CACHE.clear();
    }

    private static ImageIcon crearIcono(String ruta, int ancho, int alto) {
        BufferedImage original = cargarImagen(ruta);

        if (original == null) {
            return crearFallback(ancho, alto);
        }

        double escala = Math.min((double) ancho / original.getWidth(), (double) alto / original.getHeight());
        int nuevoAncho = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int nuevoAlto = Math.max(1, (int) Math.round(original.getHeight() * escala));

        Image escalada = original.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

        BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lienzo.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;

        g2.drawImage(escalada, x, y, null);
        g2.dispose();

        return new ImageIcon(lienzo);
    }

    private static ImageIcon crearIconoCubriendo(String ruta, int ancho, int alto) {
        BufferedImage original = cargarImagen(ruta);

        if (original == null) {
            return crearFallback(ancho, alto);
        }

        double escala = Math.max((double) ancho / original.getWidth(), (double) alto / original.getHeight());

        int nuevoAncho = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int nuevoAlto = Math.max(1, (int) Math.round(original.getHeight() * escala));

        Image escalada = original.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

        BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lienzo.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;

        g2.drawImage(escalada, x, y, null);
        g2.dispose();

        return new ImageIcon(lienzo);
    }

    private static BufferedImage cargarImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        try {
            URL recurso = GestorImagenes.class.getClassLoader().getResource(ruta);

            if (recurso != null) {
                BufferedImage imagen = ImageIO.read(recurso);

                if (imagen != null) {
                    return imagen;
                }
            }

            Path archivo = RutasAplicacion.buscarArchivo(ruta);

            if (archivo != null) {
                return ImageIO.read(archivo.toFile());
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private static ImageIcon crearFallback(int ancho, int alto) {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(TemaMars.PANEL_SECUNDARIO);
        g2.fillRoundRect(0, 0, ancho, alto, 14, 14);

        g2.setColor(TemaMars.BORDE);
        g2.drawRoundRect(1, 1, ancho - 3, alto - 3, 14, 14);

        g2.setColor(new Color(0x6A, 0x72, 0x74));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(11, Math.min(16, ancho / 9))));

        String texto = "SIN IMAGEN";
        int x = Math.max(4, (ancho - g2.getFontMetrics().stringWidth(texto)) / 2);
        int y = alto / 2 + g2.getFontMetrics().getAscent() / 2;

        g2.drawString(texto, x, y);
        g2.dispose();

        return new ImageIcon(imagen);
    }
}