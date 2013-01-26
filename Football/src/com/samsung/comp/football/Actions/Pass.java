package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Players.Player;

public class Pass extends Action {

	private Ball ball;
	private Player target;
	private static Texture passIcon;

	public Pass(Ball ball, Player target) {
		this.ball = ball;
		this.target = target;
	}

	public static void create(Texture texture) {
		passIcon = texture;
	}

	@Override
	public void execute(Player player) {
		player.pass(ball, target);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(ball.x, ball.y, target.x, target.y);
		super.draw(renderer);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(passIcon, target.x - (passIcon.getHeight() / 2), target.y
				- (passIcon.getWidth() / 2));
		super.draw(batch);
	}
}
