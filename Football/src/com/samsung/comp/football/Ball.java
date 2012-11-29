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

	public Ball(int x, int y) {
		this.x = x;
		this.y = y;

		width = BALL_SIZE;
		height = BALL_SIZE;
	}

	public Texture getTexture() {
		return TEXTURE;
	}

	public void dispose() {
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
	
	public void removeOwner(){
		owner=null;
	}

}
