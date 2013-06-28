package com.samsung.comp.football.Actions;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;

public abstract class Action {

	protected Action nextAction;

	public Action getNextAction() {
		return nextAction;
	}

	public Action getFinalAction() {
		if (nextAction != null) {
			return nextAction.getFinalAction();
		} else {
			return this;
		}
	}

	public void clearSubsequentActions() {
		nextAction = null;
	}

	public void addNextAction(Action newAction) {
		if (nextAction == null) {
			nextAction = newAction;
		} else {
			nextAction.addNextAction(newAction);
		}
	}

	public void setNextAction(Action newAction) {
		nextAction = newAction;
	}

	/**
	 * Used for player selection
	 * 
	 * @return the final point in the chain of actions
	 */
	public Vector2 getFuturePosition() {
		if (nextAction == null) {
			return getPosition();
		} else {
			return nextAction.getFuturePosition();
		}
	}

	/**
	 * Abstract method, recursively goes through the chain of actions and
	 * calculates the future point of a player
	 * 
	 * @param time
	 *            amount of time in seconds the player travels before returning
	 *            its position
	 * @param initialPosition
	 *            the player's position
	 * @param speed
	 *            the player's speed
	 * @param positionInPath
	 *            the index of the player's currently executing path
	 * @param returnNulls
	 *            whether reaching the end of the chain of paths returns a null
	 *            or the last point
	 * @return
	 */
	public abstract Vector2 getFuturePosition(float time,
			Vector2 initialPosition, float speed, int positionInPath,
			boolean returnNulls);

	public Vector2 getPosition() {
		return null;
	}

	public LinkedList<Action> getActions() {
		LinkedList<Action> temp = new LinkedList<Action>();
		if (nextAction != null) {
			temp.add(nextAction);
			temp.addAll(nextAction.getActions());
		}
		return temp;
	}

	public abstract void execute(Player player);

	public void draw(SpriteBatch batch, boolean highlighted) {
		if (nextAction != null) {
			nextAction.draw(batch, highlighted);
		}
	}

	public void draw(ShapeRenderer renderer) {
		if (nextAction != null) {
			nextAction.draw(renderer);
		}
	}

}
