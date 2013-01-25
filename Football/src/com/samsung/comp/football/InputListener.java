package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Mark;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class InputListener implements SPenTouchListener, SPenHoverListener {

	private final Game game;
	private boolean detectPresses = false;
	private List<Player> players;
	private List<Vector2> lineInProgress = new ArrayList<Vector2>();
	private Player playerBeingDrawnFrom;
	private Player selectedPlayer;

	private List<Player> selectablePlayers = new ArrayList<Player>();

	public InputListener(Game game) {
		players = new ArrayList<Player>();
		this.game = game;
	}

	public void fetchSelectablePlayers() {
		selectablePlayers = new ArrayList<Player>(game.getHumanPlayers());
		if (game.humanGoalieIsHoldingTheBall()) {
			selectablePlayers.add(game.getHumanGoalie());
		}
	}

	public void beginInputStage(List<Player> players) {
		detectPresses = true;
		this.players = players;
		fetchSelectablePlayers();
	}

	/**
	 * Finds a player that will overlap or be near a point, returns null if no
	 * player found
	 */
	private Player findPlayerPlan(Vector2 point) {
		Vector2 fieldPoint = game.translateInputToField(point);
		Vector2 playerVector;
		for (Player player : players) {
			playerVector = new Vector2(player.getPlayerLatestPosition().x, player.getPlayerLatestPosition().y);
			// TODO: Remove hard coded value
			if (playerVector.epsilonEquals(fieldPoint, 32f)) {
				Log.v("Input", "Hover");
				player.highlight();
				return player;
			}
		}
		return null;
	}
	
	/**
	 * Finds a player that will overlap or be near a point, returns null if no
	 * player found
	 */
	private Player findPlayerPlan(MotionEvent motionEvent) {
		Vector2 point = new Vector2(motionEvent.getX(), motionEvent.getY());
		return findPlayerPlan(point);
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found
	 */
	private Player findPlayer(Vector2 point) {
		Vector2 fieldPoint = game.translateInputToField(point);
		Vector2 playerVector;
		for (Player player : players) {
			playerVector = new Vector2(player.getPlayerX(), player.getPlayerY());
			// TODO: Remove hard coded value
			if (playerVector.epsilonEquals(fieldPoint, 32f)) {
				Log.v("Input", "Hover");
				player.highlight();
				return player;
			}
		}
		return null;
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found
	 */
	private Player findPlayer(MotionEvent motionEvent) {
		Vector2 point = new Vector2(motionEvent.getX(), motionEvent.getY());
		return findPlayer(point);
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
		// TODO: remove hard coded value
		if (detectPresses && arg1.getX() < 128 && arg1.getY() < 128) {
			// Log.v(TAG, "onTouchFinger: " + arg1.getX() + ", " + arg1.getY());
			detectPresses = false;
			game.beginExecution();
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
				if (selectedPlayer == null) {
					playerBeingDrawnFrom = findPlayer(event);
					if (playerBeingDrawnFrom == null
							|| !isSelectable(playerBeingDrawnFrom)) {
						return false;
					} else {
						// Begin drawing a line
						playerBeingDrawnFrom.highlight();
						lineInProgress.clear();
						lineInProgress.add(eventVector);
					}
				} else {
					Ball ball = game.getBall();
					if (selectedPlayer.hasBall()) {
						// See if the pen is pointing at a team mate
						Player receiver = findPlayer(event);
						if (receiver != null
								&& receiver.getTeam() == game.getHumanColour()) {
							Log.v("InputListener", "passing to teamate");
							selectedPlayer.setAction(new Pass(ball, receiver));
						} else {
							selectedPlayer
									.setAction(new Kick(ball, eventVector));
						}
					} else {
						Player target = findPlayer(event);
						if (target != null
								&& target.getTeam() != game.getHumanColour()) {
							Log.v("InputListener", "marking");
							selectedPlayer.setAction(new Mark(selectedPlayer,
									target));
						}

					}

				}
			}

			if (action == MotionEvent.ACTION_MOVE) {
				if (selectedPlayer != null) {
				} else {
					// Log.v("MOTION", "ACTION_MOVE");
					playerBeingDrawnFrom.highlight();
					for (int i = 0; i < event.getHistorySize(); i++) {
						lineInProgress.add(game
								.translateInputToField(new Vector2(event
										.getHistoricalX(i), event
										.getHistoricalY(i))));
					}
					lineInProgress.add(eventVector);
				}
			}

			if (action == MotionEvent.ACTION_UP) {
				if (selectedPlayer == null) {
					Player player = findPlayer(event);
					if (player == null || player != playerBeingDrawnFrom) {
						// end of the line
						lineInProgress.add(eventVector);
						playerBeingDrawnFrom.setAction(new Move(lineInProgress
								.toArray(new Vector2[lineInProgress.size()])));
						playerBeingDrawnFrom.setPlayerLatestPosition(lineInProgress.get(lineInProgress.size()-1));
						lineInProgress.clear();
					} else {
						lineInProgress.clear();
						selectedPlayer = player;
					}

				} else {
					selectedPlayer = null;
				}
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
			Player player = findPlayer(event);
			game.setHoveringPlayer(player);
			if (player != null) {
				player.highlight();
			}
		}
		return false;
	}

	Player getSelectedPlayer() {
		return selectedPlayer;
	}

	public boolean isSelectable(Player player) {
		return selectablePlayers.contains(player);
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonDown: " + arg1.getX() + ", " + arg1.getY());
		if (selectedPlayer != null) {
			selectedPlayer.clearAction();
		}
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
		// Log.v(TAG, "onHoverButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

}
