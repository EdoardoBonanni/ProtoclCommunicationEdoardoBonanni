/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author studente
 */
public class SocketServer implements Communication {
    
    private ServerSocket socketS;
    
    public SocketServer(int port) throws IOException{
        this.socketS = new ServerSocket(port);   
    }
    

    @Override
    public boolean Connect() {
        try{
            this.socketS = new ServerSocket(6789);
            while(true){
                    socketS.setSoTimeout(10000);
                    //Si apre la socket del server
                    Socket connectionSocket = socketS.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    return true;
            }
        } catch (IOException ex) { }
        return false;
    }

    @Override
    public boolean Send() {
        return true;
        
    }

    @Override
    public boolean Receive() {
        return true;
    }

    @Override
    public boolean Close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
