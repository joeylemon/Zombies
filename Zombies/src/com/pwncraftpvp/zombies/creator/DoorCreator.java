package com.pwncraftpvp.zombies.creator;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.utils.Utils;

public class DoorCreator extends Creator {

	private int door;
	public DoorCreator(Player player, Map map){
		super(player, map);
	}
	
	private int area;

	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the area id of the door.");
		}else if(step == 2){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click all blocks of the door. Right-click when done.");
		}else if(step == 3){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the price of the door.");
		}else{
			zplayer.sendMessage("You have created a new door.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the area id
	 * @param id - The area id
	 */
	public void setAreaID(int id){
		this.area = id;
		Utils.broadcastMessage("map = " + map.getName());
		Utils.broadcastMessage("id = " + id);
		this.door = Utils.getNextDoorID(map, area);
		
		zplayer.sendMessage("You have added set the area id to " + red + id + gray + ".");
		this.advanceStep();
	}
	
	/**
	 * Add a door block
	 * @param loc - The location of the block
	 */
	public void addDoorBlock(Location loc){
		List<String> blocks = main.getConfig().getStringList("maps." + map.getName() + ".areas." + area + ".doors." + door + ".blocks");
		blocks.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".doors." + door + ".blocks", blocks);
		main.saveConfig();
		zplayer.sendMessage("You have added a block to the door.");
	}
	
	/**
	 * Set the door price
	 * @param price - The price
	 */
	public void setDoorPrice(int price){
		main.getConfig().set("maps." + map + ".areas." + area + ".doors." + door + ".price", price);
		main.saveConfig();
		
		zplayer.sendMessage("You have added set the door price to $" + red + price + gray + ".");
		this.advanceStep();
	}

}
