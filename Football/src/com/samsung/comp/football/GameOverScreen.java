package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class GameOverScreen extends TextArea {

	private static final float X_OFFSET = 30f;

	private AbstractGame game;
	private BitmapFont bmf;
	private List<GameButton> scoreInfoList;

	public GameOverScreen(AbstractGame game) {
		this.game = game;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/absender1.ttf"));
		bmf = generator.generateFont(35, FreeTypeFontGenerator.DEFAULT_CHARS,
				true);
		generator.dispose();
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont unusedBmf,
			ShapeRenderer renderer) {

		if (scoreInfoList == null) {
			populateScoreInfoList();
		} else {
			for (GameButton info : scoreInfoList) {
				info.draw(batch, bmf, renderer);
			}
		}
	}

	private void populateScoreInfoList() {
		List<String> finishData = game.getFinishData();
		TextBounds bounds;
		float drawHeight = 225;
		scoreInfoList = new ArrayList<GameButton>();

		for (String str : finishData) {
			bounds = bmf.getBounds(str);

			// Create 'buttons' with X_OFFSET space from both sides
			scoreInfoList.add(new GameButton(null, X_OFFSET, drawHeight,
					Game.VIRTUAL_SCREEN_WIDTH - 2 * X_OFFSET,
					bounds.height * 2, str));

			//
			drawHeight += bounds.height * 2 + 20;
		}
	}

	@Override
	public void onPress(float x, float y) {
	}

}
