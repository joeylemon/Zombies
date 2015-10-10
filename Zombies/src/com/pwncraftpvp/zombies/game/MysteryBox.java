package com.pwncraftpvp.zombies.game;

import java.util.List;

import org.bukkit.Location;

public class MysteryBox {
	
	private int id;
	private List<Location> blocks;
	private Location light;
	public MysteryBox(int id, List<Location> blocks, Location light){
		this.id = id;
		this.blocks = blocks;
		this.light = light;
	}
	
	/**
	 * Get the id of the mystery box
	 * @return The id of the mystery box
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get the chest blocks of the box
	 * @return The chest blocks of the box
	 */
	public List<Location> getBlocks(){
		return blocks;
	}
	
	/**
	 * Get the location of the light
	 * @return The location of the light
	 */
	public Location getLightLocation(){
		return light;
	}

}
