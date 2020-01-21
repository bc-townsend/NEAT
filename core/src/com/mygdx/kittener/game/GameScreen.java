package com.mygdx.kittener.game;

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
import neat.Population;

import java.util.ArrayList;

/**
 * The GameScreen class contains all game logic and is what controls the simulation and passes
 * game information to the NEAT part of the application.
 * @author Brandon Townsend
 * @version 18 January 2020
 */
public class GameScreen extends ScreenAdapter {
    /** Reference to the game class which 'runs' the game. */
    private final MainGame game;

    /** The constant number of agents we should spawn. */
    private final int NUM_AGENTS = 10;

    /** Variable to keep track of the highest overall score we have seen. */
    private int highestOverallScore = 0;

    /** A timer in which we delay the spawning of the next generation. */
    private float delayTimer;

    /** Left-side bound of the map for spawning hazards/platforms. */
    private int leftBounds;

    /** Right-side bound of the map for spawning hazards/platforms. */
    private int rightBounds;

    /** The camera attached to this screen. */
    private OrthographicCamera camera;

    /** Map renderer. */
    private TiledMapRenderer renderer;

    // All textures the game should use.
    private Texture bus;
    private Texture raceCar;
    private Texture yellowCar;
    private Texture turtle;
    private Texture shortLog;
    private Texture mediumLog;
    private Texture longLog;
    private Texture death;
    private Texture catBack;
    private Texture catFront;
    private Texture catLeft;
    private Texture catRight;

    /** List of all hazards in the game. */
    private ArrayList<Hazard> hazards;

    /** List of all agents in the game. */
    private ArrayList<Agent> agents;

    /** Population of all organisms in the game. */
    private Population population;

