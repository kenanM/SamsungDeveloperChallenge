package com.samsung.comp.football;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import events.TextAreaObserver;

public class NullTextArea extends TextArea {

	public NullTextArea() {
	}

	@Override
	public void attach(TextAreaObserver observer) {
	}

	@Override
	public void remove(TextAreaObserver observer) {
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
