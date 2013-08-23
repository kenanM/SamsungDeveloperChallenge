package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.Players.Player;

public class StatsDisplay extends Rectangle {

	private Player player;
	private boolean flip = false;
	private final float xOffset = 8;

	Texture statPoint1 = new Texture(Gdx.files.internal("statPointRed.png"));
	Texture statPoint2 = new Texture(Gdx.files.internal("statPoint.png"));
	Texture statPoint3 = new Texture(Gdx.files.internal("statPointGreen.png"));
	Texture statPoint4 = new Texture(Gdx.files.internal("statPointBlue.png"));
	Texture statPoint5 = new Texture(Gdx.files.internal("statPointPurple.png"));

	Texture statRunIcon = new Texture(Gdx.files.internal("icons/runIcon.png"));
	Texture statTackleIcon = new Texture(
			Gdx.files.internal("icons/tackleIcon.png"));
	Texture statShootIcon = new Texture(
			Gdx.files.internal("icons/shootIcon.png"));
	Texture statSavingIcon = new Texture(
			Gdx.files.internal("icons/timerIcon.png"));

	public StatsDisplay(Player player, float x, float y, float width,
			float height, boolean flip) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.flip = flip;
		
		statPoint1 = new Texture(Gdx.files.internal("statPointRed.png"));
		statPoint2 = new Texture(Gdx.files.internal("statPoint.png"));
		statPoint3 = new Texture(Gdx.files.internal("statPointGreen.png"));
		statPoint4 = new Texture(Gdx.files.internal("statPointBlue.png"));
		statPoint5 = new Texture(Gdx.files.internal("statPointPurple.png"));
	}

	public StatsDisplay(Player player, float x, float y, float width,
			float height, boolean flip, Texture statPoint1, Texture statPoint2,
			Texture statPoint3, Texture statPoint4, Texture statPoint5,
			Texture statRunIcon, Texture statShootIcon, Texture statTackleIcon,
			Texture statSavingIcon) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.flip = flip;

		this.statPoint1 = statPoint1;
		this.statPoint2 = statPoint2;
		this.statPoint3 = statPoint3;
		this.statPoint4 = statPoint4;
		this.statPoint5 = statPoint5;

		this.statRunIcon = statRunIcon;
		this.statShootIcon = statShootIcon;
		this.statTackleIcon = statTackleIcon;
		this.statSavingIcon = statSavingIcon;
	}

	private float flippedY(float val) {
		return !flip ? y : height - y;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {

		Gdx.gl.glEnable(GL10.GL_BLEND);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);
		renderer.rect(x, y, width, height);
		renderer.end();

		renderer.begin(ShapeType.Line);
		renderer.setColor(0f, 0f, 0f, 1f);
		renderer.rect(x, y, width, height);
		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		if (player == null) {
			return;
		}

		batch.begin();
		
		float textHeight = 2 * bmf.getBounds(player.getName()).height;

		bmf.draw(batch, player.getName(), x + xOffset, y + 5);

		String costString = "Player Cost: " + player.getPlayerCost();
		bmf.draw(batch, costString, x + xOffset, y + 5 + textHeight);

		batch.draw(statRunIcon, x + xOffset, y + 2 * textHeight, 0, 0,
				statRunIcon.getWidth(), statRunIcon.getHeight(), 1, 1, 0, 0, 0,
				statRunIcon.getWidth(), statRunIcon.getHeight(), false, flip);

		batch.draw(statShootIcon, x + xOffset, y + 2 * textHeight
				+ statSavingIcon.getHeight(), 0, 0, statShootIcon.getWidth(),
				statShootIcon.getHeight(), 1, 1, 0, 0, 0,
				statShootIcon.getWidth(), statShootIcon.getHeight(), false,
				flip);

		batch.draw(statTackleIcon, x + xOffset, y + 2 * textHeight + 2
				* statSavingIcon.getHeight(), 0, 0, statTackleIcon.getWidth(),
				statTackleIcon.getHeight(), 1, 1, 0, 0, 0,
				statTackleIcon.getWidth(), statTackleIcon.getHeight(), false,
				flip);

		batch.draw(statSavingIcon, x + xOffset, y + 2 * textHeight + 3
				* +statSavingIcon.getHeight(), 0, 0, statSavingIcon.getWidth(),
				statSavingIcon.getHeight(), 1, 1, 0, 0, 0,
				statSavingIcon.getWidth(), statSavingIcon.getHeight(), false,
				flip);

		for (int i = 0; i < player.getRunSpeedBarCount(); i++) {
			batch.draw(
					statPointImageFactory(i),
					x + xOffset + statRunIcon.getWidth()
							+ (i * statPoint1.getWidth()), y + 2 * textHeight
							+ (0 * statPoint1.getHeight()), 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), 1, 1, 0, 0,
					0, statPoint1.getWidth(), statPoint1.getHeight(), false,
					flip);
		}

		for (int i = 0; i < player.getShootSpeedBarCount(); i++) {
			batch.draw(
					statPointImageFactory(i),
					x + xOffset + statRunIcon.getWidth()
							+ (i * statPoint1.getWidth()), y + 2 * textHeight
							+ (1 * statPoint1.getHeight()), 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), 1, 1, 0, 0,
					0, statPoint1.getWidth(), statPoint1.getHeight(), false,
					flip);
		}

		for (int i = 0; i < player.getTackleSkillBarCount(); i++) {
			batch.draw(
					statPointImageFactory(i),
					x + xOffset + statRunIcon.getWidth()
							+ (i * statPoint1.getWidth()), y + 2 * textHeight
							+ (2 * statPoint1.getHeight()), 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), 1, 1, 0, 0,
					0, statPoint1.getWidth(), statPoint1.getHeight(), false,
					flip);
		}

		for (int i = 0; i < player.getSavingSkillBarCount(); i++) {
			batch.draw(
					statPointImageFactory(i),
					x + xOffset + statRunIcon.getWidth()
							+ (i * statPoint1.getWidth()), y + 2 * textHeight
							+ (3 * statPoint1.getHeight()), 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), 1, 1, 0, 0,
					0, statPoint1.getWidth(), statPoint1.getHeight(), false,
					flip);
		}

		batch.end();

	}

	private Texture statPointImageFactory(int statPoints) {
		int i = statPoints / 5;
		switch (i) {
		case (0):
			return statPoint1;
		case (1):
			return statPoint2;
		case (2):
			return statPoint3;
		case (3):
			return statPoint4;
		case (4):
			return statPoint5;
		default:
			return statPoint1;
		}
	}


}
