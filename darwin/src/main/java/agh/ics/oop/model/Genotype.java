package agh.ics.oop.model;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Genotype {
    private final int[] genes;

    private Genotype(int[] genes) {
        this.genes = genes;
    }

    public static Genotype random(int length, Random random) {
        int[] newGenes = random.ints(length, 0, 8)
                .toArray();
        return new Genotype(newGenes);
    }


    public static Genotype cross(Genotype strong, Genotype weak, float strongRatio, Random random) {
        int genomeLength = strong.genes.length;
        int strongGenesCount = Math.round(genomeLength * strongRatio);

        boolean strongFromLeft = random.nextBoolean();

        Genotype firstParent = strongFromLeft ? strong : weak;
        Genotype secondParent = strongFromLeft ? weak : strong;
        int splitIndex = strongFromLeft ? strongGenesCount : (genomeLength - strongGenesCount);

        int[] childGenes = IntStream.range(0, genomeLength)
                .map(i -> (i < splitIndex) ? firstParent.genes[i] : secondParent.genes[i])
                .toArray();
        return new Genotype(childGenes);
    }

    public void mutate(int minMutations, int maxMutations, Random random) {
        int mutationsCount = minMutations + random.nextInt(maxMutations - minMutations + 1);
        mutationsCount = Math.min(mutationsCount, this.genes.length);

        List<Integer> allIndices = IntStream.range(0, this.genes.length)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(allIndices, random);

        allIndices.stream()
                .limit(mutationsCount)
                .forEach(index -> {
                    int oldValue = this.genes[index];
                    int offset = 1 + random.nextInt(7);
                    this.genes[index] = (oldValue + offset) % 8;
                });
    }

    public int similarity(Genotype other) {
        return (int) IntStream.range(0, this.genes.length)
                .filter(i -> this.genes[i] == other.genes[i])
                .count();
    }

    public int getGene(int index) {
        return genes[index % genes.length];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genotype genotype = (Genotype) o;
        return Arrays.equals(genes, genotype.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }
}