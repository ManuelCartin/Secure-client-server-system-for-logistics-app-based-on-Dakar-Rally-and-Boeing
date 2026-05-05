/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.dao.ChoferDAO;
import com.mycompany.tiendaapp.model.User;
import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    private final String rol;
    private final int idUsuario;
    private User usuarioLogueado;
    public void setUsuarioLogueado(User u) { this.usuarioLogueado = u; }
    public User getUsuarioLogueado() { return usuarioLogueado; }

    
    public MainGUI(User usuario) {
    this.usuarioLogueado = usuario;
    this.rol = usuario.getRol();
    this.idUsuario = usuario.getIdUsuario();

    setTitle("Quick Delivery SA - Sistema de Gestión");
    setSize(1200, 850);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    configurarSegunRol();
}



    private void configurarSegunRol() {

    if (rol == null) {
        JOptionPane.showMessageDialog(this, "Error: Rol nulo.");
        this.dispose();
        return;
    }

    switch (rol.toLowerCase()) {

        case "admin" ->
            mostrarPanel(new PanelInicio(this));

        case "marketing", "cliente" ->
            mostrarPanel(new PanelTracking(this, rol, idUsuario));

        case "despachador" ->
            mostrarPanel(new PanelDespachador(this, rol));

        case "mantenimiento" ->
            mostrarPanel(new PanelMantenimiento(this, rol));

        case "chofer" -> {

            ChoferDAO choferDAO = new ChoferDAO();
            int idChofer = choferDAO.obtenerIdChoferPorUsuario(usuarioLogueado.getIdUsuario());

            if (idChofer <= 0) {
                JOptionPane.showMessageDialog(this, "Este usuario no está registrado como chofer.");
                return;
            }

            int idRuta = obtenerRutaDelChofer(idChofer);

            if (idRuta <= 0) {
                JOptionPane.showMessageDialog(this, "No tienes rutas asignadas hoy.");
                return;
            }

            mostrarPanel(new PanelChofer(this, idRuta, "chofer", usuarioLogueado.getUsuario()));
        }

        default -> {
            JOptionPane.showMessageDialog(this, "Acceso no autorizado.");
            this.dispose();
        }
    }
}



    public String getRol() { return rol; }
    public int getIdUsuario() { return idUsuario; }

    public void mostrarPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private int obtenerRutaDelChofer(int idChofer) {
    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        Request req = new Request();
        req.setAction("obtenerRutaActivaDelChofer");

        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("idChofer", idChofer);
        req.setPayload(p);

        Response res = conn.sendRequest(req);

        if (res.isSuccess()) {
            return (int) res.getData(); // Devuelve el idRuta
        } else {
            JOptionPane.showMessageDialog(this, res.getMessage());
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error obteniendo ruta del chofer.");
    }

    return -1; // por si acaso
}

}