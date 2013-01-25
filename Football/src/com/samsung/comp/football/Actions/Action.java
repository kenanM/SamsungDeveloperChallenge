package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Player;

public abstract class Action {

	protected Action nextAction;

	public Action getNextAction() {
		return nextAction;
	}
	
	public void clearSubsequentActions() {
		if (nextAction != null) {
			nextAction.clearSubsequentActions();
			nextAction = null;
		}
	}
	
	public void queueAction(Action newAction) {
		if (nextAction == null) {
			nextAction = newAction;
		}
		else {
			nextAction.queueAction(newAction);
		}
	}

	public abstract void execute(Player player);

	public abstract void draw(SpriteBatch batch);

	public abstract void draw(ShapeRenderer renderer);

}
