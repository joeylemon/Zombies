package com.pwncraftpvp.zombies.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class SpawnTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private int total;
	public SpawnTask(){
		this.total = Utils.getZombiesForRound(main.game.getRound());
	}
	
	private int spawned = 0;
	
	public void run(){
		if(spawned < total){
			if(main.game.getAliveZombies() < 32){
				Utils.spawnZombie(main.game.getRandomSpawn());
				spawned++;
			}
		}else{
			main.game.spawntask = null;
			this.cancel();
		}
	}

}
