package io.btown.kittener.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.Arrays;

/**
 * Class which represents an agent (in the case of "Kittener", a cat) and all information that
 * agent needs.
 * @author Brandon Townsend
 * @version 21 January 2020
 */
public class Agent extends Rectangle {
    private static int idCounter = 0;

    /** The identification number of this agent. */
    private final int ID;

    /** The texture this agent should be currently displayed as. */
    private Texture texture;

    /** The score of this agent. */
    private int     score;

    /** The last y-coordinate the agent was located at. */
    private float   lastY;

    /** Boolean to determine whether or not this agent is dead. */
    private boolean isDead;

    /** Timer to keep track of how long the agent has "stood still". */
    private float   stillTimer;

    /** What color this agent should be displayed as. */
    private Color color;

    /**
     * An array of the agents vision. Currently holds distances between the agent and game
     * objects.
     */
    private float[] vision;

    /**
     * Constructor for an agent.
     * @param texture The starting texture it should be displayed as.
     * @param arraySize The size of the agents vision array.
     * @param xLocation The starting x-coordinate to spawn the agent at.
     */
    public Agent(Texture texture, int arraySize, float xLocation) {
        super(xLocation, 0, 32, 16);
        ID = idCounter++;
        this.texture    = texture;
        this.score      = 0;
        this.lastY      = 0f;
        this.isDead     = false;
        this.stillTimer = 0f;
        this.vision     = new float[arraySize];
        this.color      = new Color(1, 1, 1, 1);
    }

    /**
     * Returns this agents identification number.
     * @return The identification number for this agent.
     */
    public int getID() {
        return ID;
    }

    /**
     * Returns the texture currently being displayed for this agent.
     * @return The texture currently being displayed for this agent.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture that should be displayed.
     * @param texture The new texture to show this agent as.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Returns this agents score.
     * @return This agents score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets this agents score to the supplied one.
     * @param score The new score of this agent.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the last y-coordinate this agent was located at.
     * @return The last y-coordinate this agent was located at.
     */
    public float getLastY() {
        return lastY;
    }

    /**
     * Sets this agents last y-coordinate to the supplied one.
     * @param lastY The new y-coordinate for this agent.
     */
    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    /**
     * Returns whether or not this agent is dead.
     * @return True if dead, false otherwise.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Tells this agent whether or not it is dead.
     * @param dead The new boolean value to determine death.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * Returns how long the agent has been standing still.
     * @return How long the agent has been standing still.
     */
    public float getStillTimer() {
        return stillTimer;
    }

    /**
     * Sets the standing still timer to the supplied time.
     * @param stillTimer The new time the timer should hold.
     */
    public void setStillTimer(float stillTimer) {
        this.stillTimer = stillTimer;
    }

    /**
     * Returns the vision array of this agent.
     * @return The vision array of this agent.
     */
    public float[] getVision() {
        return vision;
    }

    /**
     * Sets this agents vision array to the new values passed in by the supplied array. Divides
     * them by 32, as that is the length of a side of a tile.
     * @param vision The new vision array.
     */
    public void setVision(float[] vision) {
        this.vision = vision;
    }

    /**
     * Returns the color this agent should be displayed as.
     * @return The color this agent should be displayed as.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the display color of this agent to the supplied color.
     * @param color The color to set this agent to.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Resets this agent back to its starting values.
     * @param xPos The x-coordinate position to reset this agent to.
     */
    public void reset(float xPos) {
        setX(xPos);
        setY(0);
        score       = 0;
        lastY       = 0f;
        isDead      = false;
        stillTimer  = 0f;
        Arrays.fill(vision, 0f);
    }

    /**
     * Override of base equals(). Two agents are equal if they have the same identification number.
     * @param obj The object to check for equality.
     * @return True if they have the same id number, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Agent) {
            Agent agent = (Agent) obj;
            return agent.getID() == ID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}