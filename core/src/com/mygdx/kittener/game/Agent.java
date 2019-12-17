package com.mygdx.kittener.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Agent extends Rectangle {
    /** The identification number for this agent. */
    private int ID;

    /** The texture for this agent. */
    private Texture texture;

    /** The fitness of this agent. */
    private float fitness;

    /** The last y-coordinate the agent was located at. */
    private float lastY;

    /** Variable representing if the agent is dead or not. */
    private boolean isDead;

    /** Timer keeping track of how long the agent stands still. */
    private float stillTimer;

    /** Array that keeps track of the distance between an agent and all hazards in the game. */
    private float[] visionArray;

    /**
     * Constructor for our agent.
     * @param texture The texture for the agent.
     * @param ID The identification number for the agent.
     * @param numHazards The number of hazards in the game used to determine the length of our
     *                   vision array.
     */
    public Agent(int ID, Texture texture, int numHazards) {
        this.ID = ID;
        this.texture = texture;
        this.fitness = 0.0f;
        this.lastY = 0.0f;
        this.isDead = false;
        this.stillTimer = 0.0f;
        this.visionArray = new float[numHazards];
    }

    /**
     * Returns the texture to be applied to this agent.
     * @return The texture to be applied to this agent.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Returns whether or not this agent is dead.
     * @return True if agent is dead, false otherwise.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Sets this agents texture.
     * @param texture The texture to now be applied to our agent.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Sets this agents last y-coordinate location.
     * @param lastY The last y-coordinate the agent was at.
     */
    public void setLastY(float lastY) {
        this.lastY = lastY;
    }
}
