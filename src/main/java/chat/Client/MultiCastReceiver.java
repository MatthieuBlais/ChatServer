/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Client;

import chat.Internationalization;
import java.io.IOException;
import static java.lang.Thread.MIN_PRIORITY;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

/**
 *
 * @author Matthieu Blais
 */
public class MultiCastReceiver implements Runnable {

    private TextArea area;
    private String adress;
    private int port;
    private HashMap<String, String> collection; 
    private TextArea bud;
    private MultiCastClient mcc;
    private boolean b;
    
    public MultiCastReceiver(TextArea area, String adress, int port, TextArea bud, MultiCastClient c, boolean de){
        this.area = area;
        this.adress = adress;
        this.port = port;
        b = de;
        this.bud = bud;
        mcc = c;
        collection =  new HashMap<String,String>() ;
        area.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue) {
                area.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });
    }
    
    @Override
    public void run() {
     //   System.out.println("jattendpp");
         MulticastSocket socket = null;
    DatagramPacket in = null;
    byte[] buf = new byte[1200];
    try {
      
      socket = new MulticastSocket(port);
      InetAddress address = InetAddress.getByName(adress);
      socket.joinGroup(address);
      
      while (true) {
        in = new DatagramPacket(buf, buf.length);
   //     System.out.println("jattend");
        socket.receive(in);
   //     System.out.println("Jairecu");
        String msg = new String(buf, 0, in.getLength());
        connect(msg);
        hasLeft(msg);
        hasJoin(msg);
        if(nick(extractMessage(msg), extractID(msg)))
            newBuddy(updateBuddy());
        String msg2 = extractMessage(msg);
//        System.out.println(msg);
        Platform.runLater(new Runnable() {
    public void run() {
        if (area.getText().isEmpty()) {
            if(msg2!=null)
            area.setText(msg2);
        } else {
            if(msg2!=null)
            area.setText(area.getText() + "\n" + msg2);
        }
    }
});
      }
    } catch (IOException ioe) {
     Alert a = new Alert(AlertType.ERROR);
          a.setTitle(Internationalization.get("my.error"));
          a.setHeaderText(Internationalization.get("my.error2"));
          a.setContentText(ioe.getMessage());
          if(b)
              System.out.println(ioe.getMessage());
    }    
    }
    
     public void newBuddy(String text) {
       
        Platform.runLater(new Runnable() {
    public void run() {
     
            bud.clear();
    //        System.out.println("pouet" + text +"ee");
            bud.setText(text);
    }
});
        
    }
     
     public void hasLeft(String s){
         String[] tmp = s.split("</>");
         if(tmp.length>1){
             String[] p = tmp[0].split("><<|>><");
             if(p.length>1){
    //             System.out.println("hello");
                 collection.remove(p[0]);
                 newBuddy(updateBuddy());
             }
         }
     }
     
     public void connect(String s){
         String[] tmp = s.split("</>");
     //    System.out.println("test xxxxxxxxxx");
         if(tmp.length>=1){
     //        System.out.println(tmp[0]);
             String[] p = tmp[0].split("~~");
    //         System.out.println("LONGUEUR " + p.length);
             if(p.length>1){
     //            System.out.println("BONJOUR" + p[0] + " " + p[1]);
                 collection.put(p[0],p[1]);
                 newBuddy(updateBuddy());
             }
         }
     }
     
     public void hasJoin(String s){
         String[] tmp = s.split("</>");
         if(tmp.length>1){
     //        System.out.println(tmp[0]);
             String[] p = tmp[0].split("~##~");
      //       System.out.println("length " + p.length);
             if(p.length>1){
     //            System.out.println("hello " + p[0] + " " + p[1]);
                 collection.put(p[0],p[1]);
                 newBuddy(updateBuddy());
                 mcc.newJoin();
             }
         }
     }
    
    public String extractID(String s){
        String[] string = s.split("</>");
        if(string.length>1){
            String[] p = string[0].split(">/<");
            if(p.length>1){
                
                if(nick(p[1]+"> fg",p[0])){
      //              System.out.println("je SUIs LA ");
                    newBuddy(updateBuddy());
                }
            }
            return string[0];
        }
        else
            return null;
    }
    
    public String extractMessage(String s){
        String[] string = s.split("</>");
        if(string.length>1)
            return string[1];
        else
            return null;
    }
    
    public boolean nick(String s, String id) {
        if(id != null && s != null){
        //    System.out.println("eheh");
        String[] string = s.split("> ");
        if (string.length > 1) {
     //       System.out.println(id + " et " + string[0] +" ff");
                collection.put(id, string[0]);
                return true;
        }
        }
        return false;
    }
    
    public String updateBuddy(){
        String msg="";
        for (Map.Entry<String, String> entry : collection.entrySet()) {
                 
                String key = entry.getValue();
       //         System.out.println("up : "+key);
                msg = msg + key + "\n";
                
                
    // do what you have to do here
                // In your case, an other loop.
        }   
     //   System.out.println("mon mess : " + msg);
        return msg;
    }
    
}
