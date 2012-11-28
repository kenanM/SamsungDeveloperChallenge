package com.samsung.comp.football;

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
		View game = initializeForView(new Game(), useGL2);
		SPenEventLibrary spen = new SPenEventLibrary();
		InputListener inputListener = new InputListener();
		spen.setSPenTouchListener(game, inputListener);
		spen.setSPenHoverListener(game, inputListener);
		setContentView(game);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