    public GameScreen(final MainGame game) {
        this.game = game;

        // Setting up some needed game variables.
        delayTimer = 0f;
        leftBounds = (-32 * 5);
        rightBounds = this.game.getWidth() + (32 * 5);

        // Setting up the textures.
        bus         = new Texture("core/assets/bus.png");
        raceCar     = new Texture("core/assets/racecar.png");
        yellowCar   = new Texture("core/assets/yellow_car.png");
        turtle      = new Texture("core/assets/turtle.png");
        shortLog    = new Texture("core/assets/log3.png");
        mediumLog   = new Texture("core/assets/log4.png");
        longLog     = new Texture("core/assets/log5.png");
        death       = new Texture("core/assets/death.png");
        catBack     = new Texture("core/assets/cat_back.png");
        catFront    = new Texture("core/assets/cat_front.png");
        catLeft     = new Texture("core/assets/cat_left.png");
        catRight    = new Texture("core/assets/cat_right.png");

        // Setting up this screens camera.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, this.game.getWidth(), this.game.getHeight());

        // Creating the map objects.
        hazards = new ArrayList<>(NUM_AGENTS);
        spawnMapObjects();

        // Creating the game agents.
        agents = new ArrayList<>(NUM_AGENTS);
        spawnAgents();

        // Assigning our constructed agents to our population.
        population = new Population(agents, hazards.size(), 5);

        // Creating the tiled map background.
        TiledMap map = new TmxMapLoader().load("core/assets/maps/map_no_water.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1);
    }

    /**
     * Adds the agents to our list of agents.
     */
    private void spawnAgents() {
        for(int i = 0; i < NUM_AGENTS; i++) {
            Agent agent = new Agent(i, catBack, hazards.size(), game.getWidth() / 2f);
            agents.add(agent);
        }
    }

    /**
     * Helper method used to spawn various map objects. Hazards take the form of:
     *      width, height, column (pixels), row (pixels), texture, and speed.
     */
    private void spawnMapObjects() {
        // Hazards on the seventh row from the bottom.
        hazards.add(new Hazard(32, 32, -16, 32*6, yellowCar, Speeds.RIGHT_MED.speed()));
        hazards.add(new Hazard(32, 32, -80, 32*6, yellowCar, Speeds.RIGHT_MED.speed()));
        hazards.add(new Hazard(32, 32, leftBounds+16, 32*6, yellowCar, Speeds.RIGHT_MED.speed()));

        // Hazards on the eighth row from the bottom.
        hazards.add(new Hazard(64, 32, game.getWidth(), 32*7, bus, Speeds.LEFT_SLOW.speed()));
        hazards.add(new Hazard(64, 32, rightBounds, 32*7, bus, Speeds.LEFT_SLOW.speed()));

        // Hazards on the ninth row from the bottom.
        hazards.add(new Hazard(32, 32, rightBounds, 32*8, raceCar, Speeds.LEFT_FAST.speed()));

        // Hazards on the tenth row from the bottom.
        hazards.add(new Hazard(64, 32, game.getWidth()+32, 32*9, bus, Speeds.LEFT_SLOW.speed()));
        hazards.add(new Hazard(64, 32, rightBounds-32, 32*9, bus, Speeds.LEFT_SLOW.speed()));

        // Hazards on the eleventh row from the bottom.
        hazards.add(new Hazard(32, 32, -32, 32*10, yellowCar, Speeds.RIGHT_MED.speed()));
        hazards.add(new Hazard(32, 32, -96, 32*10, yellowCar, Speeds.RIGHT_MED.speed()));
        hazards.add(new Hazard(32, 32, leftBounds, 32*10, yellowCar, Speeds.RIGHT_MED.speed()));
    }

    /**
     * Renders this screen every frame. Contains all game logic.
     * @param delta The time between two frames.
     */
    @Override
    public void render(float delta) {
        // Add time to our delayTimer
        delayTimer += delta;

        // Clear the screen with a specified color.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tells the camera to update its matrices.
        camera.update();

        // Rendering the tiled map.
        renderer.setView(camera);
        renderer.render();

        // Tells the sprite batch to render within the camera's coordinate system.
        game.batch.setProjectionMatrix(camera.combined);

        // Begin a new batch and draw all objects.
        game.batch.begin();

        // Draws all the hazards.
        for(Hazard hazard : hazards) {
            game.batch.draw(hazard.getTexture(), hazard.getX(), hazard.getY());
        }

        // Draws all agents.
        for(Agent agent : agents) {
            game.batch.draw(agent.getTexture(), agent.getX(), agent.getY());
        }

        // Outputs statistics to the screen.
        String stats = String.format("Overall High Score: %d\n" +
                                     "Current High Score: %d\n" +
                                     "Generation: %d",
                                     highestOverallScore, getHighScore(), population.getGeneration());
        game.font.draw(game.batch, stats, 4, 80);

        // Ending our sprite batch.
        game.batch.end();

        // Each hazard moving.
        updateHazards(delta);

        // Each agent moving.
        updateAgents(delta);

        // Check collision.
        checkCollisions();

        // If all agents are dead, set the final fitness values for this generation and reset.
        if(areAllAgentsDead()) {
            population.naturalSelection();
            if(delayTimer >= 4f) {
                resetGame();
                population.incrementGeneration();
            }
        } else {
            delayTimer = 0;
        }
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

                double[] output = population.getNetwork(agent.getId()).feedForward(agent.getVision());

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

    private void updateAgentVision(Agent agent) {
        float[] vision = new float[hazards.size()];

        // Grab the position of the agent.
        Vector2 agentPos = new Vector2(agent.getX(), agent.getY());
        Vector2 hazardPos = new Vector2();

        for(int i = 0; i < hazards.size(); i++) {
            Hazard hazard = hazards.get(i);
            hazardPos.set(hazard.getX(), hazard.getY());

            // Set the value in our vision array to the distance between the agent and all hazards.
            // NOTE: I'm dividing by 32 as that's the tile length and I want to reduce the values
            // of the distances, as they seem very large initially.
            vision[i] = hazardPos.dst(agentPos) / 32;
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
        } else if(agent.getX() + agent.getWidth() > game.getWidth()) {
            agent.setX(game.getWidth() - agent.getWidth());
        }
        if(agent.getY() < 0) {
            agent.setY(0);
        } else if(agent.getY() >= game.getHeight()) {
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
        for(Agent agent : agents) {
            if(!agent.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the game objects back to their original positions for the next generation.
     */
    private void resetGame() {
        //TODO Note: the developer can determine if they want map objects to spawn again every
        // generation or just keep going. For the moment, I just have them going.
//        for(Hazard hazard : hazards) {
//            hazard.reset();
//        }
        for(Agent agent : agents) {
            agent.reset(game.getWidth() / 2f);
        }
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
