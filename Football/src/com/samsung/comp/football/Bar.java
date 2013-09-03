package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.samsung.comp.football.AbstractGame.GameState;
import com.samsung.comp.precisionfootball.R;

public class Bar extends Rectangle {

	private static final long serialVersionUID = 1L;

	private Texture bar;
	private Color barColor;
	private Texture playIcon;
	private Texture cancelIcon;
	private Texture undoIcon;
	private Texture undoIconPressed;
	private String text;

	private Rectangle playIconRectangle;
	private Rectangle cancelIconRectangle;
	private Rectangle undoIconRectangle;

	public enum Position {
		UP, DOWN
	};

	private float upYValue;
	/** The distance between a button and a border */
	private int xOffset = 7;
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
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/absender1.ttf"));
		bmf = generator.generateFont(35);
		bmf.setScale(1, -1);
		generator.dispose();
		create(topOfScreen);
	}

	public void create(boolean topOfScreen) {
		bar = new Texture(Gdx.files.internal("bar.png"));
		playIcon = new Texture(Gdx.files.internal("playIcon.png"));
		undoIcon = new Texture(Gdx.files.internal("undoIcon.png"));
		undoIconPressed = new Texture(Gdx.files.internal("undoIconDown.png"));
		cancelIcon = new Texture(Gdx.files.internal("cancelIcon.png"));

		positionedAtTop = topOfScreen;

		this.y = positionedAtTop ? -64 : Game.VIRTUAL_SCREEN_HEIGHT;
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
					&& playIconRectangle.y < this.y) {
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
					&& playIconRectangle.y > this.y) {
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

		if (selectedPlayerHasActions()) {
			if (cancelActionsTimer < 0.35) {
				batch.draw(undoIconPressed, undoIconRectangle.x,
						undoIconRectangle.y, cancelIconRectangle.width,
						cancelIconRectangle.height, 0, 0, undoIcon.getWidth(),
						undoIcon.getHeight(), false, true);
			} else {
				batch.draw(undoIcon, undoIconRectangle.x, undoIconRectangle.y,
						undoIconRectangle.width, undoIconRectangle.height, 0,
						0, undoIcon.getWidth(), undoIcon.getHeight(), false,
						true);
			}
			batch.draw(cancelIcon, cancelIconRectangle.x,
					cancelIconRectangle.y, cancelIconRectangle.width,
					cancelIconRectangle.height, 0, 0, cancelIcon.getWidth(),
					cancelIcon.getHeight(), false, true);
		}

		if (textCountdownTimer > 2) {
			text = "Red: " + game.getRedScore() + " Blue: "
					+ game.getBlueScore() + "    " + game.getRemainingTime();
		}

		if (game.getGameState() == GameState.EXECUTION) {
			bmf.draw(batch, "Executing... " + text, xOffset, this.y + 22);
		} else if (game.getGameState() == GameState.SETUP) {
			bmf.draw(batch, "Setting up... " + text, xOffset, this.y + 22);
		} else if (game.getGameState() == GameState.INPUT) {
			if(selectedPlayerHasActions()){
				bmf.setScale(0.9f, -1);
			} else {
				bmf.setScale(1,-1);
			}
			
			// Gdx.app.log("bar", "Bar width = " + bmf.getBounds(text).width);
				
			
			
			int leftOffset = playIcon.getWidth();
			bmf.draw(batch, text, leftOffset, this.y + 22);
		}
	}

	public void setPositionToUp() {
		position = Position.UP;
	}

	public void setPositionToDown() {
		position = Position.DOWN;
	}

	public void onPress(float x, float y) {
		if (playIconRectangle.contains(x, y)) {
			game.playButtonPressed();

		} else if (selectedPlayerHasActions()
				&& cancelIconRectangle.contains(x, y)) {
			cancelActionsTimer = 0;
			game.getSelectedPlayer().clearActions();
			game.clearSelectedPlayer();
		} else if (selectedPlayerHasActions()
				&& undoIconRectangle.contains(x, y)
				&& cancelActionsTimer > 0.35) {
			cancelActionsTimer = 0;
			game.getSelectedPlayer().undoLastAction();
		}
	}

	public void setText(String string) {
		text = string;
		textCountdownTimer = 0;
	}

	private boolean selectedPlayerHasActions() {
		return (game.getSelectedPlayer() != null && game.getSelectedPlayer()
				.getAction() != null);
	}

	public void setBarColor(Color color) {
		this.barColor = color;
	}
}
