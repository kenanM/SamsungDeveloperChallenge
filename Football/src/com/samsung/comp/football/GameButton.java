package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * This is merely a rectangle with backgrounds and text. This does not hold a
 * set of listeners and events should be implemented via collision detection.
 */
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

		if (backgroundTexture != null) {
			batch.begin();
			batch.draw(backgroundTexture, x, y, width, height, 0, 0,
					backgroundTexture.getWidth(),
					backgroundTexture.getHeight(), false, true);
			batch.end();
		} else {
			Gdx.gl10.glEnable(GL10.GL_BLEND);
			renderer.begin(ShapeType.Filled);
			renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);
			renderer.rect(x, y, width, height);
			renderer.end();
			Gdx.gl10.glDisable(GL10.GL_BLEND);

			renderer.begin(ShapeType.Line);
			renderer.setColor(1, 1, 1, 1);
			renderer.rect(x, y, width, height);
			renderer.end();
		}
		if (text != null) {
			batch.begin();
			TextBounds bounds = bmf.getBounds(text);
			// Centralise text
			bmf.draw(batch, text, (x + width / 2) - (bounds.width / 2),
					(y + height / 2) - (bounds.height / 2));
			batch.end();
		}
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
