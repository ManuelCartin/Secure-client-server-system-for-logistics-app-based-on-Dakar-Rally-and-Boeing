/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.server;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Vane
 */

public class ServerMain {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        try (ServerSocket server = new ServerSocket(PORT)) {
            server.setReuseAddress(true); 
            System.out.println("Servidor Quick Delivery vinculado a MySQL listo en puerto " + PORT);
            
            while (true) {
                Socket client = server.accept();
                System.out.println("📱 Cliente conectado: " + client.getRemoteSocketAddress());
                pool.submit(new ClientHandler(client));
            }
        } catch (Exception e) {
            System.err.println("Error crítico en el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}