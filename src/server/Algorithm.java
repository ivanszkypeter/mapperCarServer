package server;

import java.util.HashMap;
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

	private Set<Field> visitedFields = new TreeSet<>();
	private Stack<Field> routeFromCenter =  new Stack<>();

	private Drawer2D drawer = new Drawer2D();

	public Algorithm() {
		currentCarPosition = center;
		currentCarPosition.setStatus(FieldType.FREE_VISITED);
		drawer.addField(currentCarPosition);
	}

	public Field getCurrentCarPosition() {
		return currentCarPosition;
	}

	public Field.Direction getNextField() {
		for (Entry<Field.Direction, Field> entry : currentCarPosition.getNeighbors().entrySet()) {
			if (entry.getValue().getStatus().equals(Field.FieldType.FREE_NOT_VISITED)) {
				routeFromCenter.push(currentCarPosition);
				candidateCarPosition = entry.getValue();
				return entry.getKey();
			}
		}

		// if stucked
		if (routeFromCenter.size() > 0)
		{
			Field f = routeFromCenter.pop();
			candidateCarPosition = f;
			return currentCarPosition.getCoordinate().getDirectionBeetweenCoordinates(f.getCoordinate());
		}
		return null;
	}

	public void setExistingNeighbors() {
		Coordinate2D carCoordinate = (Coordinate2D)currentCarPosition.getCoordinate();
		for (Field field : visitedFields) {
			for (Entry<Field.Direction, Field> neighbourField : field.getNeighbors().entrySet())
			{
				Coordinate2D neighbourFieldCoordinate = (Coordinate2D) neighbourField.getValue().getCoordinate();
				if (neighbourFieldCoordinate.getX() == carCoordinate.getX()+1
						&& neighbourFieldCoordinate.getY() == carCoordinate.getY())
				{
					currentCarPosition.setNeighbor(Field.Direction.RIGHT, neighbourField.getValue());
					neighbourField.getValue().setNeighbor(Field.Direction.LEFT, currentCarPosition);
				}

				if (neighbourFieldCoordinate.getX() == carCoordinate.getX()-1
						&& neighbourFieldCoordinate.getY() == carCoordinate.getY())
				{
					currentCarPosition.setNeighbor(Field.Direction.LEFT, neighbourField.getValue());
					neighbourField.getValue().setNeighbor(Field.Direction.RIGHT, currentCarPosition);
				}

				if (neighbourFieldCoordinate.getX() == carCoordinate.getX()
						&& neighbourFieldCoordinate.getY() == carCoordinate.getY()+1)
				{
					currentCarPosition.setNeighbor(Field.Direction.FORWARD, neighbourField.getValue());
					neighbourField.getValue().setNeighbor(Field.Direction.BACKWARD, currentCarPosition);
				}

				if (neighbourFieldCoordinate.getX() == carCoordinate.getX()
						&& neighbourFieldCoordinate.getY() == carCoordinate.getY()-1)
				{
					currentCarPosition.setNeighbor(Field.Direction.BACKWARD, neighbourField.getValue());
					neighbourField.getValue().setNeighbor(Field.Direction.FORWARD, currentCarPosition);
				}
			}
		}
	}

	public Map<Field.Direction, Boolean> whichFieldsToMeasure() {
		setExistingNeighbors();
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
		visitedFields.add(currentCarPosition);
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
		drawer.setCarPosition(currentCarPosition);
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
		for (Field field : visitedFields) {
			if (field.getCoordinate().equals(cord))
				return field;
		}
		return null;
	}

	public void stepSuceeded() {
		currentCarPosition = candidateCarPosition;
	}
}
