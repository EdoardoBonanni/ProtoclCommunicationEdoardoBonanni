/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 * La classe che implementa l'interfaccia Packer ed è la classe utilizzata per creare e gestire le operazioni del pacchetto del Client
 * @author Edoardo
 */
public class ClientPacker implements Packer{
    
    private String Command;
    private long OpCode;
    private long Len_Buffer;
    private long nextSeg;
    //CheckSum array di byte
    private byte[] CheckSum;
    private SendBuilder build;
    
    public ClientPacker() {
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce l'invio del primo pacchetto al Server
     * @param N_SegTot il numero di segmenti totali del file da inviare
     * @param nome_file il nome del file da inviare
     * @param MD5 i byte del MD5 del file da inviare
     * @return l'oggetto JSON da inviare
     */
    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        
        //Viene creato un array di byte contenente il numero di segmenti totali del file da inviare
        byte[] TotSeg = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_SegTot)).array();
        
        //Vengono creati 2 JSONObject uno per l'invio vero e proprio del pacchetto, l'altro contenente i campi del campo buffer
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        
        //assegnazione dei parametri nei campi del pacchetto da inviare
        upload.put("command", "U");
        byte[] cmd = "U".getBytes();
        
        upload.put("opCode", toBase64(TotSeg));
        
        //il buffer nell'upload contiene il nome del file e l'MD5
        buffer.put("fileName", nome_file);
        buffer.put("md5", toBase64((byte[])MD5));
        String buff = buffer.toString();
        byte[] buffByte = buff.getBytes();
        
        int LenNome = (int)((String)nome_file).length();
        int LenMD5 = (int)((byte[])MD5).length;
        
        //si fa la somma della lunghezza del nome del file e del MD5
        long buffLen = LenNome + LenMD5;
        //si calcola la lunghezza del buffer e vienne inserita in un array di byte che verranno inseriti nel campo BufferLength
        byte[] LenSeg = ByteBuffer.allocate(Integer.BYTES).putInt((int) (buffLen)).array();
        
        upload.put("bufferLength", toBase64(LenSeg));
        upload.put("buffer", toBase64(buffByte));
        
        //Viene calcolato il checksum del pacchetto da inviare
        byte[] pack = this.GenerateArrayByte(cmd, TotSeg, LenSeg, buffByte);
        byte bytechk = this.CreateCheckSum(pack);
        byte[] chk = {bytechk};
        upload.put("checksum", toBase64(chk));
        
        return upload;
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce l'invio dei pacchetti che contengono i segmenti del file
     * @param N_Seg il numero del segmento del file da inviare
     * @param buffer i byte del segmento del file 
     * @return l'oggetto JSON da inviare
     */
    @Override
    public Object Send(Object N_Seg, Object buffer) {
        
        JSONObject send = new JSONObject();
        long Seg = (long)N_Seg;
        
        //La lunghezza del buffer da inviare
        long lBuf = ((byte[])buffer).length;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)Seg).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt((int) (lBuf)).array();
        
        //assegnazione dei parametri nei campi del pacchetto da inviare
        send.put("command", "S");
        byte[] cmd = "S".getBytes();
        send.put("opCode", toBase64(OC));
        send.put("bufferLength", toBase64(LenBuff));
        send.put("buffer", toBase64((byte[]) buffer));
        
        //Viene calcolato il checksum del pacchetto da inviare
        byte[] pack = this.GenerateArrayByte(cmd, OC, LenBuff, (byte[]) buffer);
        byte bytechk = this.CreateCheckSum(pack);
        byte[] chk = {bytechk};
        send.put("checksum", toBase64(chk));
        
        return send;
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce la richiesta di chiusura della connessione
     * @param OpCode il motivo della richiesta di chiusura della connessione
     * @return l'oggetto JSON da inviare
     */
    @Override
    public Object End(Object OpCode) {
        JSONObject end = new JSONObject();
        
        //nel pacchetto End la LenBuffer è 0 perchè il campo BUffer è vuoto
        int lBuf = 0;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)OpCode).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        //assegnazione dei parametri nei campi del pacchetto da inviare
        end.put("command", "E");
        byte[] cmd = "E".getBytes();
        end.put("opCode", toBase64(OC));
        end.put("bufferLength", toBase64(LenBuff));
        end.put("buffer", "");
        byte[] buffer = new byte[0];
        
        //Viene calcolato il checksum del pacchetto da inviare
        byte[] pack = this.GenerateArrayByte(cmd, OC, LenBuff, buffer);
        byte bytechk = this.CreateCheckSum(pack);
        byte[] chk = {bytechk};
        end.put("checksum", toBase64(chk));
        return end;
    }

    /**
     * Il metodo implementato dall'interfaccia Packer che gestisce lo spacchettamento del pacchetto ricevuto dal Server
     * @param packet  Il pacchetto ricevuto dal Server
     */
    @Override
    public void Unpack(Object packet) {
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
        
         //Viene estratto dal pacchetto il contenuto del parametro Buffer e viene assegnato alla variabile corrispondente nella classe
        byte[] buf = toBytes((String) pack.get("buffer"));
        
        //Se il Client riceve pacchetto Nack con UnExcepted Segment assegna al valore NextSeg il contenuto del Buffer ricevuto
        //Questo perchè quando si tratta di un UnExcepted Segment il Server invia nel Buffer del pacchetto Nack il successivo pacchetto che il Client dovrà inviargli
        if(this.Command.equals("N") && this.OpCode == 2)
            this.nextSeg = ByteBuffer.wrap(buf).getLong();
        else
            this.nextSeg = 0;
        
        //l'array di byte contente la somma di tutti i byte del pacchetto(escluso il byte del checksum)
        byte[] bytePack = this.GenerateArrayByte(cmd, OC, LB, buf);
        //Il byte che rappresenta il checksum del pacchetto
        byte bytechk = this.CreateCheckSum(bytePack);
        byte[] chk = {bytechk};
        
        //assegnamento del valore checksum al campo corrispondente
        String check = (String) pack.get("checksum");
        byte[] chkPacket = toBytes(check);
        //controllo del checksum calcolato dal Client con quello contenuto dal pacchetto ricevuto dal Server
        if(Arrays.equals(chk, chkPacket)){
            this.CheckSum = chkPacket;
        }
        else{
            this.CheckSum = new byte[0];
        }
        
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
    
    /**
     * il metodo che restituisce il valore del campo Command
     * @return Il valore del campo Command
     */
    public String getCommand() {
        return Command;
    }

    /**
     * il metodo che restituisce il valore del campo OpCode
     * @return Il valore del campo OpCode
     */
    public long getOpCode() {
        return OpCode;
    }

    /**
     * il metodo che restituisce il valore del campo LenBuffer
     * @return Il valore del campo LenBuffer
     */
    public long getLen_Buffer() {
        return Len_Buffer;
    }

    /**
     * il metodo che restituisce il valore del campo NextSeg
     * @return Il valore del campo NextSeg
     */
    public long getNextSeg() {
        return nextSeg;
    }

    /**
     * il metodo che restituisce il valore del campo Checksum
     * @return Il valore del campo Checksum
     */
    public byte[] getCheckSum() {
        return CheckSum;
    }
    
    /**
     * funzione che genera l'array di byte contenente la somma dei byte dei campi del pacchetto(escluso il campo checksum)
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
        String buff = "NextSeg: " + this.nextSeg+ "\n";
        String chk = "CheckSum: " + this.CheckSum + "\n";
        return cmd + OC + lenBuff + buff + chk;
    }
}
