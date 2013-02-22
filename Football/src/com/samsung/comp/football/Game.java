package com.samsung.comp.football;

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
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.BlueGoalie;
import com.samsung.comp.football.Players.BluePlayer;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.Players.RedGoalie;
import com.samsung.comp.football.Players.RedPlayer;

public class Game implements ApplicationListener {

	private int result;

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

	private int xOffset;
	private int yOffset;
	private int drawnPitchWidth;
	private int drawnPitchHeight;
	private double scaleFactor;

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

	private static Random rng;
	private GameState gameState = GameState.EXECUTION;
	private GameState gameStateToGoIntoWhenBackButtonPressed = GameState.PAUSED;
	private float remainingMatchTime;
	private SpriteBatch batch;
	private BitmapFont bmf;
	private OrthographicCamera camera;

	private List<Player> redPlayers = new LinkedList<Player>();
	private List<Player> bluePlayers = new LinkedList<Player>();
	private Goalie redGoalie;
	private Goalie blueGoalie;

	private Ball ball;
	// TODO: Rename to elapsedRoundTime?
	private float totalTime = 0;
	private float goalScoredDrawTime = 0f;
	private int redScore = 0;
	private int blueScore = 0;

	private InputListener inputListener;
	private LibGDXInput input;
	private SoundManager soundManager;

	private TeamColour humanColour;
	private TeamColour computerColour;

	private AI ai;
	public PauseMenu pauseMenu;
	public Bar bar;

	@Override
	public void create() {

		Texture.setEnforcePotImages(false);

		input = new LibGDXInput(this);
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);

		endTexture = new Texture(Gdx.files.internal("endScreen.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		playTexture = new Texture(Gdx.files.internal("playIcon.png"));
		starFull = new Texture(Gdx.files.internal("star.png"));
		stats = new Texture(Gdx.files.internal("stats.png"));
		goalMessage = new Texture(Gdx.files.internal("GoalScored.png"));

		whistleBlow = Gdx.audio.newSound(Gdx.files
				.internal("sound/Whistle short 2.wav"));

		Kick.create(new Texture(Gdx.files.internal("target.png")));
		Mark.create(new Texture(Gdx.files.internal("markingIcon.png")));
		Move.create(new Texture(Gdx.files.internal("arrowhead.png")));
		MoveToPosition.create(new Texture(Gdx.files.internal("arrowhead.png")));
		Pass.create(new Texture(Gdx.files.internal("passingIcon.png")));
		Ball.create();
		Player.create(new Texture(Gdx.files.internal("exclaimationMark.png")));

		pauseMenu = new PauseMenu(this);
		bar = new Bar(this);

		// create the camera and the SpriteBatch
		// TODO these are not necessarily the dimensions we want.
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);
		batch = new SpriteBatch();
		bmf = new BitmapFont(true);
		bmf.scale(.35f);

		createNewPlayersAndBall();

		humanColour = TeamColour.BLUE;
		computerColour = TeamColour.RED;
		inputListener.initialise();

		remainingMatchTime = 3 * 60;

		ai = new AI(this);

		beginInputStage();

	}

	private void setStartingPositions() {
		redPlayers.get(0).x = Player.translatePlayerCoordinate(169);
		redPlayers.get(0).y = Player.translatePlayerCoordinate(704);

		redPlayers.get(1).x = Player.translatePlayerCoordinate(338);
		redPlayers.get(1).y = Player.translatePlayerCoordinate(640);

		redPlayers.get(2).x = Player.translatePlayerCoordinate(507);
		redPlayers.get(2).y = Player.translatePlayerCoordinate(704);

		redPlayers.get(3).x = Player.translatePlayerCoordinate(338);
		redPlayers.get(3).y = Player.translatePlayerCoordinate(768);

		redGoalie.x = Player.translatePlayerCoordinate(338);
		redGoalie.y = Player.translatePlayerCoordinate(900);

		bluePlayers.get(0).x = Player.translatePlayerCoordinate(338);
		bluePlayers.get(0).y = Player.translatePlayerCoordinate(334);

		bluePlayers.get(1).x = Player.translatePlayerCoordinate(169);
		bluePlayers.get(1).y = Player.translatePlayerCoordinate(320);

		bluePlayers.get(2).x = Player.translatePlayerCoordinate(338);
		bluePlayers.get(2).y = Player.translatePlayerCoordinate(256);

		bluePlayers.get(3).x = Player.translatePlayerCoordinate(507);
		bluePlayers.get(3).y = Player.translatePlayerCoordinate(320);

		blueGoalie.x = Player.translatePlayerCoordinate(338);
		blueGoalie.y = Player.translatePlayerCoordinate(124);

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();

		whistleBlow.play();
	}

