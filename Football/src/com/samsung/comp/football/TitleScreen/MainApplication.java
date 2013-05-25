package com.samsung.comp.football.TitleScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.samsung.comp.football.R;

public class MainApplication extends Activity {

	Button startTutorialButton;
	Button startGameButton;
	Button howToPlay;
	Button aboutUs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use the music stream
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// set the music stream to be the same as the system stream
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM),
				AudioManager.MODE_NORMAL);

		setContentView(R.layout.activity_main);

		startTutorialButton = (Button) findViewById(R.id.startTutorialGame);
		startTutorialButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						TutorialGameActivity.class));
			}
		});

		startGameButton = (Button) findViewById(R.id.startGame);
		startGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						GameActivity.class));
			}
		});

		howToPlay = (Button) findViewById(R.id.howToPlay);
		howToPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						InstructionsActivity.class));
			}
		});

		aboutUs = (Button) findViewById(R.id.aboutUs);
		aboutUs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						AboutUsActivity.class));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
