/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Matthieu Blais
 */
public class MultiCastClient {
    
    private MultiCastSender sender;
    private MultiCastReceiver receiver;
    private Thread thd;
    
    public MultiCastClient(TextField txt, Button btn, TextArea area, TextArea bud){
        String adress = "224.2.2.2";
        int port = 4000;
        
        receiver = new MultiCastReceiver(area,adress,port, bud,this);
        thd = new Thread(receiver);
        thd.start();
        sender = new MultiCastSender(txt, btn, adress, port);
    }
    
    public void setExit(){
        thd.interrupt();
        sender.left();
    }
    
    public void newJoin(){
        sender.newJoin();
    }
}
