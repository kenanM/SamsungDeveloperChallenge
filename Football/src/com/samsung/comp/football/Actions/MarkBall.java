package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Players.Player;

public class MarkBall extends Action {
	private Vector2 startPoint;
	private Ball ball;
	private static Texture markTexture;

	public MarkBall(Vector2 startPoint, Ball ball) {
		this.startPoint = startPoint;
		this.ball = ball;
	}

	public static void create(Texture texture) {
		markTexture = texture;
	}

	@Override
	public void execute(Player player) {
		player.followBall(ball);
	}

	public static void dispose() {
		markTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(markTexture, ball.x - markTexture.getWidth() / 2, ball.y
				- markTexture.getHeight() / 2);
		super.draw(batch);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(startPoint.x, startPoint.y, startPoint.x, startPoint.y);
		super.draw(renderer);
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;
		Vector2 position = initialPosition;

		Vector2[] path = { startPoint };

		while (distance > 0 && path != null && path.length > 0
				&& positionInPath < path.length) {

			Vector2 target = path[positionInPath];
			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				position.set(target);
				positionInPath++;

				// if reached end of path
				if (positionInPath != 0 && positionInPath == path.length) {
					if (nextAction != null) {
						float remainingTime = distance / speed;
						return nextAction.getFuturePosition(remainingTime,
								position, speed, 0, returnNulls);
					} else {
						return returnNulls ? null : position;
					}
				}
			} else {
				// Move towards the next position (which is out of reach).
				Vector2 movement = Utils.getMoveVector(position, target,
						distance);
				position.add(movement);
				break;
			}
		}
		return position;
	}

	@Override
	public Vector2 getPosition() {
		return ball.getBallPosition();
	}

}
