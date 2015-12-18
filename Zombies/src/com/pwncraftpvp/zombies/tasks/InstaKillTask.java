package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class InstaKillTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	public int runtime = 0;
	private int time = 30;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			runtime++;
		}else{
			this.cancelTask();
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		Utils.broadcastMessage("The current " + ChatColor.RED + "Insta Kill " + ChatColor.GRAY + "has run out.");
		main.game.instakilltask = null;
		this.cancel();
	}

}
