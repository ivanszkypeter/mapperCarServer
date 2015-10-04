package server;

import java.util.Map;

public class Main {

	public static void main(String[] args) {
		Communication communication = new Communication();
		Server server = new Server(communication);
	}

}
