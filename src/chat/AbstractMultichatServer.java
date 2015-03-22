/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matthieu Blais
 */
public abstract class AbstractMultichatServer implements MultiChatServer{
    
    private int port;
    private InetAddress address;
    private Map m;
    private Map nick;
    
    public AbstractMultichatServer(int port, InetAddress address){
        this.port = port;
        this.address = address;
        m = synchronizedMap(new HashMap<>());
        nick = synchronizedMap(new HashMap<>());
    }

    public int getPort(){
        return this.port;
    }
    
    public InetAddress getAddress(){
        return this.address;
    }
    
    public Map getMMap(){
        return m;
    }
    
    public Map getNickMap(){
        return nick;
    }
    
}
