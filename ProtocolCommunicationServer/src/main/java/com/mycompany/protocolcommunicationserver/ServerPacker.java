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
    
    private int TotSeg;
    private String nome_file;
    private byte[] MD5;
    private short Command;
    private long OpCode;
    private short Len_Buffer;
    private byte[] Buffer;
    private byte[] CheckSum;
    
    public ServerPacker() {
    }

    @Override
    public void Unpack(Object packet){
        JSONObject pack = (JSONObject) packet;
        
        byte[] cmd = ((String) pack.get("Command")).getBytes();
        this.Command = ByteBuffer.wrap(cmd).getShort();
        
        byte[] OC = ((String) pack.get("OpCode")).getBytes();
        this.OpCode = ByteBuffer.wrap(OC).getInt() + Integer.MAX_VALUE;
       
        byte[] LB = ((String) pack.get("Len_Buffer")).getBytes();
        this.Len_Buffer = ByteBuffer.wrap(LB).getShort();
        
        if(Command != 1){
            String buf = (String) pack.get("Buffer");
            this.Buffer = Base64.getDecoder().decode(buf);
        }
        else{
            Buffer_Unpack(pack);
        }
        String check = (String) pack.get("CheckSum");
        this.CheckSum = check.getBytes();
        
        /*
        System.out.println("Lunghezza Command Spacchettato: " + cmd.length + " byte");
        System.out.println("Lunghezza OpCode Spacchettato: " + OC.length + " byte");
        System.out.println("Lunghezza LenBuffer Spacchettato: " + LB.length + " byte");
        */
    }

    private void Buffer_Unpack(JSONObject packet){
        JSONObject buffer = (JSONObject) packet.get("Buffer");
        byte[] TS = ((String) buffer.get("N_SegTot")).getBytes();
        this.TotSeg = ByteBuffer.wrap(TS).getInt();
        
        this.nome_file = (String) buffer.get("nome_file");
        
        this.MD5 = ((String) buffer.get("MD5")).getBytes();
        /*
        System.out.println("Lunghezza TotSeg Spacchettato: " + TS.length + " byte");
        System.out.println("Lunghezza nome file Spacchettato: " + this.nome_file.length() + " byte");
        System.out.println("Lunghezza MD5 Spacchettato: " + this.MD5.length + " byte");
        */
    }
    
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(new Short("4")).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(1).array();
        byte[] LenBuffer = ByteBuffer.allocate(Short.BYTES).array();
        
        ack.put("Command", new String(cmd));
        ack.put("OpCode", new String(OC));
        ack.put("Len_Buffer", new String(LenBuffer));
        ack.put("Buffer", "");
        ack.put("CheckSum", "");
        return ack;
    }

    @Override
    public Object Nack(Object Error) {
       JSONObject nack = new JSONObject();
        
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(new Short("5")).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)Error).array();
        byte[] LenBuffer = ByteBuffer.allocate(Short.BYTES).array();
        
        nack.put("Command", new String(cmd));
        nack.put("OpCode", new String(OC));
        nack.put("Len_Buffer", new String(LenBuffer));
        nack.put("Buffer", "");
        nack.put("CheckSum", "");
        return nack;
    }

    public short getCommand() {
        return Command;
    }

    public long getOpCode() {
        return OpCode;
    }

    public short getLen_Buffer() {
        return Len_Buffer;
    }

    public byte[] getBuffer() {
        return Buffer;
    }

    public byte[] getCheckSum() {
        return CheckSum;
    }

    public int getTotSeg() {
        return TotSeg;
    }

    public String getNome_file() {
        return nome_file;
    }

    public byte[] getMD5() {
        return MD5;
    }
    
}
