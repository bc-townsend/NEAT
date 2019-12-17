package com.mygdx.kittener.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Class which controls the screen we are viewing in our game and holds all sprites, fonts, and
 * the application width and height.
 * @author Brandon Townsend
 * @version 16 December 2019
 */
public class MainGame extends com.badlogic.gdx.Game {
    /** The width of the application window. */
    private int width;

    /** The height of the application window. */
    private int height;

    /** Sprite batch used to draw 2D shapes. */
    public SpriteBatch batch;

    /** Text for displaying score and any other information needed. */
    public BitmapFont font;

    /** The screen for the actual Kittener game. */
    public Screen gameScreen;

    /** The screen for the main menu of the game. */
    public Screen mainMenuScreen;

    /**
     * Passing the application window size to the game.
     * @param width The width of the window.
     * @param height The height of the window.
     */
    public MainGame(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Returns the width of the application window.
     * @return The width of the application window.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the application window.
     * @return The height of the application window.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Method called once the application is created. Assets are usually loaded here.
     */
    @Override
    public void create() {
        // Creation of the sprite batch.
        batch = new SpriteBatch();

        // Creation of the font.
        font = new BitmapFont();

        // Setting of screens.
        mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(mainMenuScreen);
    }

    /**
     * Method called by the game loop from the application every time rendering should be
     * performed. Renders the screen we set in create().
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * On desktop this is called before the dispose() when exiting the application.
     * NOTE: Good place to save the game state.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * Called when the application is destroyed. Preceded by a call to pause(). Disposes all
     * objects which are not otherwise handled by the Java garbage collector.
     */
    @Override
    public void dispose() {
        gameScreen.dispose();
        batch.dispose();
        font.dispose();
    }
}
