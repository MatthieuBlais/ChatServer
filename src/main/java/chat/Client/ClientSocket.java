/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Client;

import chat.Internationalization;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Matthieu Blais
 */
public class ClientSocket {

    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;
    private Sender sender;
    private boolean debug;
    
    public ClientSocket(int port, InetAddress a, Button btn, TextField message, TextArea area, TextArea buddy, boolean deb) {
        debug = deb;
        out = null;
        try {
            // Connect to Nakov Chat Server
           socket = new Socket(a, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ioe) {
            Alert al = new Alert(AlertType.ERROR);
          al.setTitle(Internationalization.get("my.error"));
          al.setHeaderText(Internationalization.get("my.error2"));
          al.setContentText(ioe.getMessage());

          al.showAndWait();
            System.exit(-1);
        }
        
        
        // Create and start Sender thread
        sender = new Sender(out, btn, message);
        sender.setDaemon(true);
        sender.start();

        Listener lis = new Listener(in,btn,area,buddy, this);
        lis.setDaemon(true);
        lis.start();

    }
    
    public void newJoin(){
        sender.newJoin();
    }
    
    public void setExit(){
        try {
            sender.setExit();
                    
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
          Alert a = new Alert(AlertType.ERROR);
          a.setTitle(Internationalization.get("my.error"));
          a.setHeaderText(Internationalization.get("my.error2"));
          a.setContentText(ex.getMessage());

          a.showAndWait();
          if(debug)
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
