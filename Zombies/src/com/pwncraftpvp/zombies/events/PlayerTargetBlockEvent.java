package com.pwncraftpvp.zombies.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTargetBlockEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Block newblock;
	private Block oldblock;
	public PlayerTargetBlockEvent(Player player, Block newblock, Block oldblock){
		this.player = player;
		this.newblock = newblock;
		this.oldblock = oldblock;
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
	
	/**
	 * Get the old block
	 * @return The old block
	 */
	public Block getOldBlock(){
		return oldblock;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
