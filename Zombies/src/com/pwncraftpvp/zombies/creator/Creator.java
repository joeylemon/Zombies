package com.pwncraftpvp.zombies.creator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.core.Main;
import com.pwncraftpvp.zombies.core.ZPlayer;
import com.pwncraftpvp.zombies.game.Map;

public abstract class Creator {
	
	protected Main main = Main.getInstance();
	
	protected int step = 0;
	protected String gray = ChatColor.GRAY + "";
	protected String red = ChatColor.RED + "";
	
	protected Player player;
	protected ZPlayer zplayer;
	protected Map map;
	public Creator(Player player, Map map){
		this.player = player;
		this.zplayer = new ZPlayer(player);
		this.map = map;
	}
	
	/**
	 * Get the step of the creator
	 * @return The step of the creator
	 */
	public int getStep(){
		return step;
	}
	
	/**
	 * Advance a step in the creator
	 */
	public abstract void advanceStep();
	
	/**
	 * Get the editor item of the creator
	 * @return The editor item of the creator
	 */
	public abstract EditorItem getEditorItem();

}
