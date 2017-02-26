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
 * @author Edoardo
 */
public class Main {

    public static final byte checkSum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
           sum ^= b;
        }
        return sum;
    }
    
    public static void main(String[] args) throws IOException {
        SocketServer comm = new SocketServer(6789);
        comm.Connect();
        System.out.println("Connected with " + comm.getConnectionSocket().getInetAddress());

        ServerPacker packer = new ServerPacker();
        
        packer.Unpack(comm.Receive());
        System.out.println(packer.toString());
        FileSaver fs = new FileSaver(packer.getNome_file());
        
        comm.Send(packer.Ack(fs.getNextSeg()));
        int maxRetry = 0;
        //int prova =0;
        while(true){
            packer.Unpack(comm.Receive());
            System.out.println(packer.toString());
            if(packer.getCommand().equals("E")){
                comm.Send(packer.Ack((long)0));
                break;
            }
            else if(packer.getCommand().equals("U")){
                comm.Send(packer.Nack((long) 1, fs.getNextSeg()));
                maxRetry++;;
            }
            else if(packer.getOpCode() > fs.getNextSeg()){
                comm.Send(packer.Nack(2, fs.getNextSeg()));
                maxRetry++;
                if(maxRetry == 3){
                    comm.Send(packer.Nack((long) 3,(long) 0));
                    comm.Close();
                }
            }
            else{
                maxRetry = 0;
                fs.toFile(packer.getBuffer());
                /*
                if(prova == 3){
                    comm.Send(packer.Ack((long)10));
                    prova++;
                }
                else{*/
                fs.getNextSeg();
                comm.Send(packer.Ack(fs.getNextSeg()));
                /*}
                prova++;*/
            }
        }
        comm.Close();
    }
    
}
