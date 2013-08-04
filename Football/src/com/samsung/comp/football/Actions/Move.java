package com.samsung.comp.football.Actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.PlayerPositionData;
import com.samsung.comp.football.Players.Player;

public class Move extends Action implements MovementAction {

	private final Vector2[] path;
	private static Texture TEXTURE;
	private static Texture highlightedTexture;
	// TODO: remove this & references from attributes list
	private float rotation = 0;

	public Move(Vector2[] path) {
		this.path = path;
		if (path.length == 0) {
			throw new UnsupportedOperationException();
		}

		if (path.length > 1) {
			rotation = getRotation();
		}
	}

	public Move(Vector2[] path, Action nextAction) {
		this.path = path;
		this.nextAction = nextAction;

		if (path.length > 1) {
			rotation = getRotation();
		}
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
		player.move(path);
	}

	public Vector2[] getPath() {
		return path;
	}

	private float getRotation() {
		return Utils.getMoveVector(path[path.length - 2],
				path[path.length - 1], 1).angle();
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {

		// Arbitrary number to ensure points aren't the same

		Vector2 firstPoint = Utils.getLastDifferentPoint(path);
			Vector2 secondPoint = path[path.length - 1];

		float rotation = new Vector2(secondPoint.x - firstPoint.x,
				secondPoint.y - firstPoint.y).angle();

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

		super.draw(batch, highlighted);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		for (int i = 0; i < path.length - 1; i++) {
			renderer.line(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y);
		}
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
		return path[path.length - 1];
	}

	@Override
	public Vector2 getPosition() {
		Log.v("Move", "getPosition called");
		return path[path.length - 1];
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {

		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;
		Vector2 position = initialPosition;

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
	public PlayerPositionData getFuturePositionData(float time,
			Vector2 initialPosition, float speed, int positionInPath,
			boolean returnNulls) {
		float distance = speed * time;
		positionInPath = (positionInPath < 0) ? 0 : positionInPath;
		Vector2 position = initialPosition;
		float rotation = 0;

		while (distance > 0 && path != null && path.length > 0
				&& positionInPath < path.length) {

			Vector2 target = path[positionInPath];
			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				rotation = Utils.getMoveVector(position, target, distance)
						.angle();
				position.set(target);
				positionInPath++;

				// if reached end of path
				if (positionInPath != 0 && positionInPath == path.length) {
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

			// if (initialPosition.dst(position) > 0) {
			// this.stateTime += time;
			// this.currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		}

		return new PlayerPositionData(position, rotation);
	}

}
