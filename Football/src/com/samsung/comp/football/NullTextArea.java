package com.samsung.comp.football;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class NullTextArea extends TextArea {

	public NullTextArea() {
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {
	}

	@Override
	public boolean onTouchDown(float x, float y) {
		return false;
	}
}
