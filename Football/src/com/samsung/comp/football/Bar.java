package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Texture playIcon;

	public enum Position {
		UP, DOWN
	};

	private int upYValue;
	private int velocity = 150;

	private Position position = Position.DOWN;

	private final Game game;
	private BitmapFont bmf;

	public Bar(Game game) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create();
	}

	public void create() {
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		bar = new Texture(Gdx.files.internal("bar.png"));
		upYValue = -(Math.max(playIcon.getHeight(), bar.getHeight()));
		this.y = upYValue;
	}

	public void update(Float time) {
		if (position == Position.UP && this.y > upYValue) {
			this.y -= velocity * time;
		} else if (position == Position.DOWN && this.y < 0) {
			this.y += velocity * time;
		}

		if (this.y < upYValue) {
			y = upYValue;
		}
		if (this.y > 0) {
			y = 0;
		}

	}

	public void draw(SpriteBatch batch) {
		batch.draw(bar, 0, 0);
		batch.draw(playIcon, 25, y);

		String text = "Red: " + game.getRedScore() + " Blue: "
				+ game.getBlueScore() + "  " + game.getRemainingTime();
		bmf.draw(batch, text, (float) Game.VIRTUAL_SCREEN_WIDTH / 2 - 5, 15);
	}

	public void setPositionToUp() {
		position = Position.UP;
	}

	public void setPositionToDown() {
		position = Position.DOWN;
	}
}
