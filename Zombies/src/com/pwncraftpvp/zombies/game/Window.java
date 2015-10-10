package com.pwncraftpvp.zombies.game;

import org.bukkit.Location;

public class Window {
	
	private int id;
	private Location loc;
	private Location sign;
	public Window(int id, Location loc, Location sign){
		this.id = id;
		this.loc = loc;
		this.sign = sign;
	}
	
	/**
	 * Get the id of the window
	 * @return The id of the window
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get the location of the window
	 * @return The location of the window
	 */
	public Location getLocation(){
		return loc;
	}
	
	/**
	 * Get the repair sign location
	 * @return The repair sign location
	 */
	public Location getSignLocation(){
		return sign;
	}

}
