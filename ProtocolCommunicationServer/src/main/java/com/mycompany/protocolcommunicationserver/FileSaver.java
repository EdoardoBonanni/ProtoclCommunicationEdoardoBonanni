package com.mycompany.protocolcommunicationserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author lucag
 */
public class FileSaver {

    private final int SegmentDimension = 2048;
    private String path;
    private FileOutputStream fos; 
    
    public FileSaver(String nome_file) throws FileNotFoundException {
        this.path = System.getProperty("user.home") + "/Desktop/" + nome_file + ".jpg";
        fos = new FileOutputStream(path, true);
    }

    public void toFile(byte[] segment) throws IOException{
        fos.write(segment);
    }
    
    public void close() throws IOException{
        fos.close();
    }
    
    public long getNextSeg() throws IOException {
        FileInputStream fin = new FileInputStream(new File(path));
        long i = 1;
        byte[] b = new byte[SegmentDimension];
        while(fin.read(b) != -1){
            i++;
        }
        return i;
    }
}
