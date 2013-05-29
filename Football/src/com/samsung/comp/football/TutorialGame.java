package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.samsung.comp.events.ActionFiredObserver;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Players.BluePlayer;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class TutorialGame extends AbstractGame implements ActionFiredObserver {

	enum TutorialPhase {
		MOVE, FOLLOW, SHOOT, PASS, MARK, QUEUEING, GOALIE,
	}

	boolean q1 = false;

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

		createFirstPlayer();

		humanColour = TeamColour.BLUE;
		computerColour = TeamColour.RED;

		remainingMatchTime = ROUND_TIME;

		beginInputStage();
	}

	@Override
	protected void setStartingPositions(TeamColour centerTeam) {

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();

		whistleBlow.play();

		onGoalScored();
	}

	private void onGoalScored() {
		if (tutorialPhase == TutorialPhase.SHOOT) {
			tutorialPhase = TutorialPhase.PASS;
			createSecondPlayer();
		}

	}

	private void createFirstPlayer() {
		Player p = new BluePlayer(338, VIRTUAL_SCREEN_HEIGHT / 3);
		p.subscribe(this);
		bluePlayers.add(p);
	}

	private void createNewBall() {
		ball = new Ball(VIRTUAL_SCREEN_WIDTH / 2, VIRTUAL_SCREEN_HEIGHT / 3);
	}

	private void createSecondPlayer() {
		Player p = new BluePlayer(338, 256);
		p.subscribe(this);
		bluePlayers.add(p);
	}

	@Override
	protected void beginInputStage() {
		gameState = GameState.INPUT;
		inputStrategy.deselectPlayers();
		clearActions();
		bar.setPositionToDown();
		
		checkPhaseCompletion();
		displayTutorialMessage();
	}

	// If these get complex then refactor to use state objects.
	private void checkPhaseCompletion() {
		if (tutorialPhase == TutorialPhase.MOVE) {
			if (bluePlayers.get(0).getPlayerY() >= VIRTUAL_SCREEN_HEIGHT / 2) {
				tutorialPhase = TutorialPhase.FOLLOW;
				createNewBall();
			}
		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			if (bluePlayers.get(0).hasBall()) {
				tutorialPhase = TutorialPhase.SHOOT;
			}
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			// Handled by onGoalScored()
		} else if (tutorialPhase == TutorialPhase.PASS) {
			if (bluePlayers.get(1).hasBall()) {
				tutorialPhase = TutorialPhase.MARK;
			}
		} else if (tutorialPhase == TutorialPhase.MARK) {
			// Handled by onActionFired()
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {

		} else if (tutorialPhase == TutorialPhase.GOALIE) {

		}

	}

	protected void displayTutorialMessage() {
		if (tutorialPhase == TutorialPhase.MOVE) {
			textArea.setText("Welcome to the tutorial. \n\n"
					+ "Draw a line from your player to give them a path to follow.\n\n"
					+ "Press the play button and they'll run for 5 seconds.\n\n"
					+ "Move this player to the bottom half of the pitch.\n\n"
					+ "Tap or press back to close this window. ");
			bar.setText("Draw a line to move player");
		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			textArea.setText("Nice. Now we've put a ball onto the upper half of the pitch. \n\n"
					+ "Tap on your player to select them, then tap the ball.\n\n"
					+ "Your player will run straight towards the ball to collect it.\n\n"
					+ "Don't forget to hit the play button when you're ready.");
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			textArea.setText("Tap on your player to select them again, then tap on the pitch to shoot. \n\n"
					+ "How else are you going to score? \n\n"
					+ "Shoot the ball into either goal.");
		} else if (tutorialPhase == TutorialPhase.PASS) {
			textArea.setText("Passing");
		} else if (tutorialPhase == TutorialPhase.MARK) {
			textArea.setText("Marking");
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			textArea.setText("Queueing actions.");
			q1 = false;
		} else if (tutorialPhase == TutorialPhase.GOALIE) {
			textArea.setText("The goal keeper.");
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
		remainingMatchTime = ROUND_TIME;
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
			remainingMatchTime -= time;

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

	@Override
	public void onActionFired(Player player, Action action) {
		if (tutorialPhase == TutorialPhase.MARK) {
			if (action instanceof Mark) {
				tutorialPhase = TutorialPhase.QUEUEING;
			}
		}
 else if (tutorialPhase == TutorialPhase.QUEUEING) {
			if (action instanceof Move) {
				q1 = true;
			} else if (action instanceof Pass && q1 == true) {
				tutorialPhase = TutorialPhase.GOALIE;
			}
		}
	}

}
