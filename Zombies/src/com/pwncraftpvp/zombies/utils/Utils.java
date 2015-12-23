package com.pwncraftpvp.zombies.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.pwncraftpvp.zcomms.core.CommAPI;
import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Area;
import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.game.Weapon;

public class Utils {
	
	private static final Main main = Main.getInstance();
	private static final String gray = ChatColor.GRAY + "";
	//private static final String red = ChatColor.RED + "";
	
	private static final Random rand = new Random();
	private static String server = null;
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
	 * Spawn a dog
	 * @param loc - The location
	 */
	public static final void spawnDog(Location loc){
		Wolf wolf = (Wolf) Utils.getWorld().spawnEntity(loc, EntityType.WOLF);
		wolf.setMaxHealth(main.game.getZombieHealth() * 0.25);
		wolf.setHealth(main.game.getZombieHealth() * 0.25);
		wolf.setFireTicks(10000);
		wolf.setAngry(true);
		wolf.setTarget(Utils.getRandomPlayer(wolf));
		wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		EffectUtils.playSmokeEffect(loc);
		EffectUtils.strikeLightning(loc);
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
	 * Add an enchantment glow onto an item
	 * @param item - The item
	 * @return The item with an enchantment glow
	 */
	public static final ItemStack addGlow(ItemStack item){
		item.addUnsafeEnchantment(main.glow, 1);
		return item;
	}
	
	/**
	 * Make a zombie swing its arms
	 * @param zombie - The zombie
	 */
	public static final void swingArms(Zombie zombie){
		for(Player p : Bukkit.getOnlinePlayers()){
			PacketPlayOutAnimation animationPacket = new PacketPlayOutAnimation(((CraftEntity)zombie).getHandle(), 0);
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(animationPacket);
		}
	}
	
	/**
	 * Get nearby zombies to a location
	 * @param l - The location
	 * @param radius - The radius to check
	 * @return All nearby zombies in the location
	 */
	public static final List<Zombie> getNearbyZombies(Location l, int radius){
		List<Zombie> zombies = new ArrayList<Zombie>();
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z
						+ (chZ * 16)).getChunk().getEntities()){
					if(e instanceof Zombie){
						if (e.getLocation().distance(l) <= radius
								&& e.getLocation().getBlock() != l.getBlock()) {
							zombies.add((Zombie)e);
						}
					}
				}
			}
		}
		return zombies;
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
		
		if((round % main.game.dogrounddiv) == 0){
			zombies *= 0.5;
		}
		
		return zombies;
	}
	
	/**
	 * Get the spawn delay for a round
	 * @param round - The round
	 * @return The spawn delay, in ticks, for the round
	 */
	public static final int getDelayForRound(int round){
		if(round < 14){
			return (65 + round) - (round * 5);
		}else{
			return 10;
		}
	}
	
	/**
	 * Get the next door id for an area
	 * @param map - The map
	 * @param area - The area id
	 * @return The next door id
	 */
	public static final int getNextDoorID(Map map, int area){
		int id = 1;
		Area a = map.getArea(area);
		if(a != null){
			id = a.getDoors().size() + 1;
		}
		return id;
	}
	
	/**
	 * Get the next box id for an area
	 * @param map - The map
	 * @param area - The area id
	 * @return The next box id
	 */
	public static final int getNextBoxID(Map map, int area){
		int id = 1;
		Area a = map.getArea(area);
		if(a != null){
			id = a.getMysteryBoxes().size() + 1;
		}
		return id;
	}
	
	/**
	 * Get the next window id for an area
	 * @param map - The map
	 * @param area - The area id
	 * @return The next window id
	 */
	public static final int getNextWindowID(Map map, int area){
		int id = 1;
		Area a = map.getArea(area);
		if(a != null){
			id = a.getWindows().size() + 1;
		}
		return id;
	}
	
	/**
	 * Get the next zombie spawn id for an area
	 * @param map - The map
	 * @param area - The area id
	 * @return The next zombie spawn id
	 */
	public static final int getNextZombieSpawnID(Map map, int area){
		int id = 1;
		Area a = map.getArea(area);
		if(a != null){
			id = a.getZombieSpawns().size() + 1;
		}
		return id;
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
	 * Navigate an entity to the nearest player
	 * @param entity - The entity
	 * @return The nearest play (null if none within 20 blocks)
	 */
	public static final Player navigateToNearest(LivingEntity entity){
		Player nearby = null;
		for(Entity e : entity.getNearbyEntities(20, 20, 20)){
			if(e instanceof Player){
				nearby = (Player) e;
				break;
			}
		}
		if(nearby != null){
			Utils.setNavigation(entity, nearby.getLocation());
		}
		return nearby;
	}
	
	/**
	 * Play the chest animation
	 * @param chest - The chest to open/close
	 * @param open - True to open, false to close
	 */
	public static void playChestAnimation(Chest chest, boolean open) {
		Location location = chest.getLocation();
		WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
		world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
	}
	
	/**
	 * Send the game's current status to the hub
	 */
	public static final void sendStatus(){
		CommAPI.sendCustomMessage("hub", Utils.getServerName() + ".gameStatus", WordUtils.capitalizeFully(main.game.getStatus().toString()));
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
	 * Get a random weapon
	 * @return A random weapon
	 */
	public static final Weapon getRandomWeapon(Player player){
		ZPlayer zplayer = new ZPlayer(player);
		Weapon weapon = null;
		Random rand = new Random();
		while(weapon == null){
			for(Weapon w : Weapon.values()){
				if(rand.nextDouble() <= (w.getChance() * 0.01)){
					if(zplayer.hasWeapon(w) == false){
						weapon = w;
						break;
					}
				}
			}
		}
		return weapon;
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
	 * Get the name of the server
	 * @return The name of the server
	 */
	public static final String getServerName(){
		if(server == null){
			server = main.getConfig().getString("settings.servername");
		}
		return server;
	}
	
	/**
	 * Get a chat color from a number
	 * @param number - The number
	 * @return The corresponding chat color
	 */
	public static final ChatColor getChatColor(int number){
		ChatColor color = ChatColor.GRAY;
		if(number == 1){
			color = ChatColor.DARK_GREEN;
		}else if(number == 2){
			color = ChatColor.YELLOW;
		}else if(number == 3){
			color = ChatColor.RED;
		}else if(number == 4){
			color = ChatColor.BLUE;
		}else if(number == 5){
			color = ChatColor.AQUA;
		}else if(number == 6){
			color = ChatColor.GREEN;
		}else if(number == 7){
			color = ChatColor.DARK_BLUE;
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
	 * Get a random player near an entity
	 * @param entity - The entity
	 * @return A random nearby player
	 */
	public static final Player getRandomPlayer(LivingEntity entity){
		Random rand = new Random();
		Player player = null;
		while(player == null){
			for(Entity e : entity.getNearbyEntities(75, 75, 75)){
				if(e.getType() == EntityType.PLAYER){
					if(rand.nextDouble() <= 0.1){
						player = (Player) e;
						break;
					}
				}
			}
		}
		return player;
	}
	
	/**
	 * Remove all entities
	 */
	public static final void removeEntities(){
		for(Entity e : Utils.getWorld().getEntities()){
			if(e.getType() != EntityType.PLAYER){
				e.remove();
			}
		}
	}
	
	/**
	 * Remove all zombies
	 */
	public static final void removeZombies(){
		for(Entity e : Utils.getWorld().getEntities()){
			if(e.getType() == EntityType.ZOMBIE){
				e.remove();
			}
		}
	}
	
	/**
	 * Check if two locations are different
	 * @param loc1 - The first location
	 * @param loc2 - The second location
	 * @return True if the locations are different, false if not
	 */
	public static final boolean areDifferent(Location loc1, Location loc2){
		boolean diff = false;
		if(loc1.getX() != loc2.getX() && loc1.getY() != loc2.getY() && loc1.getZ() != loc2.getZ()){
			diff = true;
		}
		return diff;
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
	 * Get a random number in the range
	 * @param min - The lowest possible number
	 * @param max - The highest possible number
	 * @return A random number between the minimum and maximum values
	 */
	public static final int getRandomInteger(int min, int max){
	    return rand.nextInt((max - min) + 1) + min;
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
