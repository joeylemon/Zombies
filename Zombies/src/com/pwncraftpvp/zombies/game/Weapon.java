package com.pwncraftpvp.zombies.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.zombies.utils.Utils;

public enum Weapon {
	
	GALIL(Material.IRON_HOE, "Galil", "Lamentation", true, true, 10, 26, 70, 65, 35, 315, 3, 
			45, 100, 75, 35, 490, 1),
	AK47U(Material.GOLD_HOE, "AK47u", "AK47fu2", true, true, 15, 20, 65, 50, 20, 160, 2,
			43, 100, 70, 40, 280, 1),
	
	M1911(Material.WOOD_SPADE, "M1911", "Mustang and Sally", true, false, 15, 20, 38, 50, 8, 80, 9,
			20, 20, 50, 8, 80, 3),
	FIVE_SEVEN(Material.GOLD_SPADE, "Five-Seven", "Ultra", true, false, 12, 28, 55, 65, 20, 120, 7,
			45, 70, 70, 20, 200, 5),
	COLT_M16A1(Material.IRON_SPADE, "Colt M16A1", "Skullcrusher", true, false, 15, 23, 45, 50, 30, 120, 8,
			40, 60, 60, 30, 270, 5),
	
	HAND_GRENADE(Material.FIREWORK_CHARGE, "Hand Grenade", "", false, false, 0, 250, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
	
	KNIFE(Material.IRON_SWORD, "Knife", "", false, false, 0, 150, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	
	private Material material;
	private String name;
	private String upgraded;
	
	private boolean gun;
	private boolean automatic;
	private int chance;
	private int damage;
	private int headshot;
	private int accuracy;
	private int magazine;
	private int totalammo;
	private int firingrate;
	
	private int upgdamage;
	private int upgheadshot;
	private int upgaccuracy;
	private int upgmagazine;
	private int upgtotalammo;
	private int upgfiringrate;
	Weapon(Material material, String name, String upgraded, boolean gun, boolean automatic, int chance, int damage, int headshot, int accuracy, int magazine, int totalammo, int firingrate,
			int upgdamage, int upgheadshot, int upgaccuracy, int upgmagazine, int upgtotalammo, int upgfiringrate){
		this.material = material;
		this.name = name;
		this.upgraded = upgraded;
		this.gun = gun;
		this.automatic = automatic;
		this.chance = chance;
		
		this.damage = damage;
		this.headshot = headshot;
		this.accuracy = accuracy;
		this.magazine = magazine;
		this.totalammo = totalammo;
		this.firingrate = firingrate;
		
		this.upgdamage = upgdamage;
		this.upgheadshot = upgheadshot;
		this.upgaccuracy = upgaccuracy;
		this.upgmagazine = upgmagazine;
		this.upgtotalammo = upgtotalammo;
		this.upgfiringrate = upgfiringrate;
	}
	
	private ItemStack item = null;
	
	/**
	 * Get the weapon's material
	 * @return The weapon's material
	 */
	public Material getMaterial(){
		return material;
	}
	
	/**
	 * Get the weapon's name
	 * @return The weapon's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the weapon's upgraded name
	 * @return The weapon's upgraded name
	 */
	public String getUpgradedName(){
		return upgraded;
	}
	
	/**
	 * Get if the weapon is a gun
	 * @return True if the weapon is a gun, false if not
	 */
	public boolean isGun(){
		return gun;
	}
	
	/**
	 * Get if the weapon is automatic
	 * @return True if the weapon is automatic, false if not
	 */
	public boolean isAutomatic(){
		return automatic;
	}
	
	/**
	 * Get the chance of the weapon appearing in the mystery box
	 * @return The chance of the weapon appearing in the mystery box
	 */
	public int getChance(){
		return chance;
	}
	
	/**
	 * Get the weapon's damage
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's damage
	 */
	public int getDamage(boolean upgraded){
		if(upgraded == false){
			return damage;
		}else{
			return upgdamage;
		}
	}
	
	/**
	 * Get the weapon's headshot damage
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's headshot damage
	 */
	public int getHeadshotDamage(boolean upgraded){
		if(upgraded == false){
			return headshot;
		}else{
			return upgheadshot;
		}
	}
	
	/**
	 * Get the weapon's accuracy
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's accuracy
	 */
	public int getAccuracy(boolean upgraded){
		if(upgraded == false){
			return accuracy;
		}else{
			return upgaccuracy;
		}
	}
	
	/**
	 * Get the weapon's magazine size
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's magazine size
	 */
	public int getMagazineSize(boolean upgraded){
		if(upgraded == false){
			return magazine;
		}else{
			return upgmagazine;
		}
	}
	
	/**
	 * Get the weapon's total ammo
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's total ammo
	 */
	public int getTotalAmmo(boolean upgraded){
		if(upgraded == false){
			return totalammo;
		}else{
			return upgtotalammo;
		}
	}
	
	/**
	 * Get the weapon's firing rate
	 * @param upgraded - Whether the weapon is upgraded or not
	 * @return The weapon's firing rate
	 */
	public int getFiringRate(boolean upgraded){
		if(upgraded == false){
			return firingrate;
		}else{
			return upgfiringrate;
		}
	}
	
	/**
	 * Get the weapon's item
	 * @return The weapon's item
	 */
	public ItemStack getItemStack(){
		if(item == null){
			item = Utils.renameItem(new ItemStack(this.getMaterial()), ChatColor.GOLD + name);
		}
		return item;
	}

}
