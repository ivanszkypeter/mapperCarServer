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
	
	private final int SLEEP_TIME = 500;

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

	private void handshaking() {
		if (isMessage(INITIAL_MESSAGE)) {
			sendQueue.add("hi");
			setCellSize();
		} else {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handshaking();
		}
	}

	private void setCellSize() {
		sendQueue.add("cell_size:" + cellSize);

		if (isMessage(ACKNOWLEDGE_MESSAGE)) {
			measureCells();
		} else {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setCellSize();
		}
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

		getCellInfo();
	}

	private void getCellInfo() {
		String message = isMessageStartingWith("cell_info:");
		if (message == null) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getCellInfo();
		} else {
			String[] array = message.split(":");
			Map<Field.Direction, Field.FieldType> directionsToMeasure = new HashMap<Field.Direction, Field.FieldType>();
			directionsToMeasure.put(Field.Direction.LEFT, Field.FieldType.UNKNOWN);
			directionsToMeasure.put(Field.Direction.RIGHT, Field.FieldType.UNKNOWN);
			directionsToMeasure.put(Field.Direction.FORWARD, Field.FieldType.UNKNOWN);
			directionsToMeasure.put(Field.Direction.BACKWARD, Field.FieldType.UNKNOWN);
			if (array[1].substring(1,2).equals("f")) {
				directionsToMeasure.put(Field.Direction.LEFT, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(1,2).equals("o")) {
				directionsToMeasure.put(Field.Direction.LEFT, Field.FieldType.OCCUPIED);
			}

			if (array[1].substring(3,4).equals("f")) {
				directionsToMeasure.put(Field.Direction.RIGHT, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(3,4).equals("o")) {
				directionsToMeasure.put(Field.Direction.RIGHT, Field.FieldType.OCCUPIED);
			}

			if (array[1].substring(5,6).equals("f")) {
				directionsToMeasure.put(Field.Direction.FORWARD, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(5,6).equals("o")) {
				directionsToMeasure.put(Field.Direction.FORWARD, Field.FieldType.OCCUPIED);
			}

			if (array[1].substring(7,8).equals("f")) {
				directionsToMeasure.put(Field.Direction.BACKWARD, Field.FieldType.FREE_NOT_VISITED);
			} else if (array[1].substring(7,8).equals("o")) {
				directionsToMeasure.put(Field.Direction.BACKWARD, Field.FieldType.OCCUPIED);
			}

			algorithm.cellInfoReceived(directionsToMeasure);
			
			goToCells();			
		}
	}
	
	private void goToCells(){
		Field.Direction dir = algorithm.getNextField();
		
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
		
		while(!isMessage(ACKNOWLEDGE_MESSAGE)){
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// if ack message received:
		algorithm.stepSuceeded();
		
		measureCells();
	}

}
