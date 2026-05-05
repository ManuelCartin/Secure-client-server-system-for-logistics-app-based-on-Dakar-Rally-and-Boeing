/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.controller;
import com.mycompany.tiendaapp.client.ClientConnector;
import com.mycompany.tiendaapp.client.Request;
import com.mycompany.tiendaapp.client.Response;
import com.mycompany.tiendaapp.model.User;
import com.mycompany.tiendaapp.view.UserView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Vane
 */
public class UserController {
    private UserView userView;
    private String host = "localhost";
    private int port = 5555;

    public UserController(UserView userView) {
        this.userView = userView;
        initActions();
        loadUsers();
    }
    
    private void initActions() {
       this.userView.btnGuardar.addActionListener(e -> doSave());
        this.userView.btnEliminar.addActionListener(e -> doDelete());
        this.userView.btnActualizar.addActionListener(e-> doUpdate());
        this.userView.tblUsers.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                loadSelectedUser();
            }
        });
        
    }
    
    private void doSave(){
        String nombre = userView.txtNombre.getText().trim();
        String usuario = userView.txtUsuario.getText().trim();
        String correo = userView.txtCorreo.getText().trim();
        String clave = userView.txtClave.getText().trim();
        String rol = (String) userView.cmbRol.getSelectedItem();
        
        if((nombre.isEmpty()) ||(usuario.isEmpty()) || (correo.isEmpty()) || (clave.isEmpty())){
            JOptionPane.showMessageDialog(userView, "Todos los campos son obligatirios");
            return;
        }
        
        try(ClientConnector conn =  new ClientConnector(host, port)){
            Request r = new Request();
            r.setAction("register");
            Map<String, Object> p = new HashMap<>();
            p.put("nombre", nombre);
            p.put("usuario", usuario);
            p.put("correo", correo);
            p.put("clave", clave);
            p.put("rol", rol);
            r.setPayload(p);
            
            Response resp = conn.sendRequest(r);
            JOptionPane.showMessageDialog(userView, resp.getMessage());
            
            if(resp.isSuccess()){
                clearFields();
                loadUsers();
            }
                        
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userView, "Error de conexión: " + ex.getMessage());
        } 
    }
    
    private void doDelete(){
        int selectRow = userView.tblUsers.getSelectedRow();
        if(selectRow == -1){
            JOptionPane.showMessageDialog(userView, "Seleccione un usuario de la tabla");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(userView, 
                "¿Está seguro de elminar este usuario?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
        
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        
        Integer idUsuario = (Integer) userView.tblUsers.getValueAt(selectRow, 0);
        
        try(ClientConnector conn =  new ClientConnector(host, port)){
            Request r = new Request();
            r.setAction("deleteUser");
            Map<String, Object> p = new HashMap<>();
            p.put("idUsuario", idUsuario);
            r.setPayload(p);
            
            Response resp = conn.sendRequest(r);
            JOptionPane.showMessageDialog(userView, resp.getMessage());
            
            if(resp.isSuccess()){
                clearFields();
                loadUsers();
            }
                        
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userView, "Error de conexión: " + ex.getMessage());
        } 
    }
    
    private void loadUsers(){
        try(ClientConnector conn = new ClientConnector(host, port)){
            Request r = new Request();
            r.setAction("listUsers");
            r.setPayload(new HashMap<>());
            
            Response resp =  conn.sendRequest(r);
            
            if (resp.isSuccess() && resp.getData() != null){
                List<User> users = (List<User>) resp.getData();
                updateTable(users);
            }
        }catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userView, "Error al cargar usuarios: " + ex.getMessage());
         }
    }
    
    private void updateTable(List<User> users){
        DefaultTableModel model = (DefaultTableModel) userView.tblUsers.getModel();
        model.setRowCount(0); //Limpiar Tabla
        
        for(User user: users){
            model.addRow(new Object[]{
                user.getIdUsuario(),
                user.getNombre(),
                user.getUsuario(),
                user.getCorreo(),
                user.getRol()
            });
        }
    }
    
    private void loadSelectedUser(){
        int selectedRow = userView.tblUsers.getSelectedRow();
        if(selectedRow != -1){
            userView.txtNombre.setText((String) userView.tblUsers.getValueAt(selectedRow, 1));
            userView.txtUsuario.setText((String) userView.tblUsers.getValueAt(selectedRow, 2));
            userView.txtCorreo.setText((String) userView.tblUsers.getValueAt(selectedRow, 3));
            userView.txtClave.setText(""); //No mostrar clave por seguridad
            String rol = (String) userView.tblUsers.getValueAt(selectedRow, 4);
            userView.cmbRol.setSelectedItem(rol);
            
        }
    }
    
    
    private void clearFields(){
        userView.txtNombre.setText("");
        userView.txtUsuario.setText("");
        userView.txtCorreo.setText("");
        userView.txtClave.setText("");
        userView.cmbRol.setSelectedIndex(0);
        userView.tblUsers.clearSelection();
    }
    
    private void doUpdate(){
        int selectedRow = userView.tblUsers.getSelectedRow();
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(userView,
                    "Seleccion un usuario de la tabla",
                    "Actualizar Usuario"
                    , JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer idUsuario = (Integer) userView.tblUsers.getValueAt(selectedRow, 0);
        String nombre = userView.txtNombre.getText().trim();
        String usuario = userView.txtUsuario.getText().trim();
        String correo = userView.txtCorreo.getText().trim();
        String clave = userView.txtClave.getText().trim();
        String rol = (String) userView.cmbRol.getSelectedItem();
        
        if(nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty()){
            JOptionPane.showMessageDialog(userView,
                    "Debe llenar todos los campos obligatorios: Usuario, Correo, Nombre",
                    "Actualizar Usuario"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try(ClientConnector conn = new ClientConnector(host, port)){
            Request r = new Request();
            r.setAction("updateUser");
            
            Map<String, Object> p = new HashMap<>();
            p.put("idUsuario", idUsuario);
            p.put("nombre", nombre);
            p.put("usuario", usuario);
            p.put("correo", correo);
            p.put("rol", rol);
            
            if(!clave.isEmpty()){
                p.put("clave", clave);
            }
            
            r.setPayload(p);
            
            Response resp = conn.sendRequest(r);
            
            
            if(resp.isSuccess()){
                JOptionPane.showMessageDialog(userView,
                    resp.getMessage(),
                    "Actualizar Usuario",
                    JOptionPane.INFORMATION_MESSAGE );
                clearFields();
                loadUsers();
            }else{
                JOptionPane.showMessageDialog(userView,
                    resp.getMessage(),
                    "Actualizar Usuario",
                    JOptionPane.ERROR_MESSAGE );
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userView,
                    ex.getMessage(),
                    "Actualizar Usuario"
                    , JOptionPane.ERROR_MESSAGE);
        }
        
    }
}
