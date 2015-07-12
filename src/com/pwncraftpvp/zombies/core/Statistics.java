package com.pwncraftpvp.zombies.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.StoreItem;

public class Statistics {
	
	private Main main = Main.getInstance();
	
	private Player player;
	public Statistics(Player player){
		this.player = player;
	}
	
	public int brains = 0;
	public int kills = 0;
	public int downs = 0;
	public int revives = 0;
	public int box = 0;
	public int doors = 0;
	public int perks = 0;
	public int games = 0;
	public int playtime = 0;
	public List<StoreItem> store = new ArrayList<StoreItem>();
	
	/**
	 * Get the player's statistics
	 */
	public void pull(){
		ResultSet set = main.mysql.query("SELECT * FROM players WHERE name='" + player.getName() + "'");
		try{
			brains = set.getInt("brains");
			kills = set.getInt("kills");
			downs = set.getInt("downs");
			revives = set.getInt("revives");
			box = set.getInt("box");
			doors = set.getInt("doors");
			perks = set.getInt("perks");
			games = set.getInt("games");
			playtime = set.getInt("playtime");
			String list = set.getString("store");
			if(list.equalsIgnoreCase("none") == false){
				String[] split = list.split(",");
				for(int x = 0; x <= (split.length - 1); x++){
					store.add(StoreItem.valueOf(split[x].toUpperCase()));
				}
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the player's statistics
	 */
	public void push(){
		main.mysql.execute("UPDATE `players` SET `brains`='" + brains + "',`kills`='" + kills + "',`downs`='" + downs + "',`revives`='" + revives + "',`box`='" + box + "',`doors`='" + doors + "',`perks`='" + perks + 
				"',`games`='" + games + "',`playtime`='" + playtime + "' WHERE name='" + player.getName() + "'");
	}

}
