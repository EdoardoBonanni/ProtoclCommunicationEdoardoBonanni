/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class ClientPacker implements Packer{
    
    private String Command;
    private long OpCode;
    private long Len_Buffer;
    private long nextSeg;
    //CheckSum array di byte
    private byte[] CheckSum;
    private SendBuilder build;
    
    public ClientPacker() {
    }

    @Override
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5) {
        
        byte[] TotSeg = ByteBuffer.allocate(Integer.BYTES).putInt((int) ((long)N_SegTot)).array();
        
        JSONObject upload = new JSONObject();
        JSONObject buffer = new JSONObject();
        
        upload.put("command", "U");
        byte[] cmd = "U".getBytes();
        
        upload.put("opCode", toBase64(TotSeg));
        buffer.put("fileName", nome_file);
        buffer.put("md5", toBase64((byte[])MD5));
        String buff = buffer.toString();
        byte[] buffByte = buff.getBytes();
        
        int LenNome = (int)((String)nome_file).length();
        int LenMD5 = (int)((byte[])MD5).length;
        
        long buffLen = LenNome + LenMD5;
        byte[] LenSeg = ByteBuffer.allocate(Integer.BYTES).putInt((int) (buffLen)).array();
        
        upload.put("bufferLength", toBase64(LenSeg));
        upload.put("buffer", toBase64(buffByte));
        
        byte[] pack = Main.GenerateArrayByte(cmd, TotSeg, LenSeg, buffByte);
        byte bytechk = Main.checkSum(pack);
        byte[] chk = {bytechk};
        upload.put("checksum", toBase64(chk));
        
        return upload;
    }

    @Override
    public Object Send(Object N_Seg, Object buffer) {
        JSONObject send = new JSONObject();
        long Seg = (long)N_Seg;
        
        long lBuf = ((byte[])buffer).length;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)Seg).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt((int) (lBuf)).array();
        
        send.put("command", "S");
        byte[] cmd = "S".getBytes();
        send.put("opCode", toBase64(OC));
        send.put("bufferLength", toBase64(LenBuff));
        send.put("buffer", toBase64((byte[]) buffer));
        
        byte[] pack = Main.GenerateArrayByte(cmd, OC, LenBuff, (byte[]) buffer);
        byte bytechk = Main.checkSum(pack);
        byte[] chk = {bytechk};
        send.put("checksum", toBase64(chk));
        
        return send;
    }

    @Override
    public Object End(Object OpCode) {
        JSONObject end = new JSONObject();
        
        int lBuf = 0;
        byte[] OC = ByteBuffer.allocate(Integer.BYTES).putInt((int)OpCode).array();
        byte[] LenBuff = ByteBuffer.allocate(Integer.BYTES).putInt(lBuf).array();
        
        end.put("command", "E");
        byte[] cmd = "E".getBytes();
        end.put("opCode", toBase64(OC));
        end.put("bufferLength", toBase64(LenBuff));
        end.put("buffer", "");
        byte[] buffer = new byte[0];
        byte[] pack = Main.GenerateArrayByte(cmd, OC, LenBuff, buffer);
        byte bytechk = Main.checkSum(pack);
        byte[] chk = {bytechk};
        end.put("checksum", toBase64(chk));
        return end;
    }

    @Override
    public void Unpack(Object packet) {
        JSONObject pack = (JSONObject) packet;
        
        byte[] cmd = ((String) pack.get("command")).getBytes();
        this.Command = (String) pack.get("command");
        
        byte[] OC = toBytes((String) pack.get("opCode"));
        this.OpCode = ByteBuffer.wrap(OC).getInt();
        
        byte[] LB = toBytes((String) pack.get("bufferLength"));
        this.Len_Buffer = ByteBuffer.wrap(LB).getInt();
        
        byte[] buf = toBytes((String) pack.get("buffer"));
        if(this.Command.equals("N") && this.OpCode == 2)
            this.nextSeg = ByteBuffer.wrap(buf).getLong();
        else
            this.nextSeg = 0;
        
        byte[] bytePack = Main.GenerateArrayByte(cmd, OC, LB, buf);
        byte bytechk = Main.checkSum(bytePack);
        byte[] chk = {bytechk};
        
        String check = (String) pack.get("checksum");
        byte[] chkPacket = toBytes(check);
        if(Arrays.equals(chk, chkPacket)){
            this.CheckSum = chkPacket;
        }
        else{
            this.CheckSum = new byte[0];
        }
        
    }

    private String toBase64(byte[] obj){
        return Base64.getEncoder().encodeToString(obj);
    }
    
    private byte[] toBytes(String obj){
        return Base64.getDecoder().decode(obj);
    }   
    
    public String getCommand() {
        return Command;
    }

    public long getOpCode() {
        return OpCode;
    }

    public long getLen_Buffer() {
        return Len_Buffer;
    }

    public long getNextSeg() {
        return nextSeg;
    }

    public byte[] getCheckSum() {
        return CheckSum;
    }
    
    @Override
    public String toString() {
        String cmd = "Command: " + this.Command + "\n";
        String OC = "OpCode: " + this.OpCode + "\n";
        String lenBuff = "Lunghezza Buffer: " + this.Len_Buffer + "\n";
        String buff = "NextSeg: " + this.nextSeg+ "\n";
        String chk = "CheckSum: " + this.CheckSum + "\n";
        return cmd + OC + lenBuff + buff + chk;
    }
}
