package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void toUnitVectorShouldReturnCorrectVectors() {
        assertEquals(new Vector2d(0, -1), Direction.NORTH.toUnitVector());
        assertEquals(new Vector2d(1, 0), Direction.EAST.toUnitVector());
        assertEquals(new Vector2d(0, 1), Direction.SOUTH.toUnitVector());
        assertEquals(new Vector2d(-1, 0), Direction.WEST.toUnitVector());
        assertEquals(new Vector2d(1, -1), Direction.NORTH_EAST.toUnitVector());
        assertEquals(new Vector2d(1, 1), Direction.SOUTH_EAST.toUnitVector());
        assertEquals(new Vector2d(-1, 1), Direction.SOUTH_WEST.toUnitVector());
        assertEquals(new Vector2d(-1, -1), Direction.NORTH_WEST.toUnitVector());
    }

    @Test
    void shiftShouldRotateCorrectly() {
        assertEquals(Direction.NORTH, Direction.NORTH.shift(0));
        assertEquals(Direction.NORTH_EAST, Direction.NORTH.shift(1));
        assertEquals(Direction.EAST, Direction.NORTH.shift(2));
        assertEquals(Direction.SOUTH, Direction.NORTH.shift(4));
        assertEquals(Direction.NORTH_WEST, Direction.NORTH.shift(7));
    }

    @Test
    void shiftShouldWrapAroundValues() {
        assertEquals(Direction.NORTH_EAST, Direction.NORTH.shift(9));
    }

    @Test
    void randomShouldReturnCorrectDirectionBasedOnRandomInput() {
        Random stubRandomZero = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        Random stubRandomMax = new Random() {
            @Override
            public int nextInt(int bound) {
                return 7;
            }
        };

        assertEquals(Direction.NORTH, Direction.random(stubRandomZero));
        assertEquals(Direction.NORTH_WEST, Direction.random(stubRandomMax));
    }

    @Test
    void randomShouldRequestCorrectBound() {
        Random spyRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                assertEquals(8, bound);
                return 0;
            }
        };
        Direction.random(spyRandom);
    }
}