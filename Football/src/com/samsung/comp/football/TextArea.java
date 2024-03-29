package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.events.ButtonPressListener;


public class TextArea extends Rectangle {

	private static final long serialVersionUID = 1L;
	protected Texture areaTexture;
	protected String text = "NO TEXT";
	protected ButtonPressListener observer;

	public TextArea() {
		areaTexture = new Texture(Gdx.files.internal("textArea.png"));
		this.x = 0;
		this.y = Game.VIRTUAL_SCREEN_HEIGHT - areaTexture.getHeight();
		width = areaTexture.getWidth();
		height = areaTexture.getHeight();
	}

	public TextArea(float x, float y, float width, float height, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		areaTexture = new Texture(Gdx.files.internal("textArea.png"));
	}

	public void draw(SpriteBatch batch, BitmapFont bmf) {
		batch.draw(areaTexture, x, y, areaTexture.getWidth(),
				areaTexture.getHeight(), 0, 0, areaTexture.getWidth(),
				areaTexture.getHeight(), false, true);

		Color tempColor = bmf.getColor();
		bmf.setColor(Color.BLACK);
		bmf.drawWrapped(batch, text, (60), Game.VIRTUAL_SCREEN_HEIGHT - 978,
				600);
		bmf.setColor(tempColor);
	}

	public void onPress(float x, float y) {
		// if (this.contains(x, y)) {
		observer.onButtonPress();
		// }
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setListener(ButtonPressListener observer) {
		this.observer = observer;

	}

	public void clearListener() {
		this.observer = null;

	}
}
