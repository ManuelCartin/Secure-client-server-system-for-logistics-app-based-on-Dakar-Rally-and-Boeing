/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.controller.AuthController;
import com.mycompany.tiendaapp.model.PuntoEntrega;
import static com.mycompany.tiendaapp.view.PrincipalView.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.painter.PolylinePainter;


public class PanelChofer extends JPanel {

    private int idRuta;
    private String rolActivo;  

    private JTable tablaRoadbook;
    private JTable tablaProgreso;
    private JTable tablaPaquetes;

    private DefaultTableModel modeloRoadbook;
    private DefaultTableModel modeloProgreso;
    private DefaultTableModel modeloPaquetes;

    private JXMapViewer mapViewer;
    private java.util.Set<Waypoint> waypoints = new java.util.HashSet<>();


    private List<Map<String, Object>> roadbook;
    private List<Map<String, Object>> progreso;
    private org.jxmapviewer.painter.CompoundPainter<JXMapViewer> pintorCompuesto = new org.jxmapviewer.painter.CompoundPainter<>();
    private MainGUI main;
    private String usuarioChofer;


    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);

   public PanelChofer(MainGUI main, int idRuta, String rolActivo, String usuarioChofer){
    this.main = main;
    this.idRuta = idRuta;
    this.rolActivo = rolActivo;

    setBackground(fondoOscuro);
    setLayout(new BorderLayout(15, 15));
    setBorder(new EmptyBorder(20, 20, 20, 20));

    inicializarUI();
    cargarRoadbook();
    dibujarCheckpoints();
    dibujarRutaCompleta();
    cargarProgreso();
    moverMapaAlCheckpointActual();
    cargarPaquetesDelCheckpointActual();
}

    
    private static class RoutePainter implements org.jxmapviewer.painter.Painter<JXMapViewer> {

    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        this.track = track;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {

        g = (Graphics2D) g.create();
        g.setColor(Color.ORANGE);
        g.setStroke(new BasicStroke(3));

        Point lastPoint = null;

        for (GeoPosition gp : track) {
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
            Point currentPoint = new Point((int) pt.getX(), (int) pt.getY());

            if (lastPoint != null) {
                g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
            }

            lastPoint = currentPoint;
        }

        g.dispose();
    }
}

    
    private void registrarCheckpointActual() {

    if (roadbook == null || roadbook.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay roadbook cargado.");
        return;
    }

    // Determinar checkpoint actual
    int index = progreso == null ? 0 : progreso.size();

    if (index >= roadbook.size()) {
        JOptionPane.showMessageDialog(this, "Ruta completada. No hay más checkpoints.");
        return;
    }

    Map<String, Object> cp = roadbook.get(index);
    int idCheckpoint = (int) cp.get("id_checkpoint");

    String observacion = JOptionPane.showInputDialog(this, "Observación:", "Checkpoint alcanzado", JOptionPane.PLAIN_MESSAGE);
    if (observacion == null) observacion = "Sin observación";

    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        Request req = new Request();
        req.setAction("registrarCheckpoint");

        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("idRuta", idRuta);
        p.put("idCheckpoint", idCheckpoint);
        p.put("observacion", observacion);
        p.put("chofer", usuarioChofer);


        req.setPayload(p);

        Response res = conn.sendRequest(req);

        if (res.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Checkpoint registrado correctamente.");

            // Actualizar todo
            cargarProgreso();
            cargarPaquetesDelCheckpointActual();
            moverMapaAlCheckpointActual();

        } else {
            JOptionPane.showMessageDialog(this, res.getMessage());
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error registrando checkpoint.");
    }
}

    private void inicializarUI() {
        JPanel panelZonas = new JPanel();
        panelZonas.setOpaque(false);
        
    JButton btnCerrarSesion = new JButton(
        "admin".equals(rolActivo) ? "⬅ VOLVER" : "CERRAR SESIÓN"
);

btnCerrarSesion.addActionListener(e -> {

    if ("admin".equals(rolActivo)) {
        // ADMIN → volver al menú principal
        main.mostrarPanel(new PanelInicio(main));
        return;
    }

    // CHOFER , cerrar sesión
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    if (frame != null) frame.dispose();

    LoginView login = new LoginView();
    new AuthController(login);
    login.setVisible(true);
});



        // Combo de provincias
        String[] provincias = {"San José", "Alajuela", "Heredia"};
        JComboBox<String> comboProvincia = new JComboBox<>(provincias);
        comboProvincia.setSelectedIndex(-1);

        // Combo de puntos dentro de la provincia
        JComboBox<PuntoEntrega> comboPuntos = new JComboBox<>();
        comboPuntos.setEnabled(false);
        // BOTONES INFERIORES
        JButton btnRegistrar = new JButton("REGISTRAR CHECKPOINT ALCANZADO");
        btnRegistrar.setBackground(naranjaAccento);
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.addActionListener(e -> registrarCheckpointActual());

        // Crear panel de botones
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);

        // Agregar botón registrar
        panelBoton.add(btnRegistrar);

        // Crear botón entregar
        JButton btnEntregar = new JButton("MARCAR PAQUETE ENTREGADO");
        btnEntregar.setBackground(new Color(46, 139, 87));
        btnEntregar.setForeground(Color.WHITE);
        btnEntregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntregar.addActionListener(e -> marcarPaqueteEntregado());

        // Agregar botón entregar
        panelBoton.add(btnEntregar);

        // Agregar panel al sur
        add(panelBoton, BorderLayout.SOUTH);


        // llenar puntos
        comboProvincia.addActionListener(e -> {
            String provincia = (String) comboProvincia.getSelectedItem();
            if (provincia == null) return;

            comboPuntos.removeAllItems();

            for (PuntoEntrega p : PuntoEntrega.values()) {
                if (p.getProvincia().equalsIgnoreCase(provincia)) {
                    comboPuntos.addItem(p);
                }
            }

            comboPuntos.setEnabled(true);

            // Centrar mapa en la provincia
            moverMapaAProvincia(provincia);
        });

        // Cuando selecciona un punto → mover mapa al punto exacto
        comboPuntos.addActionListener(e -> {
            PuntoEntrega p = (PuntoEntrega) comboPuntos.getSelectedItem();
            if (p != null) {
                moverMapaAPunto(p);
            }
        });

        panelZonas.add(new JLabel("Provincia: "));
        panelZonas.add(comboProvincia);
        panelZonas.add(new JLabel("Punto: "));
        panelZonas.add(comboPuntos);
        panelZonas.add(btnCerrarSesion);


        add(panelZonas, BorderLayout.NORTH);

        // MAPA
        mapViewer = new JXMapViewer();
        OSMTileFactoryInfo info = new OSMTileFactoryInfo("QuickDeliveryApp", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(5);

        JPanel panelMapa = new JPanel(new BorderLayout());
        panelMapa.setBorder(new LineBorder(naranjaAccento));
        panelMapa.add(mapViewer, BorderLayout.CENTER);
        
        // Permitir arrastrar el mapa
        org.jxmapviewer.input.PanMouseInputListener pan = new org.jxmapviewer.input.PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(pan);
        mapViewer.addMouseMotionListener(pan);

        // Permitir zoom con la rueda del mouse
        mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));

        // TABLA ROADBOOK
        modeloRoadbook = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Orden", "Nombre", "Distancia", "Tiempo"}
        );
        tablaRoadbook = new JTable(modeloRoadbook);

        JScrollPane scrollRoadbook = new JScrollPane(tablaRoadbook);
        scrollRoadbook.setBorder(new LineBorder(naranjaAccento));

        // TABLA PROGRESO
        modeloProgreso = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Checkpoint", "Fecha", "Observación"}
        );
        tablaProgreso = new JTable(modeloProgreso);

        JScrollPane scrollProgreso = new JScrollPane(tablaProgreso);
        scrollProgreso.setBorder(new LineBorder(naranjaAccento));

        // TABLA PAQUETES
        modeloPaquetes = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Paquete"}
        );
        tablaPaquetes = new JTable(modeloPaquetes);

        JScrollPane scrollPaquetes = new JScrollPane(tablaPaquetes);
        scrollPaquetes.setBorder(new LineBorder(naranjaAccento));

        // PANEL IZQUIERDO
        JPanel panelIzq = new JPanel(new GridLayout(3, 1, 10, 10));
        panelIzq.setOpaque(false);
        panelIzq.add(scrollRoadbook);
        panelIzq.add(scrollProgreso);
        panelIzq.add(scrollPaquetes);

        add(panelIzq, BorderLayout.WEST);
        add(panelMapa, BorderLayout.CENTER);

    }
    
    private void moverMapaAPunto(PuntoEntrega p) {

    GeoPosition pos = new GeoPosition(p.getLatitud(), p.getLongitud());
    mapViewer.setAddressLocation(pos);
    mapViewer.setZoom(6);

    // Dibujar waypoint del punto seleccionado
    Waypoint wp = new DefaultWaypoint(pos);

    WaypointPainter<Waypoint> painter = new WaypointPainter<>();
    painter.setWaypoints(new HashSet<>(List.of(wp)));

    mapViewer.setOverlayPainter(painter);
}
    
    private void marcarPaqueteEntregado() {

    int fila = tablaPaquetes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un paquete.");
        return;
    }

    int idPaquete = (int) modeloPaquetes.getValueAt(fila, 0);

    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        Request req = new Request("actualizarPaquete");

        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("idPaquete", idPaquete);
        p.put("descripcion", "Entregado");
        p.put("destino", "N/A");
        p.put("estado", "ENTREGADO");

        req.setPayload(p);

        Response res = conn.sendRequest(req);

        if (res.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Paquete marcado como entregado.");
            cargarPaquetesDelCheckpointActual();
        } else {
            JOptionPane.showMessageDialog(this, res.getMessage());
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error marcando paquete.");
    }
}


    private void actualizarCapasMapa() {
    List<org.jxmapviewer.painter.Painter<JXMapViewer>> listaPintores = new ArrayList<>();

    // Capa de la Línea (Ruta)
    if (roadbook != null && roadbook.size() >= 2) {
        List<GeoPosition> puntos = new ArrayList<>();
        for (Map<String, Object> cp : roadbook) {
            puntos.add(new GeoPosition((double) cp.get("latitud"), (double) cp.get("longitud")));
        }
        listaPintores.add(new RoutePainter(puntos));
    }

    // Capa de Checkpoints (Waypoints)
    if (waypoints != null && !waypoints.isEmpty()) {
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        listaPintores.add(waypointPainter);
    }

    // Aplicar todo junto
    pintorCompuesto.setPainters(listaPintores);
    mapViewer.setOverlayPainter(pintorCompuesto);
}

    
    private void moverMapaAProvincia(String provincia) {

    // Buscar el primer punto de esa provincia
    for (PuntoEntrega p : PuntoEntrega.values()) {
        if (p.getProvincia().equalsIgnoreCase(provincia)) {

            GeoPosition pos = new GeoPosition(p.getLatitud(), p.getLongitud());
            mapViewer.setAddressLocation(pos);
            mapViewer.setZoom(7);

            return;
        }
    }

    JOptionPane.showMessageDialog(this, "No hay puntos registrados en " + provincia);
}
    private void dibujarCheckpoints() {
    if (roadbook == null) return;
    waypoints.clear();
    for (Map<String, Object> cp : roadbook) {
        waypoints.add(new DefaultWaypoint((double) cp.get("latitud"), (double) cp.get("longitud")));
    }
    actualizarCapasMapa(); // Llamar al unificador
}

