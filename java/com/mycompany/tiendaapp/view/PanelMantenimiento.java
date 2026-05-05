/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.controller.AuthController;
import com.mycompany.tiendaapp.model.Incidencia;
import com.mycompany.tiendaapp.service.IncidenciaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PanelMantenimiento extends JPanel {

    private JTable tabla;
    private MainGUI main; // 1. Referencia al controlador principal
    private String rolActivo; // 2. Para saber quién opera

    Color fondoOscuro = new Color(23, 24, 29);
    Color fondoSecundario = new Color(41, 44, 53);
    Color naranjaAccento = new Color(224, 145, 69);
    Color cremaTexto = new Color(252, 217, 184);

    // Actualizar el constructor para recibir los parámetros
    public PanelMantenimiento(MainGUI main, String rolActivo) {
        this.main = main;
        this.rolActivo = rolActivo;

        setBackground(fondoOscuro);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel contenedorNorte = new JPanel(new BorderLayout());
        contenedorNorte.setOpaque(false);

        // Lógica Dual del Botón Volver
        String textoBoton = "admin".equals(rolActivo) ? "⬅ VOLVER" : "🚪 CERRAR SESIÓN";
        JButton btnVolver = new JButton(textoBoton);
        estilizarBoton(btnVolver, fondoSecundario, naranjaAccento);

        btnVolver.addActionListener(e -> {
            if ("admin".equals(rolActivo)) {
                // Comportamiento Admin: Regresar al menú
                main.mostrarPanel(new PanelInicio(main));
            } else {
                // Comportamiento Técnico: Cerrar Sesión
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Desea cerrar su sesión actual?", "Confirmar Salida", 
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

        contenedorNorte.add(btnVolver, BorderLayout.WEST);
        this.add(contenedorNorte, BorderLayout.NORTH);

        
        // TABLA
        tabla = new JTable();
        configurarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(naranjaAccento));

        add(scroll, BorderLayout.CENTER);

        // BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);

        JButton btnIngresar = new JButton("INGRESAR A TALLER");
        estilizarBoton(btnIngresar, naranjaAccento, fondoOscuro);

        JButton btnHistorial = new JButton("VER HISTORIAL");
        estilizarBoton(btnHistorial, fondoSecundario, cremaTexto);

        panelBotones.add(btnHistorial);
        panelBotones.add(btnIngresar);

        add(panelBotones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresarTaller());
        btnHistorial.addActionListener(e -> cargarVehiculos());

        cargarVehiculos();
    }

    private void cargarVehiculos() {
    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        Request r = new Request();
        r.setAction("listarVehiculos");

        Response res = conn.sendRequest(r);

        if (res.isSuccess()) {
            Object[][] datos = (Object[][]) res.getData();

            String[] columnas = {"Placa", "Modelo", "Km", "Estado", "Última Incidencia"};

            Object[][] datosExtendidos = new Object[datos.length][5];

            for (int i = 0; i < datos.length; i++) {
                String placa = datos[i][0].toString();

                datosExtendidos[i][0] = placa;
                datosExtendidos[i][1] = datos[i][1];
                datosExtendidos[i][2] = datos[i][2];
                datosExtendidos[i][3] = datos[i][3];

                // Buscar incidencia registrada por el despachador
                String incidencia = IncidenciaService.obtenerIncidenciaPorPlaca(placa);

                datosExtendidos[i][4] = incidencia != null ? incidencia : "Sin reportes";
            }

            DefaultTableModel modelo = new DefaultTableModel(datosExtendidos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            tabla.setModel(modelo);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error cargando vehículos");
    }
}


    private void ingresarTaller() {
    int fila = tabla.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la lista.");
        return;
    }

    String placa = tabla.getValueAt(fila, 0).toString();
    String incidenciaReportada = tabla.getValueAt(fila, 4).toString();
    JTextArea txtReparacion = new JTextArea(5, 20);
    txtReparacion.setLineWrap(true);
    txtReparacion.setWrapStyleWord(true);
    
    Object[] message = {
        "Placa: " + placa,
        "Reporte de Despacho: " + incidenciaReportada,
        "\nDiagnóstico y Reparación Realizada:",
        new JScrollPane(txtReparacion)
    };

    int option = JOptionPane.showConfirmDialog(this, message, "Gestionar Reparación", JOptionPane.OK_CANCEL_OPTION);

    if (option == JOptionPane.OK_OPTION) {
        String notasTecnicas = txtReparacion.getText().trim();
        if (notasTecnicas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el detalle de la reparación.");
            return;
        }
        ejecutarActualizacion(placa, notasTecnicas);
    }
}


    private void ejecutarActualizacion(String placa, String notas) {
    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
        Request r = new Request("completarMantenimiento");
        Map<String, Object> p = new HashMap<>();
        p.put("placa", placa);
        p.put("notas", notas);
        p.put("nuevoEstado", "DISPONIBLE");
        r.setPayload(p);

        Response res = conn.sendRequest(r);
        JOptionPane.showMessageDialog(this, res.getMessage());

        if (res.isSuccess()) {
            cargarVehiculos();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error conectando con el servidor");
    }
}

    private void estilizarBoton(JButton b, Color fondo, Color texto) {
    b.setBackground(fondo);
    b.setForeground(texto);
    b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    b.setFocusPainted(false);
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    b.setBorder(new LineBorder(naranjaAccento, 1));
    b.setPreferredSize(new Dimension(160, 40));
}

    private void configurarTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(cremaTexto);
        t.setRowHeight(30);
        t.getTableHeader().setBackground(naranjaAccento);
        t.getTableHeader().setForeground(fondoOscuro);
    }
}
