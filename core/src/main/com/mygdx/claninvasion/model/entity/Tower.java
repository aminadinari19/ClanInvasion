package com.mygdx.claninvasion.model.entity;

import com.mygdx.claninvasion.model.level.GameTowerLevel;
import com.mygdx.claninvasion.model.level.GameTowerLevelIterator;
import com.mygdx.claninvasion.model.level.Level;
import com.mygdx.claninvasion.model.level.LevelIterator;
import org.javatuples.Pair;

import java.util.concurrent.CompletableFuture;

import static com.mygdx.claninvasion.model.level.Levels.createTowerLevelIterator;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutorService;

public class Tower extends ArtificialEntity implements Defensible {


    public static GameTowerLevelIterator towerLevelIterator = createTowerLevelIterator();
    static GameTowerLevel gameTowerLevel= towerLevelIterator.current();

    public static int COST = 200;
    public static int creationTime;
    boolean levelUpdated;

    public Tower(EntitySymbol entitySymbol, Pair<Integer, Integer> position) {
        super(entitySymbol, position);
        towerLevelIterator = createTowerLevelIterator();
        creationTime = gameTowerLevel.getCreationTime();
        towerUpdateLevel().thenRunAsync(() -> System.out.println("New tower was added"));
    }


    Tower(LevelIterator<Level> levelIterator) {
        super(levelIterator);
    }

    public static void updateTowerLevel() {
        creationTime = towerLevelIterator.current().getCreationTime();
    }


    public CompletableFuture<Boolean> towerUpdateLevel() {
        ExecutorService executor = newFixedThreadPool(1);
        CompletableFuture<Boolean> supply = CompletableFuture.<Boolean>supplyAsync(() -> {
            Boolean valueUpdated = null;
            try {
                System.out.println("Sleeping");
                Thread.sleep(creationTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return valueUpdated;
        }, executor);

        supply.whenComplete((a, b) -> executor.shutdownNow());
        return supply;
    }


    @Override
    public void damage(int amount) {
        super.damage(amount);
    }

    @Override
    public void heal() {
        super.heal();
    }


    @Override
    public CompletableFuture<Boolean> attack(ArtificialEntity artificialEntity, Fireable fire) {
        float distance = getVec2Position().dst(
                artificialEntity.getVec2Position().x,
                artificialEntity.getVec2Position().y
        );

        if (distance > 2) {
            return CompletableFuture.supplyAsync(() -> false);
        }

        if (!artificialEntity.isAlive()) {
            return CompletableFuture.supplyAsync(() -> false);
        }

        CompletableFuture<Boolean> future = CompletableFuture
                .supplyAsync(() -> fire.fire(position, artificialEntity.position).join());
        future
                .orTimeout(3, SECONDS)
                .thenAccept(a -> artificialEntity.damage(100))
                .thenAccept(a -> System.out.println("Attack by tower was completed"))
                .completeExceptionally(new RuntimeException("Could not finish the defend method"));
        return future;
    }
}
