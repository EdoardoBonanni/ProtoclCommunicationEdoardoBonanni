/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

/**
 *
 * @author Edoardo
 */
public interface Packer {
    
    public Object Ack(Object N_Seg);
    
    public Object Nack(Object Error, Object NextSeg);
    
    public void Unpack(Object packet);
}
