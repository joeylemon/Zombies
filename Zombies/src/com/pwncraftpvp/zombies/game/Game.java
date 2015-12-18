package com.pwncraftpvp.zombies.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.pwncraftpvp.zcomms.core.CommAPI;
import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.tasks.CountdownTask;
import com.pwncraftpvp.zombies.tasks.DoublePointsTask;
import com.pwncraftpvp.zombies.tasks.InstaKillTask;
import com.pwncraftpvp.zombies.tasks.MysteryBoxTask;
import com.pwncraftpvp.zombies.tasks.PerkTask;
import com.pwncraftpvp.zombies.tasks.PlayerDeathTask;
import com.pwncraftpvp.zombies.tasks.PlayerHealTask;
import com.pwncraftpvp.zombies.tasks.PowerUpTask;
import com.pwncraftpvp.zombies.tasks.ReloadTask;
import com.pwncraftpvp.zombies.tasks.SpawnTask;
import com.pwncraftpvp.zombies.tasks.UpgradeTask;
import com.pwncraftpvp.zombies.tasks.WindowDestroyTask;
import com.pwncraftpvp.zombies.tasks.WindowRepairTask;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class Game {
	
	private Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String red = ChatColor.RED + "";
	
	private Map map = null;
	private Status status = Status.WAITING;
	
	private boolean power = false;
	
	private int box = 1;
	private int round = 0;
	private int health = 0;
	
	private List<Door> doors = null;
	private List<Window> windows = null;
	private List<MysteryBox> boxes = null;
	private List<Area> unlockedareas = new ArrayList<Area>();
	
	public int boxuses = 0;
	public int killed = 0;
	public long lastkill = 0;
	public boolean ending = false;
	
	public SpawnTask spawntask = null;
	public MysteryBoxTask boxtask = null;
	public UpgradeTask upgradetask = null;
	public PowerUpTask poweruptask = null;
	public CountdownTask votingtask = null;
	public InstaKillTask instakilltask = null;
	public WindowDestroyTask windowtask = null;
	public DoublePointsTask doublepointstask = null;
	
	public List<Map> voteables = new ArrayList<Map>();
	public List<String> voted = new ArrayList<String>();
	public List<String> shooting = new ArrayList<String>();
	public List<Integer> nodamage = new ArrayList<Integer>();
	public List<String> deadplayers = new ArrayList<String>();
	
	public HashMap<String, Ammo> primary = new HashMap<String, Ammo>();
	public HashMap<String, Ammo> secondary = new HashMap<String, Ammo>();
	public HashMap<String, Integer> votes = new HashMap<String, Integer>();
	public HashMap<String, Integer> brains = new HashMap<String, Integer>();
	public HashMap<String, Integer> scores = new HashMap<String, Integer>();
	public HashMap<String, Weapon> boxweapon = new HashMap<String, Weapon>();
	public HashMap<String, ChatColor> colors = new HashMap<String, ChatColor>();
	public HashMap<String, PerkTask> perktask = new HashMap<String, PerkTask>();
	public HashMap<String, List<Perk>> perks = new HashMap<String, List<Perk>>();
	public HashMap<String, ReloadTask> reload = new HashMap<String, ReloadTask>();
	public HashMap<Integer, Integer> windowhealth = new HashMap<Integer, Integer>();
	public HashMap<String, PlayerHealTask> heal = new HashMap<String, PlayerHealTask>();
	public HashMap<String, PlayerDeathTask> death = new HashMap<String, PlayerDeathTask>();
	public HashMap<String, WindowRepairTask> repair = new HashMap<String, WindowRepairTask>();
	
	/**
	 * Get the game's status
	 * @return The game's status
	 */
	public Status getStatus(){
		return status;
	}
	
	/**
	 * Set the game's status
	 * @param status - The game's status
	 */
	public void setStatus(Status status){
		this.status = status;
		Utils.sendStatus();
	}
	
	/**
	 * Set the voteables
	 */
	public void setVoteables(){
		voteables.clear();
		votes.clear();
		
		Map map = main.maps.get(11);
		for(int x = 1; x <= 5; x++){
			voteables.add(map);
			votes.put(map.getName(), 0);
		}
		
		/*
		Random rand = new Random();
		while(voteables.size() < main.maps.size() && voteables.size() < 5){
			Map map = main.maps.get(rand.nextInt(main.maps.size()));
			if(this.isVoteable(map) == false){
				voteables.add(map);
				votes.put(map.getName(), 0);
			}
		}
		*/
	}
	
	/**
	 * Check if a map is a voteable
	 * @param map - The map to check
	 * @return True if the map is a voteable, false if not
	 */
	public boolean isVoteable(Map map){
		boolean voteable = false;
		for(Map m : voteables){
			if(m.getName().equalsIgnoreCase(map.getName()) == true){
				voteable = true;
				break;
			}
		}
		return voteable;
	}
	
	/**
	 * Get the game's current map
	 * @return The game's current map
	 */
	public Map getMap(){
		if(map == null){
			String name = null;
			int value = -1;
			for(Entry<String, Integer> entry : votes.entrySet()){
			    String k = entry.getKey();
			    Integer v = entry.getValue();
			    if(v > value){
			    	name = k;
			    	value = v;
			    }
			}
			votes.clear();
			map = new Map(name);
		}
		return map;
	}
	
	/**
	 * Get the game's round
	 * @return The game's round
	 */
	public int getRound(){
		return round;
	}
	
	/**
	 * Set the game's round
	 * @param value - The game's round
	 */
	public void setRound(int value){
		round = value;
	}
	
	/**
	 * Check if the power is on
	 * @return True if the power is on, false if not
	 */
	public boolean isPowerOn(){
		return power;
	}
	
	/**
	 * Set the power on
	 */
	public void setPowerOn(){
		power = true;
	}
	
	/**
	 * Get the zombie health
	 */
	public int getZombieHealth(){
		return health;
	}
	
	/**
	 * Increase the zombie health
	 */
	public void increaseHealth(){
		if(round == 1){
			health = 150;
		}else if(round < 10){
			health += 100;
		}else{
			health += (int) (health * 0.1);
		}
	}
	
	/**
	 * Get if the round is a dog round
	 * @return True if it is a dog round, false if not
	 */
	public boolean isDogRound(){
		return (round % 8) == 0;
	}
	
	/**
	 * Get a random spawn
	 * @return A random spawn
	 */
	public Location getRandomSpawn(boolean dogs){
		Random rand = new Random();
		Location loc = null;
		while(loc == null){
			for(Area a : unlockedareas){
				if(dogs == false){
					for(Location l : a.getZombieSpawns()){
						if(rand.nextDouble() <= 0.075){
							loc = l;
							break;
						}
					}
				}else{
					for(Location l : a.getDogSpawns()){
						if(rand.nextDouble() <= 0.075){
							loc = l;
							break;
						}
					}
				}
			}
		}
		return loc;
	}
	
	/**
	 * Get a random zombie speed
	 * @return A random zombie speed
	 */
	public int getRandomSpeed(){
		int speed = 0;
		if(round > 2){
			Random rand = new Random();
			double chance = 0.1;
			if(round >= 10 && round < 20){
				chance = 0.2;
			}else if(round >= 20 && round < 30){
				chance = 0.5;
			}else if(round >= 30 && round < 40){
				chance = 0.6;
			}else if(round >= 40 && round < 50){
				chance = 0.7;
			}else if(round >= 50){
				chance = 0.8;
			}
			if(rand.nextDouble() <= chance){
				speed = 1;
			}else if(rand.nextDouble() <= (chance / 2)){
				speed = 2;
			}
		}
		return speed;
	}
	
	/**
	 * Get the amount of living entities
	 * @return The amount of living entities
	 */
	public int getAliveEntities(){
		int alive = 0;
		for(Entity e : Utils.getWorld().getEntities()){
			if(this.isDogRound() == false){
				if(e instanceof Zombie){
					alive++;
				}
			}else{
				if(e instanceof Wolf){
					alive++;
				}
			}
		}
		return alive;
	}
	
	/**
	 * Kill an entity
	 * @param entity - The entity to kill
	 */
	public void killEntity(LivingEntity entity){
		entity.setHealth(0);
		lastkill = System.currentTimeMillis() + (60 * 1000);
		killed++;
		if(!this.isDogRound()){
			if(poweruptask == null && Math.random() <= 0.05){
				PowerUpType type = null;
				while(type == null){
					for(PowerUpType t : PowerUpType.values()){
						if(Math.random() <= 0.1){
							type = t;
							break;
						}
					}
				}
				this.dropPowerUp(entity.getLocation(), type);
			}
		}
		if(killed >= Utils.getZombiesForRound(this.getRound())){
			this.endRound();
			if(this.isDogRound()){
				this.dropPowerUp(entity.getLocation(), PowerUpType.MAX_AMMO);
			}
		}else{
			if(spawntask == null && this.getAliveEntities() == 0){
				this.startSpawnTask();
			}
		}
	}
	
	/**
	 * Drop a power up
	 * @param loc - The location
	 * @param type - The power up type
	 */
	public void dropPowerUp(Location loc, PowerUpType type){
		PowerUp powerup = new PowerUp(loc, type);
		PowerUpTask task = new PowerUpTask(powerup);
		task.runTaskTimer(main, 0, 20);
		poweruptask = task;
	}
	
	/**
	 * Apply a power up
	 * @param type - The power up type
	 */
	public void applyPowerUp(PowerUpType type){
		Utils.broadcastMessage(red + type.getName() + gray + " has been activated.");
		
		for(Player p : Bukkit.getOnlinePlayers()){
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 10, 10F);
		}
		
		if(type == PowerUpType.MAX_AMMO){
			for(Player p : Bukkit.getOnlinePlayers()){
				ZPlayer zp = new ZPlayer(p);
				zp.fillUpWeapons();
			}
		}else if(type == PowerUpType.INSTA_KILL){
			if(instakilltask != null){
				instakilltask.runtime = 0;
			}else{
				InstaKillTask task = new InstaKillTask();
				task.runTaskTimer(main, 0, 20);
				instakilltask = task;
			}
		}else if(type == PowerUpType.DOUBLE_POINTS){
			if(doublepointstask != null){
				doublepointstask.runtime = 0;
			}else{
				DoublePointsTask task = new DoublePointsTask();
				task.runTaskTimer(main, 0, 20);
				doublepointstask = task;
			}
		}else if(type == PowerUpType.NUKE){
			for(Entity e : Utils.getWorld().getEntities()){
				if(e instanceof LivingEntity){
					LivingEntity le = (LivingEntity) e;
					this.killEntity(le);
				}
			}
		}else if(type == PowerUpType.CARPENTER){
			for(Window w : this.getAllWindows()){
				if(windowhealth.containsKey(w.getID())){
					windowhealth.remove(w.getID());
					windowhealth.put(w.getID(), 6);
				}
				w.getLocation().getBlock().setType(Material.IRON_FENCE);
			}
		}
	}
	
	/**
	 * Remove health from a window
	 * @param window - The window to remove health from
	 */
	public void removeHealth(Window window){
		int current = windowhealth.get(window.getID());
		windowhealth.remove(window.getID());
		int newhealth = current - 1;
		if(newhealth < 0){
			newhealth = 0;
		}
		windowhealth.put(window.getID(), newhealth);
		
		Block block = window.getLocation().getBlock();
		EffectUtils.playBreakEffect(block);
		
		if(newhealth == 0){
			block.setType(Material.AIR);
		}
	}
	
	/**
	 * Get all doors in the map
	 * @return All doors in the map
	 */
	public List<Door> getAllDoors(){
		if(doors == null){
			if(map != null){
				doors = new ArrayList<Door>();
				for(Area a : map.getAreas()){
					for(Door d : a.getDoors()){
						doors.add(d);
					}
				}
			}
		}
		return doors;
	}
	
	/**
	 * Get all windows in the map
	 * @return All windows in the map
	 */
	public List<Window> getAllWindows(){
		if(windows == null){
			if(map != null){
				windows = new ArrayList<Window>();
				for(Area a : map.getAreas()){
					for(Window w : a.getWindows()){
						windows.add(w);
					}
				}
			}
		}
		return windows;
	}
	
	/**
	 * Get all mystery boxes in the map
	 * @return All mystery boxes in the map
	 */
	public List<MysteryBox> getAllMysteryBoxes(){
		if(boxes == null){
			if(map != null){
				boxes = new ArrayList<MysteryBox>();
				for(Area a : map.getAreas()){
					for(MysteryBox b : a.getMysteryBoxes()){
						boxes.add(b);
					}
				}
			}
		}
		return boxes;
	}
	
	/**
	 * Get the current mystery box
	 * @return The current mystery box
	 */
	public MysteryBox getCurrentMysteryBox(){
		return this.getMysteryBox(box);
	}
	
	/**
	 * Get a mystery box from its id
	 * @param id - The id
	 * @return The mystery box
	 */
	public MysteryBox getMysteryBox(int id){
		MysteryBox box = null;
		for(MysteryBox b : this.getAllMysteryBoxes()){
			if(b.getID() == id){
				box = b;
				break;
			}
		}
		return box;
	}
	
	/**
	 * Randomize the current mystery box
	 */
	public void randomizeMysteryBox(){
		int total = this.getAllMysteryBoxes().size();
		int newbox = 1;
		if(total >= 2){
			newbox = Utils.getRandomInteger(1, total);
			while(newbox == box){
				newbox = Utils.getRandomInteger(1, total);
			}
		}
		this.getMysteryBox(box).getLightLocation().getBlock().setType(Material.BEDROCK);
		this.getMysteryBox(newbox).getLightLocation().getBlock().setType(Material.AIR);
		this.box = newbox;
	}
	
	/**
	 * Reset the map's doors
	 */
	public void resetDoors(){
		for(Area a : map.getAreas()){
			for(Door d : a.getDoors()){
				for(Block b : d.getBlocks()){
					b.setType(Material.IRON_FENCE);
				}
			}
		}
	}
	
	/**
	 * Reset the map's windows
	 */
	public void resetWindows(){
		for(Area a : map.getAreas()){
			for(Window w : a.getWindows()){
				w.getLocation().getBlock().setType(Material.IRON_FENCE);
			}
		}
	}
	
	/**
	 * Reset the map's mystery boxes
	 */
	public void resetMysteryBoxes(){
		for(MysteryBox b : this.getAllMysteryBoxes()){
			if(b.getID() != 1){
				b.getLightLocation().getBlock().setType(Material.BEDROCK);
			}else{
				b.getLightLocation().getBlock().setType(Material.AIR);
			}
		}
	}
	
	/**
	 * Get an area from its id
	 * @param id - The id of the area
	 * @return The area
	 */
	public Area getArea(int id){
		Area area = null;
		for(Area a : map.getAreas()){
			if(a.getID() == id){
				area = a;
				break;
			}
		}
		return area;
	}
	
	/**
	 * Add an area to the unlocked list
	 * @param area - The area
	 */
	public void addUnlockedArea(Area area){
		unlockedareas.add(area);
	}
	
	/**
	 * Check if an area is unlocked
	 * @param area - The area
	 * @return True if the area is unlocked, false if not
	 */
	public boolean isUnlocked(int area){
		boolean unlocked = false;
		for(Area a : unlockedareas){
			if(a.getID() == area){
				unlocked = true;
				break;
			}
		}
		return unlocked;
	}
	
	/**
	 * Start the zombie spawn task
	 */
	public void startSpawnTask(){
		spawntask = new SpawnTask(this.isDogRound(), Utils.getZombiesForRound(this.getRound()) - this.killed);
		spawntask.runTaskTimer(main, 0, Utils.getDelayForRound(round));
	}
	
	/**
	 * Start the next round
	 */
	public void startRound(){
		round++;
		killed = 0;
		this.increaseHealth();
		
		Utils.broadcastSubtitle("Round " + red + this.round, 60);
		for(Player p : Bukkit.getOnlinePlayers()){
			p.playSound(p.getLocation(), Sound.WITHER_DEATH, 10, 1.5F);
		}
		
		Utils.updateScoreboards();
		
		this.startSpawnTask();
		
		if(this.isDogRound() == false){
			WindowDestroyTask windowtask = new WindowDestroyTask();
			windowtask.runTaskTimer(main, 0, 5);
			this.windowtask = windowtask;
		}
	}
	
	/**
	 * End the current round
	 */
	public void endRound(){
		if(spawntask != null){
			spawntask.cancel();
		}
		if(windowtask != null){
			windowtask.cancelTask();
		}
		Utils.removeZombies();
		Utils.broadcastSubtitle("Round over", 60);
		
		for(Player p : Bukkit.getOnlinePlayers()){
			ZPlayer zp = new ZPlayer(p);
			if(deadplayers.contains(p.getName()) == false){
				zp.giveBrains(5);
			}else{
				zp.toggleSpectating(false);
			}
			p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 10, 1.5F);
		}
		
		deadplayers.clear();
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				startRound();
			}
		}, 200);
	}
	
	/**
	 * Start the game
	 */
	public void start(){
		Map map = this.getMap();
		Utils.removeEntities();
		this.resetDoors();
		this.resetWindows();
		this.resetMysteryBoxes();
		this.setStatus(Status.STARTED);
		
		for(Area a : map.getAreas()){
			if(a.getDoors().size() == 0){
				unlockedareas.add(a);
			}
		}
		
		for(Window w : this.getAllWindows()){
			windowhealth.put(w.getID(), 6);
		}
		
		Utils.broadcastMessage("The highest voted map was " + red + map.getProperName() + gray + ".");
		Utils.broadcastMessage("Prepare for the first round.");
		
		int count = 1;
		for(Player p : Bukkit.getOnlinePlayers()){
			colors.put(p.getName(), Utils.getChatColor(count));
			brains.put(p.getName(), 0);
			ZPlayer zp = new ZPlayer(p);
			zp.setInventory(status);
			scores.put(p.getName(), 500);
			p.teleport(map.getSpawn());
			p.setHealth(p.getMaxHealth());
			count++;
		}
		
		Utils.updateScoreboards();
		
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				startRound();
			}
		}, 100);
	}
	
	/**
	 * End the game
	 */
	public void end(){
		ending = true;
		
		if(spawntask != null){
			spawntask.cancel();
		}
		if(windowtask != null){
			windowtask.cancelTask();
		}
		
		Utils.removeEntities();
		Utils.broadcastTitle("Game over", "Survived " + red + round + gray + " rounds.", 90);
		for(Player p : Bukkit.getOnlinePlayers()){
			ZPlayer zp = new ZPlayer(p);
			zp.sendMessage("You earned a total of " + red + brains.get(p.getName()) + gray + " brains this game.");
		}
		
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				setStatus(Status.WAITING);
				for(Player p : Bukkit.getOnlinePlayers()){
					ZPlayer zp = new ZPlayer(p);
					zp.logout();
					CommAPI.connect(p, "hub");
				}
				main.stats.clear();
				if(spawntask != null){
					spawntask.cancel();
				}
				resetDoors();
				resetWindows();
				main.game = new Game();
			}
		}, 100);
	}

}
