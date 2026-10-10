package com.mycompany.mars_colony.interfaz.comun;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

public final class TemaMars {

    public static final Color FONDO = new Color(0x14, 0x29, 0x36);
    public static final Color FONDO_PROFUNDO = new Color(0x0D, 0x1C, 0x26);
    public static final Color PANEL = new Color(0xF3, 0xEB, 0xDD);
    public static final Color PANEL_SECUNDARIO = new Color(0xE8, 0xDF, 0xD0);
    public static final Color ACCION = new Color(0xFF, 0xC4, 0x5A);
    public static final Color ACCION_OSCURA = new Color(0xE8, 0xA8, 0x35);
    public static final Color ENERGIA = new Color(0x6F, 0xE5, 0xEB);
    public static final Color CORRECTO = new Color(0xA3, 0xDD, 0x78);
    public static final Color ERROR = new Color(0xE9, 0x78, 0x65);
    public static final Color TEXTO = new Color(0x16, 0x29, 0x33);
    public static final Color TEXTO_SECUNDARIO = new Color(0x52, 0x61, 0x66);
    public static final Color TEXTO_CLARO = new Color(0xF7, 0xF2, 0xE9);
    public static final Color BORDE = new Color(0xC7, 0xBC, 0xA9);
    public static final Color BLOQUEADO = new Color(0x7A, 0x87, 0x8C);
    public static final Color SELECCION = new Color(0xCF, 0xF4, 0xF3);

    public static final int RADIO_PANEL = 18;
    public static final int RADIO_BOTON = 14;
    public static final int MARGEN_PANTALLA = 24;
    public static final int PADDING_PANEL = 20;
    public static final int ALTURA_BOTON = 60;

    private TemaMars() {
    }

    public static Font tituloGrande() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 38);
    }

    public static Font tituloPantalla() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 30);
    }

    public static Font tituloPanel() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 22);
    }

    public static Font textoNormal() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    }

    public static Font textoDestacado() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 18);
    }

    public static Font textoSecundario() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    }

    public static Font textoBoton() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 18);
    }

    public static Font textoPequeno() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }

    public static Border padding(int valor) {
        return BorderFactory.createEmptyBorder(valor, valor, valor, valor);
    }

    public static Border padding(int superior, int izquierda, int inferior, int derecha) {
        return BorderFactory.createEmptyBorder(superior, izquierda, inferior, derecha);
    }

    public static void aplicarGlobalmente() {
        UIManager.put("Label.font", textoNormal());
        UIManager.put("Button.font", textoBoton());
        UIManager.put("TextField.font", textoNormal());
        UIManager.put("PasswordField.font", textoNormal());
        UIManager.put("ComboBox.font", textoNormal());
        UIManager.put("Table.font", textoNormal());
        UIManager.put("TableHeader.font", textoDestacado());
        UIManager.put("TabbedPane.font", textoDestacado());
        UIManager.put("ScrollPane.background", PANEL);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("Table.selectionBackground", SELECCION);
        UIManager.put("Table.selectionForeground", TEXTO);
    }
}