package com.samsung.comp.football.Actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;

public class Move extends Action {

	private final Vector2[] path;
	private static Texture TEXTURE;
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

	public static void create(Texture texture) {
		// TODO this should override a super method.
		TEXTURE = texture;
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
	public void draw(SpriteBatch batch) {

		// Arbitrary number to ensure points aren't the same
		if (path != null && path.length > 5) {

			Vector2 firstPoint = path[path.length - 5];
			Vector2 secondPoint = path[path.length - 1];

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
}
