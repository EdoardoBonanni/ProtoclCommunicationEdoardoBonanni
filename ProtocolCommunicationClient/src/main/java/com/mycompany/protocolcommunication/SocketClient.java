/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Edoardo
 */
public class SocketClient implements Communication {

    private Socket socketC;
    private DataOutputStream outServ;
    private BufferedReader inServ;
    
    public SocketClient(String ip, int port) {
        try {
            this.socketC = new Socket(ip, port);
        } catch (IOException ex) {
            
        }
    }
    
    @Override
    public boolean Connect() {
        try {
            outServ = new DataOutputStream(socketC.getOutputStream());
            inServ = new BufferedReader(new InputStreamReader(socketC.getInputStream()));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean Send(Object obj) {
        try {
            outServ.writeBytes(obj.toString() + '\n');
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public JSONObject Receive() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject ServerJsonObject = (JSONObject) jsonParser.parse(inServ.readLine());
            return ServerJsonObject;
        } catch (IOException | ParseException ex){
            return new JSONObject();
        }
    }

    @Override
    public boolean Close() {
        try {
            this.socketC.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
}
