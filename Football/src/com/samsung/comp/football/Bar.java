package com.samsung.comp.football;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.AbstractGame.GameState;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Texture fadedBar;
	private Texture playIcon;
	private Texture fadedIcon;
	private Texture cancelIcon;
	private Texture cancelIconPressed;
	private String text;

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

	private final AbstractGame game;
	private BitmapFont bmf;

	private float fadeTimer = 0;
	private float cancelActionsTimer = 0;
	private float textCountdownTimer = 3;

	public Bar(AbstractGame game) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create();
	}

	public void create() {
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		fadedIcon = new Texture(Gdx.files.internal("transparent_button.png"));
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

		if (isCancelButtonShown() && fadeTimer > 0.2) {
			if (cancelActionsTimer < 1) {
				batch.draw(cancelIconPressed, cancelIconX, y);
			} else {
				batch.draw(cancelIcon, cancelIconX, y);
			}
		} else if (isCancelButtonShown()) {
			batch.draw(fadedIcon, cancelIconX, y);
		}

		if (textCountdownTimer > 2) {
			text = "Red: " + game.getRedScore() + " Blue: "
					+ game.getBlueScore() + "      " + game.getRemainingTime();
		}
		bmf.draw(batch, text, playIcon.getWidth() + (offset * 4), 7);
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

	public void onPress(float x, float y) {
		if (getPlayIcon().contains(x, y)) {
			playButtonPressed();

		} else if (isCancelButtonShown() && getCancelIcon().contains(x, y)) {
			// TODO (Gavin): Change this to be 'undo last action'
			cancelActionsTimer = 0;
			game.getSelectedPlayer().clearActions();
		}
	}

	public void setText(String string) {
		text = string;
		textCountdownTimer = 0;
	}

	public void playButtonPressed() {
		if (game.getGameState() == GameState.PAUSED) {
			return;
		}

		// TODO: this implementation...ewww. Also can still hold via long runs.
		if (game.getHumanGoalie().hasBall()) {
			if (!game.getHumanGoalie().kicksBall()) {
				game.getBar().setText("Goalie cannot hold onto the ball");
				Log.i("Game", "Goalie needs to kick the ball");
				return;
			}
		}

		game.beginExecution();
	}

	private boolean isCancelButtonShown() {
		return (game.getSelectedPlayer() != null && game.getSelectedPlayer()
				.getAction() != null);
	}
}
