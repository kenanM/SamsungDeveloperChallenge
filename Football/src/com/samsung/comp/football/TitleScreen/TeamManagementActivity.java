package com.samsung.comp.football.TitleScreen;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.samsung.comp.football.ActionResolver;
import com.samsung.comp.football.TeamManagement;
import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.spen.lib.input.SPenEventLibrary;

public class TeamManagementActivity extends AndroidApplication {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;

		TeamManagement screen;
		ActionResolver actionResolver = new ActionResolverAndroid(this);

		screen = new TeamManagement(new PlayerDataSource(this), actionResolver);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		View gameView = initializeForView(screen, cfg);
		SPenEventLibrary spen = new SPenEventLibrary();
		spen.setSPenHoverListener(gameView, screen);
		setContentView(gameView);
	}
}
