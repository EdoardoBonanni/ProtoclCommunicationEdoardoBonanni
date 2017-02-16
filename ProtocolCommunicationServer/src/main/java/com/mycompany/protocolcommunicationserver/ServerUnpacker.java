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
public class ServerUnpacker implements Unpacker{
    
    private int Command;
    private int OpCode;
    private int Len_Buffer;
    private byte[] Buffer;
    //CheckSum array di byte
    private String CheckSum;
    
    public ServerUnpacker() {
    }

    public void Unpack(JSONObject packet){
        int cmd = ((Long) packet.get("Command")).intValue();
        switch(cmd){
            case 1:
                Upload(packet);
                break;
            case 2:
                Send(packet);
                break;
            case 3:
                End(packet);
                break;
            default:
                break;
        }
    }

    @Override
    public void Upload(Object packet) {
        JSONObject pack = (JSONObject) packet;
        this.Command = 1;
        this.OpCode = ((Long) pack.get("OpCode")).intValue();
        this.Len_Buffer = ((Long) pack.get("Len_Buffer")).intValue();
        this.Buffer = (byte[]) pack.get("Buffer");
        this.CheckSum = (String) pack.get("CheckSum");
    }

    @Override
    public void Send(Object packet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void End(Object packet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public String getCheckSum() {
        return CheckSum;
    }
    
}
