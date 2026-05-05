/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChoferDAO {

    // Obtener id_chofer a partir del id_usuario
    public int obtenerIdChoferPorUsuario(int idUsuario) {
        String sql = "SELECT id_chofer FROM chofer WHERE id_usuario = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_chofer");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // No es chofer
    }

    // Obtener id_usuario a partir del id_chofer
    public int obtenerIdUsuarioPorChofer(int idChofer) {
        String sql = "SELECT id_usuario FROM chofer WHERE id_chofer = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idChofer);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_usuario");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Registrar chofer
    public boolean registrarChofer(int idUsuario, String licencia, String telefono) {
        String sql = "INSERT INTO chofer (id_usuario, licencia, telefono, activo) VALUES (?, ?, ?, 1)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, licencia);
            ps.setString(3, telefono);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int obtenerIdChoferPorNombre(String nombre) {
    String sql = """
        SELECT c.id_chofer
        FROM chofer c
        JOIN usuarios u ON u.id_usuario = c.id_usuario
        WHERE u.nombre = ?
    """;

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) return rs.getInt("id_chofer");

    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}


    // Listar choferes
    public List<Object[]> listarChoferes() {
    List<Object[]> lista = new ArrayList<>();

    String sql = """
        SELECT c.id_chofer, u.nombre
        FROM chofer c
        JOIN usuarios u ON u.id_usuario = c.id_usuario
        WHERE c.activo = 1
    """;

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getInt("id_chofer"),
                rs.getString("nombre")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
}

}
