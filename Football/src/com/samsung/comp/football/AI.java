package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.BallOwnerSetListener;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

public class AI {

	private static final String TAG = "AI";
	// Denotes different regions by splitting the pitch into thirds
	static final double BLUE_GOAL_AREA_TOP = Game.VIRTUAL_SCREEN_HEIGHT * 0.66;
	static final double BLUE_GOAL_AREA_BOTTOM = Game.VIRTUAL_SCREEN_HEIGHT - 100;
	static final double RED_GOAL_AREA_TOP = 100;
	static final double RED_GOAL_AREA_BOTTOM = Game.VIRTUAL_SCREEN_HEIGHT * 0.33;

	// Used to reduce the chance of passing straight back
	private Player receiver = null;

	// The distance from which the AI will attempt a shot
	private static final float SHOOTING_RANGE = 250;

	private AbstractGame game;
	private TeamColour teamColour;
	private Player goalie;
	private Ball ball;

	private Vector2 targetGoal;
	private Vector2 homeGoal;

	private List<Player> players;
	private List<Player> opponents;
	TeamColour opponentColour;

	public AI(AbstractGame game, TeamColour teamColour) {
		this.game = game;
		this.teamColour = teamColour;
		this.goalie = game.getGoalie(teamColour);
		this.ball = game.getBall();
		if (teamColour == TeamColour.RED) {
			targetGoal = Game.BLUE_GOAL;
			homeGoal = Game.RED_GOAL;
			opponentColour = TeamColour.BLUE;
		} else {
			targetGoal = Game.RED_GOAL;
			homeGoal = Game.BLUE_GOAL;
			opponentColour = TeamColour.RED;
		}
		players = new ArrayList<Player>(game.getPlayers(teamColour));
		opponents = new ArrayList<Player>(game.getPlayers(opponentColour));

		ball.addBallOwnerSetListener(new BallOwnerSetListener() {

			@Override
			public void onBallOwnerSet(Ball ball, Player newOwner) {
				// Null the receiver when they lose the ball or if it gets
				// intercepted
				if (newOwner != receiver) {
					receiver = null;
				}

			}
		});
	}

	public void getComputerActions() {
		Log.v(TAG, "getting computer actions");

		getActionsV2();
	}

