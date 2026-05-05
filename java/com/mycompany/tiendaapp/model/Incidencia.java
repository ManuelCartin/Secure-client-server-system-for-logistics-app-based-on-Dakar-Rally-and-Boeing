/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.model;

/**
 *
 * @author user
 */
public class Incidencia {
    private String vehiculo;
    private String descripcion;

    public Incidencia(String vehiculo, String descripcion) {
        this.vehiculo = vehiculo;
        this.descripcion = descripcion;
    }

    public String getVehiculo() { return vehiculo; }
    public String getDescripcion() { return descripcion; }
}
