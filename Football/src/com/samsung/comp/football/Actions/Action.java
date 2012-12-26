package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Player;

public abstract class Action {

	protected Action nextAction;

	public Action getNextAction() {
		return nextAction;
	}

	public abstract void execute(Player player);

	public abstract void draw(SpriteBatch batch);

	public abstract void draw(ShapeRenderer renderer);

}
