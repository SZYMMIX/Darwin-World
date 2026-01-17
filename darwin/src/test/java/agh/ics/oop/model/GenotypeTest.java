package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class GenotypeTest {

    private Genotype createUniformGenotype(int length, int value) {
        Random fixedRandom = new Random() {
            @Override
            public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
                return IntStream.generate(() -> value).limit(streamSize);
            }
        };
        return Genotype.random(length, fixedRandom);
    }

    @Test
    void getGeneShouldReturnCorrectValuesForNormalIndices() {
        int length = 4;

        Random sequenceRandom = new Random() {
            @Override
            public java.util.stream.IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
                return java.util.stream.IntStream.range(0, (int) streamSize);
            }
        };

        Genotype genotype = Genotype.random(length, sequenceRandom);

        assertEquals(0, genotype.getGene(0));
        assertEquals(1, genotype.getGene(1));
        assertEquals(2, genotype.getGene(2));
        assertEquals(3, genotype.getGene(3));
    }

    @Test
    void getGeneShouldWrapAroundIndex() {
        int length = 3;
        Genotype g = Genotype.random(length, new Random());

        int val0 = g.getGene(0);
        int val1 = g.getGene(1);

        assertEquals(val0, g.getGene(3));
        assertEquals(val1, g.getGene(4));
        assertEquals(val0, g.getGene(300));
    }

    @Test
    void randomShouldCreateGenotypeWithinBounds() {
        int length = 10;
        Random realRandom = new Random();

        Genotype genotype = Genotype.random(length, realRandom);

        for (int i = 0; i < length; i++) {
            int gene = genotype.getGene(i);
            assertTrue(gene >= 0 && gene <= 7);
        }
    }

    @Test
    void crossShouldTakeStrongGenesFromLeftWhenRandomIsTrue() {
        int length = 4;
        Genotype strong = createUniformGenotype(length, 1);
        Genotype weak = createUniformGenotype(length, 0);
        float ratio = 0.75f;

        Random leftSideRandom = new Random() {
            @Override
            public boolean nextBoolean() {
                return true;
            }
        };

        Genotype child = Genotype.cross(strong, weak, ratio, leftSideRandom);

        assertEquals(1, child.getGene(0));
        assertEquals(1, child.getGene(1));
        assertEquals(1, child.getGene(2));
        assertEquals(0, child.getGene(3));
    }

    @Test
    void crossShouldTakeStrongGenesFromRightWhenRandomIsFalse() {
        int length = 4;
        Genotype strong = createUniformGenotype(length, 1);
        Genotype weak = createUniformGenotype(length, 0);
        float ratio = 0.75f;

        Random rightSideRandom = new Random() {
            @Override
            public boolean nextBoolean() {
                return false;
            }
        };

        Genotype child = Genotype.cross(strong, weak, ratio, rightSideRandom);

        assertEquals(0, child.getGene(0));
        assertEquals(1, child.getGene(1));
        assertEquals(1, child.getGene(2));
        assertEquals(1, child.getGene(3));
    }

    @Test
    void mutateShouldModifyExactNumberOfGenesAndPreserveOriginal() {
        int length = 10;
        Genotype original = createUniformGenotype(length, 0);
        int mutationsCount = 3;
        Random random = new Random();

        Genotype mutated = original.mutate(mutationsCount, mutationsCount, random);

        assertEquals(10, original.similarity(createUniformGenotype(length, 0)));

        int differences = 0;
        for (int i = 0; i < length; i++) {
            if (original.getGene(i) != mutated.getGene(i)) {
                differences++;
            }
        }
        assertEquals(mutationsCount, differences);
    }

    @Test
    void mutateShouldEnsureNewValuesAreValid() {
        Genotype genotype = createUniformGenotype(5, 7);
        Random random = new Random();

        Genotype mutated = genotype.mutate(1, 1, random);

        for(int i=0; i<5; i++) {
            int val = mutated.getGene(i);
            assertTrue(val >= 0 && val <= 7);
        }
    }

    @Test
    void similarityShouldReturnCorrectCount() {
        Genotype g1 = createUniformGenotype(5, 1);
        Genotype g2 = createUniformGenotype(5, 1);
        Genotype g3 = createUniformGenotype(5, 2);

        assertEquals(5, g1.similarity(g2));
        assertEquals(0, g1.similarity(g3));
    }


}