package com.samsung.comp.football;


import android.graphics.Point;



public class Move extends Action {
	
	private Point position;
	
	
	public Move(int x, int y) {
		position = new Point(x, y);
	}

	
	// TODO This currently just teleports the player. No checks on if the space is occupied or routing are done. 
	// 
	@Override
	public boolean executeAction(Player player) {
		
		player.setPlayerPosition(position);
		
		// player moves towards the specified position,
//		int xDistance = player.getPlayerPosition().x - this.position.x;
//		int yDistance = player.getPlayerPosition().y - this.position.y;

		complete = player.getPlayerPosition() == position;
		return true;
	}
			
	
	
	
}
