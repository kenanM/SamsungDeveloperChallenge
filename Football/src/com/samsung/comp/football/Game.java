package com.samsung.comp.football;

import java.util.LinkedList;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.BlueGoalie;
import com.samsung.comp.football.Players.BluePlayer;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.Players.RedGoalie;
import com.samsung.comp.football.Players.RedPlayer;

public class Game extends AbstractGame {

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
		MarkBall.create(new Texture(Gdx.files.internal("markingIcon.png")));
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
		shapeRenderer = new ShapeRenderer();
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

	private void setStartingPositions(TeamColour centerTeam) {

		redPlayers.get(2).x = Player.translatePlayerCoordinate(169);
		redPlayers.get(2).y = Player.translatePlayerCoordinate(704);

		redPlayers.get(3).x = Player.translatePlayerCoordinate(507);
		redPlayers.get(3).y = Player.translatePlayerCoordinate(704);

		redGoalie.x = Player.translatePlayerCoordinate(338);
		redGoalie.y = Player.translatePlayerCoordinate(850);

		bluePlayers.get(2).x = Player.translatePlayerCoordinate(169);
		bluePlayers.get(2).y = Player.translatePlayerCoordinate(320);

		bluePlayers.get(3).x = Player.translatePlayerCoordinate(507);
		bluePlayers.get(3).y = Player.translatePlayerCoordinate(320);

		blueGoalie.x = Player.translatePlayerCoordinate(338);
		blueGoalie.y = Player.translatePlayerCoordinate(174);

		if (centerTeam == TeamColour.BLUE) {
			bluePlayers.get(0).x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) - (Player
							.getPlayerSize())));
			bluePlayers.get(0).y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			bluePlayers.get(1).x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) + (Player
							.getPlayerSize())));
			bluePlayers.get(1).y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			bluePlayers.get(0).setRotation(0);
			bluePlayers.get(1).setRotation(180);
		} else {
			bluePlayers.get(0).x = Player.translatePlayerCoordinate(338);
			bluePlayers.get(0).y = Player.translatePlayerCoordinate(334);

			bluePlayers.get(1).x = Player.translatePlayerCoordinate(338);
			bluePlayers.get(1).y = Player.translatePlayerCoordinate(256);
		}

		if (centerTeam == TeamColour.RED) {
			redPlayers.get(0).x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) - (Player
							.getPlayerSize())));
			redPlayers.get(0).y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			redPlayers.get(1).x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) + (Player
							.getPlayerSize())));
			redPlayers.get(1).y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			redPlayers.get(0).setRotation(0);
			redPlayers.get(1).setRotation(180);
		} else {
			redPlayers.get(0).x = Player.translatePlayerCoordinate(338);
			redPlayers.get(0).y = Player.translatePlayerCoordinate(768);

			redPlayers.get(1).x = Player.translatePlayerCoordinate(338);
			redPlayers.get(1).y = Player.translatePlayerCoordinate(640);
		}

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

		redPlayers.add(new RedPlayer(338, 768, 520, 150, 100, 20, 380));
		redPlayers.add(new RedPlayer(338, 640, 540, 200, 80, 20, 380));
		redPlayers.add(new RedPlayer(169, 704, 550, 50, 100, 40, 420));
		redPlayers.add(new RedPlayer(507, 704, 530, 150, 80, 40, 420));
		redGoalie = new RedGoalie(338, 900, this, 500);

		bluePlayers = new LinkedList<Player>();

		bluePlayers.add(new BluePlayer(338, 256));
		bluePlayers.add(new BluePlayer(338, 384));
		bluePlayers.add(new BluePlayer(507, 320));
		bluePlayers.add(new BluePlayer(169, 320));
		blueGoalie = new BlueGoalie(338, 124, this, 500);

		if (Utils.randomFloat(rng, 0, 1) > 0.5) {
			setStartingPositions(TeamColour.BLUE);
		} else {
			setStartingPositions(TeamColour.RED);
		}
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


	public String getRemainingTime() {

		int minutes = (int) remainingMatchTime / 60;
		int seconds = (int) remainingMatchTime % 60;

		String remainingTimeString = (seconds > 9) ? minutes + ":" + seconds
				: minutes + ":0" + seconds;

		return remainingTimeString;
	}



	public int getRedScore() {
		return redScore;
	}

	public int getBlueScore() {
		return blueScore;
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
		super.resize(width, height);
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
