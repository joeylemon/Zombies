package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;

public class PlayerDeathTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	private String wait = ChatColor.GOLD + "Hold right-click to revive";
	private String revive = ChatColor.GOLD + "Reviving player...";
	
	private ZPlayer zplayer;
	public ZPlayer zreviver;
	public Hologram hologram;
	public PlayerDeathTask(Player player){
		this.zplayer = new ZPlayer(player);
		this.hologram = HologramsAPI.createHologram(main, player.getLocation().add(0, 2.6, 0));
		this.hologram.appendTextLine(wait);
	}
	
	public boolean reviving = false;
	public int clicks = 0;
	private int runtime = 0;
	private int time = 30;
	
	public void run(){
		if(reviving == false){
			setHologramText(true);
			zreviver = null;
			
			clicks = 0;
			int timeleft = (time - runtime);
			if(timeleft > 0){
				runtime++;
			}else{
				this.cancelTask(true);
			}
		}else{
			setHologramText(false);
			zplayer.sendActionBar("Reviving...");
			zreviver.sendActionBar("Reviving...");
			
			if(clicks >= 20){
				this.cancelTask(false);
			}
		}
		reviving = false;
	}
	
	/**
	 * Get the task's hologram
	 * @return The task's hologram
	 */
	public Hologram getHologram(){
		return hologram;
	}
	
	public void setHologramText(boolean waiting){
		if(waiting){
			hologram.removeLine(0);
			hologram.appendTextLine(wait);
		}else{
			hologram.removeLine(0);
			hologram.appendTextLine(revive);
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(boolean kill){
		if(kill){
			zplayer.toggleDead(false, false);
		}else{
			zplayer.toggleDead(false, true);
			zreviver.addScore((int) (zplayer.getScore() * (0.05)));
		}
		hologram.delete();
		this.cancel();
	}

}
