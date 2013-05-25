package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class TutorialGame extends AbstractGame {

	enum TutorialPhase {
		MOVE, FOLLOW, SHOOT, PASS, MARK, QUEUEING, GOALIE,
	}

	private TutorialPhase tutorialPhase = TutorialPhase.MOVE;

	@Override
	public void create() {

		createLibGdxItems();
		createMainTextures();
		createSfx();
		createActions();
		createIteractiveObjects();
		createUI();
		createRenderingObjects();

		createNewPlayersAndBall();

		humanColour = TeamColour.BLUE;
		computerColour = TeamColour.RED;

		remainingMatchTime = 0;

		beginInputStage();
	}

	@Override
	protected void setStartingPositions(TeamColour centerTeam) {

	}

	private void createNewPlayersAndBall() {

		// Create a ball

		// create the players


	}

	@Override
	protected void beginInputStage() {
		gameState = GameState.INPUT;
		inputStrategy.deselectPlayers();
		clearActions();
		bar.setPositionToDown();
		
		if (tutorialPhase == TutorialPhase.MOVE) {
			textArea.setText("Welcome to the tutorial. This game is separated into phases. "
					+ "Draw a line from your player to give them the command to move. "
					+ "Press the play button with your finger when you want them to carry out your order.");
			bar.setText("Draw a line to move player");
		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			textArea.setText("Welcome");
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			textArea.setText("Welcome");
		} else if (tutorialPhase == TutorialPhase.PASS) {
			textArea.setText("Welcome");
		} else if (tutorialPhase == TutorialPhase.MARK) {
			textArea.setText("Welcome");
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			textArea.setText("Welcome");
		} else if (tutorialPhase == TutorialPhase.GOALIE) {
			textArea.setText("Welcome");
		}
		textAreaTypeDisplayed = 0;
		gameState = GameState.PAUSED;
		gameStateToGoIntoWhenBackButtonPressed = GameState.INPUT;
	}

	@Override
	public void playButtonPressed() {
		if (gameState == GameState.PAUSED) {
			return;
		}

		beginExecution();
	}

	@Override
	public void beginExecution() {
		elapsedRoundTime = 0;
		this.gameState = GameState.EXECUTION;
		bar.setPositionToUp();
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

	@Override
	protected void update() {

		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);

		if (gameState == GameState.EXECUTION) {

			elapsedRoundTime += time;

			for (Player player : getAllPlayers()) {
				player.executeAction();
				player.update(time);
			}

			if (ball != null) {
				ball.update(time);
				ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
						VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
				tackleDetection(time);
				goalScoredDetection();
			}

			if (elapsedRoundTime >= ROUND_TIME) {
				gameState = GameState.INPUT;
				beginInputStage();
			}

		}
	}

	@Override
	public String getRemainingTime() {
		return "--:--";
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
