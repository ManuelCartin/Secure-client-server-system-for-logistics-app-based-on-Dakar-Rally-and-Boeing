/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaqueteDAO {

    public Object[][] listarMonitoreo() throws Exception {

        String sql = "SELECT id, chofer, ultimo_punto, estado FROM vw_monitoreo_paquetes";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("chofer"),
                        rs.getString("ultimo_punto"),
                        rs.getString("estado")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }

    public Object[][] listarPendientes() throws Exception {

        String sql = "SELECT id_paquete, descripcion, destino, estado FROM paquetes WHERE estado = 'PENDIENTE'";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id_paquete"),
                        rs.getString("descripcion"),
                        rs.getString("destino"),
                        rs.getString("estado")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }

    public Object[][] listarPorChofer(int idChofer) throws Exception {

        String sql = """
            SELECT id_paquete, descripcion, destino, estado
            FROM paquetes
            WHERE id_chofer = ?
            ORDER BY id_paquete DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idChofer);

            try (ResultSet rs = ps.executeQuery()) {

                List<Object[]> filas = new ArrayList<>();

                while (rs.next()) {
                    filas.add(new Object[]{
                            rs.getInt("id_paquete"),
                            rs.getString("descripcion"),
                            rs.getString("destino"),
                            rs.getString("estado")
                    });
                }

                return filas.toArray(new Object[0][]);
            }
        }
    }

    public boolean asignarRuta(int idPaquete, int idChofer, int idVehiculo) throws Exception {

        String sql = """
            UPDATE paquetes
            SET id_chofer = ?, id_vehiculo = ?, estado = 'EN_RUTA'
            WHERE id_paquete = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idChofer);
            ps.setInt(2, idVehiculo);
            ps.setInt(3, idPaquete);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstado(int idPaquete, String nuevoEstado) throws Exception {

        String sql = "UPDATE paquetes SET estado = ? WHERE id_paquete = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPaquete);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean registrarTracking(int idPaquete, String punto) throws Exception {

        String sql = "INSERT INTO tracking (id_paquete, punto) VALUES (?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPaquete);
            ps.setString(2, punto);

            return ps.executeUpdate() > 0;
        }
    }


    public Object[][] listarTracking(int idPaquete) throws Exception {

        String sql = """
            SELECT punto, fecha
            FROM tracking
            WHERE id_paquete = ?
            ORDER BY fecha DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPaquete);

            try (ResultSet rs = ps.executeQuery()) {

                List<Object[]> filas = new ArrayList<>();

                while (rs.next()) {
                    filas.add(new Object[]{
                            rs.getString("punto"),
                            rs.getTimestamp("fecha")
                    });
                }

                return filas.toArray(new Object[0][]);
            }
        }
    }
    public Object[][] listarPaquetesConCliente() throws Exception {

    String sql = """
        SELECT 
            p.id_paquete,
            u.nombre AS cliente,
            p.descripcion,
            p.destino,
            c.nombre AS chofer,
            (
                SELECT t.punto
                FROM tracking t
                WHERE t.id_paquete = p.id_paquete
                ORDER BY t.fecha DESC
                LIMIT 1
            ) AS ultimo_punto,
            p.estado
        FROM paquetes p
        LEFT JOIN usuarios u ON u.id_usuario = p.id_cliente
        LEFT JOIN chofer ch ON ch.id_chofer = p.id_chofer
        LEFT JOIN usuarios c ON c.id_usuario = ch.id_usuario
        ORDER BY p.id_paquete DESC
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        List<Object[]> filas = new ArrayList<>();

        while (rs.next()) {
            filas.add(new Object[]{
                    rs.getInt("id_paquete"),
                    rs.getString("cliente"),
                    rs.getString("descripcion"),
                    rs.getString("destino"),
                    rs.getString("chofer"),
                    rs.getString("ultimo_punto"),
                    rs.getString("estado")
            });
        }

        return filas.toArray(new Object[0][]);
    }
}
public Object[][] listarPorCliente(int idCliente) throws Exception {

    String sql = """
        SELECT id_paquete, descripcion, destino, estado
        FROM paquetes
        WHERE id_cliente = ?
        ORDER BY id_paquete DESC
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id_paquete"),
                        rs.getString("descripcion"),
                        rs.getString("destino"),
                        rs.getString("estado")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }
}

public boolean registrarPaquete(int idCliente, String descripcion, String destino) throws Exception {

    String sql = """
        INSERT INTO paquetes (id_cliente, descripcion, destino, estado)
        VALUES (?, ?, ?, 'PENDIENTE')
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        ps.setString(2, descripcion);
        ps.setString(3, destino);

        return ps.executeUpdate() > 0;
    }
}

public boolean actualizarPaquete(int idPaquete, String descripcion, String destino, String estado) throws Exception {

    String sql = """
        UPDATE paquetes
        SET descripcion = ?, destino = ?, estado = ?
        WHERE id_paquete = ?
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, descripcion);
        ps.setString(2, destino);
        ps.setString(3, estado);
        ps.setInt(4, idPaquete);

        return ps.executeUpdate() > 0;
    }
}

public boolean eliminarPaquete(int idPaquete) throws Exception {

    String sql = "DELETE FROM paquetes WHERE id_paquete = ?";

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, idPaquete);

        return ps.executeUpdate() > 0;
    }
}

}
