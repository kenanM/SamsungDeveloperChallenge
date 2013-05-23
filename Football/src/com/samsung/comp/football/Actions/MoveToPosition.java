package com.samsung.comp.football.Actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;

public class MoveToPosition extends Action {

	// This player would be the
	private Vector2 startPoint;
	private final Vector2 point;
	private static Texture TEXTURE;

	public MoveToPosition(Vector2 path, Vector2 startPoint) {
		this.point = path;
		this.startPoint = startPoint;
	}

	public static void create(Texture texture) {
		TEXTURE = texture;
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
	public void draw(SpriteBatch batch) {

		if (point != null && startPoint != null) {

			Vector2 firstPoint = startPoint;
			Vector2 secondPoint = point;

			float rotation = Utils.getMoveVector(firstPoint, secondPoint, 10)
					.angle();

			batch.draw(TEXTURE, secondPoint.x - (TEXTURE.getWidth() / 2),
					secondPoint.y - (TEXTURE.getWidth() / 2),
					TEXTURE.getWidth() / 2, TEXTURE.getHeight() / 2,
					TEXTURE.getWidth(), TEXTURE.getHeight(), 1, 1,
					rotation + 45 + 90, 0, 0, TEXTURE.getWidth(),
					TEXTURE.getHeight(), false, true);
		}
		super.draw(batch);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(startPoint.x, startPoint.y, point.x, point.y);
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
		Vector2[] path = { startPoint, point };
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

}

