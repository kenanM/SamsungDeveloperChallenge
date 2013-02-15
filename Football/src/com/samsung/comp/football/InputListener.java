package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class InputListener implements SPenTouchListener, SPenHoverListener {

	private static final float INPUT_EPSILON_VALUE = 32;

	private static final String TAG = "InputListener";

	private final Game game;
	private boolean detectPresses = false;
	private List<Player> players = new ArrayList<Player>();;
	private List<Vector2> lineInProgress = new ArrayList<Vector2>();

	private Player selectedPlayer;
	private Player highlightedPlayer;

	private TeamColour playerColour;
	private List<Player> selectablePlayers = new ArrayList<Player>();
	private Player humanGoalie;
	private Player computerGoalie;

	public InputListener(Game game) {
		this.game = game;
	}

	/**
	 * Set the goalies players. Only call this method once the game has set team
	 * colours
	 */
	public void initialise() {
		playerColour = game.getHumanColour();
		humanGoalie = game.getHumanGoalie();
		computerGoalie = game.getComputerGoalie();
	}

	private void fetchSelectablePlayers() {
		selectablePlayers = new ArrayList<Player>(game.getHumanPlayers());
		if (humanGoalie.hasBall()) {
			selectablePlayers.add(humanGoalie);
		}
	}

	public void beginInputStage(List<Player> players) {
		detectPresses = true;
		this.players = players;
		fetchSelectablePlayers();
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	private Player findPlayer(Vector2 point) {
		Player temp = null;
		Vector2 playerVector;
		for (Player player : players) {

			for (int i = 0; i < player.getPositionList().size(); i++) {
				playerVector = player.getPositionList().get(i);
				if (playerVector.epsilonEquals(point, INPUT_EPSILON_VALUE)) {
					// We are biased to selectable players, return them before
					// an unselectable one.
					if (isSelectable(player)) {
						return player;
					} else {
						temp = player;
					}
				}
			}

		}
		return temp;
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found
	 */
	private Player findPlayer(MotionEvent motionEvent) {
		Vector2 point = new Vector2(motionEvent.getX(), motionEvent.getY());
		return findPlayer(game.translateInputToField(point));
	}

	public List<Vector2> getLineBeingDrawn() {
		return new ArrayList<Vector2>(lineInProgress);
	}

	Player getSelectedPlayer() {
		return selectedPlayer;
	}

	Player getHighlightedPlayer() {
		return highlightedPlayer;
	}

	public boolean isSelectable(Player player) {
		return selectablePlayers.contains(player);
	}

	@Override
	public boolean onTouchFinger(View arg0, MotionEvent arg1) {
		if (detectPresses && arg1.getX() < 128 && arg1.getY() < 128) {
			if (game.beginExecution()) {
				selectedPlayer = null;
				highlightedPlayer = null;
				detectPresses = false;
			}
		}
		return false;
	}

	@Override
	public boolean onTouchPen(View arg0, MotionEvent event) {
		if (detectPresses) {
			int action = event.getAction();
			Vector2 eventVector = game.translateInputToField(new Vector2(event
					.getX(), event.getY()));

			if (action == MotionEvent.ACTION_DOWN) {
				lineInProgress.clear();
				lineInProgress.add(eventVector);
				return true;
			}

			if (action == MotionEvent.ACTION_MOVE) {
				for (int i = 0; i < event.getHistorySize(); i++) {
					lineInProgress.add(game.translateInputToField(new Vector2(
							event.getHistoricalX(i), event.getHistoricalY(i))));
				}
				lineInProgress.add(eventVector);
				return true;
			}

			if (action == MotionEvent.ACTION_UP) {
				Player start = findPlayer(lineInProgress.get(0));
				Player finish = findPlayer(lineInProgress.get(lineInProgress
						.size() - 1));

				Vector2 startVector = lineInProgress.get(0);
				Vector2 endVector = lineInProgress
						.get(lineInProgress.size() - 1);

				// Note to self: the orderings here are very important
				if (startVector.dst(endVector) < 6 && finish == null) {
					Log.i(TAG, "You pressed: " + startVector.toString());
					pressPoint(startVector);
					lineInProgress.clear();
				} else if (start == null) {
					Log.i(TAG, "You drew a line starting from a null position");
					lineInProgress.clear();
				} else if (start == finish) {
					Log.i(TAG, "You selected a player");
					pressPlayer(start);
					lineInProgress.clear();
				} else if (!isSelectable(start)) {
					Log.i(TAG, "Your line started from an unselectable player");
					lineInProgress.clear();
				} else if (isSelectable(start)) {
					Log.i(TAG, "You drew a line from a player");
					assignMoveTo(start);
					lineInProgress.clear();
				}
			}
		}
		return true;
	}

	/** Called when a line is drawn starting and finishing on top of a player */
	private void pressPlayer(Player pressedPlayer) {
		Log.i(TAG, "Pressed player: " + pressedPlayer.toString());

		if (selectedPlayer == null && isSelectable(pressedPlayer)) {
			// Select this player
			selectedPlayer = pressedPlayer;
			return;
		} else if (selectedPlayer == pressedPlayer) {
			// deselect the player
			selectedPlayer = null;
			return;
		}

		if (selectedPlayer != null && pressedPlayer.getTeam() == playerColour) {
			// If both players are selectable pass between them
			selectedPlayer.addAction(new Pass(game.getBall(), selectedPlayer,
					pressedPlayer));

		} else if (selectedPlayer != null
				&& pressedPlayer.getTeam() != playerColour
				&& pressedPlayer != computerGoalie) {
			// If the first player is selectable, the second player is on the
			// opposingTeam but is not a goalie then mark the second player
			selectedPlayer.addAction(new Mark(selectedPlayer, pressedPlayer));
		}
		selectedPlayer = null;
	}

	private void pressPoint(Vector2 point) {
		if (selectedPlayer != null) {
			selectedPlayer.addAction(new Kick(game.getBall(), point));
		}
	}

	private void assignMoveTo(Player player) {
		Log.i(TAG, "assigning Move command to " + player.toString());
		player.addAction(new Move(lineInProgress
				.toArray(new Vector2[lineInProgress.size()])));
		selectedPlayer = null;
	}

	private void setHighlightedPlayer(Player player) {
		highlightedPlayer = player;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		if (detectPresses) {
			Player player = findPlayer(event);
			setHighlightedPlayer(player);
		}
		return false;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		if (selectedPlayer != null) {
			selectedPlayer.clearAction();
		}
	}

	public void draw(SpriteBatch batch) {
		if (highlightedPlayer != null) {
			highlightedPlayer.drawHighlight(batch);
		}
		if (selectedPlayer != null) {
			selectedPlayer.drawSelect(batch);
		}
	}

	@Override
	public boolean onTouchPenEraser(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchPenEraser: " + arg1.getX() + ", " + arg1.getY());
		return false;
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onTouchButtonDown(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchButtonDown: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onTouchButtonUp(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}
}
