package com.pwncraftpvp.zombies.game;

import org.bukkit.OfflinePlayer;

import com.pwncraftpvp.zombies.core.Main;

public enum BoardStatic {
	
	ROUND("Round");
	
	private String name;
	BoardStatic(String name){
		this.name = name;
	}
	
	private Main main = Main.getInstance();
	
	private OfflinePlayer offline = null;
	
	/**
	 * Get the name of the static
	 * @return The name of the static
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the offline player
	 * @return The offline player
	 */
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer(){
		if(offline == null){
			offline = main.getServer().getOfflinePlayer(name);
		}
		return offline;
	}

}
