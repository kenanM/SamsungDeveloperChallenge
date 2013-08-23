package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GameOverScreen extends TextArea {

	private static final float X_OFFSET = 30f;
	private static final float BUTTON_Y_MARGIN = 20f;

	private AbstractGame game;
	private BitmapFont bmf;
	private List<GameButton> scoreInfoButtons;
	private GameButton totalFundsButton;
	private int startingFunds = 0;
	private int currentFunds = 0;
	private String currentFundsString;
	private int reward = 0;
	private final float fundsAddingTime = 4f;

	float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	private Skin skin;
	private Stage stage;

	private TextButton backButton;
	private String title = "Full-Time";
	private Rectangle titleRectangle;

	public GameOverScreen(final AbstractGame game, int reward) {
		this.game = game;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/absender1.ttf"));
		bmf = generator.generateFont(35, FreeTypeFontGenerator.DEFAULT_CHARS,
				true);
		generator.dispose();
		this.reward = reward;

		this.skin = game.skin;
		stage = new Stage(resolutionX, resolutionY, false);

		backButton = new TextButton("Back to menu", skin, "default");
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				game.backButtonPressed();
			}
		});

		Table backButtonLayout = new Table(skin);
		backButtonLayout.defaults().spaceBottom(2.5f);
		backButtonLayout.row().fill().expandX().prefWidth(resolutionX * 2 / 7);
		backButtonLayout.add(backButton).expandX()
				.prefWidth(resolutionX * 2 / 7);

		backButtonLayout.setPosition(20, 32);
		backButtonLayout.pack();
		backButtonLayout.setBackground((Drawable) null);
		stage.addActor(backButtonLayout);
	}

	public GameOverScreen(AbstractGame game, int reward, int currentFunds) {
		this(game, reward);
		this.startingFunds = currentFunds;
		this.reward = reward;
	}

	@Override
	public void update(float time) {
		if (scoreInfoButtons == null) {
			populateScoreInfoList();
		}

		currentFunds += (currentFunds < startingFunds + reward) ? (time / fundsAddingTime)
				* reward
				: 0;
		currentFunds = (currentFunds > startingFunds + reward) ? startingFunds
				+ reward : currentFunds;

		currentFundsString = "Total Funds: " + currentFunds;
		totalFundsButton.setText(currentFundsString);

		stage.act();
	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont unusedBmf,
			ShapeRenderer renderer) {

		if (titleRectangle == null) {
			titleRectangle = new Rectangle(X_OFFSET, 10,
					Game.VIRTUAL_SCREEN_WIDTH - 2 * X_OFFSET,
					bmf.getBounds(title).height + 2 * 10);
		}

		Gdx.gl.glEnable(GL10.GL_BLEND);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);
		renderer.rect(0, 0, Game.VIRTUAL_SCREEN_WIDTH,
				Game.VIRTUAL_SCREEN_HEIGHT);
		renderer.rect(titleRectangle.x, titleRectangle.y, titleRectangle.width,
				titleRectangle.height);
		renderer.end();

		renderer.begin(ShapeType.Line);
		renderer.setColor(1, 1, 1, 1f);
		renderer.rect(titleRectangle.x, titleRectangle.y, titleRectangle.width,
				titleRectangle.height);
		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);


		batch.begin();
		bmf.draw(batch, title,
				Game.VIRTUAL_SCREEN_WIDTH / 2 - bmf.getBounds(title).width / 2,
				20);
		batch.end();

		if (scoreInfoButtons == null) {
			populateScoreInfoList();
		}
		for (GameButton info : scoreInfoButtons) {
			info.draw(batch, bmf, renderer);
		}

		stage.draw();
	}

	private void populateScoreInfoList() {
		scoreInfoButtons = new ArrayList<GameButton>();
		TextBounds bounds;
		float drawHeight = 225;

		// 'Button' for score
		String scoreString = game.teamA.getTeamName() + " "
				+ game.getScore(game.team1) + " : "
				+ game.getScore(game.team2) + " " + game.teamB.getTeamName();
		bounds = bmf.getBounds(scoreString);
		scoreInfoButtons.add(new GameButton(null, X_OFFSET, drawHeight,
				Game.VIRTUAL_SCREEN_WIDTH - 2 * X_OFFSET, bounds.height,
				scoreString));
		drawHeight += bounds.height * 2 + BUTTON_Y_MARGIN;

		List<String> finishData = game.getFinishData();
		for (String str : finishData) {
			bounds = bmf.getBounds(str);

			// Create 'buttons' with X_OFFSET space from both sides
			// Height = 2x the text size
			scoreInfoButtons.add(new GameButton(null, X_OFFSET, drawHeight,
					Game.VIRTUAL_SCREEN_WIDTH - 2 * X_OFFSET,
					bounds.height * 2, str));

			// Space between buttons
			drawHeight += bounds.height * 2 + BUTTON_Y_MARGIN;
		}

		// Increased separation for total funds 'button'
		drawHeight += bounds.height * 2 + BUTTON_Y_MARGIN;

		// 'Button' for total funds
		// Height = 2x the text size
		String totalFundsString = "Total Funds: " + startingFunds;
		bounds = bmf.getBounds(totalFundsString);
		// Store total funds button as a reference
		totalFundsButton = new GameButton(null, X_OFFSET, drawHeight,
				Game.VIRTUAL_SCREEN_WIDTH - 2 * X_OFFSET, bounds.height * 2,
				totalFundsString);
		scoreInfoButtons.add(totalFundsButton);
		drawHeight += bounds.height * 2 + BUTTON_Y_MARGIN;

	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer, int button) {
		return stage.touchDown((int) x, (int) y, pointer, button);
	}

	@Override
	public boolean onTouchDragged(float x, float y, int pointer) {
		return stage.touchDragged((int) x, (int) y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer, int button) {
		return stage.touchUp((int) x, (int) y, pointer, button);
	}

}
