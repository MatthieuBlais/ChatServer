/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;
import java.io.IOException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Chat extends Application {

    private static TextField message;
    private static TextArea area;
    private static boolean start = false;
    private static boolean end = false;
    private static boolean client = true;
    private static int port = 0;
    private static boolean nio = false;
    private static String address = null;

    public static void main(String[] args) {
        try {
            
            
            

            Server server;
            NIOChatServer server2;
            Options(args);
            if(client) launch(args);
            if (!end) {
                if (nio) {
                    server2 = new NIOChatServer(port, InetAddress.getByName(address));
                    if (start) {
                        (new Thread(server2)).start();
                    }
                } else {
                    server = new Server(port, InetAddress.getByName(address));
                    if (start) {
                        server.start();
                    }
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Options(String[] argv) {
        LongOpt[] longopts = new LongOpt[5];

        int c;

        StringBuffer adress = new StringBuffer();
        StringBuffer portt = new StringBuffer();
        longopts[0] = new LongOpt("address", LongOpt.REQUIRED_ARGUMENT, adress, 'a');
        longopts[1] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[2] = new LongOpt("nio", LongOpt.NO_ARGUMENT, null, 'n');
        longopts[3] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, portt, 'p');
        longopts[4] = new LongOpt("server", LongOpt.NO_ARGUMENT, null, 's');
        // 
        Getopt g = new Getopt("ChatServer", argv, "a:hnp:s", longopts);
        g.setOpterr(true);
        //
        boolean arg = false;
        while ((c = g.getopt()) != -1) {
            arg = true;
            switch (c) {

                case 'a':
                    address = g.getOptarg();
                    break;
                //
                case 'h':
                    System.out.println("-a , -- address set the IP address\n"
                            + "-h , -- help display this help and quit\n"
                            + "-n , -- nio use NIOs for the server\n"
                            + "-p , -- port = PORT set the port\n"
                            + "-c , -- client option = Start interface\n"
                            + "-s , -- server start the server");
                    break;

                case 'n':
                    nio = true;
                    break;
                //
                case 'p':
                    port = Integer.parseInt(g.getOptarg());
                    break;
                //
                case 's':
                    start = true;
                    client = false;
                    break;
                //
                case ':':
                    System.out.println("You need an argument for option "
                            + (char) g.getOptopt());
                    end = true;
                    break;
                //
                case '?':
                    System.out.println("The option '" + (char) g.getOptopt()
                            + "' is not valid");
                    end = true;
                    break;
                //
                default:
                    System.out.println("Error");
                    System.out.println("-a , -- address set the IP address\n"
                            + "-h , -- help display this help and quit\n"
                            + "-n , -- nio use NIOs for the server\n"
                            + "-p , -- port = PORT set the port\n"
                            + "-s , -- server start the server");
                    end = true;
                    break;
            }
        }
        if (!arg) {
            System.out.println("Error : No Arguments");
            System.out.println("-a , -- address set the IP address\n"
                    + "-h , -- help display this help and quit\n"
                    + "-n , -- nio use NIOs for the server\n"
                    + "-p , -- port = PORT set the port\n"
                    + "-s , -- server start the server");
            end = true;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat");
        Button btn = new Button();
        message = new TextField();
        area = new TextArea();
        btn.setText("Send Message");
        area.setDisable(false);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (message.getText().isEmpty()) {
                } else {
                    sendMessage(message.getText());
                    message.setText("");
                }
            }
        });

        area.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue) {
                area.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });

        Pane root = new Pane();
        btn.setLayoutX(200);
        btn.setLayoutY(220);
        message.setLayoutX(10);
        message.setLayoutY(220);
        message.setPrefWidth(180);
        area.setPrefWidth(280);
        area.setPrefHeight(195);
        area.setLayoutX(10);
        area.setLayoutY(10);
        root.getChildren().add(btn);
        root.getChildren().add(area);
        root.getChildren().add(message);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public void sendMessage(String text) {
        if (area.getText().isEmpty()) {
            area.setText(text);
        } else {
            area.setText(area.getText() + "\n" + text);
        }
    }

}
