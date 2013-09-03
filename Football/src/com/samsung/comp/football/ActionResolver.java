package com.samsung.comp.football;

import com.samsung.comp.football.data.PlayerDataSource;
import com.samsung.comp.precisionfootball.R;

public interface ActionResolver {
	public void openGuideBook();

	public void openGuideBook(int page);

	public PlayerDataSource openDatasource();

	public void showShortToast(final CharSequence toastMessage);

	public void showLongToast(final CharSequence toastMessage);

}
