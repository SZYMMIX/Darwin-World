package agh.ics.oop.model;

import java.util.Random;

public enum Direction {
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    private final Vector2d unitVector;

    Direction(int x, int y) {
        this.unitVector = new Vector2d(x, y);
    }

    public Vector2d toUnitVector() {
        return unitVector;
    }

    public Direction shift(int steps) {
        return values()[(this.ordinal() + steps) % values().length];
    }

    public static Direction random(Random random) {
        return values()[random.nextInt(values().length)];
    }
}