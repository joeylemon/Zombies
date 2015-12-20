package com.pwncraftpvp.zombies.creator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.utils.Utils;

public class BoxCreator extends Creator {

	public BoxCreator(Player player, Map map){
		super(player, map);
	}
	
	private int area;
	private int box;
	private int blockid = 1;

	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the area id of the box.");
		}else if(step == 2){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the blocks of the box.");
		}else if(step == 3){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the light block of the box.");
		}else{
			zplayer.sendMessage("You have created a new mystery box.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the area id
	 * @param id - The area id
	 */
	public void setAreaID(int id){
		this.area = id;
		this.box = Utils.getNextBoxID(map, area);
		
		zplayer.sendMessage("You have set the area id to " + red + id + gray + ".");
		this.advanceStep();
	}
	
	/**
	 * Add a block to the box
	 * @param loc - The location of the block
	 */
	public void addBoxBlock(Location loc){
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + "." + blockid + ".x", loc.getBlockX());
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + "." + blockid + ".y", loc.getBlockY());
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + "." + blockid + ".z", loc.getBlockZ());
		main.saveConfig();
		blockid++;
		if(blockid > 2){
			this.advanceStep();
		}
	}
	
	/**
	 * Add the light block to the box
	 * @param loc - The location of the light block
	 */
	public void addLightBlock(Location loc){
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + ".light.x", loc.getBlockX());
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + ".light.y", loc.getBlockY());
		main.getConfig().set("maps." + map + ".areas." + area + ".boxes." + box + ".light.z", loc.getBlockZ());
		main.saveConfig();
		this.advanceStep();
	}

}
