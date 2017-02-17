/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.nio.ByteBuffer;
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
    private int OpCode;
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
        //Questo nell'upload va bene perchè viene 1 bit
        System.out.println("Lunghezza Command Spacchettato: " + Integer.bitCount(((Short)this.Command).intValue()) + " bit");
        
        byte[] OC = ((String) pack.get("OpCode")).getBytes();
        this.OpCode = ByteBuffer.wrap(OC).getInt();
        //Qui non dovrebbe tornare zero bit
        System.out.println("Lunghezza OpCode Spacchettato: " + Integer.bitCount(this.OpCode) + " bit");
        
        this.Len_Buffer = new Short((String) pack.get("Len_Buffer"));
        Short c = this.Len_Buffer;
        //Questo va bene perchè viene un numero compreso tra 1 e 4 bit a seconda della lunghezza che dai al nome del file
        System.out.println("Lunghezza LenBuffer Spacchettato: " + Integer.bitCount(((Short)this.Len_Buffer).intValue()) + " bit");
        if(Command != 1){
            String buf = (String) pack.get("Buffer");
            this.Buffer = buf.getBytes();
        }
        else{
            Buffer_Unpack(pack);
        }
        String check = (String) pack.get("CheckSum");
        this.CheckSum = check.getBytes();
    }

    private void Buffer_Unpack(JSONObject packet){
        JSONObject buffer = (JSONObject) packet.get("Buffer");
        byte[] TS = ((String) buffer.get("N_SegTot")).getBytes();
        this.TotSeg = ByteBuffer.wrap(TS).getInt();
        
        this.nome_file = (String) buffer.get("nome_file");

        this.MD5 = ((String) buffer.get("MD5")).getBytes();
        
        System.out.println("Lunghezza TotSeg Spacchettato: " + Integer.bitCount(this.TotSeg) + " bit");
        System.out.println("Lunghezza nome file Spacchettato: " + this.nome_file.length() + " byte");
        System.out.println("Lunghezza MD5 Spacchettato: " + this.MD5.length + " byte");
    }
    
    @Override
    public Object Ack(Object N_Seg) {
        JSONObject ack = new JSONObject();
        ack.put("Command", new Short("4").toString());
        ack.put("OpCode", (int)N_Seg);
        ack.put("Len_Buffer", new Short("0").toString());
        ack.put("Buffer", "");
        ack.put("CheckSum", "");
        return ack;
    }

    @Override
    public Object Nack(Object Error) {
        JSONObject nack = new JSONObject();
        nack.put("Command", new Short("5").toString());
        nack.put("OpCode", (int)Error);
        nack.put("Len_Buffer", 0);
        nack.put("Buffer", "");
        nack.put("CheckSum", "");
        return nack;
    }

    public short getCommand() {
        return Command;
    }

    public int getOpCode() {
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
