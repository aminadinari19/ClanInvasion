package com.mygdx.claninvasion.model.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * This class iterates through the levels of tower,
 * soldiers and mining
 * @author andreicristea
 * @author omarashour
 * @author Dinari
 * @version 0.1
 */
public class LevelIterator<L extends Level> implements Iterator<L> {
    private final ArrayList<L> levels;
    private int currentLevelNumber;

    public LevelIterator(ArrayList<L> levels) {
        this.levels = levels;
        currentLevelNumber = 0;
        if (this.levels.size() == 0) {
            throw new IllegalArgumentException("Please provide a collection with at least one level in it.");
        }
    }

    @Override
    public boolean hasNext() {
        return currentLevelNumber <= levels.size();
    }

    public int getLevelSize() {
        return levels.size();
    }

    public L current() {
        return levels.get(currentLevelNumber);
    }

    @Override
    public L next() {
        return levels.get(currentLevelNumber++);
    }

    @Override
    public void remove() {
        levels.remove(currentLevelNumber);
    }

    @Override
    public void forEachRemaining(Consumer<? super L> action) {
        while (hasNext()) {
            action.accept(next());
        }
    }

    public int getLevelName() {
        return currentLevelNumber;
    }

    public void reset() {
        currentLevelNumber = 0;
    }
}
