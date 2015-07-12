package com.pwncraftpvp.zombies.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.EntityInsentient;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Weapon;

public class Utils {
	
	private static final Main main = Main.getInstance();
	private static final String gray = ChatColor.GRAY + "";
	//private static final String red = ChatColor.RED + "";
	
	private static final Random rand = new Random();
	private static World world = null;
	
	/**
	 * Spawn a zombie
	 * @param loc - The location
	 */
	public static final void spawnZombie(Location loc){
		Zombie zombie = (Zombie) Utils.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		zombie.setMaxHealth(main.game.getZombieHealth());
		zombie.setHealth(main.game.getZombieHealth());
		zombie.setVillager(false);
		zombie.setBaby(false);
		int speed = main.game.getRandomSpeed();
		if(speed > 0){
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (speed - 1)));
		}
	}
	
	/**
	 * Shoot a bullet from the player
	 * @param player - The player
	 * @param weapon - The player's weapon
	 * @param upgraded - The weapon's upgraded status
	 */
	public static final void shootBullet(Player player, Weapon weapon, boolean upgraded){
		double randomX = Utils.getRandomDouble(Utils.getDoubleFromAccuracy(weapon.getAccuracy(upgraded)));
		double randomZ = Utils.getRandomDouble(Utils.getDoubleFromAccuracy(weapon.getAccuracy(upgraded)));
		
		Projectile proj = player.getWorld().spawn(player.getEyeLocation().add(0, 0.25, 0), Egg.class);
		proj.setShooter(player);
		proj.setVelocity(player.getEyeLocation().getDirection().multiply(4.0));
		proj.setVelocity(new Vector(proj.getVelocity().getX() + randomX, proj.getVelocity().getY(), proj.getVelocity().getZ() + randomZ));
		if(upgraded == true){
			proj.setFireTicks(100);
		}
		
		EffectUtils.playShotSound(player.getEyeLocation());
		EffectUtils.playSmokeEffect(player.getLocation());
	}
	
	/**
	 * Get the zombies for a round
	 * @param round - The round
	 * @return The amount of zombies for the round
	 */
	public static final int getZombiesForRound(int round){
		int zombies = 6;
		int players = Bukkit.getOnlinePlayers().length;
		int cap = 24 + (6 * (players - 1));
		if(round == 1){
			zombies = 6 + (players / 2);
		}else if(round == 2){
			zombies = 6 + players;
		}else{
			zombies = (int) ((0.15 * round) * cap);
		}
		return zombies;
	}
	
	/**
	 * Get the spawn delay for a round
	 * @param round - The round
	 * @return The spawn delay, in ticks, for the round
	 */
	public static final int getDelayForRound(int round){
		int delay = 40;
		if(round >= 10 && round < 25){
			delay = 30;
		}else if(round >= 25 && round < 40){
			delay = 20;
		}else if(round >= 40){
			delay = 10;
		}
		return delay;
	}
	
	/**
	 * Send a custom plugin message
	 * @param server - The server to send it to
	 * @param tag - The message tag to read
	 * @param value - The value of the message
	 */
	public static final void sendCustomMessage(String server, String tag, String value){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(server);
		out.writeUTF("Zombies");
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try{
			msgout.writeUTF(tag + ":" + value);
		}catch (IOException e){
			e.printStackTrace();
		}
		
		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
		Utils.getRandomPlayer().sendPluginMessage(main, "BungeeCord", out.toByteArray());
	}
	
	/**
	 * Send the game's status to the hub
	 */
	public static final void sendStatus(){
		Utils.sendCustomMessage("hub", "gameStatus", WordUtils.capitalizeFully(main.game.getStatus().toString()));
	}
	
	/**
	 * Set the navigation of an entity
	 * @param entity - The entity
	 * @param loc - The location
	 */
	public static final void setNavigation(LivingEntity entity, Location loc){
	    ((EntityInsentient)((CraftLivingEntity) entity).getHandle()).getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), 1.25F);
	}
	
	/**
	 * Connect a player to a server
	 * @param player - The player to move
	 * @param server - The new server
	 */
	public static final void connect(Player player, String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(main, "BungeeCord", out.toByteArray());
	}
	
	/**
	 * Update every player's scoreboards
	 */
	public static final void updateScoreboards(){
		for(Player p : Bukkit.getOnlinePlayers()){
			ZPlayer zp = new ZPlayer(p);
			zp.updateScoreboard();
		}
	}
	
	/**
	 * Broadcast a message
	 * @param message - The message to broadcast
	 */
	public static final void broadcastMessage(String message){
		for(Player p : Bukkit.getOnlinePlayers()){
			p.sendMessage(ChatColor.GRAY + message);
		}
	}
	
	/**
	 * Broadcast a title
	 * @param title - The title
	 * @param subtitle - The subtitle
	 */
	public static final void broadcastTitle(String title, String subtitle, int stay){
		for(Player p : Bukkit.getOnlinePlayers()){
			ZPlayer zp = new ZPlayer(p);
			zp.sendTitle(gray + title, gray + subtitle, stay);
		}
	}
	
	/**
	 * Broadcast a subtitle
	 * @param subtitle - The subtitle
	 */
	public static final void broadcastSubtitle(String subtitle, int stay){
		for(Player p : Bukkit.getOnlinePlayers()){
			ZPlayer zp = new ZPlayer(p);
			zp.sendSubtitle(gray + subtitle, stay);
		}
	}
	
	/**
	 * Get the game world
	 * @return The game world
	 */
	public static final World getWorld(){
		if(world == null){
			world = Bukkit.getWorld("testworld");
		}
		return world;
	}
	
	/**
	 * Get a chat color from a number
	 * @param number - The number
	 * @return The corresponding chat color
	 */
	public static final ChatColor getChatColor(int number){
		ChatColor color = ChatColor.GRAY;
		if(number == 1){
			color = ChatColor.GRAY;
		}else if(number == 2){
			color = ChatColor.YELLOW;
		}else if(number == 3){
			color = ChatColor.RED;
		}else if(number == 4){
			color = ChatColor.BLUE;
		}else if(number == 5){
			color = ChatColor.DARK_GREEN;
		}else if(number == 6){
			color = ChatColor.GREEN;
		}else if(number == 7){
			color = ChatColor.GREEN;
		}else if(number == 8){
			color = ChatColor.DARK_GRAY;
		}
		return color;
	}
	
	/**
	 * Get the minimum amount of players required to start a game
	 * @return The minimum amount of players required to start a game
	 */
	public static final int getMinimumPlayers(){
		return 1;
	}
	
	/**
	 * Get a random player
	 * @return A random player
	 */
	public static final Player getRandomPlayer(){
		Player player = null;
		for(Player p : Bukkit.getOnlinePlayers()){
			player = p;
			break;
		}
		return player;
	}
	
	/**
	 * Remove all entities
	 */
	public static final void removeEntities(){
		for(Entity e : Utils.getWorld().getEntities()){
			if((e instanceof Player) == false){
				e.remove();
			}
		}
	}
	
	/**
	 * Get the double from a weapon's accuracy
	 * @param accuracy - The weapon's accuracy
	 * @return The double to add to a bullet's velocity
	 */
	public static final double getDoubleFromAccuracy(int accuracy){
		return (((100 - accuracy) * 0.01) / 2.5);
	}
	
	/**
	 * Randomize the double to be positive or negative
	 * @param value - The double to randomize
	 * @return The randomized double
	 */
	public static final double randomize(double value){
		int chance = rand.nextInt(1);
		if(chance == 0){
			return value;
		}else{
			return (value * -1);
		}
	}
	
	/**
	 * Get a random double
	 * @return A random double
	 */
	public static final double getRandomDouble(double max){
		return randomize(0 + (max - 0) * rand.nextDouble());
	}
	
	/**
	 * Check if a string is also an integer
	 * @param value - The string to check
	 * @return True or false depending on if the string is an integer or not
	 */
	public static final boolean isInteger(String value){
		try{
			Integer.parseInt(value);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Rename an itemstack
	 * @param item - The itemstack to rename
	 * @param name - The new name of the itemstack
	 * @param lore - The lore for the itemstack
	 * @return The renamed itemstack
	 */
	public static final ItemStack renameItem(ItemStack item, String name, String... lore){
	    ItemMeta meta = (ItemMeta) item.getItemMeta();
    	meta.setDisplayName(name);
    	List<String> desc = new ArrayList<String>();
    	for(int x = 0; x <= (lore.length - 1); x++){
    		desc.add(lore[x]);
    	}
	    meta.setLore(desc);
	    item.setItemMeta(meta);
	    return item;
	}
	
	/**
	 * Rename an itemstack
	 * @param item - The itemstack to rename
	 * @param name - The new name of the itemstack
	 * @return The renamed itemstack
	 */
	public static final ItemStack renameItem(ItemStack item, String name){
	    ItemMeta meta = (ItemMeta) item.getItemMeta();
    	meta.setDisplayName(name);
	    item.setItemMeta(meta);
	    return item;
	}

}
