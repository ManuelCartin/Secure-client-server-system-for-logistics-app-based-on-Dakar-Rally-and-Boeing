/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package security;

/**
 *
 * @author user
 */
public class SecurityMonitor {

    public static void inspect(String action, String value, Integer userId) {
        if (value == null) return;

        if (isSQLInjection(value) || isXSS(value)) {
            HoneypotDAO.registerThreat(action, value, userId);
        }
    }

    private static boolean isSQLInjection(String v) {
        String pattern = "(?i)(\\bOR\\b|\\bAND\\b|SELECT|INSERT|UPDATE|DELETE|DROP|--|;)";
        return v.matches(".*" + pattern + ".*");
    }

    private static boolean isXSS(String v) {
        return v.toLowerCase().contains("<script") || v.toLowerCase().contains("onerror=");
    }
}
