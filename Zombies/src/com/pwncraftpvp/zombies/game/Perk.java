package com.pwncraftpvp.zombies.game;

import org.apache.commons.lang3.text.WordUtils;

public enum Perk {
	
	JUGGERNOG(CustomSound.JUGGERNOG_PURCHASE, 2500),
	QUICK_REVIVE(CustomSound.QUICK_REVIVE_PURCHASE, 1500),
	DOUBLE_TAP(CustomSound.DOUBLE_TAP_PURCHASE, 2000),
	SPEED_COLA(CustomSound.SPEED_COLA_PURCHASE, 3000);
	
	private CustomSound sound;
	private int price;
	Perk(CustomSound sound, int price){
		this.sound = sound;
		this.price = price;
	}
	
	private String name = null;
	
	/**
	 * Get the sound of the perk's purchase
	 * @return The sound of the perk's purchase
	 */
	public CustomSound getPurchaseSound(){
		return sound;
	}
	
	/**
	 * Get the price of the perk
	 * @return The price of the perk
	 */
	public int getPrice(){
		return price;
	}
	
	/**
	 * Get the proper name of the perk
	 * @return The proper name of the perk
	 */
	public String getName(){
		if(name == null){
			name = WordUtils.capitalizeFully(this.toString().replace("_", " "));
		}
		
		return name;
	}

}
