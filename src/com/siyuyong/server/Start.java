package com.siyuyong.server;

import java.io.File;
import java.io.IOException;

public class Start {
    /**
     *
     * @param args
     * args[0] port
     * args[1] filepath
     */
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port, new ServerEventHandler(args[1], 4));
        System.out.println("Starting server on port:" + port);
        System.out.println("Web root folder: " + new File(args[1]).getAbsolutePath());
        while (true){
            server.listen();
        }
    }
}
