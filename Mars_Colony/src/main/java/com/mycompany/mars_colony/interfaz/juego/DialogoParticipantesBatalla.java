package com.mycompany.mars_colony.interfaz.juego;

import com.mycompany.mars_colony.interfaz.comun.BotonMars;
import com.mycompany.mars_colony.interfaz.comun.PanelMars;
import com.mycompany.mars_colony.interfaz.comun.TemaMars;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class DialogoParticipantesBatalla extends JDialog {

    private final List<ComponenteCombate> participantes;
    private final JTable tabla;
    private final Consumer<ComponenteCombate> accionSeleccionar;

    private DialogoParticipantesBatalla(Window propietario, List<ComponenteCombate> participantes, ComponenteCombate seleccionado, Consumer<ComponenteCombate> accionSeleccionar) {
        super(propietario, "Participantes", Dialog.ModalityType.APPLICATION_MODAL);

        this.participantes = new ArrayList<>(participantes);
        this.accionSeleccionar = accionSeleccionar;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(650, 450));
        setLocationRelativeTo(propietario);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(TemaMars.FONDO);
        raiz.setBorder(TemaMars.padding(20));

        PanelMars tarjeta = new PanelMars();
        tarjeta.setLayout(new BorderLayout(0, 15));

        DefaultTableModel modelo = new DefaultTableModel(new String[]{"Unidad", "Tipo", "Vida", "Estado", "Posición"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        DecimalFormat formato = new DecimalFormat("0.##");

        for (ComponenteCombate componente : this.participantes) {
            if (componente == null) {
                continue;
            }

            modelo.addRow(new Object[]{
                componente.getNombre(),
                componente.getClass().getSimpleName(),
                formato.format(componente.getVidaActual()) + " / " + formato.format(componente.getVidaMaxima()),
                componente.estaOperativo() ? "Activo" : "Destruido",
                componente.getPosicion() == null ? "-" : componente.getPosicion().toString()
            });
        }

        tabla = new JTable(modelo);
        tabla.setRowHeight(42);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setBackground(TemaMars.PANEL);
        tabla.setForeground(TemaMars.TEXTO);
        tabla.getTableHeader().setBackground(TemaMars.FONDO);
        tabla.getTableHeader().setForeground(TemaMars.TEXTO_CLARO);

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionar();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);

        BotonMars cancelar = BotonMars.secundario("Cancelar");
        cancelar.addActionListener(e -> dispose());

        BotonMars seleccionar = BotonMars.primario("SELECCIONAR");
        seleccionar.addActionListener(e -> seleccionar());

        JPanel acciones = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        acciones.setOpaque(false);
        acciones.add(cancelar);
        acciones.add(seleccionar);

        tarjeta.add(scroll, BorderLayout.CENTER);
        tarjeta.add(acciones, BorderLayout.SOUTH);

        raiz.add(tarjeta, BorderLayout.CENTER);

        setContentPane(raiz);

        seleccionarActual(seleccionado);
    }

    private void seleccionarActual(ComponenteCombate seleccionado) {
        if (seleccionado == null) {
            if (tabla.getRowCount() > 0) {
                tabla.setRowSelectionInterval(0, 0);
            }

            return;
        }

        for (int i = 0; i < participantes.size(); i++) {
            ComponenteCombate componente = participantes.get(i);

            if (componente != null && componente.getId().equals(seleccionado.getId())) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();

        if (fila < 0 || fila >= participantes.size()) {
            return;
        }

        ComponenteCombate componente = participantes.get(fila);

        if (accionSeleccionar != null) {
            accionSeleccionar.accept(componente);
        }

        dispose();
    }

    public static void mostrar(Component padre, List<ComponenteCombate> participantes, ComponenteCombate seleccionado, Consumer<ComponenteCombate> accionSeleccionar) {
        Window ventana = padre == null ? null : SwingUtilities.getWindowAncestor(padre);

        DialogoParticipantesBatalla dialogo = new DialogoParticipantesBatalla(
                ventana,
                participantes,
                seleccionado,
                accionSeleccionar
        );

        dialogo.setVisible(true);
    }
}