package agh.ics.oop.simulation;

import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ChildDataTest {

    @Test
    void randomShouldGenerateChildWithinMapBounds() {
        int width = 100;
        int height = 50;
        int initialEnergy = 20;
        int genotypeLength = 5;

        Random boundsRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                if (bound == width) return width - 1;
                if (bound == height) return height - 1;
                return 0;
            }

            @Override
            public IntStream ints(long streamSize, int origin, int bound) {
                return IntStream.generate(() -> 0).limit(streamSize);
            }
        };

        ChildData child = ChildData.random(width, height, initialEnergy, genotypeLength, boundsRandom);

        assertEquals(new Vector2d(99, 49), child.position());
        assertEquals(initialEnergy, child.initialEnergy());
        assertNull(child.parentAId());
        assertNull(child.parentBId());
        assertNotNull(child.genotype());
    }

    @Test
    void randomShouldUseCorrectParametersForGenotype() {
        int expectedLength = 10;
        Random random = new Random();

        ChildData child = ChildData.random(10, 10, 50, expectedLength, random);

        assertDoesNotThrow(() -> child.genotype().getGene(expectedLength - 1));
    }
}