package com.samsung.comp.football;

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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TeamSetupScreen extends TextArea {

	String[] listEntries = { "This is a list entry", "And another one",
			"The meaning of life", "Is hard to come by",
			"This is a list entry", "And another one", "The meaning of life",
			"Is hard to come by", "This is a list entry", "And another one",
			"The meaning of life", "Is hard to come by",
			"This is a list entry", "And another one", "The meaning of life",
			"Is hard to come by", "This is a list entry", "And another one",
			"The meaning of life", "Is hard to come by" };

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	Texture texture1;
	Texture texture2;
	Label fpsLabel;

	public TeamSetupScreen(AbstractGame game) {
		create();
	}

	public void create() {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		texture1 = new Texture(Gdx.files.internal("redSelect.png"));
		texture2 = new Texture(Gdx.files.internal("blueSelect.png"));
		TextureRegion image = new TextureRegion(texture1);
		TextureRegion imageFlipped = new TextureRegion(image);
		imageFlipped.flip(true, true);
		TextureRegion image2 = new TextureRegion(texture2);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				false);
		Gdx.input.setInputProcessor(stage);

		// Group.debug = true;

		ImageButtonStyle style = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(image);
		style.imageDown = new TextureRegionDrawable(imageFlipped);
		ImageButton iconButton = new ImageButton(style);

		Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin,
				"toggle");
		Button imgButton = new Button(new Image(image), skin);
		Button imgToggleButton = new Button(new Image(image), skin, "toggle");

		Label myLabel = new Label("this is some text.", skin);
		myLabel.setWrap(true);

		Table t = new Table();
		t.row();
		t.add(myLabel);

		t.layout();

		CheckBox checkBox = new CheckBox("Check me", skin);
		final Slider slider = new Slider(0, 10, 1, false, skin);
		TextField textfield = new TextField("", skin);
		textfield.setMessageText("Click here!");
		SelectBox dropdown = new SelectBox(new String[] { "Android", "Windows",
				"Linux", "OSX", "Android", "Windows", "Linux", "OSX",
				"Android", "Windows", "Linux", "OSX", "Android", "Windows",
				"Linux", "OSX", "Android", "Windows", "Linux", "OSX",
				"Android", "Windows", "Linux", "OSX", "Android", "Windows",
				"Linux", "OSX" }, skin);
		Image imageActor = new Image(image2);
		ScrollPane scrollPane = new ScrollPane(imageActor);
		List list = new List(listEntries, skin);
		ScrollPane scrollPane2 = new ScrollPane(list, skin);
		scrollPane2.setFlickScroll(true);
		SplitPane splitPane = new SplitPane(scrollPane, scrollPane2, false,
				skin, "default-horizontal");
		fpsLabel = new Label("fps:", skin);

		// configures an example of a TextField in password mode.
		final Label passwordLabel = new Label("Textfield in password mode: ",
				skin);
		final TextField passwordTextField = new TextField("", skin);
		passwordTextField.setMessageText("password");
		passwordTextField.setPasswordCharacter('*');
		passwordTextField.setPasswordMode(true);

		// window.debug();
		Window window = new Window("Dialog", skin);
		// Table window = new Table();
		// window.setPosition(Game.VIRTUAL_SCREEN_WIDTH,
		// Game.VIRTUAL_SCREEN_HEIGHT);
		window.setPosition(0, 0);
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		window.add(iconButton);
		window.add(buttonMulti);
		window.add(imgButton);
		window.add(imgToggleButton);
		window.row();
		window.add(checkBox);
		window.add(slider).minWidth(100).fillX().colspan(3);
		window.row();
		window.add(dropdown);
		window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
		window.row();
		window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
		window.row();
		window.add(passwordLabel).colspan(2);
		window.add(passwordTextField).minWidth(100).expandX().fillX()
				.colspan(2);
		window.row();
		window.add(fpsLabel).colspan(4);
		// window.scale(1);
		window.pack();

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
