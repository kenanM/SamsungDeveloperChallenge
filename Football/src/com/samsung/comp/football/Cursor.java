package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Cursor {
	protected Texture texture;
	protected Vector2 location = new Vector2(0, 0);
	protected boolean visible = false;

	public Cursor() {
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setLocation(float x, float y) {
		location.set(x, y);
	}

	public void setVisibility(boolean visible) {
		this.visible = visible;
	}

	public void draw(SpriteBatch batch) {
		if (texture != null && visible) {
			batch.draw(this.texture, location.x - texture.getWidth() / 2,
					location.y - texture.getHeight() / 2, texture.getWidth(),
					texture.getHeight(), 0, 0, texture.getWidth(),
					texture.getHeight(), false, false);

		}
	}

}
