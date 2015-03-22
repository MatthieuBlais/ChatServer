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
import static javafx.application.Application.launch;
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
import javafx.stage.WindowEvent;

public class Chat extends Application {

   private static TextField message;
    private static TextField portTextField;
    private static TextField ipTextField;
    private static TextArea area;
    private static TextArea Buddyarea;
    private static Button btn;
    private static boolean start = false;
    private static boolean end = false;
    private static boolean client = true;
    private static int port = 0;
    private static boolean nio = false;
    private static boolean multicast = false;
    private static String address = null;
    private static ClientSocket cl;
    private static MultiCastClient mcl;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         try {
            port = 80;
            address = "127.0.0.1";
            btn = new Button();
            message = new TextField();
            area = new TextArea();
            Buddyarea = new TextArea();
            NIOChatServer server2;
            
         //   m = synchronizedMap(new HashMap<>());
        //   nick = synchronizedMap(new HashMap<>());

           // Server server;
          //  NIOChatServer server2;
            
            Options(args);

            if (!end) {
                System.out.println("okk");
                if(multicast){
                    mcl = new MultiCastClient(message, btn, area, Buddyarea);
                    launch(args);
                }
                else{
                if (nio) {
                    server2 = new NIOChatServer(port, InetAddress.getByName(address));
                    server2.start();
                    if (start) {
                        System.out.println("pp");
                        (new Thread(server2)).start();
                    }
                } else { 
                    if (start && !client) {
                      // TODO code application logic here
            SocketServer s = new SocketServer(2000, InetAddress.getByName(address));
            s.start();
                    }
                    else{
                        System.out.println("ok3");
                     //   ChatThread chatt = new ChatThread(client,getMMap(),getNickMap());
                    //    new Thread(chatt).start();
                    }
                    if(client){
                        cl = new ClientSocket(port, InetAddress.getByName(address), btn, message, area, Buddyarea);
                         launch(args);
            }
                }
            }
            }
        
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(chat.Chat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(chat.Chat.class.getName()).log(Level.SEVERE, null, ex);
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

    
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat");
        
        Button btnConnexion = new Button("Connection");
        //message = new TextField();
        ipTextField = new TextField();
        portTextField = new TextField();
        
        Label ipLabel = new Label("Server IP");
        Label portLabel = new Label("Server port");
        
        btn.setText("Send Message");
        area.setEditable(false);
        Buddyarea.setEditable(false);
        
        
        btnConnexion.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!ipTextField.getText().isEmpty() && !portTextField.getText().isEmpty()) {
                } else {
                    //Connexion
                }
            }
        });
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
          public void handle(WindowEvent we) {
              if(!multicast)
              cl.setExit();
              else
                  mcl.setExit();
             
              System.exit(0);
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

   /* public void sendMessage(String text) {
        if (area.getText().isEmpty()) {
            area.setText(text);
        } else {
            area.setText(area.getText() + "\n" + text);
        }
    }*/
    
}
