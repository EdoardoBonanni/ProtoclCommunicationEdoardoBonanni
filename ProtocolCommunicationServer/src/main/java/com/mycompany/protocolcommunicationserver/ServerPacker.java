/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
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

    @Override
    public void Unpack(Object packet){
        JSONObject pack = (JSONObject) packet;
        
        //byte[] cmd = toBytes((String) pack.get("command"));
        this.Command = (String) pack.get("command");
        
        byte[] OC = toBytes((String) pack.get("opCode"));
        this.OpCode = ByteBuffer.wrap(OC).getInt();
       
        byte[] LB = toBytes((String) pack.get("bufferLength"));
        this.Len_Buffer = ByteBuffer.wrap(LB).getInt();
        
        if(!Command.equals("U")){
            String buf = (String) pack.get("buffer");
            this.Buffer = toBytes(buf);
        }
        else{
            Buffer_Unpack(pack);
        }
        
        String check = (String) pack.get("checksum");
        this.CheckSum = toBytes(check);

    }

    private void Buffer_Unpack(JSONObject packet){
        byte[] buffer = toBytes((String) packet.get("buffer"));
        
        String s = new String(buffer);
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(s);
        } catch (ParseException ex) { }
        
        this.nome_file = (String) json.get("fileName");
        
        this.MD5 = toBytes((String) json.get("md5"));
    }
    
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        
        int lBuf = 0;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_Seg)).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        ack.put("command", "A");
        byte[] cmd = "A".getBytes();
        ack.put("opCode", toBase64(OC));
        ack.put("bufferLength", toBase64(LenBuff));
        byte[] buffer = new byte[0];
        ack.put("buffer", toBase64(buffer));
        byte[] pack = 
                Main.GenerateArrayByte(cmd, OC, LenBuff, buffer);
        byte bytechk = Main.checkSum(pack);
        byte[] chk = {bytechk};
        ack.put("checksum", toBase64(chk));
        
        return ack;
    }

    @Override
    public Object Nack(Object Err, Object NextSeg) {
        JSONObject nack = new JSONObject();
        
        byte[] buffer = "".getBytes();
        if((int)Err == 2){
            buffer = ByteBuffer.allocate(Long.BYTES).putLong((long)NextSeg).array();
        }
        int lBuf = buffer.length ;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(((int)Err)).array();
        byte[] LenBuffer = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        nack.put("command", "N");
        byte[] cmd = "N".getBytes();
        nack.put("opCode", toBase64(OC));
        nack.put("bufferLength", toBase64(LenBuffer));
        nack.put("buffer", toBase64(buffer));
        byte[] pack = Main.GenerateArrayByte(cmd, OC, LenBuffer, buffer);
        byte bytechk = Main.checkSum(pack);
        byte[] chk = {bytechk};
        nack.put("checksum", toBase64(chk));
        
        return nack;
    }

    private String toBase64(byte[] obj){
        return Base64.getEncoder().encodeToString(obj);
    }
    
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
