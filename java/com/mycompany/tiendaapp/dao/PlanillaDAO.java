/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanillaDAO {


    public boolean registrar(int idUsuario, int horas, int entregas, String estado) throws Exception {

        String sql = """
            INSERT INTO planilla (id_usuario, horas, entregas, estado)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, horas);
            ps.setInt(3, entregas);
            ps.setString(4, estado);

            return ps.executeUpdate() > 0;
        }
    }

    public Object[][] listarPlanilla() throws Exception {

        String sql = """
            SELECT p.id, u.nombre, u.rol, p.horas, p.entregas, p.estado
            FROM planilla p
            INNER JOIN usuarios u ON u.id_usuario = p.id_usuario
            ORDER BY p.id DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Object[]> filas = new ArrayList<>();

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("horas"),
                        rs.getInt("entregas"),
                        rs.getString("estado")
                });
            }

            return filas.toArray(new Object[0][]);
        }
    }


    public Object[][] listarPorUsuario(int idUsuario) throws Exception {

        String sql = """
            SELECT id, horas, entregas, estado, fecha
            FROM planilla
            WHERE id_usuario = ?
            ORDER BY id DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                List<Object[]> filas = new ArrayList<>();

                while (rs.next()) {
                    filas.add(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("horas"),
                            rs.getInt("entregas"),
                            rs.getString("estado")
                    });
                }

                return filas.toArray(new Object[0][]);
            }
        }
    }

    public boolean actualizar(int id, int horas, int entregas, String estado) throws Exception {

        String sql = """
            UPDATE planilla
            SET horas = ?, entregas = ?, estado = ?
            WHERE id = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, horas);
            ps.setInt(2, entregas);
            ps.setString(3, estado);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }


    public boolean eliminar(int id) throws Exception {

        String sql = "DELETE FROM planilla WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
