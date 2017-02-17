/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
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
        Communication comm = new SocketServer(6789);
        comm.Connect();
        JSONObject jsonObject = new JSONObject();
        ServerPacker packer = new ServerPacker();
        jsonObject = (JSONObject) comm.Receive();
        packer.Unpack(jsonObject);
        comm.Send(packer.Ack(1));
        jsonObject.clear();
        while(true){
            jsonObject = (JSONObject) comm.Receive();
            packer.Unpack(jsonObject);
            comm.Send(packer.Ack(2));
            comm.Close();
            break;
        }
    }
    
}
