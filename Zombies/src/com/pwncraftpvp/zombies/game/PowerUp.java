package com.pwncraftpvp.zombies.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class PowerUp {
	
	private Main main = Main.getInstance();
	
	private Location loc;
	private PowerUpType type;
	private EnderCrystal crystal;
	private Hologram hologram;
	public PowerUp(Location loc, PowerUpType type){
		this.loc = loc;
		this.type = type;
		
		this.crystal = (EnderCrystal) Utils.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
		
		this.hologram = HologramsAPI.createHologram(main, loc.add(0, 2.3, 0));
		this.hologram.appendTextLine(ChatColor.DARK_GREEN + type.getName());
	}
	
	/**
	 * Get the location of the power up
	 * @return The location of the power up
	 */
	public Location getLocation(){
		return loc;
	}
	
	/**
	 * Get the type of the power up
	 * @return The type of the power up
	 */
	public PowerUpType getType(){
		return type;
	}
	
	/**
	 * Get the crystal of the power up
	 * @return The crystal of the power up
	 */
	public EnderCrystal getEnderCrystal(){
		return crystal;
	}
	
	/**
	 * Get the hologram of the power up
	 * @return The hologram of the power up
	 */
	public Hologram getHologram(){
		return hologram;
	}
	
	/**
	 * Remove the power up
	 */
	public void remove(){
		crystal.remove();
		hologram.delete();
	}

}
