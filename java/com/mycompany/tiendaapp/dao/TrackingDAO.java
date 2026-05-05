/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackingDAO {

    public boolean registrarTracking(int idPaquete, String punto) throws Exception {

        String sql = "INSERT INTO tracking (id_paquete, punto) VALUES (?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPaquete);
            ps.setString(2, punto);

            return ps.executeUpdate() > 0;
        }
    }

    public Object[][] listarTrackingPorPaquete(int idPaquete) throws Exception {

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


    public String obtenerUltimoPunto(int idPaquete) throws Exception {

        String sql = """
            SELECT punto
            FROM tracking
            WHERE id_paquete = ?
            ORDER BY fecha DESC
            LIMIT 1
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPaquete);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("punto");
                }
                return null;
            }
        }
    }

public Object[][] listarTrackingGlobal() throws Exception {

    String sql = """
        SELECT c.nombre AS cliente,
               p.id_paquete,
               p.origen,
               t.fecha,
               u.nombre AS analista
        FROM tracking t
        JOIN paquetes p ON t.id_paquete = p.id_paquete
        JOIN clientes c ON p.id_cliente = c.id_cliente
        LEFT JOIN usuarios u ON t.id_usuario = u.id_usuario
        ORDER BY t.fecha DESC
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        List<Object[]> lista = new ArrayList<>();

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getString("cliente"),
                rs.getInt("id_paquete"),
                rs.getString("origen"),
                rs.getTimestamp("fecha"),
                rs.getString("analista")
            });
        }

        return lista.toArray(new Object[0][]);
    }
}

}
