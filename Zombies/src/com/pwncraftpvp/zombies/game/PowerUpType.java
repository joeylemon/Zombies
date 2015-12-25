package com.pwncraftpvp.zombies.game;

import org.apache.commons.lang3.text.WordUtils;


public enum PowerUpType {
	
	MAX_AMMO(CustomSound.MAX_AMMO_PICK_UP),
	INSTA_KILL(CustomSound.INSTA_KILL_PICK_UP),
	DOUBLE_POINTS(CustomSound.DOUBLE_POINTS_PICK_UP),
	NUKE(CustomSound.NUKE_PICK_UP),
	CARPENTER(CustomSound.CARPENTER_PICK_UP);
	
	private CustomSound sound;
	PowerUpType(CustomSound sound){
		this.sound = sound;
	}
	
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
	
	/**
	 * Get the sound of the power up
	 * @return The sound of the power up
	 */
	public CustomSound getSound(){
		return sound;
	}

}
