package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.samsung.comp.football.Player.TeamColour;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Move;

public class Game implements ApplicationListener {

	public static final long ROUND_TIME = 5;

	public static Texture redPlayerTexture;
	public static Texture bluePlayerTexture;
	public static Texture pitchTexture;
	private static Texture highlightTexture;

	public enum GameState {
		INPUT, EXECUTION
	}

	private GameState gameState = GameState.INPUT;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private List<Player> players;
	private List<Action> actions;
	private Ball ball;
	private float totalTime = 0;

	private InputListener inputListener;

	@Override
	public void create() {

		redPlayerTexture = new Texture(Gdx.files.internal("redPlayer.png"));
		bluePlayerTexture = new Texture(Gdx.files.internal("bluePlayer.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		highlightTexture = new Texture(Gdx.files.internal("highlight.png"));

		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		// create the players and test actions
		players = new ArrayList<Player>(10);

		for (int i = 0; i < 5; i++) {
			players.add(new Player(TeamColour.RED));
			players.add(new Player(TeamColour.BLUE));
		}

		// Create a TestAction for each player
		actions = new ArrayList<Action>();
		for (Player player : players) {
			actions.add(new Move(player));
		}

		// Create a ball
		ball = new Ball(240, 400);
		actions.add(new Kick(ball, 250, 720));

		// Randomly assign each player an X,Y coordinate
		Random random = new Random();
		for (Player player : players) {
			player.x = random.nextFloat() * 480;
			player.y = random.nextFloat() * 800;
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

		float time = Gdx.graphics.getDeltaTime();
		totalTime += time;

		if (totalTime >= ROUND_TIME && gameState == GameState.EXECUTION) {
			gameState = GameState.INPUT;
			inputListener.beginInputStage(players);
		}

		// Each action should update the player's X,Y coordines
		if (gameState == GameState.EXECUTION) {
			for (Action action : actions) {
				action.executeNextStep(time);
			}
		}

		// begin a new batch and draw the players and ball
		batch.begin();
		// draw the background pitch
		batch.draw(pitchTexture, 0, 0, 740, 800);

		for (Player player : players) {
			batch.draw(player.getTexture(), player.x, player.y);
			if (player.isHighlighted()) {
				batch.draw(highlightTexture, player.x, player.y);
			}
		}
		batch.draw(ball.getTexture(), ball.x, ball.y);
		batch.end();
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setInputListener(InputListener inputListener) {
		this.inputListener = inputListener;
	}

	public void beginInputStage() {
		gameState = GameState.INPUT;
		inputListener.beginInputStage(players);
	}

	public void beginExecution(List<Action> actions) {
		Log.v("Game", "Beginning execution");
		// this.actions=actions;
		totalTime = 0;
		this.gameState = GameState.EXECUTION;
	}

	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		bluePlayerTexture.dispose();
		redPlayerTexture.dispose();
		pitchTexture.dispose();
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
