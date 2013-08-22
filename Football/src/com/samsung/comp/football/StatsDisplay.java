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

	Texture statPoint1 = new Texture(Gdx.files.internal("statPointRed.png"));
	Texture statPoint2 = new Texture(Gdx.files.internal("statPoint.png"));
	Texture statPoint3 = new Texture(Gdx.files.internal("statPointGreen.png"));
	Texture statPoint4 = new Texture(Gdx.files.internal("statPointBlue.png"));
	Texture statPoint5 = new Texture(Gdx.files.internal("statPointPurple.png"));

	Texture statIcon = new Texture(Gdx.files.internal("statPointPurple.png"));

	public StatsDisplay(Player player, float x, float y, float width,
			float height, boolean flip) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.flip = flip;
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
		renderer.line(x, y, 0, x + width, y, 0);
		renderer.line(x, y, 0, x, y + height, 0);
		renderer.line(x + width, y, 0, x + width, y + height, 0);
		renderer.line(x, y + height, 0, x + width, y + height, 0);
		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		batch.begin();
		
		float height = 2 * bmf.getBounds(player.getName()).height;

		bmf.draw(batch, player.getName(), 0, y);

		for (int i = 0; i < player.getRunSpeedBarCount(); i++) {
			batch.draw(statPointImageFactory(i), x + statIcon.getWidth()
					+ (i * statPoint1.getWidth()),
					y + height +
					(0 * statPoint1.getHeight()), 0, 0, statPoint1.getWidth(),
					statPoint1.getHeight(), 1, 1, 0, 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), false, flip);
		}

		for (int i = 0; i < player.getShootSpeedBarCount(); i++) {
			batch.draw(statPointImageFactory(i), x + statIcon.getWidth()
					+ (i * statPoint1.getWidth()),
					y + height +
					(1 * statPoint1.getHeight()), 0, 0, statPoint1.getWidth(),
					statPoint1.getHeight(), 1, 1, 0, 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), false, flip);
		}

		for (int i = 0; i < player.getTackleSkillBarCount(); i++) {
			batch.draw(statPointImageFactory(i), x + statIcon.getWidth()
					+ (i * statPoint1.getWidth()),
					y + height +
					(2 * statPoint1.getHeight()), 0, 0, statPoint1.getWidth(),
					statPoint1.getHeight(), 1, 1, 0, 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), false, flip);
		}

		for (int i = 0; i < player.getSavingSkillBarCount(); i++) {
			batch.draw(statPointImageFactory(i), x + statIcon.getWidth()
					+ (i * statPoint1.getWidth()),
					y + height +
					(3 * statPoint1.getHeight()), 0, 0, statPoint1.getWidth(),
					statPoint1.getHeight(), 1, 1, 0, 0, 0,
					statPoint1.getWidth(), statPoint1.getHeight(), false, flip);
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
