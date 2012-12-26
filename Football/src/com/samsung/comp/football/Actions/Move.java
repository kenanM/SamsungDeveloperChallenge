package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Player;

public class Move extends Action {

	Vector2[] path;
	int positionInPath;
	Player player;
	float velocity = 200;

	public Move(Player player, Vector2[] path) {
		this.player = player;
		this.path = path;
		this.nextAction = new Stop();
	}

	public Move(Player player, Vector2[] path, Action nextAction) {
		this.player = player;
		this.path = path;
		this.nextAction = nextAction;
	}

	@Override
	public void executeNextStep(float time) {
		player.move(path);
	}

	public Vector2[] getPath() {
		return path;
	}

	@Override
	public void draw(SpriteBatch batch) {
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		for (int i = 0; i < path.length - 1; i++) {
			renderer.line(path[i].x, path[i].y, path[i + 1].x,
					path[i + 1].y);
		}
	}
}