	private void getActionsV2() {

		for (Player player : players) {
			player.reset();
		}
		List<Player> playersWithoutActions = new ArrayList<Player>(players);
		sortPlayersByDistanceFromHomeGoal(playersWithoutActions);

		boolean controlBall = computerControlsBall();
		boolean opponentControlsBall = opponentControlsBall();
		boolean goalieHasBall = goalie.hasBall();
		boolean ballInOpponentHalf = isBallInOpponentHalf();
		boolean ballInDefensiveArea = getDefensiveArea().contains(
				ball.getBallX(), ball.getBallY());
		boolean ballInMidFieldArea = getMidFieldArea().contains(
				ball.getBallX(), ball.getBallY());
		boolean ballInOffensiveArea = getOffensiveArea().contains(
				ball.getBallX(), ball.getBallY());

		if (goalieHasBall) {
			Player nearestToTheGoalie = playerNearestTheGoalie(players);
			passToPlayer(goalie, nearestToTheGoalie);

			for (Player player : players) {
				moveToMidFieldPosition(player);
			}
			return;
		}

		if (controlBall) {
			Player ballOwner = ball.getOwner();
			if (withinShootingRange(ballOwner)) {
				shoot(ballOwner);
			} else {

				Player target = (playersWithoutActions
						.get(playersWithoutActions.size() - 1) != ballOwner) ? playersWithoutActions
						.get(playersWithoutActions.size() - 1)
						: playersWithoutActions.get(playersWithoutActions
								.size() - 2);

				float passChance = calculatePassChance(ballOwner, target,
						ballInDefensiveArea, ballInOffensiveArea);
				float rn = Utils.randomFloat(null, 0, 100);

				if (passChance > rn) {
					// get the farthest forward player who isn't the ballOwner

					moveForward(target);
					passToPlayer(ballOwner, target);
					moveForward(ballOwner);
					playersWithoutActions.remove(target);
					playersWithoutActions.remove(ballOwner);

				} else {
					moveForward(ballOwner);
					playersWithoutActions.remove(ballOwner);
				}
			}

			// Give remaining players actions
			for (Player player : playersWithoutActions) {
				if (Utils.randomFloat(null, 0, 1) > 0.5) {
					moveToMidFieldPosition(player);
				} else {
					moveToOffensivePosition(player);
				}
			}
		} else {
			// We don't have the ball
			if (ballInDefensiveArea || ballInMidFieldArea) {
				// Play defence:
				// Player nearest ball marks owner or follows ball
				// Mark any remaining opponents in defensive area
				// Move remaining players back to defence or mid

				// Follow ball owner or ball
				Player playerNearestBall = playerNearestVector(
						playersWithoutActions, ball.getBallPosition());

				if (opponentControlsBall) {
					followPlayer(playerNearestBall, ball.getOwner());
					playersWithoutActions.remove(playerNearestBall);

				} else {
					followBall(playerNearestBall);
					playersWithoutActions.remove(playerNearestBall);
				}

				List<Player> opponentsCloseToHomeGoal = new ArrayList<Player>();

				for (Player opponent : opponents) {
					if (getDefensiveArea().contains(opponent.getPlayerX(),
							opponent.getPlayerY())) {
						opponentsCloseToHomeGoal.add(opponent);
					}
				}

				for (Player opponent : opponentsCloseToHomeGoal) {
					if (playersWithoutActions.size() == 0) {
						break;
					}
					if (opponent == ball.getOwner()) {
						// ball owner may already be marked
						continue;
					}
					Player closestPlayer = playerNearestVector(
							playersWithoutActions, opponent.getPlayerPosition());
					followPlayer(closestPlayer, opponent);
					playersWithoutActions.remove(closestPlayer);
				}

				// Give remaining players actions
				for (Player player : playersWithoutActions) {
					float defencePositionChance = 66;
					float rn = Utils.randomFloat(null, 0, 100);
					if (defencePositionChance > rn) {
						moveTodDefensivePosition(player);
					} else {
						moveToMidFieldPosition(player);
					}
				}
			} else {

				if (opponentControlsBall) {
					// Follow ball owner
					// Mark opponents in mid or in our defence
					// Move remaining players back

					// Follow ball owner
					Player playerNearestBall = playerNearestVector(
							playersWithoutActions, ball.getBallPosition());
					followPlayer(playerNearestBall, ball.getOwner());
					playersWithoutActions.remove(playerNearestBall);

					// Create list of opponents in mid or our defence
					List<Player> opponentsInMidFieldOrHomeGoal = new ArrayList<Player>();
					for (Player opponent : opponents) {
						if (getDefensiveArea().contains(opponent.getPlayerX(),
								opponent.getPlayerY())
								|| getMidFieldArea().contains(
										opponent.getPlayerX(),
										opponent.getPlayerY())) {
							opponentsInMidFieldOrHomeGoal.add(opponent);
						}
					}

					// Create list of our players in mid or defence
					List<Player> playersInMidFieldOrDefence = new ArrayList<Player>();
					for (Player player : playersWithoutActions) {
						if (getDefensiveArea().contains(player.getPlayerX(),
								player.getPlayerY())
								|| getMidFieldArea().contains(
										player.getPlayerX(),
										player.getPlayerY())) {
							playersInMidFieldOrDefence.add(player);
						}
					}

					// Mark closest opponents in mid or our defence
					sortPlayersByDistanceFromHomeGoal(opponentsInMidFieldOrHomeGoal);
					for (Player player : playersInMidFieldOrDefence) {
						if (opponentsInMidFieldOrHomeGoal.size() == 0) {
							// no players left to mark
							break;
						}
						Player closestOpponent = playerNearestVector(
								opponentsInMidFieldOrHomeGoal,
								player.getPlayerPosition());
						followPlayer(player, closestOpponent);
						opponentsInMidFieldOrHomeGoal.remove(closestOpponent);
						playersWithoutActions.remove(player);
					}

					for (Player player : playersWithoutActions) {
						// Move backward
						moveBackward(player);
					}

				} else {
					// Collect ball and go offensive:
					// Nearest player follows ball
					// Move players in offense move away from any other players
					// Move remaining players forward

					// Follow ball
					Player playerNearestBall = playerNearestVector(
							playersWithoutActions, ball.getBallPosition());
					followBall(playerNearestBall);
					playersWithoutActions.remove(playerNearestBall);

					List<Player> playersInOffensiveArea = new ArrayList<Player>(
							playersWithoutActions);

					// Move players in offense move away from any other players
					for (Player player : playersInOffensiveArea) {
						if (countPlayersAround(player, opponentColour, 150) > 0) {
							moveToOffensivePosition(player);
							playersWithoutActions.remove(player);
						}
					}

					// Move remaining players forward
					for (Player player : playersWithoutActions) {
						moveForward(player);
					}

				}
			}
		}
	}

