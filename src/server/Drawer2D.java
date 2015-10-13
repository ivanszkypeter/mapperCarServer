package server;

import java.util.*;

public class Drawer2D {

    private Set<Field> fields = new HashSet<>();

    public void addField(Field field) {
        fields.add(field);
    }

    public void drawMap() {
        System.out.print("MAP:");
        for (int i = getMaximumY(); i >= getMinimumY(); i--)
        {
            if (i < getMaximumY())
                System.out.print("    ");
            for (int j = getMinimumX(); j <= getMaximumX(); j++)
            {
                Field field = searchFieldAtCoordinate(j,i);
                Character character = 'U';
                if (field != null)
                {
                    Coordinate2D coordinate = (Coordinate2D) field.getCoordinate();
                    switch (field.getStatus())
                    {
                        case OCCUPIED:
                            character = 'O';
                            break;
                        case FREE_VISITED:
                            character = 'V';
                            break;
                        case FREE_NOT_VISITED:
                            character = 'F';
                            break;
                    }
                }
                System.out.print(character);
            }
            System.out.println();
        }
    }

    private Integer getMinimumX() {
        int minimum = 0;
        for(Field field : fields) {
            Coordinate2D coordinate = (Coordinate2D) field.getCoordinate();
            if (coordinate.getX() < minimum)
            {
                minimum = coordinate.getX();
            }
        }
        return minimum;
    }

    private Integer getMaximumX() {
        int maximum = 0;
        for(Field field : fields) {
            Coordinate2D coordinate = (Coordinate2D) field.getCoordinate();
            if (maximum < coordinate.getX())
            {
                maximum = coordinate.getX();
            }
        }
        return maximum;
    }

    private Integer getMinimumY() {
        int minimum = 0;
        for(Field field : fields) {
            Coordinate2D coordinate = (Coordinate2D) field.getCoordinate();
            if (coordinate.getY() < minimum)
            {
                minimum = coordinate.getY();
            }
        }
        return minimum;
    }

    private Integer getMaximumY() {
        int maximum = 0;
        for(Field field : fields) {
            Coordinate2D coordinate = (Coordinate2D) field.getCoordinate();
            if (maximum < coordinate.getY())
            {
                maximum = coordinate.getY();
            }
        }
        return maximum;
    }

    private Field searchFieldAtCoordinate(int x, int y) {
        for (Field field : fields) {
            if (field.getCoordinate().equals(new Coordinate2D(x,y)))
                return field;
        }
        return null;
    }

}
