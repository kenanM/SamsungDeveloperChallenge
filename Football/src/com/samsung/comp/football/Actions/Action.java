package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Action {

	protected Action nextAction;
	protected boolean complete = false;

	public boolean isComplete() {
		return complete;
	}

	public Action getNextAction() {
		return nextAction;
	}

	public abstract void executeNextStep(float time);

	public abstract void draw(SpriteBatch batch);

	public abstract void draw(ShapeRenderer renderer);

}
