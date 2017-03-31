/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

/**
 * L'interfaccia utilizzata per la comunicazione
 * @author Edoardo
 */
public interface Communication {
    
    boolean Connect();
    
    boolean Send(Object obj);
    
    Object Receive();
     
    boolean Close();
}
