package com.samsung.comp.football;

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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.spensdk.applistener.SPenHoverListener;

public class PlayerStore implements ApplicationListener, InputProcessor,
		SPenHoverListener {
	private Skin skin;
	private Stage stage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont bmf;
	private ShapeRenderer renderer;
	private Color overlayColour = new Color(0.27f, 0.27f, 0.35f, 0.70f);
	private Color selectedListItemColour = new Color(1, 0, 0, 1f);

	protected float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	protected float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	protected int pitchResolutionX = Game.VIRTUAL_SCREEN_WIDTH;
	protected int pitchResolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64);

	private Rectangle overlayBackground = new Rectangle(0, 0,
			Game.VIRTUAL_SCREEN_WIDTH, (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 10);

	protected StatsDisplay statsDisplayAreaLeft;
	protected StatsDisplay statsDisplayAreaRight;

	protected java.util.List<Player> teamPlayersList;
	protected java.util.List<Player> storePlayersList;

	protected List storePlayersMenu;
	protected List teamPlayersMenu;

	protected int fundsDisplay;
	protected int currentFunds;
	protected int subtractionVelocity = 10000;
	protected Label fundsLabel;

	protected Button backButton;
	protected Button buyButton;

	protected ScrollPane leftScrollPane;
	protected ScrollPane rightScrollPane;

	private PlayerDataSource dataSource;
	private ActionResolver actionResolver;

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

	private int humanTeamID = 1;
	private int humanProfileID = 2;
	private int storeTeamID = 2;


	public PlayerStore(PlayerDataSource dataSource,
			ActionResolver actionResolver) {
		this.dataSource = dataSource;
		this.actionResolver = actionResolver;
	}

	@Override
	public void create() {

		createLibGDXItems();
		createTextures();
		createRenderingObjects();

		Label labelTitle = new Label("Player Store", skin);

		teamPlayersList = dataSource.getPlayersTableManager().getPlayers(
				humanTeamID);
		storePlayersList = dataSource.getPlayersTableManager().getPlayers(
				storeTeamID);

		storePlayersMenu = new List(storePlayersList.toArray(), skin);
		teamPlayersMenu = new List(teamPlayersList.toArray(), skin);
		storePlayersMenu.setColor(selectedListItemColour);
		teamPlayersMenu.setColor(selectedListItemColour);

		leftScrollPane = new ScrollPane(teamPlayersMenu, skin);
		leftScrollPane.setFlickScroll(true);
		leftScrollPane.setColor(overlayColour);

		rightScrollPane = new ScrollPane(storePlayersMenu, skin);
		rightScrollPane.setFlickScroll(true);
		rightScrollPane.setColor(overlayColour);

		currentFunds = dataSource.getProfilesTableManager()
				.getProfile(humanProfileID).getFunds();
		fundsDisplay = currentFunds;
		fundsLabel = new Label(String.valueOf(fundsDisplay), skin);

		Table playersLayout = new Table(skin);
		playersLayout.defaults().spaceBottom(2.5f).prefHeight(17.5f);

		playersLayout.row().expandX();
		playersLayout.add(labelTitle).colspan(2);

		playersLayout.row();
		playersLayout.add("Current Team");
		playersLayout.add("Available Players");

		playersLayout.row().fillX().expandX().prefHeight(280.5f);
		playersLayout.add(leftScrollPane).prefWidth(resolutionX / 2);
		playersLayout.add(rightScrollPane).prefWidth(resolutionX / 2);

		playersLayout.row().fillX().expandX().prefHeight(17.5f);
		playersLayout.add("Available Funds:").prefWidth(resolutionX / 2);
		playersLayout.add(fundsLabel).prefWidth(resolutionX / 2).fillX()
				.expandX();

		playersLayout.pack();
		playersLayout.setBackground((Drawable) null);

		playersLayout.setPosition(0, resolutionY - playersLayout.getHeight());

		stage.addActor(playersLayout);

		backButton = new TextButton("Back to menu", skin, "default");
		buyButton = new TextButton("Buy player", skin, "default");

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
		startButtonLayout.add(buyButton).expandX()
				.prefWidth(resolutionX * 2 / 7);

		startButtonLayout.setPosition(resolutionX - resolutionX * 2 / 7 - 20,
				32);
		startButtonLayout.pack();
		startButtonLayout.setBackground((Drawable) null);

		stage.addActor(backButtonLayout);
		stage.addActor(startButtonLayout);

		float statsDisplayHeight = 32 * 7;
		// The button Y in the virtual resolution coordinates
		float backButtonY = (resolutionY - backButtonLayout.getY()
				- backButtonLayout.getHeight() - 10)
				/ resolutionY;

		Player leftPlayer = teamPlayersList.size() > 0 ? teamPlayersList.get(0)
				: null;
		Player rightPlayer = storePlayersList.size() > 0 ? storePlayersList
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

		backButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});

		buyButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				// Get selected player
				int selectedPlayerIndex = storePlayersMenu.getSelectedIndex();

				if (selectedPlayerIndex == -1
						|| selectedPlayerIndex > storePlayersMenu.getItems().length - 1) {
					return;
				}

				Player selectedPlayer = storePlayersList
						.get(selectedPlayerIndex);

				// Check person can afford the player
				int playerCost = selectedPlayer.getPlayerCost();
				if (currentFunds < playerCost) {
					// Cannot afford, display a message.
					actionResolver.showShortToast("Can't afford this player!");
					return;
				}

				// Update to be in team
				selectedPlayer.setTeamID(humanTeamID);
				dataSource.getPlayersTableManager()
						.updatePlayer(selectedPlayer);

				// Subtract funds from profile
				dataSource.getProfilesTableManager().addFundsToProfile(
						humanProfileID, -playerCost);

				// Swap the players in the array lists
				teamPlayersList.add(selectedPlayer);
				storePlayersList.remove(selectedPlayer);

				// Set the menus to the new array lists
				teamPlayersMenu.setItems(teamPlayersList.toArray());
				storePlayersMenu.setItems(storePlayersList.toArray());

				// Set new selected indexes
				teamPlayersMenu.setSelectedIndex(teamPlayersList.size() - 1);
				if (storePlayersList.size() > 0) {
					storePlayersMenu.setSelectedIndex(0);
				}

				// Update stats displays
				statsDisplayAreaLeft.setPlayer(selectedPlayer);
				statsDisplayAreaRight.setPlayer(null);

				// Edit funds display
				currentFunds -= selectedPlayer.getPlayerCost();
				subtractionVelocity = selectedPlayer.getPlayerCost() / 2;

			}
		});

		teamPlayersMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = teamPlayersMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1
						|| selectedPlayerIndex > teamPlayersMenu.getItems().length - 1) {
					return;
				}

				Player selectedPlayer = teamPlayersList
						.get(selectedPlayerIndex);
				statsDisplayAreaLeft.setPlayer(selectedPlayer);
			}
		});

		storePlayersMenu.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				int selectedPlayerIndex = storePlayersMenu
						.getSelectedIndex();

				if (selectedPlayerIndex == -1) {
					return;
				}

				Player selectedPlayer = storePlayersList
						.get(selectedPlayerIndex);
				statsDisplayAreaRight.setPlayer(selectedPlayer);
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


	public void draw() {

		// Gdx.gl.glViewport(xOffset, yOffset, drawnPitchWidth, drawnPitchHeight
		// + (int) bar.getHeight());
		// Gdx.gl.glViewport(0, 0, (int) resolutionX, (int) resolutionY);
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

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

	@Override
	public boolean onHover(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// int pitchHeight = (int) (height - bar.getHeight());
		// double screenRatio = (double) width / (double) pitchHeight;
		// double pitchImageRatio = (double) VIRTUAL_SCREEN_WIDTH
		// / (double) VIRTUAL_SCREEN_HEIGHT;
		//
		// if (width > pitchHeight) {
		// // Need to draw pitch on it's side
		// }
		//
		// if (screenRatio > pitchImageRatio) {
		// // Borders to left and right
		// scaleFactor = (double) pitchHeight / (double) VIRTUAL_SCREEN_HEIGHT;
		// drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * scaleFactor);
		// drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * scaleFactor);
		// xOffset = (width - drawnPitchWidth) / 2;
		// yOffset = 0;
		// } else {
		// // Borders top and bottom
		// scaleFactor = (double) width / (double) VIRTUAL_SCREEN_WIDTH;
		// drawnPitchWidth = (int) (VIRTUAL_SCREEN_WIDTH * scaleFactor);
		// drawnPitchHeight = (int) (VIRTUAL_SCREEN_HEIGHT * scaleFactor);
		// xOffset = 0;
		// yOffset = (pitchHeight - drawnPitchHeight) / 2;
		// }

	}

	@Override
	public void render() {
		float time = Gdx.graphics.getDeltaTime();
		this.update(time);
		this.draw();
	}

	public void update(float time) {
		fundsDisplay = fundsDisplay > currentFunds ? (int) (fundsDisplay - subtractionVelocity
				* time)
				: currentFunds;
		fundsLabel.setText(String.valueOf(fundsDisplay));
		stage.act(time);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

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
			Gdx.app.exit();
			return true;
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
