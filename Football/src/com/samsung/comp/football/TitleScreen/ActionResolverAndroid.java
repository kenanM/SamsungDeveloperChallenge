package com.samsung.comp.football.TitleScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.samsung.comp.football.ActionResolver;

public class ActionResolverAndroid implements ActionResolver {
	Handler uiThread;
	Context appContext;

	public ActionResolverAndroid(Context appContext) {
		uiThread = new Handler();
		this.appContext = appContext;
	}

	@Override
	public void openGuideBook() {
		appContext.startActivity(new Intent(appContext,
				InstructionsActivity.class));
	}
}
