package com.pwncraftpvp.zombies.tasks;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.CustomSound;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.utils.EffectUtils;

public class GrenadeTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private ZPlayer zplayer;
	private Item item;
	public GrenadeTask(Player player, Item item){
		this.zplayer = new ZPlayer(player);
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
		
		EffectUtils.playExplodeEffect(loc);
		for(int i = 1; i <= 4; i++){
			Block block = loc.clone().subtract(0, 1, 0).getBlock();
			if(block != null && block.getType() != Material.AIR){
				FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, block.getType(), block.getData());
				int mul1 = 1;
				int mul2 = 1;
				if(Math.random() <= 0.5){
					mul1 = -1;
				}
				if(Math.random() <= 0.5){
					mul2 = -1;
				}
				fb.setVelocity(new Vector((Math.random() * mul1) * 0.25, 1, (Math.random() * mul2) * 0.25));
			}
		}
		
		for(Entity e : item.getNearbyEntities(5, 5, 5)){
			if(e instanceof Zombie){
				Zombie z = (Zombie) e;
				
				double damage = Weapon.HAND_GRENADE.getDamage(false);
				damage *= 1 - (z.getLocation().distance(loc) * 0.15);
				
				EffectUtils.playBloodEffect(z, false);
				double newhealth = z.getHealth() - damage;
				if(newhealth > 1){
					z.setHealth(newhealth);
					z.playEffect(EntityEffect.HURT);
					zplayer.addScore(10);
				}else{
					main.game.killEntity(z);
					zplayer.giveBrains(1);
					zplayer.setKills(zplayer.getKills() + 1);
					zplayer.addScore(50);
				}
			}
		}
		
		CustomSound.GRENADE_EXPLODE.play(loc);
		
		item.remove();
		this.cancel();
	}

}
