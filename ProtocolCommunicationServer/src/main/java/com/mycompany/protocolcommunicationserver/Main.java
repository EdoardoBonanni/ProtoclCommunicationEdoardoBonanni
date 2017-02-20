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
        
        JSONObject packet;
        ServerPacker packer = new ServerPacker();
        
        packer.Unpack(comm.Receive());
        System.out.println(packer.toString());
        FileSaver fs = new FileSaver(packer.getNome_file());
        
        comm.Send(packer.Ack(fs.getNextSeg()));
        
        while(true){
            packer.Unpack(comm.Receive());
            System.out.println(packer.toString());
            if(packer.getCommand() == 3){
                comm.Send(packer.Ack((long)0));
                break;
            }
            fs.toFile(packer.getBuffer());
            fs.getNextSeg();
            comm.Send(packer.Ack(fs.getNextSeg()));
        }
        System.out.println(packer.toString());
        comm.Close();
    }
    
}
