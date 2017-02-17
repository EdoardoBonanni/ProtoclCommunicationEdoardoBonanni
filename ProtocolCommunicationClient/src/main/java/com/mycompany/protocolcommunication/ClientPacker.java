/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ClientPacker implements Packer{
    
    private short Command;
    private long OpCode;
    private short Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private byte[] CheckSum;
    
    public ClientPacker() {
    }

    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(new Short("1")).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(0).array();
        byte[] TotSeg = ByteBuffer.allocate(Long.BYTES).putLong((Long)N_SegTot).array();
        
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        upload.put("Command", new String(cmd));
        upload.put("OpCode", new String(OC));
        
        buffer.put("N_SegTot", new String(TotSeg));
        buffer.put("nome_file", nome_file);
        buffer.put("MD5", new String((byte[]) MD5));
        
        int bitSeg = Long.bitCount((Long)N_SegTot);
        Integer b = bitSeg + (bitSeg % 8)== 0 ? 0 : 1;
        Short LenSegTot = b.shortValue();
        
        Short LenNome = ((Integer)((String)nome_file).length()).shortValue();
        Short LenMD5 = ((Integer)((byte[])MD5).length).shortValue();
        
        Short TotLen = (short)(LenSegTot + LenNome + LenMD5);
        byte[] LenSeg = ByteBuffer.allocate(Short.BYTES).putShort(TotLen).array();
        
        upload.put("Len_Buffer", new String(LenSeg));
        upload.put("Buffer", buffer);
        upload.put("CheckSum", "");
        return upload;
    }

    @Override
    public Object Send(Object N_Seg, Object buffer) {
        JSONObject send = new JSONObject();
        long Seg = (long)N_Seg;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(new Short("2")).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)Seg - Integer.MAX_VALUE).array();
        byte[] LenBuff = ByteBuffer.allocate(Short.BYTES).putShort((short)((String)buffer).length()).array();
        
        send.put("Command", new String(cmd));
        send.put("OpCode", new String(OC));
        send.put("Len_Buffer", new String(LenBuff));
        send.put("Buffer", buffer);
        send.put("CheckSum", "");
        return send;
    }

    @Override
    public Object End(Object OpCode) {
        JSONObject end = new JSONObject();
        end.put("Command", 3);
        end.put("OpCode", OpCode);
        end.put("Len_Buffer", 0);
        end.put("Buffer", "");
        end.put("CheckSum", "");
        return end;
    }

    @Override
    public void Unpack(Object packet) {
        JSONObject pack = (JSONObject) packet;
        
        byte[] cmd = ((String) pack.get("Command")).getBytes();
        this.Command = ByteBuffer.wrap(cmd).getShort();
        
        byte[] OC = ((String) pack.get("OpCode")).getBytes();
        this.OpCode = ByteBuffer.wrap(OC).getInt();
        
        byte[] LB = ((String) pack.get("Len_Buffer")).getBytes();
        this.Len_Buffer = ByteBuffer.wrap(LB).getShort();
        
        String buf = (String) pack.get("Buffer");
        this.Buffer = buf.getBytes();
        
        String check = (String) pack.get("CheckSum");
        this.CheckSum = check.getBytes();
        
        /*
        System.out.println("Lunghezza Command Spacchettato: " + cmd.length + " byte");
        System.out.println("Lunghezza OpCode Spacchettato: " + OC.length + " byte");
        System.out.println("Lunghezza LenBuffer Spacchettato: " + LB.length + " byte");
        */
    }

    public int getCommand() {
        return Command;
    }

    public long getOpCode() {
        return OpCode;
    }

    public int getLen_Buffer() {
        return Len_Buffer;
    }

    public byte[] getBuffer() {
        return Buffer;
    }

    public byte[] getCheckSum() {
        return CheckSum;
    }
}
