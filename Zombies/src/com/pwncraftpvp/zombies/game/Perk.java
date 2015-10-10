package com.pwncraftpvp.zombies.game;

import org.apache.commons.lang3.text.WordUtils;

public enum Perk {
	
	JUGGERNOG(2500),
	QUICK_REVIVE(1500),
	DOUBLE_TAP(2000),
	SPEED_COLA(3000);
	
	private int price;
	Perk(int price){
		this.price = price;
	}
	
	private String name = null;
	
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
