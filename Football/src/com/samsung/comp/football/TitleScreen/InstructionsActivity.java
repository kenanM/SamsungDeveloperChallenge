package com.samsung.comp.football.TitleScreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.samsung.comp.football.R;

public class InstructionsActivity extends Activity {

	ImageView image;

	int page = 0;

	Integer[] imageIDs = new Integer[] { R.drawable.screen_one,
			R.drawable.screen_two, R.drawable.screen_three,
			R.drawable.screen_four, R.drawable.screen_five,
			R.drawable.screen_six };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);

		// text = (TextView) findViewByID(R.id.instructions);

		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				nextInstruction();
			}
		});

		loadInstructions();
	}

	private void loadInstructions() {
		image.setImageResource(imageIDs[page]);
	}

	private void nextInstruction() {
		if (page == imageIDs.length - 1) {
			finish();
		} else {
			page++;
		}
		loadInstructions();
	}
}