/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.model.PuntoEntrega;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class PanelAsignarRutaDetalle extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<PuntoEntrega> comboZonas;
    private JComboBox<String> comboProvincia;
    private JXMapViewer mapViewer;
    private java.util.List<java.util.Map<String, Object>> roadbook = new java.util.ArrayList<>();
    private java.util.Set<Waypoint> waypoints = new java.util.HashSet<>();




    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);

    public PanelAsignarRutaDetalle() {

        setBackground(fondoOscuro);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // HEADER

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("ASIGNACIÓN DE RUTAS");
        titulo.setForeground(naranjaAccento);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBoton(btnVolver);

        btnVolver.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelDespachador());
            frame.revalidate();
        });

        header.add(titulo, BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // PANEL IZQUIERDO

        JPanel panelIzq = new JPanel(new GridLayout(6, 1, 10, 10));
        panelIzq.setBackground(fondoSecundario);
        panelIzq.setBorder(new EmptyBorder(15, 15, 15, 15));

        comboProvincia = new JComboBox<>(new String[]{"Seleccione", "San José", "Alajuela", "Heredia"});
        comboZonas = new JComboBox<>();
        comboZonas.addItem(PuntoEntrega.Seleccione);

        JComboBox<String> cbVehiculo = new JComboBox<>(new String[]{"Vehículo 1", "Vehículo 2"});

        estilizarCombo(comboProvincia);
        estilizarCombo(comboZonas);
        estilizarCombo(cbVehiculo);

        panelIzq.add(comboProvincia);
        panelIzq.add(comboZonas);
        panelIzq.add(cbVehiculo);
        
        JButton btnAgregarCP = new JButton("AGREGAR CHECKPOINT");
        estilizarBotonPrincipal(btnAgregarCP);
        panelIzq.add(btnAgregarCP);
        
        btnAgregarCP.addActionListener(e -> agregarCheckpoint());



        JButton btnAsignar = new JButton("CONFIRMAR RUTA");
        estilizarBotonPrincipal(btnAsignar);
        panelIzq.add(btnAsignar);

        add(panelIzq, BorderLayout.WEST);

        // MAPA
        JPanel panelMapa = new JPanel(new BorderLayout());
        panelMapa.setBorder(new LineBorder(naranjaAccento));
        inicializarMapa(panelMapa);
        add(panelMapa, BorderLayout.CENTER);

        // TABLA
        modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Destino", "Estado"}
        );

        tabla = new JTable(modelo);
        configurarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.setBorder(new LineBorder(naranjaAccento));
        scroll.getViewport().setBackground(fondoSecundario);

        add(scroll, BorderLayout.SOUTH);

        // EVENTOS
        comboProvincia.addActionListener(e -> cargarZonas());

        comboZonas.addActionListener(e -> {
            PuntoEntrega p = (PuntoEntrega) comboZonas.getSelectedItem();
            if (p != null && p != PuntoEntrega.Seleccione) {
                mapViewer.setAddressLocation(new GeoPosition(p.getLatitud(), p.getLongitud()));
                mapViewer.setZoom(7);
            }
        });

        btnAsignar.addActionListener(e -> asignarRuta());

        cargarPaquetes();
    }

    private void configurarTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(Color.WHITE);
        t.setRowHeight(30);
        t.setGridColor(fondoOscuro);
        t.setFillsViewportHeight(true);

        t.getTableHeader().setBackground(fondoOscuro);
        t.getTableHeader().setForeground(naranjaAccento);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBorder(new LineBorder(naranjaAccento));

        t.setDefaultRenderer(Object.class, new EstadoRenderer());
    }

    private void inicializarMapa(JPanel contenedor) {
        System.setProperty("http.agent", "QuickDeliveryApp_Fidelitas");
        mapViewer = new JXMapViewer();

        OSMTileFactoryInfo info = new OSMTileFactoryInfo(
                "QuickDeliveryApp_Fidelitas",
                "https://tile.openstreetmap.org"
        );

        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(8);
        mapViewer.setTileFactory(tileFactory);

        mapViewer.setAddressLocation(new GeoPosition(9.9333, -84.0833));
        mapViewer.setZoom(5);
        mapViewer.setBackground(fondoSecundario);

        PanMouseInputListener mm = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mm);
        mapViewer.addMouseMotionListener(mm);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        contenedor.add(mapViewer, BorderLayout.CENTER);
        
       mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {

            if (SwingUtilities.isLeftMouseButton(e)) {

                // Convertir click a coordenadas reales
                Point punto = e.getPoint();
                GeoPosition pos = mapViewer.convertPointToGeoPosition(punto);

                double lat = pos.getLatitude();
                double lon = pos.getLongitude();

                // Confirmación visual
                JOptionPane.showMessageDialog(null,
                        "Checkpoint agregado en:\nLat: " + lat + "\nLon: " + lon);

                agregarCheckpointDesdeMapa(lat, lon);
            }
    }
});
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(fondoOscuro);
        cb.setForeground(Color.WHITE);
        cb.setBorder(new LineBorder(naranjaAccento));
    }

    private void estilizarBoton(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setBorder(new LineBorder(naranjaAccento));
        b.setFocusPainted(false);
    }

    private void estilizarBotonPrincipal(JButton b) {
        b.setBackground(naranjaAccento);
        b.setForeground(fondoOscuro);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(null);
    }

    private void cargarPaquetes() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
            Request r = new Request();
            r.setAction("listarPendientes");
            Response res = conn.sendRequest(r);
            if (res.isSuccess()) {
                Object[][] datos = (Object[][]) res.getData();
                modelo.setRowCount(0);
                for (Object[] fila : datos) modelo.addRow(fila);
            }
        } catch (Exception e) {
            System.out.println("Sin datos aún");
        }
    }
    private void dibujarCheckpointEnMapa(double lat, double lon) {

    Waypoint wp = new DefaultWaypoint(lat, lon);

    waypoints.add(wp);

    WaypointPainter<Waypoint> painter = new WaypointPainter<>();
    painter.setWaypoints(waypoints);

    mapViewer.setOverlayPainter(painter);
}


    
    private void agregarCheckpointDesdeMapa(double lat, double lon) {

    int orden = roadbook.size() + 1;

    java.util.Map<String, Object> cp = new java.util.HashMap<>();
    cp.put("orden", orden);
    cp.put("nombre", "CP_" + orden);
    cp.put("latitud", lat);
    cp.put("longitud", lon);
    cp.put("distancia", 0.0);
    cp.put("tiempo", 0);

    roadbook.add(cp);

    dibujarCheckpointEnMapa(lat, lon);
}



    private void asignarRuta() {

    if (roadbook.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe agregar al menos un checkpoint");
        return;
    }

    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        // Crear ruta
        Request r = new Request("crearRuta");
        Map<String, Object> p = new HashMap<>();
        p.put("idChofer", 2);
        p.put("idVehiculo", 1);
        r.setPayload(p);

        Response res = conn.sendRequest(r);
        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Error creando ruta");
            return;
        }

        int idRuta = (int) res.getData();

        // Insertar checkpoints
        for (Map<String, Object> cp : roadbook) {
            cp.put("idRuta", idRuta);
        }

        Request r2 = new Request("insertarRoadbook");
        Map<String, Object> p2 = new HashMap<>();
        p2.put("checkpoints", roadbook);
        r2.setPayload(p2);

        Response res2 = conn.sendRequest(r2);
        if (!res2.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Error guardando checkpoints");
            return;
        }

        // Obtener primer checkpoint REAL desde la BD
        Request rCP = new Request("obtenerPrimerCheckpoint");
        Map<String, Object> pCP = new HashMap<>();
        pCP.put("idRuta", idRuta);
        rCP.setPayload(pCP);

        Response resCP = conn.sendRequest(rCP);
        if (!resCP.isSuccess()) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el primer checkpoint");
            return;
        }

        int primerCheckpointID = (int) resCP.getData();

        // Obtener paquetes seleccionados
        java.util.List<Integer> paquetes = new java.util.ArrayList<>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            paquetes.add((int) modelo.getValueAt(i, 0));
        }

        // Asignar cada paquete al primer checkpoint
        for (int idPaquete : paquetes) {
            Request r4 = new Request("asignarPaqueteCheckpoint");

            Map<String, Object> p4 = new HashMap<>();
            p4.put("idPaquete", idPaquete);
            p4.put("idRuta", idRuta);
            p4.put("idCheckpoint", primerCheckpointID);

            r4.setPayload(p4);
            conn.sendRequest(r4);
        }

        // Asociar paquetes a la ruta
        Request r3 = new Request("asociarPaquetesRuta");
        Map<String, Object> p3 = new HashMap<>();
        p3.put("idRuta", idRuta);
        p3.put("paquetes", paquetes);
        r3.setPayload(p3);

        conn.sendRequest(r3);

        JOptionPane.showMessageDialog(this, "Ruta creada correctamente con ID: " + idRuta);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error creando ruta");
        e.printStackTrace();
    }
}



    private void cargarZonas() {
        comboZonas.removeAllItems();
        comboZonas.addItem(PuntoEntrega.Seleccione);
        String prov = (String) comboProvincia.getSelectedItem();
        for (PuntoEntrega p : PuntoEntrega.values()) {
            if (p.getProvincia().equals(prov)) comboZonas.addItem(p);
        }
    }
    
    private void agregarCheckpoint() {
    PuntoEntrega p = (PuntoEntrega) comboZonas.getSelectedItem();

    if (p == null || p == PuntoEntrega.Seleccione) {
        JOptionPane.showMessageDialog(this, "Seleccione una zona válida");
        return;
    }

    java.util.Map<String, Object> cp = new java.util.HashMap<>();
    cp.put("orden", roadbook.size() + 1);
    cp.put("nombre", p.name());
    cp.put("latitud", p.getLatitud());
    cp.put("longitud", p.getLongitud());
    cp.put("distancia", 0.0);
    cp.put("tiempo", 0);

    roadbook.add(cp);

    JOptionPane.showMessageDialog(this, "Checkpoint agregado: " + p.name());
}


    private class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(naranjaAccento.darker());
                c.setForeground(Color.WHITE);
            } else {

                Object val = table.getValueAt(row, 2);
                String estado = (val != null) ? val.toString() : "";

                switch (estado) {
                    case "EN_RUTA":
                        c.setBackground(new Color(52, 116, 172));
                        break;
                    case "PENDIENTE":
                        c.setBackground(naranjaAccento);
                        break;
                    case "ENTREGADO":
                        c.setBackground(new Color(46, 139, 87));
                        break;
                    default:
                        c.setBackground(fondoSecundario);
                }
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    }
}
