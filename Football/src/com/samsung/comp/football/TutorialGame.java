package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.ActionFiredListener;
import com.samsung.comp.events.ButtonPressListener;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class TutorialGame extends AbstractGame {

	enum TutorialPhase {
		MOVE, FOLLOW, SHOOT, PASS, MARK, QUEUEING, GOALIE,
	}

	boolean q1 = false;

	private TutorialPhase tutorialPhase = TutorialPhase.MOVE;
	private boolean shootCompleted = false;
	private float setupTime = 0f;

	@Override
	public void create() {

		createLibGdxItems();
		createMainTextures();
		createSfx();
		createActions();
		createIteractiveObjects();
		createUI();
		createRenderingObjects();

		setupMovePhase();

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		remainingMatchTime = ROUND_TIME;

		beginInputStage();
	}

	@Override
	protected void setStartingPositions(TeamColour centerTeam) {

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();

		whistleBlow.play();

		if (tutorialPhase == TutorialPhase.SHOOT) {
			shootCompleted = true;
			setupTime = 0f;
		}
	}

	private void setupMovePhase() {
		Player p = new Player(338, VIRTUAL_SCREEN_HEIGHT / 3, TeamColour.BLUE);
		p.setListener(new ActionFiredListener() {
			@Override
			public void onActionFired(Player player, Action action) {
				actionFired(player, action);
			}
		});
		bluePlayers.add(p);
	}

	private void setupFollowBallPhase() {
		ball = new Ball(VIRTUAL_SCREEN_WIDTH / 2, VIRTUAL_SCREEN_HEIGHT / 3);
	}

	private void setupPassPhase() {
		Player p = new Player(0, 256, TeamColour.BLUE);
		p.setListener(new ActionFiredListener() {
			@Override
			public void onActionFired(Player player, Action action) {
				actionFired(player, action);
			}
		});
		bluePlayers.add(p);

		ball.setOwner(p);
		p.addAction(new MoveToPosition(new Vector2(
				VIRTUAL_SCREEN_WIDTH * 2 / 3, 256), p));
		p.executeAction();
		p.clearActions();

		Player p0 = bluePlayers.get(0);
		p0.addAction(
				new MoveToPosition(new Vector2(VIRTUAL_SCREEN_WIDTH / 3,
				750), p));
		p0.executeAction();
		p0.clearActions();
		setupTime = 3f;
	}

	private void setupTacklePhase() {
		Player p = new Player(VIRTUAL_SCREEN_WIDTH * 1 / 3,
				VIRTUAL_SCREEN_HEIGHT, TeamColour.RED);
		redPlayers.add(p);

		Player p1 = new Player(VIRTUAL_SCREEN_WIDTH * 2 / 3, 0, TeamColour.RED);
		redPlayers.add(p1);

		Player owner = ball.getOwner();
		owner.addAction(new Pass(ball, owner, p, owner.getPlayerPosition()));
		owner.executeAction();
		owner.clearActions();
		setupTime = 5f;
	}

	@Override
	protected void beginInputStage() {
		gameState = GameState.INPUT;
		selectedPlayer = null;
		cursor.setHighlightedPlayer(null);
		clearActions();
		bar.setPositionToDown();
		
		setupTime = 0f;
		checkPhaseCompletion();
		runPhaseSpecficActions();
		displayTutorialMessage();
	}

	private void runPhaseSpecficActions() {
		if (tutorialPhase == TutorialPhase.MOVE) {
			arrows.add(new Arrow(pointer, bluePlayers.get(0).getPlayerX(),
					bluePlayers.get(0).y));
			Arrow a1 = new Arrow(pointer);
			Arrow a2 = new Arrow(pointer);
			Arrow a3 = new Arrow(pointer);
			a1.pointAt(VIRTUAL_SCREEN_WIDTH / 2, VIRTUAL_SCREEN_HEIGHT / 2);
			a2.pointAt(VIRTUAL_SCREEN_WIDTH / 2 + a2.getWidth(),
					VIRTUAL_SCREEN_HEIGHT / 2);
			a3.pointAt(VIRTUAL_SCREEN_WIDTH / 2 - a3.getWidth(),
					VIRTUAL_SCREEN_HEIGHT / 2);
			arrows.add(a1);
			arrows.add(a2);
			arrows.add(a3);
			Arrow a = new Arrow(pointer, 25 + 32, VIRTUAL_SCREEN_HEIGHT);
			arrows.add(a);

		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			
		} else if (tutorialPhase == TutorialPhase.PASS) {
			
		} else if (tutorialPhase == TutorialPhase.MARK) {
			Player p = redPlayers.get(0);
			Player p1 = redPlayers.get(1);

			ball.setOwner(p);

			// Move to original position
			p.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 1 / 3,
							VIRTUAL_SCREEN_HEIGHT * 5 / 6), p));
			p.executeAction();

			p1.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 2 / 3,
							VIRTUAL_SCREEN_HEIGHT * 1 / 6), p));
			p1.executeAction();
			p1.clearActions();
			p.clearActions();
			setupTime = 5f;
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {

		} else if (tutorialPhase == TutorialPhase.GOALIE) {

		}
	}

	// If these get complex then refactor to use state objects.
	private void checkPhaseCompletion() {
		if (tutorialPhase == TutorialPhase.MOVE) {
			if (bluePlayers.get(0).getPlayerY() >= VIRTUAL_SCREEN_HEIGHT / 2) {
				tutorialPhase = TutorialPhase.FOLLOW;
				setupFollowBallPhase();
			}
		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			if (bluePlayers.get(0).hasBall()) {
				tutorialPhase = TutorialPhase.SHOOT;
			}
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			if (shootCompleted) {
				tutorialPhase = TutorialPhase.PASS;
				setupPassPhase();
			}
		} else if (tutorialPhase == TutorialPhase.PASS) {
			if (bluePlayers.get(0).hasBall()) {
				tutorialPhase = TutorialPhase.MARK;
				setupTacklePhase();
			}
		} else if (tutorialPhase == TutorialPhase.MARK) {
			if (ball.hasOwner()) {
				if (ball.getOwner().getTeam() == team1) {
					tutorialPhase = TutorialPhase.QUEUEING;
				}
			}
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {

		} else if (tutorialPhase == TutorialPhase.GOALIE) {

		}

	}

	protected void displayTutorialMessage() {
		textArea = new TextArea();
		textArea.setListener(new ButtonPressListener() {
			@Override
			public void onButtonPress() {
				textAreaButtonPressed();
			}
		});
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
			textArea.setText("Now our second player turned up with the ball. Lets pass the ball back. \n\n"
					+ "Select the new player then tap on the other to give an order to pass to them. \n\n");
		} else if (tutorialPhase == TutorialPhase.MARK) {
			textArea.setText("Marking opponents: \n\n"
					+ "Select your player, then select a red player to mark them. \n\n"
					+ "They'll follow them until they get the ball. \n\n"
					+ "Mark both of these players and get the ball back.");
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			textArea.setText("Queueing actions.");
			q1 = false;
		} else if (tutorialPhase == TutorialPhase.GOALIE) {
			textArea.setText("The goal keeper.");
		}
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
		arrows.clear();

		// Really should do something about these checks now being everywhere
		if (tutorialPhase == TutorialPhase.MARK) {
			Player p = redPlayers.get(0);
			Player p1 = redPlayers.get(1);

			// Add Move to target
			p.addAction(new Pass(ball, p, p1, p.getPlayerPosition()));

			p.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 1 / 3,
							VIRTUAL_SCREEN_HEIGHT * 1 / 6), p));

			p1.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 2 / 3,
							VIRTUAL_SCREEN_HEIGHT * 5 / 6), p));
		}

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

		} else if (setupTime > 0) {
			setupTime -= time;
			for (Player player : getAllPlayers()) {
				player.update(time);
			}

			if (ball != null) {
				ball.update(time);
				ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
						VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
				tackleDetection(time);
				goalScoredDetection();
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

	public void actionFired(Player player, Action action) {
		if (tutorialPhase == TutorialPhase.QUEUEING) {
			if (action instanceof Move) {
				q1 = true;
			} else if (action instanceof Pass && q1 == true) {
				tutorialPhase = TutorialPhase.GOALIE;
			}
		}
	}

}
