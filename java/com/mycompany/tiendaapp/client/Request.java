/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendaapp.client;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vane
 */

public class Request implements Serializable {

    private String action;
    private Map<String, Object> payload = new HashMap<>();

    public Request() {}

    public Request(String action) {
        this.action = action;
    }

    public void add(String key, Object value) {
        payload.put(key, value);
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}
