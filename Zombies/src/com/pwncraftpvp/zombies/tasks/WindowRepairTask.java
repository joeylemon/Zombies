package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.CustomSound;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.utils.EffectUtils;

public class WindowRepairTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	private ZPlayer zplayer;
	private Window window;
	public WindowRepairTask(Player player, Window window){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.window = window;
	}
	
	public boolean clicking = true;
	
	private boolean add = true;
	private boolean done = false;
	private int max = 6;
	private int dot = 1;
	
	public void run(){
		if(clicking == true){
			if(add == true){
				int current = main.game.windowhealth.get(window.getID()) + 1;
				main.game.windowhealth.remove(window.getID());
				main.game.windowhealth.put(window.getID(), current);
				
				if(current <= 1){
					window.getLocation().getBlock().setType(Material.IRON_FENCE);
				}
				EffectUtils.playBreakEffect(window.getLocation().getBlock());
				
				CustomSound.WINDOW_REPAIR.play(window.getLocation());
				
				int score = 10;
				if(main.game.doublepointstask != null){
					score *= 2;
				}
				zplayer.addScore(score);
				
				int left = (max - current);
				if(left <= 0){
					zplayer.sendActionBar("");
					done = true;
					EffectUtils.playGreenSparkleEffect(window.getLocation());
					this.cancelTask();
				}
				add = false;
			}else{
				add = true;
			}
			
			if(done == false){
				if(dot < 3){
					dot++;
				}else{
					dot = 1;
				}
				String dots = "...";
				if(dot == 1){
					dots = ".";
				}else if(dot == 2){
					dots = "..";
				}
				zplayer.sendActionBar(ChatColor.GRAY + "Repairing" + dots);
			}
			
			clicking = false;
		}else{
			this.cancelTask();
		}
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
