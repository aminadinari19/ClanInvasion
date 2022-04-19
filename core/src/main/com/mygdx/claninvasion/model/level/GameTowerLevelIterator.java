package com.mygdx.claninvasion.model.level;

import java.util.ArrayList;

/**
 * This class is responsible for iterating through the
 * tower level in the game
 * @author andreicristea
 * @author omarashour
 * @author Dinari
 * TODO: Logic part is missing
 */
public class GameTowerLevelIterator extends LevelIterator<GameTowerLevel>{

    private GameTowerLevel gameTowerLevel;

    public GameTowerLevelIterator(ArrayList<GameTowerLevel> gameTowerLevels) {
        super(gameTowerLevels);
    }

}
