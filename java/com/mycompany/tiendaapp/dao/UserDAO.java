/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.dao;
import com.mycompany.tiendaapp.model.User;
import com.mycompany.tiendaapp.util.DBUtil;
import java.sql.*;
import java.util.*;
import security.SecurityMonitor;

/**
 *
 * @author Vane
 */
public class UserDAO {
    public boolean create(User u) throws Exception {

    SecurityMonitor.inspect("create_user.nombre", u.getNombre(), null);
    SecurityMonitor.inspect("create_user.usuario", u.getUsuario(), null);
    SecurityMonitor.inspect("create_user.correo", u.getCorreo(), null);
    SecurityMonitor.inspect("create_user.rol", u.getRol(), null);

    String sql = "INSERT INTO usuarios (nombre, usuario, clave, correo, rol) VALUES (?, ?, ?, ?, ?)";
    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, u.getNombre());
        ps.setString(2, u.getUsuario());
        ps.setString(3, u.getClave());
        ps.setString(4, u.getCorreo());
        ps.setString(5, u.getRol());
        int rows = ps.executeUpdate();
        if (rows == 0) return false;
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) u.setIdUsuario(rs.getInt(1));
        }
        return true;
    }
}

    
    public boolean update(User u) throws Exception {
        boolean updateClave = u.getClave() != null && !u.getClave().isBlank();
        
        
        String sql = updateClave ?
                "UPDATE usuarios SET"
                + " nombre = ?, "
                + "usuario = ?,"
                + "correo = ?,"
                + "rol = ?, "
                + "clave = ? "
                + "WHERE id_usuario = ?"
                : "UPDATE usuarios SET"
                + " nombre = ?, "
                + "usuario = ?,"
                + "correo = ?,"
                + "rol = ? "
                + "WHERE id_usuario = ?";
                        
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getRol());
            int index = 5;
            if (updateClave) {
                ps.setString(index++, u.getClave());
            }
            ps.setInt(index, u.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                return false;
            }
           
            return true;
        }
    }
    
    public boolean delete(User u) throws Exception {
        String sql = "DELETE FROM usuarios "
                + "WHERE id_usuario = ?";
                        
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, u.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                return false;
            }
           
            return true;
        }
    }
    
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id_usuario,nombre, usuario, clave, correo, rol, estado, fecha_creacion FROM usuarios WHERE usuario = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setClave(rs.getString("clave"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    return u;
                } else {
                    return null;
                }
            }
        }
    }

    public User findById(Integer idUsuario) throws Exception {
        String sql = "SELECT id_usuario,nombre, usuario, clave, correo, rol, estado, fecha_creacion FROM usuarios WHERE id_usuario = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setClave(rs.getString("clave"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    return u;
                }
                return null;
            }
        }
    }
    
    public List<User> getUsers() throws Exception {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario,nombre, usuario, correo, rol FROM usuarios";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
           
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add( new User(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("correo"),
                    rs.getString("rol")
                            ));
                    
                }
            }
        }
        return usuarios;
    }
    
    public int obtenerIdPorNombre(String nombre) throws Exception {
    String sql = "SELECT id_usuario FROM usuarios WHERE nombre = ?";
    try (Connection c = DBUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return -1;
    }
}

    
}
