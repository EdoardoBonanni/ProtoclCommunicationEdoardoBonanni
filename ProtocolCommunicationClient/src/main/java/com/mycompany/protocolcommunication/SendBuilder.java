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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class SendBuilder {
    private final int SegmentDimension = 2048;
    private byte[] buffer;
    private long TotSeg;
    private byte[] MD5;
    private byte[] checksum;

    public SendBuilder(){}
    
    public SendBuilder(File myFile) throws IOException {
        FileInputStream fis = new FileInputStream(myFile);
        byte[] buff = new byte[SegmentDimension];
        fis.read(buff);
        long a = (Long)myFile.length();
        long b = a%SegmentDimension;
        if(b != 0){
            TotSeg = myFile.length()/SegmentDimension + 1;
        }
        else{
            TotSeg = myFile.length()/SegmentDimension;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.MD5 = new byte[16];
            this.MD5 = (byte[]) md.digest(buff);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Errore MD5");
        }
        //buffer.forEach((k,v)-> System.out.println(k + ", " + v));
    }
    
    public byte[] Build(long NextSeg, File myFile) throws FileNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(myFile);
        byte[] buff = new byte[SegmentDimension];
        long i = 1;
        while(fis.read(buff) != -1){
            if(i == NextSeg){
                if(NextSeg == TotSeg){
                    byte[] last = Arrays.copyOf(buff, (int)(myFile.length()%SegmentDimension));
                    return last;
                }
                buffer = buff;
                break;
            }
            i++;
        }
        return buffer;
    }
    
    public String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
 
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
    } 
    
    public long getTotSeg() {
        return TotSeg;
    }

    public byte[] getMD5() {
        return MD5;
    }

    public byte[] getChecksum() {
        return checksum;
    }
    
    
}
