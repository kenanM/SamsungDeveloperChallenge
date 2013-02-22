package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
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
	private boolean paused = false;

	private List<Player> players = new ArrayList<Player>();;
	private List<Vector2> lineInProgress = new ArrayList<Vector2>();

	private Player selectedPlayer;
	private Player highlightedPlayer;
	private boolean ballIsHighlighted;

	private TeamColour playerColour;
	private List<Player> selectablePlayers = new ArrayList<Player>();
	private Player humanGoalie;
	private Player computerGoalie;

	private Bar bar;

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
		bar = game.getBar();
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
			List<Vector2> pointsList = player.getPositionList();
			Collections.reverse(pointsList);

			for (int i = 0; i < pointsList.size(); i++) {
				playerVector = pointsList.get(i);
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
	 * hack lol stfu. Gets index of the position (probably legit and not too
	 * hacky)
	 */
	private int findPlayerIndex(Vector2 point) {
		Vector2 playerVector;
		for (Player player : players) {
			List<Vector2> pointsList = player.getPositionList();
			Collections.reverse(pointsList);

			for (int i = 0; i < pointsList.size(); i++) {
				playerVector = pointsList.get(i);
				if (playerVector.epsilonEquals(point, INPUT_EPSILON_VALUE)) {
					// We are biased to selectable players, return them before
					// an unselectable one.
					if (isSelectable(player)) {
						return pointsList.size() - 1 - i;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Finds a ball that overlaps or is near a point, returns null if no player
	 * found.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	private boolean findBall(Vector2 point) {
		return (game.getBall().getBallPosition().epsilonEquals(point,
				INPUT_EPSILON_VALUE)) ? true : false;
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

	private void setSelectedPlayer(Player player) {
		selectedPlayer = player;
		bar.setSelectedPlayer(player);
	}

	public Player getSelectedPlayer() {
		return selectedPlayer;
	}

	public Player getHighlightedPlayer() {
		return highlightedPlayer;
	}

	public boolean isSelectable(Player player) {
		return selectablePlayers.contains(player);
	}

	@Override
	public boolean onTouchFinger(View arg0, MotionEvent event) {

		if (paused) {
			Log.i(TAG, "Paused ... Finger pressed");
			game.pauseMenu.onPress(event);
		}

		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));
		float x = eventVector.x;
		float y = eventVector.y;

		if (detectPresses) {
			if (bar.getPlayIcon().contains(x, y)) {

				if (game.beginExecution()) {
					setSelectedPlayer(null);
					highlightedPlayer = null;
					detectPresses = false;
				}
			} else if (bar.press(x, y)) {
				// TODO (Gavin): Change this to be 'undo last action'
				selectedPlayer.clearAction();
			}
		}
		return false;
	}

	@Override
	public boolean onTouchPen(View arg0, MotionEvent event) {
		if (paused) {
			game.pauseMenu.onPress(event);
		}

		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));

		if (bar != null && bar.contains(eventVector.x, eventVector.y)) {
			bar.fade();
		}

		if (detectPresses) {
			int action = event.getAction();
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

				boolean startAtBall = findBall(startVector);
				boolean finishedAtBall = findBall(endVector);

				// Note to self: the orderings here are very important
				if (startVector.dst(endVector) < 15 && finish == null) {
					Log.i(TAG, "You pressed: " + startVector.toString());
					if (startAtBall && finishedAtBall && selectedPlayer != null) {
						Log.i(TAG, "You marked the ball");
						pressBall();
					} else {
						pressPoint(startVector);
					}
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
					int index = findPlayerIndex(lineInProgress.get(0));
					Log.i(TAG,
							"You drew a line from a player "
									+ String.valueOf(index));
					assignMoveTo(start, index);
					lineInProgress.clear();
				}
			}
		}
		return true;
	}

	/**
	 * Called when a line is drawn starting and finishing on top of a player &
	 * not a ball
	 */
	private void pressPlayer(Player pressedPlayer) {
		Log.i(TAG, "Pressed player: " + pressedPlayer.toString());

		if (selectedPlayer == null && isSelectable(pressedPlayer)) {
			// Select this player
			setSelectedPlayer(pressedPlayer);
			return;
		} else if (selectedPlayer == pressedPlayer) {
			// deselect the player
			setSelectedPlayer(null);
			return;
		}

		if (selectedPlayer != null && pressedPlayer.getTeam() == playerColour) {
			// If both players are selectable pass between them
			selectedPlayer.addAction(new Pass(game.getBall(), selectedPlayer,
					pressedPlayer, selectedPlayer.getFuturePosition()));

		} else if (selectedPlayer != null
				&& pressedPlayer.getTeam() != playerColour
				&& pressedPlayer != computerGoalie) {
			// If the first player is selectable, the second player is on the
			// opposingTeam but is not a goalie then mark the second player
			selectedPlayer.addAction(new Mark(selectedPlayer, pressedPlayer));
		}
		setSelectedPlayer(null);
	}

	/** Called when a line is drawn starting and finishing on top of a ball */
	private void pressBall() {
		Log.i(TAG, "Pressed ball: ");

		if (selectedPlayer != null) {
			selectedPlayer.addAction(new MarkBall(selectedPlayer
					.getFuturePosition(), game.getBall()));
			setSelectedPlayer(null);
			return;
		}
	}

	private void pressPoint(Vector2 point) {
		if (selectedPlayer != null) {
			selectedPlayer.addAction(new Kick(game.getBall(), point,
					selectedPlayer.getFuturePosition()));
			setSelectedPlayer(null);
		}
	}

	private void assignMoveTo(Player player, int index) {
		Log.i(TAG, "assigning Move command to " + player.toString());
		if (player.getFinalAction() instanceof Mark) {
			player.setAction(
					new MoveToPosition(
							lineInProgress.get(lineInProgress.size() - 1),
							lineInProgress.get(0)), index);
		} else {
			player.setAction(
					new Move(lineInProgress.toArray(new Vector2[lineInProgress
							.size()])), index);
		}

		setSelectedPlayer(null);
	}

	private void setHighlightedPlayer(Player player) {
		highlightedPlayer = player;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {

		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));

		if (bar != null && bar.contains(eventVector.x, eventVector.y)) {
			Log.i("BAR", "hover eventVector: " + eventVector.toString() + " "
					+ bar.toString());
			bar.fade();
		}

		if (detectPresses) {
			Player player = findPlayer(event);
			setHighlightedPlayer(player);

			Vector2 point = new Vector2(event.getX(), event.getY());
			ballIsHighlighted = findBall(game.translateInputToField(point));
		}
		return false;
	}

	public void draw(SpriteBatch batch) {
		if (highlightedPlayer != null) {
			highlightedPlayer.drawHighlight(batch);
		}
		if (selectedPlayer != null) {
			selectedPlayer.drawSelect(batch);
		}
		if (ballIsHighlighted) {
			game.getBall().drawHighlight(batch);
		}
	}

	public void enterPauseState() {
		paused = true;
	}

	public void exitPauseState() {
		paused = false;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonDown: " + arg1.getX() + ", " + arg1.getY());
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
