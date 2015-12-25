package com.pwncraftpvp.zombies.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum CustomSound {
	
	/*
	 * Weapon sounds
	 */
	RIFLE_SHOT("bod.weapons.rifle_shot"),
	PISTOL_SHOT("bod.weapons.pistol_shot"),
	RAY_GUN_SHOT("bod.weapons.ray_gun_shot"),
	WUNDERWAFFE_SHOT("bod.weapons.wunderwaffe_shot"),
	LMG_SHOT("bod.weapons.lmg_shot"),
	KNIFE_HIT("bod.weapons.knife_hit"),
	
	GRENADE_EXPLODE("bod.weapons.grenade_explode"),
	MONKEY_BOMB_MUSIC("bod.weapons.monkey_bomb_music"),
	
	RELOAD_1("bod.weapons.reload_1"),
	RELOAD_2("bod.weapons.reload_2"),
	
	/*
	 * Perk sounds
	 */
	JUGGERNOG_PURCHASE("bod.perks.juggernog_purchase"),
	QUICK_REVIVE_PURCHASE("bod.perks.quickrevive_purchase"),
	SPEED_COLA_PURCHASE("bod.perks.speedcola_purchase"),
	
	/*
	 * Power-up sounds
	 */
	CARPENTER_PICK_UP("bod.powerups.carpenter_pick_up"),
	DOUBLE_POINTS_PICK_UP("bod.powerups.double_points_pick_up"),
	FIRE_SALE_PICK_UP("bod.powerups.fire_sale_pick_up"),
	FIRE_SALE_MUSIC("bod.powerups.fire_sale_music"),
	INSTA_KILL_PICK_UP("bod.powerups.insta_kill_pick_up"),
	MAX_AMMO_PICK_UP("bod.powerups.max_ammo_pick_up"),
	NUKE_PICK_UP("bod.powerups.nuke_pick_up"),
	
	/*
	 * General sounds
	 */
	MYSTERY_BOX_OPEN("bod.general.mystery_box_open"),
	MYSTERY_BOX_BREAK_1("bod.general.mystery_box_break_1"),
	MYSTERY_BOX_BREAK_2("bod.general.mystery_box_break_2"),
	
	ROUND_BEGIN("bod.general.round_begin"),
	ROUND_END("bod.general.round_end"),
	DOG_ROUND_BEGIN("bod.general.dog_round_begin"),
	DOG_ROUND_END("bod.general.dog_round_end"),
	
	DOOR_OPEN("bod.general.door_open"),
	PACK_A_PUNCH_USE("bod.general.pack_a_punch"),
	POWER_ENABLE("bod.general.power_enable"),
	TELEPORTER_USE("bod.general.teleporter_use"),
	RANK_UP("bod.general.rank_up");
	
	
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
				p.playSound(loc, path, 10F, 1F);
			}
		}
	}
	
	/**
	 * Play the sound to all players
	 */
	@SuppressWarnings("deprecation")
	public void playGlobally(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
				p.playSound(p.getEyeLocation(), path, 10F, 1F);
			}
		}
	}

}
