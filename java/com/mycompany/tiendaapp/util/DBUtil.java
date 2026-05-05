/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.util;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
/**
 *
 * @author Vane
 */
public class DBUtil {
   private static String url;
    private static String user;
    private static String password;

    static {
    try (InputStream in = DBUtil.class.getClassLoader()
            .getResourceAsStream("application.properties")) {

        if (in == null) {
            throw new RuntimeException("No se encontró application.properties en el classpath");
        }

        Properties p = new Properties();
        p.load(in);

        url = p.getProperty("db.url");
        user = p.getProperty("db.user");
        password = p.getProperty("db.password");

        System.out.println("URL: " + url);
        System.out.println("USER: " + user);
        System.out.println("PASS: " + password);

        Class.forName("com.mysql.cj.jdbc.Driver");

    } catch (Exception e) {
        throw new RuntimeException("No se pudo cargar application.properties", e);
    }
}

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    } 
}
