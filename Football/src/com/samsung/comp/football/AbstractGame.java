package com.samsung.comp.football;

import input.AbstractInputStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public abstract class AbstractGame implements ApplicationListener {

	protected int result;

	// TODO: Remove these and other hard coded values
	public static final float ROUND_TIME = 5;
	public static final float BALL_CHANGE_TIME = 1f;
	public static final float BALL_PASS_TIME = 0.5f;
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

	public static final Vector2 RED_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			975);
	public static final Vector2 BLUE_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			24);

	protected int xOffset;
	protected int yOffset;
	protected int drawnPitchWidth;
	protected int drawnPitchHeight;
	protected double scaleFactor;

	public static Texture endTexture;
	public static Texture pitchTexture;
	public static Texture playTexture;
	public static Texture starFull;
	public static Texture stats;
	public static Texture goalMessage;

	Sound whistleBlow;

	public enum GameState {
		INPUT, EXECUTION, PAUSED, FINISHED
	}

	protected static Random rng;
	protected GameState gameState = GameState.EXECUTION;
	protected GameState gameStateToGoIntoWhenBackButtonPressed = GameState.PAUSED;
	protected float remainingMatchTime;
	protected SpriteBatch batch;
	protected ShapeRenderer shapeRenderer;
	protected BitmapFont bmf;
	protected OrthographicCamera camera;

	protected List<Player> redPlayers = new LinkedList<Player>();
	protected List<Player> bluePlayers = new LinkedList<Player>();
	protected Goalie redGoalie;
	protected Goalie blueGoalie;
	protected Ball ball;

	// TODO: Rename to elapsedRoundTime?
	protected float totalTime = 0;
	protected float goalScoredDrawTime = 0f;
	protected int redScore = 0;
	protected int blueScore = 0;

	protected AbstractInputStrategy inputStrategy;
	protected LibGDXInput input;
	protected SoundManager soundManager;

	protected TeamColour humanColour;
	protected TeamColour computerColour;

	protected AI ai;
	public PauseMenu pauseMenu;
	public Bar bar;

	@Override
	public void create() {

		createLibGdxItems();
		createMainTextures();
		createSfx();
		createActions();
		createIteractiveObjects();
		createUI();
		createRenderingObjects();

		humanColour = TeamColour.BLUE;
		computerColour = TeamColour.RED;

		remainingMatchTime = 3 * 60;

		beginInputStage();

	}

	protected void createRenderingObjects() {
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
	}

	protected void createUI() {
		pauseMenu = new PauseMenu(this);
		bar = new Bar(this);
	}

	protected void createIteractiveObjects() {
		Ball.create();
		Player.create(new Texture(Gdx.files.internal("exclaimationMark.png")));
	}

	protected void createSfx() {
		whistleBlow = Gdx.audio.newSound(Gdx.files
				.internal("sound/Whistle short 2.wav"));
	}

	protected void createActions() {
		Kick.create(new Texture(Gdx.files.internal("target.png")));
		Mark.create(new Texture(Gdx.files.internal("markingIcon.png")));
		MarkBall.create(new Texture(Gdx.files.internal("markingIcon.png")));
		Move.create(new Texture(Gdx.files.internal("arrowhead.png")));
		MoveToPosition.create(new Texture(Gdx.files.internal("arrowhead.png")));
		Pass.create(new Texture(Gdx.files.internal("passingIcon.png")));
	}

	protected void createMainTextures() {
		endTexture = new Texture(Gdx.files.internal("endScreen.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		playTexture = new Texture(Gdx.files.internal("playIcon.png"));
		starFull = new Texture(Gdx.files.internal("star.png"));
		stats = new Texture(Gdx.files.internal("stats.png"));
		goalMessage = new Texture(Gdx.files.internal("GoalScored.png"));
	}

	protected void createLibGdxItems() {
		Texture.setEnforcePotImages(false);

		input = new LibGDXInput(this);
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);
	}

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
	}

	protected void drawSpriteBatch() {
		// begin a new batch and draw the players and ball
		batch.begin();

		if (gameState == GameState.FINISHED) {
			batch.draw(endTexture,
					VIRTUAL_SCREEN_WIDTH / 2 - endTexture.getWidth() / 2,
					VIRTUAL_SCREEN_HEIGHT / 2 - endTexture.getHeight() / 2,
					endTexture.getWidth(), endTexture.getHeight());

			String score = "Red: " + redScore + "   Blue: " + blueScore;
			// TODO: These screen positions are a little off, Fix them.
			bmf.draw(batch, score, (float) VIRTUAL_SCREEN_WIDTH / 3,
					VIRTUAL_SCREEN_HEIGHT / 3);
			batch.end();
			return;
		}

		// draw the background pitch
		batch.draw(pitchTexture, 0, 0, VIRTUAL_SCREEN_WIDTH,
				VIRTUAL_SCREEN_HEIGHT, 0, 0, VIRTUAL_SCREEN_WIDTH,
				VIRTUAL_SCREEN_HEIGHT, false, false);

		bar.draw(batch);

		if (goalScoredDrawTime > 0) {
			batch.draw(goalMessage,
					VIRTUAL_SCREEN_WIDTH / 2 - goalMessage.getWidth() / 2,
					(VIRTUAL_SCREEN_HEIGHT / 2 - goalMessage.getHeight() / 2)
							+ (goalScoredDrawTime * 20) - (3f * 20), 0, 0,
					goalMessage.getWidth(), goalMessage.getHeight(), 1, 1, 0,
					0, 0, goalMessage.getWidth(), goalMessage.getHeight(),
					false, true);
		}

		if (gameState == GameState.INPUT) {

			for (Player player : getAllPlayers()) {
				drawActions(player.getAction(), batch);
			}

			inputStrategy.draw(batch);

		} else {
			// Execution stage
		}

		for (Player player : getAllPlayers()) {
			player.draw(batch);
		}

		ball.draw(batch);

		if (gameState == GameState.PAUSED) {
			pauseMenu.draw(batch);
		}

		batch.end();
	}

	public void drawTimeLinePoints(Player player) {
		try {
			Vector2[] points = player.getTimeLinePoints();

			if (points == null) {
				return;
			} else {
				for (int i = 0; i < points.length; i++) {
					if (points[i] != null) {
						bmf.draw(batch, String.valueOf(i + 1), points[i].x,
								points[i].y);
					}
				}
			}
		} catch (NullPointerException e) {
			return;
		}
	}

	public String getRemainingTime() {

		int minutes = (int) remainingMatchTime / 60;
		int seconds = (int) remainingMatchTime % 60;

		String remainingTimeString = (seconds > 9) ? minutes + ":" + seconds
				: minutes + ":0" + seconds;

		return remainingTimeString;
	}

	protected void drawShapeRenderer() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

		if (gameState == GameState.INPUT) {
			shapeRenderer.setColor(255, 255, 255, 255);
			List<Vector2> lineInProgress = inputStrategy.getLineBeingDrawn();

			for (int i = 0; i < lineInProgress.size() - 1; i++) {
				Vector2 a = lineInProgress.get(i);
				Vector2 b = lineInProgress.get(i + 1);
				// TODO: Fix this
				if (a == null || b == null)
					continue;
				shapeRenderer.line(a.x, a.y, b.x, b.y);
			}

			shapeRenderer.setColor(0, 0, 0, 0);

			for (Player player : getAllPlayers()) {
				drawActions(player.getAction(), shapeRenderer);
			}

		} else {
			// Execution stage
		}

		shapeRenderer.end();
	}

	protected void drawActions(Action action, SpriteBatch batch) {
		if (action != null) {
			action.draw(batch);
		}
	}

	protected void drawActions(Action action, ShapeRenderer shapeRenderer) {
		if (action != null) {
			action.draw(shapeRenderer);
			if (action.getNextAction() != null) {
				drawActions(action.getNextAction(), shapeRenderer);
			}
		}
	}

	public int getRedScore() {
		return redScore;
	}

	public int getBlueScore() {
		return blueScore;
	}

	public void drawPlayerStats(SpriteBatch batch, Player player) {
		try {

			batch.draw(stats, VIRTUAL_SCREEN_WIDTH - stats.getWidth(),
					(VIRTUAL_SCREEN_HEIGHT - (5 * starFull.getHeight())), 0, 0,
					stats.getWidth(), stats.getHeight(), 1, 1, 0, 0, 0,
					stats.getWidth(), stats.getHeight(), false, true);

			for (int i = 1; i <= player.getStarsRunSpeed(); i++) {
				batch.draw(starFull,
						VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
						(VIRTUAL_SCREEN_HEIGHT - (5 * starFull.getHeight())),
						0, 0, starFull.getWidth(), starFull.getHeight(), 1, 1,
						0, 0, 0, starFull.getWidth(), starFull.getHeight(),
						false, true);
			}

			for (int i = 1; i <= player.getStarsShootSpeed(); i++) {
				batch.draw(starFull,
						VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
						(VIRTUAL_SCREEN_HEIGHT - (4 * starFull.getHeight())),
						0, 0, starFull.getWidth(), starFull.getHeight(), 1, 1,
						0, 0, 0, starFull.getWidth(), starFull.getHeight(),
						false, true);
			}

			for (int i = 1; i <= player.getStarsTackleSkill(); i++) {
				batch.draw(starFull,
						VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
						(VIRTUAL_SCREEN_HEIGHT - (3 * starFull.getHeight())),
						0, 0, starFull.getWidth(), starFull.getHeight(), 1, 1,
						0, 0, 0, starFull.getWidth(), starFull.getHeight(),
						false, true);
			}

			for (int i = 1; i <= player.getStarsTacklePreventionSkill(); i++) {
				batch.draw(starFull,
						VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
						(VIRTUAL_SCREEN_HEIGHT - (2 * starFull.getHeight())),
						0, 0, starFull.getWidth(), starFull.getHeight(), 1, 1,
						0, 0, 0, starFull.getWidth(), starFull.getHeight(),
						false, true);
			}

			for (int i = 1; i <= player.getStarsSavingSkill(); i++) {
				batch.draw(starFull,
						VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
						(VIRTUAL_SCREEN_HEIGHT - (1 * starFull.getHeight())),
						0, 0, starFull.getWidth(), starFull.getHeight(), 1, 1,
						0, 0, 0, starFull.getWidth(), starFull.getHeight(),
						false, true);
			}
		} catch (NullPointerException e) {
			return;
		}
	}

	protected void update() {

		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);

		if (gameState == GameState.EXECUTION) {

			totalTime += time;

			remainingMatchTime -= time;

			for (Player player : getAllPlayers()) {
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

			if (remainingMatchTime < 0) {
				matchFinish();
			}
		}
	}

	protected void tackleDetection(float time) {
		for (Player player : getAllPlayers()) {

			if (player.getTackleHitbox().overlaps(ball)
					&& ball.getOwner() != player
					&& !(ball.getOwner() instanceof Goalie)
					&& ball.getTimeSinceTackle() > BALL_CHANGE_TIME
					&& player.getTimeSinceKick() > BALL_PASS_TIME) {

				if (ball.hasOwner()) {
					if (ball.getOwner().getTeam() != player.getTeam()) {
						performTackle(player);
					}
				} else {
					float delta = ball.getSpeed() - player.getSavingSkill();
					float rn = Utils.randomFloat(rng, 0, 100);

					if (rn > delta) {
						ball.setOwner(player);
					} else {
						// failed to collect ball
						player.setNoticationTime(.75f);
						player.setTimeSinceKick(.75f);
					}
				}
			}
		}
	}

	protected void performTackle(Player player) {
		float tackleChance = player.getTackleSkill()
				- ball.getOwner().getTacklePreventionSkill();
		float rn = Utils.randomFloat(rng, 0, 100);
		if (rn < tackleChance) {
			ball.setOwner(player);
			ball.clearTimeSinceTackle();
		} else {
			// failed the tackle
			player.setNoticationTime(.75f);
			player.setTimeSinceKick(.75f);
		}
	}

	protected void goalScoredDetection() {
		boolean goalScored = false;
		if (RED_GOAL_AREA.contains(ball)) {
			if (ball.hasOwner() && ball.getOwner() == redGoalie) {
				// do nothing
			} else {
				blueScore++;
				goalScored = true;
			}
		} else if (BLUE_GOAL_AREA.contains(ball)) {
			if (ball.hasOwner() && ball.getOwner() == blueGoalie) {
				// do nothing
			} else {
				redScore++;
				goalScored = true;
			}
		}

		if (goalScored) {
			beginInputStage();
			goalScoredDrawTime = 3f;
			// TODO: Sound: blow whistle
			// TODO: Sound: crowd cheer
		}
	}

	protected void matchFinish() {

		gameState = GameState.FINISHED;

		bmf.setColor(Color.BLACK);
		bmf.setScale(3);

		if (blueScore > redScore && getHumanColour() == TeamColour.BLUE) {
			result = 1;
		} else if (blueScore > redScore && getHumanColour() == TeamColour.RED) {
			result = 1;
		} else if (blueScore == redScore) {
			result = 0;
		} else {
			result = -1;
		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public Ball getBall() {
		return ball;
	}

	public void setInputStrategy(AbstractInputStrategy inputStrategy) {
		this.inputStrategy = inputStrategy;
	}

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public void clearActions() {
		for (Player player : getAllPlayers()) {
			player.reset();
		}
	}

	public void beginInputStage() {
		gameState = GameState.INPUT;
		inputStrategy.deselectPlayers();
		clearActions();
		bar.setPositionToDown();
	}

	public void beginExecution() {
		Log.i("Game", "Beginning execution");
		totalTime = 0;
		this.gameState = GameState.EXECUTION;
		ai.getComputerActions();
		bar.setPositionToUp();
		// inputStrategy.deselectPlayers();
	}

	public List<Player> getAllPlayers() {
		List<Player> result = new LinkedList<Player>();
		result.addAll(redPlayers);
		result.add(redGoalie);
		result.addAll(bluePlayers);
		result.add(blueGoalie);
		return result;
	}

	public Player getSelectedPlayer() {
		return inputStrategy.getSelectedPlayer();
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

	public Bar getBar() {
		return bar;
	}

	public Vector2 translateInputToField(Vector2 vector) {
		double vx = (vector.x / scaleFactor) - xOffset;
		double vy = (vector.y / scaleFactor) - yOffset;
		return new Vector2((float) vx, (float) vy);
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		for (Player player : getAllPlayers()) {
			player.dispose();
		}
		Ball.dispose();
		Kick.dispose();
		Move.dispose();
		MoveToPosition.dispose();
		Mark.dispose();
		MarkBall.dispose();
		pitchTexture.dispose();
		playTexture.dispose();
		starFull.dispose();
		stats.dispose();
		goalMessage.dispose();
		whistleBlow.dispose();
		batch.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		double screenRatio = (double) width / (double) height;
		double pitchImageRatio = (double) VIRTUAL_SCREEN_WIDTH
				/ (double) VIRTUAL_SCREEN_HEIGHT;

		if (width > height) {
			// Need to draw pitch on it's side
		}

		if (screenRatio > pitchImageRatio) {
			// Borders to left and right
			scaleFactor = (double) height / (double) VIRTUAL_SCREEN_HEIGHT;
			drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * scaleFactor);
			drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * scaleFactor);
			xOffset = (width - drawnPitchWidth) / 2;
			yOffset = 0;
		} else {
			// Borders top and bottom
			scaleFactor = (double) width / (double) VIRTUAL_SCREEN_WIDTH;
			drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * scaleFactor);
			drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * scaleFactor);
			xOffset = 0;
			yOffset = (height - drawnPitchHeight) / 2;
		}
	}

	public void backButtonPressed() {
		Log.i("GameState", "Back button pressed");
		if (gameState == GameState.FINISHED) {
			Gdx.app.exit();
		} else if (gameState == GameState.PAUSED) {
			gameState = gameStateToGoIntoWhenBackButtonPressed;
		} else {
			gameStateToGoIntoWhenBackButtonPressed = gameState;
			gameState = GameState.PAUSED;
		}
		Log.i("GameState", gameState.toString());
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
