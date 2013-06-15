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
	private Texture cancelIcon;
	private Texture cancelIconPressed;
	private String text;

	private Rectangle playIconRectangle;
	private Rectangle cancelIconRectangle;

	public enum Position {
		UP, DOWN
	};

	private int upYValue;
	/** The distance between a button and a border */
	private int xOffset = 25;
	private int velocity = 150;

	private Position position = Position.DOWN;

	private final AbstractGame game;
	private BitmapFont bmf;

	private float cancelActionsTimer = 0;
	private float textCountdownTimer = 3;

	public Bar(AbstractGame game) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create();
	}

	public void create() {
		bar = new Texture(Gdx.files.internal("bar.png"));
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		cancelIcon = new Texture(Gdx.files.internal("cancelIcon.png"));
		cancelIconPressed = new Texture(
				Gdx.files.internal("cancelIconDepressed.png"));

		upYValue = -2 * playIcon.getHeight();

		this.y = -playIcon.getHeight();
		this.x = 0;
		this.height = playIcon.getHeight();
		this.width = Game.VIRTUAL_SCREEN_WIDTH;

		playIconRectangle = new Rectangle(xOffset, this.y, playIcon.getWidth(),
				playIcon.getHeight());
		cancelIconRectangle = new Rectangle(Game.VIRTUAL_SCREEN_WIDTH
				- cancelIcon.getWidth() - xOffset, this.y,
				cancelIcon.getWidth(),
				cancelIcon.getHeight());
	}

	public void update(Float time) {

		if (position == Position.UP && playIconRectangle.y > upYValue) {
			playIconRectangle.y -= velocity * time;
		} else if (position == Position.DOWN
				&& playIconRectangle.y < -playIcon.getHeight()) {
			playIconRectangle.y += velocity * time;
		}

		if (playIconRectangle.y < upYValue) {
			playIconRectangle.y = upYValue;
		}
		if (playIconRectangle.y > this.y) {
			playIconRectangle.y = this.y;
		}

		cancelActionsTimer += time;
		textCountdownTimer += time;
	}

	public void draw(SpriteBatch batch) {
		batch.draw(bar, x, y);
		batch.draw(playIcon, playIconRectangle.x, playIconRectangle.y);

		if (isCancelButtonShown()) {
			if (cancelActionsTimer < 1) {
				batch.draw(cancelIconPressed, cancelIconRectangle.x,
						cancelIconRectangle.y);
			} else {
				batch.draw(cancelIcon, cancelIconRectangle.x,
						cancelIconRectangle.y);
			}
		}

		if (textCountdownTimer > 2) {
			text = "Red: " + game.getRedScore() + " Blue: "
					+ game.getBlueScore() + "      " + game.getRemainingTime();
		}
		bmf.draw(batch, text, playIcon.getWidth() + (xOffset * 4),
				7 - playIcon.getHeight());
	}

	//
	public void setPositionToUp() {
		position = Position.UP;
	}

	public void setPositionToDown() {
		position = Position.DOWN;
	}

	public void onPress(float x, float y) {
		if (playIconRectangle.contains(x, y)) {
			game.playButtonPressed();

		} else if (isCancelButtonShown() && cancelIconRectangle.contains(x, y)) {
			// TODO (Gavin): Change this to be 'undo last action'
			cancelActionsTimer = 0;
			game.getSelectedPlayer().clearActions();
		}
	}

	public void setText(String string) {
		text = string;
		textCountdownTimer = 0;
	}

	private boolean isCancelButtonShown() {
		return (game.getSelectedPlayer() != null && game.getSelectedPlayer()
				.getAction() != null);
	}
}
