/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import com.mycompany.tiendaapp.service.IncidenciaService;
import com.mycompany.tiendaapp.model.Incidencia;

public class PanelReportesDespacho extends JPanel {

    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);

    public PanelReportesDespacho() {

        this.setBackground(fondoOscuro);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("REPORTE DE INCIDENCIAS Y CIERRE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(naranjaAccento);

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBotonSecundario(btnVolver);

        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelDespachador());
            frame.revalidate();
            frame.repaint();
        });

        header.add(titulo, BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        // CUERPO CENTRAL
        JPanel contenedorCentro = new JPanel(new GridBagLayout());
        contenedorCentro.setBackground(fondoSecundario);
        contenedorCentro.setBorder(new CompoundBorder(
                new LineBorder(naranjaAccento, 1),
                new EmptyBorder(25, 40, 25, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        // Vehículo
        gbc.gridy = 0;
        contenedorCentro.add(crearEtiqueta("Vehículo con Inconveniente:", cremaTexto), gbc);

        JComboBox<String> cbVehiculos = new JComboBox<>();
        estilizarCombo(cbVehiculos);

        // Opción por defecto
        cbVehiculos.addItem("Ninguno (Día sin novedades)");

        try {
            com.mycompany.tiendaapp.dao.VehiculoDAO vehiculoDAO = new com.mycompany.tiendaapp.dao.VehiculoDAO();
            for (String v : vehiculoDAO.listarVehiculosParaCombo()) {
                cbVehiculos.addItem(v);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando vehículos desde la base de datos");
        }
        gbc.gridy = 1;
        contenedorCentro.add(cbVehiculos, gbc);

        // Observaciones
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 5, 0);
        contenedorCentro.add(crearEtiqueta("Descripción de la Falla / Observaciones:", cremaTexto), gbc);

        JTextArea txtFalla = new JTextArea(6, 30);
        txtFalla.setBackground(fondoOscuro);
        txtFalla.setForeground(Color.WHITE);
        txtFalla.setCaretColor(Color.WHITE);
        txtFalla.setLineWrap(true);
        txtFalla.setWrapStyleWord(true);
        txtFalla.setBorder(new LineBorder(naranjaAccento, 1));

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 0);
        contenedorCentro.add(new JScrollPane(txtFalla), gbc);

        // Botón generar reporte
        JButton btnDescargar = new JButton("GENERAR REPORTE Y CERRAR DÍA (PDF)");
        estilizarBotonPrincipal(btnDescargar);

        btnDescargar.addActionListener(e -> {
    String vehiculo = cbVehiculos.getSelectedItem().toString();
    String descripcion = txtFalla.getText().trim();

    if (!vehiculo.contains("Ninguno") && descripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Debe ingresar una descripción de la falla.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (vehiculo.contains("Ninguno")) {
        descripcion = "Sin novedades reportadas.";
    } else {
        // EXTRAER SOLO LA PLACA
        String placa = vehiculo.split(" - ")[0];
        Incidencia nueva = new Incidencia(placa, descripcion);
        IncidenciaService.agregarIncidencia(nueva);
    }

    JOptionPane.showMessageDialog(this,
            "Reporte generado y cierre del día completado.",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    frame.setContentPane(new PanelDespachador());
    frame.revalidate();
    frame.repaint();
});


        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        contenedorCentro.add(btnDescargar, gbc);

        this.add(contenedorCentro, BorderLayout.CENTER);
    }

    // MÉTODOS DE ESTILO
    private JLabel crearEtiqueta(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setBackground(fondoOscuro);
        cb.setForeground(Color.WHITE);
        cb.setBorder(new LineBorder(naranjaAccento, 1));
    }

    private void estilizarBotonPrincipal(JButton b) {
        b.setBackground(naranjaAccento);
        b.setForeground(fondoOscuro);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(300, 50));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new LineBorder(naranjaAccento, 1));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(naranjaAccento, 1));
        b.setPreferredSize(new Dimension(120, 35));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
