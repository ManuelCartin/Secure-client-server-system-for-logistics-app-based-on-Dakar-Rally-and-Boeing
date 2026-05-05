/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;

public class PanelGestionPaquetesCliente extends JPanel {

    private JTable tabla;
    private JComboBox<String> cmbClientes;
    private JComboBox<String> cmbChofer;
    private JComboBox<String> cmbVehiculo;
    private Map<String, Integer> mapaClientes = new HashMap<>();

    private JTextField txtDescripcion;
    private JTextField txtDestino;
    private JComboBox<String> cmbEstado;

    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);

    public PanelGestionPaquetesCliente() {
        setLayout(new BorderLayout(15, 15));
        setBackground(fondoOscuro);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // PANEL NORTE
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBotonSecundario(btnVolver);
        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelDespachador());
            frame.revalidate();
        });

        JLabel lblTitulo = new JLabel("GESTIÓN DE PAQUETES POR CLIENTE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(naranjaAccento);

        panelNorte.add(btnVolver, BorderLayout.WEST);
        panelNorte.add(lblTitulo, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // PANEL CENTRAL
        JPanel panelCentral = new JPanel(new BorderLayout(25, 0));
        panelCentral.setOpaque(false);

        // FORMULARIO
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(fondoSecundario);
        panelForm.setPreferredSize(new Dimension(350, 0));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(naranjaAccento),
                new EmptyBorder(20, 20, 20, 20)
        ));

        agregarCampo(panelForm, "Cliente:");
        cmbClientes = new JComboBox<>();
        estilizarCombo(cmbClientes);
        panelForm.add(cmbClientes);

        agregarCampo(panelForm, "Descripción:");
        txtDescripcion = crearTextField();
        panelForm.add(txtDescripcion);

        agregarCampo(panelForm, "Destino:");
        txtDestino = crearTextField();
        panelForm.add(txtDestino);

        agregarCampo(panelForm, "Estado:");
        cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN_RUTA", "ENTREGADO"});
        estilizarCombo(cmbEstado);
        panelForm.add(cmbEstado);

        agregarCampo(panelForm, "Chofer asignado:");
        cmbChofer = new JComboBox<>();
        estilizarCombo(cmbChofer);
        panelForm.add(cmbChofer);

        agregarCampo(panelForm, "Vehículo asignado:");
        cmbVehiculo = new JComboBox<>();
        estilizarCombo(cmbVehiculo);
        panelForm.add(cmbVehiculo);

        panelForm.add(Box.createVerticalStrut(15));

        JButton btnRegistrar = new JButton("REGISTRAR PAQUETE");
        estilizarBotonPrincipal(btnRegistrar);
        panelForm.add(btnRegistrar);
        panelForm.add(Box.createVerticalStrut(5));

        JButton btnActualizar = new JButton("ACTUALIZAR");
        estilizarBotonPrincipal(btnActualizar);
        panelForm.add(btnActualizar);
        panelForm.add(Box.createVerticalStrut(5));

        JButton btnEliminar = new JButton("ELIMINAR");
        estilizarBotonPrincipal(btnEliminar);
        panelForm.add(btnEliminar);
        panelForm.add(Box.createVerticalStrut(5));

        JButton btnAsignarChofer = new JButton("ASIGNAR A CHOFER");
        estilizarBotonPrincipal(btnAsignarChofer);
        panelForm.add(btnAsignarChofer);

        // TABLA
        JPanel panelTablaContenedor = new JPanel(new BorderLayout());
        panelTablaContenedor.setOpaque(false);

        JLabel lblTabla = new JLabel("PAQUETES DEL CLIENTE");
        lblTabla.setForeground(cremaTexto);
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTabla.setBorder(new EmptyBorder(0, 0, 10, 0));
        panelTablaContenedor.add(lblTabla, BorderLayout.NORTH);

        // inicializar el modelo y la tabla UNA SOLA VEZ
        String[] columnas = {"ID", "Descripción", "Destino", "Estado"};
        DefaultTableModel modeloInicial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloInicial); 
        configurarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(naranjaAccento));
        scroll.getViewport().setBackground(fondoSecundario);
        panelTablaContenedor.add(scroll, BorderLayout.CENTER);

        panelCentral.add(panelForm, BorderLayout.WEST);
        panelCentral.add(panelTablaContenedor, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // EVENTOS
        cmbClientes.addActionListener(e -> cargarPaquetesCliente());
        btnRegistrar.addActionListener(e -> registrarPaquete());
        btnActualizar.addActionListener(e -> actualizarPaquete());
        btnEliminar.addActionListener(e -> eliminarPaquete());
        btnAsignarChofer.addActionListener(e -> asignarPaqueteAChofer());
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        cargarClientesSeguro();
        cargarChoferes(cmbChofer);
        cargarVehiculos(cmbVehiculo);
    }

    private void cargarPaquetesCliente() {
        String cliente = (String) cmbClientes.getSelectedItem();
        if (cliente == null || !mapaClientes.containsKey(cliente)) return;

        int idCliente = mapaClientes.get(cliente);

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("listarPaquetesCliente");
            Map<String, Object> p = new HashMap<>();
            p.put("idCliente", idCliente);
            req.setPayload(p);

            // Se recibe la respuesta y se valida antes de usar
            Response res = conn.sendRequest(req); 

            if (res != null && res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();
                String[] columnas = {"ID", "Descripción", "Destino", "Estado"};

                if (datos == null) datos = new Object[0][4];

                tabla.setModel(new DefaultTableModel(datos, columnas) {
                    @Override
                    public boolean isCellEditable(int r, int c) { return false; }
                });

                configurarTabla(tabla);
                tabla.revalidate();
                tabla.repaint();
            }
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    // Métodos de carga y eventos
    private void cargarClientes() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("listarUsuarios");
            Response res = conn.sendRequest(req);
            if (res != null && res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();
                cmbClientes.removeAllItems();
                mapaClientes.clear();
                for (Object[] fila : datos) {
                    if ("cliente".equals(fila[4].toString().toLowerCase())) {
                        String nombre = fila[1].toString();
                        int id = (int) fila[0];
                        cmbClientes.addItem(nombre);
                        mapaClientes.put(nombre, id);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        txtDescripcion.setText(tabla.getValueAt(row, 1).toString());
        txtDestino.setText(tabla.getValueAt(row, 2).toString());
        cmbEstado.setSelectedItem(tabla.getValueAt(row, 3).toString());
    }

    private void registrarPaquete() {
        String cliente = (String) cmbClientes.getSelectedItem();
        if (cliente == null) return;
        int idCliente = mapaClientes.get(cliente);

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("registrarPaquete");
            Map<String, Object> p = new HashMap<>();
            p.put("idCliente", idCliente);
            p.put("descripcion", txtDescripcion.getText());
            p.put("destino", txtDestino.getText());
            req.setPayload(p);
            Response res = conn.sendRequest(req);
            if (res != null) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                if (res.isSuccess()) cargarPaquetesCliente();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarPaquete() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        int idPaquete = Integer.parseInt(tabla.getValueAt(row, 0).toString());

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("actualizarPaquete");
            Map<String, Object> p = new HashMap<>();
            p.put("idPaquete", idPaquete);
            p.put("descripcion", txtDescripcion.getText());
            p.put("destino", txtDestino.getText());
            p.put("estado", cmbEstado.getSelectedItem().toString());
            req.setPayload(p);
            Response res = conn.sendRequest(req);
            if (res != null && res.isSuccess()) cargarPaquetesCliente();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void eliminarPaquete() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        int idPaquete = (int) tabla.getValueAt(row, 0);
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("eliminarPaquete");
            Map<String, Object> p = new HashMap<>();
            p.put("idPaquete", idPaquete);
            req.setPayload(p);
            Response res = conn.sendRequest(req);
            if (res != null && res.isSuccess()) cargarPaquetesCliente();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void asignarPaqueteAChofer() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        int idPaquete = Integer.parseInt(tabla.getValueAt(row, 0).toString());

        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("asignarPaqueteChofer");
            Map<String, Object> p = new HashMap<>();
            p.put("idPaquete", idPaquete);
            p.put("chofer", cmbChofer.getSelectedItem().toString());
            p.put("vehiculo", cmbVehiculo.getSelectedItem().toString());
            p.put("estado", "EN_RUTA");
            req.setPayload(p);
            Response res = conn.sendRequest(req);
            if (res != null) {
                if (res.isSuccess()) cargarPaquetesCliente();
                else JOptionPane.showMessageDialog(this, res.getMessage());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarChoferes(JComboBox<String> cb) {
    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
        Request req = new Request("listarChoferes");
        Response res = conn.sendRequest(req);

        if (res != null && res.isSuccess()) {
            Object[][] datos = (Object[][]) res.getData();
            cb.removeAllItems();

            for (Object[] fila : datos) {
                cb.addItem(fila[1].toString());
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void cargarVehiculos(JComboBox<String> cb) {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request req = new Request("listarVehiculos");
            Response res = conn.sendRequest(req);
            if (res != null && res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();
                cb.removeAllItems();
                for (Object[] fila : datos) cb.addItem(fila[1].toString());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarClientesSeguro() {
        for (var l : cmbClientes.getActionListeners()) cmbClientes.removeActionListener(l);
        cargarClientes();
        cmbClientes.addActionListener(e -> cargarPaquetesCliente());
    }

    // Estilización
    private void configurarTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(Color.WHITE);
        t.setRowHeight(30);
        t.setGridColor(fondoOscuro);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setBackground(fondoOscuro);
        t.getTableHeader().setForeground(naranjaAccento);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setSelectionBackground(naranjaAccento);
        t.setSelectionForeground(fondoOscuro);
    }

    private void agregarCampo(JPanel panel, String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(cremaTexto);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
    }

    private JTextField crearTextField() {
        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        campo.setBackground(Color.WHITE);
        campo.setBorder(new LineBorder(naranjaAccento));
        return campo;
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(fondoOscuro);
        cb.setForeground(cremaTexto);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    private void estilizarBotonPrincipal(JButton b) {
        b.setBackground(naranjaAccento);
        b.setForeground(fondoOscuro);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setBorder(new LineBorder(naranjaAccento));
        b.setFocusPainted(false);
    }
}