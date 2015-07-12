package com.pwncraftpvp.zombies.tasks;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class WindowTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private HashMap<Integer, Long> delay = new HashMap<Integer, Long>();
	
	public void run(){
		for(Window w : main.game.getAllWindows()){
			if(main.game.windowhealth.get(w.getID()) > 0){
				Item item = Utils.getWorld().dropItem(w.getLocation(), new ItemStack(Material.STONE_BUTTON));
				for(Entity e : item.getNearbyEntities(3, 3, 3)){
					if(e instanceof Zombie){
						Zombie z = (Zombie) e;
						Utils.setNavigation(z, w.getLocation());
						boolean attack = true;
						if(delay.containsKey(z.getEntityId()) == true){
							if((System.currentTimeMillis() - delay.get(z.getEntityId())) < 0){
								attack = false;
							}else{
								delay.remove(z.getEntityId());
							}
						}
						if(attack == true){
							this.removeHealth(w);
							delay.put(z.getEntityId(), System.currentTimeMillis() + 4000);
						}
					}
				}
				item.remove();
			}
		}
	}
	
	/**
	 * Remove health from a window
	 * @param window - The window to remove health from
	 */
	public void removeHealth(Window window){
		int current = main.game.windowhealth.get(window.getID());
		main.game.windowhealth.remove(window.getID());
		int newhealth = current - 1;
		if(newhealth < 0){
			newhealth = 0;
		}
		main.game.windowhealth.put(window.getID(), newhealth);
		
		Block block = window.getLocation().getBlock();
		EffectUtils.playBreakEffect(block);
		
		if(newhealth == 0){
			block.setType(Material.AIR);
		}
	}

}