	private void createNewPlayersAndBall() {

		// Create a ball
		ball = new Ball(Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2),
				Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2));

		// create the players
		redPlayers = new LinkedList<Player>();

		redPlayers.add(new RedPlayer(169, 704, 550, 50, 100, 40));
		redPlayers.add(new RedPlayer(338, 640, 540, 200, 80, 20));
		redPlayers.add(new RedPlayer(507, 704, 530, 150, 80, 40));
		redPlayers.add(new RedPlayer(338, 768, 520, 150, 100, 20));
		redGoalie = new RedGoalie(338, 900, this, 500);

		bluePlayers = new LinkedList<Player>();

		bluePlayers.add(new BluePlayer(338, 384));
		bluePlayers.add(new BluePlayer(169, 320));
		bluePlayers.add(new BluePlayer(338, 256));
		bluePlayers.add(new BluePlayer(507, 320));
		blueGoalie = new BlueGoalie(338, 124, this, 500);

		ai = new AI(this);

		soundManager.play(whistleBlow);
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

	private void drawSpriteBatch() {
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
							+ (goalScoredDrawTime * 20) - (3f * 20), 0,
					0, goalMessage.getWidth(), goalMessage.getHeight(), 1, 1,
					0, 0, 0, goalMessage.getWidth(), goalMessage.getHeight(),
					false, true);
		}

		if (gameState == GameState.INPUT) {

			for (Player player : allPlayers()) {
				drawActions(player.getAction(), batch);
			}

			inputListener.draw(batch);
			drawPlayerStats(batch, inputListener.getHighlightedPlayer());

			if (inputListener.getHighlightedPlayer() != null) {
				drawTimeLinePoints(inputListener.getHighlightedPlayer());
			}
			if (inputListener.getSelectedPlayer() != null) {
				drawTimeLinePoints(inputListener.getSelectedPlayer());
			}

		} else {
			// Execution stage
		}

		for (Player player : allPlayers()) {
			player.draw(batch);
		}

		ball.draw(batch);

		if (gameState == GameState.PAUSED) {
			pauseMenu.draw(batch);
		}

		batch.end();
	}

	private void drawTimeLinePoints(Player player) {
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
	}

	public String getRemainingTime() {

		int minutes = (int) remainingMatchTime / 60;
		int seconds = (int) remainingMatchTime % 60;

		String remainingTimeString = (seconds > 9) ? minutes + ":" + seconds
				: minutes + ":0" + seconds;

		return remainingTimeString;
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
		}
	}

	private void drawActions(Action action, ShapeRenderer shapeRenderer) {
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

	private void drawPlayerStats(SpriteBatch batch, Player player) {
		if (player == null) {
			return;
		}

		batch.draw(stats, VIRTUAL_SCREEN_WIDTH - (5 * starFull.getWidth())
				- stats.getWidth() / 2, starFull.getHeight(), 0, 0,
				stats.getWidth(), stats.getHeight(), 1, 1, 0, 0, 0,
				stats.getWidth(), stats.getHeight(), false, true);

		for (int i = 1; i <= player.getStarsRunSpeed(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					starFull.getHeight(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsShootSpeed(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					2 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsTackleSkill(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					3 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

		for (int i = 1; i <= player.getStarsTacklePreventionSkill(); i++) {
			batch.draw(starFull,
					VIRTUAL_SCREEN_WIDTH - (i * starFull.getWidth()),
					4 * starFull.getWidth(), 0, 0, starFull.getWidth(),
					starFull.getHeight(), 1, 1, 0, 0, 0, starFull.getWidth(),
					starFull.getHeight(), false, true);
		}

	}

	private void update() {

		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);

		if (gameState == GameState.EXECUTION) {

			totalTime += time;

			remainingMatchTime -= time;

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

			if (remainingMatchTime < 0) {
				matchFinish();
			}
		}
	}

	private void tackleDetection(float time) {
		for (Player player : allPlayers()) {

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

	private void performTackle(Player player) {
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

	private void goalScoredDetection() {
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
			setStartingPositions();
			beginInputStage();
			goalScoredDrawTime = 3f;
			// TODO: Sound: blow whistle
			// TODO: Sound: crowd cheer
		}
	}

	private void matchFinish() {

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

	public void setInputListener(InputListener inputListener) {
		this.inputListener = inputListener;
	}

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
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
		bar.setPositionToDown();
	}

	public boolean beginExecution() {
		if (gameState == GameState.PAUSED) {
			return false;
		} else {
			Log.v("Game", "Beginning execution");
			totalTime = 0;
			this.gameState = GameState.EXECUTION;
			ai.getComputerActions();
			bar.setPositionToUp();
			return true;
		}
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
		for (Player player : allPlayers()) {
			player.dispose();
		}
		Ball.dispose();
		Kick.dispose();
		Move.dispose();
		MoveToPosition.dispose();
		pitchTexture.dispose();
		playTexture.dispose();
		starFull.dispose();
		stats.dispose();
		goalMessage.dispose();
		whistleBlow.dispose();
		batch.dispose();
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
			inputListener.exitPauseState();
		} else {
			gameStateToGoIntoWhenBackButtonPressed = gameState;
			gameState = GameState.PAUSED;
			inputListener.enterPauseState();
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
