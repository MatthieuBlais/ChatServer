/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Server;

import chat.Internationalization;
import java.io.BufferedReader;
import java.net.Socket;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

/**
 *
 * @author Matthieu Blais
 */
public class GestionServer{

    private Map<Integer, String> messageMap;
    private Map<SocketClient, String> nickMap;
    private Map<SocketClient, String> idMap;
    private int compteur;
    
    
    public GestionServer() {
        compteur = 0;
        messageMap = synchronizedMap(new HashMap<Integer, String>());
        nickMap = synchronizedMap(new HashMap<SocketClient, String>());
        
    }

    public void addClient(SocketClient c) {
        nickMap.put(c, Internationalization.get("my.guest")/*c.getSocket().getInetAddress().getHostAddress() + ":" + String.valueOf(compteur)*/);
        c.setName(Internationalization.get("my.guest")/*c.getSocket().getInetAddress().getHostAddress() + ":" + String.valueOf(compteur)*/);
        c.sendMessage(Internationalization.get("my.welcome"));
        buddy();
        compteur++;
    }

    public void deleteClient(SocketClient c) {
        nickMap.remove(c, c.getName());
        buddy();
    }

    public void getMessageFromClient(SocketClient c, String s) {
        String message = null;
        String[] tmp = s.split("~#%#~");
        if(tmp.length==2){
        if (!nick(tmp[1], c)) {
            
                message = tmp[0]+ "~~&~&" + setNick(c) + "> " + tmp[1];
 //           System.out.println("message : "+ message);
            for (Entry<SocketClient, String> entry : nickMap.entrySet()) {
                    
                SocketClient key = entry.getKey();
               
      //           System.out.println("envoie");
                key.sendMessage(message);
                
    // do what you have to do here
                // In your case, an other loop.
            }    
        }
        else{

            message = tmp[0]+ "~~&~&" + setNick(c) + "##" + Internationalization.get("my.newName") + " : "+ setNick(c);
            System.out.println("message");

            for (Entry<SocketClient, String> entry : nickMap.entrySet()) {
                    
                SocketClient key = entry.getKey();
               
    //             System.out.println("envoie");
                 key.sendMessage(message);
                
    // do what you have to do here
                // In your case, an other loop.
        }
        }
        }
        else{
            tmp = s.split("~#~#~");
            if(tmp.length == 2){
            if(tmp[1].equals("/myID/")){
      //          System.out.println("POPOPOPOPOPOPOPO");
                message = tmp[0]+ "~~&~&" + setNick(c) +"@~~@~~ aze";
            } else if(tmp[1].equals("/Iconnect/")){

            //    System.out.println("BALALALAsdfsdfsd");
                message = tmp[0]+ "~~&~&" + setNick(c) +"&~=====>" + setNick(c) +" " + Internationalization.get("my.hasjoin");

            } else if(tmp[1].equals("/Ileave/")){
                message = tmp[0]+ "~~&~&" + setNick(c) +"_____" + setNick(c) +" " + Internationalization.get("my.hasleft");
                
                deleteClient(c);
            }
            
            for (Entry<SocketClient, String> entry : nickMap.entrySet()) {
                    
                SocketClient key = entry.getKey();
               
      //           System.out.println("envoie");
                key.sendMessage(message);
                
    // do what you have to do here
                // In your case, an other loop.
            }    
            
            }
        }

    }

    public boolean nick(String s, SocketClient c) {
        String[] string = s.split(" ");
        if (string.length > 1) {
            if (string[0].equals("/nick")) {
                nickMap.put(c, string[1]);
                buddy();
                return true;
            }
        }
        return false;
    }

    public String setNick(SocketClient s) {
        return nickMap.get(s);
    }
    
       public void buddy() {
 //       Platform.runLater(new Runnable() {
 //   public void run() {
        //ba.clear();
           String msg = "buddyarea/";
       for (Entry<SocketClient, String> entry : nickMap.entrySet()) {
                    
                String key = entry.getValue();
                
                msg = msg+key+"/";
                
    // do what you have to do here
                // In your case, an other loop.
            }    
       for (Entry<SocketClient, String> entry : nickMap.entrySet()) {
   //                 System.out.println(msg);
                SocketClient key = entry.getKey();
    //             System.out.println("envoie");
                key.sendMessage(msg);
                
    // do what you have to do here
                // In your case, an other loop.
            }    
  //  }
//});
        
    }

}
