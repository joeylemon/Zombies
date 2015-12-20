package com.pwncraftpvp.zombies.creator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.utils.Utils;

public class WindowCreator extends Creator {

	public WindowCreator(Player player, Map map){
		super(player, map);
	}
	
	private int area;
	private int window;
	
	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the area id of the window.");
		}else if(step == 2){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the block of the window.");
		}else if(step == 3){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the sign of the window.");
		}else{
			zplayer.sendMessage("You have created a new window.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the area id
	 * @param id - The area id
	 */
	public void setAreaID(int id){
		this.area = id;
		this.window = Utils.getNextWindowID(map, area);
		
		zplayer.sendMessage("You have set the area id to " + red + id + gray + ".");
		this.advanceStep();
	}
	
	/**
	 * Set the window block
	 * @param loc - The location of the block
	 */
	public void setWindowBlock(Location loc){
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".x", loc.getBlockX());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".y", loc.getBlockY());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".z", loc.getBlockZ());
		main.saveConfig();
		
		zplayer.sendMessage("You have set the window block.");
		this.advanceStep();
	}
	
	/**
	 * Set the window sign
	 * @param loc - The location of the sign
	 */
	public void setWindowSign(Location loc){
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".sign.x", loc.getBlockX());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".sign.y", loc.getBlockY());
		main.getConfig().set("maps." + map.getName() + ".areas." + area + ".windows." + window + ".sign.z", loc.getBlockZ());
		main.saveConfig();
		
		zplayer.sendMessage("You have set the window sign.");
		this.advanceStep();
	}
	
	public EditorItem getEditorItem(){
		return EditorItem.WINDOW_CREATOR;
	}

}
