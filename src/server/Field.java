package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import server.Field.Direction;

public class Field implements Comparable {

	enum Direction {
		LEFT, RIGHT, FORWARD, BACKWARD
	};

	enum FieldType {
		UNKNOWN, FREE_NOT_VISITED, FREE_VISITED, OCCUPIED
	};

	private CoordinateInterface coordinate;

	private FieldType status = FieldType.UNKNOWN;

	private Map<Direction, Field> neighbors = new HashMap<>();

	public Field(CoordinateInterface coordinate) {
		this.coordinate = coordinate;
	}

	public void setStatus(FieldType status) {
		this.status = status;
	}

	public FieldType getStatus() {
		return this.status;
	}

	public void setNeighbor(Direction direction, Field field) {
		neighbors.put(direction, field);
	}

	public Field getNeighbor(Direction direction) {
		return neighbors.get(direction);
	}

	public Map<Direction, Field> getNeighbors() {
		return neighbors;
	}

	public CoordinateInterface getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(CoordinateInterface coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public boolean equals(Object obj) {
		return this.coordinate.equals(((Field) obj).getCoordinate());
	}

	@Override
	public int compareTo(Object arg0) {
		return this.equals(arg0)? 0 : 1;
	}

}
