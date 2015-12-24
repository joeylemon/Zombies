package com.pwncraftpvp.zombies.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum CustomSound {
	
	/*
	 * Weapon sounds
	 */
	RIFLE_SHOT(Sound.VILLAGER_DEATH),
	PISTOL_SHOT(Sound.VILLAGER_HAGGLE),
	RAY_GUN_SHOT(Sound.VILLAGER_HIT),
	GRENADE_EXPLODE(Sound.VILLAGER_IDLE),
	KNIFE_HIT(Sound.VILLAGER_NO),
	LMG_SHOT(Sound.VILLAGER_YES),
	
	/*
	 * Gameplay sounds
	 */
	MYSTERY_BOX_OPEN(Sound.BAT_DEATH),
	DOOR_OPEN(Sound.BAT_HURT),
	PACK_A_PUNCH_USE(Sound.BAT_IDLE),
	POWER_ENABLE(Sound.BAT_LOOP);
	
	private Sound holder;
	CustomSound(Sound holder){
		this.holder = holder;
	}
	
	/**
	 * Get the placeholder sound
	 * @return The placeholder sound
	 */
	public Sound getPlaceholder(){
		return holder;
	}
	
	/**
	 * Play the sound at a location
	 * @param loc - The location to play at
	 */
	public void play(Location loc){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
				p.playSound(loc, holder, 10F, 1F);
			}
		}
	}
	
	/**
	 * Play the sound to all players
	 */
	public void playGlobally(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
				p.playSound(p.getEyeLocation(), holder, 10F, 1F);
			}
		}
	}

}
