/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * La classe che implementa l'interfaccia communication ed è la classe utilizzata per la connessione socket del server
 * @author Edoardo
 */
public class SocketServer implements Communication {
    
    private ServerSocket socketS;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;
    private Socket connectionSocket;
    
    /**
     * Il costruttore della classe
     * @param port il numero di porta della socket
     * @throws IOException 
     */
    public SocketServer(int port) throws IOException{
        this.socketS = new ServerSocket(port);   
    }
    
    /**
     * Il metodo implementato dall'interfaccia communication che gestisce l'apertura della connessione da parte del Server
     * @return Il risultato dell'apertura della connessione col Server
     */
    @Override
    public boolean Connect() {
        try{
            while(true){
                    //Si apre la socket del server
                    connectionSocket = socketS.accept();
                    inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    return true;
            }
        } catch (IOException ex) { return false; }
    }

    /**
     * Il metodo implementato dall'interfaccia communication che permette l'invio di pacchetti da parte del Server
     * @param obj Il pacchetto da inviare al Client
     * @return Il risultato dell'invio del pacchetto che può essere positivo o negativo
     */
    @Override
    public boolean Send(Object obj) {
        try {
            outToClient.writeBytes(obj.toString() + '\n');
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Il metodo implementato dall'interfaccia communication che gestisce la ricezione dei pacchetti provenienti dal Client
     * @return Il pacchetto ricevuto dal Client
     */
    @Override
    public JSONObject Receive() {
        try {
            //oggetto che serve per fare il parse di un JSONObject
            JSONParser jsonParser = new JSONParser();
            //assegna il JSONObject ricevuto dal Client ad un altro oggetto JSONObject per il successivo invio
            JSONObject ClientJsonObject = (JSONObject) jsonParser.parse(inFromClient.readLine());
            return ClientJsonObject;
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    /**
     * Il metodo implementato dall'interfaccia communication che chiude la connessione con il Client
     * @return Il risultato della chiusura della connessione
     */
    @Override
    public boolean Close() {
        try {
            //chiude la connessione
            connectionSocket.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * il metodo che restituisce il valore del campo ConnectionSocket
     * @return Il valore del campo ConnectionSocket
     */
    public Socket getConnectionSocket() {
        return connectionSocket;
    }
}
