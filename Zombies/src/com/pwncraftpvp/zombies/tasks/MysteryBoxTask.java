package com.pwncraftpvp.zombies.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.MysteryBox;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class MysteryBoxTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private MysteryBox box;
	private Player player;
	private ZPlayer zplayer;
	private Weapon weapon;
	private Item item;
	public MysteryBoxTask(Player player, MysteryBox box){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.box = box;
		Chest chest = (Chest) box.getBlocks().get(0).getBlock().getState();
		Utils.playChestAnimation(chest, true);
		
		if(main.game.boxuses < 8){
			this.weapon = Utils.getRandomWeapon(player);
		}else{
			this.weapon = null;
		}
		
		this.item = Utils.getWorld().dropItem(box.getBlocks().get(1).add(0, 3, 0), Utils.getRandomWeapon(player).getItemStack());
		this.item.setVelocity(new Vector(0.0D, 0.1D, 0.0D));
		this.item.setPickupDelay(1000);
	}
	
	private int switches = 0;
	private int total = 16;
	private int wait = 0;
	
	@SuppressWarnings("deprecation")
	public void run(){
		int left = (total - switches);
		if(left > 0){
			if(left > 1){
				item.setItemStack(Utils.getRandomWeapon(player).getItemStack());
			}else{
				if(weapon != null){
					item.setItemStack(weapon.getItemStack());
					main.game.boxweapon.put(player.getName(), weapon);
					if(player.getTargetBlock(null, 4).getType() == Material.CHEST){
						zplayer.sendActionBar(ChatColor.GRAY + "Press right-click to trade weapons. [" + ChatColor.RED + weapon.getName() + ChatColor.GRAY + "]");
					}
				}else{
					item.remove();
					item = null;
					for(Location l : box.getBlocks()){
						EffectUtils.playExplodeEffect(l);
					}
					Utils.broadcastMessage("The mystery box has broken!");
					main.game.randomizeMysteryBox();
					this.cancelTask(false);
				}
			}
			switches++;
		}else{
			if(wait < 40){
				wait++;
			}else{
				this.cancelTask(true);
			}
		}
	}
	
	/**
	 * Perform necessary functions to cancel the task
	 */
	public void cancelTask(boolean success){
		if(success == true){
			item.remove();
			main.game.boxtask = null;
			main.game.boxuses++;
		}else{
			main.game.boxtask = null;
			main.game.boxuses = 0;
		}
		if(main.game.boxweapon.containsKey(player.getName()) == true){
			main.game.boxweapon.remove(player.getName());
		}
		Chest chest = (Chest) box.getBlocks().get(0).getBlock().getState();
		Utils.playChestAnimation(chest, false);
		this.cancel();
	}

}
