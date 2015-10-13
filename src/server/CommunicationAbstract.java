package server;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CommunicationAbstract {

    protected ConcurrentLinkedQueue<String> receiveQueue;
    protected ConcurrentLinkedQueue<String> sendQueue;

    public CommunicationAbstract(ConcurrentLinkedQueue receiveQueue, ConcurrentLinkedQueue sendQueue) {
        this.receiveQueue = receiveQueue;
        this.sendQueue = sendQueue;
    }

    public abstract void run();
}
