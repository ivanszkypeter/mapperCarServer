package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import server.Field.FieldType;

public class Algorithm {

	final Field center = new Field(new Coordinate2D(0, 0));
	private Field currentCarPosition = null;
	private Field candidateCarPosition = null;

	private Set<Field> visitedField = new TreeSet<>();
	private Stack<Field> routeFromCenter =  new Stack<>();

	private Drawer2D drawer = new Drawer2D();

	public Algorithm() {
		currentCarPosition = center;
		currentCarPosition.setStatus(FieldType.FREE_VISITED);
		drawer.addField(currentCarPosition);
	}

	public Field.Direction getNextField() {
		// return Field.Direction.RIGHT;
		for (Entry<Field.Direction, Field> entry : currentCarPosition.getNeighbors().entrySet()) {
			if (entry.getValue().getStatus().equals(Field.FieldType.FREE_NOT_VISITED)) {
				routeFromCenter.push(currentCarPosition);
				candidateCarPosition = entry.getValue();
				return entry.getKey();
			}
		}

		// if stucked
		Field f = routeFromCenter.pop();
		candidateCarPosition = f;
		return currentCarPosition.getCoordinate().getDirectionBeetweenCoordinates(f.getCoordinate());
	}

	public Map<Field.Direction, Boolean> whichFieldsToMeasure() {

		Map<Field.Direction, Boolean> map = new HashMap<Field.Direction, Boolean>();
			
		if (currentCarPosition.getNeighbor(Field.Direction.LEFT) == null)
			map.put(Field.Direction.LEFT, true);
		else
			map.put(Field.Direction.LEFT, false);
		
		if (currentCarPosition.getNeighbor(Field.Direction.RIGHT) == null)
			map.put(Field.Direction.RIGHT, true);
		else
			map.put(Field.Direction.RIGHT, false);
		
		if (currentCarPosition.getNeighbor(Field.Direction.FORWARD) == null)
			map.put(Field.Direction.FORWARD, true);
		else
			map.put(Field.Direction.FORWARD, false);
		
		if (currentCarPosition.getNeighbor(Field.Direction.BACKWARD) == null)
			map.put(Field.Direction.BACKWARD, true);
		else
			map.put(Field.Direction.BACKWARD, false);

		return map;
	}

	/**
	 * @param cellInfoMap:
	 *            value of the map is boolean type: true: cell is free, false -
	 *            cell is occupied
	 */
	public void cellInfoReceived(Map<Field.Direction, Field.FieldType> cellInfoMap) {
		System.out.println("cell info received");
		visitedField.add(currentCarPosition);
		currentCarPosition.setStatus(Field.FieldType.FREE_VISITED);
		for (Entry<Field.Direction, Field.FieldType> directionIsOccupied : cellInfoMap.entrySet()) {
			if (!directionIsOccupied.getValue().equals(Field.FieldType.UNKNOWN)) {
				CoordinateInterface newCoordinate = currentCarPosition.getCoordinate()
						.getNeighborCoordinate(directionIsOccupied.getKey());
				Field newField = searchFieldAtCoordinate(newCoordinate);
				if (newField == null) {
					newField = new Field(newCoordinate);
					newField.setStatus(directionIsOccupied.getValue());
					drawer.addField(newField);
				}
				newField.setNeighbor(getOppositeDirection(directionIsOccupied.getKey()), currentCarPosition);
				currentCarPosition.setNeighbor(directionIsOccupied.getKey(), newField);
			}
		}
		drawer.drawMap();
	}

	public Field.Direction getOppositeDirection(Field.Direction dir) {
		switch (dir) {
		case LEFT:
			return Field.Direction.RIGHT;
		case RIGHT:
			return Field.Direction.LEFT;
		case BACKWARD:
			return Field.Direction.FORWARD;
		case FORWARD:
			return Field.Direction.BACKWARD;
		}

		return null;

	}

	public Field searchFieldAtCoordinate(CoordinateInterface cord) {
		for (Field field : visitedField) {
			if (field.getCoordinate().equals(cord))
				return field;
		}
		return null;
	}

	public void stepSuceeded() {
		currentCarPosition = candidateCarPosition;
		
	}
}
