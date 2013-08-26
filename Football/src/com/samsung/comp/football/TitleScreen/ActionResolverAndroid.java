package com.samsung.comp.football.TitleScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.samsung.comp.football.ActionResolver;
import com.samsung.comp.football.data.PlayerDataSource;

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

	@Override
	public void openGuideBook(int page) {
		Intent intent = new Intent(appContext, InstructionsActivity.class);
		intent.putExtra("page", page);
		appContext.startActivity(intent);
	}

	@Override
	public PlayerDataSource openDatasource() {
		return new PlayerDataSource(appContext);

	}

	@Override
	public void showShortToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {
			public void run() {
				Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	public void showLongToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {
			public void run() {
				Toast.makeText(appContext, toastMessage, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

}
