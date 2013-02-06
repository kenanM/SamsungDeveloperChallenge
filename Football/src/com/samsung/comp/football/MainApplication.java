package com.samsung.comp.football;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.samsung.spen.lib.input.SPenEventLibrary;

public class MainApplication extends AndroidApplication {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		boolean useGL2 = false;
		Game game = new Game();
		InputListener inputListener = new InputListener(game);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// set the music stream to be the same as the system stream
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM),
				AudioManager.FLAG_SHOW_UI);

		SoundManager soundManager = new SoundManager(audioManager);

		game.setInputListener(inputListener);
		game.setSoundManager(soundManager);
		View gameView = initializeForView(game, useGL2);
		SPenEventLibrary spen = new SPenEventLibrary();
		spen.setSPenTouchListener(gameView, inputListener);
		spen.setSPenHoverListener(gameView, inputListener);
		setContentView(gameView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