	/**
	 * Adds AI actions to a single player
	 * 
	 * @param player
	 *            The player to add actions to
	 */
	public void getActions(Player player) {

		boolean controlBall = computerControlsBall();
		boolean opponentControlsBall = opponentControlsBall();
		boolean playerControlsBall = ball.getOwner() == player;
		boolean goalieHasBall = goalie.hasBall();
		boolean ballInOpponentHalf = isBallInOpponentHalf();
		boolean ballInDefensiveArea = getDefensiveArea().contains(
				ball.getBallX(), ball.getBallY());
		boolean ballInMidFieldArea = getMidFieldArea().contains(
				ball.getBallX(), ball.getBallY());
		boolean ballInOffensiveArea = getOffensiveArea().contains(
				ball.getBallX(), ball.getBallY());

		if (playerControlsBall) {

			if (withinShootingRange(player)) {
				shoot(player);
			} else {
				// If not within shooting range either pass or move

				// get the farthest forward player who isn't the ballOwner
				List<Player> sortedPlayerList = new ArrayList<Player>(players);
				sortPlayersByDistanceFromHomeGoal(sortedPlayerList);
				Player reciever = sortedPlayerList
						.get(sortedPlayerList.size() - 1);
				float passChance = calculatePassChance(player, reciever,
						ballInDefensiveArea, ballInOffensiveArea);
				float rn = Utils.randomFloat(null, 0, 100);

				if (passChance > rn) {

					reciever = (player == reciever) ? sortedPlayerList
							.get(sortedPlayerList.size() - 2) : reciever;
					passToPlayer(player, reciever);

				} else {
					moveForward(player);
				}
			}

		} else if (computerControlsBall()) {
			// Move forward
			moveForward(player);
		} else if (opponentControlsBall) {
			moveBackward(player);
		} else {
			// No one has ball
			if (receiver == player) {
				// If player is receiving a pass then collect the ball.
				Gdx.app.log(TAG, "Recipient collecting ball");
				player.addAction(new MarkBall(player.getPlayerPosition(), ball));
			} else {
				moveToMidFieldPosition(player);
			}

		}

	}

	/**
	 * Moves a player from defence into midfield, or into offence.
	 * 
	 * @param player
	 *            The player to move
	 */
	protected void moveForward(Player player) {
		if (getDefensiveArea().contains(player.getPlayerX(),
				player.getPlayerY())) {
			moveToMidFieldPosition(player);
		} else {
			moveToOffensivePosition(player);
		}
	}

	/**
	 * Moves a player from offence into midfield, or into defence.
	 * 
	 * @param player
	 *            The player to move
	 */
	protected void moveBackward(Player player) {
		if (getOffensiveArea().contains(player.getPlayerX(),
				player.getPlayerY())) {
			moveToMidFieldPosition(player);
		} else {
			moveTodDefensivePosition(player);
		}
	}

	protected float calculatePassChance(Player player, Player target,
			boolean ballInDefensiveArea, boolean ballInOffensiveArea) {
		float passChance = 35;
		if (countPlayersInfront(player, opponentColour, 100) > 0) {
			passChance += 35;
		}
		if (ballInDefensiveArea) {
			passChance += 20;
		}
		if (target == receiver) {
			passChance -= 35;
			Gdx.app.log(TAG,
					"Calculating pass chance, lowered due to just receiving ball.");
		}
		if (ballInOffensiveArea) {
			if (countPlayersInfront(player, opponentColour, 100) == 0) {
				passChance = 0;
			}
		}
		return passChance;
	}

