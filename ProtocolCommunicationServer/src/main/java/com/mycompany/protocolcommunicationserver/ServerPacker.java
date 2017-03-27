/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * La classe che implementa l'interfaccia Packer ed è la classe utilizzata per creare e gestire le operazioni del pacchetto del Server
 * @author Edoardo
 */
public class ServerPacker implements Packer{
    
    private long TotSeg;
    private String nome_file;
    private byte[] MD5;
    private String Command;
    private long OpCode;
    private long Len_Buffer;
    private byte[] Buffer;
    private byte[] CheckSum;
    
    public ServerPacker() {
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce lo spacchettamento del pacchetto ricevuto dal Client 
     * @param packet Il pacchetto ricevuto dal Client
     */
    @Override
    public void Unpack(Object packet){
        //Viene assegnato all'oggetto pack il pacchetto ricevuto dal Client
        JSONObject pack = (JSONObject) packet;
        
        //Viene estratto dal pacchetto il contenuto del parametro command e viene assegnato alla variabile corrispondente nella classe
        byte[] cmd = ((String) pack.get("command")).getBytes();
        this.Command = (String) pack.get("command");
        
        //Viene estratto dal pacchetto il contenuto del parametro opcode e viene assegnato alla variabile corrispondente nella classe
        byte[] OC = toBytes((String) pack.get("opCode"));
        this.OpCode = ByteBuffer.wrap(OC).getInt();
       
        //Viene estratto dal pacchetto il contenuto del parametro Len_Buffer e viene assegnato alla variabile corrispondente nella classe
        byte[] LB = toBytes((String) pack.get("bufferLength"));
        this.Len_Buffer = ByteBuffer.wrap(LB).getInt();
        
        //Viene controllato se il contenuto del pacchetto Command è un Upload o no
        if(!Command.equals("U")){
            // se non si tratta di un Upload viene inserito nel buffer del pacchetto legge i bytes del segemento del file ricevuto
            String buf = (String) pack.get("buffer");
            this.Buffer = toBytes(buf);
        }
        else{
            //se si tratta di un Upload chiama la seguente funzione
            Buffer_Unpack(pack);
        }
        //l'array di byte contente la somma di tutti i byte del pacchetto(escluso il byte del checksum)
        byte[] bytePack = this.GenerateArrayByte(cmd, OC, LB, this.Buffer);
        //Il byte che rappresenta il checksum del pacchetto
        byte bytechk = this.CreateCheckSum(bytePack);
        byte[] chk = {bytechk};
        
        //assegnamento del valore checksum al campo corrispondente
        String check = (String) pack.get("checksum");
        byte[] chkPacket = toBytes(check);
        //controllo del checksum calcolato dal Server con quello contenuto dal pacchetto ricevuto dal Client
        if(Arrays.equals(chk, chkPacket)){
            this.CheckSum = chkPacket;
        }
        else{
            this.CheckSum = new byte[0];
        }
    }

    /**
     * Funzione che permette lo spacchettamento del pacchetto con Command Upload proveniente dal Client
     * @param packet Il pacchetto ricevuto dal Client
     */
    private void Buffer_Unpack(JSONObject packet){
        //I byte del campo buffer del pacchetto
        this.Buffer = toBytes((String) packet.get("buffer"));
        
        //viene fatto il parse del buffer del pacchetto
        String s = new String(this.Buffer);
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(s);
        } catch (ParseException ex) { }
        
        //il nome del file estratto dal buffer
        this.nome_file = (String) json.get("fileName");
        
        //l'MD5 del file estrattio dal buffer
        this.MD5 = toBytes((String) json.get("md5"));
    }
    
    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce l'invio di Ack da parte del Server
     * @param N_Seg il numero del prossimo segmento del file che dovrà inviare il Client
     * @return il pacchetto da inviare al Client 
     */
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        
        //Nell'Ack la lunghezzza del buffer è 0
        int lBuf = 0;
        //i byte del opcode
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_Seg)).array();
        //i byte del LenBuff
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        //assegnazione dei parametri nei campi del pacchetto da inviare
        ack.put("command", "A");
        byte[] cmd = "A".getBytes();
        ack.put("opCode", toBase64(OC));
        ack.put("bufferLength", toBase64(LenBuff));
        byte[] buffer = new byte[0];
        ack.put("buffer", toBase64(buffer));
        byte[] pack = this.GenerateArrayByte(cmd, OC, LenBuff, buffer);
        byte bytechk = this.CreateCheckSum(pack);
        byte[] chk = {bytechk};
        ack.put("checksum", toBase64(chk));
        
        return ack;
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce l'invio di Nack da parte del Server
     * @param Err il numero dell'errore
     * @param NextSeg il numero del prossimo segmento del file che dovrà inviare il Client
     * @return 
     */
    @Override
    public Object Nack(Object Err, Object NextSeg) {
        JSONObject nack = new JSONObject();
        
        //nel caso di un unexpected segment viene inserito nel buffer il prossimo segmento del file che dovrà inviare il Client 
        byte[] buffer = "".getBytes();
        if((int)Err == 2){
            buffer = ByteBuffer.allocate(Long.BYTES).putLong((long)NextSeg).array();
        }
        int lBuf = buffer.length;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(((int)Err)).array();
        byte[] LenBuffer = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        //assegnazione dei parametri nei campi del pacchetto da inviare
        nack.put("command", "N");
        byte[] cmd = "N".getBytes();
        nack.put("opCode", toBase64(OC));
        nack.put("bufferLength", toBase64(LenBuffer));
        nack.put("buffer", toBase64(buffer));
        byte[] pack = this.GenerateArrayByte(cmd, OC, LenBuffer, buffer);
        byte bytechk = this.CreateCheckSum(pack);
        byte[] chk = {bytechk};
        nack.put("checksum", toBase64(chk));
        
        return nack;
    }

    /**
     * la funzione che permette di trasformare un array di byte in una stringa
     * @param obj l'array di byte da trasformare
     * @return la stringa dopo la conversione
     */
    private String toBase64(byte[] obj){
        return Base64.getEncoder().encodeToString(obj);
    }
    
    /**
     * la funzione che permette di trasformare una stringa in un array di byte 
     * @param obj la stringa da trasformare
     * @return l'array di byte dopo la conversione
     */
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
    } 
    
    public String getCommand() {
        return this.Command;
    }

    public long getOpCode() {
        return this.OpCode;
    }

    public long getLen_Buffer() {
        return this.Len_Buffer;
    }

    public byte[] getBuffer() {
        return this.Buffer;
    }

    public byte[] getCheckSum() {
        return this.CheckSum;
    }

    public long getTotSeg() {
        return this.TotSeg;
    }

    public String getNome_file() {
        return this.nome_file;
    }

    public byte[] getMD5() {
        return this.MD5;
    }
    
    /**
     * funzione che genera l'array di byte contenetente la somma dei byte dei campi del pacchetto(escluso il campo checksum)
     * @param cmd i byte del campo command
     * @param opCode i byte del campo opcode
     * @param LenSeg i byte del campo LenBuffer
     * @param buffByte i byte del campo buffer
     * @return l'array di byte risultante
     */
    public byte[] GenerateArrayByte(byte[] cmd, byte[] opCode, byte[] LenSeg, byte[] buffByte){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(cmd);
            outputStream.write(opCode);
            outputStream.write(LenSeg);
            outputStream.write(buffByte);
        } catch (IOException ex) { }
        return outputStream.toByteArray();
    }
    
    /**
     * La funzione che permette di generare il checksum sui byte dell'array passato come parametro
     * @param bytes l'array di byte in cui fare il checksum
     * @return il byte checksum
     */
    public byte CreateCheckSum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
           sum ^= b;
        }
        return sum;
    }

    /**
     * il metodo che permette di stampare i dati contenuti nel pacchetto
     * @return la stringa da visualizzare 
     */
    @Override
    public String toString() {
        String cmd = "Command: " + this.Command + "\n";
        String OC = "OpCode: " + this.OpCode + "\n";
        String lenBuff = "Lunghezza Buffer: " + this.Len_Buffer + "\n";
        String buff = "";
        if(!this.Command.equals("U")){
            buff = "Buffer: " + this.Buffer + "\n";
        }
        else{
            String file = "Nome file: " + this.nome_file + "\n";
            String MD5 = "MD5: " + this.MD5 + "\n";
            buff = file + MD5;
        }
        String chk = "CheckSum: " + this.CheckSum + "\n";
        return cmd + OC + lenBuff + buff + chk;
    }
    
}
