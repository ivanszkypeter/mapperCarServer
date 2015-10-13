package server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

	public static void main(String[] args) {

		ConcurrentLinkedQueue receiveQueue = new ConcurrentLinkedQueue<String>();
		ConcurrentLinkedQueue sendQueue = new ConcurrentLinkedQueue<String>();

		Thread communicationThread = new Thread(new CommunicationDummy(receiveQueue, sendQueue));
		Thread serverThread = new Thread(new Server(receiveQueue, sendQueue));
		communicationThread.start();
		serverThread.start();
	}

}
