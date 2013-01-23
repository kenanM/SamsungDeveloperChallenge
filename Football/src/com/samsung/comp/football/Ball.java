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
	private static final int BALL_SIZE = 12;
	private Player owner;
	private Vector2 velocity = new Vector2(0, 0);
	private float deceleration = 50;
	private float timeSinceTackle = Game.BALL_CHANGE_TIME;

	public Ball(float ballX, float ballY) {
		this.x = translateBallCoordinate(ballX);
		this.y = translateBallCoordinate(ballY);
		velocity = new Vector2(0, 0);

		width = BALL_SIZE;
		height = BALL_SIZE;
	}

	public float getDeceleration() {
		return deceleration;
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
		if (hasOwner()) {
			removeOwner();
		}
		this.owner = player;
		player.setBall(this);
	}

	public void removeOwner() {
		owner.removeBall();
		owner = null;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public float getTimeSinceTackle() {
		return timeSinceTackle;
	}

	public void clearTimeSinceTackle() {
		this.timeSinceTackle = 0;
	}

	public void resetTimeSinceTackle() {
		this.timeSinceTackle = Game.BALL_CHANGE_TIME;
	}

	public void draw(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(TEXTURE, this.x, this.y);
		batch.draw(TEXTURE, this.x, this.y, BALL_SIZE, BALL_SIZE);
	}

	public void move(Vector2 velocity) {
		this.velocity = velocity;
	}

	/**
	 * Bounce the ball off the wall when it exceeds the rectangle formed by 0,0
	 * and maxWidth,MaxHeight. Inverts the velocity according to the wall it
	 * collides with then adjusts the speed by the elasticity.
	 * 
	 * @param maxWidth
	 *            The right edge of the field.
	 * @param maxHeight
	 *            The bottom edge of the field.
	 * @param bounceElasticty
	 *            The factor to multiply the ball velocity by.
	 */
	public void ballBounceDetection(int maxWidth, int maxHeight,
			float bounceElasticty) {
		if (getBallPosition().x < 0) {
			x = translateBallCoordinate(0);
			velocity.x = velocity.x * (-1) * bounceElasticty;
		} else if (getBallPosition().x > maxWidth) {
			x = translateBallCoordinate(maxWidth);
			velocity.x = velocity.x * (-1) * bounceElasticty;
		}
		if (getBallPosition().y < 0) {
			y = translateBallCoordinate(0);
			velocity.y = velocity.y * (-1) * bounceElasticty;

		} else if (getBallPosition().y > maxHeight) {
			y = translateBallCoordinate(maxHeight);
			velocity.y = velocity.y * (-1) * bounceElasticty;
		}
	}

	public void update(float time) {

		timeSinceTackle = timeSinceTackle + time;

		if (hasOwner()) {
			Vector2 ballVector = owner.getBallPosition();
			this.x = ballVector.x;
			this.y = ballVector.y;
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
