/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
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
        
        JSONObject packet = (JSONObject) packer.Upload(build.getTotSeg(), nome_file, build.getMD5());
        comm.Send(packet);
        
        packet.clear();
        packet = (JSONObject) comm.Receive();
        packer.Unpack(packet);
        System.out.println(packer.toString());
        
        while(packer.getOpCode() <= build.getTotSeg()){
            packet = (JSONObject) packer.Send(packer.getOpCode(), build.Build(packer.getOpCode(), selectedFile));
            comm.Send(packet);
            packer.Unpack(comm.Receive());
        }
        comm.Send(packer.End(1));
        packer.Unpack((JSONObject) comm.Receive());
        System.out.println(packer.toString());
        comm.Close();
        jf.dispose();
    }
}
