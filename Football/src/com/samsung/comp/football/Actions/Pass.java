package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Player;

public class Pass extends Action {

	private Ball ball;
	Player target;

	public Pass(Ball ball, Player target) {
		this.ball = ball;
		this.target = target;
	}

	@Override
	public void execute(Player player) {
		player.pass(ball, target);

	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(ball.x, ball.y, target.x, target.y);
	}

}
