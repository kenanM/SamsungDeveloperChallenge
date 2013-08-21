package com.samsung.comp.football;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.data.PlayerDataSource;

public class TeamSetupScreen extends TextArea {
	private AbstractGame game;
	private Skin skin;
	private Stage stage;
	private Texture texture1;

	java.util.List<Player> fieldedPlayersList;
	java.util.List<Player> benchedPlayersList;

	private Player goalKeeper;

	List benchedPlayersListMenu;
	List fieldedPlayersListMenu;

	private PlayerDataSource dataSource;

	float resolutionX = Game.VIRTUAL_SCREEN_WIDTH / 2;
	float resolutionY = (Game.VIRTUAL_SCREEN_HEIGHT + 64) / 2;

	ScrollPane leftScrollPane;
	ScrollPane rightScrollPane;

	public TeamSetupScreen(AbstractGame game, PlayerDataSource dataSource) {
		this.game = game;
		this.dataSource = dataSource;
		create();
	}

	public void create() {
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		texture1 = new Texture(Gdx.files.internal("redSelect.png"));
		TextureRegion image = new TextureRegion(texture1);
		TextureRegion imageFlipped = new TextureRegion(image);
		imageFlipped.flip(true, true);
		stage = new Stage(resolutionX, resolutionY,
				false);

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

		Team playerTeam = null;
		ArrayList<String> aiTeamsList = new ArrayList<String>();
		java.util.List<Team> allTeams = dataSource.getTeamsTableManager()
				.getAllTeams();

		for (Team t : allTeams) {
			if (t.getTeamID() == 1) {
				playerTeam = t;
			} else {
				aiTeamsList.add(t.getTeamName());
			}
		}

		String teamName = "Unnamed";
		if (playerTeam != null) {
			playerTeam.getTeamName();
		} else {
			Gdx.app.error("GameDB", "No team found for the person");
			throw new NullPointerException("No team found for the person");
		}

		Gdx.input.setInputProcessor(stage);

		TextField textfield = new TextField(teamName, skin);
		textfield.setMessageText("Team Name");

		benchedPlayersListMenu = new List(benchedPlayersList.toArray(), skin);
		fieldedPlayersListMenu = new List(fieldedPlayersList.toArray(), skin);

		leftScrollPane = new ScrollPane(fieldedPlayersListMenu, skin);
		leftScrollPane.setFlickScroll(true);

		rightScrollPane = new ScrollPane(benchedPlayersListMenu, skin);
		rightScrollPane.setFlickScroll(true);

		SelectBox aiTeamSelection = new SelectBox(
				aiTeamsList.toArray(new String[] {}), skin);

		final Slider slider = new Slider(0, 2, 1, false, skin);

		ImageButtonStyle style = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(image);
		style.imageDown = new TextureRegionDrawable(imageFlipped);
		ImageButton iconButton = new ImageButton(style);

		Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin,
				"toggle");


		Window window = new Window("Dialog", skin);
		// Table window = new Table();



		window.debug();

		window.defaults().spaceBottom(2.5f).prefHeight(17.5f);

		window.row().fillX().expandX();
		window.add(textfield).minWidth(25).expandX().fillX().colspan(2);

		window.row().fillX().expandX().prefHeight(150);
		window.add(leftScrollPane).prefWidth(resolutionX / 2);
		window.add(rightScrollPane).prefWidth(resolutionX / 2);

		window.row();
		window.add(iconButton);
		window.add(iconButton);

		window.row();
		window.add(aiTeamSelection);
		window.add(slider).minWidth(25).fillX();

		window.row();

		// window.scale(1);
		window.pack();
		window.setPosition(0, resolutionY - window.getHeight());
		window.setBackground((Drawable) null);

		// stage.addActor(new Button("Behind Window", skin));
		stage.addActor(window);

		textfield.setTextFieldListener(new TextFieldListener() {
			public void keyTyped(TextField textField, char key) {
				if (key == '\n')
					textField.getOnscreenKeyboard().show(false);
			}
		});

		slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("UITest", "slider: " + slider.getValue());
			}
		});

		iconButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {

				new Dialog("Some Dialog", skin, "dialog") {
					protected void result(Object object) {
						System.out.println("Chosen: " + object);
					}
				}.text("Are you enjoying this demo?").button("Yes", true)
						.button("No", false).key(Keys.ENTER, true)
						.key(Keys.ESCAPE, false).show(stage);

				if (fieldedPlayersListMenu.getItems().length == 0
						|| benchedPlayersListMenu.getItems().length == 0
						|| fieldedPlayersListMenu.getSelectedIndex() == -1
						|| benchedPlayersListMenu.getSelectedIndex() == -1) {
					return;
				}

				Player fieldedPlayer = fieldedPlayersList
						.get(fieldedPlayersListMenu.getSelectedIndex());
				Player benchedPlayer = benchedPlayersList
						.get(benchedPlayersListMenu.getSelectedIndex());

				fieldedPlayersList.set(
						fieldedPlayersListMenu.getSelectedIndex(),
						benchedPlayer);
				benchedPlayersList.set(
						benchedPlayersListMenu.getSelectedIndex(),
						fieldedPlayer);

				fieldedPlayersListMenu.setItems(fieldedPlayersList.toArray());
				benchedPlayersListMenu.setItems(benchedPlayersList.toArray());
			}

		});
	}

	@Override
	public void update(float time) {
		stage.act(time);


	}

	@Override
	public void draw(SpriteBatch batch, BitmapFont unusedBmf,
			ShapeRenderer renderer) {

		Gdx.gl.glEnable(GL10.GL_BLEND);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.27f, 0.27f, 0.35f, 0.70f);

		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		stage.draw();
		Table.drawDebug(stage);
		Window.drawDebug(stage);

	}

	@Override
	public void onPress(float x, float y) {
	}

}
