/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.protocolcommunicationserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Edoardo
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        //calcolo dello spazio libero e dello spazio totale nell'ambiente di salvataggio
        File f = new File("C:\\");
        System.out.println("Total space: " + f.getTotalSpace() +" bytes");
        System.out.println("Free space: " + f.getFreeSpace() +" bytes");
        
        //inizializzazione della socket del server
        SocketServer comm = new SocketServer(6789);
        
        //attesa della connessione con il client
        comm.Connect();
        System.out.println("Connected with " + comm.getConnectionSocket().getInetAddress());
        
        //inizializzazione del pacchetto vuoto del server
        ServerPacker packer = new ServerPacker();
        
        //ricezione del primo pacchetto da parte del server che si aspetta sia un upload
        try{
            //viene effettuato lo spacchettamento del pacchetto ricevuto
            packer.Unpack(comm.Receive());
        }catch(Exception ex){
            /* il server riconosce che è un pacchetto non valido 
            e invia al client un nack con opcode 10 cioè invalid packet
            e nextseg 0 perchè ancora il client non ha inviato pacchetti del file */
            comm.Send(packer.Nack((long) 10, (long) 0));
        }
        //controllo del checksum contenuto nel pacchetto con quello calcolato dal server dopo lo spacchettamento
        byte[] chk = new byte[0];
        if(Arrays.equals(packer.getCheckSum(), chk)){
            /* il checksum non corrisponde e viene inviato un nack al client con opcode 6 cioè checksum error
            e nextseg 0 perchè ancora il client non ha inviato pacchetti del file 
            e viene chiusa la connessione */
            comm.Send(packer.Nack((long) 6, (long) 0));
            comm.Close();
        }
        System.out.println(packer.toString());
        //Viene controllato se la grandezza del file è maggiore nello spazio libero nell'ambiente di salvataggio
        int totseg = ((Long) packer.getOpCode()).intValue();
        if((totseg * 2048) > f.getFreeSpace()){
            /* la grandezza del file è superiore allo spazio libero nell'ambiente di salvataggio e 
            viene inviato un nack al client con opcode 8 cioè no space
            e nextseg 0 perchè ancora il client non ha inviato pacchetti del file 
            e viene chiusa la connessione */
            comm.Send(packer.Nack((long) 8, (long) 0));
            comm.Close();
        }
        if(((byte[]) packer.getMD5()).length != 16){
            /* la grandezza del campo MD5 è diversa da 16 byte quindi risulta errato e 
            viene inviato un nack al client con opcode 9 cioè MD5 error
            e nextseg 0 perchè ancora il client non ha inviato pacchetti del file 
            e viene chiusa la connessione */
            comm.Send(packer.Nack((long) 9, (long) 0));
            comm.Close();
        }
        
        //viene inizializzato un oggetto filesaver che permette di salvare il file con il nome specificato
        FileSaver fs = new FileSaver(packer.getNome_file());
        
        /* se tutti i passaggi precedenti sono andati a buon fine il server invia un ack
        che abilita il client a inviare i pacchetti contenenti il file */
        comm.Send(packer.Ack(fs.getNextSeg()));
        int maxRetry = 0;
        //variabile di prova
        //int prova =0; 
        boolean packetError = false;
        while(true){
            packetError = false;
            //per ogni pacchetto inviato dal client, il server controlla il suo contenuto
            try{
                //viene effettuato lo spacchettamento del pacchetto ricevuto
                packer.Unpack(comm.Receive());
            }catch(Exception ex){
                //il flag packeterror viene modificato a true e sta a significare che il pacchetto ricevuto non è valido 
                packetError = true;
            }
            /* se il pacchetto ricevuto non è valido 
            il server invia al client un nack con opcode 10 cioè invalid packet
            e nextseg calcolato in base al numero di pacchetti ricevuti dal server */
            if(packetError == true){
                comm.Send(packer.Nack((long) 10, fs.getNextSeg()));
            }
            else{
                //controllo del checksum contenuto nel pacchetto con quello calcolato dal server dopo lo spacchettamento
                if(Arrays.equals(packer.getCheckSum(), chk)){
                    /* il checksum non corrisponde e viene inviato un nack al client con opcode 6 cioè checksum error
                    e nextseg 0 perchè con questo errore verrà chiusa la connessione
                    e viene chiusa la connessione */
                    comm.Send(packer.Nack((long) 6, (long) 0));
                    comm.Close();
                }
                //se non ci sono stati errori viene stampato il pacchetto ricevuto
                System.out.println(packer.toString());
                /* se il comando ricevuto è un end viene inviato un ack con opcode 0 
                in attesa della chiusura della connessione */
                if(packer.getCommand().equals("E")){
                    comm.Send(packer.Ack((long)0));
                    break;
                }
                /* se il comando ricevuto è un upload viene inviato un nack con opcode 1 cioè unxpected command
                e viene aumentato il maxretry */
                else if(packer.getCommand().equals("U")){
                    comm.Send(packer.Nack((long) 1, fs.getNextSeg()));
                    maxRetry++;
                    /* se il max retry raggiunge 3 viene inviato un nack con opcode 3 cioè end tempt
                    e viene chiusa la connessione */
                    if(maxRetry == 3){
                        comm.Send(packer.Nack((long) 3,(long) 0));
                        comm.Close();
                    }
                }
                /* se l'opcode ha valore maggiore dei numeri di segmenti totali del file 
                viene inviato un nack con opcode 4 cioè wrong opcode 
                e viene aumentato il maxretry */
                else if(packer.getOpCode() > totseg){
                    comm.Send(packer.Nack((long) 4, fs.getNextSeg()));
                    maxRetry++;
                    /* se il max retry raggiunge 3 viene inviato un nack con opcode 3 cioè end tempt
                    e viene chiusa la connessione */
                    if(maxRetry == 3){
                        comm.Send(packer.Nack((long) 3,(long) 0));
                        comm.Close();
                    }
                }
                /* se l'opcode ha valore maggiore del segmento successivo che si aspetta il server
                viene inviato un nack con opcode 2 cioè unexpeted segment 
                e viene aumentato il maxretry */
                else if(packer.getOpCode() > fs.getNextSeg()){
                    comm.Send(packer.Nack(2, fs.getNextSeg()));
                    maxRetry++;
                    /* se il max retry raggiunge 3 viene inviato un nack con opcode 3 cioè end tempt
                    e viene chiusa la connessione */
                    if(maxRetry == 3){
                        comm.Send(packer.Nack((long) 3,(long) 0));
                        comm.Close();
                    }
                }
                /* se non c'è nessun errore viene aggiunto il segmento al fileoutputsream del server
                viene azzerato il maxretry 
                e viene inviato un ack che ha come opcode il segmento successivo che si aspetta il server */
                else{
                    maxRetry = 0;
                    fs.toFile(packer.getBuffer());

                    /* prova per testare se il programma riesce a gestire 
                    un segmento successivo a quello che si aspetterebbe il server */
                    /*if(prova == 3){
                        comm.Send(packer.Ack((long)10));
                        prova++;
                    }
                    else{*/
                    fs.getNextSeg();
                    comm.Send(packer.Ack(fs.getNextSeg()));
                    /*}
                    prova++;*/
                }
            }
        }
        comm.Close();
        
    }
    
}
