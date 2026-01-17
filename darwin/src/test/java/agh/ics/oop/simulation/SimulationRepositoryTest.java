package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SimulationRepositoryTest {

    private Genotype createGenotype(int val) {
        Random random = new Random() {
            @Override
            public IntStream ints(long size, int origin, int bound) {
                return IntStream.generate(() -> val).limit(size);
            }
        };
        return Genotype.random(1, random);
    }

    private ChildData createChildData(Genotype g, Integer pA, Integer pB) {
        return new ChildData(new Vector2d(0, 0), 10, g, pA, pB);
    }

    @Test
    void registerBirthShouldAssignIdsAndStoreDetails() {
        SimulationRepository repo = new SimulationRepository();
        Genotype genotype = createGenotype(0);
        ChildData data = createChildData(genotype, null, null);
        Random random = new Random();

        RuntimeAnimal animal1 = repo.registerBirth(data, 0, random);
        RuntimeAnimal animal2 = repo.registerBirth(data, 5, random);

        assertEquals(0, animal1.getId());
        assertEquals(1, animal2.getId());

        Optional<AnimalDetails> details1 = repo.getDetails(0);
        Optional<AnimalDetails> details2 = repo.getDetails(1);

        assertTrue(details1.isPresent());
        assertEquals(0, details1.get().birthDay());
        assertEquals(5, details2.get().birthDay());
    }

    @Test
    void registerBirthShouldUpdateParentChildrenLists() {
        SimulationRepository repo = new SimulationRepository();
        Genotype genotype = createGenotype(0);
        Random random = new Random();

        RuntimeAnimal p1 = repo.registerBirth(createChildData(genotype, null, null), 0, random);
        RuntimeAnimal p2 = repo.registerBirth(createChildData(genotype, null, null), 0, random);

        ChildData childData = createChildData(genotype, p1.getId(), p2.getId());

        RuntimeAnimal child = repo.registerBirth(childData, 10, random);

        assertNotNull(child);
    }

    @Test
    void registerBirthShouldTrackGenotypePopularity() {
        SimulationRepository repo = new SimulationRepository();
        Genotype gA = createGenotype(1);
        Genotype gB = createGenotype(2);
        Random random = new Random();

        repo.registerBirth(createChildData(gA, null, null), 0, random);
        repo.registerBirth(createChildData(gA, null, null), 0, random);
        repo.registerBirth(createChildData(gA, null, null), 0, random);
        repo.registerBirth(createChildData(gB, null, null), 0, random);

        SimulationStats stats = repo.getStats(Collections.emptySet(), Collections.emptySet(), 10, 10);

        List<GenotypeStat> top = stats.topGenotypes();
        assertEquals(2, top.size());

        GenotypeStat first = top.get(0);
        assertEquals(gA, first.genotype());
        assertEquals(3, first.animalIds().size());

        GenotypeStat second = top.get(1);
        assertEquals(gB, second.genotype());
        assertEquals(1, second.animalIds().size());
    }

    @Test
    void registerDeathShouldUpdateLifespanStatsAndRemoveFromGenotypeFollowers() {
        SimulationRepository repo = new SimulationRepository();
        Genotype genotype = createGenotype(0);
        Random random = new Random();

        RuntimeAnimal a1 = repo.registerBirth(createChildData(genotype, null, null), 0, random);
        RuntimeAnimal a2 = repo.registerBirth(createChildData(genotype, null, null), 10, random);

        repo.registerDeath(0, 20);

        SimulationStats stats1 = repo.getStats(Collections.emptySet(), Collections.emptySet(), 10, 10);
        assertEquals(20.0, stats1.averageLifespan(), 0.001);

        repo.registerDeath(1, 40);

        SimulationStats stats2 = repo.getStats(Collections.emptySet(), Collections.emptySet(), 10, 10);
        assertEquals(25.0, stats2.averageLifespan(), 0.001);

        assertTrue(stats2.topGenotypes().isEmpty());
    }

    @Test
    void getStatsShouldCalculateFreeFieldsCorrectly() {
        SimulationRepository repo = new SimulationRepository();
        int width = 5;
        int height = 5;
        Random random = new Random();

        Set<Vector2d> plants = Set.of(new Vector2d(0, 0), new Vector2d(1, 1));

        ChildData d1 = new ChildData(new Vector2d(1, 1), 10, createGenotype(0), null, null);
        RuntimeAnimal a1 = repo.registerBirth(d1, 0, random);

        ChildData d2 = new ChildData(new Vector2d(2, 2), 10, createGenotype(0), null, null);
        RuntimeAnimal a2 = repo.registerBirth(d2, 0, random);

        Set<RuntimeAnimal> liveAnimals = Set.of(a1, a2);

        SimulationStats stats = repo.getStats(liveAnimals, plants, width, height);

        assertEquals(22, stats.freeFieldsCount());
        assertEquals(2, stats.currentAnimalsCount());
        assertEquals(2, stats.currentPlantsCount());
    }

    @Test
    void getStatsShouldCalculateAvgEnergyAndChildren() {
        SimulationRepository repo = new SimulationRepository();
        Random r = new Random();

        ChildData d1 = new ChildData(new Vector2d(0,0),
                100, createGenotype(0), null, null);

        RuntimeAnimal a1 = RuntimeAnimal.fromChildData(1, d1, 0, r);

        ChildData d2 = new ChildData(new Vector2d(0,0),
                100, createGenotype(0), null, null);

        RuntimeAnimal a2 = RuntimeAnimal.fromChildData(2, d2, 0, r);
        RuntimeAnimal partner = RuntimeAnimal.fromChildData(3, d2, 0, r);

        a2.subtractEnergy(50);
        a2.reproduce(partner, 10, 0, 0, 0, r);
        a2.reproduce(partner, 10, 0, 0, 0, r);

        Set<RuntimeAnimal> liveAnimals = Set.of(a1, a2);

        SimulationStats stats = repo.getStats(liveAnimals, Collections.emptySet(), 10, 10);

        assertEquals(75.0, stats.averageEnergy(), 0.001);
        assertEquals(1.0, stats.averageChildren(), 0.001);
    }

    @Test
    void getDetailsShouldReturnEmptyForInvalidId() {
        SimulationRepository repo = new SimulationRepository();
        assertTrue(repo.getDetails(0).isEmpty());
        assertTrue(repo.getDetails(-1).isEmpty());
    }
}
