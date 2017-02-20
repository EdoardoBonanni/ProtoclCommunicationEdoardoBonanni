/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.nio.ByteBuffer;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ServerPacker implements Packer{
    
    private long TotSeg;
    private String nome_file;
    private byte[] MD5;
    private int Command;
    private long OpCode;
    private long Len_Buffer;
    private byte[] Buffer;
    private byte[] CheckSum;
    
    public ServerPacker() {
    }

    @Override
    public void Unpack(Object packet){
        JSONObject pack = (JSONObject) packet;
        
        byte[] cmd = toBytes((String) pack.get("command"));
        this.Command = ByteBuffer.wrap(cmd).getShort() + Short.MAX_VALUE;
        
        byte[] OC = toBytes((String) pack.get("opCode"));
        this.OpCode = ByteBuffer.wrap(OC).getInt() + Integer.MAX_VALUE;
       
        byte[] LB = toBytes((String) pack.get("bufferLength"));
        this.Len_Buffer = ByteBuffer.wrap(LB).getInt() + Integer.MAX_VALUE;
        
        if(Command != 1){
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
        JSONObject buffer = (JSONObject) packet.get("buffer");
        
        this.nome_file = (String) buffer.get("fileName");
        
        this.MD5 = toBytes((String) buffer.get("md5"));
    }
    
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        
        short comm = 4 - Short.MAX_VALUE;
        int lBuf = 0 - Integer.MAX_VALUE;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(comm).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_Seg - Integer.MAX_VALUE)).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        ack.put("command", toBase64(cmd));
        ack.put("opCode", toBase64(OC));
        ack.put("bufferLength", toBase64(LenBuff));
        ack.put("buffer", "");
        ack.put("checksum", "");
        
        return ack;
    }

    @Override
    public Object Nack(Object Error) {
        JSONObject nack = new JSONObject();
        
        short comm = 5 - Short.MAX_VALUE;
        int lBuf = 0 - Integer.MAX_VALUE;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(comm).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(((int)Error) - Integer.MAX_VALUE).array();
        byte[] LenBuffer = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        nack.put("command", toBase64(cmd));
        nack.put("opCode", toBase64(OC));
        nack.put("bufferLength", toBase64(LenBuffer));
        nack.put("buffer", "");
        nack.put("checksum", "");
        
        return nack;
    }

    private String toBase64(byte[] obj){
        return Base64.getEncoder().encodeToString(obj);
    }
    
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
    } 
    
    public int getCommand() {
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
        if(this.Command != 1){
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
