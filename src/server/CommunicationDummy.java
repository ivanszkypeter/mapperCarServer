package server;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationDummy extends CommunicationAbstract implements Runnable {

	private ArrayList<String> messagesToPC = new ArrayList<>();

	public CommunicationDummy(ConcurrentLinkedQueue receiveQueue, ConcurrentLinkedQueue sendQueue) {
		super(receiveQueue, sendQueue);
		messagesToPC.add("hi");
		messagesToPC.add("ok");
        messagesToPC.add("cell_info:LfRfFfBf");
        messagesToPC.add("ok");
        messagesToPC.add("cell_info:LfRfFfBf");
        messagesToPC.add("ok");
	}

	@Override
	public void run() {

		receiveQueue.add(messagesToPC.get(0));
		int messageNumber = 0;

	    try {
			Thread.currentThread().sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			String messageFromPC;
			while ((messageFromPC = sendQueue.poll()) != null) {
				System.out.println("PC -> CAR: "+messageFromPC);
                if (messagesToPC.size() > messageNumber)
                {
                    receiveQueue.add(messagesToPC.get(messageNumber));
                    System.out.println("CAR -> PC: "+ messagesToPC.get(messageNumber));
                    messageNumber++;
                }
			}
			
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

    }

}
