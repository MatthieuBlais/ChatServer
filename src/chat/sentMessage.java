/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

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
public class sentMessage extends Thread{
    private PrintWriter out;
    private SocketClient client;
    private String message;
    private int reveil;
    
    public sentMessage(Socket c)
    {
        try {
            reveil = 0;
          
            Socket socket = c;
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public synchronized void sendMessage(String s)
    {
        message = s;
        reveil = 1;
        notify();
         System.out.println("sendMessage");
    
    }
 
    private synchronized String getNextMessage() throws InterruptedException
    {
        while (reveil==0) wait();
           System.out.println("getNextMessage");
        reveil = 0;
        return message;
    }
 
    
    private void sendMessageToClient(String aMessage)
    {
        out.println(aMessage);
        out.flush();
    }
    
    public void run()
    {
        
        try {
           while (!isInterrupted()) {
               
               String message = getNextMessage();
               sendMessageToClient(message);
                
           }
        } catch (Exception e) {
           // Commuication problem
        }
    }
}
