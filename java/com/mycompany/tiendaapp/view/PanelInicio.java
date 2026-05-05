/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.controller.AuthController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PanelInicio extends JPanel {

    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);

private MainGUI main;

public PanelInicio(MainGUI main) {
    this.main = main; 
    
    setBackground(fondoOscuro);
    setLayout(new BorderLayout(0, 30));
    setBorder(new EmptyBorder(50, 50, 50, 50));

    // HEADER
    JLabel titulo = new JLabel("SISTEMA DE GESTIÓN Y MONITOREO", SwingConstants.CENTER);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
    titulo.setForeground(naranjaAccento);
    add(titulo, BorderLayout.NORTH);

    // GRID DE BOTONES
    JPanel gridBotones = new JPanel(new GridLayout(2, 3, 25, 25));
    gridBotones.setOpaque(false);

        // GESTIÓN DE USUARIOS
    gridBotones.add(crearBotonGrande("GESTIÓN DE USUARIOS", "👥",
        () -> main.mostrarPanel(new PanelGestionUsuarios(main, main.getRol(), false))));
    gridBotones.add(crearBotonGrande("PLANILLA", "📋",
            () -> main.mostrarPanel(new PanelRegistrarPlanilla())));

    gridBotones.add(crearBotonGrande("MANTENIMIENTO", "🛠️",
        () -> main.mostrarPanel(new PanelMantenimiento(main, main.getRol()))));

    gridBotones.add(crearBotonGrande("TRACKING", "📍",
    () -> main.mostrarPanel(new PanelTracking(main, main.getRol(), main.getIdUsuario()))));
    gridBotones.add(crearBotonGrande("SUPERVISAR DESPACHO", "📦",
        () -> main.mostrarPanel(new PanelDespachador(main, main.getRol()))));


    gridBotones.add(crearBotonGrande("Chofer", "🚚", () -> {
    abrirRutaChoferComoAdmin(); 
}));
    add(gridBotones, BorderLayout.CENTER);

    // BOTÓN CERRAR SESIÓN
    JButton btnSalir = new JButton("🚪 CERRAR SESIÓN");
    estilizarBotonSalida(btnSalir);
    btnSalir.addActionListener(e -> {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) frame.dispose();
        LoginView lv = new LoginView();
        new AuthController(lv);
        lv.setVisible(true);
    });

    JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panelInferior.setOpaque(false);
    panelInferior.add(btnSalir);
    add(panelInferior, BorderLayout.SOUTH);
}
    private JButton crearBotonGrande(String texto, String icono, Runnable accion) {
        JButton btn = new JButton("<html><center><font size='7'>" + icono +
                "</font><br><br>" + texto + "</center></html>");

        btn.setBackground(fondoSecundario);
        btn.setForeground(cremaTexto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(naranjaAccento, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> accion.run());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(naranjaAccento);
                btn.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(fondoSecundario);
                btn.setForeground(cremaTexto);
            }
        });

        return btn;
    }
    

private void abrirRutaChoferComoAdmin() {
    String input = JOptionPane.showInputDialog(
            this,
            "Ingrese el ID de la ruta que desea visualizar:",
            "Supervisión de Ruta",
            JOptionPane.QUESTION_MESSAGE
    );

    if (input == null || input.isBlank()) return;

    try {
        int idRuta = Integer.parseInt(input);
        main.mostrarPanel(new PanelChofer(main, idRuta, "admin", null));

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    }
}

    private void estilizarBotonSalida(JButton btn) {
        btn.setBackground(fondoSecundario);
        btn.setForeground(cremaTexto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(naranjaAccento, 1));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(180, 60, 60));
                btn.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(fondoSecundario);
                btn.setForeground(cremaTexto);
            }
        });
    }
}