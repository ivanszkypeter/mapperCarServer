package server;

import java.io.OutputStream;
import java.util.concurrent.Semaphore;

public class Server implements CommunicationMessageListener {
	
	final String INITIAL_MESSAGE = "hi";
	final String FINAL_MESSAGE = "bye"; 
	final String ACKNOWLEDGE_MESSAGEMENT = "ok";
	
	private Communication comm;
	
	private boolean inConnection;
	
	private final Semaphore semaphore;
	
	private OutputStream outputStream;
	
	Server(Communication comm){
		semaphore = new Semaphore(1);
		this.comm = comm;
		comm.setOnMessageListener(this);
		comm.initialize();
	}
	
	public void onMessageReceived(String message) {
		System.out.println(message);
		if (inConnection) {
			if (message.equals(FINAL_MESSAGE))
			{
				inConnection = false;
				comm.sendMessage(FINAL_MESSAGE);
			}
			else if (message.equals(ACKNOWLEDGE_MESSAGEMENT))
			{
				semaphore.release();
			}
		}
		else {
			if (message.equals(INITIAL_MESSAGE))
			{
				inConnection = true;
				comm.sendMessage(INITIAL_MESSAGE);
				startDiscover();
			}
			else
			{
				System.out.println("The first message should be:" + INITIAL_MESSAGE);
				comm.sendMessage("The first message should be:" + INITIAL_MESSAGE);
			}
		}
	}
	
	private void startDiscover() {
		comm.sendMessage("cell_size:cm");
		try {
			semaphore.acquire();
			discoverArea();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void discoverArea() {
		comm.sendMessage("measure_cells:LRFB");
	}

}
