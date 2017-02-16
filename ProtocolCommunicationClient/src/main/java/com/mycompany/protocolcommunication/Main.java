/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Communication c = new SocketClient("localhost", 6789);
        Packer p = new ClientPacker();
        while(!c.Connect());
        JSONObject jsonObject = new JSONObject();
        while(true){
            jsonObject = (JSONObject) p.Upload(10, "Pippo", "blabla");
            c.Send(jsonObject);
            jsonObject.clear();
            jsonObject = (JSONObject) c.Receive();
            String s = (String) jsonObject.get("Test");
            if(s != null)
                System.out.println(s);
            while(!c.Close());
            break;
        }
    }
    
}
