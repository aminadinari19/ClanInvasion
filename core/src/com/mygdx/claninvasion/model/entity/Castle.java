package com.mygdx.claninvasion.model.entity;

/**
 * Castle entity
 * TODO: Logic part is missing
 */
public class Castle extends ArtificialEntity {

    /**
     * Should heal the entity
     * @see ArtificialEntity
     */
    public void heal(Soldier soldier) {
        soldier.healSoldier();
    }

    /**
     * @see ArtificialEntity
     * @param amount - percent of injury
     */
    @Override
    public void damage(int amount) {
        Soldier s = new Soldier();
        s.damage(1);
    }

    /**
     * Heal attacked soldier
     * @param soldier - soldier
     * TODO Implement logic
     */
    public void healSoldier(Soldier soldier) {}

    /**
     * Damage attacked soldier
     * @param soldier - soldier
     * TODO Implement logic
     */
    public void damageSoldier(Soldier soldier) {}
}
