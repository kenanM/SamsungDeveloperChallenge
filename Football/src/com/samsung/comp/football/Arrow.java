package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Followable;
import com.samsung.comp.precisionfootball.R;

public class Arrow extends Rectangle {
	protected Texture texture;
	protected Vector2 tip;
	protected Followable followable;

	/**
	 * Constructs an arrow assuming the tip is at the same point as the the
	 * default pointer (arrowOrange, (19, height)).
	 * 
	 * @param texture
	 *            the texture to use
	 * @param x
	 *            the x coordinate to point at
	 * @param y
	 *            the y coordinate to point at
	 */

	public Arrow(Texture texture) {
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.texture = texture;
		this.tip = new Vector2(19, texture.getHeight());
	}

	public Arrow(Texture texture, float x, float y, Vector2 tip) {
		this(texture);
		this.tip = tip;
		pointAt(x, y);
	}

	public Arrow(Texture texture, float x, float y, float tipX, float tipY) {
		this(texture, x, y, new Vector2(tipX, tipY));
	}

	public void setTip(float x, float y) {
		setTip(new Vector2(x, y));
	}

	public void setTip(Vector2 tip) {
		this.tip = tip;
	}

	public void follow(Followable followable) {
		this.followable = followable;
	}

	public void stopFollowing() {
		this.followable = null;
	}

	public void draw(SpriteBatch batch) {
		batch.draw(texture, this.x, this.y, this.width, this.height, 0, 0,
				texture.getWidth(), texture.getHeight(), false, true);
	}

	public void pointAt(float x, float y) {
		this.x = x - tip.x;
		this.y = y - tip.y;
	}

	public void update() {
		if (followable != null) {
			pointAt(followable.getPosition().x, followable.getPosition().y);
		}
	}

}
