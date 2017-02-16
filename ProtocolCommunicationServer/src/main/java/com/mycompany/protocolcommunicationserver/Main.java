/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.IOException;
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
        Communication c = new SocketServer(6789);
        c.Connect();
        JSONObject jsonObject = new JSONObject();
        while(true){
            jsonObject = (JSONObject) c.Receive();
            Long s = (Long) jsonObject.get("Command");
            if(s != null)
                System.out.println(s);
            jsonObject.clear();
            jsonObject.put("Test", "Ricevuto");
            c.Send(jsonObject);
            while(!c.Close());
            break;
        }
    }
    
}
