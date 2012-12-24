package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Stop;

public class Ball extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3523719737664937244L;
	private static Texture TEXTURE;
	private static final int BALL_SIZE = 8;
	private Player owner;
	private Action action;

	public Ball(float ballX, float ballY) {
		this.x = translateBallCoordinate(ballX);
		this.y = translateBallCoordinate(ballY);

		width = BALL_SIZE;
		height = BALL_SIZE;

		action = new Stop();
	}

	public void executeNextStep(float time) {
		if (hasOwner()) {
			// TODO The player class should have a function called
			// getBallLocation() so that it can put the ball in front of the
			// player
			this.x = owner.getX();
			this.y = owner.getY();
		} else {
			action.executeNextStep(time);
		}
	}

	public float getBallX() {
		return x + (BALL_SIZE / 2);
	}

	public float getBallY() {
		return y + (BALL_SIZE / 2);
	}

	// Takes a ball's x or y co-ordinate and translates it to drawable x or y
	public static float translateBallCoordinate(float c) {
		return c - (BALL_SIZE / 2);
	}

	public static void create(Texture texture) {
		TEXTURE = texture;
	}

	public Texture getTexture() {
		return TEXTURE;
	}

	public static void dispose() {
		TEXTURE.dispose();
	}

	public void setOwner(Player player) {
		this.owner = player;
		if(action instanceof Kick){
			((Kick) action).cancel();
		}
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public void removeOwner() {
		owner = null;
	}

	public void render(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(TEXTURE, this.x, this.y);
		batch.draw(TEXTURE, this.x, this.y, BALL_SIZE, BALL_SIZE);
	}

}
