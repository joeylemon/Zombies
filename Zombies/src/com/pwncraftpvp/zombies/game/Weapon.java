package com.pwncraftpvp.zombies.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.zombies.utils.Utils;

public enum Weapon {
	
	GALIL(Material.IRON_HOE, "Galil", "Lamentation", true, true, 10, 26, 100, 65, 35, 315, 3, 
			4, 35, 490, 1),
	AK47U(Material.GOLD_HOE, "AK47u", "AK47fu2", true, true, 12, 20, 90, 50, 20, 160, 2,
			4, 40, 280, 1),
	
	LSAT(Material.IRON_PICKAXE, "LSAT", "FSIRT", true, true, 9, 40, 170, 45, 100, 400, 5,
			4, 40, 280, 1),
	
	M1911(Material.WOOD_SPADE, "M1911", "Mustang and Sally", true, false, 0, 20, 65, 50, 8, 80, 9,
			4, 8, 80, 3),
	FIVE_SEVEN(Material.GOLD_SPADE, "Five-Seven", "Ultra", true, false, 10, 28, 80, 65, 20, 120, 7,
			4, 20, 200, 5),
	COLT_M16A1(Material.IRON_SPADE, "Colt M16A1", "Skullcrusher", true, false, 12, 23, 60, 50, 30, 120, 8,
			4, 30, 270, 5),
	
	HAND_GRENADE(Material.FIREWORK_CHARGE, "Hand Grenade", "", false, false, 0, 250, 0, 0, 0, 0, 0, 0, 0, 0, 0),
	
	KNIFE(Material.IRON_SWORD, "Knife", "", false, false, 0, 150, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	
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
	
	private int upgmultiplier;
	private int upgmagazine;
	private int upgtotalammo;
	private int upgfiringrate;
	Weapon(Material material, String name, String upgraded, boolean gun, boolean automatic, int chance, int damage, int headshot, int accuracy, int magazine, int totalammo, int firingrate,
			int upgmultiplier, int upgmagazine, int upgtotalammo, int upgfiringrate){
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
		
		this.upgmultiplier = upgmultiplier;
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
			return damage * upgmultiplier;
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
			return headshot * upgmultiplier;
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
			return accuracy * upgmultiplier;
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
	
	
	/**
	 * Get a weapon from a chance number
	 * @param chance - The chance
	 * @return The weapon
	 */
	public static final Weapon getWeapon(int chance){
		Weapon weapon = null;
		
		int oldsum = 0;
		int newsum = 0;
		for(Weapon w : Weapon.values()){
			oldsum = newsum;
			newsum += w.getChance();
			Utils.broadcastMessage(oldsum + " <= " + chance + " <= " + newsum);
			if(chance >= oldsum && chance <= newsum){
				weapon = w;
				break;
			}
		}
		
		return weapon;
	}
	
	private static int chances = -1;
	
	/**
	 * Get the total number of chances
	 * @return The total number of chances
	 */
	public static final int getTotalChances(){
		if(chances == -1){
			int total = 0;
			for(Weapon w : Weapon.values()){
				total += w.getChance();
			}
			chances = total;
		}
		return chances;
	}

}
