package com.samsung.comp.football;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class GameOverScreen extends TextArea {

	AbstractGame game;



	public GameOverScreen(AbstractGame game) {
		this.game = game;
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {

	}

	@Override
	public void onPress(float x, float y) {
	}

}
