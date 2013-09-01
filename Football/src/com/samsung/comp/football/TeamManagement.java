package com.samsung.comp.football;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.spensdk.applistener.SPenHoverListener;

public class TeamManagement implements ApplicationListener, InputProcessor,
		SPenHoverListener {
	private Skin skin;
	private Stage stage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont bmf;
	private ShapeRenderer renderer;

	private Color overlayColour = new Color(0.27f, 0.27f, 0.35f, 0.70f);
	private Color buttonColour = new Color(1f, 1f, 1f, .8f);
	private Color selectedItemColour = new Color(1f, 0f, 0f, 1f);

	private Rectangle overlayBackground = new Rectangle(0, 0,
			Game.VIRTUAL_SCREEN_WIDTH, (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 6);
	
	protected float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	protected float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	protected int pitchResolutionX = Game.VIRTUAL_SCREEN_WIDTH;
	protected int pitchResolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64);

	protected TextField teamNameField;

	protected java.util.List<Player> squadList;
	protected java.util.List<Player> benchedPlayersList;

	protected Team playerTeam;
	protected List squadMenu;
	protected List benchedPlayersMenu;

	protected StatsDisplay statsDisplayAreaLeft;
	protected StatsDisplay statsDisplayAreaRight;

	private PlayerDataSource dataSource;
	private ActionResolver actionResolver;

	protected ScrollPane leftScrollPane;
	protected ScrollPane rightScrollPane;

	protected Dialog dialog;
	protected boolean dialogShown = false;

	protected Button backButton;
	protected Button startButton;

	protected Texture pitchTexture;

	protected Texture statPoint1;
	protected Texture statPoint2;
	protected Texture statPoint3;
	protected Texture statPoint4;
	protected Texture statPoint5;

	protected Texture statRunIcon;
	protected Texture statTackleIcon;
	protected Texture statShootIcon;
	protected Texture statSavingIcon;

	final int humanTeamID = 1;

	public TeamManagement(PlayerDataSource dataSource,
			ActionResolver actionResolver) {
		this.dataSource = dataSource;
		this.actionResolver = actionResolver;
	}

	@Override
	public void create() {

		createLibGDXItems();
		createTextures();
		createRenderingObjects();

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

		playerTeam = dataSource.getTeamsTableManager().getTeam(humanTeamID);

		String teamName = "Unnamed";
		if (playerTeam != null) {
			teamName = playerTeam.getTeamName();
		} else {
			Gdx.app.error("GameDB", "No team found for the person");
			throw new NullPointerException(
					"No team found in the database for the person");
		}

		teamNameField = new TextField(teamName, skin);
		teamNameField.setMessageText("Team Name");

		Squad squad = dataSource.getSquadsTableManager().getSquad(humanTeamID);
		squadList = squad.getAllPlayers();

		java.util.List<Player> allPlayers = dataSource.getPlayersTableManager()
				.getPlayers(humanTeamID);

		benchedPlayersList = new ArrayList<Player>();
		for (Player player : allPlayers) {
			benchedPlayersList.add(player);

			for (Player squadPlayer : squadList) {
				if (squadPlayer.getID() == player.getID()) {
					benchedPlayersList.remove(player);
				}
			}
		}

		benchedPlayersMenu = new List(benchedPlayersList.toArray(), skin);
		squadMenu = new List(squadList.toArray(), skin);
		benchedPlayersMenu.setColor(selectedItemColour);
		squadMenu.setColor(selectedItemColour);

		leftScrollPane = new ScrollPane(squadMenu, skin);
		leftScrollPane.setFlickScroll(true);
		leftScrollPane.setColor(overlayColour);

		rightScrollPane = new ScrollPane(benchedPlayersMenu, skin);
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

		Table playersLayout = new Table(skin);
		playersLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);

		playersLayout.row().expandX();
		playersLayout.add(labelTitle).colspan(2);

		playersLayout.row().fillX().expandX();
		playersLayout.add(teamNameField).minWidth(25).expandX().fillX().colspan(2);

		playersLayout.row();
		playersLayout.add("Fielded Players");
		playersLayout.add("Benched Players");

		playersLayout.row().fillX().expandX().prefHeight(225.5f);
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

		playersLayout.setPosition(0, resolutionY - playersLayout.getHeight());
		buttonsLayout.setPosition(0,
				playersLayout.getY() - buttonsLayout.getHeight());
		paddingLayout.setPosition(0,
				buttonsLayout.getY() - paddingLayout.getHeight());

		backButton = new TextButton("Back to Menu", skin, "default");
		startButton = new TextButton("Save and Exit", skin, "default");

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

		float statsDisplayHeight = 32 * 7;
		// The button Y in the virtual resolution coordinates
		float backButtonY = (resolutionY - backButtonLayout.getY()
				- backButtonLayout.getHeight() - 10)
				/ resolutionY;

		Player leftPlayer = squadList.size() > 0 ? squadList.get(0) : null;
		Player rightPlayer = benchedPlayersList.size() > 0 ? benchedPlayersList
				.get(0) : null;

		statsDisplayAreaLeft = new StatsDisplay(leftPlayer, 20,
				(Game.VIRTUAL_SCREEN_HEIGHT + 64) * backButtonY
						- statsDisplayHeight,
				(Game.VIRTUAL_SCREEN_WIDTH / 2) - 20 * 2, statsDisplayHeight,
				true, statPoint1, statPoint2, statPoint3, statPoint4,
				statPoint5, statRunIcon, statShootIcon, statTackleIcon,
				statSavingIcon);

		statsDisplayAreaRight = new StatsDisplay(rightPlayer,
				20 + Game.VIRTUAL_SCREEN_WIDTH / 2,
				(Game.VIRTUAL_SCREEN_HEIGHT + 64) * backButtonY
						- statsDisplayHeight,
				(Game.VIRTUAL_SCREEN_WIDTH / 2) - 20 * 2, statsDisplayHeight,
				true, statPoint1, statPoint2, statPoint3, statPoint4,
				statPoint5, statRunIcon, statShootIcon, statTackleIcon,
				statSavingIcon);

		stage.addActor(playersLayout);
		stage.addActor(buttonsLayout);
		stage.addActor(paddingLayout);
		stage.addActor(backButtonLayout);
		stage.addActor(startButtonLayout);

		teamNameField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped(TextField textField, char key) {
				if (key == '\n') {
					textField.getOnscreenKeyboard().show(false);
					stage.setKeyboardFocus(null);

				}
			}
		});

		backButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				backButtonPressed();
			}
		});

		startButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				Squad squad = new Squad(humanTeamID, squadList);
				dataSource.getSquadsTableManager().updateSquad(squad);

				playerTeam.setTeamName(teamNameField.getText());
				dataSource.getTeamsTableManager().alterTeam(playerTeam);

				Gdx.app.exit();
			}
		});

		squadMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = squadMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1) {
					return;
				}

				Player selectedPlayer = squadList
						.get(selectedPlayerIndex);
				statsDisplayAreaLeft.setPlayer(selectedPlayer);
			}
		});

		benchedPlayersMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = benchedPlayersMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1) {
					return;
				}

				Player selectedPlayer = benchedPlayersList
						.get(selectedPlayerIndex);
				statsDisplayAreaRight.setPlayer(selectedPlayer);
			}
		});

		downButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedIndex = squadMenu.getSelectedIndex();
				if (selectedIndex >= squadList.size() - 1
						|| selectedIndex == -1) {
					return;
				}

				Player selectedPlayer = squadList.get(selectedIndex);
				Player nextPlayer = squadList.get(selectedIndex + 1);

				squadList.set(selectedIndex, nextPlayer);
				squadList.set(selectedIndex + 1, selectedPlayer);

				squadMenu.setItems(squadList.toArray());
				squadMenu.setSelectedIndex(selectedIndex + 1);
			}
		});

		upButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedIndex = squadMenu.getSelectedIndex();
				if (selectedIndex <= 0) {
					return;
				}

				Player selectedPlayer = squadList.get(selectedIndex);
				Player previousPlayer = squadList
						.get(selectedIndex - 1);

				squadList.set(selectedIndex, previousPlayer);
				squadList.set(selectedIndex - 1, selectedPlayer);

				squadMenu.setItems(squadList.toArray());
				squadMenu.setSelectedIndex(selectedIndex - 1);
			}
		});

		switchButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				if (squadMenu.getItems().length == 0
						|| benchedPlayersMenu.getItems().length == 0
						|| squadMenu.getSelectedIndex() == -1
						|| benchedPlayersMenu.getSelectedIndex() == -1) {
					return;
				}

				int leftSelectedIndex = squadMenu
						.getSelectedIndex();
				int rightSelectedIndex = benchedPlayersMenu
						.getSelectedIndex();

				Player fieldedPlayer = squadList
						.get(leftSelectedIndex);
				Player benchedPlayer = benchedPlayersList
						.get(rightSelectedIndex);

				// Swap the players in the array lists
				squadList.set(leftSelectedIndex, benchedPlayer);
				benchedPlayersList.set(rightSelectedIndex, fieldedPlayer);

				// Set the menu to the new array lists
				squadMenu.setItems(squadList.toArray());
				benchedPlayersMenu.setItems(benchedPlayersList.toArray());

				// Restore the selected index
				squadMenu.setSelectedIndex(leftSelectedIndex);
				benchedPlayersMenu.setSelectedIndex(rightSelectedIndex);

				// Update stats displays
				statsDisplayAreaLeft.setPlayer(benchedPlayer);
				statsDisplayAreaRight.setPlayer(fieldedPlayer);
			}
		});
	}
	
	private void createTextures() {
		pitchTexture = new Texture(Gdx.files.internal("leftPitch.png"));
		statPoint1 = new Texture(Gdx.files.internal("statPointRed.png"));
		statPoint2 = new Texture(Gdx.files.internal("statPoint.png"));
		statPoint3 = new Texture(Gdx.files.internal("statPointGreen.png"));
		statPoint4 = new Texture(Gdx.files.internal("statPointBlue.png"));
		statPoint5 = new Texture(Gdx.files.internal("statPointPurple.png"));

		statRunIcon = new Texture(Gdx.files.internal("icons/runIcon.png"));
		statTackleIcon = new Texture(Gdx.files.internal("icons/tackleIcon.png"));
		statShootIcon = new Texture(Gdx.files.internal("icons/shootIcon.png"));
		statSavingIcon = new Texture(Gdx.files.internal("icons/savingIcon.png"));

	}

	private void createLibGDXItems() {
		Texture.setEnforcePotImages(false);

		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
	}

	protected void createRenderingObjects() {

		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		stage = new Stage(resolutionX, resolutionY, true);

		camera = new OrthographicCamera();
		camera.setToOrtho(true, Game.VIRTUAL_SCREEN_WIDTH,
				Game.VIRTUAL_SCREEN_HEIGHT + 64);
		camera.zoom = 1f;
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);

		bmf = new BitmapFont(true);
		bmf.scale(.35f);

		// camera.setToOrtho(true, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT
		// + bar.getHeight()

	}

	public void update(float time) {
		stage.act(time);

	}

	public void draw() {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// draw the background pitch
		batch.begin();
		batch.draw(pitchTexture, 0, 0, pitchResolutionX, pitchResolutionY, 0,
				0, pitchResolutionX, pitchResolutionY, false, true);
		batch.end();

		statsDisplayAreaLeft.draw(batch, bmf, renderer);
		statsDisplayAreaRight.draw(batch, bmf, renderer);

		Gdx.gl.glEnable(GL10.GL_BLEND);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);
		renderer.rect(overlayBackground.x, overlayBackground.y,
				overlayBackground.width, overlayBackground.height);

		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		stage.draw();
	}

	public void backButtonPressed() {
		// Check for any changes
		boolean changesMade = false;

		// Check the squad players and order
		java.util.List<Player> dbSquadList = dataSource.getSquadsTableManager()
				.getSquad(humanTeamID).getAllPlayers();

		for (int i = 0; i < dbSquadList.size() - 1; i++) {
			if (squadList.get(i).getID() != dbSquadList.get(i).getID()) {
				changesMade = true;
			}
		}

		// Check the team name
		if (!playerTeam.getTeamName().equals(teamNameField.getText())) {
			changesMade = true;
		}

		if (changesMade) {
			showConfirmationDialog();
		} else {
			exit();
		}
	}

	public void showConfirmationDialog() {

		if (!dialogShown) {
			dialogShown = true;
			Button yesButton = new TextButton("Yes", skin);
			Button noButton = new TextButton("No", skin);

			dialog = new Dialog("Discard changes and exit?", skin, "dialog") {
				protected void result(Object object) {
					if (object.equals(true)) {
						exit();
					}
					dialogShown = false;
				}
			}.text("You will lose any changes you made.")
					.button(noButton, false).button(yesButton, true)
					.key(Keys.ENTER, true).key(Keys.ESCAPE, false)
					.key(Keys.BACK, false);

			dialog.show(stage);
		}
	}

	public void exit() {
		Gdx.app.exit();
	}

	@Override
	public boolean onHover(View arg0, MotionEvent arg1) {
		return false;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		float time = Gdx.graphics.getDeltaTime();
		this.update(time);
		this.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		dataSource.close();

		stage.dispose();
		bmf.dispose();
		renderer.dispose();

		pitchTexture.dispose();
		statPoint1.dispose();
		statPoint2.dispose();
		statPoint3.dispose();
		statPoint4.dispose();
		statPoint5.dispose();

		statRunIcon.dispose();
		statTackleIcon.dispose();
		statShootIcon.dispose();
		statSavingIcon.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			stage.setKeyboardFocus(null);

			if (!stage.keyDown(Keys.BACK)) {
				backButtonPressed();
				return true;
			}
		}
		if (keycode == Keys.MENU) {

		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return stage.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return stage.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return stage.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return stage.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return stage.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return stage.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return stage.scrolled(amount);
	}

}
