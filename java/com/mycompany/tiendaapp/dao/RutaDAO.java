/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;

public class RutaDAO {

    public int crearRuta(int idChofer, int idVehiculo) {
        String sql = "INSERT INTO ruta (id_chofer, id_vehiculo) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idChofer);
            ps.setInt(2, idVehiculo);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public Integer obtenerRutaActiva(int idChofer) {

    String sql = "SELECT id_ruta FROM ruta WHERE id_chofer = ? AND estado = 'EN_RUTA' LIMIT 1";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idChofer);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) return rs.getInt("id_ruta");

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

}
