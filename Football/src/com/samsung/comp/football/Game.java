package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.BallOwnerSetListener;
import com.samsung.comp.events.MovementCompletedListener;
import com.samsung.comp.events.OpponentEntersProximityListener;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.data.PlayerDataSource;

public class Game extends AbstractGame {
	
	public Game(PlayerDataSource playerDatabase, ActionResolver actionResolver, float matchTime,
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

		team1 = TeamColour.BLUE;
		team2 = TeamColour.RED;

		createNewPlayersAndBall();
		
		createTeams();
		loadPlayersFromDB();

		remainingMatchTime = (remainingMatchTime <= 0) ? 3 * 60
				: remainingMatchTime;

		// Set reward multipliers
		gameLengthScoreMultiplier = (float) (1 + 1.5 * ((remainingMatchTime - 60) / 60));
		teamDifficultyScoreMultiplier = calculateTeamDifficultyMultiplier();
		Gdx.app.log("GameOver", "Game Length Multiplier: x"
				+ gameLengthScoreMultiplier);

		baseReward = 20000;

		controlsActive = true;

		textArea = new AISelectionScreen(this, playerDatabase,
				new AISelectionListener() {

					@Override
					public void onStartButtonPressed(AISelectionScreen screen) {
						teamB = screen.getSelectedAITeam();
						Gdx.app.log("Game", "Loading AI players. Team ID = "
								+ teamB.getTeamID());
						loadAiPlayersFromDB(teamB.getTeamID());

						coinFlipForStart();
						createAI();

						soundManager.play(whistleBlow);
						playerDatabase.close();
						beginInputStage();
					}
				});
		gameState = GameState.FINISHED;
	}

	private float calculateTeamDifficultyMultiplier() {
		return teamB.getDifficulty();
	}

	@Override
	protected void onGoalScored(TeamColour goalAreaColour) {
		setStartingPositions(goalAreaColour);

		beginInputStage();
		goalScoredDrawTime = 3f;
		soundManager.play(crowdCheer);

		whistleBlow.play();
	}

	@Override
	protected List<String> getFinishData() {
		List<String> finishData = new ArrayList<String>();
		String textStr;

		finishData.add("Single Player Game: " + baseReward);
		
		textStr = "Score Bonus: +";
		textStr += calculateScoreBonus();
		finishData.add(textStr);

		// Remove redundant decimals

		textStr = "Game Length Multiplier: x";
		textStr += (gameLengthScoreMultiplier % 1 == 0) ? String
				.valueOf(Integer.valueOf((int) gameLengthScoreMultiplier))
				: gameLengthScoreMultiplier;
		finishData.add(textStr);

		textStr = "AI Team Difficulty Multiplier: x";
		textStr += (teamDifficultyScoreMultiplier % 1 == 0) ? String
				.valueOf(Integer.valueOf((int) teamDifficultyScoreMultiplier))
				: teamDifficultyScoreMultiplier;
		finishData.add(textStr);
		
		textStr = "Total Reward: ";
		textStr += calculateRewardFunds();
		finishData.add(textStr);
		
		return finishData;
	}

	@Override
	protected int calculateRewardFunds() {
		int reward = baseReward;

		reward += calculateScoreBonus();

		reward *= gameLengthScoreMultiplier;
		reward *= teamDifficultyScoreMultiplier;
		reward *= aiDifficultyScoreMultiplier;

		return reward;
	}

	private double calculateScoreBonus() {
		int humanScore = getScore(team1);
		int AIScore = getScore(team2);
		if (humanScore > AIScore) {
			return 30000;
		} else if (humanScore == AIScore) {
			return 7500;
		}
		return 0;
	}

