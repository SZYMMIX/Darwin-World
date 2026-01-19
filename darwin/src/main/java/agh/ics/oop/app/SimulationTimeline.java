package agh.ics.oop.app;

import agh.ics.oop.simulation.SimulationSnapshot;

import java.util.LinkedList;

public class SimulationTimeline {
    private static final int MAX_HISTORY_SIZE = 100;
    private final LinkedList<SimulationSnapshot> history = new LinkedList<>();
    private int cursor = -1;

    public synchronized void add(SimulationSnapshot snapshot) {
        history.add(snapshot);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
            if (cursor != -1) {
                cursor = Math.max(0, cursor - 1);
            }
        }
    }

    public synchronized SimulationSnapshot getCurrent() {
        if (history.isEmpty()) return null;
        if (cursor == -1 || cursor >= history.size()) return history.getLast();
        return history.get(cursor);
    }

    public synchronized boolean shiftCursor(int direction) {
        if (history.isEmpty()) return false;

        int maxIndex = history.size() - 1;
        int currentIdx = (cursor == -1) ? maxIndex : cursor;

        int newIndex = currentIdx + direction;

        if (newIndex >= maxIndex + 1) {
            cursor = -1;
            return true;
        }

        if (newIndex >= 0) {
            cursor = newIndex;
            return true;
        }

        return false;
    }

    public void resetCursorToLive() {
        this.cursor = -1;
    }

    public boolean isLive() {
        return cursor == -1;
    }
}