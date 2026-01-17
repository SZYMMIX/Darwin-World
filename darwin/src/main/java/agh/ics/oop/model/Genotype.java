package agh.ics.oop.model;


import java.util.Arrays;
import java.util.Random;

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
        int weakGenesCount = genomeLength - strongGenesCount;

        int[] childGenes = new int[genomeLength];
        boolean strongFromLeft = random.nextBoolean();

        if (strongFromLeft) {
            System.arraycopy(strong.genes, 0, childGenes, 0, strongGenesCount);
            System.arraycopy(weak.genes, strongGenesCount, childGenes, strongGenesCount, weakGenesCount);
        } else {
            System.arraycopy(weak.genes, 0, childGenes, 0, weakGenesCount);
            System.arraycopy(strong.genes, weakGenesCount, childGenes, weakGenesCount, strongGenesCount);
        }

        return new Genotype(childGenes);
    }

    public void mutate(int minMutations, int maxMutations, Random random) {
        int mutationsCount = minMutations + random.nextInt(maxMutations - minMutations + 1);

        mutationsCount = Math.min(mutationsCount, this.genes.length);

        int[] indices = new int[this.genes.length];
        for (int i = 0; i < this.genes.length; i++) {
            indices[i] = i;
        }

        int currentPoolSize = this.genes.length;

        for (int k = 0; k < mutationsCount; k++) {
            int randomPoolIndex = random.nextInt(currentPoolSize);

            int geneIndexToMutate = indices[randomPoolIndex];

            int oldValue = this.genes[geneIndexToMutate];
            int offset = 1 + random.nextInt(7); // 1..7
            this.genes[geneIndexToMutate] = (oldValue + offset) % 8;

            indices[randomPoolIndex] = indices[currentPoolSize - 1];

            currentPoolSize--;
        }
    }

    public int similarity(Genotype other) {
        int sameCount = 0;
        for (int i = 0; i < this.genes.length; i++) {
            if (this.genes[i] == other.genes[i]) {
                sameCount++;
            }
        }
        return sameCount;
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