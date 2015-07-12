package com.pwncraftpvp.zombies.game;

import org.bukkit.Location;

public class Window {
	
	private int id;
	private Location loc;
	public Window(int id, Location loc){
		this.id = id;
		this.loc = loc;
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

}
