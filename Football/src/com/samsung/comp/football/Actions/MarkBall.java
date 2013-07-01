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
	private static Texture highlightedTexture;

	public MarkBall(Vector2 startPoint, Ball ball) {
		this.startPoint = startPoint;
		this.ball = ball;
	}

	public static void create(Texture texture, Texture highlighted) {
		markTexture = texture;
		highlightedTexture = highlighted;
	}

	@Override
	public void execute(Player player) {
		player.followBall(ball);
	}

	public static void dispose() {
		markTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {
		if (highlighted) {
			batch.draw(highlightedTexture, ball.getBallX()
					- (highlightedTexture.getHeight() / 2), ball.getBallY()
					- (highlightedTexture.getWidth() / 2));
		} else {
			batch.draw(markTexture, ball.getBallX() - markTexture.getWidth()
					/ 2, ball.getBallY() - markTexture.getHeight() / 2);
		}
		super.draw(batch, highlighted);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(startPoint.x, startPoint.y,
				ball.getBallX(),
				ball.getBallY());
		super.draw(renderer);
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;

		Vector2 position = initialPosition;
		Vector2 target = ball.getBallPosition();

		while (distance > 0 && positionInPath == 0) {

			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				position.set(target);
				positionInPath++;

				// if reached end of path
				if (positionInPath != 0) {
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
