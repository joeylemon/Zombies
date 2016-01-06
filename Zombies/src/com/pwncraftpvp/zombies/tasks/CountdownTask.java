package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class CountdownTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String red = ChatColor.RED + "";
	
	public int runtime = 0;
	public int time = 60;
	public boolean pause = false;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			if(!pause){
				runtime++;
				if(timeleft == 60 || timeleft == 30 || timeleft == 10 || timeleft == 3 || timeleft == 2 || timeleft == 1){
					Utils.broadcastMessage(red + timeleft + gray + " seconds until the game begins.");
					if(timeleft > 3){
						Utils.broadcastMessage("Vote for a map with " + red + "/vote" + gray + ".");
					}
				}
			}
		}else{
			main.game.start();
			this.cancel();
		}
	}

}
