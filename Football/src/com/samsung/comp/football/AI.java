package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.BluePlayer;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.football.Players.RedPlayer;

public class AI {

	private static final String TAG = "AI";

	// Denotes different regions by splitting the pitch into thirds
	static final double RED_GOAL_AREA_TOP = Game.VIRTUAL_SCREEN_HEIGHT * 0.66;
	static final double RED_GOAL_AREA_BOTTOM = Game.VIRTUAL_SCREEN_HEIGHT - 100;
	static final double BLUE_GOAL_AREA_TOP = 100;
	static final double BLUE_GOAL_AREA_BOTTOM = Game.VIRTUAL_SCREEN_HEIGHT * 0.33;

	// The distance from which the AI will attempt a shot
	private static final float SHOOTING_RANGE = 250;

	private Game game;
	private TeamColour teamColour;
	private Player goalie;
	private Ball ball;

	private Vector2 targetGoal;
	private Vector2 homeGoal;

	public AI(Game game) {
		// Decide whether the AI or the Game should carry out the
		// "if(teamColour == TeamColour.RED)" logic
		this.game = game;
		this.teamColour = game.getComputerColour();
		this.goalie = game.getComputerGoalie();
		this.ball = game.getBall();
		if (teamColour == TeamColour.RED) {
			targetGoal = Game.BLUE_GOAL;
			homeGoal = Game.RED_GOAL;
		} else {
			targetGoal = Game.RED_GOAL;
			homeGoal = Game.BLUE_GOAL;
		}
	}

	public void getComputerActions() {
		Log.v(TAG, "getting computer actions");

		List<Player> players = new ArrayList<Player>(game.getComputerPlayers());
		sortPlayersByDistanceFromHomeGoal(players);

		boolean controlBall = computerControlsBall();
		boolean goalieHasBall = goalie.hasBall();

		if (goalieHasBall) {
			Player nearestToTheGoalie = playerNearestTheGoalie(players);
			goalie.addAction(new Pass(ball, goalie, nearestToTheGoalie, goalie
					.getFuturePosition()));
			players.remove(nearestToTheGoalie);

			for (Player player : players) {
				moveToMidFieldPosition(player);
			}
			return;
		}

		if (controlBall) {
			Player ballOwner = ball.getOwner();
			if (withinShootingRange(ballOwner)) {
				shoot(ballOwner);
				players.remove(ballOwner);
			} else {
				// If not within shooting range either pass or move
				Player receiver = players.get(3);
				if (receiver != ballOwner) {
					players.remove(receiver);
					ballOwner.addAction(new Pass(ball, ballOwner, receiver,
							ballOwner.getFuturePosition()));
				} else {
					moveToOffensivePosition(ballOwner);
				}
			}

			moveToMidFieldPosition(players.get(0));
			moveToOffensivePosition(players.get(1));
			if (players.size() == 3) {
				moveToOffensivePosition(players.get(2));
			}

		} else {
			// We don't control the ball
			Player playerNearestBall = playerNearestVector(players,
					ball.getBallPosition());
			players.remove(playerNearestBall);
			moveToPosition(playerNearestBall, ball.getBallPosition());
			moveTodDefensivePosition(players.get(0));
			moveBetween(players.get(1), homeGoal, ball.getBallPosition());
			moveToMidFieldPosition(players.get(2));
		}
	}

	private void sortPlayersByDistanceFromHomeGoal(List<Player> list) {
		Collections.sort(list, new PlayerComparator(homeGoal));
	}

	private void sortPlayersByDistanceFromTargetGoal(List<Player> list) {
		Collections.sort(list, new PlayerComparator(targetGoal));
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
	public Player playerNearestVector(List<Player> listOfPlayers, Vector2 vector) {
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

	/** Checks whether the ball is in the computer players side of the court */
	private boolean computerControlsBall() {
		if (teamColour == TeamColour.RED) {
			return (ball.hasOwner() && ball.getOwner() instanceof RedPlayer);
		} else {
			return (ball.hasOwner() && ball.getOwner() instanceof BluePlayer);
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
		// TODO remove the magic number from the following line
		float x = (float) (40 + (Math.random()
				* (Game.VIRTUAL_SCREEN_WIDTH - 40) + 1));

		float y = (float) (bottom + (Math.random() * (top - bottom) + 1));
		Log.v(TAG, "x:" + x + " y:" + y + "top/bottom " + top + "/" + bottom);
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
		player.addAction(new Kick(ball, targetGoal));
	}
}
