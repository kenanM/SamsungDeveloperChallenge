package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Player.TeamColour;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Move;

public class Game implements ApplicationListener {

	public static final long ROUND_TIME = 5;

	public static Texture redPlayerTexture;
	public static Texture bluePlayerTexture;
	public static Texture pitchTexture;
	public static Texture blueHoverTexture;
	public static Texture redHoverTexture;

	public enum GameState {
		INPUT, EXECUTION
	}

	private GameState gameState = GameState.EXECUTION;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private List<Player> players;
	private List<Action> actions = new ArrayList<Action>();
	private Ball ball;
	private float totalTime = 0;

	private InputListener inputListener;

	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 1200;

	@Override
	public void create() {

		redPlayerTexture = new Texture(Gdx.files.internal("redPlayer.png"));
		bluePlayerTexture = new Texture(Gdx.files.internal("bluePlayer.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		redHoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		blueHoverTexture = new Texture(Gdx.files.internal("blue hover.png"));

		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(true, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();

		// create the players and test actions
		players = new ArrayList<Player>(10);
		players.add(new Player(TeamColour.RED, 400, 700));
		players.add(new Player(TeamColour.RED, 200, 800));
		players.add(new Player(TeamColour.RED, 600, 800));
		players.add(new Player(TeamColour.RED, 400, 850));
		players.add(new Player(TeamColour.RED, 400, 1100));

		players.add(new Player(TeamColour.BLUE, 400, 500));
		players.add(new Player(TeamColour.BLUE, 200, 400));
		players.add(new Player(TeamColour.BLUE, 600, 400));
		players.add(new Player(TeamColour.BLUE, 400, 350));
		players.add(new Player(TeamColour.BLUE, 400, 100));

		// Create a ball
		ball = new Ball(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
		// actions.add(new Kick(ball, 250, 720));

		beginInputStage();

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
		batch.draw(pitchTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 700,
				1024, false, false);

		for (Player player : players) {
			batch.draw(player.getTexture(), player.x, player.y);
			if (player.isHighlighted()) {
				batch.draw(player.getHighlightTexture(), player.x - 16,
						player.y - 16);
			}
		}
		batch.draw(ball.getTexture(), ball.x, ball.y);
		batch.end();

		if (gameState == GameState.INPUT) {
			ShapeRenderer shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(255, 255, 255, 255);
			List<Vector2> lineInProgress = inputListener.getLineBeingDrawn();
			for (int i = 0; i < lineInProgress.size() - 1; i++) {
				Vector2 a = lineInProgress.get(i);
				Vector2 b = lineInProgress.get(i + 1);
				shapeRenderer.line(a.x, a.y, b.x, b.y);
			}
			shapeRenderer.setColor(0, 0, 0, 0);
			for(Action action:actions){
				if (action instanceof Move) {
					Move movement = (Move) action;
					Vector2[] path = movement.getPath();
					for(int i=0;i<path.length-1;i++) {
						shapeRenderer.line(path[i].x, path[i].y, path[i+1].x, path[i+1].y);
					}
				}
			}
			shapeRenderer.end();

		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setInputListener(InputListener inputListener) {
		this.inputListener = inputListener;
	}

	public void clearActions(){
		actions.clear();
	}
	
	public void addAction(Action action){
		actions.add(action);
	}
	
	public void beginInputStage() {
		gameState = GameState.INPUT;
		inputListener.beginInputStage(players);
	}

	public void beginExecution() {
		Log.v("Game", "Beginning execution");
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
