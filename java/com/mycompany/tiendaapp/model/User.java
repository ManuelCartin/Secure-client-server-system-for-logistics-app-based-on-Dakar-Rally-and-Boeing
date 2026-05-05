/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Vane
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer idUsuario;
    private String nombre;
    private String usuario;
    private String clave; // hashed
    private String correo;
    private String rol;
    private String estado;
    private Timestamp fechaCreacion;

    // Constructores, getters y setters

    public User() {}

    public User(String nombre, String usuario, String clave, String correo, String rol) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.correo = correo;
        this.rol = rol;
    }

    public User(Integer idUsuario, String nombre, String usuario, String correo, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.correo = correo;
        this.rol = rol;
    }
    
    

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    
}