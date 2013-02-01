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
		renderer.line(ball.x, ball.y, target.getPlayerPosition().x,
				target.getPlayerPosition().y);
		super.draw(renderer);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(passIcon, target.getPlayerPosition().x - passIcon.getWidth()
				/ 2, target.getPlayerPosition().y - passIcon.getHeight() / 2);
		super.draw(batch);
	}
}
