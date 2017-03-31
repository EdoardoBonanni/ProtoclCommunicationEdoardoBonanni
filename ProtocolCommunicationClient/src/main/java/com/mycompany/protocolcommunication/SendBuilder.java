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
import java.util.Arrays;
import java.util.Base64;

/**
 * La classe che permette di dividere in segmenti il file da inviare al Server
 * @author Edoardo
 */
public class SendBuilder {
    private final int SegmentDimension = 2048;
    private byte[] buffer;
    private long TotSeg;
    private byte[] MD5;
    private byte[] checksum;

    public SendBuilder(){}
    
    /**
     * Il costruttore della classe che permette di estrapolare il numeo di segmenti totali del file da inviare
     * @param myFile il file da inviare
     * @throws IOException l'eccezione che si può generare alla lettura del file
     */
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
    }
    
    /**
     * La funzione che permette di trovare il numero di segmenti inviati e il succcessivo da inviare
     * @param NextSeg il numero del successivo segmento da inviare
     * @param myFile il file da inviare
     * @return il segmento successivo da inviare
     * @throws FileNotFoundException l'eccezione che si può generare se il file non viene trovato
     * @throws IOException l'eccezione che si può generare alla lettura del file
     */
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
    
    /**
     * Funzione che restituisce l'estensione del file
     * @param file il file da inviare
     * @return l'estensione del file da inviare
     */
    public String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
 
    /**
     * la funzione che permette di trasformare una stringa in un array di byte 
     * @param obj la stringa da trasformare
     * @return l'array di byte dopo la conversione
     */
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
    } 
    
    /**
     * il metodo che restituisce il numero di segmenti totali
     * @return Il numero di segmenti totali
     */
    public long getTotSeg() {
        return TotSeg;
    }

    /**
     * il metodo che restituisce il valore del campo MD5
     * @return Il valore del campo MD5
     */
    public byte[] getMD5() {
        return MD5;
    }

    /**
     * il metodo che restituisce il valore del campo Checksum
     * @return Il valore del campo Checksum
     */
    public byte[] getChecksum() {
        return checksum;
    }
    
    
}
