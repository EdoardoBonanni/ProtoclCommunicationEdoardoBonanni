/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

/**
 *
 * @author Edoardo
 */
public class SendBuilder {
    private final int SegmentDimension = 4096;
    private HashMap<Long, byte[]> buffer = new HashMap<>();

    public SendBuilder(File myFile) throws IOException {
        FileInputStream fis = new FileInputStream(myFile);
        byte[] a= new byte[SegmentDimension];
        long i = 1;
        while(fis.read(a) != -1){
            buffer.put(i, a);
            i++;
        }
        //buffer.forEach((k,v)-> System.out.println(k + ", " + v));
    }
    
    public String Build(long NextSeg){
        byte[] bfr = buffer.get(NextSeg);
        String bs64 = Base64.getEncoder().encodeToString(bfr);
        return bs64;
    }
}
