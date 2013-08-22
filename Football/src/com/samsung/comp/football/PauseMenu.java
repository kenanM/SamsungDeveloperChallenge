package com.samsung.comp.football;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenu extends TextArea {

	private static Texture quitButtonTexture;
	private static Rectangle quitButton;

	public PauseMenu() {
		areaTexture = new Texture(Gdx.files.internal("pauseScreen.png"));
		quitButtonTexture = new Texture(Gdx.files.internal("quitGame.png"));
		quitButton = new Rectangle(Game.VIRTUAL_SCREEN_WIDTH / 2
				- quitButtonTexture.getWidth() / 2,
				Game.VIRTUAL_SCREEN_HEIGHT / 2, quitButtonTexture.getWidth(),
				quitButtonTexture.getHeight());

		this.x = Game.VIRTUAL_SCREEN_WIDTH / 2 - areaTexture.getWidth() / 2;
		this.y = Game.VIRTUAL_SCREEN_HEIGHT / 2 - areaTexture.getHeight() / 2;
		this.width = areaTexture.getWidth();
		this.height = areaTexture.getHeight();
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {
		batch.begin();
		batch.draw(areaTexture, x, y, width, height);
		batch.draw(quitButtonTexture, Game.VIRTUAL_SCREEN_WIDTH / 2
				- quitButtonTexture.getWidth() / 2,
				Game.VIRTUAL_SCREEN_HEIGHT / 2, quitButtonTexture.getWidth(),
				quitButtonTexture.getHeight());
		batch.end();
	}

	@Override
	public boolean onTouchDown(float x, float y) {

		Log.i("PauseMenu", x + ", " + y);

		if (quitButton.contains(x, y)) {
			Gdx.app.exit();
			// notifyObserver();
			return true;
		}
		return false;
	}
}
