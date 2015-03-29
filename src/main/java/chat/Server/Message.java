/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthieu Blais
 */
public class Message extends Thread{
    
    private GestionServer gestion;
    private SocketClient client;
    private BufferedReader in;
    private Socket socket;
    
 
    public Message(SocketClient c, GestionServer g, boolean d)
    {
        try {
            gestion = g;
            client = c;
           socket = c.getSocket();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            if(d)
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public void run()
    {
        try {
           while (!isInterrupted()) {
               String message = in.readLine();
               if(message != null)
               gestion.getMessageFromClient(client, message);
           }
        } catch (IOException ioex) {
   
        }
    }
 
}