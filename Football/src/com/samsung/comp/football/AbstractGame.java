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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
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
import com.samsung.spensdk.applistener.SPenHoverListener;

public abstract class AbstractGame implements ApplicationListener,
		InputProcessor, SPenHoverListener {

	protected int result;

	// TODO: Remove these and other hard coded values
	public static final float RECLAIM_BALL_TIME = 0.75f;
	public static final float CANNOT_TACKLE_TIME = 1f;
	public static final float TACKLE_IMMUNITY_TIME = 0.75f;
	public static final float BOUNCE_ELASTICITY = 0.5f;
	public static final float INPUT_EPSILON_VALUE = 32;
	public static final int VIRTUAL_SCREEN_WIDTH = 676;
	public static final int VIRTUAL_SCREEN_HEIGHT = 1024;
	// TODO: Restrict input, ball / player movement etc. to these
	public static final int PLAYING_AREA_WIDTH = 670;
	public static final int PLAYING_AREA_HEIGHT = 1024;

	// TODO: HACK: rough area for goal here
	public static final Rectangle RED_GOAL_AREA = new Rectangle(290, 0, 110, 44);
	public static final Rectangle BLUE_GOAL_AREA = new Rectangle(290, 980, 110,
			44);

	public static final Vector2 BLUE_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			975);
	public static final Vector2 RED_GOAL = new Vector2(PLAYING_AREA_WIDTH / 2,
			24);

	private static final String INPUT_TAG = "GameInputStrategy";

	protected ActionResolver actionResolver;

	protected int xOffset;
	protected int yOffset;
	protected int drawnPitchWidth;
	protected int drawnPitchHeight;
	protected double scaleFactor;

	public static Texture endTexture;
	public static Texture pitchTexture;
	public static Texture playTexture;
	public static Texture statPoint1;
	public static Texture statPoint2;
	public static Texture statPoint3;
	public static Texture statPoint4;
	public static Texture statPoint5;
	public static Texture stats;
	public static Texture goalMessage;

	protected Sound whistleBlow;
	protected Sound crowdCheer;

	public enum GameState {
		INPUT, EXECUTION, SETUP, PAUSED, FINISHED
	}

	protected static Random rng;
	protected GameState gameState = GameState.INPUT;
	// TODO: This needs to go. Replace with separate pause / not paused enum.
	protected GameState gameStateToGoIntoWhenBackButtonPressed = GameState.PAUSED;
	protected float roundTime = 5;
	protected float remainingMatchTime;
	protected byte scoreLimit = -1;
	protected SpriteBatch batch;
	protected ShapeRenderer shapeRenderer;
	protected BitmapFont bmf;
	protected OrthographicCamera camera;
	protected boolean controlsActive = false;

	protected List<Player> redPlayers = new LinkedList<Player>();
	protected List<Player> bluePlayers = new LinkedList<Player>();
	protected Goalie redGoalie;
	protected Goalie blueGoalie;
	protected Ball ball;
	protected ArrayList<Arrow> arrows = new ArrayList<Arrow>();

	protected float elapsedRoundTime = 0;
	protected float remainingSetupTime = 0;
	protected float goalScoredDrawTime = 0f;
	protected int redScore = 0;
	protected int blueScore = 0;

	protected SoundManager soundManager;

	protected TeamColour team1;
	protected TeamColour team2;
	protected TeamColour currentTeam = TeamColour.BLUE;

	protected AI ai;
	protected TextArea textArea;
	public Bar bar;
	protected boolean positionStatsAtTop = false;
	protected boolean positionUIBarAtTop = false;
	private boolean repositionCameraOnUpdate = false;

	protected Player selectedPlayer;
	protected boolean isBallHighlighted;
	protected ArrayList<Vector2> lineInProgress = new ArrayList<Vector2>();
	protected Cursor cursor;

	protected Texture kickSprite;
	protected Texture markSprite;
	protected Texture markBallSprite;
	protected Texture passSprite;
	protected Texture KickSprite;
	protected Texture moveSprite;
	protected Texture unselectableSprite;

	protected Texture kickSpriteHighlighted;
	protected Texture markSpriteHighlighted;
	protected Texture markBallSpriteHighlighted;
	protected Texture passSpriteHighlighted;
	protected Texture KickSpriteHighlighted;
	protected Texture moveSpriteHighlighted;

	protected Texture redSelectTexture;
	protected Texture blueSelectTexture;
	protected float selectTextureStateTime = 0f;

	protected Texture ghostSpriteSheet;
	protected Animation ghostRunAnimation;
	protected float ghostStateTime = 0f;
	protected TextureRegion ghostFrame;
	
	protected Texture pointer;
	protected Texture pathArrow;
	protected Texture pushIndicator;

	protected abstract void onGoalScored(TeamColour scoringTeam);

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

		remainingMatchTime = (remainingMatchTime <= 0) ? 3 * 60
				: remainingMatchTime;

		controlsActive = true;

		beginInputStage();

	}

	protected void createRenderingObjects() {
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT
				+ bar.getHeight());
		camera.zoom = 1f;
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
		pointer = new Texture(Gdx.files.internal("pointerOrange.png"));
		pathArrow = new Texture(Gdx.files.internal("arrowPath.png"));
		pushIndicator = new Texture(
				Gdx.files.internal("pushIndicatorOrange.png"));
		textArea = new NullTextArea();
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
		crowdCheer = Gdx.audio.newSound(Gdx.files
				.internal("sound/football crowd.wav"));
	}

	protected void createActions() {
		kickSprite = new Texture(Gdx.files.internal("target.png"));
		passSprite = new Texture(Gdx.files.internal("passingIcon.png"));
		markSprite = new Texture(Gdx.files.internal("markingIcon.png"));
		markBallSprite = new Texture(Gdx.files.internal("markingIcon.png"));
		moveSprite = new Texture(Gdx.files.internal("arrowhead.png"));
		unselectableSprite = new Texture(
				Gdx.files.internal("unselectableHover.png"));

		kickSpriteHighlighted = new Texture(
				Gdx.files.internal("targetBlue.png"));
		passSpriteHighlighted = new Texture(
				Gdx.files.internal("passingIconBlue.png"));
		markSpriteHighlighted = new Texture(
				Gdx.files.internal("markingIconBlue.png"));
		markBallSpriteHighlighted = new Texture(
				Gdx.files.internal("markingIconBlue.png"));
		moveSpriteHighlighted = new Texture(
				Gdx.files.internal("arrowheadBlue.png"));

		Kick.create(kickSprite, kickSpriteHighlighted);
		Pass.create(passSprite, passSpriteHighlighted);
		Mark.create(markSprite, markSpriteHighlighted);
		MarkBall.create(markBallSprite, markBallSpriteHighlighted);
		Move.create(moveSprite, moveSpriteHighlighted);
		MoveToPosition.create(moveSprite, moveSpriteHighlighted);
	}

	protected void createMainTextures() {
		endTexture = new Texture(Gdx.files.internal("endScreen.png"));
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		playTexture = new Texture(Gdx.files.internal("playIcon.png"));
		statPoint1 = new Texture(Gdx.files.internal("statPointRed.png"));
		statPoint2 = new Texture(Gdx.files.internal("statPoint.png"));
		statPoint3 = new Texture(Gdx.files.internal("statPointGreen.png"));
		statPoint4 = new Texture(Gdx.files.internal("statPointBlue.png"));
		statPoint5 = new Texture(Gdx.files.internal("statPointPurple.png"));
		stats = new Texture(Gdx.files.internal("stats.png"));
		goalMessage = new Texture(Gdx.files.internal("GoalScored.png"));

		redSelectTexture = new Texture(Gdx.files.internal("redSelect.png"));
		blueSelectTexture = new Texture(Gdx.files.internal("blueSelect.png"));

		ghostSpriteSheet = new Texture(Gdx.files.internal("shadowPlayer.png"));
		ghostRunAnimation = new Animation(0.10f, Utils.createTextureRegion(
				ghostSpriteSheet, 10));
	}

	protected void createLibGdxItems() {
		Texture.setEnforcePotImages(false);

		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
	}

	public TextureRegion getGhostFrame(float time) {
		TextureRegion region = ghostRunAnimation.getKeyFrame(time, true);
		TextureRegion frame = new TextureRegion(region,
				region.getRegionWidth() / 2 - 64 / 2,
				region.getRegionHeight() / 2 - 64 / 2, 64, 64);
		return frame;
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
				VIRTUAL_SCREEN_HEIGHT, false, true);

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

			Player highlightedPlayer = cursor.getHighlightedPlayer();
			Player selPlayer = this.selectedPlayer;

			if (highlightedPlayer != null) {
				drawPlayerStats(batch, highlightedPlayer);

			} else {

				if (selPlayer != null) {
					drawPlayerStats(batch, selPlayer);
				}
			}

			for (Player player : getPlayers(currentTeam)) {
				drawActions(
						player.getAction(),
						batch,
						(player == highlightedPlayer || player == selPlayer));
			}

			if (getGoalie(currentTeam) != null) {
				drawActions(
						getGoalie(currentTeam).getAction(),
						batch,
						(getGoalie(currentTeam) == highlightedPlayer || getGoalie(currentTeam) == selPlayer));
			}

			if (highlightedPlayer != null) {
				if (isSelectable(highlightedPlayer)) {
					drawTimeLinePoints(highlightedPlayer);
					drawGhost(highlightedPlayer, ghostStateTime);
				}
			}

			if (selPlayer != null) {
				selPlayer.drawSelect(batch, selectTextureStateTime);
				drawTimeLinePoints(selPlayer);
				drawGhost(selPlayer, ghostStateTime);
			}

			for (Arrow arrow : arrows) {
				arrow.draw(batch);
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

	public void drawGhost(Player player, float futureTime) {
		PlayerPositionData ghostsPosition = player
				.getFuturePositionData(futureTime, false);
		Vector2 ghostPos = ghostsPosition.position;

		// If the ghost is at the same position as the player, don't render
		if (player.getPosition().epsilonEquals(ghostPos, 0))
			return;

		float rotation = ghostsPosition.rotation;
		if (ghostPos != null) {
			PlayerPositionData ghostsNextPosition = player
					.getFuturePositionData(futureTime + 0.1f, false);
			Vector2 futurePos = ghostsNextPosition.position;
			//Only update the ghost animation if it has actually moved
			if (!futurePos.epsilonEquals(ghostPos, 0) || ghostFrame == null) {
				ghostFrame = getGhostFrame(futureTime);
			}
			batch.draw(ghostFrame, futurePos.x - ghostFrame.getRegionWidth()
					/ 2, futurePos.y - ghostFrame.getRegionHeight() / 2,
					ghostFrame.getRegionWidth() / 2,
					ghostFrame.getRegionHeight() / 2,
					ghostFrame.getRegionWidth(), ghostFrame.getRegionHeight(),
					1, 1, rotation, true);
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

			for (Player player : getPlayers(currentTeam)) {
				if (player == selectedPlayer
						|| player == cursor.getHighlightedPlayer()) {
					shapeRenderer.setColor(0, 0, 255, 0);
					drawActions(player.getAction(), shapeRenderer);
					shapeRenderer.setColor(0, 0, 0, 0);
				} else {
					drawActions(player.getAction(), shapeRenderer);
				}
			}

			if (getGoalie(currentTeam) != null) {
				drawActions(getGoalie(currentTeam).getAction(), shapeRenderer);
			}
		} else {
			// Execution stage
		}

		shapeRenderer.end();
	}

	protected void drawActions(Action action, SpriteBatch batch,
			boolean highlighted) {
		if (action != null) {
			action.draw(batch, highlighted);
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
			if (positionStatsAtTop) {
				batch.draw(stats, VIRTUAL_SCREEN_WIDTH - stats.getWidth() - 25
						* 10, 0, 0, 0, stats.getWidth(), stats.getHeight(), 1,
						1, 0, 0, 0, stats.getWidth(), stats.getHeight(), false,
						true);

				for (int i = 0; i < player.getRunSpeedBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(0 * statPoint1.getHeight()), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(),
							statPoint1.getHeight(), false, true);
				}

				for (int i = 0; i < player.getShootSpeedBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(1 * statPoint1.getHeight()), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(),
							statPoint1.getHeight(), false, true);
				}

				for (int i = 0; i < player.getTackleSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(2 * statPoint1.getHeight()), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(),
							statPoint1.getHeight(), false, true);
				}

				for (int i = 0; i < player.getTacklePreventionSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(3 * statPoint1.getHeight()), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(),
							statPoint1.getHeight(), false, true);
				}

				for (int i = 0; i < player.getSavingSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(4 * statPoint1.getHeight()), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(),
							statPoint1.getHeight(), false, true);
				}
			} else {
				batch.draw(stats, VIRTUAL_SCREEN_WIDTH - stats.getWidth() - 25
						* 10,
						(VIRTUAL_SCREEN_HEIGHT - (5 * statPoint1.getHeight())),
						0, 0, stats.getWidth(), stats.getHeight(), 1, 1, 0, 0,
						0, stats.getWidth(), stats.getHeight(), false, true);

				for (int i = 0; i < player.getRunSpeedBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(VIRTUAL_SCREEN_HEIGHT - (5 * statPoint1
									.getHeight())), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(), statPoint1
									.getHeight(), false, true);
				}

				for (int i = 0; i < player.getShootSpeedBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(VIRTUAL_SCREEN_HEIGHT - (4 * statPoint1
									.getHeight())), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(), statPoint1
									.getHeight(), false, true);
				}

				for (int i = 0; i < player.getTackleSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(VIRTUAL_SCREEN_HEIGHT - (3 * statPoint1
									.getHeight())), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(), statPoint1
									.getHeight(), false, true);
				}

				for (int i = 0; i < player.getTacklePreventionSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(VIRTUAL_SCREEN_HEIGHT - (2 * statPoint1
									.getHeight())), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(), statPoint1
									.getHeight(), false, true);
				}

				for (int i = 0; i < player.getSavingSkillBarCount(); i++) {
					batch.draw(statPointImageFactory(i), VIRTUAL_SCREEN_WIDTH
							- 25 * 10 + (i * statPoint1.getWidth()),
							(VIRTUAL_SCREEN_HEIGHT - (1 * statPoint1
									.getHeight())), 0, 0,
							statPoint1.getWidth(), statPoint1.getHeight(), 1,
							1, 0, 0, 0, statPoint1.getWidth(), statPoint1
									.getHeight(), false, true);
				}
			}
		} catch (NullPointerException e) {
			return;
		}
	}

	/**
	 * Returns a stat point texture from an integer
	 * 
	 * @param statPoints
	 *            the number of stat bars currently displayed
	 * @return
	 */
	private Texture statPointImageFactory(int statPoints) {
		int i = statPoints / 5;
		switch (i) {
		case (0):
			return statPoint1;
		case (1):
			return statPoint2;
		case (2):
			return statPoint3;
		case (3):
			return statPoint4;
		case (4):
			return statPoint5;
		default:
			return statPoint1;
		}
	}

	protected void randomiseStats(List<Player> players) {
		int rn;
		for (Player player : players) {
			rn = Utils.randomInt(rng, -3, 3);
			player.setRunSpeed(player.getRunSpeed() + rn
					* player.getRunSpeedBarValue());

			rn = Utils.randomInt(rng, -3, 3);
			player.setShootSpeed(player.getShootSpeed() + rn
					* player.getShootSpeedBarValue());

			rn = Utils.randomInt(rng, -3, 3);
			player.setTackleSkill(player.getTackleSkill() + rn
					* player.getTackleSkillBarValue());

			rn = Utils.randomInt(rng, -3, 3);
			player.setTacklePreventionSkill(player.getTacklePreventionSkill()
					+ rn * player.getTacklePreventionBarValue());

			rn = Utils.randomInt(rng, -3, 3);
			player.setSavingSkill(player.getSavingSkill() + rn
					* player.getSavingSkillBarValue());

			player.trimAllSkills();
		}
	}

	abstract protected void update();

	protected void tackleDetection(float time) {
		for (Player player : getAllPlayers()) {

			// Check for overlap with ball, and owner isn't goalie or self
			if (player.getTackleHitbox().contains(ball.getBallPosition())
					&& ball.getOwner() != player
					&& !(ball.getOwner() instanceof Goalie)) {

				// Check if ball is owned
				if (ball.hasOwner()) {
					// Check for a possible tackle
					if (isTacklePossible(player)) {
						performTackle(player);
					}
				} else if (player.getCannotCollectBallTime() <= 0) {
					// Cannot collect the ball if they recently kicked it (for
					// passing through self)
					float delta = ball.getSpeed() - player.getSavingSkill();
					float rn = Utils.randomFloat(rng, 0, 100);

					if (rn > delta) {
						// Clear collection restriction to allow quick repass
						for (Player p : getAllPlayers()) {
							p.setCannotCollectBallTime(0);
						}
						ball.setOwner(player);
					} else {
						// failed to collect ball
						player.setNoticationTime(.75f);
						player.setCannotCollectBallTime(RECLAIM_BALL_TIME);
					}
				}
			}
		}
	}

	/**
	 * Cannot tackle the owner if owner recently obtained the ball. Cannot
	 * tackle if the challenger recently failed or kicked the ball.
	 * 
	 * @param challenger
	 *            The player initiating the tackle
	 * @return
	 */
	protected boolean isTacklePossible(Player challenger) {
		return ball.getOwner().getTeam() != challenger.getTeam()
				&& ball.getOwner().getTackleImmunityTime() <= 0
				&& challenger.getCannotTackleTime() <= 0;
	}

	protected void performTackle(Player player) {
		float tackleChance = player.getTackleSkill()
				- ball.getOwner().getTacklePreventionSkill();
		float rn = Utils.randomFloat(rng, 0, 100);
		if (rn < tackleChance) {
			float rotation = 0;
			rotation = Utils.getMoveVector(ball.getOwner().getPlayerPosition(),
					player.getPlayerPosition(), 5).angle();
			player.setTackleImmunityTime(TACKLE_IMMUNITY_TIME);
			player.setRotation(rotation);
			ball.setOwner(player);
		} else {
			// failed the tackle
			player.setNoticationTime(.75f);
			player.setCannotTackleTime(TACKLE_IMMUNITY_TIME);
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
				onGoalScored(TeamColour.RED);

			}
		} else if (BLUE_GOAL_AREA.contains(ball)) {
			if (ball.hasOwner() && ball.getOwner() == blueGoalie) {
				// do nothing
			} else {
				redScore++;
				goalScored = true;
				onGoalScored(TeamColour.BLUE);
			}
		}

		if (redScore == scoreLimit || blueScore == scoreLimit) {
			matchFinish();
		}
	}

	/**
	 * Adds actions to move all players except the goalie either away from each
	 * other, or if a goal keeper has the ball, away from that goal.
	 * 
	 * @param teamColour
	 *            The goal to move away from
	 */
	protected void setupPlayerPositioning() {
		Player goalieWithBall = ball.getOwner() instanceof Goalie ? ball
				.getOwner() : null;
		TeamColour goalieColour = goalieWithBall != null ? goalieWithBall
				.getTeam() : null;

		for (Player player : getAllPlayers()) {
			if (player == getGoalie(goalieColour)) {
				continue;
			} else {
				if (goalieColour != null
						&& getGoalAreaCircle(goalieColour).contains(
								player.getPlayerPosition())) {
					moveAwayFromGoal(player, goalieColour);
				} else {
					separatePlayer(player);
				}
			}
		}
	}

	/**
	 * Adds an action to move the player away from the parameterised goal.
	 * 
	 * @param player
	 *            The player to move
	 * 
	 * @param teamColour
	 *            The goal to move away from
	 */

	protected void moveAwayFromGoal(Player player, TeamColour teamColour) {
		Vector2 towardsGoal = Utils.getMoveVector(player.getPlayerPosition(),
				getGoalVector(teamColour), 225 - (player.getPlayerPosition()
						.dst(getGoalVector(teamColour))));
		Vector2 awayFromGoal = new Vector2(-towardsGoal.x, -towardsGoal.y);
		awayFromGoal.add(player.getPlayerPosition());

		player.addAction(new MoveToPosition(new Vector2(awayFromGoal.x,
				awayFromGoal.y), player));
	}

	/**
	 * Moves any group of intersecting players away from each other.
	 * Depreciated.
	 * 
	 * 
	 * @return Returns true if it has assigned any actions to separate the
	 *         players. Otherwise returns false.
	 */
	private void separatePlayers() {

		// for each player
		for (Player player1 : getAllPlayers()) {
			List<Player> overlappingPlayers = new ArrayList<Player>();

			// add any overlapping players to list
			for (Player player2 : getAllPlayers()) {
				if (player1 == player2) {
					continue;
				}

				Circle playerCollisionCircle = new Circle(
						player1.getPlayerPosition(), 64);
				if (playerCollisionCircle
						.contains(player2.getPlayerPosition())) {
					overlappingPlayers.add(player2);
				}
			}

			if (overlappingPlayers.size() == 0) {
				continue;
			} else {
				// calculate the centre point of the players
				Vector2 vectorSum = new Vector2(player1.getPlayerPosition());
				for (Player overlapPlayer : overlappingPlayers) {
					vectorSum.x += overlapPlayer.getPlayerX();
					vectorSum.y += overlapPlayer.getPlayerY();
				}
				Vector2 vectorAverage = new Vector2(vectorSum.x
						/ (overlappingPlayers.size() + 1), vectorSum.y
						/ (overlappingPlayers.size() + 1));

				// move away from the centre point
				Vector2 towardsCentre = Utils.getMoveVector(
						player1.getPlayerPosition(), vectorAverage, 3);

				player1.setAction(
						new MoveToPosition(player1.getPlayerPosition().cpy()
								.sub(towardsCentre), player1), 0);
			}
		}
	}

	private boolean separatePlayer(Player player) {

		List<Player> overlappingPlayers = new ArrayList<Player>();
		// add any overlapping players to list
		for (Player player2 : getAllPlayers()) {
			if (player == player2) {
				continue;
			}

			Circle playerCollisionCircle = new Circle(
					player.getPlayerPosition(), 64);
			if (playerCollisionCircle.contains(player2.getPlayerPosition())) {
				overlappingPlayers.add(player2);
			}
		}

		if (overlappingPlayers.size() == 0) {
			return false;
		} else {
			// calculate the centre point of the players
			Vector2 vectorSum = new Vector2(player.getPlayerPosition());
			for (Player overlapPlayer : overlappingPlayers) {
				vectorSum.x += overlapPlayer.getPlayerX();
				vectorSum.y += overlapPlayer.getPlayerY();
			}
			Vector2 vectorAverage = new Vector2(vectorSum.x
					/ (overlappingPlayers.size() + 1), vectorSum.y
					/ (overlappingPlayers.size() + 1));

			// move away from the centre point
			Vector2 towardsCentre = Utils.getMoveVector(
					player.getPlayerPosition(), vectorAverage, 3);

			if (player.getAction() == null) {
				player.addAction(new MoveToPosition(player.getPlayerPosition()
						.cpy().sub(towardsCentre), player));
			}

			return true;
		}
	}

	protected void matchFinish() {

		gameState = GameState.FINISHED;

		bmf.setColor(Color.BLACK);
		bmf.setScale(3);

		if (blueScore > redScore && team1 == TeamColour.BLUE) {
			result = 1;
		} else if (blueScore > redScore && team1 == TeamColour.RED) {
			result = 1;
		} else if (blueScore == redScore) {
			result = 0;
		} else {
			result = -1;
		}
	}

	protected Vector2 arrowTipFactory(Texture texture) {
		if (texture == pathArrow) {
			return new Vector2(100, 195);
		}
		if (texture == pushIndicator) {
			return new Vector2(25, 80);
		}
		if (texture == pointer) {
			return new Vector2(19, 86);
		}
		return new Vector2(0, 0);
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
		cursor.setHighlightedPlayer(null);
		clearActions();
		bar.setPositionToDown();
	}

	public void beginExecution() {
		Gdx.app.log("Game", "Beginning execution");
		elapsedRoundTime = 0;
		this.gameState = GameState.EXECUTION;
		bar.setPositionToUp();
	}

	public void beginSetupPhase(float setupTime) {
		remainingSetupTime = setupTime;
		gameState = GameState.SETUP;
		clearActions();
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

	public void clearSelectedPlayer() {
		selectedPlayer = null;
	}

	public List<Player> getPlayers(TeamColour colour) {
		if (colour == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	public Player getGoalie(TeamColour colour) {
		if (colour == TeamColour.RED) {
			return redGoalie;
		} else {
			return blueGoalie;
		}
	}

	/**
	 * Depreciated.
	 * 
	 * @return team1
	 */
	public TeamColour getHumanColour() {
		return team1;
	}

	/**
	 * Depreciated if AI becomes constructed with a colour.
	 */
	public TeamColour getComputerColour() {
		return team2;
	}

	public TeamColour getCurrentTeamColour() {
		return currentTeam;
	}

	/**
	 * depreciated Use getPlayers(TeamColour colour) instead
	 * 
	 * @return Team1's players
	 */
	public List<Player> getHumanPlayers() {
		if (team1 == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	/**
	 * depreciated Use getPlayers(TeamColour colour) instead
	 * 
	 * @return Team2's Players
	 */
	public List<Player> getComputerPlayers() {
		if (team2 == TeamColour.RED) {
			return redPlayers;
		} else {
			return bluePlayers;
		}
	}

	/**
	 * depreciated Use getGoalie(TeamColour colour) instead
	 * 
	 * @return Team1's Goalie
	 */
	public Player getHumanGoalie() {
		if (team1 == TeamColour.RED) {
			return redGoalie;
		} else {
			return blueGoalie;
		}
	}

	/**
	 * depreciated Use getGoalie(TeamColour colour) instead
	 * 
	 * @return Team2's Goalie
	 */
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

	protected Rectangle getStatsRectangle(boolean isAtTopPosition) {
		float pointWidth = 10;
		float pointCount = 25;
		try {
			if (isAtTopPosition) {
				return new Rectangle(VIRTUAL_SCREEN_WIDTH - stats.getWidth()
						- pointCount * pointWidth, 0, stats.getWidth()
						+ pointWidth * pointCount, stats.getHeight());
			} else {
				return new Rectangle(VIRTUAL_SCREEN_WIDTH - stats.getWidth()
						- pointCount * pointWidth, VIRTUAL_SCREEN_HEIGHT
						- stats.getHeight(), stats.getWidth() + pointWidth
						* pointCount, stats.getHeight());
			}
		} catch (NullPointerException e) {
			return null;
		}
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
		statPoint1.dispose();
		statPoint2.dispose();
		statPoint3.dispose();
		statPoint4.dispose();
		statPoint5.dispose();
		stats.dispose();
		goalMessage.dispose();
		whistleBlow.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		ghostSpriteSheet.dispose();
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

	public void menuButtonPressed() {
		actionResolver.openGuideBook();
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
		Player playersGoalie = getGoalie(currentTeam);
		if (playersGoalie != null) {
			if (playersGoalie.hasBall()) {
				if (!playersGoalie.kicksBall(roundTime)) {
					getBar().setText("Goalie cannot hold onto the ball");
					Gdx.app.log("Game", "Goalie needs to kick the ball");
					return;
				}
			}
		}

		beginExecution();
	}

	protected void textAreaButtonPressed() {
		gameState = gameStateToGoIntoWhenBackButtonPressed;
		textArea = new NullTextArea();
	}

	public void onGoalieObtainsBall(TeamColour teamColour) {
		beginSetupPhase(2.5f);
	}

	protected Circle getGoalAreaCircle(TeamColour teamColour) {

		float circleRadius = 200;
		if (teamColour == TeamColour.RED) {
			return new Circle(RED_GOAL, circleRadius);
		} else {
			return new Circle(BLUE_GOAL, circleRadius);
		}
	}

	protected Vector2 getGoalVector(TeamColour teamColour) {
		if (teamColour == TeamColour.RED) {
			return RED_GOAL;
		} else if (teamColour == TeamColour.BLUE) {
			return BLUE_GOAL;
		}
		return null;
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
				Circle inputDetectionCircle = new Circle(playerVector,
						INPUT_EPSILON_VALUE);
				if (inputDetectionCircle.contains(point)) {
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
				Circle inputDetectionCircle = new Circle(playerVector,
						INPUT_EPSILON_VALUE);
				if (inputDetectionCircle.contains(point)) {
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

		Circle inputDetectionCircle = new Circle(ball.getBallPosition(),
				INPUT_EPSILON_VALUE);
		return inputDetectionCircle.contains(point);
	}

	protected boolean isSelectable(Player player) {

		if (getPlayers(currentTeam).contains(player)) {
			return true;
		}

		if (getGoalie(currentTeam) != null) {
			if (getGoalie(currentTeam).hasBall()
					&& getGoalie(currentTeam) == player) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when a line is drawn starting and finishing on top of a player &
	 * not a ball
	 */
	private void pressPlayer(Player pressedPlayer) {

		if (selectedPlayer == null && isSelectable(pressedPlayer)) {
			selectTextureStateTime = 0f;
			selectedPlayer = pressedPlayer;
			return;
		} else if (selectedPlayer == pressedPlayer) {
			selectedPlayer = null;
			return;
		}

		if (selectedPlayer != null
				&& pressedPlayer.getTeam() == getCurrentTeamColour()) {
			// If both players are selectable pass between them
			selectedPlayer.addAction(new Pass(ball, selectedPlayer,
					pressedPlayer, selectedPlayer.getFuturePosition()));

		} else if (selectedPlayer != null
				&& pressedPlayer.getTeam() != getCurrentTeamColour()
				&& !(pressedPlayer instanceof Goalie)) {
			// If the first player is selectable, the second player is on the
			// opposingTeam but is not a goalie then mark the second player
			selectedPlayer.addAction(new Mark(selectedPlayer
					.getFuturePosition(), pressedPlayer, this.getBall()));
		}
		selectedPlayer = null;
	}

	/** Called when a line is drawn starting and finishing on top of a ball */
	private void pressBall() {
		Gdx.app.log(INPUT_TAG, "Pressed ball: ");

		if (selectedPlayer != null) {
			selectedPlayer.addAction(new MarkBall(selectedPlayer
					.getFuturePosition(), ball));
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
			return true;
		} else if (keycode == Keys.MENU) {
			menuButtonPressed();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	/**
	 * @warning Changing variables accessed by other methods can cause it to
	 *          have an out of date variable.
	 * 
	 * @warning2 The input processor can and will run before the libgdx life
	 *           cycle. Hence any textures or other variables initialised at
	 *           this stage will not be ready.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if (!controlsActive) {
			return false;
		}

		Vector2 point = translateInputToField(new Vector2(screenX, screenY));

		if (getStatsRectangle(positionStatsAtTop) != null
				&& getStatsRectangle(positionStatsAtTop).contains(point.x,
						point.y)) {
			positionStatsAtTop = !positionStatsAtTop;
		}

		if (getGameState() == GameState.PAUSED) {
			textArea.onPress(point.x, point.y);
			return true;
		}

		if (getGameState() == GameState.INPUT) {

			bar.onPress(point.x, point.y);

			if (getGameState() == GameState.INPUT) {
				cursor.setHighlightedPlayer(findPlayer(point));
				cursor.setTexture(null);

				isBallHighlighted = findBall(point);

				if (!bar.contains(point.x, point.y)) {
					lineInProgress.clear();
					lineInProgress.add(point);
				}
			}
		}

		return true;
	}

	/**
	 * @warning Changing variables accessed by other methods can cause it to
	 *          have an out of date variable.
	 * 
	 * @warning2 The input processor can and will run before the libgdx life
	 *           cycle. Hence any textures or other variables initialised at
	 *           this stage will not be ready.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (!controlsActive) {
			return false;
		}

		cursor.setHighlightedPlayer(null);
		isBallHighlighted = false;
		cursor.setTexture(null);

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

		boolean canShoot = canShoot(start);
		boolean canPass = canPass(start, finish);
		boolean canMark = canMark(start, finish);
		boolean canMarkBall = canMarkBall(startVector, endVector);
		boolean canSelectPlayer = canSelectPlayer(start, finish);
		boolean canMove = canMove(start, finish);

		if (canPass) {
			selectedPlayer.addAction(new Pass(ball, selectedPlayer, start,
					selectedPlayer.getFuturePosition()));
			selectedPlayer = null;
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (canMarkBall) {
			selectedPlayer.addAction(new MarkBall(selectedPlayer
					.getFuturePosition(), ball));
			selectedPlayer = null;
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (canMark) {
			selectedPlayer.addAction(new Mark(selectedPlayer
					.getFuturePosition(), start, ball));
			selectedPlayer = null;
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (canShoot) {
			selectedPlayer.addAction(new Kick(ball, endVector, selectedPlayer
					.getFuturePosition()));
			selectedPlayer = null;
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (canSelectPlayer) {
			selectedPlayer = selectedPlayer == null ? start : null;
			selectTextureStateTime = 0f;
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (canMove) {
			assignMoveTo(start, findPlayerIndex(startVector));
			lineInProgress.clear();
			cursor.setTexture(null);
			return true;
		}

		if (!canSelectPlayer) {

		}

		// Old algorithm
		// if (start == null && finish == null) {
		// Gdx.app.log(INPUT_TAG, "You pressed: " + startVector.toString());
		// if (startAtBall && finishedAtBall && selectedPlayer != null) {
		// Gdx.app.log(INPUT_TAG, "You marked the ball");
		// pressBall();
		// } else {
		// pressPoint(endVector);
		// }
		//
		// } else if (startAtBall && finishedAtBall && selectedPlayer != null) {
		// Gdx.app.log(INPUT_TAG, "You marked the ball");
		// pressBall();
		//
		// } else if (start == null) {
		// Gdx.app.log(INPUT_TAG,
		// "You drew a line starting from a null position");
		//
		// } else if (start == finish) {
		// Gdx.app.log(INPUT_TAG, "You selected a player");
		// pressPlayer(start);
		// } else if (!isSelectable(start)) {
		// Gdx.app.log(INPUT_TAG,
		// "Your line started from an unselectable player");
		// } else if (isSelectable(start)) {
		// Gdx.app.log(INPUT_TAG, "You drew a line from a player");
		// int index = findPlayerIndex(lineInProgress.get(0));
		// Gdx.app.log(INPUT_TAG,
		// "You drew a line from a player " + String.valueOf(index));
		// assignMoveTo(start, index);
		//
		// }

		lineInProgress.clear();
		cursor.setTexture(null);
		return true;
	}

	private boolean canSelectPlayer(Player start, Player finish) {
		return start != null && start == finish && isSelectable(start);
	}

	private boolean canMove(Player start, Player finish) {
		return start != null && finish != start && isSelectable(start);
	}

	private boolean canShoot(Player start) {
		return start == null && selectedPlayer != null;
	}

	private boolean canPass(Player start, Player finish) {
		return start != null && start == finish && selectedPlayer != null
				&& start.getTeam() == getCurrentTeamColour()
				&& start != selectedPlayer;
	}

	private boolean canMark(Player start, Player finish) {
		return start != null && start == finish && selectedPlayer != null
				&& start.getTeam() != getCurrentTeamColour()
				&& !(start instanceof Goalie) && start != selectedPlayer;
	}

	private boolean canMarkBall(Vector2 start, Vector2 finish) {
		return findBall(start) && findBall(finish) && selectedPlayer != null;
	}

	/**
	 * @warning Changing variables accessed by other methods can cause it to
	 *          have an out of date variable.
	 * 
	 * @warning2 The input processor can and will run before the libgdx life
	 *           cycle. Hence any textures or other variables initialised at
	 *           this stage will not be ready.
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if (!controlsActive) {
			return false;
		}

		Vector2 point = translateInputToField(new Vector2(screenX, screenY));

		if (getStatsRectangle(positionStatsAtTop) != null
				&& getStatsRectangle(positionStatsAtTop).contains(point.x,
						point.y)) {
			positionStatsAtTop = !positionStatsAtTop;
		}

		if (getGameState() == GameState.INPUT) {
			if (!bar.contains(point.x, point.y)) {
				lineInProgress.add(point);
				if (lineInProgress.size() < 2) {
					return false;
				}
				cursor.setLocation(point.x, point.y);
				cursor.setRotation(0);

				Player startPlayer = findPlayer(lineInProgress.get(0));
				Player endPlayer = findPlayer(lineInProgress.get(lineInProgress
						.size() - 1));

				Vector2 startVector = lineInProgress.get(0);
				Vector2 endVector = lineInProgress
						.get(lineInProgress.size() - 1);

				boolean startAtBall = findBall(startVector);
				boolean endAtBall = findBall(endVector);
				

				boolean canShoot = canShoot(startPlayer);
				boolean canPass = canPass(startPlayer, endPlayer);
				boolean canMark = canMark(startPlayer, endPlayer);
				boolean canMarkBall = canMarkBall(startVector, endVector);
				boolean canSelectPlayer = canSelectPlayer(startPlayer,
						endPlayer);
				boolean canMove = canMove(startPlayer, endPlayer);

				if (canPass) {
					cursor.setTexture(passSprite);
					return true;
				}

				if (canMarkBall) {
					cursor.setTexture(markBallSprite);
					return true;
				}

				if (canMark) {
					cursor.setTexture(markSprite);
					return true;
				}

				if (canShoot) {
					cursor.setTexture(kickSprite);
					return true;
				}

				if (canSelectPlayer) {
					cursor.setTexture(null);
				}

				if (canMove) {

					cursor.setTexture(moveSprite);

					if (lineInProgress.size() >= 5) {
						Vector2 nearEndPoint = lineInProgress
								.get(lineInProgress.size() - 5);
						float rotation = new Vector2(endVector.x
								- nearEndPoint.x, endVector.y - nearEndPoint.y)
								.angle();
						cursor.setRotation(rotation + 45 + 90);
					}
					return true;
				}
				
				if (!canSelectPlayer && startPlayer != null) {
					cursor.setTexture(unselectableSprite);
					return true;
				}
				

				// Old algorithm
//				boolean tapBall = (startAtBall && endAtBall);
//				boolean tapPlayer = (startPlayer == endPlayer);
//
//				if (selectedPlayer == null) {
//					if (startPlayer == null) {
//						// Does nothing
//						cursor.setTexture(null);
//						return true;
//					} else if (tapPlayer) {
//						// Select player
//						cursor.setTexture(null);
//						return true;
//					} else {
//						// Move player
//						cursor.setTexture(moveSprite);
//
//						if (lineInProgress.size() >= 5) {
//							Vector2 nearEndPoint = lineInProgress
//									.get(lineInProgress.size() - 5);
//							float rotation = new Vector2(endVector.x
//									- nearEndPoint.x, endVector.y
//									- nearEndPoint.y).angle();
//							cursor.setRotation(rotation + 45 + 90);
//						}
//
//						return true;
//					}
//				} else {
//					if (tapBall) {
//						// Mark ball action
//						cursor.setTexture(markBallSprite);
//						return true;
//					}
//					if (startPlayer == null) {
//						// Kick action
//						cursor.setTexture(kickSprite);
//						return true;
//					} else if (tapPlayer) {
//						if (startPlayer.getTeam() == getCurrentTeamColour()) {
//							if (startPlayer == selectedPlayer) {
//								// Deselect player
//								cursor.setTexture(null);
//								return true;
//							} else {
//								// Pass action
//								cursor.setTexture(passSprite);
//								return true;
//							}
//						} else {
//							// Mark player action
//							if (endPlayer instanceof Goalie) {
//								cursor.setTexture(unselectableSprite);
//							} else {
//								cursor.setTexture(markSprite);
//							}
//							return true;
//						}
//					} else {
//						// Move player
//						cursor.setTexture(moveSprite);
//
//						if (lineInProgress.size() >= 5) {
//							Vector2 nearEndPoint = lineInProgress
//									.get(lineInProgress.size() - 5);
//							float rotation = new Vector2(endVector.x
//									- nearEndPoint.x, endVector.y
//									- nearEndPoint.y).angle();
//							cursor.setRotation(rotation + 45 + 90);
//						}
//
//						return true;
//					}
//				}
			}
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		if (!controlsActive) {
			return false;
		}

		Vector2 hoverPoint = translateInputToField(new Vector2(screenX, screenY));

		if (getGameState() == GameState.INPUT) {
			cursor.setHighlightedPlayer(findPlayer(hoverPoint));
			isBallHighlighted = findBall(hoverPoint);
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/**
	 * @warning Changing variables accessed by other methods can cause it to
	 *          have an out of date variable.
	 * 
	 * @warning2 The input processor can and will run before the libgdx life
	 *           cycle. Hence any textures or other variables initialised at
	 *           this stage will not be ready.
	 */
	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		if (!controlsActive) {
			return false;
		}

		Vector2 hoverPoint = translateInputToField(new Vector2(event.getX(),
				event.getY()));

		if (bar != null && bar.contains(hoverPoint.x, hoverPoint.y)) {
			Gdx.app.log("BAR", "hover eventVector: " + hoverPoint.toString()
					+ " " + bar.toString());
			isBallHighlighted = false;
			cursor.setHighlightedPlayer(null);
			cursor.setTexture(null);
			return false;
		}

		// When almost out of range hover detects itself at top left
		if (hoverPoint.x <= 1 && hoverPoint.y <= 1) {
			isBallHighlighted = false;
			cursor.setHighlightedPlayer(null);
			cursor.setTexture(null);
			return false;
		}

		if (getStatsRectangle(positionStatsAtTop) != null
				&& getStatsRectangle(positionStatsAtTop).contains(hoverPoint.x,
						hoverPoint.y)) {
			positionStatsAtTop = !positionStatsAtTop;
		}

		if (getGameState() == GameState.INPUT) {
			if (!bar.contains(hoverPoint.x, hoverPoint.y)) {

				Player hoverPlayer = findPlayer(hoverPoint);
				isBallHighlighted = findBall(hoverPoint);
				cursor.setLocation(hoverPoint.x, hoverPoint.y);
				cursor.setHighlightedPlayer(hoverPlayer);

				boolean canShoot = canShoot(hoverPlayer);
				boolean canPass = canPass(hoverPlayer, hoverPlayer);
				boolean canMark = canMark(hoverPlayer, hoverPlayer);
				boolean canMarkBall = canMarkBall(hoverPoint, hoverPoint);
				boolean canSelectPlayer = canSelectPlayer(hoverPlayer,
						hoverPlayer);

				if (canPass) {
					cursor.setTexture(passSprite);
					return true;
				}

				if (canMarkBall) {
					cursor.setTexture(markBallSprite);
					return true;
				}

				if (canMark) {
					cursor.setTexture(markSprite);
					return true;
				}

				if (canShoot) {
					cursor.setTexture(kickSprite);
					return true;
				}

				if (!canSelectPlayer && hoverPlayer != null) {

				}

				// Old algorithmn
				// if (selectedPlayer != null) {
				// if (isBallHighlighted) {
				// // Mark ball action
				// cursor.setTexture(markBallSprite);
				// return true;
				// } else if (hoverPlayer == null) {
				// // Kick action
				// cursor.setTexture(kickSprite);
				// return true;
				// } else if (hoverPlayer.getTeam() == getCurrentTeamColour()) {
				// if (hoverPlayer == selectedPlayer) {
				// // Deselect player
				// cursor.setTexture(null);
				// return true;
				// } else {
				// // Pass action
				// cursor.setTexture(passSprite);
				// return true;
				// }
				// } else {
				// // Mark player action
				// if (hoverPlayer instanceof Goalie) {
				// cursor.setTexture(unselectableSprite);
				// } else {
				// cursor.setTexture(markSprite);
				// }
				// return true;
				// }
				// }

			}
		}
		cursor.setTexture(null);
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
