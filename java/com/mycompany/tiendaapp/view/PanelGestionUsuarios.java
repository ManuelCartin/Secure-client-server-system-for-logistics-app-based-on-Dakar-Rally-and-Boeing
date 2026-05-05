/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.*;
import com.mycompany.tiendaapp.controller.AuthController;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PanelGestionUsuarios extends JPanel {

    private JTable tabla;
    private JTextField tfNombre, tfUsuario, tfCorreo;
    private JPasswordField pfClave;
    private JComboBox<String> cbRol;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnRefrescar, btnVolver;
    private JScrollPane scroll;
    
    private String rolActivo; 
    private MainGUI main;
    private boolean esRegistroExterno;

    public PanelGestionUsuarios(MainGUI main, String rolActivo, boolean esRegistroExterno) {
        this.main = main;
        this.rolActivo = rolActivo;
        this.esRegistroExterno = esRegistroExterno;

        setLayout(new BorderLayout());
        setBackground(new Color(23, 24, 29));

        // CREACIÓN DE COMPONENTES
        btnVolver = new JButton();
        tabla = new JTable();
        scroll = new JScrollPane(tabla);
        tfNombre = new JTextField();
        tfUsuario = new JTextField();
        tfCorreo = new JTextField();
        pfClave = new JPasswordField();
        cbRol = new JComboBox<>(new String[]{
            "admin", "chofer", "despachador", "marketing", "mantenimiento", "cliente"
        });
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnRefrescar = new JButton("Refrescar");

        // CONFIGURACIÓN DEL BOTÓN VOLVER / SALIR
        String textoBoton = "admin".equals(rolActivo) ? "⬅ VOLVER" : "🚪 CERRAR SESIÓN";
        btnVolver.setText(textoBoton);
        btnVolver.setBackground(new Color(41, 44, 53));
        btnVolver.setForeground(new Color(224, 145, 69));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if ("admin".equals(rolActivo)) {
                main.mostrarPanel(new PanelInicio(main));
            } else {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cerrar su sesión?", "Salir", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (frame != null) frame.dispose();
                    LoginView lv = new LoginView();
                    new AuthController(lv);
                    lv.setVisible(true);
                }
            }
        });

        // ARMADO DE LA INTERFAZ
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(btnVolver, BorderLayout.WEST);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBackground(new Color(23, 24, 29));
        form.add(label("Nombre:")); form.add(tfNombre);
        form.add(label("Usuario:")); form.add(tfUsuario);
        form.add(label("Clave:")); form.add(pfClave);
        form.add(label("Correo:")); form.add(tfCorreo);
        form.add(label("Rol:")); form.add(cbRol);
        northPanel.add(form, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel();
        botonesPanel.setBackground(new Color(23, 24, 29));
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnActualizar);
        botonesPanel.add(btnEliminar);
        botonesPanel.add(btnRefrescar);
        add(botonesPanel, BorderLayout.SOUTH);

        // APLICAR LÓGICA DE RESTRICCIÓN
        if (esRegistroExterno) {
            cbRol.setSelectedItem("cliente");
            cbRol.setEnabled(false);
            btnActualizar.setVisible(false);
            btnEliminar.setVisible(false);
            btnRefrescar.setVisible(false);
            scroll.setVisible(false); 
            btnRegistrar.setText("CREAR MI CUENTA");
        } else if (!"admin".equals(rolActivo)) {
            // Si es un empleado pero no es admin, no puede editar ni borrar otros
            btnActualizar.setVisible(false);
            btnEliminar.setVisible(false);
        }

        // EVENTOS
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefrescar.addActionListener(e -> cargarTabla());
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        if (!esRegistroExterno) {
            cargarTabla();
        }
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(252, 217, 184));
        return l;
    }

    private void cargarTabla() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request r = new Request("listarUsuarios");
            Response resp = conn.sendRequest(r);
            if (resp.isSuccess()) {
                Object[][] datos = (Object[][]) resp.getData();
                tabla.setModel(new javax.swing.table.DefaultTableModel(
                        datos,
                        new String[]{"ID", "Nombre", "Usuario", "Correo", "Rol"}
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        tfNombre.setText(tabla.getValueAt(row, 1).toString());
        tfUsuario.setText(tabla.getValueAt(row, 2).toString());
        tfCorreo.setText(tabla.getValueAt(row, 3).toString());
        cbRol.setSelectedItem(tabla.getValueAt(row, 4).toString());
        pfClave.setText(""); 
    }

    private boolean validarPermisoAdmin(String accion) {
        if (!"admin".equals(rolActivo)) {
            JOptionPane.showMessageDialog(this, "Acceso denegado: solo un administrador puede " + accion + ".");
            return false;
        }
        return true;
    }

    private void registrar() {
        if (!esRegistroExterno && !validarPermisoAdmin("registrar usuarios")) return;

        String rolSeleccionado = cbRol.getSelectedItem().toString();
        String clave = new String(pfClave.getPassword());

        if (!rolSeleccionado.equals("cliente") && clave.isBlank()) {
            JOptionPane.showMessageDialog(this, "Los empleados deben tener una contraseña.");
            return;
        }

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request r = new Request("register");
            Map<String, Object> p = new HashMap<>();
            p.put("nombre", tfNombre.getText());
            p.put("usuario", tfUsuario.getText());
            p.put("clave", clave);
            p.put("correo", tfCorreo.getText());
            p.put("rol", rolSeleccionado);

            r.setPayload(p);
            Response resp = conn.sendRequest(r);
            JOptionPane.showMessageDialog(this, resp.getMessage());
            
            if (resp.isSuccess()) {
                limpiarCampos();
                if (!esRegistroExterno) {
                    cargarTabla();
                } else {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (frame != null) frame.dispose();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void actualizar() {
        if (!validarPermisoAdmin("actualizar usuarios")) return;
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        int id = (int) tabla.getValueAt(row, 0);
        String clave = new String(pfClave.getPassword());

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request r = new Request("updateUser");
            Map<String, Object> p = new HashMap<>();
            p.put("idUsuario", id);
            p.put("nombre", tfNombre.getText());
            p.put("usuario", tfUsuario.getText());
            p.put("correo", tfCorreo.getText());
            p.put("rol", cbRol.getSelectedItem().toString());
            p.put("clave", clave.isBlank() ? null : clave);
            r.setPayload(p);
            Response resp = conn.sendRequest(r);
            JOptionPane.showMessageDialog(this, resp.getMessage());
            if (resp.isSuccess()) cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void eliminar() {
        if (!validarPermisoAdmin("eliminar usuarios")) return;
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        int id = (int) tabla.getValueAt(row, 0);
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar ID: " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request r = new Request("deleteUser");
            Map<String, Object> p = new HashMap<>();
            p.put("idUsuario", id);
            r.setPayload(p);
            Response resp = conn.sendRequest(r);
            JOptionPane.showMessageDialog(this, resp.getMessage());
            if (resp.isSuccess()) cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfUsuario.setText("");
        tfCorreo.setText("");
        pfClave.setText("");
        cbRol.setSelectedIndex(0);
    }
}