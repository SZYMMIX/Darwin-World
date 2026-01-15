package agh.ics.oop.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

import agh.ics.oop.simulation.handlers.BirthHandler;
import agh.ics.oop.simulation.handlers.ConsumptionHandler;
import agh.ics.oop.simulation.handlers.DeathHandler;
import agh.ics.oop.simulation.handlers.GrowthHandler;
import agh.ics.oop.simulation.handlers.MovementHandler;

public class Simulation {
    private final Random random;

    private final List<Animal> animals;
    private final List<Animal> dead;
    private final Map<Vector2d, Plant> plants;

    private final DeathHandler death;
    private final MovementHandler movement;
    private final ConsumptionHandler consumption;
    private final BirthHandler birth;
    private final GrowthHandler growth;

    private int currentDay;

    public Simulation(SimulationParameters parameters) {
        this.random = new Random();

        this.animals = new ArrayList<>();
        this.dead = new ArrayList<>();
        this.plants = new HashMap<>();

        this.death = new DeathHandler(parameters, this.animals, this.dead);
        this.movement = new MovementHandler(parameters, this.animals);
        this.consumption = new ConsumptionHandler(parameters, this.animals, this.plants);
        this.birth = new BirthHandler(parameters, this.animals, this.plants, this.random);
        this.growth = new GrowthHandler(parameters, this.plants, this.random);

        this.currentDay = 0;
    }

    public void step() {
        death.handle();
        movement.handle();
        consumption.handle();
        birth.handle();
        growth.handle();

        death.subtractEnergy();

        currentDay++;
    }
}
