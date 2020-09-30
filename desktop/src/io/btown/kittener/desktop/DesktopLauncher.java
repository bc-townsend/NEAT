package io.btown.kittener.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import io.btown.kittener.game.MainGame;

/**
 * Launches the game of "Kittener", which is based upon the classic arcade game "Frogger".
 * @author Chance Simmons and Brandon Townsend
 * @version 16 December 2019
 */
public class DesktopLauncher {
	/** The pixel height and height of a square block, representing a game tile. */
	private static final int BLOCK_SIZE = 32;

	/** The number of blocks our game will be wide. */
	private static final int WIDTH = 16;

	/** The number of blocks our game will be tall. */
	private static final int HEIGHT = 12;

	/**
	 * Driver for our application.
	 * @param args Arguments for our application (if any).
	 */
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("core/assets/cat_front.png", Files.FileType.Internal);
		config.useGL30					= false;
		config.forceExit 				= true;
		config.foregroundFPS 			= 60;
		config.height 					= BLOCK_SIZE * HEIGHT;
		config.width 					= BLOCK_SIZE * WIDTH;
		config.initialBackgroundColor 	= Color.BLACK;
		config.resizable 				= true;
		config.title 					= "NEAT Kittener";
		config.x = 0;
		config.y = 0;

		ApplicationListener game = new MainGame(config.width, config.height);
		Application app = new LwjglApplication(game, config);
	}
}
