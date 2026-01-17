package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeAnimalTest {

    private Genotype createPassiveGenotype(int length) {
        Random random = new Random() {
            @Override
            public IntStream ints(long streamSize, int origin, int bound) {
                return IntStream.generate(() -> 0).limit(streamSize);
            }
        };
        return Genotype.random(length, random);
    }

    private RuntimeAnimal createAnimal(int id, int energy, Vector2d pos, Direction direction, int birthDay, Genotype genotype) {
        ChildData childData = new ChildData(pos, energy, genotype, null, null);

        Random directionRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return direction.ordinal();
            }
        };

        return RuntimeAnimal.fromChildData(id, childData, birthDay, directionRandom);
    }

    @Test
    void moveShouldRotateAccordingToGenotype() {
        int currentDay = 10;
        int birthDay = 0;

        Random random = new Random() {
            @Override
            public IntStream ints(long streamSize, int origin, int bound) {
                return IntStream.range(0, (int) streamSize);
            }
        };
        Genotype genotype = Genotype.random(20, random);

        RuntimeAnimal animal = createAnimal(1, 100, new Vector2d(5, 5), Direction.NORTH, birthDay, genotype);

        animal.move(100, 100, currentDay);

        AnimalSnapshot snapshot = animal.getSnapshot();
        assertEquals(Direction.EAST, snapshot.direction());
        assertEquals(new Vector2d(6, 5), snapshot.position());
    }

    @Test
    void moveShouldWrapAroundWidth() {
        int width = 10;
        int height = 10;
        Genotype passiveGenotype = createPassiveGenotype(10);

        RuntimeAnimal animal = createAnimal(1,
                100, new Vector2d(9, 5), Direction.EAST, 0, passiveGenotype);

        animal.move(width, height, 0);

        assertEquals(new Vector2d(0, 5), animal.getPosition());
    }

    @Test
    void moveShouldBounceOfTopAndBottom() {
        int width = 10;
        int height = 10;
        Genotype passiveGenotype = createPassiveGenotype(10);
        RuntimeAnimal animalBottom = createAnimal(1,
                100, new Vector2d(5, 9), Direction.SOUTH, 0, passiveGenotype);

        animalBottom.move(width, height, 0);

        assertEquals(new Vector2d(5, 9), animalBottom.getPosition());
        assertEquals(Direction.NORTH, animalBottom.getSnapshot().direction());

        RuntimeAnimal animalTop = createAnimal(2,
                100, new Vector2d(5, 0), Direction.NORTH, 0, passiveGenotype);

        animalTop.move(width, height, 0);

        assertEquals(new Vector2d(5, 0), animalTop.getPosition());
        assertEquals(Direction.SOUTH, animalTop.getSnapshot().direction());
    }

    @Test
    void reproduceShouldFailIfParentsHaveLowEnergy() {
        int minEnergy = 20;
        Genotype g = createPassiveGenotype(5);
        RuntimeAnimal p1 = createAnimal(1, 10, new Vector2d(0,0), Direction.NORTH, 0, g);
        RuntimeAnimal p2 = createAnimal(2, 50, new Vector2d(0,0), Direction.NORTH, 0, g);

        ChildData child = p1.reproduce(p2, minEnergy, 10, 0, 0, new Random());

        assertNull(child);
    }

    @Test
    void reproduceShouldCreateCorrectChildAndReduceEnergy() {
        int cost = 10;
        int minEnergy = 20;
        Genotype g = createPassiveGenotype(5);

        RuntimeAnimal p1 = createAnimal(1, 50, new Vector2d(2,2), Direction.NORTH, 0, g);
        RuntimeAnimal p2 = createAnimal(2, 50, new Vector2d(2,2), Direction.SOUTH, 0, g);

        Random random = new Random();

        ChildData child = p1.reproduce(p2, minEnergy, cost, 0, 0, random);

        assertNotNull(child);
        assertEquals(40, p1.getEnergy());
        assertEquals(40, p2.getEnergy());
        assertEquals(2 * cost, child.initialEnergy());

        assertEquals(1, p1.getChildrenCount());
        assertEquals(1, p2.getChildrenCount());

        assertEquals(1, child.parentAId());
        assertEquals(2, child.parentBId());
        assertEquals(new Vector2d(2,2), child.position());
    }

    @Test
    void getSortedGroupsShouldRespectHierarchy() {
        Genotype g = createPassiveGenotype(5);
        Vector2d pos = new Vector2d(1, 1);

        RuntimeAnimal a1 = createAnimal(1, 100, pos, Direction.NORTH, 10, g);
        RuntimeAnimal a2 = createAnimal(2, 90, pos, Direction.NORTH, 10, g);
        RuntimeAnimal a3 = createAnimal(3, 100, pos, Direction.NORTH, 5, g);

        RuntimeAnimal a4 = createAnimal(4, 120, pos, Direction.NORTH, 5, g);
        RuntimeAnimal dummy = createAnimal(99, 50, pos, Direction.NORTH, 0, g);
        a4.reproduce(dummy, 10, 10, 0, 0, new Random());
        a4.subtractEnergy(10);

        List<RuntimeAnimal> list = Arrays.asList(a1, a2, a3, a4);

        Map<Vector2d, List<RuntimeAnimal>> groups = RuntimeAnimal.getSortedGroups(list);
        List<RuntimeAnimal> sortedGroup = groups.get(pos);


        assertEquals(4, sortedGroup.get(0).getId());
        assertEquals(3, sortedGroup.get(1).getId());
        assertEquals(1, sortedGroup.get(2).getId());
        assertEquals(2, sortedGroup.get(3).getId());
    }

    @Test
    void eatShouldIncreaseEnergy() {
        int initialEnergy = 50;
        int plantEnergy = 20;
        RuntimeAnimal animal = createAnimal(1,
                initialEnergy, new Vector2d(0,0), Direction.NORTH, 0, createPassiveGenotype(1));

        animal.eat(plantEnergy);

        assertEquals(initialEnergy + plantEnergy, animal.getEnergy());
    }

    @Test
    void subtractEnergyShouldDecreaseEnergy() {
        int initialEnergy = 50;
        int cost = 15;
        RuntimeAnimal animal = createAnimal(1,
                initialEnergy, new Vector2d(0,0), Direction.NORTH, 0, createPassiveGenotype(1));

        animal.subtractEnergy(cost);

        assertEquals(initialEnergy - cost, animal.getEnergy());
    }

    @Test
    void isDeadShouldReturnTrueWhenEnergyIsZeroOrLess() {
        RuntimeAnimal animalZero = createAnimal(1,
                0, new Vector2d(0,0), Direction.NORTH, 0, createPassiveGenotype(1));
        RuntimeAnimal animalNegative = createAnimal(2,
                -5, new Vector2d(0,0), Direction.NORTH, 0, createPassiveGenotype(1));
        RuntimeAnimal animalPositive = createAnimal(3,
                1, new Vector2d(0,0), Direction.NORTH, 0, createPassiveGenotype(1));

        assertTrue(animalZero.isDead());
        assertTrue(animalNegative.isDead());
        assertFalse(animalPositive.isDead());
    }

    @Test
    void fromChildDataShouldCorrectlyInitializeAnimal() {
        int id = 123;
        int birthDay = 5;
        int initialEnergy = 100;
        int parentA = 10;
        int parentB = 20;
        Vector2d pos = new Vector2d(10, 20);

        Genotype genotype = Genotype.random(3, new Random());

        ChildData childData = new ChildData(pos, initialEnergy, genotype, parentA, parentB);

        Random directionRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 2;
            }
        };

        RuntimeAnimal animal = RuntimeAnimal.fromChildData(id, childData, birthDay, directionRandom);

        assertEquals(id, animal.getId());
        assertEquals(initialEnergy, animal.getEnergy());
        assertEquals(pos, animal.getPosition());
        assertEquals(0, animal.getChildrenCount());

        assertEquals(Direction.EAST, animal.getSnapshot().direction());

        AnimalDetails details = animal.getDetails();
        assertEquals(birthDay, details.birthDay());
        assertEquals(parentA, details.parentAId());
        assertEquals(parentB, details.parentBId());

        assertEquals(genotype, details.genotype());
    }
}