/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends AbstractMultichatServer{
    
    private ServerSocket serverS;
   /* Map m;
    Map nick; */
    
    public Server(int port, InetAddress address){
        super(port, address);
        try {
            serverS = new ServerSocket(getPort(), 3, getAddress());
         /*   m = synchronizedMap(new HashMap<Socket,BufferedReader>());
            nick = synchronizedMap(new HashMap<Socket,String>());*/
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void start() throws IOException{
        Socket client = null;
        
           while(true){
                client = serverS.accept();
            	ChatThread chatt= new ChatThread(client,getMMap(),getNickMap());
                new Thread(chatt).start();
                //ex.submit(new ChatThread(client,m,nick));
            }
        
    }
    
    public void read(Socket s){
        boolean done = false;
        while(!done){
        try {
            DataInputStream streamIn = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            String line = streamIn.readUTF();
            System.out.println(line);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    public void close(Socket s){
        if (s != null)    try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
