package com.pwncraftpvp.zombies.tasks;

import java.util.HashMap;

import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.utils.Utils;

public class WindowDestroyTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private HashMap<Integer, Long> delay = new HashMap<Integer, Long>();
	
	public void run(){
		for(Window w : main.game.getAllWindows()){
			if(main.game.windowhealth.get(w.getID()) > 0){
				for(Zombie z : Utils.getNearbyZombies(w.getLocation(), 5)){
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
						Utils.swingArms(z);
						main.game.removeHealth(w);
						delay.put(z.getEntityId(), System.currentTimeMillis() + 4000);
					}
				}
			}
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(){
		this.cancel();
	}

}