	/**
	 * Counts the number of players both closer to the goal and in the proximity
	 * of a player.
	 * 
	 * @param player
	 *            The player
	 * @param teamColourToCount
	 *            The team to count
	 * @param proximity
	 *            The proximity a player has to be within to be counted
	 * @return
	 */
	private int countPlayersInfront(Player player,
			TeamColour teamColourToCount, float proximity) {
		TeamColour playerColour = player.getTeam();
		Vector2 playerPosition = player.getPlayerPosition();
		Vector2 playerTargetGoal = (playerColour == teamColour) ? targetGoal
				: homeGoal;

		int playerCount = 0;
		Circle proximityCircle = new Circle(playerPosition, proximity);

		for (Player otherPlayer : game.getPlayers(teamColourToCount)) {
			float playerDistanceFromGoal = playerTargetGoal.dst(player
					.getPlayerPosition());
			float otherPlayerDistanceFromGoal = playerTargetGoal
					.dst(otherPlayer.getPlayerPosition());
			if (proximityCircle.contains(otherPlayer.getPlayerPosition())
					&& playerDistanceFromGoal > otherPlayerDistanceFromGoal) {
				playerCount++;
			}
		}

		return playerCount;
	}

	/**
	 * Counts the number of players in the proximity of a player.
	 * 
	 * @param player
	 *            The player
	 * @param teamColourToCount
	 *            The team to count
	 * @param proximity
	 *            The proximity a player has to be within to be counted
	 * @return
	 */
	private int countPlayersAround(Player player, TeamColour teamColourToCount,
			float proximity) {
		TeamColour playerColour = player.getTeam();
		Vector2 playerPosition = player.getPlayerPosition();

		int playerCount = 0;
		Circle proximityCircle = new Circle(playerPosition, proximity);

		for (Player otherPlayer : game.getPlayers(teamColourToCount)) {
			if (proximityCircle.contains(otherPlayer.getPlayerPosition())) {
				playerCount++;
			}
		}

		return playerCount;
	}

	/**
	 * Returns a list of all players around another player
	 * 
	 * @param player
	 *            The centre player
	 * @param proximity
	 *            The proximity another player has to be to be in the list
	 * @return A list of players within proximity of the player.
	 */

	private ArrayList<Player> playersAround(Player player, float proximity) {
		Vector2 playerPosition = player.getPlayerPosition();

		ArrayList<Player> players = new ArrayList<Player>();
		Circle proximityCircle = new Circle(playerPosition, proximity);

		for (Player otherPlayer : game.getAllPlayers()) {
			if (proximityCircle.contains(otherPlayer.getPlayerPosition())) {
				players.add(otherPlayer);
			}
		}

		return players;
	}

	/**
	 * Sorts a list of players by distance from their own goal, closest first.
	 * 
	 * @param list
	 *            The list of players to sort
	 */
	private void sortPlayersByDistanceFromHomeGoal(List<Player> list) {
		Collections.sort(list, new PlayerComparator(homeGoal));
	}

	private class PlayerComparator implements Comparator<Player> {

		Vector2 target;

		public PlayerComparator(Vector2 target) {
			this.target = target;
		}

		@Override
		public int compare(Player lhs, Player rhs) {
			float l = lhs.getPlayerPosition().dst(target);
			float r = rhs.getPlayerPosition().dst(target);
			return Math.round(l - r);
		}
	}

	private Player playerNearestTheGoalie(List<Player> players) {
		return playerNearestVector(players, goalie.getPlayerPosition());
	}

	/** Finds the player in the list nearest a given Vector */
	private Player playerNearestVector(List<Player> listOfPlayers,
			Vector2 vector) {
		float minValue = Float.MAX_VALUE;
		Player minPlayer = null;

		for (Player player : listOfPlayers) {
			if (vector.dst(player.getPlayerPosition()) < minValue) {
				minPlayer = player;
				minValue = vector.dst(player.getPlayerPosition());
			}
		}
		return minPlayer;
	}

	private boolean computerControlsBall() {
		return (ball.hasOwner() && ball.getOwner().getTeam() == teamColour);
	}

	private boolean opponentControlsBall() {
		return (ball.hasOwner() && ball.getOwner().getTeam() != teamColour);
	}

	/** Checks whether the ball is in the computer players side of the court */
	private boolean isBallInOpponentHalf() {
		return ball.getBallPosition().dst(targetGoal) < ball.getPosition().dst(
				homeGoal);

	}

