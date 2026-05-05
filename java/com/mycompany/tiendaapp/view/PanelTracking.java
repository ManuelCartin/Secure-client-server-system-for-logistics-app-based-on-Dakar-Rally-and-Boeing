/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.view;

import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.controller.AuthController;
import static com.mycompany.tiendaapp.view.PrincipalView.main;

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

public class PanelTracking extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JXMapViewer mapViewer;
    private Timer timerRefresco;

    private final Color fondoOscuro = new Color(23, 24, 29);
    private final Color fondoSecundario = new Color(41, 44, 53);
    private final Color naranjaAccento = new Color(224, 145, 69);
    private final Color cremaTexto = new Color(252, 217, 184);
    private MainGUI main; 
    private int idCliente;
    private String rolActivo;

    public PanelTracking(MainGUI main, String rol, int idCliente) { 
        this.main = main;
        this.rolActivo = rol;

    setBackground(fondoOscuro);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("TRACKING EN TIEMPO REAL");
        titulo.setForeground(naranjaAccento);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnVolver = new JButton("⬅ VOLVER");
        estilizarBoton(btnVolver);
        btnVolver.addActionListener(e -> cerrarYVolver());

        header.add(titulo, BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // MAPA
        JPanel panelMapa = new JPanel(new BorderLayout());
        panelMapa.setBorder(new LineBorder(naranjaAccento));
        inicializarMapa(panelMapa);
        add(panelMapa, BorderLayout.CENTER);

        // TABLA
        configurarEstructuraTabla();
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.setBorder(new LineBorder(naranjaAccento));
        scroll.getViewport().setBackground(fondoSecundario);
        add(scroll, BorderLayout.SOUTH);

        // LÓGICA DE ACTUALIZACIÓN
        cargarTracking();
        iniciarTimerRefresco();
        String textoBoton = ("admin".equals(rolActivo) || "marketing".equals(rolActivo)) 
                            ? "⬅ VOLVER" : "🚪 CERRAR SESIÓN";
        btnVolver.setText(textoBoton);
        if ("marketing".equals(rolActivo.toLowerCase())) {
            titulo.setText("ANÁLISIS DE LOGÍSTICA Y TRACKING");
        } else if ("admin".equals(rolActivo.toLowerCase())) {
            titulo.setText("PANEL DE CONTROL TOTAL - TRACKING");
        } else {
            titulo.setText("MIS PEDIDOS EN RUTA");
        }
    String texto = "admin".equals(rolActivo.toLowerCase()) ? "⬅ VOLVER AL MENÚ" : "🚪 CERRAR SESIÓN";
    btnVolver.setText(texto);
    }

    private void cerrarYVolver() {
    if (timerRefresco != null) timerRefresco.stop(); 
    
    if ("admin".equals(rolActivo.toLowerCase())) {
        main.mostrarPanel(new PanelInicio(main)); 
    } 
    else {
        int confirm = JOptionPane.showConfirmDialog(
            this, "¿Desea cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) frame.dispose();

            LoginView lv = new LoginView();
            new AuthController(lv);
            lv.setVisible(true);
        } else {
            if (timerRefresco != null) timerRefresco.start();
        }
    }
}

    private void configurarEstructuraTabla() {
    modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID PAQUETE", "CHOFER", "ÚLTIMA UBICACIÓN", "ESTADO"} 
    ) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };

    tabla = new JTable(modelo);
    configurarEsteticaTabla(tabla);

tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            String estado = value.toString().toUpperCase();
            if (estado.contains("ENTREGADO")) lbl.setForeground(new Color(100, 255, 100));
            else if (estado.contains("RUTA") || estado.contains("EN_RUTA")) lbl.setForeground(naranjaAccento);
            else lbl.setForeground(Color.LIGHT_GRAY);
        } else {
            lbl.setText("");
        }
        return lbl;
    }
});
}

    private void iniciarTimerRefresco() {
        timerRefresco = new Timer(5000, e -> cargarTracking());
        timerRefresco.start();
    }

    private void cargarTracking() {
    new SwingWorker<Object[][], Void>() {
        @Override
        protected Object[][] doInBackground() throws Exception {
            try (ClientConnector conn = new ClientConnector("localhost", 5555)) {
                Request r = new Request("listarPaquetesPorRol"); 
                Map<String, Object> p = new HashMap<>();
                
                p.put("idCliente", idCliente);
                p.put("rol", rolActivo.toLowerCase());
                r.setPayload(p);
                Response res = conn.sendRequest(r);
                return res.isSuccess() ? (Object[][]) res.getData() : null;
            }
        }

            @Override
            protected void done() {
                try {
                    Object[][] datos = get();
                    if (datos != null) {
                        modelo.setRowCount(0);
                        for (Object[] fila : datos) modelo.addRow(fila);
                    }
                } catch (Exception e) {
                    System.err.println("Error en refresco automático: " + e.getMessage());
                }
            }
        }.execute();
}

    private void inicializarMapa(JPanel contenedor) {
        mapViewer = new JXMapViewer();
        OSMTileFactoryInfo info = new OSMTileFactoryInfo("QuickDelivery", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Ubicación inicial (Costa Rica)
        mapViewer.setAddressLocation(new GeoPosition(9.9333, -84.0833));
        mapViewer.setZoom(7);

        PanMouseInputListener mm = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mm);
        mapViewer.addMouseMotionListener(mm);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        contenedor.add(mapViewer, BorderLayout.CENTER);
    }

    private void configurarEsteticaTabla(JTable t) {
        t.setBackground(fondoSecundario);
        t.setForeground(Color.WHITE);
        t.setRowHeight(35);
        t.setGridColor(fondoOscuro);
        t.setSelectionBackground(naranjaAccento);
        t.setSelectionForeground(fondoOscuro);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        t.getTableHeader().setBackground(fondoOscuro);
        t.getTableHeader().setForeground(naranjaAccento);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

t.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            String texto = value.toString().toUpperCase();
            if (texto.contains("ENTREGADO")) lbl.setForeground(new Color(100, 255, 100));
            else if (texto.contains("RUTA")) lbl.setForeground(naranjaAccento);
            else lbl.setForeground(Color.LIGHT_GRAY);
        } else {
            lbl.setText("");
        }
        return lbl;
    }
});
    }

    private void estilizarBoton(JButton b) {
        b.setBackground(fondoSecundario);
        b.setForeground(naranjaAccento);
        b.setBorder(new LineBorder(naranjaAccento));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}