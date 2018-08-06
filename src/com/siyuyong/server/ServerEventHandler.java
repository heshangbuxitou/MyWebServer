package com.siyuyong.server;

import com.siyuyong.http.ServletContext;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerEventHandler {

    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private ServletContext context = null;
    private Queue q;
    private int socketConnects = 0;

    public ServerEventHandler(String root, int workerThreads) {
        q = new Queue();
        context = new ServletContext(new File(root));
        for (int i = 0; i < workerThreads; i++) {
            new RequestHandlerThread(context, q).start();
        }
    }

    public void acceptNewClient(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        SelectionKey readKey = socketChannel.register(selector, SelectionKey.OP_READ);
        readKey.attach(new Client(readKey, q));
    }

    public void readDataFromSocket(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count = socketChannel.read(byteBuffer);
        if (count > 0) {
            byteBuffer.flip();
            byte[] buffer = new byte[count];
            byteBuffer.get(buffer, 0, count);
            ((Client) key.attachment()).write(buffer);
        } else if (count < 0) {
            key.cancel();
            socketChannel.close();
        }
        byteBuffer.clear();
    }
}
