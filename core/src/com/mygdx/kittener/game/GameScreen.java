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

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends ScreenAdapter {
    /** Reference to the game class which 'runs' the game. */
    private final MainGame game;

    /** The constant number of agents we should spawn. */
    private final int NUM_AGENTS = 20;

    /** A timer in which we delay the spawning of the next generation. */
    private float delayTimer;

    /** Left-side bound of the map for spawning hazards/platforms. */
    private int leftBounds;

    /** Right-side bound of the map for spawning hazards/platforms. */
    private int rightBounds;

    /** The camera attached to this screen. */
    private OrthographicCamera camera;

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
//    private ArrayList<Hazard> hazards;

    /** List of all agents in the game. */
    private ArrayList<Agent> agents;

    /** Map renderer. */
    private TiledMapRenderer renderer;

    public GameScreen(final MainGame game) {
        this.game = game;

        // Setting up some needed game variables.
        delayTimer = 0.0f;
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

        // Creating the game agents.
        agents = new ArrayList<>(NUM_AGENTS);
        spawnAgents();

        // Creating the tile map background.
        TiledMap map = new TmxMapLoader().load("core/assets/maps/map_no_water.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1);
    }

    private void spawnAgents() {
        for(int i = 0; i < NUM_AGENTS; i++) {
            Agent agent = new Agent(i, catBack, 0);
            agents.add(agent);
        }
    }

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

        // Draws all agents.
        for(Agent agent : agents) {
            game.batch.draw(agent.getTexture(), agent.x, agent.y);
        }

        // Outputs statistics to the screen.
        String stats = String.format("High Score: %s\nGeneration: %d", 1000, 1);
        game.font.draw(game.batch, stats, 5, 64);

        // Ending our sprite batch.
        game.batch.end();

        // Each agent moving.
        for(Agent agent : agents) {

            // Perform the following if the agent is not dead.
            if(!agent.isDead()) {
                int dir = 0;

                switch(dir) {
                    case 0: // Moving up.
                        agent.y += Speeds.RIGHT_MED.move(delta);
                        agent.setTexture(catBack);
                        break;
                    case 1: // Moving down.
                        agent.y += Speeds.LEFT_MED.move(delta);
                        agent.setTexture(catFront);
                        break;
                    case 2: // Moving left.
                        agent.x += Speeds.LEFT_MED.move(delta);
                        agent.setTexture(catLeft);
                        break;
                    case 3: // Moving right.
                        agent.x += Speeds.RIGHT_MED.move(delta);
                        agent.setTexture(catRight);
                        break;
                    default: // Choosing not to move.
                        break;
                }
            }
        }
    }
}
