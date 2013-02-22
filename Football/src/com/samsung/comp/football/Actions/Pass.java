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

	public Pass(Ball ball, Player kicker, Player target,
			Vector2 kickStartLocation) {
		this.ball = ball;
		this.kicker = kicker;
		this.target = target;
		this.kickStartLocation = kickStartLocation;
	}

	public static void create(Texture texture) {
		passIcon = texture;
	}

	@Override
	public void execute(Player player) {
		player.pass(ball, target);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(kickStartLocation.x, kickStartLocation.y,
				target.getPlayerPosition().x, target.getPlayerPosition().y);
		super.draw(renderer);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(passIcon, target.getPlayerPosition().x - passIcon.getWidth()
				/ 2, target.getPlayerPosition().y - passIcon.getHeight() / 2);
		super.draw(batch);
	}

	@Override
	public Vector2 getFuturePosition(float time, Vector2 initialPosition,
			float speed, int positionInPath, boolean returnNulls) {
		if (nextAction != null) {
			return nextAction.getFuturePosition(time, initialPosition, speed,
					0, returnNulls);
		} else {
			return initialPosition;
		}
	}

	@Override
	public Vector2 getPosition() {
		return kickStartLocation;
	}
}
