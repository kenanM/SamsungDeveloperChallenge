package com.samsung.comp.football;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.data.PlayerDataSource;

public class TeamSetupScreen extends TextArea {
	private AbstractGame game;
	private Skin skin;
	private Stage stage;
	private BitmapFont bmf;
	private Color overlayColour = new Color(0.27f, 0.27f, 0.35f, 0.70f);
	private Color hardLabelColour = new Color(1f, 0f, 0f, 1f);
	private Color medLabelColour = new Color(0.12f, 0.51f, .95f, 1f);
	private Color easyLabelColour = new Color(0f, 1f, 0f, 1f);
	private Color buttonColour = new Color(1f, 1f, 1f, .8f);

	java.util.List<Player> fieldedPlayersList;
	java.util.List<Player> benchedPlayersList;

	Team playerTeam;
	ArrayList<Team> aiTeamsList;
	ArrayList<String> aiTeamNamesList;

	java.util.List<Team> allTeams;

	List benchedPlayersListMenu;
	List fieldedPlayersListMenu;

	Slider slider;
	Label labelTeamDifficulty;
	Label labelAIDifficulty;

	SelectBox aiTeamSelection;

	StatsDisplay statsDisplayAreaLeft;
	StatsDisplay statsDisplayAreaRight;

	private PlayerDataSource dataSource;

	float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	ScrollPane leftScrollPane;
	ScrollPane rightScrollPane;

	Button backButton;
	Button startButton;

	public TeamSetupScreen(AbstractGame game, PlayerDataSource dataSource) {
		this.game = game;
		this.skin = game.skin;
		this.dataSource = dataSource;
		create();
	}

	public void create() {
		stage = new Stage(resolutionX, resolutionY, false);

		bmf = new BitmapFont(true);
		bmf.scale(.5f);

		TextureRegion upIconUpRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/upIconUp.png")));

		TextureRegion upIconRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/upIcon.png")));

		TextureRegion downIconUpRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/downIconUp.png")));

		TextureRegion downIconRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/downIcon.png")));

		TextureRegion switchIconUpRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/switchIconUp.png")));

		TextureRegion switchIconRegion = new TextureRegion(new Texture(
				Gdx.files.internal("icons/switchIcon.png")));

		Label labelTitle = new Label("Team Setup", skin);

		java.util.List<Player> allPlayers = dataSource.getPlayersTableManager()
				.getAllPlayers(1);

		fieldedPlayersList = new ArrayList<Player>();
		benchedPlayersList = new ArrayList<Player>();
		for (Player player : allPlayers) {
			int i = 0;
			if (i < 5) {
				fieldedPlayersList.add(player);
			} else {
				benchedPlayersList.add(player);
			}
		}

		playerTeam = null;
		aiTeamsList = new ArrayList<Team>();
		aiTeamNamesList = new ArrayList<String>();
		allTeams = dataSource.getTeamsTableManager().getAllTeams();

		for (Team t : allTeams) {
			if (t.getTeamID() == 1) {
				playerTeam = t;
			} else {
				aiTeamsList.add(t);
				aiTeamNamesList.add(t.getTeamName());
			}
		}

		String teamName = "Unnamed";
		if (playerTeam != null) {
			playerTeam.getTeamName();
		} else {
			Gdx.app.error("GameDB", "No team found for the person");
			throw new NullPointerException("No team found for the person");
		}

		TextField textfield = new TextField(teamName, skin);
		textfield.setMessageText("Team Name");

		benchedPlayersListMenu = new List(benchedPlayersList.toArray(), skin);
		fieldedPlayersListMenu = new List(fieldedPlayersList.toArray(), skin);
		benchedPlayersListMenu.setColor(hardLabelColour);
		fieldedPlayersListMenu.setColor(hardLabelColour);

		leftScrollPane = new ScrollPane(fieldedPlayersListMenu, skin);
		leftScrollPane.setFlickScroll(true);
		leftScrollPane.setColor(overlayColour);

		rightScrollPane = new ScrollPane(benchedPlayersListMenu, skin);
		rightScrollPane.setFlickScroll(true);
		rightScrollPane.setColor(overlayColour);

		ImageButtonStyle styleUpIcon = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		styleUpIcon.imageDown = new TextureRegionDrawable(upIconRegion);
		styleUpIcon.imageUp = new TextureRegionDrawable(upIconUpRegion);

		ImageButtonStyle styleDownIcon = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		styleDownIcon.imageDown = new TextureRegionDrawable(downIconRegion);
		styleDownIcon.imageUp = new TextureRegionDrawable(downIconUpRegion);

		ImageButtonStyle styleSwitchIcon = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		styleSwitchIcon.imageDown = new TextureRegionDrawable(switchIconRegion);
		styleSwitchIcon.imageUp = new TextureRegionDrawable(switchIconUpRegion);

		ImageButton upButton = new ImageButton(styleUpIcon);
		ImageButton downButton = new ImageButton(styleDownIcon);
		ImageButton switchButton = new ImageButton(styleSwitchIcon);
		upButton.setColor(buttonColour);
		downButton.setColor(buttonColour);
		switchButton.setColor(buttonColour);

		aiTeamSelection = new SelectBox(
				aiTeamNamesList.toArray(new String[] {}), skin);

		final Slider slider = new Slider(0, 2, 1, false, skin);

		labelTeamDifficulty = new Label("Amateurs", skin);
		labelTeamDifficulty.setColor(easyLabelColour);
		labelAIDifficulty = new Label("Easy", skin);
		labelAIDifficulty.setColor(easyLabelColour);

		Table playersLayout = new Table(skin);
		playersLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);

