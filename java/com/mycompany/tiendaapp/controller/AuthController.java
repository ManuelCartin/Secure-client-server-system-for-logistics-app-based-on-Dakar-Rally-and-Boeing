/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.controller;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.dao.ChoferDAO;
import com.mycompany.tiendaapp.model.User;
import com.mycompany.tiendaapp.view.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class AuthController {

    private LoginView loginView;
    private String host = "127.0.0.1";
    private int port = 5555;

    public AuthController(LoginView lv) {
        this.loginView = lv;
        initActions();
    }

    private void initActions() {
        loginView.btnLogin.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String usuario = loginView.tfUsuario.getText().trim();
        String clave = new String(loginView.pfClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Complete todos los campos.");
            return;
        }

        try (ClientConnector conn = new ClientConnector(host, port)) {
            Request r = new Request("login");
            Map<String, Object> p = new HashMap<>();
            p.put("usuario", usuario);
            p.put("clave", clave);
            r.setPayload(p);

            Response resp = conn.sendRequest(r);

            if (resp.isSuccess()) {
                // Extraer el objeto User de la respuesta (necesario para el ID)
                User u = (User) resp.getData();
                if (u == null) {
                    JOptionPane.showMessageDialog(null, "Error: El servidor no envió datos de usuario.");
                    return;
                }

                String rol = resp.getMessage(); // El servidor devuelve el rol en el mensaje
                int idUsuario = u.getIdUsuario(); // Extrar el ID real

                JOptionPane.showMessageDialog(loginView, "Bienvenido, " + usuario);
                loginView.dispose();

                // Manejo de Paneles según Rol
                switch (rol.toLowerCase()) {

                    case "admin" -> {
                        // Ahora pasamos ROL e ID
                        MainGUI main = new MainGUI(u);
                        main.setVisible(true);
                    }

                    case "despachador" -> {
                        MainGUI main = new MainGUI(u);
                        main.setVisible(true);
                    }


                    case "marketing" -> {
                        MainGUI main = new MainGUI(u);
                        main.setVisible(true);
                    }


                    case "mantenimiento" -> {
                        MainGUI main = new MainGUI(u);
                        main.setVisible(true);
                    }

                    case "chofer" -> {
    ChoferDAO dao = new ChoferDAO();
    int idChofer = dao.obtenerIdChoferPorUsuario(idUsuario);

    if (idChofer <= 0) {
        JOptionPane.showMessageDialog(null, "Error: este usuario no está registrado como chofer.");
        return;
    }
    PanelChofer panel = new PanelChofer(null, idChofer, "chofer", usuario);

    JFrame frame = new JFrame("Panel del Chofer");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1200, 800);
    frame.setLocationRelativeTo(null);
    frame.setContentPane(panel);
    frame.setVisible(true);
    return;
}


                    default -> JOptionPane.showMessageDialog(null, "Rol no reconocido: " + rol);
                }

            } else {
                JOptionPane.showMessageDialog(loginView, resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (java.net.ConnectException ex) {
            JOptionPane.showMessageDialog(loginView, "❌ Error: El servidor no responde.", "Conexión Fallida", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(loginView, "Error técnico: " + ex.getMessage());
        }
    }
}