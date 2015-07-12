package com.pwncraftpvp.zombies.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.ZPlayer;

public class PlayerDeathTask extends BukkitRunnable {
	
	private ZPlayer zplayer;
	public PlayerDeathTask(Player player){
		this.zplayer = new ZPlayer(player);
	}
	
	public boolean reviving = false;
	private int runtime = 0;
	private int time = 30;
	
	public void run(){
		if(reviving == false){
			int timeleft = (time - runtime);
			if(timeleft > 0){
				runtime++;
			}else{
				this.cancelTask();
			}
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		zplayer.toggleDead(false, false);
		this.cancel();
	}

}
