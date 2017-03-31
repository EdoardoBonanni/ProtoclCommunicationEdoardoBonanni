/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * La classe che implementa l'interfaccia communication ed è la classe utilizzata per la connessione socket del Client
 * @author Edoardo
 */
public class SocketClient implements Communication {

    private Socket socketC;
    private DataOutputStream outServ;
    private BufferedReader inServ;
    
    /**
     * Il costruttore della classe
     * @param ip l'ip a cui il Client si deve collegare
     * @param port il numero della porta della socket del Client
     */
    public SocketClient(String ip, int port) {
        try {
            this.socketC = new Socket(ip, port);
        } catch (IOException ex) {
            
        }
    }
    
    /**
     * Il metodo implementato dall'interfaccia communication che gestisce l'apertura della connessione da parte del Client
     * @return Il risultato dell'apertura della connessione col Client
     */
    @Override
    public boolean Connect() {
        try {
//            //Si apre la socket del Client
            outServ = new DataOutputStream(socketC.getOutputStream());
            inServ = new BufferedReader(new InputStreamReader(socketC.getInputStream()));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Il metodo implementato dall'interfaccia communication che permette l'invio di pacchetti da parte del Client
     * @param obj Il pacchetto da inviare al Server
     * @return Il risultato dell'invio del pacchetto che può essere positivo o negativo
     */
    @Override
    public boolean Send(Object obj) {
        try {
            outServ.writeBytes(obj.toString() + '\n');
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Il metodo implementato dall'interfaccia communication che gestisce la ricezione dei pacchetti provenienti dal Server
     * @return Il pacchetto ricevuto dal Server
     */
    @Override
    public JSONObject Receive() {
        try {
            //oggetto che serve per fare il parse di un JSONObject
            JSONParser jsonParser = new JSONParser();
            //assegna il JSONObject ricevuto dal Server ad un altro oggetto JSONObject per il successivo invio
            JSONObject ServerJsonObject = (JSONObject) jsonParser.parse(inServ.readLine());
            return ServerJsonObject;
        } catch (IOException | ParseException ex){
            return null;
        }
    }

    /**
     * Il metodo implementato dall'interfaccia communication che chiude la connessione con il Server
     * @return Il risultato della chiusura della connessione
     */
    @Override
    public boolean Close() {
        try {
            //chiude la connessione
            this.socketC.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
}
