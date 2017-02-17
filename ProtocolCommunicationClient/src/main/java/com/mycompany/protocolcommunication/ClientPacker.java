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
    private int OpCode;
    private short Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private byte[] CheckSum;
    
    public ClientPacker() {
    }

    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        byte[] cmd = ByteBuffer.allocate(Short.BYTES).putShort(new Short("1")).array();
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt(1).array();
        
        ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
        intBuffer.putInt((Integer)N_SegTot);
        byte[] TotSeg = intBuffer.array();
        
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        upload.put("Command", new String(cmd));
        upload.put("OpCode", new String(OC));
        buffer.put("N_SegTot", new String(TotSeg));
        buffer.put("nome_file", nome_file);
        buffer.put("MD5", new String((byte[]) MD5));
        /*
        int a = ((Long)myFile.length()).intValue();
        int b = a%4096;
        if(b != 0){
            TotSeg = (int) (myFile.length()/4096) + 1;
        }
        else{
            TotSeg = (int) (myFile.length()/4096);
        }
        */
        int bitSeg = Integer.bitCount((Integer)N_SegTot);
        Integer b = bitSeg + (bitSeg % 8)== 0 ? 0 : 1;
        Short LenSegTot = b.shortValue();
        System.out.println("Lunghezza Segmenti: " + bitSeg + " bit");
        Integer nome = ((String)nome_file).length();
        Short LenNome = nome.shortValue();
        System.out.println("Lunghezza Nome: " + LenNome + " byte");
        Short LenMD5 = ((Integer)((byte[])MD5).length).shortValue();
        System.out.println("Lunghezza MD5: " + LenMD5 + " byte");
        Short TotLen = (short)(LenSegTot + LenNome + LenMD5);
        upload.put("Len_Buffer", TotLen.toString());
        upload.put("Buffer", buffer);
        upload.put("CheckSum", "");
        return upload;
    }

    @Override
    public Object Send(Object N_Seg, Object buffer) {
        JSONObject send = new JSONObject();
        send.put("Command", 2);
        send.put("OpCode", N_Seg);
        send.put("Len_Buffer", ((byte[])buffer).length);
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
        this.Command = new Short((String) pack.get("Command"));
        this.OpCode = ((Long) pack.get("OpCode")).intValue();
        this.Len_Buffer = new Short((String) pack.get("Len_Buffer"));
        String buf = (String) pack.get("Buffer");
        this.Buffer = buf.getBytes();
        String check = (String) pack.get("CheckSum");
        this.CheckSum = check.getBytes();
    }

    public int getCommand() {
        return Command;
    }

    public int getOpCode() {
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
