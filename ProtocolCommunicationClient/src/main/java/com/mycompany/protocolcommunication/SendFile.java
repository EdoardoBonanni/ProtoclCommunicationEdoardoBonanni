/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Edoardo
 */
public class SendFile {
    private long TotSeg;
    private byte[] MD5;

    public SendFile() {
    }
    
    public void CheckFile(File myFile){
        long a = (Long)myFile.length();
        long b = a%4096;
        if(b != 0){
            TotSeg = myFile.length()/4096 + 1;
        }
        else{
            TotSeg = myFile.length()/4096;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.MD5 = new byte[1];//md.digest(imageData);
        } catch (NoSuchAlgorithmException ex) {}
    }
    
    public long getTotSeg() {
        return TotSeg;
    }

    public byte[] getMD5() {
        return MD5;
    }
}
