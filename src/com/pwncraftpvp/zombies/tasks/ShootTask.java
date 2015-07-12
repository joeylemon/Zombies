package com.pwncraftpvp.zombies.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Ammo;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.utils.Utils;

public class ShootTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	private ZPlayer zplayer;
	private Weapon weapon;
	private boolean upgraded;
	private Ammo ammo;
	private int slot;
	public ShootTask(Player player, Weapon weapon, boolean upgraded, Ammo ammo, int slot){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.weapon = weapon;
		this.upgraded = upgraded;
		this.ammo = ammo;
		this.slot = slot;
		if(main.game.reload.containsKey(player.getName()) == true){
			main.game.reload.get(player.getName()).cancelTask(false);
		}
	}
	
	private int shot = 0;
	private int total = 2;
	
	public void run(){
		Utils.shootBullet(player, weapon, upgraded);
		
		ammo.setMagazine(ammo.getMagazine() - 1);
		zplayer.sendAmmo(ammo);
		
		int maxdura = player.getItemInHand().getType().getMaxDurability();
		int clip = maxdura / weapon.getMagazineSize(upgraded);
		player.getItemInHand().setDurability((short) (maxdura - (clip * ammo.getMagazine())));
		
		shot++;
		this.cancelTask();
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		if((total - shot) <= 0 || ammo.getMagazine() <= 0){
			zplayer.setAmmo(slot, ammo);
			if(main.game.shooting.contains(player.getName()) == true){
				main.game.shooting.remove(player.getName());
			}
			this.cancel();
		}
	}

}
