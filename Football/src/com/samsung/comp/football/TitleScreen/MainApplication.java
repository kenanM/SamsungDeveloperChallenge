package com.samsung.comp.football.TitleScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.samsung.comp.football.R;
import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.comp.football.data.PlayerDatabaseHelper;

public class MainApplication extends Activity {

	Button startTutorialButton;
	Button startGameButton;
	Button startMultiplayerButton;
	Button howToPlay;
	Button aboutUs;

	boolean statusBarAtTop = false;
	float roundTime = 5f;
	float matchTime = 3 * 60;
	byte scoreLimit = -1;

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
				Intent gameIntent = new Intent(MainApplication.this,
						GameActivity.class);
				gameIntent.putExtra("Round_Time", roundTime);
				gameIntent.putExtra("Match_Time", matchTime);
				gameIntent.putExtra("Score_Limit", scoreLimit);
				gameIntent.putExtra("Status_Bar_Top", statusBarAtTop);

				startActivity(gameIntent);
			}
		});

		startMultiplayerButton = (Button) findViewById(R.id.startMultiplayerGame);
		startMultiplayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent multiplayerGameIntent = new Intent(MainApplication.this,
						MultiplayerGameActivity.class);
				multiplayerGameIntent.putExtra("Round_Time", roundTime);
				multiplayerGameIntent.putExtra("Match_Time", matchTime);
				multiplayerGameIntent.putExtra("Score_Limit", scoreLimit);
				multiplayerGameIntent
						.putExtra("Status_Bar_Top", statusBarAtTop);

				startActivity(multiplayerGameIntent);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.statusBarTop:
			item.setChecked(!item.isChecked());
			statusBarAtTop = item.isChecked();
			return true;
		case R.id.statusBarBottom:
			item.setChecked(!item.isChecked());
			statusBarAtTop = !item.isChecked();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// This is called every time the menu button is pressed.
		return super.onPrepareOptionsMenu(menu);
	}
}
