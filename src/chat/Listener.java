/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Matthieu Blais
 */
public class Listener extends Thread {

    private BufferedReader in;
    private Button btn;
    private TextArea area;
    private TextArea bud;
    private HashMap<String, String> collection;
    private String prev;
    private ClientSocket cs;
    
    public Listener(BufferedReader in, Button btn, TextArea message, TextArea bud, ClientSocket cs) {
        this.in = in;
        this.btn = btn;
        this.area = message;
        this.bud = bud;
        this.cs = cs;
        collection = new HashMap<String, String>();
        prev = Internationalization.get("my.guest");

        area.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue) {
                area.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });
        this.bud.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue) {
                bud.setScrollTop(Double.MAX_VALUE);

                //use Double.MIN_VALUE to scroll to the top
            }
        });
    }

    public void newBuddy(String text) {

        Platform.runLater(new Runnable() {
            public void run() {

                bud.clear();
                System.out.println("pouet" + text + "ee");
                bud.setText(text);
            }
        });

    }

    public void sendMessage(String text) {
        String s = nick(text);
        Platform.runLater(new Runnable() {
            public void run() {
                if (area.getText().isEmpty()) {
                    if (s != null) {
                        area.setText(s);
                    }
                } else {
                    if (s != null) {
                        area.setText(area.getText() + "\n" + s);
                    }
                }
            }
        });

    }

    public String nick(String s) {
        String[] tmp = s.split("> ");
        System.out.println(s);
        System.out.println("LA LONGUEUR DE MA CHAINE " + tmp.length);
        String message = "";
        if (tmp.length > 1) {
            for (int i = 1; i < tmp.length; i++) {
                message = message + tmp[i];
            }
            String[] p = tmp[0].split("~~&~&");
            if (p.length > 1) {
                collection.put(p[0], p[1]);
                newBuddy(updateBuddy());
                message = p[1] + "> " + message;
            }
        } else {
            System.out.println("BOUTEILLE");
            tmp = s.split("##");
            if (tmp.length > 1) {
                String[] p = tmp[0].split("~~&~&");
                if (p.length > 1) {
                    collection.put(p[0], p[1]);
                    newBuddy(updateBuddy());
                    message = null;
                }
            } else {
                System.out.println("EVOTILEAKKFDQS");
                tmp = s.split("_____");
                if (tmp.length > 1) {
                    String[] pp = tmp[0].split("~~&~&");
                    if (pp.length > 1) {
                        collection.remove(pp[0], pp[1]);
                        newBuddy(updateBuddy());
                        message = tmp[1];
                    }
                } else {
                    tmp = s.split("&~=====>");
                    if (tmp.length > 1) {
                        String[] pp = tmp[0].split("~~&~&");
                        if (pp.length > 1) {
                            collection.put(pp[0], pp[1]);
                            newBuddy(updateBuddy());
                            message = tmp[1];
                            cs.newJoin();
                        }
                    } else {
                        System.out.println(s + " BLAH");
                        tmp = s.split("@~~@~~");
                        System.out.println(tmp.length);
                        if(tmp.length>1){
                            System.out.println("BLOUH");
                            String[] pp = tmp[0].split("~~&~&");
                        if (pp.length > 1) {
                            System.out.println("BLEMH");
                            collection.put(pp[0], pp[1]);
                            newBuddy(updateBuddy());
                            message = null;
                       
                        }
                        }else  message = null;
                    }
                }

            }

        }
          //  message = s;

        return message;
    }

    public String updateBuddy() {
        String msg = "";
        for (Map.Entry<String, String> entry : collection.entrySet()) {

            String key = entry.getValue();
            System.out.println("up : " + key);
            msg = msg + key + "\n";

    // do what you have to do here
            // In your case, an other loop.
        }
        System.out.println("mon mess : " + msg);
        return msg;
    }

    public void run() {
        try {
            /* btn.setOnAction(new EventHandler<ActionEvent>() {

             @Override
             public void handle(ActionEvent event) {
             System.out.println("fiiou");
             if (!message.getText().isEmpty()) {
             String text = message.getText();
                   
             message.setText(new String(""));
             }
             }
             });*/

            // Read messages from the server and print them
            String message2;
            String message3 = "";
            String tmp[];
            while ((message2 = in.readLine()) != null) {
                System.err.println("BALANCE LE SON " + message2);
                tmp = message2.split("/");
                if (tmp.length > 0) {
                    if (tmp[0].equals("buddyarea")) {
                        for (int i = 1; i < tmp.length; i++) {
                            message3 = message3 + tmp[i] + "\n";
                        }
                        //   sendBuddy(message3);
                    } else {
                        sendMessage(message2);
                    }
                } else {
                    sendMessage(message2);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
