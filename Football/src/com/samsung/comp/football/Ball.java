package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Ball extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3523719737664937244L;
	private static final Texture TEXTURE = new Texture(
			Gdx.files.internal("ball.png"));
	private static final int BALL_SIZE = 8;
	private Player owner;
	private Player previousOwner;

	public Ball(float ballX, float ballY) {
		this.x = translateBallCoordinate(ballX);
		this.y = translateBallCoordinate(ballY);

		width = BALL_SIZE;
		height = BALL_SIZE;
	}
	
	public float getBallX() {
		return x + (BALL_SIZE/2);
	}
	
	public float getBallY() {
		return y + (BALL_SIZE/2);
	}
	
	// Takes a ball's x or y co-ordinate and translates it to drawable x or y
	public static float translateBallCoordinate(float c){
		return c - (BALL_SIZE/2);
	}

	public Texture getTexture() {
		return TEXTURE;
	}

	public void dispose() {
		TEXTURE.dispose();
	}

	public void setOwner(Player player) {
		if (hasOwner()) {
			this.previousOwner = owner;
		}
		this.owner = player;
	}

	public Player getOwner() {
		return owner;
	}

	public Player getPreviousOwner() {
		return previousOwner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean hasPreviousOwner() {
		return previousOwner != null;
	}

	public void removeOwner() {
		this.previousOwner = owner;
		owner = null;
	}

}
