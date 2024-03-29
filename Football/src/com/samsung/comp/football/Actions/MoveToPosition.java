package com.samsung.comp.football.Actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.PlayerPositionData;
import com.samsung.comp.football.Players.Player;

public class MoveToPosition extends Action implements MovementAction {

	private Followable startPoint;
	private final Vector2 point;
	private static Texture TEXTURE;
	private static Texture highlightedTexture;

	public MoveToPosition(Vector2 path, Followable startPoint) {
		this.point = path;
		this.startPoint = startPoint;
	}

	public static void create(Texture texture, Texture highlighted) {
		TEXTURE = texture;
		highlightedTexture = highlighted;
	}

	public static void dispose() {
		TEXTURE.dispose();
	}

	@Override
	public void execute(Player player) {
		player.move(new Vector2[] { point });
	}

	public Vector2 getPoint() {
		return point;
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {

		if (point != null && startPoint != null) {

			Vector2 firstPoint = startPoint.getPosition();
			Vector2 secondPoint = point;

			float rotation = Utils.getMoveVector(firstPoint, secondPoint, 10)
					.angle();
			
			if (highlighted) {
				batch.draw(highlightedTexture, secondPoint.x
						- (highlightedTexture.getWidth() / 2), secondPoint.y
						- (highlightedTexture.getWidth() / 2),
						highlightedTexture.getWidth() / 2,
						highlightedTexture.getHeight() / 2,
						highlightedTexture.getWidth(),
						highlightedTexture.getHeight(), 1, 1,
						rotation + 45 + 90, 0, 0,
						highlightedTexture.getWidth(),
						highlightedTexture.getHeight(), false, true);
			} else {
				batch.draw(TEXTURE, secondPoint.x - (TEXTURE.getWidth() / 2),
						secondPoint.y - (TEXTURE.getWidth() / 2),
						TEXTURE.getWidth() / 2, TEXTURE.getHeight() / 2,
						TEXTURE.getWidth(), TEXTURE.getHeight(), 1, 1,
						rotation + 45 + 90, 0, 0, TEXTURE.getWidth(),
						TEXTURE.getHeight(), false, true);
			}
		}
		super.draw(batch, highlighted);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(startPoint.getPosition().x, startPoint.getPosition().y,
				point.x, point.y);
		super.draw(renderer);
	}

	@Override
	public Vector2 getFuturePosition() {
		if (nextAction != null) {
			Vector2 nextPosition = nextAction.getFuturePosition();
			if (nextPosition != null) {
				return nextPosition;
			}
		}
		return point;
	}

	@Override
	public Vector2 getPosition() {
		Log.v("Move", "getPosition called");
		return point;
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;

		Vector2 position = initialPosition;
		Vector2 target = point;

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
		Vector2 oldPosition = position.cpy();
		Vector2 target = point;
		float rotation = 0;

		while (distance > 0 && positionInPath == 0) {

			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				rotation = Utils.getMoveVector(oldPosition, target, distance)
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
								position, rotation);
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

}

