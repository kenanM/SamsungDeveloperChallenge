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
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Move;

public class Game implements ApplicationListener {

	//TODO: Remove these and other hard coded values
	public static final long ROUND_TIME = 5;
	public static final int PITCH_IMAGE_WIDTH = 670;
	public static final int PITCH_IMAGE_HEIGHT = 1024;
	//TODO: Restrict input, ball / player movement etc. to these
	public static final int PLAYING_AREA_WIDTH = 670;
	public static final int PLAYING_AREA_HEIGHT = 1024;
	
	public int xOffset;
	public int yOffset;
	public int drawnPitchWidth;
	public int drawnPitchHeight;
		
	public static Texture redPlayerTexture;
	public static Texture bluePlayerTexture;
	public static Texture pitchTexture;
	public static Texture blueHoverTexture;
	public static Texture redHoverTexture;
	public static Texture targetTexture;
	public static Texture playTexture;

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

	@Override
	public void create() {

		targetTexture = new Texture(Gdx.files.internal("target.png"));
		redPlayerTexture = new Texture(Gdx.files.internal("redPlayer.png"));
		bluePlayerTexture = new Texture(Gdx.files.internal("bluePlayer.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		redHoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		blueHoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		playTexture = new Texture(Gdx.files.internal("playIcon.png"));

		// create the camera and the SpriteBatch
		// TODO ese are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(true, PITCH_IMAGE_WIDTH,
				PITCH_IMAGE_HEIGHT);
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
		ball = new Ball(
				Ball.translateBallCoordinate(Gdx.graphics.getWidth() / 2),
				Ball.translateBallCoordinate(Gdx.graphics.getHeight() / 2));
		// actions.add(new Kick(ball, 250, 720));

		beginInputStage();

	}

	@Override
	public void render() {
		// clear the screen with a dark blue color.
		// TODO we need to set a background image of a football pitch
		Gdx.gl.glViewport(xOffset, yOffset, drawnPitchWidth, drawnPitchHeight);
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
			

			tackling();
			
		}


		// TODO: This currently draws the ball to the top left of the player.
		// This will not be centered if the ball texture doesn't match the
		// player.
		if (ball.getOwner() != null) {
			ball.x = ball.getOwner().x;
			ball.y = ball.getOwner().y;
		}

		// begin a new batch and draw the players and ball
		batch.begin();

		// draw the background pitch
		batch.draw(pitchTexture, 0, 0, PITCH_IMAGE_WIDTH,
				PITCH_IMAGE_HEIGHT, 0, 0, PITCH_IMAGE_WIDTH, PITCH_IMAGE_HEIGHT, false, false);
		batch.end();

		batch.begin();
		for (Player player : players) {
			batch.draw(player.getTexture(), player.x, player.y);
			if (player.isHighlighted()) {
				// TODO: Hard coded value needs removing from rendering the
				// hover texture. Centre point of hover texture should be centre
				// of player
				batch.draw(player.getHighlightTexture(), player.x - 16,
						player.y - 16);
			}
		}
		batch.end();

		if (inputListener.getSelectedPlayer() != null) {
			inputListener.getSelectedPlayer().highlight();
		}

		batch.begin();
		batch.draw(ball.getTexture(), ball.x, ball.y);
		batch.end();

		if (gameState == GameState.INPUT) {
			batch.begin();
			batch.draw(playTexture, 0, 0);
			batch.end();

			ShapeRenderer shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(255, 255, 255, 255);
			List<Vector2> lineInProgress = inputListener.getLineBeingDrawn();

			for (int i = 0; i < lineInProgress.size() - 1; i++) {
				Vector2 a = lineInProgress.get(i);
				Vector2 b = lineInProgress.get(i + 1);
				// TODO: Fix this
				if (a == null || b == null)
					continue;
				shapeRenderer.line(a.x, a.y, b.x, b.y);
			}
			shapeRenderer.setColor(0, 0, 0, 0);
			for (Action action : actions) {
				if (action instanceof Move) {
					Move movement = (Move) action;
					Vector2[] path = movement.getPath();
					for (int i = 0; i < path.length - 1; i++) {
						shapeRenderer.line(path[i].x, path[i].y, path[i + 1].x,
								path[i + 1].y);
					}
				}

				if (action instanceof Kick) {
					Kick kick = (Kick) action;
					Vector2 target = kick.getTarget();
					batch.begin();
					// TODO: need a util method to calculate offsets based on
					// texture
					batch.draw(targetTexture,
							target.x - (targetTexture.getHeight() / 2),
							target.y - (targetTexture.getWidth() / 2));
					batch.end();
				}
			}
			shapeRenderer.end();
		} else {
			// Execution stage

		}
	}

	private void tackling() {
		for (Player player : players) {
			Boolean takePossesion = false;

			if (player.overlaps(ball)) {
				takePossesion = true;
			}

			if (takePossesion) {
				for (Action action : actions) {
					if (action instanceof Kick) {
						Kick kick = (Kick) action;
						kick.cancel();
					}
				}
				ball.setOwner(player);
			}
		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public Ball getBall() {
		return ball;
	}

	public void setInputListener(InputListener inputListener) {
		this.inputListener = inputListener;
	}

	public void clearActions() {
		actions.clear();
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	public void beginInputStage() {
		gameState = GameState.INPUT;
		inputListener.beginInputStage(players);
	}

	public void beginExecution() {
		Log.v("Game", "Beginning execution " + actions.size() + " actions.");
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
		float screenRatio = (float)width / (float)height;
		float pitchImageRatio = (float) PITCH_IMAGE_WIDTH / (float)PITCH_IMAGE_HEIGHT;
		
		float stretchFactor;

		
		if (width > height) {
			// Need to draw pitch on it's side
		}

		if (screenRatio > pitchImageRatio) {
			// Borders to left and right
			stretchFactor = (float)height / (float)PITCH_IMAGE_HEIGHT;
			drawnPitchWidth = (int) (PITCH_IMAGE_WIDTH * stretchFactor); 
			drawnPitchHeight = (int) (PITCH_IMAGE_HEIGHT * stretchFactor);
			xOffset = (width - drawnPitchWidth) / 2;
			yOffset = 0;
		} else {
			// Borders top and bottom
			stretchFactor = (float)width / (float)PITCH_IMAGE_WIDTH;
			drawnPitchWidth = (int) (PITCH_IMAGE_WIDTH * stretchFactor);
			drawnPitchHeight = (int) (PITCH_IMAGE_HEIGHT * stretchFactor); 
			xOffset = 0;
			yOffset = (height - drawnPitchHeight)/2;
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	

}
