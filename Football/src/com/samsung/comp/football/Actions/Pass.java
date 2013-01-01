package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Player;

public class Pass extends Action {
	
	Player target;

	@Override
	public void execute(Player player) {
		player.pass(target);

	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(ShapeRenderer renderer) {
		// TODO Auto-generated method stub

	}

}
