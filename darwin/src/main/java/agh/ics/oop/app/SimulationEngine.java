package agh.ics.oop.app;

import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimulationEngine implements Runnable {

    private final Simulation simulation;
    private final List<Consumer<SimulationSnapshot>> observers = new ArrayList<>();

    private volatile boolean running = true;
    private volatile boolean paused = true;
    private volatile int moveDelay = 200;

    public SimulationEngine(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        while (running) {
            if (paused) {
                sleep(100);
                continue;
            }

            SimulationSnapshot snapshot = simulation.step();
            notifyObservers(snapshot);
            sleep(moveDelay);
        }
    }

    public void addObserver(Consumer<SimulationSnapshot> observer) {
        observers.add(observer);
    }

    private void notifyObservers(SimulationSnapshot snapshot) {
        for (Consumer<SimulationSnapshot> observer : observers) {
            observer.accept(snapshot);
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            running = false;
        }
    }

    public void setMoveDelay(int moveDelay) {
        this.moveDelay = moveDelay;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public void stop() {
        this.running = false;
    }
}