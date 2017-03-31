/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.*;
import org.json.simple.JSONObject;

/**
 *
 * @author Edoardo
 */
public class Main {
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        //Finestra che permetterà di scegliere il file da inviare
        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(new File(userDir + "/Desktop"));
        JFrame jf = new JFrame();
        
        /*inizializzazione della socket del Client
        con l'ip a cui si vuole colllegare e la porta utilizzata*/
        Communication comm = new SocketClient("localhost", 6789);
        ClientPacker packer = new ClientPacker();
        
        //attesa della connessione con il Server
        while(!comm.Connect());
        
        //Finestra che permette di scegliere il file da inviare
        int returnVal = fc.showOpenDialog(jf);
        File selectedFile = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
        }
        
        //funzione che permette di recuperare il nome del file
        String nome_file = JOptionPane.showInputDialog(jf, "Nome del File");
        
        //Viene preparato il file da inviare e vengono calcolati il numero di segmenti totali
        SendBuilder build = new SendBuilder(selectedFile);
        
        //Viene creato il primo pacchetto (Upload) da inviare
        JSONObject packet = (JSONObject) packer.Upload(build.getTotSeg(), nome_file + "." + build.getFileExtension(selectedFile), build.getMD5());
        comm.Send(packet);
        
        //Il Client riceve il pacchetto che contiene la conferma del Server di poter iniziare ad inviare il file
        packet.clear();
        packet = (JSONObject) comm.Receive();
        packer.Unpack(packet);
        System.out.println(packer.toString());
        boolean reSend = false;
        byte[] chk = new byte[0];
        boolean packetError = false;
        //Il Client in questo momento può inviare il primo segmento del file
        while(packer.getOpCode() <= build.getTotSeg() && (packer.getCommand().equals("A")  || packer.getCommand().equals("N"))){
            /* Per ogni pacchetto ricevuto dal Server il Client controlla se lOpCode che riceve è minore del totale di segmenti da inviare
               e se il pacchetto è un Ack o un Nack
               Se il pacchetto ricevuto non rispetta una di queste 2 condizioni il Client smette di inviare i segmenti del file e chiude la connessione
               Perchè se l'OpCode è superiore al numero totale di segmenti del file significa che il file è finito, 
               nell'altro caso si tratta di un Server non compatibile */
            /* Se il ReSend è True si tratta del caso in cui il Server ha avuto problemi nel leggere il segmento o comunque per qualsiasi motivo non ha potuto salvare quel segmento
               Il ReSend quindi fa capire al Client che il successivo pacchetto da inviare è lo stesso che ha inviato precedentemente
               Se il ReSend è False il Client legge l'OpCode inviato nel Ack dal Server che corrisponde al successivo segmento da inviare  */
            if(reSend){
                packet = (JSONObject) packer.Send(packer.getNextSeg(), build.Build(packer.getNextSeg(), selectedFile));
            }
            else{
                packet = (JSONObject) packer.Send(packer.getOpCode(), build.Build(packer.getOpCode(), selectedFile));
            }
            //L'invio del pacchetto contenente il segmento del file
            comm.Send(packet);
            
            //Viene spacchettato il pacchetto ma se si tratta di un pacchetto diverso da quello compatibile il Client chiude la connessione
            try{
                packer.Unpack(comm.Receive());
            }catch(Exception ex){
                comm.Close();
            }
            
            /* Viene controllato che il Checksum calcolato dal Client dpolo spacchettamento sia uguale a quello contenuto nel campo Checksum del pacchetto inviato dal Server
              Se non è così ilClient chiude la connessione */
            if(Arrays.equals(packer.getCheckSum(), chk)){
                comm.Close();
            }
            
            /* Se il Client riceve un Unexpected Segment o un UnExcpected Command dal Server, 
               Il Client capisce che deve reinviare il pacchetto precedente
            */
            if(packer.getCommand().equals("N") && packer.getOpCode() == 2){
                reSend = true;
            }
            else if(packer.getCommand().equals("N") && packer.getOpCode() == 1){
                reSend = true;
            }
            /* Se il Client riceve un pacchetto Nacck con opCode 3 cioè End Tempt,
               Il Client capisce che il Server non riceve più il messaggio o non riesce a leggerlo,
               dato che ha finito i tentativi il Client chiude la connessione
            */
            else if(packer.getCommand().equals("N") && packer.getOpCode() == 3){
                comm.Close();
            }
            /* Se non c'è problemi nella lettura del pacchetto il Client non dovrà reinviare il pacchetto precedente 
               perchè il Server è riuscito a leggere il segmento precedente
            */
            else{
                reSend = false;
            }
            System.out.println(packer.toString());
        }
        // Dopo aver finito l'invio dei segmenti del file il Client invia un pacchetto End con OpCode 1 cioè End of File
        comm.Send(packer.End(1));
        // Appena riceve il messaggio di chiusura anche dal Server il Client chiude la connessione
        packer.Unpack((JSONObject) comm.Receive());
        System.out.println(packer.toString());
        comm.Close();
        jf.dispose();
    }
}
