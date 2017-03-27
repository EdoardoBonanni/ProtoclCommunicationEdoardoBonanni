/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

/**
 * L'interfaccia utilizzata per la comunicazione
 * @author Edoardo
 */
public interface Communication {
    
    public boolean Connect();
    
    public boolean Send(Object obj);
    
    public Object Receive();
     
    public boolean Close();
}
