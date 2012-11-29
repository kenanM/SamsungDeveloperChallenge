package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Move;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class InputListener implements SPenTouchListener, SPenHoverListener {

	private static final String TAG = "InputListener";
	private final Game game;
	private boolean detectPresses = false;
	private List<Player> players;
	private List<Vector2> lineInProgress = new ArrayList<Vector2>();
	private Player playerBeingDrawnFrom;
	private Player selectedPlayer;

	public InputListener(Game game) {
		players = new ArrayList<Player>();
		this.game = game;
	}

	public void beginInputStage(List<Player> players) {
		detectPresses = true;
		this.players = players;
		game.clearActions();
	}

	/**
	 * Finds a player that overlaps or nears a point, returns null if no player
	 * found
	 */
	private Player findPlayer(Vector2 point) {
		Vector2 playerVector;
		for (Player player : players) {
			playerVector = new Vector2(player.x + 16, player.y + 16);
			if (playerVector.epsilonEquals(point, 32f)) {
				Log.v("Input", "Hover");
				player.highlight();
				return player;
			}
		}
		return null;
	}

	public List<Vector2> getLineBeingDrawn() {
		return new ArrayList<Vector2>(lineInProgress);
	}

	@Override
	public void onTouchButtonDown(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchButtonDown: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onTouchButtonUp(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public boolean onTouchFinger(View arg0, MotionEvent arg1) {
		if (detectPresses && arg1.getX() < 128 && arg1.getY() < 128) {
			// Log.v(TAG, "onTouchFinger: " + arg1.getX() + ", " + arg1.getY());
			detectPresses = false;
			game.beginExecution();
		}
		return false;
	}

	@Override
	public boolean onTouchPen(View arg0, MotionEvent motionEvent) {
		int action = motionEvent.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			if (selectedPlayer == null) {
				// Log.v("MOTION", "ACTION_DOWN");
				Vector2 vector = new Vector2(motionEvent.getX(),
						motionEvent.getY());
				playerBeingDrawnFrom = findPlayer(vector);
				selectedPlayer = null;
				if (playerBeingDrawnFrom == null) {
					return false;
				} else {
					// Being drawing a line
					playerBeingDrawnFrom.highlight();
					lineInProgress.clear();
					lineInProgress.add(vector);
				}
			} else {
				Ball ball = game.getBall();
				if (ball.hasOwner() && ball.getOwner() == selectedPlayer) {
					game.addAction(new Kick(ball, motionEvent.getX(),
							motionEvent.getY()));
				}
			}
		}
		if (action == MotionEvent.ACTION_MOVE) {
			if (selectedPlayer != null) {
			} else {
				// Log.v("MOTION", "ACTION_MOVE");
				playerBeingDrawnFrom.highlight();
				for (int i = 0; i < motionEvent.getHistorySize(); i++) {
					lineInProgress.add(new Vector2(motionEvent
							.getHistoricalX(i), motionEvent.getHistoricalY(i)));
				}
				lineInProgress.add(new Vector2(motionEvent.getX(), motionEvent
						.getY()));
			}
		}
		if (action == MotionEvent.ACTION_UP) {
			Log.v("MOTION", "ACTION_UP");
			if (selectedPlayer == null) {
				Player player = findPlayer(new Vector2(motionEvent.getX(),
						motionEvent.getY()));
				if (player == null || player != playerBeingDrawnFrom) {
					// end of the line
					lineInProgress.add(new Vector2(motionEvent.getX(),
							motionEvent.getY()));
					game.addAction(new Move(playerBeingDrawnFrom,
							lineInProgress.toArray(new Vector2[lineInProgress
									.size()])));
					lineInProgress.clear();
				} else {
					lineInProgress.clear();
					selectedPlayer = player;
				}

			} else {
				selectedPlayer = null;
			}
		}
		return true;
	}

	@Override
	public boolean onTouchPenEraser(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onTouchPenEraser: " + arg1.getX() + ", " + arg1.getY());
		return false;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		if (detectPresses) {
			// Log.v(TAG, "onHover: " + event.getX() + ", " + event.getY());
			Vector2 hoverPoint = new Vector2(event.getX(), event.getY());
			Player player = findPlayer(hoverPoint);
			if (player != null) {
				player.highlight();
			}
		}
		return false;
	}

	Player getSelectedPlayer() {
		return selectedPlayer;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonDown: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

}
