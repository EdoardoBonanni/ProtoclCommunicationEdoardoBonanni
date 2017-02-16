/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

/**
 *
 * @author Edoardo
 */
public interface Packer {
    
    public Object Upload(Object N_SegTot, Object nome_file, Object MD5);
    
    public Object Send(Object N_Seg, Object buffer);
    
    public Object End(Object OpCode);
    
    public void Unpack(Object packet);
}
