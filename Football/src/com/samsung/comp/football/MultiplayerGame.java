package com.samsung.comp.football;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class MultiplayerGame extends AbstractGame {

	Color redColor = Color.RED;
	Color blueColor = new Color(0.2f, 0.6f, 1f, 1f);

	public MultiplayerGame(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	public MultiplayerGame(ActionResolver actionResolver, float matchTime,
			float roundTime, boolean statusBarAtTop, byte scoreLimit) {
		this.actionResolver = actionResolver;
		this.remainingMatchTime = matchTime;
		this.roundTime = roundTime;
		this.positionUIBarAtTop = statusBarAtTop;
		this.scoreLimit = scoreLimit;
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

		createNewPlayersAndBall();

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		remainingMatchTime = (remainingMatchTime <= 0) ? 3 * 60
				: remainingMatchTime;

		bar.setBarColor(blueColor);

		beginInputStage();
	}

	@Override
	protected void onGoalScored(TeamColour goalAreaColour) {
		setStartingPositions(goalAreaColour);

		currentTeam = TeamColour.BLUE;
		bar.setBarColor(blueColor);

		beginInputStage();
		goalScoredDrawTime = 3f;
		soundManager.play(crowdCheer);

		whistleBlow.play();

	}

	protected void setStartingPositions(TeamColour centerTeam) {

		redPlayers.get(2).x = Player.translatePlayerCoordinate(169);
		redPlayers.get(2).y = Player.translatePlayerCoordinate(320);

		redPlayers.get(3).x = Player.translatePlayerCoordinate(507);
		redPlayers.get(3).y = Player.translatePlayerCoordinate(320);

		redGoalie.x = Player.translatePlayerCoordinate(338);
		redGoalie.y = Player.translatePlayerCoordinate(174);

		bluePlayers.get(2).x = Player.translatePlayerCoordinate(169);
		bluePlayers.get(2).y = Player.translatePlayerCoordinate(704);

		bluePlayers.get(3).x = Player.translatePlayerCoordinate(507);
		bluePlayers.get(3).y = Player.translatePlayerCoordinate(704);

		blueGoalie.x = Player.translatePlayerCoordinate(338);
		blueGoalie.y = Player.translatePlayerCoordinate(850);

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
			bluePlayers.get(0).y = Player.translatePlayerCoordinate(768);

			bluePlayers.get(1).x = Player.translatePlayerCoordinate(338);
			bluePlayers.get(1).y = Player.translatePlayerCoordinate(640);
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
			redPlayers.get(0).y = Player.translatePlayerCoordinate(334);

			redPlayers.get(1).x = Player.translatePlayerCoordinate(338);
			redPlayers.get(1).y = Player.translatePlayerCoordinate(256);
		}

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();
	}

	private void createNewPlayersAndBall() {

		// Create a ball
		ball = new Ball(Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2),
				Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2));

		// create the players
		redPlayers = new LinkedList<Player>();

		redPlayers.add(new Player(338, 256, TeamColour.RED, 520, 150, 100, 20,
				380));
		redPlayers.add(new Player(338, 384, TeamColour.RED, 540, 200, 80, 20,
				380));
		redPlayers.add(new Player(169, 320, TeamColour.RED, 550, 100, 100, 40,
				420));
		redPlayers.add(new Player(507, 320, TeamColour.RED, 530, 150, 80, 40,
				420));
		redGoalie = new Goalie(338, 124, TeamColour.RED, this, 500);

		bluePlayers = new LinkedList<Player>();

		bluePlayers.add(new Player(338, 768, TeamColour.BLUE, 520, 150, 100,
				20, 380));
		bluePlayers.add(new Player(338, 640, TeamColour.BLUE, 540, 200, 80, 20,
				380));
		bluePlayers.add(new Player(507, 704, TeamColour.BLUE, 550, 100, 100,
				40, 420));
		bluePlayers.add(new Player(169, 704, TeamColour.BLUE, 530, 150, 80, 40,
				420));
		blueGoalie = new Goalie(338, 900, TeamColour.BLUE, this, 500);

		if (Utils.randomFloat(rng, 0, 1) > 0.5) {
			setStartingPositions(TeamColour.BLUE);
		} else {
			setStartingPositions(TeamColour.RED);
		}

		soundManager.play(whistleBlow);
	}

	@Override
	public void beginExecution() {
		if (currentTeam == TeamColour.BLUE) {
			currentTeam = TeamColour.RED;
			bar.setBarColor(redColor);
		} else {
			Gdx.app.log("Game", "Beginning execution");
			elapsedRoundTime = 0;
			this.gameState = GameState.EXECUTION;
			bar.setPositionToUp();
			bar.setBarColor(null);
		}
		selectedPlayer = null;
		cursor.setHighlightedPlayer(null);
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
	protected void update() {
		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);
		selectTextureStateTime += time;
		ghostStateTime = (ghostStateTime + time) % 5;

		if (gameState == GameState.SETUP) {
			remainingSetupTime -= time;
			if (remainingSetupTime <= 0) {
				currentTeam = TeamColour.BLUE;
				bar.setBarColor(blueColor);
				beginInputStage();
			}
		}

		if (gameState == GameState.EXECUTION || gameState == GameState.SETUP) {
			for (Player player : getAllPlayers()) {
				player.update(time);
			}
		}

		if (gameState == GameState.EXECUTION) {
			elapsedRoundTime += time;
			remainingMatchTime -= time;

			ball.update(time);
			ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
					VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
			tackleDetection(time);
			goalScoredDetection();

			if (elapsedRoundTime >= roundTime) {
				beginSetupPhase(0.7f);
			}

			if (remainingMatchTime < 0) {
				matchFinish();
			}
		}

	}

}
