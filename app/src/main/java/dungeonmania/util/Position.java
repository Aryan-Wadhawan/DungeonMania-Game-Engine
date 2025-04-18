package dungeonmania.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DO NOT CHANGE THIS FILE
 */
public final class Position {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private final int x;
    private final int y;
    private final int layer; // Used only by the frontend, irrelevant for the backend

    public Position(int x, int y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.layer = 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(x, y);
    }

    // Note we do al equality checks without z since we won't test backends with it
    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;

        return x == other.x && y == other.y;
    }

    public int magnitude() {
        return (int) Math.abs(x) + Math.abs(y);
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getLayer() {
        return layer;
    }

    public final Position asLayer(int layer) {
        return new Position(x, y, layer);
    }

    public static final Position translateBy(Position old, int deltaX, int deltaY) {
        return translateBy(old, new Position(deltaX, deltaY));
    }

    public static final Position translateBy(Position old, Direction direction) {
        return translateBy(old, direction.getOffset());
    }

    public static final Position translateBy(Position old, Position delta) {
        return new Position(old.x + delta.x, old.y + delta.y, old.layer + delta.layer);
    }

    // (Note: doesn't include z)

    /**
     * Calculates the position vector of b relative to a (ie. the direction from a
     * to b)
     *
     * @return The relative position vector
     */

    public static final Position calculatePositionBetween(Position a, Position b) {
        return new Position(b.x - a.x, b.y - a.y);
    }

    public static final boolean isAdjacent(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) == 1;
    }

    @Override
    public final String toString() {
        return "Position [x=" + x + ", y=" + y + ", z=" + layer + "]";
    }

    // Return Adjacent positions in an array list with the following element
    // positions:
    // 0 1 2
    // 7 p 3
    // 6 5 4
    public List<Position> getAdjacentPositions() {
        List<Position> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Position(x - 1, y - 1));
        adjacentPositions.add(new Position(x, y - 1));
        adjacentPositions.add(new Position(x + 1, y - 1));
        adjacentPositions.add(new Position(x + 1, y));
        adjacentPositions.add(new Position(x + 1, y + 1));
        adjacentPositions.add(new Position(x, y + 1));
        adjacentPositions.add(new Position(x - 1, y + 1));
        adjacentPositions.add(new Position(x - 1, y));
        return adjacentPositions;
    }

    /**
     * Get cardinally adjacent positions only
     *
     * @return
     */
    public List<Position> getCardinallyAdjacentPositions() {
        List<Position> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Position(x, y - 1));
        adjacentPositions.add(new Position(x + 1, y));
        adjacentPositions.add(new Position(x, y + 1));
        adjacentPositions.add(new Position(x - 1, y));
        return adjacentPositions;
    }

    /**
    * Checks whether two positions are within a specified square-shaped radius of each other.
    * <p>
    * This method considers both cardinal and diagonal directions. It returns true if the
    * horizontal and vertical distances between position {@code a} and position {@code b} 
    * are each less than or equal to the specified {@code radius}.
    * </p>
    *
    * @param a The first position.
    * @param b The second position.
    * @param radius The maximum allowed distance in both x and y directions.
    * @return {@code true} if {@code a} and {@code b} are within the radius, {@code false} otherwise.
    */
    public static boolean withinRadius(Position a, Position b, int radius) {
        return Math.abs(a.getX() - b.getX()) <= radius && Math.abs(a.getY() - b.getY()) <= radius;
    }

}
