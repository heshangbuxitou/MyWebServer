package com.siyuyong.server;

import java.io.*;
import java.nio.channels.SelectionKey;

public class Client {
    SelectionKey key = null;
    Queue myQueue = null;
    public PipedInputStream clientInputStream;
    private OutputStream clientOut;
    private boolean requestReady = false;


    public Client(SelectionKey selectionKey, Queue q) throws IOException {
        key = selectionKey;
        myQueue = q;
        initializePipe();
    }

    private void initializePipe() throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        clientInputStream = new PipedInputStream(pos);
        clientOut = new BufferedOutputStream(pos, 1048);
    }

    public synchronized void write(byte[] buffer) throws IOException {
        clientOut.write(buffer);
        clientOut.flush();
        if (!requestReady) {
            requestReady = true;
            myQueue.insert(this);
        }
    }

    public synchronized boolean notifyRequestDone() throws IOException {
        if (clientInputStream.available() <= 0) {
            requestReady = false;
        }
        return !requestReady;
    }
}
