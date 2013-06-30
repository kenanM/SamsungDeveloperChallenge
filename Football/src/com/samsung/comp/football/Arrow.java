package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Arrow extends Rectangle {
	protected Texture texture;
	protected float arrowX;
	protected float arrowY;
	protected Vector2 tip;

	public Arrow(Texture texture) {
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.texture = texture;
		this.tip = new Vector2(19, texture.getHeight());
	}

	public Arrow(Texture texture, float x, float y) {
		this(texture);
		pointAt(x, y);
	}

	public void draw(SpriteBatch batch) {
		batch.draw(texture, this.x, this.y, this.width, this.height, 0, 0,
				texture.getWidth(), texture.getHeight(), false, true);
	}

	public void pointAt(float x, float y) {
		arrowX = x;
		arrowY = y;
		this.x = x - tip.x;
		this.y = y - tip.y;
	}

}
