/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author Matthieu Blais
 */
public class Sender extends Thread{

    private PrintWriter out;
    private Button btn;
    private TextField message;
    private String nickName;
    private String ID;
    private SecureRandom random;

    public Sender(PrintWriter aOut, Button btn, TextField message) {
        out = aOut;
        this.btn = btn;
        this.message = message;
        random = new SecureRandom();
        ID = SessionId();
        nickName = "Guest";
        setConnect();
        this.btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
    //            System.out.println("dfgfdlkgj "+message.getText());
                if (!message.getText().equals("")) {
    //                System.out.println("poulet");
                    String text = ID+"~#%#~"+message.getText();
                    out.println(text);
                    out.flush();
   //                 System.out.println("J ai fini");
                    message.setText(new String(""));
                }
            }
            });
        
    }
    
    public void newJoin(){
        String text = ID+"~#~#~"+"/myID/";
        out.println(text);
        out.flush();
    }
    
    public void setConnect(){
        String text = ID+"~#~#~"+"/Iconnect/";
        out.println(text);
        out.flush();
    }
    
    public void setExit(){
        String text = ID+"~#~#~"+"/Ileave/";
        out.println(text);
        out.flush();
    }

    public String SessionId() {
        return new BigInteger(130, random).toString(32);
    }
 
}

