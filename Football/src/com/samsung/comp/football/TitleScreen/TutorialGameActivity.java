package com.samsung.comp.football.TitleScreen;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.samsung.comp.football.SoundManager;
import com.samsung.comp.football.TutorialGame;
import com.samsung.spen.lib.input.SPenEventLibrary;

public class TutorialGameActivity extends AndroidApplication {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		boolean useGL2 = false;
		TutorialGame game = new TutorialGame();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		SoundManager soundManager = new SoundManager(audioManager);

		game.setSoundManager(soundManager);
		View gameView = initializeForView(game, useGL2);
		SPenEventLibrary spen = new SPenEventLibrary();
		spen.setSPenHoverListener(gameView, game);
		setContentView(gameView);
	}
}
