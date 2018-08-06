package com.siyuyong.server;

import java.util.LinkedList;

public class Queue extends LinkedList {

    private int waitingThreads = 0;

    public synchronized Object remove() {
        if (isEmpty()) {
            try {
                waitingThreads++;
                wait();
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            waitingThreads--;
        }
        return removeFirst();
    }

    public synchronized void insert(Object obj) {
        addLast(obj);
        notify();
    }

    public boolean isEmpty() {
        return (size() - waitingThreads <= 0);
    }
}
