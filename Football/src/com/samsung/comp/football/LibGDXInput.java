package com.samsung.comp.football;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class LibGDXInput implements InputProcessor{

	private final Game game;

	public LibGDXInput(Game game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode==Keys.BACK){
			game.backButtonPressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
