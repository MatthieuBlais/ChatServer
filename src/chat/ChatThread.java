/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthieu Blais
 */
public class ChatThread implements Runnable {

    private Socket socket;
    private Map<Socket, BufferedReader> map;
    private Map<Socket, String> nickMap;
    private BufferedReader in;

    public ChatThread(Socket s, Map<Socket, BufferedReader> m, Map<Socket, String> n) {
        socket = s;
        map = m;
        nickMap=n;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            map.put(socket, in);
        } catch (IOException ex) {
            Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Welcome on Chat server version 1.0");

            boolean done = false;
            while (!done) {

                // Get a set of the entries
                Set set = map.entrySet();
             
                    // Get an iterator
                    Iterator i = set.iterator();
                    // Display elements
                    while (i.hasNext()) {
                        Map.Entry me = (Map.Entry) i.next();
                        if (me.getKey() == socket) {
                            BufferedReader b = (BufferedReader) me.getValue();
                            String input = b.readLine();
                            if (input != null) {
                                nick(input);
                                System.out.println(setNick((Socket)me.getKey())+"> "+input);
                            } else {
                                done = true;
                            }
                        } else {
                            BufferedReader b = (BufferedReader) me.getValue();
                            String input = b.readLine();
                            if(input != null)
                            System.out.print(setNick((Socket)me.getKey()) +"> "+input);
                        }
                    }
                
            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void nick(String s) {
        String[] string= s.split(" ");
        if(string.length>1){
            if(string[0].equals("/nick")){
                nickMap.put(socket, string[1]);
            }
        }
    }
    
    public String setNick(Socket s){
        return nickMap.get(s); 
    }

}
