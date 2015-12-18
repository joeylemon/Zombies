package com.pwncraftpvp.zombies.game;

import org.apache.commons.lang3.text.WordUtils;


public enum PowerUpType {
	
	MAX_AMMO,
	INSTA_KILL,
	DOUBLE_POINTS,
	NUKE,
	CARPENTER;
	
	private String name = null;
	
	/**
	 * Get the name of the power up
	 * @return The name of the power up
	 */
	public String getName(){
		if(name == null){
			name = WordUtils.capitalizeFully(this.toString().replace("_", " "));
		}
		return name;
	}

}
