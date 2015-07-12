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
	 * Get the areas of the map
	 * @return The areas of the map
	 */
	public List<Area> getAreas(){
		if(areas == null){
			areas = new ArrayList<Area>();
			for(String a : main.getConfig().getConfigurationSection("maps." + name + ".areas").getKeys(false)){
				int areaid = Integer.parseInt(a);
				List<Door> doors = new ArrayList<Door>();
				List<Window> windows = new ArrayList<Window>();
				List<Location> spawns = new ArrayList<Location>();
				
				ConfigurationSection doorSect = main.getConfig().getConfigurationSection("maps." + name + ".areas." + areaid + ".doors");
				if(doorSect != null){
					for(String d : doorSect.getKeys(false)){
						int doorid = Integer.parseInt(d);
						List<Block> blocks = new ArrayList<Block>();
						for(String s : main.getConfig().getStringList("maps." + name + ".areas." + areaid + ".doors." + doorid + ".blocks")){
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
				
				ConfigurationSection windowSect = main.getConfig().getConfigurationSection("maps." + name + ".areas." + areaid + ".windows");
				if(windowSect != null){
					for(String w : windowSect.getKeys(false)){
						int windowid = Integer.parseInt(w);
						double x,y,z;
						x = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".windows." + windowid + ".x");
						y = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".windows." + windowid + ".y");
						z = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".windows." + windowid + ".z");
						windows.add(new Window(windowid, new Location(Utils.getWorld(), x, y, z)));
					}
				}
				
				ConfigurationSection spawnSect = main.getConfig().getConfigurationSection("maps." + name + ".areas." + areaid + ".spawns");
				if(spawnSect != null){
					for(String s : spawnSect.getKeys(false)){
						int spawnid = Integer.parseInt(s);
						double x,y,z;
						x = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".spawns." + spawnid + ".x");
						y = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".spawns." + spawnid + ".y");
						z = main.getConfig().getDouble("maps." + name + ".areas." + areaid + ".spawns." + spawnid + ".z");
						spawns.add(new Location(Utils.getWorld(), x, y, z));
					}
				}
				
				areas.add(new Area(areaid, doors, windows, spawns));
			}
		}
		return areas;
	}

}
