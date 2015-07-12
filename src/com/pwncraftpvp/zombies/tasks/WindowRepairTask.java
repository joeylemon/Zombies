package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.utils.EffectUtils;

public class WindowRepairTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	private Window window;
	public WindowRepairTask(Player player, Window window){
		this.player = player;
		this.window = window;
	}
	
	public boolean clicking = true;
	private int max = 6;
	
	public void run(){
		int current = main.game.windowhealth.get(window.getID()) + 1;
		main.game.windowhealth.remove(window.getID());
		main.game.windowhealth.put(window.getID(), current);
		
		if(current <= 1){
			window.getLocation().getBlock().setType(Material.IRON_FENCE);
		}
		EffectUtils.playBreakEffect(window.getLocation().getBlock());
		
		int left = (max - current);
		if(left <= 0){
			player.sendMessage(ChatColor.GRAY + "You have finished repairing the window.");
			this.cancelTask();
		}else if(clicking == false){
			player.sendMessage(ChatColor.GRAY + "You have stopped repairing the window.");
			this.cancelTask();
		}
		
		clicking = false;
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		if(main.game.repair.containsKey(player.getName()) == true){
			main.game.repair.remove(player.getName());
		}
		this.cancel();
	}

}
