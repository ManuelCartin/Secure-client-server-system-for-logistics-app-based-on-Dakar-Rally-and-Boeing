/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package security;

import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author user
 */
public class HoneypotDAO {

    public static void registerThreat(String action, String payload, Integer userId) {
        try (Connection c = DBUtil.getConnection()) {

            // Insert en Secure_House
            PreparedStatement ps1 = c.prepareStatement(
                "INSERT INTO Secure_House (UserId, Hash_Validacion, Token_Sesion, Ultima_IP) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps1.setInt(1, userId != null ? userId : -1);
            ps1.setString(2, hash(payload));
            ps1.setString(3, generateFakeToken());
            ps1.setString(4, getClientIP());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int codeAmenaza = rs.next() ? rs.getInt(1) : -1;

            // Insert en Audit_Trail
            PreparedStatement ps2 = c.prepareStatement(
                "INSERT INTO Audit_Trail (Code_Amenaza, UserId, Error_Detectado) VALUES (?, ?, ?)"
            );
            ps2.setInt(1, codeAmenaza);
            ps2.setInt(2, userId != null ? userId : -1);
            ps2.setString(3, action);
            ps2.executeUpdate();

            // Insert en Quarantine_Log
            PreparedStatement ps3 = c.prepareStatement(
                "INSERT INTO Quarantine_Log (Code_Amenaza, UserId, Query_Sospechosa, Intento_Payload) VALUES (?, ?, ?, ?)"
            );
            ps3.setInt(1, codeAmenaza);
            ps3.setInt(2, userId != null ? userId : -1);
            ps3.setString(3, action);
            ps3.setString(4, payload);
            ps3.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String hash(String input) {
        return Integer.toHexString(input.hashCode());
    }

    private static String generateFakeToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private static String getClientIP() {
        return "0.0.0.0";
    }
}
