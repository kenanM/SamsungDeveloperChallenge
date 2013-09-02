package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.samsung.comp.football.data.PlayerDataSource;

public class AISelectionScreen extends TextArea {
	private AbstractGame game;
	private Skin skin;
	private Stage stage;
	private BitmapFont bmf;
	private Color overlayColour = new Color(0.27f, 0.27f, 0.35f, 0.70f);
	private Color hardLabelColour = new Color(1f, 0f, 0f, 1f);
	private Color yellowLabelColour = new Color(0.902f, 0.902f, 0f, 1f);
	private Color medLabelColour = new Color(0.05f, 0.59f, .85f, 1f);
	private Color easyLabelColour = new Color(0f, 1f, 0f, 1f);
	private Color buttonColour = new Color(1f, 1f, 1f, .8f);

	private Rectangle overlayBackground = new Rectangle(0, 0,
			Game.VIRTUAL_SCREEN_WIDTH, (Game.VIRTUAL_SCREEN_HEIGHT + 64));

	java.util.List<Team> aiTeamsList;
	List aiTeamsMenu;
	ScrollPane aiScrollPane;
	Label labelTeamDifficulty;
	Label labelSelectedTeam;

	final Slider gameLengthSlider;
	Label labelGameLength;

	private PlayerDataSource dataSource;

	float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	Button backButton;
	Button startButton;

	AISelectionListener listener;

	public AISelectionScreen(AbstractGame game, PlayerDataSource dataSource,
			AISelectionListener listener) {
		this.game = game;
		this.skin = game.skin;
		this.dataSource = dataSource;
		gameLengthSlider = new Slider(0, 180, 60, false, skin);
		create();
		this.listener = listener;
	}

	public void create() {
		stage = new Stage(resolutionX, resolutionY, true);

		bmf = new BitmapFont(true);
		bmf.scale(.5f);

		Label labelTitle = new Label("Single Player", skin);

		aiTeamsList = dataSource.getTeamsTableManager().getOpponentTeams();
		aiTeamsMenu = new List(aiTeamsList.toArray(), skin);
		aiTeamsMenu.setColor(hardLabelColour);

		aiScrollPane = new ScrollPane(aiTeamsMenu, skin);
		aiScrollPane.setFlickScroll(true);
		aiScrollPane.setColor(overlayColour);

		labelTeamDifficulty = new Label("Easy", skin);
		labelTeamDifficulty.setColor(easyLabelColour);

		labelSelectedTeam = new Label("The Misfits", skin);

		labelGameLength = new Label("30 seconds", skin);
		labelGameLength.setColor(easyLabelColour);

		Table aiLayout = new Table(skin);
		aiLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);

		aiLayout.row().expandX();
		aiLayout.add(labelTitle).colspan(2);

		aiLayout.row();
		aiLayout.add("Select an opponent").prefWidth(resolutionX).colspan(2);
		
		aiLayout.row().fillX().expandX().prefHeight(280.5f);
		aiLayout.add(aiScrollPane).prefWidth(resolutionX).colspan(2);

		aiLayout.row();
		aiLayout.add("Selected opponent");
		aiLayout.add("Team Difficulty");

		aiLayout.row();
		aiLayout.add(labelSelectedTeam);
		aiLayout.add(labelTeamDifficulty);

		aiLayout.row();
		aiLayout.add();

		aiLayout.row();
		aiLayout.add("Game Length");
		aiLayout.add("Time");

		aiLayout.row();
		aiLayout.add(gameLengthSlider);
		aiLayout.add(labelGameLength);

		aiLayout.pack();
		aiLayout.setBackground((Drawable) null);

