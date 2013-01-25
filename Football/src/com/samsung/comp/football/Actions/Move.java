package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Player;

public class Move extends Action {

	private final Vector2[] path;
	private static Texture TEXTURE;
	private float rotation = 0;

	public Move(Vector2[] path) {
		this.path = path;

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
		Vector2 lastPoint = path[path.length - 1];

		batch.draw(TEXTURE, lastPoint.x - TEXTURE.getWidth(), lastPoint.y
				- TEXTURE.getWidth() / 2, lastPoint.x, lastPoint.y,
				TEXTURE.getWidth(), TEXTURE.getHeight(), 1, 1, rotation, 0, 0,
				TEXTURE.getWidth(), TEXTURE.getHeight(), false, true);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		for (int i = 0; i < path.length - 1; i++) {
			renderer.line(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y);
		}
	}
}
