package com.pwncraftpvp.zombies.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class SpawnTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private boolean dogs;
	private int total;
	public SpawnTask(boolean dogs, int total){
		this.dogs = dogs;
		this.total = total;
		Utils.removeZombies();
	}
	
	private int spawned = 0;
	
	public void run(){
		if(spawned < total){
			if(dogs == false){
				if(main.game.getAliveEntities() < 32){
					Utils.spawnZombie(main.game.getRandomSpawn(false));
					spawned++;
				}
			}else{
				Utils.spawnDog(main.game.getRandomSpawn(true));
				spawned++;
			}
		}else{
			main.game.spawntask = null;
			this.cancel();
		}
	}

}
