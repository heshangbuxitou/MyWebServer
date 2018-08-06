package com.siyuyong.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {

    private Selector selector = null;
    private int myPort = 0;
    private ServerEventHandler myHandler = null;

    Server(int port, ServerEventHandler serverEventHandler) throws IOException {
        myPort = port;
        selector = SelectorFactory.getSelector(port);
        myHandler = serverEventHandler;
    }

    public void listen() {
        SelectionKey key = null;
        try {
            for (; ; ) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    key = (SelectionKey) it.next();
                    handleKey(key);
                    it.remove();
                }

            }
        } catch (IOException e) {
            key.cancel();
        } catch (NullPointerException e) {
            // NullPointer at sun.nio.ch.WindowsSelectorImpl, Bug: 4729342
            e.printStackTrace();
        }
    }

    public void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            myHandler.acceptNewClient(selector, key);
        } else if (key.isReadable()) {
            myHandler.readDataFromSocket(key);
        }
    }
}
