/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Communication comm = new SocketClient("localhost", 6789);
        ClientPacker packer = new ClientPacker();
        SendFile file = new SendFile();
        
        while(!comm.Connect());
        JSONObject jsonObject = new JSONObject();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        
        while(true){
            System.out.println("Inserire il path dell'immagine da inviare ");
            String imagePath = input.readLine();

            System.out.println("Inserire nome del file"); 
            String nome_file = input.readLine();
            
            file.CheckFile(imagePath);
            
            jsonObject = (JSONObject) packer.Upload(file.getTotSeg(), nome_file, file.getMD5());
            comm.Send(jsonObject);
            jsonObject.clear();
            jsonObject = (JSONObject) comm.Receive();
            packer.Unpack(jsonObject);
            System.out.println(packer.getOpCode());
            comm.Close();
            break;
        }
    }
    
}
