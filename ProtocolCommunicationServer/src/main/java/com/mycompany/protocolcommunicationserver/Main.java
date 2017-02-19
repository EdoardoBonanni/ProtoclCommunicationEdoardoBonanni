/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author studente
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        SocketServer comm = new SocketServer(6789);
        comm.Connect();
        System.out.println("Connected with " + comm.getConnectionSocket().getInetAddress());
        JSONObject jsonObject = new JSONObject();
        ServerPacker packer = new ServerPacker();
        jsonObject = (JSONObject) comm.Receive();
        packer.Unpack(jsonObject);
        System.out.println(packer.toString());
        comm.Send(packer.Ack((long)1));
        jsonObject.clear();
        while(true){
            jsonObject = (JSONObject) comm.Receive();
            packer.Unpack(jsonObject);
            System.out.println(packer.toString());
            comm.Send(packer.Ack((long)2));
            comm.Close();
            break;
        }
    }
    
}
