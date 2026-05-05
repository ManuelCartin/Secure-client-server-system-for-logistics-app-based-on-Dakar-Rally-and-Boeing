/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 *
 * @author Vane
 */
public class ClientConnector implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientConnector(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Response sendRequest(Request req) throws Exception {
        out.writeObject(req);
        out.flush();
        Object resp = in.readObject();
        if (resp instanceof Response response) return response;
        throw new IllegalStateException("Respuesta inesperada del servidor");
    }

    @Override
    public void close() throws Exception {
        try { if (out != null) out.close(); } catch (IOException e) {}
        try { if (in != null) in.close(); } catch (IOException e) {}
        try { if (socket != null) socket.close(); } catch (IOException e) {}
    }
}