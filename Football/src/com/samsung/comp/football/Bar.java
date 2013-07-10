package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.AbstractGame.GameState;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Color barColor;
	private Texture playIcon;
	private Texture cancelIcon;
	private Texture cancelIconPressed;
	private Texture undoIcon;
	private String text;

	private Rectangle playIconRectangle;
	private Rectangle cancelIconRectangle;
	private Rectangle undoIconRectangle;

	public enum Position {
		UP, DOWN
	};

	private float upYValue;
	/** The distance between a button and a border */
	private int xOffset = 25;
	private int velocity = 150;
	// private float playIconMoveDistance = 2 * playIcon.getHeight();

	private boolean positionedAtTop = true;
	private Position position = Position.DOWN;

	private final AbstractGame game;
	private BitmapFont bmf;

	private float cancelActionsTimer = 0;
	private float textCountdownTimer = 3;

	public Bar(AbstractGame game) {
		this(game, true);
	}

	public Bar(AbstractGame game, boolean topOfScreen) {
		this.game = game;
		bmf = new BitmapFont(true);
		bmf.scale(.35f);
		create(topOfScreen);
	}

	public void create(boolean topOfScreen) {
		bar = new Texture(Gdx.files.internal("bar.png"));
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		undoIcon = new Texture(Gdx.files.internal("undoIcon.png"));
		cancelIcon = new Texture(Gdx.files.internal("cancelIcon.png"));
		cancelIconPressed = new Texture(
				Gdx.files.internal("cancelIconDepressed.png"));

		positionedAtTop = topOfScreen;

		this.y = positionedAtTop ? -64
				: Game.VIRTUAL_SCREEN_HEIGHT;
		this.x = 0;

		this.height = 64;
		this.width = Game.VIRTUAL_SCREEN_WIDTH;

		upYValue = positionedAtTop ? this.y - playIcon.getHeight() : this.y
				+ playIcon.getHeight();

		playIconRectangle = new Rectangle(xOffset, this.y, this.getHeight(),
				this.getHeight());
		cancelIconRectangle = new Rectangle(Game.VIRTUAL_SCREEN_WIDTH
				- this.getHeight() - xOffset, this.y, this.getHeight(),
				this.getHeight());

		undoIconRectangle = new Rectangle(Game.VIRTUAL_SCREEN_WIDTH
				- this.getHeight() - xOffset * 2 - this.getHeight(), this.y,
				this.getHeight(), this.getHeight());
	}

	public void update(Float time) {

		// Refactor: Separate the algorithms for moving the bar at the top and
		// bottom with a Strategy pattern
		if (positionedAtTop) {

			if (position == Position.UP && playIconRectangle.y > upYValue) {
				playIconRectangle.y = playIconRectangle.y - velocity * time;
			} else if (position == Position.DOWN
					&& playIconRectangle.y < -playIcon.getHeight()) {
				playIconRectangle.y = playIconRectangle.y + velocity * time;
			}

			if (playIconRectangle.y < upYValue) {
				playIconRectangle.y = upYValue;
			}
			if (playIconRectangle.y > this.y) {
				playIconRectangle.y = this.y;
			}

		} else {

			if (position == Position.UP && playIconRectangle.y < upYValue) {
				playIconRectangle.y = playIconRectangle.y + velocity * time;
			} else if (position == Position.DOWN
					&& playIconRectangle.y > -playIcon.getHeight()) {
				playIconRectangle.y = playIconRectangle.y - velocity * time;
			}

			if (playIconRectangle.y > upYValue) {
				playIconRectangle.y = upYValue;
			}
			if (playIconRectangle.y < this.y) {
				playIconRectangle.y = this.y;
			}
		}

		cancelActionsTimer += time;
		textCountdownTimer += time;
	}

	public void draw(SpriteBatch batch) {
		Color tempColor = null;
		if (barColor != null) {
			tempColor = batch.getColor();
			batch.setColor(barColor);
		}
		batch.draw(bar, x, y, this.getWidth(), this.getHeight());
		if (tempColor != null) {
			batch.setColor(tempColor);
		}

		batch.draw(playIcon, playIconRectangle.x, playIconRectangle.y,
				playIconRectangle.width, playIconRectangle.height);

		if (isCancelButtonShown()) {
			if (cancelActionsTimer < 1) {
				batch.draw(cancelIconPressed, cancelIconRectangle.x,
						cancelIconRectangle.y, cancelIconRectangle.width,
						cancelIconRectangle.height, 0, 0,
						cancelIcon.getWidth(), cancelIcon.getHeight(), false,
						true);
			} else {
				batch.draw(cancelIcon, cancelIconRectangle.x,
						cancelIconRectangle.y, cancelIconRectangle.width,
						cancelIconRectangle.height, 0, 0,
						cancelIcon.getWidth(), cancelIcon.getHeight(), false,
						true);
				batch.draw(undoIcon, undoIconRectangle.x, undoIconRectangle.y,
						undoIconRectangle.width, undoIconRectangle.height, 0,
						0, undoIcon.getWidth(), undoIcon.getHeight(), false,
						true);
			}
		}

		if (textCountdownTimer > 2) {
			text = "Red: " + game.getRedScore() + " Blue: "
					+ game.getBlueScore() + "      " + game.getRemainingTime();
		}
		bmf.draw(batch, text, playIcon.getWidth() + (xOffset * 4), this.y + 7);

		if (game.getGameState() == GameState.EXECUTION) {
			bmf.draw(batch, "Executing...", xOffset, this.y + 7);
		}
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
			cancelActionsTimer = 0;
			game.getSelectedPlayer().clearActions();
			game.clearSelectedPlayer();
		} else if (isCancelButtonShown() && undoIconRectangle.contains(x, y)) {
			cancelActionsTimer = 0;
			game.getSelectedPlayer().undoLastAction();
			game.clearSelectedPlayer();
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

	public void setBarColor(Color color) {
		this.barColor = color;
	}
}
