package com.samsung.comp.football;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.BallOwnerSetListener;
import com.samsung.comp.football.Actions.Followable;
import com.samsung.comp.football.Actions.Utils;
import com.samsung.comp.football.Players.Player;

public class Ball extends Rectangle implements Followable {

	private static final long serialVersionUID = -3523719737664937244L;

	private static Animation animation;
	private static TextureRegion texture;
	private static Texture animationSheet;
	private static Texture hoverTexture;
	private float stateTime = 0;
	private static final int FRAMES = 4;

	private static final int BALL_SIZE = 12;
	private static final int HOVER_SIZE = 64;

	private Player owner;
	private Vector2 velocity = new Vector2(0, 0);
	private float deceleration = 125;

	private ArrayList<BallOwnerSetListener> listeners = new ArrayList<BallOwnerSetListener>();

	public Ball(float ballX, float ballY) {
		this.x = translateBallCoordinate(ballX);
		this.y = translateBallCoordinate(ballY);
		velocity = new Vector2(0, 0);

		width = BALL_SIZE;
		height = BALL_SIZE;
	}

	@Override
	public Vector2 getPosition() {
		return getBallPosition();
	}

	public boolean addBallOwnerSetListener(BallOwnerSetListener listener) {
		return listeners.add(listener);
	}

	public boolean removeBallOwnerSetListener(BallOwnerSetListener listener) {
		return listeners.remove(listener);
	}

	public float getDeceleration() {
		return deceleration;
	}

	public static float translateHoverCoordinate(float c) {
		return c - (HOVER_SIZE / 2);
	}

	public static int getBallSize() {
		return BALL_SIZE;
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

	public static void create() {
		animationSheet = new Texture(Gdx.files.internal("ballAnimation.png"));
		animation = new Animation(0.10f, Utils.createTextureRegion(
				animationSheet, FRAMES));
		texture = animation.getKeyFrame(0);
		hoverTexture = new Texture(Gdx.files.internal("ballHover.png"));
	}

	public static void dispose() {
		animationSheet.dispose();
		hoverTexture.dispose();
	}

	public void setOwner(Player player) {
		if (hasOwner()) {
			removeOwner();
		}

		this.owner = player;
		player.setBall(this);

		for (BallOwnerSetListener listener : listeners) {
			listener.onBallOwnerSet(this, player);
		}
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

	public float getSpeed() {
		return Vector2.Zero.dst(velocity);
	}

	public void resetBall() {
		if (hasOwner()) {
			removeOwner();
		}
		velocity = new Vector2(0, 0);
	}

	public Texture getHighlightTexture() {
		return hoverTexture;
	}

	public void draw(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(TEXTURE, this.x, this.y);
		batch.draw(texture, this.x, this.y, BALL_SIZE, BALL_SIZE);
	}

	public void drawHighlight(SpriteBatch batch) {
		batch.draw(this.getHighlightTexture(),
				translateHoverCoordinate(getBallPosition().x),
				translateHoverCoordinate(getBallPosition().y));
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

		Vector2 start = new Vector2(x, y);
		if (hasOwner()) {
			Vector2 ballVector = owner.getBallPosition();
			this.x = translateBallCoordinate(ballVector.x);
			this.y = translateBallCoordinate(ballVector.y);
		} else {
			this.x = (this.x + ((velocity.x) * time));
			this.y = (this.y + ((velocity.y) * time));
			decelerate(time);
		}

		// If the ball has moved update the texture
		if (this.x != start.x || this.y != start.y) {
			stateTime += time;
			texture = animation.getKeyFrame(stateTime, true);
		}

		// restrictToField();
	}

	private void decelerate(float time) {
		float newSpeed = Math.max(0, velocity.dst(Vector2.Zero)
				- (deceleration * time));
		velocity = Utils.getMoveVector(Vector2.Zero, velocity, newSpeed);
	}

	private void restrictToField() {
		this.x = this.x > (Game.VIRTUAL_SCREEN_WIDTH - BALL_SIZE) ? Game.VIRTUAL_SCREEN_WIDTH
				- BALL_SIZE
				: this.x;
		this.y = this.y > (Game.VIRTUAL_SCREEN_HEIGHT - BALL_SIZE) ? Game.VIRTUAL_SCREEN_HEIGHT
				- BALL_SIZE
				: this.y;

		this.x = this.x < (0 + BALL_SIZE) ? 0 + BALL_SIZE : this.x;
		this.y = this.y < (0 + BALL_SIZE) ? 0 + BALL_SIZE : this.y;
	}

}
