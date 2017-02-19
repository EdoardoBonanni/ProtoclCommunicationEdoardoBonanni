/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.nio.ByteBuffer;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ClientPacker implements Packer{
    
    private int Command;
    private long OpCode;
    private int Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private byte[] CheckSum;
    
    public ClientPacker() {
    }

    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        
        short comm = 1 - Short.MAX_VALUE;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(comm).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(0 - Integer.MAX_VALUE).array();
        byte[] TotSeg = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_SegTot - Integer.MAX_VALUE)).array();
        
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        
        upload.put("Command", toBase64(cmd));
        upload.put("OpCode", toBase64(OC));
        
        buffer.put("N_SegTot", toBase64(TotSeg));
        buffer.put("nome_file", nome_file);
        buffer.put("MD5", toBase64((byte[])MD5));
        
        int bitSeg = Integer.toBinaryString(4096).length();
        Integer b;
        if(bitSeg%8 > 0)
            b = bitSeg/8 + 1;
        else
            b = bitSeg/8;
        Short LenSegTot = b.shortValue();
        
        Short LenNome = ((Integer)((String)nome_file).length()).shortValue();
        Short LenMD5 = ((Integer)((byte[])MD5).length).shortValue();
        
        Short TotLen = (short)(LenSegTot + LenNome + LenMD5);
        byte[] LenSeg = ByteBuffer.allocate(Short.BYTES).putShort((short) (TotLen - Short.MAX_VALUE)).array();
        
        upload.put("Len_Buffer", toBase64(LenSeg));
        upload.put("Buffer", buffer);
        upload.put("CheckSum", "");
        
        /*
        System.out.println("Command: " + 1 + ", Lunghezza: " + cmd.length);
        System.out.println("OpCode: " + 0 + ", Lunghezza: " + OC.length);
        System.out.println("Lunghezza Buffer: " + TotLen);
        System.out.println("Numero Segmenti totali: " + N_SegTot + ", Lunghezza: " + LenSegTot);
        System.out.println("Nome: " + nome_file + ", Lunghezza: " + LenNome);
        System.out.println("Lunghezza MD5: " + LenMD5);*/
        
        return upload;
    }

    @Override
    public Object Send(Object N_Seg, Object buffer) {
        JSONObject send = new JSONObject();
        long Seg = (long)N_Seg;
        
        short comm = 2 - Short.MAX_VALUE;
        short lBuf = (short) ((byte[])buffer).length;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(comm).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)Seg - Integer.MAX_VALUE).array();
        byte[] LenBuff = ByteBuffer.allocate(Short.BYTES).putShort((short) (lBuf - Short.MAX_VALUE)).array();
        
        send.put("Command", toBase64(cmd));
        send.put("OpCode", toBase64(OC));
        send.put("Len_Buffer", toBase64(LenBuff));
        send.put("Buffer", toBase64((byte[]) buffer));
        send.put("CheckSum", "");
        
        return send;
    }

    @Override
    public Object End(Object OpCode) {
        JSONObject end = new JSONObject();
        
        short comm = 3 - Short.MAX_VALUE;
        short lBuf = 0 - Short.MAX_VALUE;
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(comm).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)OpCode - Integer.MAX_VALUE).array();
        byte[] LenBuff = ByteBuffer.allocate(Short.BYTES).putShort(lBuf).array();
        
        end.put("Command", toBase64(cmd));
        end.put("OpCode", toBase64(OC));
        end.put("Len_Buffer", toBase64(LenBuff));
        end.put("Buffer", "");
        end.put("CheckSum", "");
        return end;
    }

    @Override
    public void Unpack(Object packet) {
        JSONObject pack = (JSONObject) packet;
        
        byte[] cmd = toBytes((String) pack.get("Command"));
        this.Command = ByteBuffer.wrap(cmd).getShort() + Short.MAX_VALUE;
        
        byte[] OC = toBytes((String) pack.get("OpCode"));
        this.OpCode = ByteBuffer.wrap(OC).getInt() + Integer.MAX_VALUE;
        
        byte[] LB = toBytes((String) pack.get("Len_Buffer"));
        this.Len_Buffer = ByteBuffer.wrap(LB).getShort() + Short.MAX_VALUE;
        
        String buf = (String) pack.get("Buffer");
        this.Buffer = toBytes(buf);
        
        String check = (String) pack.get("CheckSum");
        this.CheckSum = toBytes(check);
        
    }

    private String toBase64(byte[] obj){
        return Base64.getEncoder().encodeToString(obj);
    }
    
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
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
    
    @Override
    public String toString() {
        String cmd = "Command: " + this.Command + "\n";
        String OC = "OpCode: " + this.OpCode + "\n";
        String lenBuff = "Lunghezza Buffer: " + this.Len_Buffer + "\n";
        String buff = "Buffer: " + this.Buffer + "\n";
        String chk = "CheckSum: " + this.CheckSum + "\n";
        return cmd + OC + lenBuff + buff + chk;
    }
}
