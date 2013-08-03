package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.ButtonPressListener;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class TutorialGame extends AbstractGame {

	enum TutorialPhase {
		MOVE, FOLLOW, SHOOT, PASS, MARK, QUEUEING, GOALIE,
	}

	private TutorialPhase tutorialPhase = TutorialPhase.MOVE;
	private int guideBookPage = 0;

	public TutorialGame(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

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
		displayArrows();
		beginInputStage();

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		remainingMatchTime = roundTime;

		controlsActive = true;
	}

	@Override
	protected void onGoalScored(TeamColour goalAreaColour) {

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();

		goalScoredDrawTime = 3f;
		soundManager.play(crowdCheer);

		whistleBlow.play();

		if (tutorialPhase == TutorialPhase.SHOOT) {
			tutorialPhase = TutorialPhase.PASS;
			setupPassPhase();
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			tutorialPhase = TutorialPhase.GOALIE;
			setupGoaliePhase(1.5f);
		} else if (tutorialPhase == TutorialPhase.GOALIE) {
			tutorialPhase = TutorialPhase.GOALIE;
			Gdx.app.exit();
		} else {
			beginInputStage();
		}

		displayArrows();
	}

	private void setupMovePhase() {
		Player p = new Player(338, VIRTUAL_SCREEN_HEIGHT / 3, TeamColour.BLUE);
		bluePlayers.add(p);
		beginInputStage();
	}

	private void setupFollowBallPhase() {
		ball = new Ball(VIRTUAL_SCREEN_WIDTH / 2, VIRTUAL_SCREEN_HEIGHT / 3);
		beginInputStage();
	}

	private void setupPassPhase() {
		beginSetupPhase(4f);
		Player p = new Player(0, 256, TeamColour.BLUE);
		bluePlayers.add(p);

		ball.setOwner(p);
		p.addAction(new MoveToPosition(new Vector2(VIRTUAL_SCREEN_WIDTH / 2,
				320), p));

		Player p0 = bluePlayers.get(0);
		p0.addAction(new MoveToPosition(new Vector2(VIRTUAL_SCREEN_WIDTH / 6,
				650), p));
	}

	private void setupMarkTacklePhase() {
		beginSetupPhase(2f);
		Player p = new Player(VIRTUAL_SCREEN_WIDTH * 1 / 3,
				VIRTUAL_SCREEN_HEIGHT, TeamColour.RED);
		redPlayers.add(p);
		p.addAction(new MoveToPosition(new Vector2(
				VIRTUAL_SCREEN_WIDTH * 1 / 3, VIRTUAL_SCREEN_HEIGHT * 2 / 3), p));

		Player p1 = new Player(VIRTUAL_SCREEN_WIDTH * 2 / 3, 0, TeamColour.RED);
		redPlayers.add(p1);
		p1.addAction(new MoveToPosition(new Vector2(
				VIRTUAL_SCREEN_WIDTH * 2 / 3, VIRTUAL_SCREEN_HEIGHT * 1 / 3), p));

		Player owner = ball.getOwner();
		owner.addAction(new Pass(ball, owner, p, owner.getPlayerPosition()));
	}

	private void setupQueuePhase() {
		beginSetupPhase(3f);
		Player p = redPlayers.get(0);
		Player ballOwner = ball.getOwner();

		p.addAction(new MarkBall(p.getPlayerPosition(), ball));
		ballOwner.addAction(new Pass(ball, ballOwner, p, ballOwner
				.getPlayerPosition()));
	}

	private void setupGoaliePhase(float setupTime) {
		beginSetupPhase(setupTime);
		redGoalie = new Goalie(VIRTUAL_SCREEN_WIDTH / 2, 0, TeamColour.RED,
				this, 500);
		blueGoalie = new Goalie(VIRTUAL_SCREEN_WIDTH / 2,
				VIRTUAL_SCREEN_HEIGHT,
				TeamColour.BLUE, this, 500);
	}

	@Override
	protected void beginInputStage() {
		gameState = GameState.INPUT;
		selectedPlayer = null;
		cursor.setHighlightedPlayer(null);
		clearActions();
		bar.setPositionToDown();

		displayTutorialMessage();
	}

	/**
	 * Used to setup the current phase every time after the initial setup
	 * 
	 * @return Returns true if there is any setting up to perform.
	 */
	private boolean runSubsequentPhaseSetup() {
		if (tutorialPhase == TutorialPhase.MOVE) {

		} else if (tutorialPhase == TutorialPhase.FOLLOW) {

		} else if (tutorialPhase == TutorialPhase.SHOOT) {

		} else if (tutorialPhase == TutorialPhase.PASS) {

		} else if (tutorialPhase == TutorialPhase.MARK) {
			beginSetupPhase(5f);

			Player p = redPlayers.get(0);
			Player p1 = redPlayers.get(1);

			ball.setOwner(p);

			// Move to original position
			p.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 1 / 3,
							VIRTUAL_SCREEN_HEIGHT * 5 / 6), p));

			p1.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 2 / 3,
							VIRTUAL_SCREEN_HEIGHT * 1 / 6), p));
			return true;

		} else if (tutorialPhase == TutorialPhase.QUEUEING) {

		} else if (tutorialPhase == TutorialPhase.GOALIE) {

		}
		return false;
	}

	// TODO: refactor to use state objects.
	/**
	 * Checks completion of current phase and performs the initial setup of the
	 * subsequent phase.
	 */
	private void checkPhaseCompletion() {
		boolean phaseCompleted = false;
		if (tutorialPhase == TutorialPhase.MOVE) {
			if (bluePlayers.get(0).getPlayerY() >= VIRTUAL_SCREEN_HEIGHT / 2) {
				tutorialPhase = TutorialPhase.FOLLOW;
				setupFollowBallPhase();
				phaseCompleted = true;
			}
		} else if (tutorialPhase == TutorialPhase.FOLLOW) {
			if (bluePlayers.get(0).hasBall()) {
				tutorialPhase = TutorialPhase.SHOOT;
				phaseCompleted = true;
			}
		} else if (tutorialPhase == TutorialPhase.SHOOT) {

		} else if (tutorialPhase == TutorialPhase.PASS) {
			if (bluePlayers.get(0).hasBall()) {
				tutorialPhase = TutorialPhase.MARK;
				setupMarkTacklePhase();
				phaseCompleted = true;
			}
		} else if (tutorialPhase == TutorialPhase.MARK) {
			if (ball.hasOwner()) {
				if (ball.getOwner().getTeam() == team1) {
					tutorialPhase = TutorialPhase.QUEUEING;
					setupQueuePhase();
					phaseCompleted = true;
				}
			}
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {

		} else if (tutorialPhase == TutorialPhase.GOALIE) {

		}

		displayArrows();

		if (!phaseCompleted) {
			if (!runSubsequentPhaseSetup()) {
				beginInputStage();
			}
		}
	}

	protected void displayArrows() {

		if (tutorialPhase == TutorialPhase.MOVE) {

			Player bluePlayer = bluePlayers.get(0);
			Arrow a1 = new Arrow(pathArrow);
			a1.setTip(24, 24);
			a1.pointAt(bluePlayer.getPlayerX(), bluePlayer.getPlayerY());
			arrows.add(a1);

			Arrow push = new Arrow(pushIndicator, 25 + 32,
					VIRTUAL_SCREEN_HEIGHT, arrowTipFactory(pushIndicator));
			arrows.add(push);

		} else if (tutorialPhase == TutorialPhase.FOLLOW) {

			Arrow push = new Arrow(pushIndicator, bluePlayers.get(0)
					.getPlayerX(), bluePlayers.get(0).getPlayerY(),
					arrowTipFactory(pushIndicator));
			Arrow push2 = new Arrow(pushIndicator, ball.getBallX(), ball.y,
					arrowTipFactory(pushIndicator));
			arrows.add(push);
			arrows.add(push2);

		} else if (tutorialPhase == TutorialPhase.SHOOT) {

			Arrow push = new Arrow(pushIndicator, ball.getBallX(), ball.y,
					arrowTipFactory(pushIndicator));
			Arrow push2 = new Arrow(pushIndicator,
					Game.VIRTUAL_SCREEN_WIDTH / 2,
					Game.RED_GOAL_AREA.getHeight(),
					arrowTipFactory(pushIndicator));

			push.follow(ball);
			arrows.add(push);
			arrows.add(push2);
		} else if (tutorialPhase == TutorialPhase.PASS) {
			Player p1 = bluePlayers.get(0);
			Player p2 = bluePlayers.get(1);
			
			Arrow push = new Arrow(pushIndicator, p1.getPlayerX(),
					p1.getPlayerY(),
					arrowTipFactory(pushIndicator));
			Arrow push2 = new Arrow(pushIndicator, p2.getPlayerX(),
					p2.getPlayerY(),
					arrowTipFactory(pushIndicator));
			
			push.follow(p1);
			push2.follow(p2);

			arrows.add(push);
			arrows.add(push2);

		} else if (tutorialPhase == TutorialPhase.MARK) {

			Player rp = redPlayers.get(0);
			Player rp1 = redPlayers.get(1);

			Player bp = bluePlayers.get(0);
			Player bp1 = bluePlayers.get(1);
			
			Arrow bluePush1 = new Arrow(pushIndicator, bp.getPlayerX(), bp.getPlayerY(),
					arrowTipFactory(pushIndicator));
			
			Arrow bluePush2 = new Arrow(pushIndicator, bp1.getPlayerX(), bp1.getPlayerY(),
					arrowTipFactory(pushIndicator));
			
			Arrow redPush1 = new Arrow(pushIndicator, rp.getPlayerX(), rp.getPlayerY(),
					arrowTipFactory(pushIndicator));

			Arrow redPush2 = new Arrow(pushIndicator, rp1.getPlayerX(), rp1.getPlayerY(),
					arrowTipFactory(pushIndicator));

			bluePush1.follow(bp);
			bluePush2.follow(bp1);
			redPush1.follow(rp);
			redPush2.follow(rp1);

			arrows.add(bluePush1);
			arrows.add(bluePush2);
			arrows.add(redPush1);
			arrows.add(redPush2);

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
					+ "Your player will run straight towards the ball to collect it, even if it's moving.\n\n"
					+ "Don't forget to hit the play button when you're ready.");
		} else if (tutorialPhase == TutorialPhase.SHOOT) {
			textArea.setText("Now that you have the ball, tap on your player to select them again, then tap on the pitch to shoot. \n\n"
					+ "How else are you going to score? \n\n"
					+ "Shoot the ball into either goal.");
		} else if (tutorialPhase == TutorialPhase.PASS) {
			textArea.setText("Now our second player has turned up with the ball. Lets pass the ball back. \n\n"
					+ "Select the new player then tap on the other to give an order to pass to them. \n\n"
					+ "Tip: If you order the receiver to move, the other player will pass ahead of their path for you. \n\n");
		} else if (tutorialPhase == TutorialPhase.MARK) {
			textArea.setText("Marking opponents: \n\n"
					+ "Select your player, then select a red player to mark them. \n\n"
					+ "They'll follow them until they get the ball. \n\n"
					+ "Mark both of these players and get the ball back. \n\n"
					+ "Tip: If you queue up another action, they will perform it once they have the ball");
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			textArea.setText("You can give multiple actions to any player to plan out better strategies and maneuvers. \n\n"
					+ "You can continue a path from any of your previous actions. \n\n"
					+ "Continuing a path from a marked opponent or the ball will create a straight line to the next point. \n\n"
					+ "Try tackling the red player, then moving away from them and scoring a goal.");
		} else if (tutorialPhase == TutorialPhase.GOALIE) {
			textArea.setText("The goal keeper acts differently than your other players. \n\n"
					+ "It moves automatically, and you can give them instructions only when they have the ball. \n\n"
					+ "If they are holding the ball, they must be ordered to pass or kick it before play can continue. \n\n"
					+ "Goal keepers also have a better chance of stopping a fast moving ball than other players do and cannot be tackled. \n\n"
					+ "Score a goal to complete the tutorial.");
		}
		gameStateToGoIntoWhenBackButtonPressed = gameState;
		gameState = GameState.PAUSED;
	}

	/** Returns a random vector within a given area */
	private Vector2 randomVector2(float x, float y, float width,
			float height) {
		float rx = Utils.randomFloat(null, x, x + width);
		float ry = Utils.randomFloat(null, y, y + height);
		return new Vector2(rx, ry);
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
		remainingMatchTime = roundTime;
		elapsedRoundTime = 0;
		this.gameState = GameState.EXECUTION;
		bar.setPositionToUp();
		arrows.clear();

		// Really should use be using state objects
		if (tutorialPhase == TutorialPhase.MARK) {
			Player p = redPlayers.get(0);
			Player p1 = redPlayers.get(1);

			// Add Move to target
			p.addAction(new Pass(ball, p, p1, p.getPlayerPosition()));

			p.addAction(new MoveToPosition(
					new Vector2(VIRTUAL_SCREEN_WIDTH * 1 / 3,
							VIRTUAL_SCREEN_HEIGHT * 1 / 6), p));
			p1.addAction(new Move(new Vector2[] {
					p1.getPlayerPosition(),
					randomVector2(40, 100, Game.VIRTUAL_SCREEN_WIDTH - 2 * 40,
							Game.VIRTUAL_SCREEN_HEIGHT - 2 * 100) }));
		} else if (tutorialPhase == TutorialPhase.QUEUEING) {
			// Order red players to move through 3 random points close to itself

			Player p = redPlayers.get(0);
			Player p1 = redPlayers.get(1);

			Vector2 randomMidfield1;
			Vector2 randomMidfield2;
			Vector2 randomMidfield3;

			randomMidfield1 = randomVector2(p.getPlayerX() - 100,
					p.getPlayerY() - 100, 200, 200);

			randomMidfield2 = randomVector2(p.getPlayerX() - 100,
					p.getPlayerY() - 100, 200, 200);

			randomMidfield3 = randomVector2(p.getPlayerX() - 100,
					p.getPlayerY() - 100, 200, 200);

			restrictToField(randomMidfield1);
			restrictToField(randomMidfield2);
			restrictToField(randomMidfield3);

			p.addAction(new Move(new Vector2[] { randomMidfield1,
					randomMidfield2, randomMidfield3 }));

			randomMidfield1 = randomVector2(p1.getPlayerX() - 100,
					p1.getPlayerY() - 100, 200, 200);

			randomMidfield2 = randomVector2(p1.getPlayerX() - 100,
					p1.getPlayerY() - 100, 200, 200);

			randomMidfield3 = randomVector2(p1.getPlayerX() - 100,
					p1.getPlayerY() - 100, 200, 200);

			restrictToField(randomMidfield1);
			restrictToField(randomMidfield2);
			restrictToField(randomMidfield3);

			p1.addAction(new Move(new Vector2[] { randomMidfield1,
					randomMidfield2, randomMidfield3 }));

		}

	}

	protected void restrictToField(Vector2 vector) {
		vector.x = (vector.x > Game.VIRTUAL_SCREEN_WIDTH) ? Game.VIRTUAL_SCREEN_WIDTH
				: vector.x;
		vector.x = (vector.x < 0) ? Game.VIRTUAL_SCREEN_WIDTH : vector.x;
		vector.y = (vector.y > Game.VIRTUAL_SCREEN_HEIGHT) ? Game.VIRTUAL_SCREEN_HEIGHT
				: vector.y;
		vector.y = (vector.y < 0) ? Game.VIRTUAL_SCREEN_HEIGHT : vector.y;
	}

	@Override
	protected void update() {

		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);
		selectTextureStateTime += time;
		ghostStateTime = (ghostStateTime + time) % roundTime;

		for (Arrow arrow : arrows) {
			arrow.update();
		}

		if (gameState == GameState.SETUP) {
			remainingSetupTime -= time;
			giveBackBall();
			setupPlayerPositioning();
			if (remainingSetupTime <= 0) {
				beginInputStage();
			}
		}

		if (gameState == GameState.EXECUTION || gameState == GameState.SETUP) {

			for (Player player : getAllPlayers()) {
				player.update(time);
				player.restrictToArea(0, 0, Game.VIRTUAL_SCREEN_WIDTH,
						Game.VIRTUAL_SCREEN_HEIGHT);
			}

			if (ball != null) {
				ball.update(time);
				ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
						VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
				tackleDetection(time);
				goalScoredDetection();
			}
		}

		if (gameState == GameState.EXECUTION) {
			elapsedRoundTime += time;
			remainingMatchTime -= time;

			if (elapsedRoundTime >= roundTime) {
				checkPhaseCompletion();
			}
		}
	}

	private void giveBackBall() {
		Player goalie = redGoalie;
		Player ballOwner = ball != null ? ball.getOwner() : null;
		if (goalie != null && goalie == ball.getOwner()) {
			goalie.addAction(new Pass(ball, goalie, bluePlayers.get(0), goalie
					.getPlayerPosition()));
		}
	}

	@Override
	public void menuButtonPressed() {
		actionResolver.openGuideBook(guideBookPage);
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
