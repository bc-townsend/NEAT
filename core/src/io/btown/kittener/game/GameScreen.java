package io.btown.kittener.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import io.btown.kittener.neat.Population;

import java.util.*;

/**
 * The GameScreen class contains all game logic and is what controls the simulation and passes
 * game information to the NEAT part of the application.
 * @author Brandon Townsend
 * @version 21 January 2020
 */
public class GameScreen extends ScreenAdapter {
    /** Reference to the game class which 'runs' the game. */
    private final MainGame GAME;

    // All textures the game should use.+
    static final Texture bus = new Texture("core/assets/bus.png");
    static final Texture raceCar = new Texture("core/assets/racecar.png");
    static final Texture yellowCar = new Texture("core/assets/yellow_car.png");
    static final Texture turtle = new Texture("core/assets/turtle.png");
    static final Texture shortLog = new Texture("core/assets/log3.png");
    static final Texture mediumLog = new Texture("core/assets/log4.png");
    static final Texture longLog = new Texture("core/assets/log5.png");
    static final Texture death = new Texture("core/assets/death.png");
    static final Texture catBack = new Texture("core/assets/cat_back.png");
    static final Texture catFront = new Texture("core/assets/cat_front.png");
    static final Texture catLeft = new Texture("core/assets/cat_left.png");
    static final Texture catRight = new Texture("core/assets/cat_right.png");

    /** The constant number of agents we should spawn. */
    private final int NUM_AGENTS = 100;

    /** Variable to keep track of the highest overall score we have seen. */
    private int highestOverallScore = 0;

    /** Left-side bound of the map for spawning hazards/platforms. */
    private final int leftBounds;

    /** Right-side bound of the map for spawning hazards/platforms. */
    private final int rightBounds;

    /** The camera attached to this screen. */
    private final OrthographicCamera camera;

    /** Map renderer. */
    private final TiledMapRenderer renderer;

    /** List of all hazards in the game. */
    private final ArrayList<Hazard> hazards;

    /** List of all agents in the game. */
    private final ArrayList<Agent> agents;

    /** Population of all organisms in the game. */
    private final Population population;

    private boolean gameReset = false;

