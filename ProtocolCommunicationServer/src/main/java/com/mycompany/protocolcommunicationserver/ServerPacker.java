/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ServerPacker implements Packer{
    
    private int TotSeg;
    private String nome_file;
    //MD5 array di byte
    private byte[] MD5;
    private int Command;
    private int OpCode;
    private int Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private byte[] CheckSum;
    
    public ServerPacker() {
    }

    @Override
    public void Unpack(Object packet){
        JSONObject pack = (JSONObject) packet;
        this.Command = ((Long) pack.get("Command")).intValue();
        this.OpCode = ((Long) pack.get("OpCode")).intValue();
        this.Len_Buffer = ((Long) pack.get("Len_Buffer")).intValue();
        if(Command != 1){
            String buf = (String) pack.get("Buffer");
            this.Buffer = buf.getBytes();
        }
        else
            Buffer_Unpack(pack);
        String check = (String) pack.get("CheckSum");
        this.CheckSum = check.getBytes();
    }

    private void Buffer_Unpack(JSONObject packet){
        JSONObject buffer = (JSONObject) packet.get("Buffer");
        this.TotSeg = ((Long) buffer.get("N_SegTot")).intValue();
        this.nome_file = (String) buffer.get("nome_file");
        String preMD5 = (String) buffer.get("MD5");
        this.MD5 = preMD5.getBytes();
    }
    
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        ack.put("Command", 4);
        ack.put("OpCode", (int)N_Seg);
        ack.put("Len_Buffer", 0);
        ack.put("Buffer", "");
        ack.put("CheckSum", "");
        return ack;
    }

    @Override
    public Object Nack(Object Error) {
        JSONObject nack = new JSONObject();
        nack.put("Command", 5);
        nack.put("OpCode", (int)Error);
        nack.put("Len_Buffer", 0);
        nack.put("Buffer", "");
        nack.put("CheckSum", "");
        return nack;
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
