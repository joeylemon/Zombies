package com.pwncraftpvp.zombies.game;

import org.bukkit.Material;

public enum WeaponType {
	
	RIFLE(Material.IRON_HOE),
	PISTOL(Material.WOOD_HOE),
	EXPLOSIVE(Material.FIREWORK_CHARGE);
	
	private Material material;
	WeaponType(Material material){
		this.material = material;
	}
	
	/**
	 * Get the weapon's material
	 * @return The weapon's material
	 */
	public Material getMaterial(){
		return material;
	}

}
