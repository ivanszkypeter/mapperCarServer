package server;

import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server implements Runnable {

	final String INITIAL_MESSAGE = "hi";
	final String FINAL_MESSAGE = "bye";
	final String ACKNOWLEDGE_MESSAGEMENT = "ok";

	private ConcurrentLinkedQueue<String> receiveQueue;
	private ConcurrentLinkedQueue<String> sendQueue;

	@Override
	public void run() {

		while (true) {		
			String receivedMessage;
			while ((receivedMessage = receiveQueue.poll()) != null) {
				System.out.println("message received: " + receivedMessage);
				sendQueue.add("received: " + receivedMessage);
			}
			
			try {
				Thread.currentThread().sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	Server(ConcurrentLinkedQueue receiveQueue, ConcurrentLinkedQueue sendQueue) {
		this.receiveQueue = receiveQueue;
		this.sendQueue = sendQueue;
	}

	private void startDiscover() {
		// comm.sendMessage("cell_size:cm");
		discoverArea();
	}

	private void discoverArea() {
		// comm.sendMessage("measure_cells:LRFB");
	}

}
