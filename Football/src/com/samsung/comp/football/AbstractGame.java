package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
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
import com.samsung.comp.events.ButtonPressListener;
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
import com.samsung.spensdk.applistener.SPenHoverListener;

public abstract class AbstractGame implements ApplicationListener,
		InputProcessor, SPenHoverListener {

	protected int result;

	// TODO: Remove these and other hard coded values
	public static final float ROUND_TIME = 5;
	public static final float BALL_CHANGE_TIME = 1f;
	public static final float BALL_PASS_TIME = 0.5f;
	public static final float BOUNCE_ELASTICITY = 0.5f;
	public static final float INPUT_EPSILON_VALUE = 32;
	public static final int VIRTUAL_SCREEN_WIDTH = 676;
	public static final int VIRTUAL_SCREEN_HEIGHT = 1024;
	// TODO: Restrict input, ball / player movement etc. to these
	public static final int PLAYING_AREA_WIDTH = 670;
	public static final int PLAYING_AREA_HEIGHT = 1024;

	// TODO: HACK: rough area for goal here
	public static final Rectangle BLUE_GOAL_AREA = new Rectangle(290, 0, 110,
			44);
	public static final Rectangle RED_GOAL_AREA = new Rectangle(290, 980, 110,
			44);

	public static final Vector2 RED_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			975);
	public static final Vector2 BLUE_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			24);

	private static final String INPUT_TAG = "GameInputStrategy";

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
	// TODO: This needs to go. Replace with separate pause / not paused enum.
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
	protected float elapsedRoundTime = 0;
	protected float goalScoredDrawTime = 0f;
	protected int redScore = 0;
	protected int blueScore = 0;

	protected SoundManager soundManager;

	protected TeamColour team1;
	protected TeamColour team2;

	protected AI ai;
	protected TextArea textArea;
	public Bar bar;
	protected boolean positionUIBarAtTop = true;
	private boolean repositionCameraOnUpdate = false;

	protected Player selectedPlayer;
	protected Player highlightedPlayer;
	protected boolean isBallHighlighted;
	protected ArrayList<Vector2> lineInProgress = new ArrayList<Vector2>();
	protected Cursor cursor;
	boolean team1Turn = true;
	
	protected Texture kickSprite;
	protected Texture markSprite;
	protected Texture markBallSprite;
	protected Texture passSprite;
	protected Texture KickSprite;

	protected abstract void setStartingPositions(TeamColour centerTeam);

	@Override
	public void create() {

		createLibGdxItems();
		createMainTextures();
		createSfx();
		createActions();
		createIteractiveObjects();
		createUI();
		createRenderingObjects();

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		remainingMatchTime = 3 * 60;

		beginInputStage();

	}

	protected void createRenderingObjects() {
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT
				+ bar.getHeight());
		if (positionUIBarAtTop) {
			// Move the camera
			camera.translate(0, -bar.getHeight());
			Gdx.app.log("CAMERA_POSITION", "Camera position: "
					+ camera.position.x + "," + camera.position.y + ","
					+ camera.position.z + " UP ");
		} else {
			// Leave the camera at the top
			Gdx.app.log("CAMERA_POSITION", "Camera position: "
					+ camera.position.x + "," + camera.position.y + ","
					+ camera.position.z + " down ");
		}
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
	}

	protected void createUI() {
		textArea = new TextArea();
		textArea.setListener(new ButtonPressListener() {
			@Override
			public void onButtonPress() {
				textAreaButtonPressed();
			}
		});
		bar = new Bar(this, positionUIBarAtTop);
		cursor = new Cursor();
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
		kickSprite = new Texture(Gdx.files.internal("target.png"));
		passSprite = new Texture(Gdx.files.internal("passingIcon.png"));
		markSprite = new Texture(Gdx.files.internal("markingIcon.png"));
		markBallSprite = new Texture(Gdx.files.internal("markingIcon.png"));
		
		Kick.create(kickSprite);
		Pass.create(passSprite);
		Mark.create(markSprite);
		MarkBall.create(markBallSprite);
		Move.create(new Texture(Gdx.files.internal("arrowhead.png")));
		MoveToPosition.create(new Texture(Gdx.files.internal("arrowhead.png")));
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

		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void render() {

		update();

		if (repositionCameraOnUpdate) {
			positionUIBarAtTop = !positionUIBarAtTop;
			bar = new Bar(this, positionUIBarAtTop);

			if (positionUIBarAtTop) {
				camera.position.set(VIRTUAL_SCREEN_WIDTH / 2,
						(VIRTUAL_SCREEN_HEIGHT / 2) - (bar.getHeight() / 2), 0);
				Gdx.app.log("CAMERA_POSITION", "Camera position: "
						+ camera.position.x + "," + camera.position.y + ","
						+ " UP ");
			} else {
				camera.position.set(VIRTUAL_SCREEN_WIDTH / 2,
						(VIRTUAL_SCREEN_HEIGHT / 2) + (bar.getHeight() / 2), 0);
				Gdx.app.log("CAMERA_POSITION", "Camera position: "
						+ camera.position.x + "," + camera.position.y + ","
						+ " DOWN ");
			}
			repositionCameraOnUpdate = false;
		}

		// clear the screen with a dark blue color.
		Gdx.gl.glViewport(xOffset, yOffset, drawnPitchWidth, drawnPitchHeight
				+ (int) bar.getHeight());
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

			for (Player player : getHumanPlayers()) {
				drawActions(player.getAction(), batch);
			}

			if (getHumanGoalie() != null) {
				drawActions(getHumanGoalie().getAction(), batch);
			}

			if (highlightedPlayer != null) {
				highlightedPlayer.drawHighlight(batch);
				drawTimeLinePoints(highlightedPlayer);
				drawPlayerStats(batch, highlightedPlayer);
			}

			if (selectedPlayer != null) {
				selectedPlayer.drawSelect(batch);
				drawTimeLinePoints(selectedPlayer);
			}

			if (isBallHighlighted) {
				ball.drawHighlight(batch);
			}

			if (cursor != null) {
				cursor.draw(batch);
			}

		} else {
			// Execution stage
		}

		for (Player player : getAllPlayers()) {
			player.draw(batch);
		}

		if (ball != null) {
			ball.draw(batch);
		}

		if (gameState == GameState.PAUSED) {
			bmf.scale(.22f);
			textArea.draw(batch, bmf);
			bmf.scale(-.22f);
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

			for (int i = 0; i < lineInProgress.size() - 1; i++) {
				Vector2 a = lineInProgress.get(i);
				Vector2 b = lineInProgress.get(i + 1);
				// TODO: Fix this
				if (a == null || b == null)
					continue;
				shapeRenderer.line(a.x, a.y, b.x, b.y);
			}

			shapeRenderer.setColor(0, 0, 0, 0);

			for (Player player : getHumanPlayers()) {
				drawActions(player.getAction(), shapeRenderer);
			}

			if (getHumanGoalie() != null) {
				drawActions(getHumanGoalie().getAction(), shapeRenderer);
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

	abstract protected void update();

	protected void tackleDetection(float time) {
		for (Player player : getAllPlayers()) {

			if (player.getTackleHitbox().overlaps(ball)
					&& ball.getOwner() != player
					&& !(ball.getOwner() instanceof Goalie)
					&& ball.getTimeSinceTackle() > BALL_CHANGE_TIME
					&& player.getTimeSinceKick() > BALL_PASS_TIME
					&& player.getTimeFailedTackle() > BALL_CHANGE_TIME) {

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
						player.setTimeSinceKick(0);
						player.setTimeSinceFailedTackle(0);
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
			player.setTimeSinceFailedTackle(0);
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
				setStartingPositions(TeamColour.RED);
			}
		} else if (BLUE_GOAL_AREA.contains(ball)) {
			if (ball.hasOwner() && ball.getOwner() == blueGoalie) {
				// do nothing
			} else {
				redScore++;
				goalScored = true;
				setStartingPositions(TeamColour.BLUE);
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

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public void clearActions() {
		for (Player player : getAllPlayers()) {
			player.reset();
		}
	}

	protected void beginInputStage() {
		gameState = GameState.INPUT;
		selectedPlayer = null;
		highlightedPlayer = null;
		clearActions();
		bar.setPositionToDown();
	}

	public void beginExecution() {
		Gdx.app.log("Game", "Beginning execution");
		elapsedRoundTime = 0;
		this.gameState = GameState.EXECUTION;
		if (ai != null) {
			ai.getComputerActions();
		}
		bar.setPositionToUp();
	}

	public List<Player> getAllPlayers() {
		List<Player> result = new LinkedList<Player>();
		if (redPlayers != null) {
			result.addAll(redPlayers);
		}
		if (redGoalie != null) {
			result.add(redGoalie);
		}
		if (bluePlayers != null) {
			result.addAll(bluePlayers);
		}
		if (blueGoalie != null) {
			result.add(blueGoalie);
		}
		return result;
	}

	public Player getSelectedPlayer() {
		return selectedPlayer;
	}

	public TeamColour getHumanColour() {
		return team1;
	}

	public TeamColour getComputerColour() {
		return team2;
	}

	public List<Player> getHumanPlayers() {
		if (team1 == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	public List<Player> getComputerPlayers() {
		if (team2 == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	public Player getHumanGoalie() {
		if (team1 == TeamColour.RED) {
			return redGoalie;
		} else {
			return blueGoalie;
		}
	}

	public Player getComputerGoalie() {
		if (team2 == TeamColour.RED) {
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

		try {
			if (positionUIBarAtTop) {
				vy -= bar.getHeight() / scaleFactor;
			} else {

			}

		} catch (NullPointerException e) {
		}

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
		int pitchHeight = (int) (height - bar.getHeight());
		double screenRatio = (double) width / (double) pitchHeight;
		double pitchImageRatio = (double) VIRTUAL_SCREEN_WIDTH
				/ (double) VIRTUAL_SCREEN_HEIGHT;

		if (width > pitchHeight) {
			// Need to draw pitch on it's side
		}

		if (screenRatio > pitchImageRatio) {
			// Borders to left and right
			scaleFactor = (double) pitchHeight / (double) VIRTUAL_SCREEN_HEIGHT;
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
			yOffset = (pitchHeight - drawnPitchHeight) / 2;
		}
	}

	public void backButtonPressed() {
		Gdx.app.log("GameState", "Back button pressed");
		if (gameState == GameState.FINISHED) {
			Gdx.app.exit();
		} else if (gameState == GameState.PAUSED) {
			gameState = gameStateToGoIntoWhenBackButtonPressed;
			textArea = new NullTextArea();
		} else {
			gameStateToGoIntoWhenBackButtonPressed = gameState;
			gameState = GameState.PAUSED;
			textArea = new PauseMenu();
		}
		Gdx.app.log("GameState", gameState.toString());

	}

	public void playButtonPressed() {
		if (getGameState() == GameState.PAUSED) {
			return;
		}

		// TODO: this implementation...ewww. Also can still hold ball via long
		// runs.
		if (getHumanGoalie().hasBall()) {
			if (!getHumanGoalie().kicksBall()) {
				getBar().setText("Goalie cannot hold onto the ball");
				Gdx.app.log("Game", "Goalie needs to kick the ball");
				return;
			}
		}

		beginExecution();
	}

	protected void textAreaButtonPressed() {
		gameState = gameStateToGoIntoWhenBackButtonPressed;
		textArea = new NullTextArea();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	protected Player findPlayer(Vector2 point) {
		Player temp = null;
		Vector2 playerVector;
		for (Player player : getAllPlayers()) {
			List<Vector2> pointsList = player.getPositionList();
			Collections.reverse(pointsList);

			for (int i = 0; i < pointsList.size(); i++) {
				playerVector = pointsList.get(i);
				if (playerVector.epsilonEquals(point, INPUT_EPSILON_VALUE)) {
					// We are biased to selectable players, return them before
					// an unselectable one.
					if (isSelectable(player)) {
						return player;
					} else {
						temp = player;
					}
				}
			}

		}
		return temp;
	}

	/**
	 * Looks for a player at or near the specified point. Gets index of the
	 * selected position (Possibly a hack). Returns an integer indicating how
	 * far in the list of actions has been selected.
	 * 
	 * @return The action list queue index at the selected position
	 */
	protected int findPlayerIndex(Vector2 point) {
		Vector2 playerVector;
		for (Player player : getAllPlayers()) {
			List<Vector2> pointsList = player.getPositionList();
			Collections.reverse(pointsList);

			for (int i = 0; i < pointsList.size(); i++) {
				playerVector = pointsList.get(i);
				if (playerVector.epsilonEquals(point, INPUT_EPSILON_VALUE)) {
					// We are biased to selectable players, return them before
					// an unselectable one.
					if (isSelectable(player)) {
						return pointsList.size() - 1 - i;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Finds if the ball overlaps or is near a point.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	protected boolean findBall(Vector2 point) {
		if (getBall() == null) {
			return false;
		}

		return (getBall().getBallPosition().epsilonEquals(point,
				INPUT_EPSILON_VALUE)) ? true : false;
	}

	protected boolean isSelectable(Player player) {
		if (team1Turn) {
			if (getHumanPlayers().contains(player)) {
				return true;
			}

			if (getHumanGoalie() != null) {
				if (getHumanGoalie().hasBall() && getHumanGoalie() == player) {
					return true;
				}
			}
			return false;
		} else {
			if (getComputerPlayers().contains(player)) {
				return true;
			}

			if (getComputerGoalie() != null) {
				if (getComputerGoalie().hasBall()
						&& getComputerGoalie() == player) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Called when a line is drawn starting and finishing on top of a player &
	 * not a ball
	 */
	private void pressPlayer(Player pressedPlayer) {

		if (selectedPlayer == null && isSelectable(pressedPlayer)) {
			selectedPlayer = pressedPlayer;
			return;
		} else if (selectedPlayer == pressedPlayer) {
			selectedPlayer = null;
			return;
		}

		if (selectedPlayer != null
				&& pressedPlayer.getTeam() == getHumanColour()) {
			// If both players are selectable pass between them
			selectedPlayer.addAction(new Pass(ball, selectedPlayer,
					pressedPlayer, selectedPlayer.getFuturePosition()));

		} else if (selectedPlayer != null
				&& pressedPlayer.getTeam() != getHumanColour()
				&& pressedPlayer != getComputerGoalie()) {
			// If the first player is selectable, the second player is on the
			// opposingTeam but is not a goalie then mark the second player
			selectedPlayer.addAction(new Mark(selectedPlayer, pressedPlayer));
		}
		selectedPlayer = null;
	}

	/** Called when a line is drawn starting and finishing on top of a ball */
	private void pressBall() {
		Gdx.app.log(INPUT_TAG, "Pressed ball: ");

		if (selectedPlayer != null) {
			selectedPlayer.addAction(new MarkBall(selectedPlayer, ball));
			selectedPlayer = null;
			return;
		}
	}

	private void pressPoint(Vector2 point) {
		if (selectedPlayer != null) {
			selectedPlayer.addAction(new Kick(getBall(), point, selectedPlayer
					.getFuturePosition()));
			selectedPlayer = null;
		}
	}

	private void assignMoveTo(Player player, int index) {
		Gdx.app.log(INPUT_TAG, "assigning Move command to " + player.toString());

		if (index != 0) {
			// TODO: Refactor Marking into a generic abstract following action
			if (player.getAction(index) instanceof Mark) {
				Mark markAction = (Mark) player.getAction(index);
				player.setAction(
						new MoveToPosition(lineInProgress.get(lineInProgress
								.size() - 1), markAction.getTarget()), index);
			} else if (player.getAction(index) instanceof MarkBall) {
				player.setAction(
						new MoveToPosition(lineInProgress.get(lineInProgress
								.size() - 1), ball), index);
			} else {
				player.setAction(
						new Move(lineInProgress
								.toArray(new Vector2[lineInProgress.size()])),
						index);
			}
		} else {
			player.setAction(
					new Move(lineInProgress.toArray(new Vector2[lineInProgress
							.size()])), index);
		}

		selectedPlayer = null;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			backButtonPressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector2 point = translateInputToField(new Vector2(screenX, screenY));

		if (getGameState() == GameState.PAUSED) {
			textArea.onPress(point.x, point.y);
		}

		if (getGameState() == GameState.INPUT) {

			bar.onPress(point.x, point.y);

			if (getGameState() == GameState.INPUT) {
				highlightedPlayer = findPlayer(point);
				isBallHighlighted = findBall(point);

				if (!bar.contains(point.x, point.y)) {
					lineInProgress.clear();
					lineInProgress.add(point);
				}
			}
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		highlightedPlayer = null;
		isBallHighlighted = false;
		cursor.setVisibility(false);

		if (lineInProgress.size() < 1) {
			return false;
		}

		Player start = findPlayer(lineInProgress.get(0));
		Player finish = findPlayer(lineInProgress
				.get(lineInProgress.size() - 1));

		Vector2 startVector = lineInProgress.get(0);
		Vector2 endVector = lineInProgress.get(lineInProgress.size() - 1);

		boolean startAtBall = findBall(startVector);
		boolean finishedAtBall = findBall(endVector);

		// Note to self: the orderings here are very important
		if (start == null && finish == null) {
			Gdx.app.log(INPUT_TAG, "You pressed: " + startVector.toString());
			if (startAtBall && finishedAtBall && selectedPlayer != null) {
				Gdx.app.log(INPUT_TAG, "You marked the ball");
				pressBall();
			} else {
				pressPoint(endVector);
			}

		} else if (startAtBall && finishedAtBall && selectedPlayer != null) {
			Gdx.app.log(INPUT_TAG, "You marked the ball");
			pressBall();

		} else if (start == null) {
			Gdx.app.log(INPUT_TAG,
					"You drew a line starting from a null position");

		} else if (start == finish) {
			Gdx.app.log(INPUT_TAG, "You selected a player");
			pressPlayer(start);
		} else if (!isSelectable(start)) {
			Gdx.app.log(INPUT_TAG,
					"Your line started from an unselectable player");
		} else if (isSelectable(start)) {
			Gdx.app.log(INPUT_TAG, "You drew a line from a player");
			int index = findPlayerIndex(lineInProgress.get(0));
			Gdx.app.log(INPUT_TAG,
					"You drew a line from a player " + String.valueOf(index));
			assignMoveTo(start, index);

		}

		lineInProgress.clear();
		cursor.setVisibility(false);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		Vector2 point = translateInputToField(new Vector2(screenX, screenY));

		if (getGameState() == GameState.INPUT) {
			if (!bar.contains(point.x, point.y)) {
				lineInProgress.add(point);
				cursor.setLocation(point.x, point.y);

				Player start = findPlayer(lineInProgress.get(0));
				Player finish = findPlayer(lineInProgress
						.get(lineInProgress.size() - 1));

				Vector2 startVector = lineInProgress.get(0);
				Vector2 endVector = lineInProgress.get(lineInProgress.size() - 1);

				boolean startAtBall = findBall(startVector);
				boolean finishedAtBall = findBall(endVector);

				if (start == null && finish == null) {
					if (startAtBall && finishedAtBall && selectedPlayer != null) {
						if (selectedPlayer != null) {
							cursor.setVisibility(true);
							cursor.setTexture(markBallSprite);
						}
					} else {
						if (selectedPlayer != null) {
							cursor.setVisibility(true);
							cursor.setTexture(kickSprite);
						}
					}

				} else if (startAtBall && finishedAtBall && selectedPlayer != null) {
					if (selectedPlayer != null) {
						cursor.setVisibility(true);
						cursor.setTexture(markBallSprite);
					}
				} else if (start == null) {
					cursor.setVisibility(false);

				} else if (start == finish) {
					if (selectedPlayer == start) {
						cursor.setVisibility(false);
					} else if (selectedPlayer != null
							&& start.getTeam() == getHumanColour()) {
						cursor.setTexture(passSprite);
						cursor.setVisibility(true);

					} else if (selectedPlayer != null
							&& start.getTeam() != getHumanColour()
							&& start != getComputerGoalie()) {
						cursor.setTexture(markSprite);
						cursor.setVisibility(true);
					}
				} else if (isSelectable(start)) {
					cursor.setVisibility(false);
				} else {
					cursor.setVisibility(false);
				}
			}
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2 hoverPoint = translateInputToField(new Vector2(screenX, screenY));

		if (getGameState() == GameState.INPUT) {
			highlightedPlayer = findPlayer(hoverPoint);
			isBallHighlighted = findBall(hoverPoint);
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		Vector2 hoverPoint = translateInputToField(new Vector2(event.getX(),
				event.getY()));

		if (bar != null && bar.contains(hoverPoint.x, hoverPoint.y)) {
			Gdx.app.log("BAR", "hover eventVector: " + hoverPoint.toString()
					+ " " + bar.toString());
		}

		if (getGameState() == GameState.INPUT) {
			highlightedPlayer = findPlayer(hoverPoint);
			isBallHighlighted = findBall(hoverPoint);
		}
		return true;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		// repositionCameraOnUpdate = true;
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
	}

}
