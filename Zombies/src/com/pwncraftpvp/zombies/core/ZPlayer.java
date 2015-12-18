package com.pwncraftpvp.zombies.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.pwncraftpvp.zombies.game.Ammo;
import com.pwncraftpvp.zombies.game.BoardStatic;
import com.pwncraftpvp.zombies.game.Map;
import com.pwncraftpvp.zombies.game.Perk;
import com.pwncraftpvp.zombies.game.Status;
import com.pwncraftpvp.zombies.game.StoreItem;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.tasks.PlayerDeathTask;
import com.pwncraftpvp.zombies.tasks.ReloadTask;
import com.pwncraftpvp.zombies.tasks.ShootTask;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.TextUtils;
import com.pwncraftpvp.zombies.utils.TitleUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class ZPlayer {
	
	private Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String red = ChatColor.RED + "";
	
	private Player player;
	public ZPlayer(Player player){
		this.player = player;
	}
	
	/**
	 * Send a message header to the player
	 * @param header - The header to be sent
	 */
	public void sendMessageHeader(String header){
		player.sendMessage(TextUtils.centerText(gray + "-=-(" + red + TextUtils.getDoubleArrow() + gray + ")-=-" + "  " + red + header + "  " + gray + "-=-(" + red + TextUtils.getBackwardsDoubleArrow()
				+ gray + ")-=-"));
	}
	
	/**
	 * Send a message to the player
	 * @param message - The message to be sent
	 */
	public void sendMessage(String message){
		player.sendMessage(gray + message);
	}
	
	/**
	 * Send an error message to the player
	 * @param error - The error message to be sent
	 */
	public void sendError(String error){
		player.sendMessage(ChatColor.DARK_RED + error);
	}
	
	/**
	 * Send an action bar message to the player
	 * @param message - The message to send to the player via the action bar
	 */
	public void sendActionBar(String message){
		TitleUtils.sendActionBar(player, gray + message);
	}
	
	/**
	 * Send a title to the player 
	 * @param title - The title to send
	 * @param subtitle - The subtitle to send
	 */
	public void sendTitle(String title, String subtitle, int stay){
		TitleUtils.sendTitle(player, 10, stay, 10, title, subtitle);
	}
	
	/**
	 * Send a subtitle to the player
	 * @param subtitle - The subtitle to send
	 * @param stay - The duration of the subtitle
	 */
	public void sendSubtitle(String subtitle, int stay){
		TitleUtils.sendTitle(player, 10, stay, 10, "", subtitle);
	}
	
	/**
	 * Send the player the vote
	 */
	public void sendVote(){
		this.sendMessageHeader("Vote");
		int count = 1;
		for(Map m : main.game.voteables){
			this.sendMessage(red + count + ". " + gray + m.getProperName() + ": " + red + main.game.votes.get(m.getName()) + gray + " votes");
			count++;
		}
		this.sendMessage("");
		this.sendMessage("Type " + red + "/vote <number> " + gray + "to vote for a map.");
	}
	
	/**
	 * Send an ammo action bar
	 * @param ammo - The ammo
	 */
	public void sendAmmo(Ammo ammo){
		this.sendActionBar(gray + "" + ammo.getMagazine() + red + "/" + gray + ammo.getTotal());
	}
	
	/**
	 * Update the player's scoreboard
	 */
	@SuppressWarnings("deprecation")
	public void updateScoreboard(){
		Scoreboard board = player.getScoreboard();
		if(board == null){
			board = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(board);
		}
		
		String objname = ChatColor.DARK_RED + "Zombies";
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null){
			obj = board.registerNewObjective("board", "dummy");
			obj.setDisplayName(objname);
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}else{
			obj.setDisplayName(objname);
		}
		
		for(BoardStatic s : BoardStatic.values()){
			if(board.getTeam(s.getName()) == null){
				board.registerNewTeam(s.getName());
			}
			Team team = board.getTeam(s.getName());
			if(team.hasPlayer(s.getOfflinePlayer()) == false){
				team.addPlayer(s.getOfflinePlayer());
			}
			
			team.setDisplayName(s.getName());
			team.setPrefix(ChatColor.DARK_RED + "");
			team.setSuffix(" " + gray + TextUtils.getArrow() + ChatColor.GOLD + " " + main.game.getRound());
			
			obj.getScore(s.getOfflinePlayer()).setScore(2);
		}
		
		for(Player p : Bukkit.getOnlinePlayers()){
			if(board.getTeam(p.getName()) == null){
				board.registerNewTeam(p.getName());
			}
			Team team = board.getTeam(p.getName());
			if(team.hasPlayer(p) == false){
				team.addPlayer(p);
			}
			team.setDisplayName(gray + p.getName());
			team.setPrefix(main.game.colors.get(p.getName()) + "");
			team.setSuffix(" " + gray + TextUtils.getArrow() + ChatColor.GOLD + " " + main.game.scores.get(p.getName()));
			obj.getScore(p).setScore(1);
		}
	}
	
	/**
	 * Perform necessary logout functions
	 */
	public void logout(){
		long login = 0;
		if(main.login.containsKey(player.getName())){
			login = main.login.get(player.getName());
			main.login.remove(player.getName());
		}
		long logout = System.currentTimeMillis();
		int playtime = (int) ((logout - login) / 1000);
		if(login == 0){
			playtime = 0;
		}
		
		this.setPlaytime(playtime);
		this.getStats().push();
		
		int online = Bukkit.getOnlinePlayers().length;
		int min = Utils.getMinimumPlayers();
		if(online < min){
			if(main.game.getStatus() == Status.STARTED){
				main.game.end();
			}else if(main.game.getStatus() == Status.VOTING){
				main.game.votingtask.cancel();
				main.game.votingtask = null;
				main.game.setStatus(Status.WAITING);
			}
		}
	}
	
	/**
	 * Give the player a weapon
	 * @param weapon - The weapon to give
	 */
	public void giveWeapon(Weapon weapon, boolean upgraded){
		int slot = 0;
		
		if(player.getInventory().getItem(0) == null){
			slot = 0;
		}else if(player.getInventory().getItem(1) == null){
			slot = 1;
		}else if(player.getInventory().getHeldItemSlot() <= 1){
			slot = player.getInventory().getHeldItemSlot();
		}
		
		this.setAmmo(slot, new Ammo(weapon, weapon.getMagazineSize(upgraded), weapon.getTotalAmmo(upgraded)));
		
		ItemStack item = weapon.getItemStack();
		if(upgraded == true){
			Utils.renameItem(item, ChatColor.GOLD + weapon.getUpgradedName());
			Utils.addGlow(item);
		}
		
		player.getInventory().setItem(slot, item);
	}
	
	/**
	 * Set a slot to a weapon
	 * @param slot - The slot
	 * @param weapon - The weapon
	 * @param amount - Amount of items
	 */
	public void setSlot(int slot, Weapon weapon, int amount){
		ItemStack item = weapon.getItemStack();
		item.setAmount(amount);
		player.getInventory().setItem(slot, item);
	}
	
	/**
	 * Set the player's inventory
	 * @param status - The status to set the inventory for
	 */
	public void setInventory(Status status){
		player.getInventory().clear();
		if(status == Status.STARTED){
			this.giveWeapon(Weapon.M1911, false);
			this.setSlot(2, Weapon.KNIFE, 1);
			this.setSlot(3, Weapon.HAND_GRENADE, 2);
		}
	}
	
	/**
	 * Get the player's statistics
	 * @return The player's statistics
	 */
	public Statistics getStats(){
		if(main.stats.containsKey(player.getName()) == true){
			return main.stats.get(player.getName());
		}else{
			Statistics stats = new Statistics(player);
			stats.pull();
			main.stats.put(player.getName(), stats);
			return stats;
		}
	}
	
	/**
	 * Shoot the player's weapon in hand
	 */
	public void shootWeapon(int slot){
		if(main.game.shooting.contains(player.getName()) == false){
			Weapon weapon = this.getWeaponInHand();
			if(weapon != null && weapon.isGun()){
				Ammo ammo = this.getAmmo(slot, weapon);
				if(ammo.getMagazine() > 0){
					boolean upgraded = this.isWeaponUpgraded();
					if(weapon.isAutomatic() == true){
						ShootTask task = new ShootTask(player, weapon, this.isWeaponUpgraded(), this.getAmmo(slot, weapon), slot);
						task.runTaskTimer(main, 0, weapon.getFiringRate(upgraded));
						if(main.game.shooting.contains(player.getName()) == true){
							main.game.shooting.remove(player.getName());
						}
						main.game.shooting.add(player.getName());
					}else{
						if(main.game.reload.containsKey(player.getName()) == true){
							main.game.reload.get(player.getName()).cancelTask(false);
						}
						
						Utils.shootBullet(player, weapon, upgraded);
						
						ammo.setMagazine(ammo.getMagazine() - 1);
						this.setAmmo(slot, ammo);
						
						int maxdura = player.getItemInHand().getType().getMaxDurability();
						int clip = maxdura / weapon.getMagazineSize(upgraded);
						player.getItemInHand().setDurability((short) (maxdura - (clip * ammo.getMagazine())));
						
						
						if(main.game.shooting.contains(player.getName()) == true){
							main.game.shooting.remove(player.getName());
						}
						main.game.shooting.add(player.getName());
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								main.game.shooting.remove(player.getName());
							}
						}, weapon.getFiringRate(upgraded));
					}
				}else{
					EffectUtils.playEmptyClipSound(player.getEyeLocation());
				}
			}
		}
	}
	
	/**
	 * Reload the player's weapon in hand
	 */
	public void reloadWeapon(int slot){
		if(main.game.reload.containsKey(player.getName()) == false){
			Weapon weapon = this.getWeaponInHand();
			if(weapon != null){
				Ammo ammo = this.getAmmo(slot, weapon);
				if(ammo.getMagazine() < weapon.getMagazineSize(this.isWeaponUpgraded())){
					if(ammo.getTotal() > 0){
						ReloadTask task = new ReloadTask(player, weapon, slot);
						int speed = 8;
						if(this.hasPerk(Perk.SPEED_COLA) == true){
							speed = 4;
						}
						task.runTaskTimer(main, 0, speed);
						main.game.reload.put(player.getName(), task);
					}
				}
			}
		}
	}
	
	/**
	 * Fill up the ammo on all weapons
	 */
	public void fillUpWeapons(){
		for(int i = 0; i <= 1; i++){
			Weapon w = this.getWeaponInSlot(i);
			if(w != null){
				Ammo ammo = this.getAmmo(i, w);
				boolean upgraded = this.isWeaponUpgraded(i);
				this.setAmmo(i, new Ammo(w, ammo.getMagazine(), w.getTotalAmmo(upgraded)));
				player.getInventory().getItem(i).setDurability((short) 0);
			}
		}
	}
	
	/**
	 * Get ammo for a weapon
	 * @param slot - The slot of the weapon
	 * @param weapon - The weapon
	 * @return The ammo of the weapon
	 */
	public Ammo getAmmo(int slot, Weapon weapon){
		Ammo ammo = null;
		if(slot == 0){
			if(main.game.primary.containsKey(player.getName()) == true){
				ammo = main.game.primary.get(player.getName());
			}
		}else if(slot == 1){
			if(main.game.secondary.containsKey(player.getName()) == true){
				ammo = main.game.secondary.get(player.getName());
			}
		}
		if(ammo == null){
			boolean upgraded = this.isWeaponUpgraded();
			ammo = new Ammo(weapon, weapon.getMagazineSize(upgraded), weapon.getTotalAmmo(upgraded));
		}
		return ammo;
	}
	
	/**
	 * Set the ammo for a weapon
	 * @param type - The weapon type
	 * @param ammo - The ammo for the weapon
	 */
	public void setAmmo(int slot, Ammo ammo){
		if(slot == 0){
			if(main.game.primary.containsKey(player.getName()) == true){
				main.game.primary.remove(player.getName());
			}
			main.game.primary.put(player.getName(), ammo);
		}else if(slot == 1){
			if(main.game.secondary.containsKey(player.getName()) == true){
				main.game.secondary.remove(player.getName());
			}
			main.game.secondary.put(player.getName(), ammo);
		}
		Weapon hand = this.getWeaponInHand();
		if(hand != null && hand == ammo.getWeapon()){
			this.sendAmmo(ammo);
		}
	}
	
	/**
	 * Get the player's weapon in hand
	 * @return The player's weapon in hand (null if none)
	 */
	public Weapon getWeaponInHand(){
		Weapon weapon = null;
		ItemStack item = player.getItemInHand();
		if(item != null && item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
			String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			for(Weapon w : Weapon.values()){
				if(w.getName().equalsIgnoreCase(name) == true || w.getUpgradedName().equalsIgnoreCase(name) == true){
					weapon = w;
					break;
				}
			}
		}
		return weapon;
	}
	
	/**
	 * Get the player's weapon in hand
	 * @return The player's weapon in hand (null if none)
	 */
	public Weapon getWeaponInSlot(int slot){
		Weapon weapon = null;
		ItemStack item = player.getInventory().getItem(slot);
		if(item != null && item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
			String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			for(Weapon w : Weapon.values()){
				if(w.getName().equalsIgnoreCase(name) == true || w.getUpgradedName().equalsIgnoreCase(name) == true){
					weapon = w;
					break;
				}
			}
		}
		return weapon;
	}
	
	/**
	 * Check if the player's weapon in hand is upgraded
	 * @return True if it is upgraded, false if not
	 */
	public boolean isWeaponUpgraded(){
		boolean upgraded = false;
		ItemStack item = player.getItemInHand();
		if(item != null){
			if(item.getEnchantments().size() > 0){
				upgraded = true;
			}
		}
		return upgraded;
	}
	
	/**
	 * Check if the player's weapon in a slot is upgraded
	 * @return True if it is upgraded, false if not
	 */
	public boolean isWeaponUpgraded(int slot){
		boolean upgraded = false;
		ItemStack item = player.getInventory().getItem(slot);
		if(item != null){
			if(item.getEnchantments().size() > 0){
				upgraded = true;
			}
		}
		return upgraded;
	}
	
	/**
	 * Check if the player has a weapon
	 * @param weapon - The weapon
	 * @return True if the player has the weapon, false if not
	 */
	public boolean hasWeapon(Weapon weapon){
		boolean has = false;
		for(int x = 0; x <= 1; x++){
			Weapon w = this.getWeaponInSlot(x);
			if(w == weapon){
				has = true;
				break;
			}
		}
		return has;
	}
	
	/**
	 * Give the player a perk
	 * @param perk - The perk
	 */
	public void givePerk(Perk perk){
		List<Perk> perks = null;
		if(main.game.perks.containsKey(player.getName()) == true){
			perks = main.game.perks.get(player.getName());
			main.game.perks.remove(player.getName());
		}else{
			perks = new ArrayList<Perk>();
		}
		perks.add(perk);
		main.game.perks.put(player.getName(), perks);
	}
	
	/**
	 * Check if the player has a perk
	 * @param perk - The perk
	 * @return True if the player has a perk, false if not
	 */
	public boolean hasPerk(Perk perk){
		boolean has = false;
		if(main.game.perks.containsKey(player.getName()) == true){
			for(Perk p : main.game.perks.get(player.getName())){
				if(perk == p){
					has = true;
					break;
				}
			}
		}
		return has;
	}
	
	/**
	 * Toggle if the player is dead
	 * @param toggle - True to toggle the player as dead, false to toggle the player as living/spectating
	 * @param revived - True if the player was revived, false if not
	 */
	public void toggleDead(boolean toggle, boolean revived){
		if(toggle == true){
			this.setDowns(this.getDowns() + 1);
			if((main.game.deadplayers.size() + 1) < Bukkit.getOnlinePlayers().length && (main.game.death.size() + 1) < Bukkit.getOnlinePlayers().length){
				if(main.game.death.containsKey(player.getName()) == true){
					main.game.death.get(player.getName()).cancel();
					main.game.death.remove(player.getName());
				}
				
				Utils.broadcastMessage(red + player.getName() + gray + " has died and needs to be revived.");
				
				this.removeScore((int) (this.getScore() * (0.05)));
				player.getInventory().clear();
				
				PlayerDeathTask task = new PlayerDeathTask(player);
				task.runTaskTimer(main, 0, 20);
				main.game.death.put(player.getName(), task);
			}else{
				main.game.end();
			}
		}else{
			if(revived == true){
				Utils.broadcastMessage(red + player.getName() + gray + " has been revived.");
				
				this.setInventory(Status.STARTED);
				player.setHealth(20);
			}else{
				Utils.broadcastMessage(red + player.getName() + gray + " has died and is now spectating.");
				main.game.deadplayers.add(player.getName());
				this.toggleSpectating(true);
			}
			
			if(main.game.death.containsKey(player.getName()) == true){
				main.game.death.get(player.getName()).cancel();
				main.game.death.remove(player.getName());
			}
		}
	}
	
	/**
	 * Toggle if the player is spectating
	 * @param toggle - True if the player is spectating, false if not
	 */
	public void toggleSpectating(boolean toggle){
		if(toggle == true){
			for(Player p : Bukkit.getOnlinePlayers()){
				p.hidePlayer(player);
			}
			player.getInventory().clear();
			player.setAllowFlight(true);
			player.setFlying(true);
			player.teleport(main.game.getMap().getSpawn());
		}else{
			for(Player p : Bukkit.getOnlinePlayers()){
				p.showPlayer(player);
			}
			this.setInventory(Status.STARTED);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.teleport(main.game.getMap().getSpawn());
		}
	}
	
	/**
	 * Get the player's score
	 * @return The player's score
	 */
	public int getScore(){
		return main.game.scores.get(player.getName());
	}
	
	/**
	 * Set the player's score
	 * @param value - The player's score
	 */
	public void setScore(int value){
		if(main.game.scores.containsKey(player.getName()) == true){
			main.game.scores.remove(player.getName());
		}
		main.game.scores.put(player.getName(), value);
	}
	
	/**
	 * Give the player points
	 * @param value - The amount of points to give
	 */
	public void addScore(int value){
		this.setScore(this.getScore() + value);
		Utils.updateScoreboards();
	}
	
	/**
	 * Remove points from the player
	 * @param value - The amount of points to remove
	 */
	public void removeScore(int value){
		this.setScore(this.getScore() - value);
		Utils.updateScoreboards();
	}
	
	/**
	 * Get the player's brains
	 * @return The player's brains
	 */
	public int getBrains(){
		return this.getStats().brains;
	}
	
	/**
	 * Set the player's brains
	 * @param value - The brains value
	 */
	public void setBrains(int value){
		this.getStats().brains = value;
	}
	
	/**
	 * Give the player brains
	 * @param value - The amount of brains to give
	 */
	public void giveBrains(int value){
		this.setBrains(this.getBrains() + value);
		int current = 0;
		if(main.game.brains.containsKey(player.getName()) == true){
			current = main.game.brains.get(player.getName());
			main.game.brains.remove(player.getName());
		}
		main.game.brains.put(player.getName(), current + value);
	}
	
	/**
	 * Get the player's kills
	 * @return The player's kills
	 */
	public int getKills(){
		return this.getStats().kills;
	}
	
	/**
	 * Set the player's kills
	 * @param value - The kills value
	 */
	public void setKills(int value){
		this.getStats().kills = value;
	}
	
	/**
	 * Get the player's downs
	 * @return The player's downs
	 */
	public int getDowns(){
		return this.getStats().downs;
	}
	
	/**
	 * Set the player's downs
	 * @param value - The downs value
	 */
	public void setDowns(int value){
		this.getStats().downs = value;
	}
	
	/**
	 * Get the player's revives
	 * @return The player's revives
	 */
	public int getRevives(){
		return this.getStats().revives;
	}
	
	/**
	 * Set the player's revives
	 * @param value - The revives value
	 */
	public void setRevives(int value){
		this.getStats().revives = value;
	}
	
	/**
	 * Get the player's box attempts
	 * @return The player's box attempts
	 */
	public int getMysteryBoxAttempts(){
		return this.getStats().box;
	}
	
	/**
	 * Set the player's box attempts
	 * @param value - The box attempts value
	 */
	public void setMysteryBoxAttempts(int value){
		this.getStats().box = value;
	}
	
	/**
	 * Get the player's doors opened
	 * @return The player's doors opened
	 */
	public int getDoorsOpened(){
		return this.getStats().doors;
	}
	
	/**
	 * Set the player's doors opened
	 * @param value - The doors opened value
	 */
	public void setDoorsOpened(int value){
		this.getStats().doors = value;
	}
	
	/**
	 * Get the player's perks purchased
	 * @return The player's perks purchased
	 */
	public int getPerksPurchased(){
		return this.getStats().perks;
	}
	
	/**
	 * Set the player's perks purchased
	 * @param value - The perks purchased value
	 */
	public void setPerksPurchased(int value){
		this.getStats().perks = value;
	}
	
	/**
	 * Get the player's games played
	 * @return The player's games played
	 */
	public int getGamesPlayed(){
		return this.getStats().games;
	}
	
	/**
	 * Set the player's games played
	 * @param value - The games played value
	 */
	public void setGamesPlayed(int value){
		this.getStats().games = value;
	}
	
	/**
	 * Get the player's playtime
	 * @return The player's playtime
	 */
	public int getPlaytime(){
		return this.getStats().playtime;
	}
	
	/**
	 * Set the player's playtime
	 * @param value - The playtime value, in seconds
	 */
	public void setPlaytime(int value){
		this.getStats().playtime = value;
	}
	
	/**
	 * Check if the player has a store item
	 * @param item - The store item
	 * @return True if the player has the store item, false if not
	 */
	public boolean hasStoreItem(StoreItem item){
		return this.getStats().store.contains(item);
	}

}
