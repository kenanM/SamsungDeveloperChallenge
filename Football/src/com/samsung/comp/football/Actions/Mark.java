package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;

public class Mark extends Action {

	private Player target;
	private Player player;
	private static Texture markTexture;

	public Mark(Player player, Player target) {
		this.player = player;
		this.target = target;
	}

	public Player getTarget() {
		return target;
	}

	public static void create(Texture texture) {
		markTexture = texture;
	}

	@Override
	public void execute(Player player) {
		player.mark(target);
	}

	public static void dispose() {
		markTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(markTexture,
				target.getPlayerPosition().x - markTexture.getWidth() / 2,
				target.getPlayerPosition().y - markTexture.getHeight() / 2);
		super.draw(batch);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(player.getPlayerPosition().x,
				player.getPlayerPosition().y, target.getPlayerPosition().x,
				target.getPlayerPosition().y);
		super.draw(renderer);
	}
	
	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed) {
		float distance = speed * time;
		int positionInPath = 0;
		Vector2 position = initialPosition;
		
		Vector2[] path = new Vector2[]{target.getPlayerPosition()};

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
								position, speed);
					} else {
						return null;
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
