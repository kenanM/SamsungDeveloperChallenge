package com.samsung.comp.football;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.BlueGoalie;
import com.samsung.comp.football.Players.BluePlayer;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.Players.RedGoalie;
import com.samsung.comp.football.Players.RedPlayer;

public class MultiplayerGame extends AbstractGame {

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

		remainingMatchTime = 3 * 60;

		beginInputStage();
	}

	@Override
	protected void setStartingPositions(TeamColour centerTeam) {

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

		soundManager.play(whistleBlow);
	}

	@Override
	public void beginExecution() {
		if (team1Turn) {
			team1Turn = false;
		} else {
			Gdx.app.log("Game", "Beginning execution");
			elapsedRoundTime = 0;
			this.gameState = GameState.EXECUTION;
			bar.setPositionToUp();
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

			ball.update(time);
			ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
					VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
			tackleDetection(time);
			goalScoredDetection();

			if (elapsedRoundTime >= ROUND_TIME) {
				gameState = GameState.INPUT;
				team1Turn = true;
				beginInputStage();
			}

			if (remainingMatchTime < 0) {
				matchFinish();
			}
		}

	}

}
