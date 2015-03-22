/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Matthieu Blais
 */
public class NIOChatServer extends AbstractMultichatServer implements Runnable {

    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer buf;
    private Map<SocketChannel, String> nickMap;
    private int compteur;

    public NIOChatServer(int port, InetAddress address) {
        super(port, address);
        compteur = 0;
        nickMap = synchronizedMap(new HashMap<SocketChannel, String>());
    }

    @Override
    public void start() throws IOException {
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(getAddress(), getPort()));
        this.ssc.configureBlocking(false);
        this.selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        buf = ByteBuffer.allocate(5000);
    }

    /*public void accept() throws IOException{
       	
     this.buf = ByteBuffer.allocate(8000);
     this.ssc.register(selector, SelectionKey.OP_ACCEPT);
     }*/
    @Override
    public void run() {

        try {
            System.out.println("Server starting on port " + getPort());

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (this.ssc.isOpen()) {

                selector.select();
                iter = this.selector.selectedKeys().iterator();
                while (iter.hasNext()) {

                    key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    //    else if(key.isWritable()) doWrite(key);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException, server of port " + getPort() + " terminating. Stack trace:");
            e.printStackTrace();
        }

    }

    private String decode(String s) throws CharacterCodingException {
        // Returns a charset object for the named charset.
        Charset charset = Charset.forName("ISO-8859-1");

        // Constructs a new decoder for this charset.
        CharsetDecoder decoder = charset.newDecoder();

        // Constructs a new encoder for this charset.
        CharsetEncoder encoder = charset.newEncoder();

        // Wrap the character sequence into a buffer.
        CharBuffer uCharBuffer = CharBuffer.wrap(s);

        // Encode the remaining content of a single input character buffer to a new byte buffer.
        // Converts to ISO-8859-1 bytes and stores them to the byte buffer
        ByteBuffer bbuf = encoder.encode(uCharBuffer);

        // Decode the remaining content of a single input byte buffer to a new character buffer.
        // Converts from ISO-8859-1 bytes and stores them to the character buffer
        CharBuffer cbuf = decoder.decode(bbuf);
        return cbuf.toString();
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        nickMap.put(sc, "Guest");
        System.err.println("jai ajoute " + nickMap.get(sc));
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, getAddress());
        sc.write(ByteBuffer.wrap("Welcome on the ECE Chat 1.0\n".getBytes()));
    }

    public void deleteClient(SocketChannel c) {
    //    nickMap.remove(c);
        //   buddy();
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        System.err.println("EST CE QUE JAI "+nickMap.get(ch));
        StringBuilder sb = new StringBuilder();
        int a = 0;
        buf.clear();
        int read = 0;
        if (ch.isConnected()) {
            try {
                while ((read = ch.read(buf)) > 0) {
                    buf.flip();
                    byte[] bytes = new byte[buf.limit()];
                    buf.get(bytes);
                    sb.append(new String(bytes));
                    buf.clear();
                }
                System.out.println("read : " + read);
                String msg = null;
                if (read < 0) {
                    msg = key.attachment() + " left the chat.\n";
                    ch.close();
                } else {
                 //    msg = nickMap.get(ch)+ "> "+decode(sb.toString());
                    String ss = decode(sb.toString());
                    ss.replaceAll("\\r\\n", "\\n");
                    String[] t = ss.split("\\r?\\n");
                    String s = "";
                    for (int l=0; l<t.length; l++){
                        s = s + t[l];
                    }
                    System.out.println("JAI RECU CA " + s);
                    String[] tmp = s.split("~#%#~");
                    if (tmp.length == 2) {

                        if (!nick(tmp[1], ch)) {
                            System.out.println("OUESH");
                            msg="";
                            msg = tmp[0] + "~~&~&" + nickMap.get(ch) + "> " + tmp[1];
                            /*    ByteBuffer msgBuf2=ByteBuffer.wrap(msg.getBytes());
                             ch.write(ByteBuffer.wrap(msg.getBytes()));*/

                        } else {
                            msg = "";
                            msg = tmp[0] + "~~&~&" + nickMap.get(ch) + "## ff";
                        }
                    } else {
                        System.err.println("JE SSSUIIISSS LAAAAAAAAA");
                        tmp = s.split("~#~#~");
                        if (tmp.length == 2) {
                            System.err.println("JE SSSUIIISSS LAAAAAAAAA");
                            if (tmp[1].equals("/myID/")) {
                                System.out.println("POPOPOPOPOPOPOPO");
                                msg = tmp[0] + "~~&~&" + nickMap.get(ch) + "@~~@~~ aze";
                            } else if (tmp[1].equals("/Iconnect/")) {
                                System.out.println("BALALALAsdfsdfsd");
                                msg = tmp[0] + "~~&~&" + nickMap.get(ch) + "&~=====>" + nickMap.get(ch) + " has join the chat";
                            } else if (tmp[1].equals("/Ileave/")) {
                                msg = tmp[0] + "~~&~&" + nickMap.get(ch) + "_____" + nickMap.get(ch) + " has left the chat";

                            //    deleteClient(ch);
                            }
                        }
                    }
                }
                if (msg != null) {
                    System.out.println(msg);
                    msg = msg + "\n";
                    ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
                    
                    for (Map.Entry<SocketChannel, String> entry : nickMap.entrySet()) {

                        SocketChannel c = entry.getKey();
                        System.out.println("envoie " + msg);
                        // if(c!=ch && a==1){
                        int ff = c.write(msgBuf);
                        System.err.println("JAI ECRI " + ff);
                        msgBuf.rewind();//}

    // do what you have to do here
                        // In your case, an other loop.
                    }
                }
                /*for(SelectionKey key2 : selector.keys()) {
                 if(key.isValid() && key.channel() instanceof SocketChannel) {
                 SocketChannel sch=(SocketChannel) key.channel();
                 sch.write(msgBuf);
                 msgBuf.rewind();
                 }
                 }*/
            } catch (IOException e) {

            }

        }
    }

    public boolean nick(String s, SocketChannel sc) {
        s.replaceAll("\\r\\n", "\\n");
        String[] string = s.split(" ");
        if (string.length > 1) {

            if (string[0].equals("/nick")) {
                System.out.println("NICK " + string[1].split("\\r?\\n")[0] + " po");
                nickMap.put(sc, string[1].split("\\r?\\n")[0]);
                return true;
            }
        }
        return false;
    }
}
