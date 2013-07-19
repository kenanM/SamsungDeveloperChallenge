package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Players.Player;

public class Pass extends Action {

	private Ball ball;
	private Player target;
	private Player kicker;
	private Vector2 kickStartLocation;
	private static Texture passIcon;
	private static Texture highlightedTexture;

	public Pass(Ball ball, Player kicker, Player target,
			Vector2 kickStartLocation) {
		this.ball = ball;
		this.kicker = kicker;
		this.target = target;
		this.kickStartLocation = kickStartLocation;
	}

	public static void create(Texture texture, Texture highlighted) {
		passIcon = texture;
		highlightedTexture = highlighted;
	}

	@Override
	public void execute(Player player) {
		player.pass(ball, target);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		Vector2 fullLineVector = new Vector2(target.getPlayerPosition().x
				- kickStartLocation.x, target.getPlayerPosition().y
				- kickStartLocation.y);
		float fullLineDistance = fullLineVector.dst(Vector2.Zero);

		Vector2 partialLineVector = Utils.getMoveVector(kickStartLocation,
				target.getPlayerPosition(), 32);
		float partialLineDistance = partialLineVector.dst(Vector2.Zero);
		float distanceTravelled = 0f;

		Vector2 currentPosition = new Vector2(kickStartLocation);
		Vector2 targetPosition = kickStartLocation.cpy().add(partialLineVector);

		while (distanceTravelled < fullLineDistance - partialLineDistance) {

			renderer.line(currentPosition.x, currentPosition.y,
					targetPosition.x, targetPosition.y);

			currentPosition.add(partialLineVector);
			targetPosition.add(partialLineVector);
			distanceTravelled += partialLineDistance;

			currentPosition.add(partialLineVector);
			targetPosition.add(partialLineVector);
			distanceTravelled += partialLineDistance;
		}
		super.draw(renderer);
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {
		if (highlighted) {
			batch.draw(highlightedTexture, target.getPlayerPosition().x
					- passIcon.getWidth() / 2, target.getPlayerPosition().y
					- passIcon.getHeight() / 2);
		} else {
			batch.draw(passIcon,
					target.getPlayerPosition().x - passIcon.getWidth() / 2,
					target.getPlayerPosition().y - passIcon.getHeight() / 2);
		}
		super.draw(batch, highlighted);
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		if (nextAction != null) {
			return nextAction.getFuturePosition(time, initialPosition, speed,
					0, returnNulls);
		} else {
			return returnNulls ? null : initialPosition;
		}
	}

	@Override
	public Vector2 getPosition() {
		return kickStartLocation;
	}
}
