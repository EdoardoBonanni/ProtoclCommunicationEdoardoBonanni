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
    
    /* MD5
    byte[] TotalFileMD5;
    try {
        this.TotalFileMD5 = c;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(TotalFileMD5);
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(ClientPacker.class.getName()).log(Level.SEVERE, null, ex);
    }*/
    
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
        buffer.put("MD5", MD5);
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
}
