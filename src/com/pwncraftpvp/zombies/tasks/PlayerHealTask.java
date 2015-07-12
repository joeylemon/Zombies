package com.pwncraftpvp.zombies.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;

public class PlayerHealTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	public PlayerHealTask(Player player){
		this.player = player;
	}
	
	public int runtime = 0;
	private int time = 4;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			runtime++;
		}else{
			this.cancelTask(true);
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(boolean heal){
		if(main.game.heal.containsKey(player.getName()) == true){
			main.game.heal.remove(player.getName());
		}
		if(heal == true){
			player.setHealth(player.getMaxHealth());
		}
		this.cancel();
	}

}
