package com.pwncraftpvp.zombies.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum CustomSound {
	
	/*
	 * Weapon sounds
	 */
	RIFLE_SHOT("weapons.rifle_shot"),
	PISTOL_SHOT("weapons.pistol_shot"),
	RAY_GUN_SHOT("weapons.ray_gun_shot"),
	WUNDERWAFFE_SHOT("weapons.wunderwaffe_shot"),
	LMG_SHOT("weapons.lmg_shot"),
	KNIFE_HIT("weapons.knife_hit"),
	
	GRENADE_EXPLODE("weapons.grenade_explode"),
	MONKEY_BOMB_MUSIC("weapons.monkey_bomb_music"),
	
	RELOAD_1("weapons.reload_1"),
	RELOAD_2("weapons.reload_2"),
	
	/*
	 * Perk sounds
	 */
	JUGGERNOG_PURCHASE("perks.juggernog_purchase"),
	QUICK_REVIVE_PURCHASE("perks.quickrevive_purchase"),
	SPEED_COLA_PURCHASE("perks.speedcola_purchase"),
	DOUBLE_TAP_PURCHASE("perks.doubletap_purchase"),
	
	/*
	 * Power-up sounds
	 */
	CARPENTER_PICK_UP("powerups.carpenter_pick_up"),
	DOUBLE_POINTS_PICK_UP("powerups.double_points_pick_up"),
	FIRE_SALE_PICK_UP("powerups.fire_sale_pick_up"),
	FIRE_SALE_MUSIC("powerups.fire_sale_music"),
	INSTA_KILL_PICK_UP("powerups.insta_kill_pick_up"),
	MAX_AMMO_PICK_UP("powerups.max_ammo_pick_up"),
	NUKE_PICK_UP("powerups.nuke_pick_up"),
	
	/*
	 * General sounds
	 */
	MYSTERY_BOX_OPEN("general.mystery_box_open"),
	MYSTERY_BOX_BREAK_1("general.mystery_box_break_1"),
	MYSTERY_BOX_BREAK_2("general.mystery_box_break_2"),
	
	ROUND_BEGIN("general.round_begin"),
	ROUND_END("general.round_end"),
	DOG_ROUND_BEGIN("general.dog_round_begin"),
	DOG_ROUND_END("general.dog_round_end"),
	DOG_SPAWN("general.dog_spawn"),
	
	DOOR_OPEN("general.door_open"),
	WINDOW_REPAIR("general.window_repair"),
	PACK_A_PUNCH_USE("general.pack_a_punch"),
	POWER_ENABLE("general.power_enable"),
	TELEPORTER_USE("general.teleporter_use"),
	RANK_UP("general.rank_up");
	
	
	private String path;
	CustomSound(String path){
		this.path = path;
	}
	
	/**
	 * Get the path to the sound
	 * @return The path to the sound
	 */
	public String getPath(){
		return path;
	}
	
	/**
	 * Play the sound at a location
	 * @param loc - The location to play at
	 */
	@SuppressWarnings("deprecation")
	public void play(Location loc){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
				p.playSound(loc, path, 3F, 1F);
			}
		}
	}
	
	/**
	 * Play the sound to a player
	 * @param player - The player to play to
	 */
	@SuppressWarnings("deprecation")
	public void play(Player player){
		player.playSound(player.getEyeLocation(), path, 3F, 1F);
	}
	
	/**
	 * Play the sound to all players
	 */
	@SuppressWarnings("deprecation")
	public void playGlobally(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
				p.playSound(p.getEyeLocation(), path, 3F, 1F);
			}
		}
	}
	
	/**
	 * Play the sound to all players with a given volume
	 * @param volume - The volume
	 */
	@SuppressWarnings("deprecation")
	public void playGlobally(float volume){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
				p.playSound(p.getEyeLocation(), path, volume, 1F);
			}
		}
	}

}
