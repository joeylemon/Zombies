package com.pwncraftpvp.zombies.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.utils.Utils;

public class Map {
	
	private Main main = Main.getInstance();
	
	private String name;
	public Map(String name){
		this.name = name;
	}
	
	private List<Area> areas = null;
	
	/**
	 * Get the map's name
	 * @return The map's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the map's proper name
	 * @return The map's proper name
	 */
	public String getProperName(){
		return name.replace("_", " ");
	}
	
	/**
	 * Get the map's spawn
	 * @return The map's spawn
	 */
	public Location getSpawn(){
		double x,y,z;
		x = main.getConfig().getDouble("maps." + name + ".spawn.x");
		y = main.getConfig().getDouble("maps." + name + ".spawn.y");
		z = main.getConfig().getDouble("maps." + name + ".spawn.z");
		int yaw,pitch;
		yaw = main.getConfig().getInt("maps." + name + ".spawn.yaw");
		pitch = main.getConfig().getInt("maps." + name + ".spawn.pitch");
		return new Location(Utils.getWorld(), x, y, z, yaw, pitch);
	}
	
	/**
	 * Get the map's upgrade chest location
	 * @return The map's upgrade chest location
	 */
	public Location getUpgrade(){
		double x,y,z;
		x = main.getConfig().getDouble("maps." + name + ".upgrade.x");
		y = main.getConfig().getDouble("maps." + name + ".upgrade.y");
		z = main.getConfig().getDouble("maps." + name + ".upgrade.z");
		return new Location(Utils.getWorld(), x, y, z);
	}
	
	/**
	 * Get the location of a perk
	 * @param perk - The perk
	 * @return The location of the perk
	 */
	public Location getPerkLocation(Perk perk){
		double x,y,z;
		x = main.getConfig().getDouble("maps." + name + ".perks." + perk.toString().toLowerCase() + ".x");
		y = main.getConfig().getDouble("maps." + name + ".perks." + perk.toString().toLowerCase() + ".y");
		z = main.getConfig().getDouble("maps." + name + ".perks." + perk.toString().toLowerCase() + ".z");
		return new Location(Utils.getWorld(), x, y, z);
	}
	
	/**
	 * Get if the map is day
	 * @return True if the map is day, false if not
	 */
	public boolean isDay(){
		return main.getConfig().getBoolean("maps." + name + ".day");
	}
	
	/**
	 * Get the areas of the map
	 * @return The areas of the map
	 */
	public List<Area> getAreas(){
		if(areas == null){
			areas = new ArrayList<Area>();
			
			String prefix = "maps." + name + ".areas.";
			
			for(String a : main.getConfig().getConfigurationSection("maps." + name + ".areas").getKeys(false)){
				int areaid = Integer.parseInt(a);
				List<Door> doors = new ArrayList<Door>();
				List<Window> windows = new ArrayList<Window>();
				List<MysteryBox> boxes = new ArrayList<MysteryBox>();
				List<Location> dogspawns = new ArrayList<Location>();
				List<Location> zombiespawns = new ArrayList<Location>();
				
				ConfigurationSection doorSect = main.getConfig().getConfigurationSection(prefix + areaid + ".doors");
				if(doorSect != null){
					for(String d : doorSect.getKeys(false)){
						int doorid = Integer.parseInt(d);
						List<Block> blocks = new ArrayList<Block>();
						for(String s : main.getConfig().getStringList(prefix + areaid + ".doors." + doorid + ".blocks")){
							String[] split = s.split(",");
							int x,y,z;
							x = Integer.parseInt(split[0]);
							y = Integer.parseInt(split[1]);
							z = Integer.parseInt(split[2]);
							Location loc = new Location(Utils.getWorld(), x, y, z);
							blocks.add(loc.getBlock());
						}
						doors.add(new Door(doorid, areaid, name, blocks));
					}
				}
				
				ConfigurationSection windowSect = main.getConfig().getConfigurationSection(prefix + areaid + ".windows");
				if(windowSect != null){
					for(String w : windowSect.getKeys(false)){
						int windowid = Integer.parseInt(w);
						
						double x1,y1,z1;
						x1 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".x");
						y1 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".y");
						z1 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".z");
						
						double x2,y2,z2;
						x2 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".sign.x");
						y2 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".sign.y");
						z2 = main.getConfig().getDouble(prefix + areaid + ".windows." + windowid + ".sign.z");
						
						windows.add(new Window(windowid, new Location(Utils.getWorld(), x1, y1, z1), new Location(Utils.getWorld(), x2, y2, z2)));
					}
				}
				
				ConfigurationSection zombieSpawnSect = main.getConfig().getConfigurationSection(prefix + areaid + ".spawns");
				if(zombieSpawnSect != null){
					for(String s : zombieSpawnSect.getKeys(false)){
						int spawnid = Integer.parseInt(s);
						double x,y,z;
						x = main.getConfig().getDouble(prefix + areaid + ".spawns." + spawnid + ".x");
						y = main.getConfig().getDouble(prefix + areaid + ".spawns." + spawnid + ".y");
						z = main.getConfig().getDouble(prefix + areaid + ".spawns." + spawnid + ".z");
						zombiespawns.add(new Location(Utils.getWorld(), x, y, z));
					}
				}
				
				ConfigurationSection dogSpawnSect = main.getConfig().getConfigurationSection(prefix + areaid + ".dogspawns");
				if(dogSpawnSect != null){
					for(String s : dogSpawnSect.getKeys(false)){
						int spawnid = Integer.parseInt(s);
						double x,y,z;
						x = main.getConfig().getDouble(prefix + areaid + ".dogspawns." + spawnid + ".x");
						y = main.getConfig().getDouble(prefix + areaid + ".dogspawns." + spawnid + ".y");
						z = main.getConfig().getDouble(prefix + areaid + ".dogspawns." + spawnid + ".z");
						dogspawns.add(new Location(Utils.getWorld(), x, y, z));
					}
				}
				
				ConfigurationSection boxSect = main.getConfig().getConfigurationSection(prefix + areaid + ".boxes");
				if(boxSect != null){
					for(String w : boxSect.getKeys(false)){
						int boxid = Integer.parseInt(w);
						
						List<Location> blocks = new ArrayList<Location>();
						
						double x1,y1,z1;
						x1 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".1.x");
						y1 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".1.y");
						z1 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".1.z");
						
						double x2,y2,z2;
						x2 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".2.x");
						y2 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".2.y");
						z2 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".2.z");
						
						double x3,y3,z3;
						x3 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".light.x");
						y3 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".light.y");
						z3 = main.getConfig().getDouble(prefix + areaid + ".boxes." + boxid + ".light.z");
						
						blocks.add(new Location(Utils.getWorld(), x1, y1, z1));
						blocks.add(new Location(Utils.getWorld(), x2, y2, z2));
						
						boxes.add(new MysteryBox(boxid, blocks, new Location(Utils.getWorld(), x3, y3, z3)));
					}
				}
				
				areas.add(new Area(areaid, doors, windows, zombiespawns, dogspawns, boxes));
			}
		}
		return areas;
	}
	
	/**
	 * Get an area by its id
	 * @param id - The area id
	 * @return The area
	 */
	public Area getArea(int id){
		Area area = null;
		for(Area a : this.getAreas()){
			if(a.getID() == id){
				area = a;
				break;
			}
		}
		return area;
	}

}
