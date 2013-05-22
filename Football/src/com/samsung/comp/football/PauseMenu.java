package com.samsung.comp.football;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenu {

	private static Texture pauseTexture;
	private static Texture quitGameTexture;
	private AbstractGame game;
	private static Rectangle quitButton;

	public PauseMenu(AbstractGame game) {
		this.game = game;
		pauseTexture = new Texture(Gdx.files.internal("pauseScreen.png"));
		quitGameTexture = new Texture(Gdx.files.internal("quitGame.png"));
		quitButton = new Rectangle(Game.VIRTUAL_SCREEN_WIDTH / 2
				- quitGameTexture.getWidth() / 2,
				Game.VIRTUAL_SCREEN_HEIGHT / 2, quitGameTexture.getWidth(),
				quitGameTexture.getHeight());
	}

	public void draw(SpriteBatch batch) {
		batch.draw(pauseTexture,
				Game.VIRTUAL_SCREEN_WIDTH / 2 - pauseTexture.getWidth() / 2,
				Game.VIRTUAL_SCREEN_HEIGHT / 2 - pauseTexture.getHeight() / 2,
				pauseTexture.getWidth(), pauseTexture.getHeight());
		batch.draw(quitGameTexture, Game.VIRTUAL_SCREEN_WIDTH / 2
				- quitGameTexture.getWidth() / 2,
				Game.VIRTUAL_SCREEN_HEIGHT / 2, quitGameTexture.getWidth(),
				quitGameTexture.getHeight());
	}

	public void onPress(float x, float y) {

		Log.i("PauseMenu", x + ", " + y);

		if (quitButton.contains(x, y)) {
			Gdx.app.exit();
		}
	}
}
