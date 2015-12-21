package com.pwncraftpvp.zombies.creator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pwncraftpvp.zombies.game.Map;

public class UpgradeCreator extends Creator {
	
	public UpgradeCreator(Player player, Map map){
		super(player, map);
	}
	
	public void advanceStep(){
		step++;
		if(step == 1){
			zplayer.sendMessage(red + "Step " + step + gray + ": Left click the pack-a-punch block.");
		}else{
			zplayer.sendMessage("You have created the pack-a-punch.");
			zplayer.exitCreator();
		}
	}
	
	/**
	 * Set the pack-a-punch block
	 * @param loc - The location of the block
	 */
	public void setUpgradeBlock(Location loc){
		main.getConfig().set("maps." + map.getName() + ".upgrade.x", loc.getBlockX());
		main.getConfig().set("maps." + map.getName() + ".upgrade.y", loc.getBlockY());
		main.getConfig().set("maps." + map.getName() + ".upgrade.z", loc.getBlockZ());
		main.saveConfig();
		
		zplayer.sendMessage("You have set the pack-a-punch block.");
		this.advanceStep();
	}
	
	public EditorItem getEditorItem(){
		return EditorItem.UPGRADE_CREATOR;
	}

}
