/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {


    public Object[][] listarVehiculos() throws Exception {

        String sql = "SELECT id_vehiculo, placa, modelo, kilometraje, estado FROM vehiculos";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getInt("kilometraje"),
                        rs.getString("estado")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }


    public Object[][] listarDisponibles() throws Exception {

        String sql = """
            SELECT id_vehiculo, placa, modelo, kilometraje
            FROM vehiculos
            WHERE estado = 'DISPONIBLE'
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id_vehiculo"),
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getInt("kilometraje")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }


    public boolean enviarAMantenimiento(String placa) throws Exception {

        String sql = """
            UPDATE vehiculos
            SET estado = 'MANTENIMIENTO'
            WHERE placa = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, placa);

            return ps.executeUpdate() > 0;
        }
    }


    public boolean marcarDisponible(String placa) throws Exception {

        String sql = """
            UPDATE vehiculos
            SET estado = 'DISPONIBLE'
            WHERE placa = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, placa);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarKilometraje(String placa, int nuevoKm) throws Exception {

        String sql = """
            UPDATE vehiculos
            SET kilometraje = ?
            WHERE placa = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, nuevoKm);
            ps.setString(2, placa);

            return ps.executeUpdate() > 0;
        }
    }


    public Integer obtenerIdPorPlaca(String placa) throws Exception {
        String sql = "SELECT id_vehiculo FROM vehiculos WHERE placa = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_vehiculo");
                return null;
            }
        }
    }
    
    public int obtenerIdPorNombre(String nombre) throws Exception {
        String sql = "SELECT id_vehiculo FROM vehiculos WHERE modelo = ?"; 
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vehiculo");
                }
                return -1;
            }
        }
    }
    
    public boolean actualizarEstado(String placa, String nuevoEstado) throws Exception {

    String sql = """
        UPDATE vehiculos
        SET estado = ?
        WHERE placa = ?
    """;

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, nuevoEstado);
        ps.setString(2, placa);

        return ps.executeUpdate() > 0;
    }
}

    public List<String> listarVehiculosParaCombo() throws Exception {

    String sql = "SELECT placa, modelo FROM vehiculos";

    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        List<String> lista = new ArrayList<>();

        while (rs.next()) {
            String placa = rs.getString("placa");
            String modelo = rs.getString("modelo");
            lista.add(placa + " - " + modelo);
        }

        return lista;
    }
}

}
