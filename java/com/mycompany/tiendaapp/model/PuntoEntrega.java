/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.tiendaapp.model;

/**
 *
 * @author user
 */

public enum PuntoEntrega {
    Seleccione("Seleccione ubicación...", 0, 0, "N/A"),
    
    // SAN JOSÉ
    SJ_Centro("San José Centro (Catedral)", 9.9333, -84.0795, "San José"),
    SJ_Norte("San José Norte (Tibás/Moravia)", 9.9548, -84.0712, "San José"),
    SJ_Sur("San José Sur (Desamparados/Hutcas)", 9.8989, -84.0732, "San José"),
    SJ_Este("San José Este (San Pedro/Curri)", 9.9281, -84.0485, "San José"),
    SJ_Oeste("San José Oeste (Escazú/Sabana)", 9.9319, -84.1404, "San José"),
    
    // ALAJUELA
    AL_Centro("Alajuela Centro", 10.0163, -84.2141, "Alajuela"),
    AL_SanCarlos("San Carlos", 10.3238, -84.4271, "Alajuela"),
    
    // HEREDIA
    HE_Centro("Heredia Centro", 9.9981, -84.1197, "Heredia"),
    HE_Barva("Barva", 10.0131, -84.1200, "Heredia");

    private final String nombre;
    private final double lat;
    private final double lon;
    private final String provincia; // Nuevo campo

    PuntoEntrega(String nombre, double lat, double lon, String provincia) {
        this.nombre = nombre;
        this.lat = lat;
        this.lon = lon;
        this.provincia = provincia;
    }

    public double getLatitud() { return lat; }
    public double getLongitud() { return lon; }
    public String getProvincia() { return provincia; }

    @Override
    public String toString() { return nombre; }
}
