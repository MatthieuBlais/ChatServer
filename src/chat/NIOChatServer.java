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
import java.util.Iterator;

/**
 *
 * @author Matthieu Blais
 */
public class NIOChatServer extends AbstractMultichatServer implements Runnable{
 
    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer buf;
    
    public NIOChatServer(int port, InetAddress address){
        super(port, address);
    }

    @Override
    public void start() throws IOException {
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(getAddress(),getPort()));
	this.ssc.configureBlocking(false);
    }
    
    public void accept() throws IOException{
       	this.selector = Selector.open();
        this.buf = ByteBuffer.allocate(8000);
	this.ssc.register(selector, SelectionKey.OP_ACCEPT);
    }
    
    @Override
    public void run(){
        
        try {
		System.out.println("Server starting on port " + getPort());
 
		Iterator<SelectionKey> iter;
		SelectionKey key;
			while(this.ssc.isOpen()) {
				selector.select();
				iter=this.selector.selectedKeys().iterator();
				while(iter.hasNext()) {
					key = iter.next();
					iter.remove();
					if(key.isAcceptable()) 
                                            handleAccept(key);
					if(key.isReadable()) 
                                            handleRead(key);
				}
			}
		} catch(IOException e) {
			System.out.println("IOException, server of port " +getPort()+ " terminating. Stack trace:");
			e.printStackTrace();
		}

    }
    
    private String decode(String s) throws CharacterCodingException{
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
        sc.configureBlocking(false);
	sc.register(selector, SelectionKey.OP_ACCEPT, getAddress());
	sc.write(ByteBuffer.wrap("Welcome on the Chat".getBytes()));
    }
    
    private void handleRead(SelectionKey key) throws IOException {
		SocketChannel ch = (SocketChannel) key.channel();
		StringBuilder sb = new StringBuilder();
 
		buf.clear();
		int read = 0;
		while( (read = ch.read(buf)) > 0 ) {
			buf.flip();
			byte[] bytes = new byte[buf.limit()];
			buf.get(bytes);
			sb.append(new String(bytes));
			buf.clear();
		}
		String msg;
		if(read<0) {
			msg = key.attachment()+" left the chat.\n";
			ch.close();
		}
		else {
                        nick(decode(sb.toString()), key);
			msg = key.attachment()+"> "+ decode(sb.toString());
		}
 
		System.out.println(msg);
		
                ByteBuffer msgBuf=ByteBuffer.wrap(msg.getBytes());
		for(SelectionKey key2 : selector.keys()) {
			if(key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel sch=(SocketChannel) key.channel();
				sch.write(msgBuf);
				msgBuf.rewind();
			}
		}
	}
    
        public void nick(String s, SelectionKey key) {
        String[] string= s.split(" ");
        if(string.length>1){
            if(string[0].equals("/nick")){
                key.attach(string[1]);
            }
        }
    }
}
