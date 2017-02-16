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
public interface Unpacker {
    
    public void Upload(Object packet);
    
    public void Send(Object packet);
    
    public void End(Object packet);
}
