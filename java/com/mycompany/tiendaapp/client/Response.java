/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.client;
import java.io.Serializable;
/**
 *
 * @author Vane
 */
public class Response implements Serializable {
    private boolean success;
    private String message;
    private Object data;


    public Response() {}
    public Response(boolean success, String message) {
        this.success = success; this.message = message;
    }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
