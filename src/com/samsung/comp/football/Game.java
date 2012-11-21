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

	SpriteBatch batch;
	OrthographicCamera camera;
	List<Player> players;

	@Override
	public void create() {

		redPlayerTexture = new Texture(Gdx.files.internal("redPlayer.png"));
		bluePlayerTexture = new Texture(Gdx.files.internal("bluePlayer.png"));
		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
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
			player.x = random.nextFloat() * 800;
			player.y = random.nextFloat() * 480;
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
			player.update();
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
