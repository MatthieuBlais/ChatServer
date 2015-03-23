/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

/**
 *
 * @author Matthieu Blais
 */
public class SocketServer extends AbstractMultichatServer {

    private ServerSocket serverS;
    private GestionServer gestion;
    
    
    public SocketServer(int port, InetAddress address) {
        super(port, address);
        try {
            serverS = new ServerSocket(getPort(), 100, getAddress());
            System.out.println("Server is starting...");
            gestion = new GestionServer();
         /*   m = synchronizedMap(new HashMap<Socket,BufferedReader>());
            nick = synchronizedMap(new HashMap<Socket,String>());*/
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start() throws IOException {
       while (true) {
           try {
               Socket socket = serverS.accept();
               SocketClient newClient = new SocketClient(socket,gestion);
               gestion.addClient(newClient);
               newClient.startMessage();
               newClient.startReceive();
     //          System.out.println("New client");
           } catch (IOException ioe) {
               ioe.printStackTrace();
           }
        }    
    }
    
    public void close(){
        if (serverS == null)    try {
            serverS.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
