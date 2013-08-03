package com.samsung.comp.football;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.samsung.comp.events.BallOwnerSetListener;
import com.samsung.comp.events.MovementCompletedListener;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.data.PlayerDataSource;

public class Game extends AbstractGame {

	public Game(Context context, ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
		this.context = context;
	}

	public Game(ActionResolver actionResolver, float matchTime,
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
		randomiseStats(getAllPlayers());

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		ai = new AI(this);
		createMovementCompletedListeners(team2);

		ball.addBallOwnerSetListener(new BallOwnerSetListener() {

			@Override
			public void onBallOwnerSet(Ball ball, Player newOwner) {
				if (gameState == GameState.EXECUTION) {
					ai.getComputerActions();
				}
			}
		});

		remainingMatchTime = (remainingMatchTime <= 0) ? 3 * 60
				: remainingMatchTime;

		controlsActive = true;

		beginInputStage();
	}

	@Override
	protected void onGoalScored(TeamColour goalAreaColour) {
		setStartingPositions(goalAreaColour);

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
		Log.v("db", "opening database at line 162");
		PlayerDataSource database = new PlayerDataSource(context);
		Log.v("db", "created database at line 164");

		redPlayers = database.getTeam(0);
		Log.v("db", "got team at line 167");

		redPlayers.get(0).setPosition(338, 256);
		redPlayers.get(0).setPosition(338, 384);
		redPlayers.get(0).setPosition(169, 320);
		redPlayers.get(0).setPosition(507, 320);
		for (Player player: redPlayers){
			player.setTeam(TeamColour.RED);
		}
		redGoalie = database.getGoalie(0);
		Log.v("db", "got goalie at line 177");

		redGoalie.setPosition(338, 124);
		redGoalie.setTeam(TeamColour.RED);
		

		bluePlayers = database.getTeam(0);
		redPlayers.get(0).setPosition(338, 768);
		redPlayers.get(0).setPosition(338, 640);
		redPlayers.get(0).setPosition(169, 704);
		redPlayers.get(0).setPosition(507, 704);
		for (Player player: redPlayers){
			player.setTeam(TeamColour.BLUE);
		}
		
		blueGoalie = database.getGoalie(0);
		blueGoalie.setPosition(338, 900);
		blueGoalie.setTeam(TeamColour.RED);

		database.close();
		
		if (Utils.randomFloat(rng, 0, 1) > 0.5) {
			setStartingPositions(TeamColour.BLUE);
		} else {
			setStartingPositions(TeamColour.RED);
		}

		soundManager.play(whistleBlow);
	}

	private void createMovementCompletedListeners(TeamColour teamColour) {
		for (Player player : getPlayers(teamColour)) {
			player.addMovementCompletedListener(new MovementCompletedListener() {

				@Override
				public void onMovementCompleted(Player player, Action nextAction) {
					ai.getActions(player);
				}
			});
		}
	}

	@Override
	public void beginExecution() {
		super.beginExecution();
		ai.getComputerActions();
	};

	@Override
	protected void update() {
		float time = Gdx.graphics.getDeltaTime();
		goalScoredDrawTime = Math.max(0, goalScoredDrawTime - time);

		bar.update(time);
		selectTextureStateTime += time;
		ghostStateTime = (ghostStateTime + time) % 5;

		if (gameState == GameState.SETUP) {
			remainingSetupTime -= time;
			setupPlayerPositioning();
			if (ball.hasOwner()) {
				ball.update(time);
				ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
						VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
			}

			for (Player player : getAllPlayers()) {
				if (player == blueGoalie || player == redGoalie) {
					continue;
				} else {
					player.update(time);
					player.restrictToArea(0, 0, Game.VIRTUAL_SCREEN_WIDTH,
							Game.VIRTUAL_SCREEN_HEIGHT);
				}
			}

			if (remainingSetupTime <= 0) {
				beginInputStage();
			}
		}

		if (gameState == GameState.EXECUTION) {
			elapsedRoundTime += time;
			remainingMatchTime -= time;

			for (Player player : getAllPlayers()) {
				player.update(time);
				player.restrictToArea(0, 0, Game.VIRTUAL_SCREEN_WIDTH,
						Game.VIRTUAL_SCREEN_HEIGHT);
			}

			ball.update(time);
			ball.ballBounceDetection(VIRTUAL_SCREEN_WIDTH,
					VIRTUAL_SCREEN_HEIGHT, BOUNCE_ELASTICITY);
			goalScoredDetection();
			tackleDetection(time);

			if (elapsedRoundTime >= roundTime) {
				beginSetupPhase(0.7f);
			}

			if (remainingMatchTime < 0) {
				matchFinish();
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

}
