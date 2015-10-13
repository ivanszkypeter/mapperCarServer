package server;

import server.Field.Direction;

public interface CoordinateInterface <T extends CoordinateInterface> {
	
	public T getNeighborCoordinate(Field.Direction dir);
	
	/**
	 * Get relative direction between two neighbouring coordinates.
	 * If they are not neighbouring, then null is returned.
	 * @param coordinate
	 * @return
	 */
	public Field.Direction getDirectionBeetweenCoordinates(T coordinate);
	
}
