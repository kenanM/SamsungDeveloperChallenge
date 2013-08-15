package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameButton extends Rectangle {

	private Texture backgroundTexture;
	private String text;

	public GameButton(Texture texture, float x, float y, float width, float height) {
		this.backgroundTexture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public GameButton(Texture texture, float x, float y, float width, float height,
			String text) {
		this(texture, x, y, width, height);
		this.text = text;
	}
	
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {
		batch.begin();
		if (backgroundTexture != null) {
			batch.draw(backgroundTexture, x, y, width, height, 0, 0,
					backgroundTexture.getWidth(),
					backgroundTexture.getHeight(), false, true);
		}
		if (text != null) {
			TextBounds bounds = bmf.getBounds(text);
			bmf.draw(batch, text, x, y);
		}
		batch.end();
	}
	
	

}
