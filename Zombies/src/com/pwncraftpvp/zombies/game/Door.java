package com.pwncraftpvp.zombies.game;

import java.util.List;

import org.bukkit.block.Block;

import com.pwncraftpvp.zombies.core.Main;

public class Door {
	
	private Main main = Main.getInstance();
	
	private int id;
	private int area;
	private String map;
	private List<Block> blocks;
	public Door(int id, int area, String map, List<Block> blocks){
		this.id = id;
		this.area = area;
		this.map = map;
		this.blocks = blocks;
	}
	
	/**
	 * Get the id of the door
	 * @return The id of the door
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get the id of the area the door is in
	 * @return The id of the area the door is in
	 */
	public int getAreaID(){
		return area;
	}
	
	/**
	 * Get the name of the map the door is in
	 * @return The name of the map the door is in
	 */
	public String getMapName(){
		return map;
	}
	
	/**
	 * Get the list of blocks in the door
	 * @return The list of blocks in the door
	 */
	public List<Block> getBlocks(){
		return blocks;
	}
	
	/**
	 * Get the price of the door
	 * @return
	 */
	public int getPrice(){
		return main.getConfig().getInt("maps." + map + ".areas." + area + ".doors." + id + ".price");
	}
	
	/**
	 * Check if a block is a part of the door
	 * @param block - The block to check
	 * @return True if the block is a part of the door, false if not
	 */
	public boolean isBlock(Block block){
		boolean is = false;
		for(Block b : blocks){
			if(b.getLocation().getBlockX() == block.getLocation().getBlockX() && 
					b.getLocation().getBlockY() == block.getLocation().getBlockY() && 
					b.getLocation().getBlockZ() == block.getLocation().getBlockZ()){
				is = true;
				break;
			}
		}
		return is;
	}

}
