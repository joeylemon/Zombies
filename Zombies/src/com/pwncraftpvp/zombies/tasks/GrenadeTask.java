package com.pwncraftpvp.zombies.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.utils.Utils;

public class GrenadeTask extends BukkitRunnable {
	
	private Item item;
	public GrenadeTask(Item item){
		this.item = item;
	}
	
	private int runtime = 0;
	private int time = 3;
	
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
	@SuppressWarnings("deprecation")
	public void cancelTask(){
		Location loc = item.getLocation();
		for(int x = -2; x <= 2; x++){
			for(int y = -2; y <= 0; y++){
				for(int z = -2; z <= 2; z++){
					Block block = loc.add(x, y, z).getBlock();
					if(block != null && block.getType() != Material.AIR){
						FallingBlock fb = loc.getWorld().spawnFallingBlock(block.getLocation().add(0, 1, 0), block.getType(), block.getData());
						fb.getVelocity().setY(6);
						fb.getVelocity().setX(Utils.getRandomInteger(-2, 2));
						fb.getVelocity().setZ(Utils.getRandomInteger(-2, 2));
					}
				}
			}
		}
		
		item.remove();
		this.cancel();
	}

}
