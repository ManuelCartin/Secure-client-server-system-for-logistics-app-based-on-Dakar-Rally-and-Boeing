/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CheckpointDAO {


    public boolean registrarCheckpoint(int idRuta, int idCheckpoint, String observacion, String chofer) {

        String sql = "INSERT INTO checkpoint_reportado " +
                     "(id_ruta, id_checkpoint, observacion, registrado_por) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ps.setInt(2, idCheckpoint);
            ps.setString(3, observacion);
            ps.setString(4, chofer);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> listarRoadbook(int idRuta) {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id_checkpoint, orden, nombre, latitud, longitud, distancia_km, tiempo_estimado " +
                     "FROM roadbook_checkpoint WHERE id_ruta = ? ORDER BY orden ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> cp = new HashMap<>();
                cp.put("id_checkpoint", rs.getInt("id_checkpoint"));
                cp.put("orden", rs.getInt("orden"));
                cp.put("nombre", rs.getString("nombre"));
                cp.put("latitud", rs.getDouble("latitud"));
                cp.put("longitud", rs.getDouble("longitud"));
                cp.put("distancia", rs.getDouble("distancia_km"));
                cp.put("tiempo", rs.getInt("tiempo_estimado"));
                lista.add(cp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Map<String, Object>> listarProgreso(int idRuta) {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT r.id_checkpoint, c.nombre, r.fecha_hora, r.observacion " +
                     "FROM checkpoint_reportado r " +
                     "JOIN roadbook_checkpoint c ON c.id_checkpoint = r.id_checkpoint " +
                     "WHERE r.id_ruta = ? ORDER BY r.fecha_hora ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> cp = new HashMap<>();
                cp.put("id_checkpoint", rs.getInt("id_checkpoint"));
                cp.put("nombre", rs.getString("nombre"));
                cp.put("fecha_hora", rs.getTimestamp("fecha_hora"));
                cp.put("observacion", rs.getString("observacion"));
                lista.add(cp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Integer obtenerUltimoCheckpoint(int idRuta) {

        String sql = "SELECT id_checkpoint FROM checkpoint_reportado " +
                     "WHERE id_ruta = ? ORDER BY fecha_hora DESC LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("id_checkpoint");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // No hay progreso aún
    }
    
    public boolean insertarCheckpoint(int idRuta, int orden, String nombre,
                                  double latitud, double longitud,
                                  double distancia, int tiempo) {

    String sql = "INSERT INTO roadbook_checkpoint " +
                 "(id_ruta, orden, nombre, latitud, longitud, distancia_km, tiempo_estimado) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idRuta);
        ps.setInt(2, orden);
        ps.setString(3, nombre);
        ps.setDouble(4, latitud);
        ps.setDouble(5, longitud);
        ps.setDouble(6, distancia);
        ps.setInt(7, tiempo);

        ps.executeUpdate();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    public List<Integer> listarPaquetesPorCheckpoint(int idCheckpoint) {

        List<Integer> lista = new ArrayList<>();

        String sql = "SELECT id_paquete FROM paquete_checkpoint WHERE id_checkpoint = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCheckpoint);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(rs.getInt("id_paquete"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public int obtenerPrimerCheckpoint(int idRuta) {
    String sql = "SELECT id_checkpoint FROM roadbook_checkpoint WHERE id_ruta = ? ORDER BY orden ASC LIMIT 1";

    try (Connection conn = DBUtil.getConnection();   // ← CORREGIDO
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idRuta);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) return rs.getInt("id_checkpoint");

    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}

    public boolean asignarPaqueteCheckpoint(int idPaquete, int idRuta, int idCheckpoint) {
    String sql = "INSERT INTO paquete_checkpoint (id_paquete, id_ruta, id_checkpoint) VALUES (?, ?, ?)";

    try (Connection conn = DBUtil.getConnection();   // ← CORREGIDO
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPaquete);
        ps.setInt(2, idRuta);
        ps.setInt(3, idCheckpoint);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}
