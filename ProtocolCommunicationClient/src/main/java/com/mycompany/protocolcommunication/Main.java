/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.*;
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
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(new File(userDir + "/Desktop"));
        JFrame jf = new JFrame();
        
        Communication comm = new SocketClient("localhost", 6789);
        ClientPacker packer = new ClientPacker();
        
        while(!comm.Connect());
        
        int returnVal = fc.showOpenDialog(jf);
        File selectedFile = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
        }
        
        String nome_file = JOptionPane.showInputDialog(jf, "Nome del File");
        
        SendBuilder build = new SendBuilder(selectedFile);
        
        JSONObject packet = (JSONObject) packer.Upload(build.getTotSeg(), nome_file + "." + build.getFileExtension(selectedFile), build.getMD5());
        comm.Send(packet);
        
        packet.clear();
        packet = (JSONObject) comm.Receive();
        packer.Unpack(packet);
        System.out.println(packer.toString());
        boolean reSend = false;
        byte[] chk = new byte[0];
        boolean packetError = false;
        while(packer.getOpCode() <= build.getTotSeg() && (packer.getCommand().equals("A")  || packer.getCommand().equals("N"))){
            if(reSend){
                packet = (JSONObject) packer.Send(packer.getNextSeg(), build.Build(packer.getNextSeg(), selectedFile));
            }
            else{
                packet = (JSONObject) packer.Send(packer.getOpCode(), build.Build(packer.getOpCode(), selectedFile));
            }
            comm.Send(packet);
            try{
                packer.Unpack(comm.Receive());
            }catch(Exception ex){
                comm.Close();
            }
            if(Arrays.equals(packer.getCheckSum(), chk)){
                comm.Close();
            }
            if(packer.getCommand().equals("N") && packer.getOpCode() == 2){
                reSend = true;
            }
            else if(packer.getCommand().equals("N") && packer.getOpCode() == 1){
                reSend = true;
            }
            else if(packer.getCommand().equals("N") && packer.getOpCode() == 3){
                comm.Close();
            }
            else{
                reSend = false;
            }
            System.out.println(packer.toString());
        }
        comm.Send(packer.End(1));
        packer.Unpack((JSONObject) comm.Receive());
        System.out.println(packer.toString());
        comm.Close();
        jf.dispose();
    }
}
