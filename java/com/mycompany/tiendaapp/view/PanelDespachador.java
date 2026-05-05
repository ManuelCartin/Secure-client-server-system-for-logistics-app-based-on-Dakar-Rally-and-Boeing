/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.controller.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PanelDespachador extends JPanel {

    private MainGUI main;
    private String rolActivo;
    private JTable tabla;

    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);

    // Constructor correcto
    public PanelDespachador(MainGUI main, String rolActivo) {
        this.main = main;
        this.rolActivo = rolActivo;
        initUI();
    }

    // Constructor fallback
    public PanelDespachador() {
        this.rolActivo = "despachador";
        initUI();
    }

    private void initUI() {
        setBackground(fondoOscuro);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel contenedorNorte = new JPanel(new BorderLayout(0, 15));
        contenedorNorte.setOpaque(false);

        // Texto dinámico según rol
        String textoBoton = "admin".equals(rolActivo) ? "⬅ VOLVER" : "🚪 CERRAR SESIÓN";

        JButton btnVolver = new JButton(textoBoton);
        btnVolver.setBackground(fondoSecundario);
        btnVolver.setForeground(naranjaAccento);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(new LineBorder(naranjaAccento));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVolver.addActionListener(e -> {
            if ("admin".equals(rolActivo)) {
                // ADMIN, vuelve al menú principal
                main.mostrarPanel(new PanelInicio(main));
            } else {
                // DESPACHADOR, cerrar sesión
                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Desea cerrar su sesión actual?",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (frame != null) frame.dispose();

                    LoginView lv = new LoginView();
                    new AuthController(lv);
                    lv.setVisible(true);
                }
            }
        });

        JPanel panelBotonWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelBotonWrap.setOpaque(false);
        panelBotonWrap.add(btnVolver);

        contenedorNorte.add(panelBotonWrap, BorderLayout.NORTH);
        this.add(contenedorNorte, BorderLayout.NORTH);

        // TABLA CENTRAL
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setOpaque(false);

        JLabel lblTabla = new JLabel("MONITOREO DE PAQUETES");
        lblTabla.setForeground(cremaTexto);
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 16));

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        configurarTabla(tabla, scroll);

        panelCentral.add(lblTabla, BorderLayout.NORTH);
        panelCentral.add(scroll, BorderLayout.CENTER);

        this.add(panelCentral, BorderLayout.CENTER);

        // BOTONES LATERALES
        JPanel panelAcciones = new JPanel(new GridLayout(5, 1, 0, 10));
        panelAcciones.setOpaque(false);
        panelAcciones.setPreferredSize(new Dimension(220, 0));

        JButton btnAsignar = crearBoton("ASIGNAR RUTA");
        btnAsignar.addActionListener(e -> cambiarPanel(new PanelAsignarRutaDetalle()));

        JButton btnEstado = crearBoton("CAMBIAR ESTADO");
        btnEstado.addActionListener(e -> cambiarPanel(new PanelEstadoPaquetes()));

        JButton btnReporte = crearBoton("GENERAR REPORTE");
        btnReporte.addActionListener(e -> cambiarPanel(new PanelReportesDespacho()));

        JButton btnPaquetes = crearBoton("GESTIÓN DE PAQUETES");
        btnPaquetes.addActionListener(e -> cambiarPanel(new PanelGestionPaquetesCliente()));

        JButton btnAlertar = crearBoton("ALERTAR CHOFER");
        btnAlertar.setBackground(new Color(192, 57, 43));
        btnAlertar.setForeground(Color.WHITE);
        btnAlertar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "⚠️ Alerta enviada al chofer")
        );

        panelAcciones.add(btnAsignar);
        panelAcciones.add(btnEstado);
        panelAcciones.add(btnReporte);
        panelAcciones.add(btnPaquetes);
        panelAcciones.add(btnAlertar);

        this.add(panelAcciones, BorderLayout.EAST);

        cargarPaquetes();
    }

    private void cambiarPanel(JPanel nuevo) {
        if (main != null) {
            main.mostrarPanel(nuevo);
        } else {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.setContentPane(nuevo);
                frame.revalidate();
            }
        }
    }

    private void cargarPaquetes() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request();
            req.setAction("listarPaquetes");
            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();
                DefaultTableModel modelo = new DefaultTableModel(
                        datos,
                        new String[]{"ID", "Chofer", "Último Punto", "Estado"}
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) { return false; }
                };
                tabla.setModel(modelo);
                aplicarRenderer(tabla);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando paquetes");
        }
    }

    private JButton crearBoton(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(naranjaAccento);
        b.setForeground(fondoOscuro);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(naranjaAccento));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void configurarTabla(JTable t, JScrollPane scroll) {
        t.setBackground(fondoSecundario);
        t.setForeground(cremaTexto);
        t.setGridColor(naranjaAccento);
        t.setRowHeight(35);
        t.setSelectionBackground(naranjaAccento);
        t.setSelectionForeground(Color.BLACK);
        t.setFillsViewportHeight(true);

        t.getTableHeader().setBackground(fondoOscuro);
        t.getTableHeader().setForeground(naranjaAccento);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        scroll.getViewport().setBackground(fondoSecundario);
        scroll.setBorder(new LineBorder(naranjaAccento));
        aplicarRenderer(t);
    }

    private void aplicarRenderer(JTable t) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(fondoSecundario);
        renderer.setForeground(cremaTexto);

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
}
