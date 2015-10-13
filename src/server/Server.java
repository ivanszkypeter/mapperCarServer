package server;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.omg.CORBA.UNKNOWN;

public class Server implements Runnable {

	final String INITIAL_MESSAGE = "hi";
	final String FINAL_MESSAGE = "bye";
	final String ACKNOWLEDGE_MESSAGE = "ok";
	
	private final int SLEEP_TIME = 10;

	private ConcurrentLinkedQueue<String> receiveQueue;
	private ConcurrentLinkedQueue<String> sendQueue;

	private Algorithm algorithm = new Algorithm();

	private boolean hasConnection = false;

	private int cellSize = 24; // size of the cells in centimeters

	Server(ConcurrentLinkedQueue receiveQueue, ConcurrentLinkedQueue sendQueue) {
		this.receiveQueue = receiveQueue;
		this.sendQueue = sendQueue;
	}

	@Override
	public void run() {

		handshaking();
	}

	private Boolean isMessage(String message) {
		String receivedMessage;
		return ((receivedMessage = receiveQueue.poll()) != null) && receivedMessage.equals(message);
	}

	private String isMessageStartingWith(String message) {
		String receivedMessage;
		if (((receivedMessage = receiveQueue.poll()) != null) && receivedMessage.startsWith(message)) {
			return receivedMessage;
		}
		return null;
	}

	/**
	 * Block the thread until a specific message arrive.
	 */
	private void waitFor(String messageToWaitFor) {
		while(!isMessage(messageToWaitFor)) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Block the thread until a message with a specific starting arrive.
	 * After that it returns with the whole message.
	 */
	private String waitForMessageToStartWith(String messageToWaitFor) {
		String message;
		while(true) {
			message = isMessageStartingWith(messageToWaitFor);
			if (message != null)
				return message;
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Block the thread until a specific message arrive. Send periodically the ping.
	 */
	private void waitForAndPing(String messageToWaitFor, String messageToPing) {
		int period = 0;
		while(!isMessage(messageToWaitFor)) {
			if (period % 100 == 0) {
				sendQueue.add(messageToPing);
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			period++;
		}
	}

	/**
	 * Block the thread until a specific message arrive. After that immediately send an answer.
	 */
	private void waitForAndSend(String messageToWaitFor, String messageToSend) {
		waitFor(messageToWaitFor);
		sendQueue.add(messageToSend);
	}

	/**
	 * Send a message and wait for its response.
	 */
	private void sendAndWaitFor(String messageToSend,String messageToWaitFor) {
		waitForAndPing(messageToWaitFor, messageToSend);
	}

	/* Initial message sending between the communication parties.
	 */
	private void handshaking() {
		waitForAndSend(INITIAL_MESSAGE, INITIAL_MESSAGE);
		setCellSize();
	}

	private void setCellSize() {
		sendAndWaitFor("cell_size:" + cellSize, ACKNOWLEDGE_MESSAGE);
		measureCells();
	}

	private void measureCells() {
		String messageToSend = "measure_cells:";
		Map<Field.Direction, Boolean> directionsToMeasure = algorithm.whichFieldsToMeasure();
		if (directionsToMeasure.get(Field.Direction.LEFT)) {
			messageToSend += "L";
		} else
			messageToSend += "_";

		if (directionsToMeasure.get(Field.Direction.RIGHT)) {
			messageToSend += "R";
		} else
			messageToSend += "_";

		if (directionsToMeasure.get(Field.Direction.FORWARD)) {
			messageToSend += "F";
		} else
			messageToSend += "_";

		if (directionsToMeasure.get(Field.Direction.BACKWARD)) {
			messageToSend += "B";
		} else
			messageToSend += "_";

		// sending message
		sendQueue.add(messageToSend);

		getCellInfo(directionsToMeasure);
	}

	private void getCellInfo(Map<Field.Direction, Boolean> directionsToMeasure) {
		String message = waitForMessageToStartWith("cell_info:");
		String[] array = message.split(":");

		Map<Field.Direction, Field.FieldType> measuredDirections = new HashMap<Field.Direction, Field.FieldType>();
		measuredDirections.put(Field.Direction.LEFT, Field.FieldType.UNKNOWN);
		measuredDirections.put(Field.Direction.RIGHT, Field.FieldType.UNKNOWN);
		measuredDirections.put(Field.Direction.FORWARD, Field.FieldType.UNKNOWN);
		measuredDirections.put(Field.Direction.BACKWARD, Field.FieldType.UNKNOWN);

		if (directionsToMeasure.get(Field.Direction.LEFT)) {
			if (array[1].substring(1,2).equals("f")) {
				measuredDirections.put(Field.Direction.LEFT, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(1,2).equals("o")) {
				measuredDirections.put(Field.Direction.LEFT, Field.FieldType.OCCUPIED);
			}
		}

		if (directionsToMeasure.get(Field.Direction.RIGHT)) {
			if (array[1].substring(3, 4).equals("f")) {
				measuredDirections.put(Field.Direction.RIGHT, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(3, 4).equals("o")) {
				measuredDirections.put(Field.Direction.RIGHT, Field.FieldType.OCCUPIED);
			}
		}

		if (directionsToMeasure.get(Field.Direction.FORWARD)) {
			if (array[1].substring(5, 6).equals("f")) {
				measuredDirections.put(Field.Direction.FORWARD, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(5, 6).equals("o")) {
				measuredDirections.put(Field.Direction.FORWARD, Field.FieldType.OCCUPIED);
			}
		}

		if (directionsToMeasure.get(Field.Direction.BACKWARD)) {
			if (array[1].substring(7, 8).equals("f")) {
				measuredDirections.put(Field.Direction.BACKWARD, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(7, 8).equals("o")) {
				measuredDirections.put(Field.Direction.BACKWARD, Field.FieldType.OCCUPIED);
			}
		}

		algorithm.cellInfoReceived(measuredDirections);

		goToCells();
	}
	
	private void goToCells(){
		Field.Direction dir = algorithm.getNextField();
		if (dir != null)
		{
			String messageToSend = "go_to:";
			switch(dir){
				case LEFT:
					messageToSend += "L";
					break;
				case RIGHT:
					messageToSend += "R";
					break;
				case FORWARD:
					messageToSend += "F";
					break;
				case BACKWARD:
					messageToSend += "B";
					break;
			}

			sendQueue.add(messageToSend);

			waitFor(ACKNOWLEDGE_MESSAGE);

			algorithm.stepSuceeded();

			measureCells();
		}
		else
		{
			sendQueue.add("bye");
		}
	}

}
