package com.mygdx.claninvasion.model.entity;

import com.badlogic.gdx.math.Vector2;
import org.javatuples.Pair;

/**
 * Basic Entity of application, will be used in the view part for the Sprites
 * @author Dinari
 * @version 0.01
 */
public class Entity {
    protected final EntitySymbol entitySymbol;
    protected Pair<Integer , Integer> position;

    /** Creates new Entity. */
    public Entity() {
        entitySymbol = EntitySymbol.TREE;
    }

    public Entity(EntitySymbol entitySymbol, Pair<Integer, Integer> position) {
        this.entitySymbol = entitySymbol;
        this.position = position;
    }

    /** Returns map symbol
     * @return - symbol synced with map representation
     * @see EntitySymbol
     */
    public EntitySymbol getSymbol() {
        return entitySymbol;
    }

    /**
     * Returns map symbol
     * @return - position on the map Of x
     *
     */
    public Integer getPositionX() {
        return position.getValue0();
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    /**
     * Returns map symbol
     * @return - position on the map of Y
     *
     */
    public Integer getPositionY() {
        return position.getValue1();
    }

    public Vector2 getVec2Position() {
        return new Vector2(position.getValue0(), position.getValue1());
    }
}
