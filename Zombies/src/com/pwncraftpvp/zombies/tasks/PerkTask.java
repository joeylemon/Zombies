package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Perk;

public class PerkTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	private ZPlayer zplayer;
	private Perk perk;
	private int oldslot;
	public PerkTask(Player player, Perk perk, int oldslot){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.perk = perk;
		this.oldslot = oldslot;
	}
	
	private int runtime = 0;
	private int time = 6;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			player.playSound(player.getEyeLocation(), Sound.DRINK, 1, 1);
			runtime++;
		}else{
			zplayer.givePerk(perk);
			zplayer.sendMessage("You have consumed " + ChatColor.RED + perk.getName() + ChatColor.GRAY + ".");
			player.getInventory().setHeldItemSlot(oldslot);
			player.getInventory().setItem(8, null);
			this.cancelTask();
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		if(main.game.perktask.containsKey(player.getName()) == true){
			main.game.perktask.remove(player.getName());
		}
		this.cancel();
	}

}
