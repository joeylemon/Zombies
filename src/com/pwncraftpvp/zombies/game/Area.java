package com.pwncraftpvp.zombies.game;

import java.util.List;

import org.bukkit.Location;

public class Area {
	
	private int id;
	private List<Door> doors;
	private List<Window> windows;
	private List<Location> spawns;
	public Area(int id, List<Door> doors, List<Window> windows, List<Location> spawns){
		this.id = id;
		this.doors = doors;
		this.windows = windows;
		this.spawns = spawns;
	}
	
	/**
	 * Get the id of the area
	 * @return The id of the area
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get the list of doors in the area
	 * @return The list of doors in the area
	 */
	public List<Door> getDoors(){
		return doors;
	}
	
	/**
	 * Get the list of windows in the area
	 * @return The list of windows in the area
	 */
	public List<Window> getWindows(){
		return windows;
	}
	
	/**
	 * Get the spawns of the area
	 * @return The spawns of the area
	 */
	public List<Location> getSpawns(){
		return spawns;
	}

}
