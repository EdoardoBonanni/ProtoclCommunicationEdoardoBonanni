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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author studente
 */
public class SocketServer implements Communication {
    
    private ServerSocket socketS;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;
    private Socket connectionSocket;
    
    public SocketServer(int port) throws IOException{
        this.socketS = new ServerSocket(port);   
    }
    

    @Override
    public boolean Connect() {
        try{
            while(true){
                    //Si apre la socket del server
                    connectionSocket = socketS.accept();
                    inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    return true;
            }
        } catch (IOException ex) { return false; }
    }

    @Override
    public boolean Send(Object obj) {
        try {
            outToClient.writeBytes(obj.toString() + '\n');
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public JSONObject Receive() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject ClientJsonObject = (JSONObject) jsonParser.parse(inFromClient.readLine());
            return ClientJsonObject;
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    @Override
    public boolean Close() {
        try {
            connectionSocket.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }
}
