package com.samsung.comp.football.TitleScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.samsung.comp.precisionfootball.R;

public class MainApplication extends Activity {

	ImageButton startTutorialButton;
	ImageButton startGameButton;
	ImageButton startMultiplayerButton;
	ImageButton howToPlay;
	ImageButton manageTeam;
	ImageButton playerStore;

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

		startTutorialButton = (ImageButton) findViewById(R.id.startTutorialGame);
		startTutorialButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						TutorialGameActivity.class));
			}
		});

		startGameButton = (ImageButton) findViewById(R.id.startGame);
		startGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent gameIntent = new Intent(MainApplication.this,
						GameActivity.class);
				gameIntent.putExtra("Round_Time", roundTime);
				gameIntent.putExtra("Score_Limit", scoreLimit);
				gameIntent.putExtra("Status_Bar_Top", statusBarAtTop);

				startActivity(gameIntent);
			}
		});

		startMultiplayerButton = (ImageButton) findViewById(R.id.startMultiplayerGame);
		startMultiplayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent multiplayerGameIntent = new Intent(MainApplication.this,
						MultiplayerGameActivity.class);
				multiplayerGameIntent.putExtra("Round_Time", roundTime);
				multiplayerGameIntent.putExtra("Score_Limit", scoreLimit);
				multiplayerGameIntent
						.putExtra("Status_Bar_Top", statusBarAtTop);

				showDialog(MainApplication.this, multiplayerGameIntent).show();
			}
		});

		howToPlay = (ImageButton) findViewById(R.id.howToPlay);
		howToPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						InstructionsActivity.class));
			}
		});

		manageTeam = (ImageButton) findViewById(R.id.manageTeam);
		manageTeam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						TeamManagementActivity.class));
			}
		});

		playerStore = (ImageButton) findViewById(R.id.playerStore);
		playerStore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainApplication.this,
						PlayerStoreActivity.class));
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
		return false;
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

	private AlertDialog showDialog(Context context, final Intent intent) {
		CharSequence[] options = { "One Minute", "Two Minutes", "Three Minutes" };
		final float[] values = { 60f, 120f, 180f};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("How long would you like to play for?")
				.setSingleChoiceItems(options, -1,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int choice) {
								intent.putExtra("Match_Time", values[choice]);
								dialog.dismiss();
								startActivity(intent);
							}
						});
		return builder.create();
	}
}
