package com.samsung.comp.football.TitleScreen;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.SoundManager;
import com.samsung.comp.input.SPenInput;
import com.samsung.spen.lib.input.SPenEventLibrary;

public class GameActivity extends AndroidApplication {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		boolean useGL2 = false;
		Game game = new Game();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		SoundManager soundManager = new SoundManager(audioManager);

		SPenInput sPenInputStrategy = new SPenInput(game);
		game.setInputStrategy(sPenInputStrategy);
		game.setSoundManager(soundManager);
		View gameView = initializeForView(game, useGL2);
		SPenEventLibrary spen = new SPenEventLibrary();
		spen.setSPenTouchListener(gameView, sPenInputStrategy);
		spen.setSPenHoverListener(gameView, sPenInputStrategy);
		setContentView(gameView);
	}
}
