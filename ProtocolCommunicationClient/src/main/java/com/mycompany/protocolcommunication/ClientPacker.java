/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ClientPacker implements Packer{
    
    private int Command;
    private int OpCode;
    private int Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private byte[] CheckSum;
    
    public ClientPacker() {
    }

    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        upload.put("Command", 1);
        upload.put("OpCode", 0);
        buffer.put("N_SegTot", N_SegTot);
        buffer.put("nome_file", nome_file);
        buffer.put("MD5", new String((byte[]) MD5));
        upload.put("Len_Buffer", buffer.size());
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
        this.Command = ((Long) pack.get("Command")).intValue();
        this.OpCode = ((Long) pack.get("OpCode")).intValue();
        this.Len_Buffer = ((Long) pack.get("Len_Buffer")).intValue();
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
