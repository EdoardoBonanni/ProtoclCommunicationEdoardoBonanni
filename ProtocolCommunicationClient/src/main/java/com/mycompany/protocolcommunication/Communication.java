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
public interface Communication {
    
    public boolean Connect();
    
    public boolean Send();
    
    public boolean Receive();
     
    public boolean Close();
}
