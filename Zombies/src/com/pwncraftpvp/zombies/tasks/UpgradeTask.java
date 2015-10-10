package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class UpgradeTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	private Location loc = main.game.getMap().getUpgrade();
	
	private Player player;
	private ZPlayer zplayer;
	private Item item;
	private Weapon weapon;
	public UpgradeTask(Player player, Weapon weapon){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.weapon = weapon;
		
		this.item = Utils.getWorld().dropItem(loc.add(0.5, 3, 0.5), weapon.getItemStack());
		this.item.setVelocity(new Vector(0.0D, 0.1D, 0.0D));
		this.item.setPickupDelay(1000);
	}
	
	private int time = 12;
	private int runtime = 0;
	
	private boolean waiting = false;
	
	@SuppressWarnings("deprecation")
	public void run(){
		int timeleft = (time - runtime);
		runtime++;
		if(timeleft == 8){
			EffectUtils.playExplodeEffect(loc);
			item.getItemStack().addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
			zplayer.sendMessage("Your upgraded weapon is available.");
			if(player.getTargetBlock(null, 4).getType() == Material.ENDER_CHEST){
				zplayer.sendActionBar(ChatColor.GRAY + "Press right-click to accept weapon. [" + ChatColor.RED + weapon.getUpgradedName() + ChatColor.GRAY + "]");
			}
			waiting = true;
		}else if(timeleft == 0){
			zplayer.sendMessage("You failed to accept your upgraded weapon in time.");
			this.cancelTask();
		}
	}
	
	/**
	 * Get the player
	 * @return The player
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Get the weapon
	 * @return The weapon
	 */
	public Weapon getWeapon(){
		return weapon;
	}
	
	/**
	 * Get if the task is waiting
	 * @return True if the task is waiting, false if not
	 */
	public boolean isWaiting(){
		return waiting;
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		this.cancel();
		item.remove();
		main.game.upgradetask = null;
	}

}
