package com.samsung.comp.football.Actions;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;

public class Kick extends Action {

	private static final String TAG = "ball";

	private Ball ball;
	private Vector2 target;
	private float velocity = 30;

	public Kick(Ball ball, int targetX, int targetY) {
		this.ball = ball;
		this.target = new Vector2(targetX, targetY);
	}

	@Override
	public void executeNextStep(float time) {

		if (complete) {
			return;
		}

		Vector2 position = new Vector2(ball.x, ball.y);
		float distance = time * velocity;

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

}
