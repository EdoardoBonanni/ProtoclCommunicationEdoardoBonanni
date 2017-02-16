/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author Edoardo
 */
public class SendFile {
    private int TotSeg;
    private byte[] MD5;

    public SendFile() {
    }
    
    public void CheckFile(String imagePath){
        File myFile = new File(imagePath);
        String imageDataString = "";
        byte imageData[] = null;
        try {            
            try (FileInputStream imageInFile = new FileInputStream(myFile)) {
                imageData = new byte[(int) myFile.length()];
                imageInFile.read(imageData);

                // Converting Image byte array into Base64 String
                imageDataString = Base64.getEncoder().encodeToString(imageData);
            }
                System.out.println("Image Successfully Manipulated!");
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException e2) {
            System.out.println("Exception while reading the Image " + e2);
        }
        int a = ((Long)myFile.length()).intValue();
        int b = a%4096;
        if(b != 0){
            TotSeg = (int) (myFile.length()/4096) + 1;
        }
        else{
            TotSeg = (int) (myFile.length()/4096);
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.MD5 = md.digest(imageData);
        } catch (NoSuchAlgorithmException ex) {}
    }

    public int getTotSeg() {
        return TotSeg;
    }

    public byte[] getMD5() {
        return MD5;
    }
}
