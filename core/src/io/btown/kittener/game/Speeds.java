package io.btown.kittener.game;

/**
 * Enumeration containing speeds for all objects in the game.
 * @author Chance Simmons and Brandon Townsend
 * @version 16 December 2019
 */
public enum Speeds {
    RIGHT_SLOW(64f),
    RIGHT_MED(128f),
    RIGHT_FAST(256f),
    LEFT_SLOW(RIGHT_SLOW.objectSpeed * -1),
    LEFT_MED(RIGHT_MED.objectSpeed * -1),
    LEFT_FAST(RIGHT_FAST.objectSpeed * -1);

    /**
     * The speed at which an object moves.
     */
    public final float objectSpeed;

    /**
     * Constructor for the enum representing an object's speed.
     * @param objectSpeed The speed at which an object moves.
     */
    Speeds(float objectSpeed) {
        this.objectSpeed = objectSpeed;
    }

    /**
     * Moves an object based on its speed and the delta time of the game.
     * @param deltaTime The time between frames in the game.
     * @return How far the object should move.
     */
    public float move(float deltaTime) {
        return objectSpeed * deltaTime;
    }
}
