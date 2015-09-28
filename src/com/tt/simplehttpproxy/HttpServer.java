package com.tt.simplehttpproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HttpServer implements Runnable {

	private ServerSocketChannel serverSocketChannel;
	
	private Selector selector;
	
	public HttpServer() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9999));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			Log.i("server initilialized");
		} catch (IOException e) {
			Log.e("error initializing server", e);
		}
	}
	
	@Override
	public void run() {
		try {
			Log.i("server is accepting connections");
			while (!Thread.currentThread().isInterrupted()) {
				selector.select(10 * 1000);
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while (keys.hasNext()) {
					SelectionKey selectionKey = keys.next();
					keys.remove();
					if (!selectionKey.isValid()) {
						continue;
					}
					if (selectionKey.isAcceptable()) {
						accept(selectionKey);
					}
					if (selectionKey.isWritable()) {
						write(selectionKey);
					}
					if (selectionKey.isReadable()) {
						read(selectionKey);
					}
				}
			}
		} catch (IOException e) {
			Log.e("error looping through keys", e);
		} finally {
			close();
		}
	}

    private void accept(SelectionKey key) throws IOException{
    	Log.i("accept");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }
	
    private void write(SelectionKey key) throws IOException{
    	Log.i("write");
        SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap("HTTP/1.1 200 OK\nContent-Length: 0".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }
    
    private void read(SelectionKey key) throws IOException{
    	Log.i("read");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        readBuffer.clear();
        int read;
        try {
            read = channel.read(readBuffer);
        } catch (IOException e) {
            System.out.println("Reading problem, closing connection");
            key.cancel();
            channel.close();
            return;
        }
        if (read == -1){
            System.out.println("Nothing was there to be read, closing connection");
            channel.close();
            key.cancel();
            return;
        }
        readBuffer.flip();
        byte[] data = new byte[1000];
        readBuffer.get(data, 0, read);
        System.out.println("Received: "+new String(data));
 
        key.interestOps(SelectionKey.OP_WRITE);
    }
    
    private void close(){
        if (selector != null){
            try {
                selector.close();
                serverSocketChannel.socket().close();
                serverSocketChannel.close();
                Log.i("server closed");
            } catch (IOException e) {
                Log.e("error closing server", e);
            }
        }
    }
	
}