    /**
     * Constructor for the main logic behind the game.
     * @param game A back-reference to the application controller so we can switch screens if
     *             need be.
     */
    public GameScreen(final MainGame game) {
        GAME = game;

        // Setting up some needed game variables.
        leftBounds = (-32 * 5);
        rightBounds = this.GAME.getWidth() + (32 * 5);

        // Setting up this screens camera.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME.getWidth(), GAME.getHeight());

        // Creating the map objects.
        hazards = new ArrayList<>(NUM_AGENTS);
        spawnMapObjects();

        // Creating the game agents.
        agents = new ArrayList<>(NUM_AGENTS);
        spawnAgents();

        // Assigning our constructed agents to our population.
        population = new Population(NUM_AGENTS, hazards.size(), 5);

        // Creating the tiled map background.
        TiledMap map = new TmxMapLoader().load("core/assets/maps/map_no_water.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1);
    }

    /**
     * Adds the agents to our list of agents.
     */
    private void spawnAgents() {
        for(int i = 0; i < NUM_AGENTS; i++) {
            Agent agent = new Agent(catBack, hazards.size(), GAME.getWidth() / 2f);
            agents.add(agent);
        }
    }

    /**
     * Helper method used to spawn various map objects. Hazards take the form of:
     *      width, height, column (pixels), row (pixels), texture, and speed.
     * You can uncomment or add any extra that you would like. If they do not appear, the hazards beginning x location is
     * most likely over the left or right bounds.
     */
    private void spawnMapObjects() {
        // Hazards on the first row from the bottom.
        hazards.add(new Hazard(32, 32, -20, 32, yellowCar, Speeds.RIGHT_MED.objectSpeed));
//        hazards.add(new Hazard(32, 32, -84, 32, yellowCar, Speeds.RIGHT_MED.objectSpeed));
//        hazards.add(new Hazard(32, 32, leftBounds+16, 32, yellowCar, Speeds.RIGHT_MED.objectSpeed));

        // Hazards on the second row from the bottom.
        hazards.add(new Hazard(64, 32, GAME.getWidth() + 32, 32*2, bus,
                Speeds.LEFT_SLOW.objectSpeed));
//        hazards.add(new Hazard(64, 32, rightBounds, 32*2, bus, Speeds.LEFT_SLOW.objectSpeed));

        // Hazards on the third row from the bottom.
//        hazards.add(new Hazard(32, 32, rightBounds, 32*3, raceCar, Speeds.LEFT_FAST.objectSpeed));

        // Hazards on the fourth row from the bottom.
//        hazards.add(new Hazard(64, 32, GAME.getWidth()+32, 32*4, bus, Speeds.LEFT_SLOW.objectSpeed));
//        hazards.add(new Hazard(64, 32, rightBounds-32, 32*4, bus, Speeds.LEFT_SLOW.objectSpeed));

        // Hazards on the fourth row from the bottom.
        hazards.add(new Hazard(32, 32, -40, 32*4, yellowCar, Speeds.RIGHT_MED.objectSpeed));
        hazards.add(new Hazard(32, 32, -104, 32*4, yellowCar, Speeds.RIGHT_MED.objectSpeed));
//        hazards.add(new Hazard(32, 32, leftBounds, 32*4, yellowCar, Speeds.RIGHT_MED.objectSpeed));

        // Hazards on the seventh row from the bottom.
        hazards.add(new Hazard(32, 32, -16, 32*6, yellowCar, Speeds.RIGHT_MED.objectSpeed));
        hazards.add(new Hazard(32, 32, -80, 32*6, yellowCar, Speeds.RIGHT_MED.objectSpeed));
        hazards.add(new Hazard(32, 32, leftBounds+16, 32*6, yellowCar, Speeds.RIGHT_MED.objectSpeed));

        // Hazards on the eighth row from the bottom.
        hazards.add(new Hazard(64, 32, GAME.getWidth(), 32*7, bus, Speeds.LEFT_SLOW.objectSpeed));
        hazards.add(new Hazard(64, 32, rightBounds, 32*7, bus, Speeds.LEFT_SLOW.objectSpeed));

        // Hazards on the ninth row from the bottom.
        hazards.add(new Hazard(32, 32, rightBounds, 32*8, raceCar, Speeds.LEFT_FAST.objectSpeed));

        // Hazards on the tenth row from the bottom.
        hazards.add(new Hazard(64, 32, GAME.getWidth()+32, 32*9, bus, Speeds.LEFT_SLOW.objectSpeed));
        hazards.add(new Hazard(64, 32, rightBounds-32, 32*9, bus, Speeds.LEFT_SLOW.objectSpeed));

        // Hazards on the eleventh row from the bottom.
        hazards.add(new Hazard(32, 32, -32, 32*10, yellowCar, Speeds.RIGHT_MED.objectSpeed));
        hazards.add(new Hazard(32, 32, -96, 32*10, yellowCar, Speeds.RIGHT_MED.objectSpeed));
        hazards.add(new Hazard(32, 32, leftBounds, 32*10, yellowCar, Speeds.RIGHT_MED.objectSpeed));
    }

    /**
     * Renders this screen every frame. Contains all game logic.
     * @param delta The time between two frames.
     */
    @Override
    public void render(float delta) {
        // Clear the screen with a specified color.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tells the camera to update its matrices. Don't believe this is needed, as the camera does not move.
//        camera.update();

        // Rendering the tiled map.
        renderer.setView(camera);
        renderer.render();

        // Tells the sprite batch to render within the camera's coordinate system.
        GAME.batch.setProjectionMatrix(camera.combined);

        // Begin a new batch and draw all objects.
        GAME.batch.begin();

        // Draws all the hazards.
        hazards.forEach(hazard -> GAME.batch.draw(hazard.getTexture(), hazard.x, hazard.y));

        agents.forEach(agent -> {
            GAME.batch.setColor(agent.getColor());
            GAME.batch.draw(agent.getTexture(), agent.x, agent.y);
            GAME.batch.setColor(Color.WHITE);
        });

        // Outputs statistics to the screen.
        String stats = String.format("Overall High Score: %d\n" +
                                     "Current High Score: %d\n" +
                                     "Generation: %d",
                                     highestOverallScore, getHighScore(), population.getGeneration());

        GAME.font.draw(GAME.batch, stats, 4, 80);

        // Ending our sprite batch.
        GAME.batch.end();

        // This if statement prevents a rendering lag at the beginning of a generation. Won't affect results if it is
        // removed, but does make the game look laggy when it should not appear that way.
        if(gameReset) {
            gameReset = false;
        } else {
            // Each hazard moving.
            updateHazards(delta);

            // Each agent moving.
            updateAgents(delta);

            // Check collision.
            checkCollisions();
        }

        // Apply opacity to the agent's color if they are dead.
        agents.parallelStream().filter(Agent::isDead).forEach(agent -> {
            Color color = agent.getColor();
            float alpha = color.a > 0.1f ? color.a * 0.99f : 0.1f;
            agent.setColor(new Color(color.r, color.g, color.b, alpha));
        });

        // If all agents are dead, set the final fitness values for this generation and reset.
        if(areAllAgentsDead()) {
            performNaturalSelection();

            for (Agent agent : agents) {
                agent.setColor(population.getColor(agent.getID()));
            }
            population.incrementGeneration();
            resetGame();
            gameReset = true;
        }
    }

    private void performNaturalSelection() {
        for (Agent agent : agents) {
            population.assignFitness(agent.getID(), agent.getScore());
        }
        population.naturalSelection();
    }

    /**
     * Checks to see whether agents have collided with any map objects.
     */
    private void checkCollisions() {
        for(Agent agent : agents) {

            if(!agent.isDead()) {
                for(Hazard hazard : hazards) {

                    if(hazard.overlaps(agent)) {
                        agent.setTexture(death);
                        agent.setDead(true);
                    }
                }
            }
        }
    }

    /**
     * Helper method to control the hazards' movements.
     * @param delta The time between two frames.
     */
    private void updateHazards(float delta) {
        for(Hazard hazard : hazards) {
            hazard.setX(hazard.getX() + (hazard.getSpeed() * delta));

            // Wrapping the hazards around the screen. Simulates "respawning".
            if(hazard.getX() > rightBounds) {
                hazard.setX(leftBounds);
            } else if(hazard.getX() < leftBounds) {
                hazard.setX(rightBounds);
            }
        }
    }

    /**
     * Helper method that controls each agents' movements.
     * @param delta The time between two frames.
     */
    private void updateAgents(float delta) {
        for(Agent agent : agents) {

            // Perform the following if the agent is not dead.
            if(!agent.isDead()) {
                updateAgentVision(agent);

                double[] output = population.getOutput(agent.getID(), agent.getVision());

                int dir = 0;
                for(int i = 0; i < output.length; i++) {
                    if(output[i] > output[dir]) {
                        dir = i;
                    }
                }

                moveAgent(agent, dir, delta);

                int prevScore = agent.getScore();
                int newScore = calculateAgentScore(agent);

                // If the agent has not increased in score, add to the stillness timer.
                if(prevScore >= newScore) {
                    agent.setStillTimer(agent.getStillTimer() + delta);

                    // If an agent is still for longer than 8 seconds, we kill it off.
                    if(agent.getStillTimer() > 8f) {
                        agent.setTexture(death);
                        agent.setDead(true);
                    }
                } else {
                    agent.setStillTimer(0);
                }
            }
        }
    }

    /**
     * Updates the agent's vision array based on the distance between the agent and the hazards
     * of the game.
     * @param agent The agent whose vision array should be updated.
     */
    private void updateAgentVision(Agent agent) {
        float[] vision = new float[hazards.size()];

        // Grab the position of the agent's center.
        Vector2 agentCenter = new Vector2();
        agentCenter = agent.getCenter(agentCenter);
        Vector2 hazardCenter = new Vector2();

        for(int i = 0; i < hazards.size(); i++) {
            Hazard hazard = hazards.get(i);
            hazardCenter = hazard.getCenter(hazardCenter);

            // Comparing the distances between our agent and all the hazards on the game board.
            vision[i] = hazardCenter.dst(agentCenter);
        }
        agent.setVision(vision);
    }

    /**
     * Moves a specified agent in a supplied direction and makes sure the agent does not escape
     * the game bounds.
     * @param agent The agent to move.
     * @param direction The direction in which the agent should move.
     * @param delta The time between frames.
     */
    private void moveAgent(Agent agent, int direction, float delta) {
        switch(direction) {
            case 0: // Moving up.
                agent.setY(agent.getY() + Speeds.RIGHT_MED.move(delta));
                agent.setTexture(catBack);
                break;
            case 1: // Moving down.
                agent.setY(agent.getY() + Speeds.LEFT_MED.move(delta));
                agent.setTexture(catFront);
                break;
            case 2: // Moving left.
                agent.setX(agent.getX() + Speeds.LEFT_MED.move(delta));
                agent.setTexture(catLeft);
                break;
            case 3: // Moving right.
                agent.setX(agent.getX() + Speeds.RIGHT_MED.move(delta));
                agent.setTexture(catRight);
                break;
            default: // Choosing not to move.
                break;
        }

        // Make sure the agents do not escape the game bounds.
        if(agent.getX() < 0) {
            agent.setX(0);
        } else if(agent.getX() + agent.getWidth() > GAME.getWidth()) {
            agent.setX(GAME.getWidth() - agent.getWidth());
        }
        if(agent.getY() < 0) {
            agent.setY(0);
        } else if(agent.getY() >= GAME.getHeight()) {
            agent.setY(0f);
            agent.setLastY(0f);
        }
    }

    /**
     * Calculates the score an agent should be set to.
     * @param agent The agent to calculate the score for.
     * @return The score.
     */
    private int calculateAgentScore(Agent agent) {
        int prevScore = agent.getScore();
        int score = 0;
        float lastY = agent.getLastY();

        if(agent.getY() > lastY) {
            score = (int) (agent.getY() - lastY) * 10 + prevScore;
            agent.setScore(score);
            agent.setLastY(agent.getY());
        }

        return score;
    }

    /**
     * Returns the highest score achieved this generation.
     * @return The highest score achieved this generation.
     */
    private int getHighScore() {
        int highScore = 0;
        for(Agent agent : agents) {
            if(agent.getScore() > highScore) {
                highScore = agent.getScore();
            }
        }

        if(highScore > highestOverallScore) {
            highestOverallScore = highScore;
        }
        return highScore;
    }

    /**
     * Checks to see whether all of our agents are dead.
     * @return True if all agents are dead, false otherwise.
     */
    private boolean areAllAgentsDead() {
        long numDead = agents.parallelStream().filter(Agent::isDead).count();
        return numDead == NUM_AGENTS;
    }

    /**
     * Resets the game objects back to their original positions for the next generation.
     */
    private void resetGame() {
        hazards.parallelStream().forEach(Hazard::reset);

        agents.parallelStream().forEach(agent -> {
            agent.reset(GAME.getWidth() / 2f);
            agent.setTexture(catBack);
        });
    }

    /**
     * Removes all textures that have been loaded upon the closing of this screen.
     */
    @Override
    public void dispose() {
        super.dispose();
        bus.dispose();
        raceCar.dispose();
        yellowCar.dispose();
        turtle.dispose();
        shortLog.dispose();
        mediumLog.dispose();
        longLog.dispose();
        death.dispose();
        catBack.dispose();
        catFront.dispose();
        catLeft.dispose();
        catRight.dispose();
    }
}
