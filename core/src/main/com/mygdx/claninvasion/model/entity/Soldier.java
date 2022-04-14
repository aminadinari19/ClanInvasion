package com.mygdx.claninvasion.model.entity;

import com.mygdx.claninvasion.model.gamestate.GamePhase;
import com.mygdx.claninvasion.model.gamestate.GameState;
import com.mygdx.claninvasion.model.helpers.Direction;
import com.mygdx.claninvasion.model.level.GameSoldierLevelIterator;
import com.mygdx.claninvasion.model.level.Levels;
import com.mygdx.claninvasion.model.map.WorldCell;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Soldier class implementation
 * @version 0.01
 */
public class Soldier extends ArtificialEntity {
    private static final int ATTACK = 100;
    private static final int STEP = 1;
    private AtomicBoolean isTrained = new AtomicBoolean(false);
    public Soldier() {
        super();
        level = Levels.createSoldierLevelIterator();
    }

    /**
     * Attack castle method implementation
     * @param castle - opponents castle
     * @see Castle
     */
    public boolean attackCastle(Castle castle) {
        float distance = getVec2Position().dst(castle.getVec2Position().x, castle.getVec2Position().y);

        GameSoldierLevelIterator level = (GameSoldierLevelIterator)this.level;
        if (distance < level.current().getVisibleArea()) {
            castle.damage(ATTACK + level.current().getAttackIncrease());
            return true;
        }

        return false;
    }
    public void move(WorldCell cell) {
        position = position.setAt0(cell.getMapPosition().getValue0());
        position = position.setAt1(cell.getMapPosition().getValue1());
        cell.setOccupier(this);
    }

    /**
     * Make a step inside map
     */
    public void step(Direction direction) {
        if (Direction.equals(Direction.DOWN, direction)) {
            position = position.setAt1(position.getValue1() + STEP);
        } else if (Direction.equals(Direction.UP, direction)) {
            position = position.setAt1(position.getValue1() - STEP);
        } else if (Direction.equals(Direction.LEFT, direction)) {
            position = position.setAt0(position.getValue0() - STEP);
        } else if (Direction.equals(Direction.RIGHT, direction)) {
            position = position.setAt0(position.getValue0() + STEP);
        } else {
            throw new IllegalArgumentException(
                    "Such direction not found. Expected \"up\", \"down\", \"left\", or \"right\", got" + direction.alias
            );
        }
    }

    /**
     * Train soldier algorithm
     */
    public boolean train(GamePhase phase) {
        if (phase == GamePhase.ATTACK) {
            return false;
        }

        try {
            CompletableFuture.supplyAsync(() -> this.isTrained.getAndSet(true))
                    .get(level.current().getReactionTime(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return true;
    }
}
