/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RoadbookDAO {

    public void insertarCheckpoint(int idRuta, int orden, String nombre,
                                   double lat, double lon, double distancia, int tiempo) {

        String sql = "INSERT INTO roadbook_checkpoint " +
                "(id_ruta, orden, nombre, latitud, longitud, distancia_km, tiempo_estimado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ps.setInt(2, orden);
            ps.setString(3, nombre);
            ps.setDouble(4, lat);
            ps.setDouble(5, lon);
            ps.setDouble(6, distancia);
            ps.setInt(7, tiempo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asociarPaquete(int idPaquete, int idRuta) {

        String sql = "UPDATE paquetes SET id_ruta = ? WHERE id_paquete = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRuta);
            ps.setInt(2, idPaquete);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
