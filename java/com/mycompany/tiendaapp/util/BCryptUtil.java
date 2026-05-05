/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.util;
import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author Vane
 */
public class BCryptUtil {
  // Genera hash (usa salt interno)
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    // Valida contraseña vs hash
    public static boolean checkPassword(String plain, String hashed) {
        if (plain == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plain, hashed);
        } catch (Exception ex) {
            return false;
        }
    }
}
