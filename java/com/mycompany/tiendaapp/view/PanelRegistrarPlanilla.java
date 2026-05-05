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

public class PanelRegistrarPlanilla extends JPanel {

    private JTable tabla;

    Color fondoOscuro = new Color(23, 24, 29);
    Color fondoSecundario = new Color(41, 44, 53);
    Color naranjaAccento = new Color(224, 145, 69);
    Color cremaTexto = new Color(252, 217, 184);

    public PanelRegistrarPlanilla() {

        setBackground(fondoOscuro);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBotonSecundario(btnVolver);

        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelInicio((MainGUI) frame));
            frame.revalidate();
        });

        JLabel lblTitulo = new JLabel("CONTROL OPERATIVO DE PERSONAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(naranjaAccento);

        panelNorte.add(btnVolver, BorderLayout.WEST);
        panelNorte.add(lblTitulo, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // CONTENIDO
        JPanel panelContenido = new JPanel(new BorderLayout(20, 0));
        panelContenido.setOpaque(false);

        // FORMULARIO IZQUIERDO
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(fondoSecundario);
        panelForm.setPreferredSize(new Dimension(350, 0));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(naranjaAccento, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JTextField txtNombre = crearTextField();
        JTextField txtDni = crearTextField();
        JTextField txtLicencia = crearTextField();
        JTextField txtSueldo = crearTextField();

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{
                "CHOFER", "DESPACHADOR"
        });
        estilizarCombo(cbTipo);

        agregarEtiqueta(panelForm, "Nombre Completo:");
        panelForm.add(txtNombre);

        panelForm.add(Box.createVerticalStrut(10));
        agregarEtiqueta(panelForm, "Tipo de Personal:");
        panelForm.add(cbTipo);

        panelForm.add(Box.createVerticalStrut(10));
        agregarEtiqueta(panelForm, "DNI:");
        panelForm.add(txtDni);

        panelForm.add(Box.createVerticalStrut(10));
        agregarEtiqueta(panelForm, "Licencia (Chofer):");
        panelForm.add(txtLicencia);

        panelForm.add(Box.createVerticalStrut(10));
        agregarEtiqueta(panelForm, "Sueldo:");
        panelForm.add(txtSueldo);

        panelForm.add(Box.createVerticalStrut(20));

        JButton btnGuardar = new JButton("REGISTRAR PERSONAL");
        estilizarBotonPrincipal(btnGuardar);
        panelForm.add(btnGuardar);

        // TABLA DERECHA
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setOpaque(false);

        JLabel lblTabla = new JLabel("PERSONAL REGISTRADO");
        lblTabla.setForeground(cremaTexto);
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabla = new JTable();
        configurarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(fondoSecundario);
        scroll.setBorder(new LineBorder(naranjaAccento));

        panelTabla.add(lblTabla, BorderLayout.NORTH);
        panelTabla.add(scroll, BorderLayout.CENTER);

        panelContenido.add(panelForm, BorderLayout.WEST);
        panelContenido.add(panelTabla, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);

        // EVENTOS
        btnGuardar.addActionListener(e -> {
            try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

                Request req = new Request();
                req.setAction("registrarPersonal");

                java.util.Map<String, Object> p = new java.util.HashMap<>();
                p.put("nombre", txtNombre.getText());
                p.put("rol", cbTipo.getSelectedItem().toString());
                p.put("dni", txtDni.getText());
                p.put("licencia", txtLicencia.getText());
                p.put("sueldo", txtSueldo.getText());

                req.setPayload(p);

                Response res = conn.sendRequest(req);

                JOptionPane.showMessageDialog(this, res.getMessage());

                if (res.isSuccess()) {
                    limpiarCampos(txtNombre, txtDni, txtLicencia, txtSueldo);
                    cargarPersonal();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error registrando personal");
            }
        });

        cargarPersonal();
    }

    private void cargarPersonal() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

            Request req = new Request();
            req.setAction("listarPlanilla");

            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {

                Object[][] datos = (Object[][]) res.getData();

                DefaultTableModel modelo = new DefaultTableModel(
                        datos,
                        new String[]{"ID", "Nombre", "Rol", "Sueldo", "Estado"}
                );

                tabla.setModel(modelo);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando planilla");
        }
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField c : campos) c.setText("");
    }

    private void agregarEtiqueta(JPanel panel, String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(cremaTexto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(l);
    }

    private JTextField crearTextField() {
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        t.setBackground(fondoOscuro);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(new LineBorder(naranjaAccento, 1));
        return t;
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
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setBorder(new LineBorder(naranjaAccento, 1));
    }

    private void configurarTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(cremaTexto);
        t.setRowHeight(30);
        t.setGridColor(fondoOscuro);
        t.setSelectionBackground(naranjaAccento);
        t.setSelectionForeground(fondoOscuro);
        t.getTableHeader().setBackground(naranjaAccento);
        t.getTableHeader().setForeground(fondoOscuro);
    }
}
