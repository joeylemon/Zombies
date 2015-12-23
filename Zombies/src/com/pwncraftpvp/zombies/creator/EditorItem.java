package com.pwncraftpvp.zombies.creator;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;

public enum EditorItem {
	
	DOOR_CREATOR(Material.STICK),
	BOX_CREATOR(Material.BLAZE_ROD),
	WINDOW_CREATOR(Material.ARROW),
	UPGRADE_CREATOR(Material.GOLD_INGOT),
	PERK_CREATOR(Material.IRON_INGOT),
	ZOMBIE_SPAWN_CREATOR(Material.ROTTEN_FLESH),
	DOG_SPAWN_CREATOR(Material.RAW_BEEF),
	MAP_SPAWN_SETTER(Material.ANVIL);
	
	private Material material;
	EditorItem(Material material){
		this.material = material;
	}
	
	private String name = null;
	
	/**
	 * Get the name of the item
	 * @return The name of the item
	 */
	public String getName(){
		if(name == null){
			name = WordUtils.capitalizeFully(this.toString().replace("_", " "));
		}
		return name;
	}
	
	/**
	 * Get the material of the item
	 * @return The material of the item
	 */
	public Material getMaterial(){
		return material;
	}

}