		Table paddingLayout = new Table(skin);
		paddingLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);
		paddingLayout.row();
		paddingLayout.add();

		paddingLayout.pack();
		paddingLayout.setBackground((Drawable) null);

		aiLayout.setPosition(0, resolutionY - aiLayout.getHeight());
		paddingLayout.setPosition(0, aiLayout.getY() - aiLayout.getHeight());

		stage.addActor(aiLayout);
		stage.addActor(paddingLayout);

		backButton = new TextButton("Back to menu", skin, "default");
		startButton = new TextButton("Begin match!", skin, "default");

		Table backButtonLayout = new Table(skin);
		backButtonLayout.defaults().spaceBottom(2.5f);
		backButtonLayout.row().fill().expandX().prefWidth(resolutionX * 2 / 7);
		backButtonLayout.add(backButton).expandX()
				.prefWidth(resolutionX * 2 / 7);

		backButtonLayout.setPosition(20, 32);
		backButtonLayout.pack();
		backButtonLayout.setBackground((Drawable) null);

		Table startButtonLayout = new Table(skin);
		startButtonLayout.defaults().spaceBottom(2.5f);
		startButtonLayout.row().fill().expandX().prefWidth(resolutionX * 2 / 7);
		startButtonLayout.add(startButton).expandX()
				.prefWidth(resolutionX * 2 / 7);

		startButtonLayout.setPosition(resolutionX - resolutionX * 2 / 7 - 20,
				32);
		startButtonLayout.pack();
		startButtonLayout.setBackground((Drawable) null);

		stage.addActor(backButtonLayout);
		stage.addActor(startButtonLayout);

		backButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				game.backButtonPressed();
			}
		});

		startButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				hackNotify();
			}
		});

		
		aiTeamsMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedAITeamIndex = aiTeamsMenu
						.getSelectedIndex();

				if (selectedAITeamIndex == -1) {
					return;
				}
				
				Team selectedAITeam = aiTeamsList.get(selectedAITeamIndex);

				labelSelectedTeam.setText(selectedAITeam.getTeamName());

				float value = selectedAITeam.getDifficulty();
				if (value == 1) {
					labelTeamDifficulty.setText("Easy");
					labelTeamDifficulty.setColor(easyLabelColour);
				} else if (value == 2) {
					labelTeamDifficulty.setText("Medium");
					labelTeamDifficulty.setColor(medLabelColour);
				} else if (value == 3) {
					labelTeamDifficulty.setText("Hard");
					labelTeamDifficulty.setColor(hardLabelColour);
				}

			}
		});

		gameLengthSlider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				float time = gameLengthSlider.getValue();

				// Quick, short, normal, long
				// 30, 60, 120, 180

				if (time == 0) {
					labelGameLength.setText("30 seconds");
					labelGameLength.setColor(easyLabelColour);
				} else if (time == 60) {
					labelGameLength.setText("1 minute");
					labelGameLength.setColor(medLabelColour);
				} else if (time == 120) {
					labelGameLength.setText("2 minutes");
					labelGameLength.setColor(yellowLabelColour);
				} else if (time == 180) {
					labelGameLength.setText("3 minutes");
					labelGameLength.setColor(hardLabelColour);
				}
			}
		});
	}

	private void hackNotify() {
		// Can't call keyword this from inner class
		listener.onStartButtonPressed(this);
	}

	public Team getSelectedAITeam() {
		int selectedAITeamIndex = aiTeamsMenu.getSelectedIndex();
		return selectedAITeamIndex == -1 ? null : aiTeamsList
				.get(selectedAITeamIndex);
	}
	
	public float getSelectedGameLength() {
		float time = gameLengthSlider.getValue();
		if (time == 0) {
			return 30;
		} else if (time == 60) {
			return 60;
		} else if (time == 120) {
			return 120;
		} else if (time == 180) {
			return 180;
		}
		return 30;
	}

	@Override
	public void update(float time) {
		stage.act(time);

	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {

		Gdx.gl.glEnable(GL10.GL_BLEND);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);
		renderer.rect(overlayBackground.x, overlayBackground.y,
				overlayBackground.width, overlayBackground.height);

		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		stage.draw();
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

	@Override
	public boolean keyTyped(char character) {
		return stage.keyTyped(character);
	}

}