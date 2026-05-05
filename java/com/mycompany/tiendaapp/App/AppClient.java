/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.App;

import com.mycompany.tiendaapp.controller.AuthController;
import com.mycompany.tiendaapp.view.LoginView;
import javax.swing.*;

public class AppClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView lv = new LoginView();
            new AuthController(lv);
            lv.setVisible(true);
        });
    }
}
