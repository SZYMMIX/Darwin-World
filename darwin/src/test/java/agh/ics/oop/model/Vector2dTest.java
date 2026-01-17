package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {

    @Test
    void addShouldSumTwoVectorsCorrectly() {
        Vector2d vector1 = new Vector2d(5, 2);
        Vector2d vector2 = new Vector2d(5, 2);

        Vector2d vector3 = vector1.add(vector2);

        assertEquals(new Vector2d(10, 4), vector3);
    }

    @Test
    void addShouldBeACommutativeOperation() {
        Vector2d vector1 = new Vector2d(5, 2);
        Vector2d vector2 = new Vector2d(5, 2);

        assertEquals(vector2.add(vector1), vector1.add(vector2));
    }

    @Test
    void addShouldSumCorrectlyWithNeutralElement() {
        Vector2d vector1 = new Vector2d(5, 2);
        Vector2d vector2 = new Vector2d(0, 0);

        assertEquals(vector1, vector1.add(vector2));
    }

    @Test
    void addShouldSumNegativeVectorsCorrectly() {
        Vector2d vector1 = new Vector2d(1, 2);
        Vector2d vector2 = new Vector2d(-5, -4);

        Vector2d vector3 = vector1.add(vector2);

        assertEquals(new Vector2d(-4, -2), vector3);
    }
}