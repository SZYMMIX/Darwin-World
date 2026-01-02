package agh.ics.oop.model;

import java.util.Random;

public class Genotype {
    private final int[] genes;

    private Genotype(int[] genes) {
        this.genes = genes;
    }

    public static Genotype random(int length, Random random) {
        int[] newGenes = new int[length];
        // TODO: Fill newGenes with random values [0-7]
        return new Genotype(newGenes);
    }

    public static Genotype cross(Genotype first, Genotype second, float ratio, Random random) {
        // TODO: Implement crossover logic:
        // Every gene form first animal has ratio chance to be copied
        // Otherwise the gene from the second animal is copied
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void mutate(int genesCount, Random random) {
        // TODO: Randomly change 'genesCount' indices to new values [0-7]
    }

    public int similarity(Genotype other) {
        // TODO: Calculate how many genes are identical at the same indices
        return 0;
    }

    public int getGene(int index) {
        return genes[index % genes.length];
    }
}