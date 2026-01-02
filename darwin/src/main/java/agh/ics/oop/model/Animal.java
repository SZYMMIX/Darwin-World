package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal {
    private Vector2d position;
    private Direction direction;
    private int energy;
    private final Genotype genotype;

    private final int birthDay;
    private Integer deathDay = null;
    private final List<Animal> children = new ArrayList<>();

    private Animal(Vector2d position, Direction direction, int energy, Genotype genotype, int birthDay) {
        this.position = position;
        this.direction = direction;
        this.energy = energy;
        this.genotype = genotype;
        this.birthDay = birthDay;
    }

    public static Animal createRandom(Vector2d position, int initialEnergy, int genomeLength, int birthDay, Random random) {
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        Genotype genotype = Genotype.random(genomeLength, random);
        return new Animal(position, direction, initialEnergy, genotype, birthDay);
    }

    public static Animal fromParents(Animal first, Animal second, int birthDay, int initialEnergy, Random random) {
        Animal strong = first.energy >= second.energy ? first : second;
        Animal weak = first.energy >= second.energy ? second : first;

        float energyRatio = (float) strong.energy / (strong.energy + weak.energy);

        Genotype childGenotype = Genotype.cross(strong.genotype, weak.genotype, energyRatio, random);

        Animal child = new Animal(first.position, Direction.values()[random.nextInt(8)], initialEnergy, childGenotype, birthDay);

        first.children.add(child);
        second.children.add(child);

        return child;
    }

    public void move(int width, int height, int currentDay) {
        int dayOfLife = currentDay - birthDay;
        int rotateSteps = genotype.getGene(dayOfLife);

        this.direction = this.direction.shift(rotateSteps);

        Vector2d nextPos = this.position.add(this.direction.toUnitVector());

        if (nextPos.y() < 0 || nextPos.y() >= height) {
            this.direction = this.direction.shift(4);
            return;
        }

        int newX = nextPos.x();
        if (newX < 0) {
            newX = width - 1;
        } else if (newX >= width) {
            newX = 0;
        }

        this.position = new Vector2d(newX, nextPos.y());
    }

    public void eat(int energyFromPlant) {
        this.energy += energyFromPlant;
    }

    public void subtractEnergy(int amount) {
        this.energy -= amount;
    }

    public void markAsDead(int deathDay) {
        this.deathDay = deathDay;
        this.energy = 0;
    }

    public boolean isDead() {
        return this.deathDay != null || this.energy <= 0;
    }

    public Vector2d getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public Genotype getGenotype() { return genotype; }
    public int getEnergy() { return energy; }

    public int getAge(int currentDay) {
        if (deathDay != null) {
            return deathDay - birthDay;
        }
        return currentDay - birthDay;
    }

    public List<Animal> getChildren() {
        return List.copyOf(children);
    }

    public int getChildrenCount() {
        return children.size();
    }
}