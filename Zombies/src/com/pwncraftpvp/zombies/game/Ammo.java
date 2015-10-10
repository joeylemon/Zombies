package com.pwncraftpvp.zombies.game;

public class Ammo {
	
	private WeaponType type;
	private int magazine;
	private int total;
	public Ammo(WeaponType type, int magazine, int total){
		this.type = type;
		this.magazine = magazine;
		this.total = total;
	}
	
	/**
	 * Get the weapon type
	 * @return The weapon type
	 */
	public WeaponType getWeaponType(){
		return type;
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
