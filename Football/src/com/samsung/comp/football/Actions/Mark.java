package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Player;

public class Mark extends Action {

	Player target;
	
	@Override
	public void execute(Player player) {
		player.mark(target);

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
