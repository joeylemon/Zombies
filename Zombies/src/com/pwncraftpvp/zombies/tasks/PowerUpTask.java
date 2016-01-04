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
	
	private int timeFactor = 2;
	private boolean visible = true;
	
	private int runtime = 0;
	private int time = (30 * timeFactor);
	
	public void run(){
		int timeleft = (time - runtime);
		if(timeleft > 0){
			runtime++;
			boolean remove = false;
			if(timeleft <= (6 * timeFactor)){
				remove = true;
				if(!visible){
					remove = false;
				}
			}else if(timeleft <= (15 * timeFactor)){
				remove = ((timeleft % 4) == 0);
			}
			this.setLine(remove);
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
	 * Set the line
	 * @param hidden - If the line should be invisible
	 */
	public void setLine(boolean hidden){
		if(hidden){
			if(visible){
				powerup.getHologram().removeLine(0);
				visible = false;
			}
		}else{
			if(!visible){
				powerup.getHologram().appendTextLine(ChatColor.GREEN + powerup.getType().getName());
				visible = true;
			}
		}
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
