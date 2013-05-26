package com.samsung.comp.input;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.AbstractGame.GameState;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.MarkBall;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Players.Player;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class SPenInput extends AbstractInput implements
		SPenTouchListener, SPenHoverListener {

	private static final String TAG = "GameInputStrategy";

	public SPenInput(AbstractGame game) {
		this.game = game;
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found
	 */
	private Player findPlayer(MotionEvent motionEvent) {
		Vector2 point = new Vector2(motionEvent.getX(), motionEvent.getY());
		return findPlayer(game.translateInputToField(point));
	}


	@Override
	public boolean onTouchFinger(View arg0, MotionEvent event) {

		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));
		float x = eventVector.x;
		float y = eventVector.y;

		game.onPress(x, y);
		return false;
	}

	@Override
	public boolean onTouchPen(View arg0, MotionEvent event) {

		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));

		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			game.onPress(eventVector.x, eventVector.y);
		}

		if (game.getGameState() == GameState.INPUT) {

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
				} else if (startAtBall && finishedAtBall
						&& selectedPlayer != null) {
					Log.i(TAG, "You marked the ball");
					pressBall();
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

		if (selectedPlayer == null && isSelectable(pressedPlayer)) {
			selectedPlayer = pressedPlayer;
			return;
		} else if (selectedPlayer == pressedPlayer) {
			selectedPlayer = null;
			return;
		}

		if (selectedPlayer != null
				&& pressedPlayer.getTeam() == game.getHumanColour()) {
			// If both players are selectable pass between them
			selectedPlayer.addAction(new Pass(game.getBall(), selectedPlayer,
					pressedPlayer, selectedPlayer.getFuturePosition()));

		} else if (selectedPlayer != null
				&& pressedPlayer.getTeam() != game.getHumanColour()
				&& pressedPlayer != game.getComputerGoalie()) {
			// If the first player is selectable, the second player is on the
			// opposingTeam but is not a goalie then mark the second player
			selectedPlayer.addAction(new Mark(selectedPlayer, pressedPlayer));
		}
		selectedPlayer = null;
	}

	/** Called when a line is drawn starting and finishing on top of a ball */
	private void pressBall() {
		Log.i(TAG, "Pressed ball: ");

		if (selectedPlayer != null) {
			selectedPlayer.addAction(new MarkBall(selectedPlayer
					.getFuturePosition(), game.getBall()));
			selectedPlayer = null;
			return;
		}
	}

	private void pressPoint(Vector2 point) {
		if (selectedPlayer != null) {
			selectedPlayer.addAction(new Kick(game.getBall(), point,
					selectedPlayer.getFuturePosition()));
			selectedPlayer = null;
		}
	}

	private void assignMoveTo(Player player, int index) {
		Log.i(TAG, "assigning Move command to " + player.toString());
		if (player.getFinalAction() instanceof Mark
				|| player.getFinalAction() instanceof MarkBall) {
			player.setAction(
					new MoveToPosition(
							lineInProgress.get(lineInProgress.size() - 1),
							lineInProgress.get(0)), index);
		} else {
			player.setAction(
					new Move(lineInProgress.toArray(new Vector2[lineInProgress
							.size()])), index);
		}

		selectedPlayer = null;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		Vector2 eventVector = game.translateInputToField(new Vector2(event
				.getX(), event.getY()));

		if (game.getBar() != null
				&& game.getBar().contains(eventVector.x, eventVector.y)) {
			Log.i("BAR", "hover eventVector: " + eventVector.toString() + " "
					+ game.getBar().toString());
			game.getBar().fade();
		}

		if (game.getGameState() == GameState.INPUT) {
			Player player = findPlayer(event);
			highlightedPlayer = player;

			Vector2 point = new Vector2(event.getX(), event.getY());
			isBallHighlighted = findBall(game.translateInputToField(point));
		}
		return false;
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
