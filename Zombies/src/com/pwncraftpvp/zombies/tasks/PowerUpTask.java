package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.game.PowerUp;

public class PowerUpTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private PowerUp powerup;
	public PowerUpTask(PowerUp powerup){
		this.powerup = powerup;
	}
	
	private int runtime = 0;
	private int time = 30;
	
	private boolean remove = true;
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			runtime++;
			if(timeleft <= 10){
				if(remove){
					powerup.getHologram().removeLine(0);
					remove = false;
				}else{
					powerup.getHologram().appendTextLine(ChatColor.GREEN + powerup.getType().getName());
					remove = true;
				}
			}
		}else{
			this.cancelTask();
		}
	}
	
	/**
	 * Get the power up
	 * @return The power up
	 */
	public PowerUp getPowerUp(){
		return powerup;
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		powerup.remove();
		main.game.poweruptask = null;
		this.cancel();
	}

}
