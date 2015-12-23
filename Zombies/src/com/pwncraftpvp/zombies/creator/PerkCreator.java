package com.pwncraftpvp.zombies.creator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.game.Perk;

public class PerkCreator extends Creator {

	public PerkCreator(Player player, Map map){
		super(player, map);
	}
	
	private Perk perk;
	
	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Type the name of the perk.");
		}else if(step == 2){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the perk block.");
		}else{
			zplayer.sendMessage("You have created a new perk.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the perk type
	 * @param perk - The perk type
	 */
	public void setPerk(Perk perk){
		this.perk = perk;
		
		zplayer.sendMessage("You have set the perk type.");
		this.advanceStep();
	}
	
	/**
	 * Set the perk block
	 * @param loc - The location of the block
	 */
	public void setPerkBlock(Location loc){
		main.getConfig().set("maps." + map + ".perks." + perk.toString().toLowerCase() + ".x", loc.getBlockX());
		main.getConfig().set("maps." + map + ".perks." + perk.toString().toLowerCase() + ".y", loc.getBlockY());
		main.getConfig().set("maps." + map + ".perks." + perk.toString().toLowerCase() + ".z", loc.getBlockZ());
		main.saveConfig();
		
		zplayer.sendMessage("You have set the perk block.");
		this.advanceStep();
	}
	
	public EditorItem getEditorItem(){
		return EditorItem.PERK_CREATOR;
	}

}
