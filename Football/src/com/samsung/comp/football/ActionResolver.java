package com.samsung.comp.football;

import com.samsung.comp.football.data.PlayerDataSource;

public interface ActionResolver {
	public void openGuideBook();

	public void openGuideBook(int page);

	public PlayerDataSource openDatasource();
}
