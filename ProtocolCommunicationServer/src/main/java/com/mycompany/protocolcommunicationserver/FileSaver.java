package com.mycompany.protocolcommunicationserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucag
 */
public class FileSaver {

    private String path;
    private HashMap<Long, byte[]> buffer; 
    
    public FileSaver(String nome_file) {
        this.path = "C:\\Users\\lucag\\Desktop\\" + nome_file + ".jpg";
        this.buffer = new HashMap<>();
    }
    
    public void toBuffer(byte[] segment){
        buffer.put((long) buffer.size() + 1, segment);
        /*
        */
    }

    public void toFile(){
        try {
            FileOutputStream fos = new FileOutputStream(path, true);
            buffer.forEach((k,v)-> {
                try {
                    fos.write(v);
                } catch (IOException ex) {
                }
            });
            fos.close();
        } catch (IOException ex) {
            
        }
    }
    
    public long getNextSeg() {
        return buffer.size() + 1;
    }
}
