package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TextArea extends Rectangle implements Observable {

	private static final long serialVersionUID = 1L;
	protected Texture areaTexture;
	protected String text = "NO TEXT";
	// normally a list of observers
	protected Observer observer;

	public TextArea() {
		areaTexture = new Texture(Gdx.files.internal("textArea.png"));
		this.x = Game.VIRTUAL_SCREEN_WIDTH / 2 - areaTexture.getWidth() / 2;
		this.y = Game.VIRTUAL_SCREEN_HEIGHT / 2 - areaTexture.getHeight() / 2;
		width = areaTexture.getWidth();
		height = areaTexture.getHeight();
	}

	public TextArea(Observer observer) {
		areaTexture = new Texture(Gdx.files.internal("textArea.png"));
		this.x = Game.VIRTUAL_SCREEN_WIDTH / 2 - areaTexture.getWidth() / 2;
		this.y = Game.VIRTUAL_SCREEN_HEIGHT / 2 - areaTexture.getHeight() / 2;
		width = areaTexture.getWidth();
		height = areaTexture.getHeight();
		this.observer = observer;
	}

	public TextArea(float x, float y, float width, float height, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		areaTexture = new Texture(Gdx.files.internal("textArea.png"));
	}

	@Override
	public void attach(Observer observer) {
		this.observer = observer;
	}

	@Override
	public void remove(Observer observer) {
		if (this.observer == observer) {
			observer = null;
		}
	}

	@Override
	public void notifyCanClose() {
		if (observer != null) {
			observer.onNotifyCanClose();
		}
	}

	public void draw(SpriteBatch batch, BitmapFont bmf) {
		batch.draw(areaTexture, x, y, areaTexture.getWidth(),
				areaTexture.getHeight());

		bmf.drawWrapped(batch, text, x + 30, y + 45, width);
	}

	public void onPress(float x, float y) {
		if (this.contains(x, y)) {
			notifyCanClose();
		}
	}

	public void setText(String text) {
		this.text = text;
	}
}
