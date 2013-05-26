package com.samsung.comp.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.Players.Player;

public abstract class AbstractInput {

	protected static final String TAG = "AbstractInputStrategy";

	protected static final float INPUT_EPSILON_VALUE = 32;
	protected ArrayList<Vector2> lineInProgress = new ArrayList<Vector2>();

	protected AbstractGame game;

	protected Player selectedPlayer;
	protected Player highlightedPlayer;
	protected boolean isBallHighlighted;

	public List<Vector2> getLineBeingDrawn() {
		return new ArrayList<Vector2>(lineInProgress);
	}

	/**
	 * Finds a player that overlaps or is near a point, returns null if no
	 * player found.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	protected Player findPlayer(Vector2 point) {
		Player temp = null;
		Vector2 playerVector;
		for (Player player : game.getAllPlayers()) {
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
	 * Looks for a player at or near the specified point. Gets index of the
	 * selected position (Possibly a hack). Returns an integer indicating how
	 * far in the list of actions has been selected.
	 * 
	 * @return The action list queue index at the selected position
	 */
	protected int findPlayerIndex(Vector2 point) {
		Vector2 playerVector;
		for (Player player : game.getAllPlayers()) {
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
	 * Finds if the ball overlaps or is near a point.
	 * 
	 * @Warning THIS FUNCTION ASSUMES THAT YOU HAVE TRANSLATED THE INPUT TO
	 *          FIELD COORDINATES
	 */
	protected boolean findBall(Vector2 point) {
		if (game.getBall() == null) {
			return false;
		}

		return (game.getBall().getBallPosition().epsilonEquals(point,
				INPUT_EPSILON_VALUE)) ? true
				: false;
	}

	protected boolean isSelectable(Player player) {
		if (game.getHumanPlayers().contains(player)) {
			return true;
		}

		if (game.getHumanGoalie() != null) {
			if (game.getHumanGoalie().hasBall()
					&& game.getHumanGoalie() == player) {
				return true;
			}
		}
		return false;
	}

	public void draw(SpriteBatch batch) {
		if (highlightedPlayer != null) {
			highlightedPlayer.drawHighlight(batch);
			game.drawTimeLinePoints(highlightedPlayer);
			game.drawPlayerStats(batch, highlightedPlayer);
		}
		if (selectedPlayer != null) {
			selectedPlayer.drawSelect(batch);
			game.drawTimeLinePoints(selectedPlayer);
		}
		if (isBallHighlighted) {
			game.getBall().drawHighlight(batch);
		}

	}

	public void deselectPlayers() {
		selectedPlayer = null;
		highlightedPlayer = null;
	}

	public Player getSelectedPlayer() {
		return selectedPlayer;
	}

}
