package agh.ics.oop.simulation.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.List;

import agh.ics.oop.model.Animal;
import agh.ics.oop.simulation.SimulationParameters;

public class DeathHandler {
    private final int dailyEnergyCost;

    private final List<Animal> animals;
    private final List<Animal> dead;

    public DeathHandler(SimulationParameters parameters, List<Animal> animals, List<Animal> dead) {
        this.dailyEnergyCost = parameters.dailyEnergyCost();

        this.animals = animals;
        this.dead = dead;
    }

    public void handle(int currentDay) {
        Iterator<Animal> iterator = animals.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if (animal.isDead()) {
                animal.markAsDead(currentDay);
                dead.add(animal);
                iterator.remove();
            }
        }
    }

    public void subtractEnergy() {
        for(Animal animal : animals){
            animal.subtractEnergy(dailyEnergyCost);
        }
    }
}
