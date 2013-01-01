package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Player;

public class Kick extends Action {

	private Ball ball;
	private Vector2 target;
	private static Texture targetTexture;

	public Kick(Ball ball, Vector2 target) {
		this.ball = ball;
		this.target = target;
	}

	@Override
	public void execute(Player player) {
		player.kick(ball, target);
	}

	public Vector2 getTarget() {
		return target;
	}

	public static void create(Texture texture) {
		targetTexture = texture;
	}

	public static void dispose() {
		targetTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(targetTexture, target.x - (targetTexture.getHeight() / 2),
				target.y - (targetTexture.getWidth() / 2));
	}

	@Override
	public void draw(ShapeRenderer renderer) {
	}

}
