/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 *
 * @author Edoardo
 */
public class SendBuilder {
    private final int SegmentDimension = 4096;
    private HashMap<Long, byte[]> buffer = new HashMap<>();
    private long TotSeg;
    private byte[] MD5;

    public SendBuilder(File myFile) throws IOException {
        FileInputStream fis = new FileInputStream(myFile);
        byte[] buff = new byte[SegmentDimension];
        
        long i = 1;
        while(fis.read(buff) != -1){
            buffer.put(i, buff);
            i++;
        }
        
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
            this.MD5 = new byte[16];//md.digest(imageData);
        } catch (NoSuchAlgorithmException ex) {}
        //buffer.forEach((k,v)-> System.out.println(k + ", " + v));
    }
    
    public byte[] Build(long NextSeg){
        return buffer.get(NextSeg);
    }
    
    public long getTotSeg() {
        return TotSeg;
    }

    public byte[] getMD5() {
        return MD5;
    }
}
