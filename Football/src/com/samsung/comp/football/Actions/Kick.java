package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.PlayerPositionData;
import com.samsung.comp.football.Players.Player;

public class Kick extends Action {

	private Ball ball;
	private Vector2 kickStartLocation;
	private Vector2 target;
	private static Texture targetTexture;
	private static Texture highlightedTexture;

	public Kick(Ball ball, Vector2 target, Vector2 kickStartLocation) {
		this.ball = ball;
		this.target = target;
		this.kickStartLocation = kickStartLocation;
	}

	@Override
	public void execute(Player player) {
		player.kick(ball, target);
	}

	public Vector2 getTarget() {
		return target;
	}

	public static void create(Texture texture, Texture highlighted) {
		targetTexture = texture;
		highlightedTexture = highlighted;
	}

	public static void dispose() {
		targetTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch, boolean highlighted) {
		if (highlighted) {
			batch.draw(highlightedTexture,
					target.x - (highlightedTexture.getHeight() / 2), target.y
							- (highlightedTexture.getWidth() / 2));
		} else {
			batch.draw(targetTexture, target.x
					- (targetTexture.getHeight() / 2), target.y
					- (targetTexture.getWidth() / 2));
		}

		super.draw(batch, highlighted);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		Vector2 fullLineVector = new Vector2(target.x - kickStartLocation.x,
				target.y
				- kickStartLocation.y);
		float fullLineDistance = fullLineVector.dst(Vector2.Zero);

		Vector2 partialLineVector = Utils.getMoveVector(kickStartLocation,
				target, 32);
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
	public PlayerPositionData getFuturePositionData(float time,
			Vector2 initialPosition, float speed, int positionInPath,
			boolean returnNulls) {
		if (nextAction != null) {
			return nextAction.getFuturePositionData(time, initialPosition,
					speed, 0, returnNulls);
		} else {
			return returnNulls ? null : new PlayerPositionData(initialPosition,
					-1);
		}
	}

	@Override
	public Vector2 getPosition() {
		return kickStartLocation;
	}
}
