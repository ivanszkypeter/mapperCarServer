package server;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Communication implements SerialPortEventListener, Runnable {

	SerialPort serialPort;

	private ConcurrentLinkedQueue<String> receiveQueue;
	private ConcurrentLinkedQueue<String> sendQueue;

	public Communication(ConcurrentLinkedQueue receiveQueue, ConcurrentLinkedQueue sendQueue) {
		/*
		 * Thread t=new Thread() { public void run() { //the following line will
		 * keep this app alive for 1000 seconds, //waiting for events to occur
		 * and responding to them (printing incoming messages to console). try
		 * {Thread.sleep(1000000);} catch (InterruptedException ie) {} } };
		 * t.start();
		 */

		this.receiveQueue = receiveQueue;
		this.sendQueue = sendQueue;
	}

	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM9", // Windows
			//"COM12", // Windows
			"COM20",
	};

	@Override
	public void run() {
		initialize();

		while (true) {
			String messageToSend;
			while ((messageToSend = sendQueue.poll()) != null) {
				sendMessage(messageToSend);
			}
			
			try {
				Thread.currentThread().sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// TODO programból kultúrált kilépés

	}

	private void sendMessage(String msg) {
		try {
			output.write(msg);
			output.flush();
			
			System.out.println("message to send: "+msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A BufferedReader which will be fed by a InputStreamReader converting the
	 * bytes into characters making the displayed results codepage independent
	 */
	private BufferedReader input;
	/** The output stream to the port */
	private BufferedWriter output;

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	private void initialize() {
		// the next line is for Raspberry Pi and
		// gets us into the while loop and was suggested here was suggested
		// http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
		// System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				receiveQueue.add(inputLine);
				
				System.out.println("message received: " + inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

}