		playersLayout.row().expandX();
		playersLayout.add(labelTitle).colspan(2);

		playersLayout.row().fillX().expandX();
		playersLayout.add(textfield).minWidth(25).expandX().fillX().colspan(2);

		playersLayout.row();
		playersLayout.add("Fielded Players");
		playersLayout.add("Benched Players");

		playersLayout.row().fillX().expandX().prefHeight(135);
		playersLayout.add(leftScrollPane).prefWidth(resolutionX / 2);
		playersLayout.add(rightScrollPane).prefWidth(resolutionX / 2);

		playersLayout.pack();
		playersLayout.setBackground((Drawable) null);

		Table buttonsLayout = new Table(skin);
		buttonsLayout.defaults().spaceBottom(2.5f);
		buttonsLayout.row().fill().expandX().prefWidth(resolutionX / 5);
		buttonsLayout.add(upButton).expandX();
		buttonsLayout.add(downButton).expandX();
		buttonsLayout.add(switchButton).expandX();
		buttonsLayout.add().expandX();
		buttonsLayout.add().expandX();

		buttonsLayout.pack();
		buttonsLayout.setBackground((Drawable) null);

		Table paddingLayout = new Table(skin);
		paddingLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);
		paddingLayout.row();
		paddingLayout.add();

		paddingLayout.pack();
		paddingLayout.setBackground((Drawable) null);

		Table aiPlayerLayout = new Table(skin);
		aiPlayerLayout.defaults().space(4.5f).prefHeight(17.5f);
		aiPlayerLayout.row();
		aiPlayerLayout.add("AI Team");
		aiPlayerLayout.add(aiTeamSelection);
		aiPlayerLayout.add(labelTeamDifficulty);

		aiPlayerLayout.row();
		aiPlayerLayout.add("AI Difficulty");
		aiPlayerLayout.add(slider).minWidth(25).fill();
		aiPlayerLayout.add(labelAIDifficulty);

		aiPlayerLayout.pack();
		aiPlayerLayout.setBackground((Drawable) null);

		playersLayout.setPosition(0, resolutionY - playersLayout.getHeight());
		buttonsLayout.setPosition(0,
				playersLayout.getY() - buttonsLayout.getHeight());
		paddingLayout.setPosition(0,
				buttonsLayout.getY() - paddingLayout.getHeight());
		aiPlayerLayout.setPosition(0,
				paddingLayout.getY() - aiPlayerLayout.getHeight());

		// Get the % height of bottom left of the AI table relative to the stage
		// Multiply this by the game's resolution to draw to the same
		// position but using the game's coordinate system
		float verticalProportion = (resolutionY - aiPlayerLayout.getY() + 20)
				/ resolutionY;

		statsDisplayAreaLeft = new StatsDisplay(fieldedPlayersList.get(0), 20,
				(Game.VIRTUAL_SCREEN_HEIGHT + 64) * verticalProportion,
				(Game.VIRTUAL_SCREEN_WIDTH / 2) - 20 * 2, 32 * 7, true,
				game.statPoint1, game.statPoint2, game.statPoint3,
				game.statPoint4, game.statPoint5, game.statRunIcon,
				game.statShootIcon, game.statTackleIcon, game.statSavingIcon);

		statsDisplayAreaRight = new StatsDisplay(null,
				20 + Game.VIRTUAL_SCREEN_WIDTH / 2,
				(Game.VIRTUAL_SCREEN_HEIGHT + 64) * verticalProportion,
				(Game.VIRTUAL_SCREEN_WIDTH / 2) - 20 * 2, 32 * 7, true,
				game.statPoint1, game.statPoint2, game.statPoint3,
				game.statPoint4, game.statPoint5, game.statRunIcon,
				game.statShootIcon, game.statTackleIcon, game.statSavingIcon);

		stage.addActor(playersLayout);
		stage.addActor(buttonsLayout);
		stage.addActor(paddingLayout);
		stage.addActor(aiPlayerLayout);

		backButton = new TextButton("Back to menu", skin, "default");
		startButton = new TextButton("Begin match!", skin, "default");

		// Position of the bottom left of the stats display as a %
		float verticalProportionBack = (statsDisplayAreaLeft.getY() + statsDisplayAreaLeft
				.getHeight()) / (Game.VIRTUAL_SCREEN_HEIGHT + 64);

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

		textfield.setTextFieldListener(new TextFieldListener() {
			public void keyTyped(TextField textField, char key) {
				if (key == '\n')
					textField.getOnscreenKeyboard().show(false);
			}
		});

		backButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				game.backButtonPressed();
			}
		});

		fieldedPlayersListMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = fieldedPlayersListMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1) {
					return;
				}

				Player selectedPlayer = fieldedPlayersList
						.get(selectedPlayerIndex);
				statsDisplayAreaLeft.setPlayer(selectedPlayer);
			}
		});

		benchedPlayersListMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = benchedPlayersListMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1) {
					return;
				}

				Player selectedPlayer = benchedPlayersList
						.get(selectedPlayerIndex);
				statsDisplayAreaRight.setPlayer(selectedPlayer);
			}
		});

		aiTeamSelection.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("Team Setup", "AI Team: " + "");

				int selectedAITeamIndex = aiTeamSelection.getSelectionIndex();

				if (selectedAITeamIndex == -1) {
					return;
				}

				Team selectedAITeam = aiTeamsList.get(selectedAITeamIndex);

				float value = selectedAITeam.getDifficulty();
				if (value == 1) {
					labelTeamDifficulty.setText("Amateurs");
					labelTeamDifficulty.setColor(easyLabelColour);
				} else if (value == 2) {
					labelTeamDifficulty.setText("Rising Stars");
					labelTeamDifficulty.setColor(medLabelColour);
				} else if (value == 3) {
					labelTeamDifficulty.setText("Experts");
					labelTeamDifficulty.setColor(hardLabelColour);
				}

			}
		});

		slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("Team Setup", "slider: " + slider.getValue());

				float value = slider.getValue();
				if (value == 0) {
					labelAIDifficulty.setText("Easy");
					labelAIDifficulty.setColor(easyLabelColour);
				} else if (value == 1) {
					labelAIDifficulty.setText("Medium");
					labelAIDifficulty.setColor(medLabelColour);
				} else if (value == 2) {
					labelAIDifficulty.setText("Hard");
					labelAIDifficulty.setColor(hardLabelColour);
				}

			}
		});

		downButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedIndex = fieldedPlayersListMenu.getSelectedIndex();
				if (selectedIndex >= fieldedPlayersList.size() - 1
						|| selectedIndex == -1) {
					return;
				}

				Player selectedPlayer = fieldedPlayersList.get(selectedIndex);
				Player nextPlayer = fieldedPlayersList.get(selectedIndex + 1);

				fieldedPlayersList.set(selectedIndex, nextPlayer);
				fieldedPlayersList.set(selectedIndex + 1, selectedPlayer);

				fieldedPlayersListMenu.setItems(fieldedPlayersList.toArray());
				fieldedPlayersListMenu.setSelectedIndex(selectedIndex + 1);
			}
		});

		upButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedIndex = fieldedPlayersListMenu.getSelectedIndex();
				if (selectedIndex <= 0) {
					return;
				}

				Player selectedPlayer = fieldedPlayersList.get(selectedIndex);
				Player previousPlayer = fieldedPlayersList
						.get(selectedIndex - 1);

				fieldedPlayersList.set(selectedIndex, previousPlayer);
				fieldedPlayersList.set(selectedIndex - 1, selectedPlayer);

				fieldedPlayersListMenu.setItems(fieldedPlayersList.toArray());
				fieldedPlayersListMenu.setSelectedIndex(selectedIndex - 1);
			}
		});

		switchButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				if (fieldedPlayersListMenu.getItems().length == 0
						|| benchedPlayersListMenu.getItems().length == 0
						|| fieldedPlayersListMenu.getSelectedIndex() == -1
						|| benchedPlayersListMenu.getSelectedIndex() == -1) {
					return;
				}

				int leftSelectedIndex = fieldedPlayersListMenu
						.getSelectedIndex();
				int rightSelectedIndex = benchedPlayersListMenu
						.getSelectedIndex();

				Player fieldedPlayer = fieldedPlayersList
						.get(leftSelectedIndex);
				Player benchedPlayer = benchedPlayersList
						.get(rightSelectedIndex);

				// Swap the players in the array lists
				fieldedPlayersList.set(leftSelectedIndex, benchedPlayer);
				benchedPlayersList.set(rightSelectedIndex, fieldedPlayer);

				// Set the menu to the new array lists
				fieldedPlayersListMenu.setItems(fieldedPlayersList.toArray());
				benchedPlayersListMenu.setItems(benchedPlayersList.toArray());

				// Restore the selected index
				fieldedPlayersListMenu.setSelectedIndex(leftSelectedIndex);
				benchedPlayersListMenu.setSelectedIndex(rightSelectedIndex);

				// Update stats displays
				statsDisplayAreaLeft.setPlayer(benchedPlayer);
				statsDisplayAreaRight.setPlayer(fieldedPlayer);
			}

		});
	}

	@Override
	public void update(float time) {
		stage.act(time);

	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont bmf, ShapeRenderer renderer) {
		statsDisplayAreaLeft.draw(batch, bmf, renderer);
		statsDisplayAreaRight.draw(batch, bmf, renderer);
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

}
