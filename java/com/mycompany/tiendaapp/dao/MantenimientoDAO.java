/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    public boolean registrarMantenimiento(int idVehiculo, String descripcion, String estado) throws Exception {

        String sql = """
            INSERT INTO mantenimiento (id_vehiculo, descripcion, estado)
            VALUES (?, ?, ?)
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            ps.setString(2, descripcion);
            ps.setString(3, estado);

            return ps.executeUpdate() > 0;
        }
    }

    public Object[][] listarHistorial() throws Exception {

        String sql = """
            SELECT m.id_mantenimiento, v.placa, m.descripcion, m.estado, m.fecha
            FROM mantenimiento m
            INNER JOIN vehiculos v ON v.id_vehiculo = m.id_vehiculo
            ORDER BY m.fecha DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id_mantenimiento"),
                        rs.getString("placa"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }

    public Object[][] listarPorVehiculo(int idVehiculo) throws Exception {

        String sql = """
            SELECT id_mantenimiento, descripcion, estado, fecha
            FROM mantenimiento
            WHERE id_vehiculo = ?
            ORDER BY fecha DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);

            try (ResultSet rs = ps.executeQuery()) {

                List<Object[]> filas = new ArrayList<>();

                while (rs.next()) {
                    filas.add(new Object[]{
                            rs.getInt("id_mantenimiento"),
                            rs.getString("descripcion"),
                            rs.getString("estado"),
                            rs.getTimestamp("fecha")
                    });
                }

                return filas.toArray(new Object[0][]);
            }
        }
    }

    public boolean actualizarEstado(int idMantenimiento, String nuevoEstado) throws Exception {

        String sql = """
            UPDATE mantenimiento
            SET estado = ?
            WHERE id_mantenimiento = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMantenimiento);

            return ps.executeUpdate() > 0;
        }
    }
    
    public Integer obtenerIdVehiculoPorPlaca(String placa) throws Exception {

    String sql = "SELECT id_vehiculo FROM vehiculos WHERE placa = ?";

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, placa);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id_vehiculo");
            }
            return null;
        }
    }
}

    public boolean registrarMantenimientoPorPlaca(String placa, String descripcion) throws Exception {

    Integer idVehiculo = obtenerIdVehiculoPorPlaca(placa);
    if (idVehiculo == null) return false;

    String sql = """
        INSERT INTO mantenimiento (id_vehiculo, descripcion, estado)
        VALUES (?, ?, 'COMPLETADO')
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, idVehiculo);
        ps.setString(2, descripcion);

        return ps.executeUpdate() > 0;
    }
}


    public Integer obtenerVehiculoDeMantenimiento(int idMantenimiento) throws Exception {

        String sql = """
            SELECT id_vehiculo
            FROM mantenimiento
            WHERE id_mantenimiento = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMantenimiento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vehiculo");
                }
                return null;
            }
        }
    }
    
    public boolean actualizarEstadoVehiculo(String placa, String nuevoEstado) throws Exception {

    String sql = "UPDATE vehiculos SET estado = ? WHERE placa = ?";

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, nuevoEstado);
        ps.setString(2, placa);

        return ps.executeUpdate() > 0;
    }
}

}
