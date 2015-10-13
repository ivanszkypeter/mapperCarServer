package server;

import server.Field.Direction;

public class Coordinate2D implements CoordinateInterface<Coordinate2D> {

	private int x;
	private int y;

	Coordinate2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Coordinate2D getNeighborCoordinate(Direction dir) {

		switch (dir) {
		case LEFT:
			return new Coordinate2D(x - 1, y);
		case RIGHT:
			return new Coordinate2D(x + 1, y);
		case FORWARD:
			return new Coordinate2D(x, y + 1);
		case BACKWARD:
			return new Coordinate2D(x, y - 1);
		}

		return null;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate2D other = (Coordinate2D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public Direction getDirectionBeetweenCoordinates(Coordinate2D coordinate) {

		int diffX = x - ((Coordinate2D) coordinate).getX();
		int diffY = y - ((Coordinate2D) coordinate).getY();

		if (diffX == 1 && diffY == 0) {
			return Field.Direction.LEFT;
		}
		else if (diffX == -1 && diffY == 0) {
			return Field.Direction.RIGHT;
		}
		else if (diffX == 0 && diffY == 1) {
			return Field.Direction.BACKWARD;
		}
		else if (diffX == 0 && diffY == -1) {
			return Field.Direction.FORWARD;
		}
		return null;
	}
}
