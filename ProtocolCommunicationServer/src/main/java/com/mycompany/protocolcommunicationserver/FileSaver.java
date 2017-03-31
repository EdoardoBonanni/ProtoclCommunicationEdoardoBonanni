package com.mycompany.protocolcommunicationserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * La classe che permette di salvare il file ricevuto dal Client
 * @author Edoardo
 */
public class FileSaver {

    private final int SegmentDimension = 2048;
    private String path;
    private FileOutputStream fos; 
    
    /**
     * Il costrutttore della classe dove viene specificato il path dove salvare il file
     * @param nome_file il nome con cui salvare il file
     * @throws FileNotFoundException 
     */
    public FileSaver(String nome_file) throws FileNotFoundException {
        this.path = System.getProperty("user.home") + "/Desktop/" + nome_file;
        fos = new FileOutputStream(path, true);
    }

    /**
     * Funzione che permette la scrittura di un segmento nel fileoutputstream
     * @param segment l'array di byte del buffer di un segmento
     * @throws IOException 
     */
    public void toFile(byte[] segment) throws IOException{
        fos.write(segment);
    }
    
    /**
     * Funzione che permette la chiusura del filoutputstream
     * @throws IOException 
     */
    public void close() throws IOException{
        fos.close();
    }
    
    /**
     * Funzione che calcola il segmento che deve ricevere dal Client in base a quelli che ha già
     * @return il prossimo segmento del file da ricevere dal Client
     * @throws IOException 
     */
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
