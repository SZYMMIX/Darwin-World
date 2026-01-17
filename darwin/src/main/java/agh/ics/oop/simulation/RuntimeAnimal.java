package agh.ics.oop.simulation;

import agh.ics.oop.model.*;

import java.util.*;
import java.util.stream.Collectors;

class RuntimeAnimal {
    private final int id;
    private final AnimalDetails details;

    private Vector2d position;
    private Direction direction;
    private int energy;
    private int childrenCount;

    private RuntimeAnimal(int id, AnimalDetails details, Vector2d position, Direction direction, int energy, int childrenCount) {
        this.id = id;
        this.details = details;
        this.position = position;
        this.direction = direction;
        this.energy = energy;
        this.childrenCount = childrenCount;
    }

    static RuntimeAnimal fromChildData(int id, ChildData childData, int birthDay, Random random) {
        AnimalDetails details = new AnimalDetails(childData.genotype(), birthDay, childData.parentAId(), childData.parentBId());
        Direction direction = Direction.random(random);

        return new RuntimeAnimal(id, details, childData.position(), direction, childData.initialEnergy(), 0);
    }

    void move(int width, int height, int currentDay) {
        int age = currentDay - details.birthDay();
        int rotateSteps = details.genotype().getGene(age);
        this.direction = this.direction.shift(rotateSteps);

        Vector2d moveVector = this.direction.toUnitVector();
        Vector2d nextPos = this.position.add(moveVector);

        if (nextPos.y() < 0 || nextPos.y() >= height) {
            this.direction = this.direction.shift(4);
        } else {
            int newX = (nextPos.x() + width) % width;
            if (newX < 0) newX += width;
            this.position = new Vector2d(newX, nextPos.y());
        }
    }

    ChildData reproduce(RuntimeAnimal partner, int minEnergy, int energyCost, int mutationsCount, Random random) {
        if (this.energy < minEnergy) return null;
        if (partner == null || partner.energy < minEnergy) return null;

        this.subtractEnergy(energyCost);
        partner.subtractEnergy(energyCost);

        this.childrenCount++;
        partner.childrenCount++;

        float ratio = (float) this.energy / (this.energy + partner.energy);
        Genotype childGenotype = Genotype.cross(this.details.genotype(), partner.details.genotype(), ratio, random)
                                         .mutate(mutationsCount, random);

        return new ChildData(
                position,
                energyCost * 2,
                childGenotype,
                this.id,
                partner.id
        );
    }

    void eat(int plantEnergy) {
        this.energy += plantEnergy;
    }

    void subtractEnergy(int cost) {
        this.energy -= cost;
    }

    boolean isDead() {
        return energy <= 0;
    }

    AnimalSnapshot getSnapshot() {
        return new AnimalSnapshot(id, position, direction, energy);
    }

    static Map<Vector2d, List<RuntimeAnimal>> getSortedGroups(Collection<RuntimeAnimal> animals) {
        Map<Vector2d, List<RuntimeAnimal>> groups = animals.stream()
                .collect(Collectors.groupingBy(a -> a.position));

        groups.values().forEach(group ->
                group.sort((a, b) -> {
                    int energyDiff = Integer.compare(b.energy, a.energy);
                    if (energyDiff != 0) return energyDiff;

                    int ageDiff = Integer.compare(a.details.birthDay(), b.details.birthDay());
                    if (ageDiff != 0) return ageDiff;

                    return Integer.compare(b.childrenCount, a.childrenCount);
                })
        );

        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeAnimal that = (RuntimeAnimal) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    int getId() { return id; }
    AnimalDetails getDetails() { return details; }
    int getEnergy() { return energy; }
    int getChildrenCount() { return childrenCount; }
    Vector2d getPosition() { return position; }
}