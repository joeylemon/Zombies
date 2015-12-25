package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Ammo;
import com.pwncraftpvp.zombies.game.CustomSound;
import com.pwncraftpvp.zombies.game.Weapon;

public class ReloadTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	private ZPlayer zplayer;
	private Weapon weapon;
	private int slot;
	private int dura;
	public ReloadTask(Player player, Weapon weapon, int slot){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.weapon = weapon;
		this.slot = slot;
		this.dura = player.getItemInHand().getDurability();
		CustomSound.RELOAD_1.play(player);
	}
	
	private int runtime = 0;
	private int time = 3;
	private int dot = 1;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			runtime++;
			
			int clip = dura / time;
			player.getItemInHand().setDurability((short) (dura - (clip * runtime)));
			
			if(dot < 3){
				dot++;
			}else{
				dot = 1;
			}
			String dots = "...";
			if(dot == 1){
				dots = ".";
			}else if(dot == 2){
				dots = "..";
			}
			if(player.getInventory().getHeldItemSlot() == slot){
				zplayer.sendActionBar(ChatColor.GRAY + "Reloading" + dots);
			}
		}else{
			this.cancelTask(true);
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(boolean reload){
		if(main.game.reload.containsKey(player.getName()) == true){
			main.game.reload.remove(player.getName());
		}
		
		player.getItemInHand().setDurability((short) 0);
		
		CustomSound.RELOAD_2.play(player);
		
		if(reload == true){
			boolean upgraded = zplayer.isWeaponUpgraded();
			int magsize = weapon.getMagazineSize(upgraded);
			
			Ammo ammo = zplayer.getAmmo(slot, weapon);
			int currentmag = ammo.getMagazine();
			int currenttotal = ammo.getTotal();
			if(currenttotal < magsize){
				ammo.setMagazine(currenttotal);
				ammo.setTotal(0);
			}else{
				ammo.setMagazine(magsize);
				ammo.setTotal(currenttotal - (magsize - currentmag));
			}
			zplayer.setAmmo(slot, ammo);
		}
		
		this.cancel();
	}

}
