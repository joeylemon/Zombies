package com.pwncraftpvp.zombies.game;

public class Ammo {
	
	private Weapon weapon;
	private int magazine;
	private int total;
	public Ammo(Weapon weapon, int magazine, int total){
		this.weapon = weapon;
		this.magazine = magazine;
		this.total = total;
	}
	
	/**
	 * Get the weapon
	 * @return The weapon
	 */
	public Weapon getWeapon(){
		return weapon;
	}
	
	/**
	 * Get the rounds in the magazine
	 * @return The rounds in the magazine
	 */
	public int getMagazine(){
		return magazine;
	}
	
	/**
	 * Set the rounds in the magazine
	 * @param value - The rounds in the magazine
	 */
	public void setMagazine(int value){
		magazine = value;
	}
	
	/**
	 * Get the total amount of rounds
	 * @return The total amount of rounds
	 */
	public int getTotal(){
		return total;
	}
	
	/**
	 * Set the total amount of rounds
	 * @param value - The total amount of rounds
	 */
	public void setTotal(int value){
		total = value;
	}

}