	private Rectangle getDefensiveArea() {
		if (teamColour == TeamColour.RED) {
			return new Rectangle(0, (float) RED_GOAL_AREA_TOP,
					Game.VIRTUAL_SCREEN_WIDTH, Game.VIRTUAL_SCREEN_HEIGHT / 3);
		} else {
			return new Rectangle(0, (float) BLUE_GOAL_AREA_TOP,
					Game.VIRTUAL_SCREEN_WIDTH, Game.VIRTUAL_SCREEN_HEIGHT / 3);
		}
	}

	private Rectangle getMidFieldArea() {
		return new Rectangle(0, (float) RED_GOAL_AREA_BOTTOM,
				Game.VIRTUAL_SCREEN_WIDTH, Game.VIRTUAL_SCREEN_HEIGHT / 3);
	}

	private Rectangle getOffensiveArea() {
		if (teamColour == TeamColour.RED) {
			return new Rectangle(0, (float) BLUE_GOAL_AREA_TOP,
					Game.VIRTUAL_SCREEN_WIDTH, Game.VIRTUAL_SCREEN_HEIGHT / 3);
		} else {
			return new Rectangle(0, (float) RED_GOAL_AREA_TOP,
					Game.VIRTUAL_SCREEN_WIDTH, Game.VIRTUAL_SCREEN_HEIGHT / 3);
		}
	}

	private void moveTodDefensivePosition(Player player) {
		if (teamColour == TeamColour.RED) {
			moveToArea(player, RED_GOAL_AREA_TOP, RED_GOAL_AREA_BOTTOM);
		} else {
			moveToArea(player, BLUE_GOAL_AREA_TOP, BLUE_GOAL_AREA_BOTTOM);
		}
	}

	private void moveToMidFieldPosition(Player player) {
		moveToArea(player, BLUE_GOAL_AREA_BOTTOM, RED_GOAL_AREA_TOP);
	}

	private void moveToOffensivePosition(Player player) {
		if (teamColour == TeamColour.RED) {
			moveToArea(player, BLUE_GOAL_AREA_TOP, BLUE_GOAL_AREA_BOTTOM);
		} else {
			moveToArea(player, RED_GOAL_AREA_TOP, RED_GOAL_AREA_BOTTOM);
		}
	}

	/** Instruct the player to move to a random spot within a given area */
	private void moveToArea(Player player, double top, double bottom) {
		float x = rand(40, Game.VIRTUAL_SCREEN_WIDTH - 40);
		float y = rand((float) bottom, (float) top);
		moveToPosition(player, new Vector2(x, y));
	}

	private void moveBetween(Player player, Vector2 a, Vector2 b) {
		// TODO test this, I'm pretty sure it doesn't work
		float distance = a.dst(b) / 2;
		Vector2 distanceVector = Utils.getMoveVector(a, b, distance);
		Vector2 target = a.cpy().add(distanceVector);
		moveToPosition(player, target);
	}

	private void moveToPosition(Player player, Vector2 vector) {
		player.addAction(new Move(new Vector2[] { player.getPlayerPosition(),
				vector }));
	}

	private boolean withinShootingRange(Player player) {
		return player.getPlayerPosition().dst(targetGoal) < SHOOTING_RANGE;
	}

	private void shoot(Player player) {
		player.addAction(new Kick(ball, targetGoal, player.getFuturePosition()));
	}

	private void followBall(Player player) {
		player.addAction(new MarkBall(player.getPlayerPosition(), ball));
	}

	private void followPlayer(Player player, Player targetPlayer) {
		try {
			player.addAction(new Mark(player.getPlayerPosition(), targetPlayer,
					ball));
		} catch (NullPointerException e) {
			boolean playerNull = player == null;
			boolean targetPlayerNull = targetPlayer == null;
			Gdx.app.error(TAG, "player null: " + Boolean.toString(playerNull)
					+ " & target null: " + Boolean.toString(targetPlayerNull));
			throw new NullPointerException();
		}
	}

	private void passToPlayer(Player player, Player targetPlayer) {
		player.addAction(new Pass(ball, player, targetPlayer, player
				.getBallPosition()));
		receiver = targetPlayer;
	}

	/** Generate a random number between two values */
	public static float rand(float max, float min) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

}
