/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.controller.AuthController;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame {

    public static class Tema {
        public static final Color FONDO = new Color(23, 24, 29);
        public static final Color CAMPO = new Color(41, 44, 53);
        public static final Color BOTON = new Color(224, 145, 69);
        public static final Color TEXTO = new Color(252, 217, 184);
    }

    public JTextField tfUsuario = new JTextField(20);
    public JPasswordField pfClave = new JPasswordField(20);
    public JButton btnLogin = new JButton("Entrar");
    public JButton btnRegister = new JButton("Registrar");

    public LoginView() {
        super("Login - TiendaApp");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Tema.FONDO);
        p.setBorder(new EmptyBorder(20, 50, 10, 50));

        // Usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Tema.TEXTO);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        tfUsuario.setBackground(Tema.CAMPO);
        tfUsuario.setForeground(Tema.TEXTO);
        tfUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tfUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Tema.TEXTO);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        pfClave.setBackground(Tema.CAMPO);
        pfClave.setForeground(Tema.TEXTO);
        pfClave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pfClave.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Agregar al panel
        p.add(lblUser);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(tfUsuario);
        p.add(Box.createRigidArea(new Dimension(0, 15)));

        p.add(lblPass);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(pfClave);

        // Panel botones
        JPanel pButtons = new JPanel();
        pButtons.setBackground(Tema.FONDO);

        btnLogin.setBackground(Tema.BOTON);
        btnLogin.setForeground(Tema.FONDO);

        btnRegister.setBackground(Tema.CAMPO);
        btnRegister.setForeground(Tema.TEXTO);

        pButtons.add(btnLogin);
        pButtons.add(btnRegister);

        // Acción del botón Registrar
        btnRegister.addActionListener(e -> {
        JFrame frame = new JFrame("Crear Nueva Cuenta");
        frame.setSize(500, 600); 
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new PanelGestionUsuarios(null, "cliente", true)); 
        frame.setVisible(true);
    });

        getContentPane().setBackground(Tema.FONDO);
        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(pButtons, BorderLayout.SOUTH);
    }
}