	protected void setStartingPositions(TeamColour centerTeam) {

		// Non centred positions
		Vector2 redStriker = new Vector2(Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(334));
		Vector2 redMidA = new Vector2(Player.translatePlayerCoordinate(507),
				Player.translatePlayerCoordinate(320));
		Vector2 redMidB = new Vector2(Player.translatePlayerCoordinate(169),
				Player.translatePlayerCoordinate(320));
		Vector2 redDef = new Vector2(Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(256));
		Vector2 redGoalKeeper = new Vector2(
				Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(174));

		Vector2 blueStriker = new Vector2(
				Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(640));
		Vector2 blueMidA = new Vector2(Player.translatePlayerCoordinate(169),
				Player.translatePlayerCoordinate(704));
		Vector2 blueMidB = new Vector2(Player.translatePlayerCoordinate(507),
				Player.translatePlayerCoordinate(704));
		Vector2 blueDef = new Vector2(Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(768));
		Vector2 blueGoalKeeper = new Vector2(
				Player.translatePlayerCoordinate(338),
				Player.translatePlayerCoordinate(850));

		if (centerTeam == TeamColour.BLUE) {
			blueDef = blueMidA.cpy();

			blueStriker.x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) - (Player
							.getPlayerSize())));
			blueStriker.y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			blueMidA.x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) + (Player
							.getPlayerSize())));
			blueMidA.y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			bluePlayers.get(0).setRotation(0);
			bluePlayers.get(1).setRotation(180);
		}

		if (centerTeam == TeamColour.RED) {
			redDef = redMidA.cpy();

			redStriker.x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) - (Player
							.getPlayerSize())));
			redStriker.y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			redMidA.x = Player
					.translatePlayerCoordinate((float) ((VIRTUAL_SCREEN_WIDTH / 2) + (Player
							.getPlayerSize())));
			redMidA.y = Player
					.translatePlayerCoordinate(VIRTUAL_SCREEN_HEIGHT / 2);

			redPlayers.get(0).setRotation(0);
			redPlayers.get(1).setRotation(180);
		}

		bluePlayers.get(0).setPosition(blueStriker);
		bluePlayers.get(1).setPosition(blueMidA);
		bluePlayers.get(2).setPosition(blueMidB);
		bluePlayers.get(3).setPosition(blueDef);
		blueGoalie.setPosition(blueGoalKeeper);

		redPlayers.get(0).setPosition(redStriker);
		redPlayers.get(1).setPosition(redMidA);
		redPlayers.get(2).setPosition(redMidB);
		redPlayers.get(3).setPosition(redDef);
		redGoalie.setPosition(redGoalKeeper);

		ball.x = Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2);
		ball.y = Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2);

		ball.resetBall();
	}

	private void createNewPlayersAndBall() {
		// Create a ball
		ball = new Ball(Ball.translateBallCoordinate(PLAYING_AREA_WIDTH / 2),
				Ball.translateBallCoordinate(PLAYING_AREA_HEIGHT / 2));
	}

	private void loadPlayersFromDB() {

		Squad userSquad = playerDatabase.getSquadsTableManager().getSquad(
				userTeamID);
		List<Player> userPlayers = userSquad.getAllPlayers();

		bluePlayers = new ArrayList<Player>();
		for (int i = 0; i < 5; i++) {
			Player p = userPlayers.get(i);
			if (i == 4) {
				blueGoalie = new Goalie(p.getID(), p.getName(),
						p.getShootSpeed(), p.getRunSpeed(), p.getTackleSkill(),
						p.getSavingSkill(), p.getTeamID(), p.getPlayerCost());
				blueGoalie.initialize(this, TeamColour.BLUE);
			} else {
				bluePlayers.add(p);
				p.initialize(TeamColour.BLUE);
			}
		}
	}

	private void loadAiPlayersFromDB(int aiTeamID) {

		List<Player> aiPlayers = playerDatabase.getPlayersTableManager()
				.getPlayers(aiTeamID);

		redPlayers = new ArrayList<Player>();
		for (int i = 0; i < 5; i++) {
			Player p = aiPlayers.get(i);
			if (i == 4) {
				redGoalie = new Goalie(p.getID(), p.getName(),
						p.getShootSpeed(), p.getRunSpeed(), p.getTackleSkill(),
						p.getSavingSkill(), p.getTeamID(), p.getPlayerCost());
				redGoalie.initialize(this, TeamColour.RED);
			} else {
				redPlayers.add(p);
				p.initialize(TeamColour.RED);
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

	private void createTeams() {
		Gdx.app.log("GameDB", "Assigning teams...");

		teamA = playerDatabase.getTeamsTableManager().getTeam(userTeamID);
		Gdx.app.log("GameDB", "Suceeded assigning Team A");

		teamA.setTeamName(playerDatabase.getTeamsTableManager()
				.getTeam(userTeamID).getTeamName());
		playerDatabase.getTeamsTableManager().alterTeam(teamA);

		teamB = playerDatabase.getTeamsTableManager().getTeam(2);
		Gdx.app.log("GameDB", "Suceeded assigning Team B");

	}

	private void createMovementCompletedListeners(TeamColour teamColour) {
		for (Player player : getPlayers(teamColour)) {
			player.addMovementCompletedListener(new MovementCompletedListener() {

				@Override
				public void onMovementCompleted(Player player, Action nextAction) {
					if (gameState == GameState.EXECUTION) {
						ai.getActions(player);
					}
				}
			});
		}
	}

	private void createOpponentEntersProximityListeners(TeamColour teamColour) {
		for (Player aiPlayer : getPlayers(teamColour)) {
			aiPlayer.addOpponentEntersProximityListener(new OpponentEntersProximityListener() {

				@Override
				public void onOpponentEntersProximity(Player player,
						Player opponent) {
					if (gameState == GameState.EXECUTION
							&& player == ball.getOwner()) {
						Gdx.app.log("Game",
								"Opponent in proximity. Getting new actions.");
						player.reset();
						ai.getActions(player);
					}
				}
			});
		}
	}

	private void createAI() {
		ai = new AI(this, team2);
		createMovementCompletedListeners(team2);
		createOpponentEntersProximityListeners(team2);

		ball.addBallOwnerSetListener(new BallOwnerSetListener() {

			@Override
			public void onBallOwnerSet(Ball ball, Player newOwner) {
				if (gameState == GameState.EXECUTION) {
					ai.getComputerActions();
				}
			}
		});
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
				player.checkForNewOpponentsInProximity(100,
						getAllPlayers(team1));
				// player.checkForNewOpponentsInProximity(100,
				// getPlayers(team1));
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
