/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelEstadoPaquetes extends JPanel {

    private JTable tabla;
    private JComboBox<String> cbEstado;
    private JTextField txtCheck;

    // Colores
    Color fondoOscuro = new Color(23, 24, 29);
    Color fondoSecundario = new Color(41, 44, 53);
    Color naranjaAccento = new Color(224, 145, 69);
    Color cremaTexto = new Color(252, 217, 184);

    public PanelEstadoPaquetes() {

        setBackground(fondoOscuro);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        construirHeader();
        construirCuerpo();

        cargarTabla();
    }

    private void construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("ACTUALIZAR ESTADO DE PAQUETES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(naranjaAccento);

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBotonSecundario(btnVolver);

        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelDespachador());
            frame.revalidate();
        });

        header.add(titulo, BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void construirCuerpo() {
        JPanel panelCuerpo = new JPanel(new BorderLayout(20, 0));
        panelCuerpo.setOpaque(false);

        // Tabla
        tabla = new JTable();
        configurarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarDatosSeleccionados();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(fondoSecundario);
        scroll.setBorder(new LineBorder(naranjaAccento));

        panelCuerpo.add(scroll, BorderLayout.CENTER);

        // Panel derecho
        JPanel panelEdicion = new JPanel();
        panelEdicion.setLayout(new BoxLayout(panelEdicion, BoxLayout.Y_AXIS));
        panelEdicion.setBackground(fondoSecundario);
        panelEdicion.setPreferredSize(new Dimension(250, 0));
        panelEdicion.setBorder(new EmptyBorder(15, 15, 15, 15));

        agregarEtiqueta(panelEdicion, "Nuevo Estado:");
        cbEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN CAMINO", "ENTREGADO", "RETRASADO"});
        estilizarCombo(cbEstado);
        panelEdicion.add(cbEstado);

        panelEdicion.add(Box.createVerticalStrut(15));
        agregarEtiqueta(panelEdicion, "Ubicación del Checkpoint:");
        txtCheck = crearTextField();
        panelEdicion.add(txtCheck);

        panelEdicion.add(Box.createVerticalStrut(20));
        JButton btnActualizar = new JButton("ACTUALIZAR ESTADO");
        estilizarBotonPrincipal(btnActualizar);
        btnActualizar.addActionListener(e -> actualizarEstado());
        panelEdicion.add(btnActualizar);

        panelCuerpo.add(panelEdicion, BorderLayout.EAST);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    private void cargarTabla() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

            Request req = new Request();
            req.setAction("listarEstadoPaquetes");

            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();

                DefaultTableModel modelo = new DefaultTableModel(
                        datos,
                        new String[]{"ID", "Cliente", "Estado Actual", "Último Checkpoint"}
                );

                tabla.setModel(modelo);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando estados");
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        cbEstado.setSelectedItem(tabla.getValueAt(fila, 2));
        txtCheck.setText((String) tabla.getValueAt(fila, 3));
    }

    private void actualizarEstado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paquete");
            return;
        }

        int id = (int) tabla.getValueAt(fila, 0);
        String nuevoEstado = (String) cbEstado.getSelectedItem();
        String checkpoint = txtCheck.getText();

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

            Request req = new Request();
            req.setAction("actualizarEstadoPaquete");
            req.add("idPaquete", id);
            req.add("nuevoEstado", nuevoEstado);
            req.add("checkpoint", checkpoint);

            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Estado actualizado");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getMessage());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error conectando al servidor");
        }
    }

    private void agregarEtiqueta(JPanel panel, String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(cremaTexto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l);
    }

    private JTextField crearTextField() {
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        t.setBackground(fondoOscuro);
        t.setForeground(Color.WHITE);
        t.setBorder(new LineBorder(naranjaAccento));
        return t;
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cb.setBackground(fondoOscuro);
        cb.setForeground(Color.WHITE);
    }

    private void estilizarBotonPrincipal(JButton b) {
        b.setBackground(naranjaAccento);
        b.setForeground(fondoOscuro);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setBorder(new LineBorder(naranjaAccento));
        b.setPreferredSize(new Dimension(200, 35));
    }

    private void configurarTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(Color.WHITE);
        t.setRowHeight(30);
        t.getTableHeader().setBackground(naranjaAccento);
        t.getTableHeader().setForeground(fondoOscuro);
    }
}
