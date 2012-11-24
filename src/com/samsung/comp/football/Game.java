package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.samsung.comp.football.Player.TeamColour;

public class Game implements ApplicationListener {

	public static Texture redPlayerTexture;
	public static Texture bluePlayerTexture;

	
	public enum GameState {
		INPUT, EXECUTION
	}
	
	SpriteBatch batch;
	OrthographicCamera camera;
	List<Player> players;
	private GameState gameState = GameState.INPUT;
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public void create() {
		redPlayerTexture = new Texture(Gdx.files.internal("redPlayer.png"));
		bluePlayerTexture = new Texture(Gdx.files.internal("bluePlayer.png"));
		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		int displayWidth = Gdx.graphics.getWidth();
		int displayHeight = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, displayWidth, displayHeight);
		batch = new SpriteBatch();

		// create the players
		players = new ArrayList<Player>(10);
		for (int i = 0; i < 5; i++) {
			players.add(new Player(TeamColour.RED));
			players.add(new Player(TeamColour.BLUE));
		}

		// Randomly assign each player an X,Y coordinate
		Random random = new Random();
		for (Player player : players) {
			
			// Resolution based on display area (float)
//			float x = random.nextFloat() * displayWidth;
//			float y = random.nextFloat() * displayHeight;
			
			// Fixed resolution
//			player.x = random.nextFloat() * 800;
//			player.y = random.nextFloat() * 480;
			
			// Resolution based on display area (int)
			int x = random.nextInt(displayWidth);
			int y = random.nextInt(displayHeight);
			
			player.setPlayerPosition(x, y);
			// Need to run a player.update() to set the draw locations 
			player.update();
			
		}
	}

	@Override
	public void render() {
		// clear the screen with a dark blue color.
		// TODO we need to set a background image of a football pitch
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// Update each players position/status
		for (Player player : players) {			
			if (gameState == GameState.EXECUTION){
				player.update();
				
				// Check if all actions are complete, end the execution phase if true.
				boolean allActionsComplete=true;
				for (Action action : player.getActions() ) {
					allActionsComplete = allActionsComplete && action.isComplete();
				}
				
				if (allActionsComplete){
					gameState = GameState.INPUT;
				}
			}
			
		}


		// begin a new batch and draw the players
		batch.begin();
		for (Player player : players) {
			batch.draw(player.getTexture(), player.x, player.y);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		bluePlayerTexture.dispose();
		redPlayerTexture.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
