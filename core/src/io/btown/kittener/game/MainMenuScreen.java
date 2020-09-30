package io.btown.kittener.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Class which represents the first screen that users see, the main menu screen. Gives players to
 * option to start a new simulation or load from an older one.
 * @author Brandon Townsend
 * @version 17 January 2020
 */
public class MainMenuScreen extends ScreenAdapter {
    /** A reference the game class from which we can switch screens. */
    private final MainGame GAME;

    /** A camera object to look at the game. */
    OrthographicCamera camera;

    /** A map renderer for the background. */
    OrthogonalTiledMapRenderer renderer;

    /**
     * Constructor for the main menu screen.
     * @param game Reference for the game class.
     */
    public MainMenuScreen(final MainGame game) {
        GAME = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME.getWidth(), GAME.getHeight());
        TiledMap background = new TmxMapLoader().load("core/assets/maps/map_no_water.tmx");
        renderer = new OrthogonalTiledMapRenderer(background, 1);
    }

    /**
     * Renders the main menu screen.
     * @param delta Time between frames.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Updates the camera and sets our view.
        camera.update();
        renderer.setView(camera);
        renderer.render();
        GAME.batch.setProjectionMatrix(camera.combined);

        // Begins what we should update to the screen.
        GAME.batch.begin();

        // The string of text for the menu.
        String menuText = "Welcome to Kittener\nPress (1) to start\nPress (2) to load";
        GAME.font.draw(GAME.batch, menuText, 4, 248);
        GAME.batch.end();

        // If the (1) key is pressed, start a new game.
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            GAME.setScreen(GAME.gameScreen);
            dispose();
        }

        // If the (2) key is pressed, load from a saved game.
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            System.out.println("Feature not implemented yet");
        }
    }
}
