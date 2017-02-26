/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
    
    public static byte[] GenerateArrayByte(byte[] cmd, byte[] opCode, byte[] LenSeg, byte[] buffByte){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(cmd);
            outputStream.write(opCode);
            outputStream.write(LenSeg);
            outputStream.write(buffByte);
        } catch (IOException ex) { }
        return outputStream.toByteArray();
    }
    
    public static void main(String[] args) throws IOException {
        File f = new File("C:\\");
        System.out.println("Total space: " + f.getTotalSpace() +" bytes");
        System.out.println("Free space: " + f.getFreeSpace() +" bytes");
        
        SocketServer comm = new SocketServer(6789);
        
        comm.Connect();
        System.out.println("Connected with " + comm.getConnectionSocket().getInetAddress());

        ServerPacker packer = new ServerPacker();
        
        try{
            packer.Unpack(comm.Receive());
        }catch(Exception ex){
            comm.Send(packer.Nack((long) 10, (long) 0));
        }
        byte[] chk = new byte[0];
        if(Arrays.equals(packer.getCheckSum(), chk)){
            comm.Send(packer.Nack((long) 6, (long) 0));
            comm.Close();
        }
        System.out.println(packer.toString());
        int totseg = ((Long) packer.getOpCode()).intValue();
        if((totseg * 2048) > f.getFreeSpace()){
            comm.Send(packer.Nack((long) 8, (long) 0));
            comm.Close();
        }
        if(((byte[]) packer.getMD5()).length != 16){
            comm.Send(packer.Nack((long) 9, (long) 0));
            comm.Close();
        }
        
        FileSaver fs = new FileSaver(packer.getNome_file());
        
        comm.Send(packer.Ack(fs.getNextSeg()));
        int maxRetry = 0;
        //int prova =0;
        boolean packetError = false;
        while(true){
            packetError = false;
            try{
                packer.Unpack(comm.Receive());
            }catch(Exception ex){
                packetError = true;
            }
            if(packetError == true){
                comm.Send(packer.Nack((long) 10, fs.getNextSeg()));
            }
            else{
                if(Arrays.equals(packer.getCheckSum(), chk)){
                    comm.Send(packer.Nack((long) 6, (long) 0));
                    comm.Close();
                }
                System.out.println(packer.toString());
                if(packer.getCommand().equals("E")){
                    comm.Send(packer.Ack((long)0));
                    break;
                }
                else if(packer.getCommand().equals("U")){
                    comm.Send(packer.Nack((long) 1, fs.getNextSeg()));
                    maxRetry++;
                    if(maxRetry == 3){
                        comm.Send(packer.Nack((long) 3,(long) 0));
                        comm.Close();
                    }
                }
                else if(packer.getOpCode() > totseg){
                    comm.Send(packer.Nack((long) 4, fs.getNextSeg()));
                    maxRetry++;
                    if(maxRetry == 3){
                        comm.Send(packer.Nack((long) 3,(long) 0));
                        comm.Close();
                    }
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

                    /*if(prova == 3){
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
        }
        comm.Close();
        
    }
    
}
