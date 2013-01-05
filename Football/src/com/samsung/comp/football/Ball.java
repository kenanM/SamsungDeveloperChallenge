package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Utils;

public class Ball extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3523719737664937244L;
	private static Texture TEXTURE;
	private static final int BALL_SIZE = 8;
	private Player owner;
	private Vector2 velocity = new Vector2(0, 0);
	private float deceleration = 50;

	public Ball(float ballX, float ballY) {
		this.x = translateBallCoordinate(ballX);
		this.y = translateBallCoordinate(ballY);
		velocity = new Vector2(0, 0);

		width = BALL_SIZE;
		height = BALL_SIZE;
	}

	public float getBallX() {
		return x + (BALL_SIZE / 2);
	}

	public float getBallY() {
		return y + (BALL_SIZE / 2);
	}

	public Vector2 getBallPosition() {
		return new Vector2(getBallX(), getBallY());
	}

	/**
	 * Takes a balls position's x or y component and translates it to a drawable
	 * x or y component.
	 * 
	 * @param c
	 *            The ball position's x or y component.
	 * @return The drawable x or y component of the ball.
	 */
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

	public void draw(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(TEXTURE, this.x, this.y);
		batch.draw(TEXTURE, this.x, this.y, BALL_SIZE, BALL_SIZE);
	}

	public void move(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void update(float time) {

		if (hasOwner()) {
			// TODO The player class should have a function called
			// getBallLocation() so that it can put the ball in front of the
			// player
			this.x = owner.getX();
			this.y = owner.getY() - 15;
		} else {

			this.x = (this.x + ((velocity.x) * time));
			this.y = (this.y + ((velocity.y) * time));
			decelerate(time);
		}
	}

	private void decelerate(float time) {
		float newSpeed = Math.max(0, velocity.dst(Vector2.Zero)
				- (deceleration * time));
		velocity = Utils.getMoveVector(Vector2.Zero, velocity, newSpeed);
	}

}