private void dibujarRutaCompleta() {
    actualizarCapasMapa(); 
}
       
    private void mostrarCheckpointsDeProvincia(String provincia) {

    mapViewer.setOverlayPainter(null); // limpiar

    List<Waypoint> puntos = new ArrayList<>();

    for (Map<String, Object> cp : roadbook) {
        String nombre = (String) cp.get("nombre");

        for (PuntoEntrega p : PuntoEntrega.values()) {
            if (nombre.contains(p.name()) && p.getProvincia().equalsIgnoreCase(provincia)) {
                puntos.add(new DefaultWaypoint(p.getLatitud(), p.getLongitud()));
            }
        }
    }

    WaypointPainter<Waypoint> painter = new WaypointPainter<>();
    painter.setWaypoints(new HashSet<>(puntos));
    mapViewer.setOverlayPainter(painter);
}
    
    // CARGAR ROADBOOK

    private void cargarRoadbook() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

            Request req = new Request();
            req.setAction("listarRoadbook");

            java.util.Map<String, Object> p = new java.util.HashMap<>();
            p.put("idRuta", idRuta);
            req.setPayload(p);

            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {
                roadbook = (List<Map<String, Object>>) res.getData();
                modeloRoadbook.setRowCount(0);

                for (Map<String, Object> cp : roadbook) {
                    modeloRoadbook.addRow(new Object[]{
                            cp.get("orden"),
                            cp.get("nombre"),
                            cp.get("distancia"),
                            cp.get("tiempo")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // CARGAR PROGRESO
    private void cargarProgreso() {
        try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

            Request req = new Request();
            req.setAction("listarProgresoRuta");

            java.util.Map<String, Object> p = new java.util.HashMap<>();
            p.put("idRuta", idRuta);
            req.setPayload(p);

            Response res = conn.sendRequest(req);

            if (res.isSuccess()) {
                progreso = (List<Map<String, Object>>) res.getData();
                modeloProgreso.setRowCount(0);

                for (Map<String, Object> cp : progreso) {
                    modeloProgreso.addRow(new Object[]{
                            cp.get("nombre"),
                            cp.get("fecha_hora"),
                            cp.get("observacion")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MOVER MAPA AL CHECKPOINT ACTUAL
    private void moverMapaAlCheckpointActual() {

        if (roadbook == null || roadbook.isEmpty()) return;

        int index = progreso == null ? 0 : progreso.size();

        if (index >= roadbook.size()) index = roadbook.size() - 1;

        Map<String, Object> cp = roadbook.get(index);

        double lat = (double) cp.get("latitud");
        double lon = (double) cp.get("longitud");

        mapViewer.setAddressLocation(new GeoPosition(lat, lon));
        mapViewer.setZoom(6);
    }
    private void cargarPaquetesDelCheckpointActual() {

    if (roadbook == null || roadbook.isEmpty()) return;

    // Determinar checkpoint actual
    int index = progreso == null ? 0 : progreso.size();
    if (index >= roadbook.size()) index = roadbook.size() - 1;

    Map<String, Object> cp = roadbook.get(index);
    int idCheckpoint = (int) cp.get("id_checkpoint");

    try (ClientConnector conn = new ClientConnector("localhost", 5555)) {

        Request req = new Request();
        req.setAction("listarPaquetesPorCheckpoint");

        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("idCheckpoint", idCheckpoint);
        req.setPayload(p);

        Response res = conn.sendRequest(req);

        if (res.isSuccess()) {

            java.util.List<Integer> paquetes = (java.util.List<Integer>) res.getData();

            modeloPaquetes.setRowCount(0);

            for (int idPaquete : paquetes) {
                modeloPaquetes.addRow(new Object[]{idPaquete});
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
