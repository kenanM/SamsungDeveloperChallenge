package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.PlayerPositionData;
import com.samsung.comp.football.Players.Player;

public class Mark extends Action implements MovementAction {

	private Player target;
	private Ball ball;
	private Vector2 startPoint;
	private static Texture markTexture;
	private static Texture highlightedTexture;

	public Mark(Vector2 startPoint, Player target, Ball ball) {
		this.startPoint = startPoint;
		this.target = target;
		this.ball = ball;
	}

	public Player getTarget() {
		return target;
	}

	public static void create(Texture texture, Texture highlighted) {
		markTexture = texture;
		highlightedTexture = highlighted;
	}

	@Override
	public void execute(Player player) {
		player.mark(target, ball);
	}

	public static void dispose() {
		markTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {
		if (highlighted) {
			batch.draw(highlightedTexture, target.getPlayerPosition().x
					- markTexture.getWidth() / 2, target.getPlayerPosition().y
					- markTexture.getHeight() / 2);
		} else {
			batch.draw(markTexture,
					target.getPlayerPosition().x - markTexture.getWidth() / 2,
					target.getPlayerPosition().y - markTexture.getHeight() / 2);
		}
		super.draw(batch, highlighted);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(startPoint.x, startPoint.y, target.getPlayerPosition().x,
				target.getPlayerPosition().y);
		super.draw(renderer);
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;

		Vector2 position = initialPosition;
		Vector2 target = this.target.getMarkPosition(ball);

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
	public PlayerPositionData getFuturePositionData(float time,
			Vector2 initialPosition, float speed, int positionInPath,
			boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;

		Vector2 position = initialPosition;
		Vector2 target = this.target.getMarkPosition(ball);
		float rotation = 0;

		while (distance > 0 && positionInPath == 0) {

			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				rotation = Utils.getMoveVector(position, target, distance)
						.angle();
				position.set(target);
				positionInPath++;

				// if reached end of path
				if (positionInPath != 0) {
					if (nextAction != null) {
						float remainingTime = distance / speed;
						return nextAction.getFuturePositionData(remainingTime,
								position, speed, 0, returnNulls);
					} else {
						return returnNulls ? null : new PlayerPositionData(
								position, Utils.getMoveVector(
										this.target.getPlayerPosition(),
										ball.getBallPosition(), distance)
										.angle());
					}
				}
			} else {
				// Move towards the next position (which is out of reach).
				Vector2 movement = Utils.getMoveVector(position, target,
						distance);
				position.add(movement);
				rotation = movement.angle();
				break;
			}

			// if (oldPosition.dst(position) > 0) {
			// this.stateTime += time;
			// this.currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		}

		return new PlayerPositionData(position, rotation);
	}

	@Override
	public Vector2 getPosition() {
		return target.getPlayerPosition();
	}

}
