/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.server;

import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.dao.*;
import com.mycompany.tiendaapp.model.User;
import com.mycompany.tiendaapp.util.BCryptUtil;
import com.mycompany.tiendaapp.dao.CheckpointDAO;
import com.mycompany.tiendaapp.service.IncidenciaService;


import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {

    private Socket socket;

    private UserDAO userDAO = new UserDAO();
    private PaqueteDAO paqueteDAO = new PaqueteDAO();
    private VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private TrackingDAO trackingDAO = new TrackingDAO();
    private MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();
    private PlanillaDAO planillaDAO = new PlanillaDAO();
    private CheckpointDAO checkpointDAO = new CheckpointDAO();
    private ChoferDAO choferDAO = new ChoferDAO();


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            Object obj;

            while ((obj = in.readObject()) != null) {

                if (!(obj instanceof Request)) continue;

                Request req = (Request) obj;

                Response resp = handleRequest(req);

                out.writeObject(resp);
                out.flush();
            }

        } catch (EOFException eof) {
            System.out.println("Cliente desconectado");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception ex) {}
        }
    }

    private Response handleRequest(Request req) {
    try {
        String action = req.getAction();
        Map<String, Object> p = req.getPayload();

        if ("login".equalsIgnoreCase(action)) {

            String usuario = (String) p.get("usuario");
            String clave = (String) p.get("clave");

            User u = userDAO.findByUsername(usuario);

            if (u == null)
                return new Response(false, "Usuario no existe");

            if (!BCryptUtil.checkPassword(clave, u.getClave()))
                return new Response(false, "Contraseña incorrecta");

            Response r = new Response(true, u.getRol());
            r.setData(u);
            return r;
        }



        if ("register".equalsIgnoreCase(action)) {

            String nombre = (String) p.get("nombre");
            String usuario = (String) p.get("usuario");
            String clave = (String) p.get("clave");
            String correo = (String) p.get("correo");
            String rol = (String) p.get("rol");

            if (userDAO.findByUsername(usuario) != null)
                return new Response(false, "Usuario ya existe");

            String hashed = BCryptUtil.hashPassword(clave);

            User u = new User(nombre, usuario, hashed, correo, rol);

            boolean ok = userDAO.create(u);

            return ok
                    ? new Response(true, "Usuario registrado")
                    : new Response(false, "Error al registrar");
        }


        if ("listarUsuarios".equalsIgnoreCase(action)) {

            List<User> users = userDAO.getUsers();
            Object[][] datos = new Object[users.size()][5];

            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                datos[i][0] = u.getIdUsuario();
                datos[i][1] = u.getNombre();
                datos[i][2] = u.getUsuario();
                datos[i][3] = u.getCorreo();
                datos[i][4] = u.getRol();
            }

            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }
        
        if ("listarChoferes".equalsIgnoreCase(action)) {
            List<Object[]> lista = choferDAO.listarChoferes();
            Response r = new Response(true, "OK");
            r.setData(lista.toArray(new Object[0][]));
            return r;
        }



        if ("deleteUser".equalsIgnoreCase(action)) {

            int idUsuario = (int) p.get("idUsuario");

            User u = new User();
            u.setIdUsuario(idUsuario);

            boolean ok = userDAO.delete(u);

            return ok
                    ? new Response(true, "Usuario eliminado")
                    : new Response(false, "Error eliminando usuario");
        }


        if ("listarPaquetes".equalsIgnoreCase(action)) {
            Object[][] datos = paqueteDAO.listarMonitoreo();
            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }

        if ("asignarRuta".equalsIgnoreCase(action)) {

            int idPaquete = (int) p.get("idPaquete");
            int idUsuarioChofer = (int) p.get("idChofer");
            int idChofer = choferDAO.obtenerIdChoferPorUsuario(idUsuarioChofer);

            int idVehiculo = (int) p.get("idVehiculo");

            boolean ok = paqueteDAO.asignarRuta(idPaquete, idChofer, idVehiculo);

            return ok
                    ? new Response(true, "Ruta asignada correctamente")
                    : new Response(false, "Error asignando ruta");
        }


        if ("registrarUbicacion".equalsIgnoreCase(action)) {

            int idPaquete = (int) p.get("idPaquete");
            String punto = (String) p.get("punto");

            boolean ok = trackingDAO.registrarTracking(idPaquete, punto);

            return ok
                    ? new Response(true, "Ubicación registrada")
                    : new Response(false, "Error registrando ubicación");
        }

        if ("listarTracking".equalsIgnoreCase(action)) {

            Object[][] datos = trackingDAO.listarTrackingGlobal();

            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }
        

        if ("asignarPaqueteChofer".equalsIgnoreCase(action)) {

            int idPaquete = (int) p.get("idPaquete");
            String choferNombre = (String) p.get("chofer");
            String vehiculoNombre = (String) p.get("vehiculo");

            // Obtener ID del chofer
            int idChofer = choferDAO.obtenerIdChoferPorNombre(choferNombre);
            if (idChofer <= 0)
                return new Response(false, "Chofer no encontrado");

            // Obtener ID del vehículo
            int idVehiculo = vehiculoDAO.obtenerIdPorNombre(vehiculoNombre);
            if (idVehiculo <= 0)
                return new Response(false, "Vehículo no encontrado");

            boolean ok = paqueteDAO.asignarRuta(idPaquete, idChofer, idVehiculo);

            return ok
                    ? new Response(true, "Paquete asignado correctamente")
                    : new Response(false, "Error asignando paquete");
        }


        if ("listarVehiculos".equalsIgnoreCase(action)) {

            Object[][] datos = vehiculoDAO.listarVehiculos();

            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }

        if ("enviarTaller".equalsIgnoreCase(action)) {

            String placa = (String) p.get("placa");

            boolean ok = vehiculoDAO.enviarAMantenimiento(placa);

            return ok
                    ? new Response(true, "Vehículo enviado a mantenimiento")
                    : new Response(false, "Error enviando a taller");
        }

        if ("registrarMantenimiento".equalsIgnoreCase(action)) {

            int idVehiculo = (int) p.get("idVehiculo");
            String descripcion = (String) p.get("descripcion");
            String estado = (String) p.get("estado");

            boolean ok = mantenimientoDAO.registrarMantenimiento(idVehiculo, descripcion, estado);

            return ok
                    ? new Response(true, "Mantenimiento registrado")
                    : new Response(false, "Error registrando mantenimiento");
        }

        if ("listarMantenimientos".equalsIgnoreCase(action)) {

            Object[][] datos = mantenimientoDAO.listarHistorial();

            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }


        if ("listarPlanilla".equalsIgnoreCase(action)) {

            Object[][] datos = planillaDAO.listarPlanilla();

            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }

        if ("registrarPersonal".equalsIgnoreCase(action)) {

            int idUsuario = (int) p.get("idUsuario");
            int horas = (int) p.get("horas");
            int entregas = (int) p.get("entregas");
            String estado = (String) p.get("estado");

            boolean ok = planillaDAO.registrar(idUsuario, horas, entregas, estado);

            return ok
                    ? new Response(true, "Personal registrado")
                    : new Response(false, "Error registrando personal");
        }

        if ("listarPaquetesCliente".equalsIgnoreCase(action)) {
            int idCliente = (int) p.get("idCliente");
            Object[][] datos = paqueteDAO.listarPorCliente(idCliente);
            Response r = new Response(true, "OK");
            r.setData(datos);
            return r;
        }

        if ("registrarPaquete".equalsIgnoreCase(action)) {
            int idCliente = (int) p.get("idCliente");
            String descripcion = (String) p.get("descripcion");
            String destino = (String) p.get("destino");

            boolean ok = paqueteDAO.registrarPaquete(idCliente, descripcion, destino);

            return ok ? new Response(true, "Paquete registrado")
                      : new Response(false, "Error registrando paquete");
        }

        if ("actualizarPaquete".equalsIgnoreCase(action)) {
            int idPaquete = (int) p.get("idPaquete");
            String descripcion = (String) p.get("descripcion");
            String destino = (String) p.get("destino");
            String estado = (String) p.get("estado");

            boolean ok = paqueteDAO.actualizarPaquete(idPaquete, descripcion, destino, estado);

            return ok ? new Response(true, "Paquete actualizado")
                      : new Response(false, "Error actualizando paquete");
        }

        if ("eliminarPaquete".equalsIgnoreCase(action)) {
            int idPaquete = (int) p.get("idPaquete");

            boolean ok = paqueteDAO.eliminarPaquete(idPaquete);

            return ok ? new Response(true, "Paquete eliminado")
                      : new Response(false, "Error eliminando paquete");
        }

        if ("updateUser".equalsIgnoreCase(action)) {

            int idUsuario = (int) p.get("idUsuario");
            String nombre = (String) p.get("nombre");
            String usuario = (String) p.get("usuario");
            String correo = (String) p.get("correo");
            String rol = (String) p.get("rol");
            String clave = (String) p.get("clave"); // puede venir vacía

            User u = new User();
            u.setIdUsuario(idUsuario);
            u.setNombre(nombre);
            u.setUsuario(usuario);
            u.setCorreo(correo);
            u.setRol(rol);


            if (clave != null && !clave.isBlank()) {
                String hashed = BCryptUtil.hashPassword(clave);
                u.setClave(hashed);
            } else {
                u.setClave(null);
            }

            boolean ok = userDAO.update(u);

            return ok
                    ? new Response(true, "Usuario actualizado")
                    : new Response(false, "Error actualizando usuario");
        }
        

        if ("crearRutaConRoadbook".equalsIgnoreCase(action)) {

        int idUsuarioChofer = (int) p.get("idChofer");
        int idChofer = choferDAO.obtenerIdChoferPorUsuario(idUsuarioChofer);

        int idVehiculo = (int) p.get("idVehiculo");

        List<Map<String, Object>> checkpoints = (List<Map<String, Object>>) p.get("checkpoints");
        List<Integer> paquetes = (List<Integer>) p.get("paquetes");

        RutaDAO rutaDAO = new RutaDAO();
        CheckpointDAO checkpointDAO = new CheckpointDAO();
        RoadbookDAO roadbookDAO = new RoadbookDAO();

        // Crear la ruta y obtener idRuta
        int idRuta = rutaDAO.crearRuta(idChofer, idVehiculo);

        if (idRuta <= 0) {
            return new Response(false, "Error creando ruta");
        }

    // Insertar checkpoints con idRuta correcto
    for (Map<String, Object> cp : checkpoints) {
        checkpointDAO.insertarCheckpoint(
            idRuta,
            (int) cp.get("orden"),
            (String) cp.get("nombre"),
            (double) cp.get("latitud"),
            (double) cp.get("longitud"),
            (double) cp.get("distancia"),
            (int) cp.get("tiempo")
        );
    }

    // Asociar paquetes a la ruta
    for (int idPaquete : paquetes) {
        roadbookDAO.asociarPaquete(idPaquete, idRuta);
    }

    // Respuesta final
    Response r = new Response(true, "Ruta creada con éxito");
    r.setData(idRuta);
    return r;
}


        if ("listarRoadbook".equalsIgnoreCase(action)) {

            int idRuta = (int) p.get("idRuta");

            List<Map<String, Object>> lista = checkpointDAO.listarRoadbook(idRuta);

            Response r = new Response(true, "OK");
            r.setData(lista);
            return r;
        }

        if ("registrarCheckpoint".equalsIgnoreCase(action)) {

            int idRuta = (int) p.get("idRuta");
            int idCheckpoint = (int) p.get("idCheckpoint");
            String observacion = (String) p.get("observacion");
            String chofer = (String) p.get("chofer");

            boolean ok = checkpointDAO.registrarCheckpoint(idRuta, idCheckpoint, observacion, chofer);

            return ok
                    ? new Response(true, "Checkpoint registrado")
                    : new Response(false, "Error registrando checkpoint");
        }

        if ("listarProgresoRuta".equalsIgnoreCase(action)) {

            int idRuta = (int) p.get("idRuta");

            List<Map<String, Object>> progreso = checkpointDAO.listarProgreso(idRuta);

            Response r = new Response(true, "OK");
            r.setData(progreso);
            return r;
        }

        if ("listarPaquetesPorCheckpoint".equalsIgnoreCase(action)) {

            int idCheckpoint = (int) p.get("idCheckpoint");

            List<Integer> paquetes = checkpointDAO.listarPaquetesPorCheckpoint(idCheckpoint);

            Response r = new Response(true, "OK");
            r.setData(paquetes);
            return r;
        }

        if ("obtenerRutaActivaDelChofer".equalsIgnoreCase(action)) {

            int idChofer = (int) p.get("idChofer");

            RutaDAO rutaDAO = new RutaDAO();
            Integer idRuta = rutaDAO.obtenerRutaActiva(idChofer);

            if (idRuta == null)
                return new Response(false, "No tiene rutas activas");

            Response r = new Response(true, "OK");
            r.setData(idRuta);
            return r;
        }
        
        if ("obtenerPrimerCheckpoint".equalsIgnoreCase(action)) {

            int idRuta = (int) p.get("idRuta");

            int idCheckpoint = checkpointDAO.obtenerPrimerCheckpoint(idRuta);

            if (idCheckpoint <= 0)
                return new Response(false, "No hay checkpoints para esta ruta");

            Response r = new Response(true, "OK");
            r.setData(idCheckpoint);
            return r;
        }

       if ("asignarPaqueteCheckpoint".equalsIgnoreCase(action)) {

            int idPaquete = (int) p.get("idPaquete");
            int idRuta = (int) p.get("idRuta");
            int idCheckpoint = (int) p.get("idCheckpoint");

            boolean ok = checkpointDAO.asignarPaqueteCheckpoint(idPaquete, idRuta, idCheckpoint);

            return ok
                    ? new Response(true, "Paquete asignado al checkpoint")
                    : new Response(false, "Error asignando paquete al checkpoint");
        }
       
       if ("completarMantenimiento".equalsIgnoreCase(action)) {

    String placa = (String) p.get("placa");
    String notas = (String) p.get("notas");
    String nuevoEstado = (String) p.get("nuevoEstado");

    // Registrar mantenimiento en historial
    boolean okHistorial = mantenimientoDAO.registrarMantenimientoPorPlaca(placa, notas);

    // Actualizar estado del vehículo
    boolean okVehiculo = vehiculoDAO.actualizarEstado(placa, nuevoEstado);

    if (okHistorial && okVehiculo) {

        // Eliminar incidencia de memoria (ya fue reparada)
        IncidenciaService.eliminarIncidenciaPorPlaca(placa);

        return new Response(true, "Mantenimiento completado correctamente.");
    } else {
        return new Response(false, "Error completando mantenimiento.");
    }
}
        return new Response(false, "Acción no soportada: " + action);

    } catch (Exception ex) {
        ex.printStackTrace();
        return new Response(false, "Error interno: " + ex.getMessage());
    }
}

}
