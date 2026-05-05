/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.App;

/**
 *
 * @author user
 */
public class Launcher {
    public static void main(String[] args) {

        new Thread(() -> {
            com.mycompany.tiendaapp.server.ServerMain.main(args);
        }).start();

        com.mycompany.tiendaapp.App.AppClient.main(args);
    }
}
