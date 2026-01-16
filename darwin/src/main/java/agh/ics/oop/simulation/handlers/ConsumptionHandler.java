package agh.ics.oop.simulation.handlers;

import java.util.*;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

public class ConsumptionHandler {
    private final int plantEnergy;
    private final int poisonEnergyCost;
    private final Optional<Genotype> immunityGenotype;
    private final Random random;
    private final int genomeLength;

    private final List<Animal> animals;
    private final Map<Vector2d, Plant> plants;
    private final Comparator<Animal> conflictResolver;

    public ConsumptionHandler(SimulationParameters parameters, List<Animal> animals, Map<Vector2d, Plant> plants, Random random) {
        this.plantEnergy = parameters.plantEnergy();
        this.poisonEnergyCost = parameters.poisonEnergyCost();
        this.genomeLength = parameters.genomeLength();

        if (parameters.isPoisonMap()) {
            this.immunityGenotype = Optional.of(Genotype.random(genomeLength, random));
        } else {
            this.immunityGenotype = Optional.empty();
        }
        this.animals = animals;
        this.plants = plants;
        this.random = random;

        this.conflictResolver = Comparator.comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getBirthDay)
                .thenComparingInt(Animal::getChildrenCount).reversed()
                .thenComparing(x -> random.nextDouble());
    }

    public void handle() {
        Map<Vector2d, List<Animal>> animalsByPosition = new HashMap<>();
        for (Animal animal : animals) {
            animalsByPosition.computeIfAbsent(animal.getPosition(), k -> new ArrayList<>()).add(animal);
        }

        for (Vector2d plantPosition : new HashSet<>(plants.keySet())) {
            if (animalsByPosition.containsKey(plantPosition)) {
                List<Animal> candidates = animalsByPosition.get(plantPosition);

                candidates.sort(conflictResolver);
                Animal winner = candidates.get(0);

                Plant plant = plants.get(plantPosition);
                eat(winner, plant);

                plants.remove(plantPosition);
            }
        }
    }

    private void eat(Animal animal, Plant plant){
        if (immunityGenotype.isPresent() && plant.isPoisonous()) {
            Genotype pattern = immunityGenotype.get();
            int similarity = animal.getGenotype().similarity(pattern);

            double resistance = (double) similarity / genomeLength;

            int actualCost = (int) (poisonEnergyCost * (1.0 - resistance));

            animal.subtractEnergy(actualCost);

        } else {
            animal.eat(plantEnergy);
        }
    }
}
