package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Texture fadedBar;
	private Texture playIcon;
	private Texture fadedPlayIcon;
	private String text;

	public enum Position {
		UP, DOWN
	};

	private int upYValue;
	private int xOffset = 25;

	private int velocity = 150;

	private Position position = Position.DOWN;

	private final Game game;
	private BitmapFont bmf;

	private float fadeTimer = 0;

	public Bar(Game game) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create();
	}

	public void create() {
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		fadedPlayIcon = new Texture(Gdx.files.internal("playIcon_faded.png"));
		bar = new Texture(Gdx.files.internal("bar.png"));
		fadedBar = new Texture(Gdx.files.internal("bar_faded.png"));
		upYValue = -playIcon.getHeight();
		this.y = upYValue;
		this.x = 0;

		this.height = playIcon.getHeight();
		this.width = bar.getWidth();
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
		fadeTimer += time;
	}

	public void draw(SpriteBatch batch) {
		if (fadeTimer > 0.2) {
			batch.draw(bar, 0, 0);
			batch.draw(playIcon, xOffset, y);
		} else {
			batch.draw(fadedBar, 0, 0);
			batch.draw(fadedPlayIcon, xOffset, y);
		}

		text = "Red: " + game.getRedScore() + " Blue: " + game.getBlueScore()
				+ "  " + game.getRemainingTime();
		bmf.draw(batch, text, (float) Game.VIRTUAL_SCREEN_WIDTH / 2 - 5, 15);
	}

	public void setPositionToUp() {
		position = Position.UP;
	}

	public void setPositionToDown() {
		position = Position.DOWN;
	}

	public Rectangle getPlayIcon() {
		return new Rectangle(xOffset, y, playIcon.getWidth(),
				playIcon.getHeight());
	}

	public void fade() {
		fadeTimer = 0;
	}
}
