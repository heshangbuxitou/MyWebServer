package com.siyuyong.server;

import com.siyuyong.http.DefaultServlet;
import com.siyuyong.http.HttpRequest;
import com.siyuyong.http.HttpResponse;
import com.siyuyong.http.ServletContext;

import java.io.IOException;

public class RequestHandlerThread extends Thread {
    private Queue myQueue = null;
    private ServletContext servletContext = null;
    private DefaultServlet defaultServlet = new DefaultServlet();

    public RequestHandlerThread(ServletContext context, Queue q) {
        servletContext = context;
        myQueue = q;
    }

    public void run() {
        while (true) {
            Client client = (Client) myQueue.remove();
            try {
                for (; ; ) {
                    HttpRequest req = new HttpRequest(client.clientInputStream, servletContext);
                    HttpResponse resp = new HttpResponse(client.key);
                    defaultServlet.service(req, resp);
                    if (client.notifyRequestDone()) {
                        break;
                    }
                }
            } catch (IOException e) {
                client.key.cancel();
                client.key.selector().wakeup();
//                e.printStackTrace();
            }
        }
    }
}
