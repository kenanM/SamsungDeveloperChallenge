package com.samsung.comp.football.TitleScreen;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.SoundManager;
import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.spen.lib.input.SPenEventLibrary;

public class GameActivity extends AndroidApplication {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		
		AbstractGame game;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			float roundTime = bundle.getFloat("Round_Time");
			float matchTime = bundle.getFloat("Match_Time");
			byte scoreLimit = bundle.getByte("Score_Limit");
			boolean statusBarAtTop = bundle.getBoolean("Status_Bar_Top");
			game = new Game(new PlayerDataSource(this), new ActionResolverAndroid(this), matchTime,
					roundTime, statusBarAtTop, scoreLimit);
		} else {
			game = new Game(new PlayerDataSource(this), new ActionResolverAndroid(this));
		}

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		SoundManager soundManager = new SoundManager(audioManager);

		game.setSoundManager(soundManager);
		View gameView = initializeForView(game, cfg);
		SPenEventLibrary spen = new SPenEventLibrary();
		spen.setSPenHoverListener(gameView, game);
		setContentView(gameView);
	}
}
