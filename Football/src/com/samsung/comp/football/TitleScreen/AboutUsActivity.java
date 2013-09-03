package com.samsung.comp.football.TitleScreen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.samsung.comp.precisionfootball.R;

public class AboutUsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		TextView text = (TextView) findViewById(R.id.textView);
		text.setText("Penpoint Football was built by Team Null.\n\n\n\nWe would love to hear what you think, contact us at contact.nullsoft@gmail.com\n\n\n\nYou are using version 0.1.");
	}
}
