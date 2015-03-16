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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Chat extends Application {

    private static TextField message;
    private static TextField portTextField;
    private static TextField ipTextField;
    private static TextArea area;
    private static TextArea Buddyarea;
    private static boolean start = false;
    private static boolean end = false;
    private static boolean client = true;
    private static int port = 0;
    private static boolean nio = false;
    private static boolean multicast = false;
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
        LongOpt[] longopts = new LongOpt[6];

        int c;

        StringBuffer adress = new StringBuffer();
        StringBuffer portt = new StringBuffer();
        longopts[0] = new LongOpt("address", LongOpt.REQUIRED_ARGUMENT, adress, 'a');
        longopts[1] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[2] = new LongOpt("nio", LongOpt.NO_ARGUMENT, null, 'n');
        longopts[3] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, portt, 'p');
        longopts[4] = new LongOpt("multicast", LongOpt.NO_ARGUMENT, null, 'm');
        longopts[5] = new LongOpt("server", LongOpt.NO_ARGUMENT, null, 's');
        // 
        Getopt g = new Getopt("ChatServer", argv, "a:hnp:ms", longopts);
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
                            + "-m , -- use multicast socket\n"
                            + "-s , -- server start the server");
                    break;

                case 'n':
                    nio = true;
                    break;
                    
                case 'm':
                    multicast = true;
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
                            + "-m , -- use multicast socket\n"
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
                    + "-m , -- use multicast socket\n"
                    + "-s , -- server start the server");
            end = true;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat");
        Button btn = new Button();
        Button btnConnexion = new Button("Connection");
        message = new TextField();
        ipTextField = new TextField();
        portTextField = new TextField();
        Buddyarea = new TextArea();
        Label ipLabel = new Label("Server IP");
        Label portLabel = new Label("Server port");
        area = new TextArea();
        btn.setText("Send Message");
        area.setEditable(false);
        Buddyarea.setEditable(false);
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
        
        btnConnexion.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!ipTextField.getText().isEmpty() && !portTextField.getText().isEmpty()) {
                } else {
                    //Connexion
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
        btn.setLayoutX(300);
        btn.setLayoutY(320);
         btnConnexion.setLayoutX(250);
        btnConnexion.setLayoutY(30);
        btnConnexion.setPrefWidth(140);
        message.setLayoutX(10);
        message.setLayoutY(320);
        message.setPrefWidth(280);
        ipTextField.setLayoutX(10);
        ipTextField.setLayoutY(30);
        ipTextField.setPrefWidth(150);
        portTextField.setLayoutX(170);
        portTextField.setLayoutY(30);
        portTextField.setPrefWidth(70);
        area.setPrefWidth(380);
        area.setPrefHeight(235);
        area.setLayoutX(10);
        area.setLayoutY(70);
        Buddyarea.setPrefHeight(330);
         Buddyarea.setPrefWidth(130);
          Buddyarea.setLayoutX(400);
        Buddyarea.setLayoutY(10);
        ipLabel.setLayoutX(60);
        ipLabel.setLayoutY(7);
        portLabel.setLayoutX(175);
        portLabel.setLayoutY(7);
        root.getChildren().add(btn);
        root.getChildren().add(area);
        root.getChildren().add(ipTextField);
        root.getChildren().add(ipLabel);
        root.getChildren().add(portLabel);
        root.getChildren().add(btnConnexion);
        root.getChildren().add(portTextField);
        root.getChildren().add(message);
        root.getChildren().add(Buddyarea);
        primaryStage.setScene(new Scene(root, 540, 350));
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
