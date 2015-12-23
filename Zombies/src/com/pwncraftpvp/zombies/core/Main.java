package com.pwncraftpvp.zombies.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.pwncraftpvp.zcomms.core.CommAPI;
import com.pwncraftpvp.zcomms.core.MySQL;
import com.pwncraftpvp.zombies.creator.Creator;
import com.pwncraftpvp.zombies.events.PlayerTargetBlockEvent;
import com.pwncraftpvp.zombies.game.Game;
import com.pwncraftpvp.zombies.game.Glow;
import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.game.Status;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.utils.Utils;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private String gray = ChatColor.GRAY + "";
	private String red = ChatColor.RED + "";
	
	public Game game;
	public Glow glow;
	public MySQL mysql;
	public String nmsver = null;
	
	public BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SELF};
	
	public List<Map> maps = new ArrayList<Map>();
	
	public HashMap<String, Map> editor = new HashMap<String, Map>();
	public HashMap<String, Long> login = new HashMap<String, Long>();
	public HashMap<String, Block> targetblock = new HashMap<String, Block>();
	public HashMap<String, Creator> creator = new HashMap<String, Creator>();
	public HashMap<String, Statistics> stats = new HashMap<String, Statistics>();
	
	/**
	 * Get the instance of this class
	 * @return The instance of this class
	 */
	public static final Main getInstance(){
		return instance;
	}
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		instance = this;
		mysql = CommAPI.getMySQL();
		
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		
		game = new Game();
		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
		
		try{
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		glow = new Glow(69);
		
		try{
			EnchantmentWrapper.registerEnchantment(glow);
		}catch (IllegalArgumentException ex){
			ex.printStackTrace();
		}
		
		for(String s : this.getConfig().getConfigurationSection("maps").getKeys(false)){
			maps.add(new Map(s));
		}
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			public void run(){
				Utils.removeEntities();
			}
		}, 20);
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				for(Player p : Bukkit.getOnlinePlayers()){
					Block oldblock = null;
					if(targetblock.containsKey(p.getName()) == true){
						oldblock = targetblock.get(p.getName());
					}
					Block newblock = p.getTargetBlock(null, 4);
					
					if((oldblock == null && newblock != null) || 
							oldblock.getType() != newblock.getType() || 
							Utils.areDifferent(oldblock.getLocation(), newblock.getLocation())){
						getServer().getPluginManager().callEvent(new PlayerTargetBlockEvent(p, newblock, oldblock));
						if(targetblock.containsKey(p.getName()) == true){
							targetblock.remove(p.getName());
						}
						targetblock.put(p.getName(), newblock);
					}
				}
				
				if((System.currentTimeMillis() - game.lastkill) > 0){
					if(game.spawntask == null && game.getAliveEntities() == 0){
						game.startSpawnTask();
						game.lastkill = System.currentTimeMillis() + (10 * 1000);
					}
				}
				
				if(game.getStatus() == Status.STARTED){
					if(game.getMap().isDay() == true){
						Utils.getWorld().setTime(6000);
					}else{
						Utils.getWorld().setTime(14000);
					}
				}else{
					Utils.getWorld().setTime(14000);
				}
				Utils.getWorld().setWeatherDuration(0);
			}
		}, 0, 5);
	}
	
	public void onDisable(){
		mysql.close();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			ZPlayer zplayer = new ZPlayer(player);
			if(cmd.getName().equalsIgnoreCase("vote")){
				if(game.getStatus() == Status.VOTING){
					if(args.length > 0){
						if(game.voted.contains(player.getName()) == false){
							if(Utils.isInteger(args[0]) == true){
								int number = Integer.parseInt(args[0]);
								if(number >= 1 && number <= maps.size() && number <= 5){
									Map map = game.voteables.get((number - 1));
									int current = game.votes.get(map.getName());
									game.votes.remove(map.getName());
									game.votes.put(map.getName(), current + 1);
									game.voted.add(player.getName());
									zplayer.sendMessage("You have voted for " + red + map.getProperName() + gray + ".");
								}else{
									zplayer.sendError("You must enter a number 1-5.");
								}
							}else{
								zplayer.sendError("You must enter a number.");
							}
						}else{
							zplayer.sendError("You have already voted.");
						}
					}else{
						zplayer.sendVote();
					}
				}else{
					zplayer.sendError("The voting period has ended.");
				}
			}else if(cmd.getName().equalsIgnoreCase("zombies") || cmd.getName().equalsIgnoreCase("z")){
				if(player.isOp() == true){
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("setzombiespawn")){
							if(args.length == 4){
								if(Utils.isInteger(args[1]) == true && Utils.isInteger(args[3]) == true){
									int area = Integer.parseInt(args[1]);
									String map = args[2];
									int spawn = Integer.parseInt(args[3]);
									
									this.getConfig().set("maps." + map + ".areas." + area + ".spawns." + spawn + ".x", player.getLocation().getX());
									this.getConfig().set("maps." + map + ".areas." + area + ".spawns." + spawn + ".y", player.getLocation().getY());
									this.getConfig().set("maps." + map + ".areas." + area + ".spawns." + spawn + ".z", player.getLocation().getZ());
									this.saveConfig();
									
									zplayer.sendMessage("You have set zombie spawn #" + red + spawn + gray + ".");
								}else{
									zplayer.sendError("You have entered an invalid id.");
								}
							}else{
								zplayer.sendError("Usage: /" + cmd.getName() + " setzombiespawn <area id> <map name> <spawn id>");
							}
						}else if(args[0].equalsIgnoreCase("setdogspawn")){
							if(args.length == 4){
								if(Utils.isInteger(args[1]) == true && Utils.isInteger(args[3]) == true){
									int area = Integer.parseInt(args[1]);
									String map = args[2];
									int spawn = Integer.parseInt(args[3]);
									
									this.getConfig().set("maps." + map + ".areas." + area + ".dogspawns." + spawn + ".x", player.getLocation().getX());
									this.getConfig().set("maps." + map + ".areas." + area + ".dogspawns." + spawn + ".y", player.getLocation().getY());
									this.getConfig().set("maps." + map + ".areas." + area + ".dogspawns." + spawn + ".z", player.getLocation().getZ());
									this.saveConfig();
									
									zplayer.sendMessage("You have set dog spawn #" + red + spawn + gray + ".");
								}else{
									zplayer.sendError("You have entered an invalid id.");
								}
							}else{
								zplayer.sendError("Usage: /" + cmd.getName() + " setdogspawn <area id> <map name> <spawn id>");
							}
						}else if(args[0].equalsIgnoreCase("setday")){
							if(args.length == 3){
								String map = args[1];
								boolean day = true;
								if(args[2].equalsIgnoreCase("false") == true){
									day = false;
								}
								
								this.getConfig().set("maps." + map + ".day", day);
								this.saveConfig();
								
								zplayer.sendMessage("You have set day time to " + red + day + gray + ".");
							}else{
								zplayer.sendError("Usage: /" + cmd.getName() + " setday <map name> <true/false>");
							}
						}else if(args[0].equalsIgnoreCase("editor")){
							if(args.length == 2){
								String map = args[1];
								zplayer.toggleEditor(new Map(map));
							}else{
								zplayer.sendError("Usage: /" + cmd.getName() + " editor <map name>");
							}
						}else if(args[0].equalsIgnoreCase("weapons")){
							Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Weapons");
							for(Weapon w : Weapon.values()){
								inv.addItem(w.getItemStack());
							}
							player.openInventory(inv);
						}else if(args[0].equalsIgnoreCase("resetvotes")){
							game.setVoteables();
							zplayer.sendMessage("You have reset the voting maps.");
						}else if(args[0].equalsIgnoreCase("givebrains")){
							zplayer.setBrains(zplayer.getBrains() + 5);
							zplayer.sendMessage("You were given " + red + 5 + gray + " brains.");
						}else if(args[0].equalsIgnoreCase("givepoints")){
							if(args.length == 2){
								if(Utils.isInteger(args[1]) == true){
									int score = Integer.parseInt(args[1]);
									zplayer.addScore(score);
									zplayer.sendMessage("You were given " + red + score + gray + " points.");
								}else{
									zplayer.sendError("You have entered an invalid amount.");
								}
							}else{
								zplayer.sendError("Usage: /" + cmd.getName() + " givepoints <amount>");
							}
						}else if(args[0].equalsIgnoreCase("start")){
							game.votingtask.runtime = game.votingtask.time;
						}else if(args[0].equalsIgnoreCase("sendstatus")){
							Utils.sendStatus();
							zplayer.sendMessage("Sent the status.");
						}else{
							zplayer.sendError("Invalid arguments.");
						}
					}else{
						zplayer.sendError("Invalid arguments.");
					}
				}
			}
		}
		return false;
	}

}
