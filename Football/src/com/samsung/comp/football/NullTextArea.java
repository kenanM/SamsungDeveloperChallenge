package com.samsung.comp.football;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NullTextArea extends TextArea {

	public NullTextArea() {
	}

	@Override
	public void attach(Observer observer) {
	}

	@Override
	public void remove(Observer observer) {
	}

	@Override
	public void notifyCanClose() {
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf) {
	}

	@Override
	public void onPress(float x, float y) {
	}
}
