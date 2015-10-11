package com.pwncraftpvp.zombies.tasks;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.utils.Utils;

public class WindowDestroyTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private ItemStack button = new ItemStack(Material.STONE_BUTTON);
	
	private HashMap<Integer, Item> item = new HashMap<Integer, Item>();
	private HashMap<Integer, Long> delay = new HashMap<Integer, Long>();
	
	public void run(){
		for(Window w : main.game.getAllWindows()){
			if(main.game.windowhealth.get(w.getID()) > 0){
				Item item = null;
				if(this.item.containsKey(w.getID()) == false){
					item = Utils.getWorld().dropItem(w.getLocation(), button);
					item.setPickupDelay(9999999);
					this.item.put(w.getID(), item);
				}else{
					item = this.item.get(w.getID());
				}
				for(Entity e : item.getNearbyEntities(3, 3, 3)){
					if(e.getType() == EntityType.ZOMBIE){
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
							main.game.removeHealth(w);
							delay.put(z.getEntityId(), System.currentTimeMillis() + 4000);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		for(Window w : main.game.getAllWindows()){
			if(this.item.containsKey(w.getID()) == true){
				this.item.get(w.getID()).remove();
			}
		}
		this.cancel();
	}

}
