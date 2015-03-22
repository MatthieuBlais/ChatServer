/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.net.Socket;

/**
 *
 * @author Matthieu Blais
 */
public class SocketClient {
    private Socket client = null;
    private String name;
    private Message message = null;
    private sentMessage receive = null;
    
    public SocketClient(Socket s, GestionServer g){
        client = s;
        name = client.getInetAddress().getHostAddress();
        receive = new sentMessage(s);
        message = new Message(this,g);
    }
 /*   public ClientListener mClientListener = null;
    public ClientSender mClientSender = null;*/

    public Socket getSocket(){
        return client;
    }
    
    public void setName(String n){
        name = n;
    }
    
    public String getName(){
        return name;
    }
    
    public Message getMessage(){
        return message;
    }
    
    public sentMessage getReceive(){
        return receive;
    }
    
    public void startMessage(){
        message.start();
    }
    
    public void startReceive(){
        receive.start();
    }
    
    public void sendMessage(String s){
        receive.sendMessage(s);
    }
}
