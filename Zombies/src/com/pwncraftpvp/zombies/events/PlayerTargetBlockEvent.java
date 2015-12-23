package com.pwncraftpvp.zombies.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTargetBlockEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Block newblock;
	public PlayerTargetBlockEvent(Player player, Block newblock){
		this.player = player;
		this.newblock = newblock;
	}
	
	/**
	 * Get the player
	 * @return The player
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Get the new block
	 * @return The new block
	 */
	public Block getNewBlock(){
		return newblock;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
