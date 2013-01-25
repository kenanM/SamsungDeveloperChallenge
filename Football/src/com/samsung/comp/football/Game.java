package com.samsung.comp.football;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Player.TeamColour;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.Utils;

public class Game implements ApplicationListener {

	public static final Vector2 RED_GOAL = new Vector2(337, 1000);
	public static final Vector2 BLUE_GOAL = new Vector2(337, 24);

	// TODO: Remove these and other hard coded values
	public static final float ROUND_TIME = 5;
	public static final float BALL_CHANGE_TIME = 1f;
	public static final float BALL_PASS_TIME = 1f;
	public static final float BOUNCE_ELASTICITY = 0.5f;
	public static final int VIRTUAL_SCREEN_WIDTH = 676;
	public static final int VIRTUAL_SCREEN_HEIGHT = 1024;
	// TODO: Restrict input, ball / player movement etc. to these
	public static final int PLAYING_AREA_WIDTH = 670;
	public static final int PLAYING_AREA_HEIGHT = 1024;
	// Image here isn't vertical symmetrical
	public static final Rectangle PLAYING_AREA = new Rectangle(23, 41,
			676 - 23 - 23, 1024 - 41 - 45);
	// TODO: HACK: rough area for goal here
	public static final Rectangle BLUE_GOAL_AREA = new Rectangle(290, 0, 110,
			44);
	public static final Rectangle RED_GOAL_AREA = new Rectangle(290, 980, 110,
			44);

	private int xOffset;
	private int yOffset;
	private int drawnPitchWidth;
	private int drawnPitchHeight;
	private float stretchFactor;

	public static Texture pitchTexture;
	public static Texture playTexture;
	public static Texture starFull;
	public static Texture stats;
	public static Texture goalMessage;
	public static Texture d0;
	public static Texture d1;
	public static Texture d2;
	public static Texture d3;
	public static Texture d4;
	public static Texture d5;
	public static Texture d6;
	public static Texture d7;
	public static Texture d8;
	public static Texture d9;
	public static Texture[] digits = new Texture[10];

	Sound whistleBlow;

	public enum GameState {
		INPUT, EXECUTION
	}

	private static Random rng;
	private GameState gameState = GameState.EXECUTION;
	private SpriteBatch batch;
	private BitmapFont bmf;
	private OrthographicCamera camera;

	private List<Player> redPlayers = new LinkedList<Player>();
	private List<Player> bluePlayers = new LinkedList<Player>();
	private Player redGoalie;
	private Player blueGoalie;

	private Ball ball;
	// TODO: Rename to elapsedRoundTime?
	private float totalTime = 0;
	private float goalScoredDrawTime = 0f;
	private int redScore = 0;
	private int blueScore = 0;

	private InputListener inputListener;
	private Player hoveringPlayer;

	private TeamColour humanColour;
	private TeamColour computerColour;

	@Override
	public void create() {

		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		playTexture = new Texture(Gdx.files.internal("playIcon.png"));
		starFull = new Texture(Gdx.files.internal("star.png"));
		stats = new Texture(Gdx.files.internal("stats.png"));
		goalMessage = new Texture(Gdx.files.internal("GoalScored.png"));
		d0 = new Texture(Gdx.files.internal("digits/digit0.png"));
		d1 = new Texture(Gdx.files.internal("digits/digit1.png"));
		d2 = new Texture(Gdx.files.internal("digits/digit2.png"));
		d3 = new Texture(Gdx.files.internal("digits/digit3.png"));
		d4 = new Texture(Gdx.files.internal("digits/digit4.png"));
		d5 = new Texture(Gdx.files.internal("digits/digit5.png"));
		d6 = new Texture(Gdx.files.internal("digits/digit6.png"));
		d7 = new Texture(Gdx.files.internal("digits/digit7.png"));
		d8 = new Texture(Gdx.files.internal("digits/digit8.png"));
		d9 = new Texture(Gdx.files.internal("digits/digit9.png"));

		digits[0] = d0;
		digits[1] = d1;
		digits[2] = d2;
		digits[3] = d3;
		digits[4] = d4;
		digits[5] = d5;
		digits[6] = d6;
		digits[7] = d7;
		digits[8] = d8;
		digits[9] = d9;

		whistleBlow = Gdx.audio.newSound(Gdx.files
				.internal("sound/Whistle short 2.wav"));

		Kick.create(new Texture(Gdx.files.internal("target.png")));
		Mark.create(new Texture(Gdx.files.internal("target.png")));

		Ball.create(new Texture(Gdx.files.internal("ball.png")));

		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);
		batch = new SpriteBatch();
		bmf = new BitmapFont();

