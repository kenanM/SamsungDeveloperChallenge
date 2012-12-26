package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;

public class Kick extends Action {

	private Ball ball;
	private Vector2 target;
	private float speed = 180;
	private float deceleration = 10;
	private static Texture targetTexture;

	public Kick(Ball ball, Vector2 target) {
		this.ball = ball;
		this.target = target;
		// TODO do this better
		ball.removeOwner();
	}

	@Override
	public void executeNextStep(float time) {

		if (complete) {
			return;
		}

		Vector2 position = new Vector2(ball.x, ball.y);
		float distance = time * speed;
		speed = Math.max(0, speed - (time * deceleration));

		// If the target is within range of our current position just move to
		// the target.
		if (position.dst(target) < distance) {
			ball.x = target.x;
			ball.y = target.y;
			complete = true;
			return;

		} else {

			Vector2 movement = Utils.getMoveVector(position, target, distance);
			position.add(movement);

			ball.x = position.x;
			ball.y = position.y;
		}
	}

	public void cancel() {
		complete = true;
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
