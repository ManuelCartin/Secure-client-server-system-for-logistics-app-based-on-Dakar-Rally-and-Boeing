/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.service;

import java.util.ArrayList;
import java.util.List;
import com.mycompany.tiendaapp.model.Incidencia;

public class IncidenciaService {

    private static final List<Incidencia> incidencias = new ArrayList<>();

    public static void agregarIncidencia(Incidencia i) {
        incidencias.add(i);
    }

    public static String obtenerIncidenciaPorPlaca(String placa) {
        if (placa == null) return null;

        for (Incidencia i : incidencias) {
            if (i != null && i.getVehiculo() != null && i.getVehiculo().contains(placa)) {
                return i.getDescripcion();
            }
        }
        return null;
    }

    public static List<Incidencia> obtenerIncidencias() {
        return incidencias;
    }
    public static void eliminarIncidenciaPorPlaca(String placa) {
    incidencias.removeIf(i -> i.getVehiculo().equals(placa));
}

}

