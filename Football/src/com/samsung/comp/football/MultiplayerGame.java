package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.data.PlayerDataSource;

public class MultiplayerGame extends AbstractGame {

	Color redColor = Color.RED;
	Color blueColor = new Color(0.2f, 0.6f, 1f, 1f);
	
	public MultiplayerGame(PlayerDataSource playerDatabase, ActionResolver actionResolver, float matchTime,
			float roundTime, boolean statusBarAtTop, byte scoreLimit) {
		this.playerDatabase = playerDatabase;
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
		createGoals();

		createNewPlayersAndBall();

		playerDatabase.close();

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		remainingMatchTime = (remainingMatchTime <= 0) ? 3 * 60
				: remainingMatchTime;

		baseReward = 20000;
		gameLengthScoreMultiplier = (float) (1 + 1.5 * ((remainingMatchTime - 60) / 60));
		Gdx.app.log("GameOver", "Game Length Multiplier: x"
				+ gameLengthScoreMultiplier);

		bar.setBarColor(blueColor);

		controlsActive = true;

		currentTeam = TeamColour.BLUE;
		selectedPlayer = bluePlayers.get(0);
		textArea = new TeamSetupScreen(this, actionResolver.openDatasource(),
				false, new TeamSetupListener() {
					@Override
					public void onStartButtonPressed(TeamSetupScreen screen) {
						if (currentTeam == TeamColour.BLUE) {
							switchPlayerAssembly(screen);
						} else {
							completeAssembly(screen);
						}
					}

					@Override
					public void onSelectedPlayerChanged(TeamSetupScreen screen,
							Player player, int index) {
						selectedPlayer = getAllPlayers(currentTeam).get(index);
						selectTextureStateTime = 0f;
					}
				});

		// Finished state draws the text area object
		gameState = GameState.FINISHED;
	}

	private void completeAssembly(TeamSetupScreen screen) {

		selectedPlayer = null;
		cursor.setHighlightedPlayer(null);

		String teamName = screen.getTeamName();
		List<Player> fieldedPlayers = screen.getSelectedFieldedPlayers();

		// Create Team
		teamB = new Team(-1, -1, teamName, -1);

		loadPlayers(fieldedPlayers);
		coinFlipForStart();

		// Set reward multipliers
		gameLengthScoreMultiplier = (float) (1 + 1.5 * ((remainingMatchTime - 60) / 60));
		Gdx.app.log("GameOver", "Game Length Multiplier: x"
				+ gameLengthScoreMultiplier);

		bar.setBarColor(blueColor);
		currentTeam = TeamColour.BLUE;
		beginInputStage();
		playerDatabase.close();
	}

	private void switchPlayerAssembly(TeamSetupScreen screen) {

		selectedPlayer = redPlayers.get(0);
		cursor.setHighlightedPlayer(null);

		String teamName = screen.getTeamName();
		List<Player> fieldedPlayers = screen.getSelectedFieldedPlayers();

		// Create Team
		teamA = new Team(-1, -1, teamName, -1);

		loadPlayers(fieldedPlayers);
		setStartingPositions(TeamColour.BLUE);
		currentTeam = TeamColour.RED;
		bar.setBarColor(redColor);
	}

	private void loadPlayers(List<Player> fieldedPlayers) {
		getPlayers(currentTeam).clear();
		for (int i = 0; i < 5; i++) {
			Player p = fieldedPlayers.get(i);
			Player pCopy = new Player(p.getID(), p.getName(),
					p.getShootSpeed(), p.getRunSpeed(), p.getTackleSkill(),
					p.getSavingSkill(), p.getTeamID(), p.getPlayerCost());
			if (i == 4) {
				setGoalie(
						currentTeam,
						new Goalie(pCopy.getID(), pCopy.getName(), pCopy
								.getShootSpeed(), pCopy.getRunSpeed(), pCopy
								.getTackleSkill(), pCopy.getSavingSkill(),
								pCopy.getTeamID(), pCopy.getPlayerCost()));
				getGoalie(currentTeam).initialize(this, currentTeam);
			} else {
				getPlayers(currentTeam).add(pCopy);
				pCopy.initialize(currentTeam);
			}
		}
	}

	private void coinFlipForStart() {
		if (Utils.randomFloat(rng, 0, 1) > 0.5) {
			setStartingPositions(TeamColour.BLUE);
		} else {
			setStartingPositions(TeamColour.RED);
		}
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

	@Override
	protected List<String> getFinishData() {
		List<String> finishData = new ArrayList<String>();
		String textStr;

		finishData.add("Mutliplayer Game: " + baseReward);

		textStr = "Game Length Multiplier: x";
		textStr += (gameLengthScoreMultiplier % 1 == 0) ? String
				.valueOf(Integer.valueOf((int) gameLengthScoreMultiplier))
				: gameLengthScoreMultiplier;
		finishData.add(textStr);

		textStr = "Total Reward: ";
		textStr += calculateRewardFunds();
		finishData.add(textStr);

		return finishData;
	}

	@Override
	protected int calculateRewardFunds() {
		int reward = baseReward;
		reward *= gameLengthScoreMultiplier;
		return reward;
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
			bluePlayers.get(0).y = Player.translatePlayerCoordinate(640);

			bluePlayers.get(1).x = Player.translatePlayerCoordinate(338);
			bluePlayers.get(1).y = Player.translatePlayerCoordinate(768);
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

		redPlayers = new ArrayList<Player>();
		bluePlayers = new ArrayList<Player>();
		for (int i = 0; i < 4; i++) {
			redPlayers.add(new Player(0, 0, TeamColour.RED));
			redPlayers.get(i).initialize(TeamColour.RED);
			bluePlayers.add(new Player(0, 0, TeamColour.BLUE));
			bluePlayers.get(i).initialize(TeamColour.BLUE);
		}
		redGoalie = new Goalie(0, 0, TeamColour.RED, this, 0);
		redGoalie.initialize(this, TeamColour.RED);
		blueGoalie = new Goalie(0, 0, TeamColour.BLUE, this, 0);
		blueGoalie.initialize(this, TeamColour.BLUE);
		
		setStartingPositions(TeamColour.RED);

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

		textArea.update(time);
		bar.update(time);
		selectTextureStateTime += time;
		ghostStateTime = (ghostStateTime + time) % roundTime;

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
				currentTeam = TeamColour.BLUE;
				bar.setBarColor(blueColor);
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
				beginSetupPhase(1.5f);
			}

			if (remainingMatchTime < 0) {
				matchFinish();
			}
		}

	}

}
