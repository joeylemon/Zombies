package com.pwncraftpvp.zombies.creator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.utils.Utils;

public class ZombieSpawnCreator extends Creator {
	
	public ZombieSpawnCreator(Player player, Map map) {
		super(player, map);
	}
	
	private int area;
	private int spawn;
	
	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the area of the spawn.");
		}else if(step == 2){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click at the spawn location.");
		}else{
			zplayer.sendMessage("You have created a new zombie spawn.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the area id
	 * @param id - The area id
	 */
	public void setAreaID(int id){
		this.area = id;
		this.spawn = Utils.getNextZombieSpawnID(map, area);
		
		zplayer.sendMessage("You have set the area id to " + red + id + gray + ".");
		this.advanceStep();
	}
	
	/**
	 * Set the spawn location
	 * @param loc - The location
	 */
	public void setSpawnLocation(Location loc){
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".spawns." + spawn + ".x", loc.getX());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".spawns." + spawn + ".y", loc.getY());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".spawns." + spawn + ".z", loc.getZ());
		main.saveConfig();
		
		zplayer.sendMessage("You have set the spawn location.");
		this.advanceStep();
	}
	
	public EditorItem getEditorItem(){
		return EditorItem.ZOMBIE_SPAWN_CREATOR;
	}
	
}
