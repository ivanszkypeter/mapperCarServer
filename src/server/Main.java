package server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

	public static void main(String[] args) {

		ConcurrentLinkedQueue receiveQueue = new ConcurrentLinkedQueue<String>();
		ConcurrentLinkedQueue sendQueue = new ConcurrentLinkedQueue<String>();
		
		CommunicationCOM communicationCOM = new CommunicationCOM(receiveQueue, sendQueue);
		Server server = new Server(receiveQueue, sendQueue);
		
		Thread communicationThread = new Thread(communicationCOM);
		Thread serverThread = new Thread(server);
		communicationThread.start();
		serverThread.start();
		
		new GUI(communicationCOM, server);
	}

}