		setStartingPositions();

		humanColour = TeamColour.BLUE;
		computerColour = TeamColour.RED;

		beginInputStage();

	}

	private void setStartingPositions() {
		// create the players
		redPlayers = new LinkedList<Player>();

		redPlayers.add(new RedPlayer(169, 704, 400, 100, 100, 20));
		redPlayers.add(new RedPlayer(338, 640, 300, 400, 80, 0));
		redPlayers.add(new RedPlayer(507, 704, 200, 300, 100, 0));
		redPlayers.add(new RedPlayer(338, 768, 200, 300, 100, 0));
		redGoalie = new RedPlayer(338, 900);

		bluePlayers = new LinkedList<Player>();

		bluePlayers.add(new BluePlayer(338, 384));
		bluePlayers.add(new BluePlayer(169, 320));
		bluePlayers.add(new BluePlayer(338, 256));
		bluePlayers.add(new BluePlayer(507, 320));
		blueGoalie = new BluePlayer(338, 124);

		// Create a ball
		ball = new Ball(Ball.translateBallCoordinate(VIRTUAL_SCREEN_WIDTH / 2),
				Ball.translateBallCoordinate(VIRTUAL_SCREEN_HEIGHT / 2));

		whistleBlow.play();
	}

	public void setHoveringPlayer(Player hoveringPlayer) {
		this.hoveringPlayer = hoveringPlayer;
	}

	// TODO: HACK FOR DRAWING STATS (HOVERINGPLAYER)
	int a = 0;

	@Override
	public void render() {

		update();

		// clear the screen with a dark blue color.
		Gdx.gl.glViewport(xOffset, yOffset, drawnPitchWidth, drawnPitchHeight);
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		drawSpriteBatch();
		drawShapeRenderer();

		a++;
		if (a == 100) {
			hoveringPlayer = null;
			a = 0;
		}
	}

	private void drawSpriteBatch() {
		// begin a new batch and draw the players and ball
		batch.begin();

		// draw the background pitch
		batch.draw(pitchTexture, 0, 0, VIRTUAL_SCREEN_WIDTH,
				VIRTUAL_SCREEN_HEIGHT, 0, 0, VIRTUAL_SCREEN_WIDTH,
				VIRTUAL_SCREEN_HEIGHT, false, false);

		drawPlayerScore(batch, bmf);

		if (goalScoredDrawTime > 0) {
			batch.draw(goalMessage,
					VIRTUAL_SCREEN_WIDTH / 2 - goalMessage.getWidth() / 2,
					VIRTUAL_SCREEN_HEIGHT / 2 - goalMessage.getHeight() / 2, 0,
					0, goalMessage.getWidth(), goalMessage.getHeight(), 1, 1,
					0, 0, 0, goalMessage.getWidth(), goalMessage.getHeight(),
					false, true);
		}

		if (gameState == GameState.INPUT) {
			batch.draw(playTexture, 0, 0);

			drawPlayerStats(batch, hoveringPlayer);

			for (Player player : allPlayers()) {
				drawActions(player.getAction(), batch);
			}
		} else {
			// Execution stage
		}

		for (Player player : allPlayers()) {
			player.draw(batch);
		}

		ball.draw(batch);

		batch.end();
	}

	private void drawShapeRenderer() {
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

		if (gameState == GameState.INPUT) {
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

			for (Player player : allPlayers()) {
				drawActions(player.getAction(), shapeRenderer);
			}
		} else {
			// Execution stage
		}

		shapeRenderer.end();
	}

	private void drawActions(Action action, SpriteBatch batch) {
		if (action != null) {
			action.draw(batch);
			drawActions(action.getNextAction(), batch);
		}
	}

	private void drawActions(Action action, ShapeRenderer shapeRenderer) {
		if (action != null) {
			action.draw(shapeRenderer);
			drawActions(action.getNextAction(), shapeRenderer);
		}
	}

	private void drawPlayerScore(SpriteBatch batch, BitmapFont bmf) {
		// bmf.scale(4);
		// bmf.draw(batch, "Blue: " + blueScore +" | Red: " + redScore,
		// (float)VIRTUAL_SCREEN_WIDTH/2 - 64, 20);

		Texture redCurrentScoreTexture;
		Texture blueCurrentScoreTexture;
		if (redScore > 9) {
			redCurrentScoreTexture = digits[9];
		} else {
			redCurrentScoreTexture = digits[redScore];
		}

		if (blueScore > 9) {
			blueCurrentScoreTexture = digits[9];
		} else {
			blueCurrentScoreTexture = digits[blueScore];
		}

		batch.draw(redCurrentScoreTexture, VIRTUAL_SCREEN_WIDTH / 2 - 32, 0, 0,
				0, redCurrentScoreTexture.getWidth(),
				redCurrentScoreTexture.getHeight(), 1, 1, 0, 0, 0,
				redCurrentScoreTexture.getWidth(),
				redCurrentScoreTexture.getHeight(), false, true);

		batch.draw(blueCurrentScoreTexture, VIRTUAL_SCREEN_WIDTH / 2 + 32, 0,
				0, 0, redCurrentScoreTexture.getWidth(),
				redCurrentScoreTexture.getHeight(), 1, 1, 0, 0, 0,
				redCurrentScoreTexture.getWidth(),
				redCurrentScoreTexture.getHeight(), false, true);

	}

	private void drawPlayerStats(SpriteBatch batch, Player player) {
		if (player == null) {
			return;
		}
		// BitmapFont bmf = new BitmapFont();
		// bmf.draw
		// bmf.draw(batch, "str", (float)VIRTUAL_SCREEN_WIDTH-128, 10);

		batch.draw(stats, VIRTUAL_SCREEN_WIDTH - (5 * starFull.getWidth())
				- stats.getWidth() / 2, 0, 0, 0, stats.getWidth(),
				stats.getHeight(), 1, 1, 0, 0, 0, stats.getWidth(),
				stats.getHeight(), false, true);

		for (int i = 1; i <= player.getStarsRunSpeed(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()), 0, 0, 0,
					starFull.getWidth(), starFull.getHeight(), 1, 1, 0, 0, 0,
					starFull.getWidth(), starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsShootSpeed(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					1 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsTackleSkill(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					2 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsTacklePreventionSkill(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					3 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

	}

	private void update() {
		float time = Gdx.graphics.getDeltaTime();
		totalTime += time;
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		if (inputListener.getSelectedPlayer() != null) {
			inputListener.getSelectedPlayer().highlight();
		}

		// Each action should update the player's X,Y coordines
		if (gameState == GameState.EXECUTION) {

			for (Player player : allPlayers()) {
				player.executeAction();
				player.update(time);
			}

			ball.update(time);
			ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
					VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
			tackleDetection(time);
			goalScoredDetection();

			if (totalTime >= ROUND_TIME) {
				gameState = GameState.INPUT;
				beginInputStage();
			}

		}
	}

	private void tackleDetection(float time) {
		for (Player player : allPlayers()) {

			if (player.overlaps(ball) && ball.getOwner() != player
					&& ball.getTimeSinceTackle() > BALL_CHANGE_TIME
					&& player.getTimeSinceKick() > BALL_PASS_TIME) {

				if (ball.hasOwner()) {
					if (ball.getOwner().getTeam() != player.getTeam()) {
						performTackle(player);
					}
				} else {
					ball.setOwner(player);
				}
			}
		}
	}

	private void performTackle(Player player) {
		float tackleChance = player.getTackleSkill()
				- ball.getOwner().getTacklePreventionSkill();
		float rn = Utils.randomFloat(rng, 0, 100);
		if (rn < tackleChance) {
			ball.setOwner(player);
			ball.clearTimeSinceTackle();
		}
	}

	private void goalScoredDetection() {
		if (RED_GOAL_AREA.contains(ball)) {
			blueScore++;
			setStartingPositions();
			inputListener.resetAI();
			beginInputStage();
			goalScoredDrawTime = 3f;
			// TODO: Sound: blow whistle
			// TODO: Sound: crowd cheer
		} else if (BLUE_GOAL_AREA.contains(ball)) {
			redScore++;
			setStartingPositions();
			inputListener.resetAI();
			beginInputStage();
			goalScoredDrawTime = 3f;
			// TODO: Sound: blow whistle
			// TODO: Sound: crowd cheer
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
		for (Player player : allPlayers()) {
			player.reset();
		}
	}

	public void beginInputStage() {
		gameState = GameState.INPUT;
		clearActions();
		inputListener.beginInputStage(allPlayers());
	}

	public void beginExecution() {
		Log.v("Game", "Beginning execution");
		totalTime = 0;
		this.gameState = GameState.EXECUTION;
	}

	private List<Player> allPlayers() {
		List<Player> result = new LinkedList<Player>();
		result.addAll(redPlayers);
		result.add(redGoalie);
		result.addAll(bluePlayers);
		result.add(blueGoalie);
		return result;
	}

	public TeamColour getHumanColour() {
		return humanColour;
	}

	public TeamColour getComputerColour() {
		return computerColour;
	}

	public List<Player> getHumanPlayers() {
		if (humanColour == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	public List<Player> getComputerPlayers() {
		if (computerColour == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	public boolean humanGoalieIsHoldingTheBall() {
		if (humanColour == TeamColour.RED) {
			return ball.hasOwner() && ball.getOwner() == redGoalie;
		} else {
			return ball.hasOwner() && ball.getOwner() == blueGoalie;
		}
	}

	public boolean computerGoalieIsHoldingTheBall() {
		if (computerColour == TeamColour.RED) {
			return ball.hasOwner() && ball.getOwner() == redGoalie;
		} else {
			return ball.hasOwner() && ball.getOwner() == blueGoalie;
		}
	}

	public Player getHumanGoalie() {
		if (humanColour == TeamColour.RED) {
			return redGoalie;
		} else {
			return blueGoalie;
		}
	}

	public Player getComputerGoalie() {
		if (computerColour == TeamColour.RED) {
			return redGoalie;
		} else {
			return blueGoalie;
		}
	}

	public Vector2 translateInputToField(Vector2 vector) {
		float vx = (vector.x / stretchFactor) - xOffset;
		float vy = (vector.y / stretchFactor) - yOffset;
		return new Vector2(vx, vy);
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		for (Player player : allPlayers()) {
			player.dispose();
		}
		Ball.dispose();
		Kick.dispose();
		pitchTexture.dispose();
		playTexture.dispose();
		starFull.dispose();
		stats.dispose();
		goalMessage.dispose();
		d0.dispose();
		d1.dispose();
		d2.dispose();
		d3.dispose();
		d4.dispose();
		d5.dispose();
		d6.dispose();
		d7.dispose();
		d8.dispose();
		d9.dispose();

		whistleBlow.dispose();

		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		float screenRatio = (float) width / (float) height;
		float pitchImageRatio = (float) VIRTUAL_SCREEN_WIDTH
				/ (float) VIRTUAL_SCREEN_HEIGHT;

		if (width > height) {
			// Need to draw pitch on it's side
		}

		if (screenRatio > pitchImageRatio) {
			// Borders to left and right
			stretchFactor = (float) height / (float) VIRTUAL_SCREEN_HEIGHT;
			drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * stretchFactor);
			drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * stretchFactor);
			xOffset = (width - drawnPitchWidth) / 2;
			yOffset = 0;
		} else {
			// Borders top and bottom
			stretchFactor = (float) width / (float) VIRTUAL_SCREEN_WIDTH;
			drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * stretchFactor);
			drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * stretchFactor);
			xOffset = 0;
			yOffset = (height - drawnPitchHeight) / 2;
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
