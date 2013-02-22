package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.Players.Player;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Texture fadedBar;
	private Texture playIcon;
	private Texture fadedIcon;
	private Texture cancelIcon;
	private Texture cancelIconPressed;
	private String text;
	private Player selectedPlayer;

	public enum Position {
		UP, DOWN
	};

	private int upYValue;
	/** The distance between a button and a border */
	private int offset = 25;
	/** The 'X' coordinate of the cancel icon */
	private int cancelIconX;
	private int velocity = 150;

	private Position position = Position.DOWN;

	private final Game game;
	private BitmapFont bmf;

	private float fadeTimer = 0;
	private float cancelActionsTimer = 0;
	private float textCountdownTimer = 3;

	private boolean showingCancelButton = false;

	public Bar(Game game) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create();
	}

	public void create() {
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		fadedIcon = new Texture(Gdx.files.internal("playIcon_faded.png"));
		cancelIcon = new Texture(Gdx.files.internal("cancelIcon.png"));
		cancelIconPressed = new Texture(
				Gdx.files.internal("cancelIconDepressed.png"));
		bar = new Texture(Gdx.files.internal("bar.png"));
		fadedBar = new Texture(Gdx.files.internal("bar_faded.png"));
		cancelIconX = Game.VIRTUAL_SCREEN_WIDTH - cancelIcon.getWidth()
				- offset;
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
		cancelActionsTimer += time;
		textCountdownTimer += time;
	}

	public void draw(SpriteBatch batch) {
		if (fadeTimer > 0.2) {
			batch.draw(bar, 0, 0);
			batch.draw(playIcon, offset, y);
		} else {
			batch.draw(fadedBar, 0, 0);
			batch.draw(fadedIcon, offset, y);
		}

		if (showingCancelButton && fadeTimer > 0.2) {
			if (cancelActionsTimer < 1) {
				batch.draw(cancelIconPressed, cancelIconX, y);
			} else {
				batch.draw(cancelIcon, cancelIconX, y);
			}
		} else if (showingCancelButton) {
			batch.draw(fadedIcon, cancelIconX, y);
		}

		if (textCountdownTimer > 2) {
			text = "Red: " + game.getRedScore() + " Blue: "
					+ game.getBlueScore() + "      " + game.getRemainingTime();
		}
		bmf.draw(batch, text, (float) Game.VIRTUAL_SCREEN_WIDTH / 2 - 5, 15);
	}

	public void setPositionToUp() {
		position = Position.UP;
	}

	public void setPositionToDown() {
		position = Position.DOWN;
	}

	public Rectangle getPlayIcon() {
		return new Rectangle(offset, y, playIcon.getWidth(),
				playIcon.getHeight());
	}

	public Rectangle getCancelIcon() {
		return new Rectangle(cancelIconX, y, cancelIcon.getWidth(),
				cancelIcon.getHeight());
	}

	public void fade() {
		fadeTimer = 0;
	}

	public void setSelectedPlayer(Player player) {
		selectedPlayer = player;
		if (selectedPlayer != null) {
			showingCancelButton = true;
		} else {
			showingCancelButton = false;
		}
	}

	public boolean press(float x, float y) {
		if (showingCancelButton && getCancelIcon().contains(x, y)) {
			cancelActionsTimer = 0;
			return true;
		} else {
			return false;
		}
	}

	public void setText(String string) {
		text = string;
		textCountdownTimer = 0;
	}
}
