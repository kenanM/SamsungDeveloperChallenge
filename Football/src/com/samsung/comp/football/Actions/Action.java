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

	public void clearSubsequentActions() {
		nextAction = null;
	}

	public void setNextAction(Action newAction) {
		if (nextAction == null) {
			nextAction = newAction;
		} else {
			nextAction.setNextAction(newAction);
		}

	}

	public Vector2 getFuturePosition() {
		if (nextAction == null) {
			return getPosition();
		} else {
			return nextAction.getFuturePosition();
		}
	}

	public abstract Vector2 getFuturePosition(float time, Vector2 initialPosition, float speed);

	public Vector2 getPosition() {
		return null;
	}

	public LinkedList<Action> getActions() {
		LinkedList<Action> temp = new LinkedList<Action>();
		if (nextAction != null) {
			temp.addAll(nextAction.getActions());
		}
		return temp;
	}

	public abstract void execute(Player player);

	public void draw(SpriteBatch batch) {
		if (nextAction != null) {
			nextAction.draw(batch);
		}
	}

	public void draw(ShapeRenderer renderer) {
		if (nextAction != null) {
			nextAction.draw(renderer);
		}
	}

}
